// 同源反代：nginx 将 /api 转发到后端 8080
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
        ElMessage.error(err.message || '网络请求失败');
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

const AdminAPI = {
    login: (username, password) => api.post('/admin/v1/login', { username, password }),
    logout: () => api.post('/admin/v1/logout'),
    getInfo: () => api.get('/admin/v1/info'),

    getDashboardStats: () => api.get('/admin/v1/dashboard/stats'),
    getDashboardChart: (days) => api.get('/admin/v1/dashboard/chart', { days }),
    getRecentOrders: () => api.get('/admin/v1/dashboard/recent-orders'),

    getOrderList: (params) => api.get('/admin/v1/order/list', params),
    getOrderDetail: (id) => api.get('/admin/v1/order/detail', { id }),
    updateOrderStatus: (id, status) => api.post('/admin/v1/order/status', { id, status }),

    getUserList: (params) => api.get('/admin/v1/user/list', params),
    updateUserStatus: (id, status) => api.post('/admin/v1/user/status', { id, status }),

    getPriceConfig: () => api.get('/admin/v1/price/config'),
    savePriceConfig: (data) => api.post('/admin/v1/price/config', data),

    getCouponList: (params) => api.get('/admin/v1/coupon/list', params),
    createCoupon: (data) => api.post('/admin/v1/coupon', data),
    updateCoupon: (data) => api.put('/admin/v1/coupon', data),
    deleteCoupon: (id) => api.delete('/admin/v1/coupon/' + id),

    getSysConfig: () => api.get('/admin/v1/system/config'),
    saveSysConfig: (data) => api.post('/admin/v1/system/config', data)
};

window.AdminAPI = AdminAPI;
