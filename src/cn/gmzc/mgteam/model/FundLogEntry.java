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