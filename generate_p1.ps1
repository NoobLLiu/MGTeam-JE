# MGTeam Java Source Generator
# Generates all source files with Unicode escapes for non-ASCII text

$ROOT = $PSScriptRoot
$SRC = Join-Path $ROOT 'src\cn\gmzc\mgteam'

function Write-JavaFile {
    param($Path, $Content)
    $fullPath = "$SRC\$Path"
    [System.IO.File]::WriteAllText($fullPath, $Content, [System.Text.UTF8Encoding]::new($false))
    Write-Host "  Created $Path"
}

# ==================== MODEL ====================

Write-JavaFile "model\Team.java" @'
package cn.gmzc.mgteam.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Team {
    private String name;
    private List<MemberEntry> operators = new ArrayList<>();
    private List<MemberEntry> members = new ArrayList<>();
    private List<MemberApplication> membersapplications = new ArrayList<>();
    private long funds;
    private long activity;
    private String createdAt;
    private String notice = "";
    private boolean isPublic = true;
    private boolean allowFriendlyFire = true;
    private Map<String, WarpPoint> warpPoints = new LinkedHashMap<>();

    public Team() {}
    public Team(String name, UUID creatorUuid, String creatorName) {
        this.name = name;
        this.operators.add(new MemberEntry(creatorUuid.toString(), creatorName));
        this.createdAt = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new java.util.Date());
    }

    public String getName() { return name; }
    public void setName(String n) { name = n; }
    public List<MemberEntry> getOperators() { return operators; }
    public void setOperators(List<MemberEntry> o) { operators = o; }
    public List<MemberEntry> getMembers() { return members; }
    public void setMembers(List<MemberEntry> m) { members = m; }
    public List<MemberApplication> getMembersapplications() { return membersapplications; }
    public void setMembersapplications(List<MemberApplication> a) { membersapplications = a; }
    public long getFunds() { return funds; }
    public void setFunds(long f) { funds = f; }
    public long getActivity() { return activity; }
    public void setActivity(long a) { activity = a; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String c) { createdAt = c; }
    public String getNotice() { return notice != null ? notice : ""; }
    public void setNotice(String n) { notice = n; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean p) { isPublic = p; }
    public boolean isAllowFriendlyFire() { return allowFriendlyFire; }
    public void setAllowFriendlyFire(boolean f) { allowFriendlyFire = f; }
    public Map<String, WarpPoint> getWarpPoints() { return warpPoints; }
    public void setWarpPoints(Map<String, WarpPoint> w) { warpPoints = w; }
    public int getMemberCount() { return operators.size() + members.size(); }

    public static class MemberEntry {
        private String uuid; private String name;
        public MemberEntry() {}
        public MemberEntry(String u, String n) { uuid = u; name = n; }
        public String getUuid() { return uuid; }
        public void setUuid(String u) { uuid = u; }
        public String getName() { return name; }
        public void setName(String n) { name = n; }
        public UUID getUniqueId() { return uuid != null ? UUID.fromString(uuid) : null; }
    }

    public static class MemberApplication {
        private String uuid; private String name; private String AppliedAt;
        public MemberApplication() {}
        public MemberApplication(String u, String n) { uuid = u; name = n; AppliedAt = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new java.util.Date()); }
        public String getUuid() { return uuid; }
        public void setUuid(String u) { uuid = u; }
        public String getName() { return name; }
        public void setName(String n) { name = n; }
        public String getAppliedAt() { return AppliedAt; }
        public void setAppliedAt(String a) { AppliedAt = a; }
        public UUID getUniqueId() { return uuid != null ? UUID.fromString(uuid) : null; }
    }

    public static class WarpPoint {
        private int x, y, z, dim;
        private String creatorUuid, creatorName, createdAt;
        public WarpPoint() {}
        public WarpPoint(int x, int y, int z, int d, String cu, String cn) { this.x=x;this.y=y;this.z=z;dim=d;creatorUuid=cu;creatorName=cn;createdAt=new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new java.util.Date()); }
        public int getX() { return x; } public void setX(int v) { x=v; }
        public int getY() { return y; } public void setY(int v) { y=v; }
        public int getZ() { return z; } public void setZ(int v) { z=v; }
        public int getDim() { return dim; } public void setDim(int v) { dim=v; }
        public String getCreatorUuid() { return creatorUuid; } public void setCreatorUuid(String v) { creatorUuid=v; }
        public String getCreatorName() { return creatorName; } public void setCreatorName(String v) { creatorName=v; }
        public String getCreatedAt() { return createdAt; } public void setCreatedAt(String v) { createdAt=v; }
    }
}
'@

Write-JavaFile "model\MessageEntry.java" @'
package cn.gmzc.mgteam.model;

public class MessageEntry {
    private String senderUuid;
    private String senderName;
    private String content;
    private String time;
    private long timestamp;

