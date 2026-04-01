/**
 * 优印达小程序 - 应用入口
 * 负责全局状态管理、登录态维护、异常处理
 */

const request = require('./utils/request');

App({
  globalData: {
    userInfo: null,
    token: null,
    openid: null,
    isLogin: false,
    systemInfo: null,
    locationInfo: null
  },

  onLaunch() {
    console.log('[App] 小程序启动');
    this.initSystemInfo();
    this.checkLoginStatus();
  },

  onShow() {
    console.log('[App] 小程序显示');
  },

  onHide() {
    console.log('[App] 小程序隐藏');
  },

  onError(err) {
    console.error('[App] 全局错误:', err);
  },

  onPageNotFound(res) {
    console.warn('[App] 页面未找到:', res);
    wx.redirectTo({
      url: '/pages/index/index'
    });
  },

  /**
   * 初始化系统信息
   */
  initSystemInfo() {
    try {
      const systemInfo = wx.getSystemInfoSync();
      this.globalData.systemInfo = systemInfo;
      console.log('[App] 系统信息:', systemInfo);
    } catch (err) {
      console.error('[App] 获取系统信息失败:', err);
    }
  },

  /**
   * 检查登录状态
   */
  checkLoginStatus() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    const openid = wx.getStorageSync('openid');
    
    if (token && userInfo) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
      this.globalData.openid = openid;
      this.globalData.isLogin = true;
      console.log('[App] 已登录，用户信息:', userInfo);
    } else {
      console.log('[App] 未登录');
    }
  },

  /**
   * 微信登录
   * @returns {Promise} 登录结果
   */
  login() {
    return new Promise((resolve, reject) => {
      wx.login({
        success: (res) => {
          if (res.code) {
            console.log('[App] 获取微信code成功');
            // 调用后端登录接口
            request.post('/auth/login', {
              code: res.code
            }).then((result) => {
              console.log('[App] 登录成功:', result);
              this.globalData.token = result.token;
              this.globalData.userInfo = result.userInfo;
              this.globalData.openid = result.openid;
              this.globalData.isLogin = true;
              
              wx.setStorageSync('token', result.token);
              wx.setStorageSync('userInfo', result.userInfo);
              wx.setStorageSync('openid', result.openid);
              
              resolve(result);
            }).catch((err) => {
              console.error('[App] 登录失败:', err);
              reject(err);
            });
          } else {
            console.error('[App] 获取微信code失败:', res);
            reject(new Error('微信登录失败'));
          }
        },
        fail: (err) => {
          console.error('[App] wx.login调用失败:', err);
          reject(err);
        }
      });
    });
  },

  /**
   * 退出登录
   */
  logout() {
    console.log('[App] 退出登录');
    this.globalData.userInfo = null;
    this.globalData.token = null;
    this.globalData.openid = null;
    this.globalData.isLogin = false;
    
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
    wx.removeStorageSync('openid');
    
    wx.reLaunch({
      url: '/pages/index/index'
    });
  },

  /**
   * 确保已登录
   * @returns {Promise} 
   */
  ensureLogin() {
    return new Promise((resolve, reject) => {
      if (this.globalData.isLogin) {
        resolve();
      } else {
        wx.showModal({
          title: '提示',
          content: '请先登录',
          confirmText: '去登录',
          success: (res) => {
            if (res.confirm) {
              this.login().then(() => {
                resolve();
              }).catch((err) => {
                reject(err);
              });
            } else {
              reject(new Error('用户取消登录'));
            }
          }
        });
      }
    });
  },

  /**
   * 更新用户信息
   * @param {Object} userInfo 用户信息
   */
  updateUserInfo(userInfo) {
    this.globalData.userInfo = { ...this.globalData.userInfo, ...userInfo };
    wx.setStorageSync('userInfo', this.globalData.userInfo);
  },

  /**
   * 获取当前位置
   * @returns {Promise} 位置信息
   */
  getLocation() {
    return new Promise((resolve, reject) => {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.globalData.locationInfo = res;
          resolve(res);
        },
        fail: (err) => {
          console.error('[App] 获取位置失败:', err);
          reject(err);
        }
      });
    });
  }
});
