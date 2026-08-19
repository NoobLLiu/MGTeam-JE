package cn.gmzc.skincache.api;

import java.util.UUID;
import org.bukkit.inventory.meta.SkullMeta;

/** 云端编译用 ABI 桩：仅编译期对齐 GMZCSkinCache 服务签名，不打进插件 jar。 */
public interface PlayerSkinService {
    void apply(SkullMeta meta, UUID playerId);
}
