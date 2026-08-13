AdminApp.component('users', {
    template: `
        <div>
            <div class="page-header">
                <h2 class="page-title">用户管理</h2>
            </div>

            <div class="filter-bar">
                <el-input v-model="search" placeholder="搜索昵称/手机号" style="width: 240px;" clearable />
                <el-button type="primary">查询</el-button>
                <el-button>重置</el-button>
            </div>

            <div class="card">
                <el-table :data="userList" style="width: 100%">
                    <el-table-column prop="id" label="ID" width="80" />
                    <el-table-column label="头像" width="80">
                        <template #default="{row}">
                            <el-avatar :size="40" style="background: linear-gradient(135deg, #FF7D00, #FF9A3C);">{{ row.nickname.charAt(0) }}</el-avatar>
                        </template>
                    </el-table-column>
                    <el-table-column prop="nickname" label="昵称" width="140" />
                    <el-table-column prop="phone" label="手机号" width="140" />
                    <el-table-column prop="orderCount" label="订单数" width="100" />
                    <el-table-column prop="totalAmount" label="累计消费" width="120">
                        <template #default="{row}">¥{{ row.totalAmount }}</template>
                    </el-table-column>
                    <el-table-column prop="registerTime" label="注册时间" width="180" />
                    <el-table-column prop="status" label="状态" width="100">
                        <template #default="{row}">
                            <el-tag :type="row.status === '正常' ? 'success' : 'danger'" size="small">{{ row.status }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="150" fixed="right">
                        <template #default="{row}">
                            <el-button type="primary" link size="small">详情</el-button>
                            <el-button :type="row.status==='正常'?'danger':'success'" link size="small" @click="toggleStatus(row)">
                                {{ row.status==='正常'?'禁用':'启用' }}
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div style="margin-top: 20px; text-align: right;">
                    <el-pagination
                        v-model:current-page="currentPage"
                        v-model:page-size="pageSize"
                        :page-sizes="[10, 20, 50]"
                        :total="total"
                        layout="total, sizes, prev, pager, next, jumper"
                        background />
                </div>
            </div>
        </div>
    `,
    data() {
        return {
            search: '',
            currentPage: 1,
            pageSize: 10,
            total: 128,
            userList: [
                { id: 1, nickname: '张三', phone: '138****1234', orderCount: 12, totalAmount: '256.00', registerTime: '2026-06-01 10:00:00', status: '正常' },
                { id: 2, nickname: '李四', phone: '139****5678', orderCount: 5, totalAmount: '89.00', registerTime: '2026-06-15 14:30:00', status: '正常' },
                { id: 3, nickname: '王五', phone: '137****9012', orderCount: 0, totalAmount: '0.00', registerTime: '2026-07-01 09:15:00', status: '禁用' },
                { id: 4, nickname: '赵六', phone: '136****3456', orderCount: 28, totalAmount: '680.00', registerTime: '2026-05-20 16:45:00', status: '正常' }
            ]
        };
    },
    methods: {
        toggleStatus(row) {
            row.status = row.status === '正常' ? '禁用' : '正常';
            ElementPlus.ElMessage.success('操作成功');
        }
    }
});
