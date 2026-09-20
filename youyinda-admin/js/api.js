// 同源反代：nginx 将 /api 转发到后端 8080；本地联调请自行将 API_BASE 指向后端地址
const API_BASE = '/api';

function request(url, options = {}) {
    const token = localStorage.getItem('admin_token');
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    return fetch(API_BASE + url, {
        ...options,
        headers: headers
    }).then(async res => {
        const data = await res.json();
        if (data.code === 200) {
            return data.data;
        } else if (data.code === 401) {
            localStorage.removeItem('admin_token');
            window.location.reload();
            throw new Error('未登录或登录已过期');
        } else {
            throw new Error(data.message || '请求失败');
        }
    }).catch(err => {
        console.error('API Error:', err);
        ElementPlus.ElMessage.error(err.message || '网络请求失败');
        throw err;
    });
}

const api = {
    get: (url, params) => {
        let query = '';
        if (params) {
            const searchParams = new URLSearchParams();
            Object.keys(params).forEach(key => {
                if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
                    searchParams.append(key, params[key]);
                }
            });
            query = '?' + searchParams.toString();
        }
        return request(url + query, { method: 'GET' });
    },
    post: (url, data) => request(url, {
        method: 'POST',
        body: JSON.stringify(data || {})
    }),
    put: (url, data) => request(url, {
        method: 'PUT',
        body: JSON.stringify(data || {})
    }),
    delete: (url) => request(url, { method: 'DELETE' })
};

// 与后端 controller 映射逐条对齐（路径/方法/参数以 youyinda-backend 实际 @XxxMapping 为准）
const AdminAPI = {
    // ===== 登录/管理员 =====
    login: (username, password) => api.post('/admin/v1/login', { username, password }),
    logout: () => api.post('/admin/v1/logout'),
    getInfo: () => api.get('/admin/v1/info'),
    getAdminUserList: (params) => api.get('/admin/v1/user/list', params),
    saveAdminUser: (data) => data.id ? api.put('/admin/v1/user', data) : api.post('/admin/v1/user', data),
    deleteAdminUser: (id) => api.delete('/admin/v1/user/' + id),
    getRoleList: () => api.get('/admin/v1/role/list'),
    saveRole: (data) => api.post('/admin/v1/role', data),
    deleteRole: (id) => api.delete('/admin/v1/role/' + id),
    getPermissionList: () => api.get('/admin/v1/permission/list'),

    // ===== 数据仪表盘 =====
    getDashboardStats: () => api.get('/admin/v1/dashboard/stats'),
    getDashboardChart: (days) => api.get('/admin/v1/dashboard/chart', { days }),
    getRecentOrders: () => api.get('/admin/v1/dashboard/recent-orders'),

    // ===== 订单管理 =====
    getOrderList: (params) => api.get('/admin/v1/orders', params),
    getOrderDetail: (id) => api.get('/admin/v1/orders/' + id),
    updateOrderStatus: (id, status) => api.put('/admin/v1/orders/' + id + '/status?status=' + status),
    refundOrder: (id, reason) => api.post('/admin/v1/orders/' + id + '/refund?reason=' + encodeURIComponent(reason || '')),

    // ===== 用户管理 =====
    getUserList: (params) => api.get('/admin/v1/users', params),
    getUserDetail: (id) => api.get('/admin/v1/users/' + id),
    updateUserStatus: (id, status) => api.put('/admin/v1/users/' + id + '/status?status=' + status),

    // ===== 基础价格（打印/快递）=====
    getPrintPriceList: (params) => api.get('/admin/v1/prices/print', params),
    getPrintPrice: (id) => api.get('/admin/v1/prices/print/' + id),
    createPrintPrice: (data) => api.post('/admin/v1/prices/print', data),
    updatePrintPrice: (data) => data.id ? api.put('/admin/v1/prices/print', data) : api.post('/admin/v1/prices/print', data),
    deletePrintPrice: (id) => api.delete('/admin/v1/prices/print/' + id),
    getExpressPriceList: (params) => api.get('/admin/v1/prices/express', params),
    getExpressPrice: (id) => api.get('/admin/v1/prices/express/' + id),
    createExpressPrice: (data) => api.post('/admin/v1/prices/express', data),
    updateExpressPrice: (data) => data.id ? api.put('/admin/v1/prices/express', data) : api.post('/admin/v1/prices/express', data),
    deleteExpressPrice: (id) => api.delete('/admin/v1/prices/express/' + id),

    // ===== 盈利规则 =====
    getProfitRuleList: (params) => api.get('/admin/v1/profit-rules', params),
    getProfitRule: (id) => api.get('/admin/v1/profit-rules/' + id),
    createProfitRule: (data) => api.post('/admin/v1/profit-rules', data),
    updateProfitRule: (data) => data.id ? api.put('/admin/v1/profit-rules', data) : api.post('/admin/v1/profit-rules', data),
    deleteProfitRule: (id) => api.delete('/admin/v1/profit-rules/' + id),

    // ===== 优惠券 =====
    getCouponList: (params) => api.get('/admin/v1/coupons', params),
    getCouponDetail: (id) => api.get('/admin/v1/coupons/' + id),
    createCoupon: (data) => api.post('/admin/v1/coupons', data),
    updateCoupon: (data) => data.id ? api.put('/admin/v1/coupons', data) : api.post('/admin/v1/coupons', data),
    deleteCoupon: (id) => api.delete('/admin/v1/coupons/' + id),

    // ===== 系统配置 =====
    getSysConfigList: (params) => api.get('/admin/v1/sys-config', params),
    getAllSysConfig: () => api.get('/admin/v1/sys-config/all'),
    getSysConfig: (id) => api.get('/admin/v1/sys-config/' + id),
    createSysConfig: (data) => api.post('/admin/v1/sys-config', data),
    saveSysConfig: (data) => data.id ? api.put('/admin/v1/sys-config/' + data.id, data) : api.post('/admin/v1/sys-config', data),
    deleteSysConfig: (id) => api.delete('/admin/v1/sys-config/' + id),

    // ===== 第三方 API =====
    getThirdApiList: (params) => api.get('/admin/v1/third-api', params),
    getThirdApi: (id) => api.get('/admin/v1/third-api/' + id),
    createThirdApi: (data) => api.post('/admin/v1/third-api', data),
    updateThirdApi: (data) => data.id ? api.put('/admin/v1/third-api/' + data.id, data) : api.post('/admin/v1/third-api', data),
    deleteThirdApi: (id) => api.delete('/admin/v1/third-api/' + id),
    syncThirdApi: (id) => api.post('/admin/v1/third-api/sync/' + id),

    // ===== 操作日志 =====
    getLogList: (params) => api.get('/admin/v1/logs', params)
};

window.AdminAPI = AdminAPI;
