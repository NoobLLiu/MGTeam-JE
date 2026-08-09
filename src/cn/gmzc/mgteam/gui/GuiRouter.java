package cn.gmzc.mgteam.gui;

import cn.gmzc.mgteam.MGTeamPlugin;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.bukkit.entity.Player;

public class GuiRouter {
    private static MGTeamPlugin p;
    private static BedrockFormSender bedrockFormSender;

    public static void init(MGTeamPlugin p2) {
        p = p2;
        bedrockFormSender = new BedrockFormSender(p2);
        bedrockFormSender.start();
    }
    public static void shutdown() {
        if (bedrockFormSender != null) {
            bedrockFormSender.stop();
            bedrockFormSender = null;
        }
    }
    public static MGTeamPlugin p() { return p; }
    public static String cur() { return p().getConfig2().getCurrencyName(); }

    public static boolean isBedrock(Player pl) {
        return bedrockFormSender != null && bedrockFormSender.isBedrock(pl);
    }

    public static boolean sendForm(Player pl, Form form) {
        return bedrockFormSender != null && bedrockFormSender.send(pl, form);
    }

    public static boolean sendForm(Player pl, FormBuilder<?, ?, ?> builder) {
        return bedrockFormSender != null && bedrockFormSender.send(pl, builder);
    }

    public static void openMainMenu(Player pl) { if(isBedrock(pl))BedrockForms.openMainMenu(pl);else JavaMenus.openMainMenu(pl); }
    public static void openTeamDetail(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openTeamDetail(pl,tid);else JavaMenus.openTeamDetail(pl,tid); }
    public static void openWarpMainMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openWarpMainMenu(pl,tid);else JavaMenus.openWarpMenu(pl,tid); }
    public static void openWarpTeleportMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openWarpTeleportMenu(pl,tid);else JavaMenus.openWarpMenu(pl,tid,0); }
    public static void openAddWarpMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openAddWarpMenu(pl,tid);else JavaMenus.openAddWarpMenu(pl,tid); }
    public static void openRemoveWarpMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openRemoveWarpMenu(pl,tid);else JavaMenus.openWarpMenu(pl,tid,0); }
    public static void openTpaMainMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openTpaMainMenu(pl,tid);else JavaMenus.openTpaMainMenu(pl,tid); }
    public static void openTeamFundMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openTeamFundMenu(pl,tid);else JavaMenus.openTeamFundMenu(pl,tid); }
    public static void openDepositMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openDepositMenu(pl,tid);else JavaMenus.openDepositMenu(pl,tid); }
    public static void openWithdrawMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openWithdrawMenu(pl,tid);else JavaMenus.openWithdrawMenu(pl,tid); }
    public static void openFundLogMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openFundLogMenu(pl,tid);else JavaMenus.openFundLogMenu(pl,tid); }
    public static void openMessageBoard(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openMessageBoard(pl,tid);else JavaMenus.openMessageBoard(pl,tid); }
    public static void openAddMessageMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openAddMessageMenu(pl,tid);else JavaMenus.openAddMessageMenu(pl,tid); }
    public static void openTeamManageMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openTeamManageMenu(pl,tid);else JavaMenus.openTeamManageMenu(pl,tid); }
    public static void openManageMembers(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openManageMembers(pl,tid);else JavaMenus.openManageMembers(pl,tid); }
    public static void openManageApplications(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openManageApplications(pl,tid);else JavaMenus.openManageApplications(pl,tid); }
    public static void openNoticeEditMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openNoticeEditMenu(pl,tid);else JavaMenus.openNoticeEditMenu(pl,tid); }
    public static void openRenameTeamMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openRenameTeamMenu(pl,tid);else JavaMenus.openRenameTeamMenu(pl,tid); }
    public static void openDisbandConfirmMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openDisbandConfirmMenu(pl,tid);else JavaMenus.openDisbandConfirmMenu(pl,tid); }
    public static void openQuitConfirmMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openQuitConfirmMenu(pl,tid);else JavaMenus.openQuitConfirmMenu(pl,tid); }
    public static void openTeamRankingMenu(Player pl) { if(isBedrock(pl))BedrockForms.openTeamRankingMenu(pl);else JavaMenus.openTeamRankingMenu(pl); }
    public static void openJoinByIdInput(Player pl) { if(isBedrock(pl))BedrockForms.openJoinByIdInput(pl);else JavaMenus.openJoinByIdInput(pl); }
    public static void openCreateTeamMenu(Player pl) { if(isBedrock(pl))BedrockForms.openCreateTeamMenu(pl);else JavaMenus.openCreateTeamMenu(pl); }
    public static void openAdminTeamList(Player pl) { if(isBedrock(pl))BedrockForms.openAdminTeamList(pl);else JavaMenus.openAdminTeamList(pl); }
    public static void openAdminTeamMenu(Player pl,String tid) { if(isBedrock(pl))BedrockForms.openAdminTeamMenu(pl,tid);else JavaMenus.openAdminTeamMenu(pl,tid); }
    public static void openNewMessageAlert(Player pl,String tid,String tn) { if(isBedrock(pl))BedrockForms.openNewMessageAlert(pl,tid,tn);else JavaMenus.openNewMessageAlert(pl,tid,tn); }
    public static void openAlert(Player pl,String msg,Runnable cb) { if(isBedrock(pl))BedrockForms.openAlert(pl,msg,cb);else JavaMenus.openAlert(pl,msg,cb); }
}
