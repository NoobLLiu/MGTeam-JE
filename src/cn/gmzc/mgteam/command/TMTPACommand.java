package cn.gmzc.mgteam.command;

import cn.gmzc.mgteam.MGTeamPlugin;
import cn.gmzc.mgteam.gui.GuiRouter;
import cn.gmzc.mgteam.GrowthLevelAccess;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TMTPACommand implements CommandExecutor, TabCompleter {
    private final MGTeamPlugin p;
    public TMTPACommand(MGTeamPlugin p){this.p=p;}
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
        if(a.length==0){GuiRouter.openTpaMainMenu(pl,tid);return true;}
        String name=String.join(" ",a).trim();
        Player target=Bukkit.getPlayer(name);
        if(target==null||target.getUniqueId().equals(pl.getUniqueId())
                ||!tid.equals(p.getTeamData().getPlayerTeamId(target.getUniqueId()))){
            pl.sendMessage("\u00a7c\u8be5\u73a9\u5bb6\u4e0d\u5728\u7ebf\u6216\u4e0d\u5728\u4f60\u7684\u56e2\u961f\uff01");return true;
        }
        pl.teleport(target.getLocation());
        pl.sendMessage("\u00a7a\u5df2\u4f20\u9001\u81f3 "+target.getName());
        target.sendMessage("\u00a7e"+pl.getName()+"\u4f20\u9001\u5230\u4e86\u4f60\u8eab\u8fb9");
        return true;
    }
    public List<String> onTabComplete(CommandSender s,Command c,String l,String[] a){
        List<String> out=new ArrayList<>();
        if(!(s instanceof Player))return out;
        Player pl=(Player)s;
        if(GrowthLevelAccess.restricted(pl))return out;
        String tid=p.getTeamData().getPlayerTeamId(pl.getUniqueId());
        if(tid==null||a.length>1)return out;
        String prefix=a.length==1?a[0].toLowerCase():"";
        for(Player op:Bukkit.getOnlinePlayers()){
            if(!op.getUniqueId().equals(pl.getUniqueId())
                    &&tid.equals(p.getTeamData().getPlayerTeamId(op.getUniqueId()))
                    &&op.getName().toLowerCase().startsWith(prefix)){
                out.add(op.getName());
            }
        }
        return out;
    }
}
