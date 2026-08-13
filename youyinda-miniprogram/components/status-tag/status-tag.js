/**
 * 状态标签组件
 */
Component({
  options: {
    styleIsolation: 'apply-shared'
  },

  properties: {
    text: {
      type: String,
      value: ''
    },
    type: {
      type: String,
      value: 'default'
    },
    size: {
      type: String,
      value: 'medium'
    },
    plain: {
      type: Boolean,
      value: false
    },
    round: {
      type: Boolean,
      value: false
    },
    dot: {
      type: Boolean,
      value: false
    }
  }
});
