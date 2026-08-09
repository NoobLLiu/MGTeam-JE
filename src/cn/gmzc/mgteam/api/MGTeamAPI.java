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
    public static boolean orgAddMoney(String oid,long c,String r){Team t=p.getTeamData().get(oid);if(t==null)return false;if(t.getFunds()+c<0)return false;String reason=r!=null?r:"\u5916\u90e8\u63d2\u4ef6";long bb=t.getFunds();p.getFundLog().addLog(oid,c,reason,bb,bb+c);t.setFunds(bb+c);p.getTeamData().save();p.getFundLog().save();p.notifyFundChange(oid,bb,c,bb+c,reason,null);return true;}
    public static int getPlayerAuxInTeam(UUID u,String oid){Team t=p.getTeamData().get(oid);if(t==null)return -1;String us=u.toString();for(Team.MemberEntry m:t.getOperators())if(m.getUuid().equals(us))return 4;for(Team.MemberEntry m:t.getMembers())if(m.getUuid().equals(us))return 2;return -1;}
    /**
     * 基金垫付功能暂时停用。保留方法签名以兼容旧的外部调用，但不会启用或修改任何状态。
     */
    @Deprecated
    public static boolean getFundConsumeStatus(UUID u){return false;}
    @Deprecated
    public static void setFundConsumeStatus(UUID u,boolean en){}
    public static boolean reducePlayerMoneyDirect(UUID u,double a){return p.getEconomy().withdraw(u,a);}
}
