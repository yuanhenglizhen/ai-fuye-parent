package com.hanzaitu.common.exception;


public class AuthException extends RuntimeException{

    private final String msg;

    public AuthException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public static AuthException createAuthException(String msg) {
        return new AuthException(msg);
    }

}
