# 机器学习 9 个考点 · 可交互学习 App

手机上的离线学习工具：9 个考点全部可交互，白天/黑夜双主题。

- 零依赖、零权限、完全离线
- 单文件 HTML（`app/src/main/assets/index.html`），WebView 加载
- 主题：跟随系统 / 手动浅色 / 手动深色，通过 `window.AndroidHost.applyChrome()` 同步原生状态栏颜色

## 构建

推送到 main 或手动触发 `Build APK`，产物在 Actions 的 `ML9-apk` artifact。

## 改内容

只改 `app/src/main/assets/index.html`，推送后自动重新构建。
