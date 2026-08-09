package cn.gmzc.mgteam.command;

import cn.gmzc.mgteam.gui.GuiRouter;
import cn.gmzc.mgteam.GrowthLevelAccess;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import cn.gmzc.mgteam.MGTeamPlugin;

public class TMCommand implements CommandExecutor {
    private final MGTeamPlugin p;
    public TMCommand(MGTeamPlugin p){this.p=p;}
    public boolean onCommand(CommandSender s,Command c,String l,String[] a){
        if(!(s instanceof Player)){s.sendMessage("\u00a7c\u8be5\u547d\u4ee4\u53ea\u80fd\u7531\u73a9\u5bb6\u6267\u884c\uff01");return true;}
        Player pl=(Player)s;
        if(GrowthLevelAccess.restricted(pl)){
            pl.sendMessage("\u00a7c\u8be5\u529f\u80fd\u9700\u8981\u6210\u957f\u7b49\u7ea7\u8fbe\u5230 \u00a7e" + GrowthLevelAccess.REQUIRED_LEVEL + "\u00a7c \u7ea7\u540e\u624d\u80fd\u4f7f\u7528\u3002");
            pl.sendMessage("\u00a77\u5f53\u524d\u6210\u957f\u7b49\u7ea7\uff1a\u00a7f" + GrowthLevelAccess.level(pl));
            return true;
        }
        GuiRouter.openMainMenu(pl);return true;
    }
}