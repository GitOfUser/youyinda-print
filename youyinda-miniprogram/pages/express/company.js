const request = require('../../utils/request');
const priceUtil = require('../../utils/price');

Page({
  data: {
    companyList: [],
    selectedCompany: null
  },

  onLoad() {
    this.loadCompanyList();
  },

  loadCompanyList() {
    const app = getApp();
    const flowState = app.globalData.flowState || {};
    const packageInfo = flowState.packageInfo || wx.getStorageSync('packageInfo') || {};
    const senderAddress = flowState.senderAddress || wx.getStorageSync('senderAddress') || {};
    const receiverAddress = flowState.receiverAddress || wx.getStorageSync('receiverAddress') || {};

    wx.showLoading({ title: '加载中...' });

    // 从后端获取快递公司列表，并携带重量和地址信息用于实时报价
    request.get('/express/company/list', {
      weight: packageInfo.weight || 1,
      senderProvince: senderAddress.province || '',
      receiverProvince: receiverAddress.province || ''
    }).then((data) => {
      wx.hideLoading();
      // data 可能是数组，也可能是 { list: [...] } 结构
      const companyList = Array.isArray(data) ? data : (data.list || data.data || []);
      const formattedList = companyList.map(company => ({
        id: company.id,
        name: company.expressName,
        desc: company.timelinessDesc || '',
        price: this.calcExpressPrice(packageInfo.weight || 1, 10).toFixed(2),
        logo: company.expressLogo || '',
        expressCode: company.expressCode
      }));
      this.setData({ companyList: formattedList });
      if (formattedList.length === 0) {
        wx.showToast({ title: '暂无可用快递公司', icon: 'none' });
      }
    }).catch((err) => {
      wx.hideLoading();
      console.error('加载快递公司失败:', err);
      wx.showToast({ title: '加载失败，请重试', icon: 'none' });
    });
  },

  /**
   * 计算快递价格（临时方案，待后端报价接口就绪后替换）
   */
  calcExpressPrice(weight, basePrice) {
    if (!weight || weight <= 0) weight = 1;
    let totalPrice = basePrice;
    if (weight > 1) {
      totalPrice += Math.ceil(weight - 1) * 5;
    }
    return totalPrice;
  },

  selectCompany(e) {
    this.setData({
      selectedCompany: e.currentTarget.dataset.item
    });
  },

  goToConfirm() {
    const app = getApp();
    if (!app.globalData.flowState) app.globalData.flowState = {};
    app.globalData.flowState.selectedCompany = this.data.selectedCompany;
    // 写入旧 Storage key 以兼容其他模块
    wx.setStorageSync('selectedCompany', this.data.selectedCompany);
    wx.navigateTo({
      url: '/pages/express/confirm'
    });
  }
});
