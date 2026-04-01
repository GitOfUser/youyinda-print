/**
 * 状态标签组件
 */
Component({
  properties: {
    // 标签文本
    text: {
      type: String,
      value: ''
    },
    // 类型：primary/success/warning/danger/info/default
    type: {
      type: String,
      value: 'default'
    },
    // 尺寸：small/medium/large
    size: {
      type: String,
      value: 'medium'
    },
    // 是否镂空
    plain: {
      type: Boolean,
      value: false
    }
  }
});
