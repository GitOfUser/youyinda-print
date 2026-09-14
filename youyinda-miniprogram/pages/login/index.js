// pages/login/index.js
const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    loading: false,
    agreed: false,
    phone: '',
    password: '',
    confirmPassword: '',
    mode: 'login' // 'login' | 'register'
  },

  onLoad() {
    if (app.globalData.isLogin) {
      const pages = getCurrentPages();
      if (pages.length > 1) {
        wx.navigateBack();
      } else {
        wx.reLaunch({
          url: '/pages/index/index'
        });
      }
    }
  },

  toggleAgree() {
    this.setData({
      agreed: !this.data.agreed
    });
  },

  onUnload() {
    // 页面卸载时清理状态
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value });
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  onConfirmPasswordInput(e) {
    this.setData({ confirmPassword: e.detail.value });
  },

  /**
   * 切换登录/注册模式
   */
  switchMode() {
    this.setData({
      mode: this.data.mode === 'login' ? 'register' : 'login',
      password: '',
      confirmPassword: ''
    });
  },

  /**
   * 提交按钮：按当前模式分发到登录或注册
   */
  doSubmit() {
    if (this.data.mode === 'login') {
      this.doLogin();
    } else {
      this.doRegister();
    }
  },

  /**
   * 手机号密码登录
   */
  doLogin() {
    if (this.data.loading) return;

    if (!this.data.agreed) {
      wx.showToast({
        title: '请先同意用户协议和隐私政策',
        icon: 'none'
      });
      return;
    }

    const phone = this.data.phone;
    const password = this.data.password;
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '请输入正确的手机号',
        icon: 'none'
      });
      return;
    }
    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      });
      return;
    }

    this.setData({ loading: true });
    wx.showLoading({ title: '登录中...' });

    app.loginWithPassword(phone, password).then(() => {
      wx.hideLoading();
      wx.showToast({
        title: '登录成功',
        icon: 'success'
      });
      setTimeout(() => {
        const pages = getCurrentPages();
        if (pages.length > 1) {
          wx.navigateBack();
        } else {
          wx.switchTab({
            url: '/pages/index/index'
          });
        }
      }, 1500);
    }).catch(err => {
      wx.hideLoading();
      this.setData({ loading: false });
      wx.showToast({
        title: err.message || '登录失败',
        icon: 'none'
      });
    });
  },

  /**
   * 手机号密码注册
   */
  doRegister() {
    if (this.data.loading) return;

    if (!this.data.agreed) {
      wx.showToast({
        title: '请先同意用户协议和隐私政策',
        icon: 'none'
      });
      return;
    }

    const phone = this.data.phone;
    const password = this.data.password;
    const confirmPassword = this.data.confirmPassword;
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '请输入正确的手机号',
        icon: 'none'
      });
      return;
    }
    if (!/^\S{6,20}$/.test(password)) {
      wx.showToast({
        title: '密码长度需为6-20位',
        icon: 'none'
      });
      return;
    }
    if (password !== confirmPassword) {
      wx.showToast({
        title: '两次输入的密码不一致',
        icon: 'none'
      });
      return;
    }

    this.setData({ loading: true });
    wx.showLoading({ title: '注册中...' });

    app.register(phone, password).then(() => {
      wx.hideLoading();
      wx.showToast({
        title: '注册成功',
        icon: 'success'
      });
      setTimeout(() => {
        const pages = getCurrentPages();
        if (pages.length > 1) {
          wx.navigateBack();
        } else {
          wx.switchTab({
            url: '/pages/index/index'
          });
        }
      }, 1500);
    }).catch(err => {
      wx.hideLoading();
      this.setData({ loading: false });
      wx.showToast({
        title: err.message || '注册失败',
        icon: 'none'
      });
    });
  },

  viewUserAgreement() {
    wx.showModal({
      title: '用户协议',
      content: '用户协议内容...',
      showCancel: false
    });
  },

  viewPrivacyPolicy() {
    wx.showModal({
      title: '隐私政策',
      content: '隐私政策内容...',
      showCancel: false
    });
  }
});
