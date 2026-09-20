window.UsersPage = {
    name: 'users',
    data() {
        return {
            loading: false,
            userList: [],
            total: 0,
            query: {
                pageNum: 1,
                pageSize: 10,
                nickname: '',
                phone: ''
            }
        };
    },
    mounted() {
        this.loadUsers();
    },
    methods: {
        formatMoney(v) {
            return v === null || v === undefined ? '0.00' : Number(v).toFixed(2);
        },
        formatDate(v) {
            if (!v) return '-';
            return String(v).replace('T', ' ').slice(0, 19);
        },
        handleSearch() {
            this.query.pageNum = 1;
            this.loadUsers();
        },
        handleReset() {
            this.query = { pageNum: 1, pageSize: 10, nickname: '', phone: '' };
            this.loadUsers();
        },
        async loadUsers() {
            this.loading = true;
            try {
                const params = {
                    pageNum: this.query.pageNum,
                    pageSize: this.query.pageSize,
                    nickname: this.query.nickname || undefined,
                    phone: this.query.phone || undefined
                };
                const res = await AdminAPI.getUserList(params);
                this.userList = res.records || [];
                this.total = res.total || 0;
            } catch (e) {
                console.error('load users failed:', e);
            } finally {
                this.loading = false;
            }
        },
        handlePageChange(page) {
            this.query.pageNum = page;
            this.loadUsers();
        },
        handleSizeChange(size) {
            this.query.pageSize = size;
            this.query.pageNum = 1;
            this.loadUsers();
        },
        async handleToggleStatus(row) {
            const action = Number(row.status) === 1 ? '禁用' : '启用';
            try {
                await ElMessageBox.confirm(`确定${action}用户「${row.nickname || row.id}」吗？`, `${action}用户`, { type: 'warning' });
            } catch (e) {
                return;
            }
            const newStatus = Number(row.status) === 1 ? 0 : 1;
            try {
                await AdminAPI.updateUserStatus(row.id, newStatus);
                ElMessage.success(`用户已${action}`);
                this.loadUsers();
            } catch (e) {
                console.error('toggle user status failed:', e);
            }
        }
    },
    template: `
    <div class="page-container">
        <div class="page-header">
            <h2>用户管理</h2>
        </div>

        <div class="filter-bar">
            <el-input v-model="query.nickname" placeholder="昵称" clearable style="width: 200px;" @keyup.enter="handleSearch" />
            <el-input v-model="query.phone" placeholder="手机号" clearable style="width: 180px;" @keyup.enter="handleSearch" />
            <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>&nbsp;查询
            </el-button>
            <el-button @click="handleReset">重置</el-button>
        </div>

        <div class="table-card">
            <el-table v-loading="loading" :data="userList" stripe style="width: 100%">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="nickname" label="昵称" min-width="130" />
                <el-table-column prop="phone" label="手机号" width="130" />
                <el-table-column label="余额" width="110">
                    <template #default="{ row }">¥{{ formatMoney(row.balance) }}</template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag size="small" :type="Number(row.status) === 1 ? 'success' : 'danger'">
                            {{ Number(row.status) === 1 ? '正常' : '禁用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="注册时间" min-width="150">
                    <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="110" fixed="right">
                    <template #default="{ row }">
                        <el-button link :type="Number(row.status) === 1 ? 'danger' : 'success'" size="small" @click="handleToggleStatus(row)">
                            {{ Number(row.status) === 1 ? '禁用' : '启用' }}
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination-wrap">
                <el-pagination
                    background
                    layout="total, sizes, prev, pager, next"
                    :total="total"
                    :current-page="query.pageNum"
                    :page-size="query.pageSize"
                    :page-sizes="[10, 20, 50]"
                    @current-change="handlePageChange"
                    @size-change="handleSizeChange"
                />
            </div>
        </div>
    </div>
    `
};
