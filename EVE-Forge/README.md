# EVE-Forge

EVE-Forge 是一个为 EVE Online 游戏提供市场数据分析服务的开源项目，包括商品价格查询、历史订单分析、模糊文本匹配商品、每日随机推荐等功能。

## 功能特性

- 商品价格查询
- 历史订单分析
- 模糊文本匹配商品搜索
- 每日随机推荐
- 星域地图数据管理
- 缓存优化的高频访问数据性能

## 技术栈

- Spring Boot 4.0.1
- MyBatis 持久层框架
- Redis 缓存
- MySQL 数据库
- JFreeChart 图表生成
- Shiro QQ机器人通信框架

## 环境要求

- JDK 21
- Redis 服务
- MySQL 数据库

## 安装与运行

1. 克隆项目：
   ```bash
   git clone https://github.com/yourusername/EVE-Forge.git
   ```

2. 配置环境变量：
   复制 `.env.example` 到 `.env` 并根据需要修改配置

3. 构建项目：
   ```bash
   mvn clean install
   ```

4. 运行项目：
   ```bash
   mvn spring-boot:run
   ```

## 配置说明

项目使用 `application.yml` 进行配置，请确保在部署时修改数据库连接、Redis连接等相关配置。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。

## 许可证

本项目采用 [LICENSE] 许可证 - 查看 [LICENSE](./LICENSE) 文件了解详情。