# qihang-shops 前端

Vue 2 + Element UI + Avue，开发辅助说明（部署与整体介绍见仓库根目录 README）。

## 开发

```bash
npm install
npm run dev
```

访问 http://localhost:88 ，开发环境 `/dev-api` 已代理到 `http://localhost:8098`（需先启动后端）。

## 发布

```bash
npm run build:prod
```

产物在 `dist/`，Nginx 配置参考 `nginx.conf`（`/prod-api/` 反向代理到后端）。
