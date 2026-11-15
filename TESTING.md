# 测试指南

SmartWMS 使用自动化测试保护登录会话、权限控制、后端安全配置和应用启动等关键路径。新增功能或修复缺陷时，应同时增加能复现对应行为的测试。

## 前端测试

前端使用 Vitest 5、jsdom 和 V8 覆盖率工具。测试文件与源码放在同一目录，命名为 `*.test.ts`。

```bash
cd frontend

# 执行单元测试
npm test

# 执行测试并检查覆盖率门禁
npm run test:coverage
```

当前覆盖率门禁要求 `src/stores/user.ts` 和 `src/directives/permission.ts` 达到 100%。路由文件包含大量仅负责懒加载页面的函数，因此 `src/router/index.ts` 单独要求语句和行覆盖率不低于 50%、函数覆盖率不低于 20%、分支覆盖率不低于 90%，重点约束认证与权限守卫分支。

覆盖率只代表被纳入统计的代码，不替代真实浏览器端到端测试。

测试约定：

- 每个测试描述一个可观察行为，不只检查对象是否存在。
- 使用 `beforeEach` 重置 Pinia 和 `localStorage`，避免测试相互影响。
- 网络请求必须模拟，不连接真实后端或第三方服务。
- 新增条件分支时，至少覆盖成功和失败路径。

## 后端测试

后端使用 JUnit 5 和 Spring Boot Test。

```bash
cd backend
mvn test
```

生产配置测试使用 H2 模拟数据库，并验证 Swagger、CORS 和可选模型服务的安全默认值。

## 持续集成

GitHub Actions 会在推送到 `main` 和面向 `main` 的 Pull Request 上执行：

1. 前端生产依赖安全审计。
2. 前端测试与覆盖率门禁。
3. 前端生产构建。
4. 后端测试与打包。
5. Docker Compose 配置渲染。

任何一项失败时都不应合并变更。
