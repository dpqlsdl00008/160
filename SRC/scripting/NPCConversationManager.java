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
 but WITHOUT ANY WARRANTY; without even the implied wavrranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package scripting;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import client.inventory.Equip;
import client.Skill;
import client.inventory.Item;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import constants.GameConstants;
import client.inventory.ItemFlag;
import client.MapleClient;
import client.MapleQuestStatus;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.SkillFactory;
import client.SkillEntry;
import client.MapleStat;
import client.SpeedQuiz;
import server.MapleCarnivalParty;
import server.Randomizer;
import server.MapleInventoryManipulator;
import server.MapleShopFactory;
import server.MapleSquad;
import server.maps.MapleMap;
import server.maps.Event_DojoAgent;
import server.quest.MapleQuest;
import tools.packet.CField;
import server.MapleItemInformationProvider;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import database.DatabaseConnection;
import handling.UIType;
import handling.channel.handler.DueyHandler;
import handling.channel.handler.HiredMerchantHandler;
import handling.channel.handler.PlayersHandler;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.exped.ExpeditionType;
import handling.world.exped.MapleExpedition;
import handling.world.guild.MapleGuild;
import server.MapleCarnivalChallenge;

import java.util.HashMap;

import handling.world.guild.MapleGuildAlliance;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import javax.script.Invocable;

import server.MapleStatEffect;
import server.SpeedRunner;
import server.StructItemOption;
import server.Timer.CloneTimer;
import server.life.*;
import server.maps.Event_PyramidSubway;
import tools.FileoutputUtil;
import tools.Pair;
import tools.StringUtil;
import tools.Triple;
import tools.packet.CField.NPCPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.CWvsContext.InfoPacket;

public class NPCConversationManager extends AbstractPlayerInteraction {

