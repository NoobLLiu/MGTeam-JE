package cn.gmzc.mgteam.command;

import cn.gmzc.mgteam.gui.GuiRouter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import cn.gmzc.mgteam.MGTeamPlugin;

public class MGOPCommand implements CommandExecutor {
    private final MGTeamPlugin p;
    public MGOPCommand(MGTeamPlugin p){this.p=p;}
    public boolean onCommand(CommandSender s,Command c,String l,String[] a){
        if(!s.hasPermission("mgteam.admin")){s.sendMessage("\u00a7c\u6ca1\u6709\u6743\u9650\uff01");return true;}
        if(!(s instanceof Player)){s.sendMessage("\u00a7c\u73a9\u5bb6\u6267\u884c\uff01");return true;}
        GuiRouter.openAdminTeamList((Player)s);return true;
    }
}