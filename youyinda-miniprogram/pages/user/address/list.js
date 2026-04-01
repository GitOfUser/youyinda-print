const request = require('../../../utils/request');

Page({
  data: {
    addressList: [],
    selectMode: false,
    selectType: ''
  },

  onLoad(options) {
    if (options.select) {
      this.setData({
        selectMode: true,
        selectType: options.type || ''
      });
    }
  },

  onShow() {
    this.loadAddressList();
  },

  loadAddressList() {
    wx.showLoading({
      title: '加载中...'
    });

    request.get('/user/address/list').then(res => {
      wx.hideLoading();
      this.setData({
        addressList: res.list || []
      });
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '加载失败',
        icon: 'none'
      });
    });
  },

  selectAddress(e) {
    if (this.data.selectMode) {
      const pages = getCurrentPages();
      const prevPage = pages[pages.length - 2];
      
      if (prevPage) {
        const address = e.currentTarget.dataset.item;
        if (this.data.selectType === 'sender') {
          prevPage.setData({ senderAddress: address });
        } else if (this.data.selectType === 'receiver') {
          prevPage.setData({ receiverAddress: address });
        } else {
          prevPage.setData({ address: address });
        }
        wx.navigateBack();
      }
    }
  },

  editAddress(e) {
    wx.navigateTo({
      url: `/pages/user/address/edit?id=${e.currentTarget.dataset.id}`
    });
  },

  deleteAddress(e) {
    wx.showModal({
      title: '提示',
      content: '确定要删除这个地址吗？',
      success: (res) => {
        if (res.confirm) {
          request.post('/user/address/delete', {
            id: e.currentTarget.dataset.id
          }).then(() => {
            wx.showToast({
              title: '删除成功',
              icon: 'success'
            });
            setTimeout(() => {
              this.loadAddressList();
            }, 1500);
          }).catch(err => {
            wx.showToast({
              title: err.message || '删除失败',
              icon: 'none'
            });
          });
        }
      }
    });
  },

  addAddress() {
    wx.navigateTo({
      url: '/pages/user/address/edit'
    });
  }
});
