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
    locationInfo: null,
    /** 流程状态对象：存储打印/快递多步流程中间数据，替代 Storage */
    flowState: {}
  },

  /**
   * 清空流程状态（订单创建成功后调用）
   */
  clearFlowState() {
    this.globalData.flowState = {};
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
   * 手机号密码登录
   * @param {string} phone 手机号
   * @param {string} password 密码
   * @returns {Promise} 登录结果
   */
  loginWithPassword(phone, password) {
    return new Promise((resolve, reject) => {
      // 调用后端手机号密码登录接口
      request.post('/auth/password-login', {
        phone: phone,
        password: password
      }).then((result) => {
        console.log('[App] 手机号密码登录成功:', result);
        this.globalData.token = result.token;
        this.globalData.userInfo = result.userInfo;
        this.globalData.openid = result.openid;
        this.globalData.isLogin = true;

        wx.setStorageSync('token', result.token);
        wx.setStorageSync('userInfo', result.userInfo);
        wx.setStorageSync('openid', result.openid);

        resolve(result);
      }).catch((err) => {
        console.error('[App] 手机号密码登录失败:', err);
        reject(err);
      });
    });
  },

  /**
   * 手机号密码注册
   * @param {string} phone 手机号
   * @param {string} password 密码
   * @returns {Promise} 注册结果（注册成功即登录）
   */
  register(phone, password) {
    return new Promise((resolve, reject) => {
      // 调用后端注册接口
      request.post('/auth/register', {
        phone: phone,
        password: password
      }).then((result) => {
        console.log('[App] 注册成功:', result);
        this.globalData.token = result.token;
        this.globalData.userInfo = result.userInfo;
        this.globalData.openid = result.openid;
        this.globalData.isLogin = true;

        wx.setStorageSync('token', result.token);
        wx.setStorageSync('userInfo', result.userInfo);
        wx.setStorageSync('openid', result.openid);

        resolve(result);
      }).catch((err) => {
        console.error('[App] 注册失败:', err);
        reject(err);
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
              // 手机号验证码登录无法静默自动重登，跳转登录页
              wx.reLaunch({
                url: '/pages/login/index'
              });
              reject(new Error('请先登录'));
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
