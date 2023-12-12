const Fields = {
  EVENT: "event",
  DATA: "data",
  ID: "id",
  RETRY: "retry",
};

// removes leading BOM, etc
const decodeUTF8 = (str) => decodeURIComponent(escape(str));
const normalizeToLF = (str) => str.replace(/\r\n|\r/g, "\n");

function throwError(e) {
  setTimeout(function () {
    throw e;
  }, 0);
}

function Event(type) {
  this.type = type;
  this.target = undefined;
}

function MessageEvent(type, options) {
  Event.call(this, type);
  this.data = options.data;
  this.lastEventId = options.lastEventId;
}

MessageEvent.prototype = Object.create(Event.prototype);

function ConnectionEvent(type, options) {
  Event.call(this, type);
  this.status = options.status;
  this.statusText = options.statusText;
  this.headers = options.headers;
}

ConnectionEvent.prototype = Object.create(Event.prototype);

function ErrorEvent(type, options) {
  Event.call(this, type);
  this.error = options.error;
}

ErrorEvent.prototype = Object.create(Event.prototype);

class EventTargetPolyfill {
  _listeners = [];

  constructor() {
    this._listeners = Object.create(null);
  }

  dispatchEvent(event) {
    event.target = this;
    var typeListeners = this._listeners[event.type];
    if (typeListeners != undefined) {
      var length = typeListeners.length;
      for (var i = 0; i < length; i += 1) {
        var listener = typeListeners[i];
        try {
          if (typeof listener.handleEvent === "function") {
            listener.handleEvent(event);
          } else {
            listener.call(this, event);
          }
        } catch (e) {
          throwError(e);
        }
      }
    }
  }

  addEventListener(type, listener) {
    type = String(type);
    var listeners = this._listeners;
    var typeListeners = listeners[type];
    if (typeListeners == undefined) {
      typeListeners = [];
      listeners[type] = typeListeners;
    }
    var found = false;
    for (var i = 0; i < typeListeners.length; i += 1) {
      if (typeListeners[i] === listener) {
        found = true;
      }
    }
    if (!found) {
      typeListeners.push(listener);
    }
  }

  removeEventListener(type, listener) {
    type = String(type);
    var listeners = this._listeners;
    var typeListeners = listeners[type];
    if (typeListeners != undefined) {
      var filtered = [];
      for (var i = 0; i < typeListeners.length; i += 1) {
        if (typeListeners[i] !== listener) {
          filtered.push(typeListeners[i]);
        }
      }
      if (filtered.length === 0) {
        delete listeners[type];
      } else {
        listeners[type] = filtered;
      }
    }
  }
}

class EventSourcePolyfill extends EventTargetPolyfill {
  CONNECTING = 0;
  OPEN = 1;
  CLOSED = 2;
  url;
  withCredentials;
  lastEventId = "";
  reconnectionTime = 2000;
  responseTextCursor = 0;
  eventTypeBuffer = "";
  idBuffer = "";
  dataBuffer = "";
  canReconnect = true;

  // casted to ! because lib.dom is wrong
  onerror;

  onmessage;
  onopen;

  readyState;

  xhr;

  constructor(url, config) {
    super();
    this.url = url;
    this.config = config || {};
    this.withCredentials = Boolean(config && config.withCredentials);
    this.addEventListener("error", (e) => {
      if (this.onerror) this.onerror(e);
    });
    this.addEventListener("message", (e) => {
      // listener set as Event & later casted because lib.dom is wrong
      if (this.onmessage) this.onmessage(e);
    });
    this.addEventListener("open", (e) => {
      if (this.onopen) this.onopen(e);
    });
    this.connect();
  }

  announceConnection() {
    this.readyState = this.OPEN;
    this.dispatchEvent(new Event("open"));
    this.responseTextCursor = 0;
  }

