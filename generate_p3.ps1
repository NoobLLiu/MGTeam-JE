# MGTeam Part 3: GUI Router, BedrockForms, JavaMenus, Main Plugin

$ROOT = "D:\codex 2\mc-server\mc-server\local-plugins\mgteam"
$SRC = "$ROOT\src\cn\gmzc\mgteam"
function Write-JavaFile { param($Path, $Content) $fullPath = "$SRC\$Path"; [System.IO.File]::WriteAllText($fullPath, $Content, [System.Text.UTF8Encoding]::new($false)); Write-Host "  $Path" }

# ==================== GUI ROUTER ====================

Write-JavaFile "gui\GuiRouter.java" @'
package cn.gmzc.mgteam.gui;

import cn.gmzc.mgteam.MGTeamPlugin;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class GuiRouter {
    private static MGTeamPlugin p;

    public static void init(MGTeamPlugin p2) { p = p2; }
    public static MGTeamPlugin p() { return p; }
    public static String cur() { return p().getConfig2().getCurrencyName(); }

    public static boolean isBedrock(Player pl) {
        try { return FloodgateApi.getInstance().isFloodgatePlayer(pl.getUniqueId()); } catch (Exception e) { return false; }
    }

    public static void openMainMenu(Player pl) { if(isBedrock(pl))BedrockForms.openMainMenu(pl);else JavaMenus.openMainMenu(pl); }
    public static void openTeamDetail(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openTeamDetail(pl,tid);else JavaMenus.openTeamDetail(pl,tid); }
    public static void openWarpMainMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openWarpMainMenu(pl,tid);else JavaMenus.openWarpMainMenu(pl,tid); }
    public static void openWarpTeleportMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openWarpTeleportMenu(pl,tid);else JavaMenus.openWarpTeleportMenu(pl,tid); }
    public static void openAddWarpMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openAddWarpMenu(pl,tid);else JavaMenus.openAddWarpMenu(pl,tid); }
    public static void openRemoveWarpMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openRemoveWarpMenu(pl,tid);else JavaMenus.openRemoveWarpMenu(pl,tid); }
    public static void openTpaMainMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openTpaMainMenu(pl,tid);else JavaMenus.openTpaMainMenu(pl,tid); }
    public static void openTeamFundMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openTeamFundMenu(pl,tid);else JavaMenus.openTeamFundMenu(pl,tid); }
    public static void openDepositMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openDepositMenu(pl,tid);else JavaMenus.openDepositMenu(pl,tid); }
    public static void openWithdrawMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openWithdrawMenu(pl,tid);else JavaMenus.openWithdrawMenu(pl,tid); }
    public static void openFundLogMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openFundLogMenu(pl,tid);else JavaMenus.openFundLogMenu(pl,tid); }
    public static void openMessageBoard(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openMessageBoard(pl,tid);else JavaMenus.openMessageBoard(pl,tid); }
    public static void openAddMessageMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openAddMessageMenu(pl,tid);else JavaMenus.openAddMessageMenu(pl,tid); }
    public static void openTeamManageMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openTeamManageMenu(pl,tid);else JavaMenus.openTeamManageMenu(pl,tid); }
    public static void openManageMembers(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openManageMembers(pl,tid);else JavaMenus.openManageMembers(pl,tid); }
    public static void openManageApplications(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openManageApplications(pl,tid);else JavaMenus.openManageApplications(pl,tid); }
    public static void openNoticeEditMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openNoticeEditMenu(pl,tid);else JavaMenus.openNoticeEditMenu(pl,tid); }
    public static void openRenameTeamMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openRenameTeamMenu(pl,tid);else JavaMenus.openRenameTeamMenu(pl,tid); }
    public static void openDisbandConfirmMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openDisbandConfirmMenu(pl,tid);else JavaMenus.openDisbandConfirmMenu(pl,tid); }
    public static void openQuitConfirmMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openQuitConfirmMenu(pl,tid);else JavaMenus.openQuitConfirmMenu(pl,tid); }
    public static void openTeamRankingMenu(Player pl) { if(isBedrock(pl))BedrockForms.openTeamRankingMenu(pl);else JavaMenus.openTeamRankingMenu(pl); }
    public static void openJoinByIdInput(Player pl) { if(isBedrock(pl))BedrockForms.openJoinByIdInput(pl);else JavaMenus.openJoinByIdInput(pl); }
    public static void openCreateTeamMenu(Player pl) { if(isBedrock(pl))BedrockForms.openCreateTeamMenu(pl);else JavaMenus.openCreateTeamMenu(pl); }
    public static void openAdminTeamList(Player pl) { if(isBedrock(pl))BedrockForms.openAdminTeamList(pl);else JavaMenus.openAdminTeamList(pl); }
    public static void openAdminTeamMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openAdminTeamMenu(pl,tid);else JavaMenus.openAdminTeamMenu(pl,tid); }
    public static void openNewMessageAlert(Player pl,String tid,String tn) { if(isBedrock(pl))BedrockForms.openNewMessageAlert(pl,tid,tn);else JavaMenus.openNewMessageAlert(pl,tid,tn); }
    public static void openAlert(Player pl,String msg,Runnable cb) { if(isBedrock(pl))BedrockForms.openAlert(pl,msg,cb);else JavaMenus.openAlert(pl,msg,cb); }
}
'@

# ==================== BEDROCK FORMS ====================

Write-JavaFile "gui\BedrockForms.java" @'
package cn.gmzc.mgteam.gui;

import cn.gmzc.mgteam.model.FundLogEntry;
import cn.gmzc.mgteam.model.MessageEntry;
import cn.gmzc.mgteam.model.Team;
import cn.gmzc.mgteam.util.Util;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FlooodgateApi;

public class BedrockForms {
    private static MGTeamPlugin p() { return GuiRouter.p(); }
    private static String cur() { return GuiRouter.cur(); }

