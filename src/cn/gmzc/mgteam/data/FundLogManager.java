package cn.gmzc.mgteam.data;

import cn.gmzc.mgteam.model.FundLogEntry;
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
import java.util.logging.Logger;

public class FundLogManager {
    private final File f; private final com.google.gson.Gson g; private final Logger l;
    private Map<String,List<FundLogEntry>> data=new LinkedHashMap<>();
    public FundLogManager(File folder,Logger log){f=new File(folder,"MGteamdata_fundlogs.json");g=new GsonBuilder().setPrettyPrinting().create();l=log;}
    public void load(){if(!f.exists())return;try(FileReader r=new FileReader(f)){Type t=new TypeToken<LinkedHashMap<String,List<FundLogEntry>>>(){}.getType();Map<String,List<FundLogEntry>>m=g.fromJson(r,t);if(m!=null)data=m;}catch(Exception e){l.warning("[MGTeam] \u52a0\u8f7d\u6d41\u6c34\u8d26\u5931\u8d25");}}
    public void save(){try(FileWriter w=new FileWriter(f)){g.toJson(data,w);}catch(Exception e){l.warning("[MGTeam] \u4fdd\u5b58\u6d41\u6c34\u8d26\u5931\u8d25");}}
    public void addLog(String tid,long ch,String r,long bb,long ba){List<FundLogEntry>list=data.computeIfAbsent(tid,k->new ArrayList<>());list.add(0,new FundLogEntry(ch,r,bb,ba));}
    public List<FundLogEntry> getLogs(String tid){return data.getOrDefault(tid,new ArrayList<>());}
    public void deleteTeamLogs(String tid){data.remove(tid);}
}