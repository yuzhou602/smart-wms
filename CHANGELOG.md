# 更新日志

本文件记录 SmartWMS 各版本的重要变化。

## [1.0.1] - 2026-09-27

### Added

- 增加 GitHub Actions，自动执行前端构建、依赖安全检查、后端测试和 Docker Compose 配置校验。
- 增加 Dependabot 周期性依赖检查。
- 增加 JWT 与 CORS 安全配置测试，后端测试总数提升至 14 项。

### Changed

- 前端升级到 Vite 8、ECharts 6、Vue ECharts 8，并按需加载 Element Plus 组件和图标。
- 后端升级到 Spring Boot 3.5.16、Spring AI 1.0.9 和 Springdoc 2.8.17。
- Docker 部署默认只在本机暴露数据库、Redis 和后端端口，后端容器使用非 root 用户运行。
- README 调整为与当前代码能力一致的功能说明和生产部署说明。

### Fixed

- 修复前端依赖中的已知高危和中危安全公告，完整 `npm audit` 结果为 0。
- 生产环境默认关闭 Swagger，并将跨域访问改为显式白名单配置。
- OpenAI 聊天、嵌入、图像、语音和审核模型默认关闭，不再影响无 AI 配置的生产启动。
