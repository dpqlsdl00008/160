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
 MERCHANTABILITY or FITESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client;

import client.linkskill.LinkSkillInfo;
import constants.GameConstants;
import constants.ServerConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import javax.script.ScriptEngine;
import database.DatabaseConnection;
import database.DatabaseException;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleGuildCharacter;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import server.maps.MapleMap;
import server.shops.IMaplePlayerShop;
import tools.FileoutputUtil;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import server.CharacterCardFactory;
import server.quest.MapleQuest;
import tools.MapleKMSEncryption;
import tools.Pair;
import tools.packet.CLogin;
import tools.packet.CWvsContext;

public class MapleClient {

    private static final long serialVersionUID = 9179541993413738569L;
    public static final byte LOGIN_NOTLOGGEDIN = 0,
            LOGIN_SERVER_TRANSITION = 1,
            LOGIN_LOGGEDIN = 2,
            LOGIN_WAITING = 3,
            CASH_SHOP_TRANSITION = 4,
            LOGIN_CS_LOGGEDIN = 5,
            CHANGE_CHANNEL = 6;
    public static final int DEFAULT_CHARSLOT = 3;
    public static final AttributeKey<MapleClient> CLIENTKEY = AttributeKey.valueOf("mapleclient_netty");
    //   public static final String CLIENT_KEY = "CLIENT";
    private MapleSession session;
    private MapleCharacter player;
    private int channel = 1, accId = -1, world, birthday;
    private int charslots = DEFAULT_CHARSLOT;
    private boolean loggedIn = false, serverTransition = false;
    private transient Calendar tempban = null;
    private String accountName, banreason;
    private transient long lastPong = 0, lastPing = 0;
    private boolean monitored = false, receiving = true;
    private boolean gm;
    private int ACash = 0, allowed = 0;
    private byte greason = 1, gender = -1;
    public transient short loginAttempt = 0;
    private transient List<Integer> allowedChar = new LinkedList<Integer>();
    private transient Map<String, ScriptEngine> engines = new HashMap<String, ScriptEngine>();
    private transient ScheduledFuture<?> idleTask = null;
    private transient String secondPassword, salt2, tempIP = ""; // To be used only on login
    private final transient Lock mutex = new ReentrantLock(true);
    private final transient Lock npc_mutex = new ReentrantLock();
    private long lastNpcClick = 0;
    private final static Lock login_mutex = new ReentrantLock(true);
    private Map<Integer, Pair<Short, Short>> charInfo = new LinkedHashMap<>();
    private boolean cs = false;
    private boolean stop = false;

    private final Deque<byte[]> toSendPacket = new ArrayDeque<>(128);
    private Channel socketChannel = null;
    private transient MapleKMSEncryption send, recv;
    private String macString;
    private transient Set<String> macs = new HashSet<>();
    private String tempMac = "";

    // 계정별 KeyValue, CustomData 왜 굳이 2개 쓰는지는 모르겠음, 누적과 현재 포인트를 따로 관리하고 싶었나 봄.
    private Map<String, String> keyValues = new HashMap<>();
    private Map<Integer, List<Pair<String, String>>> customDatas = new HashMap<>();
    private final Map<Integer, LinkSkillInfo> linkSkills = new LinkedHashMap<>();

    public MapleClient(Channel session, int channel, MapleKMSEncryption send, MapleKMSEncryption receive) throws IOException {

        this.socketChannel = session;
        this.session = new MapleSession(this);
        setChannel(channel);
        if (channel == -10) {
            cs = true;
        }
        this.send = send;
        this.recv = receive;
    }

    public Map<Integer, LinkSkillInfo> getLinkSkills() {
        return linkSkills;
    }

    public boolean checkConnection(int RchrId, int OchrId, int skillid) {
        for (LinkSkillInfo LinkInfo : linkSkills.values()) {
            if (LinkInfo.getRchrId() == RchrId && LinkInfo.getOchrId() == OchrId && LinkInfo.getSkillId() == skillid) {
                return true;
            }
        }
        return false;
    }

    public boolean alreadyLinked(int skillid) {
        for (LinkSkillInfo LinkInfo : linkSkills.values()) {
            if (LinkInfo.getSkillId() == skillid) {
                return true;
            }
        }
        return false;
    }

    public boolean alreadyLinked(int RchrId, int skillid) {
        for (LinkSkillInfo LinkInfo : linkSkills.values()) {
            if (LinkInfo.getRchrId() == RchrId && LinkInfo.getSkillId() == skillid) {
                return true;
            }
        }
        return false;
    }

    public LinkSkillInfo findLinkSkillInfo(int skillid) {
        for (Entry<Integer, LinkSkillInfo> LinkInfo : linkSkills.entrySet()) {
            if (LinkInfo.getKey() == skillid) {
                return LinkInfo.getValue();
            }
        }
        return null;
    }

    public boolean alreadyLinkingv(int RchrId, int skillid) {
        for (LinkSkillInfo LinkInfo : linkSkills.values()) {
            if (LinkInfo.getRchrId() == RchrId && LinkInfo.getSkillId() == skillid) {
                return true;
            }
        }
        return false;
    }

    public boolean alreadyLinking(int OchrId, int skillid) {
        for (LinkSkillInfo LinkInfo : linkSkills.values()) {
            if (LinkInfo.getOchrId() == OchrId && LinkInfo.getSkillId() == skillid) {
                return true;
            }
        }
        return false;
    }

