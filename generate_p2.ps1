# MGTeam Part 2: Data managers, Economy, Commands, Listener, API

$ROOT = $PSScriptRoot
$SRC = Join-Path $ROOT 'src\cn\gmzc\mgteam'
function Write-JavaFile { param($Path, $Content) $fullPath = "$SRC\$Path"; [System.IO.File]::WriteAllText($fullPath, $Content, [System.Text.UTF8Encoding]::new($false)); Write-Host "  $Path" }

# ==================== DATA MANAGERS ====================

Write-JavaFile "data\TeamDataManager.java" @'
package cn.gmzc.mgteam.data;

import cn.gmzc.mgteam.model.Team;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class TeamDataManager {
    private final File file; private final com.google.gson.Gson gson; private final Logger log;
    private Map<String,Team> data = new LinkedHashMap<>();
    public TeamDataManager(File folder, Logger l) { file=new File(folder,"MGteamdata.json"); gson=new GsonBuilder().setPrettyPrinting().create(); log=l; }
    public void load() {
        if(!file.exists()){data=new LinkedHashMap<>();return;}
        try(FileReader r=new FileReader(file)){ Type t=new TypeToken<LinkedHashMap<String,Team>>(){}.getType(); Map<String,Team> m=gson.fromJson(r,t); if(m!=null){data=m;for(Team tm:data.values()){if(tm.getWarpPoints()==null)tm.setWarpPoints(new LinkedHashMap<>());}} log.info("[MGTeam] \u5df2\u52a0\u8f7d\u56e2\u961f\u6570\u636e\uff0c\u5171"+data.size()+"\u4e2a\u56e2\u961f"); }
        catch(Exception e){log.warning("[MGTeam] \u52a0\u8f7d\u56e2\u961f\u6570\u636e\u5931\u8d25: "+e.getMessage());data=new LinkedHashMap<>();}
    }
    public void save(){try(FileWriter w=new FileWriter(file)){gson.toJson(data,w);}catch(Exception e){log.warning("[MGTeam] \u4fdd\u5b58\u5931\u8d25: "+e.getMessage());}}
    public Map<String,Team> getAll(){return data;}
    public Team get(String id){return data.get(id);}
    public void put(String id,Team t){data.put(id,t);}
    public Team remove(String id){return data.remove(id);}
    public boolean containsId(String id){return data.containsKey(id);}
    public int size(){return data.size();}
    public String getPlayerTeamId(UUID uuid){String s=uuid.toString();for(Map.Entry<String,Team> e:data.entrySet()){Team t=e.getValue();for(Team.MemberEntry m:t.getOperators()){if(s.equals(m.getUuid()))return e.getKey();}for(Team.MemberEntry m:t.getMembers()){if(s.equals(m.getUuid()))return e.getKey();}}return null;}
    public boolean isPlayerInTeam(UUID uuid){return getPlayerTeamId(uuid)!=null;}
    public boolean isTeamOperator(UUID uuid,String tid){Team t=data.get(tid);if(t==null)return false;String s=uuid.toString();for(Team.MemberEntry m:t.getOperators()){if(s.equals(m.getUuid()))return true;}return false;}
    public boolean nameExists(String name){for(Team t:data.values()){if(t.getName().equalsIgnoreCase(name))return true;}return false;}
}
'@

Write-JavaFile "data\MessageDataManager.java" @'
package cn.gmzc.mgteam.data;