    // MAIN MENU
    public static void openMainMenu(Player pl) {
        String tid = p().getTeamData().getPlayerTeamId(pl.getUniqueId());
        if (tid != null) {
            Team t = p().getTeamData().get(tid);
            boolean isOp = p().getTeamData().isTeamOperator(pl.getUniqueId(), tid);
            int online = 0;
            for (Player op : Bukkit.getOnlinePlayers()) {
                if (!op.getUniqueId().equals(pl.getUniqueId()) && tid.equals(p().getTeamData().getPlayerTeamId(op.getUniqueId()))) online++;
            }
            String teamName = t.getName();
            SimpleForm.Builder f = SimpleForm.builder()
                .title("\u00a7l\u3010" + teamName + "\u3011")
                .content("\u00a7e\u6b22\u8fce\u56de\u6765\uff0c\u00a7f" + pl.getName() + "\u00a7e\uff01\n\n\u00a7e\u56e2\u961f\u516c\u544a\uff1a\n" + t.getNotice().replace("\\n","\n"))
                .button("\u00a7l\u8be6\u60c5\u4fe1\u606f\n\u00a7r\u00a7t\u67e5\u770b\u56e2\u961f\u8be6\u60c5", FormImage.of(FormImage.Type.PATH,"textures/menu_1/thebook"))
                .button("\u00a7l\u4f20\u9001\u951a\u70b9\n\u00a7r\u00a7t\u56e2\u961f\u5171\u4eab\u4f20\u9001\u70b9", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmtp"));
            if (online > 0) f.button("\u00a7l\u6210\u5458\u4e92\u4f20\n\u00a7r\u00a72\u5f53\u524d\u5728\u7ebf\u961f\u53cb\uff1a" + online, FormImage.of(FormImage.Type.PATH,"textures/menu_1/online"));
            else f.button("\u00a7l\u6210\u5458\u4e92\u4f20\n\u00a7r\u00a7t\u6210\u5458\u95f4\u514d\u540c\u610f\u4f20\u9001", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmtpa"));
            f.button("\u00a7l\u56e2\u961f\u79ef\u91d1\n\u00a7r\u00a7t\u5f53\u524d\u79ef\u91d1\uff1a" + t.getFunds() + cur(), FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmmoney"));
            if (p().getMessageData().hasNewMessages(pl.getUniqueId(), tid))
                f.button("\u00a7l\u7559\u8a00\u677f\n\u00a7r\u00a72\u6709\u65b0\u7684\u7559\u8a00\uff0c\u70b9\u51fb\u67e5\u770b", FormImage.of(FormImage.Type.PATH,"textures/menu_1/newliuyan"));
            else
                f.button("\u00a7l\u7559\u8a00\u677f\n\u00a7r\u00a7t\u67e5\u770b\u548c\u53d1\u5e03\u56e2\u961f\u6210\u5458\u7559\u8a00", FormImage.of(FormImage.Type.PATH,"textures/menu_1/liuyanban"));
            if (isOp) {
                f.button("\u00a7l\u7ba1\u7406\u56e2\u961f\n\u00a7r\u00a7t\u56e2\u961f\u7ba1\u7406\u5458\u4e13\u5c5e\u83dc\u5355", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmset"));
                f.button("\u00a7c\u00a7l\u9000\u51fa\u56e2\u961f", FormImage.of(FormImage.Type.PATH,"textures/menu_1/jiesan"));
            } else {
                f.button("\u00a7c\u00a7l\u9000\u51fa\u56e2\u961f", FormImage.of(FormImage.Type.PATH,"textures/menu_1/jiesan"));
            }
            f.closedResultHandler(() -> {});
            f.validResultHandler(r -> {
                int id = r.clickedButtonId();
                if (isOp) {
                    switch (id) {
                        case 0: openTeamDetail(pl,tid); break; case 1: openWarpMainMenu(pl,tid); break;
                        case 2: openTpaMainMenu(pl,tid); break; case 3: openTeamFundMenu(pl,tid); break;
                        case 4: openMessageBoard(pl,tid); break; case 5: openTeamManageMenu(pl,tid); break;
                        case 6: handleQuitTeam(pl,tid); break;
                    }
                } else {
                    switch (id) {
                        case 0: openTeamDetail(pl,tid); break; case 1: openWarpMainMenu(pl,tid); break;
                        case 2: openTpaMainMenu(pl,tid); break; case 3: openTeamFundMenu(pl,tid); break;
                        case 4: openMessageBoard(pl,tid); break; case 5: handleQuitTeam(pl,tid); break;
                    }
                }
            });
            FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f.build());
        } else {
            SimpleForm f = SimpleForm.builder()
                .title("\u00a7l\u3010\u56e2\u961f\u7cfb\u7edf\u3011")
                .content("\u00a7c\u4f60\u5f53\u524d\u672a\u52a0\u5165\u4efb\u4f55\u56e2\u961f\u3002\n\n\u00a77\u9009\u62e9\u529f\u80fd\uff1a")
                .button("\u00a7l\u521b\u5efa\u65b0\u56e2\u961f", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmtpa"))
                .button("\u00a7l\u901a\u8fc7ID\u641c\u7d22\u56e2\u961f", FormImage.of(FormImage.Type.PATH,"textures/menu_1/guanlishenqing"))
                .button("\u00a7l\u5728\u6392\u884c\u699c\u4e2d\u67e5\u627e\u516c\u5f00\u56e2\u961f", FormImage.of(FormImage.Type.PATH,"textures/menu_1/findtm"))
                .closedResultHandler(() -> {})
                .validResultHandler(r -> {
                    switch (r.clickedButtonId()) {
                        case 0: openCreateTeamCheck(pl); break; case 1: openJoinByIdInput(pl); break;
                        case 2: openTeamRankingMenu(pl); break;
                    }
                })
                .build();
            FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
        }
    }

    // TEAM DETAIL
    public static void openTeamDetail(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); if (t == null) return;
        boolean isOp = p().getTeamData().isTeamOperator(pl.getUniqueId(), tid);
        StringBuilder c = new StringBuilder();
        c.append("\u00a7l\u6211\u7684\u56e2\u961f\uff1a\u00a7f").append(t.getName()).append("\n\u00a77\u56e2\u961fID\uff1a\u00a7f").append(tid).append("\n");
        c.append("\u00a76\u6211\u7684\u8eab\u4efd\uff1a").append(isOp ? "\u00a7c\u56e2\u961f\u7ba1\u7406\u5458" : "\u00a7a\u6210\u5458").append("\n\n");
        c.append("\u00a7l\u56e2\u961f\u6210\u957f\u503c\uff1a\u00a7f").append(t.getActivity()).append("\n");
        if (!t.getNotice().isEmpty()) c.append("\n\u00a7l\u56e2\u961f\u516c\u544a\uff1a\n").append(t.getNotice().replace("\\n","\n")).append("\n");
        c.append("\n\u00a7l\u7ba1\u7406\u5458\uff1a\n"); for (Team.MemberEntry m : t.getOperators()) c.append("\u00a7f\u2022 ").append(Util.pName(m.getName())).append("\n");
        c.append("\n\u00a7l\u666e\u901a\u6210\u5458\uff1a\n"); if (t.getMembers().isEmpty()) c.append("\u00a77\u6682\u65e0\n"); else for (Team.MemberEntry m : t.getMembers()) c.append("\u00a7f\u2022 ").append(Util.pName(m.getName())).append("\n");
        c.append("\n\u00a7l\u4eba\u6570\uff1a\u00a7f").append(t.getMemberCount()).append("\n");
        c.append("\u00a7l\u56e2\u961f\u8d44\u91d1\uff1a\u00a7f").append(t.getFunds()).append(" \u00a77").append(cur()).append("\n");
        if (isOp && !t.getMembersapplications().isEmpty()) c.append("\n\u00a7l\u5f85\u5904\u7406\u7533\u8bf7\uff1a\u00a7c").append(t.getMembersapplications().size()).append("\u6761");
        SimpleForm f = SimpleForm.builder().title("\u00a7l\u3010\u56e2\u961f\u8be6\u60c5\u3011").content(c.toString())
            .button("\u00a7l\u8fd4\u56de\u4e3b\u83dc\u5355", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"))
            .closedResultHandler(() -> {}).validResultHandler(r -> openMainMenu(pl)).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // WARP
    public static void openWarpMainMenu(Player pl, String tid) {
        SimpleForm f = SimpleForm.builder().title("\u00a7l\u3010\u4f20\u9001\u951a\u70b9\u3011").content("\u00a77\u9009\u62e9\u64cd\u4f5c\uff1a")
            .button("\u00a7e\u00a7l\u524d\u5f80\u4f20\u9001\u70b9", FormImage.of(FormImage.Type.PATH,"textures/menu_1/gotohome"))
            .button("\u00a72\u00a7l\u6dfb\u52a0\u4f20\u9001\u70b9", FormImage.of(FormImage.Type.PATH,"textures/menu_1/addtmhome"))
            .button("\u00a7c\u00a7l\u79fb\u9664\u4f20\u9001\u70b9", FormImage.of(FormImage.Type.PATH,"textures/menu_1/removetmhome"))
            .button("\u00a7l\u8fd4\u56de\u4e3b\u83dc\u5355", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"))
            .closedResultHandler(() -> {}).validResultHandler(r -> { switch(r.clickedButtonId()){case 0:openWarpTeleportMenu(pl,tid);break;case 1:openAddWarpMenu(pl,tid);break;case 2:openRemoveWarpMenu(pl,tid);break;case 3:openMainMenu(pl);break;} }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    public static void openWarpTeleportMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); Map<String,Team.WarpPoint> warps = t.getWarpPoints();
        if (warps.isEmpty()) { openAlert(pl,"\u00a7c\u6ca1\u6709\u4f20\u9001\u70b9\uff01",()->openWarpMainMenu(pl,tid)); return; }
        SimpleForm.Builder f = SimpleForm.builder().title("\u00a7l\u3010\u4f20\u9001\u951a\u70b9\u3011").content("\u00a7e\u9009\u62e9\u4f20\u9001\u70b9\uff1a");
        List<String> names = new ArrayList<>(warps.keySet());
        for (String n : names) { Team.WarpPoint wp = warps.get(n); f.button("\u00a7l"+n+"\n"+Util.dimName(wp.getDim())+" | "+Util.plainName(wp.getCreatorName()), FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmtp")); }
        f.button("\u00a7l\u8fd4\u56de", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"));
        f.closedResultHandler(() -> {}); f.validResultHandler(r -> { int id=r.clickedButtonId(); if(id>=0&&id<names.size())teleportToWarp(pl,tid,names.get(id),warps.get(names.get(id))); else openWarpMainMenu(pl,tid); });
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f.build());
    }

    public static void openAddWarpMenu(Player pl, String tid) {
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u6dfb\u52a0\u4f20\u9001\u70b9\u3011")
            .label("\u00a7e\u6dfb\u52a0\u65b0\u7684\u4f20\u9001\u951a\u70b9\n\n\u00a77\u5c06\u4f60\u5f53\u524d\u7684\u4f4d\u7f6e\u8bbe\u4e3a\u4f20\u9001\u70b9")
            .input("\u00a77\u4f20\u9001\u70b9\u540d\u79f0", "\u4f8b\u5982\uff1a\u57fa\u5730\u3001\u77ff\u6d1e", "")
            .closedResultHandler(() -> openWarpMainMenu(pl,tid))
            .validResultHandler(r -> {
                String name = r.asInput(1); if (name==null||name.trim().isEmpty()||name.trim().length()>10){ openAlert(pl,"\u00a7c\u540d\u79f0\u5fc5\u987b1-10\u5b57\u7b26\uff01",()->openAddWarpMenu(pl,tid)); return; }
                name=name.trim(); Team t=p().getTeamData().get(tid); if(t.getWarpPoints().containsKey(name)){openAlert(pl,"\u00a7c\u540d\u79f0\u5df2\u5b58\u5728\uff01",()->openAddWarpMenu(pl,tid));return;}
                Location loc=pl.getLocation(); Team.WarpPoint wp=new Team.WarpPoint(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(),Util.dimId(loc.getWorld()),pl.getUniqueId().toString(),pl.getName());
                t.getWarpPoints().put(name,wp); p().getTeamData().save(); pl.sendMessage("\u00a7a[\u4f20\u9001\u951a\u70b9] \u521b\u5efa\u6210\u529f\uff01"); openWarpMainMenu(pl,tid);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    public static void openRemoveWarpMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); boolean isOp = p().getTeamData().isTeamOperator(pl.getUniqueId(), tid);
        Map<String,Team.WarpPoint> warps = t.getWarpPoints();
        List<String> removable = new ArrayList<>();
        for(Map.Entry<String,Team.WarpPoint> e : warps.entrySet()) { if(isOp||pl.getUniqueId().toString().equals(e.getValue().getCreatorUuid())) removable.add(e.getKey()); }
        if(removable.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u53ef\u79fb\u9664\u7684\u4f20\u9001\u70b9",()->openWarpMainMenu(pl,tid));return;}
        SimpleForm.Builder f = SimpleForm.builder().title("\u00a7l\u3010\u79fb\u9664\u4f20\u9001\u70b9\u3011").content(isOp?"\u00a7e\u70b9\u51fb\u79fb\u9664\uff08\u7ba1\u7406\u5458\u53ef\u79fb\u9664\u6240\u6709\uff09":"\u00a7e\u53ea\u80fd\u79fb\u9664\u81ea\u5df1\u521b\u5efa\u7684");
        for(String n:removable){Team.WarpPoint wp=warps.get(n); f.button("\u00a7l"+n+"\n"+Util.dimName(wp.getDim())+" | "+Util.plainName(wp.getCreatorName()), FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmtp"));}
        f.button("\u00a7l\u8fd4\u56de", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"));
        f.closedResultHandler(() -> {}); f.validResultHandler(r -> {
            int id=r.clickedButtonId(); if(id>=0&&id<removable.size()){warps.remove(removable.get(id));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u5220\u9664\uff01");openRemoveWarpMenu(pl,tid);}else openWarpMainMenu(pl,tid);
        });
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f.build());
    }

    // TPA
    public static void openTpaMainMenu(Player pl, String tid) {
        List<Player> mates = new ArrayList<>();
        for(Player op : Bukkit.getOnlinePlayers()) { if(!op.getUniqueId().equals(pl.getUniqueId())&&tid.equals(p().getTeamData().getPlayerTeamId(op.getUniqueId()))) mates.add(op); }
        if(mates.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u5176\u4ed6\u5728\u7ebf\u961f\u53cb\uff01",()->{});return;}
        SimpleForm.Builder f = SimpleForm.builder().title("\u00a7l\u3010\u6210\u5458\u4e92\u4f20\u3011").content("\u00a77\u5171"+mates.size()+"\u540d\u961f\u53cb\u5728\u7ebf\n\u00a7e\u9009\u62e9\u8981\u4f20\u9001\u7684\u961f\u53cb\uff1a");
        for(Player m : mates) {
            String line2; if(m.getWorld().equals(pl.getWorld())){double d=pl.getLocation().distance(m.getLocation());line2="X:"+m.getLocation().getBlockX()+" | \u8ddd\u79bb:"+String.format("%.1f",d)+"\u7c73";}else line2="\u00a77\u7ef4\u5ea6:"+m.getWorld().getName();
            f.button("\u00a7l"+m.getName()+"\n"+line2, FormImage.of(FormImage.Type.PATH,"textures/ui/icon_steve"));
        }
        f.button("\u00a7l\u53d6\u6d88", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"));
        f.closedResultHandler(() -> {}); f.validResultHandler(r -> {
            int id=r.clickedButtonId(); if(id>=0&&id<mates.size()){Player t=mates.get(id);if(!tid.equals(p().getTeamData().getPlayerTeamId(t.getUniqueId()))){openAlert(pl,"\u00a7c\u5df2\u79bb\u5f00\u56e2\u961f\uff01",()->{});return;}pl.teleport(t.getLocation());pl.sendMessage("\u00a7a\u5df2\u4f20\u9001\u81f3 "+t.getName());t.sendMessage("\u00a7e"+pl.getName()+"\u4f20\u9001\u5230\u4e86\u4f60\u8eab\u8fb9\uff01");}
        });
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f.build());
    }

    // FUND
    public static void openTeamFundMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); boolean en = p().getFundConsume().getStatus(pl.getUniqueId());
        SimpleForm f = SimpleForm.builder().title("\u00a7l\u3010\u56e2\u961f\u79ef\u91d1\u3011").content("\u00a7e\u56e2\u961f\u79ef\u91d1\u7ba1\u7406\n\n\u00a7l\u5f53\u524d\uff1a\u00a7f"+t.getFunds()+" \u00a77"+cur())
            .button("\u00a72\u00a7l\u5b58\u5165\u79ef\u91d1", FormImage.of(FormImage.Type.PATH,"textures/menu_1/addtmmoney"))
            .button("\u00a7c\u00a7l\u53d6\u51fa\u79ef\u91d1", FormImage.of(FormImage.Type.PATH,"textures/menu_1/gettmmoney"))
            .button("\u00a7l\u79ef\u91d1\u6d88\u8d39\uff1a"+(en?"\u00a72\u5f00":"\u00a7c\u5173")+"\n\u00a7r\u00a7t\u7531\u56e2\u961f\u79ef\u91d1\u81ea\u52a8\u57ab\u4ed8", FormImage.of(FormImage.Type.PATH,"textures/menu_1/xiaofei"))
            .button("\u00a7l\u8fd4\u56de\u4e3b\u83dc\u5355", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"))
            .closedResultHandler(() -> {}).validResultHandler(r -> {
                switch(r.clickedButtonId()){case 0:openDepositMenu(pl,tid);break;case 1:openWithdrawMenu(pl,tid);break;case 2:boolean ns=!p().getFundConsume().getStatus(pl.getUniqueId());p().getFundConsume().setStatus(pl.getUniqueId(),ns);p().getFundConsume().save();pl.sendMessage(ns?"\u00a7a\u5df2\u5f00\u542f\u79ef\u91d1\u6d88\u8d39\u4fdd\u62a4\uff01":"\u00a7a\u5df2\u5173\u95ed\u79ef\u91d1\u6d88\u8d39\u4fdd\u62a4\uff01");openTeamFundMenu(pl,tid);break;case 3:openMainMenu(pl);break;}
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    public static void openDepositMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); double bal = p().getEconomy().getBalance(pl.getUniqueId());
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u5b58\u5165\u8d44\u91d1\u3011")
            .label("\u00a7e\u6211\u7684\u4f59\u989d\uff1a\u00a7f"+String.format("%.0f",bal)+" \u00a77"+cur()+"\n\u00a7e\u56e2\u961f\u8d44\u91d1\uff1a\u00a7f"+t.getFunds()+" \u00a77"+cur())
            .input("\u00a77\u8f93\u5165\u5b58\u5165\u91d1\u989d", "\u4f8b\u5982\uff1a100", "")
            .closedResultHandler(() -> openTeamFundMenu(pl,tid)).validResultHandler(r -> {
                String in=r.asInput(1); String err=Util.validateAmount(in); if(err!=null){openAlert(pl,"\u00a7c"+err,()->openDepositMenu(pl,tid));return;}
                long amt=Util.parseAmount(in); if(!p().getEconomy().withdraw(pl.getUniqueId(),amt)){openAlert(pl,"\u00a7c\u4f59\u989d\u4e0d\u8db3\uff01",()->openDepositMenu(pl,tid));return;}
                long bb=t.getFunds(); t.setFunds(bb+amt); p().getTeamData().save(); p().getFundLog().addLog(tid,amt,"\u73a9\u5bb6 "+pl.getName()+" \u5b58\u5165",bb,t.getFunds()); p().getFundLog().save();
                pl.sendMessage("\u00a7a\u6210\u529f\u5b58\u5165 "+amt+" "+cur()+"\uff01"); openTeamFundMenu(pl,tid);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    public static void openWithdrawMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid);
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u53d6\u51fa\u8d44\u91d1\u3011")
            .label("\u00a7e\u56e2\u961f\u8d44\u91d1\uff1a\u00a7f"+t.getFunds()+" \u00a77"+cur())
            .input("\u00a77\u8f93\u5165\u53d6\u51fa\u91d1\u989d", "\u4f8b\u5982\uff1a100", "")
            .closedResultHandler(() -> openTeamFundMenu(pl,tid)).validResultHandler(r -> {
                String in=r.asInput(1); String err=Util.validateAmount(in); if(err!=null){openAlert(pl,"\u00a7c"+err,()->openWithdrawMenu(pl,tid));return;}
                long amt=Util.parseAmount(in); if(t.getFunds()<amt){openAlert(pl,"\u00a7c\u56e2\u961f\u8d44\u91d1\u4e0d\u8db3\uff01",()->openWithdrawMenu(pl,tid));return;}
                long bb=t.getFunds(); t.setFunds(bb-amt); if(!p().getEconomy().deposit(pl.getUniqueId(),amt)){t.setFunds(bb);openAlert(pl,"\u00a7c\u64cd\u4f5c\u5931\u8d25\uff01",()->openWithdrawMenu(pl,tid));return;}
                p().getTeamData().save(); p().getFundLog().addLog(tid,-amt,"\u73a9\u5bb6 "+pl.getName()+" \u53d6\u51fa",bb,t.getFunds()); p().getFundLog().save();
                pl.sendMessage("\u00a7a\u6210\u529f\u53d6\u51fa "+amt+" "+cur()+"\uff01"); openTeamFundMenu(pl,tid);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    public static void openFundLogMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); List<FundLogEntry> logs = p().getFundLog().getLogs(tid);
        int limit = p().getConfig2().getFundLogDisplayLimit(); List<FundLogEntry> display = logs.size()>limit ? logs.subList(0,limit) : logs;
        StringBuilder c = new StringBuilder();
        c.append("\u00a7e\u56e2\u961f\uff1a\u00a7f").append(t.getName()).append("\n\u00a7e\u5f53\u524d\u79ef\u91d1\uff1a\u00a7f").append(t.getFunds()).append(cur()).append("\n\u00a7e\u8bb0\u5f55\u6570\uff1a\u00a7f").append(logs.size()).append("\u6761\n\n");
        if(display.isEmpty())c.append("\u00a77\u6682\u65e0\u8bb0\u5f55\u3002"); else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
            for(FundLogEntry e : display) {
                String ts=sdf.format(new Date(e.getTimestamp())); String ch=e.getChange()>=0?"\u00a7a+"+e.getChange():"\u00a7c"+e.getChange();
                c.append("\u00a77[").append(ts).append("]\u00a7f ").append(e.getBalanceBefore()).append(" \u2192 ").append(e.getBalanceAfter()).append(" (").append(ch).append("\u00a7r)\n  \u539f\u56e0\uff1a").append(e.getReason()).append("\n");
            }
        }
        SimpleForm f = SimpleForm.builder().title("\u00a7l\u3010\u79ef\u91d1\u6d41\u6c34\u3011").content(c.toString())
            .button("\u00a7l\u8fd4\u56de", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"))
            .closedResultHandler(() -> openTeamManageMenu(pl,tid)).validResultHandler(r -> openTeamManageMenu(pl,tid)).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // MESSAGE BOARD
    public static void openMessageBoard(Player pl, String tid) {
        p().getMessageData().setLastViewTime(pl.getUniqueId(), tid);
        List<MessageEntry> msgs = p().getMessageData().getMessages(tid);
        StringBuilder c = new StringBuilder();
        c.append("\u00a7e\u56e2\u961f\uff1a\u00a7f").append(p().getTeamData().get(tid).getName()).append("\n\n");
        if(msgs.isEmpty())c.append("\u00a77\u8fd8\u6ca1\u6709\u7559\u8a00\uff01\n\n"); else {
            int show=Math.min(10,msgs.size()); c.append("\u00a7e\u6700\u8fd1\u7559\u8a00\uff08\u5171").append(msgs.size()).append("\u6761\uff09\n\n");
            for(int i=0;i<show;i++){MessageEntry m=msgs.get(i);c.append("\u00a7f"+(i+1)+". \u00a7e"+m.getSenderName()+" \u00a77("+Util.timeAgo(m.getTime())+")\n   \u00a7f"+m.getContent()+"\n\n");}
            if(msgs.size()>10)c.append("\u00a77...\u8fd8\u6709"+(msgs.size()-10)+"\u6761\u66f4\u65e9\u7684\u7559\u8a00\n\n");
        }
        SimpleForm.Builder f = SimpleForm.builder().title("\u00a7l\u3010\u7559\u8a00\u677f\u3011").content(c.toString())
            .button("\u00a72\u00a7l\u6dfb\u52a0\u7559\u8a00", FormImage.of(FormImage.Type.PATH,"textures/menu_1/addliuyan"))
            .button("\u00a7l\u8fd4\u56de\u4e3b\u83dc\u5355", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"));
        f.closedResultHandler(() -> {}); f.validResultHandler(r -> { if(r.clickedButtonId()==0)openAddMessageMenu(pl,tid); else openMainMenu(pl); });
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f.build());
    }

    public static void openAddMessageMenu(Player pl, String tid) {
        long cd=p().getMessageCooldowns().getOrDefault(tid+"_"+pl.getUniqueId(),0L); int cdSec=p().getConfig2().getMessageCooldownSeconds();
        if(System.currentTimeMillis()-cd<cdSec*1000L){long r=(cdSec*1000L-(System.currentTimeMillis()-cd))/1000;openAlert(pl,"\u00a7c\u51b7\u5374\u4e2d\uff01\u8bf7\u7b49"+r/60+"\u5206"+r%60+"\u79d2",()->openMessageBoard(pl,tid));return;}
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u6dfb\u52a0\u7559\u8a00\u3011")
            .label("\u00a7e\u53d1\u5e03\u7559\u8a00\u5230\u7559\u8a00\u677f\n\n\u00a77\u2022\u6700\u591a100\u5b57\n\u202210\u5206\u949f\u51b7\u5374")
            .input("\u00a77\u7559\u8a00\u5185\u5bb9", "\u6211\u60f3\u8bf4\u7684\u8bdd...", "")
            .closedResultHandler(() -> openMessageBoard(pl,tid)).validResultHandler(r -> {
                String c=r.asInput(1); if(c==null||c.trim().isEmpty()){openAlert(pl,"\u00a7c\u4e0d\u80fd\u4e3a\u7a7a\uff01",()->openAddMessageMenu(pl,tid));return;}
                c=c.trim(); if(c.length()>100){openAlert(pl,"\u00a7c\u8fc7\u957f\uff01\u9650100\u5b57",()->openAddMessageMenu(pl,tid));return;}
                p().getMessageData().addMessage(tid,new MessageEntry(pl.getUniqueId().toString(),pl.getName(),c)); p().getMessageData().save(); p().getMessageCooldowns().put(tid+"_"+pl.getUniqueId(),System.currentTimeMillis());
                pl.sendMessage("\u00a7a\u53d1\u5e03\u6210\u529f\uff01"); openMessageBoard(pl,tid);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // TEAM MANAGE
    public static void openTeamManageMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); int apps = t.getMembersapplications().size();
        SimpleForm.Builder f = SimpleForm.builder().title("\u00a7l\u3010\u56e2\u961f\u7ba1\u7406\u3011")
            .content("\u00a77\u56e2\u961f\uff1a\u00a7f"+t.getName()+"\n\u00a77\u516c\u544a\uff1a"+(t.getNotice().isEmpty()?"\u00a77\u672a\u8bbe\u7f6e":"\u00a7a\u5df2\u8bbe\u7f6e")+"\n\u00a77\u72b6\u6001\uff1a"+(t.isPublic()?"\u00a7a\u516c\u5f00":"\u00a7c\u79c1\u5bc6"))
            .button("\u00a7l\u7ba1\u7406\u6210\u5458\n\u00a7r\u00a7t\u8bbe\u7f6e\u6743\u9650\u4e0e\u79fb\u9664", FormImage.of(FormImage.Type.PATH,"textures/menu_1/guanliplayer"));
        if(apps>0)f.button("\u00a7l\u7533\u8bf7\n\u00a7r\u00a7c"+apps+"\u6761\u65b0\u7533\u8bf7\uff01", FormImage.of(FormImage.Type.PATH,"textures/menu_1/xindeshenqing"));
        else f.button("\u00a7l\u7533\u8bf7\n\u00a7r\u00a7t\u6682\u65e0\u7533\u8bf7", FormImage.of(FormImage.Type.PATH,"textures/menu_1/guanlishenqing"));
        f.button("\u00a7l\u7f16\u8f91\u516c\u544a", FormImage.of(FormImage.Type.PATH,"textures/menu_1/guanligonggao"))
            .button("\u00a7l\u79ef\u91d1\u6d41\u6c34", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmmoney"))
            .button("\u00a7l\u4fee\u6539\u540d\u79f0\n\u00a7r\u00a7t"+t.getName(), FormImage.of(FormImage.Type.PATH,"textures/menu_1/guanligonggao"))
            .button("\u00a7l\u516c\u5f00\u6027\n\u00a7r"+(t.isPublic()?"\u00a72\u516c\u5f00":"\u00a7c\u79c1\u5bc6"), FormImage.of(FormImage.Type.PATH,t.isPublic()?"textures/menu_1/unlock":"textures/menu_1/lock"))
            .button("\u00a7c\u00a7l\u89e3\u6563\u56e2\u961f", FormImage.of(FormImage.Type.PATH,"textures/menu_1/jiesan"))
            .button("\u00a7l\u8fd4\u56de\u4e3b\u83dc\u5355", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"));
        f.closedResultHandler(() -> openMainMenu(pl)); f.validResultHandler(r -> {
            switch(r.clickedButtonId()){
                case 0:openManageMembers(pl,tid);break;case 1:openManageApplications(pl,tid);break;case 2:openNoticeEditMenu(pl,tid);break;
                case 3:openFundLogMenu(pl,tid);break;case 4:openRenameTeamMenu(pl,tid);break;
                case 5:t.setPublic(!t.isPublic());p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u66f4\u6539\u72b6\u6001\uff01");openTeamManageMenu(pl,tid);break;
                case 6:openDisbandConfirmMenu(pl,tid);break;case 7:openMainMenu(pl);break;
            }
        });
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f.build());
    }

    // MANAGE MEMBERS
    public static void openManageMembers(Player pl, String tid) {
        Team t = p().getTeamData().get(tid);
        List<String> opts = new ArrayList<>(), types = new ArrayList<>(), uuids = new ArrayList<>();
        for(Team.MemberEntry m:t.getOperators()){opts.add("\u7ba1\u7406\u5458-"+Util.plainName(m.getName()));types.add("op");uuids.add(m.getUuid());}
        for(Team.MemberEntry m:t.getMembers()){opts.add("\u6210\u5458-"+Util.plainName(m.getName()));types.add("mem");uuids.add(m.getUuid());}
        if(opts.isEmpty()){openAlert(pl,"\u00a7c\u5217\u8868\u4e3a\u7a7a\uff01",()->openTeamManageMenu(pl,tid));return;}
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u7ba1\u7406\u6210\u5458\u3011")
            .label("\u00a7e\u9009\u62e9\u6210\u5458\uff1a").dropdown("\u00a77\u6210\u5458",opts)
            .label("\u00a7e\u64cd\u4f5c\uff1a").dropdown("\u00a77\u64cd\u4f5c",java.util.Arrays.asList("\u8bbe\u4e3a\u7ba1\u7406\u5458","\u8bbe\u4e3a\u6210\u5458","\u79fb\u51fa\u56e2\u961f"))
            .closedResultHandler(() -> openTeamManageMenu(pl,tid)).validResultHandler(r -> {
                int idx=r.asDropdown(1); int op=r.asDropdown(3); if(idx<0||idx>=opts.size())return; String uid=uuids.get(idx); String typ=types.get(idx);
                if("op".equals(typ)&&op==0){openAlert(pl,"\u00a7c\u5df2\u662f\u7ba1\u7406\u5458\uff01",()->openManageMembers(pl,tid));return;}
                if("mem".equals(typ)&&op==1){openAlert(pl,"\u00a7c\u5df2\u662f\u6210\u5458\uff01",()->openManageMembers(pl,tid));return;}
                if(uid.equals(pl.getUniqueId().toString())&&op==2){openAlert(pl,"\u00a7c\u5148\u964d\u7ea7\u518d\u9000\u51fa\uff01",()->openManageMembers(pl,tid));return;}
                if("op".equals(typ)&&t.getOperators().size()<=1&&(op==1||op==2)){openAlert(pl,"\u00a7c\u81f3\u5c11\u4fdd\u7559\u4e00\u4e2a\u7ba1\u7406\u5458\uff01",()->openManageMembers(pl,tid));return;}
                boolean self=uid.equals(pl.getUniqueId().toString())&&op==1;
                if(op==0){t.getMembers().removeIf(m->m.getUuid().equals(uid));t.getOperators().add(new Team.MemberEntry(uid,""));pl.sendMessage("\u00a7a\u5df2\u8bbe\u4e3a\u7ba1\u7406\u5458\uff01");}
                else if(op==1){t.getOperators().removeIf(m->m.getUuid().equals(uid));t.getMembers().add(new Team.MemberEntry(uid,""));pl.sendMessage("\u00a7a\u5df2\u8bbe\u4e3a\u6210\u5458\uff01");}
                else if(op==2){if("op".equals(typ))t.getOperators().removeIf(m->m.getUuid().equals(uid));else t.getMembers().removeIf(m->m.getUuid().equals(uid));p().getFundConsume().remove(UUID.fromString(uid));Player tp=Bukkit.getPlayer(UUID.fromString(uid));if(tp!=null)tp.sendMessage("\u00a7c\u4f60\u5df2\u88ab\u79fb\u51fa\u56e2\u961f "+t.getName()+"\uff01");pl.sendMessage("\u00a7a\u5df2\u79fb\u51fa\uff01");}
                p().getTeamData().save();p().getFundConsume().save();if(self)openTeamDetail(pl,tid);else openManageMembers(pl,tid);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // MANAGE APPLICATIONS
    public static void openManageApplications(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); List<Team.MemberApplication> apps = t.getMembersapplications();
        if(apps.isEmpty()){openAlert(pl,"\u00a77\u6682\u65e0\u7533\u8bf7\u3002",()->openTeamManageMenu(pl,tid));return;}
        SimpleForm.Builder f = SimpleForm.builder().title("\u00a7l\u3010\u7533\u8bf7\u7ba1\u7406\u3011").content("\u00a7e\u5171"+apps.size()+"\u6761\u7533\u8bf7\uff1a");
        for(Team.MemberApplication a:apps)f.button(Util.plainName(a.getName())+"\n"+a.getAppliedAt(), FormImage.of(FormImage.Type.PATH,"textures/ui/icon_steve"));
        f.button("\u00a7l\u8fd4\u56de", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"));
        f.closedResultHandler(() -> {}); f.validResultHandler(r -> {
            int id=r.clickedButtonId(); if(id>=0&&id<apps.size())handleApplication(pl,tid,apps.get(id)); else openTeamManageMenu(pl,tid);
        });
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f.build());
    }

    private static void handleApplication(Player pl, String tid, Team.MemberApplication app) {
        SimpleForm f = SimpleForm.builder().title("\u00a7l\u3010\u5904\u7406\u7533\u8bf7\u3011")
            .content("\u00a7e\u73a9\u5bb6\uff1a"+Util.pName(app.getName())+"\n\u00a7e\u65f6\u95f4\uff1a"+app.getAppliedAt()+"\n\n\u00a77\u5904\u7406\u65b9\u5f0f\uff1a")
            .button("\u00a7c\u00a7l\u5ffd\u7565\u7533\u8bf7", FormImage.of(FormImage.Type.PATH,"textures/ui/cancel"))
            .button("\u00a72\u00a7l\u901a\u8fc7\u7533\u8bf7", FormImage.of(FormImage.Type.PATH,"textures/menu_1/right"))
            .button("\u00a7l\u8fd4\u56de", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"))
            .closedResultHandler(() -> openManageApplications(pl,tid)).validResultHandler(r -> {
                switch(r.clickedButtonId()){case 0:Team t0=p().getTeamData().get(tid);t0.getMembersapplications().removeIf(a->a.getUuid().equals(app.getUuid()));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u5ffd\u7565");openManageApplications(pl,tid);break;
                case 1:Team t1=p().getTeamData().get(tid);if(p().getTeamData().isPlayerInTeam(app.getUniqueId())){pl.sendMessage("\u00a7c\u5df2\u52a0\u5165\u5176\u4ed6\u56e2\u961f");t1.getMembersapplications().removeIf(a->a.getUuid().equals(app.getUuid()));p().getTeamData().save();openManageApplications(pl,tid);return;}
                t1.getMembers().add(new Team.MemberEntry(app.getUuid(),app.getName()));t1.getMembersapplications().removeIf(a->a.getUuid().equals(app.getUuid()));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u901a\u8fc7 "+app.getName()+" \u7684\u7533\u8bf7\uff01");Player tp=Bukkit.getPlayer(app.getUniqueId());if(tp!=null)tp.sendMessage("\u00a7a\u6b22\u8fce\u52a0\u5165 "+t1.getName()+"\uff01");openManageApplications(pl,tid);break;
                case 2:openManageApplications(pl,tid);break;}
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // NOTICE
    public static void openNoticeEditMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid);
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u7f16\u8f91\u516c\u544a\u3011")
            .label("\u00a7e\u6700\u591a100\u5b57\uff0c\u7528 \\n \u6362\u884c")
            .input("\u00a77\u516c\u544a\u5185\u5bb9", "\u8f93\u5165...", t.getNotice())
            .closedResultHandler(() -> openTeamManageMenu(pl,tid)).validResultHandler(r -> {
                String n=r.asInput(1); if(n!=null&&n.length()>100){openAlert(pl,"\u00a7c\u8fc7\u957f\uff01\u6700100\u5b57",()->openNoticeEditMenu(pl,tid));return;}
                t.setNotice(n!=null?n:""); p().getTeamData().save(); pl.sendMessage("\u00a7a\u516c\u544a\u5df2\u66f4\u65b0\uff01"); openMainMenu(pl);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // RENAME
    public static void openRenameTeamMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid);
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u4fee\u6539\u540d\u79f0\u3011")
            .label("\u00a7e2-10\u5b57\u7b26\uff0c\u4e0d\u80fd\u91cd\u540d\n\u00a7l\u5f53\u524d\uff1a\u00a7f"+t.getName())
            .input("\u00a77\u65b0\u540d\u79f0", "\u8f93\u5165...", t.getName())
            .closedResultHandler(() -> openTeamManageMenu(pl,tid)).validResultHandler(r -> {
                String nn=r.asInput(1); if(nn==null||nn.trim().length()<2||nn.trim().length()>10){openAlert(pl,"\u00a7c\u5fc5\u987b2-10\u5b57\uff01",()->openRenameTeamMenu(pl,tid));return;}
                nn=nn.trim(); if(p().getTeamData().nameExists(nn)){openAlert(pl,"\u00a7c\u5df2\u5b58\u5728\u540c\u540d\u56e2\u961f\uff01",()->openRenameTeamMenu(pl,tid));return;}
                String old=t.getName(); t.setName(nn); p().getTeamData().save(); pl.sendMessage("\u00a7a\u540d\u79f0\u5df2\u4ece "+old+" \u4fee\u6539\u4e3a "+nn+"\uff01"); openMainMenu(pl);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // DISBAND & QUIT
    public static void openDisbandConfirmMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid);
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u89e3\u6563\u56e2\u961f\u3011")
            .label("\u00a7c\u00a7l\u6b64\u64cd\u4f5c\u4e0d\u53ef\u64a4\u9500\uff01\n\n\u8f93\u5165\u56e2\u961f\u540d\u79f0\u786e\u8ba4\uff1a"+t.getName())
            .input("\u00a77\u786e\u8ba4", "\u8bf7\u8f93\u5165\uff1a"+t.getName(), "")
            .closedResultHandler(() -> openTeamManageMenu(pl,tid)).validResultHandler(r -> {
                String in=r.asInput(1); if(in==null||!in.trim().equals(t.getName())){openAlert(pl,"\u00a7c\u540d\u79f0\u8f93\u5165\u9519\u8bef\uff01",()->openDisbandConfirmMenu(pl,tid));return;}
                for(Team.MemberEntry m:t.getOperators())p().getFundConsume().remove(m.getUniqueId()); for(Team.MemberEntry m:t.getMembers())p().getFundConsume().remove(m.getUniqueId());
                String tn=t.getName(); p().getTeamData().remove(tid); p().getMessageData().deleteTeamMessages(tid); p().getFundLog().deleteTeamLogs(tid); p().getTeamData().save(); p().getMessageData().save(); p().getFundConsume().save(); p().getFundLog().save();
                pl.sendMessage("\u00a7a\u56e2\u961f "+tn+" \u5df2\u89e3\u6563\uff01"); openMainMenu(pl);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    public static void openQuitConfirmMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid);
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u9000\u51fa\u56e2\u961f\u3011")
            .label("\u00a7e\u786e\u8ba4\u9000\u51fa "+t.getName()+" \uff1f\n\u00a77\u8f93\u5165 yes \u786e\u8ba4")
            .input("\u00a77\u786e\u8ba4", "\u8bf7\u8f93\u5165\uff1ayes", "")
            .closedResultHandler(() -> openMainMenu(pl)).validResultHandler(r -> {
                String in=r.asInput(1); if(in==null||!in.trim().equalsIgnoreCase("yes")){openAlert(pl,"\u00a7c\u8f93\u5165\u9519\u8bef\uff01",()->openQuitConfirmMenu(pl,tid));return;}
                t.getMembers().removeIf(m->m.getUuid().equals(pl.getUniqueId().toString())); p().getFundConsume().remove(pl.getUniqueId()); p().getTeamData().save(); p().getFundConsume().save();
                pl.sendMessage("\u00a7a\u5df2\u9000\u51fa "+t.getName()+"\uff01"); openMainMenu(pl);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // RANKING
    public static void openTeamRankingMenu(Player pl) {
        List<String> pIds = new ArrayList<>(); List<Team> pTeams = new ArrayList<>();
        for(Map.Entry<String,Team> e : p().getTeamData().getAll().entrySet()){if(e.getValue().isPublic()){pIds.add(e.getKey());pTeams.add(e.getValue());}}
        if(pTeams.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u516c\u5f00\u56e2\u961f\uff01",()->openMainMenu(pl));return;}
        java.util.Collections.sort(pTeams,(a,b)->{if(a.getActivity()>0&&b.getActivity()==0)return -1;if(b.getActivity()>0&&a.getActivity()==0)return 1;if(a.getActivity()>0&&b.getActivity()>0)return Long.compare(b.getActivity(),a.getActivity());return Long.compare(b.getFunds(),a.getFunds());});
        SimpleForm.Builder f = SimpleForm.builder().title("\u00a7l\u3010\u6392\u884c\u699c\u3011").content("\u00a7e\u6309\u6210\u957f\u503c\u4e0e\u79ef\u91d1\u7efc\u5408\u6392\u5e8f\n\u00a77\u5171"+pTeams.size()+"\u4e2a\u56e2\u961f");
        for(int i=0;i<pTeams.size();i++){Team t=pTeams.get(i);f.button("\u00a7l"+t.getName()+"\n\u6210\u957f\u503c:"+t.getActivity()+" | \u79ef\u91d1:"+t.getFunds()+" | "+t.getMemberCount()+"\u4eba", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmtpa"));}
        f.button("\u00a7l\u8fd4\u56de", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"));
        f.closedResultHandler(() -> openMainMenu(pl)); f.validResultHandler(r -> {int id=r.clickedButtonId();if(id>=0&&id<pTeams.size()) showApplyConfirm(pl,pIds.get(id),pTeams.get(id));else openMainMenu(pl);});
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f.build());
    }

    // JOIN BY ID
    public static void openJoinByIdInput(Player pl) {
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u641c\u7d22\u56e2\u961f\u3011")
            .label("\u00a7e\u8f93\u51654\u4f4d\u56e2\u961fID\uff1a")
            .input("\u00a77\u56e2\u961fID", "ABCD", "")
            .closedResultHandler(() -> openMainMenu(pl)).validResultHandler(r -> {
                String in=r.asInput(1); if(in==null||in.trim().length()!=4){openAlert(pl,"\u00a7cID\u5fc5\u987b4\u4f4d\uff01",()->openJoinByIdInput(pl));return;}
                String id=in.trim().toUpperCase(); Team t=p().getTeamData().get(id); if(t==null){openAlert(pl,"\u00a7c\u672a\u627e\u5230\u8be5\u56e2\u961f\uff01",()->openJoinByIdInput(pl));return;}
                showApplyConfirm(pl,id,t);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    private static void showApplyConfirm(Player pl, String tid, Team t) {
        SimpleForm f = SimpleForm.builder().title("\u00a7l\u3010\u7533\u8bf7\u52a0\u5165\u3011")
            .content("\u00a7e"+t.getName()+"\n\u00a77ID: "+tid+"\n\u6210\u957f\u503c:"+t.getActivity()+" | \u79ef\u91d1:"+t.getFunds()+cur()+"\n\u4eba\u6570:"+t.getMemberCount()+"\n\n\u00a77\u786e\u5b9a\u8981\u53d1\u9001\u7533\u8bf7\u5417\uff1f")
            .button("\u00a7c\u00a7l\u53d6\u6d88", FormImage.of(FormImage.Type.PATH,"textures/ui/cancel"))
            .button("\u00a72\u00a7l\u786e\u5b9a", FormImage.of(FormImage.Type.PATH,"textures/menu_1/right"))
            .closedResultHandler(() -> openMainMenu(pl)).validResultHandler(r -> {
                if(r.clickedButtonId()==1){UUID u=pl.getUniqueId();for(Map.Entry<String,Team>e:p().getTeamData().getAll().entrySet())e.getValue().getMembersapplications().removeIf(a->a.getUuid().equals(u.toString()));t.getMembersapplications().add(new Team.MemberApplication(u.toString(),pl.getName()));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u53d1\u9001\u7533\u8bf7\uff01");}openMainMenu(pl);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // CREATE TEAM
    public static void openCreateTeamCheck(Player pl) {
        if(p().getConfig2().isEnablePlaytimeCheck()){
            int min=pl.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE)/20/60; int req=p().getConfig2().getPlaytimeRequiredMinutes();
            if(min<req){openAlert(pl,"\u00a7c\u5728\u7ebf\u65f6\u957f\u4e0d\u8db3 ("+min+"/"+req+"\u5206\u949f\uff01",()->openMainMenu(pl));return;}
        }
        openCreateTeamMenu(pl);
    }

    public static void openCreateTeamMenu(Player pl) {
        double bal=p().getEconomy().getBalance(pl.getUniqueId()); long cost=p().getConfig2().getCreateTeamCost();
        CustomForm f = CustomForm.builder().title("\u00a7l\u3010\u521b\u5efa\u56e2\u961f\u3011")
            .label("\u00a7e\u8d39\u7528\uff1a\u00a7c"+cost+" \u00a77"+cur()+"\n\u00a7e\u4f59\u989d\uff1a\u00a7f"+String.format("%.0f",bal)+" \u00a77"+cur())
            .input("\u00a77\u56e2\u961f\u540d\u79f0", "2-10\u4e2a\u5b57\u7b26", "")
            .closedResultHandler(() -> openMainMenu(pl)).validResultHandler(r -> {
                String n=r.asInput(1); if(n==null||n.trim().length()<2||n.trim().length()>10){openAlert(pl,"\u00a7c\u540d\u79f0\u5fc5\u987b2-10\u5b57\uff01",()->openCreateTeamMenu(pl));return;}
                n=n.trim(); if(p().getTeamData().nameExists(n)){openAlert(pl,"\u00a7c\u540d\u79f0\u5df2\u5b58\u5728\uff01",()->openCreateTeamMenu(pl));return;}
                if(p().getEconomy().getBalance(pl.getUniqueId())<cost){openAlert(pl,"\u00a7c\u4f59\u989d\u4e0d\u8db3\uff01",()->openMainMenu(pl));return;}
                if(!p().getEconomy().withdraw(pl.getUniqueId(),cost)){openAlert(pl,"\u00a7c\u6263\u6b3e\u5931\u8d25\uff01",()->openMainMenu(pl));return;}
                String id=Util.generateTeamId(p().getTeamData().getAll().keySet()); if(id==null){openAlert(pl,"\u00a7c\u521b\u5efa\u5931\u8d25\uff01",()->openMainMenu(pl));return;}
                Team t=new Team(n,pl.getUniqueId(),pl.getName()); p().getTeamData().put(id,t); p().getTeamData().save(); pl.sendMessage("\u00a7a\u56e2\u961f "+n+" \u521b\u5efa\u6210\u529f\uff01ID: "+id); openTeamDetail(pl,id);
            }).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // ADMIN
    public static void openAdminTeamList(Player pl) {
        List<String> ids = new ArrayList<>(p().getTeamData().getAll().keySet());
        if(ids.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u56e2\u961f\uff01",()->{});return;}
        java.util.Collections.sort(ids,(a,b)->{Team ta=p().getTeamData().get(a),tb=p().getTeamData().get(b);if(ta.getActivity()>0&&tb.getActivity()==0)return -1;if(tb.getActivity()>0&&ta.getActivity()==0)return 1;if(ta.getActivity()>0&&tb.getActivity()>0)return Long.compare(tb.getActivity(),ta.getActivity());return Long.compare(tb.getFunds(),ta.getFunds());});
        SimpleForm.Builder f = SimpleForm.builder().title("\u00a7l\u3010OP\u7ba1\u7406\u3011").content("\u00a77\u5171"+ids.size()+"\u4e2a\u56e2\u961f\n\u70b9\u51fb\u8fdb\u5165\u7ba1\u7406");
        for(String id:ids){Team t=p().getTeamData().get(id);f.button("\u00a7l"+t.getName()+"\n\u6210\u957f\u503c:"+t.getActivity()+" | \u79ef\u91d1:"+t.getFunds()+" | "+t.getMemberCount()+"\u4eba", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmset"));}
        f.button("\u00a7l\u5173\u95ed", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"));
        f.closedResultHandler(() -> {}); f.validResultHandler(r -> {int id=r.clickedButtonId();if(id>=0&&id<ids.size())openAdminTeamMenu(pl,ids.get(id));});
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f.build());
    }

    public static void openAdminTeamMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); if(t==null){openAlert(pl,"\u00a7c\u4e0d\u5b58\u5728\uff01",()->openAdminTeamList(pl));return;}
        SimpleForm f = SimpleForm.builder().title("\u00a7l[\u00a7r"+t.getName()+"\u00a7r\u00a7l]\u00a7c[OP]")
            .content("\u00a7cOP\u7ba1\u7406\n\u00a7e\u56e2\u961f\uff1a\u00a7f"+t.getName()+"\n\u00a7eID\uff1a\u00a7f"+tid+"\n\u00a7e\u6210\u957f\u503c\uff1a\u00a7f"+t.getActivity())
            .button("\u00a7l\u8be6\u60c5", FormImage.of(FormImage.Type.PATH,"textures/menu_1/thebook"))
            .button("\u00a7l\u4f20\u9001\u951a\u70b9", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmtp"))
            .button("\u00a7l\u4e92\u4f20", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmtpa"))
            .button("\u00a7l\u79ef\u91d1", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmmoney"))
            .button("\u00a7l\u7559\u8a00\u677f", FormImage.of(FormImage.Type.PATH,"textures/menu_1/liuyanban"))
            .button("\u00a7l\u7ba1\u7406", FormImage.of(FormImage.Type.PATH,"textures/menu_1/tmset"))
            .button("\u00a7c\u00a7l\u8fd4\u56de", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"))
            .closedResultHandler(() -> {}).validResultHandler(r -> {switch(r.clickedButtonId()){case 0:openTeamDetail(pl,tid);break;case 1:openWarpMainMenu(pl,tid);break;case 2:openTpaMainMenu(pl,tid);break;case 3:openTeamFundMenu(pl,tid);break;case 4:openMessageBoard(pl,tid);break;case 5:openTeamManageMenu(pl,tid);break;case 6:openAdminTeamList(pl);break;}})
            .build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // NEW MESSAGE ALERT
    public static void openNewMessageAlert(Player pl, String tid, String tn) {
        SimpleForm f = SimpleForm.builder().title("\u00a7l\u3010\u65b0\u7559\u8a00\u63d0\u9192\u3011")
            .content("\u00a7e\u56e2\u961f "+tn+" \u6709\u65b0\u7684\u7559\u8a00\uff01\n\u00a77\u662f\u5426\u73b0\u5728\u67e5\u770b\uff1f")
            .button("\u00a72\u00a7l\u7acb\u5373\u67e5\u770b", FormImage.of(FormImage.Type.PATH,"textures/menu_1/liuyanban"))
            .button("\u00a7c\u00a7l\u7a0d\u540e\u67e5\u770b", FormImage.of(FormImage.Type.PATH,"textures/menu_1/lastpage"))
            .closedResultHandler(() -> {}).validResultHandler(r -> {if(r.clickedButtonId()==0)openMessageBoard(pl,tid);}).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // ALERT
    public static void openAlert(Player pl, String msg, Runnable cb) {
        SimpleForm f = SimpleForm.builder().title("\u00a7l\u3010\u63d0\u793a\u3011").content(msg)
            .button("\u00a72\u00a7l\u786e\u5b9a", FormImage.of(FormImage.Type.PATH,"textures/menu_1/right"))
            .closedResultHandler(() -> {if(cb!=null)cb.run();})
            .validResultHandler(r -> {if(cb!=null)cb.run();}).build();
        FlooodgateApi.getInstance().sendForm(pl.getUniqueId(), f);
    }

    // HELPERS
    private static void teleportToWarp(Player pl, String tid, String wn, Team.WarpPoint wp) {
        World w; switch(wp.getDim()){case -1:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.NETHER).findFirst().orElse(null);break;case 1:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.THE_END).findFirst().orElse(null);break;default:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.NORMAL).findFirst().orElse(null);break;}
        if(w==null){openAlert(pl,"\u00a7c\u7ef4\u5ea6\u5f02\u5e38\uff01",()->openWarpTeleportMenu(pl,tid));return;}
        pl.teleport(new Location(w,wp.getX()+0.5,wp.getY(),wp.getZ()+0.5)); pl.sendMessage("\u00a7a\u5df2\u4f20\u9001\u81f3 "+wn+"\uff01");
    }

    private static void handleQuitTeam(Player pl, String tid) {
        if(p().getTeamData().isTeamOperator(pl.getUniqueId(),tid)){openAlert(pl,"\u00a7c\u7ba1\u7406\u5458\u8bf7\u5148\u964d\u7ea7\u6216\u89e3\u6563\u56e2\u961f\uff01",()->openMainMenu(pl));}
        else openQuitConfirmMenu(pl,tid);
    }
}
'@

Write-Host "Part 3: BedrockForms.java created"
Write-Host "Now generating JavaMenus.java and MGTeamPlugin.java..."
