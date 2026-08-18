package cn.gmzc.mgteam.command;

import cn.gmzc.essentialsxmenu.TeleportWaitBridge;
import cn.gmzc.mgteam.MGTeamPlugin;
import cn.gmzc.mgteam.gui.GuiRouter;
import cn.gmzc.mgteam.GrowthLevelAccess;
import cn.gmzc.mgteam.model.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TMTPCommand implements CommandExecutor, TabCompleter {
    private final MGTeamPlugin p;
    public TMTPCommand(MGTeamPlugin p){this.p=p;}
    public boolean onCommand(CommandSender s,Command c,String l,String[] a){
        if(!(s instanceof Player)){s.sendMessage("\u00a7c\u8be5\u547d\u4ee4\u53ea\u80fd\u7531\u73a9\u5bb6\u6267\u884c\uff01");return true;}
        Player pl=(Player)s;
        if(GrowthLevelAccess.restricted(pl)){
            pl.sendMessage("\u00a7c\u8be5\u529f\u80fd\u9700\u8981\u6210\u957f\u7b49\u7ea7\u8fbe\u5230 \u00a7e" + GrowthLevelAccess.REQUIRED_LEVEL + "\u00a7c \u7ea7\u540e\u624d\u80fd\u4f7f\u7528\u3002");
            pl.sendMessage("\u00a77\u5f53\u524d\u6210\u957f\u7b49\u7ea7\uff1a\u00a7f" + GrowthLevelAccess.level(pl));
            return true;
        }
        String tid=p.getTeamData().getPlayerTeamId(pl.getUniqueId());
        if(tid==null){GuiRouter.openAlert(pl,"\u00a7c\u60a8\u4e0d\u5728\u4efb\u4f55\u4e00\u4e2a\u56e2\u961f\u4e2d\uff01",null);return true;}
        if(a.length==0){GuiRouter.openWarpMainMenu(pl,tid);return true;}
        Team t=p.getTeamData().get(tid);
        if(t==null){pl.sendMessage("\u00a7c\u56e2\u961f\u6570\u636e\u5f02\u5e38\uff01");return true;}
        String name=String.join(" ",a).trim();
        String wn=resolveWarp(t.getWarpPoints(),name);
        if(wn==null){pl.sendMessage("\u00a7c\u672a\u627e\u5230\u4f20\u9001\u70b9 "+name+"\uff01");return true;}
        teleportToWarp(pl,wn,t.getWarpPoints().get(wn));
        return true;
    }
    public List<String> onTabComplete(CommandSender s,Command c,String l,String[] a){
        List<String> out=new ArrayList<>();
        if(!(s instanceof Player))return out;
        Player pl=(Player)s;
        if(GrowthLevelAccess.restricted(pl))return out;
        String tid=p.getTeamData().getPlayerTeamId(pl.getUniqueId());
        if(tid==null)return out;
        Team t=p.getTeamData().get(tid);
        if(t==null||a.length>1)return out;
        String prefix=a.length==1?a[0].toLowerCase():"";
        for(String wn:t.getWarpPoints().keySet())if(wn.toLowerCase().startsWith(prefix))out.add(wn);
        return out;
    }
    private static String resolveWarp(Map<String,Team.WarpPoint> warps,String name){
        if(warps.containsKey(name))return name;
        for(String k:warps.keySet())if(k.equalsIgnoreCase(name))return k;
        return null;
    }
    private void teleportToWarp(Player pl,String wn,Team.WarpPoint wp){
        World w;
        String wName=wp.getWorld();
        if(wName!=null&&!wName.isEmpty())w=Bukkit.getWorld(wName);else w=null;
        if(w==null){
            switch(wp.getDim()){case -1:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.NETHER).findFirst().orElse(null);break;case 1:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.THE_END).findFirst().orElse(null);break;default:w=Bukkit.getWorlds().stream().filter(w2->w2.getEnvironment()==World.Environment.NORMAL).findFirst().orElse(null);break;}
        }
        if(w==null){pl.sendMessage("\u00a7c\u7ef4\u5ea6\u5f02\u5e38\uff0c\u65e0\u6cd5\u4f20\u9001\uff01");return;}
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
}
