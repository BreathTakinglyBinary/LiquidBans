/*
 * (C) Copyright 2018 The DKBans Project (Davide Wietlisbach)
 *
 * @author Davide Wietlisbach
 * @since 30.12.18 14:39
 * @Website https://github.com/DevKrieger/DKBans
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package ch.dkrieger.bansystem.lib.reason;

import ch.dkrieger.bansystem.lib.DKBansPlatform;
import ch.dkrieger.bansystem.lib.Messages;
import ch.dkrieger.bansystem.lib.player.history.BanType;
import ch.dkrieger.bansystem.lib.utils.Document;
import ch.dkrieger.bansystem.lib.utils.Duration;
import ch.dkrieger.bansystem.lib.utils.GeneralUtil;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReasonProvider {

    private DKBansPlatform platform;
    private List<KickReason> kickReasons;
    private List<BanReason> banReasons;
    private List<ReportReason> reportReasons;
    private List<UnbanReason> unbanReasons;
    private List<WarnReason> warnReasons;

    public ReasonProvider(DKBansPlatform platform){
        this.platform = platform;
        this.banReasons = new ArrayList<>();
        this.kickReasons = new ArrayList<>();
        this.reportReasons = new ArrayList<>();
        this.unbanReasons = new ArrayList<>();
        this.warnReasons = new ArrayList<>();
        loadBanReasons();
        //loadUnbanReasons();
        loadReportReasons();
        loadKickReasons();
        loadWarnReasons();
    }
    public ReasonProvider(DKBansPlatform platform,List<KickReason> kickReasons, List<BanReason> banReasons, List<ReportReason> reportReasons, List<UnbanReason> unbanReasons) {
        this.platform = platform;
        this.kickReasons = kickReasons;
        this.banReasons = banReasons;
        this.reportReasons = reportReasons;
        this.unbanReasons = unbanReasons;
    }
    public List<KickReason> getKickReasons() {
        return kickReasons;
    }
    public List<BanReason> getBanReasons() {
        return banReasons;
    }
    public List<ReportReason> getReportReasons() {
        return reportReasons;
    }
    public List<UnbanReason> getUnbanReasons() {
        return unbanReasons;
    }
    public List<WarnReason> getWarnReasons() {
        return warnReasons;
    }

    public KickReason searchKickReason(String search){
        if(GeneralUtil.isNumber(search)) return getKickReason(Integer.valueOf(search));
        return getKickReason(search);
    }
    public KickReason getKickReason(int id){
      return GeneralUtil.iterateOne(this.kickReasons,reason -> reason.getID() == id);
    }
    public KickReason getKickReason(String name){
        return GeneralUtil.iterateOne(this.kickReasons,reason -> reason.hasAlias(name));
    }

    public BanReason searchBanReason(String search){
        if(GeneralUtil.isNumber(search)) return getBanReason(Integer.valueOf(search));
        return getBanReason(search);
    }

    public BanReason getBanReason(int id){
        return GeneralUtil.iterateOne(this.banReasons,reason -> reason.getID() == id);
    }


    public BanReason getBanReason(String name){
        return GeneralUtil.iterateOne(this.banReasons,reason -> reason.hasAlias(name));
    }

    public ReportReason searchReportReason(String search){
        if(GeneralUtil.isNumber(search)) return getReportReason(Integer.valueOf(search));
        return getReportReason(search);
    }

    public ReportReason getReportReason(int id){
        return GeneralUtil.iterateOne(this.reportReasons,reason -> reason.getID() == id);
    }
    public ReportReason getReportReason(String name){
        return GeneralUtil.iterateOne(this.reportReasons,reason -> reason.hasAlias(name));
    }

    public UnbanReason searchUnbanReason(String search){
        if(GeneralUtil.isNumber(search)) return getUnbanReason(Integer.valueOf(search));
        return getUnbanReason(search);
    }
    public UnbanReason getUnbanReason(int id){
        return GeneralUtil.iterateOne(this.unbanReasons,reason -> reason.getID() == id);
    }
    public UnbanReason getUnbanReason(String name){
        return GeneralUtil.iterateOne(this.unbanReasons,reason -> reason.hasAlias(name));
    }

    public WarnReason searchWarnReason(String search){
        if(GeneralUtil.isNumber(search)) return getWarnReason(Integer.valueOf(search));
        return getWarnReason(search);
    }
    public WarnReason getWarnReason(int id){
        return GeneralUtil.iterateOne(this.warnReasons,reason -> reason.getID() == id);
    }
    public WarnReason getWarnReason(String name){
        return GeneralUtil.iterateOne(this.warnReasons,reason -> reason.hasAlias(name));
    }
    public void loadBanReasons(){
        File file = new File(this.platform.getFolder(),"ban-reasons.yml");
        Configuration config = null;
        if(file.exists()){
            try{
                config = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
                Configuration reasons = config.getSection("reasons");
                this.banReasons = new ArrayList<>();
                if(reasons != null){
                    for(String key : reasons.getKeys()){
                        try{
                            Map<Integer,BanReasonEntry> durations = new HashMap<>();
                            Configuration durationConfig = config.getSection("reasons."+key+".durations");
                            for(String DKey : durationConfig.getKeys()){
                                durations.put(Integer.valueOf(DKey),new BanReasonEntry(
                                        BanType.valueOf(config.getString("reasons."+key+".durations."+DKey+".type"))
                                        ,config.getLong("reasons."+key+".durations."+DKey+".time")
                                        ,TimeUnit.valueOf(config.getString("reasons."+key+".durations."+DKey+".unit"))));
                            }
                            this.banReasons.add(new BanReason(Integer.valueOf(key)
                                    ,config.getInt("reasons."+key+".points.points")
                                    ,config.getString("reasons."+key+".name")
                                    ,config.getString("reasons."+key+".display")
                                    ,config.getString("reasons."+key+".permission")
                                    ,config.getBoolean("reasons."+key+".hidden")
                                    ,config.getStringList("reasons."+key+".points.aliases")
                                    ,config.getDouble("reasons."+key+".points.divider")
                                    ,BanType.valueOf(config.getString("reasons."+key+".historytype"))
                                    ,durations));
                        }catch (Exception exception){
                            exception.printStackTrace();
                            System.out.println(Messages.SYSTEM_PREFIX+"Could not load ban-reason "+key);
                            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                        }
                    }
                }
                if(this.banReasons.size() > 0) return;
            }catch (Exception exception){
                if(file.exists()){
                    file.renameTo(new File(this.platform.getFolder(),"ban-reasons-old-"+GeneralUtil.getRandomString(15)+".yml"));
                    System.out.println(Messages.SYSTEM_PREFIX+"Could not load ban-reasons, generating new (Saved als old)");
                    System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                }
            }
        }
        config = new Configuration();
        this.banReasons.add(new BanReason(1,30,"hacking","&4Hacking","dkbans.ban.reason.hacking"
                ,false, Arrays.asList("hacks","hacker"),0.0, BanType.NETWORK
                ,new BanReasonEntry(BanType.NETWORK,30, TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.NETWORK,60, TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.NETWORK,90, TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.NETWORK,-1, TimeUnit.DAYS)));
        this.banReasons.add(new BanReason(2,15,"provocation","&4Provocation","dkbans.ban.reason.provocation"
                ,false, Arrays.asList("provocation","provo"),0.5,BanType.CHAT
                ,new BanReasonEntry(BanType.CHAT,30,TimeUnit.MINUTES)
                ,new BanReasonEntry(BanType.CHAT,4,TimeUnit.HOURS)
                ,new BanReasonEntry(BanType.CHAT,10,TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.CHAT,30,TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.NETWORK,10,TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.NETWORK,30,TimeUnit.DAYS)));

        this.banReasons.add(new BanReason(3,12,"insult","&4Insult","dkbans.ban.reason.insult"
                ,false, Arrays.asList("insult"),0.5,BanType.CHAT
                ,new BanReasonEntry(BanType.CHAT,5,TimeUnit.HOURS)
                ,new BanReasonEntry(BanType.CHAT,1,TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.CHAT,10,TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.CHAT,30,TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.NETWORK,10,TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.NETWORK,30,TimeUnit.DAYS)));

        this.banReasons.add(new BanReason(4,20,"spam/promotion","&4Spam/Promotion","dkbans.ban.reason.promotion"
                ,false, Arrays.asList("spam","spamming"),0.0,BanType.NETWORK
                ,new BanReasonEntry(BanType.NETWORK,3,TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.NETWORK,10,TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.NETWORK,30,TimeUnit.DAYS)
                ,new BanReasonEntry(BanType.NETWORK,-1,TimeUnit.DAYS)));

        this.banReasons.add(new BanReason(5,300,"permanent","&4Permanent","dkbans.ban.reason.permanent"
                ,false, Arrays.asList("spam","spamming"),0.0,BanType.NETWORK
                ,new BanReasonEntry(BanType.NETWORK,-1,TimeUnit.DAYS)));
        for(BanReason reason : this.banReasons){
            config.set("reasons."+reason.getID()+".name",reason.getName());
            config.set("reasons."+reason.getID()+".display",reason.getRawDisplay());
            config.set("reasons."+reason.getID()+".permission",reason.getPermission());
            config.set("reasons."+reason.getID()+".aliases",reason.getAliases());
            config.set("reasons."+reason.getID()+".hidden",reason.isHidden());
            config.set("reasons."+reason.getID()+".historytype",reason.getHistoryType().toString());
            config.set("reasons."+reason.getID()+".points.points",reason.getPoints());
            config.set("reasons."+reason.getID()+".points.divider",reason.getDivider());
            for(Map.Entry<Integer, BanReasonEntry> entry : reason.getDurations().entrySet()){
                config.set("reasons."+reason.getID()+".durations."+entry.getKey()+".type",entry.getValue().getType().toString());
                config.set("reasons."+reason.getID()+".durations."+entry.getKey()+".time",entry.getValue().getDuration().getTime());
                config.set("reasons."+reason.getID()+".durations."+entry.getKey()+".unit",entry.getValue().getDuration().getUnit().toString());
            }
        }
        try{
            YamlConfiguration.getProvider(YamlConfiguration.class).save(config,file);
        }catch (Exception e){}
    }
    public void loadUnbanReasons(){
        /*
        File file = new File(this.platform.getFolder(),"unban-reasons.yml");
        Configuration config = null;
        if(file.exists()){
            try{
                config = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
                Configuration reasons = config.getSection("reasons");
                this.unbanReasons = new ArrayList<>();
                if(reasons != null){
                    for(String key : reasons.getKeys()){
                        try{
                            this.unbanReasons.add(new UnbanReason(Integer.valueOf(key),config.getInt("points"   )))
                                    //int id, int points, String name, String display, String permission, boolean hidden, List<String> aliases, int maxPoints, boolean removeAllPoints
                            // , List<Integer> notForBanID, Duration maxDuration, Duration removeDuration, double durationDivider
                        }catch (Exception exception){
                            exception.printStackTrace();
                            System.out.println(Messages.SYSTEM_PREFIX+"Could not load unban-reason "+key);
                            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                        }
                    }
                }
                if(this.unbanReasons.size() > 0) return;
            }catch (Exception exception){
                if(file.exists()){
                    file.renameTo(new File(this.platform.getFolder(),"unban-reasons-old-"+GeneralUtil.getRandomString(15)+".yml"));
                    System.out.println(Messages.SYSTEM_PREFIX+"Could not load unban-reasons, generating new (Saved als old)");
                }
            }
        } //int id, int points, String name, String display, String permission, boolean hidden, List<String> aliases, int maxPoints, boolean removeAllPoints, List<Integer> notForBanID, Duration maxDuration, Duration removeDuration, double durationDivider
        config = new Configuration();
        this.unbanReasons.add(new UnbanReason(1,30,"falseban","&4False ban","dkbans.unban.reason.falsban"
                ,false, Arrays.asList("fals","hacker"),100,true,Arrays.asList(),new Duration(-1,TimeUnit.DAYS),new Duration(-1,TimeUnit.DAYS),0D));
        this.unbanReasons.add(new UnbanReason(1,10,"acceptedrequest","&4Accepted unban request","dkbans.unban.reason.acceptedrequest"
                ,false, Arrays.asList("fals","hacker"),100,true,Arrays.asList(),new Duration(-1,TimeUnit.DAYS),new Duration(-1,TimeUnit.DAYS),0D));
        this.unbanReasons.add(new UnbanReason(1,30,"falseban","&4False ban","dkbans.unban.reason.falsban"
                ,false, Arrays.asList("fals","hacker"),100,true,Arrays.asList(),new Duration(-1,TimeUnit.DAYS),new Duration(-1,TimeUnit.DAYS),0D));


        for(UnbanReason reason : this.unbanReasons){
            config.set("reasons."+reason.getID()+".name",reason.getName());
            config.set("reasons."+reason.getID()+".display",reason.getRawDisplay());
            config.set("reasons."+reason.getID()+".permission",reason.getPermission());
            config.set("reasons."+reason.getID()+".aliases",reason.getAliases());
            config.set("reasons."+reason.getID()+".hidden",reason.isHidden());
            config.set("reasons."+reason.getID()+".points.remove.points",reason.getPoints());
            config.set("reasons."+reason.getID()+".points.remove.all",reason.isRemoveAllPoints());
            config.set("reasons."+reason.getID()+".points.maximal",reason.getMaxPoints());
            config.set("reasons."+reason.getID()+".duration.divider",reason.getDurationDivider());
            config.set("reasons."+reason.getID()+".duration.remove.time",reason.getRemoveDuration().getTime());
            config.set("reasons."+reason.getID()+".duration.remove.unit",reason.getRemoveDuration().getUnit());
            config.set("reasons."+reason.getID()+".duration.maximal.time",reason.getMaxDuration().getTime());
            config.set("reasons."+reason.getID()+".duration.maximal.unit",reason.getMaxDuration().getUnit());
        }
        try{
            YamlConfiguration.getProvider(YamlConfiguration.class).save(config,file);
        }catch (Exception e){}
         */
    }
    public void loadReportReasons(){
        File file = new File(this.platform.getFolder(),"report-reasons.yml");
        Configuration config = null;
        if(file.exists()){
            try{
                config = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
                Configuration reasons = config.getSection("reasons");
                this.reportReasons = new ArrayList<>();
                if(reasons != null) {
                    for (String key : reasons.getKeys()) {
                        try{
                            this.reportReasons.add(new ReportReason(Integer.valueOf(key),0
                                    ,config.getString("reasons."+key+".name")
                                    ,config.getString("reasons."+key+".display")
                                    ,config.getString("reasons."+key+".permission")
                                    ,config.getBoolean("reasons."+key+".hidden")
                                    ,config.getStringList("reasons."+key+".aliases")
                                    ,config.getInt("reasons."+key+".forbanreason")));
                        }catch (Exception exception){
                            exception.printStackTrace();
                            System.out.println(Messages.SYSTEM_PREFIX+"Could not load report-reason "+key);
                            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                        }
                    }
                }
                if(this.reportReasons.size() > 0) return;
            }catch (Exception exception){
                if(file.exists()){
                    file.renameTo(new File(this.platform.getFolder(),"report-reasons-old-"+GeneralUtil.getRandomString(15)+".yml"));
                    System.out.println(Messages.SYSTEM_PREFIX+"Could not load report-reasons, generating new (Saved als old)");
                    System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                }
            }
        }
        config = new Configuration();
        this.reportReasons.add(new ReportReason(1,0,"hacking","&4Hacking","dkbans.report.reason.hacking"
                ,false,Arrays.asList("hacks","hacker"),1));
        this.reportReasons.add(new ReportReason(2,0,"provocation","&4Provocation","dkbans.report.reason.provocation"
                ,false,Arrays.asList("provocation","provo"),2));
        this.reportReasons.add(new ReportReason(3,0,"insult","&4Insult","dkbans.report.reason.insult"
                ,false,Arrays.asList("insult"),3));
        this.reportReasons.add(new ReportReason(4,0,"spam/provocation","&4Spam/Promotion","dkbans.report.reason.promotion"
                ,false,Arrays.asList("spam","spamming"),4));

        for(ReportReason reason : this.reportReasons){
            config.set("reasons."+reason.getID()+".name",reason.getName());
            config.set("reasons."+reason.getID()+".display",reason.getRawDisplay());
            config.set("reasons."+reason.getID()+".permission",reason.getPermission());
            config.set("reasons."+reason.getID()+".aliases",reason.getAliases());
            config.set("reasons."+reason.getID()+".hidden",reason.isHidden());
            config.set("reasons."+reason.getID()+".forbanreason",reason.getForBan());
        }
        try{
            YamlConfiguration.getProvider(YamlConfiguration.class).save(config,file);
        }catch (Exception e){}
    }
    public void loadKickReasons(){
        File file = new File(this.platform.getFolder(),"kick-reasons.yml");
        Configuration config = null;
        if(file.exists()){
            try{
                config = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
                Configuration reasons = config.getSection("reasons");
                this.kickReasons= new ArrayList<>();
                if(reasons != null) {
                    for (String key : reasons.getKeys()) {
                        try{
                            this.kickReasons.add(new KickReason(Integer.valueOf(key)
                                    ,config.getInt("reasons."+key+".points")
                                    ,config.getString("reasons."+key+".name")
                                    ,config.getString("reasons."+key+".display")
                                    ,config.getString("reasons."+key+".permission")
                                    ,config.getBoolean("reasons."+key+".hidden")
                                    ,config.getStringList("reasons."+key+".aliases")));
                        }catch (Exception exception){
                            exception.printStackTrace();
                            System.out.println(Messages.SYSTEM_PREFIX+"Could not load kick-reason "+key);
                            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                        }
                    }
                }
                if(this.kickReasons.size() > 0) return;
            }catch (Exception exception){
                if(file.exists()){
                    file.renameTo(new File(this.platform.getFolder(),"kick-reasons-old-"+GeneralUtil.getRandomString(15)+".yml"));
                    System.out.println(Messages.SYSTEM_PREFIX+"Could not load kick-reasons, generating new (Saved als old)");
                }
            }
        }//int id, int points, String name, String display, String permission, boolean hidden, List<String> aliases, int forban
        config = new Configuration();
        this.kickReasons.add(new KickReason(1,2,"provocation","&4Provocation","dkbans.kick.reason.provocation"
                ,false,Arrays.asList("provocation","provo")));
        this.kickReasons.add(new KickReason(2,2,"insult","&4Insult","dkbans.kick.reason.insult"
                ,false,Arrays.asList("insult")));
        this.kickReasons.add(new KickReason(3,5,"bugusing","&4Bugusing","dkbans.kick.reason.bugusing"
                ,false, Arrays.asList("bug","bugging")));
        this.kickReasons.add(new KickReason(4,0,"annoy","&4Annoy","dkbans.kick.reason.annoy"
                ,false, Arrays.asList("annoy")));

        for(KickReason reason : this.kickReasons){
            config.set("reasons."+reason.getID()+".name",reason.getName());
            config.set("reasons."+reason.getID()+".display",reason.getRawDisplay());
            config.set("reasons."+reason.getID()+".permission",reason.getPermission());
            config.set("reasons."+reason.getID()+".aliases",reason.getAliases());
            config.set("reasons."+reason.getID()+".hidden",reason.isHidden());
            config.set("reasons."+reason.getID()+".points",reason.getPoints());
        }
        try{
            YamlConfiguration.getProvider(YamlConfiguration.class).save(config,file);
        }catch (Exception e){}
    }
    public void loadWarnReasons(){
        File file = new File(this.platform.getFolder(),"warn-reasons.yml");
        Configuration config = null;
        if(file.exists()){
            try{
                config = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
                Configuration reasons = config.getSection("reasons");
                this.warnReasons= new ArrayList<>();
                if(reasons != null) {
                    for (String key : reasons.getKeys()) {
                        try{
                            this.warnReasons.add(new WarnReason(Integer.valueOf(key)
                                    ,config.getInt("reasons."+key+".points")
                                    ,config.getString("reasons."+key+".name")
                                    ,config.getString("reasons."+key+".display")
                                    ,config.getString("reasons."+key+".permission")
                                    ,config.getBoolean("reasons."+key+".hidden")
                                    ,config.getStringList("reasons."+key+".aliases")
                                    ,config.getInt("reasons."+key+".autoban.count")
                                    ,config.getInt("reasons."+key+".autoban.banid")));
                        }catch (Exception exception){
                            exception.printStackTrace();
                            System.out.println(Messages.SYSTEM_PREFIX+"Could not load warn-reason "+key);
                            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                        }
                    }
                }
                if(this.kickReasons.size() > 0) return;
            }catch (Exception exception){
                if(file.exists()){
                    file.renameTo(new File(this.platform.getFolder(),"warn-reasons-old-"+GeneralUtil.getRandomString(15)+".yml"));
                    System.out.println(Messages.SYSTEM_PREFIX+"Could not load warn-reasons, generating new (Saved als old)");
                }
            }
        }
        config = new Configuration();
        this.warnReasons.add(new WarnReason(1,2,"provocation","&4Provocation","dkbans.warn.reason.provocation"
                ,false,Arrays.asList("provocation","provo"),3,2));
        this.warnReasons.add(new WarnReason(2,2,"insult","&4Insult","dkbans.warn.reason.insult"
                ,false,Arrays.asList("insult"),3,3));
        this.warnReasons.add(new WarnReason(3,2,"spam/promotion","&4Spam/Promotion","dkbans.warn.reason.promotion"
                ,false,Arrays.asList("spamming","spam","promotion"),2,4));

        for(WarnReason reason : this.warnReasons){
            config.set("reasons."+reason.getID()+".name",reason.getName());
            config.set("reasons."+reason.getID()+".display",reason.getRawDisplay());
            config.set("reasons."+reason.getID()+".permission",reason.getPermission());
            config.set("reasons."+reason.getID()+".aliases",reason.getAliases());
            config.set("reasons."+reason.getID()+".hidden",reason.isHidden());
            config.set("reasons."+reason.getID()+".points",reason.getPoints());
            config.set("reasons."+reason.getID()+".autoban.count",reason.getAutoBanCount());
            config.set("reasons."+reason.getID()+".autoban.banid",reason.getForBan());
        }
        try{
            YamlConfiguration.getProvider(YamlConfiguration.class).save(config,file);
        }catch (Exception e){}
    }
}
