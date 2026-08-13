const request = require('../../utils/request');
const util = require('../../utils/util');

// 后端状态码映射（与 OrderMain.status 对齐）
const STATUS_MAP = {
  '待支付': 1,
  '待打印': 2,
  '待发货': 3,
  '待收货': 4,
  '已取消': 5,
  '已完成': 6,
  '售后中': 7
};

// 状态码→展示文本（覆盖打印/快递的通用映射）
const STATUS_TEXT = {
  1: '待付款',
  2: '待处理',
  3: '待发货',
  4: '待收货',
  5: '已取消',
  6: '已完成',
  7: '售后中'
};

Page({
  data: {
    currentTab: 0,
    currentStatus: -1,
    orderList: []
  },

  onLoad() {
    this.loadOrderList();
  },

  onShow() {
    this.loadOrderList();
  },

  switchTab(e) {
    this.setData({
      currentTab: parseInt(e.currentTarget.dataset.index),
      currentStatus: -1
    });
    this.loadOrderList();
  },

  switchStatus(e) {
    this.setData({
      currentStatus: parseInt(e.currentTarget.dataset.status)
    });
    this.loadOrderList();
  },

  loadOrderList() {
    wx.showLoading({
      title: '加载中...'
    });

    const tab = this.data.currentTab;
    const promises = [];
    const filterStatus = this.data.currentStatus;

    // 全部 / 打印 → 拉取打印订单
    if (tab === 0 || tab === 1) {
      promises.push(
        request.get('/print/order/list').then(res => {
          const list = Array.isArray(res) ? res : [];
          return list.map(item => this.normalize(item, 1));
        }).catch(() => [])
      );
    }
    // 全部 / 快递 → 拉取快递订单
    if (tab === 0 || tab === 2) {
      promises.push(
        request.get('/express/order/list').then(res => {
          const list = (res && res.records) || (Array.isArray(res) ? res : []);
          return list.map(item => this.normalize(item, 2));
        }).catch(() => [])
      );
    }

    Promise.all(promises).then(results => {
      wx.hideLoading();
      let orderList = results.flat();

      // 按状态筛选
      if (filterStatus !== -1) {
        orderList = orderList.filter(item => item.statusNum === filterStatus);
      }

      // 按创建时间倒序
      orderList.sort((a, b) => {
        const t1 = a.createTime ? new Date(a.createTime).getTime() : 0;
        const t2 = b.createTime ? new Date(b.createTime).getTime() : 0;
        return t2 - t1;
      });

      this.setData({ orderList });
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    });
  },

  /**
   * 标准化后端响应，统一 statusNum / statusText / orderType
   * @param {Object} item 后端返回的订单对象
   * @param {Number} orderType 1=打印, 2=快递
   */
  normalize(item, orderType) {
    // 后端返回的 status 可能是中文文本（PrintOrderVO）或数字（ExpressOrderVO）
    const rawStatus = item.status;
    let statusNum;
    if (typeof rawStatus === 'number') {
      statusNum = rawStatus;
    } else {
      statusNum = STATUS_MAP[rawStatus] || 0;
    }

    return {
      ...item,
      orderType: orderType,
      statusNum: statusNum,
      status: statusNum,
      statusClass: this.getStatusClass(statusNum),
      statusTag: this.getStatusTag(statusNum),
      statusText: this.getStatusText(statusNum),
      createTime: util.formatTime(item.createTime),
      canPay: statusNum === 1,
      canCancel: statusNum === 1,
      canTrack: orderType === 2 && statusNum >= 3
    };
  },

  getStatusClass(statusNum) {
    if (statusNum === 1) return 'pending';
    if (statusNum === 5) return 'canceled';
    if (statusNum === 6) return 'success';
    return 'processing';
  },

  getStatusTag(statusNum) {
    const tagMap = {
      1: 'tag-warning',
      2: 'tag-primary',
      3: 'tag-primary',
      4: 'tag-print',
      5: 'tag-gray',
      6: 'tag-success',
      7: 'tag-gray'
    };
    return tagMap[statusNum] || 'tag-gray';
  },

  getStatusText(statusNum) {
    return STATUS_TEXT[statusNum] || '未知';
  },

  goToDetail(e) {
    wx.navigateTo({
      url: `/pages/order/detail?id=${e.currentTarget.dataset.id}`
    });
  },

  payOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    request.post('/wx-pay/create', {
      orderId: orderId
    }).then(payParams => {
      wx.requestPayment({
        ...payParams,
        success: () => {
          wx.showToast({
            title: '支付成功',
            icon: 'success'
          });
          setTimeout(() => {
            this.loadOrderList();
          }, 1500);
        },
        fail: () => {
          wx.showToast({
            title: '支付取消',
            icon: 'none'
          });
        }
      });
    }).catch(err => {
      wx.showToast({
        title: err.message || '支付失败',
        icon: 'none'
      });
    });
  },

  cancelOrder(e) {
    const item = this.data.orderList.find(o => o.id === e.currentTarget.dataset.id);
    const orderType = item ? item.orderType : this.data.currentTab;
    wx.showModal({
      title: '提示',
      content: '确定要取消订单吗？',
      success: (res) => {
        if (res.confirm) {
          const cancelUrl = orderType === 2
            ? `/express/order/${e.currentTarget.dataset.id}/cancel`
            : `/print/order/cancel/${e.currentTarget.dataset.id}`;
          request.post(cancelUrl).then(() => {
            wx.showToast({
              title: '取消成功',
              icon: 'success'
            });
            setTimeout(() => {
              this.loadOrderList();
            }, 1500);
          }).catch(err => {
            wx.showToast({
              title: err.message || '取消失败',
              icon: 'none'
            });
          });
        }
      }
    });
  },

  goToTrack(e) {
    wx.navigateTo({
      url: `/pages/express/track?orderId=${e.currentTarget.dataset.id}`
    });
  },

  goToHome() {
    wx.switchTab({
      url: '/pages/index/index'
    });
  }
});
