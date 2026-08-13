// pages/login/index.js
const app = getApp();

Page({
  data: {
    loading: false,
    agreed: false
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

  onGetPhoneNumber(e) {
    if (!this.data.agreed) {
      wx.showToast({
        title: '请先同意用户协议和隐私政策',
        icon: 'none'
      });
      return;
    }

    if (e.detail.errMsg === 'getPhoneNumber:ok') {
      this.doLogin(e.detail);
    } else {
      wx.showToast({
        title: '需要授权手机号才能登录',
        icon: 'none'
      });
    }
  },

  doLogin(phoneData) {
    if (this.data.loading) return;

    this.setData({ loading: true });
    wx.showLoading({ title: '登录中...' });

    app.login(phoneData).then(() => {
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

  onPhoneLogin() {
    wx.showToast({
      title: '手机号登录功能开发中',
      icon: 'none'
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