    public int getLinkingCount(int OchrId, int skillid) {
        int count = 0;
        for (LinkSkillInfo LinkInfo : linkSkills.values()) {
            if (LinkInfo.getOchrId() == OchrId) {
                count++;
            }
        }
        return count;
    }

    public boolean canChange(int OchrId, int skillid) {
        boolean ischarge = true;
        for (LinkSkillInfo LinkInfo : linkSkills.values()) {
            if (LinkInfo.getOchrId() == OchrId && LinkInfo.getSkillId() == skillid) {
                ischarge = LinkInfo.getChangable();
            }
        }
        return ischarge;
    }

    public ArrayList<Integer> getLinkingSkills(int OchrId) {
        ArrayList<Integer> skillarray = new ArrayList<Integer>();
        for (int skillid : linkSkills.keySet()) {
            if (getRchrId(OchrId, skillid) != -1) {
                skillarray.add(skillid);
            }
        }
        return skillarray;
    }

    public ArrayList<Integer> getLinkedSkills(int RchrId) {
        ArrayList<Integer> skillarray = new ArrayList<Integer>();
        for (int skillid : linkSkills.keySet()) {
            if (getOchrId(RchrId, skillid) != -1) {
                skillarray.add(skillid);
            }
        }
        return skillarray;
    }

    public int getOchrId(int RchrId, int skillid) {
        for (LinkSkillInfo LinkInfo : linkSkills.values()) {
            if (checkConnection(RchrId, LinkInfo.getOchrId(), skillid)) {
                return LinkInfo.getOchrId();
            }
        }
        return -1;
    }

    public int getRchrId(int OchrId, int skillid) {
        for (LinkSkillInfo LinkInfo : linkSkills.values()) {
            if (checkConnection(LinkInfo.getRchrId(), OchrId, skillid)) {
                return LinkInfo.getRchrId();
            }
        }
        return -1;
    }

    public void addLinkSkill(int RchrId, int OchrId, int skillid, long length, long starttime) {
        LinkSkillInfo id = new LinkSkillInfo(RchrId, OchrId, skillid, length, starttime);
        linkSkills.put(skillid, id);
    }