    public static void writeLog(String path, String data, boolean writeafterend) {
        try {
            File fFile = new File(path);

            if (!fFile.exists()) {
                fFile.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(path, true);
            long time = System.currentTimeMillis();
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String str = dayTime.format(new Date(time));
            String msg = "\r\n" + str + " | " + data;
            out.write(msg.getBytes());
            out.close();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getText;
    private byte type; // -1 = NPC, 0 = start quest, 1 = end quest
    private byte lastMsg = -1;
    public boolean pendingDisposal = false;
    private Invocable iv;
    private int objectid;

    public NPCConversationManager(MapleClient c, int npc, int questid, byte type, Invocable iv) {
        super(c, npc, questid);
        this.type = type;
        this.iv = iv;
    }

    public Invocable getIv() {
        return iv;
    }

    public int getNpc() {
        return id;
    }

    public int getQuest() {
        return id2;
    }

    public int setObjectId(int oid) {
        return objectid = oid;
    }

    public int getObjectId() {
        return objectid;
    }

    public byte getType() {
        return type;
    }

    public void safeDispose() {
        pendingDisposal = true;
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(c);
    }

    public void dispose(String custom) {
        NPCScriptManager.getInstance().dispose(c, custom);
    }

    public void askMapSelection(final String sel) {
        if (lastMsg > -1) {
            return;
        }
        c.getSession().write(NPCPacket.getMapSelection(id, sel));
        lastMsg = (byte) 0x10;
    }
    
    public void askMapSelection(final String nStr, final int nType) {
        if (lastMsg > -1) {
            return;
        }
        c.sendPacket(NPCPacket.getMapSelection(id, nStr, nType));
        lastMsg = (byte) 0x10;
    }

    public void sendNext(String text) {
        sendNext(text, id);
    }

    public void sendNext(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { //sendNext will dc otherwise!
            sendSimple(text);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) 0, text, "00 01", (byte) 0));
        lastMsg = 0;
    }

    public void sendPlayerToNpc(String text) {
        sendNextS(text, (byte) 3, id);
    }

    public void sendNextNoESC(String text) {
        sendNextS(text, (byte) 1, id);
    }

    public void sendNextNoESC(String text, int id) {
        sendNextS(text, (byte) 1, id);
    }

    public void sendNextS(String text, byte type) {
        sendNextS(text, type, id);
    }

    public void sendNextS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) 0, text, "00 01", type, idd));
        lastMsg = 0;
    }

    public void sendNextS(String text, byte type, int idd, int npcid) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimpleS(text, type);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(npcid, (byte) 0, text, "00 01", type, idd));
        lastMsg = 0;
    }

    public void sendPrev(String text) {
        sendPrev(text, id);
    }

    public void sendPrev(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) 0, text, "01 00", (byte) 0));
        lastMsg = 0;
    }

    public void sendPrevS(String text, byte type) {
        sendPrevS(text, type, id);
    }

    public void sendPrevS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) 0, text, "01 00", type, idd));
        lastMsg = 0;
    }

    public void sendNextPrev(String text) {
        sendNextPrev(text, id);
    }

    public void sendNextPrev(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) 0, text, "01 01", (byte) 0));
        lastMsg = 0;
    }

    public void PlayerToNpc(String text) {
        sendNextPrevS(text, (byte) 3);
    }

    public void sendNextPrevS(String text) {
        sendNextPrevS(text, (byte) 3);
    }

    public void sendNextPrevS(String text, byte type) {
        sendNextPrevS(text, type, id);
    }

    public void sendNextPrevS(String text, byte type, int idd) {
        sendNextPrevS(text, type, idd, id);
    }

    public void sendNextPrevS(String text, byte type, int idd, int npcid) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(npcid, (byte) 0, text, "01 01", type, idd));
        lastMsg = 0;
    }

    public void sendOk(String text) {
        sendOk(text, id);
    }

    public void sendOk(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) 0, text, "00 00", (byte) 0));
        lastMsg = 0;
    }

    public void sendOkS(String text, byte type) {
        sendOkS(text, type, id);
    }

    public void sendOkS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) 0, text, "00 00", type, idd));
        lastMsg = 0;
    }

    public void sendYesNo(String text) {
        sendYesNo(text, id);
    }

    public void sendYesNo(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) 2, text, "", (byte) 0));
        lastMsg = 2;
    }

    public void sendYesNoS(String text, byte type) {
        sendYesNoS(text, type, id);
    }

    public void sendYesNoS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) 2, text, "", type, idd));
        lastMsg = 2;
    }

    public void sendAcceptDecline(String text) {
        askAcceptDecline(text);
    }

    public void sendAcceptDeclineNoESC(String text) {
        askAcceptDeclineNoESC(text);
    }

    public void askAcceptDecline(String text) {
        askAcceptDecline(text, id);
    }

    public void sendAcceptDecline(String text, int id) {
        askAcceptDecline(text, id);
    }

    public void askAcceptDecline(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = (byte) 0xE;
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) lastMsg, text, "", (byte) 0));
    }

    public void sendAcceptDeclineS(String text, byte type) {
        askAcceptDeclineS(text, type, id);
    }

    public void askAcceptDeclineS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = (byte) 0xE;
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) lastMsg, text, "", type, idd));
    }

    public void askAcceptDeclineNoESC(String text) {
        askAcceptDeclineNoESC(text, id);
    }

    public void askAcceptDeclineNoESC(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = (byte) 0xE;
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) lastMsg, text, "", (byte) 1));
    }

    public void askAvatar(String text, int... args) {
        if (lastMsg > -1) {
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalkStyle(id, text, args));
        lastMsg = 9;
    }

    public void sendSimple(String text) {
        sendSimple(text, id);
    }

    public void sendSimple(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) { //sendSimple will dc otherwise!
            sendNext(text);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(id, (byte) 5, text, "", (byte) 0));
        lastMsg = 5;
    }

    public void sendSimpleS(String text, byte type) {
        sendSimpleS(text, type, id);
    }

    public void sendSimpleS(String text, byte type, int idd) {
        if (lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) { //sendSimple will dc otherwise!
            sendNextS(text, type);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalk(idd, (byte) 5, text, "", (byte) type, idd));
        lastMsg = 5;
    }

    public void sendStyle(String text, int styles[]) {
        if (lastMsg > -1) {
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalkStyle(id, text, styles));
        lastMsg = 9;
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalkNum(id, text, def, min, max));
        lastMsg = 4;
    }

    public void sendGetNumberS(String text, int min, int max, int npcID) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimple(text);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalkNum(npcID, text, 1, min, max));
        lastMsg = 4;
    }

    public void sendGetText(String text) {
        sendGetText(text, id);
    }

    public void sendGetText(String text, int id) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.getSession().write(NPCPacket.getNPCTalkText(id, text));
        lastMsg = 3;
    }

    public void setGetText(String text) {
        this.getText = text;
    }

    public String getText() {
        return getText;
    }

    public void setHair(int hair) {
        if (MapleItemInformationProvider.getInstance().itemExist(hair)) {
            c.getPlayer().dropMessage(5, "It does not exist!");
            return;
        }
        getPlayer().setHair(hair);
        getPlayer().updateSingleStat(MapleStat.Hair, hair);
        getPlayer().equipChanged();
    }

    public void setFace(int face) {
        if (MapleItemInformationProvider.getInstance().itemExist(face)) {
            c.getPlayer().dropMessage(5, "It does not exist!");
            return;
        }
        getPlayer().setFace(face);
        getPlayer().updateSingleStat(MapleStat.Face, face);
        getPlayer().equipChanged();
    }

    public void setSkin(int color) {
        getPlayer().setSkinColor((byte) color);
        getPlayer().updateSingleStat(MapleStat.Skin, color);
        getPlayer().equipChanged();
    }

    public int setRandomAvatar(int ticket, int... args_all) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);

        int args = args_all[Randomizer.nextInt(args_all.length)];
        if (args < 100) {
            c.getPlayer().setSkinColor((byte) args);
            c.getPlayer().updateSingleStat(MapleStat.Skin, args);
        } else if (args < 30000) {
            c.getPlayer().setFace(args);
            c.getPlayer().updateSingleStat(MapleStat.Face, args);
        } else {
            c.getPlayer().setHair(args);
            c.getPlayer().updateSingleStat(MapleStat.Hair, args);
        }
        c.getPlayer().equipChanged();
        c.getPlayer().RandomHair = args;

        return 1;
    }

    public int setAvatar(int args) {
        //if (!haveItem(ticket)) {
        //    return -1;
        //}
        //gainItem(ticket, (short) -1);

        if (args < 100) {
            c.getPlayer().setSkinColor((byte) args);
            c.getPlayer().updateSingleStat(MapleStat.Skin, args);
        } else if ((args >= 20000 && args <= 29999) || (args >= 50000 && args <= 59999)) {
            c.getPlayer().setFace(args);
            c.getPlayer().updateSingleStat(MapleStat.Face, args);
        } else {
            c.getPlayer().setHair(args);
            c.getPlayer().updateSingleStat(MapleStat.Hair, args);
        }
        c.getPlayer().equipChanged();

        return 1;
    }

    public int setAvatar(int ticket, int args) { // 5150053
        if (!haveItem(GameConstants.isCuponCheck(ticket))) { //  || !haveItem(ticket)
            return -1;
        }
        if (haveItem(GameConstants.isCuponCheck(ticket))) {
            gainItem(GameConstants.isCuponCheck(ticket), (short) -1);
        } else {
            gainItem(ticket, (short) -1);
        }
        if (args < 100) {
            c.getPlayer().setSkinColor((byte) args);
            c.getPlayer().updateSingleStat(MapleStat.Skin, args);
        } else if (args < 30000) {
            c.getPlayer().setFace(args);
            c.getPlayer().updateSingleStat(MapleStat.Face, args);
        } else {
            c.getPlayer().setHair(args);
            c.getPlayer().updateSingleStat(MapleStat.Hair, args);
        }
        c.getPlayer().equipChanged();

        return 1;
    }

    public void sendStorage() {
        c.getPlayer().setConversation(4);
        c.getPlayer().getStorage().sendStorage(c, id);
    }

    public void openShop(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(c);
    }

    public void openAdminShop(int id) {
        MapleShopFactory.getInstance().getShop(id).sendAdminShop(c);
    }

    public void openShopNPC(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(c, this.id);
    }

    public int gainGachaponItem(int id, int quantity) {
        return gainGachaponItem(id, quantity, c.getPlayer().getMap().getStreetName());
    }

    public int gainGachaponItem(int id, int quantity, final String msg) {
        try {
            if (!MapleItemInformationProvider.getInstance().itemExists(id)) {
                return -1;
            }
            final Item item = MapleInventoryManipulator.addbyId_Gachapon(c, id, (short) quantity);

            if (item == null) {
                return -1;
            }
            final byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            if (rareness > 0) {
                World.Broadcast.broadcastMessage(CWvsContext.getGachaponMega(c.getPlayer().getName(), " : got a(n)", item, rareness, msg));
            }
            c.getSession().write(InfoPacket.getShowItemGain(item.getItemId(), (short) quantity, true));
            return item.getItemId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void changeJob(short job) {
        c.getPlayer().changeJob(job);
    }

    public void setFree() {
        c.getSession().write(CField.UIPacket.getDirectionStatus(false));
        c.sendPacket(CUserLocal.setInGameDirectionMode(0));
        c.getSession().write(CField.UIPacket.getDirectionInfo("Effect/DirectionNewPirate.img/newPirate/balloonMsg1/20", 7000, 0, -132, 0, 0));
        c.removeClickedNPC();
        c.getPlayer().dropMessage(-1, "Go through the portals.");
    }

    public void videoByScript(String customData) {
        c.getSession().write(CUserLocal.videoByScript(customData, true));
    }

    public void setSubcategory(int z) {
        getPlayer().setSubcategory(z);
    }

    public void openScript(int npcid, String js) {
        NPCScriptManager.getInstance().start(c, npcid, js);
    }

    public void startQuest(int idd) {
        MapleQuest.getInstance(idd).start(getPlayer(), id);
    }

    public void completeQuest(int idd) {
        MapleQuest.getInstance(idd).complete(getPlayer(), id);
    }

    public void forfeitQuest(int idd) {
        MapleQuest.getInstance(idd).forfeit(getPlayer());
    }

    public void forceStartQuest() {
        MapleQuest.getInstance(id2).forceStart(getPlayer(), getNpc(), null);
    }

    public void forceStartQuest(int idd) {
        MapleQuest.getInstance(idd).forceStart(getPlayer(), getNpc(), null);
    }

    public void forceStartQuest(String customData) {
        MapleQuest.getInstance(id2).forceStart(getPlayer(), getNpc(), customData);
    }

    public void forceCompleteQuest() {
        MapleQuest.getInstance(id2).forceComplete(getPlayer(), getNpc());
    }

    public void forceCompleteQuest(final int idd) {
        MapleQuest.getInstance(idd).forceComplete(getPlayer(), getNpc());
    }

    public String getQuestCustomData() {
        return c.getPlayer().getQuestNAdd(MapleQuest.getInstance(id2)).getCustomData();
    }

    public void setQuestCustomData(String customData) {
        getPlayer().getQuestNAdd(MapleQuest.getInstance(id2)).setCustomData(customData);
        final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(id2)); //퀘스트 커스텀 값 변경시 즉시적용
        c.getPlayer().updateQuest(stats, true);
    }

    public String getQuestCustomData(int qid) {
        return c.getPlayer().getQuestNAdd(MapleQuest.getInstance(qid)).getCustomData();
    }

    public void setQuestCustomData(int qid, String customData) {
        getPlayer().getQuestNAdd(MapleQuest.getInstance(qid)).setCustomData(customData);
        final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(qid)); //퀘스트 커스텀 값 변경시 즉시적용 이러면 편하겠지?
        c.getPlayer().updateQuest(stats, true);
    }

    public int getMeso() {
        return getPlayer().getMeso();
    }

    public void gainAp(final int amount) {
        c.getPlayer().gainAp((short) amount);
    }

    public void expandInventory(byte type, int amt) {
        c.getPlayer().expandInventory(type, amt);
    }

    public void unequipEverything() {
        MapleInventory equipped = getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
        List<Short> ids = new LinkedList<Short>();
        for (Item item : equipped.newList()) {
            ids.add(item.getPosition());
        }
        for (short id : ids) {
            MapleInventoryManipulator.unequip(getC(), id, equip.getNextFreeSlot());
        }
    }

    public final void clearSkills() {
        final Map<Skill, SkillEntry> skills = new HashMap<>(getPlayer().getSkills());
        final Map<Skill, SkillEntry> newList = new HashMap<>();
        for (Entry<Skill, SkillEntry> skill : skills.entrySet()) {
            newList.put(skill.getKey(), new SkillEntry((byte) 0, (byte) 0, -1));
        }
        getPlayer().changeSkillsLevel(newList);
        newList.clear();
        skills.clear();
    }

    public boolean hasSkill(int skillid) {
        Skill theSkill = SkillFactory.getSkill(skillid);
        if (theSkill != null) {
            return c.getPlayer().getSkillLevel(theSkill) > 0;
        }
        return false;
    }

    public void showEffect(boolean broadcast, String effect) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.showEffect(effect));
        } else {
            c.getSession().write(CField.showEffect(effect));
        }
    }

    public void playSound(boolean broadcast, String sound) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.playSound(sound));
        } else {
            c.getPlayer().getClient().sendPacket(CField.playSound(sound));
        }
    }

    public void playSounds(boolean broadcast, String sound) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.playSounds(sound));
        } else {
            c.getSession().write(CField.playSounds(sound));
        } // playSounds
    }

    public void environmentChange(boolean broadcast, String env) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.environmentChange(env, 2));
        } else {
            c.getSession().write(CField.environmentChange(env, 2));
        }
    }

    public void updateBuddyCapacity(int capacity) {
        c.getPlayer().setBuddyCapacity((byte) capacity);
    }

    public int getBuddyCapacity() {
        return c.getPlayer().getBuddyCapacity();
    }

    public int partyMembersInMap() {
        int inMap = 0;
        if (getPlayer().getParty() == null) {
            return inMap;
        }
        for (MapleCharacter char2 : getPlayer().getMap().getCharactersThreadsafe()) {
            if (char2.getParty() != null && char2.getParty().getId() == getPlayer().getParty().getId()) {
                inMap++;
            }
        }
        return inMap;
    }

    public List<MapleCharacter> getPartyMembers() {
        if (getPlayer().getParty() == null) {
            return null;
        }
        List<MapleCharacter> chars = new LinkedList<MapleCharacter>(); // creates an empty array full of shit..
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            for (ChannelServer channel : ChannelServer.getAllInstances()) {
                MapleCharacter ch = channel.getPlayerStorage().getCharacterById(chr.getId());
                if (ch != null) { // double check <3
                    chars.add(ch);
                }
            }
        }
        return chars;
    }

    public void warpPartyWithExp(int mapId, int exp) {
        if (getPlayer().getParty() == null) {
            warp(mapId, 0);
            gainExp(exp);
            return;
        }
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
            }
        }
    }

    public void warpPartyWithExpMeso(int mapId, int exp, int meso) {
        if (getPlayer().getParty() == null) {
            warp(mapId, 0);
            gainExp(exp);
            gainMeso(meso);
            return;
        }
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
                curChar.gainMeso(meso, true);
            }
        }
    }

    public MapleSquad getSquad(String type) {
        return c.getChannelServer().getMapleSquad(type);
    }

    public int getSquadAvailability(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        return squad.getStatus();
    }

    public boolean registerSquad(String type, int minutes, String startText) {
        if (c.getChannelServer().getMapleSquad(type) == null) {
            final MapleSquad squad = new MapleSquad(c.getChannel(), type, c.getPlayer(), minutes * 60 * 1000, startText);
            final boolean ret = c.getChannelServer().addMapleSquad(squad, type);
            if (ret) {
                final MapleMap map = c.getPlayer().getMap();

                map.broadcastMessage(CField.getClock(minutes * 60));
                map.broadcastMessage(CWvsContext.serverNotice(6, c.getPlayer().getName() + startText));
            } else {
                squad.clear();
            }
            return ret;
        }
        return false;
    }

    public boolean getSquadList(String type, byte type_) {
        try {
            final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
            if (squad == null) {
                return false;
            }
            if (type_ == 0 || type_ == 3) { // Normal viewing
                sendNext(squad.getSquadMemberString(type_));
            } else if (type_ == 1) { // Squad Leader banning, Check out banned participant
                sendSimple(squad.getSquadMemberString(type_));
            } else if (type_ == 2) {
                if (squad.getBannedMemberSize() > 0) {
                    sendSimple(squad.getSquadMemberString(type_));
                } else {
                    sendNext(squad.getSquadMemberString(type_));
                }
            }
            return true;
        } catch (NullPointerException ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
            return false;
        }
    }
