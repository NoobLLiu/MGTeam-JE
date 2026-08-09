package cn.gmzc.mgteam.web;

import cn.gmzc.mgteam.GrowthLevelAccess;
import cn.gmzc.mgteam.MGTeamPlugin;
import cn.gmzc.mgteam.model.FundLogEntry;
import cn.gmzc.mgteam.model.MessageEntry;
import cn.gmzc.mgteam.model.Team;
import cn.gmzc.mgteam.util.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * 团队系统全功能导出接口（WebTeamManager）。
 *
 * <p>目标：把游戏内团队菜单/命令的每个按钮功能导出给网页端，行为与游戏内一致：
 * <ul>
 *   <li>校验一致：成长等级、名称长度/唯一、余额、权限、冷却、目标存在性均复用游戏内同一规则。</li>
 *   <li>单一数据源：所有写操作直接修改 TeamDataManager/MessageDataManager/FundLogManager 的同一存储，
 *       并触发与游戏内相同的资金/留言提醒。</li>
 *   <li>并发安全：写操作按操作者 UUID 加可重入锁，并强制回服务器主线程串行执行（callSyncMethod）。</li>
 *   <li>管理员权限由调用方认证后以 admin 标志显式传入，对应游戏内 mgteam.admin 权限。</li>
 * </ul>
 */
public class WebTeamManager {

    private final MGTeamPlugin plugin;
    private final Map<String, ReentrantLock> playerLocks = new ConcurrentHashMap<>();

    public WebTeamManager(MGTeamPlugin plugin) {
        this.plugin = plugin;
    }

    // ===================== 只读：团队列表/详情/成员/申请/传送点/资金/留言/排行 =====================

    /** 公开团队排行列表（与游戏内排行菜单一致：仅公开团队，成长值>0 优先，成长值/资金降序），支持分页与搜索。 */
    public Map<String, Object> teamList(int page, int pageSize, String query) {
        return onMain(() -> {
            List<Map.Entry<String, Team>> ranked = new ArrayList<>();
            for (Map.Entry<String, Team> e : plugin.getTeamData().getAll().entrySet()) {
                if (e.getValue().isPublic()) ranked.add(e);
            }
            ranked.sort(Comparator.comparing(Map.Entry<String, Team>::getValue, WebTeamManager::compareTeams));
            String q = query == null ? "" : query.trim();
            if (!q.isEmpty()) {
                ranked.removeIf(e -> !matchesQuery(e.getKey(), e.getValue(), q));
            }
            Map<String, Object> data = pageView(ranked, page, pageSize, 35, 100, e -> teamView(e.getKey(), e.getValue()));
            return ok(data);
        });
    }

    /** 管理员：全部团队列表（含私密团队），排序与公开排行一致。 */
    public Map<String, Object> allTeams(boolean admin, int page, int pageSize) {
        if (!admin) return fail("需要管理员权限");
        return onMain(() -> {
            List<Map.Entry<String, Team>> all = new ArrayList<>(plugin.getTeamData().getAll().entrySet());
            all.sort(Comparator.comparing(Map.Entry<String, Team>::getValue, WebTeamManager::compareTeams));
            return ok(pageView(all, page, pageSize, 45, 100, e -> teamView(e.getKey(), e.getValue())));
        });
    }