import cn.gmzc.mgteam.model.MessageEntry;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MessageDataManager {
    private final File f; private final com.google.gson.Gson g; private final Logger l;
    private Map<String,List<MessageEntry>> msgs=new LinkedHashMap<>();
    private Map<String,Long> views=new LinkedHashMap<>();
    private int max=100;
    public MessageDataManager(File folder,Logger log){f=new File(folder,"MGteamdata_messages.json");g=new GsonBuilder().setPrettyPrinting().create();l=log;}
    public void setMaxStored(int m){max=m;}
    public void load(){
        if(!f.exists())return;
        try(FileReader r=new FileReader(f)){Type t=new TypeToken<LinkedHashMap<String,Object>>(){}.getType();Map<String,Object>root=g.fromJson(r,t);if(root==null)return;
            if(root.containsKey("messages")){String j=g.toJson(root.get("messages"));Type lt=new TypeToken<LinkedHashMap<String,List<MessageEntry>>>(){}.getType();Map<String,List<MessageEntry>>m=g.fromJson(j,lt);if(m!=null)msgs=m;}
            if(root.containsKey("views")){String j=g.toJson(root.get("views"));Type vt=new TypeToken<LinkedHashMap<String,Long>>(){}.getType();Map<String,Long>m=g.fromJson(j,vt);if(m!=null)views=m;}
        }catch(Exception e){l.warning("[MGTeam] \u52a0\u8f7d\u7559\u8a00\u677f\u5931\u8d25: "+e.getMessage());}
    }
    public void save(){try(FileWriter w=new FileWriter(f)){Map<String,Object>root=new LinkedHashMap<>();root.put("messages",msgs);root.put("views",views);g.toJson(root,w);}catch(Exception e){l.warning("[MGTeam] \u4fdd\u5b58\u7559\u8a00\u677f\u5931\u8d25: "+e.getMessage());}}
    public List<MessageEntry> getMessages(String tid){return msgs.computeIfAbsent(tid,k->new ArrayList<>());}
    public void addMessage(String tid,MessageEntry e){List<MessageEntry>list=getMessages(tid);list.add(0,e);if(list.size()>max)list.subList(max,list.size()).clear();}
    public void deleteTeamMessages(String tid){msgs.remove(tid);}
    public boolean hasNewMessages(UUID uuid,String tid){String k=tid+"_"+uuid;long lv=views.getOrDefault(k,0L);List<MessageEntry>list=getMessages(tid);return !list.isEmpty()&&list.get(0).getTimestamp()>lv;}
    public void setLastViewTime(UUID uuid,String tid){views.put(tid+"_"+uuid,System.currentTimeMillis());}
}
'@

Write-JavaFile "data\FundConsumeManager.java" @'
package cn.gmzc.mgteam.data;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class FundConsumeManager {
    private final File f; private final com.google.gson.Gson g; private final Logger l;
    private Map<String,CE> data=new LinkedHashMap<>();
    public FundConsumeManager(File folder,Logger log){f=new File(folder,"MGteamdata_fundconsume.json");g=new GsonBuilder().setPrettyPrinting().create();l=log;}
    public void load(){if(!f.exists())return;try(FileReader r=new FileReader(f)){Type t=new TypeToken<LinkedHashMap<String,CE>>(){}.getType();Map<String,CE>m=g.fromJson(r,t);if(m!=null)data=m;}catch(Exception e){l.warning("[MGTeam] \u52a0\u8f7d\u6d88\u8d39\u5f00\u5173\u5931\u8d25");}}
    public void save(){try(FileWriter w=new FileWriter(f)){g.toJson(data,w);}catch(Exception e){l.warning("[MGTeam] \u4fdd\u5b58\u6d88\u8d39\u5f00\u5173\u5931\u8d25");}}
    public boolean getStatus(UUID u){CE e=data.get(u.toString());return e!=null&&e.enabled;}
    public void setStatus(UUID u,boolean en){CE e=new CE();e.enabled=en;e.updatedAt=new java.text.SimpleDateFormat("yyyy-MM-dd'\''T'\''HH:mm:ss.SSS'\''Z'\''").format(new java.util.Date());data.put(u.toString(),e);}
    public void remove(UUID u){data.remove(u.toString());}
    static class CE{boolean enabled;String updatedAt;}
}
'@

Write-JavaFile "data\FundLogManager.java" @'
package cn.gmzc.mgteam.data;

