package cn.gmzc.mgteam.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private final JavaPlugin plugin;
    private long createTeamCost;
    private String currencyName;
    private boolean enablePlaytimeCheck;
    private int playtimeRequiredMinutes;
    private int messageCooldownSeconds;
    private int maxMessagesStored;
    private int fundLogDisplayLimit;

    public Config(JavaPlugin p) { plugin=p; plugin.saveDefaultConfig(); reload(); }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration c = plugin.getConfig();
        createTeamCost=c.getLong("create-team-cost",100000);
        currencyName=c.getString("currency-name","\u661f\u5149\u70b9");
        enablePlaytimeCheck=c.getBoolean("enable-playtime-check",true);
        playtimeRequiredMinutes=c.getInt("playtime-required-minutes",600);
        messageCooldownSeconds=c.getInt("message-cooldown-seconds",600);
        maxMessagesStored=c.getInt("max-messages-stored",100);
        fundLogDisplayLimit=c.getInt("fund-log-display-limit",50);
    }

    public long getCreateTeamCost() { return createTeamCost; }
    public String getCurrencyName() { return currencyName; }
    public boolean isEnablePlaytimeCheck() { return enablePlaytimeCheck; }
    public int getPlaytimeRequiredMinutes() { return playtimeRequiredMinutes; }
    public int getMessageCooldownSeconds() { return messageCooldownSeconds; }
    public int getMaxMessagesStored() { return maxMessagesStored; }
    public int getFundLogDisplayLimit() { return fundLogDisplayLimit; }
}
