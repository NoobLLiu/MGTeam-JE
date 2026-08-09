package cn.gmzc.mgteam.data;

import cn.gmzc.mgteam.model.MessageEntry;
import cn.gmzc.mgteam.model.Team;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MessageDataManager {
    private final File f; private final com.google.gson.Gson g; private final Logger l;
    private Map<String,List<MessageEntry>> msgs=new LinkedHashMap<>();
    private Map<String,Long> views=new LinkedHashMap<>();
    private Map<String,Long> noticeViews=new LinkedHashMap<>();
    private boolean noticeViewsMigrated;
    private int max=100;
    public MessageDataManager(File folder,Logger log){f=new File(folder,"MGteamdata_messages.json");g=new GsonBuilder().setPrettyPrinting().create();l=log;}
    public void setMaxStored(int m){max=m;}
    public void load(){
        if(!f.exists())return;
        try(FileReader r=new FileReader(f)){Type t=new TypeToken<LinkedHashMap<String,Object>>(){}.getType();Map<String,Object>root=g.fromJson(r,t);if(root==null)return;
            if(root.containsKey("messages")){String j=g.toJson(root.get("messages"));Type lt=new TypeToken<LinkedHashMap<String,List<MessageEntry>>>(){}.getType();Map<String,List<MessageEntry>>m=g.fromJson(j,lt);if(m!=null)msgs=m;}
            if(root.containsKey("views")){String j=g.toJson(root.get("views"));Type vt=new TypeToken<LinkedHashMap<String,Long>>(){}.getType();Map<String,Long>m=g.fromJson(j,vt);if(m!=null)views=m;}
            noticeViewsMigrated=Boolean.TRUE.equals(root.get("noticeViewsMigrated"));
            if(root.containsKey("noticeViews")){String j=g.toJson(root.get("noticeViews"));Type vt=new TypeToken<LinkedHashMap<String,Long>>(){}.getType();Map<String,Long>m=g.fromJson(j,vt);if(m!=null)noticeViews=m;}
        }catch(Exception e){l.warning("[MGTeam] \u52a0\u8f7d\u7559\u8a00\u677f\u5931\u8d25: "+e.getMessage());}
    }
    public void save(){try(FileWriter w=new FileWriter(f)){Map<String,Object>root=new LinkedHashMap<>();root.put("messages",msgs);root.put("views",views);root.put("noticeViews",noticeViews);root.put("noticeViewsMigrated",noticeViewsMigrated);g.toJson(root,w);}catch(Exception e){l.warning("[MGTeam] \u4fdd\u5b58\u7559\u8a00\u677f\u5931\u8d25: "+e.getMessage());}}
    public boolean migrateLegacyNoticeViews(Map<String,Team> teams){
        if(noticeViewsMigrated)return false;
        noticeViewsMigrated=true;
        boolean changed=true;
        for(Map.Entry<String,Team> entry:teams.entrySet()){
            String tid=entry.getKey();
            Team team=entry.getValue();
            long updatedAt=team.getNoticeUpdatedAt();
            if(team.getNotice().isBlank()||updatedAt<=0)continue;
            if(team.getOperators()!=null)for(Team.MemberEntry member:team.getOperators()){
                changed|=seedNoticeView(tid,member,updatedAt);
            }
            if(team.getMembers()!=null)for(Team.MemberEntry member:team.getMembers()){
                changed|=seedNoticeView(tid,member,updatedAt);
            }
        }
        return changed;
    }
    private boolean seedNoticeView(String tid,Team.MemberEntry member,long updatedAt){
        if(member==null||member.getUuid()==null)return false;
        String key=tid+"_"+member.getUuid();
        if(noticeViews.containsKey(key))return false;
        noticeViews.put(key,updatedAt);
        return true;
    }
    public List<MessageEntry> getMessages(String tid){return msgs.computeIfAbsent(tid,k->new ArrayList<>());}
    public void addMessage(String tid,MessageEntry e){List<MessageEntry>list=getMessages(tid);list.add(0,e);if(list.size()>max)list.subList(max,list.size()).clear();}
    public void deleteTeamMessages(String tid){msgs.remove(tid);}
    public boolean hasNewMessages(UUID uuid,String tid){String k=tid+"_"+uuid;long lv=views.getOrDefault(k,0L);List<MessageEntry>list=getMessages(tid);return !list.isEmpty()&&list.get(0).getTimestamp()>lv;}
    public void setLastViewTime(UUID uuid,String tid){views.put(tid+"_"+uuid,System.currentTimeMillis());}
    public boolean hasNewNotice(UUID uuid,String tid,long updatedAt){return updatedAt>0&&updatedAt>noticeViews.getOrDefault(tid+"_"+uuid,0L);}
    public void setNoticeRead(UUID uuid,String tid,long updatedAt){if(updatedAt>0)noticeViews.put(tid+"_"+uuid,updatedAt);}
}