import cn.gmzc.mgteam.model.FundLogEntry;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FundLogManager {
    private final File f; private final com.google.gson.Gson g; private final Logger l;
    private Map<String,List<FundLogEntry>> data=new LinkedHashMap<>();
    public FundLogManager(File folder,Logger log){f=new File(folder,"MGteamdata_fundlogs.json");g=new GsonBuilder().setPrettyPrinting().create();l=log;}
    public void load(){if(!f.exists())return;try(FileReader r=new FileReader(f)){Type t=new TypeToken<LinkedHashMap<String,List<FundLogEntry>>>(){}.getType();Map<String,List<FundLogEntry>>m=g.fromJson(r,t);if(m!=null)data=m;}catch(Exception e){l.warning("[MGTeam] \u52a0\u8f7d\u6d41\u6c34\u8d26\u5931\u8d25");}}
    public void save(){try(FileWriter w=new FileWriter(f)){g.toJson(data,w);}catch(Exception e){l.warning("[MGTeam] \u4fdd\u5b58\u6d41\u6c34\u8d26\u5931\u8d25");}}
    public void addLog(String tid,long ch,String r,long bb,long ba){List<FundLogEntry>list=data.computeIfAbsent(tid,k->new ArrayList<>());list.add(0,new FundLogEntry(ch,r,bb,ba));}
    public List<FundLogEntry> getLogs(String tid){return data.getOrDefault(tid,new ArrayList<>());}
    public void deleteTeamLogs(String tid){data.remove(tid);}
}
'@

# ==================== ECONOMY ====================

Write-JavaFile "economy\EconomyHook.java" @'
package cn.gmzc.mgteam.economy;

import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHook {
    private Economy eco;
    public boolean setup(){RegisteredServiceProvider<Economy>rsp=Bukkit.getServer().getServicesManager().getRegistration(Economy.class);if(rsp==null)return false;eco=rsp.getProvider();return eco!=null;}
    public double getBalance(UUID u){if(eco==null)return 0;return eco.getBalance(Bukkit.getOfflinePlayer(u));}
    public boolean withdraw(UUID u,double a){if(eco==null)return false;EconomyResponse r=eco.withdrawPlayer(Bukkit.getOfflinePlayer(u),a);return r.transactionSuccess();}
    public boolean deposit(UUID u,double a){if(eco==null)return false;EconomyResponse r=eco.depositPlayer(Bukkit.getOfflinePlayer(u),a);return r.transactionSuccess();}
    public boolean isReady(){return eco!=null;}
}
'@

# ==================== COMMANDS ====================

Write-JavaFile "command\TMCommand.java" @'
package cn.gmzc.mgteam.command;

import cn.gmzc.mgteam.gui.GuiRouter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TMCommand implements CommandExecutor {
    public boolean onCommand(CommandSender s,Command c,String l,String[] a){
        if(!(s instanceof Player)){s.sendMessage("\u00a7c\u8be5\u547d\u4ee4\u53ea\u80fd\u7531\u73a9\u5bb6\u6267\u884c\uff01");return true;}
        GuiRouter.openMainMenu((Player)s);return true;
    }
}
'@

Write-JavaFile "command\TMTPCommand.java" @'
package cn.gmzc.mgteam.command;

import cn.gmzc.mgteam.MGTeamPlugin;
import cn.gmzc.mgteam.gui.GuiRouter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TMTPCommand implements CommandExecutor {
    private final MGTeamPlugin p;
    public TMTPCommand(MGTeamPlugin p){this.p=p;}
    public boolean onCommand(CommandSender s,Command c,String l,String[] a){
        if(!(s instanceof Player)){s.sendMessage("\u00a7c\u8be5\u547d\u4ee4\u53ea\u80fd\u7531\u73a9\u5bb6\u6267\u884c\uff01");return true;}
        Player pl=(Player)s;String tid=p.getTeamData().getPlayerTeamId(pl.getUniqueId());
        if(tid==null){GuiRouter.openAlert(pl,"\u00a7c\u60a8\u4e0d\u5728\u4efb\u4f55\u4e00\u4e2a\u56e2\u961f\u4e2d\uff01",null);return true;}
        GuiRouter.openWarpMainMenu(pl,tid);return true;
    }
}
'@

