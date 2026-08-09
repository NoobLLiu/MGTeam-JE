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
        time=new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new java.util.Date());
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
