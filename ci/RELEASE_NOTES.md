# MGTeam-JE 更新说明

Minecraft 团队系统插件（Paper/Spigot 1.21.11+）：自由组队、团队资金、留言板与公告、活跃度排行榜，Java 版 GUI + 基岩版表单双端支持。

## 独立版特性

- 无硬依赖，可独立运行；启动时自动检测 GMZCSkinCache / GMZCTitles / GMZCEssentialsMenu，未安装时对应功能自动降级
- 安装 Vault 后启用团队资金功能
- 安装 Geyser + Floodgate 后启用基岩版表单
- 安装 PlaceholderAPI 后启用 %mgteam_*% 占位符

## 本版本内容

- 删除网页导出接口中的传送/锚点功能（teamWarps、addWarp、removeWarp、teleportToWarp、teleportToPlayer）：这些功能依赖在线玩家对象，仅面向游戏内在线传送，不适合网页后端调用。
- 保留团队资金、留言、成员、申请、解散等全功能服务端导出接口（WebTeamManager），供 StarCityBridge 插件统一对接。
- 新增云端构建流水线（GitHub Actions）：公开依赖 Maven 自动拉取，服务器私有 GMZC 插件用 ABI 桩对齐签名（不打包进 jar），本地与 CI 均已验证编译。

## 使用

将 MGTeam-1.0.0.jar 放入 plugins 目录，重启服务器，输入 /tm 打开团队系统。

## 运行要求

- Paper 1.21.11+，Java 25
- 运行时可选依赖：GMZCSkinCache、GMZCTitles、GMZCEssentialsMenu、Vault、Floodgate/Geyser（Bedrock 表单）、PlaceholderAPI