Write-JavaFile "command\TMTPACommand.java" @'
package cn.gmzc.mgteam.command;

import cn.gmzc.mgteam.MGTeamPlugin;
import cn.gmzc.mgteam.gui.GuiRouter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TMTPACommand implements CommandExecutor {
    private final MGTeamPlugin p;
    public TMTPACommand(MGTeamPlugin p){this.p=p;}
    public boolean onCommand(CommandSender s,Command c,String l,String[] a){
        if(!(s instanceof Player)){s.sendMessage("\u00a7c\u8be5\u547d\u4ee4\u53ea\u80fd\u7531\u73a9\u5bb6\u6267\u884c\uff01");return true;}
        Player pl=(Player)s;String tid=p.getTeamData().getPlayerTeamId(pl.getUniqueId());
        if(tid==null){GuiRouter.openAlert(pl,"\u00a7c\u60a8\u4e0d\u5728\u4efb\u4f55\u4e00\u4e2a\u56e2\u961f\u4e2d\uff01",null);return true;}
        GuiRouter.openTpaMainMenu(pl,tid);return true;
    }
}
'@

Write-JavaFile "command\TMSyncCommand.java" @'
package cn.gmzc.mgteam.command;

import cn.gmzc.mgteam.MGTeamPlugin;
import cn.gmzc.mgteam.model.Team;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TMSyncCommand implements CommandExecutor {
    private final MGTeamPlugin p;
    public TMSyncCommand(MGTeamPlugin p){this.p=p;}
    public boolean onCommand(CommandSender s,Command c,String l,String[] a){
        if(!s.hasPermission("mgteam.admin")){s.sendMessage("\u00a7c\u6ca1\u6709\u6743\u9650\uff01");return true;}
        int fixed=0,failed=0,skipped=0;
        for(Map.Entry<String,Team>e:p.getTeamData().getAll().entrySet()){Team t=e.getValue();
            for(Team.MemberEntry m:t.getOperators()){if(isBad(m.getName())){OfflinePlayer op=Bukkit.getOfflinePlayer(UUID.fromString(m.getUuid()));String nn=op.getName();if(nn!=null&&!nn.isEmpty()){m.setName(nn);fixed++;}else failed++;}else skipped++;}
            for(Team.MemberEntry m:t.getMembers()){if(isBad(m.getName())){OfflinePlayer op=Bukkit.getOfflinePlayer(UUID.fromString(m.getUuid()));String nn=op.getName();if(nn!=null&&!nn.isEmpty()){m.setName(nn);fixed++;}else failed++;}else skipped++;}
            for(Team.MemberApplication a2:t.getMembersapplications()){if(isBad(a2.getName())){OfflinePlayer op=Bukkit.getOfflinePlayer(UUID.fromString(a2.getUuid()));String nn=op.getName();if(nn!=null&&!nn.isEmpty()){a2.setName(nn);fixed++;}}}
            if(t.getWarpPoints()!=null){for(Team.WarpPoint wp:t.getWarpPoints().values()){if(isBad(wp.getCreatorName())){try{OfflinePlayer op=Bukkit.getOfflinePlayer(UUID.fromString(wp.getCreatorUuid()));String nn=op.getName();if(nn!=null&&!nn.isEmpty()){wp.setCreatorName(nn);fixed++;}}catch(Exception ex){failed++;}}}}
        }
        if(fixed>0)p.getTeamData().save();
        s.sendMessage("\u00a7a\u540c\u6b65\u5b8c\u6210\uff01\u6210\u529f:"+fixed+" \u5931\u8d25:"+failed+" \u8df3\u8fc7:"+skipped);
        return true;
    }
    private boolean isBad(String n){return n==null||n.isEmpty()||"null".equals(n);}
}
'@

Write-JavaFile "command\MGOPCommand.java" @'
package cn.gmzc.mgteam.command;

