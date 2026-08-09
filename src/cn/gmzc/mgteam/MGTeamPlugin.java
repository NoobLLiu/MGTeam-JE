package cn.gmzc.mgteam;

import cn.gmzc.mgteam.api.MGTeamAPI;
import cn.gmzc.mgteam.api.MGTeamPapiExpansion;
import cn.gmzc.mgteam.command.MGOPCommand;
import cn.gmzc.mgteam.command.TMCommand;
import cn.gmzc.mgteam.command.TMTPACommand;
import cn.gmzc.mgteam.command.TMTPCommand;
import cn.gmzc.mgteam.command.TMSyncCommand;
import cn.gmzc.mgteam.config.Config;
import cn.gmzc.mgteam.data.FundLogManager;
import cn.gmzc.mgteam.data.MessageDataManager;
import cn.gmzc.mgteam.data.TeamDataManager;
import cn.gmzc.mgteam.economy.EconomyHook;
import cn.gmzc.mgteam.gui.AnvilInputGUI;
import cn.gmzc.mgteam.gui.GuiRouter;
import cn.gmzc.mgteam.gui.JavaMenus;
import cn.gmzc.mgteam.listener.MGListeners;
import cn.gmzc.mgteam.model.Team;
import cn.gmzc.mgteam.model.MessageEntry;
import cn.gmzc.mgteam.web.WebTeamManager;
import cn.gmzc.skincache.api.PlayerSkinService;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

public class MGTeamPlugin extends JavaPlugin implements Listener {
    private TeamDataManager teamData;
    private MessageDataManager messageData;
    private FundLogManager fundLog;
    private Config config;
    private EconomyHook economy;
    private MGListeners listeners;
    private AnvilInputGUI anvilInputGUI;
    private WebTeamManager webTeamManager;
    private PlayerSkinService playerSkinService;
    private final Map<UUID,Consumer<Integer>> guiHandlers = new HashMap<>();
    final Map<String,Long> messageCooldowns = new HashMap<>();

