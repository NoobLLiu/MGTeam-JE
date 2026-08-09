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