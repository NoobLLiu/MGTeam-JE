# MGTeam Part 4: JavaMenus.java and MGTeamPlugin.java

$ROOT = "D:\codex 2\mc-server\mc-server\local-plugins\mgteam"
$SRC = "$ROOT\src\cn\gmzc\mgteam"
function Write-JavaFile { param($Path, $Content) $fullPath = "$SRC\$Path"; [System.IO.File]::WriteAllText($fullPath, $Content, [System.Text.UTF8Encoding]::new($false)); Write-Host "  $Path" }

# First fix FloodgateApi typo in BedrockForms.java
$bfPath = "$SRC\gui\BedrockForms.java"
$bfContent = [System.IO.File]::ReadAllText($bfPath, [System.Text.UTF8Encoding]::UTF8)
$bfContent = $bfContent -replace 'FlooodgateApi', 'FloodgateApi'
$bfContent = $bfContent -replace 'import org.geysermc.floodgate.api.FlooodgateApi;', 'import org.geysermc.floodgate.api.FloodgateApi;'
[System.IO.File]::WriteAllText($bfPath, $bfContent, [System.Text.UTF8Encoding]::new($false))
Write-Host "  Fixed FloodgateApi typo in BedrockForms.java"

# ==================== JAVA MENUS ====================

Write-JavaFile "gui\JavaMenus.java" @'
package cn.gmzc.mgteam.gui;

