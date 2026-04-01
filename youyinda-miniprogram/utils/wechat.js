/**
 * 优印达小程序 - 微信能力封装
 * 统一封装授权、支付、地址获取、消息订阅等微信原生能力
 */

/**
 * 获取用户信息授权
 * @returns {Promise} 用户信息
 */
function getUserProfile() {
  return new Promise((resolve, reject) => {
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        resolve(res.userInfo);
      },
      fail: (err) => {
        console.error('[Wechat] 获取用户信息失败:', err);
        reject(err);
      }
    });
  });
}

/**
 * 获取手机号（需要后端解密）
 * @param {Object} e 按钮回调事件对象
 * @returns {Promise} 包含code的对象
 */
function getPhoneNumber(e) {
  return new Promise((resolve, reject) => {
    if (e.detail.errMsg === 'getPhoneNumber:ok') {
      resolve({
        code: e.detail.code,
        encryptedData: e.detail.encryptedData,
        iv: e.detail.iv
      });
    } else {
      reject(new Error('用户拒绝授权手机号'));
    }
  });
}

/**
 * 微信支付
 * @param {Object} paymentData 支付参数（后端返回）
 * @returns {Promise} 支付结果
 */
function requestPayment(paymentData) {
  return new Promise((resolve, reject) => {
    wx.requestPayment({
      timeStamp: paymentData.timeStamp,
      nonceStr: paymentData.nonceStr,
      package: paymentData.package,
      signType: paymentData.signType || 'RSA',
      paySign: paymentData.paySign,
      success: (res) => {
        console.log('[Wechat] 支付成功:', res);
        resolve(res);
      },
      fail: (err) => {
        console.error('[Wechat] 支付失败:', err);
        if (err.errMsg && err.errMsg.includes('cancel')) {
          reject(new Error('用户取消支付'));
        } else {
          reject(err);
        }
      }
    });
  });
}

/**
 * 选择微信地址
 * @returns {Promise} 地址信息
 */
function chooseAddress() {
  return new Promise((resolve, reject) => {
    wx.chooseAddress({
      success: (res) => {
        const address = {
          name: res.userName,
          phone: res.telNumber,
          province: res.provinceName,
          city: res.cityName,
          district: res.countyName,
          detail: res.detailInfo,
          postalCode: res.postalCode,
          fullAddress: `${res.provinceName}${res.cityName}${res.countyName}${res.detailInfo}`
        };
        resolve(address);
      },
      fail: (err) => {
        console.error('[Wechat] 选择地址失败:', err);
        reject(err);
      }
    });
  });
}

/**
 * 获取用户位置
 * @param {boolean} isHighAccuracy 是否高精度
 * @returns {Promise} 位置信息
 */
function getLocation(isHighAccuracy = false) {
  return new Promise((resolve, reject) => {
    wx.getLocation({
      type: 'gcj02',
      isHighAccuracy: isHighAccuracy,
      success: (res) => {
        resolve({
          latitude: res.latitude,
          longitude: res.longitude,
          speed: res.speed,
          accuracy: res.accuracy
        });
      },
      fail: (err) => {
        console.error('[Wechat] 获取位置失败:', err);
        reject(err);
      }
    });
  });
}

/**
 * 选择位置
 * @returns {Promise} 位置信息
 */
function chooseLocation() {
  return new Promise((resolve, reject) => {
    wx.chooseLocation({
      success: (res) => {
        resolve({
          name: res.name,
          address: res.address,
          latitude: res.latitude,
          longitude: res.longitude
        });
      },
      fail: (err) => {
        console.error('[Wechat] 选择位置失败:', err);
        reject(err);
      }
    });
  });
}

/**
 * 扫码
 * @param {boolean} onlyFromCamera 是否只允许相机扫码
 * @returns {Promise} 扫码结果
 */
function scanCode(onlyFromCamera = false) {
  return new Promise((resolve, reject) => {
    wx.scanCode({
      onlyFromCamera: onlyFromCamera,
      success: (res) => {
        resolve({
          result: res.result,
          scanType: res.scanType,
          charSet: res.charSet,
          path: res.path
        });
      },
      fail: (err) => {
        console.error('[Wechat] 扫码失败:', err);
        reject(err);
      }
    });
  });
}

/**
 * 订阅消息
 * @param {Array} tmplIds 模板ID数组
 * @returns {Promise} 订阅结果
 */
function requestSubscribeMessage(tmplIds) {
  return new Promise((resolve, reject) => {
    wx.requestSubscribeMessage({
      tmplIds: tmplIds,
      success: (res) => {
        console.log('[Wechat] 订阅消息成功:', res);
        resolve(res);
      },
      fail: (err) => {
        console.error('[Wechat] 订阅消息失败:', err);
        reject(err);
      }
    });
  });
}

/**
 * 保存图片到相册
 * @param {string} filePath 图片路径
 * @returns {Promise}
 */
function saveImageToPhotosAlbum(filePath) {
  return new Promise((resolve, reject) => {
    wx.saveImageToPhotosAlbum({
      filePath: filePath,
      success: (res) => {
        resolve(res);
      },
      fail: (err) => {
        console.error('[Wechat] 保存图片失败:', err);
        reject(err);
      }
    });
  });
}

/**
 * 预览图片
 * @param {Array} urls 图片URL数组
 * @param {string} current 当前显示图片URL
 */
