const { createApp } = Vue;
const { ElMessage, ElMessageBox } = ElementPlus;

window.AdminApp = createApp({
    data() {
        return {
            activeMenu: 'dashboard',
            menuTitles: {
                'dashboard': '数据仪表盘',
                'orders': '订单管理',
                'users': '用户管理',
                'price-base': '基础价格配置',
                'profit-rule': '盈利规则配置',
                'coupon': '优惠券管理',
                'sys-config': '系统配置'
            }
        };
    },
    computed: {
        currentPageTitle() {
            return this.menuTitles[this.activeMenu] || '';
        }
    },
    methods: {
        handleMenuSelect(index) {
            this.activeMenu = index;
        }
    }
});

AdminApp.use(ElementPlus);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    AdminApp.component(key, component);
}
