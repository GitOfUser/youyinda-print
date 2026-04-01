Page({
  data: {
    itemType: '',
    itemName: '',
    weight: '',
    remark: ''
  },

  selectItemType() {
    const itemTypes = ['文件', '衣服', '食品', '电子产品', '其他'];
    wx.showActionSheet({
      itemList: itemTypes,
      success: (res) => {
        this.setData({
          itemType: itemTypes[res.tapIndex]
        });
      }
    });
  },

  onItemNameInput(e) {
    this.setData({ itemName: e.detail.value });
  },

  onWeightInput(e) {
    this.setData({ weight: e.detail.value });
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value });
  },

  goToCompany() {
    wx.setStorageSync('packageInfo', {
      itemType: this.data.itemType,
      itemName: this.data.itemName,
      weight: parseFloat(this.data.weight),
      remark: this.data.remark
    });
    wx.navigateTo({
      url: '/pages/express/company'
    });
  }
});
