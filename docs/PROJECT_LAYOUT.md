# 项目目录与维护边界

本仓库是 SmartWMS 的源码仓库。它包含后端、前端、Docker 编排与演示截图；构建缓存与本机配置不应提交。以下是当前工作区的目录说明，不代表每个外部服务已经运行。

| 路径 | 用途 | 处理原则 |
|---|---|---|
| `backend/src/main/java/com/smartwms/` | Spring Boot 业务模块、安全和 API | 源码，Git 跟踪 |
| `backend/src/main/resources/` | 环境配置模板和数据库脚本 | Git 跟踪；不得写入真实密钥 |
| `backend/src/test/` | 后端测试 | Git 跟踪 |
| `backend/target/` | Maven 编译与测试产物 | Git 忽略，不在整理时删除 |
| `frontend/src/` | Vue 页面、路由、状态、API、样式和测试 | Git 跟踪 |
| `frontend/node_modules/`、`frontend/dist/`、`frontend/coverage/` | 依赖、生产构建和覆盖率产物 | Git 忽略，不在整理时删除 |
| `docs/screenshots/` | README 用于展示的界面截图 | Git 跟踪；不是临时截图 |
| `.github/` | CI 与依赖更新配置 | Git 跟踪 |
| `.env.example`、`docker-compose.yml` | 本地/容器部署入口 | 跟踪模板，真实 `.env` 忽略 |
| `TESTING.md`、`CHANGELOG.md` | 测试策略和变更记录 | 保持文档与实际执行结果一致 |

本地工作目录的上一级可能存放原始 `设计文档.txt`；它不在 Git 仓库内。设计参考不代表功能已实现，上传前应单独审查。

## 当前工作区注意事项

- 提交或合并前先用 `git status --short` 检查当前分支及未提交改动，不要因为整理目录而覆盖既有工作。
- `docs/screenshots/` 是已跟踪的展示资源；不要像构建缓存一样移动或删除。
- Git 配置可能将 `https://github.com/...` 改写为第三方代理；上传私有数据前应核对实际推送目标，不要为方便推送而忽略来源检查。

## 验证入口

在仓库根目录执行：

```powershell
cd frontend
npm test
npm run build
cd ../backend
mvn test
```

如需环境变量、数据库和 Redis 的启动步骤，以 [README](../README.md) 及 [测试说明](../TESTING.md) 为准；不要将本地 `.env`、数据库或令牌加入 Git。
