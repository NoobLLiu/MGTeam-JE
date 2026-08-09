# MGTeam 团队系统网页导出接口文档（WebTeamManager）

> 版本：2026-08-10。本文档描述导出接口的完整能力、与游戏内每个菜单/按钮的对应关系、校验一致性、并发模型与调用方式，精细度对齐 StockExchange `docs/web-export-api.md`。

## 1. 目标与设计原则

网页端应能复刻游戏内团队系统的全部菜单与按钮功能，且**效果与游戏内一致**：

1. **校验一致**：成长等级、名称长度/唯一、余额、权限、冷却、目标存在性均与游戏内同一规则；
   游戏内已有的校验，导出接口同样触发并返回相同中文提示。
2. **单一数据源**：导出接口直接操作 `TeamDataManager` / `MessageDataManager` / `FundLogManager` 的同一存储，
   不另写第二套状态；存取款、留言发布等操作触发与游戏内相同的资金/留言聊天提醒。
3. **并发安全**：写操作按操作者 UUID 加可重入锁，并强制回服务器主线程串行执行（`callSyncMethod`），
   同一个人多个请求、或多个人同时操作，数据不会交错。
4. **管理员权限**：`admin` 布尔标志由调用方（网页后端/桥接层）认证后显式传入，对应游戏内 `mgteam.admin` 权限；
   导出接口本身不做网页登录。

## 2. 调用入口

### Java 调用（插件内/桥接插件）

```java
MGTeamPlugin plugin = MGTeamPlugin.getInstance(); // 或通过 Bukkit.getPluginManager().getPlugin("MGTeam") 获取
WebTeamManager web = plugin.getWebTeamManager();
Map<String, Object> result = web.teamDetail("xI1a");
// result: { "ok": true, "data": { ... } } 或 { "ok": false, "message": "..." }
```

响应统一为：

```json
{ "ok": true, "message": "", "data": { ... } }
```

失败时 `ok=false`，`message` 为与游戏内一致的中文提示。

> 说明：当前 MGTeam 尚未接入 StarCityBridge 的 WebSocket 动作转发模块；如后续接入，
> 建议动作名与下方“接口方法”同名（或按映射表第 4 节命名），响应格式保持不变。

## 3. 通用约定

- `uuid`：玩家 UUID 字符串；所有玩家级写操作会校验格式，并要求对应玩家在线（与游戏内 GUI 一致）。
- `tid`：团队 ID，大小写不敏感（内部走 `TeamDataManager.resolveId`，与游戏内搜索一致）。
- `admin`：布尔标志，仅当调用方已确认管理员身份后才传 `true`；对应游戏内 `mgteam.admin`。
- 分页从 1 开始；`page_size` 超出范围时按游戏内默认值处理（各接口默认值见映射表）。
- 金额为长整型正整数，上限与游戏内一致（`9007199254740991`）。
- 所有写操作均要求成长等级达到团队系统等级（与 `/tm` 入口一致）。

## 4. 菜单/按钮 → 接口映射表

