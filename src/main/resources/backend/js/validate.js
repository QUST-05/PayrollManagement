
function isValidUsername (str) {
  return ['admin', 'editor'].indexOf(str.trim()) >= 0;
}

function isExternal (path) {
  return /^(https?:|mailto:|tel:)/.test(path);
}

function isCellPhone (val) {
  if (!/^1(3|4|5|6|7|8)\d{9}$/.test(val)) {
    return false
  } else {
    return true
  }
}

//校验账号
function checkUserName (rule, value, callback){
  if (value == "") {
    callback(new Error("请输入账号"))
  } else if (value.length > 20 || value.length <3) {
    callback(new Error("账号长度应是3-20"))
  } else {
    callback()
  }
}

//校验姓名
function checkName (rule, value, callback){
  if (value == "") {
    callback(new Error("请输入姓名"))
  } else if (value.length > 12) {
    callback(new Error("账号长度应是1-12"))
  } else {
    callback()
  }
}

function checkPhone (rule, value, callback){
  // let phoneReg = /(^1[3|4|5|6|7|8|9]\d{9}$)|(^09\d{8}$)/;
  if (value == "") {
    callback(new Error("请输入手机号"))
  } else if (!isCellPhone(value)) {//引入methods中封装的检查手机格式的方法
    callback(new Error("请输入正确的手机号!"))
  } else {
    callback()
  }
}


function validID (rule,value,callback) {
  // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
  let reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  if(value == '') {
    callback(new Error('请输入身份证号码'))
  } else if (reg.test(value)) {
    callback()
  } else {
    callback(new Error('身份证号码不正确'))
  }
}

function checkBeforePassword (rule, value, callback){
    // let phoneReg = /(^1[3|4|5|6|7|8|9]\d{9}$)|(^09\d{8}$)/;
    if (value == "") {
        callback(new Error("请输入新密码"))
    } else if (value != "000000") {//与原来密码不匹配
        callback(new Error("与原来密码不匹配!"))
    } else {
        callback()
    }
}

function checkPassword (rule, value, callback){
    // let phoneReg = /(^1[3|4|5|6|7|8|9]\d{9}$)|(^09\d{8}$)/;
    if (value == "") {
        callback(new Error("请输入新密码"))
    } else if (value == "0") {//引入methods中封装的检查手机格式的方法
        callback(new Error("长度太短，请重新设置!"))
    } else {
        callback()
    }
}

function checkRepassword (rule, value, callback){
    // let phoneReg = /(^1[3|4|5|6|7|8|9]\d{9}$)|(^09\d{8}$)/;
    if (value == "") {
        callback(new Error("请再次输入密码"))
    } else if (!isCellPhone(value)) {//引入methods中封装的检查手机格式的方法
        callback(new Error("两次密码不一致!"))
    } else {
        callback()
    }
}