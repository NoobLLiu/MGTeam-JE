# MGTeam —— Minecraft 团队系统

> 一款面向 Paper / Spigot 1.21 的团队（公会）系统插件：自由组队、共享传送点、公共存款、留言板与团队公告，Java 版与基岩版双端 GUI 支持。

| 项目 | 说明 |
|---|---|
| 插件版本 | 1.0.0 |
| 适用版本 | Paper / Spigot 1.21+ |
| 经济系统 | Vault（软依赖） |
| 占位符 | PlaceholderAPI（软依赖） |
| 作者 | Codex |

---

## 功能特性

### 组队系统
- **创建团队**：消耗固定费用（默认 10000，可配置），生成 4 位唯一团队 ID，团队名称 2-10 字且全局唯一
- **申请加入**：按团队 ID 搜索并提交申请，由管理员审批（支持批准 / 拒绝）
- **公开 / 私密**：公开团队可被搜索查看；私密团队仅可凭 ID 申请
- **成员管理**：晋升 / 降级管理员、移出成员（至少保留 1 名管理员，管理员不能直接退出团队）
- **团队管理**：改名、编辑公告（100 字上限）、解散（需输入团队名二次确认，防止误操作）
- **名称同步**：玩家改名后登录自动同步；`/tmsync` 可手动全量同步

### 共享传送点（/tmtp）
- 团队成员共享的传送锚点：添加、删除、一键传送
- **跨维度支持**：主世界 / 下界 / 末地均可传送
- **自定义图标**：内置多种图标可选（分页选择），Tab 补全锚点名

### 团队互传（/tmtpa）
- 一键传送到在线队友身边（目标必须在线且同团队）
- 在线队友列表 + Tab 补全

### 公共存款（积金）
- 团队公共资金（基于 Vault）：成员存入、管理员取出
- **完整资金流水日志**，分页查看
- 资金变动实时提醒在线管理员（聊天栏）

### 留言板与公告
- 团队留言板：发布（100 字上限、冷却可配置）、未读计数、打开即已读
- 新留言 / 新公告聊天提醒；Java 版登录后有弹窗提示
- 公告更新自动标记未读

### 团队活跃度
- 玩家获取经验时团队活跃度 +1，每小时按百分比衰减（可配置）
- 排行榜按活跃度排序（活跃度相同时按团队资金排序）

### 双端 GUI
- **Java 版玩家**：完整 GUI 菜单（创建 / 详情 / 管理 / 传送 / 资金 / 留言 / 排行）
- **基岩版玩家**（Geyser + floodgate）：自动切换 Bedrock Forms 表单，操作体验一致

### PlaceholderAPI 占位符

| 占位符 | 说明 |
|---|---|
| `%mgteam_org_name%` | 所在团队名称（无团队为空） |
| `%mgteam_org_id%` | 所在团队 ID |
| `%mgteam_is_owner%` | 是否为团队管理员（`true` / `false`） |
| `%mgteam_org_money%` | 团队当前资金 |
| `%mgteam_org_activity%` | 团队活跃度 |
| `%mgteam_org_member_count%` | 团队成员总数 |

### Web 管理接口
- 内置 `WebTeamManager` 导出接口：校验规则与游戏内完全一致，可供网页端复刻全部菜单操作（创建 / 申请 / 传送 / 资金 / 留言 / 管理 / 解散等）
- 并发安全：写操作按玩家加锁并在主线程串行执行
- 详见仓库内 `docs/mgteam-web-api.md`

### 开发者 API
- `MGTeamAPI` 静态接口，供其他插件调用：

| 方法 | 说明 |
|---|---|
| `getPlayerOrgName(UUID)` | 玩家所在团队名称 |
| `getPlayerOrgId(UUID)` | 玩家所在团队 ID |
| `playerIsOwner(UUID)` | 是否团队管理员 |
| `orgGetMoney(String oid)` | 团队资金 |
| `orgAddMoney(String oid, long, String)` | 向团队资金增减并记录流水 |
| `getPlayerAuxInTeam(UUID, String oid)` | 玩家在团队中的角色等级 |
| `reducePlayerMoneyDirect(UUID, double)` | 直接扣除玩家余额（Vault） |

---

