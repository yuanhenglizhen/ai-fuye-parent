/**
 * 生成指定长度的随机字符串的函数
 */
export function randomString(len) {
    len = len || 32;
    var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
    /****默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/
    var maxPos = $chars.length;
    var pwd = '';
    for (let i = 0; i < len; i++) {
        pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    let randomStr = pwd + new Date().getTime()
    return randomStr;
}

/*
* 数组深拷贝方法
* */
export function deepClone(obj) {
    if (obj === null || typeof obj !== 'object') {
        return obj;
    }
    let clone = Array.isArray(obj) ? [] : {};
    Object.keys(obj).forEach(key => {
        clone[key] = deepClone(obj[key]);
    });
    return clone;
}

export function format(dat){
    //获取年月日，时间
    let year = dat.getFullYear();
    let mon = (dat.getMonth()+1) < 10 ? "0"+(dat.getMonth()+1) : dat.getMonth()+1;
    let data = dat.getDate()  < 10 ? "0"+(dat.getDate()) : dat.getDate();
    let hour = dat.getHours()  < 10 ? "0"+(dat.getHours()) : dat.getHours();
    let min =  dat.getMinutes()  < 10 ? "0"+(dat.getMinutes()) : dat.getMinutes();
    let seon = dat.getSeconds() < 10 ? "0"+(dat.getSeconds()) : dat.getSeconds();

    let newDate = year +"-"+ mon +"-"+ data;
    return newDate;
}