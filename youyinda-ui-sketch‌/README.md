# 优印达 UI 设计方案 · 本地预览说明

## 📁 文件说明

| 文件 | 说明 |
|------|------|
| `index.html` | 设计方案导航首页（入口） |
| `miniapp.html` | 微信小程序端 · 8 个高保真页面 |
| `admin.html` | PC 端运营管理后台 · 7 个高保真页面 |
| `启动预览.bat` | 一键启动本地预览脚本 |

---

## 🚀 方式一：双击脚本（推荐）

直接双击 `启动预览.bat`，脚本会自动：
1. 检测是否安装了 Python
2. 如有 Python → 启动 HTTP Server 并自动打开浏览器
3. 如无 Python → 直接用浏览器打开文件

---

## 🌐 方式二：Python HTTP Server（手动）

在 `youyinda-ui/` 目录打开命令行，执行：

```powershell
# Windows PowerShell
cd "c:\Users\22915\WorkBuddy\Claw\youyinda-ui"
python -m http.server 8888
```

然后在浏览器访问：

- 导航首页：http://localhost:8888/index.html
- 小程序端：http://localhost:8888/miniapp.html
- 管理后台：http://localhost:8888/admin.html

> **注意**：若提示端口被占用（WinError 10013），将 `8888` 替换为其他端口如 `9000`、`7777`。

---

## 📂 方式三：直接用浏览器打开文件

HTML 文件为**完全自包含**的单文件，无需服务器即可直接在浏览器中查看。

**步骤：**
1. 在资源管理器中找到 `youyinda-ui` 文件夹
2. 右键 `index.html` → 选择「打开方式」→ 选择 Chrome / Edge 浏览器
3. 在页面中点击对应链接跳转到小程序端或管理后台

> 由于 `file://` 协议限制，部分页面间跳转链接可能需要在同一文件夹下直接打开对应 HTML 文件。

---

## 💡 推荐使用 VS Code Live Server（开发者）

如已安装 VS Code，可通过以下方式预览：
1. 用 VS Code 打开 `youyinda-ui` 文件夹
2. 安装扩展：[Live Server](https://marketplace.visualstudio.com/items?itemName=ritwickdey.LiveServer)
3. 右键 `index.html` → 选择「Open with Live Server」
4. 浏览器自动打开，支持实时刷新

---

## 🎨 设计规范速查

| 项目 | 值 |
|------|----|
| 主色 | `#FF7D00` 活力橙 |
| 成功色 | `#00B42A` 安全绿 |
| 错误色 | `#F53F3F` 警示红 |
| 信息色 | `#165DFF` 信息蓝 |
| 背景色 | `#F9F6F0` 护眼米白 |
| 标题色 | `#1D2129` |
| 按钮圆角 | 8px |
| 卡片圆角 | 12px |
| 小程序尺寸 | 375px |
| PC后台尺寸 | 1920×1080 |
