const priceUtil = require('../../utils/price');

const CART_KEY = 'printCart';

function getCart() {
  return wx.getStorageSync(CART_KEY) || { items: [] };
}

function saveCart(cart) {
  wx.setStorageSync(CART_KEY, cart);
}

Page({
  data: {
    items: [],
    selectedIds: [],
    allSelected: false,
    selectedCount: 0,
    selectedTotal: '0.00'
  },

  onShow() {
    this.loadCart();
  },

  loadCart() {
    const cart = getCart();
    const selectedIds = this.data.selectedIds;
    const items = (cart.items || []).map(item => {
      const config = item.config || {};
      return {
        ...item,
        selected: selectedIds.indexOf(item.cartId) >= 0,
        configSummary: this.buildConfigSummary(item)
      };
    });
    this.setData({ items });
    this.computeSummary();
  },

  buildConfigSummary(item) {
    const config = item.config || {};
    const colorText = config.colorType === 2 ? '彩色' : '黑白';
    const duplexText = config.duplex === 2 ? '双面' : '单面';
    const bindingName = priceUtil.BINDING_NAMES[config.binding] || '无装订';
    return `${config.paperSize || 'A4'} | ${colorText} | ${duplexText} | ${config.copies || 1}份 | ${bindingName}`;
  },

  toggleSelect(e) {
    const cartId = e.currentTarget.dataset.id;
    let selectedIds = this.data.selectedIds;
    if (selectedIds.indexOf(cartId) >= 0) {
      selectedIds = selectedIds.filter(id => id !== cartId);
    } else {
      selectedIds = [...selectedIds, cartId];
    }
    this.setData({ selectedIds });
    const items = this.data.items.map(it => ({
      ...it,
      selected: selectedIds.indexOf(it.cartId) >= 0
    }));
    this.setData({ items });
    this.computeSummary();
  },

  toggleAll() {
    const allSelected = !this.data.allSelected;
    const selectedIds = allSelected ? this.data.items.map(it => it.cartId) : [];
    const items = this.data.items.map(it => ({ ...it, selected: allSelected }));
    this.setData({ selectedIds, allSelected, items });
    this.computeSummary();
  },

  computeSummary() {
    const items = this.data.items;
    const selectedIds = this.data.selectedIds;
    let selectedCount = 0;
    let selectedTotal = 0;
    items.forEach(it => {
      if (selectedIds.indexOf(it.cartId) >= 0) {
        selectedCount++;
        selectedTotal += Number(it.config.totalPrice || 0);
      }
    });
    this.setData({
      selectedCount,
      selectedTotal: selectedTotal.toFixed(2),
      allSelected: items.length > 0 && selectedCount === items.length
    });
  },

  removeItem(e) {
    const cartId = e.currentTarget.dataset.id;
    wx.showModal({
      title: '提示',
      content: '确定将该文件移出购物车吗？',
      success: (res) => {
        if (res.confirm) {
          const cart = getCart();
          cart.items = (cart.items || []).filter(it => it.cartId !== cartId);
          saveCart(cart);
          this.setData({
            selectedIds: this.data.selectedIds.filter(id => id !== cartId)
          });
          this.loadCart();
        }
      }
    });
  },

  clearCart() {
    if (this.data.items.length === 0) return;
    wx.showModal({
      title: '提示',
      content: '确定要清空购物车吗？',
      success: (res) => {
        if (res.confirm) {
          saveCart({ items: [] });
          this.setData({ selectedIds: [], allSelected: false, selectedCount: 0, selectedTotal: '0.00' });
          this.loadCart();
        }
      }
    });
  },

  goUpload() {
    wx.navigateTo({
      url: '/pages/print/upload'
    });
  },

  editConfig(e) {
    const cartId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/print/config?mode=single&id=${cartId}`
    });
  },

  batchConfig() {
    const ids = this.data.selectedIds;
    if (ids.length === 0) {
      wx.showToast({ title: '请先勾选文件', icon: 'none' });
      return;
    }
    wx.navigateTo({
      url: `/pages/print/config?mode=batch&ids=${ids.join(',')}`
    });
  },

  goConfirm() {
    const ids = this.data.selectedIds;
    if (ids.length === 0) {
      wx.showToast({ title: '请先勾选文件', icon: 'none' });
      return;
    }
    wx.navigateTo({
      url: `/pages/print/confirm?ids=${ids.join(',')}`
    });
  }
});