    public MessageEntry() {}
    public MessageEntry(String su, String sn, String c) {
        senderUuid=su; senderName=sn; content=c;
        time=new java.text.SimpleDateFormat("yyyy-MM-dd'\''T'\''HH:mm:ss.SSS'\''Z'\''").format(new java.util.Date());
        timestamp=System.currentTimeMillis();
    }
    public String getSenderUuid() { return senderUuid; }
    public void setSenderUuid(String v) { senderUuid=v; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String v) { senderName=v; }
    public String getContent() { return content; }
    public void setContent(String v) { content=v; }
    public String getTime() { return time; }
    public void setTime(String v) { time=v; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long v) { timestamp=v; }
}
'@

Write-JavaFile "model\FundLogEntry.java" @'
package cn.gmzc.mgteam.model;

public class FundLogEntry {
    private long timestamp; private String time;
    private long change; private String reason;
    private long balanceBefore; private long balanceAfter;
    public FundLogEntry() {}
    public FundLogEntry(long c, String r, long bb, long ba) {
        timestamp=System.currentTimeMillis();
        time=new java.text.SimpleDateFormat("yyyy-MM-dd'\''T'\''HH:mm:ss.SSS'\''Z'\''").format(new java.util.Date());
        change=c; reason=r; balanceBefore=bb; balanceAfter=ba;
    }
    public long getTimestamp() { return timestamp; } public void setTimestamp(long v) { timestamp=v; }
    public String getTime() { return time; } public void setTime(String v) { time=v; }
    public long getChange() { return change; } public void setChange(long v) { change=v; }
    public String getReason() { return reason; } public void setReason(String v) { reason=v; }
    public long getBalanceBefore() { return balanceBefore; } public void setBalanceBefore(long v) { balanceBefore=v; }
    public long getBalanceAfter() { return balanceAfter; } public void setBalanceAfter(long v) { balanceAfter=v; }
}
'@

# ==================== UTIL ====================

Write-JavaFile "util\Util.java" @'
package cn.gmzc.mgteam.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.World;

public class Util {
    public static final String S = "\u00a7"; // §

    public static String generateTeamId(Set<String> existing) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String nums = "0123456789";
        String all = chars + nums;
        for (int a = 0; a < 100; a++) {
            StringBuilder sb = new StringBuilder();
            sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
            for (int i = 0; i < 3; i++) sb.append(all.charAt(ThreadLocalRandom.current().nextInt(all.length())));
            String id = sb.toString();
            if (!existing.contains(id)) return id;
        }
        return null;
    }

    public static String dimName(int dim) {
        switch (dim) { case -1: return "\u4e0b\u754c"; case 1: return "\u672b\u5730"; default: return "\u4e3b\u4e16\u754c"; }
    }

    public static int dimId(World w) {
        switch (w.getEnvironment()) { case NETHER: return -1; case THE_END: return 1; default: return 0; }
    }

    public static String timeAgo(String iso) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'\''T'\''HH:mm:ss", Locale.US);
            Date d = sdf.parse(iso.length()>19?iso.substring(0,19):iso);
            long diff = System.currentTimeMillis()-d.getTime();
            long mins = diff/60000, hrs = diff/3600000, days = diff/86400000;
            if(mins<1)return "\u521a\u521a";
            if(mins<60)return mins+"\u5206\u949f\u524d";
            if(hrs<24)return hrs+"\u5c0f\u65f6\u524d";
            if(days<7)return days+"\u5929\u524d";
            return new SimpleDateFormat("M\u6708d\u65e5 HH:mm",Locale.CHINA).format(d);
        } catch(Exception e) { return "\u672a\u77e5\u65f6\u95f4"; }
    }

    public static String pName(String n) {
        return (n==null||n.isEmpty()||"\u00a7null".equals(n))?S+"7(\u8be5\u73a9\u5bb6\u957f\u671f\u672a\u4e0a\u7ebf\uff0c\u65e0\u6cd5\u663e\u793a\u540d\u79f0)":S+"f"+n;
    }

    public static String plainName(String n) {
        return (n==null||n.isEmpty()||"null".equals(n))?"(\u672a\u77e5\u73a9\u5bb6)":n;
    }

    public static String validateAmount(String input) {
        if(input==null||input.trim().isEmpty())return "\u8bf7\u8f93\u5165\u91d1\u989d\uff01";
        String t=input.trim();
        if(t.contains(".")||t.contains(","))return "\u91d1\u989d\u4e0d\u80fd\u5305\u542b\u5c0f\u6570\uff01";
        if(!t.matches("\\d+")) { if(t.contains("-"))return "\u91d1\u989d\u4e0d\u80fd\u4e3a\u8d1f\u6570\uff01"; return "\u8bf7\u8f93\u5165\u6709\u6548\u7684\u6b63\u6574\u6570\u91d1\u989d\uff01"; }
        long amt=Long.parseLong(t); if(amt<=0)return "\u91d1\u989d\u5fc5\u987b\u5927\u4e8e0\uff01"; if(amt>9007199254740991L)return "\u91d1\u989d\u8fc7\u5927\uff01";
        return null;
    }

    public static long parseAmount(String input) { return Long.parseLong(input.trim()); }
}
'@

# ==================== CONFIG ====================

Write-JavaFile "config\Config.java" @'
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
'@

Write-Host "Model, util, config files created."
Write-Host "Generator part 1 complete. Run part 2 next."
