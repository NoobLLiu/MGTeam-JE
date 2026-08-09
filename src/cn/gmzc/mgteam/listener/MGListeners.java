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
import org.bukkit.scheduler.BukkitTask;

public class MGListeners implements Listener {
    private final MGTeamPlugin p;
    private BukkitTask activityTask;
    public MGListeners(MGTeamPlugin p){this.p=p;}

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player pl=e.getPlayer();UUID u=pl.getUniqueId();boolean sv=false;
        for(Map.Entry<String,Team>entry:p.getTeamData().getAll().entrySet()){Team t=entry.getValue();
            for(Team.MemberEntry m:t.getOperators()){sv |= syncEntry(m, u, pl.getName());}
            for(Team.MemberEntry m:t.getMembers()){sv |= syncEntry(m, u, pl.getName());}
            for(Team.MemberApplication a:t.getMembersapplications()){if(u.toString().equals(a.getUuid())&&bad(a.getName())){a.setName(pl.getName());sv=true;}}
            if(t.getWarpPoints()!=null){for(Team.WarpPoint wp:t.getWarpPoints().values()){if(u.toString().equals(wp.getCreatorUuid())&&bad(wp.getCreatorName())){wp.setCreatorName(pl.getName());sv=true;}}}
        }
        if(sv)p.getTeamData().save();
        String tid=p.getTeamData().getPlayerTeamId(u);
        if(tid!=null&&p.getMessageData().hasNewMessages(u,tid)){
            new BukkitRunnable(){public void run(){Player pl2=Bukkit.getPlayer(u);if(pl2!=null&&pl2.isOnline()){Team t=p.getTeamData().get(tid);if(t!=null)GuiRouter.openNewMessageAlert(pl2,tid,t.getName());}}}.runTaskLater(p,60L);
        }
    }

    private static boolean syncEntry(Team.MemberEntry m, UUID u, String playerName) {
        if (!u.toString().equals(m.getUuid())) return false;
        boolean changed = false;
        if (bad(m.getName())) { m.setName(playerName); changed = true; }
        return changed;
    }

    @EventHandler
    public void onExp(PlayerExpChangeEvent e){
        Player pl=e.getPlayer();String tid=p.getTeamData().getPlayerTeamId(pl.getUniqueId());
        if(tid!=null){Team t=p.getTeamData().get(tid);if(t!=null)t.setActivity(t.getActivity()+1);}
    }

    public void startActivityTimer(){
        if (activityTask != null) return;
        activityTask = new BukkitRunnable(){public void run(){
            boolean changed = false;
            for(Team t:p.getTeamData().getAll().values()){
                long a=t.getActivity();
                if(a>0){
                    long d=Math.max(1,(long)Math.ceil(a*0.01));
                    t.setActivity(Math.max(0,a-d));
                    changed = true;
                }
            }
            if (changed) p.getTeamData().save();
        }}.runTaskTimer(p,72000L,72000L);
    }

    public void stopActivityTimer() {
        if (activityTask != null) {
            activityTask.cancel();
            activityTask = null;
        }
    }

    private static boolean bad(String n){return n==null||n.isEmpty()||"null".equals(n);}
}
