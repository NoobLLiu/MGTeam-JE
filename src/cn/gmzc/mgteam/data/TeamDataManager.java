package cn.gmzc.mgteam.data;

import cn.gmzc.mgteam.model.Team;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class TeamDataManager {
    private final File file; private final com.google.gson.Gson gson; private final Logger log;
    private Map<String,Team> data = new LinkedHashMap<>();
    public TeamDataManager(File folder, Logger l) { file=new File(folder,"MGteamdata.json"); gson=new GsonBuilder().setPrettyPrinting().create(); log=l; }
    public void load() {
        if(!file.exists()){data=new LinkedHashMap<>();return;}
        try(FileReader r=new FileReader(file)){ Type t=new TypeToken<LinkedHashMap<String,Team>>(){}.getType(); Map<String,Team> m=gson.fromJson(r,t); if(m!=null){data=m;for(Team tm:data.values()){if(tm.getWarpPoints()==null)tm.setWarpPoints(new LinkedHashMap<>());}} log.info("[MGTeam] \u5df2\u52a0\u8f7d\u56e2\u961f\u6570\u636e\uff0c\u5171"+data.size()+"\u4e2a\u56e2\u961f"); }
        catch(Exception e){log.warning("[MGTeam] \u52a0\u8f7d\u56e2\u961f\u6570\u636e\u5931\u8d25: "+e.getMessage());data=new LinkedHashMap<>();}
    }
    public void save(){try(FileWriter w=new FileWriter(file)){gson.toJson(data,w);}catch(Exception e){log.warning("[MGTeam] \u4fdd\u5b58\u5931\u8d25: "+e.getMessage());}}
    public Map<String,Team> getAll(){return data;}
    public Team get(String id){return data.get(id);}
    /**
     * Resolve a player-entered team ID to the canonical key stored in the data
     * map. Team IDs are displayed with mixed case, but lookup should not depend
     * on a player preserving that case (especially on Bedrock keyboards).
     *
     * The exact key is checked first so existing data remains deterministic if
     * a legacy file ever contains IDs that differ only by case.
     */
    public String resolveId(String rawId){
        if(rawId==null)return null;
        String id=rawId.trim();
        if(id.isEmpty())return null;
        if(data.containsKey(id))return id;
        for(String key:data.keySet())if(key!=null&&key.equalsIgnoreCase(id))return key;
        return null;
    }
    public Team resolve(String rawId){
        String id=resolveId(rawId);
        return id==null?null:data.get(id);
    }
    public void put(String id,Team t){data.put(id,t);}
    public Team remove(String id){return data.remove(id);}
    public boolean containsId(String id){return data.containsKey(id);}
    public int size(){return data.size();}
    public String getPlayerTeamId(UUID uuid){String s=uuid.toString();for(Map.Entry<String,Team> e:data.entrySet()){Team t=e.getValue();for(Team.MemberEntry m:t.getOperators()){if(s.equals(m.getUuid()))return e.getKey();}for(Team.MemberEntry m:t.getMembers()){if(s.equals(m.getUuid()))return e.getKey();}}return null;}
    public boolean isPlayerInTeam(UUID uuid){return getPlayerTeamId(uuid)!=null;}
    public boolean isTeamOperator(UUID uuid,String tid){Team t=data.get(tid);if(t==null)return false;String s=uuid.toString();for(Team.MemberEntry m:t.getOperators()){if(s.equals(m.getUuid()))return true;}return false;}
    public boolean nameExists(String name){for(Team t:data.values()){if(t.getName().equalsIgnoreCase(name))return true;}return false;}
}
