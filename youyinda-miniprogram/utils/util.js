/**
 * 优印达小程序 - 通用工具函数
 * 包含日期格式化、价格格式化、表单校验、地址解析等
 */

/**
 * 日期格式化
 * @param {Date|number|string} date 日期对象或时间戳
 * @param {string} format 格式化模板，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期字符串
 */
function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return '';
  
  const d = typeof date === 'object' ? date : new Date(date);
  
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hour = String(d.getHours()).padStart(2, '0');
  const minute = String(d.getMinutes()).padStart(2, '0');
  const second = String(d.getSeconds()).padStart(2, '0');
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
    .replace('ss', second);
}

/**
 * 相对时间格式化
 * @param {Date|number|string} date 日期对象或时间戳
 * @returns {string} 相对时间描述
 */
function formatRelativeTime(date) {
  if (!date) return '';
  
  const now = new Date().getTime();
  const target = new Date(date).getTime();
  const diff = now - target;
  
  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const week = 7 * day;
  const month = 30 * day;
  
  if (diff < minute) {
    return '刚刚';
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前';
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前';
  } else if (diff < week) {
    return Math.floor(diff / day) + '天前';
  } else if (diff < month) {
    return Math.floor(diff / week) + '周前';
  } else {
    return formatDate(date, 'YYYY-MM-DD');
  }
}

/**
 * 价格格式化（保留2位小数，添加¥符号）
 * @param {number|string} price 价格
 * @param {boolean} showSymbol 是否显示¥符号
 * @returns {string} 格式化后的价格
 */
function formatPrice(price, showSymbol = true) {
  if (price === null || price === undefined || price === '') {
    return showSymbol ? '¥0.00' : '0.00';
  }
  
  const num = parseFloat(price);
  if (isNaN(num)) {
    return showSymbol ? '¥0.00' : '0.00';
  }
  
  const formatted = num.toFixed(2);
  return showSymbol ? '¥' + formatted : formatted;
}

/**
 * 价格格式化（不保留小数，用于展示整数价格）
 * @param {number|string} price 价格
 * @param {boolean} showSymbol 是否显示¥符号
 * @returns {string} 格式化后的价格
 */
function formatPriceInt(price, showSymbol = true) {
  if (price === null || price === undefined || price === '') {
    return showSymbol ? '¥0' : '0';
  }
  
  const num = parseFloat(price);
  if (isNaN(num)) {
    return showSymbol ? '¥0' : '0';
  }
  
  // 如果是整数，不显示小数
  if (num % 1 === 0) {
    return showSymbol ? '¥' + num : String(num);
  }
  
  return formatPrice(price, showSymbol);
}

/**
 * 手机号码脱敏
 * @param {string} phone 手机号码
 * @returns {string} 脱敏后的手机号
 */
function maskPhone(phone) {
  if (!phone || phone.length !== 11) return phone;
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
}

/**
 * 姓名脱敏
 * @param {string} name 姓名
 * @returns {string} 脱敏后的姓名
 */
function maskName(name) {
  if (!name) return '';
  if (name.length <= 1) return name;
  if (name.length === 2) return name[0] + '*';
  return name[0] + '*'.repeat(name.length - 2) + name[name.length - 1];
}

/**
 * 地址脱敏
 * @param {string} address 地址
 * @returns {string} 脱敏后的地址
 */
function maskAddress(address) {
  if (!address || address.length <= 6) return address;
  return address.substring(0, 6) + '***';
}

/**
 * 验证手机号
 * @param {string} phone 手机号
 * @returns {boolean} 是否有效
 */
function isValidPhone(phone) {
  const reg = /^1[3-9]\d{9}$/;
  return reg.test(phone);
}

/**
 * 验证邮箱
 * @param {string} email 邮箱
 * @returns {boolean} 是否有效
 */
function isValidEmail(email) {
  const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return reg.test(email);
}

/**
 * 验证身份证号
 * @param {string} idCard 身份证号
 * @returns {boolean} 是否有效
 */
function isValidIdCard(idCard) {
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
  return reg.test(idCard);
}

/**
 * 验证非空
 * @param {*} value 值
 * @returns {boolean} 是否非空
 */
function isNotEmpty(value) {
  if (value === null || value === undefined) return false;
  if (typeof value === 'string') return value.trim().length > 0;
  if (Array.isArray(value)) return value.length > 0;
  if (typeof value === 'object') return Object.keys(value).length > 0;
  return true;
}

