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
    private long noticeUpdatedAt;
    private boolean isPublic = true;
    private boolean allowFriendlyFire = true;
    private Map<String, WarpPoint> warpPoints = new LinkedHashMap<>();

    public Team() {}
    public Team(String name, UUID creatorUuid, String creatorName) {
        this.name = name;
        this.operators.add(new MemberEntry(creatorUuid.toString(), creatorName));
        long now = System.currentTimeMillis();
        this.createdAt = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new java.util.Date(now));
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
    public void setNotice(String n) {
        String next = n != null ? n : "";
        if (!next.equals(getNotice())) {
            noticeUpdatedAt = System.currentTimeMillis();
        }
        notice = next;
    }
    public long getNoticeUpdatedAt() { return noticeUpdatedAt; }
    public void setNoticeUpdatedAt(long value) { noticeUpdatedAt = value; }
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
        private String icon = "COMPASS";
        private String world = "";
        public WarpPoint() {}
        public WarpPoint(int x, int y, int z, int d, String cu, String cn) { this(x,y,z,d,cu,cn,"COMPASS",""); }
        public WarpPoint(int x, int y, int z, int d, String cu, String cn, String icon) { this(x,y,z,d,cu,cn,icon,""); }
        public WarpPoint(int x, int y, int z, int d, String cu, String cn, String icon, String world) { this.x=x;this.y=y;this.z=z;dim=d;creatorUuid=cu;creatorName=cn;this.icon=icon;this.world=world;createdAt=new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new java.util.Date()); }
        public int getX() { return x; } public void setX(int v) { x=v; }
        public int getY() { return y; } public void setY(int v) { y=v; }
        public int getZ() { return z; } public void setZ(int v) { z=v; }
        public int getDim() { return dim; } public void setDim(int v) { dim=v; }
        public String getCreatorUuid() { return creatorUuid; } public void setCreatorUuid(String v) { creatorUuid=v; }
        public String getCreatorName() { return creatorName; } public void setCreatorName(String v) { creatorName=v; }
        public String getCreatedAt() { return createdAt; } public void setCreatedAt(String v) { createdAt=v; }
        public String getIcon() { return icon != null ? icon : "COMPASS"; }
        public void setIcon(String v) { icon = v; }
        public String getWorld() { return world != null ? world : ""; }
        public void setWorld(String v) { world = v; }
        public String getWorldDisplay() {
            String w = getWorld();
            if (!w.isEmpty()) return w;
            switch (dim) { case -1: return "\u4e0b\u754c"; case 1: return "\u672b\u5730"; default: return "\u4e3b\u4e16\u754c"; }
        }
    }
}
