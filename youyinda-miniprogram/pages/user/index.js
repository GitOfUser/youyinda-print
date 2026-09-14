const app = getApp();

Page({
  data: {
    userInfo: null,
    orderStats: {
      pending: 0,
      processing: 0,
      completed: 0,
      all: 0
    }
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
    wx.navigateTo({
      url: '/pages/login/index'
    });
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
  },

  goToSetting() {
    wx.showToast({
      title: '设置功能即将上线',
      icon: 'none'
    });
  },

  contactService() {
    wx.showActionSheet({
      itemList: ['拨打客服电话 400-123-4567', '复制客服微信 youyinda_001'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.makePhoneCall({
            phoneNumber: '400-123-4567'
          });
        } else if (res.tapIndex === 1) {
          wx.setClipboardData({
            data: 'youyinda_001',
            success: () => {
              wx.showToast({
                title: '已复制客服微信号',
                icon: 'none'
              });
            }
          });
        }
      }
    });
  }
});
