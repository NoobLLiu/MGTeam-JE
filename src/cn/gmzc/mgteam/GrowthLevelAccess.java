package cn.gmzc.mgteam;

import cn.gmzc.titles.api.GrowthUnlocks;
import cn.gmzc.titles.api.TitleLevelService;
import cn.gmzc.titles.api.TitleLevelServices;
import org.bukkit.entity.Player;

/**
 * 成长等级访问控制——与菜单/交易市场/展柜/竞技场相同的等级门槛。
 * 等级由 GMZCTitles 提供（softdepend），服务不可用时一律按 0 级处理。
 */
public final class GrowthLevelAccess {

    public static final int REQUIRED_LEVEL = GrowthUnlocks.TEAM_LEVEL;

    private GrowthLevelAccess() {
    }

    public static int level(Player player) {
        if (player == null) {
            return 0;
        }
        TitleLevelService service = TitleLevelServices.get();
        if (service == null) {
            return 0;
        }
        try {
            return Math.max(0, service.getLevel(player.getUniqueId()));
        } catch (RuntimeException ignored) {
            return 0;
        }
    }

    public static boolean restricted(Player player) {
        return player != null && level(player) < REQUIRED_LEVEL;
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