    /** 团队详情：基本信息 + 成员列表 + 统计。 */
    public Map<String, Object> teamDetail(String tid) {
        return onMain(() -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            return ok(teamDetailView(id, t));
        });
    }

    /** 团队成员列表（管理员在前，与游戏内详情页一致）。 */
    public Map<String, Object> teamMembers(String tid) {
        return onMain(() -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            List<Map<String, Object>> members = new ArrayList<>();
            for (Team.MemberEntry m : operatorsOf(t)) members.add(memberView(m, true));
            for (Team.MemberEntry m : membersOf(t)) members.add(memberView(m, false));
            return ok(map("team_id", id, "members", members, "total", members.size()));
        });
    }

    /** 待处理申请（管理员/操作者可见）。 */
    public Map<String, Object> teamApplications(String tid, String actorUuid, boolean admin) {
        return onMain(() -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!admin) {
                if (!validUuid(actorUuid)) return fail("无效的操作者UUID");
                if (!plugin.getTeamData().isTeamOperator(parseUuid(actorUuid), id)) return fail("需要管理员权限");
            }
            Team t = plugin.getTeamData().get(id);
            List<Map<String, Object>> apps = new ArrayList<>();
            for (Team.MemberApplication a : applicationsOf(t)) apps.add(applicationView(a));
            return ok(map("team_id", id, "applications", apps, "total", apps.size()));
        });
    }

    /** 团队传送点列表。 */
    public Map<String, Object> teamWarps(String tid) {
        return onMain(() -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            List<Map<String, Object>> warps = new ArrayList<>();
            for (Map.Entry<String, Team.WarpPoint> e : t.getWarpPoints().entrySet()) {
                warps.add(warpView(e.getKey(), e.getValue()));
            }
            return ok(map("team_id", id, "warps", warps, "total", warps.size()));
        });
    }

    /** 团队资金与货币名。 */
    public Map<String, Object> teamFunds(String tid) {
        return onMain(() -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            return ok(map("team_id", id, "funds", t.getFunds(), "currency_name", plugin.getConfig2().getCurrencyName()));
        });
    }

    /** 资金流水（与游戏内流水页一致，默认 50 条上限，支持分页）。 */
    public Map<String, Object> fundLogs(String tid, int page, int pageSize) {
        return onMain(() -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            List<FundLogEntry> logs = plugin.getFundLog().getLogs(id);
            return ok(pageView(logs, page, pageSize, plugin.getConfig2().getFundLogDisplayLimit(), 100, this::fundLogView));
        });
    }

    /** 团队留言（最新在前，与游戏内留言板一致）。 */
    public Map<String, Object> teamMessages(String tid, int page, int pageSize) {
        return onMain(() -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            List<MessageEntry> msgs = plugin.getMessageData().getMessages(id);
            return ok(pageView(msgs, page, pageSize, 10, 100, this::messageView));
        });
    }

    /** 玩家在指定团队的未读状态（留言/公告），不改变任何已读标记。 */
    public Map<String, Object> messageState(String tid, String uuid) {
        return onMain(() -> {
            if (!validUuid(uuid)) return fail("无效的玩家UUID");
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            UUID u = parseUuid(uuid);
            long noticeUpdatedAt = t.getNoticeUpdatedAt();
            return ok(map(
                "team_id", id,
                "has_new_messages", plugin.getMessageData().hasNewMessages(u, id),
                "has_new_notice", plugin.getMessageData().hasNewNotice(u, id, noticeUpdatedAt),
                "notice_updated_at", noticeUpdatedAt,
                "last_view_time", plugin.getMessageData().getLastViewTime(u, id),
                "message_count", plugin.getMessageData().getMessages(id).size()
            ));
        });
    }

    /** 玩家当前所在团队。 */
    public Map<String, Object> myTeam(String uuid) {
        return onMain(() -> {
            if (!validUuid(uuid)) return fail("无效的玩家UUID");
            String id = plugin.getTeamData().getPlayerTeamId(parseUuid(uuid));
            if (id == null) return ok(map("in_team", false));
            Team t = plugin.getTeamData().get(id);
            boolean op = plugin.getTeamData().isTeamOperator(parseUuid(uuid), id);
            return ok(map(
                "in_team", true,
                "team_id", id,
                "team_name", t.getName(),
                "role", op ? "OPERATOR" : "MEMBER",
                "team", teamView(id, t)
            ));
        });
    }

    /** 在线队友列表（与互传 GUI 一致：同团队在线成员，排除自己，含距离）。 */
    public Map<String, Object> onlineTeammates(String uuid) {
        return onMain(() -> {
            if (!validUuid(uuid)) return fail("无效的玩家UUID");
            UUID u = parseUuid(uuid);
            String tid = plugin.getTeamData().getPlayerTeamId(u);
            if (tid == null) return ok(map("in_team", false, "teammates", new ArrayList<>()));
            Player self = Bukkit.getPlayer(u);
            List<Map<String, Object>> mates = new ArrayList<>();
            for (Player op : Bukkit.getOnlinePlayers()) {
                if (op.getUniqueId().equals(u)) continue;
                if (!tid.equals(plugin.getTeamData().getPlayerTeamId(op.getUniqueId()))) continue;
                Map<String, Object> view = map(
                    "uuid", op.getUniqueId().toString(),
                    "name", op.getName(),
                    "world", op.getWorld().getName()
                );
                if (self != null && op.getWorld().equals(self.getWorld())) {
                    view.put("distance", Math.round(self.getLocation().distance(op.getLocation()) * 10.0) / 10.0);
                }
                mates.add(view);
            }
            return ok(map("in_team", true, "team_id", tid, "teammates", mates, "total", mates.size()));
        });
    }

    /** 按团队ID/名称搜索（ID 大小写不敏感，名称包含匹配），最多 20 条。 */
    public Map<String, Object> teamSearch(String query) {
        return onMain(() -> {
            String q = query == null ? "" : query.trim();
            if (q.isEmpty()) return ok(map("teams", new ArrayList<>(), "total", 0));
            List<Map<String, Object>> found = new ArrayList<>();
            for (Map.Entry<String, Team> e : plugin.getTeamData().getAll().entrySet()) {
                if (matchesQuery(e.getKey(), e.getValue(), q)) {
                    found.add(teamView(e.getKey(), e.getValue()));
                    if (found.size() >= 20) break;
                }
            }
            return ok(map("teams", found, "total", found.size()));
        });
    }

    // ===================== 写操作：团队生命周期 =====================

    /** 创建团队（与游戏内一致：成长等级、名称2-10字且唯一、扣除创建费用）。 */
    public Map<String, Object> createTeam(String uuid, String name) {
        return withPlayerLock(uuid, () -> {
            Player pl = onlinePlayer(uuid);
            if (pl == null) return fail("该操作需要玩家在线");
            if (GrowthLevelAccess.restricted(pl)) return fail("该功能需要成长等级达到 " + GrowthLevelAccess.REQUIRED_LEVEL + " 级");
            if (plugin.getTeamData().getPlayerTeamId(pl.getUniqueId()) != null) return fail("您已在团队中");
            String n = name == null ? "" : name.trim();
            if (n.length() < 2 || n.length() > 10) return fail("必须2-10字");
            if (plugin.getTeamData().nameExists(n)) return fail("名称已存在");
            long cost = plugin.getConfig2().getCreateTeamCost();
            if (plugin.getEconomy().getBalance(pl.getUniqueId()) < cost) return fail("余额不足");
            if (!plugin.getEconomy().withdraw(pl.getUniqueId(), cost)) return fail("扣款失败");
            String id = Util.generateTeamId(plugin.getTeamData().getAll().keySet());
            if (id == null) return fail("生成团队ID失败");
            Team t = new Team(n, pl.getUniqueId(), pl.getName());
            plugin.getTeamData().put(id, t);
            plugin.getTeamData().save();
            return ok(map(
                "team_id", id,
                "team_name", n,
                "cost", cost,
                "currency_name", plugin.getConfig2().getCurrencyName()
            ));
        });
    }

    /** 申请加入团队（与游戏内一致：先移除玩家在所有团队的历史申请，再添加）。 */
    public Map<String, Object> applyJoin(String uuid, String tid) {
        return withPlayerLock(uuid, () -> {
            Player pl = onlinePlayer(uuid);
            if (pl == null) return fail("该操作需要玩家在线");
            if (GrowthLevelAccess.restricted(pl)) return fail("该功能需要成长等级达到 " + GrowthLevelAccess.REQUIRED_LEVEL + " 级");
            if (plugin.getTeamData().getPlayerTeamId(pl.getUniqueId()) != null) return fail("您已在团队中");
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("未找到团队");
            Team t = plugin.getTeamData().get(id);
            String u = pl.getUniqueId().toString();
            for (Team other : plugin.getTeamData().getAll().values()) {
                applicationsOf(other).removeIf(a -> u.equals(a.getUuid()));
            }
            applicationsOf(t).add(new Team.MemberApplication(u, pl.getName()));
            plugin.getTeamData().save();
            return ok(map("team_id", id, "message", "已发送申请"));
        });
    }

    /** 通过申请（管理员/管理标志）。 */
    public Map<String, Object> acceptApplication(String tid, String actorUuid, String applicantUuid, boolean admin) {
        return withManageLock(tid, actorUuid, admin, () -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!validUuid(applicantUuid)) return fail("无效的申请人UUID");
            Team t = plugin.getTeamData().get(id);
            Team.MemberApplication app = findApplication(t, applicantUuid);
            if (app == null) return fail("申请不存在");
            UUID au = parseUuid(applicantUuid);
            if (plugin.getTeamData().isPlayerInTeam(au)) {
                applicationsOf(t).removeIf(a -> au.toString().equals(a.getUuid()));
                plugin.getTeamData().save();
                return fail("已在其他团队");
            }
            membersOf(t).add(new Team.MemberEntry(app.getUuid(), app.getName()));
            applicationsOf(t).removeIf(a -> au.toString().equals(a.getUuid()));
            plugin.getTeamData().save();
            Player tp = Bukkit.getPlayer(au);
            if (tp != null) tp.sendMessage("\u00a7a欢迎加入 " + t.getName());
            return ok(map("team_id", id, "message", "已通过"));
        });
    }

    /** 忽略申请（管理员/管理标志）。 */
    public Map<String, Object> rejectApplication(String tid, String actorUuid, String applicantUuid, boolean admin) {
        return withManageLock(tid, actorUuid, admin, () -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!validUuid(applicantUuid)) return fail("无效的申请人UUID");
            Team t = plugin.getTeamData().get(id);
            Team.MemberApplication app = findApplication(t, applicantUuid);
            if (app == null) return fail("申请不存在");
            applicationsOf(t).removeIf(a -> applicantUuid.equalsIgnoreCase(a.getUuid()));
            plugin.getTeamData().save();
            return ok(map("team_id", id, "message", "已忽略"));
        });
    }

    /** 设为管理员（管理员/管理标志）。 */
    public Map<String, Object> promoteMember(String tid, String actorUuid, String targetUuid, boolean admin) {
        return withManageLock(tid, actorUuid, admin, () -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!validUuid(targetUuid)) return fail("无效的成员UUID");
            Team t = plugin.getTeamData().get(id);
            Team.MemberEntry member = findMember(t, targetUuid);
            if (member == null) return fail("成员不存在");
            if (isOperator(t, targetUuid)) return fail("已是管理员");
            membersOf(t).removeIf(m -> targetUuid.equalsIgnoreCase(m.getUuid()));
            operatorsOf(t).add(new Team.MemberEntry(member.getUuid(), member.getName()));
            plugin.getTeamData().save();
            return ok(map("team_id", id, "message", "已设为管理员"));
        });
    }

    /** 降级为成员（管理员/管理标志；至少保留一个管理员，允许本人降级后退出）。 */
    public Map<String, Object> demoteOperator(String tid, String actorUuid, String targetUuid, boolean admin) {
        return withManageLock(tid, actorUuid, admin, () -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!validUuid(targetUuid)) return fail("无效的管理员UUID");
            Team t = plugin.getTeamData().get(id);
            Team.MemberEntry member = findMember(t, targetUuid);
            if (member == null) return fail("成员不存在");
            if (!isOperator(t, targetUuid)) return fail("已是成员");
            if (operatorsOf(t).size() <= 1) return fail("至少保留一个管理员");
            operatorsOf(t).removeIf(m -> targetUuid.equalsIgnoreCase(m.getUuid()));
            membersOf(t).add(new Team.MemberEntry(member.getUuid(), member.getName()));
            plugin.getTeamData().save();
            return ok(map("team_id", id, "message", "已设为成员"));
        });
    }

    /** 移出成员（管理员/管理标志；管理员需先降级，不能直接移出）。 */
    public Map<String, Object> removeMember(String tid, String actorUuid, String targetUuid, boolean admin) {
        return withManageLock(tid, actorUuid, admin, () -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!validUuid(targetUuid)) return fail("无效的成员UUID");
            Team t = plugin.getTeamData().get(id);
            Team.MemberEntry member = findMember(t, targetUuid);
            if (member == null) return fail("成员不存在");
            if (isOperator(t, targetUuid)) return fail("管理员不能直接移出团队，请先降级为普通成员");
            if (targetUuid.equalsIgnoreCase(actorUuid)) return fail("请先将自己降级为普通成员，再退出团队");
            membersOf(t).removeIf(m -> targetUuid.equalsIgnoreCase(m.getUuid()));
            plugin.getTeamData().save();
            Player tp = Bukkit.getPlayer(parseUuid(targetUuid));
            if (tp != null) tp.sendMessage("\u00a7c你已被移出 " + t.getName());
            return ok(map("team_id", id, "message", "已移出"));
        });
    }

    /** 退出团队（普通成员；管理员需先降级）。 */
    public Map<String, Object> quitTeam(String uuid) {
        return withPlayerLock(uuid, () -> {
            Player pl = onlinePlayer(uuid);
            if (pl == null) return fail("该操作需要玩家在线");
            if (GrowthLevelAccess.restricted(pl)) return fail("该功能需要成长等级达到 " + GrowthLevelAccess.REQUIRED_LEVEL + " 级");
            String id = plugin.getTeamData().getPlayerTeamId(pl.getUniqueId());
            if (id == null) return fail("您不在任何团队中");
            Team t = plugin.getTeamData().get(id);
            if (plugin.getTeamData().isTeamOperator(pl.getUniqueId(), id)) {
                return fail("管理员不能直接退出团队，请先降级为普通成员，然后再执行退出");
            }
            membersOf(t).removeIf(m -> pl.getUniqueId().toString().equals(m.getUuid()));
            plugin.getTeamData().save();
            return ok(map("team_id", id, "message", "已退出"));
        });
    }

    /** 修改团队名称（管理员/管理标志）。 */
    public Map<String, Object> renameTeam(String tid, String actorUuid, String newName, boolean admin) {
        return withManageLock(tid, actorUuid, admin, () -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            String n = newName == null ? "" : newName.trim();
            if (n.length() < 2 || n.length() > 10) return fail("必须2-10字");
            if (plugin.getTeamData().nameExists(n)) return fail("已存在");
            String old = t.getName();
            t.setName(n);
            plugin.getTeamData().save();
            return ok(map("team_id", id, "old_name", old, "new_name", n));
        });
    }

    /** 编辑公告（管理员/管理标志；最多100字，空串清除）。 */
    public Map<String, Object> setNotice(String tid, String actorUuid, String notice, boolean admin) {
        return withManageLock(tid, actorUuid, admin, () -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            String n = notice == null ? "" : notice;
            if (n.length() > 100) return fail("过长");
            t.setNotice(n);
            plugin.getTeamData().save();
            return ok(map("team_id", id, "notice", t.getNotice()));
        });
    }

    /** 切换公开/私密（管理员/管理标志）。 */
    public Map<String, Object> setPublic(String tid, String actorUuid, boolean isPublic, boolean admin) {
        return withManageLock(tid, actorUuid, admin, () -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            t.setPublic(isPublic);
            plugin.getTeamData().save();
            return ok(map("team_id", id, "public", isPublic));
        });
    }

    /** 切换允许友伤（管理员/管理标志）。 */
    public Map<String, Object> setFriendlyFire(String tid, String actorUuid, boolean allow, boolean admin) {
        return withManageLock(tid, actorUuid, admin, () -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            t.setAllowFriendlyFire(allow);
            plugin.getTeamData().save();
            return ok(map("team_id", id, "allow_friendly_fire", allow));
        });
    }

    /** 解散团队（管理员/管理标志；需传入与团队名一致的确认名，与游戏内一致）。 */
    public Map<String, Object> disbandTeam(String tid, String actorUuid, String confirmName, boolean admin) {
        return withManageLock(tid, actorUuid, admin, () -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            if (confirmName == null || !confirmName.trim().equals(t.getName())) return fail("名称错误");
            String tn = t.getName();
            plugin.getTeamData().remove(id);
            plugin.getMessageData().deleteTeamMessages(id);
            plugin.getFundLog().deleteTeamLogs(id);
            plugin.getTeamData().save();
            plugin.getMessageData().save();
            plugin.getFundLog().save();
            return ok(map("team_id", id, "team_name", tn, "message", "团队 " + tn + " 已解散"));
        });
    }

    // ===================== 写操作：传送点 / 互传 / 资金 / 留言 =====================

    /** 新增传送点（成员；名称1-10字且唯一；坐标为调用方指定，与世界/维度一致）。 */
    public Map<String, Object> addWarp(String tid, String uuid, String name, int x, int y, int z, int dim, String world, String icon) {
        return withPlayerLock(uuid, () -> {
            Player pl = onlinePlayer(uuid);
            if (pl == null) return fail("该操作需要玩家在线");
            if (GrowthLevelAccess.restricted(pl)) return fail("该功能需要成长等级达到 " + GrowthLevelAccess.REQUIRED_LEVEL + " 级");
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!id.equals(plugin.getTeamData().getPlayerTeamId(pl.getUniqueId()))) return fail("您不在此团队中");
            Team t = plugin.getTeamData().get(id);
            String n = name == null ? "" : name.trim();
            if (n.length() < 1 || n.length() > 10) return fail("名称必须1-10字");
            if (t.getWarpPoints().containsKey(n)) return fail("名称已存在");
            String ic = icon;
            if (ic == null || Material.matchMaterial(ic) == null) ic = "COMPASS";
            Team.WarpPoint wp = new Team.WarpPoint(x, y, z, dim, pl.getUniqueId().toString(), pl.getName(), ic, world == null ? "" : world);
            t.getWarpPoints().put(n, wp);
            plugin.getTeamData().save();
            return ok(map("team_id", id, "name", n, "message", "传送点创建成功"));
        });
    }

    /** 删除传送点（创建者本人或管理员/管理标志）。 */
    public Map<String, Object> removeWarp(String tid, String uuid, String name, boolean admin) {
        return withPlayerLock(uuid, () -> {
            Player pl = onlinePlayer(uuid);
            if (pl == null) return fail("该操作需要玩家在线");
            if (GrowthLevelAccess.restricted(pl)) return fail("该功能需要成长等级达到 " + GrowthLevelAccess.REQUIRED_LEVEL + " 级");
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!id.equals(plugin.getTeamData().getPlayerTeamId(pl.getUniqueId()))) return fail("您不在此团队中");
            Team t = plugin.getTeamData().get(id);
            String wn = resolveWarpName(t, name);
            if (wn == null) return fail("传送点不存在");
            Team.WarpPoint wp = t.getWarpPoints().get(wn);
            boolean can = admin || plugin.getTeamData().isTeamOperator(pl.getUniqueId(), id)
                || pl.getUniqueId().toString().equals(wp.getCreatorUuid());
            if (!can) return fail("只能删除自己创建的锚点");
            t.getWarpPoints().remove(wn);
            plugin.getTeamData().save();
            return ok(map("team_id", id, "name", wn, "message", "传送点已删除"));
        });
    }

    /** 传送到团队传送点（与 tmtp 指令一致）。 */
    public Map<String, Object> teleportToWarp(String tid, String uuid, String name) {
        return withPlayerLock(uuid, () -> {
            Player pl = onlinePlayer(uuid);
            if (pl == null) return fail("该操作需要玩家在线");
            if (GrowthLevelAccess.restricted(pl)) return fail("该功能需要成长等级达到 " + GrowthLevelAccess.REQUIRED_LEVEL + " 级");
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!id.equals(plugin.getTeamData().getPlayerTeamId(pl.getUniqueId()))) return fail("您不在此团队中");
            Team t = plugin.getTeamData().get(id);
            String wn = resolveWarpName(t, name);
            if (wn == null) return fail("未找到传送点 " + name);
            Team.WarpPoint wp = t.getWarpPoints().get(wn);
            World w = resolveWorld(wp);
            if (w == null) return fail("维度异常，无法传送");
            pl.teleport(new Location(w, wp.getX() + 0.5, wp.getY(), wp.getZ() + 0.5));
            return ok(map("team_id", id, "name", wn, "message", "已传送至 " + wn));
        });
    }

    /** 传送到队友（与 tmtpa/互传 GUI 一致）。 */
    public Map<String, Object> teleportToPlayer(String uuid, String targetUuid) {
        return withPlayerLock(uuid, () -> {
            Player pl = onlinePlayer(uuid);
            if (pl == null) return fail("该操作需要玩家在线");
            if (GrowthLevelAccess.restricted(pl)) return fail("该功能需要成长等级达到 " + GrowthLevelAccess.REQUIRED_LEVEL + " 级");
            if (!validUuid(targetUuid)) return fail("无效的目标玩家UUID");
            Player target = Bukkit.getPlayer(parseUuid(targetUuid));
            if (target == null) return fail("目标玩家不在线");
            if (target.getUniqueId().equals(pl.getUniqueId())) return fail("不能传送到自己");
            String tid = plugin.getTeamData().getPlayerTeamId(pl.getUniqueId());
            if (tid == null || !tid.equals(plugin.getTeamData().getPlayerTeamId(target.getUniqueId()))) {
                return fail("该玩家不在你的团队");
            }
            pl.teleport(target.getLocation());
            pl.sendMessage("\u00a7a已传送至 " + target.getName());
            target.sendMessage("\u00a7e" + pl.getName() + "传送到了你身边");
            return ok(map("team_id", tid, "target_name", target.getName(), "message", "已传送至 " + target.getName()));
        });
    }

    /** 存入团队资金（成员；与游戏内一致：扣余额、写流水、触发管理员提醒）。 */
    public Map<String, Object> depositFunds(String tid, String uuid, long amount) {
        return withPlayerLock(uuid, () -> {
            Player pl = onlinePlayer(uuid);
            if (pl == null) return fail("该操作需要玩家在线");
            if (GrowthLevelAccess.restricted(pl)) return fail("该功能需要成长等级达到 " + GrowthLevelAccess.REQUIRED_LEVEL + " 级");
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!id.equals(plugin.getTeamData().getPlayerTeamId(pl.getUniqueId()))) return fail("您不在此团队中");
            String err = amountError(amount);
            if (err != null) return fail(err);
            if (!plugin.getEconomy().withdraw(pl.getUniqueId(), amount)) return fail("余额不足");
            Team t = plugin.getTeamData().get(id);
            long bb = t.getFunds();
            t.setFunds(bb + amount);
            plugin.getTeamData().save();
            plugin.getFundLog().addLog(id, amount, "存入 " + pl.getName(), bb, t.getFunds());
            plugin.getFundLog().save();
            plugin.notifyFundChange(id, bb, amount, t.getFunds(), "存入 " + pl.getName(), pl.getName());
            return ok(map("team_id", id, "change", amount, "balance_before", bb, "balance_after", t.getFunds(), "message", "成功存入 " + amount));
        });
    }

    /** 取出团队资金（管理员/管理标志；与菜单说明一致，写流水并触发管理员提醒）。 */
    public Map<String, Object> withdrawFunds(String tid, String uuid, long amount, boolean admin) {
        return withPlayerLock(uuid, () -> {
            Player pl = onlinePlayer(uuid);
            if (pl == null) return fail("该操作需要玩家在线");
            if (GrowthLevelAccess.restricted(pl)) return fail("该功能需要成长等级达到 " + GrowthLevelAccess.REQUIRED_LEVEL + " 级");
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!id.equals(plugin.getTeamData().getPlayerTeamId(pl.getUniqueId()))) return fail("您不在此团队中");
            if (!admin && !plugin.getTeamData().isTeamOperator(pl.getUniqueId(), id)) return fail("需要管理员权限");
            String err = amountError(amount);
            if (err != null) return fail(err);
            Team t = plugin.getTeamData().get(id);
            if (t.getFunds() < amount) return fail("团队资金不足");
            long bb = t.getFunds();
            t.setFunds(bb - amount);
            if (!plugin.getEconomy().deposit(pl.getUniqueId(), amount)) {
                t.setFunds(bb);
                return fail("操作失败");
            }
            plugin.getTeamData().save();
            plugin.getFundLog().addLog(id, -amount, "取出 " + pl.getName(), bb, t.getFunds());
            plugin.getFundLog().save();
            plugin.notifyFundChange(id, bb, -amount, t.getFunds(), "取出 " + pl.getName(), pl.getName());
            return ok(map("team_id", id, "change", -amount, "balance_before", bb, "balance_after", t.getFunds(), "message", "成功取出 " + amount));
        });
    }

    /** 发布留言（成员；冷却与100字限制与游戏内一致，不改变他人已读状态）。 */
    public Map<String, Object> postMessage(String tid, String uuid, String content) {
        return withPlayerLock(uuid, () -> {
            Player pl = onlinePlayer(uuid);
            if (pl == null) return fail("该操作需要玩家在线");
            if (GrowthLevelAccess.restricted(pl)) return fail("该功能需要成长等级达到 " + GrowthLevelAccess.REQUIRED_LEVEL + " 级");
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!id.equals(plugin.getTeamData().getPlayerTeamId(pl.getUniqueId()))) return fail("您不在此团队中");
            String c = content == null ? "" : content.trim();
            if (c.isEmpty()) return fail("不能为空");
            if (c.length() > 100) return fail("过长");
            int cdSec = plugin.getConfig2().getMessageCooldownSeconds();
            long cd = plugin.getMessageCooldowns().getOrDefault(id + "_" + pl.getUniqueId(), 0L);
            long remain = cdSec * 1000L - (System.currentTimeMillis() - cd);
            if (remain > 0) {
                long r = remain / 1000;
                return fail("冷却中 " + r / 60 + "分" + r % 60 + "秒");
            }
            MessageEntry me = new MessageEntry(pl.getUniqueId().toString(), pl.getName(), c);
            plugin.getMessageData().addMessage(id, me);
            plugin.getMessageData().save();
            plugin.getMessageCooldowns().put(id + "_" + pl.getUniqueId(), System.currentTimeMillis());
            plugin.notifyTeamMessage(id, me);
            return ok(map("team_id", id, "message", "发布成功"));
        });
    }

    /** 标记留言已读（网页端便利：允许离线玩家标记，与打开留言板等效）。 */
    public Map<String, Object> markMessagesRead(String tid, String uuid) {
        return onMain(() -> {
            if (!validUuid(uuid)) return fail("无效的玩家UUID");
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            if (!id.equals(plugin.getTeamData().getPlayerTeamId(parseUuid(uuid)))) return fail("您不在此团队中");
            plugin.getMessageData().setLastViewTime(parseUuid(uuid), id);
            plugin.getMessageData().save();
            return ok(map("team_id", id, "message", "已标记已读"));
        });
    }

    /** 标记公告已读（与游戏内查看公告等效）。 */
    public Map<String, Object> markNoticeRead(String tid, String uuid) {
        return onMain(() -> {
            if (!validUuid(uuid)) return fail("无效的玩家UUID");
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            long updatedAt = t.getNoticeUpdatedAt();
            if (t.getNotice().isBlank() || updatedAt <= 0) return ok(map("team_id", id, "changed", false));
            UUID u = parseUuid(uuid);
            if (!plugin.getMessageData().hasNewNotice(u, id, updatedAt)) return ok(map("team_id", id, "changed", false));
            plugin.getMessageData().setNoticeRead(u, id, updatedAt);
            plugin.getMessageData().save();
            return ok(map("team_id", id, "changed", true));
        });
    }

    // ===================== 管理员接口 =====================

    /** 同步团队内玩家名（对应 /tmsync，admin 标志对应 mgteam.admin）。 */
    public Map<String, Object> adminSyncNames(boolean admin) {
        if (!admin) return fail("需要管理员权限");
        return onMain(() -> {
            int fixed = 0, failed = 0, skipped = 0;
            for (Team t : plugin.getTeamData().getAll().values()) {
                for (Team.MemberEntry m : operatorsOf(t)) {
                    int[] r = syncName(m);
                    fixed += r[0]; failed += r[1]; skipped += r[2];
                }
                for (Team.MemberEntry m : membersOf(t)) {
                    int[] r = syncName(m);
                    fixed += r[0]; failed += r[1]; skipped += r[2];
                }
                for (Team.MemberApplication a : applicationsOf(t)) {
                    if (isBad(a.getName())) {
                        OfflinePlayer op = Bukkit.getOfflinePlayer(parseUuid(a.getUuid()));
                        String nn = op.getName();
                        if (nn != null && !nn.isEmpty()) {
                            a.setName(nn);
                            fixed++;
                        }
                    }
                }
                if (t.getWarpPoints() != null) {
                    for (Team.WarpPoint wp : t.getWarpPoints().values()) {
                        if (isBad(wp.getCreatorName())) {
                            try {
                                OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(wp.getCreatorUuid()));
                                String nn = op.getName();
                                if (nn != null && !nn.isEmpty()) {
                                    wp.setCreatorName(nn);
                                    fixed++;
                                }
                            } catch (Exception ex) {
                                failed++;
                            }
                        }
                    }
                }
            }
            if (fixed > 0) plugin.getTeamData().save();
            return ok(map("fixed", fixed, "failed", failed, "skipped", skipped, "message", "同步完成"));
        });
    }

    /** 重载配置（对应 config 热重载；不重载运行数据，避免丢失内存中状态）。 */
    public Map<String, Object> adminReloadConfig(boolean admin) {
        if (!admin) return fail("需要管理员权限");
        return onMain(() -> {
            plugin.getConfig2().reload();
            return ok(map("message", "配置已重载"));
        });
    }

    /** 管理员直接解散任意团队（admin 标志对应 mgteam.admin，不需为团队成员）。 */
    public Map<String, Object> adminDisband(boolean admin, String tid, String confirmName) {
        if (!admin) return fail("需要管理员权限");
        return onMain(() -> {
            String id = plugin.getTeamData().resolveId(tid);
            if (id == null) return fail("团队不存在");
            Team t = plugin.getTeamData().get(id);
            if (confirmName == null || !confirmName.trim().equals(t.getName())) return fail("名称错误");
            String tn = t.getName();
            plugin.getTeamData().remove(id);
            plugin.getMessageData().deleteTeamMessages(id);
            plugin.getFundLog().deleteTeamLogs(id);
            plugin.getTeamData().save();
            plugin.getMessageData().save();
            plugin.getFundLog().save();
            return ok(map("team_id", id, "team_name", tn, "message", "团队 " + tn + " 已解散"));
        });
    }

    // ===================== 并发与调度 =====================

    private Map<String, Object> withPlayerLock(String uuid, Callable<Map<String, Object>> task) {
        if (!validUuid(uuid)) return fail("无效的玩家UUID");
        ReentrantLock lock = playerLocks.computeIfAbsent(uuid, key -> new ReentrantLock());
        boolean acquired = false;
        try {
            acquired = lock.tryLock(15L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return fail("操作被中断，请重试");
        }
        if (!acquired) return fail("你有一笔操作正在进行，请稍后再试");
        try {
            return onMain(task);
        } finally {
            lock.unlock();
            if (!lock.isLocked()) playerLocks.remove(uuid, lock);
        }
    }

    private Map<String, Object> withManageLock(String tid, String actorUuid, boolean admin, Callable<Map<String, Object>> task) {
        if (!validUuid(actorUuid)) return fail("无效的操作者UUID");
        return withPlayerLock(actorUuid, () -> {
            if (!admin) {
                Player actor = Bukkit.getPlayer(parseUuid(actorUuid));
                if (actor == null) return fail("该操作需要操作者在线");
                String id = plugin.getTeamData().resolveId(tid);
                if (id == null) return fail("团队不存在");
                if (!plugin.getTeamData().isTeamOperator(parseUuid(actorUuid), id)) return fail("需要管理员权限");
            }
            return task.call();
        });
    }

    private Map<String, Object> onMain(Callable<Map<String, Object>> task) {
        if (Bukkit.isPrimaryThread()) {
            try {
                return task.call();
            } catch (Exception e) {
                plugin.getLogger().warning("[WebTeam] 操作异常: " + e.getMessage());
                return fail("操作失败: " + e.getMessage());
            }
        }
        try {
            return Bukkit.getScheduler().callSyncMethod(plugin, task).get(30L, TimeUnit.SECONDS);
        } catch (Exception e) {
            plugin.getLogger().warning("[WebTeam] 主线程执行失败: " + e.getMessage());
            return fail("服务器繁忙，请稍后再试");
        }
    }

    // ===================== 视图与工具 =====================

    private Map<String, Object> teamView(String id, Team t) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("team_id", id);
        view.put("name", t.getName());
        view.put("funds", t.getFunds());
        view.put("activity", t.getActivity());
        view.put("created_at", t.getCreatedAt());
        view.put("public", t.isPublic());
        view.put("allow_friendly_fire", t.isAllowFriendlyFire());
        view.put("notice", t.getNotice());
        view.put("notice_updated_at", t.getNoticeUpdatedAt());
        view.put("member_count", t.getMemberCount());
        view.put("operator_count", operatorsOf(t).size());
        view.put("warp_count", t.getWarpPoints() == null ? 0 : t.getWarpPoints().size());
        view.put("message_count", plugin.getMessageData().getMessages(id).size());
        view.put("application_count", applicationsOf(t).size());
        view.put("currency_name", plugin.getConfig2().getCurrencyName());
        return view;
    }

    private Map<String, Object> teamDetailView(String id, Team t) {
        Map<String, Object> view = teamView(id, t);
        List<Map<String, Object>> members = new ArrayList<>();
        for (Team.MemberEntry m : operatorsOf(t)) members.add(memberView(m, true));
        for (Team.MemberEntry m : membersOf(t)) members.add(memberView(m, false));
        view.put("members", members);
        return view;
    }

    private Map<String, Object> memberView(Team.MemberEntry m, boolean operator) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("uuid", m.getUuid());
        view.put("name", Util.plainName(m.getName()));
        view.put("role", operator ? "OPERATOR" : "MEMBER");
        try {
            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(m.getUuid()));
            view.put("online", op.isOnline());
        } catch (Exception e) {
            view.put("online", false);
        }
        return view;
    }

    private Map<String, Object> warpView(String name, Team.WarpPoint wp) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("name", name);
        view.put("x", wp.getX());
        view.put("y", wp.getY());
        view.put("z", wp.getZ());
        view.put("dim", wp.getDim());
        view.put("world", wp.getWorld());
        view.put("world_display", wp.getWorldDisplay());
        view.put("creator_uuid", wp.getCreatorUuid());
        view.put("creator_name", Util.plainName(wp.getCreatorName()));
        view.put("icon", wp.getIcon());
        view.put("created_at", wp.getCreatedAt());
        return view;
    }

    private Map<String, Object> messageView(MessageEntry m) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("sender_uuid", m.getSenderUuid());
        view.put("sender_name", Util.plainName(m.getSenderName()));
        view.put("content", m.getContent());
        view.put("time", m.getTime());
        view.put("timestamp", m.getTimestamp());
        return view;
    }

    private Map<String, Object> fundLogView(FundLogEntry e) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("timestamp", e.getTimestamp());
        view.put("time", e.getTime());
        view.put("change", e.getChange());
        view.put("reason", e.getReason());
        view.put("balance_before", e.getBalanceBefore());
        view.put("balance_after", e.getBalanceAfter());
        return view;
    }

    private Map<String, Object> applicationView(Team.MemberApplication a) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("uuid", a.getUuid());
        view.put("name", Util.plainName(a.getName()));
        view.put("applied_at", a.getAppliedAt());
        return view;
    }

    private <T> Map<String, Object> pageView(List<T> list, int page, int pageSize, int defaultSize, int maxSize, java.util.function.Function<T, Map<String, Object>> mapper) {
        int ps = pageSize < 1 || pageSize > maxSize ? defaultSize : pageSize;
        int p = page < 1 ? 1 : page;
        int totalPages = Math.max(1, (list.size() + ps - 1) / ps);
        int current = Math.min(p, totalPages);
        int start = (current - 1) * ps;
        int end = Math.min(start + ps, list.size());
        List<Map<String, Object>> views = new ArrayList<>();
        for (int i = start; i < end; i++) views.add(mapper.apply(list.get(i)));
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("page", current);
        data.put("page_size", ps);
        data.put("total_pages", totalPages);
        data.put("total_items", list.size());
        data.put("items", views);
        return data;
    }

    private static int compareTeams(Team a, Team b) {
        if (a.getActivity() > 0 && b.getActivity() == 0) return -1;
        if (b.getActivity() > 0 && a.getActivity() == 0) return 1;
        if (a.getActivity() > 0 && b.getActivity() > 0) return Long.compare(b.getActivity(), a.getActivity());
        return Long.compare(b.getFunds(), a.getFunds());
    }

    private boolean matchesQuery(String id, Team t, String q) {
        return id.equalsIgnoreCase(q)
            || t.getName().toLowerCase().contains(q.toLowerCase())
            || id.toLowerCase().contains(q.toLowerCase());
    }

    private String resolveWarpName(Team t, String name) {
        if (name == null) return null;
        String n = name.trim();
        if (t.getWarpPoints().containsKey(n)) return n;
        for (String k : t.getWarpPoints().keySet()) {
            if (k.equalsIgnoreCase(n)) return k;
        }
        return null;
    }

    private World resolveWorld(Team.WarpPoint wp) {
        World w = null;
        String wName = wp.getWorld();
        if (wName != null && !wName.isEmpty()) w = Bukkit.getWorld(wName);
        if (w == null) {
            switch (wp.getDim()) {
                case -1: w = Bukkit.getWorlds().stream().filter(w2 -> w2.getEnvironment() == World.Environment.NETHER).findFirst().orElse(null); break;
                case 1: w = Bukkit.getWorlds().stream().filter(w2 -> w2.getEnvironment() == World.Environment.THE_END).findFirst().orElse(null); break;
                default: w = Bukkit.getWorlds().stream().filter(w2 -> w2.getEnvironment() == World.Environment.NORMAL).findFirst().orElse(null); break;
            }
        }
        return w;
    }

    private Player onlinePlayer(String uuid) {
        if (!validUuid(uuid)) return null;
        Player pl = Bukkit.getPlayer(parseUuid(uuid));
        return pl != null && pl.isOnline() ? pl : null;
    }

    private Team.MemberEntry findMember(Team t, String uuid) {
        for (Team.MemberEntry m : operatorsOf(t)) if (uuid.equalsIgnoreCase(m.getUuid())) return m;
        for (Team.MemberEntry m : membersOf(t)) if (uuid.equalsIgnoreCase(m.getUuid())) return m;
        return null;
    }

    private Team.MemberApplication findApplication(Team t, String uuid) {
        for (Team.MemberApplication a : applicationsOf(t)) if (uuid.equalsIgnoreCase(a.getUuid())) return a;
        return null;
    }

    private boolean isOperator(Team t, String uuid) {
        for (Team.MemberEntry m : operatorsOf(t)) if (uuid.equalsIgnoreCase(m.getUuid())) return true;
        return false;
    }

    private List<Team.MemberEntry> operatorsOf(Team t) {
        return t.getOperators() == null ? new ArrayList<>() : t.getOperators();
    }

    private List<Team.MemberEntry> membersOf(Team t) {
        return t.getMembers() == null ? new ArrayList<>() : t.getMembers();
    }

    private List<Team.MemberApplication> applicationsOf(Team t) {
        return t.getMembersapplications() == null ? new ArrayList<>() : t.getMembersapplications();
    }

    private String amountError(long amount) {
        if (amount <= 0) return "金额必须为正整数";
        if (amount > 9007199254740991L) return "金额过大";
        return null;
    }

    private int[] syncName(Team.MemberEntry m) {
        if (isBad(m.getName())) {
            try {
                OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(m.getUuid()));
                String nn = op.getName();
                if (nn != null && !nn.isEmpty()) {
                    m.setName(nn);
                    return new int[]{1, 0, 0};
                }
                return new int[]{0, 1, 0};
            } catch (Exception ex) {
                return new int[]{0, 1, 0};
            }
        }
        return new int[]{0, 0, 1};
    }

    private boolean isBad(String n) {
        return n == null || n.isEmpty() || "null".equals(n);
    }

    private boolean validUuid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private UUID parseUuid(String uuid) {
        return UUID.fromString(uuid);
    }

    private Map<String, Object> ok(Map<String, Object> data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ok", true);
        result.put("data", data == null ? new LinkedHashMap<>() : data);
        return result;
    }

    private Map<String, Object> fail(String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ok", false);
        result.put("message", message);
        return result;
    }

    private Map<String, Object> map(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) map.put(String.valueOf(kv[i]), kv[i + 1]);
        return map;
    }
}
