package cn.gmzc.mgteam.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.World;

public class Util {
    public static final String S = "\u00a7"; // 搂

    public static String generateTeamId(Set<String> existing) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String nums = "0123456789";
        String all = chars + nums;
        for (int a = 0; a < 100; a++) {
            StringBuilder sb = new StringBuilder();
            sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
            for (int i = 0; i < 3; i++) sb.append(all.charAt(ThreadLocalRandom.current().nextInt(all.length())));
            String id = sb.toString();
            boolean collision = false;
            for (String old : existing) {
                if (old != null && old.equalsIgnoreCase(id)) {
                    collision = true;
                    break;
                }
            }
            if (!collision) return id;
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
            if (iso != null) iso = iso.replace("'", "");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
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