  connect(url = this.url) {
    this.readyState = this.CONNECTING;
    const xhr = (this.xhr = new XMLHttpRequest())// plus.net.XMLHttpRequest());
    xhr.open("GET", url, true);
    xhr.withCredentials = this.withCredentials;
    xhr.setRequestHeader("Accept", "text/event-stream");
    xhr.setRequestHeader("Cache-Control", "no-cache");
    if (this.lastEventId) {
      xhr.setRequestHeader("Last-Event-ID", this.lastEventId);
    }

    if (this.config.headers) {
      for (let key in this.config.headers) {
        xhr.setRequestHeader(key, this.config.headers[key]);
      }
    }

    xhr.onreadystatechange = () => {
      if (xhr.readyState <= 1 || this.readyState === this.CLOSED) return;
      // 服务器返回的HTTP状态代码，如200表示成功，而404表示"Not Found"错误； 当readyState小于3的时候此属性值为0。
      // https://www.html5plus.org/doc/zh_cn/xhr.html#plus.net.XMLHttpRequest.status
      if (xhr.readyState < 3) {
        return;
      }
      if (xhr.readyState === 4) {
        // is done
        this.reestablishConnection();
        return;
      }
      console.log(xhr.readyState);
      console.log(xhr.status);
      console.log(`-----------------`);

      switch (xhr.status) {
        case 200:
          this.handleConnection(xhr);
          this.interpretStream(xhr);
          break;
        case 204:
          this.canReconnect = false;
          break;
        case 301:
        case 307:
          const redirectUrl = xhr.getResponseHeader("Location");
          this.failConnection(xhr, true);
          if (redirectUrl) {
            this.connect(redirectUrl);
          }
          break;
        default:
          this.failConnection(xhr);
      }
    };
    xhr.send();
  }

  dispatchMessageEvent(origin) {
    this.lastEventId = this.idBuffer;
    if (this.dataBuffer === "") {
      this.eventTypeBuffer = "";
      return;
    }
    if (this.dataBuffer[this.dataBuffer.length - 1] === "\n") {
      this.dataBuffer = this.dataBuffer.slice(0, -1);
    }
    const eventType = this.eventTypeBuffer || "message";
    const event = new MessageEvent(eventType, {
      data: this.dataBuffer,
      origin,
      lastEventId: this.lastEventId,
    });
    this.eventTypeBuffer = "";
    this.dataBuffer = "";
    this.dispatchEvent(event);
  }

  handleConnection(xhr) {
    if (this.readyState === this.CONNECTING) {
      const contentType = xhr.getResponseHeader("Content-Type");
      if (contentType && contentType.toLowerCase() === "text/event-stream") {
        this.announceConnection();
      } else {
        this.failConnection(xhr);
      }
    }
  }

  failConnection(xhr, isSilent = false) {
    this.readyState = this.CLOSED;
    if (!isSilent) {
      this.dispatchEvent(new Event("error"));
    }
    this.canReconnect = false;
    xhr.abort();
  }

  interpretStream(xhr) {
    if (this.readyState !== this.OPEN) return;

    let responseText = "";
    try {
      responseText = xhr.responseText;
    } catch {
      return;
    }

    const rawChunk = responseText.substring(this.responseTextCursor);
    this.responseTextCursor = responseText.length;

    const chunk = normalizeToLF(decodeUTF8(rawChunk));

    const lines = chunk.split("\n");
    for (let ii = 0; ii < lines.length; ii++) {
      const line = lines[ii];
      if (line === "") {
        this.dispatchMessageEvent(xhr.responseURL);
      } else {
        const firstColonIdx = line.indexOf(":");
        if (firstColonIdx === 0) {
          // ignore comment line
        } else if (firstColonIdx !== -1) {
          const field = line.substring(0, firstColonIdx);
          const untrimmedVal = line.substring(firstColonIdx + 1);
          const value =
            untrimmedVal.indexOf(" ") === 0
              ? untrimmedVal.slice(1)
              : untrimmedVal;
          this.processField({ field, value });
        } else {
          this.processField({ field: line, value: "" });
        }
      }
    }
  }

  processField(payload) {
    switch (payload.field) {
      case Fields.EVENT:
        this.eventTypeBuffer = payload.value;
        break;
      case Fields.DATA:
        this.dataBuffer += `${payload.value}\n`;
        break;
      case Fields.ID:
        if (payload.value.indexOf("\u0000") === -1) {
          this.idBuffer = payload.value;
        }
        break;
      case Fields.RETRY:
        const interval = +payload.value;
        if (Number.isInteger(interval)) {
          this.reconnectionTime = interval;
        }
    }
  }

  reestablishConnection() {
    if (this.readyState === this.CLOSED || !this.canReconnect) return;
    this.readyState = this.CONNECTING;
    this.dispatchEvent(new Event("error"));
    setTimeout(() => {
      if (this.readyState !== this.CONNECTING) return;
      this.connect();
    }, this.reconnectionTime);
  }

  close() {
    this.readyState = this.CLOSED;
    this.xhr && this.xhr.abort();
  }
}

export default EventSourcePolyfill;
