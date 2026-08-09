package cn.gmzc.mgteam.gui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;

public final class WarpIconCatalog {
    private static final List<WarpIcon> ICONS = List.of(
        new WarpIcon(Material.COMPASS, "\u6307\u5357\u9488"),
        new WarpIcon(Material.GRASS_BLOCK, "\u8349\u65b9\u5757"),
        new WarpIcon(Material.DIRT, "\u6ce5\u571f\u5757"),
        new WarpIcon(Material.COBBLESTONE, "\u5706\u77f3"),
        new WarpIcon(Material.STONE, "\u77f3\u5934"),
        new WarpIcon(Material.BLUE_ICE, "\u84dd\u51b0"),
        new WarpIcon(Material.OBSIDIAN, "\u9ed1\u66dc\u77f3"),
        new WarpIcon(Material.NETHERRACK, "\u4e0b\u754c\u5ca9"),
        new WarpIcon(Material.TARGET, "\u6807\u9776"),
        new WarpIcon(Material.OBSERVER, "\u89c2\u5bdf\u8005"),
        new WarpIcon(Material.CHEST, "\u7bb1\u5b50"),
        new WarpIcon(Material.PISTON, "\u6d3b\u585e"),
        new WarpIcon(Material.BOOKSHELF, "\u4e66\u67b6"),
        new WarpIcon(Material.WHITE_WOOL, "\u767d\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.ORANGE_WOOL, "\u6a59\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.MAGENTA_WOOL, "\u54c1\u7ea2\u7f8a\u6bdb"),
        new WarpIcon(Material.LIGHT_BLUE_WOOL, "\u6de1\u84dd\u7f8a\u6bdb"),
        new WarpIcon(Material.YELLOW_WOOL, "\u9ec4\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.LIME_WOOL, "\u9ec4\u7eff\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.PINK_WOOL, "\u7c89\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.GRAY_WOOL, "\u7070\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.LIGHT_GRAY_WOOL, "\u6de1\u7070\u7f8a\u6bdb"),
        new WarpIcon(Material.CYAN_WOOL, "\u9752\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.PURPLE_WOOL, "\u7d2b\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.BLUE_WOOL, "\u84dd\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.BROWN_WOOL, "\u68d5\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.GREEN_WOOL, "\u7eff\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.RED_WOOL, "\u7ea2\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.BLACK_WOOL, "\u9ed1\u8272\u7f8a\u6bdb"),
        new WarpIcon(Material.SPAWNER, "\u5237\u602a\u7b3c"),
        new WarpIcon(Material.TRIAL_SPAWNER, "\u8bd5\u70bc\u5237\u602a\u7b3c"),
        new WarpIcon(Material.HEAVY_CORE, "\u6c89\u91cd\u6838\u5fc3"),
        new WarpIcon(Material.CRAFTER, "\u81ea\u52a8\u5408\u6210\u5668"),
        new WarpIcon(Material.DRAGON_EGG, "\u9f99\u86cb"),
        new WarpIcon(Material.TNT, "TNT"),
        new WarpIcon(Material.END_PORTAL_FRAME, "\u672b\u5730\u4f20\u9001\u95e8\u6846\u67b6"),
        new WarpIcon(Material.HONEYCOMB, "\u871c\u818a"),
        new WarpIcon(Material.HEART_OF_THE_SEA, "\u6d77\u6d0b\u4e4b\u5fc3"),
        new WarpIcon(Material.NETHER_STAR, "\u4e0b\u754c\u4e4b\u661f"),
        new WarpIcon(Material.BOOK, "\u4e66"),
        new WarpIcon(Material.ENCHANTED_BOOK, "\u9644\u9b54\u4e66"),
        new WarpIcon(Material.WHEAT, "\u5c0f\u9ea6"),
        new WarpIcon(Material.LEATHER, "\u76ae\u9769"),
        new WarpIcon(Material.CARROT, "\u80e1\u841d\u535c"),
        new WarpIcon(Material.BREAD, "\u9762\u5305"),
        new WarpIcon(Material.POTATO, "\u9a6c\u94c3\u85af"),
        new WarpIcon(Material.POISONOUS_POTATO, "\u6bd2\u9a6c\u94c3\u85af"),
        new WarpIcon(Material.SLIME_BALL, "\u7c98\u6db2\u7403"),
        new WarpIcon(Material.ENDER_PEARL, "\u672b\u5f71\u73cd\u73e0"),
        new WarpIcon(Material.ENDER_EYE, "\u672b\u5f71\u4e4b\u773c"),
        new WarpIcon(Material.MINECART, "\u77ff\u8f66"),
        new WarpIcon(Material.CHEST_MINECART, "\u7bb1\u5b50\u77ff\u8f66"),
        new WarpIcon(Material.OAK_LOG, "\u6a61\u6728\u539f\u6728"),
        new WarpIcon(Material.SPRUCE_LOG, "\u4e91\u6749\u539f\u6728"),
        new WarpIcon(Material.BIRCH_LOG, "\u767d\u6866\u539f\u6728"),
        new WarpIcon(Material.JUNGLE_LOG, "\u4e1b\u6797\u539f\u6728"),
        new WarpIcon(Material.ACACIA_LOG, "\u91d1\u5408\u6b22\u539f\u6728"),
        new WarpIcon(Material.DARK_OAK_LOG, "\u6df1\u8272\u6a61\u6728\u539f\u6728"),
        new WarpIcon(Material.MANGROVE_LOG, "\u7ea2\u6811\u6728\u539f\u6728"),
        new WarpIcon(Material.CHERRY_LOG, "\u6a31\u82b1\u539f\u6728"),
        new WarpIcon(Material.PALE_OAK_LOG, "\u82cd\u767d\u6a61\u6728\u539f\u6728"),
        new WarpIcon(Material.CRIMSON_STEM, "\u7ecf\u7ea2\u83cc\u67c4"),
        new WarpIcon(Material.WARPED_STEM, "\u8be1\u5f02\u83cc\u67c4"),
        new WarpIcon(Material.COAL_BLOCK, "\u7164\u70ad\u5757"),
        new WarpIcon(Material.IRON_BLOCK, "\u94c1\u5757"),
        new WarpIcon(Material.COPPER_BLOCK, "\u94dc\u5757"),
        new WarpIcon(Material.GOLD_BLOCK, "\u91d1\u5757"),
        new WarpIcon(Material.DIAMOND_BLOCK, "\u94bb\u77f3\u5757"),
        new WarpIcon(Material.EMERALD_BLOCK, "\u7eff\u5b9d\u77f3\u5757"),
        new WarpIcon(Material.REDSTONE_BLOCK, "\u7ea2\u77f3\u5757"),
        new WarpIcon(Material.LAPIS_BLOCK, "\u9752\u91d1\u77f3\u5757"),
        new WarpIcon(Material.NETHERITE_BLOCK, "\u4e0b\u754c\u5408\u91d1\u5757"),
        new WarpIcon(Material.QUARTZ_BLOCK, "\u77f3\u82f1\u5757"),
        new WarpIcon(Material.AMETHYST_BLOCK, "\u7d2b\u6c34\u6676\u5757"),
        new WarpIcon(Material.COAL, "\u7164\u70ad"),
        new WarpIcon(Material.IRON_INGOT, "\u94c1\u9531"),
        new WarpIcon(Material.COPPER_INGOT, "\u94dc\u9531"),
        new WarpIcon(Material.GOLD_INGOT, "\u91d1\u9531"),
        new WarpIcon(Material.DIAMOND, "\u94bb\u77f3"),
        new WarpIcon(Material.EMERALD, "\u7eff\u5b9d\u77f3"),
        new WarpIcon(Material.REDSTONE, "\u7ea2\u77f3\u7c89"),
        new WarpIcon(Material.LAPIS_LAZULI, "\u9752\u91d1\u77f3"),
        new WarpIcon(Material.QUARTZ, "\u4e0b\u754c\u77f3\u82f1"),
        new WarpIcon(Material.NETHERITE_INGOT, "\u4e0b\u754c\u5408\u91d1\u9531"),
        new WarpIcon(Material.AMETHYST_SHARD, "\u7d2b\u6c34\u6676\u788e\u7247"),
        new WarpIcon(Material.CRAFTING_TABLE, "\u5de5\u4f5c\u53f0"),
        new WarpIcon(Material.ENCHANTING_TABLE, "\u9644\u9b54\u53f0"),
        new WarpIcon(Material.ANVIL, "\u94c1\u5777"),
        new WarpIcon(Material.SMITHING_TABLE, "\u953b\u9020\u53f0"),
        new WarpIcon(Material.CARTOGRAPHY_TABLE, "\u5236\u56fe\u53f0"),
        new WarpIcon(Material.FLETCHING_TABLE, "\u5236\u7bad\u53f0"),
        new WarpIcon(Material.GRINDSTONE, "\u7802\u8f6e"),
        new WarpIcon(Material.LOOM, "\u7ec7\u5e03\u673a"),
        new WarpIcon(Material.STONECUTTER, "\u5207\u77f3\u673a"),
        new WarpIcon(Material.BREWING_STAND, "\u917f\u9020\u53f0"),
        new WarpIcon(Material.FURNACE, "\u7194\u7089"),
        new WarpIcon(Material.BLAST_FURNACE, "\u9ad8\u7089"),
        new WarpIcon(Material.SMOKER, "\u70df\u718f\u7089"),
        new WarpIcon(Material.BARREL, "\u6728\u6876"),
        new WarpIcon(Material.CAULDRON, "\u70bc\u836f\u9505"),
        new WarpIcon(Material.COMPOSTER, "\u5806\u80a5\u6876"),
        new WarpIcon(Material.WHITE_DYE, "\u767d\u8272\u67d3\u6599"),
        new WarpIcon(Material.ORANGE_DYE, "\u6a59\u8272\u67d3\u6599"),
        new WarpIcon(Material.MAGENTA_DYE, "\u54c1\u7ea2\u67d3\u6599"),
        new WarpIcon(Material.LIGHT_BLUE_DYE, "\u6de1\u84dd\u67d3\u6599"),
        new WarpIcon(Material.YELLOW_DYE, "\u9ec4\u8272\u67d3\u6599"),
        new WarpIcon(Material.LIME_DYE, "\u9ec4\u7eff\u8272\u67d3\u6599"),
        new WarpIcon(Material.PINK_DYE, "\u7c89\u8272\u67d3\u6599"),
        new WarpIcon(Material.GRAY_DYE, "\u7070\u8272\u67d3\u6599"),
        new WarpIcon(Material.LIGHT_GRAY_DYE, "\u6de1\u7070\u67d3\u6599"),
        new WarpIcon(Material.CYAN_DYE, "\u9752\u8272\u67d3\u6599"),
        new WarpIcon(Material.PURPLE_DYE, "\u7d2b\u8272\u67d3\u6599"),
        new WarpIcon(Material.BLUE_DYE, "\u84dd\u8272\u67d3\u6599"),
        new WarpIcon(Material.BROWN_DYE, "\u68d5\u8272\u67d3\u6599"),
        new WarpIcon(Material.GREEN_DYE, "\u7eff\u8272\u67d3\u6599"),
        new WarpIcon(Material.RED_DYE, "\u7ea2\u8272\u67d3\u6599"),
        new WarpIcon(Material.BLACK_DYE, "\u9ed1\u8272\u67d3\u6599"),
        new WarpIcon(Material.VILLAGER_SPAWN_EGG, "\u6751\u6c11\u5237\u602a\u86cb"),
        new WarpIcon(Material.IRON_GOLEM_SPAWN_EGG, "\u94c1\u50f5\u5152\u5237\u602a\u86cb"),
        new WarpIcon(Material.ZOMBIE_SPAWN_EGG, "\u50f5\u5c38\u5237\u602a\u86cb"),
        new WarpIcon(Material.PIGLIN_SPAWN_EGG, "\u732a\u7075\u5237\u602a\u86cb"),
        new WarpIcon(Material.BEE_SPAWN_EGG, "\u871c\u8702\u5237\u602a\u86cb"),
        new WarpIcon(Material.WITCH_SPAWN_EGG, "\u5973\u5deb\u5237\u602a\u86cb"),
        new WarpIcon(Material.AXOLOTL_SPAWN_EGG, "\u7f8e\u897f\u8712\u5237\u602a\u86cb"),
        new WarpIcon(Material.PIG_SPAWN_EGG, "\u732a\u5237\u602a\u86cb"),
        new WarpIcon(Material.CHICKEN_SPAWN_EGG, "\u9e21\u5237\u602a\u86cb"),
        new WarpIcon(Material.SNIFFER_SPAWN_EGG, "\u55c5\u63a2\u517d\u5237\u602a\u86cb"),
        new WarpIcon(Material.PANDA_SPAWN_EGG, "\u718a\u732b\u5237\u602a\u86cb"),
        new WarpIcon(Material.EVOKER_SPAWN_EGG, "\u5e7b\u9b54\u8005\u5237\u602a\u86cb"),
        new WarpIcon(Material.GUARDIAN_SPAWN_EGG, "\u5b88\u536b\u8005\u5237\u602a\u86cb"),
        new WarpIcon(Material.ALLAY_SPAWN_EGG, "\u60a6\u7075\u5237\u602a\u86cb"),
        new WarpIcon(Material.WARDEN_SPAWN_EGG, "\u575a\u5b88\u8005\u5237\u602a\u86cb"),
        new WarpIcon(Material.BLAZE_SPAWN_EGG, "\u70c8\u706b\u4eba\u5237\u602a\u86cb"),
        new WarpIcon(Material.CREEPER_SPAWN_EGG, "\u82e6\u529b\u6015\u5237\u602a\u86cb"),
        new WarpIcon(Material.ENDERMAN_SPAWN_EGG, "\u672b\u5f71\u4eba\u5237\u602a\u86cb"),
        new WarpIcon(Material.SLIME_SPAWN_EGG, "\u53f2\u83b1\u59c6\u5237\u602a\u86cb")
    );
    private static final Map<String, WarpIcon> BY_NAME = buildIndex();

    private WarpIconCatalog() {
    }

    public static List<WarpIcon> all() {
        return ICONS;
    }

    public static WarpIcon defaultIcon() {
        return ICONS.getFirst();
    }

    public static WarpIcon resolve(String materialName) {
        if (materialName == null || materialName.isBlank()) {
            return defaultIcon();
        }
        return BY_NAME.getOrDefault(materialName.toUpperCase(), defaultIcon());
    }

    private static Map<String, WarpIcon> buildIndex() {
        LinkedHashMap<String, WarpIcon> index = new LinkedHashMap<>();
        for (WarpIcon icon : ICONS) {
            index.put(icon.material().name(), icon);
        }
        return Map.copyOf(index);
    }
}
