package cn.gmzc.mgteam.data;

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

public class FundConsumeManager {
    private final File f; private final com.google.gson.Gson g; private final Logger l;
    private Map<String,CE> data=new LinkedHashMap<>();
    public FundConsumeManager(File folder,Logger log){f=new File(folder,"MGteamdata_fundconsume.json");g=new GsonBuilder().setPrettyPrinting().create();l=log;}
    public void load(){if(!f.exists())return;try(FileReader r=new FileReader(f)){Type t=new TypeToken<LinkedHashMap<String,CE>>(){}.getType();Map<String,CE>m=g.fromJson(r,t);if(m!=null)data=m;}catch(Exception e){l.warning("[MGTeam] \u52a0\u8f7d\u6d88\u8d39\u5f00\u5173\u5931\u8d25");}}
    public void save(){try(FileWriter w=new FileWriter(f)){g.toJson(data,w);}catch(Exception e){l.warning("[MGTeam] \u4fdd\u5b58\u6d88\u8d39\u5f00\u5173\u5931\u8d25");}}
    public boolean getStatus(UUID u){CE e=data.get(u.toString());return e!=null&&e.enabled;}
    public void setStatus(UUID u,boolean en){CE e=new CE();e.enabled=en;e.updatedAt=new java.text.SimpleDateFormat("yyyy-MM-dd'\''T'\''HH:mm:ss.SSS'\''Z'\''").format(new java.util.Date());data.put(u.toString(),e);}
    public void remove(UUID u){data.remove(u.toString());}
    static class CE{boolean enabled;String updatedAt;}
}