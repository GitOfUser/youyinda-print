const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    fileList: [],
    uploading: false,
    uploadProgress: 0,
    canNext: false
  },

  chooseFile() {
    wx.chooseMessageFile({
      count: 10,
      type: 'file',
      success: (res) => {
        const newFiles = res.tempFiles.map(file => ({
          name: file.name,
          size: this.formatFileSize(file.size),
          sizeRaw: file.size,
          path: file.path,
          type: file.type || (file.name ? file.name.split('.').pop() : ''),
          uploaded: false,
          fileUrl: '',
          fileId: ''
        }));
        this.setData({
          fileList: [...this.data.fileList, ...newFiles],
          canNext: true
        });
        // 自动上传新选的文件
        this.uploadFiles(newFiles);
      }
    });
  },

  /**
   * 上传文件到服务器
   */
  uploadFiles(newFiles) {
    if (newFiles.length === 0) return;

    this.setData({ uploading: true, uploadProgress: 0 });

    const totalCount = newFiles.length;
    let completedCount = 0;
    const uploadResults = [];

    const doUpload = (index) => {
      if (index >= newFiles.length) {
        this.setData({ uploading: false });
        wx.showToast({ title: '全部上传完成', icon: 'success' });
        return;
      }

      const file = newFiles[index];
      wx.showLoading({ title: `上传中 ${index + 1}/${totalCount}` });

      request.upload('/print/file/upload', file.path, 'file', {})
        .then((res) => {
          completedCount++;
          const result = res.data || res;
          uploadResults.push({
            index: index,
            fileId: result.fileId || '',
            fileUrl: result.fileUrl || ''
          });

          // 更新文件列表中的上传状态
          const key = `fileList[${this.data.fileList.length - totalCount + index}]`;
          this.setData({
            [key + '.fileUrl']: result.fileUrl || '',
            [key + '.fileId']: result.fileId || '',
            [key + '.uploaded']: true,
            uploadProgress: Math.round((completedCount / totalCount) * 100)
          });

          wx.hideLoading();
          doUpload(index + 1);
        })
        .catch((err) => {
          completedCount++;
          wx.hideLoading();
          wx.showToast({
            title: `上传失败(${index + 1}/${totalCount}): ${err.message || '网络错误'}`,
            icon: 'none',
            duration: 3000
          });
          const key = `fileList[${this.data.fileList.length - totalCount + index}]`;
          this.setData({
            [key + '.uploadError']: true,
            uploadProgress: Math.round((completedCount / totalCount) * 100)
          });
          doUpload(index + 1);
        });
    };

    doUpload(0);
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
    this.setData({
      fileList,
      canNext: fileList.length > 0
    });
  },

  goToConfig() {
    if (this.data.uploading) {
      wx.showToast({ title: '文件正在上传中，请稍候', icon: 'none' });
      return;
    }

    const hasError = this.data.fileList.some(f => f.uploadError);
    if (hasError) {
      wx.showModal({
        title: '提示',
        content: '部分文件上传失败，是否继续？失败的文件将不会包含在订单中。',
        success: (res) => {
          if (res.confirm) {
            this.navigateToConfig();
          }
        }
      });
      return;
    }

    this.navigateToConfig();
  },

  navigateToConfig() {
    const validFiles = this.data.fileList.filter(f => f.uploaded && f.fileUrl);
    if (validFiles.length === 0) {
      wx.showToast({ title: '没有成功上传的文件', icon: 'none' });
      return;
    }
    const app = getApp();
    if (!app.globalData.flowState) app.globalData.flowState = {};
    app.globalData.flowState.printFiles = validFiles;
    // 写入旧 Storage key 以兼容其他模块
    wx.setStorageSync('printFiles', validFiles);
    wx.navigateTo({
      url: '/pages/print/config'
    });
  }
});
