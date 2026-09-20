const { createApp } = Vue;
const { ElMessage, ElMessageBox } = ElementPlus;

window.AdminApp = createApp({
    data() {
        return {
            loggedIn: !!localStorage.getItem('admin_token'),
            loginForm: { username: '', password: '' },
            loginLoading: false,
            adminInfo: null,
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
        },
        adminDisplayName() {
            if (this.adminInfo) {
                return this.adminInfo.realName || this.adminInfo.username || '管理员';
            }
            return '管理员';
        }
    },
    methods: {
        handleMenuSelect(index) {
            this.activeMenu = index;
        },
        async doLogin() {
            if (!this.loginForm.username || !this.loginForm.password) {
                ElMessage.warning('请输入用户名和密码');
                return;
            }
            this.loginLoading = true;
            try {
                const res = await AdminAPI.login(this.loginForm.username, this.loginForm.password);
                localStorage.setItem('admin_token', res.token);
                this.loggedIn = true;
                this.loginForm.password = '';
                await this.fetchAdminInfo();
                ElMessage.success('登录成功');
            } catch (e) {
                console.error('login failed:', e);
            } finally {
                this.loginLoading = false;
            }
        },
        async fetchAdminInfo() {
            try {
                this.adminInfo = await AdminAPI.getInfo();
            } catch (e) {
                console.error('fetch info failed:', e);
            }
        },
        async doLogout() {
            try {
                await AdminAPI.logout();
            } catch (e) {
                console.error('logout api failed:', e);
            }
            localStorage.removeItem('admin_token');
            this.loggedIn = false;
            this.adminInfo = null;
            this.activeMenu = 'dashboard';
            ElMessage.success('已退出登录');
        }
    },
    async mounted() {
        if (this.loggedIn) {
            await this.fetchAdminInfo();
        }
    }
});

AdminApp.use(ElementPlus);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    AdminApp.component(key, component);
}
