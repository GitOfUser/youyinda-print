window.SysConfigPage = {
    name: 'sysconfig',
    data() {
        return {
            activeTab: 'third',
            // ===== 第三方API =====
            thirdLoading: false,
            thirdList: [],
            thirdTotal: 0,
            thirdQuery: { pageNum: 1, pageSize: 10, apiType: '' },
            thirdDialogVisible: false,
            thirdForm: { providerCode: '', providerName: '', apiType: '', apiUrl: '', appId: '', appSecret: '', timeout: 10, status: 1 },
            // ===== 基础设置 =====
            baseLoading: false,
            baseSaving: false,
            baseConfigs: [],
            // ===== 公告管理 =====
            noticeList: []
        };
    },
    mounted() {
        this.loadThirdConfigs();
        this.loadBaseConfigs();
    },
    methods: {
        formatDate(v) {
            if (!v) return '-';
            return String(v).replace('T', ' ').slice(0, 19);
        },
        // ==================== 第三方API ====================
        async loadThirdConfigs() {
            this.thirdLoading = true;
            try {
                const res = await AdminAPI.getThirdApiList({
                    pageNum: this.thirdQuery.pageNum,
                    pageSize: this.thirdQuery.pageSize,
                    apiType: this.thirdQuery.apiType || undefined
                });
                this.thirdList = res.records || [];
                this.thirdTotal = res.total || 0;
            } catch (e) {
                console.error('load third api failed:', e);
            } finally {
                this.thirdLoading = false;
            }
        },
        thirdSearch() {
            this.thirdQuery.pageNum = 1;
            this.loadThirdConfigs();
        },
        thirdPageChange(page) {
            this.thirdQuery.pageNum = page;
            this.loadThirdConfigs();
        },
        openThirdDialog(row) {
            this.thirdForm = row ? { ...row } : { providerCode: '', providerName: '', apiType: '', apiUrl: '', appId: '', appSecret: '', timeout: 10, status: 1 };
            this.thirdDialogVisible = true;
        },
        async saveThird() {
            try {
                if (this.thirdForm.id) {
                    await AdminAPI.updateThirdApi(this.thirdForm);
                } else {
                    await AdminAPI.createThirdApi(this.thirdForm);
                }
                ElMessage.success('保存成功');
                this.thirdDialogVisible = false;
                this.loadThirdConfigs();
            } catch (e) {
                console.error('save third api failed:', e);
            }
        },
        async deleteThird(row) {
            try {
                await ElMessageBox.confirm(`确定删除第三方配置「${row.providerName}」吗？`, '删除确认', { type: 'warning' });
            } catch (e) {
                return;
            }
            try {
                await AdminAPI.deleteThirdApi(row.id);
                ElMessage.success('删除成功');
                this.loadThirdConfigs();
            } catch (e) {
                console.error('delete third api failed:', e);
            }
        },
        async syncThird(row) {
            try {
                await AdminAPI.syncThirdApi(row.id);
                ElMessage.success(`已触发「${row.providerName}」价格同步`);
            } catch (e) {
                console.error('sync third api failed:', e);
            }
        },
        // ==================== 基础设置 ====================
        async loadBaseConfigs() {
            this.baseLoading = true;
            try {
                const list = await AdminAPI.getAllSysConfig();
                this.baseConfigs = (list || []).map(c => ({
                    id: c.id,
                    configKey: c.configKey,
                    configValue: c.configValue,
                    configType: c.configType,
                    description: c.description,
                    isActive: c.isActive
                }));
            } catch (e) {
                console.error('load base configs failed:', e);
            } finally {
                this.baseLoading = false;
            }
        },
        async saveBaseConfig() {
            this.baseSaving = true;
            try {
                for (const cfg of this.baseConfigs) {
                    await AdminAPI.saveSysConfig({ id: cfg.id, configKey: cfg.configKey, configValue: cfg.configValue, configType: cfg.configType, description: cfg.description, isActive: cfg.isActive });
                }
                ElMessage.success('基础设置已保存');
                this.loadBaseConfigs();
            } catch (e) {
                console.error('save base config failed:', e);
            } finally {
                this.baseSaving = false;
            }
        }
    },
    template: `
    <div class="page-container">
        <div class="page-header">
            <h2>系统配置</h2>
        </div>
        <el-tabs v-model="activeTab">
            <el-tab-pane label="第三方API配置" name="third">
                <div class="filter-bar">
                    <el-input v-model="thirdQuery.apiType" placeholder="API类型" clearable style="width: 180px;" @keyup.enter="thirdSearch" />
                    <el-button type="primary" @click="thirdSearch">查询</el-button>
                    <el-button type="primary" plain style="float: right;" @click="openThirdDialog()">新增配置</el-button>
                </div>
                <div class="table-card">
                    <el-table v-loading="thirdLoading" :data="thirdList" stripe>
                        <el-table-column prop="providerCode" label="服务商编码" min-width="130" />
                        <el-table-column prop="providerName" label="服务商名称" min-width="140" />
                        <el-table-column prop="apiType" label="API类型" width="110" />
                        <el-table-column prop="apiUrl" label="接口地址" min-width="180" show-overflow-tooltip />
                        <el-table-column prop="appId" label="AppId" min-width="140" show-overflow-tooltip />
                        <el-table-column label="超时(秒)" width="90">
                            <template #default="{ row }">{{ row.timeout }}</template>
                        </el-table-column>
                        <el-table-column label="状态" width="90">
                            <template #default="{ row }">
                                <el-tag size="small" :type="Number(row.status) === 1 ? 'success' : 'info'">{{ Number(row.status) === 1 ? '启用' : '停用' }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="200" fixed="right">
                            <template #default="{ row }">
                                <el-button link type="primary" size="small" @click="openThirdDialog(row)">编辑</el-button>
                                <el-button link type="warning" size="small" @click="syncThird(row)">同步价格</el-button>
                                <el-button link type="danger" size="small" @click="deleteThird(row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="pagination-wrap">
                        <el-pagination background layout="total, prev, pager, next" :total="thirdTotal" :current-page="thirdQuery.pageNum" :page-size="thirdQuery.pageSize" @current-change="thirdPageChange" />
                    </div>
                </div>
            </el-tab-pane>

            <el-tab-pane label="基础设置" name="base">
                <div v-loading="baseLoading" class="table-card" style="max-width: 720px;">
                    <div class="card-title">系统基础参数</div>
                    <el-table :data="baseConfigs" stripe>
                        <el-table-column prop="description" label="配置项" min-width="150" />
                        <el-table-column prop="configKey" label="Key" min-width="150" />
                        <el-table-column label="值" min-width="200">
                            <template #default="{ row }">
                                <el-input v-model="row.configValue" size="small" />
                            </template>
                        </el-table-column>
                    </el-table>
                    <div style="margin-top: 16px; text-align: right;">
                        <el-button type="primary" :loading="baseSaving" @click="saveBaseConfig">保存设置</el-button>
                    </div>
                </div>
            </el-tab-pane>

            <el-tab-pane label="公告管理" name="notice">
                <el-alert type="info" :closable="false" show-icon title="公告管理暂未提供后端接口（youyinda-backend 无公告相关 Controller），该模块当前展示占位数据，可后续接入内容管理模块。" />
                <div class="table-card" style="margin-top: 16px;">
                    <el-table :data="noticeList" empty-text="暂无公告数据">
                        <el-table-column prop="title" label="标题" min-width="200" />
                        <el-table-column prop="createTime" label="发布时间" width="180" />
                        <el-table-column prop="status" label="状态" width="100" />
                    </el-table>
                </div>
            </el-tab-pane>
        </el-tabs>

        <el-dialog v-model="thirdDialogVisible" title="第三方API配置" width="560px">
            <el-form label-width="100px">
                <el-form-item label="服务商编码">
                    <el-input v-model="thirdForm.providerCode" placeholder="如：yilianyun / kuaidi100" />
                </el-form-item>
                <el-form-item label="服务商名称">
                    <el-input v-model="thirdForm.providerName" placeholder="如：易联云" />
                </el-form-item>
                <el-form-item label="API类型">
                    <el-select v-model="thirdForm.apiType" style="width: 100%;">
                        <el-option label="打印" value="print" />
                        <el-option label="快递" value="express" />
                        <el-option label="OCR" value="ocr" />
                        <el-option label="支付" value="pay" />
                    </el-select>
                </el-form-item>
                <el-form-item label="接口地址">
                    <el-input v-model="thirdForm.apiUrl" placeholder="https://..." />
                </el-form-item>
                <el-form-item label="AppId">
                    <el-input v-model="thirdForm.appId" />
                </el-form-item>
                <el-form-item label="AppSecret">
                    <el-input v-model="thirdForm.appSecret" show-password />
                </el-form-item>
                <el-form-item label="超时(秒)">
                    <el-input-number v-model="thirdForm.timeout" :min="1" :step="1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="thirdForm.status" :active-value="1" :inactive-value="0" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="thirdDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveThird">保存</el-button>
            </template>
        </el-dialog>
    </div>
    `
};
