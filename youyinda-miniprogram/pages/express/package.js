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
    // 输入校验
    if (!this.data.itemType) {
      wx.showToast({
        title: '请选择物品类型',
        icon: 'none'
      });
      return;
    }
    
    const weight = parseFloat(this.data.weight);
    if (!this.data.weight || isNaN(weight) || weight <= 0) {
      wx.showToast({
        title: '请输入有效的包裹重量',
        icon: 'none'
      });
      return;
    }
    
    if (weight > 100) {
      wx.showToast({
        title: '包裹重量不能超过100公斤',
        icon: 'none'
      });
      return;
    }
    
    const packageInfo = {
      itemType: this.data.itemType,
      itemName: this.data.itemName,
      weight: weight,
      remark: this.data.remark
    };
    const app = getApp();
    if (!app.globalData.flowState) app.globalData.flowState = {};
    app.globalData.flowState.packageInfo = packageInfo;
    // 写入旧 Storage key 以兼容其他模块
    wx.setStorageSync('packageInfo', packageInfo);
    wx.navigateTo({
      url: '/pages/express/company'
    });
  }
});
