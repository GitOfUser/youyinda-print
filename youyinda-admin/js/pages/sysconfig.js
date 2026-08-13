AdminApp.component('sys-config', {
    template: `
        <div>
            <div class="page-header">
                <h2 class="page-title">系统配置</h2>
            </div>

            <el-tabs v-model="activeTab">
                <el-tab-pane label="第三方API配置" name="api">
                    <div class="card">
                        <el-form :model="apiConfig" label-width="140px" style="max-width: 700px;">
                            <el-divider content-position="left">微信小程序配置</el-divider>
                            <el-form-item label="AppID">
                                <el-input v-model="apiConfig.wxAppId" />
                            </el-form-item>
                            <el-form-item label="AppSecret">
                                <el-input v-model="apiConfig.wxAppSecret" type="password" show-password />
                            </el-form-item>
                            <el-form-item label="商户号">
                                <el-input v-model="apiConfig.wxMchId" />
                            </el-form-item>

                            <el-divider content-position="left">打印服务商配置</el-divider>
                            <el-form-item label="服务商名称">
                                <el-select v-model="apiConfig.printProvider" style="width: 100%;">
                                    <el-option label="易联云" value="yilianyun" />
                                    <el-option label="飞鹅云打印" value="feie" />
                                    <el-option label="自定义API" value="custom" />
                                </el-select>
                            </el-form-item>
                            <el-form-item label="API地址">
                                <el-input v-model="apiConfig.printApiUrl" placeholder="https://" />
                            </el-form-item>
                            <el-form-item label="API Key">
                                <el-input v-model="apiConfig.printApiKey" type="password" show-password />
                            </el-form-item>

                            <el-divider content-position="left">快递服务商配置</el-divider>
                            <el-form-item label="快递100 Key">
                                <el-input v-model="apiConfig.kuaidi100Key" />
                            </el-form-item>
                            <el-form-item label="快递鸟ID">
                                <el-input v-model="apiConfig.kdniaoId" />
                            </el-form-item>

                            <el-form-item>
                                <el-button type="primary" @click="saveConfig('api')">保存配置</el-button>
                                <el-button @click="testConnection">测试连接</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </el-tab-pane>

                <el-tab-pane label="基础设置" name="basic">
                    <div class="card">
                        <el-form :model="basicConfig" label-width="140px" style="max-width: 700px;">
                            <el-form-item label="平台名称">
                                <el-input v-model="basicConfig.siteName" />
                            </el-form-item>
                            <el-form-item label="客服电话">
                                <el-input v-model="basicConfig.servicePhone" />
                            </el-form-item>
                            <el-form-item label="客服微信">
                                <el-input v-model="basicConfig.serviceWechat" />
                            </el-form-item>
                            <el-form-item label="营业时间">
                                <el-time-picker v-model="basicConfig.businessHoursStart" format="HH:mm" value-format="HH:mm" placeholder="开始" style="width: 180px;" />
                                <span style="margin: 0 12px;">-</span>
                                <el-time-picker v-model="basicConfig.businessHoursEnd" format="HH:mm" value-format="HH:mm" placeholder="结束" style="width: 180px;" />
                            </el-form-item>
                            <el-form-item label="订单自动确认">
                                <el-switch v-model="basicConfig.autoConfirm" active-text="开启" inactive-text="关闭" />
                            </el-form-item>
                            <el-form-item label="订单通知">
                                <el-switch v-model="basicConfig.orderNotify" active-text="开启" inactive-text="关闭" />
                            </el-form-item>

                            <el-form-item>
                                <el-button type="primary" @click="saveConfig('basic')">保存配置</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </el-tab-pane>

                <el-tab-pane label="公告管理" name="notice">
                    <div class="card">
                        <div style="margin-bottom: 16px; text-align: right;">
                            <el-button type="primary" @click="addNotice">发布公告</el-button>
                        </div>
                        <el-table :data="noticeList" style="width: 100%">
                            <el-table-column prop="title" label="公告标题" />
                            <el-table-column prop="createTime" label="发布时间" width="180" />
                            <el-table-column prop="status" label="状态" width="100">
                                <template #default="{row}">
                                    <el-tag :type="row.status==='已发布'?'success':'info'" size="small">{{ row.status }}</el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column label="操作" width="180">
                                <template #default="{row}">
                                    <el-button type="primary" link size="small">编辑</el-button>
                                    <el-button type="danger" link size="small">删除</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
    `,
    data() {
        return {
            activeTab: 'api',
            apiConfig: {
                wxAppId: 'wx1234567890abcdef',
                wxAppSecret: '',
                wxMchId: '',
                printProvider: 'yilianyun',
                printApiUrl: 'https://open-api.yilianyun.net',
                printApiKey: '',
                kuaidi100Key: '',
                kdniaoId: ''
            },
            basicConfig: {
                siteName: '优印达',
                servicePhone: '400-888-8888',
                serviceWechat: 'youyinda-service',
                businessHoursStart: '08:00',
                businessHoursEnd: '22:00',
                autoConfirm: true,
                orderNotify: true
            },
            noticeList: [
                { id: 1, title: '系统维护通知：7月25日凌晨2点-4点系统升级', createTime: '2026-07-20 10:00', status: '已发布' },
                { id: 2, title: '新用户首单立减5元活动进行中', createTime: '2026-07-15 14:30', status: '已发布' }
            ]
        };
    },
    methods: {
        saveConfig(type) {
            ElementPlus.ElMessage.success('配置保存成功');
        },
        testConnection() {
            ElementPlus.ElMessage.success('连接测试成功');
        },
        addNotice() {
            ElementPlus.ElMessage.info('公告编辑功能开发中');
        }
    }
});
