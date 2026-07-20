// pages/login/index.js
const app = getApp();

Page({
  data: {
    loading: false
  },

  onLoad() {
    // 检查是否已登录
    if (app.globalData.isLogin) {
      wx.navigateBack();
    }
  },

  // 微信一键登录
  doLogin() {
    if (this.data.loading) return;

    this.setData({ loading: true });
    wx.showLoading({ title: '登录中...' });

    app.login().then(() => {
      wx.hideLoading();
      wx.showToast({
        title: '登录成功',
        icon: 'success'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).catch(err => {
      wx.hideLoading();
      this.setData({ loading: false });
      wx.showToast({
        title: err.message || '登录失败',
        icon: 'none'
      });
    });
  }
});