import cn.gmzc.mgteam.MGTeamPlugin;
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
    static Map<UUID,PendingChatInput> pendingInputs = new HashMap<>();

    public static void handleChatInput(Player pl, String msg) {
        PendingChatInput pi = pendingInputs.remove(pl.getUniqueId());
        if (pi != null) pi.cb.accept(msg);
    }

    private static void requestInput(Player pl, String title, String prompt, Consumer<String> cb) {
        pl.closeInventory();
        pl.sendMessage("\u00a7e\u00a7l" + title);
        pl.sendMessage("\u00a77" + prompt);
        pl.sendMessage("\u00a7e\u8f93\u5165 cancel \u53d6\u6d88\uff1a");
        pendingInputs.put(pl.getUniqueId(), new PendingChatInput(cb));
    }

    private static void requestIC(Player pl, String title, String prompt, Consumer<String> cb) {
        requestInput(pl,title,prompt,in -> { if(!in.equalsIgnoreCase("cancel")) cb.accept(in); else pl.sendMessage("\u00a77\u5df2\u53d6\u6d88"); });
    }

    private static ItemStack item(Material m, String n, String... lore) {
        ItemStack i = new ItemStack(m); ItemMeta meta = i.getItemMeta(); meta.setDisplayName(n);
        if(lore.length>0){List<String> l=new ArrayList<>();for(String s:lore)l.add(s);meta.setLore(l);}
        i.setItemMeta(meta); return i;
    }

    private static ItemStack skull(String owner, String display, String... lore) {
        ItemStack i = new ItemStack(Material.PLAYER_HEAD); SkullMeta m=(SkullMeta)i.getItemMeta(); m.setDisplayName(display); p().getPlayerSkinService().applyByName(m, owner);
        if(lore.length>0){List<String> l=new ArrayList<>();for(String s:lore)l.add(s);m.setLore(l);}
        i.setItemMeta(m); return i;
    }

    // MAIN MENU
    public static void openMainMenu(Player pl) {
        String tid = p().getTeamData().getPlayerTeamId(pl.getUniqueId());
        if (tid != null) {
            Team t = p().getTeamData().get(tid); boolean isOp = p().getTeamData().isTeamOperator(pl.getUniqueId(), tid);
            int online=0; for(Player op:Bukkit.getOnlinePlayers()){if(!op.getUniqueId().equals(pl.getUniqueId())&&tid.equals(p().getTeamData().getPlayerTeamId(op.getUniqueId())))online++;}
            Inventory inv = Bukkit.createInventory(null, isOp?18:9, "\u00a7l"+t.getName());
            inv.setItem(0, item(Material.BOOK, "\u00a7l\u8be6\u60c5\u4fe1\u606f","\u00a7r\u00a7t\u67e5\u770b\u56e2\u961f\u8be6\u60c5"));
            inv.setItem(1, item(Material.COMPASS, "\u00a7l\u4f20\u9001\u951a\u70b9","\u00a7r\u00a7t\u56e2\u961f\u5171\u4eab\u4f20\u9001\u70b9"));
            inv.setItem(2, item(Material.ENDER_PEARL, online>0?"\u00a7l\u4e92\u4f20 \u00a72"+online:"\u00a7l\u4e92\u4f20","\u00a7r\u00a7t\u514d\u540c\u610f\u4f20\u9001"));
            inv.setItem(3, item(Material.GOLD_NUGGET, "\u00a7l\u79ef\u91d1","\u00a7r\u00a7t"+t.getFunds()+cur()));
            if(p().getMessageData().hasNewMessages(pl.getUniqueId(),tid)) inv.setItem(4, item(Material.WRITABLE_BOOK, "\u00a7l\u7559\u8a00\u677f","\u00a7r\u00a72\u6709\u65b0\u7559\u8a00"));
            else inv.setItem(4, item(Material.WRITABLE_BOOK, "\u00a7l\u7559\u8a00\u677f","\u00a7r\u00a7t\u67e5\u770b\u53d1\u5e03\u7559\u8a00"));
            if(isOp) { inv.setItem(6, item(Material.REPEATER, "\u00a7l\u7ba1\u7406\u56e2\u961f","\u00a7r\u00a7t\u7ba1\u7406\u5458\u83dc\u5355")); inv.setItem(8, item(Material.BARRIER, "\u00a7c\u00a7l\u9000\u51fa\u56e2\u961f")); }
            else inv.setItem(8, item(Material.BARRIER, "\u00a7c\u00a7l\u9000\u51fa\u56e2\u961f"));
            pl.openInventory(inv);
            p().setGuiContext(pl.getUniqueId(), id -> {
                if(isOp) { switch(id){case 0:openTeamDetail(pl,tid);break;case 1:openWarpMainMenu(pl,tid);break;case 2:openTpaMainMenu(pl,tid);break;case 3:openTeamFundMenu(pl,tid);break;case 4:openMessageBoard(pl,tid);break;case 6:openTeamManageMenu(pl,tid);break;case 8:handleQuitTeam(pl,tid);break;} }
                else { switch(id){case 0:openTeamDetail(pl,tid);break;case 1:openWarpMainMenu(pl,tid);break;case 2:openTpaMainMenu(pl,tid);break;case 3:openTeamFundMenu(pl,tid);break;case 4:openMessageBoard(pl,tid);break;case 8:handleQuitTeam(pl,tid);break;} }
            });
        } else {
            Inventory inv = Bukkit.createInventory(null, 9, "\u00a7l\u56e2\u961f\u7cfb\u7edf");
            inv.setItem(2, item(Material.LIME_WOOL, "\u00a7l\u521b\u5efa\u65b0\u56e2\u961f"));
            inv.setItem(4, item(Material.COMPASS, "\u00a7l\u641c\u7d22\u56e2\u961f"));
            inv.setItem(6, item(Material.HOPPER, "\u00a7l\u6392\u884c\u699c"));
            pl.openInventory(inv);
            p().setGuiContext(pl.getUniqueId(), id -> { switch(id){case 2:openCreateTeamCheck(pl);break;case 4:openJoinByIdInput(pl);break;case 6:openTeamRankingMenu(pl);break;} });
        }
    }

    // TEAM DETAIL
    public static void openTeamDetail(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); if(t==null)return;
        boolean isOp = p().getTeamData().isTeamOperator(pl.getUniqueId(), tid);
        Inventory inv = Bukkit.createInventory(null, 27, "\u00a7l\u8be6\u60c5");
        inv.setItem(13, item(Material.FILLED_MAP, "\u00a7l"+t.getName(),
            "\u00a77ID: "+tid, (isOp?"\u00a7c\u7ba1\u7406\u5458":"\u00a7a\u6210\u5458"),
            "\u00a7e\u6210\u957f\u503c: \u00a7f"+t.getActivity(), "\u00a7e\u79ef\u91d1: \u00a7f"+t.getFunds()+cur()));
        int slot=0; inv.setItem(18, item(Material.REDSTONE_BLOCK, "\u00a7l\u7ba1\u7406\u5458"));
        for(Team.MemberEntry m:t.getOperators()){if(slot<9)inv.setItem(slot++,skull(m.getName()!=null?m.getName():"Steve","\u00a7c"+Util.plainName(m.getName())));}
        slot=9; inv.setItem(19, item(Material.EMERALD_BLOCK, "\u00a7l\u6210\u5458"));
        for(Team.MemberEntry m:t.getMembers()){if(slot<18)inv.setItem(slot++,skull(m.getName()!=null?m.getName():"Steve","\u00a7a"+Util.plainName(m.getName())));}
        inv.setItem(22, item(Material.ARROW, "\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv); p().setGuiContext(pl.getUniqueId(), s->{if(s==22)openMainMenu(pl);});
    }

    // WARP
    public static void openWarpMainMenu(Player pl, String tid) {
        Inventory inv = Bukkit.createInventory(null, 9, "\u00a7l\u4f20\u9001\u951a\u70b9");
        inv.setItem(1, item(Material.ENDER_PEARL, "\u00a7e\u00a7l\u524d\u5f80\u4f20\u9001\u70b9"));
        inv.setItem(3, item(Material.LIME_WOOL, "\u00a72\u00a7l\u6dfb\u52a0\u4f20\u9001\u70b9"));
        inv.setItem(5, item(Material.RED_WOOL, "\u00a7c\u00a7l\u79fb\u9664\u4f20\u9001\u70b9"));
        inv.setItem(8, item(Material.ARROW, "\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv); p().setGuiContext(pl.getUniqueId(), id->{switch(id){case 1:openWarpTeleportMenu(pl,tid);break;case 3:openAddWarpMenu(pl,tid);break;case 5:openRemoveWarpMenu(pl,tid);break;case 8:openMainMenu(pl);break;}});
    }

    public static void openWarpTeleportMenu(Player pl, String tid) {
        Team t = p().getTeamData().get(tid); Map<String,Team.WarpPoint> warps = t.getWarpPoints();
        if(warps.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u4f20\u9001\u70b9",()->openWarpMainMenu(pl,tid));return;}
        int slots=Math.min(45,warps.size()); int rows=(slots/9)+1; int size=Math.min(54,(rows+1)*9);
        Inventory inv = Bukkit.createInventory(null, size, "\u00a7l\u4f20\u9001"); int idx=0;
        for(Map.Entry<String,Team.WarpPoint> e:warps.entrySet()){if(idx>=45)break;Team.WarpPoint wp=e.getValue();inv.setItem(idx++,item(Material.COMPASS,"\u00a7l"+e.getKey(),Util.dimName(wp.getDim())+" | "+Util.plainName(wp.getCreatorName())));}
        int bs=size-9; inv.setItem(bs, item(Material.ARROW, "\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv); List<String> names=new ArrayList<>(warps.keySet());
        p().setGuiContext(pl.getUniqueId(), id->{if(id==bs){openWarpMainMenu(pl,tid);return;}if(id>=0&&id<names.size())teleportToWarp(pl,tid,names.get(id),warps.get(names.get(id)));});
    }

    public static void openAddWarpMenu(Player pl, String tid) {
        requestIC(pl,"\u6dfb\u52a0\u4f20\u9001\u70b9","\u8f93\u5165\u540d\u79f0(1-10\u5b57):", name->{
            if(name.length()<1||name.length()>10){openAlert(pl,"\u00a7c\u5fc5\u987b1-10\u5b57",()->openAddWarpMenu(pl,tid));return;}
            Team t=p().getTeamData().get(tid); if(t.getWarpPoints().containsKey(name)){openAlert(pl,"\u00a7c\u540d\u79f0\u5df2\u5b58\u5728",()->openAddWarpMenu(pl,tid));return;}
            Location loc=pl.getLocation(); Team.WarpPoint wp=new Team.WarpPoint(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(),Util.dimId(loc.getWorld()),pl.getUniqueId().toString(),pl.getName());
            t.getWarpPoints().put(name,wp); p().getTeamData().save(); pl.sendMessage("\u00a7a\u521b\u5efa\u6210\u529f\uff01"); openWarpMainMenu(pl,tid);
        });
    }

    public static void openRemoveWarpMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); boolean isOp=p().getTeamData().isTeamOperator(pl.getUniqueId(),tid);
        Map<String,Team.WarpPoint> warps=t.getWarpPoints(); List<String> rm=new ArrayList<>();
        for(Map.Entry<String,Team.WarpPoint> e:warps.entrySet()){if(isOp||pl.getUniqueId().toString().equals(e.getValue().getCreatorUuid()))rm.add(e.getKey());}
        if(rm.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u53ef\u79fb\u9664\u7684",()->openWarpMainMenu(pl,tid));return;}
        int slots=Math.min(45,rm.size()); int rows=(slots/9)+1; int size=Math.min(54,(rows+1)*9);
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7l\u79fb\u9664\u4f20\u9001\u70b9"); int idx=0;
        for(String wn:rm){if(idx>=45)break;Team.WarpPoint wp=warps.get(wn);inv.setItem(idx++,item(Material.TNT,"\u00a7c\u00a7l"+wn,Util.dimName(wp.getDim())+" | "+Util.plainName(wp.getCreatorName())));}
        int bs=size-9; inv.setItem(bs,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv); p().setGuiContext(pl.getUniqueId(),id->{if(id==bs){openWarpMainMenu(pl,tid);return;}if(id>=0&&id<rm.size()){warps.remove(rm.get(id));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u5220\u9664");openRemoveWarpMenu(pl,tid);}});
    }

    // TPA
    public static void openTpaMainMenu(Player pl, String tid) {
        List<Player> mates=new ArrayList<>();
        for(Player op:Bukkit.getOnlinePlayers()){if(!op.getUniqueId().equals(pl.getUniqueId())&&tid.equals(p().getTeamData().getPlayerTeamId(op.getUniqueId())))mates.add(op);}
        if(mates.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u5728\u7ebf\u961f\u53cb",()->{});return;}
        int slots=Math.min(45,mates.size()); int rows=(slots/9)+1; int size=Math.min(54,(rows+1)*9);
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7l\u4e92\u4f20"); int idx=0;
        for(Player m:mates){String l2;if(m.getWorld().equals(pl.getWorld())){double d=pl.getLocation().distance(m.getLocation());l2="\u8ddd\u79bb:"+String.format("%.1f",d)+"\u7c73 | "+m.getLocation().getBlockX();}else l2="\u00a77"+m.getWorld().getName();inv.setItem(idx++,skull(m.getName(),"\u00a7l"+m.getName(),l2));}
        int bs=size-9; inv.setItem(bs,item(Material.ARROW,"\u00a7l\u53d6\u6d88"));
        pl.openInventory(inv); p().setGuiContext(pl.getUniqueId(),id->{if(id==bs)return;if(id>=0&&id<mates.size()){Player t=mates.get(id);if(!tid.equals(p().getTeamData().getPlayerTeamId(t.getUniqueId()))){openAlert(pl,"\u00a7c\u5df2\u79bb\u5f00\u56e2\u961f",()->{});return;}pl.teleport(t.getLocation());pl.sendMessage("\u00a7a\u5df2\u4f20\u9001\u81f3 "+t.getName());t.sendMessage("\u00a7e"+pl.getName()+"\u4f20\u9001\u5230\u4e86\u4f60\u8eab\u8fb9");}});
    }

    // FUND
    public static void openTeamFundMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); boolean en=p().getFundConsume().getStatus(pl.getUniqueId());
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l\u79ef\u91d1");
        inv.setItem(0,item(Material.GREEN_WOOL,"\u00a72\u00a7l\u5b58\u5165")); inv.setItem(1,item(Material.RED_WOOL,"\u00a7c\u00a7l\u53d6\u51fa"));
        inv.setItem(2,item(Material.COMPARATOR,"\u00a7l\u6d88\u8d39:"+(en?"\u00a72\u5f00":"\u00a7c\u5173")));
        inv.setItem(8,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv); p().setGuiContext(pl.getUniqueId(),id->{switch(id){case 0:openDepositMenu(pl,tid);break;case 1:openWithdrawMenu(pl,tid);break;case 2:boolean ns=!p().getFundConsume().getStatus(pl.getUniqueId());p().getFundConsume().setStatus(pl.getUniqueId(),ns);p().getFundConsume().save();pl.sendMessage(ns?"\u00a7a\u5f00\u542f":"\u00a7a\u5173\u95ed");openTeamFundMenu(pl,tid);break;case 8:openMainMenu(pl);break;}});
    }

    public static void openDepositMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); double bal=p().getEconomy().getBalance(pl.getUniqueId());
        pl.sendMessage("\u00a7e\u4f59\u989d:"+String.format("%.0f",bal)+cur()+" | \u56e2\u961f:"+t.getFunds()+cur());
        requestIC(pl,"\u5b58\u5165","\u8f93\u5165\u91d1\u989d:",in->{
            String err=Util.validateAmount(in); if(err!=null){openAlert(pl,"\u00a7c"+err,()->openDepositMenu(pl,tid));return;}
            long amt=Util.parseAmount(in); if(!p().getEconomy().withdraw(pl.getUniqueId(),amt)){openAlert(pl,"\u00a7c\u4f59\u989d\u4e0d\u8db3",()->openDepositMenu(pl,tid));return;}
            long bb=t.getFunds();t.setFunds(bb+amt);p().getTeamData().save();p().getFundLog().addLog(tid,amt,"\u5b58\u5165 "+pl.getName(),bb,t.getFunds());p().getFundLog().save();pl.sendMessage("\u00a7a\u6210\u529f\u5b58\u5165 "+amt+cur());openTeamFundMenu(pl,tid);
        });
    }

    public static void openWithdrawMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); pl.sendMessage("\u00a7e\u56e2\u961f\u8d44\u91d1:"+t.getFunds()+cur());
        requestIC(pl,"\u53d6\u51fa","\u8f93\u5165\u91d1\u989d:",in->{
            String err=Util.validateAmount(in);if(err!=null){openAlert(pl,"\u00a7c"+err,()->openWithdrawMenu(pl,tid));return;}
            long amt=Util.parseAmount(in);if(t.getFunds()<amt){openAlert(pl,"\u00a7c\u4e0d\u8db3",()->openWithdrawMenu(pl,tid));return;}
            long bb=t.getFunds();t.setFunds(bb-amt);if(!p().getEconomy().deposit(pl.getUniqueId(),amt)){t.setFunds(bb);openAlert(pl,"\u00a7c\u5931\u8d25",()->openWithdrawMenu(pl,tid));return;}
            p().getTeamData().save();p().getFundLog().addLog(tid,-amt,"\u53d6\u51fa "+pl.getName(),bb,t.getFunds());p().getFundLog().save();pl.sendMessage("\u00a7a\u6210\u529f\u53d6\u51fa "+amt+cur());openTeamFundMenu(pl,tid);
        });
    }

    public static void openFundLogMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); List<FundLogEntry> logs=p().getFundLog().getLogs(tid);
        int limit=p().getConfig2().getFundLogDisplayLimit(); List<FundLogEntry> display=logs.size()>limit?logs.subList(0,limit):logs;
        int slots=Math.min(45,display.size());int rows=(slots/9)+1;int size=Math.min(54,(rows+1)*9);if(size<9)size=9;
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7l\u6d41\u6c34"); SimpleDateFormat sdf=new SimpleDateFormat("MM/dd HH:mm"); int idx=0;
        for(FundLogEntry e:display){if(idx>=45)break;String ts=sdf.format(new Date(e.getTimestamp()));String ch=e.getChange()>=0?"\u00a7a+"+e.getChange():"\u00a7c"+e.getChange();inv.setItem(idx++,item(Material.PAPER,ts+" ("+ch+"\u00a7r)",e.getBalanceBefore()+"\u2192"+e.getBalanceAfter(),e.getReason()));}
        int bs=size-9;inv.setItem(bs,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));pl.openInventory(inv);p().setGuiContext(pl.getUniqueId(),id->{openTeamManageMenu(pl,tid);});
    }

    // MESSAGE BOARD
    public static void openMessageBoard(Player pl, String tid) {
        p().getMessageData().setLastViewTime(pl.getUniqueId(),tid); List<MessageEntry> msgs=p().getMessageData().getMessages(tid);
        int show=Math.min(10,msgs.size()); int size=((show+2)/9+1)*9;if(size<9)size=9;if(size>54)size=54;
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7l\u7559\u8a00\u677f"); int idx=0;
        for(int i=0;i<show;i++){MessageEntry m=msgs.get(i);inv.setItem(idx++,item(Material.PAPER,"\u00a7e"+m.getSenderName()+" \u00a77("+Util.timeAgo(m.getTime())+")","\u00a7f"+m.getContent()));}
        if(show==0)inv.setItem(0,item(Material.PAPER,"\u00a77\u6682\u65e0\u7559\u8a00"));
        int addS=(size/9-1)*9; inv.setItem(addS,item(Material.WRITABLE_BOOK,"\u00a72\u00a7l\u6dfb\u52a0\u7559\u8a00")); inv.setItem(addS+1,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv); p().setGuiContext(pl.getUniqueId(),id->{if(id==addS)openAddMessageMenu(pl,tid);else if(id==addS+1)openMainMenu(pl);});
    }

    public static void openAddMessageMenu(Player pl, String tid) {
        long cd=p().getMessageCooldowns().getOrDefault(tid+"_"+pl.getUniqueId(),0L); int cdSec=p().getConfig2().getMessageCooldownSeconds();
        if(System.currentTimeMillis()-cd<cdSec*1000L){long r=(cdSec*1000L-(System.currentTimeMillis()-cd))/1000;openAlert(pl,"\u00a7c\u51b7\u5374\u4e2d "+r/60+"\u5206"+r%60+"\u79d2",()->openMessageBoard(pl,tid));return;}
        requestIC(pl,"\u6dfb\u52a0\u7559\u8a00","\u8f93\u5165\u5185\u5bb9(\u6700100\u5b57):",c->{
            if(c.isEmpty()){openAlert(pl,"\u00a7c\u4e0d\u80fd\u4e3a\u7a7a",()->openAddMessageMenu(pl,tid));return;}
            if(c.length()>100){openAlert(pl,"\u00a7c\u8fc7\u957f",()->openAddMessageMenu(pl,tid));return;}
            p().getMessageData().addMessage(tid,new MessageEntry(pl.getUniqueId().toString(),pl.getName(),c));p().getMessageData().save();p().getMessageCooldowns().put(tid+"_"+pl.getUniqueId(),System.currentTimeMillis());pl.sendMessage("\u00a7a\u53d1\u5e03\u6210\u529f");openMessageBoard(pl,tid);
        });
    }

    // TEAM MANAGE
    public static void openTeamManageMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); int apps=t.getMembersapplications().size();
        Inventory inv=Bukkit.createInventory(null,27,"\u00a7l\u7ba1\u7406");
        inv.setItem(0,item(Material.PLAYER_HEAD,"\u00a7l\u7ba1\u7406\u6210\u5458")); inv.setItem(1,item(Material.PAPER,apps>0?"\u00a7l\u7533\u8bf7 \u00a7c"+apps:"\u00a7l\u7533\u8bf7 \u00a77\u6682\u65e0"));
        inv.setItem(2,item(Material.BOOK,"\u00a7l\u516c\u544a")); inv.setItem(3,item(Material.GOLD_NUGGET,"\u00a7l\u6d41\u6c34"));
        inv.setItem(4,item(Material.NAME_TAG,"\u00a7l\u4fee\u6539\u540d\u79f0","\u00a7r"+t.getName()));
        inv.setItem(5,item(t.isPublic()?Material.LIME_WOOL:Material.RED_WOOL,"\u00a7l\u72b6\u6001","\u00a7r"+(t.isPublic()?"\u00a72\u516c\u5f00":"\u00a7c\u79c1\u5bc6")));
        inv.setItem(8,item(Material.TNT,"\u00a7c\u00a7l\u89e3\u6563")); inv.setItem(26,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv); p().setGuiContext(pl.getUniqueId(),id->{
            switch(id){case 0:openManageMembers(pl,tid);break;case 1:openManageApplications(pl,tid);break;case 2:openNoticeEditMenu(pl,tid);break;case 3:openFundLogMenu(pl,tid);break;case 4:openRenameTeamMenu(pl,tid);break;
            case 5:t.setPublic(!t.isPublic());p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u66f4\u6539");openTeamManageMenu(pl,tid);break;case 8:openDisbandConfirmMenu(pl,tid);break;case 26:openMainMenu(pl);break;}
        });
    }

    // MANAGE MEMBERS
    public static void openManageMembers(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); List<Team.MemberEntry> all=new ArrayList<>(); all.addAll(t.getOperators());all.addAll(t.getMembers());
        if(all.isEmpty()){openAlert(pl,"\u00a7c\u4e3a\u7a7a",()->openTeamManageMenu(pl,tid));return;}
        int slots=Math.min(45,all.size());int rows=(slots/9)+1;int size=Math.min(54,(rows+1)*9);if(size<9)size=9;
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7l\u6210\u5458"); int idx=0;
        for(Team.MemberEntry m:t.getOperators())inv.setItem(idx++,skull(m.getName()!=null?m.getName():"Steve","\u00a7c"+Util.plainName(m.getName())));
        for(Team.MemberEntry m:t.getMembers())inv.setItem(idx++,skull(m.getName()!=null?m.getName():"Steve","\u00a7a"+Util.plainName(m.getName())));
        int bs=size-9;inv.setItem(bs,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv); p().setGuiContext(pl.getUniqueId(),id->{if(id==bs){openTeamManageMenu(pl,tid);return;}if(id>=0&&id<all.size())openMemberAction(pl,tid,all.get(id));});
    }

    private static void openMemberAction(Player pl, String tid, Team.MemberEntry member) {
        Team t=p().getTeamData().get(tid); boolean isOp=t.getOperators().stream().anyMatch(m->m.getUuid().equals(member.getUuid()));
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l"+Util.plainName(member.getName()));
        inv.setItem(1,item(Material.GREEN_WOOL,"\u00a7a\u00a7l\u8bbe\u4e3a\u7ba1\u7406\u5458")); inv.setItem(3,item(Material.YELLOW_WOOL,"\u00a7e\u00a7l\u8bbe\u4e3a\u6210\u5458"));
        inv.setItem(5,item(Material.RED_WOOL,"\u00a7c\u00a7l\u79fb\u51fa")); inv.setItem(8,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv); p().setGuiContext(pl.getUniqueId(), id->{
            if(id==8){openManageMembers(pl,tid);return;}
            if(id==1&&isOp){openAlert(pl,"\u00a7c\u5df2\u662f\u7ba1\u7406\u5458",()->openManageMembers(pl,tid));return;}
            if(id==3&&!isOp){openAlert(pl,"\u00a7c\u5df2\u662f\u6210\u5458",()->openManageMembers(pl,tid));return;}
            if(member.getUuid().equals(pl.getUniqueId().toString())&&id==5){openAlert(pl,"\u00a7c\u5148\u964d\u7ea7",()->openManageMembers(pl,tid));return;}
            if(isOp&&(id==3||id==5)&&t.getOperators().size()<=1){openAlert(pl,"\u00a7c\u81f3\u5c11\u4fdd\u7559\u4e00\u4e2a\u7ba1\u7406\u5458",()->openManageMembers(pl,tid));return;}
            boolean self=member.getUuid().equals(pl.getUniqueId().toString())&&id==3;
            if(id==1){t.getMembers().removeIf(m->m.getUuid().equals(member.getUuid()));t.getOperators().add(new Team.MemberEntry(member.getUuid(),member.getName()));pl.sendMessage("\u00a7a\u5df2\u8bbe\u4e3a\u7ba1\u7406\u5458");}
            else if(id==3){t.getOperators().removeIf(m->m.getUuid().equals(member.getUuid()));t.getMembers().add(new Team.MemberEntry(member.getUuid(),member.getName()));pl.sendMessage("\u00a7a\u5df2\u8bbe\u4e3a\u6210\u5458");}
            else if(id==5){if(isOp)t.getOperators().removeIf(m->m.getUuid().equals(member.getUuid()));else t.getMembers().removeIf(m->m.getUuid().equals(member.getUuid()));p().getFundConsume().remove(member.getUniqueId());Player tp=Bukkit.getPlayer(member.getUniqueId());if(tp!=null)tp.sendMessage("\u00a7c\u4f60\u5df2\u88ab\u79fb\u51fa "+t.getName());pl.sendMessage("\u00a7a\u5df2\u79fb\u51fa");}
            p().getTeamData().save();p().getFundConsume().save();if(self)openTeamDetail(pl,tid);else openManageMembers(pl,tid);
        });
    }

    // MANAGE APPLICATIONS
    public static void openManageApplications(Player pl, String tid) {
        Team t=p().getTeamData().get(tid); List<Team.MemberApplication> apps=t.getMembersapplications();
        if(apps.isEmpty()){openAlert(pl,"\u00a77\u6682\u65e0",()->openTeamManageMenu(pl,tid));return;}
        int slots=Math.min(45,apps.size());int rows=(slots/9)+1;int size=Math.min(54,(rows+1)*9);if(size<9)size=9;
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7l\u7533\u8bf7");int idx=0;
        for(Team.MemberApplication a:apps)inv.setItem(idx++,skull(a.getName()!=null?a.getName():"Steve","\u00a7e"+Util.plainName(a.getName()),"\u00a77"+a.getAppliedAt()));
        int bs=size-9;inv.setItem(bs,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv);p().setGuiContext(pl.getUniqueId(),id->{if(id==bs){openTeamManageMenu(pl,tid);return;}if(id>=0&&id<apps.size())openAppAction(pl,tid,apps.get(id));});
    }

    private static void openAppAction(Player pl, String tid, Team.MemberApplication app) {
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l"+Util.plainName(app.getName()));
        inv.setItem(2,item(Material.RED_WOOL,"\u00a7c\u00a7l\u5ffd\u7565")); inv.setItem(4,item(Material.LIME_WOOL,"\u00a72\u00a7l\u901a\u8fc7")); inv.setItem(8,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv);p().setGuiContext(pl.getUniqueId(),id->{
            if(id==8){openManageApplications(pl,tid);return;}
            Team t=p().getTeamData().get(tid);if(t==null)return;
            if(id==2){t.getMembersapplications().removeIf(a->a.getUuid().equals(app.getUuid()));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u5ffd\u7565");openManageApplications(pl,tid);}
            else if(id==4){if(p().getTeamData().isPlayerInTeam(app.getUniqueId())){pl.sendMessage("\u00a7c\u5df2\u5728\u5176\u4ed6\u56e2\u961f");t.getMembersapplications().removeIf(a->a.getUuid().equals(app.getUuid()));p().getTeamData().save();openManageApplications(pl,tid);return;}
            t.getMembers().add(new Team.MemberEntry(app.getUuid(),app.getName()));t.getMembersapplications().removeIf(a->a.getUuid().equals(app.getUuid()));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u901a\u8fc7");Player tp=Bukkit.getPlayer(app.getUniqueId());if(tp!=null)tp.sendMessage("\u00a7a\u6b22\u8fce\u52a0\u5165 "+t.getName());openManageApplications(pl,tid);}
        });
    }

    // NOTICE / RENAME / DISBAND / QUIT
    public static void openNoticeEditMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);
        requestIC(pl,"\u516c\u544a","\u8f93\u5165\u5185\u5bb9(\u6700100\u5b57,"+t.getNotice()+"):",n->{
            if(n.length()>100){openAlert(pl,"\u00a7c\u8fc7\u957f",()->openNoticeEditMenu(pl,tid));return;}
            t.setNotice(n);p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u66f4\u65b0");openMainMenu(pl);
        });
    }

    public static void openRenameTeamMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);
        requestIC(pl,"\u4fee\u6539\u540d\u79f0","\u8f93\u5165\u65b0\u540d\u79f0(2-10\u5b57,"+t.getName()+"):",nn->{
            if(nn.length()<2||nn.length()>10){openAlert(pl,"\u00a7c\u5fc5\u987b2-10\u5b57",()->openRenameTeamMenu(pl,tid));return;}
            if(p().getTeamData().nameExists(nn)){openAlert(pl,"\u00a7c\u5df2\u5b58\u5728",()->openRenameTeamMenu(pl,tid));return;}
            String old=t.getName();t.setName(nn);p().getTeamData().save();pl.sendMessage("\u00a7a"+old+"\u2192"+nn);openMainMenu(pl);
        });
    }

    public static void openDisbandConfirmMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);
        requestIC(pl,"\u00a7c\u89e3\u6563","\u8f93\u5165\u56e2\u961f\u540d\u79f0\u786e\u8ba4("+t.getName()+"):",in->{
            if(!in.equals(t.getName())){openAlert(pl,"\u00a7c\u540d\u79f0\u9519\u8bef",()->openDisbandConfirmMenu(pl,tid));return;}
            for(Team.MemberEntry m:t.getOperators())p().getFundConsume().remove(m.getUniqueId());for(Team.MemberEntry m:t.getMembers())p().getFundConsume().remove(m.getUniqueId());
            String tn=t.getName();p().getTeamData().remove(tid);p().getMessageData().deleteTeamMessages(tid);p().getFundLog().deleteTeamLogs(tid);p().getTeamData().save();p().getMessageData().save();p().getFundConsume().save();p().getFundLog().save();
            pl.sendMessage("\u00a7a\u56e2\u961f "+tn+" \u5df2\u89e3\u6563");openMainMenu(pl);
        });
    }

    public static void openQuitConfirmMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);
        requestIC(pl,"\u9000\u51fa","\u8f93\u5165 yes \u786e\u8ba4:",in->{
            if(!in.equalsIgnoreCase("yes")){openAlert(pl,"\u00a7c\u8f93\u5165\u9519\u8bef",()->openQuitConfirmMenu(pl,tid));return;}
            t.getMembers().removeIf(m->m.getUuid().equals(pl.getUniqueId().toString()));p().getFundConsume().remove(pl.getUniqueId());p().getTeamData().save();p().getFundConsume().save();
            pl.sendMessage("\u00a7a\u5df2\u9000\u51fa");openMainMenu(pl);
        });
    }

    // RANKING
    public static void openTeamRankingMenu(Player pl) {
        List<String> pIds=new ArrayList<>(); List<Team> pTeams=new ArrayList<>();
        for(Map.Entry<String,Team> e:p().getTeamData().getAll().entrySet()){if(e.getValue().isPublic()){pIds.add(e.getKey());pTeams.add(e.getValue());}}
        if(pTeams.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u516c\u5f00\u56e2\u961f",()->openMainMenu(pl));return;}
        java.util.Collections.sort(pTeams,(a,b)->{if(a.getActivity()>0&&b.getActivity()==0)return-1;if(b.getActivity()>0&&a.getActivity()==0)return 1;if(a.getActivity()>0&&b.getActivity()>0)return Long.compare(b.getActivity(),a.getActivity());return Long.compare(b.getFunds(),a.getFunds());});
        int slots=Math.min(45,pTeams.size());int rows=(slots/9)+1;int size=Math.min(54,(rows+1)*9);if(size<9)size=9;
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7l\u6392\u884c\u699c");int idx=0;
        for(int i=0;i<pTeams.size()&&idx<45;i++){Team t=pTeams.get(i);inv.setItem(idx++,skull(t.getName(),"\u00a7l"+t.getName(),"\u6210\u957f\u503c:"+t.getActivity()+" | \u79ef\u91d1:"+t.getFunds()+" | "+t.getMemberCount()+"\u4eba"));}
        int bs=size-9;inv.setItem(bs,item(Material.ARROW,"\u00a7l\u8fd4\u56de"));
        pl.openInventory(inv);p().setGuiContext(pl.getUniqueId(),id->{if(id==bs){openMainMenu(pl);return;}if(id>=0&&id<pIds.size())showApplyConfirm(pl,pIds.get(id),pTeams.get(id));});
    }

    // JOIN BY ID
    public static void openJoinByIdInput(Player pl) {
        requestIC(pl,"\u641c\u7d22","\u8f93\u51654\u4f4d\u56e2\u961fID:",in->{
            if(in.length()!=4){openAlert(pl,"\u00a7cID\u5fc5\u987b4\u4f4d",()->openJoinByIdInput(pl));return;}
            Team t=p().getTeamData().get(in.toUpperCase());if(t==null){openAlert(pl,"\u00a7c\u672a\u627e\u5230",()->openJoinByIdInput(pl));return;}
            showApplyConfirm(pl,in.toUpperCase(),t);
        });
    }

    private static void showApplyConfirm(Player pl, String tid, Team t) {
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l\u7533\u8bf7\u52a0\u5165");
        inv.setItem(2,item(Material.PAPER,"\u00a7e"+t.getName(),"\u00a77"+tid,"\u6210\u957f\u503c:"+t.getActivity(),"\u79ef\u91d1:"+t.getFunds()+cur(),"\u4eba\u6570:"+t.getMemberCount()));
        inv.setItem(4,item(Material.LIME_WOOL,"\u00a72\u00a7l\u786e\u5b9a\u7533\u8bf7"));inv.setItem(8,item(Material.ARROW,"\u00a7c\u00a7l\u53d6\u6d88"));
        pl.openInventory(inv);p().setGuiContext(pl.getUniqueId(),id->{if(id==4){UUID u=pl.getUniqueId();for(Map.Entry<String,Team> e:p().getTeamData().getAll().entrySet())e.getValue().getMembersapplications().removeIf(a->a.getUuid().equals(u.toString()));t.getMembersapplications().add(new Team.MemberApplication(u.toString(),pl.getName()));p().getTeamData().save();pl.sendMessage("\u00a7a\u5df2\u53d1\u9001\u7533\u8bf7");openMainMenu(pl);}else openMainMenu(pl);});
    }

    // CREATE
    public static void openCreateTeamCheck(Player pl) {
        if(p().getConfig2().isEnablePlaytimeCheck()){int min=pl.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE)/20/60;int req=p().getConfig2().getPlaytimeRequiredMinutes();if(min<req){openAlert(pl,"\u00a7c\u65f6\u957f\u4e0d\u8db3("+min+"/"+req+")",()->openMainMenu(pl));return;}}
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

    // ADMIN
    public static void openAdminTeamList(Player pl) {
        List<String> ids=new ArrayList<>(p().getTeamData().getAll().keySet());
        if(ids.isEmpty()){openAlert(pl,"\u00a7c\u6ca1\u6709\u56e2\u961f",()->{});return;}
        java.util.Collections.sort(ids,(a,b)->{Team ta=p().getTeamData().get(a),tb=p().getTeamData().get(b);if(ta.getActivity()>0&&tb.getActivity()==0)return-1;if(tb.getActivity()>0&&ta.getActivity()==0)return 1;if(ta.getActivity()>0&&tb.getActivity()>0)return Long.compare(tb.getActivity(),ta.getActivity());return Long.compare(tb.getFunds(),ta.getFunds());});
        int slots=Math.min(45,ids.size());int rows=(slots/9)+1;int size=Math.min(54,(rows+1)*9);if(size<9)size=9;
        Inventory inv=Bukkit.createInventory(null,size,"\u00a7lOP\u7ba1\u7406");int idx=0;
        for(String id:ids){if(idx>=45)break;Team t=p().getTeamData().get(id);inv.setItem(idx++,item(Material.REPEATER,"\u00a7l"+t.getName(),"\u6210\u957f\u503c:"+t.getActivity()+" | \u79ef\u91d1:"+t.getFunds()+" | "+t.getMemberCount()+"\u4eba"));}
        int bs=size-9;inv.setItem(bs,item(Material.BARRIER,"\u00a7l\u5173\u95ed"));pl.openInventory(inv);
        p().setGuiContext(pl.getUniqueId(),id->{if(id==bs)return;if(id>=0&&id<ids.size())openAdminTeamMenu(pl,ids.get(id));});
    }

    public static void openAdminTeamMenu(Player pl, String tid) {
        Team t=p().getTeamData().get(tid);if(t==null){openAlert(pl,"\u00a7c\u4e0d\u5b58\u5728",()->openAdminTeamList(pl));return;}
        Inventory inv=Bukkit.createInventory(null,18,"\u00a7lOP: "+t.getName());
        inv.setItem(0,item(Material.BOOK,"\u00a7l\u8be6\u60c5"));inv.setItem(1,item(Material.COMPASS,"\u00a7l\u4f20\u9001\u951a\u70b9"));
        inv.setItem(2,item(Material.ENDER_PEARL,"\u00a7l\u4e92\u4f20"));inv.setItem(3,item(Material.GOLD_NUGGET,"\u00a7l\u79ef\u91d1"));
        inv.setItem(4,item(Material.WRITABLE_BOOK,"\u00a7l\u7559\u8a00\u677f"));inv.setItem(5,item(Material.REPEATER,"\u00a7l\u7ba1\u7406"));
        inv.setItem(17,item(Material.ARROW,"\u00a7c\u8fd4\u56de"));
        pl.openInventory(inv);p().setGuiContext(pl.getUniqueId(),id->{switch(id){case 0:openTeamDetail(pl,tid);break;case 1:openWarpMainMenu(pl,tid);break;case 2:openTpaMainMenu(pl,tid);break;case 3:openTeamFundMenu(pl,tid);break;case 4:openMessageBoard(pl,tid);break;case 5:openTeamManageMenu(pl,tid);break;case 17:openAdminTeamList(pl);break;}});
    }

    // NEW MESSAGE ALERT
    public static void openNewMessageAlert(Player pl, String tid, String tn) {
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l\u65b0\u7559\u8a00");
        inv.setItem(2,item(Material.PAPER,"\u00a7e"+tn+" \u6709\u65b0\u7559\u8a00"));inv.setItem(4,item(Material.WRITABLE_BOOK,"\u00a72\u00a7l\u7acb\u5373\u67e5\u770b"));inv.setItem(8,item(Material.BARRIER,"\u00a7c\u00a7l\u7a0d\u540e"));
        pl.openInventory(inv);p().setGuiContext(pl.getUniqueId(),id->{if(id==4)openMessageBoard(pl,tid);});
    }

    // ALERT
    public static void openAlert(Player pl, String msg, Runnable cb) {
        Inventory inv=Bukkit.createInventory(null,9,"\u00a7l\u63d0\u793a");
        inv.setItem(4,item(Material.PAPER,msg.length()>40?msg.substring(0,40):msg));inv.setItem(8,item(Material.LIME_WOOL,"\u00a72\u00a7l\u786e\u5b9a"));
        pl.openInventory(inv);p().setGuiContext(pl.getUniqueId(),id->{if(id==8&&cb!=null)cb.run();});
    }

    // HELPERS
    private static void handleQuitTeam(Player pl, String tid) {
        if(p().getTeamData().isTeamOperator(pl.getUniqueId(),tid)){openAlert(pl,"\u00a7c\u7ba1\u7406\u5458\u8bf7\u5148\u964d\u7ea7",()->openMainMenu(pl));}
        else openQuitConfirmMenu(pl,tid);
    }

    private static void teleportToWarp(Player pl, String tid, String wn, Team.WarpPoint wp) {
        World w;switch(wp.getDim()){case -1:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.NETHER).findFirst().orElse(null);break;case 1:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.THE_END).findFirst().orElse(null);break;default:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.NORMAL).findFirst().orElse(null);break;}
        if(w==null){openAlert(pl,"\u00a7c\u7ef4\u5ea6\u5f02\u5e38",()->openWarpTeleportMenu(pl,tid));return;}
        pl.teleport(new Location(w,wp.getX()+0.5,wp.getY(),wp.getZ()+0.5));pl.sendMessage("\u00a7a\u5df2\u4f20\u9001\u81f3 "+wn);
    }

    static class PendingChatInput {
        Consumer<String> cb;
        PendingChatInput(Consumer<String> c){cb=c;}
    }
}
'@

