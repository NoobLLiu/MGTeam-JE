package cn.gmzc.mgteam.economy;

import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHook {
    private Economy eco;
    public boolean setup(){RegisteredServiceProvider<Economy>rsp=Bukkit.getServer().getServicesManager().getRegistration(Economy.class);if(rsp==null)return false;eco=rsp.getProvider();return eco!=null;}
    public double getBalance(UUID u){if(eco==null)return 0;return eco.getBalance(Bukkit.getOfflinePlayer(u));}
    public boolean withdraw(UUID u,double a){if(eco==null)return false;EconomyResponse r=eco.withdrawPlayer(Bukkit.getOfflinePlayer(u),a);return r.transactionSuccess();}
    public boolean deposit(UUID u,double a){if(eco==null)return false;EconomyResponse r=eco.depositPlayer(Bukkit.getOfflinePlayer(u),a);return r.transactionSuccess();}
    public boolean isReady(){return eco!=null;}
}