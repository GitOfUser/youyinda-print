const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    fileList: []
  },

  chooseFile() {
    wx.chooseMessageFile({
      count: 10,
      type: 'file',
      success: (res) => {
        const newFiles = res.tempFiles.map(file => ({
          name: file.name,
          size: this.formatFileSize(file.size),
          path: file.path,
          type: file.type
        }));
        this.setData({
          fileList: [...this.data.fileList, ...newFiles]
        });
      }
    });
  },

  formatFileSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  },

  deleteFile(e) {
    const index = e.currentTarget.dataset.index;
    const fileList = this.data.fileList;
    fileList.splice(index, 1);
    this.setData({ fileList });
  },

  goToConfig() {
    wx.setStorageSync('printFiles', this.data.fileList);
    wx.navigateTo({
      url: '/pages/print/config'
    });
  }
});