import cn.gmzc.mgteam.gui.GuiRouter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MGOPCommand implements CommandExecutor {
    public boolean onCommand(CommandSender s,Command c,String l,String[] a){
        if(!s.hasPermission("mgteam.admin")){s.sendMessage("\u00a7c\u6ca1\u6709\u6743\u9650\uff01");return true;}
        if(!(s instanceof Player)){s.sendMessage("\u00a7c\u73a9\u5bb6\u6267\u884c\uff01");return true;}
        GuiRouter.openAdminTeamList((Player)s);return true;
    }
}
'@

# ==================== LISTENER ====================

Write-JavaFile "listener\MGListeners.java" @'
package cn.gmzc.mgteam.listener;

import cn.gmzc.mgteam.MGTeamPlugin;
import cn.gmzc.mgteam.gui.GuiRouter;
import cn.gmzc.mgteam.model.Team;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MGListeners implements Listener {
    private final MGTeamPlugin p;
    public MGListeners(MGTeamPlugin p){this.p=p;}

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player pl=e.getPlayer();UUID u=pl.getUniqueId();boolean sv=false;
        for(Map.Entry<String,Team>entry:p.getTeamData().getAll().entrySet()){Team t=entry.getValue();
            for(Team.MemberEntry m:t.getOperators()){if(u.toString().equals(m.getUuid())&&bad(m.getName())){m.setName(pl.getName());sv=true;}}
            for(Team.MemberEntry m:t.getMembers()){if(u.toString().equals(m.getUuid())&&bad(m.getName())){m.setName(pl.getName());sv=true;}}
            for(Team.MemberApplication a:t.getMembersapplications()){if(u.toString().equals(a.getUuid())&&bad(a.getName())){a.setName(pl.getName());sv=true;}}
            if(t.getWarpPoints()!=null){for(Team.WarpPoint wp:t.getWarpPoints().values()){if(u.toString().equals(wp.getCreatorUuid())&&bad(wp.getCreatorName())){wp.setCreatorName(pl.getName());sv=true;}}}
        }
        if(sv)p.getTeamData().save();
        String tid=p.getTeamData().getPlayerTeamId(u);
        if(tid!=null&&p.getMessageData().hasNewMessages(u,tid)){
            new BukkitRunnable(){public void run(){Player pl2=Bukkit.getPlayer(u);if(pl2!=null&&pl2.isOnline()){Team t=p.getTeamData().get(tid);if(t!=null)GuiRouter.openNewMessageAlert(pl2,tid,t.getName());}}}.runTaskLater(p,60L);
        }
    }

    @EventHandler
    public void onExp(PlayerExpChangeEvent e){
        Player pl=e.getPlayer();String tid=p.getTeamData().getPlayerTeamId(pl.getUniqueId());
        if(tid!=null){Team t=p.getTeamData().get(tid);if(t!=null)t.setActivity(t.getActivity()+1);}
    }

    public void startActivityTimer(){
        new BukkitRunnable(){public void run(){for(Team t:p.getTeamData().getAll().values()){long a=t.getActivity();if(a>0){long d=Math.max(1,(long)Math.ceil(a*0.01));t.setActivity(Math.max(0,a-d));}}p.getTeamData().save();}}.runTaskTimer(p,72000L,72000L);
    }

    private boolean bad(String n){return n==null||n.isEmpty()||"null".equals(n);}
}
'@

# ==================== API ====================

Write-JavaFile "api\MGTeamAPI.java" @'
package cn.gmzc.mgteam.api;

