const app = getApp();

Page({
  data: {
    userInfo: null
  },

  onLoad() {
    this.setData({
      userInfo: app.globalData.userInfo
    });
  },

  onShow() {
    this.setData({
      userInfo: app.globalData.userInfo
    });
  },

  doLogin() {
    app.login().then(() => {
      this.setData({
        userInfo: app.globalData.userInfo
      });
    }).catch(() => {});
  },

  doLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout();
        }
      }
    });
  },

  goToOrderList() {
    app.ensureLogin().then(() => {
      wx.switchTab({
        url: '/pages/order/list'
      });
    }).catch(() => {});
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
  },

  goToAbout() {
    wx.showModal({
      title: '关于优印达',
      content: '优印达 v1.0.0\n\n专业的云打印和快递寄件服务平台',
      showCancel: false
    });
  }
});