| 游戏内菜单/功能 | 按钮/操作 | 导出方法 | 备注 |
|---|---|---|---|
| 团队系统主菜单（无团队） | 创建新团队 | `createTeam(uuid, name)` | 扣创建费用、生成 4 位 ID |
| 团队系统主菜单（无团队） | 搜索团队 | `teamSearch(query)` | ID/名称匹配 |
| 团队系统主菜单（无团队） | 排行榜 | `teamList(page, pageSize, query)` | 仅公开团队，排序与游戏内一致 |
| 申请确认页 | 确定申请 | `applyJoin(uuid, tid)` | 先清理玩家全部旧申请 |
| 团队详情 | 成员列表 | `teamDetail(tid)` / `teamMembers(tid)` | 管理员在前 |
| 传送锚点 | 点击传送 | `teleportToWarp(tid, uuid, name)` | 与 `tmtp <传送点>` 一致 |
| 传送锚点 | 添加锚点 | `addWarp(tid, uuid, name, x, y, z, dim, world, icon)` | 网页版支持指定坐标 |
| 传送锚点（删除模式） | 删除锚点 | `removeWarp(tid, uuid, name, admin)` | 创建者或管理员 |
| 互传 | 点击队友传送 | `teleportToPlayer(uuid, targetUuid)` | 与 `tmtpa <玩家>` 一致 |
| 互传 | 在线队友列表 | `onlineTeammates(uuid)` | 同 GUI 列表 |
| 积金 | 查看资金 | `teamFunds(tid)` | 余额+货币名 |
| 积金 | 存入 | `depositFunds(tid, uuid, amount)` | 扣玩家余额 |
| 积金 | 取出 | `withdrawFunds(tid, uuid, amount, admin)` | 管理员/管理标志 |
| 流水 | 查看流水 | `fundLogs(tid, page, pageSize)` | 默认 50 条（配置可调） |
| 留言板 | 查看留言 | `teamMessages(tid, page, pageSize)` | 最新在前，默认 10 条 |
| 留言板 | 未读状态 | `messageState(tid, uuid)` | 只读，不改变已读 |
| 留言板 | 发布留言 | `postMessage(tid, uuid, content)` | 冷却+100 字限制，不写入他人已读 |
| 留言板 | 打开即已读 | `markMessagesRead(tid, uuid)` | 与打开留言板等效 |
| 团队公告 | 查看公告 | `teamDetail(tid)`（notice 字段） | 与打开公告等效 |
| 团队公告 | 标记公告已读 | `markNoticeRead(tid, uuid)` | 与游戏内查看公告等效 |
| 管理团队 | 管理成员 | `teamMembers(tid)` + `promoteMember/demoteOperator/removeMember` | 管理员/管理标志 |
| 管理团队 | 申请处理 | `teamApplications(tid, actorUuid, admin)` + `acceptApplication/rejectApplication` | 管理员/管理标志 |
| 管理团队 | 编辑公告 | `setNotice(tid, actorUuid, notice, admin)` | 最多 100 字 |
| 管理团队 | 修改名称 | `renameTeam(tid, actorUuid, newName, admin)` | 2-10 字且唯一 |
| 管理团队 | 状态切换 | `setPublic(tid, actorUuid, isPublic, admin)` | 公开/私密 |
| 管理团队 | 友伤切换 | `setFriendlyFire(tid, actorUuid, allow, admin)` | |
| 管理团队 | 解散 | `disbandTeam(tid, actorUuid, confirmName, admin)` | 需输入团队名确认 |
| 退出团队 | 输入 yes 确认 | `quitTeam(uuid)` | 管理员需先降级 |
| 我的团队 | 查询当前团队 | `myTeam(uuid)` | 含角色 |
| OP 管理 | 全部团队 | `allTeams(admin, page, pageSize)` | 含私密团队 |
| OP 管理 | 任意解散 | `adminDisband(admin, tid, confirmName)` | mgteam.admin |
| /tmsync | 同步玩家名 | `adminSyncNames(admin)` | 返回 fixed/failed/skipped |
| 配置热重载 | - | `adminReloadConfig(admin)` | 仅重载配置，不重载数据 |

## 5. 写操作校验清单（与游戏内一致）

| 校验 | 覆盖接口 |
|---|---|
| 成长等级 ≥ 团队系统等级 | 全部玩家写操作 |
| 玩家在线 | 全部玩家写操作（标记已读除外） |
| 玩家不在任何团队 | createTeam / applyJoin |
| 团队名称 2-10 字且唯一 | createTeam / renameTeam |
| 团队 ID 存在（大小写不敏感） | 全部 tid 参数接口 |
| 操作者在该团队 | addWarp/removeWarp/teleportToWarp/depositFunds/withdrawFunds/postMessage/quitTeam |
| 操作者需为管理员（或 admin 标志） | 管理菜单全部操作、withdrawFunds |
| 管理员至少保留一个 | demoteOperator |
| 管理员不能直接移出/退出 | removeMember/quitTeam |
| 传送点名 1-10 字且唯一 | addWarp |
| 删除权限（创建者或管理员） | removeWarp |
| 余额/团队资金充足 | createTeam/depositFunds/withdrawFunds |
| 金额为正整数且不超上限 | depositFunds/withdrawFunds |
| 留言非空且 ≤100 字、冷却中 | postMessage |
| 解散需输入与团队名一致的确认名 | disbandTeam/adminDisband |
| 申请已存在、申请人未在其他团队 | acceptApplication/rejectApplication |