import cn.gmzc.mgteam.MGTeamPlugin;
import cn.gmzc.mgteam.model.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MGTeamAPI {
    private static MGTeamPlugin p;
    public static void init(MGTeamPlugin p2){p=p2;}
    public static String getPlayerOrgName(UUID u){String tid=p.getTeamData().getPlayerTeamId(u);if(tid==null)return null;Team t=p.getTeamData().get(tid);return t!=null?t.getName():null;}
    public static String getPlayerOrgId(UUID u){return p.getTeamData().getPlayerTeamId(u);}
    public static boolean playerIsOwner(UUID u){String tid=p.getTeamData().getPlayerTeamId(u);return tid!=null&&p.getTeamData().isTeamOperator(u,tid);}
    public static long orgGetMoney(String oid){Team t=p.getTeamData().get(oid);return t!=null?t.getFunds():0;}
    public static boolean orgAddMoney(String oid,long c,String r){Team t=p.getTeamData().get(oid);if(t==null)return false;if(t.getFunds()+c<0)return false;p.getFundLog().addLog(oid,c,r!=null?r:"\u5916\u90e8\u63d2\u4ef6",t.getFunds(),t.getFunds()+c);t.setFunds(t.getFunds()+c);p.getTeamData().save();p.getFundLog().save();return true;}
    public static int getPlayerAuxInTeam(UUID u,String oid){Team t=p.getTeamData().get(oid);if(t==null)return -1;String us=u.toString();for(Team.MemberEntry m:t.getOperators())if(m.getUuid().equals(us))return 4;for(Team.MemberEntry m:t.getMembers())if(m.getUuid().equals(us))return 2;return -1;}
    public static boolean getFundConsumeStatus(UUID u){return p.getFundConsume().getStatus(u);}
    public static void setFundConsumeStatus(UUID u,boolean en){p.getFundConsume().setStatus(u,en);p.getFundConsume().save();}
    public static boolean reducePlayerMoneyDirect(UUID u,double a){return p.getEconomy().withdraw(u,a);}
}
'@

Write-JavaFile "api\MGTeamPapiExpansion.java" @'
package cn.gmzc.mgteam.api;

import cn.gmzc.mgteam.MGTeamPlugin;
import cn.gmzc.mgteam.model.Team;
import java.util.UUID;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class MGTeamPapiExpansion extends PlaceholderExpansion {
    private final MGTeamPlugin p;
    public MGTeamPapiExpansion(MGTeamPlugin p){this.p=p;}
    public String getIdentifier(){return "mgteam";}
    public String getAuthor(){return "Codex";}
    public String getVersion(){return "1.0.0";}
    public boolean persist(){return true;}
    public String onPlaceholderRequest(Player pl,String params){
        if(pl==null)return "";UUID u=pl.getUniqueId();
        if(params.equalsIgnoreCase("org_name")){String tid=p.getTeamData().getPlayerTeamId(u);if(tid==null)return "";Team t=p.getTeamData().get(tid);return t!=null?t.getName():"";}
        if(params.equalsIgnoreCase("org_id")){String id=p.getTeamData().getPlayerTeamId(u);return id!=null?id:"";}
        if(params.equalsIgnoreCase("is_owner")){String tid=p.getTeamData().getPlayerTeamId(u);return tid!=null&&p.getTeamData().isTeamOperator(u,tid)?"true":"false";}
        if(params.equalsIgnoreCase("org_money")){String tid=p.getTeamData().getPlayerTeamId(u);if(tid==null)return "0";Team t=p.getTeamData().get(tid);return t!=null?String.valueOf(t.getFunds()):"0";}
        if(params.equalsIgnoreCase("org_activity")){String tid=p.getTeamData().getPlayerTeamId(u);if(tid==null)return "0";Team t=p.getTeamData().get(tid);return t!=null?String.valueOf(t.getActivity()):"0";}
        if(params.equalsIgnoreCase("org_member_count")){String tid=p.getTeamData().getPlayerTeamId(u);if(tid==null)return "0";Team t=p.getTeamData().get(tid);return t!=null?String.valueOf(t.getMemberCount()):"0";}
        if(params.equalsIgnoreCase("fund_consume")){return p.getFundConsume().getStatus(u)?"\u5f00":"\u5173";}
        return null;
    }
}
'@

Write-Host "Part 2 complete: data managers, economy, commands, listener, API"
