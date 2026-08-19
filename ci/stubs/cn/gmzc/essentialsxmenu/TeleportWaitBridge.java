package cn.gmzc.essentialsxmenu;

import org.bukkit.entity.Player;

/** 云端编译用 ABI 桩：与服务器内 GMZCEssentialsMenu 的传送等待桥对齐。 */
public final class TeleportWaitBridge {
    public static boolean startWarmup(Player player, Runnable task) {
        return false;
    }

    public static void allowNextTeleport(Player player) {
    }

    private TeleportWaitBridge() {
    }
}