    public void loadLinkSkillStorage() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        linkSkills.clear();
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM skills_linked WHERE id = ?");
            ps.setInt(1, accId);
            rs = ps.executeQuery();
            while (rs.next()) {
                LinkSkillInfo id = new LinkSkillInfo(rs.getInt("RchrId"), rs.getInt("OchrId"), rs.getInt("SkillId"), rs.getLong("length"), rs.getLong("StartTime"));
                if (id.getLength() + id.getStartTime() - System.currentTimeMillis() <= 0) {
                    id.setChangable(true);
                }
                linkSkills.put(rs.getInt("SkillId"), id);
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public void saveLinkedSkill(Connection con) {
        try {
            if (linkSkills != null) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM skills_linked WHERE `id` = ?");
                ps.setInt(1, accId);
                ps.executeUpdate();
                ps.close();

                PreparedStatement pse = con.prepareStatement("INSERT INTO skills_linked (id, RchrId, OchrId, SkillId, length, StartTime) VALUES (?, ?, ?, ?, ?, ?)");
                pse.setInt(1, accId);

                for (Entry<Integer, LinkSkillInfo> keyValue : linkSkills.entrySet()) {
                    pse.setInt(2, keyValue.getValue().getRchrId());
                    pse.setInt(3, keyValue.getValue().getOchrId());
                    pse.setInt(4, keyValue.getValue().getSkillId());
                    pse.setLong(5, keyValue.getValue().getLength());
                    pse.setLong(6, keyValue.getValue().getStartTime());
                    pse.execute();
                }
                pse.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    public Map<String, String> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(Map<String, String> keyValues) {
        this.keyValues = keyValues;
    }

    public String getKeyValue(String key) {
        if (keyValues.containsKey(key)) {
            return keyValues.get(key);
        }
        return null;
    }

    public void setKeyValue(String key, String value) {
        keyValues.put(key, value);
    }

    public void removeKeyValue(String key) {
        keyValues.remove(key);
    }

    public void loadKeyValues() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        keyValues.clear();
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM acckeyvalue WHERE id = ?");
            ps.setInt(1, accId);
            rs = ps.executeQuery();
            while (rs.next()) {
                keyValues.put(rs.getString("key"), rs.getString("value"));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public void saveKeyValueToDB(Connection con) {
        try {
            if (keyValues != null) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM acckeyvalue WHERE `id` = ?");
                ps.setInt(1, accId);
                ps.executeUpdate();
                ps.close();

                PreparedStatement pse = con.prepareStatement("INSERT INTO acckeyvalue (`id`, `key`, `value`) VALUES (?, ?, ?)");
                pse.setInt(1, accId);

                for (Entry<String, String> keyValue : keyValues.entrySet()) {
                    pse.setString(2, keyValue.getKey());
                    pse.setString(3, keyValue.getValue());
                    pse.execute();
                }
                pse.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    public Map<Integer, List<Pair<String, String>>> getCustomDatas() {
        return customDatas;
    }

    public String getCustomData(int id, String key) {
        if (customDatas.containsKey(id)) {
            for (Pair<String, String> datas : customDatas.get(id)) {
                if (datas.left.equals(key)) {
                    return datas.right;
                }
            }
        }
        return null;
    }

    public void setCustomData(int id, String key, String value) {
        if (customDatas.containsKey(id)) {
            for (Pair<String, String> datas : customDatas.get(id)) {
                if (datas.getLeft().equals(key)) {
                    datas.right = value;
                    session.write(CWvsContext.InfoPacket.updateClientInfoQuest(id, key + "=" + value));
                    return;
                }
            }
            customDatas.get(id).add(new Pair<>(key, value));
        } else {
            List<Pair<String, String>> datas = new ArrayList<>();
            datas.add(new Pair<>(key, value));
            customDatas.put(id, datas);
        }

        session.write(CWvsContext.InfoPacket.updateClientInfoQuest(id, key + "=" + value));
    }

    public void loadCustomDatas() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        customDatas.clear();
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM account_customdata WHERE accid = ?");
            ps.setInt(1, accId);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                if (!customDatas.containsKey(id)) {
                    //customDatas.put(id, new ArrayList<>());
                }

                customDatas.get(id).add(new Pair<>(rs.getString("key"), rs.getString("value")));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public void saveCustomDataToDB(Connection con) {
        try {
            if (customDatas != null) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM account_customdata WHERE `accid` = ?");
                ps.setInt(1, accId);
                ps.executeUpdate();
                ps.close();

                PreparedStatement pse = con.prepareStatement("INSERT INTO account_customdata (`accid`, `id`, `key`, `value`) VALUES (?, ?, ?, ?)");
                pse.setInt(1, accId);

                for (Entry<Integer, List<Pair<String, String>>> customData : customDatas.entrySet()) {
                    pse.setInt(2, customData.getKey());
                    for (Pair<String, String> data : customData.getValue()) {
                        pse.setString(3, data.left);
                        pse.setString(4, data.right);
                        pse.execute();
                    }
                }
                pse.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    public int getHPoint() {
        try {
            return Integer.parseInt(getKeyValue("HPoint"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void gainHPointR(int value) {
        if (player != null) {
            int amount = getKeyValue("HPoint") != null ? Integer.parseInt(getKeyValue("HPoint")) : 0;
            setKeyValue("HPoint", String.valueOf(amount + value));

            int totalhpoint = getKeyValue("TotalHPoint") != null ? Integer.parseInt(getKeyValue("TotalHPoint")) : 0;
            setKeyValue("TotalHPoint", String.valueOf(totalhpoint + value));
        }
    }

    public void removeHPoint(int value) {
        value = Math.abs(value);
        int amount = getKeyValue("HPoint") != null ? Integer.parseInt(getKeyValue("HPoint")) : 0;
        setKeyValue("HPoint", ((amount - value <= 0) ? "0" : String.valueOf(amount - value)));
    }

    public void gainHPoint(int value) {
        if (value >= 0) {
            gainHPointR(value);
        } else {
            removeHPoint(value);
        }
    }

    public int getChargePoint() {
        try {
            return Integer.parseInt(getCustomData(5, "amount"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void removeCharge(int value) {
        if (value < 0) {
            value *= -1;
        }
        if (player != null) {
            int amount = getCustomData(5, "amount") != null ? Integer.parseInt(getCustomData(5, "amount")) : 0;
            if (amount - value <= 0) {
                setCustomData(5, "amount", String.valueOf(0));
            } else {
                setCustomData(5, "amount", String.valueOf(amount - value));
            }
        }
    }

    public void gainCharge(int value) {
        gainCharge(value, 1);
    }

    public void gainCharge(int value, int rate) {
        if (value < 0) {
            value *= -1;
        }

        if (player != null) {

            int amount = getCustomData(5, "amount") != null ? Integer.parseInt(getCustomData(5, "amount")) : 0;
            setCustomData(5, "amount", String.valueOf(amount + value));

            int totalAmount = getCustomData(6, "total_amount") != null ? Integer.parseInt(getCustomData(6, "total_amount")) : 0;
            setCustomData(6, "total_amount", String.valueOf(totalAmount + value));
        }
    }

    public final MapleSession getSession() {
        return session;
    }

    public final Channel getSocketChannel() {
        return socketChannel;
    }

    public MapleKMSEncryption getReceiveCrypto() {
        return recv;
    }

    public MapleKMSEncryption getSendCrypto() {
        return send;
    }

    public MapleKMSEncryption getSend() {
        return send;
    }

    public MapleKMSEncryption getRecv() {
        return recv;
    }

    public String getIp() {
        return socketChannel.remoteAddress().toString().split(":")[0];
    }

    public void sclose() {
        session.close();
        disconnect(true, cs);
    }

    public void close() throws IOException {
        sclose();
    }

    public void sendPacket(final byte[] data) {
        if (data == null) {
            return;
        }
        socketChannel.writeAndFlush(data);
    }

    public void setStop() {
        stop = true;
    }

    public final Lock getLock() {
        return mutex;
    }

    public final Lock getNPCLock() {
        return npc_mutex;
    }

    public MapleCharacter getPlayer() {
        return player;
    }

    public void setPlayer(MapleCharacter player) {
        this.player = player;
    }

    public void createdChar(final int id) {
        allowedChar.add(id);
    }

    public final boolean login_Auth(final int id) {
        return allowedChar.contains(id);
    }

    public final List<MapleCharacter> loadCharacters(final int serverId) { // TODO make this less costly zZz
        final List<MapleCharacter> chars = new LinkedList<>();

        final Map<Integer, CardData> cardss = CharacterCardFactory.getInstance().loadCharacterCards(accId, serverId);;
        for (final CharNameAndId cni : loadCharactersInternal(serverId)) {
            final MapleCharacter chr = MapleCharacter.loadCharFromDB(cni.id, this, false, cardss);
            chars.add(chr);
            charInfo.put(chr.getId(), new Pair<>(chr.getLevel(), chr.getJob())); // to be used to update charCards
            if (!login_Auth(chr.getId())) {
                allowedChar.add(chr.getId());
            }
        }
        return chars;
    }

    public final void updateCharacterCards(final Map<Integer, Integer> cids) {
        if (charInfo.isEmpty()) {
            return;
        }
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement psu = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("DELETE FROM `character_cards` WHERE `accid` = ?");
            ps.setInt(1, accId);
            ps.executeUpdate();
            ps.close();
            psu = con.prepareStatement("INSERT INTO `character_cards` (id, accid, worldid, characterid, position) VALUES (NULL, ?, ?, ?, ?)");
            for (final Entry<Integer, Integer> ii : cids.entrySet()) {
                final Pair<Short, Short> info = charInfo.get(ii.getValue()); // charinfo we can use here as characters are already loaded
                if (info == null || ii.getValue() == 0 || !CharacterCardFactory.getInstance().canHaveCard(info.getLeft(), info.getRight())) {
                    continue;
                }
                psu.setInt(1, accId);
                psu.setInt(2, world);
                psu.setInt(3, ii.getValue());
                psu.setInt(4, ii.getKey()); // position shouldn't matter much, will reset upon login
                psu.executeUpdate();
            }
        } catch (SQLException sqlE) {
            System.out.println("Failed to update character cards. Reason: " + sqlE.toString());
        } finally {
            try {
                if (psu != null) {
                    psu.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public boolean canMakeCharacter(int serverId) {
        int tmp1 = loadCharactersSize(serverId);
        int tmp2 = getCharacterSlots();
        //System.out.println(tmp1 + " / " + tmp2 );
        return loadCharactersSize(serverId) < getCharacterSlots();
    }

    public List<String> loadCharacterNames(int serverId) {
        List<String> chars = new LinkedList<String>();
        for (CharNameAndId cni : loadCharactersInternal(serverId)) {
            chars.add(cni.name);
        }
        return chars;
    }

    private List<CharNameAndId> loadCharactersInternal(int serverId) {
        List<CharNameAndId> chars = new LinkedList<CharNameAndId>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT id, name, gm FROM characters WHERE accountid = ? AND world = ?");
            ps.setInt(1, accId);
            ps.setInt(2, serverId);
            rs = ps.executeQuery();
            while (rs.next()) {
                chars.add(new CharNameAndId(rs.getString("name"), rs.getInt("id")));
                LoginServer.getLoginAuth(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("error loading characters internal");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return chars;
    }

    private int loadCharactersSize(int serverId) {
        int chars = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT count(*) FROM characters WHERE accountid = ? AND world = ?");
            ps.setInt(1, accId);
            ps.setInt(2, serverId);
            rs = ps.executeQuery();
            if (rs.next()) {
                chars = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("error loading characters internal");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return chars;
    }

    public boolean isLoggedIn() {
        return loggedIn && accId >= 0;
    }

    private Calendar getTempBanCalendar(ResultSet rs) throws SQLException {
        Calendar lTempban = Calendar.getInstance();
        if (rs.getLong("tempban") == 0) { // basically if timestamp in db is 0000-00-00
            lTempban.setTimeInMillis(0);
            return lTempban;
        }
        Calendar today = Calendar.getInstance();
        lTempban.setTimeInMillis(rs.getTimestamp("tempban").getTime());

        if (today.getTimeInMillis() < lTempban.getTimeInMillis()) {
            return lTempban;
        }

        lTempban.setTimeInMillis(0);
        return lTempban;
    }

    public Calendar getTempBanCalendar() {
        return tempban;
    }

    public byte getGBanReason() {
        return greason;
    }

    public String getBanReason() {
        return banreason;
    }

    public boolean hasBannedIP() {
        boolean ret = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT COUNT(*) FROM ipbans WHERE ? LIKE CONCAT(ip, '%')");
            ps.setString(1, getSessionIPAddress());
            rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            System.err.println("Error checking ip bans" + ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return ret;
    }

    /**
     * Returns 0 on success, a state to be used for otherwise.
     *
     * @return The state of the login.
     */
    public int finishLogin() {
        login_mutex.lock();
        try {
            final byte state = getLoginState();
            if (state > MapleClient.LOGIN_NOTLOGGEDIN) { // already loggedin
                loggedIn = false;
                return 7;
            }
            updateLoginState(MapleClient.LOGIN_LOGGEDIN, getSessionIPAddress());
        } finally {
            login_mutex.unlock();
        }
        return 0;
    }

    public void clearInformation() {
        accountName = null;
        accId = -1;
        secondPassword = null;
        salt2 = null;
        gm = false;
        loggedIn = false;
        greason = (byte) 0;
        banreason = null;
        tempban = null;
        gender = (byte) -1;
        charInfo.clear();
    }

    public int login(String login, String pwd, boolean ipMacBanned) {
        int loginok = 5;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, login);
            rs = ps.executeQuery();
            if (rs.next()) {
                final int banned = rs.getInt("banned");
                final String passhash = rs.getString("password");
                accountName = login;
                accId = rs.getInt("id");
                secondPassword = rs.getString("2ndpassword");
                salt2 = rs.getString("salt2");
                gm = rs.getInt("gm") > 0;
                banreason = rs.getString("banreason");
                greason = rs.getByte("greason");
                tempban = getTempBanCalendar(rs);
                gender = rs.getByte("gender");
                ACash = rs.getInt("ACash");
                allowed = rs.getInt("allowed");

                if (banned > 0) {
                    return 3;
                }
                
                if (ServerConstants.ConnectorSetting) {
                    if (allowed != 1 && !gm) {
                        this.session.write(CWvsContext.serverNotice(1, "Richensia.exe를 실행하여 접속하여 주시길 바랍니다."));
                        rs.close();
                        ps.close();
                        loginok = 20;
                        return loginok;
                    }
                }

                if (secondPassword != null && salt2 != null) {
                    secondPassword = LoginCrypto.rand_r(secondPassword);
                }
                ps.close();

                if (banned > 0 && !gm) {
                    loginok = 3;
                } else {
                    if (banned == -1) {
                        unban();
                    }
                    byte loginstate = getLoginState();
                    if (loginstate > MapleClient.LOGIN_NOTLOGGEDIN) { // already loggedin
                        loggedIn = false;
                        loginok = 7;
                    }
                    boolean found = false;
                    if (loginok == 7) { //Before denying login, check if the player is actually logged in
                        for (ChannelServer ch : ChannelServer.getAllInstances()) {
                            for (MapleCharacter c : ch.getPlayerStorage().getAllCharacters()) {
                                if (c.getAccountID() == accId) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!found) {
                        loginok = 0;
                    }
                    if (loginok != 7) {
                        // Check if the passwords are correct here. :B
                        if (passhash.equals(pwd)) {
                            loginok = 0;
                        } else {
                            loggedIn = false;
                            loginok = 4;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return loginok;
    }

    public boolean CheckSecondPassword(String in) {
        boolean allow = false;
        boolean updatePasswordHash = false;
        // Check if the passwords are correct here. :B
        if (in.equals(secondPassword)) {
            // Check if a password upgrade is needed.
            allow = true;
        }
        return allow;
    }

    private void unban() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?");
            ps.setInt(1, accId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public static final byte unban(String charname) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                con.close();
                return -1;
            }
            final int accid = rs.getInt(1);
            rs.close();
            ps.close();
            ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?");
            ps.setInt(1, accid);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
            return -2;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return 0;
    }

    public void setAccID(int id) {
        this.accId = id;
    }

    public int getAccID() {
        return this.accId;
    }

    public final void updateLoginState(final int newstate, final String SessionID) { // TODO hide?
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?");
            ps.setInt(1, newstate);
            ps.setString(2, SessionID);
            ps.setInt(3, getAccID());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error updating login state" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        if (newstate == MapleClient.LOGIN_NOTLOGGEDIN) {
            loggedIn = false;
            serverTransition = false;
        } else {
            serverTransition = (newstate == MapleClient.LOGIN_SERVER_TRANSITION || newstate == MapleClient.CHANGE_CHANNEL);
            loggedIn = !serverTransition;
        }
    }

    public final void updateSecondPassword() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ? WHERE id = ?");
            ps.setString(1, secondPassword);
            ps.setInt(2, accId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error updating login state" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public final void updatGender() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE `accounts` SET `gender` = ? WHERE id = ?");
            ps.setByte(1, gender);
            ps.setInt(2, accId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error updating login state" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public final byte getLoginState() { // TODO hide?
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT loggedin, lastlogin, banned, `birthday` + 0 AS `bday` FROM accounts WHERE id = ?");
            ps.setInt(1, getAccID());
            rs = ps.executeQuery();
            if (!rs.next() || rs.getInt("banned") > 0) {
                rs.close();
                ps.close();
                con.close();
                session.close();
                throw new DatabaseException("Account doesn't exist or is banned");
            }
            birthday = rs.getInt("bday");
            byte state = rs.getByte("loggedin");

            if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
                if (rs.getTimestamp("lastlogin").getTime() + 20000 < System.currentTimeMillis()) { // connecting to chanserver timeout
                    state = MapleClient.LOGIN_NOTLOGGEDIN;
                    updateLoginState(state, getSessionIPAddress());
                }
            }
            rs.close();
            ps.close();
            con.close();
            if (state == MapleClient.LOGIN_LOGGEDIN) {
                loggedIn = true;
            } else {
                loggedIn = false;
            }
            return state;
        } catch (SQLException e) {
            loggedIn = false;
            throw new DatabaseException("error getting login state", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public final boolean checkBirthDate(final int date) {
        return birthday == date;
    }

    public final void removalTask(boolean shutdown) {
        try {
            player.cancelAllBuffs_();
            player.cancelAllDebuffs();
            if (player.getMarriageId() > 0) {
                final MapleQuestStatus stat1 = player.getQuestNoAdd(MapleQuest.getInstance(160001));
                final MapleQuestStatus stat2 = player.getQuestNoAdd(MapleQuest.getInstance(160002));
                if (stat1 != null && stat1.getCustomData() != null && (stat1.getCustomData().equals("2_") || stat1.getCustomData().equals("2"))) {
                    //dc in process of marriage
                    if (stat2 != null && stat2.getCustomData() != null) {
                        stat2.setCustomData("0");
                    }
                    stat1.setCustomData("3");
                }
            }
            if (player.getMapId() == GameConstants.JAIL) {
                final MapleQuestStatus stat1 = player.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_TIME));
                final MapleQuestStatus stat2 = player.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_QUEST));
                if (stat1.getCustomData() == null) {
                    stat1.setCustomData(String.valueOf(System.currentTimeMillis()));
                } else if (stat2.getCustomData() == null) {
                    stat2.setCustomData("0"); //seconds of jail
                } else { //previous seconds - elapsed seconds
                    int seconds = Integer.parseInt(stat2.getCustomData()) - (int) ((System.currentTimeMillis() - Long.parseLong(stat1.getCustomData())) / 1000);
                    if (seconds < 0) {
                        seconds = 0;
                    }
                    stat2.setCustomData(String.valueOf(seconds));
                }
            }
            player.changeRemoval(true);
            if (player.getEventInstance() != null) {
                player.getEventInstance().playerDisconnected(player, player.getId());
            }
            final IMaplePlayerShop shop = player.getPlayerShop();
            if (shop != null) {
                shop.removeVisitor(player);
                if (shop.isOwner(player)) {
                    if (shop.getShopType() == 1 && shop.isAvailable() && !shutdown) {
                        shop.setOpen(true);
                    } else {
                        shop.closeShop(true, !shutdown);
                    }
                }
            }
            player.setMessenger(null);
            if (player.getMap() != null) {
                if (shutdown || (getChannelServer() != null && getChannelServer().isShutdown())) {
                    int questID = -1;
                    switch (player.getMapId()) {
                        case 240060200: //HT
                            questID = 160100;
                            break;
                        case 240060201: //ChaosHT
                            questID = 160103;
                            break;
                        case 280030000: //Zakum
                            questID = 160101;
                            break;
                        case 280030001: //ChaosZakum
                            questID = 160102;
                            break;
                        case 270050100: //PB
                            questID = 160101;
                            break;
                        case 105100300: //Balrog
                        case 105100400: //Balrog
                            questID = 160106;
                            break;
                        case 211070000: //VonLeon
                        case 211070100: //VonLeon
                        case 211070101: //VonLeon
                        case 211070110: //VonLeon
                            questID = 160107;
                            break;
                        case 551030200: //scartar
                            questID = 160108;
                            break;
                        case 271040100: //cygnus
                            questID = 160109;
                            break;
                        case 262030300: // hilla   
                            questID = 160110;
                            break;
                        case 272030400:
                            questID = 160111;
                            break;
                    }
                    if (questID > 0) {
                        player.getQuestNAdd(MapleQuest.getInstance(questID)).setCustomData("0"); //reset the time.
                    }
                } else if (player.isAlive()) {
                    switch (player.getMapId()) {
                        case 541010100: //latanica
                        case 541020800: //krexel
                        case 220080001: //pap
                            player.getMap().addDisconnected(player.getId());
                            break;
                    }
                }
                player.getMap().removePlayer(player);
            }
        } catch (final Throwable e) {
            FileoutputUtil.outputFileError(FileoutputUtil.Acc_Stuck, e);
        }
    }

    public final void disconnect(final boolean RemoveInChannelServer, final boolean fromCS) {
        disconnect(RemoveInChannelServer, fromCS, false);
    }

    public final void disconnect(final boolean RemoveInChannelServer, final boolean fromCS, final boolean shutdown) {
        if (player != null) {
            MapleMap map = player.getMap();
            final MapleParty party = player.getParty();
            final boolean clone = player.isClone();
            final String namez = player.getName();
            final int idz = player.getId(), messengerid = player.getMessenger() == null ? 0 : player.getMessenger().getId(), gid = player.getGuildId(), fid = player.getFamilyId();
            final BuddyList bl = player.getBuddylist();
            final MaplePartyCharacter chrp = new MaplePartyCharacter(player);
            final MapleMessengerCharacter chrm = new MapleMessengerCharacter(player);
            final MapleGuildCharacter chrg = player.getMGC();
            final MapleFamilyCharacter chrf = player.getMFC();

            removalTask(shutdown);
            LoginServer.getLoginAuth(player.getId());
            player.saveToDB(true, fromCS);
            if (shutdown) {
                player = null;
                receiving = false;
                return;
            }
            /*    System.out.print("곶 :" + macString);
             if (!macString.equals("")) {
             if (getChannelServer().containsConnectedMacs(macString)) {
             getChannelServer().removeConnectedMacs(macString);
             }
             }*/
            if (!fromCS) {
                final ChannelServer ch = ChannelServer.getInstance(map == null ? channel : map.getChannel());
                final int chz = World.Find.findChannel(idz);
                if (chz < -1) {
                    disconnect(RemoveInChannelServer, true);//u lie
                    return;
                }
                try {
                    if (chz == -1 || ch == null || clone || ch.isShutdown()) {
                        player = null;
                        return;//no idea
                    }
                    if (messengerid > 0) {
                        World.Messenger.leaveMessenger(messengerid, chrm);
                    }
                    if (party != null) {
                        chrp.setOnline(false);
                        World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                        if (map != null && party.getLeader().getId() == idz) {
                            MaplePartyCharacter lchr = null;
                            for (MaplePartyCharacter pchr : party.getMembers()) {
                                if (pchr != null && map.getCharacterById(pchr.getId()) != null && (lchr == null || lchr.getLevel() < pchr.getLevel())) {
                                    lchr = pchr;
                                }
                            }
                            if (lchr != null) {
                                World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER_DC, lchr);
                            }
                        }
                    }
                    if (bl != null) {
                        if (!serverTransition) {
                            World.Buddy.loggedOff(namez, idz, channel, bl.getBuddyIds());
                        } else { // Change channel
                            World.Buddy.loggedOn(namez, idz, channel, bl.getBuddyIds());
                        }
                    }
                    if (gid > 0 && chrg != null) {
                        World.Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if (fid > 0 && chrf != null) {
                        World.Family.setFamilyMemberOnline(chrf, false, -1);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    FileoutputUtil.outputFileError(FileoutputUtil.Acc_Stuck, e);
                    System.err.println(getLogMessage(this, "ERROR") + e);
                } finally {
                    if (RemoveInChannelServer && ch != null) {
                        ch.removePlayer(idz, accId, namez);
                    }
                    player = null;
                }
            } else {
                final int ch = World.Find.findChannel(idz);
                if (ch > 0) {
                    disconnect(RemoveInChannelServer, false);//u lie
                    return;
                }
                try {
                    if (party != null) {
                        chrp.setOnline(false);
                        World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                    }
                    if (!serverTransition) {
                        World.Buddy.loggedOff(namez, idz, channel, bl.getBuddyIds());
                    } else { // Change channel
                        World.Buddy.loggedOn(namez, idz, channel, bl.getBuddyIds());
                    }
                    if (gid > 0 && chrg != null) {
                        World.Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if (fid > 0 && chrf != null) {
                        World.Family.setFamilyMemberOnline(chrf, false, -1);
                    }
                    if (player != null) {
                        player.setMessenger(null);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    FileoutputUtil.outputFileError(FileoutputUtil.Acc_Stuck, e);
                    System.err.println(getLogMessage(this, "ERROR") + e);
                } finally {
                    if (RemoveInChannelServer && ch > 0) {
                        CashShopServer.getPlayerStorage().deregisterPlayer(idz, accId, namez);
                    }
                    player = null;
                }
            }
        }
        if (!serverTransition && isLoggedIn()) {
            updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, getSessionIPAddress());
        }
        engines.clear();
    }

    public final String getSessionIPAddress() {
        return getIp();
    }

    public final boolean CheckIPAddress() {
        if (this.accId < 0) {
            return false;
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT SessionIP, banned FROM accounts WHERE id = ?");
            ps.setInt(1, this.accId);
            rs = ps.executeQuery();
            boolean canlogin = false;
            if (rs.next()) {
                final String sessionIP = rs.getString("SessionIP");

                if (sessionIP != null) { // Probably a login proced skipper?
                    canlogin = getSessionIPAddress().equals(sessionIP.split(":")[0]);
                }
                if (rs.getInt("banned") > 0) {
                    canlogin = false; //canlogin false = close client
                }
            }
            rs.close();
            ps.close();
            con.close();
            return canlogin;
        } catch (final SQLException e) {
            System.out.println("Failed in checking IP address for client.");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return true;
    }

    public final void DebugMessage(final StringBuilder sb) {
        sb.append(getIp());
        sb.append(" Closing: ");
        sb.append(stop);
        sb.append(" loggedin: ");
        sb.append(isLoggedIn());
        sb.append(" has char: ");
        sb.append(getPlayer() != null);
    }

    public final int getChannel() {
        return channel;
    }

    public final ChannelServer getChannelServer() {
        return ChannelServer.getInstance(channel);
    }

    public final int deleteCharacter(final int cid) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT guildid, guildrank, familyid, name FROM characters WHERE id = ? AND accountid = ?");
            ps.setInt(1, cid);
            ps.setInt(2, accId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return 0;
            }
            /*
            if (cid > 0) {
                rs.close();
                ps.close();
                return 6;
            }
             */
            if (rs.getInt("guildid") > 0) {
                if (rs.getInt("guildrank") == 1) {
                    rs.close();
                    ps.close();
                    return 18;
                }
                World.Guild.deleteGuildCharacter(rs.getInt("guildid"), cid);
            }
            if (rs.getInt("familyid") > 0 && World.Family.getFamily(rs.getInt("familyid")) != null) {
                rs.close();
                ps.close();
                return 29;
            }
            rs.close();
            ps.close();

            /*
            ps2 = con.prepareStatement("SELECT * FROM hiredmerch WHERE characterid = ?");
            ps2.setInt(1, cid);
            rs2 = ps2.executeQuery();
            if (!rs2.next()) {
                rs2.close();
                ps2.close();
                return 0;
            }
            if (rs2.getLong("time") > 0) {
                rs2.close();
                ps2.close();
                return 38;
            }
            rs2.close();
            ps2.close();
             */
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM characters WHERE id = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM hiredmerch WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryitems WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid_to = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM dueypackages WHERE RecieverId = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE buddyid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM keymap WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM regrocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM hyperrocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM extendedSlots WHERE characterid = ?", cid);
            //MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM link_skill WHERE cid = ?", cid);
            return 0;
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
            e.printStackTrace();
        } finally {
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps2 != null) {
                    ps2.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return 10;
    }

    public final byte getGender() {
        return gender;
    }

    public final void setGender(final byte gender) {
        this.gender = gender;
    }

    public final int getACash() {
        return ACash;
    }

    public final void setACash(final int ACash) {
        this.ACash = ACash;
    }

    public final String getSecondPassword() {
        return secondPassword;
    }

    public final void setSecondPassword(final String secondPassword) {
        this.secondPassword = secondPassword;
    }

    public final String getAccountName() {
        return accountName;
    }

    public final void setAccountName(final String accountName) {
        this.accountName = accountName;
    }

    public final void setChannel(final int channel) {
        this.channel = channel;
    }

    public final int getWorld() {
        return world;
    }

    public final void setWorld(final int world) {
        this.world = world;
    }

    public final int getLatency() {
        return (int) (lastPong - lastPing);
    }

    public final long getLastPong() {
        return lastPong;
    }

    public final long getLastPing() {
        return lastPing;
    }

    public final void pongReceived() {
        lastPong = System.currentTimeMillis();
    }

    public static final String getLogMessage(final MapleClient cfor, final String message) {
        return getLogMessage(cfor, message, new Object[0]);
    }

    public static final String getLogMessage(final MapleCharacter cfor, final String message) {
        return getLogMessage(cfor == null ? null : cfor.getClient(), message);
    }

    public static final String getLogMessage(final MapleCharacter cfor, final String message, final Object... parms) {
        return getLogMessage(cfor == null ? null : cfor.getClient(), message, parms);
    }

    public static final String getLogMessage(final MapleClient cfor, final String message, final Object... parms) {
        final StringBuilder builder = new StringBuilder();
        if (cfor != null) {
            if (cfor.getPlayer() != null) {
                builder.append("<");
                builder.append(MapleCharacterUtil.makeMapleReadable(cfor.getPlayer().getName()));
                builder.append(" (cid: ");
                builder.append(cfor.getPlayer().getId());
                builder.append(")> ");
            }
            if (cfor.getAccountName() != null) {
                builder.append("(Account: ");
                builder.append(cfor.getAccountName());
                builder.append(") ");
            }
        }
        builder.append(message);
        int start;
        for (final Object parm : parms) {
            start = builder.indexOf("{}");
            builder.replace(start, start + 2, parm.toString());
        }
        return builder.toString();
    }

    public static final int findAccIdForCharacterName(final String charName) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, charName);
            rs = ps.executeQuery();
            int ret = -1;
            if (rs.next()) {
                ret = rs.getInt("accountid");
            }
            rs.close();
            ps.close();
            con.close();
            return ret;
        } catch (final SQLException e) {
            System.err.println("findAccIdForCharacterName SQL error");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return -1;
    }

    public final boolean isGm() {
        return gm;
    }

    public final void setScriptEngine(final String name, final ScriptEngine e) {
        engines.put(name, e);
    }

    public final ScriptEngine getScriptEngine(final String name) {
        return engines.get(name);
    }

    public final void removeScriptEngine(final String name) {
        engines.remove(name);
    }

    public final ScheduledFuture<?> getIdleTask() {
        return idleTask;
    }

    public final void setIdleTask(final ScheduledFuture<?> idleTask) {
        this.idleTask = idleTask;
    }

    protected static final class CharNameAndId {

        public final String name;
        public final int id;

        public CharNameAndId(final String name, final int id) {
            super();
            this.name = name;
            this.id = id;
        }
    }

    public int getCharacterSlots() {
        if (isGm()) {
            return 15;
        }
        if (charslots != DEFAULT_CHARSLOT) {
            return charslots; //save a sql
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psu = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM character_slots WHERE accid = ? AND worldid = ?");
            ps.setInt(1, accId);
            ps.setInt(2, world);
            rs = ps.executeQuery();
            if (rs.next()) {
                charslots = rs.getInt("charslots");
            } else {
                psu = con.prepareStatement("INSERT INTO character_slots (accid, worldid, charslots) VALUES (?, ?, ?)");
                psu.setInt(1, accId);
                psu.setInt(2, world);
                psu.setInt(3, charslots);
                psu.executeUpdate();
                psu.close();
            }
        } catch (SQLException sqlE) {
            sqlE.printStackTrace();
        } finally {
            try {
                if (psu != null) {
                    psu.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return charslots;
    }

    public boolean gainCharacterSlot() {
        if (getCharacterSlots() >= 15) {
            return false;
        }
        charslots++;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE character_slots SET charslots = ? WHERE worldid = ? AND accid = ?");
            ps.setInt(1, charslots);
            ps.setInt(2, world);
            ps.setInt(3, accId);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException sqlE) {
            sqlE.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return true;
    }

    public boolean isMonitored() {
        return monitored;
    }

    public void setMonitored(boolean m) {
        this.monitored = m;
    }

    public boolean isReceiving() {
        return receiving;
    }

    public void setReceiving(boolean m) {
        this.receiving = m;
    }

    public boolean canClickNPC() {
        return lastNpcClick + 500 < System.currentTimeMillis();
    }

    public void setClickedNPC() {
        lastNpcClick = System.currentTimeMillis();
    }

    public void removeClickedNPC() {
        lastNpcClick = 0;
    }

    public final Timestamp getCreated() { // TODO hide?
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT createdat FROM accounts WHERE id = ?");
            ps.setInt(1, getAccID());
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                con.close();
                return null;
            }
            Timestamp ret = rs.getTimestamp("createdat");
            rs.close();
            ps.close();
            con.close();
            return ret;
        } catch (SQLException e) {
            throw new DatabaseException("error getting create", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public String getTempIP() {
        return tempIP;
    }

    public void setTempIP(String s) {
        this.tempIP = s;
    }

    public boolean isLocalhost() {
        return ServerConstants.Use_Localhost;
    }

    public String getMacString() {
        return macString;
    }

    public void setMacString(String s) {
        this.macString = s;
    }

}
