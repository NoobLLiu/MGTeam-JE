package cn.gmzc.mgteam.gui;

import cn.gmzc.essentialsxmenu.TeleportWaitBridge;
import cn.gmzc.mgteam.MGTeamPlugin;
import cn.gmzc.mgteam.GrowthLevelAccess;
import cn.gmzc.mgteam.model.FundLogEntry;
import cn.gmzc.mgteam.model.MessageEntry;
import cn.gmzc.mgteam.model.Team;
import cn.gmzc.mgteam.util.Util;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class JavaMenus {
    private static MGTeamPlugin p() { return GuiRouter.p(); }
    private static String cur() { return GuiRouter.cur(); }
    public static Map<UUID,PendingChatInput> pendingInputs = new HashMap<>();
    private static final Map<UUID, Consumer<Material>> pendingIconCallbacks = new HashMap<>();

    private static final int PAGED_SIZE = 54;
    private static final int PAGE_AREA = 45;
    private static final int SLOT_PREV = 45;
    private static final int SLOT_INFO = 46;
    private static final int SLOT_NEXT = 52;
    private static final int SLOT_BACK = 53;

    private static ItemStack filler(Material m) { ItemStack i = new ItemStack(m); ItemMeta me = i.getItemMeta(); me.setDisplayName(" "); i.setItemMeta(me); return i; }

    public static void handleChatInput(Player pl, String msg) {
        PendingChatInput pi = pendingInputs.remove(pl.getUniqueId());
        if (pi != null) pi.cb.accept(msg);
    }

    private static void requestInput(Player pl, String title, String prompt, Consumer<String> cb) {
        String anvilTitle = title != null ? title : "\u00a7l\u8f93\u5165";
        String placeholder = "";
        if (prompt != null && !prompt.isBlank()) {
            String clean = prompt.replaceAll("\u8f93\u5165", "").replaceAll(":", "").replaceAll("\uff1a", "").trim();
            if (!clean.isEmpty()) placeholder = clean;
            else placeholder = prompt;
        }
        p().getAnvilInputGUI().openInput(pl, anvilTitle, placeholder, cb);
    }

    private static void requestIC(Player pl, String title, String prompt, Consumer<String> cb) {
        requestInput(pl,title,prompt,in -> { if(in == null || in.equalsIgnoreCase("cancel")) pl.sendMessage("\u00a77\u5df2\u53d6\u6d88"); else cb.accept(in); });
    }

    private static ItemStack item(Material m, String n, String... lore) {
        return itemWithModelData(m, n, null, lore);
    }

    private static ItemStack itemWithModelData(Material m, String n, Integer modelData, String... lore) {
        ItemStack i = new ItemStack(m); ItemMeta meta = i.getItemMeta(); meta.setDisplayName(n);
        if(lore.length>0){List<String> l=new ArrayList<>();for(String s:lore)l.add(s);meta.setLore(l);}
        Integer customModelData = modelData != null ? modelData : functionalModelData(n);
        if (customModelData != null) meta.setCustomModelData(customModelData);
        i.setItemMeta(meta); return i;
    }

    private static Integer functionalModelData(String displayName) {
        String name = ChatColor.stripColor(displayName == null ? "" : displayName);
        if (name.contains("\u8fd4\u56de")) return 2400013;
        if (name.contains("\u4e0a\u4e00\u9875")) return 2400011;
        if (name.contains("\u4e0b\u4e00\u9875")) return 2400012;
        if (name.contains("\u5173\u95ed")) return 2400015;
        if (name.contains("\u53d6\u6d88") || name.contains("\u5ffd\u7565") || name.contains("\u7a0d\u540e")) return 2400015;
        if (name.contains("\u901a\u8fc7") || name.contains("\u786e\u5b9a")) return 2400014;
        if (name.contains("\u89e3\u6563") || name.contains("\u5220\u9664")) return 2400018;
        if (name.contains("\u9000\u51fa") || name.contains("\u79bb\u5f00")) return 2400038;
        if (name.contains("\u79fb\u51fa")) return 2400017;
        if (name.contains("\u65b0\u5efa") || name.contains("\u521b\u5efa") || name.contains("\u6dfb\u52a0")) return 2400016;
        if (name.contains("\u7533\u8bf7") || name.contains("\u9080\u8bf7")) return 2400035;
        if (name.contains("\u8be6\u60c5") || name.contains("\u8be6\u7ec6") || name.contains("\u63d0\u793a")) return 2400010;
        if (name.contains("\u4f20\u9001\u951a\u70b9")) return 2400020;
        if (name.contains("\u4e92\u4f20")) return 2400024;
        if (name.contains("\u56e2\u961f\u516c\u544a") || name.contains("\u516c\u544a")) return 2400027;
        if (name.contains("\u7559\u8a00") || name.contains("\u65b0\u7559\u8a00") || name.contains("\u7acb\u5373\u67e5\u770b")) return 2400026;
        if (name.contains("\u79ef\u91d1") || name.contains("\u8d44\u91d1") || name.contains("\u6d41\u6c34")) return 2400025;
        if (name.contains("\u5b58\u5165")) return 2400016;
        if (name.contains("\u53d6\u51fa")) return 2400045;
        if (name.contains("\u6d88\u8d39")) return 2400019;
        if (name.contains("\u641c\u7d22")) return 2400031;
        if (name.contains("\u6392\u884c\u699c")) return 2400036;
        if (name.contains("\u7ba1\u7406\u6210\u5458")) return 2400024;
        if (name.equals("\u7ba1\u7406") || name.contains("\u7ba1\u7406\u56e2\u961f")) return 2400028;
        if (name.contains("\u56fe\u6807\u9009\u62e9")) return 2400010;
        if (name.contains("\u8bbe\u4e3a\u7ba1\u7406\u5458") || name.contains("\u8bbe\u4e3a\u6210\u5458")) return 2400028;
        if (name.contains("\u72b6\u6001")) return name.contains("\u516c\u5f00") ? 2400029 : 2400030;
        return null;
    }

    private static ItemStack skull(Team.MemberEntry member, String display, String... lore) {
        ItemStack i = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta m = (SkullMeta) i.getItemMeta();
        m.setDisplayName(display);
        if (member != null) {
            p().getPlayerSkinService().apply(m, member.getUniqueId());
        }
        if (lore.length > 0) { List<String> l = new ArrayList<>(); for (String s : lore) l.add(s); m.setLore(l); }
        i.setItemMeta(m);
        return i;
    }

    private static ItemStack skull(UUID ownerId, String display, String... lore) {
        ItemStack i = new ItemStack(Material.PLAYER_HEAD); SkullMeta m=(SkullMeta)i.getItemMeta(); m.setDisplayName(display); p().getPlayerSkinService().apply(m, ownerId);
        if(lore.length>0){List<String> l=new ArrayList<>();for(String s:lore)l.add(s);m.setLore(l);}
        i.setItemMeta(m); return i;
    }

    private static ItemStack decorativeSkull(String display, String... lore) {
        ItemStack i = new ItemStack(Material.PLAYER_HEAD); SkullMeta m=(SkullMeta)i.getItemMeta(); m.setDisplayName(display);
        if(lore.length>0){List<String> l=new ArrayList<>();for(String s:lore)l.add(s);m.setLore(l);}
        i.setItemMeta(m); return i;
    }

    private static Material warpIconMaterial(Team.WarpPoint wp) {
        try { return Material.valueOf(wp.getIcon()); } catch (Exception e) { return Material.COMPASS; }
    }

    private static String lineText(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.replace("\\n", " ").replace("\n", " ");
    }

    // ==================== MAIN MENU ====================
    public static void openMainMenu(Player pl) {
        if (GrowthLevelAccess.deny(pl)) {
            return;
        }
        String tid = p().getTeamData().getPlayerTeamId(pl.getUniqueId());
        if (tid != null) {
            Team t = p().getTeamData().get(tid); boolean isOp = p().getTeamData().isTeamOperator(pl.getUniqueId(), tid);
            boolean hasNotice = t.getNotice() != null && !t.getNotice().isEmpty();
            boolean hasNewMessages = p().getMessageData().hasNewMessages(pl.getUniqueId(), tid);
            boolean hasNewNotice = hasNotice
                && p().getMessageData().hasNewNotice(pl.getUniqueId(), tid, t.getNoticeUpdatedAt());
            int serverMenuSlot = isOp ? 17 : (hasNotice ? 6 : 7);
            int online=0; for(Player op:Bukkit.getOnlinePlayers()){if(!op.getUniqueId().equals(pl.getUniqueId())&&tid.equals(p().getTeamData().getPlayerTeamId(op.getUniqueId())))online++;}
            Inventory inv = Bukkit.createInventory(null, isOp?18:9, "\u00a7l"+t.getName());
            inv.setItem(0, item(Material.BOOK, "\u00a7l\u8be6\u60c5\u4fe1\u606f","\u00a7r\u00a7t\u67e5\u770b\u56e2\u961f\u8be6\u60c5"));
            inv.setItem(1, item(Material.COMPASS, "\u00a7l\u4f20\u9001\u951a\u70b9","\u00a7r\u00a7t\u56e2\u961f\u5171\u4eab\u4f20\u9001\u70b9"));
            inv.setItem(2, item(Material.ENDER_PEARL, online>0?"\u00a7l\u4e92\u4f20 \u00a72"+online:"\u00a7l\u4e92\u4f20","\u00a7r\u00a7t\u514d\u540c\u610f\u4f20\u9001"));
            inv.setItem(3, item(Material.GOLD_NUGGET, "\u00a7l\u79ef\u91d1","\u00a7r\u00a7t"+t.getFunds()+cur(),"\u00a77\u56e2\u961f\u516c\u5171\u8d44\u91d1\u7ba1\u7406"));
            if(hasNewMessages) inv.setItem(4, itemWithModelData(Material.WRITABLE_BOOK, "\u00a7l\u7559\u8a00\u677f", 2400053, "\u00a7r\u00a72\u6709\u65b0\u7559\u8a00"));
            else inv.setItem(4, item(Material.WRITABLE_BOOK, "\u00a7l\u7559\u8a00\u677f","\u00a7r\u00a7t\u67e5\u770b\u53d1\u5e03\u7559\u8a00","\u00a77\u56e2\u961f\u5185\u90e8\u7559\u8a00\u4ea4\u6d41"));
            if(isOp) { inv.setItem(6, item(Material.REPEATER, "\u00a7l\u7ba1\u7406\u56e2\u961f","\u00a7r\u00a7t\u7ba1\u7406\u5458\u83dc\u5355")); inv.setItem(8, item(Material.BARRIER, "\u00a7c\u00a7l\u9000\u51fa\u56e2\u961f","\u00a77\u9000\u51fa\u540e\u6570\u636e\u4e0d\u53ef\u6062\u590d\uff0c\u8bf7\u8c28\u614e\u64cd\u4f5c")); }
            else inv.setItem(8, item(Material.BARRIER, "\u00a7c\u00a7l\u9000\u51fa\u56e2\u961f","\u00a77\u9000\u51fa\u540e\u6570\u636e\u4e0d\u53ef\u6062\u590d\uff0c\u8bf7\u8c28\u614e\u64cd\u4f5c"));
            if (hasNotice) {
                String preview = lineText(t.getNotice());
                if(preview.length()>30) preview = preview.substring(0,30)+"...";
                inv.setItem(7, itemWithModelData(
                    Material.PAPER,
                    "\u00a7l\u00a76\u56e2\u961f\u516c\u544a",
                    hasNewNotice ? 2400054 : 2400027,
                    "\u00a7f"+preview,
                    "\u00a77\u70b9\u51fb\u67e5\u770b\u516c\u544a\u5168\u6587"
                ));
            }
            inv.setItem(serverMenuSlot, item(Material.ARROW, "\u00a7f\u8fd4\u56de\u4e3b\u83dc\u5355", "\u00a77\u8fd4\u56de\u670d\u52a1\u5668\u4e3b\u83dc\u5355"));
            p().openGui(pl, inv, id -> {
                if (id == serverMenuSlot) {
                    openServerMenu(pl);
                    return;
                }
                if (id == 7 && hasNotice) {
                    p().markNoticeRead(pl, tid);
                    openAlert(pl, "\u00a76\u56e2\u961f\u516c\u544a\uff1a\u00a7f"+lineText(t.getNotice()), () -> openMainMenu(pl));
                    return;
                }
                if(isOp) { switch(id){case 0:openTeamDetail(pl,tid);break;case 1:openWarpMenu(pl,tid);break;case 2:openTpaMainMenu(pl,tid);break;case 3:openTeamFundMenu(pl,tid);break;case 4:openMessageBoard(pl,tid);break;case 6:openTeamManageMenu(pl,tid);break;case 8:handleQuitTeam(pl,tid);break;} }
                else { switch(id){case 0:openTeamDetail(pl,tid);break;case 1:openWarpMenu(pl,tid);break;case 2:openTpaMainMenu(pl,tid);break;case 3:openTeamFundMenu(pl,tid);break;case 4:openMessageBoard(pl,tid);break;case 8:handleQuitTeam(pl,tid);break;} }
            });
        } else {
            long cost = p().getConfig2().getCreateTeamCost();
            Inventory inv = Bukkit.createInventory(null, 9, "\u00a7l\u56e2\u961f\u7cfb\u7edf");
            inv.setItem(0, item(Material.ARROW, "\u00a7f\u8fd4\u56de\u4e3b\u83dc\u5355", "\u00a77\u8fd4\u56de\u670d\u52a1\u5668\u4e3b\u83dc\u5355"));
            inv.setItem(2, item(Material.LIME_WOOL, "\u00a7l\u521b\u5efa\u65b0\u56e2\u961f","\u00a77\u82b1\u8d39 "+cost+cur()+" \u521b\u5efa\u5c5e\u4e8e\u4f60\u7684\u56e2\u961f"));
            inv.setItem(4, item(Material.COMPASS, "\u00a7l\u641c\u7d22\u56e2\u961f","\u00a77\u901a\u8fc7\u56e2\u961fID\u641c\u7d22\u5e76\u7533\u8bf7\u52a0\u5165"));
            inv.setItem(6, item(Material.HOPPER, "\u00a7l\u6392\u884c\u699c","\u00a77\u67e5\u770b\u6240\u6709\u56e2\u961f\u7684\u6210\u957f\u503c\u4e0e\u8d44\u91d1\u6392\u540d"));
            p().openGui(pl, inv, id -> { switch(id){case 0:openServerMenu(pl);break;case 2:openCreateTeamCheck(pl);break;case 4:openJoinByIdInput(pl);break;case 6:openTeamRankingMenu(pl);break;} });
        }
    }

    // ==================== TEAM DETAIL ====================
    public static void openTeamDetail(Player pl, String tid) {
        openTeamDetail(pl, tid, 0);
    }

    private static void openTeamDetail(Player pl, String tid, int page) {
        Team t = p().getTeamData().get(tid); if(t==null)return;
        boolean isOp = p().getTeamData().isTeamOperator(pl.getUniqueId(), tid);
        List<Team.MemberEntry> all = new ArrayList<>();
        all.addAll(t.getOperators());
        all.addAll(t.getMembers());
        int totalPages = Math.max(1, (all.size() + PAGE_AREA - 1) / PAGE_AREA);
        page = Math.max(0, Math.min(page, totalPages - 1));
        int start = page * PAGE_AREA;
        int end = Math.min(start + PAGE_AREA, all.size());
        Inventory inv = Bukkit.createInventory(null, PAGED_SIZE, "\u00a7l\u8be6\u60c5");
        for (int i = start; i < end; i++) {
            Team.MemberEntry member = all.get(i);
            boolean memberIsOp = t.getOperators().stream()
                .anyMatch(operator -> operator.getUuid().equals(member.getUuid()));
            inv.setItem(i - start, skull(
                member,
                (memberIsOp ? "\u00a7c" : "\u00a7a") + Util.plainName(member.getName()),
                memberIsOp ? "\u00a77\u7ba1\u7406\u5458" : "\u00a77\u6210\u5458"
            ));
        }
        inv.setItem(
            SLOT_PREV,
            itemWithModelData(
                Material.ARROW,
                page > 0 ? "\u00a7f\u4e0a\u4e00\u9875" : "\u00a78\u4e0a\u4e00\u9875",
                page > 0 ? 2400011 : 2400061
            )
        );
        inv.setItem(
            SLOT_INFO,
            item(
                Material.FILLED_MAP,
                "\u00a7l" + t.getName(),
                "\u00a77ID: " + tid,
                isOp ? "\u00a7c\u7ba1\u7406\u5458" : "\u00a7a\u6210\u5458",
                "\u00a7e\u6210\u957f\u503c: \u00a7f" + t.getActivity(),
                "\u00a7e\u79ef\u91d1: \u00a7f" + t.getFunds() + cur(),
                "\u00a77\u7b2c " + (page + 1) + "/" + totalPages + " \u9875",
                "\u00a77\u5171 " + all.size() + " \u540d\u6210\u5458"
            )
        );
        inv.setItem(
            SLOT_NEXT,
            itemWithModelData(
                Material.ARROW,
                page < totalPages - 1 ? "\u00a7f\u4e0b\u4e00\u9875" : "\u00a78\u4e0b\u4e00\u9875",
                page < totalPages - 1 ? 2400012 : 2400062
            )
        );
        inv.setItem(SLOT_BACK, item(Material.ARROW, "\u00a7l\u8fd4\u56de"));
        int finalPage = page;
        p().openGui(pl, inv, slot -> {
            if (slot == SLOT_PREV && finalPage > 0) {
                openTeamDetail(pl, tid, finalPage - 1);
            } else if (slot == SLOT_NEXT && finalPage < totalPages - 1) {
                openTeamDetail(pl, tid, finalPage + 1);
            } else if (slot == SLOT_BACK) {
                openMainMenu(pl);
            }
        });
    }

    // ==================== WARP (UNIFIED GUI) ====================
    private static final Map<UUID, Boolean> warpDeleteMode = new HashMap<>();

    public static void openWarpMenu(Player pl, String tid) {
        openWarpMenu(pl, tid, 0);
    }

    public static void openWarpMenu(Player pl, String tid, int page) {
        Team t = p().getTeamData().get(tid);
        boolean isOp = p().getTeamData().isTeamOperator(pl.getUniqueId(), tid);
        boolean deleting = warpDeleteMode.getOrDefault(pl.getUniqueId(), false);
        Map<String, Team.WarpPoint> warps = t.getWarpPoints();
        List<String> names = new ArrayList<>(warps.keySet());
        int totalPages = Math.max(1, (int) Math.ceil((double) Math.max(1, names.size()) / PAGE_AREA));
        if (page >= totalPages) page = totalPages - 1;
        if (page < 0) page = 0;
        String title = deleting ? "\u00a7c\u5220\u9664\u6a21\u5f0f" : "\u00a7l\u4f20\u9001\u951a\u70b9";
        Inventory inv = Bukkit.createInventory(null, PAGED_SIZE, title);
        Material bgMat = deleting ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE;
        ItemStack bg = filler(bgMat);
        for (int i = 0; i < PAGED_SIZE; i++) inv.setItem(i, bg);
        int start = page * PAGE_AREA;
        int idx = 0;
        for (int i = start; i < Math.min(start + PAGE_AREA, names.size()); i++) {
            String wn = names.get(i);
            Team.WarpPoint wp = warps.get(wn);
            Material icon = warpIconMaterial(wp);
            boolean canDelete = isOp || pl.getUniqueId().toString().equals(wp.getCreatorUuid());
            if (deleting) {
                if (canDelete) {
                    inv.setItem(idx++, item(Material.TNT, "\u00a7c\u00a7l" + wn,
                        "\u00a77\u4e16\u754c: " + wp.getWorldDisplay(),
                        "\u00a77\u521b\u5efa\u8005: " + Util.plainName(wp.getCreatorName()),
                        "\u00a7c\u70b9\u51fb\u540e\u7acb\u5373\u5220\u9664"));
                } else {
                    inv.setItem(idx++, item(Material.BARRIER, "\u00a77" + wn,
                        "\u00a77\u4e16\u754c: " + wp.getWorldDisplay(),
                        "\u00a77\u521b\u5efa\u8005: " + Util.plainName(wp.getCreatorName()),
                        "\u00a7c\u65e0\u6cd5\u5220\u9664\uff1a\u56e2\u961f\u6210\u5458\u53ea\u80fd\u5220\u9664\u81ea\u5df1\u521b\u5efa\u7684\u951a\u70b9"));
                }
            } else {
                inv.setItem(idx++, item(icon, "\u00a7e\u00a7l" + wn,
                    "\u00a77\u4e16\u754c: " + wp.getWorldDisplay(),
                    "\u00a77\u521b\u5efa\u8005: " + Util.plainName(wp.getCreatorName()),
                    "\u00a7a\u70b9\u51fb\u4f20\u9001"));
            }
        }
        if (names.isEmpty() && !deleting) {
            inv.setItem(0, item(Material.PAPER, "\u00a77\u6682\u65e0\u4f20\u9001\u951a\u70b9",
                "\u00a77\u70b9\u51fb\u4e0b\u65b9\u201c\u6dfb\u52a0\u951a\u70b9\u201d\u5728\u5f53\u524d\u4f4d\u7f6e\u521b\u5efa\u4e00\u4e2a"));
        }
        inv.setItem(
            SLOT_PREV,
            itemWithModelData(
                Material.ARROW,
                page > 0 ? "\u00a7f\u4e0a\u4e00\u9875" : "\u00a78\u4e0a\u4e00\u9875",
                page > 0 ? 2400011 : 2400061
            )
        );
        String infoLine1 = deleting ? "\u00a77\u70b9\u51fb TNT \u5373\u53ef\u5220\u9664" : "\u00a77\u7b2c " + (page+1) + "/" + totalPages + " \u9875";
        String infoLine2 = "\u00a77\u5171 " + names.size() + " \u4e2a\u4f20\u9001\u70b9";
        String infoLine3 = deleting && isOp ? "\u00a7a\u4f60\u662f\u7ba1\u7406\u5458\uff0c\u53ef\u5220\u9664\u6240\u6709\u951a\u70b9" : (deleting ? "\u00a77\u4f60\u53ea\u80fd\u5220\u9664\u81ea\u5df1\u521b\u5efa\u7684\u951a\u70b9" : "");
        Material infoMat = deleting ? Material.BARRIER : Material.PAPER;
        String infoTitle = deleting ? "\u00a7c\u5220\u9664\u6a21\u5f0f" : "\u00a7e\u9875\u7801\u4fe1\u606f";
        inv.setItem(SLOT_INFO, item(infoMat, infoTitle, infoLine1, infoLine2, infoLine3));
        inv.setItem(
            SLOT_NEXT,
            itemWithModelData(
                Material.ARROW,
                page < totalPages - 1 ? "\u00a7f\u4e0b\u4e00\u9875" : "\u00a78\u4e0b\u4e00\u9875",
                page < totalPages - 1 ? 2400012 : 2400062
            )
        );
        inv.setItem(47, item(Material.LIME_WOOL, "\u00a72\u00a7l\u6dfb\u52a0\u951a\u70b9",
            "\u00a77\u5728\u5f53\u524d\u7ad9\u7acb\u4f4d\u7f6e\u8bb0\u5f55\u65b0\u7684\u56e2\u961f\u5171\u4eab\u4f20\u9001\u70b9",
            "\u00a77\u53ef\u4ee5\u4e3a\u6bcf\u4e2a\u951a\u70b9\u9009\u62e9\u56fe\u6807"));
        if (deleting) {
            inv.setItem(48, item(Material.LIME_WOOL, "\u00a7a\u00a7l\u5b8c\u6210\u5220\u9664",
                "\u00a77\u70b9\u51fb\u8fd4\u56de\u4f20\u9001\u6a21\u5f0f"));
        } else {
            inv.setItem(48, item(Material.RED_WOOL, "\u00a7c\u00a7l\u5220\u9664\u951a\u70b9",
                "\u00a77\u5207\u6362\u5230\u5220\u9664\u6a21\u5f0f",
                "\u00a77\u7ba1\u7406\u5458\u53ef\u5220\u9664\u4efb\u610f\u951a\u70b9\uff0c\u6210\u5458\u4ec5\u53ef\u5220\u81ea\u5df1\u521b\u5efa\u7684"));
        }
        inv.setItem(SLOT_BACK, item(Material.ARROW, "\u00a7f\u8fd4\u56de", "\u00a77\u8fd4\u56de\u56e2\u961f\u4e3b\u83dc\u5355"));
        int finalPage = page;
        boolean finalDeleting = deleting;
        p().openGui(pl, inv, id -> {
            if (id == SLOT_BACK) { warpDeleteMode.remove(pl.getUniqueId()); openMainMenu(pl); return; }
            if (id == SLOT_PREV && finalPage > 0) { openWarpMenu(pl, tid, finalPage - 1); return; }
            if (id == SLOT_NEXT && finalPage < totalPages - 1) { openWarpMenu(pl, tid, finalPage + 1); return; }
            if (id == 47) {
                if (GuiRouter.isBedrock(pl)) { pl.closeInventory(); BedrockForms.openAddWarpMenu(pl, tid); }
                else openAddWarpMenu(pl, tid);
                return;
            }
            if (id == 48) { warpDeleteMode.put(pl.getUniqueId(), !finalDeleting); openWarpMenu(pl, tid, finalPage); return; }
            if (finalDeleting) {
                int delIdx = start + id;
                if (id >= 0 && id < PAGE_AREA && delIdx < names.size()) {
                    String wn = names.get(delIdx);
                    boolean can = isOp || pl.getUniqueId().toString().equals(warps.get(wn).getCreatorUuid());
                    if (can) { warps.remove(wn); p().getTeamData().save(); pl.sendMessage("\u00a7a\u4f20\u9001\u70b9\u5df2\u5220\u9664\uff01"); }
                    openWarpMenu(pl, tid, finalPage);
                }
                return;
            }
            int warpIdx = start + id;
            if (id >= 0 && id < PAGE_AREA && warpIdx < names.size()) {
                warpDeleteMode.remove(pl.getUniqueId());
                teleportToWarp(pl, tid, names.get(warpIdx), warps.get(names.get(warpIdx)));
            }
        });
    }

    public static void openAddWarpMenu(Player pl, String tid) {
        requestIC(pl,"\u6dfb\u52a0\u4f20\u9001\u70b9","\u8f93\u5165\u540d\u79f0(1-10\u5b57):", name->{
            if(name.length()<1||name.length()>10){openAlert(pl,"\u00a7c\u540d\u79f0\u5fc5\u987b1-10\u5b57\u7b26\uff01",()->openAddWarpMenu(pl,tid));return;}
            Team t=p().getTeamData().get(tid); if(t.getWarpPoints().containsKey(name)){openAlert(pl,"\u00a7c\u540d\u79f0\u5df2\u5b58\u5728\uff01",()->openAddWarpMenu(pl,tid));return;}
            Location loc=pl.getLocation();
            openWarpIconMenu(pl, icon -> {
                if (icon == null) { openWarpMenu(pl, tid, 0); return; }
                Team.WarpPoint wp=new Team.WarpPoint(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(),Util.dimId(loc.getWorld()),pl.getUniqueId().toString(),pl.getName(),icon.name(),loc.getWorld().getName());
                t.getWarpPoints().put(name,wp); p().getTeamData().save(); pl.sendMessage("\u00a7a\u4f20\u9001\u70b9\u521b\u5efa\u6210\u529f\uff01"); openWarpMenu(pl, tid, 0);
            });
        });
    }

    private static void openWarpIconMenu(Player pl, Consumer<Material> callback) {
        openWarpIconMenu(pl, callback, 0);
    }

    private static void openWarpIconMenu(Player pl, Consumer<Material> callback, int page) {
        pendingIconCallbacks.put(pl.getUniqueId(), callback);
        Inventory inv = Bukkit.createInventory(null, PAGED_SIZE, "\u00a7l\u9009\u62e9\u951a\u70b9\u56fe\u6807");
        ItemStack g = filler(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < PAGED_SIZE; i++) inv.setItem(i, g);
        List<WarpIcon> icons = WarpIconCatalog.all();
        int totalPages = Math.max(1, (icons.size() + PAGE_AREA - 1) / PAGE_AREA);
        int safePage = Math.max(0, Math.min(page, totalPages - 1));
        int start = safePage * PAGE_AREA;
        for (int i = 0; i < PAGE_AREA && start + i < icons.size(); i++) {
            WarpIcon wi = icons.get(start + i);
            inv.setItem(i, item(wi.material(), "\u00a7e\u00a7l" + wi.label(), "\u00a77\u70b9\u51fb\u9009\u62e9\u6b64\u56fe\u6807"));
        }
        inv.setItem(SLOT_BACK, item(Material.BARRIER, "\u00a7c\u00a7l\u53d6\u6d88\u6dfb\u52a0", "\u00a77\u8fd4\u56de\u4f20\u9001\u951a\u70b9\u4e3b\u83dc\u5355"));
        inv.setItem(SLOT_INFO, item(Material.BOOK, "\u00a7e\u56fe\u6807\u9009\u62e9", "\u00a77\u4e3a\u4f60\u7684\u4f20\u9001\u951a\u70b9\u9009\u62e9\u4e00\u4e2a\u56fe\u6807", "\u00a77\u5171 " + icons.size() + " \u79cd\u56fe\u6807\u53ef\u9009", "\u00a77\u5f53\u524d\u9875: " + (safePage + 1) + "/" + totalPages));
        if (safePage > 0) {
            inv.setItem(SLOT_PREV, item(Material.ARROW, "\u00a7f\u4e0a\u4e00\u9875", "\u00a77\u67e5\u770b\u4e0a\u4e00\u9875\u56fe\u6807"));
        }
        if (safePage + 1 < totalPages) {
            inv.setItem(SLOT_NEXT, item(Material.ARROW, "\u00a7f\u4e0b\u4e00\u9875", "\u00a77\u67e5\u770b\u4e0b\u4e00\u9875\u56fe\u6807"));
        }
        p().openGui(pl, inv, id -> {
            if (id == SLOT_BACK) {
                Consumer<Material> cb = pendingIconCallbacks.remove(pl.getUniqueId());
                if (cb != null) cb.accept(null);
                return;
            }
            if (id == SLOT_PREV && safePage > 0) {
                openWarpIconMenu(pl, pendingIconCallbacks.get(pl.getUniqueId()), safePage - 1);
                return;
            }
            if (id == SLOT_NEXT && safePage + 1 < totalPages) {
                openWarpIconMenu(pl, pendingIconCallbacks.get(pl.getUniqueId()), safePage + 1);
                return;
            }
            if (id >= 0 && id < PAGE_AREA) {
                int iconIndex = start + id;
                if (iconIndex < icons.size()) {
                    Consumer<Material> cb = pendingIconCallbacks.remove(pl.getUniqueId());
                    if (cb != null) cb.accept(icons.get(iconIndex).material());
                }
            }
        });
    }

    // ==================== TPA ====================
    public static void openTpaMainMenu(Player pl, String tid) {
        openTpaMainMenu(pl, tid, 0);
    }

    private static void openTpaMainMenu(Player pl, String tid, int page) {
        List<Player> mates = new ArrayList<>();
        for (Player op : Bukkit.getOnlinePlayers()) {
            if (!op.getUniqueId().equals(pl.getUniqueId())
                && tid.equals(p().getTeamData().getPlayerTeamId(op.getUniqueId()))) {
                mates.add(op);
            }
        }
        if (mates.isEmpty()) {
            openAlert(pl, "\u00a7c\u6ca1\u6709\u5728\u7ebf\u961f\u53cb\uff01", () -> openMainMenu(pl));
            return;
        }

        int totalPages = Math.max(1, (mates.size() + PAGE_AREA - 1) / PAGE_AREA);
        page = Math.max(0, Math.min(page, totalPages - 1));
        int start = page * PAGE_AREA;
        int end = Math.min(start + PAGE_AREA, mates.size());
        Inventory inv = Bukkit.createInventory(null, PAGED_SIZE, "\u00a7l\u4e92\u4f20");
        for (int i = start; i < end; i++) {
            Player mate = mates.get(i);
            String detail;
            if (mate.getWorld().equals(pl.getWorld())) {
                double distance = pl.getLocation().distance(mate.getLocation());
                detail = "\u8ddd\u79bb:" + String.format("%.1f", distance) + "\u7c73 | "
                    + mate.getLocation().getBlockX();
            } else {
                detail = "\u00a77" + mate.getWorld().getName();
            }
            inv.setItem(
                i - start,
                skull(
                    mate.getUniqueId(),
                    "\u00a7l" + mate.getName(),
                    detail,
                    "\u00a77\u70b9\u51fb\u514d\u540c\u610f\u76f4\u63a5\u4f20\u9001"
                )
            );
        }
        inv.setItem(
            SLOT_PREV,
            itemWithModelData(
                Material.ARROW,
                page > 0 ? "\u00a7f\u4e0a\u4e00\u9875" : "\u00a78\u4e0a\u4e00\u9875",
                page > 0 ? 2400011 : 2400061
            )
        );
        inv.setItem(
            SLOT_INFO,
            item(
                Material.PAPER,
                "\u00a7e\u9875\u7801\u4fe1\u606f",
                "\u00a77\u7b2c " + (page + 1) + "/" + totalPages + " \u9875",
                "\u00a77\u5171 " + mates.size() + " \u540d\u5728\u7ebf\u961f\u53cb"
            )
        );
        inv.setItem(
            SLOT_NEXT,
            itemWithModelData(
                Material.ARROW,
                page < totalPages - 1 ? "\u00a7f\u4e0b\u4e00\u9875" : "\u00a78\u4e0b\u4e00\u9875",
                page < totalPages - 1 ? 2400012 : 2400062
            )
        );
        inv.setItem(
            SLOT_BACK,
            itemWithModelData(
                Material.RED_WOOL,
                "\u00a7l\u53d6\u6d88",
                2400015,
                "\u00a77\u8fd4\u56de\u56e2\u961f\u4e3b\u83dc\u5355"
            )
        );
        int finalPage = page;
        p().openGui(pl, inv, id -> {
            if (id == SLOT_PREV && finalPage > 0) {
                openTpaMainMenu(pl, tid, finalPage - 1);
            } else if (id == SLOT_NEXT && finalPage < totalPages - 1) {
                openTpaMainMenu(pl, tid, finalPage + 1);
            } else if (id == SLOT_BACK) {
                openMainMenu(pl);
            } else if (id >= 0 && id < end - start) {
                Player target = Bukkit.getPlayer(mates.get(start + id).getUniqueId());
                if (target == null || !tid.equals(p().getTeamData().getPlayerTeamId(target.getUniqueId()))) {
                    openAlert(
                        pl,
                        "\u00a7c\u961f\u53cb\u5df2\u79bb\u7ebf\u6216\u79bb\u5f00\u56e2\u961f",
                        () -> openTpaMainMenu(pl, tid, finalPage)
                    );
                    return;
                }
                pl.teleport(target.getLocation());
                pl.sendMessage("\u00a7a\u5df2\u4f20\u9001\u81f3 " + target.getName());
                target.sendMessage("\u00a7e" + pl.getName() + "\u4f20\u9001\u5230\u4e86\u4f60\u8eab\u8fb9");
                openMainMenu(pl);
            }
        });
    }

    // ==================== FUND ====================
    public static void openTeamFundMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l\u79ef\u91d1");
        inv.setItem(0,itemWithModelData(Material.GOLD_NUGGET,"\u00a72\u00a7l\u5b58\u5165",2400025,"\u00a77\u5411\u56e2\u961f\u79ef\u91d1\u4e2d\u5b58\u5165\u661f\u5149\u70b9"));
        inv.setItem(1,itemWithModelData(Material.GOLD_NUGGET,"\u00a7c\u00a7l\u53d6\u51fa",2400025,"\u00a77\u4ece\u56e2\u961f\u79ef\u91d1\u4e2d\u53d6\u51fa\u661f\u5149\u70b9\uff0c\u9700\u8981\u7ba1\u7406\u5458\u6743\u9650"));
        inv.setItem(2,item(Material.ARROW,"\u00a7l\u8fd4\u56de","\u00a77\u8fd4\u56de\u56e2\u961f\u4e3b\u83dc\u5355"));
        p().openGui(pl, inv, id->{switch(id){case 0:openDepositMenu(pl,tid);break;case 1:openWithdrawMenu(pl,tid);break;case 2:openMainMenu(pl);break;}});
    }

    public static void openDepositMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); double bal=p().getEconomy().getBalance(pl.getUniqueId());
        pl.sendMessage("\u00a7e\u4f59\u989d:"+String.format("%.0f",bal)+cur()+" | \u56e2\u961f:"+t.getFunds()+cur());
        requestIC(pl,"\u5b58\u5165","\u8f93\u5165\u91d1\u989d:",in->{
            String err=Util.validateAmount(in); if(err!=null){openAlert(pl,"\u00a7c"+err,()->openDepositMenu(pl,tid));return;}
            long amt=Util.parseAmount(in); if(!p().getEconomy().withdraw(pl.getUniqueId(),amt)){openAlert(pl,"\u00a7c\u4f59\u989d\u4e0d\u8db3",()->openDepositMenu(pl,tid));return;}
            long bb=t.getFunds();t.setFunds(bb+amt);p().getTeamData().save();p().getFundLog().addLog(tid,amt,"\u5b58\u5165 "+pl.getName(),bb,t.getFunds());p().getFundLog().save();p().notifyFundChange(tid,bb,amt,t.getFunds(),"\u5b58\u5165 "+pl.getName(),pl.getName());pl.sendMessage("\u00a7a\u6210\u529f\u5b58\u5165 "+amt+cur());openTeamFundMenu(pl,tid);
        });
    }

    public static void openWithdrawMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); pl.sendMessage("\u00a7e\u56e2\u961f\u8d44\u91d1:"+t.getFunds()+cur());
        requestIC(pl,"\u53d6\u51fa","\u8f93\u5165\u91d1\u989d:",in->{
            String err=Util.validateAmount(in);if(err!=null){openAlert(pl,"\u00a7c"+err,()->openWithdrawMenu(pl,tid));return;}
            long amt=Util.parseAmount(in);if(t.getFunds()<amt){openAlert(pl,"\u00a7c\u4e0d\u8db3",()->openWithdrawMenu(pl,tid));return;}
            long bb=t.getFunds();t.setFunds(bb-amt);if(!p().getEconomy().deposit(pl.getUniqueId(),amt)){t.setFunds(bb);openAlert(pl,"\u00a7c\u5931\u8d25",()->openWithdrawMenu(pl,tid));return;}
            p().getTeamData().save();p().getFundLog().addLog(tid,-amt,"\u53d6\u51fa "+pl.getName(),bb,t.getFunds());p().getFundLog().save();p().notifyFundChange(tid,bb,-amt,t.getFunds(),"\u53d6\u51fa "+pl.getName(),pl.getName());pl.sendMessage("\u00a7a\u6210\u529f\u53d6\u51fa "+amt+cur());openTeamFundMenu(pl,tid);
        });
    }

    public static void openFundLogMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); List<FundLogEntry> logs=p().getFundLog().getLogs(tid);
        int limit=p().getConfig2().getFundLogDisplayLimit(); List<FundLogEntry> display=logs.size()>limit?logs.subList(0,limit):logs;
        int slots=Math.min(45,display.size());int rows=(slots/9)+1;int size=Math.min(54,(rows+1)*9);if(size<9)size=9;
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7l\u6d41\u6c34"); SimpleDateFormat sdf=new SimpleDateFormat("MM/dd HH:mm"); int idx=0;
        for(FundLogEntry e:display){if(idx>=45)break;String ts=sdf.format(new Date(e.getTimestamp()));String ch=e.getChange()>=0?"\u00a7a+"+e.getChange():"\u00a7c"+e.getChange();inv.setItem(idx++,item(Material.PAPER,ts+" ("+ch+"\u00a7r)",e.getBalanceBefore()+"\u2192"+e.getBalanceAfter(),e.getReason()));}
        int bs=size-9;inv.setItem(bs,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));p().openGui(pl, inv, id->{openTeamManageMenu(pl,tid);});
    }

    // ==================== MESSAGE BOARD ====================
    public static void openMessageBoard(Player pl, String tid) {
        p().getMessageData().setLastViewTime(pl.getUniqueId(),tid);
        p().getMessageData().save();
        List<MessageEntry> msgs=p().getMessageData().getMessages(tid);
        int show=Math.min(10,msgs.size());
        int size; int addS;
        if (show == 0) {
            size = 9; addS = 6;
        } else {
            int rows = (int)Math.ceil((double)show / 9.0) + 1;
            size = Math.min(54, rows * 9);
            addS = (rows - 1) * 9;
        }
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7l\u7559\u8a00\u677f"); int idx=0;
        for(int i=0;i<show;i++){MessageEntry m=msgs.get(i);inv.setItem(idx++,item(Material.PAPER,"\u00a7e"+m.getSenderName()+" \u00a77("+Util.timeAgo(m.getTime())+")","\u00a7f"+lineText(m.getContent())));}
        if(show==0)inv.setItem(4,item(Material.PAPER,"\u00a77\u6682\u65e0\u7559\u8a00","\u00a77\u8fd8\u6ca1\u6709\u4eba\u53d1\u5e03\u7559\u8a00\uff0c\u6765\u505a\u7b2c\u4e00\u4e2a\u5427"));
        inv.setItem(addS,item(Material.WRITABLE_BOOK,"\u00a72\u00a7l\u6dfb\u52a0\u7559\u8a00","\u00a77\u53d1\u5e03\u65b0\u7684\u7559\u8a00\u5230\u7559\u8a00\u677f","\u00a7710\u5206\u949f\u51b7\u5374\uff0c\u6700\u591a100\u5b57")); inv.setItem(addS+1,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        p().openGui(pl, inv, id->{if(id==addS)openAddMessageMenu(pl,tid);else if(id==addS+1)openMainMenu(pl);});
    }

    public static void openAddMessageMenu(Player pl, String tid) {
        long cd=p().getMessageCooldowns().getOrDefault(tid+"_"+pl.getUniqueId(),0L); int cdSec=p().getConfig2().getMessageCooldownSeconds();
        if(System.currentTimeMillis()-cd<cdSec*1000L){long r=(cdSec*1000L-(System.currentTimeMillis()-cd))/1000;openAlert(pl,"\u00a7c\u51b7\u5374\u4e2d "+r/60+"\u5206"+r%60+"\u79d2",()->openMessageBoard(pl,tid));return;}
        requestIC(pl,"\u6dfb\u52a0\u7559\u8a00","\u8f93\u5165\u5185\u5bb9(\u6700100\u5b57):",c->{
            if(c.isEmpty()){openAlert(pl,"\u00a7c\u4e0d\u80fd\u4e3a\u7a7a",()->openAddMessageMenu(pl,tid));return;}
            if(c.length()>100){openAlert(pl,"\u00a7c\u8fc7\u957f",()->openAddMessageMenu(pl,tid));return;}
            MessageEntry me=new MessageEntry(pl.getUniqueId().toString(),pl.getName(),c);p().getMessageData().addMessage(tid,me);p().getMessageData().save();p().getMessageCooldowns().put(tid+"_"+pl.getUniqueId(),System.currentTimeMillis());p().notifyTeamMessage(tid,me);pl.sendMessage("\u00a7a\u53d1\u5e03\u6210\u529f");openMessageBoard(pl,tid);
        });
    }

    // ==================== TEAM MANAGE ====================
    public static void openTeamManageMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); int apps=t.getMembersapplications().size();
        Inventory inv=Bukkit.createInventory(null,27,"\u00a7l\u7ba1\u7406");
        inv.setItem(0,item(Material.PLAYER_HEAD,"\u00a7l\u7ba1\u7406\u6210\u5458","\u00a77\u7ba1\u7406\u56e2\u961f\u7684\u7ba1\u7406\u5458\u548c\u666e\u901a\u6210\u5458"));
        inv.setItem(1,item(Material.PAPER,apps>0?"\u00a7l\u7533\u8bf7 \u00a7c"+apps:"\u00a7l\u7533\u8bf7 \u00a77\u6682\u65e0","\u00a77\u67e5\u770b\u5e76\u5904\u7406\u52a0\u5165\u7533\u8bf7"));
        inv.setItem(2,item(Material.BOOK,"\u00a7l\u516c\u544a","\u00a77\u7f16\u8f91\u56e2\u961f\u516c\u544a\uff0c\u516c\u544a\u5c06\u663e\u793a\u5728\u6240\u6709\u6210\u5458\u7684\u4e3b\u83dc\u5355\u4e2d"));
        inv.setItem(3,item(Material.GOLD_NUGGET,"\u00a7l\u6d41\u6c34","\u00a77\u67e5\u770b\u56e2\u961f\u79ef\u91d1\u7684\u6bcf\u7b14\u6536\u652f\u660e\u7ec6"));
        inv.setItem(4,item(Material.NAME_TAG,"\u00a7l\u4fee\u6539\u540d\u79f0","\u00a7r\u5f53\u524d: "+t.getName(),"\u00a77\u4fee\u6539\u56e2\u961f\u540d\u79f0\uff0c2-10\u5b57\u7b26"));
        inv.setItem(5,item(t.isPublic()?Material.LIME_WOOL:Material.RED_WOOL,"\u00a7l\u72b6\u6001","\u00a7r"+(t.isPublic()?"\u00a72\u516c\u5f00":"\u00a7c\u79c1\u5bc6"),"\u00a77\u5207\u6362\u56e2\u961f\u62db\u52df\u72b6\u6001\uff08\u516c\u5f00/\u79c1\u5bc6\uff09"));
        inv.setItem(8,item(Material.TNT,"\u00a7c\u00a7l\u89e3\u6563","\u00a77\u7acb\u5373\u89e3\u6563\u56e2\u961f\uff0c\u6b64\u64cd\u4f5c\u4e0d\u53ef\u64a4\u9500\uff01","\u00a77\u9700\u8981\u8f93\u5165\u56e2\u961f\u540d\u79f0\u786e\u8ba4")); inv.setItem(26,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        p().openGui(pl, inv, id->{
            switch(id){case 0:openManageMembers(pl,tid);break;case 1:openManageApplications(pl,tid);break;case 2:openNoticeEditMenu(pl,tid);break;case 3:openFundLogMenu(pl,tid);break;case 4:openRenameTeamMenu(pl,tid);break;
            case 5:t.setPublic(!t.isPublic());p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u66f4\u6539");openTeamManageMenu(pl,tid);break;case 8:openDisbandConfirmMenu(pl,tid);break;case 26:openMainMenu(pl);break;}
        });
    }

    // ==================== MANAGE MEMBERS ====================
    public static void openManageMembers(Player pl, String tid) {
        openManageMembers(pl, tid, 0);
    }

    private static void openManageMembers(Player pl, String tid, int page) {
        Team t = p().getTeamData().get(tid);
        List<Team.MemberEntry> all = new ArrayList<>();
        all.addAll(t.getOperators());
        all.addAll(t.getMembers());
        if (all.isEmpty()) {
            openAlert(pl, "\u00a7c\u5217\u8868\u4e3a\u7a7a", () -> openTeamManageMenu(pl, tid));
            return;
        }
        int totalPages = Math.max(1, (all.size() + PAGE_AREA - 1) / PAGE_AREA);
        page = Math.max(0, Math.min(page, totalPages - 1));
        int start = page * PAGE_AREA;
        int end = Math.min(start + PAGE_AREA, all.size());
        Inventory inv = Bukkit.createInventory(null, PAGED_SIZE, "\u00a7l\u6210\u5458");
        for (int i = start; i < end; i++) {
            Team.MemberEntry member = all.get(i);
            boolean isOp = t.getOperators().stream()
                .anyMatch(operator -> operator.getUuid().equals(member.getUuid()));
            inv.setItem(
                i - start,
                skull(
                    member,
                    (isOp ? "\u00a7c" : "\u00a7a") + Util.plainName(member.getName()),
                    isOp ? "\u00a77\u7ba1\u7406\u5458\uff0c\u70b9\u51fb\u8fdb\u884c\u64cd\u4f5c"
                        : "\u00a77\u6210\u5458\uff0c\u70b9\u51fb\u8fdb\u884c\u64cd\u4f5c"
                )
            );
        }
        inv.setItem(
            SLOT_PREV,
            itemWithModelData(
                Material.ARROW,
                page > 0 ? "\u00a7f\u4e0a\u4e00\u9875" : "\u00a78\u4e0a\u4e00\u9875",
                page > 0 ? 2400011 : 2400061
            )
        );
        inv.setItem(
            SLOT_INFO,
            item(
                Material.PAPER,
                "\u00a7e\u9875\u7801\u4fe1\u606f",
                "\u00a77\u7b2c " + (page + 1) + "/" + totalPages + " \u9875",
                "\u00a77\u5171 " + all.size() + " \u540d\u6210\u5458"
            )
        );
        inv.setItem(
            SLOT_NEXT,
            itemWithModelData(
                Material.ARROW,
                page < totalPages - 1 ? "\u00a7f\u4e0b\u4e00\u9875" : "\u00a78\u4e0b\u4e00\u9875",
                page < totalPages - 1 ? 2400012 : 2400062
            )
        );
        inv.setItem(SLOT_BACK, item(Material.ARROW, "\u00a7l\u8fd4\u56de"));
        int finalPage = page;
        p().openGui(pl, inv, id -> {
            if (id == SLOT_PREV && finalPage > 0) {
                openManageMembers(pl, tid, finalPage - 1);
            } else if (id == SLOT_NEXT && finalPage < totalPages - 1) {
                openManageMembers(pl, tid, finalPage + 1);
            } else if (id == SLOT_BACK) {
                openTeamManageMenu(pl, tid);
            } else if (id >= 0 && id < end - start) {
                openMemberAction(pl, tid, all.get(start + id), finalPage);
            }
        });
    }

    private static void openMemberAction(Player pl, String tid, Team.MemberEntry member, int page) {
        Team t=p().getTeamData().get(tid); boolean isOp=t.getOperators().stream().anyMatch(m->m.getUuid().equals(member.getUuid()));
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l"+Util.plainName(member.getName()));
        inv.setItem(1,item(Material.GREEN_WOOL,"\u00a7a\u00a7l\u8bbe\u4e3a\u7ba1\u7406\u5458","\u00a77\u5c06\u8be5\u6210\u5458\u63d0\u5347\u4e3a\u56e2\u961f\u7ba1\u7406\u5458"));
        inv.setItem(3,item(Material.YELLOW_WOOL,"\u00a7e\u00a7l\u8bbe\u4e3a\u6210\u5458","\u00a77\u5c06\u8be5\u7ba1\u7406\u5458\u964d\u7ea7\u4e3a\u666e\u901a\u6210\u5458"));
        inv.setItem(5,item(Material.RED_WOOL,"\u00a7c\u00a7l\u79fb\u51fa","\u00a77\u5c06\u8be5\u6210\u5458\u79fb\u51fa\u56e2\u961f","\u00a77\u79fb\u51fa\u540e\u65e0\u6cd5\u64a4\u9500"));
        inv.setItem(8,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        p().openGui(pl, inv, id->{
            if(id==8){openManageMembers(pl,tid,page);return;}
            if(id==1&&isOp){openAlert(pl,"\u00a7c\u5df2\u662f\u7ba1\u7406\u5458",()->openManageMembers(pl,tid,page));return;}
            if(id==3&&!isOp){openAlert(pl,"\u00a7c\u5df2\u662f\u6210\u5458",()->openManageMembers(pl,tid,page));return;}
            if(isOp&&id==5){openAlert(pl,"\u00a7c\u7ba1\u7406\u5458\u4e0d\u80fd\u76f4\u63a5\u79fb\u51fa\u56e2\u961f\uff0c\u8bf7\u5148\u5c06\u8be5\u7ba1\u7406\u5458\u964d\u7ea7\u4e3a\u666e\u901a\u6210\u5458\uff0c\u518d\u6267\u884c\u79fb\u51fa\u64cd\u4f5c\u3002",()->openManageMembers(pl,tid,page));return;}
            if(member.getUuid().equals(pl.getUniqueId().toString())&&id==5){openAlert(pl,"\u00a7c\u8bf7\u5148\u5c06\u81ea\u5df1\u964d\u7ea7\u4e3a\u666e\u901a\u6210\u5458\uff0c\u518d\u9000\u51fa\u56e2\u961f\u3002",()->openManageMembers(pl,tid,page));return;}
            if(isOp&&id==3&&t.getOperators().size()<=1){openAlert(pl,"\u00a7c\u81f3\u5c11\u4fdd\u7559\u4e00\u4e2a\u7ba1\u7406\u5458",()->openManageMembers(pl,tid,page));return;}
            boolean self=member.getUuid().equals(pl.getUniqueId().toString())&&id==3;
            if(id==1){
                t.getMembers().removeIf(m->m.getUuid().equals(member.getUuid()));
                Team.MemberEntry ne = new Team.MemberEntry(member.getUuid(), member.getName());
                t.getOperators().add(ne);
                pl.sendMessage("\u00a7a\u5df2\u8bbe\u4e3a\u7ba1\u7406\u5458");}
            else if(id==3){
                t.getOperators().removeIf(m->m.getUuid().equals(member.getUuid()));
                Team.MemberEntry ne = new Team.MemberEntry(member.getUuid(), member.getName());
                t.getMembers().add(ne);
                pl.sendMessage("\u00a7a\u5df2\u8bbe\u4e3a\u6210\u5458");}
            else if(id==5){if(isOp)t.getOperators().removeIf(m->m.getUuid().equals(member.getUuid()));else t.getMembers().removeIf(m->m.getUuid().equals(member.getUuid()));Player tp=Bukkit.getPlayer(member.getUniqueId());if(tp!=null)tp.sendMessage("\u00a7c\u4f60\u5df2\u88ab\u79fb\u51fa "+t.getName());pl.sendMessage("\u00a7a\u5df2\u79fb\u51fa");}
            p().getTeamData().save();if(self)openTeamDetail(pl,tid);else openManageMembers(pl,tid,page);
        });
    }

    // ==================== MANAGE APPLICATIONS ====================
    public static void openManageApplications(Player pl, String tid) {
        openManageApplications(pl, tid, 0);
    }

    private static void openManageApplications(Player pl, String tid, int page) {
        Team t = p().getTeamData().get(tid);
        List<Team.MemberApplication> apps = t.getMembersapplications();
        if (apps.isEmpty()) {
            openAlert(pl, "\u00a77\u6682\u65e0\u7533\u8bf7", () -> openTeamManageMenu(pl, tid));
            return;
        }
        int totalPages = Math.max(1, (apps.size() + PAGE_AREA - 1) / PAGE_AREA);
        page = Math.max(0, Math.min(page, totalPages - 1));
        int start = page * PAGE_AREA;
        int end = Math.min(start + PAGE_AREA, apps.size());
        Inventory inv = Bukkit.createInventory(null, PAGED_SIZE, "\u00a7l\u7533\u8bf7");
        for (int i = start; i < end; i++) {
            Team.MemberApplication application = apps.get(i);
            inv.setItem(
                i - start,
                skull(
                    application.getUniqueId(),
                    "\u00a7e" + Util.plainName(application.getName()),
                    "\u00a77" + application.getAppliedAt(),
                    "\u00a77\u70b9\u51fb\u5904\u7406\u7533\u8bf7"
                )
            );
        }
        inv.setItem(
            SLOT_PREV,
            itemWithModelData(
                Material.ARROW,
                page > 0 ? "\u00a7f\u4e0a\u4e00\u9875" : "\u00a78\u4e0a\u4e00\u9875",
                page > 0 ? 2400011 : 2400061
            )
        );
        inv.setItem(
            SLOT_INFO,
            item(
                Material.PAPER,
                "\u00a7e\u9875\u7801\u4fe1\u606f",
                "\u00a77\u7b2c " + (page + 1) + "/" + totalPages + " \u9875",
                "\u00a77\u5171 " + apps.size() + " \u6761\u7533\u8bf7"
            )
        );
        inv.setItem(
            SLOT_NEXT,
            itemWithModelData(
                Material.ARROW,
                page < totalPages - 1 ? "\u00a7f\u4e0b\u4e00\u9875" : "\u00a78\u4e0b\u4e00\u9875",
                page < totalPages - 1 ? 2400012 : 2400062
            )
        );
        inv.setItem(SLOT_BACK, item(Material.ARROW, "\u00a7l\u8fd4\u56de"));
        int finalPage = page;
        p().openGui(pl, inv, id -> {
            if (id == SLOT_PREV && finalPage > 0) {
                openManageApplications(pl, tid, finalPage - 1);
            } else if (id == SLOT_NEXT && finalPage < totalPages - 1) {
                openManageApplications(pl, tid, finalPage + 1);
            } else if (id == SLOT_BACK) {
                openTeamManageMenu(pl, tid);
            } else if (id >= 0 && id < end - start) {
                openAppAction(pl, tid, apps.get(start + id), finalPage);
            }
        });
    }

    private static void openAppAction(Player pl, String tid, Team.MemberApplication app, int page) {
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l"+Util.plainName(app.getName()));
        inv.setItem(2,item(Material.RED_WOOL,"\u00a7c\u00a7l\u5ffd\u7565","\u00a77\u62d2\u7edd\u8be5\u7533\u8bf7\uff0c\u4e0d\u4f1a\u901a\u77e5\u7533\u8bf7\u4eba"));
        inv.setItem(4,item(Material.LIME_WOOL,"\u00a72\u00a7l\u901a\u8fc7","\u00a77\u6279\u51c6\u8be5\u7533\u8bf7\uff0c\u7533\u8bf7\u4eba\u5c06\u52a0\u5165\u56e2\u961f"));
        inv.setItem(8,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        p().openGui(pl, inv, id->{
            if(id==8){openManageApplications(pl,tid,page);return;}
            Team t=p().getTeamData().get(tid);if(t==null)return;
            if(id==2){t.getMembersapplications().removeIf(a->a.getUuid().equals(app.getUuid()));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u5ffd\u7565");openManageApplications(pl,tid,page);}
            else if(id==4){if(p().getTeamData().isPlayerInTeam(app.getUniqueId())){pl.sendMessage("\u00a7c\u5df2\u5728\u5176\u4ed6\u56e2\u961f");t.getMembersapplications().removeIf(a->a.getUuid().equals(app.getUuid()));p().getTeamData().save();openManageApplications(pl,tid,page);return;}
            t.getMembers().add(new Team.MemberEntry(app.getUuid(),app.getName()));t.getMembersapplications().removeIf(a->a.getUuid().equals(app.getUuid()));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u901a\u8fc7");Player tp=Bukkit.getPlayer(app.getUniqueId());if(tp!=null)tp.sendMessage("\u00a7a\u6b22\u8fce\u52a0\u5165 "+t.getName());openManageApplications(pl,tid,page);}
        });
    }

    // ==================== NOTICE / RENAME / DISBAND / QUIT ====================
    public static void openNoticeEditMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);
        requestIC(pl,"\u516c\u544a","\u8f93\u5165\u5185\u5bb9(\u6700100\u5b57,\\n\u6362\u884c):",n->{
            if(n!=null&&n.length()>100){openAlert(pl,"\u00a7c\u8fc7\u957f",()->openNoticeEditMenu(pl,tid));return;}
            t.setNotice(n!=null?n:"");p().getTeamData().save();pl.sendMessage("\u00a7a\u516c\u544a\u5df2\u66f4\u65b0");openMainMenu(pl);
        });
    }

    public static void openRenameTeamMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);
        requestIC(pl,"\u4fee\u6539\u540d\u79f0","\u8f93\u5165\u65b0\u540d\u79f0(2-10\u5b57):",nn->{
            if(nn.length()<2||nn.length()>10){openAlert(pl,"\u00a7c\u5fc5\u987b2-10\u5b57",()->openRenameTeamMenu(pl,tid));return;}
            if(p().getTeamData().nameExists(nn)){openAlert(pl,"\u00a7c\u5df2\u5b58\u5728",()->openRenameTeamMenu(pl,tid));return;}
            String old=t.getName();t.setName(nn);p().getTeamData().save();pl.sendMessage("\u00a7a"+old+"\u2192"+nn);openMainMenu(pl);
        });
    }

    public static void openDisbandConfirmMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);
        requestIC(pl,"\u00a7c\u8f93\u5165\u56e2\u961f\u540d\u79f0\u4ee5\u786e\u8ba4\u89e3\u6563","\u56e2\u961f\u540d\u79f0\uff1a"+t.getName(),in->{
            if(!in.trim().equals(t.getName())){openAlert(pl,"\u00a7c\u540d\u79f0\u9519\u8bef",()->openDisbandConfirmMenu(pl,tid));return;}
            String tn=t.getName();p().getTeamData().remove(tid);p().getMessageData().deleteTeamMessages(tid);p().getFundLog().deleteTeamLogs(tid);p().getTeamData().save();p().getMessageData().save();p().getFundLog().save();
            pl.sendMessage("\u00a7a\u56e2\u961f "+tn+" \u5df2\u89e3\u6563");openMainMenu(pl);
        });
    }

    public static void openQuitConfirmMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);
        requestIC(pl,"\u9000\u51fa","\u8f93\u5165 yes \u786e\u8ba4:",in->{
            if(!in.equalsIgnoreCase("yes")){openAlert(pl,"\u00a7c\u8f93\u5165\u9519\u8bef",()->openQuitConfirmMenu(pl,tid));return;}
            t.getMembers().removeIf(m->m.getUuid().equals(pl.getUniqueId().toString()));p().getTeamData().save();
            pl.sendMessage("\u00a7a\u5df2\u9000\u51fa");openMainMenu(pl);
        });
    }

    // ==================== RANKING ====================
    public static void openTeamRankingMenu(Player pl) {
        List<Map.Entry<String,Team>> ranked=new ArrayList<>();
        for(Map.Entry<String,Team> e:p().getTeamData().getAll().entrySet()){if(e.getValue().isPublic())ranked.add(e);}
        if(ranked.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u516c\u5f00\u56e2\u961f",()->openMainMenu(pl));return;}
        java.util.Collections.sort(ranked,(a,b)->{Team ta=a.getValue(),tb=b.getValue();if(ta.getActivity()>0&&tb.getActivity()==0)return-1;if(tb.getActivity()>0&&ta.getActivity()==0)return 1;if(ta.getActivity()>0&&tb.getActivity()>0)return Long.compare(tb.getActivity(),ta.getActivity());return Long.compare(tb.getFunds(),ta.getFunds());});
        int visible=Math.min(45,ranked.size());int rows=(visible/9)+1;int size=Math.min(54,(rows+1)*9);if(size<9)size=9;
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7l\u6392\u884c\u699c");int idx=0;
        for(int i=0;i<visible;i++){Team t=ranked.get(i).getValue();inv.setItem(idx++,decorativeSkull("\u00a7l"+t.getName(),"\u6210\u9577\u503c:"+t.getActivity()+" | \u79ef\u91d1:"+t.getFunds()+" | "+t.getMemberCount()+"\u4eba"));}
        int bs=size-9;inv.setItem(bs,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        p().openGui(pl, inv, id->{if(id==bs){openMainMenu(pl);return;}if(id>=0&&id<visible){Map.Entry<String,Team> entry=ranked.get(id);showApplyConfirm(pl,entry.getKey(),entry.getValue());}});
    }

    // ==================== JOIN BY ID ====================
    public static void openJoinByIdInput(Player pl) {
        requestIC(pl,"\u641c\u7d22","\u8f93\u51654\u4f4d\u56e2\u961fID:",in->{
            if(in==null||in.trim().length()!=4){openAlert(pl,"\u00a7cID\u5fc5\u987b4\u4f4d",()->openJoinByIdInput(pl));return;}
            String tid=p().getTeamData().resolveId(in);
            if(tid==null){openAlert(pl,"\u00a7c\u672a\u627e\u5230",()->openJoinByIdInput(pl));return;}
            showApplyConfirm(pl,tid,p().getTeamData().get(tid));
        });
    }

    private static void showApplyConfirm(Player pl, String tid, Team t) {
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l\u7533\u8bf7\u52a0\u5165");
        inv.setItem(2,item(Material.PAPER,"\u00a7e"+t.getName(),"\u00a77"+tid,"\u6210\u957f\u503c:"+t.getActivity(),"\u79ef\u91d1:"+t.getFunds()+cur(),"\u4eba\u6570:"+t.getMemberCount()));
        inv.setItem(4,item(Material.LIME_WOOL,"\u00a72\u00a7l\u786e\u5b9a\u7533\u8bf7","\u00a77\u5411\u8be5\u56e2\u961f\u53d1\u9001\u52a0\u5165\u7533\u8bf7\uff0c\u7b49\u5f85\u7ba1\u7406\u5458\u5ba1\u6838"));
        inv.setItem(8,item(Material.ARROW,"\u00a7c\u00a7l\u53d6\u6d88"));
        p().openGui(pl, inv, id->{if(id==4){UUID u=pl.getUniqueId();for(Map.Entry<String,Team> e:p().getTeamData().getAll().entrySet())e.getValue().getMembersapplications().removeIf(a->a.getUuid().equals(u.toString()));t.getMembersapplications().add(new Team.MemberApplication(u.toString(),pl.getName()));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u53d1\u9001\u7533\u8bf7");openMainMenu(pl);}else openMainMenu(pl);});
    }

    // ==================== CREATE ====================
    public static void openCreateTeamCheck(Player pl) {
        openCreateTeamMenu(pl);
    }

    public static void openCreateTeamMenu(Player pl) {
        double bal=p().getEconomy().getBalance(pl.getUniqueId()); long cost=p().getConfig2().getCreateTeamCost();
        requestIC(pl,"\u521b\u5efa\u56e2\u961f","\u8d39\u7528:"+cost+cur()+" \u4f59\u989d:"+String.format("%.0f",bal)+cur()+"\n\u8f93\u5165\u540d\u79f0(2-10\u5b57):",n->{
            if(n.length()<2||n.length()>10){openAlert(pl,"\u00a7c\u5fc5\u987b2-10\u5b57",()->openCreateTeamMenu(pl));return;}
            if(p().getTeamData().nameExists(n)){openAlert(pl,"\u00a7c\u540d\u79f0\u5df2\u5b58\u5728",()->openCreateTeamMenu(pl));return;}
            if(p().getEconomy().getBalance(pl.getUniqueId())<cost){openAlert(pl,"\u00a7c\u4f59\u989d\u4e0d\u8db3",()->openMainMenu(pl));return;}
            if(!p().getEconomy().withdraw(pl.getUniqueId(),cost)){openAlert(pl,"\u00a7c\u6263\u6b3e\u5931\u8d25",()->openMainMenu(pl));return;}
            String id=Util.generateTeamId(p().getTeamData().getAll().keySet());if(id==null){openAlert(pl,"\u00a7c\u5931\u8d25",()->openMainMenu(pl));return;}
            Team t=new Team(n,pl.getUniqueId(),pl.getName());p().getTeamData().put(id,t);p().getTeamData().save();pl.sendMessage("\u00a7a\u521b\u5efa\u6210\u529f ID:"+id);openTeamDetail(pl,id);
        });
    }

    // ==================== ADMIN ====================
    public static void openAdminTeamList(Player pl) {
        List<String> ids=new ArrayList<>(p().getTeamData().getAll().keySet());
        if(ids.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u56e2\u961f",()->{});return;}
        java.util.Collections.sort(ids,(a,b)->{Team ta=p().getTeamData().get(a),tb=p().getTeamData().get(b);if(ta.getActivity()>0&&tb.getActivity()==0)return-1;if(tb.getActivity()>0&&ta.getActivity()==0)return 1;if(ta.getActivity()>0&&tb.getActivity()>0)return Long.compare(tb.getActivity(),ta.getActivity());return Long.compare(tb.getFunds(),ta.getFunds());});
        int slots=Math.min(45,ids.size());int rows=(slots/9)+1;int size=Math.min(54,(rows+1)*9);if(size<9)size=9;
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7lOP\u7ba1\u7406");int idx=0;
        for(String id:ids){if(idx>=45)break;Team t=p().getTeamData().get(id);inv.setItem(idx++,item(Material.REPEATER,"\u00a7l"+t.getName(),"\u6210\u957f\u503c:"+t.getActivity()+" | \u79ef\u91d1:"+t.getFunds()+" | "+t.getMemberCount()+"\u4eba"));}
        int bs=size-9;inv.setItem(bs,item(Material.BARRIER,"\u00a7l\u5173\u95ed"));p().openGui(pl, inv, id->{if(id==bs)return;if(id>=0&&id<ids.size())openAdminTeamMenu(pl,ids.get(id));});
    }

    public static void openAdminTeamMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);if(t==null){openAlert(pl,"\u00a7c\u4e0d\u5b58\u5728",()->openAdminTeamList(pl));return;}
        Inventory inv=Bukkit.createInventory(null,18,"\u00a7lOP: "+t.getName());
        inv.setItem(0,item(Material.BOOK,"\u00a7l\u8be6\u60c5"));inv.setItem(1,item(Material.COMPASS,"\u00a7l\u4f20\u9001\u951a\u70b9"));
        inv.setItem(2,item(Material.ENDER_PEARL,"\u00a7l\u4e92\u4f20"));inv.setItem(3,item(Material.GOLD_NUGGET,"\u00a7l\u79ef\u91d1"));
        inv.setItem(4,item(Material.WRITABLE_BOOK,"\u00a7l\u7559\u8a00\u677f"));inv.setItem(5,item(Material.REPEATER,"\u00a7l\u7ba1\u7406"));
        inv.setItem(17,item(Material.ARROW,"\u00a7c\u8fd4\u56de"));
        p().openGui(pl, inv, id->{switch(id){case 0:openTeamDetail(pl,tid);break;case 1:openWarpMenu(pl,tid);break;case 2:openTpaMainMenu(pl,tid);break;case 3:openTeamFundMenu(pl,tid);break;case 4:openMessageBoard(pl,tid);break;case 5:openTeamManageMenu(pl,tid);break;case 17:openAdminTeamList(pl);break;}});
    }

    // ==================== NEW MESSAGE ALERT ====================
    public static void openNewMessageAlert(Player pl, String tid, String tn) {
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l\u65b0\u7559\u8a00");
        inv.setItem(2,item(Material.PAPER,"\u00a7e"+tn+" \u6709\u65b0\u7559\u8a00"));inv.setItem(4,item(Material.WRITABLE_BOOK,"\u00a72\u00a7l\u7acb\u5373\u67e5\u770b","\u00a77\u70b9\u51fb\u67e5\u770b\u56e2\u961f\u7559\u8a00\u677f"));inv.setItem(8,item(Material.BARRIER,"\u00a7c\u00a7l\u7a0d\u540e"));
        p().openGui(pl, inv, id->{if(id==4)openMessageBoard(pl,tid);});
    }

    // ==================== ALERT ====================
    public static void openAlert(Player pl, String msg, Runnable cb) {
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l\u63d0\u793a");
        inv.setItem(4,item(Material.PAPER,msg.length()>40?msg.substring(0,40):msg));inv.setItem(8,item(Material.LIME_WOOL,"\u00a72\u00a7l\u786e\u5b9a"));
        p().openGui(pl, inv, id->{if(id==8&&cb!=null)cb.run();});
    }

    // ==================== HELPERS ====================
    private static void openServerMenu(Player pl) {
        pl.closeInventory();
        Bukkit.getScheduler().runTask(p(), () -> {
            if (pl.isOnline()) {
                pl.performCommand("menu");
            }
        });
    }

    private static void handleQuitTeam(Player pl, String tid) {
        if (p().getTeamData().isTeamOperator(pl.getUniqueId(), tid)) {
            openAlert(
                pl,
                "\u00a7c\u7ba1\u7406\u5458\u4e0d\u80fd\u76f4\u63a5\u9000\u51fa\u56e2\u961f\uff0c\u8bf7\u5148\u5728\u6210\u5458\u7ba1\u7406\u4e2d\u5c06\u81ea\u5df1\u964d\u7ea7\u4e3a\u666e\u901a\u6210\u5458\uff0c\u7136\u540e\u518d\u6267\u884c\u9000\u51fa\uff1b\u5982\u9700\u7ed3\u675f\u6574\u4e2a\u56e2\u961f\uff0c\u8bf7\u4f7f\u7528\u201c\u89e3\u6563\u56e2\u961f\u201d\u529f\u80fd\u3002",
                () -> openMainMenu(pl)
            );
        }
        else openQuitConfirmMenu(pl,tid);
    }

    private static void teleportToWarp(Player pl, String tid, String wn, Team.WarpPoint wp) {
        World w;
        String wName = wp.getWorld();
        if (!wName.isEmpty()) { w = Bukkit.getWorld(wName); }
        else { w = null; }
        if (w == null) {
            switch(wp.getDim()){case -1:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.NETHER).findFirst().orElse(null);break;case 1:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.THE_END).findFirst().orElse(null);break;default:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.NORMAL).findFirst().orElse(null);break;}
        }
        if(w==null){openAlert(pl,"\u00a7c\u7ef4\u5ea6\u5f02\u5e38",()->openWarpMenu(pl,tid,0));return;}
        Location destination = new Location(w, wp.getX() + 0.5, wp.getY(), wp.getZ() + 0.5);
        if (!TeleportWaitBridge.startWarmup(pl, () -> {
            TeleportWaitBridge.allowNextTeleport(pl);
            if (pl.teleport(destination)) {
                pl.sendMessage("\u00a7a\u5df2\u4f20\u9001\u81f3 " + wn);
            } else {
                pl.sendMessage("\u00a7c\u4f20\u9001\u5931\u8d25\uff01");
            }
        })) {
            pl.sendMessage("\u00a7c\u4f20\u9001\u7cfb\u7edf\u6682\u4e0d\u53ef\u7528\uff01");
        }
    }

    public static class PendingChatInput {
        Consumer<String> cb;
        PendingChatInput(Consumer<String> c){cb=c;}
    }
}