Write-Host "  JavaMenus.java created"

# ==================== MAIN PLUGIN CLASS ====================

Write-JavaFile "MGTeamPlugin.java" @'
package cn.gmzc.mgteam;

import cn.gmzc.mgteam.api.MGTeamAPI;
import cn.gmzc.mgteam.api.MGTeamPapiExpansion;
import cn.gmzc.mgteam.command.MGOPCommand;
import cn.gmzc.mgteam.command.TMCommand;
import cn.gmzc.mgteam.command.TMTPACommand;
import cn.gmzc.mgteam.command.TMTPCommand;
import cn.gmzc.mgteam.command.TMSyncCommand;
import cn.gmzc.mgteam.config.Config;
import cn.gmzc.mgteam.data.FundConsumeManager;
import cn.gmzc.mgteam.data.FundLogManager;
import cn.gmzc.mgteam.data.MessageDataManager;
import cn.gmzc.mgteam.data.TeamDataManager;
import cn.gmzc.mgteam.economy.EconomyHook;
import cn.gmzc.mgteam.gui.GuiRouter;
import cn.gmzc.mgteam.gui.JavaMenus;
import cn.gmzc.mgteam.listener.MGListeners;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MGTeamPlugin extends JavaPlugin implements Listener {
    private TeamDataManager teamData;
    private MessageDataManager messageData;
    private FundConsumeManager fundConsume;
    private FundLogManager fundLog;
    private Config config;
    private EconomyHook economy;
    private MGListeners listeners;
    private final Map<UUID,Consumer<Integer>> guiHandlers = new HashMap<>();
    final Map<String,Long> messageCooldowns = new HashMap<>();

    public void onEnable() {
        saveDefaultConfig(); config = new Config(this); economy = new EconomyHook();
        if(!economy.setup()) getLogger().warning("[MGTeam] Vault\u672a\u627e\u5230\uff0c\u7ecf\u6d4e\u529f\u80fd\u4e0d\u53ef\u7528");
        File df = getDataFolder(); if(!df.exists())df.mkdirs();
        teamData=new TeamDataManager(df,getLogger());messageData=new MessageDataManager(df,getLogger());
        fundConsume=new FundConsumeManager(df,getLogger());fundLog=new FundLogManager(df,getLogger());
        teamData.load();messageData.load();fundConsume.load();fundLog.load();
        messageData.setMaxStored(config.getMaxMessagesStored());
        GuiRouter.init(this); MGTeamAPI.init(this);
        listeners=new MGListeners(this); Bukkit.getPluginManager().registerEvents(listeners,this); Bukkit.getPluginManager().registerEvents(this,this);
        listeners.startActivityTimer();
        reg("tm",new TMCommand(this));reg("tmtp",new TMTPCommand(this));reg("tmtpa",new TMTPACommand(this));reg("tmsync",new TMSyncCommand(this));reg("mgop",new MGOPCommand(this));
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!=null){new MGTeamPapiExpansion(this).register();getLogger().info("[MGTeam] PAPI\u6269\u5c55\u5df2\u6ce8\u518c");}
        getLogger().info("[MGTeam] MGTeam v1.0.0 \u5df2\u52a0\u8f7d");
    }

    public void onDisable(){teamData.save();messageData.save();fundConsume.save();fundLog.save();getLogger().info("[MGTeam] \u6570\u636e\u5df2\u4fdd\u5b58");}

    public TeamDataManager getTeamData(){return teamData;}
    public MessageDataManager getMessageData(){return messageData;}
    public FundConsumeManager getFundConsume(){return fundConsume;}
    public FundLogManager getFundLog(){return fundLog;}
    public Config getConfig2(){return config;}
    public EconomyHook getEconomy(){return economy;}
    public Map<String,Long> getMessageCooldowns(){return messageCooldowns;}

    public void setGuiContext(UUID u,Consumer<Integer> h){if(h!=null)guiHandlers.put(u,h);else guiHandlers.remove(u);}

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player))return;
        String t=e.getView().getTitle();
        if(t.startsWith("\u00a7l")||t.startsWith("\u00a7c\u00a7l")||t.startsWith("\u00a7e\u00a7l")){
            e.setCancelled(true);
            Consumer<Integer> h=guiHandlers.remove(((Player)e.getWhoClicked()).getUniqueId());
            if(h!=null&&e.getRawSlot()>=0&&e.getRawSlot()<e.getInventory().getSize())h.accept(e.getRawSlot());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player pl=e.getPlayer();
        if(JavaMenus.pendingInputs.containsKey(pl.getUniqueId())){e.setCancelled(true);String m=e.getMessage();Bukkit.getScheduler().runTask(this,()->JavaMenus.handleChatInput(pl,m));}
    }

    private void reg(String n,org.bukkit.command.CommandExecutor ex){org.bukkit.command.PluginCommand c=getCommand(n);if(c!=null)c.setExecutor(ex);}
}
'@

Write-Host "  MGTeamPlugin.java created"
Write-Host "Part 4 complete!"
