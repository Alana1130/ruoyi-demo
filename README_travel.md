# 大学生旅游小程序后端（xinglv 扩展）

## 1. 初始化数据库

在项目使用的 MySQL 库中执行：

```bash
mysql -u root -p your_database < sql/xinglv_travel.sql
```

或在 Navicat 等工具中打开并执行 `sql/xinglv_travel.sql`。  
脚本会创建表：`xl_destination`、`xl_attraction`、`xl_food`、`xl_accommodation`、`xl_route`、`xl_route_item`、`xl_travel_user`、`xl_user_checkin`、`xl_reward_item`、`xl_user_exchange` 等，并插入示例数据。

## 2. 接口说明

所有接口前缀：`/travel`，使用 **@Anonymous**，无需登录即可访问。  
小程序通过请求头 `X-User-Id` 或参数 `userId` 传递用户标识（不传则自动生成游客身份）。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /travel/destinations | 目的地列表 |
| GET | /travel/destinations/{id} | 目的地详情（含景点、美食、住宿） |
| GET | /travel/routes/by-destination/{id} | 某目的地的推荐线路 |
| GET | /travel/routes/{id} | 线路详情（按天节点） |
| GET | /travel/budget/plan | 预算规划（参数：maxBudget、days、destinationId 可选） |
| POST | /travel/checkin/light | 点亮目的地（body：destinationId） |
| GET | /travel/checkin/my | 我的点亮列表 |
| GET | /travel/rewards/items | 兑换商品列表（参数 type 可选） |
| POST | /travel/rewards/exchange | 兑换（body：rewardItemId） |
| GET | /travel/rewards/my-exchanges | 我的兑换记录 |

## 3. 启动与联调

启动 ruoyi-admin 后，接口基地址为：`http://localhost:8080`（以实际端口为准）。  
小程序端在 xinglvQ 的 `utils/request.js` 中配置相同 baseUrl 即可联调。
