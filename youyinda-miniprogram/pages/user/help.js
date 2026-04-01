Page({
  data: {
    helpList: [
      {
        question: '如何上传文件进行打印？',
        answer: '您可以点击首页的"文档打印"按钮，选择从微信聊天、手机相册或文件管理中选择需要打印的文件，支持Word、Excel、PDF、图片等多种格式。',
        expanded: false
      },
      {
        question: '打印订单多久可以发货？',
        answer: '一般情况下，您的订单会在支付成功后2小时内开始处理，当天内发货。具体发货时间可能因订单量和打印服务商的处理情况有所不同。',
        expanded: false
      },
      {
        question: '快递寄件支持哪些快递公司？',
        answer: '我们支持顺丰、中通、圆通、韵达等多家主流快递公司，您可以根据价格和时效偏好进行选择。',
        expanded: false
      },
      {
        question: '如何申请退款？',
        answer: '如果您的订单还未开始处理，可以在订单详情页直接申请取消订单，款项将原路退回。如果订单已开始处理，请联系客服协助处理。',
        expanded: false
      },
      {
        question: '优惠券如何使用？',
        answer: '在订单确认页面，您可以选择可用的优惠券进行抵扣。每张优惠券都有对应的使用条件和有效期，请在有效期内使用。',
        expanded: false
      }
    ]
  },

  toggleAnswer(e) {
    const index = e.currentTarget.dataset.index;
    const helpList = this.data.helpList;
    helpList[index].expanded = !helpList[index].expanded;
    this.setData({ helpList });
  },

  contactService() {
    wx.showToast({
      title: '客服功能开发中',
      icon: 'none'
    });
  },

  makePhoneCall() {
    wx.makePhoneCall({
      phoneNumber: '400-123-4567'
    });
  }
});
