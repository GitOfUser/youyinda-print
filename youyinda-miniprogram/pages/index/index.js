const app = getApp();
const request = require('../../utils/request');

Page({
  data: {},

  onLoad() {
    if (!app.globalData.isLogin) {
      // 密码登录需用户手动输入手机号和密码，此处不自动登录
      console.log('未登录，等待用户手动登录');
    }
  },

  goToPrint() {
    app.ensureLogin().then(() => {
      wx.navigateTo({
        url: '/pages/print/upload'
      });
    }).catch(() => {});
  },

  goToExpress() {
    app.ensureLogin().then(() => {
      wx.navigateTo({
        url: '/pages/express/sender'
      });
    }).catch(() => {});
  },

  goToOrderList() {
    wx.switchTab({
      url: '/pages/order/list'
    });
  },

  goToAddress() {
    app.ensureLogin().then(() => {
      wx.navigateTo({
        url: '/pages/user/address/list'
      });
    }).catch(() => {});
  },

  goToCoupon() {
    app.ensureLogin().then(() => {
      wx.navigateTo({
        url: '/pages/user/coupon'
      });
    }).catch(() => {});
  },

  goToHelp() {
    wx.navigateTo({
      url: '/pages/user/help'
    });
  }
});
