package cn.gmzc.mgteam;

import cn.gmzc.titles.api.GrowthUnlocks;
import cn.gmzc.titles.api.TitleLevelService;
import cn.gmzc.titles.api.TitleLevelServices;
import org.bukkit.entity.Player;

/**
 * 成长等级访问控制——与菜单/交易市场/展柜/竞技场相同的等级门槛。
 * 等级由 GMZCTitles 提供（softdepend）。
 * 启动时自动检测 GMZCTitles 服务：已安装则保持原有等级门槛；
 * 未安装时等级系统自动停用（等价于无门槛），插件其余功能不受影响，全程静默。
 */
public final class GrowthLevelAccess {

    private static volatile boolean levelSystemEnabled;

    public static final int REQUIRED_LEVEL = safeTeamLevel();

    private static int safeTeamLevel() {
        try {
            return GrowthUnlocks.TEAM_LEVEL;
        } catch (Throwable t) {
            return 0;
        }
    }

    private GrowthLevelAccess() {
    }

    /** 启动时检测 GMZCTitles 是否可用，不可用时静默停用等级门槛。 */
    public static void init() {
        levelSystemEnabled = false;
        try {
            levelSystemEnabled = TitleLevelServices.get() != null;
        } catch (Throwable ignored) {
            levelSystemEnabled = false;
        }
    }

    public static boolean isLevelSystemEnabled() {
        return levelSystemEnabled;
    }

    public static int level(Player player) {
        if (player == null) {
            return 0;
        }
        try {
            TitleLevelService service = TitleLevelServices.get();
            if (service == null) {
                return 0;
            }
            return Math.max(0, service.getLevel(player.getUniqueId()));
        } catch (Throwable ignored) {
            return 0;
        }
    }

    public static boolean restricted(Player player) {
        if (player == null) {
            return false;
        }
        if (!levelSystemEnabled) {
            return false;
        }
        return level(player) < REQUIRED_LEVEL;
    }

    public static boolean deny(Player player) {
        if (!restricted(player)) {
            return false;
        }
        player.sendMessage(
            "§c团队系统需要成长等级达到 §e" + REQUIRED_LEVEL
                + "§c 级后才能使用。当前成长等级：§f" + level(player)
        );
        return true;
    }
}