## 6. 并发模型

- **写操作**：`withPlayerLock(uuid, task)` 先获取该玩家 15 秒内可用的可重入锁；
  然后 `callSyncMethod` 回主线程执行（30 秒超时）。锁保证同一玩家的多个请求串行；
  主线程串行保证不同玩家的写操作也不会交错。
- **读操作**：无锁，直接在主线程读取（`onMain`），存储层本身同步。
- **同玩家双开**：同一玩家同时提交两笔写操作时，第二笔等待第一笔完成，超时返回
  “你有一笔操作正在进行，请稍后再试”。
- **幂等性**：接口不提供幂等键；重复提交会重复执行（与游戏内重复点击一致），网页端应自行防抖。

## 7. 关键差异说明（网页 vs 游戏）

- `withdrawFunds` 强制校验管理员或 `admin` 标志，与积金菜单按钮说明“取出需要管理员权限”一致。
  （注意：当前游戏内 GUI 取出按钮未做该权限校验，属于既有缺口，导出接口按菜单声明实现；修复 GUI 时保持一致。）
- `addWarp` 允许网页端直接传坐标/维度/世界名，游戏内使用玩家当前站立位置。
- `markMessagesRead` / `markNoticeRead` 允许对离线玩家调用（网页端“标记已读”便利），游戏内仅在线玩家打开菜单触发。
- `teamList` / `allTeams` / `teamMessages` / `fundLogs` 支持分页；游戏内排行/留言/流水页有 45/10/50 条硬上限，网页端分页只是展示拆分，顺序与可见范围一致。
- `teamSearch` 支持按名称包含与 ID 包含搜索；游戏内仅支持精确 4 位 ID。
- 申请通过时若申请人已加入其他团队，接口会移除该申请并返回失败（与游戏内“已在其他团队”行为一致）。

## 8. 安全说明

- `admin` 标志由调用方认证后传入；导出接口本身不做网页登录。
- 任何写操作都不会凭空产生资产：创建团队扣余额、存款扣玩家余额、取款经经济插件发放并回滚失败。
- 主线程串行 + 权限校验防止并发下重复扣款/越权操作。
- 读取接口不返回服务器敏感信息（不含配置路径、权限组、玩家背包数据）。

## 9. 构建与部署

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File D:\java-server\dev\local-plugins\mgteam\build.ps1
```

产物：`D:\java-server\dev\local-plugins\mgteam\build\MGTeam-1.0.0.jar`。
导出接口类位于 `src\cn\gmzc\mgteam\web\WebTeamManager.java`（统一 web 文件夹），
插件入口 `MGTeamPlugin#getWebTeamManager()`。部署与游戏内插件一致：替换
`D:\java-server\StarCIty\plugins\[原创-团队系统]MGTeam-1.0.0.jar` 后下次 `start.bat` 重启生效。

## 10. 验证清单（重启后）

- [ ] 插件启用日志无 `WebTeam` 异常；`getWebTeamManager()` 返回非空。
- [ ] `teamList` / `teamDetail` / `teamMembers` / `teamWarps` / `teamFunds` / `fundLogs` / `teamMessages`
      返回数据与游戏内 GUI 一致。
- [ ] `createTeam` 扣款、生成 ID 后游戏内主菜单可见；重复名称/余额不足返回游戏内相同提示。
- [ ] `applyJoin` → 游戏内管理员的申请列表可见；`acceptApplication` 后成员出现在详情中。
- [ ] `depositFunds` / `withdrawFunds` 后游戏内积金与流水一致，且在线管理员收到资金提醒。
- [ ] `postMessage` 后游戏内留言板可见、其他在线成员收到聊天提醒，且未读状态不被清除。
- [ ] `addWarp` / `removeWarp` / `teleportToWarp` 与游戏内传送点一致。
- [ ] 管理操作（改名/公告/公开/友伤/解散）与游戏内结果一致；非管理员且无 admin 标志时被拒绝。
- [ ] 同一玩家并发提交两笔写操作，第二笔等待或返回繁忙提示，数据不重复扣减。
