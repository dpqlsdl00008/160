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
package client;

import client.inventory.Equip;
import constants.GameConstants;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.Serializable;

import database.DatabaseConnection;
import handling.QuestType;
import handling.world.World;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.quest.MapleQuest;
import tools.Pair;
import tools.Triple;
import tools.data.OutPacket;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;

public class MonsterBook implements Serializable {

    private static final long serialVersionUID = 7179541993413738569L;
    private boolean changed = false;
    private int currentSet = -1, level = 0, setScore, finishedSets;
    private Map<Integer, Integer> cards;
    private List<Integer> cardItems = new ArrayList<Integer>();
    private Map<Integer, Pair<Integer, Boolean>> sets = new HashMap<Integer, Pair<Integer, Boolean>>();
    private int SpecialCard = 0, NormalCard = 0, BookLevel = 1;

    public MonsterBook(Map<Integer, Integer> cards, MapleCharacter chr) {
        this.cards = cards;
        calculateItem();
        calculateScore();

        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(GameConstants.CURRENT_SET));
        if (stat != null && stat.getCustomData() != null) {
            currentSet = Integer.parseInt(stat.getCustomData());
            if (!sets.containsKey(currentSet) || !sets.get(currentSet).right) {
                currentSet = -1;
            }
        }
    }

    public byte calculateScore() {
        byte returnval = 0;
        sets.clear();
        int oldLevel = level, oldSetScore = setScore;
        setScore = 0;
        finishedSets = 0;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (int i : cardItems) {
            //we need the card id but we store the mob id lol
            final Integer x = ii.getSetId(i);
            if (x != null && x.intValue() > 0) {
                final Triple<Integer, List<Integer>, List<Integer>> set = ii.getMonsterBookInfo(x);
                if (set != null) {
                    if (!sets.containsKey(x)) {
                        sets.put(x, new Pair<Integer, Boolean>(1, Boolean.FALSE));
                    } else {
                        sets.get(x).left++;
                    }
                    if (sets.get(x).left == set.mid.size()) {
                        sets.get(x).right = Boolean.TRUE;
                        setScore += set.left;
                        if (currentSet == -1) {
                            currentSet = x;
                            returnval = 2;
                        }
                        finishedSets++;
                    }
                }
            }
        }
        level = 10;
        for (byte i = 0; i < 10; i++) {
            if (GameConstants.getSetExpNeededForLevel(i) > setScore) {
                level = (byte) i;
                break;
            }
        }
        if (level > oldLevel) {
            returnval = 2;
        } else if (setScore > oldSetScore) {
            returnval = 1;
        }
        return returnval;
    }

    public void writeCharInfoPacket(OutPacket mplew) {
        //cid, then the character's level
        List<Integer> cardSize = new ArrayList<Integer>(10); //0 = total, 1-9 = card types..
        for (int i = 0; i < 10; i++) {
            cardSize.add(0);
        }
        for (int x : cardItems) {
            cardSize.set(0, cardSize.get(0) + 1);
            cardSize.set(((x / 1000) % 10) + 1, cardSize.get(((x / 1000) % 10) + 1) + 1);
        }
        for (int i : cardSize) {
            mplew.writeInt(i);
        }
        mplew.writeInt(setScore);
        mplew.writeInt(currentSet);
        mplew.writeInt(finishedSets);
    }

    public void writeFinished(OutPacket mplew) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        mplew.write(1);
        mplew.writeShort(cardItems.size());
        final List<Integer> mbList = new ArrayList<Integer>(ii.getMonsterBookList());
        Collections.sort(mbList);
        final int fullCards = (mbList.size() / 8) + (mbList.size() % 8 > 0 ? 1 : 0);
        mplew.writeShort(fullCards); //which cards of all you have; more efficient than writing each card

        for (int i = 0; i < fullCards; i++) {
            int currentMask = 0x1, maskToWrite = 0;
            for (int y = (i * 8); y < ((i * 8) + 8); y++) {
                if (mbList.size() <= y) {
                    break;
                }
                if (cardItems.contains(mbList.get(y))) {
                    maskToWrite |= currentMask;
                }
                currentMask *= 2;
            }
            mplew.write(maskToWrite);
        }

        final int fullSize = (cardItems.size() / 2) + (cardItems.size() % 2 > 0 ? 1 : 0);
        mplew.writeShort(fullSize); //i honestly don't know the point of this, is it to signify yes/no if you have the card or not?... which is already done...?
        for (int i = 0; i < fullSize; i++) {
            mplew.write(i == (cardItems.size() / 2) ? 1 : 0x11);
        }
    }

    public void writeUnfinished(OutPacket mplew) {
        mplew.write(0);
        mplew.writeShort(cardItems.size());
        for (int i : cardItems) {
            mplew.writeShort(i % 10000);
            mplew.write(1); //whether you have the card or not? idk
        }
    }

    public void calculateItem() {
        cardItems.clear();
        for (Entry<Integer, Integer> s : cards.entrySet()) {
            addCardItem(s.getKey(), s.getValue());
        }
    }

    public void addCardItem(int key, int value) {
        if (value >= 2) {
            Integer x = MapleItemInformationProvider.getInstance().getItemIdByMob(key);
            if (x != null && x.intValue() > 0) {
                cardItems.add(x.intValue());
            }
        }
    }

    public void modifyBook(Equip eq) {
        eq.setStr((short) level);
        eq.setDex((short) level);
        eq.setInt((short) level);
        eq.setLuk((short) level);
        eq.setPotential1(0);
        eq.setPotential2(0);
        eq.setPotential3(0);
        eq.setPotential4(0);
        eq.setPotential5(0);
        if (currentSet > -1) {
            final Triple<Integer, List<Integer>, List<Integer>> set = MapleItemInformationProvider.getInstance().getMonsterBookInfo(currentSet);
            if (set != null) {
                for (int i = 0; i < set.right.size(); i++) {
                    if (i == 0) {
                        eq.setPotential1(set.right.get(i).intValue());
                    } else if (i == 1) {
                        eq.setPotential2(set.right.get(i).intValue());
                    } else if (i == 2) {
                        eq.setPotential3(set.right.get(i).intValue());
                    } else if (i == 3) {
                        eq.setPotential4(set.right.get(i).intValue());
                    } else if (i == 4) {
                        eq.setPotential5(set.right.get(i).intValue());
                        break;
                    }
                }
            } else {
                currentSet = -1;
            }
        }
    }

    public int getSetScore() {
        return setScore;
    }

    public int getLevel() {
        return level;
    }

    public int getSet() {
        return currentSet;
    }

    public boolean changeSet(int c) {
        if (sets.containsKey(c) && sets.get(c).right) {
            this.currentSet = c;
            return true;
        }
        return false;
    }

    public void changed() {
        changed = true;
    }

    public Map<Integer, Integer> getCards() {
        return cards;
    }

    public final int getSeen() {
        return cards.size();
    }

    public final int getCaught() {
        int ret = 0;
        for (int i : cards.values()) {
            if (i >= 2) {
                ret++;
            }
        }
        return ret;
    }

    public final int getLevelByCard(final int cardid) {
        return cards.get(cardid) == null ? 0 : cards.get(cardid);
    }

    public static MonsterBook loadCards(final int charid, final MapleCharacter chr) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM monsterbook WHERE charid = ? ORDER BY cardid ASC");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            Map<Integer, Integer> cards = new LinkedHashMap<>();
            int cardid, level;
            while (rs.next()) {
                cardid = rs.getInt("cardid");
                level = rs.getInt("level");
                cards.put(cardid, level);
            }
            rs.close();
            ps.close();
            con.close();
            return new MonsterBook(cards, chr);
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
        return null;
    }

    public final void saveCards(final int charid) {
        if (!changed) {
            return;
        }
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("DELETE FROM monsterbook WHERE charid = ?");
            ps.setInt(1, charid);
            ps.execute();
            ps.close();
            changed = false;
            if (cards.isEmpty()) {
                ps.close();
                con.close();
                return;
            }
            boolean first = true;
            final StringBuilder query = new StringBuilder();
            for (final Entry<Integer, Integer> all : cards.entrySet()) {
                if (first) {
                    first = false;
                    query.append("INSERT INTO monsterbook VALUES (DEFAULT,");
                } else {
                    query.append(",(DEFAULT,");
                }
                query.append(charid);
                query.append(",");
                query.append(all.getKey()); // Card ID
                query.append(",");
                query.append(all.getValue()); // Card level
                query.append(")");
            }
            ps = con.prepareStatement(query.toString());
            ps.execute();
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
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public final void monsterCaught(final MapleClient c, final int cardid, final int sourceid, final String cardname) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        changed = true;
        c.sendPacket(CWvsContext.MonsterBookSetCover(2));
        c.sendPacket(EffectPacket.showForeignEffect(16));
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPacket.showForeignEffect(c.getPlayer().getId(), 16), false);
        for (final Entry<Integer, Integer> all : cards.entrySet()) {
            if (all.getKey() == sourceid) {
                if (all.getValue() > 4) {
                    //c.sendPacket(CWvsContext.MonsterBookSetCard(0, 5));
                } else {
                    all.setValue(all.getValue() + 1);
                    addCardItem(sourceid, 1);
                    c.getPlayer().dropMessage(5, "[" + ii.getName(sourceid) + "]가 몬스터 북에 성공적으로 등록되었습니다. (" + all.getValue() + "/5)");
                    //c.sendPacket(CWvsContext.MonsterBookSetCard(sourceid, all.getValue()));
                    if (all.getValue() == 5) {
                        c.getPlayer().getClient().sendPacket(CUserLocal.questResult(QuestType.QuestRes_End_QuestTimer, 1047, 0, 0));
                        final int mobID = MapleItemInformationProvider.getInstance().getCardMobId(sourceid);
                        c.getPlayer().getQuest(MapleQuest.getInstance(1047)).getQuest().forceStart(c.getPlayer(), 9010017, "" + mobID + "/" + mobID + "/15");
                    }
                }
                this.saveCards(c.getPlayer().getAccountID());
                return;
            }
        }
        cards.put(sourceid, 1);
        addCardItem(sourceid, 1);
        this.saveCards(c.getPlayer().getAccountID());
        c.getPlayer().dropMessage(5, "[" + ii.getName(sourceid) + "]가 몬스터 북에 성공적으로 등록되었습니다. (1/5)");
        //c.sendPacket(CWvsContext.MonsterBookSetCard(sourceid, 1));
    }

    public final int getChapter(final MapleCharacter user, int chapter) {
        int min = 0;
        int max = 0;
        switch (chapter) {
            case 1: {
                min = 2380000;
                max = 2380020;
                break;
            }
            case 2: {
                min = 2381000;
                max = 2381085;
                break;
            }
            case 3: {
                min = 2382000;
                max = 2382112;
                break;
            }
            case 4: {
                min = 2383000;
                max = 2383059;
                break;
            }
            case 5: {
                min = 2384000;
                max = 2384062;
                break;
            }
            case 6: {
                min = 2385000;
                max = 2385044;
                break;
            }
            case 7: {
                min = 2386000;
                max = 2386034;
                break;
            }
            case 8: {
                min = 2387000;
                max = 2387013;
                break;
            }
            case 9: {
                min = 2388000;
                max = 2388084;
                break;
            }
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int completed = 0;
        for (int i = min; i < max; i++) {
            if (ii.getItemInformation(i) == null) {
                continue;
            }
            if (this.getCard(user.getAccountID(), i) > 4) {
                completed++;
            }
        }
        return completed;
    }

    public final int getCardCount(final MapleCharacter user, boolean specialCard) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM monsterbook WHERE charid = ? ORDER BY cardid ASC");
            ps.setInt(1, user.getAccountID());
            rs = ps.executeQuery();
            int v1 = 0;
            int v2 = 0;
            int v3 = 0;
            int v4 = 0;
            while (rs.next()) {
                v1 = rs.getInt("cardid");
                v2 = rs.getInt("level");
                if (v1 / 1000 > 2387) {
                    v3 += 1;
                } else {
                    v4 += 1;
                }
            }
            rs.close();
            ps.close();
            con.close();
            if (specialCard) {
                return v3;
            }
            return v4;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
        }
        return 0;
    }

    public final int getCard(int accountID, int cardID) {
        int v1 = 0;
        for (final Entry<Integer, Integer> all : cards.entrySet()) {
            if (all.getKey() == cardID) {
                v1 = all.getValue();
            }
        }
        return v1;
    }

    public boolean hasCard(int cardid) {
        return cardItems == null ? false : cardItems.contains(cardid);
    }

    public final void monsterSeen(final MapleClient c, final int cardid, final String cardname) {
        if (cards.containsKey(cardid)) {
            return;
        }
        changed = true;
        cards.put(cardid, 1);
        c.getSession().write(EffectPacket.showForeignEffect(16));
    }

    public final int getTotalCard() {
        return SpecialCard + NormalCard;
    }
}
