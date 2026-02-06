/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.login;

import constants.GameConstants;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
//import server.ServerProperties;
import tools.Triple;

public class LoginInformationProvider {

    public enum JobType {
        UltimateAdventurer(-1, "Ultimate", 0, 130000000),
        Resistance(0, "Resistance", 3000, 931000000),
        Adventurer(1, "Adventurer", 0, 0),
        Cygnus(2, "Premium", 1000, 130030000),
        Aran(3, "Orient", 2000, 914000000),
        Evan(4, "Evan", 2001, 900090000),
        Mercedes(5, "Mercedes", 2002, 910150000),
        Demon(6, "Demon", 3001, 927000000),
        Phantom(7, "Phantom", 2003, 915000000),
        DualBlade(8, "DualBlade", 0, 103050900),
        Mihile(9, "Mihile", 5000, 913070000),
        CannonShooter(13, "", 0, 3000600),
        Hayato(17, "Hayato", 6000, 507100000),
        Kanna(18, "Kanna", 6001, 507100100),
        Mukhyun(19, "Mukhyun", 6500, 910000000),
        //Mukhyun(19, "Mukhyun", 6002, 875003000),
        BeastTamer(20, "BeastTamer", 5200, 910000000),;
        
        public int type, id, map;
        public String job;

        private JobType(int type, String job, int id, int map) {
            this.type = type;
            this.job = job;
            this.id = id;
            this.map = map;
        }

        public static JobType getByJob(String g) {
            for (JobType e : JobType.values()) {
                if (e.job.length() > 0 && g.startsWith(e.job)) {
                    return e;
                }
            }
            return Adventurer;
        }

        public static JobType getByType(int g) {
            for (JobType e : JobType.values()) {
                if (e.type == g) {
                    return e;
                }
            }
            return Adventurer;
        }

        public static JobType getById(int g) {
            for (JobType e : JobType.values()) {
                if (e.id == g || (g == 508 && e.type == 8)) {
                    return e;
                }
            }
            return Adventurer;
        }
    }
    private final static LoginInformationProvider instance = new LoginInformationProvider();
    protected final List<String> ForbiddenName = new ArrayList<String>();
    //gender, val, job
    protected final Map<Triple<Integer, Integer, Integer>, List<Integer>> makeCharInfo = new HashMap<Triple<Integer, Integer, Integer>, List<Integer>>();
    //0 = eyes 1 = hair 2 = haircolor 3 = skin 4 = top 5 = bottom 6 = shoes 7 = weapon

    public static LoginInformationProvider getInstance() {
        return instance;
    }

    protected LoginInformationProvider() {
        final String WZpath = System.getProperty("net.sf.odinms.wzpath");
        final MapleDataProvider prov = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz"));
        MapleData nameData = prov.getData("ForbiddenName.img");
        for (final MapleData data : nameData.getChildren()) {
            ForbiddenName.add(MapleDataTool.getString(data));
        }
        nameData = prov.getData("Curse.img");
        for (final MapleData data : nameData.getChildren()) {
            ForbiddenName.add(MapleDataTool.getString(data).split(",")[0]);
        }
        final MapleData infoData = prov.getData("MakeCharInfo.img");
        for (MapleData dat : infoData) {
            try {
                final int type = JobType.getById(Integer.parseInt(dat.getName())).type;
                for (MapleData d : dat) {
                    int val;
                    if (d.getName().endsWith("male")) {
                        val = 0;
                    } else if (d.getName().endsWith("female")) {
                        val = 1;
                    } else {
                        continue;
                    }
                    for (MapleData da : d) {
                        final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(val, Integer.parseInt(da.getName()), type);
                        List<Integer> our = makeCharInfo.get(key);
                        if (our == null) {
                            our = new ArrayList<Integer>();
                            makeCharInfo.put(key, our);
                        }
                        for (MapleData dd : da) {
                            our.add(MapleDataTool.getInt(dd, -1));
                        }
                    }
                }
            } catch (NumberFormatException e) {
            }
        }
        final MapleData uA = infoData.getChildByPath("UltimateAdventurer");
        for (MapleData dat : uA) {
            final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(-1, Integer.parseInt(dat.getName()), JobType.UltimateAdventurer.type);
            List<Integer> our = makeCharInfo.get(key);
            if (our == null) {
                our = new ArrayList<Integer>();
                makeCharInfo.put(key, our);
            }
            for (MapleData d : dat) {
                our.add(MapleDataTool.getInt(d, -1));
            }
        }
    }

    /*  public static boolean isExtendedSpJob(int jobId) {
        return (jobId / 1000 == 3 
         || jobId / 100 == 22
         || jobId == 2001
         || jobId / 100 == 23
         || jobId == 2002
         || jobId / 100 == 24 
         || jobId == 2003
         || jobId / 100 == 51
         || jobId / 1000 == 5
         || jobId / 100 == 27 
         || jobId == 2004
         || jobId / 100 == 61
         || jobId / 1000 == 6);
    }*/
    public static boolean isExtendedSpJob(int jobId) {
        return GameConstants.isSeparatedSp(jobId);
    }

    public final boolean isForbiddenName(final String in) {
        for (final String name : ForbiddenName) {
            if (in.toLowerCase().contains(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public final boolean isEligibleItem(final int gender, final int val, final int job, final int item) {
        if (item < 0) {
            return false;
        }
        final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(gender, val, job);
        final List<Integer> our = makeCharInfo.get(key);
        if (our == null) {
            return false;
        }
        return our.contains(item);
    }
}