function previewImage(urls, current) {
  wx.previewImage({
    urls: urls,
    current: current || urls[0]
  });
}

/**
 * 选择图片
 * @param {Object} options 配置选项
 * @returns {Promise} 图片信息
 */
function chooseImage(options = {}) {
  return new Promise((resolve, reject) => {
    wx.chooseImage({
      count: options.count || 1,
      sizeType: options.sizeType || ['compressed'],
      sourceType: options.sourceType || ['album', 'camera'],
      success: (res) => {
        resolve({
          tempFilePaths: res.tempFilePaths,
          tempFiles: res.tempFiles
        });
      },
      fail: (err) => {
        console.error('[Wechat] 选择图片失败:', err);
        reject(err);
      }
    });
  });
}

/**
 * 选择文件（聊天文件）
 * @param {Object} options 配置选项
 * @returns {Promise} 文件信息
 */
function chooseMessageFile(options = {}) {
  return new Promise((resolve, reject) => {
    wx.chooseMessageFile({
      count: options.count || 1,
      type: options.type || 'file',
      extension: options.extension || ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'txt'],
      success: (res) => {
        resolve({
          tempFiles: res.tempFiles.map(file => ({
            path: file.path,
            name: file.name,
            size: file.size,
            type: file.type,
            time: file.time
          }))
        });
      },
      fail: (err) => {
        console.error('[Wechat] 选择文件失败:', err);
        reject(err);
      }
    });
  });
}

/**
 * 显示加载提示
 * @param {string} title 提示文字
 * @param {boolean} mask 是否显示遮罩
 */
function showLoading(title = '加载中...', mask = true) {
  wx.showLoading({
    title: title,
    mask: mask
  });
}

/**
 * 隐藏加载提示
 */
function hideLoading() {
  wx.hideLoading();
}

/**
 * 显示成功提示
 * @param {string} title 提示文字
 * @param {number} duration 显示时长
 */
function showSuccess(title = '操作成功', duration = 1500) {
  wx.showToast({
    title: title,
    icon: 'success',
    duration: duration
  });
}

/**
 * 显示错误提示
 * @param {string} title 提示文字
 * @param {number} duration 显示时长
 */
function showError(title = '操作失败', duration = 2000) {
  wx.showToast({
    title: title,
    icon: 'error',
    duration: duration
  });
}

/**
 * 显示普通提示
 * @param {string} title 提示文字
 * @param {number} duration 显示时长
 */
function showToast(title, duration = 1500) {
  wx.showToast({
    title: title,
    icon: 'none',
    duration: duration
  });
}

/**
 * 显示确认对话框
 * @param {Object} options 配置选项
 * @returns {Promise} 用户选择结果
 */
function showModal(options = {}) {
  return new Promise((resolve) => {
    wx.showModal({
      title: options.title || '提示',
      content: options.content || '',
      confirmText: options.confirmText || '确定',
      cancelText: options.cancelText || '取消',
      showCancel: options.showCancel !== false,
      confirmColor: options.confirmColor || '#FF7D00',
      success: (res) => {
        resolve(res.confirm);
      }
    });
  });
}

/**
 * 显示操作菜单
 * @param {Array} itemList 选项列表
 * @returns {Promise} 选择的索引
 */
function showActionSheet(itemList) {
  return new Promise((resolve, reject) => {
    wx.showActionSheet({
      itemList: itemList,
      success: (res) => {
        resolve(res.tapIndex);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 设置剪贴板内容
 * @param {string} data 要复制的内容
 * @returns {Promise}
 */
function setClipboardData(data) {
  return new Promise((resolve, reject) => {
    wx.setClipboardData({
      data: data,
      success: (res) => {
        resolve(res);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 获取剪贴板内容
 * @returns {Promise} 剪贴板内容
 */
function getClipboardData() {
  return new Promise((resolve, reject) => {
    wx.getClipboardData({
      success: (res) => {
        resolve(res.data);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 检查授权状态
 * @param {string} scope 授权scope
 * @returns {Promise<boolean>} 是否已授权
 */
function checkAuth(scope) {
  return new Promise((resolve) => {
    wx.getSetting({
      success: (res) => {
        resolve(res.authSetting[scope] === true);
      },
      fail: () => {
        resolve(false);
      }
    });
  });
}

/**
 * 发起授权请求
 * @param {string} scope 授权scope
 * @returns {Promise} 授权结果
 */
function authorize(scope) {
  return new Promise((resolve, reject) => {
    wx.authorize({
      scope: scope,
      success: () => {
        resolve(true);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 打开设置页面
 * @returns {Promise} 设置结果
 */
function openSetting() {
  return new Promise((resolve) => {
    wx.openSetting({
      success: (res) => {
        resolve(res.authSetting);
      }
    });
  });
}

module.exports = {
  getUserProfile,
  getPhoneNumber,
  requestPayment,
  chooseAddress,
  getLocation,
  chooseLocation,
  scanCode,
  requestSubscribeMessage,
  saveImageToPhotosAlbum,
  previewImage,
  chooseImage,
  chooseMessageFile,
  showLoading,
  hideLoading,
  showSuccess,
  showError,
  showToast,
  showModal,
  showActionSheet,
  setClipboardData,
  getClipboardData,
  checkAuth,
  authorize,
  openSetting
};
