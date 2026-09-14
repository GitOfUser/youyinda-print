const app = getApp();
const request = require('../../utils/request');
const priceUtil = require('../../utils/price');

const CART_KEY = 'printCart';

function genCartId() {
  return 'pc_' + Date.now() + '_' + Math.floor(Math.random() * 100000);
}

function getCart() {
  return wx.getStorageSync(CART_KEY) || { items: [] };
}

function saveCart(cart) {
  wx.setStorageSync(CART_KEY, cart);
}

function defaultConfig() {
  const price = priceUtil.calcItemPrice({
    paperSize: 'A4',
    colorType: 1,
    duplex: 1,
    copies: 1,
    binding: 'none'
  });
  return {
    paperSize: 'A4',
    colorType: 1,
    duplex: 1,
    copies: 1,
    binding: 'none',
    basePrice: price.basePrice,
    colorPrice: price.colorPrice,
    bindingPrice: price.bindingPrice,
    totalPrice: price.totalPrice
  };
}

Page({
  data: {
    fileList: [],
    uploading: false,
    cartCount: 0
  },

  onShow() {
    this.refreshCartCount();
  },

  refreshCartCount() {
    const cart = getCart();
    this.setData({ cartCount: (cart.items || []).length });
  },

  chooseFile() {
    wx.chooseMessageFile({
      count: 10,
      type: 'file',
      success: (res) => {
        const startIndex = this.data.fileList.length;
        const newFiles = res.tempFiles.map(file => ({
          name: file.name,
          size: this.formatFileSize(file.size),
          sizeRaw: file.size,
          path: file.path,
          type: file.type || (file.name ? file.name.split('.').pop() : ''),
          status: 'uploading',
          progress: 0,
          fileUrl: '',
          fileId: '',
          cartId: '',
          inCart: false
        }));
        this.setData({
          fileList: [...this.data.fileList, ...newFiles]
        });
        this.uploadFiles(newFiles, startIndex);
      }
    });
  },

  /**
   * 上传文件到服务器，成功后自动加入购物车并持久化
   */
  uploadFiles(newFiles, baseIndex) {
    if (newFiles.length === 0) return;
    this.setData({ uploading: true });

    const totalCount = newFiles.length;
    let completedCount = 0;
    let successCount = 0;

    const doUpload = (index) => {
      if (index >= newFiles.length) {
        this.setData({ uploading: false });
        this.refreshCartCount();
        if (successCount > 0) {
          wx.showToast({ title: '已加入购物车', icon: 'success' });
        } else {
          wx.showToast({ title: '上传失败，请重试', icon: 'none' });
        }
        return;
      }

      const file = newFiles[index];
      const listIndex = baseIndex + index;

      request.upload('/print/file/upload', file.path, 'file', {})
        .then((res) => {
          completedCount++;
          successCount++;
          const result = res.data || res;
          const fileUrl = result.fileUrl || '';
          const fileId = result.fileId || '';

          // 生成购物车项并持久化
          const cartId = genCartId();
          const config = defaultConfig();
          const cart = getCart();
          cart.items.push({
            cartId,
            name: file.name,
            size: file.size,
            sizeRaw: file.sizeRaw,
            fileUrl,
            fileId,
            config
          });
          saveCart(cart);

          this.setData({
            [`fileList[${listIndex}].status`]: 'success',
            [`fileList[${listIndex}].progress`]: 100,
            [`fileList[${listIndex}].fileUrl`]: fileUrl,
            [`fileList[${listIndex}].fileId`]: fileId,
            [`fileList[${listIndex}].cartId`]: cartId,
            [`fileList[${listIndex}].inCart`]: true
          });

          this.refreshCartCount();
          doUpload(index + 1);
        })
        .catch((err) => {
          completedCount++;
          console.error('[Upload] 上传失败:', err);
          this.setData({
            [`fileList[${listIndex}].status`]: 'error',
            [`fileList[${listIndex}].progress`]: 0
          });
          doUpload(index + 1);
        });
    };

    doUpload(0);
  },

  /**
   * 重试上传失败的文件
   */
  retryUpload(e) {
    const index = e.currentTarget.dataset.index;
    const file = this.data.fileList[index];
    if (!file) return;

    // 防御：若该文件曾入购物车，先移除旧记录，重传成功后重新加入
    if (file.inCart && file.cartId) {
      const cart = getCart();
      cart.items = (cart.items || []).filter(it => it.cartId !== file.cartId);
      saveCart(cart);
      this.setData({
        [`fileList[${index}].inCart`]: false,
        [`fileList[${index}].cartId`]: ''
      });
    }

    this.setData({
      [`fileList[${index}].status`]: 'uploading',
      [`fileList[${index}].progress`]: 0
    });

    this.uploadFiles([this.data.fileList[index]], index);
  },

  formatFileSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  },

  /**
   * 从列表移除文件，若已入购物车则同步移除
   */
  deleteFile(e) {
    const index = e.currentTarget.dataset.index;
    const file = this.data.fileList[index];
    if (!file) return;

    if (file.inCart && file.cartId) {
      const cart = getCart();
      cart.items = (cart.items || []).filter(it => it.cartId !== file.cartId);
      saveCart(cart);
    }

    const fileList = this.data.fileList.slice();
    fileList.splice(index, 1);
    this.setData({ fileList });
    this.refreshCartCount();
  },

  goCart() {
    wx.navigateTo({
      url: '/pages/print/cart'
    });
  }
});