    public void onEnable() {
        config = new Config(this); economy = new EconomyHook();
        if(!economy.setup()) getLogger().warning("[MGTeam] Vault\u672a\u627e\u5230\uff0c\u7ecf\u6d4e\u529f\u80fd\u4e0d\u53ef\u7528");
        File df = getDataFolder(); if(!df.exists())df.mkdirs();
        teamData=new TeamDataManager(df,getLogger());messageData=new MessageDataManager(df,getLogger());
        fundLog=new FundLogManager(df,getLogger());
        playerSkinService = Bukkit.getServicesManager().load(PlayerSkinService.class);
        if (playerSkinService == null) {
            throw new IllegalStateException("GMZCSkinCache service is unavailable");
        }
        teamData.load();messageData.load();
        if(messageData.migrateLegacyNoticeViews(teamData.getAll())){
            messageData.save();
        }
        fundLog.load();
        messageData.setMaxStored(config.getMaxMessagesStored());
        GuiRouter.init(this); MGTeamAPI.init(this);
        webTeamManager=new WebTeamManager(this);
        anvilInputGUI = new AnvilInputGUI(this);
        listeners=new MGListeners(this); Bukkit.getPluginManager().registerEvents(listeners,this); Bukkit.getPluginManager().registerEvents(this,this); Bukkit.getPluginManager().registerEvents(anvilInputGUI,this);
        listeners.startActivityTimer();
        reg("tm",new TMCommand(this));reg("tmtp",new TMTPCommand(this));reg("tmtpa",new TMTPACommand(this));reg("tmsync",new TMSyncCommand(this));reg("mgop",new MGOPCommand(this));
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!=null){new MGTeamPapiExpansion(this).register();getLogger().info("[MGTeam] PAPI\u6269\u5c55\u5df2\u6ce8\u518c");}
        getLogger().info("[MGTeam] MGTeam v1.0.0 \u5df2\u52a0\u8f7d");
    }

    public void onDisable(){
        if (listeners != null) listeners.stopActivityTimer();
        GuiRouter.shutdown();
        if (teamData != null) teamData.save();
        if (messageData != null) messageData.save();
        if (fundLog != null) fundLog.save();
        getLogger().info("[MGTeam] \u6570\u636e\u5df2\u4fdd\u5b58");
    }

    public TeamDataManager getTeamData(){return teamData;}
    public MessageDataManager getMessageData(){return messageData;}
    public FundLogManager getFundLog(){return fundLog;}
    public Config getConfig2(){return config;}
    public EconomyHook getEconomy(){return economy;}
    public AnvilInputGUI getAnvilInputGUI(){return anvilInputGUI;}
    public WebTeamManager getWebTeamManager(){return webTeamManager;}
    public PlayerSkinService getPlayerSkinService(){return playerSkinService;}
    public Map<String,Long> getMessageCooldowns(){return messageCooldowns;}

    public void markNoticeRead(Player player,String tid){
        if(player==null||tid==null||teamData==null||messageData==null)return;
        String playerTid=teamData.getPlayerTeamId(player.getUniqueId());
        if(!tid.equals(playerTid))return;
        Team team=teamData.get(tid);
        long updatedAt=team==null?0:team.getNoticeUpdatedAt();
        if(team==null||team.getNotice().isBlank()||updatedAt<=0)return;
        if(!messageData.hasNewNotice(player.getUniqueId(),tid,updatedAt))return;
        messageData.setNoticeRead(player.getUniqueId(),tid,updatedAt);
        messageData.save();
    }

    public boolean hasNewMenuAlert(Player player) {
        if (player == null || teamData == null || messageData == null) return false;
        String tid = teamData.getPlayerTeamId(player.getUniqueId());
        if (tid == null) return false;
        Team team = teamData.get(tid);
        if (team == null) return false;
        if (teamData.isTeamOperator(player.getUniqueId(), tid)
            && !team.getMembersapplications().isEmpty()) {
            return true;
        }
        return messageData.hasNewMessages(player.getUniqueId(), tid)
            || (!team.getNotice().isBlank()
                && messageData.hasNewNotice(player.getUniqueId(), tid, team.getNoticeUpdatedAt()));
    }

    public void notifyFundChange(String tid,long before,long change,long after,String reason,String playerName){
        Team t=teamData.get(tid);
        if(t==null)return;
        String cur=config.getCurrencyName();
        String name=(playerName==null||playerName.isEmpty())?"\u672a\u77e5":playerName;
        String msg="\u00a7e[\u56e2\u961f\u8d44\u91d1] \u00a77\u53d8\u52a8\u524d:\u00a7f"+before+cur+" \u00a77\u53d8\u52a8:\u00a7"+(change>=0?"a+":"c")+change+cur+" \u00a77\u53d8\u52a8\u540e:\u00a7f"+after+cur+" \u00a77\u539f\u56e0:\u00a7f"+reason+" \u00a77\u76f8\u5173\u73a9\u5bb6:\u00a7f"+name;
        for(Player op:Bukkit.getOnlinePlayers()){
            if(teamData.isTeamOperator(op.getUniqueId(),tid))op.sendMessage(msg);
        }
    }

    public void notifyTeamMessage(String tid,MessageEntry e){
        Team t=teamData.get(tid);
        if(t==null||e==null)return;
        String msg="\u00a7e[\u56e2\u961f\u7559\u8a00] \u00a7f"+e.getSenderName()+" \u5728\u56e2\u961f\u300c"+t.getName()+"\u300d\u53d1\u5e03\u4e86\u65b0\u7559\u8a00\uff0c\u53ef\u5728\u7559\u8a00\u677f\u67e5\u770b";
        String su=e.getSenderUuid();
        for(Player m:Bukkit.getOnlinePlayers()){
            if(su!=null&&su.equals(m.getUniqueId().toString()))continue;
            if(tid.equals(teamData.getPlayerTeamId(m.getUniqueId())))m.sendMessage(msg);
        }
    }

    public void setGuiContext(UUID u,Consumer<Integer> h){if(h!=null)guiHandlers.put(u,h);else guiHandlers.remove(u);}

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player))return;
        if(e.getView().getTopInventory().getType()==InventoryType.ANVIL)return;
        String t=e.getView().getTitle();
        if(t.startsWith("\u00a7l")||t.startsWith("\u00a7c\u00a7l")||t.startsWith("\u00a7e\u00a7l")||t.startsWith("\u00a7c")){
            e.setCancelled(true);
            Consumer<Integer> h=guiHandlers.get(((Player)e.getWhoClicked()).getUniqueId());
            if(h!=null&&e.getRawSlot()>=0&&e.getRawSlot()<e.getInventory().getSize())h.accept(e.getRawSlot());
        }
    }

    private void reg(String n,org.bukkit.command.CommandExecutor ex){org.bukkit.command.PluginCommand c=getCommand(n);if(c!=null){c.setExecutor(ex);if(ex instanceof org.bukkit.command.TabCompleter)c.setTabCompleter((org.bukkit.command.TabCompleter)ex);}}
}
