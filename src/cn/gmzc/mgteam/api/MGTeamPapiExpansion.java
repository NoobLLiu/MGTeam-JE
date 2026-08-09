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
        // 基金垫付功能暂时停用；保留占位符并固定返回关闭，避免外部菜单显示未实现功能。
        if(params.equalsIgnoreCase("fund_consume")){return "\u5173";}
        return null;
    }
}