## 截图

（请在发布时替换为游戏内实际截图：团队主菜单 / 传送锚点 / 积金 / 留言板 / 基岩版表单等）

---

## 安装

1. 服务端为 Paper / Spigot 1.21+，并安装所需依赖插件（见下方「依赖」）
2. 将 `MGTeam-1.0.0.jar` 放入服务端 `plugins` 目录
3. 重启服务器
4. 玩家输入 `/tm` 打开团队系统主菜单

> 修改 `plugins/MGTeam/config.yml` 后需重启服务器生效（Web 接口支持热重载配置）。

---

## 命令

| 命令 | 说明 | 权限 |
|---|---|---|
| `/tm`（别名 `/teammate`） | 团队系统主命令，打开主菜单 | `mgteam.user` |
| `/tmtp` | 打开传送锚点菜单；`/tmtp <锚点名>` 直接传送 | `mgteam.user` |
| `/tmtpa` | 打开互传菜单；`/tmtpa <玩家>` 直接传送至队友 | `mgteam.user` |
| `/tmsync` | 同步团队数据中的玩家名称 | `mgteam.admin` |
| `/mgop` | 打开管理员面板（查看全部团队、任意解散） | `mgteam.admin` |

## 权限

| 权限 | 说明 | 默认 |
|---|---|---|
| `mgteam.admin` | 管理员面板、名称同步 | OP |
| `mgteam.user` | 团队系统玩家功能 | 所有玩家 |

## 配置（config.yml）

```yaml
# MGTeam 配置文件
create-team-cost: 10000        # 创建团队所需费用
currency-name: "星光点"        # 货币显示名称
enable-playtime-check: false   # 是否启用在线时长限制
playtime-required-minutes: 600 # 要求的在线时长（分钟）
message-cooldown-seconds: 600  # 留言发布冷却（秒）
max-messages-stored: 100       # 每队最大留言存储数
fund-log-display-limit: 50     # 资金流水每页显示条数
activity-reduce-interval-minutes: 60  # 活跃度衰减间隔（分钟）
activity-decay-percent: 0.01   # 每次衰减的百分比
```

## 依赖

| 类型 | 插件 | 说明 |
|---|---|---|
| 软依赖 | Vault | 团队资金与创建费用结算；未安装时资金功能不可用，其余功能正常 |
| 软依赖 | PlaceholderAPI | `%mgteam_*%` 占位符 |
| 软依赖 | Geyser-Spigot + floodgate | 基岩版 Bedrock Forms 支持 |
| 软依赖 | GMZCSkinCache | 玩家皮肤头像显示；未安装时自动降级为普通头颅，不影响其他功能 |
| 软依赖 | GMZCTitles | 成长等级门槛；未安装时自动放行（无等级限制），安装后恢复等级校验 |

> 插件在启动时自动检测对接插件：未安装的依赖会被静默停用对应功能，其余功能不受影响，插件始终可独立运行。

## 注意事项

- 团队系统**成长等级门槛**由 GMZCTitles 提供：安装 GMZCTitles 后，需达到指定成长等级方可使用团队功能；未安装时无门槛，所有玩家可直接使用
- 基岩版表单功能需要服务端正确配置 Geyser + floodgate
- 团队资金相关操作（存入 / 取出 / 创建扣费）依赖 Vault 经济插件

---

## 更新日志

**1.0.0**（独立版，首发）
- 独立可用：移除 GMZCSkinCache 硬依赖，启动时自动检测对接插件（GMZCSkinCache / GMZCTitles），未安装则对应功能自动降级，插件可独立运行
- 完整组队系统：创建 / 申请 / 审批 / 管理 / 解散 / 退出
- 共享传送锚点与团队互传（跨维度、自定义图标）
- 团队公共存款（Vault）与资金流水日志
- 留言板与团队公告（未读状态、实时提醒）
- 团队活跃度与排行榜
- Java 版 GUI + 基岩版表单双端支持
- PlaceholderAPI 占位符与 `MGTeamAPI` 开发者接口
- `WebTeamManager` 网页导出接口（详见 `docs/mgteam-web-api.md`）

---

## 版权声明

© Codex，保留所有权利。未经授权请勿二次分发或修改后公开发布。