//public void teachSkill(int id, int skillevel, byte masterlevel, long expiration) {
    //  getPlayer().changeSkillLevelAll(SkillFactory.getSkill(id), skillevel, masterlevel, expiration);
    //}

    public byte isSquadLeader(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        } else {
            if (squad.getLeader() != null && squad.getLeader().getId() == c.getPlayer().getId()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public boolean reAdd(String eim, String squad) {
        EventInstanceManager eimz = getDisconnected(eim);
        MapleSquad squadz = getSquad(squad);
        if (eimz != null && squadz != null) {
            squadz.reAddMember(getPlayer());
            eimz.registerPlayer(getPlayer());
            return true;
        }
        return false;
    }

    public void banMember(String type, int pos) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(pos);
        }
    }

    public void acceptMember(String type, int pos) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.acceptMember(pos);
        }
    }

    public int addMember(String type, boolean join) {
        try {
            final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
            if (squad != null) {
                return squad.addMember(c.getPlayer(), join);
            }
            return -1;
        } catch (NullPointerException ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
            return -1;
        }
    }

    public byte isSquadMember(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        } else {
            if (squad.getMembers().contains(c.getPlayer())) {
                return 1;
            } else if (squad.isBanned(c.getPlayer())) {
                return 2;
            } else {
                return 0;
            }
        }
    }

    public void resetReactors() {
        getPlayer().getMap().resetReactors();
    }

    public void genericGuildMessage(int code) {
        c.getSession().write(GuildPacket.genericGuildMessage((byte) code));
    }

    public void disbandGuild() {
        final int gid = c.getPlayer().getGuildId();
        if (gid <= 0 || c.getPlayer().getGuildRank() != 1) {
            return;
        }
        World.Guild.disbandGuild(gid);
    }

    public void increaseGuildCapacity(boolean trueMax) {
        if (c.getPlayer().getMeso() < 500000 && !trueMax) {
            c.getSession().write(CWvsContext.serverNotice(1, "50만 메소가 충분하지 않습니다."));
            return;
        }
        final int gid = c.getPlayer().getGuildId();
        if (gid <= 0) {
            return;
        }
        if (World.Guild.increaseGuildCapacity(gid, trueMax)) {
            if (!trueMax) {
                c.getPlayer().gainMeso(-500000, true, true);
            } else {
                gainGP(-25000);
            }
            c.sendPacket(NPCPacket.getNPCTalk(2010007, (byte) 0, "\r\n축하하네~ 자네 길드의 인원 수는 이제 #e#b" + c.getPlayer().getGuild().getCapacity() + "명#k#n으로 늘어났네... 앞으로도 더 많이 성장해서 나를 다시 찾아오도록 하게나...", "00 01", (byte) 0));
        }
    }
    
    public void displayGuildRanks() {
        c.sendPacket(GuildPacket.showGuildRanks(id, MapleGuildRanking.getInstance().getRank()));
    }

    public String displayTraningRanks(MapleCharacter user) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String say = "";
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("SELECT name, level, job, traningDamage FROM characters ORDER BY traningDamage desc LIMIT 10");
            rs = ps.executeQuery();
            int count = 1;
            while (rs.next()) {
                if (rs.getLong("traningDamage") > 0) {
                    say += "\r\n#r" + count + "위#k : " + rs.getString("name") + " #d(Lv." + rs.getInt("level") + " / " + MapleCarnivalChallenge.getJobNameById(rs.getInt("job")) + ")#k";
                    say += "\r\n#Cgray#　　  - 데미지 : " + user.getNum(rs.getLong("traningDamage")) + "\r\n";
                    count++;
                }
            }
        } catch (SQLException e) {
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
        }
        return say;
    }

    public void displayMulungRanks() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("SELECT `name`, `time` FROM dojo_ranks ORDER BY `time` ASC LIMIT 50");
            rs = ps.executeQuery();
            c.getSession().write(CWvsContext.getMulungRanks(rs));
        } catch (SQLException e) {
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
        }
    }

    public boolean removePlayerFromInstance() {
        if (c.getPlayer().getEventInstance() != null) {
            c.getPlayer().getEventInstance().removePlayer(c.getPlayer());
            return true;
        }
        return false;
    }

    public boolean isPlayerInstance() {
        if (c.getPlayer().getEventInstance() != null) {
            return true;
        }
        return false;
    }

    public void makeTaintedEquip(byte slot) {
        Equip sel = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        sel.setStr((short) 69);
        sel.setDex((short) 69);
        sel.setInt((short) 69);
        sel.setLuk((short) 69);
        sel.setHp((short) 69);
        sel.setMp((short) 69);
        sel.setWatk((short) 69);
        sel.setMatk((short) 69);
        sel.setWdef((short) 69);
        sel.setMdef((short) 69);
        sel.setAcc((short) 69);
        sel.setAvoid((short) 69);
        sel.setHands((short) 69);
        sel.setSpeed((short) 69);
        sel.setJump((short) 69);
        sel.setUpgradeSlots((byte) 69);
        sel.setViciousHammer((byte) 69);
        sel.setEnhance((byte) 69);
        c.getPlayer().equipChanged();
        c.getPlayer().fakeRelog();
    }

    public void changeStat(byte slot, int type, int amount) {
        Equip sel = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        switch (type) {
            case 0:
                sel.setStr((short) amount);
                break;
            case 1:
                sel.setDex((short) amount);
                break;
            case 2:
                sel.setInt((short) amount);
                break;
            case 3:
                sel.setLuk((short) amount);
                break;
            case 4:
                sel.setHp((short) amount);
                break;
            case 5:
                sel.setMp((short) amount);
                break;
            case 6:
                sel.setWatk((short) amount);
                break;
            case 7:
                sel.setMatk((short) amount);
                break;
            case 8:
                sel.setWdef((short) amount);
                break;
            case 9:
                sel.setMdef((short) amount);
                break;
            case 10:
                sel.setAcc((short) amount);
                break;
            case 11:
                sel.setAvoid((short) amount);
                break;
            case 12:
                sel.setHands((short) amount);
                break;
            case 13:
                sel.setSpeed((short) amount);
                break;
            case 14:
                sel.setJump((short) amount);
                break;
            case 15:
                sel.setUpgradeSlots((byte) amount);
                break;
            case 16:
                sel.setViciousHammer((byte) amount);
                break;
            case 17:
                sel.setLevel((byte) amount);
                break;
            case 18:
                sel.setEnhance((byte) amount);
                break;
            case 19:
                sel.setPotential1(amount);
                break;
            case 20:
                sel.setPotential2(amount);
                break;
            case 21:
                sel.setPotential3(amount);
                break;
            case 22:
                sel.setPotential4(amount);
                break;
            case 23:
                sel.setPotential5(amount);
                break;
            case 24:
                sel.setOwner(getText());
                break;
            default:
                break;
        }
        c.getPlayer().equipChanged();
        c.getPlayer().fakeRelog();
    }

    public void openDuey() {
        c.getPlayer().setConversation(2);
        c.getSession().write(CField.sendDuey((byte) 9, null, null));
    }

    public void openMerchantItemStore() {
        c.getPlayer().setConversation(3);
        HiredMerchantHandler.displayMerch(c);
        //c.getSession().write(CWvsContext.enableActions());
    }

    public void sendPVPWindow() {
        c.sendPacket(CUserLocal.openUI(UIType.PVPLAUNCHER));
        c.sendPacket(CField.sendPVPMaps());
    }

    public void sendOpenUI(UIType ui) {
        c.sendPacket(CUserLocal.openUI(ui));
    }

    public void sendOpenUIWithOption(UIType ui) {
        c.sendPacket(CUserLocal.openUIWithOption(ui, id));
    }

    public void sendOpenUIWithOption(UIType ui, int option) {
        c.sendPacket(CUserLocal.openUIWithOption(ui, option));
    }

    public void sendRepairWindow() {
        c.getSession().write(UIPacket.sendRepairWindow(id));
    }

    public void sendRedLeaf() {
        c.getSession().write(UIPacket.sendRedLeaf(id));
    }

    public void sendAzwan() {
        c.getSession().write(UIPacket.startAzwan(id));
    }

    public void sendProfessionWindow() {
        c.sendPacket(CUserLocal.openUI(UIType.MAKING_SKILL));
    }

    public final int getDojoPoints() {
        return dojo_getPts();
    }

    public final int getDojoRecord() {
        return c.getPlayer().getIntNoRecord(GameConstants.DOJO_RECORD);
    }

    public void setDojoRecord(final boolean reset) {
        if (reset) {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO_RECORD)).setCustomData("0");
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO)).setCustomData("0");
        } else {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO_RECORD)).setCustomData(String.valueOf(c.getPlayer().getIntRecord(GameConstants.DOJO_RECORD) + 1));
        }
    }

    public boolean start_DojoAgent(final boolean dojo, final boolean party) {
        if (dojo) {
            return Event_DojoAgent.warpStartDojo(c.getPlayer(), party);
        }
        return Event_DojoAgent.warpStartAgent(c.getPlayer(), party);
    }

    public boolean start_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpStartPyramid(c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpStartSubway(c.getPlayer());
    }

    public boolean bonus_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpBonusPyramid(c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpBonusSubway(c.getPlayer());
    }

    public final short getKegs() {
        return c.getChannelServer().getFireWorks().getKegsPercentage();
    }

    public void giveKegs(final int kegs) {
        c.getChannelServer().getFireWorks().giveKegs(c.getPlayer(), kegs);
    }

    public final short getSunshines() {
        return c.getChannelServer().getFireWorks().getSunsPercentage();
    }

    public void addSunshines(final int kegs) {
        c.getChannelServer().getFireWorks().giveSuns(c.getPlayer(), kegs);
    }

    public final short getDecorations() {
        return c.getChannelServer().getFireWorks().getDecsPercentage();
    }

    public void addDecorations(final int kegs) {
        try {
            c.getChannelServer().getFireWorks().giveDecs(c.getPlayer(), kegs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final MapleCarnivalParty getCarnivalParty() {
        return c.getPlayer().getCarnivalParty();
    }

    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return c.getPlayer().getNextCarnivalRequest();
    }

    public final MapleCarnivalChallenge getCarnivalChallenge(MapleCharacter chr) {
        return new MapleCarnivalChallenge(chr);
    }

    public void maxStats() {
        Map<MapleStat, Integer> statup = new EnumMap<MapleStat, Integer>(MapleStat.class);
        c.getPlayer().getStat().str = (short) Short.MAX_VALUE;
        c.getPlayer().getStat().dex = (short) Short.MAX_VALUE;
        c.getPlayer().getStat().int_ = (short) Short.MAX_VALUE;
        c.getPlayer().getStat().luk = (short) Short.MAX_VALUE;

        int overrDemon = GameConstants.isDemon(c.getPlayer().getJob()) ? GameConstants.getMPByJob(c.getPlayer()) : 99999;
        c.getPlayer().getStat().maxhp = 99999;
        c.getPlayer().getStat().maxmp = overrDemon;
        c.getPlayer().getStat().setHp(99999, c.getPlayer());
        c.getPlayer().getStat().setMp(overrDemon, c.getPlayer());

        statup.put(MapleStat.Str, Integer.valueOf(Short.MAX_VALUE));
        statup.put(MapleStat.Dex, Integer.valueOf(Short.MAX_VALUE));
        statup.put(MapleStat.Luk, Integer.valueOf(Short.MAX_VALUE));
        statup.put(MapleStat.Int, Integer.valueOf(Short.MAX_VALUE));
        statup.put(MapleStat.HP, Integer.valueOf(99999));
        statup.put(MapleStat.MaxHP, Integer.valueOf(99999));
        statup.put(MapleStat.MP, Integer.valueOf(overrDemon));
        statup.put(MapleStat.MaxMP, Integer.valueOf(overrDemon));
        c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
        c.getSession().write(CWvsContext.OnPlayerStatChanged(statup, c.getPlayer()));
    }

    public int setAndroid(int args) {
        if (args < 30000) {
            c.getPlayer().getAndroid().setFace(args);
            c.getPlayer().getAndroid().saveToDb();
        } else {
            c.getPlayer().getAndroid().setHair(args);
            c.getPlayer().getAndroid().saveToDb();
        }
        c.getPlayer().equipChanged();

        return 1;
    }

    public int getAndroidStat(final String type) {
        if (type.equals("HAIR")) {
            return c.getPlayer().getAndroid().getHair();
        } else if (type.equals("FACE")) {
            return c.getPlayer().getAndroid().getFace();
        } else if (type.equals("GENDER")) {
            int itemid = c.getPlayer().getAndroid().getItemId();
            if (itemid == 1662000 || itemid == 1662002) {
                return 0;
            } else {
                return 1;
            }
        }
        return -1;
    }

    public void reloadChar() {
        getPlayer().getClient().getSession().write(CField.getCharInfo(getPlayer()));
        getPlayer().getMap().removePlayer(getPlayer());
        getPlayer().getMap().addPlayer(getPlayer());
    }

    public void askAndroid(String text, int... args) {
        if (lastMsg > -1) {
            return;
        }
        c.getSession().write(CField.getAndroidTalkStyle(id, text, args));
        lastMsg = 10;
    }

    @Override
    public MapleCharacter getChar() {
        return getPlayer();
    }

    public static int editEquipById(MapleCharacter chr, int max, int itemid, String stat, int newval) {
        return editEquipById(chr, max, itemid, stat, (short) newval);
    }

    public static int editEquipById(MapleCharacter chr, int max, int itemid, String stat, short newval) {
        // Is it an equip?
        if (!MapleItemInformationProvider.getInstance().isEquip(itemid)) {
            return -1;
        }

        // Get List
        List<Item> equips = chr.getInventory(MapleInventoryType.EQUIP).listById(itemid);
        List<Item> equipped = chr.getInventory(MapleInventoryType.EQUIPPED).listById(itemid);

        // Do you have any?
        if (equips.isEmpty() && equipped.isEmpty()) {
            return 0;
        }

        int edited = 0;

        // edit items
        for (Item itm : equips) {
            Equip e = (Equip) itm;
            if (edited >= max) {
                break;
            }
            edited++;
            if (stat.equals("str")) {
                e.setStr(newval);
            } else if (stat.equals("dex")) {
                e.setDex(newval);
            } else if (stat.equals("int")) {
                e.setInt(newval);
            } else if (stat.equals("luk")) {
                e.setLuk(newval);
            } else if (stat.equals("watk")) {
                e.setWatk(newval);
            } else if (stat.equals("matk")) {
                e.setMatk(newval);
            } else {
                return -2;
            }
        }
        for (Item itm : equipped) {
            Equip e = (Equip) itm;
            if (edited >= max) {
                break;
            }
            edited++;
            if (stat.equals("str")) {
                e.setStr(newval);
            } else if (stat.equals("dex")) {
                e.setDex(newval);
            } else if (stat.equals("int")) {
                e.setInt(newval);
            } else if (stat.equals("luk")) {
                e.setLuk(newval);
            } else if (stat.equals("watk")) {
                e.setWatk(newval);
            } else if (stat.equals("matk")) {
                e.setMatk(newval);
            } else {
                return -2;
            }
        }

        // Return items edited
        return (edited);
    }

    public Triple<String, Map<Integer, String>, Long> getSpeedRun(String typ) {
        final ExpeditionType type = ExpeditionType.valueOf(typ);
        if (SpeedRunner.getSpeedRunData(type) != null) {
            return SpeedRunner.getSpeedRunData(type);
        }
        return new Triple<String, Map<Integer, String>, Long>("", new HashMap<Integer, String>(), 0L);
    }

    public boolean getSR(Triple<String, Map<Integer, String>, Long> ma, int sel) {
        if (ma.mid.get(sel) == null || ma.mid.get(sel).length() <= 0) {
            dispose();
            return false;
        }
        sendOk(ma.mid.get(sel));
        return true;
    }

    public Equip getEquip(int itemid) {
        return (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemid);
    }

    public void setExpiration(Object statsSel, long expire) {
        if (statsSel instanceof Equip) {
            ((Equip) statsSel).setExpiration(System.currentTimeMillis() + (expire * 24 * 60 * 60 * 1000));
        }
    }

    public void setLock(Object statsSel) {
        if (statsSel instanceof Equip) {
            Equip eq = (Equip) statsSel;
            if (eq.getExpiration() == -1) {
                eq.setFlag((byte) (eq.getFlag() | ItemFlag.Locked.getValue()));
            } else {
                eq.setFlag((byte) (eq.getFlag() | ItemFlag.Untradable.getValue()));
            }
        }
    }

    public boolean addFromDrop(Object statsSel) {
        if (statsSel instanceof Item) {
            final Item it = (Item) statsSel;
            return MapleInventoryManipulator.checkSpace(getClient(), it.getItemId(), it.getQuantity(), it.getOwner()) && MapleInventoryManipulator.addFromDrop(getClient(), it, false);
        }
        return false;
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type) {
        return replaceItem(slot, invType, statsSel, offset, type, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type, boolean takeSlot) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte) invType);
        if (inv == null) {
            return false;
        }
        Item item = getPlayer().getInventory(inv).getItem((byte) slot);
        if (item == null || statsSel instanceof Item) {
            item = (Item) statsSel;
        }
        if (offset > 0) {
            if (inv != MapleInventoryType.EQUIP) {
                return false;
            }
            Equip eq = (Equip) item;
            if (takeSlot) {
                if (eq.getUpgradeSlots() < 1) {
                    return false;
                } else {
                    eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() - 1));
                }
                if (eq.getExpiration() == -1) {
                    eq.setFlag((byte) (eq.getFlag() | ItemFlag.Locked.getValue()));
                } else {
                    eq.setFlag((byte) (eq.getFlag() | ItemFlag.Untradable.getValue()));
                }
            }
            if (type.equalsIgnoreCase("Slots")) {
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + offset));
                eq.setViciousHammer((byte) (eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("Level")) {
                eq.setLevel((byte) (eq.getLevel() + offset));
            } else if (type.equalsIgnoreCase("Hammer")) {
                eq.setViciousHammer((byte) (eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("STR")) {
                eq.setStr((short) (eq.getStr() + offset));
            } else if (type.equalsIgnoreCase("DEX")) {
                eq.setDex((short) (eq.getDex() + offset));
            } else if (type.equalsIgnoreCase("INT")) {
                eq.setInt((short) (eq.getInt() + offset));
            } else if (type.equalsIgnoreCase("LUK")) {
                eq.setLuk((short) (eq.getLuk() + offset));
            } else if (type.equalsIgnoreCase("HP")) {
                eq.setHp((short) (eq.getHp() + offset));
            } else if (type.equalsIgnoreCase("MP")) {
                eq.setMp((short) (eq.getMp() + offset));
            } else if (type.equalsIgnoreCase("WATK")) {
                eq.setWatk((short) (eq.getWatk() + offset));
            } else if (type.equalsIgnoreCase("MATK")) {
                eq.setMatk((short) (eq.getMatk() + offset));
            } else if (type.equalsIgnoreCase("WDEF")) {
                eq.setWdef((short) (eq.getWdef() + offset));
            } else if (type.equalsIgnoreCase("MDEF")) {
                eq.setMdef((short) (eq.getMdef() + offset));
            } else if (type.equalsIgnoreCase("ACC")) {
                eq.setAcc((short) (eq.getAcc() + offset));
            } else if (type.equalsIgnoreCase("Avoid")) {
                eq.setAvoid((short) (eq.getAvoid() + offset));
            } else if (type.equalsIgnoreCase("Hands")) {
                eq.setHands((short) (eq.getHands() + offset));
            } else if (type.equalsIgnoreCase("Speed")) {
                eq.setSpeed((short) (eq.getSpeed() + offset));
            } else if (type.equalsIgnoreCase("Jump")) {
                eq.setJump((short) (eq.getJump() + offset));
            } else if (type.equalsIgnoreCase("ItemEXP")) {
                eq.setItemEXP(eq.getItemEXP() + offset);
            } else if (type.equalsIgnoreCase("Expiration")) {
                eq.setExpiration((long) (eq.getExpiration() + offset));
            } else if (type.equalsIgnoreCase("Flag")) {
                eq.setFlag((byte) (eq.getFlag() + offset));
            }
            item = eq.copy();
        }
        MapleInventoryManipulator.removeFromSlot(getClient(), inv, (short) slot, item.getQuantity(), false);
        return MapleInventoryManipulator.addFromDrop(getClient(), item, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int upgradeSlots) {
        return replaceItem(slot, invType, statsSel, upgradeSlots, "Slots");
    }

    public boolean isCash(final int itemId) {
        return MapleItemInformationProvider.getInstance().isCash(itemId);
    }

    public int getTotalStat(final int itemId) {
        return MapleItemInformationProvider.getInstance().getTotalStat((Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId));
    }

    public int getReqLevel(final int itemId) {
        return MapleItemInformationProvider.getInstance().getReqLevel(itemId);
    }

    public MapleStatEffect getEffect(int buff) {
        return MapleItemInformationProvider.getInstance().getItemEffect(buff);
    }

    public void buffGuild(final int buff, final int duration, final String msg) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.getItemEffect(buff) != null && getPlayer().getGuildId() > 0) {
            final MapleStatEffect mse = ii.getItemEffect(buff);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    if (chr.getGuildId() == getPlayer().getGuildId()) {
                        mse.applyTo(chr, chr, true, null, duration);
                        chr.dropMessage(5, "Your guild has gotten a " + msg + " buff.");
                    }
                }
            }
        }
    }

    public boolean createAlliance(String alliancename) {
        MapleParty pt = c.getPlayer().getParty();
        MapleCharacter otherChar = c.getChannelServer().getPlayerStorage().getCharacterById(pt.getMemberByIndex(1).getId());
        if (otherChar == null || otherChar.getId() == c.getPlayer().getId()) {
            return false;
        }
        try {
            return World.Alliance.createAlliance(alliancename, c.getPlayer().getId(), otherChar.getId(), c.getPlayer().getGuildId(), otherChar.getGuildId());
        } catch (Exception re) {
            re.printStackTrace();
            return false;
        }
    }

    public boolean addCapacityToAlliance() {
        try {
            final MapleGuild gs = World.Guild.getGuild(c.getPlayer().getGuildId());
            if (gs != null && c.getPlayer().getGuildRank() == 1 && c.getPlayer().getAllianceRank() == 1) {
                if (World.Alliance.getAllianceLeader(gs.getAllianceId()) == c.getPlayer().getId() && World.Alliance.changeAllianceCapacity(gs.getAllianceId())) {
                    gainMeso(-MapleGuildAlliance.CHANGE_CAPACITY_COST);
                    return true;
                }
            }
        } catch (Exception re) {
            re.printStackTrace();
        }
        return false;
    }

    public boolean disbandAlliance() {
        try {
            final MapleGuild gs = World.Guild.getGuild(c.getPlayer().getGuildId());
            if (gs != null && c.getPlayer().getGuildRank() == 1 && c.getPlayer().getAllianceRank() == 1) {
                if (World.Alliance.getAllianceLeader(gs.getAllianceId()) == c.getPlayer().getId() && World.Alliance.disbandAlliance(gs.getAllianceId())) {
                    return true;
                }
            }
        } catch (Exception re) {
            re.printStackTrace();
        }
        return false;
    }

    public byte getLastMsg() {
        return lastMsg;
    }

    public final void setLastMsg(final byte last) {
        this.lastMsg = last;
    }

    public final void maxAllSkills() {
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(skil.getId()) && skil.getId() < 90000000) { //no db/additionals/resistance skills
                sa.put(skil, new SkillEntry((byte) skil.getMaxLevel(), (byte) skil.getMaxLevel(), SkillFactory.getDefaultSExpiry(skil)));
            }
        }
        getPlayer().changeSkillsLevel(sa);
    }

    public final void maxSkillsByJob() {
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(skil.getId()) && skil.canBeLearnedBy(getPlayer().getJob())) { //no db/additionals/resistance skills
                if (!skil.isInvisible()) {
                    sa.put(skil, new SkillEntry((byte) skil.getMaxLevel(), (byte) skil.getMaxLevel(), SkillFactory.getDefaultSExpiry(skil)));
                }
            }
        }
        getPlayer().changeSkillsLevel(sa);
    }

    public final void resetStats(int str, int dex, int z, int luk) {
        c.getPlayer().resetStats(str, dex, z, luk);
    }

    public void killAllMonsters(int mapid) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
        map.killAllMonsters(false); // No drop.
    }

    public void cleardrops() {
        MapleMonsterInformationProvider.getInstance().clearDrops();
    }

    public final boolean dropItem(int slot, int invType, int quantity) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte) invType);
        if (inv == null) {
            return false;
        }
        return MapleInventoryManipulator.drop(c, inv, (short) slot, (short) quantity, true);
    }

    public final List<Integer> getAllPotentialInfo() {
        List<Integer> list = new ArrayList<>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().keySet());
        Collections.sort(list);
        return list;
    }

    public final List<Integer> getAllPotentialInfoSearch(String content) {
        List<Integer> list = new ArrayList<>();
        for (Entry<Integer, List<StructItemOption>> i : MapleItemInformationProvider.getInstance().getAllPotentialInfo().entrySet()) {
            for (StructItemOption ii : i.getValue()) {
                if (ii.toString().contains(content)) {
                    list.add(i.getKey());
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    public void MakeGMItem(byte slot, MapleCharacter player) {
        MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
        Equip eu = (Equip) equip.getItem(slot);
        int item = equip.getItem(slot).getItemId();
        short hand = eu.getHands();
        byte level = eu.getLevel();
        Equip nItem = new Equip(item, slot, (byte) 0);
        nItem.setStr((short) 32767); // STR
        nItem.setDex((short) 32767); // DEX
        nItem.setInt((short) 32767); // INT
        nItem.setLuk((short) 32767); //LUK
        nItem.setUpgradeSlots((byte) 0);
        nItem.setHands(hand);
        nItem.setLevel(level);
        player.getInventory(MapleInventoryType.EQUIP).removeItem(slot);
        player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
    }

    public final String getPotentialInfo(final int id) {
        final List<StructItemOption> potInfo = MapleItemInformationProvider.getInstance().getPotentialInfo(id);
        final StringBuilder builder = new StringBuilder("#b#ePOTENTIAL INFO FOR ID: ");
        builder.append(id);
        builder.append("#n#k\r\n\r\n");
        int minLevel = 1, maxLevel = 10;
        for (StructItemOption item : potInfo) {
            builder.append("#eLevels ");
            builder.append(minLevel);
            builder.append("~");
            builder.append(maxLevel);
            builder.append(": #n");
            builder.append(item.get(potInfo.toString()));
            minLevel += 10;
            maxLevel += 10;
            builder.append("\r\n");
        }
        return builder.toString();
    }

    public final void sendRPS() {
        c.getSession().write(CField.getRPSMode((byte) 8, -1, -1, -1));
    }

    public final void setQuestRecord(Object ch, final int questid, final String data) {
        ((MapleCharacter) ch).getQuestNAdd(MapleQuest.getInstance(questid)).setCustomData(data);
    }

    public final void doWeddingEffect(final Object ch) {
        final MapleCharacter chr = (MapleCharacter) ch;
        final MapleCharacter player = getPlayer();
        getMap().broadcastMessage(CWvsContext.yellowChat(player.getName() + ", do you take " + chr.getName() + " as your wife and promise to stay beside her through all downtimes, crashes, and lags?"));
        CloneTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (chr == null || player == null) {
                    warpMap(680000500, 0);
                } else {
                    chr.getMap().broadcastMessage(CWvsContext.yellowChat(chr.getName() + ", do you take " + player.getName() + " as your husband and promise to stay beside him through all downtimes, crashes, and lags?"));
                }
            }
        }, 10000);
        CloneTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (chr == null || player == null) {
                    if (player != null) {
                        setQuestRecord(player, 160001, "3");
                        setQuestRecord(player, 160002, "0");
                    } else if (chr != null) {
                        setQuestRecord(chr, 160001, "3");
                        setQuestRecord(chr, 160002, "0");
                    }
                    warpMap(680000500, 0);
                } else {
                    setQuestRecord(player, 160001, "2");
                    setQuestRecord(chr, 160001, "2");
                    sendNPCText(player.getName() + " and " + chr.getName() + ", I wish you two all the best on your " + LoginServer.getServerName() + " journey together!", 9201002);
                    chr.getMap().floatNotice("You may now kiss the bride, " + player.getName() + "!", 5120006, false);
                    if (chr.getGuildId() > 0) {
                        World.Guild.guildPacket(chr.getGuildId(), CWvsContext.sendMarriage(false, chr.getName()));
                    }
                    if (chr.getFamilyId() > 0) {
                        World.Family.familyPacket(chr.getFamilyId(), CWvsContext.sendMarriage(true, chr.getName()), chr.getId());
                    }
                    if (player.getGuildId() > 0) {
                        World.Guild.guildPacket(player.getGuildId(), CWvsContext.sendMarriage(false, player.getName()));
                    }
                    if (player.getFamilyId() > 0) {
                        World.Family.familyPacket(player.getFamilyId(), CWvsContext.sendMarriage(true, chr.getName()), player.getId());
                    }
                }
            }
        }, 20000); //10 sec 10 sec

    }

    public void putKey(int key, int type, int action) {
        getPlayer().changeKeybinding(key, (byte) type, action);
        getClient().getSession().write(CField.getKeymap(getPlayer().getKeyLayout()));
    }

    public void doRing(final String name, final int itemid) {
        PlayersHandler.DoRing(getClient(), name, itemid);
    }

    public int getNaturalStats(final int itemid, final String it) {
        Map<String, Integer> eqStats = MapleItemInformationProvider.getInstance().getEquipStats(itemid);
        if (eqStats != null && eqStats.containsKey(it)) {
            return eqStats.get(it);
        }
        return 0;
    }

    public boolean isEligibleName(String t) {
        return MapleCharacterUtil.canCreateChar(t, getPlayer().isGM()) && (!LoginInformationProvider.getInstance().isForbiddenName(t) || getPlayer().isGM());
    }

    public String checkDrop(int mobId) {
        final List<MonsterDropEntry> ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId);
        if (ranks != null && ranks.size() > 0) {
            int num = 0, itemId = 0, ch = 0;
            MonsterDropEntry de;
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < ranks.size(); i++) {
                de = ranks.get(i);
                if (de.chance > 0 && (de.questid <= 0 || (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0))) {
                    itemId = de.itemId;
                    if (num == 0) {
                        name.append("#o" + mobId + "# 몬스터가 드롭하는 아이템 정보입니다. \r\n");
                        name.append("--------------------------------------\r\n");
                    }
                    String namez = "#z" + itemId + "#";
                    if (itemId == 0) { //meso
                        itemId = 4031041; //display sack of cash
                        namez = (de.Minimum * getClient().getChannelServer().getMesoRate()) + " ~ " + (de.Maximum * getClient().getChannelServer().getMesoRate()) + " 랜덤 메소";
                    }
                    ch = de.chance * getClient().getChannelServer().getDropRate();
                    name.append((num + 1) + ") #v" + itemId + "#" + namez + " - " + (Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0) + "% 퍼센트 " + (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0 ? ("") : "") + "\r\n");
                    num++;
                }
            }
            if (name.length() > 0) {
                return name.toString();
            }

        }
        return "No drops was returned.";
    }

    public String getLeftPadded(final String in, final char padchar, final int length) {
        return StringUtil.getLeftPaddedStr(in, padchar, length);
    }

    public void handleDivorce() {
        if (getPlayer().getMarriageId() <= 0) {
            sendNext("Please make sure you have a marriage.");
            return;
        }
        final int chz = World.Find.findChannel(getPlayer().getMarriageId());
        if (chz == -1) {
            Connection con = null;
            PreparedStatement ps = null;
            try {
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement("UPDATE queststatus SET customData = ? WHERE characterid = ? AND (quest = ? OR quest = ?)");
                ps.setString(1, "0");
                ps.setInt(2, getPlayer().getMarriageId());
                ps.setInt(3, 160001);
                ps.setInt(4, 160002);
                ps.executeUpdate();
                ps.close();

                ps = con.prepareStatement("UPDATE characters SET marriageid = ? WHERE id = ?");
                ps.setInt(1, 0);
                ps.setInt(2, getPlayer().getMarriageId());
                ps.executeUpdate();
                ps.close();
                con.close();
            } catch (SQLException e) {
                outputFileError(e);
                return;
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
            setQuestRecord(getPlayer(), 160001, "0");
            setQuestRecord(getPlayer(), 160002, "0");
            getPlayer().setMarriageId(0);
            sendNext("You have been successfully divorced...");
            return;
        } else if (chz < -1) {
            sendNext("Please make sure your partner is logged on.");
            return;
        }
        MapleCharacter cPlayer = ChannelServer.getInstance(chz).getPlayerStorage().getCharacterById(getPlayer().getMarriageId());
        if (cPlayer != null) {
            cPlayer.dropMessage(1, "Your partner has divorced you.");
            cPlayer.setMarriageId(0);
            setQuestRecord(cPlayer, 160001, "0");
            setQuestRecord(getPlayer(), 160001, "0");
            setQuestRecord(cPlayer, 160002, "0");
            setQuestRecord(getPlayer(), 160002, "0");
            getPlayer().setMarriageId(0);
            sendNext("You have been successfully divorced...");
        } else {
            sendNext("An error occurred...");
        }
    }

    public String getReadableMillis(long startMillis, long endMillis) {
        return StringUtil.getReadableMillis(startMillis, endMillis);
    }

    public void sendUltimateExplorer() {
        getClient().getSession().write(CWvsContext.ultimateExplorer());
    }

    public void sendPendant(boolean b) {
        c.getSession().write(CWvsContext.pendantSlot(b));
    }

    public void changeJobById(short job) {
        c.getPlayer().changeJob(job);
    }

    public int getJobId() {
        return getPlayer().getJob();
    }

    public int getLevel() {
        return getPlayer().getLevel();
    }

    public int getEquipId(byte slot) {
        MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
        Equip eu = (Equip) equip.getItem(slot);
        return equip.getItem(slot).getItemId();
    }

    public int getUseId(byte slot) {
        MapleInventory use = getPlayer().getInventory(MapleInventoryType.USE);
        return use.getItem(slot).getItemId();
    }

    public int getSetupId(byte slot) {
        MapleInventory setup = getPlayer().getInventory(MapleInventoryType.SETUP);
        return setup.getItem(slot).getItemId();
    }

    public int getCashId(byte slot) {
        MapleInventory cash = getPlayer().getInventory(MapleInventoryType.CASH);
        return cash.getItem(slot).getItemId();
    }

    public int getETCId(byte slot) {
        MapleInventory etc = getPlayer().getInventory(MapleInventoryType.ETC);
        return etc.getItem(slot).getItemId();
    }

    public String EquipList(MapleClient c) {
        StringBuilder str = new StringBuilder();
        MapleInventory equip = c.getPlayer().getInventory(MapleInventoryType.EQUIP);
        List<String> stra = new LinkedList<String>();
        for (Item item : equip.list()) {
            stra.add("#b#L" + item.getPosition() + "##v" + item.getItemId() + "##z" + item.getItemId() + "##l#k\r\n");
        }
        for (String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }

    public String UseList(MapleClient c) {
        StringBuilder str = new StringBuilder();
        MapleInventory use = c.getPlayer().getInventory(MapleInventoryType.USE);
        List<String> stra = new LinkedList<String>();
        for (Item item : use.list()) {
            stra.add("#L" + item.getPosition() + "##v" + item.getItemId() + "##l");
        }
        for (String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }

    public String CashList(MapleClient c) {
        StringBuilder str = new StringBuilder();
        MapleInventory cash = c.getPlayer().getInventory(MapleInventoryType.CASH);
        List<String> stra = new LinkedList<String>();
        for (Item item : cash.list()) {
            stra.add("#L" + item.getPosition() + "##v" + item.getItemId() + "##l");
        }
        for (String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }

    public String ETCList(MapleClient c) {
        StringBuilder str = new StringBuilder();
        MapleInventory etc = c.getPlayer().getInventory(MapleInventoryType.ETC);
        List<String> stra = new LinkedList<String>();
        for (Item item : etc.list()) {
            stra.add("#L" + item.getPosition() + "##v" + item.getItemId() + "##l");
        }
        for (String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }

    public String SetupList(MapleClient c) {
        StringBuilder str = new StringBuilder();
        MapleInventory setup = c.getPlayer().getInventory(MapleInventoryType.SETUP);
        List<String> stra = new LinkedList<String>();
        for (Item item : setup.list()) {
            stra.add("#L" + item.getPosition() + "##v" + item.getItemId() + "##l");
        }
        for (String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }

    //Two methods below are used for pendant slot in donor npc.
    public void setCData(String quest, String data) {
        if (quest.equalsIgnoreCase("pendant")) {
            final MapleQuestStatus record = getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
            record.setCustomData(data);
        }
    }

    public long getCData(String quest) {
        if (quest.equalsIgnoreCase("pendant")) {
            MapleQuest test = MapleQuest.getInstance(GameConstants.PENDANT_SLOT);
            final MapleQuestStatus record = getPlayer().getQuestNAdd(test);
            if (record.getCustomData() != null) {
                return Long.parseLong(record.getCustomData());
            }
        }
        return 0;
    }

    public String startSpeedQuiz() {
        if (c.getPlayer().getSpeedQuiz() != null) {
            c.getPlayer().setSpeedQuiz(null);
            return "Ahh..it seemed that something was broken. Please let the admins know about this issue right away!";
        }
        final long time = c.getPlayer().getLastSpeedQuiz();
        final long now = System.currentTimeMillis();
        if (time > 0) { // Contains data
            boolean can = (time + 3600000) < now;
            if (!can) {
                int remaining = (int) ((((time + 3600000) - now) / 1000) / 60);
                return "You've already tried the speed quiz in the past hour. Please come back again in " + remaining + " minutes.";
            }
        }
        c.getPlayer().setLastSpeedQuiz(now);
        c.getPlayer().setSpeedQuiz(new SpeedQuiz(c, id)); // 0 mode 
        return null;
    }

    public int[] getAvailableStyles(int[] styles) {
        List<Integer> exist = new ArrayList<Integer>();
        for (int i = 0; i < styles.length; i++) {
            if (MapleItemInformationProvider.getInstance().itemExist(styles[i])) {
                exist.add(styles[i]);
            }
        }
        return toIntArray(exist);
    }

    private int[] toIntArray(List<Integer> integerList) {
        int[] intArray = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); i++) {
            intArray[i] = integerList.get(i);
        }
        return intArray;
    }

    public void givePartyBuff(int buff) {
        if (c.getPlayer().getParty() != null) {
            MapleStatEffect effect = getEffect(buff);
            for (MaplePartyCharacter pchr : c.getPlayer().getParty().getMembers()) {
                MapleCharacter chr = c.getPlayer().getMap().getCharacterById(pchr.getId());
                if (chr != null) {
                    effect.applyTo(chr);
                }
            }
        }
    }

    public void giveRecommend(int id) {
        //  MapleCharacter chr = c.getPlayer().getMap().getCharacterById(id);
        final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterById(id);
        if (c.getPlayer().getRecommend() == 0) {
            if (chr != null) {
                DueyHandler.addNewItemToDb(chr, 4310000, 25, 0, chr.getId(), "[추천인]", "보상지급 ", true);
                chr.modifyCSPoints(1, 5000);
            } // 밑에줄은 추천누른사람보상으로
            c.getPlayer().dropMessage(2430260, " 추천인 등록이 완료되었습니다! 즐거운 에레브 아일렌드 되세요.");
            c.getPlayer().modifyCSPoints(1, 5000);
            DueyHandler.addNewItemToDb(c.getPlayer(), 2430260, 1, 0, c.getPlayer().getId(), "[추천인]", "보상지급 ", true);
            DueyHandler.addNewItemToDb(c.getPlayer(), 2450064, 1, 0, c.getPlayer().getId(), "[이벤트]", "보상지급 ", true);
        }
        c.getPlayer().setRecommend(1);
    }

    public void giveEventStar(int id) {
        if (ChannelServer.getInstance(1).Eventstat() == true) {
            if (getQuestStatus(11200) == 2 || getQuestStatus(11214) == 2 || getQuestStatus(11207) == 2) {
                int questid = 11200;
                if (getQuestStatus(11200) == 2) {
                    questid = 11200;
                } else if (getQuestStatus(11207) == 2) {
                    questid = 11207;
                } else if (getQuestStatus(11214) == 2) {
                    questid = 11214;
                }
                MapleQuestStatus marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(questid));
                if (marr.getCustomData() == null) {
                    marr.setCustomData("0");
                }
                if (c.getPlayer().getDateKeyS("라이징스타") == "3") {
                    sendNext("앗 이미 보상을 전부 받으셨어요.");
                } else {
                    if (Long.parseLong(marr.getCustomData()) < System.currentTimeMillis()) {
                        final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterById(id);
                        if (ChannelServer.getInstance(1).Eventstatchr() == 0 && getQuestStatus(11200) == 2
                                || ChannelServer.getInstance(1).Eventstatchr() == 1 && getQuestStatus(11207) == 2
                                || ChannelServer.getInstance(1).Eventstatchr() == 2 && getQuestStatus(11214) == 2) {
                            DueyHandler.addNewItemToDb(chr, 4310030, 2, 0, chr.getId(), "[보상]", "보상지급 ", true);
                            c.getPlayer().dropMessage(4310030, " 자신이 응원하는 스타의 선물! 즐거운 에레브 아일렌드 되세요!");
                        } else {
                            c.getPlayer().dropMessage(4310000, " 메인이 아니라 아쉽지만 응원하는 스타의 선물! 즐거운 에레브 아일렌드 되세요!");
                        }
                        DueyHandler.addNewItemToDb(chr, 4310000, 15, 0, chr.getId(), "[보상]", "보상지급 ", true);
                        marr.setCustomData(String.valueOf(System.currentTimeMillis() + (30 * 60 * 1000)));
                        c.getPlayer().setDateKeyS("라이징스타", (Integer.parseInt(c.getPlayer().getDateKeyS("라이징스타")) + 1) + "");
                        sendNext("응원해주셔서 감사합니다!!");

                    } else {
                        sendNext("앗 이미 보상을 받으셨어요.");
                    }
                }
            } else {
                sendNext("팬클럽에 가입하시지 않으신거같아요.");
            }
        } else {
            sendNext("아직 공연이 준비되지않았어요~");
        }
    }

    public void sendDemonSelect5() {
        c.getSession().write(UIPacket.메소마켓(c.getPlayer().getId()));
    }

    public void 원정대(final String type, final String eventname, final int questid) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        // 원정대 생성
        registerSquad(type, 5, "원정대 입장한당...");
        // 원정대 가입
        for (MaplePartyCharacter pchr : c.getPlayer().getParty().getMembers()) {
            MapleCharacter chr = c.getPlayer().getMap().getCharacterById(pchr.getId());
            if (squad != null && chr != null) {
                squad.addMember(chr, true);
            }
        }
        //원정대이벤트스크립트 작동 + 입장
        getEventManager(eventname).startInstance(getSquad(type), c.getPlayer().getMap(), questid);
    }

    public final boolean BossCheck(String a, int i) {
        for (final MapleCharacter chr : getPartyMembers()) {
            if (chr.getDateKey(a) == null) {
                chr.setDateKey(a, "0");
            }
            if (Integer.parseInt(chr.getDateKey(a)) >= i) {
                return false;
            }
        }
        return true;
    }

    public final void BossAdd(String a) {
        for (final MapleCharacter partymem : getPartyMembers()) {
            partymem.setDateKey(a, (Integer.parseInt(partymem.getDateKey(a)) + 1) + "");
        }
    }

    public final void BossAddSquad(String name, String a) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(name);
        for (String mmbr : squad.getMembers()) {
            MapleCharacter chr = c.getPlayer().getMap().getCharacterByName(mmbr);
            chr.setDateKey(a, (Integer.parseInt(chr.getDateKey(a)) + 1) + "");
        }
    }

    public final boolean BossCheckSquad(String name, String a, int i) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(name);
        for (String mmbr : squad.getMembers()) {
            MapleCharacter chr = c.getPlayer().getMap().getCharacterByName(mmbr);
            if (chr.getDateKey(a) == null) {
                chr.setDateKey(a, "0");
            }
            if (Integer.parseInt(chr.getDateKey(a)) >= i) {
                return false;
            }
        }
        return true;
    }

    public String getcashItemNames(String a) {
        StringBuilder sb = new StringBuilder();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        try {
            for (Pair<Integer, String> itemPair : ii.getEqItemss()) {
                if (itemPair.getRight().contains(a)) {
                    if (ii.isCash(itemPair.getLeft())) {
                        sb.append("\r\n#L").append(itemPair.getLeft()).append("##i").append(itemPair.getLeft()).append(":#").append(itemPair.getRight()).append("#l");
                    }
                }
            }
        } catch (Exception ex) {
        }
        if (!sb.toString().equals("")) {
            return sb.toString();
        } else {
            return "\r\n#r잘못된 아이템 이름입니다.#k";
        }
    }

    public void getMulungBuff(String cText) {
        if (lastMsg > -1) {
            return;
        }
        c.getSession().write(NPCPacket.getMulungBuff(cText));
        lastMsg = (byte) 0x10;
    }
}