/**
 * 解析地址字符串
 * @param {string} address 完整地址
 * @returns {Object} 解析后的地址对象 {province, city, district, detail}
 */
function parseAddress(address) {
  if (!address) return { province: '', city: '', district: '', detail: '' };
  
  // 简单解析，按省市区关键词分割
  const provinceMatch = address.match(/^(.*?省|.*?自治区|.*?市辖区|.*?特别行政区)/);
  const cityMatch = address.match(/(.*?市|.*?自治州|.*?地区|.*?盟)/);
  const districtMatch = address.match(/(.*?区|.*?县|.*?旗|.*?镇)/);
  
  const province = provinceMatch ? provinceMatch[0] : '';
  const city = cityMatch ? cityMatch[0] : '';
  const district = districtMatch ? districtMatch[0] : '';
  
  // 剩余部分作为详细地址
  let detail = address
    .replace(province, '')
    .replace(city, '')
    .replace(district, '');
  
  return { province, city, district, detail };
}

/**
 * 文件大小格式化
 * @param {number} bytes 字节数
 * @returns {string} 格式化后的文件大小
 */
function formatFileSize(bytes) {
  if (bytes === 0) return '0 B';
  
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

/**
 * 生成随机字符串
 * @param {number} length 长度
 * @returns {string} 随机字符串
 */
function randomString(length = 16) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

/**
 * 防抖函数
 * @param {Function} fn 原函数
 * @param {number} delay 延迟时间（毫秒）
 * @returns {Function} 防抖后的函数
 */
function debounce(fn, delay = 300) {
  let timer = null;
  return function(...args) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

/**
 * 节流函数
 * @param {Function} fn 原函数
 * @param {number} interval 间隔时间（毫秒）
 * @returns {Function} 节流后的函数
 */
function throttle(fn, interval = 300) {
  let lastTime = 0;
  return function(...args) {
    const now = Date.now();
    if (now - lastTime >= interval) {
      lastTime = now;
      fn.apply(this, args);
    }
  };
}

/**
 * 深拷贝
 * @param {*} obj 原对象
 * @returns {*} 拷贝后的对象
 */
function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj;
  if (obj instanceof Date) return new Date(obj.getTime());
  if (obj instanceof Array) return obj.map(item => deepClone(item));
  if (obj instanceof Object) {
    const copy = {};
    Object.keys(obj).forEach(key => {
      copy[key] = deepClone(obj[key]);
    });
    return copy;
  }
  return obj;
}

/**
 * 对象属性过滤
 * @param {Object} obj 原对象
 * @param {Array} keys 需要保留的键
 * @returns {Object} 过滤后的对象
 */
function pick(obj, keys) {
  const result = {};
  keys.forEach(key => {
    if (obj.hasOwnProperty(key)) {
      result[key] = obj[key];
    }
  });
  return result;
}

/**
 * 对象属性排除
 * @param {Object} obj 原对象
 * @param {Array} keys 需要排除的键
 * @returns {Object} 处理后的对象
 */
function omit(obj, keys) {
  const result = { ...obj };
  keys.forEach(key => {
    delete result[key];
  });
  return result;
}

/**
 * 数组去重
 * @param {Array} arr 原数组
 * @param {string} key 对象数组的去重键
 * @returns {Array} 去重后的数组
 */
function unique(arr, key) {
  if (!key) {
    return [...new Set(arr)];
  }
  const seen = new Set();
  return arr.filter(item => {
    const val = item[key];
    if (seen.has(val)) return false;
    seen.add(val);
    return true;
  });
}

/**
 * 数组分组
 * @param {Array} arr 原数组
 * @param {string|Function} key 分组键或函数
 * @returns {Object} 分组后的对象
 */
function groupBy(arr, key) {
  return arr.reduce((result, item) => {
    const groupKey = typeof key === 'function' ? key(item) : item[key];
    if (!result[groupKey]) {
      result[groupKey] = [];
    }
    result[groupKey].push(item);
    return result;
  }, {});
}

module.exports = {
  formatDate,
  formatRelativeTime,
  formatPrice,
  formatPriceInt,
  maskPhone,
  maskName,
  maskAddress,
  isValidPhone,
  isValidEmail,
  isValidIdCard,
  isNotEmpty,
  parseAddress,
  formatFileSize,
  randomString,
  debounce,
  throttle,
  deepClone,
  pick,
  omit,
  unique,
  groupBy
};
