/**
 * 优印达小程序 - 网络请求封装
 * 统一处理请求拦截、响应拦截、Token管理、错误处理
 */

// API基础地址
const BASE_URL = 'http://localhost:8080/api/v1';

// 请求队列，用于登录态过期时暂存请求
let requestQueue = [];
let isRefreshing = false;

/**
 * 获取Token
 * @returns {string|null} Token
 */
function getToken() {
  return wx.getStorageSync('token');
}

/**
 * 统一错误处理
 * @param {Error} err 错误对象
 * @param {string} url 请求URL
 */
function handleError(err, url) {
  console.error('[Request] 请求失败:', url, err);
  
  let message = '网络请求失败，请稍后重试';
  
  if (err.statusCode === 401) {
    message = '登录已过期，请重新登录';
    // 清除登录态
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
    const app = getApp();
    if (app) {
      app.globalData.isLogin = false;
      app.globalData.token = null;
      app.globalData.userInfo = null;
    }
  } else if (err.statusCode === 403) {
    message = '暂无权限访问';
  } else if (err.statusCode === 404) {
    message = '请求的资源不存在';
  } else if (err.statusCode === 500) {
    message = '服务器内部错误';
  } else if (err.errMsg && err.errMsg.includes('timeout')) {
    message = '请求超时，请检查网络';
  } else if (err.errMsg && err.errMsg.includes('fail')) {
    message = '网络连接失败，请检查网络';
  }
  
  // 显示错误提示
  wx.showToast({
    title: message,
    icon: 'none',
    duration: 2000
  });
  
  return Promise.reject(err);
}

/**
 * 基础请求方法
 * @param {Object} options 请求配置
 * @returns {Promise} 请求结果
 */
function request(options) {
  return new Promise((resolve, reject) => {
    const token = getToken();
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    };
    
    // 如果有token，添加到请求头
    if (token) {
      header['Authorization'] = 'Bearer ' + token;
    }
    
    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: header,
      timeout: options.timeout || 10000,
      success: (res) => {
        console.log('[Request] 响应:', options.url, res);
        
        // 处理HTTP状态码
        if (res.statusCode >= 200 && res.statusCode < 300) {
          const data = res.data;
          
          // 处理业务状态码
          if (data.code === 200 || data.code === 0) {
            resolve(data.data || data);
          } else if (data.code === 401) {
            // Token过期，需要重新登录
            handleTokenExpired(options, resolve, reject);
          } else {
            // 业务错误
            const errMsg = data.message || data.msg || '请求失败';
            wx.showToast({
              title: errMsg,
              icon: 'none'
            });
            reject(new Error(errMsg));
          }
        } else if (res.statusCode === 401) {
          handleTokenExpired(options, resolve, reject);
        } else {
          handleError({ statusCode: res.statusCode }, options.url).catch(reject);
        }
      },
      fail: (err) => {
        handleError(err, options.url).catch(reject);
      }
    });
  });
}

/**
 * 处理Token过期
 * @param {Object} options 原请求配置
 * @param {Function} resolve Promise resolve
 * @param {Function} reject Promise reject
 */
function handleTokenExpired(options, resolve, reject) {
  if (isRefreshing) {
    // 正在刷新token，将请求加入队列
    requestQueue.push({ options, resolve, reject });
  } else {
    isRefreshing = true;
    requestQueue.push({ options, resolve, reject });
    
    // 重新登录
    const app = getApp();
    if (app) {
      app.login().then(() => {
        // 重新执行队列中的请求
        requestQueue.forEach(({ options, resolve }) => {
          request(options).then(resolve);
        });
        requestQueue = [];
        isRefreshing = false;
      }).catch((err) => {
        requestQueue.forEach(({ reject }) => reject(err));
        requestQueue = [];
        isRefreshing = false;
        
        // 跳转到登录页
        wx.navigateTo({
          url: '/pages/login/index'
        });
      });
    } else {
      isRefreshing = false;
      reject(new Error('登录态过期'));
    }
  }
}

/**
 * GET请求
 * @param {string} url 请求地址
 * @param {Object} params 请求参数
 * @param {Object} options 其他配置
 * @returns {Promise}
 */
function get(url, params = {}, options = {}) {
  // 构建查询字符串
  let queryString = '';
  if (Object.keys(params).length > 0) {
    const query = Object.keys(params)
      .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
      .join('&');
    queryString = '?' + query;
  }
  
  return request({
    url: url + queryString,
    method: 'GET',
    ...options
  });
}

/**
 * POST请求
 * @param {string} url 请求地址
 * @param {Object} data 请求数据
 * @param {Object} options 其他配置
 * @returns {Promise}
 */
function post(url, data = {}, options = {}) {
  return request({
    url: url,
    method: 'POST',
    data: data,
    ...options
  });
}

/**
 * PUT请求
 * @param {string} url 请求地址
 * @param {Object} data 请求数据
 * @param {Object} options 其他配置
 * @returns {Promise}
 */
function put(url, data = {}, options = {}) {
  return request({
    url: url,
    method: 'PUT',
    data: data,
    ...options
  });
}

/**
 * DELETE请求
 * @param {string} url 请求地址
 * @param {Object} params 请求参数
 * @param {Object} options 其他配置
 * @returns {Promise}
 */
function del(url, params = {}, options = {}) {
  let queryString = '';
  if (Object.keys(params).length > 0) {
    const query = Object.keys(params)
      .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
      .join('&');
    queryString = '?' + query;
  }
  
  return request({
    url: url + queryString,
    method: 'DELETE',
    ...options
  });
}

/**
 * 上传文件
 * @param {string} url 上传地址
 * @param {string} filePath 文件路径
 * @param {string} name 文件字段名
 * @param {Object} formData 附加表单数据
 * @returns {Promise}
 */
function upload(url, filePath, name = 'file', formData = {}) {
  return new Promise((resolve, reject) => {
    const token = getToken();
    const header = {};
    
    if (token) {
      header['Authorization'] = 'Bearer ' + token;
    }
    
    wx.uploadFile({
      url: BASE_URL + url,
      filePath: filePath,
      name: name,
      formData: formData,
      header: header,
      success: (res) => {
        console.log('[Request] 上传成功:', url, res);
        
        if (res.statusCode >= 200 && res.statusCode < 300) {
          try {
            const data = JSON.parse(res.data);
            if (data.code === 200 || data.code === 0) {
              resolve(data.data || data);
            } else {
              reject(new Error(data.message || '上传失败'));
            }
          } catch (e) {
            resolve(res.data);
          }
        } else {
          reject(new Error('上传失败'));
        }
      },
      fail: (err) => {
        handleError(err, url).catch(reject);
      }
    });
  });
}

/**
 * 下载文件
 * @param {string} url 下载地址
 * @returns {Promise}
 */
function download(url) {
  return new Promise((resolve, reject) => {
    wx.downloadFile({
      url: BASE_URL + url,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.tempFilePath);
        } else {
          reject(new Error('下载失败'));
        }
      },
      fail: (err) => {
        handleError(err, url).catch(reject);
      }
    });
  });
}

module.exports = {
  request,
  get,
  post,
  put,
  delete: del,
  upload,
  download,
  BASE_URL
};
