package tools.packet;

import client.CharacterTemporaryStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleKeyLayout;
import client.MapleQuestStatus;
import client.MapleStat;
import client.Skill;
import client.SkillMacro;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleAndroid;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import constants.MapConstants;
import handling.SendPacketOpcode;
import handling.UserEffectType;
import handling.cashshop.CashShopServer;
import handling.channel.handler.EventList;
import handling.channel.handler.PlayerInteractionHandler;
import handling.world.World;
import handling.world.guild.MapleGuild;

import java.awt.Point;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import server.MapleDueyActions;
import server.MapleShop;
import server.MapleTrade;
import server.Randomizer;
import server.events.MapleSnowball;
import server.life.MapleNPC;
import server.maps.MapleDragon;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMist;
import server.maps.MapleNodes;
import server.maps.MapleReactor;
import server.maps.MapleSummon;
import server.maps.MechDoor;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import tools.AttackPair;
import tools.HexTool;
import tools.Pair;
import tools.Triple;
import tools.data.OutPacket;

public class CField {

    public static int DEFAULT_BUFFMASK = 0;

    static {
        DEFAULT_BUFFMASK |= CharacterTemporaryStat.EnergyCharge.getValue();
        DEFAULT_BUFFMASK |= CharacterTemporaryStat.DashSpeed.getValue();
        DEFAULT_BUFFMASK |= CharacterTemporaryStat.DashJump.getValue();
        DEFAULT_BUFFMASK |= CharacterTemporaryStat.MonsterRiding.getValue();
        DEFAULT_BUFFMASK |= CharacterTemporaryStat.WindBooster.getValue();
        DEFAULT_BUFFMASK |= CharacterTemporaryStat.GuidedBullet.getValue();
        DEFAULT_BUFFMASK |= CharacterTemporaryStat.Undead.getValue();
    }

    public static byte[] getPacketFromHexString(String hex) {
        return HexTool.getByteArrayFromHexString(hex);
    }

    public static byte[] getServerIP(int port, int clientId) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SERVER_IP.getValue());
        mplew.write(0);
        mplew.write(0);
        try {
            mplew.write(InetAddress.getByName(CashShopServer.getonlyIP()).getAddress());
        } catch (UnknownHostException e) {
            System.out.println("[Error] ServerIP");
        }
        mplew.writeShort(port);
        mplew.writeInt(clientId);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeZeroBytes(6);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] getChannelChange(int port) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CHANGE_CHANNEL.getValue());
        mplew.write(1);
        try {
            mplew.write(InetAddress.getByName(CashShopServer.getonlyIP()).getAddress());
        } catch (UnknownHostException e) {
            System.out.println("[Error] ServerIP");
        }
        mplew.writeShort(port);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] getPVPType(int type, List<Pair<Integer, String>> players1, int team, boolean enabled, int lvl) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_TYPE.getValue());
        mplew.write(type);
        mplew.write(lvl);
        mplew.write(enabled ? 1 : 0);
        mplew.write(0);
        if (type > 0) {
            mplew.write(team);
            mplew.writeInt(players1.size());
            for (Pair pl : players1) {
                mplew.writeInt(((Integer) pl.left).intValue());
                mplew.writeMapleAsciiString((String) pl.right);
                mplew.writeShort(2660);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPTransform(int type) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_TRANSFORM.getValue());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] getPVPDetails(List<Pair<Integer, Integer>> players) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_DETAILS.getValue());
        mplew.write(1);
        mplew.write(0);
        mplew.writeInt(players.size());
        for (Pair pl : players) {
            mplew.writeInt(((Integer) pl.left));
            mplew.write(((Integer) pl.right));
        }

        return mplew.getPacket();
    }

    public static byte[] enablePVP(boolean enabled) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_ENABLED.getValue());
        mplew.write(enabled ? 1 : 2);

        return mplew.getPacket();
    }

    public static byte[] getPVPScore(int score, boolean kill) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_SCORE.getValue());
        mplew.writeInt(score);
        mplew.write(kill ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] FlipTheCoin(boolean on) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.FLIP_THE_COIN.getValue());
        mplew.write(on ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] getPVPResult(List<Pair<Integer, MapleCharacter>> flags, int exp, int winningTeam, int playerTeam) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_RESULT.getValue());
        mplew.writeInt(flags.size());
        for (Pair f : flags) {
            mplew.writeInt(((MapleCharacter) f.right).getId());
            mplew.writeMapleAsciiString(((MapleCharacter) f.right).getName());
            mplew.writeInt(((Integer) f.left).intValue());
            mplew.writeShort(((MapleCharacter) f.right).getTeam() + 1);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        mplew.writeZeroBytes(24);
        mplew.writeInt(exp);
        mplew.write(0);
        mplew.writeShort(100);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(winningTeam);
        mplew.write(playerTeam);

        return mplew.getPacket();
    }

    public static byte[] getPVPTeam(List<Pair<Integer, String>> players) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_TEAM.getValue());
        mplew.writeInt(players.size());
        for (Pair pl : players) {
            mplew.writeInt(((Integer) pl.left).intValue());
            mplew.writeMapleAsciiString((String) pl.right);
            mplew.writeShort(2660);
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPScoreboard(List<Pair<Integer, MapleCharacter>> flags, int type) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_SCOREBOARD.getValue());
        mplew.writeShort(flags.size());
        for (Pair f : flags) {
            mplew.writeInt(((MapleCharacter) f.right).getId());
            mplew.writeMapleAsciiString(((MapleCharacter) f.right).getName());
            mplew.writeInt(((Integer) f.left).intValue());
            mplew.write(type == 0 ? 0 : ((MapleCharacter) f.right).getTeam() + 1);
        }
        System.out.println("getPVPScoreboard: " + mplew.toString());
        return mplew.getPacket();
    }

    public static byte[] getPVPPoints(int p1, int p2) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_POINTS.getValue());
        mplew.writeInt(p1);
        mplew.writeInt(p2);

        return mplew.getPacket();
    }

    public static byte[] getPVPKilled(String lastWords) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_KILLED.getValue());
        mplew.writeMapleAsciiString(lastWords);

        return mplew.getPacket();
    }

    public static byte[] getPVPMode(int mode) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_MODE.getValue());
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] getPVPIceHPBar(int hp, int maxHp) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_ICEKNIGHT.getValue());
        mplew.writeInt(hp);
        mplew.writeInt(maxHp);

        return mplew.getPacket();
    }

    public static byte[] getCaptureFlags(MapleMap map) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CAPTURE_FLAGS.getValue());
        mplew.writeRect(map.getArea(0));
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(0)).left).x);
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(0)).left).y);
        mplew.writeRect(map.getArea(1));
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(1)).left).x);
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(1)).left).y);

        return mplew.getPacket();
    }

    public static byte[] getCapturePosition(MapleMap map) {
        OutPacket mplew = new OutPacket();

        Point p1 = map.getPointOfItem(2910000);
        Point p2 = map.getPointOfItem(2910001);
        mplew.writeShort(SendPacketOpcode.CAPTURE_POSITION.getValue());
        mplew.write(p1 == null ? 0 : 1);
        if (p1 != null) {
            mplew.writeInt(p1.x);
            mplew.writeInt(p1.y);
        }
        mplew.write(p2 == null ? 0 : 1);
        if (p2 != null) {
            mplew.writeInt(p2.x);
            mplew.writeInt(p2.y);
        }

        return mplew.getPacket();
    }

    public static byte[] resetCapture() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CAPTURE_RESET.getValue());

        return mplew.getPacket();
    }

    public static byte[] getMacros(SkillMacro[] macros) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnMacroSysDataInit.getValue());
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (macros[i] != null) {
                count++;
            }
        }
        mplew.write(count);
        for (int i = 0; i < 5; i++) {
            SkillMacro macro = macros[i];
            if (macro != null) {
                mplew.writeMapleAsciiString(macro.getName());
                mplew.write(macro.getShout());
                mplew.writeInt(macro.getSkill1());
                mplew.writeInt(macro.getSkill2());
                mplew.writeInt(macro.getSkill3());
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getCharInfo(MapleCharacter chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SET_FIELD.getValue());
        mplew.writeLong(chr.getClient().getChannel() - 1);
        mplew.write(0);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.write(1);
        mplew.writeShort(0);
        int seed1 = Randomizer.nextInt();
        int seed2 = Randomizer.nextInt();
        int seed3 = Randomizer.nextInt();
        chr.getCalcDamage().SetSeed(seed1, seed2, seed3);
        mplew.writeInt(seed1);
        mplew.writeInt(seed2);
        mplew.writeInt(seed3);
        //End random seed        
        //chr.CRand().connectData(mplew); // Random number generator
        PacketHelper.addCharacterInfo(mplew, chr);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeInt(100);
        mplew.write(0); //Resistance Check
        mplew.write(GameConstants.isPhantom(chr.getJob()) ? 1 : 0);
        // mplew.write(1);
        // if (chr.getMap().getFieldType().equals("63") || chr.getMap().equals("36")) {
        //     mplew.write(0);
        //}
        return mplew.getPacket();
    }

    public static byte[] getWarpToMap(final MapleMap to, final int spawnPoint, final MapleCharacter chr) {

        final OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.SET_FIELD.getValue());

        mplew.writeLong(chr.getClient().getChannel() - 1);
        mplew.write(0);
        mplew.write(to.getPortals().size());
        mplew.writeInt(0);
        mplew.writeInt(0);

        mplew.writeInt(to.getId());
        mplew.write(spawnPoint);
        mplew.writeInt(chr.getStat().getHp());
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeInt(100);
        mplew.write(0);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] spawnFlags(List<Pair<String, Integer>> flags) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.LOGIN_WELCOME.getValue());
        mplew.write(2);//플래그사이즈
        mplew.writeMapleAsciiString("main");
        mplew.writeInt(1); // CRC
        mplew.writeMapleAsciiString("sub");
        mplew.writeInt(Randomizer.rand(0, 1)); // CRC
        /*
        mplew.write(flags == null ? 0 : flags.size());
        if (flags != null) {
            for (Pair f : flags) {
                mplew.writeMapleAsciiString((String) f.left);
                mplew.write(((Integer) f.right));
            }
        }
         */
        return mplew.getPacket();
    }

    public static byte[] serverBlocked(int type) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.TRANSFER_CHANNEL_REQ_IGNORED.getValue());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] pvpBlocked(int type) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.TRANSFER_PVP_REQ_IGNORED.getValue());
        mplew.write(type);
        System.out.println("pvpBlocked: Type: " + type);
        return mplew.getPacket();
    }

    public static byte[] showEquipEffect() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.FIELD_SPECIFIC_DATA.getValue());

        return mplew.getPacket();
    }

    public static byte[] showEquipEffect(int team) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.FIELD_SPECIFIC_DATA.getValue());
        mplew.writeShort(team);

        return mplew.getPacket();
    }

    public static byte[] multiChat(String name, String chattext, int mode) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.GROUP_MESSAGE.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithCS(String target, boolean buddy) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(2);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithMTS(String target, boolean buddy) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(0);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static byte[] getWhisper(String sender, int channel, String text) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(18);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(channel - 1);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] getWhisperReply(String target, byte reply) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(10);
        mplew.writeMapleAsciiString(target);
        mplew.write(reply);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithMap(String target, int mapid, boolean buddy) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(1);
        mplew.writeInt(mapid);
        mplew.writeZeroBytes(8);

        return mplew.getPacket();
    }

    public static byte[] getFindReply(String target, int channel, boolean buddy) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);
        mplew.writeInt(channel - 1);

        return mplew.getPacket();
    }

    public static final byte[] MapEff(String path) {
        return environmentChange(path, 3);
    }

    public static final byte[] MapNameDisplay(int mapid) {
        return environmentChange("maplemap/enter/" + mapid, 3);
    }

    public static final byte[] Aran_Start() {
        return environmentChange("Aran/balloon", 4);
    }

    public static final byte[] Aran_ComboAttack(String data) {
        return environmentChange(data, 8);
    }

    public static byte[] musicChange(String song) {
        return environmentChange(song, 6);
    }

    public static byte[] showEffect(String effect) {
        return environmentChange(effect, 3);
    }

    public static byte[] playSound(String sound) {
        return environmentChange(sound, 4);
    }

    public static byte[] playSounds(String sound) {
        return environmentChanges(sound);
    }

    public static byte[] environmentChange(String env, int mode) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(env);
        //mplew.writeInt(0); // 160에서 사라짐?
        return mplew.getPacket();
    }

    public static byte[] environmentChanges(String uol) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        mplew.write(3);
        mplew.writeMapleAsciiString(uol);
        //mplew.write(0); 160에서 사라짐
        return mplew.getPacket();
    }

    public static byte[] trembleEffect(int type, int delay) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        mplew.write(1);
        mplew.write(type);
        mplew.writeInt(delay);

        return mplew.getPacket();
    }

    public static byte[] floatNotice(String msg, int itemid, boolean active) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.FLOAT_NOTICE.getValue());
        oPacket.EncodeInt(active ? itemid : 0);
        if (active) {
            oPacket.EncodeString(msg);
        }
        return oPacket.getPacket();
    }

    public static byte[] GameMaster_Func(int value) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.ADMIN_RESULT.getValue());
        mplew.write(value);
        mplew.writeZeroBytes(17);

        return mplew.getPacket();
    }

    public static byte[] GmHide(boolean ff) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.ADMIN_RESULT.getValue());
        mplew.write(24);
        mplew.write(ff ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.QUIZ.getValue());
        mplew.write(askQuestion ? 1 : 0);
        mplew.write(questionSet);
        mplew.writeShort(questionId);

        return mplew.getPacket();
    }

    public static byte[] showEventInstructions() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.DESC.getValue());
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] showSpecialEffect(int cid, int itemId) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] sendClock(int type) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.CLOCK.getValue());
        oPacket.EncodeByte(type);
        switch (type) {
            case 0: {
                oPacket.EncodeInt(type);
            }
        }
        return oPacket.getPacket();
    }

    public static byte[] getPVPClock(int type, int time) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(3);
        mplew.write(type);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getClock(int time) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(2);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    // OnScoreUpateWitchTower
    public static byte[] setWitchKeyCount(int nCount) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(417);
        mplew.write(nCount);
        return mplew.getPacket();
    }

    public static byte[] getDeathCountClock(int nTime) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(3);
        mplew.write(1);
        mplew.writeInt(nTime);
        return mplew.getPacket();
    }

    public static byte[] getClockTime(int hour, int min, int sec) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(1);
        mplew.write(hour);
        mplew.write(min);
        mplew.write(sec);

        return mplew.getPacket();
    }

    public static byte[] boatPacket(int effect, int mode) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CONTIMOVE.getValue());
        mplew.write(effect);
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] setBoatState(int effect) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CONTISTATE.getValue());
        mplew.write(effect);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] stopClock() {
        return getPacketFromHexString(Integer.toHexString(SendPacketOpcode.DESTROY_CLOCK.getValue()) + " 00");
    }

    public static byte[] showAriantScoreBoard() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.ARIANTARENA_SHOW_RESULT.getValue());

        return mplew.getPacket();
    }

    public static byte[] sendPyramidUpdate(int amount) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PYRAMID_1.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] sendPyramidResult(byte rank, int amount) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PYRAMID_2.getValue());
        mplew.write(rank);
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] quickSlot(String skil) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.QUICKSLOT_KEY.getValue());
        mplew.write(skil == null ? 0 : 1);
        if (skil != null) {
            String[] slots = skil.split(",");
            for (int i = 0; i < 8; i++) {
                mplew.writeInt(Integer.parseInt(slots[i]));
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getMovingPlatforms(MapleMap map) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.FOOTHOLD_INFO.getValue());
        mplew.writeInt(map.getPlatforms().size());
        for (MapleNodes.MaplePlatform mp : map.getPlatforms()) {
            mplew.writeMapleAsciiString(mp.name);
            mplew.writeInt(mp.start);
            mplew.writeInt(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); x++) {
                mplew.writeInt(((Integer) mp.SN.get(x)).intValue());
            }
            mplew.writeInt(mp.speed);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.x2);
            mplew.writeInt(mp.y1);
            mplew.writeInt(mp.y2);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.y1);
            mplew.writeShort(mp.r);
        }

        return mplew.getPacket();
    }

    public static byte[] sendPyramidKills(int amount) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(203);
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] sendPVPMaps() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_INFO.getValue());
        mplew.write(3);
        for (int i = 0; i < 20; i++) {
            mplew.writeInt(10);
        }
        mplew.writeZeroBytes(124);
        mplew.writeShort(150);
        mplew.write(0);

        return mplew.getPacket();
    }

    /* 187 */
    public static byte[] createForceAtom(boolean isByUser, int skillID, int userID, int forceAtomType, int key, int inc, int firstImpact, int secondImpact, int unk) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.CREATE_FORCE_ATOM.getValue());
        oPacket.EncodeByte(isByUser);
        oPacket.EncodeInt(userID);
        oPacket.EncodeInt(forceAtomType);
        if (forceAtomType != 0) {
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(skillID);
        }
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(isByUser ? 1 : Randomizer.rand(100000, 500000)); // key
        oPacket.EncodeInt(inc); // inc
        oPacket.EncodeInt(firstImpact); // high
        oPacket.EncodeInt(secondImpact); // speed
        oPacket.EncodeInt(unk); // angle
        oPacket.EncodeByte(0);
        return oPacket.getPacket();
    }

    public static byte[] getAndroidTalkStyle(int npc, String talk, int... args) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(10);
        mplew.writeMapleAsciiString(talk);
        mplew.write(args.length);

        for (int i = 0; i < args.length; i++) {
            mplew.writeInt(args[i]);
        }
        return mplew.getPacket();
    }

    public static byte[] achievementRatio(int amount) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.ACHIEVEMENT_RATIO.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] setQuickMoveInfo(MapleCharacter chr) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SET_QUICK_MOVE_INFO.getValue());

        oPacket.EncodeByte(8);

        oPacket.EncodeString(chr.getName());
        oPacket.EncodeInt(9000086);
        oPacket.EncodeInt(30);
        oPacket.EncodeInt(0);
        oPacket.EncodeString("#c데미지#를 측정한다.");

        oPacket.EncodeString(chr.getName());
        oPacket.EncodeInt(9000230);
        oPacket.EncodeInt(19);
        oPacket.EncodeInt(0);
        oPacket.EncodeString("구현 준비중입니다.");

        oPacket.EncodeString(chr.getName());
        oPacket.EncodeInt(9000090);
        oPacket.EncodeInt(3);
        oPacket.EncodeInt(0);
        oPacket.EncodeString("구현 준비중입니다.");

        oPacket.EncodeString(chr.getName());
        oPacket.EncodeInt(9000089);
        oPacket.EncodeInt(6);
        oPacket.EncodeInt(0);
        oPacket.EncodeString("각종 #c마을 #및 #c사냥터#를 이동한다.");

        oPacket.EncodeString(chr.getName());
        oPacket.EncodeInt(9010038);
        oPacket.EncodeInt(8);
        oPacket.EncodeInt(0);
        oPacket.EncodeString("각종 #c상점#을 이용한다.");

        oPacket.EncodeString(chr.getName());
        oPacket.EncodeInt(1012117);
        oPacket.EncodeInt(13);
        oPacket.EncodeInt(0);
        oPacket.EncodeString("#c성형 #및 #c헤어#를 변경한다.");

        oPacket.EncodeString(chr.getName());
        oPacket.EncodeInt(9000201);
        oPacket.EncodeInt(15);
        oPacket.EncodeInt(0);
        oPacket.EncodeString("#c몬스터 북#의 효과를 확인 및 적용한다.");

        oPacket.EncodeString(chr.getName());
        oPacket.EncodeInt(3003146);
        oPacket.EncodeInt(27);
        oPacket.EncodeInt(0);
        oPacket.EncodeString("#c아케인 심볼#의 효과를 확인 및 적용한다.");

        return oPacket.getPacket();
        /*
        OutPacket oPacket = new OutPacket();
        int[][] qInfo = {
            {9070004, 999},
            {9071003, 30},
            {9010022, 10},
            {9000087, 10},
            {9000088, 10},
            {9000090, 10},
            {9000089, 10},
            {9000000, 0},};
        oPacket.EncodeShort(SendPacketOpcode.SET_QUICK_MOVE_INFO.getValue());
        oPacket.EncodeByte(qInfo.length);
        for (int i = 0; i < qInfo.length; i++) {
            oPacket.EncodeString(chr.getName());
            oPacket.EncodeInt(qInfo[i][0]);
            oPacket.EncodeInt(i);
            oPacket.EncodeInt(qInfo[i][1]);
            String qMsg = "";
            switch (i) {
                case 0: {
                    qMsg = "#c<배틀 스퀘어>#로 이동한다.";
                    break;
                }
                case 1: {
                    qMsg = "파티원들과 힘을 합쳐 강력한 몬스터들을 공략할 수 있는 #c<몬스터 파크>#로 이동한다.";
                    break;
                }
                case 2: {
                    qMsg = "파티 퀘스트 등 각 종 컨텐츠 맵으로 이동 시켜주는 #c<차원의 거울>#을 이용한다.";
                    break;
                }
                case 3: {
                    qMsg = "다른 유저들과 아이템을 거래 할 수 있는 #c<자유 시장>#으로 이동한다.";
                    break;
                }
                case 4: {
                    qMsg = "전문 기술의 마을 #c<마이스터 빌>#로 이동한다.";
                    break;
                }
                case 5: {
                    qMsg = "현재 위치에서 가장 가까운 #c<대륙 이동 정거장>#으로 이동한다.";
                    break;
                }
                case 6: {
                    qMsg = "인근의 주요 지역으로 캐릭터를 이동시켜 주는 #c<택시>#를 이용한다.";
                    break;
                }
                case 7: {
                    qMsg = "진행 중인 #c<이벤트>#에 참여한다.";
                    break;
                }
            }
            oPacket.EncodeString(qMsg);
        }
        return oPacket.getPacket();
         */
    }

    public static byte[] OnUserEnterField(MapleCharacter chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnUserEnterField.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeMapleAsciiString(chr.getName());
        MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
        if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
            mplew.writeMapleAsciiString(ultExplorer.getCustomData());
        } else {
            mplew.writeMapleAsciiString("");
        }
        if (chr.getGuildId() <= 0) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeInt(0);
                mplew.writeInt(0);
            }
        }
        //mplew.write(0); //왓더밸류잼

        final List<Pair<Integer, Integer>> buffvalue = new ArrayList<>();
        final List<Pair<Integer, Integer>> buffvaluenew = new ArrayList<>();
        final int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        mask[0] |= 0xFE000000;
        mask[1] |= 0x28000;
        //mask[6] |= 0x1000;
        //NOT SURE: SPARK
        if (chr.getBuffedValue(CharacterTemporaryStat.Speed) != null) {//CTS_Speed
            mask[mask.length - 1] |= CharacterTemporaryStat.Speed.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Speed).intValue()), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ComboCounter) != null) {//CTS_ComboCounter
            mask[mask.length - 1] |= CharacterTemporaryStat.ComboCounter.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.ComboCounter).intValue()), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) {//CTS_WeaponCharge
            mask[mask.length - 1] |= CharacterTemporaryStat.WeaponCharge.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.WeaponCharge).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.WeaponCharge)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DragonRoar) != null) {
            mask[mask.length - 1] |= CharacterTemporaryStat.DragonRoar.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.DragonRoar).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.DragonRoar)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Shock) != null) {
            mask[mask.length - CharacterTemporaryStat.Shock.getPosition()] |= CharacterTemporaryStat.Shock.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Shock).intValue()), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Darkness) != null) {
            mask[mask.length - CharacterTemporaryStat.Darkness.getPosition()] |= CharacterTemporaryStat.Darkness.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Darkness).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Darkness)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Seal) != null) {
            mask[mask.length - CharacterTemporaryStat.Seal.getPosition()] |= CharacterTemporaryStat.Seal.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Seal).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Seal)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Weakness) != null) {
            mask[mask.length - CharacterTemporaryStat.Weakness.getPosition()] |= CharacterTemporaryStat.Weakness.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Weakness).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Weakness)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Frozen) != null) {
            mask[mask.length - CharacterTemporaryStat.Frozen.getPosition()] |= CharacterTemporaryStat.Frozen.getValue();
            buffvalue.add(new Pair<>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Frozen)), 2));
            buffvalue.add(new Pair<>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Frozen)), 3));
        }
        //경치저하, 슬로우
        if (chr.getBuffedValue(CharacterTemporaryStat.Unk_0x400000_5) != null) {
            mask[mask.length - CharacterTemporaryStat.Unk_0x400000_5.getPosition()] |= CharacterTemporaryStat.Unk_0x400000_5.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Unk_0x400000_5).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Unk_0x400000_5)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Team) != null) {
            mask[mask.length - CharacterTemporaryStat.Team.getPosition()] |= CharacterTemporaryStat.Team.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Team).intValue()), 1));
        }

        /*
        if (chr.getBuffedValue(CharacterTemporaryStat.TimeBomb) != null) {
            mask[mask.length - CharacterTemporaryStat.TimeBomb.getPosition()] |= CharacterTemporaryStat.TimeBomb.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.TimeBomb).intValue()), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Explosion) != null) {
            mask[mask.length - CharacterTemporaryStat.Explosion.getPosition()] |= CharacterTemporaryStat.Explosion.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Explosion).intValue()), 1));
        }
         */
        if (chr.getBuffedValue(CharacterTemporaryStat.Poison) != null) {
            mask[mask.length - CharacterTemporaryStat.Poison.getPosition()] |= CharacterTemporaryStat.Poison.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Poison).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Poison)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ShadowPartner) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.ShadowPartner.getPosition()] |= CharacterTemporaryStat.ShadowPartner.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.ShadowPartner).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.ShadowPartner)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DarkSight) != null || chr.isHidden()) {
            mask[mask.length - 1] |= CharacterTemporaryStat.DarkSight.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.SoulArrow) != null) {
            mask[mask.length - 1] |= CharacterTemporaryStat.SoulArrow.getValue();
            //buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.SoulArrow)), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Morph) != null) {//CTS_Morph
            mask[mask.length - 2] |= CharacterTemporaryStat.Morph.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getStatForBuff(CharacterTemporaryStat.Morph).getMorph(chr)), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getStatForBuff(CharacterTemporaryStat.Morph).getMorph(chr)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Ghost) != null) {
            mask[mask.length - CharacterTemporaryStat.Ghost.getPosition()] |= CharacterTemporaryStat.Ghost.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Ghost).intValue()), 2));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Attract) != null) {
            mask[mask.length - CharacterTemporaryStat.Attract.getPosition()] |= CharacterTemporaryStat.Attract.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Attract).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Attract)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.NoBulletConsume) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.NoBulletConsume.getPosition()] |= CharacterTemporaryStat.NoBulletConsume.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.NoBulletConsume)), 3));
        }
        /*
        if (chr.getBuffedValue(CharacterTemporaryStat.RespectPImmune) != null) {
            mask[mask.length - CharacterTemporaryStat.RespectPImmune.getPosition()] |= CharacterTemporaryStat.RespectPImmune.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.RespectPImmune)), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.RespectMImmune) != null) {
            mask[mask.length - CharacterTemporaryStat.RespectMImmune.getPosition()] |= CharacterTemporaryStat.RespectMImmune.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.RespectMImmune)), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DefenseAtt) != null) {
            mask[mask.length - CharacterTemporaryStat.DefenseAtt.getPosition()] |= CharacterTemporaryStat.DefenseAtt.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.DefenseAtt)), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DefenseState) != null) {
            mask[mask.length - CharacterTemporaryStat.DefenseState.getPosition()] |= CharacterTemporaryStat.DefenseState.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.DefenseState)), 4));
        }
         */
        if (chr.getBuffedValue(CharacterTemporaryStat.DojangBerserk) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.DojangBerserk.getPosition()] |= CharacterTemporaryStat.DojangBerserk.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.DojangBerserk).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.DojangBerserk)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DojangInvincible) != null) {//테스트해봐야함
            mask[mask.length - 2] |= CharacterTemporaryStat.DojangInvincible.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.WindWalk) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.WindWalk.getPosition()] |= CharacterTemporaryStat.WindWalk.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.WindWalk).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.WindWalk)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.StopMotion) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.StopMotion.getPosition()] |= CharacterTemporaryStat.StopMotion.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.StopMotion).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.StopMotion)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.MagicShield) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.MagicShield.getPosition()] |= CharacterTemporaryStat.MagicShield.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.MagicShield)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Flying) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.Flying.getPosition()] |= CharacterTemporaryStat.Flying.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DrawBack) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.DrawBack.getPosition()] |= CharacterTemporaryStat.DrawBack.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.DrawBack).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.DrawBack)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.FinalCut) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.FinalCut.getPosition()] |= CharacterTemporaryStat.FinalCut.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.FinalCut).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.FinalCut)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Cyclone) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.Cyclone.getPosition()] |= CharacterTemporaryStat.Cyclone.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Cyclone).intValue()), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Mechanic) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.Mechanic.getPosition()] |= CharacterTemporaryStat.Mechanic.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Mechanic).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Mechanic)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DarkAura) != null) {//CTS_DarkAura
            mask[mask.length - CharacterTemporaryStat.DarkAura.getPosition()] |= CharacterTemporaryStat.DarkAura.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.DarkAura).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.DarkAura)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.BlueAura) != null) {//CTS_BlueAura
            mask[mask.length - CharacterTemporaryStat.BlueAura.getPosition()] |= CharacterTemporaryStat.BlueAura.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.BlueAura).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.BlueAura)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.YellowAura) != null) {//CTS_YellowAura
            mask[mask.length - CharacterTemporaryStat.YellowAura.getPosition()] |= CharacterTemporaryStat.YellowAura.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.YellowAura).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.YellowAura)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.BlessingArmor) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.BlessingArmor.getPosition()] |= CharacterTemporaryStat.BlessingArmor.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.GiantPotion) != null) {
            mask[mask.length - CharacterTemporaryStat.GiantPotion.getPosition()] |= CharacterTemporaryStat.GiantPotion.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.GiantPotion).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.GiantPotion)), Integer.valueOf(3)));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.HolyMagicShell) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.HolyMagicShell.getPosition()] |= CharacterTemporaryStat.HolyMagicShell.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DamAbsorbShield) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.DamAbsorbShield.getPosition()] |= CharacterTemporaryStat.DamAbsorbShield.getValue();

        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DevilishPower) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.DevilishPower.getPosition()] |= CharacterTemporaryStat.DevilishPower.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.DevilishPower).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.DevilishPower)), 3));
        }
        /*
        if (chr.getBuffedValue(CharacterTemporaryStat.SpiritInfusion) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.SpiritInfusion.getPosition()] |= CharacterTemporaryStat.SpiritInfusion.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.SpiritInfusion).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.SpiritInfusion)), 3));
        }
         */
        if (chr.getBuffedValue(CharacterTemporaryStat.SaintSaver) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.SaintSaver.getPosition()] |= CharacterTemporaryStat.SaintSaver.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.SaintSaver).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.SaintSaver)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.VenomSnake) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.VenomSnake.getPosition()] |= CharacterTemporaryStat.VenomSnake.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.VenomSnake).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.VenomSnake)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Invisible) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.Invisible.getPosition()] |= CharacterTemporaryStat.Invisible.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Invisible).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.Invisible)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.HiddenPieceOn) != null) {
            mask[mask.length - CharacterTemporaryStat.HiddenPieceOn.getPosition()] |= CharacterTemporaryStat.HiddenPieceOn.getValue();
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.HiddenPieceOn).intValue()), 2));
            buffvalue.add(new Pair<Integer, Integer>(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.HiddenPieceOn)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.RepeatEffect) != null) {//CTS_ShadowPartner
            mask[mask.length - CharacterTemporaryStat.RepeatEffect.getPosition()] |= CharacterTemporaryStat.RepeatEffect.getValue();
            buffvalue.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.RepeatEffect), 2));
            buffvalue.add(new Pair<>(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.RepeatEffect)), 3));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Unk_0x100000_8) != null) {
            mask[mask.length - CharacterTemporaryStat.Unk_0x100000_8.getPosition()] |= CharacterTemporaryStat.Unk_0x100000_8.getValue();
            buffvaluenew.add(new Pair(chr.getBuffedValue(CharacterTemporaryStat.Unk_0x100000_8), Integer.valueOf(2)));
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Unk_0x100000_8)), Integer.valueOf(4)));
            buffvaluenew.add(new Pair(Integer.valueOf(5), Integer.valueOf(0)));
            MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
            Item weapon = equip.getItem((byte) -11);
            if (chr.getBuffSource(CharacterTemporaryStat.Unk_0x100000_8) != 61101002 && chr.getBuffSource(CharacterTemporaryStat.Unk_0x100000_8) != 61110211) {
                buffvaluenew.add(new Pair(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Unk_0x100000_8) == 61121217 ? 4 : 2), Integer.valueOf(4)));
                buffvaluenew.add(new Pair(Integer.valueOf(5), Integer.valueOf(4)));
                buffvaluenew.add(new Pair(Integer.valueOf(weapon.getItemId()), Integer.valueOf(4)));
                buffvaluenew.add(new Pair(Integer.valueOf(5), Integer.valueOf(4)));
            } else {
                buffvaluenew.add(new Pair(Integer.valueOf(chr.getBuffSource(CharacterTemporaryStat.Unk_0x100000_8) == 61110211 ? 3 : 1), Integer.valueOf(4)));
                buffvaluenew.add(new Pair(Integer.valueOf(3), Integer.valueOf(4)));
                buffvaluenew.add(new Pair(Integer.valueOf(weapon.getItemId()), Integer.valueOf(4)));
                buffvaluenew.add(new Pair(Integer.valueOf(3), Integer.valueOf(4)));
            }
            if (chr.getTrueBuffSource(CharacterTemporaryStat.Unk_0x100000_8) != 61101002) {
                buffvaluenew.add(new Pair(Integer.valueOf(8), Integer.valueOf(0)));
            }
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Unk_0x400000_8) != null) {//수정해야함 되있네.
            mask[mask.length - CharacterTemporaryStat.Unk_0x400000_8.getPosition()] |= CharacterTemporaryStat.Unk_0x400000_8.getValue();
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getBuffedValue(CharacterTemporaryStat.Unk_0x400000_8)), Integer.valueOf(2)));
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(CharacterTemporaryStat.Unk_0x400000_8)), Integer.valueOf(4)));
            buffvaluenew.add(new Pair(Integer.valueOf(9), Integer.valueOf(0)));
        }

        // 토네이도 추가시켜야됨
        /*
        mask[mask.length - MapleBuffStat.LUMINOUS_GAGE.getPosition()] |= MapleBuffStat.LUMINOUS_GAGE.getValue();
        buffvaluenew.add(new Pair(Integer.valueOf(1), Integer.valueOf(0)));        
        buffvaluenew.add(new Pair(Integer.valueOf(1), Integer.valueOf(2)));
        buffvaluenew.add(new Pair(Integer.valueOf(20040217), Integer.valueOf(4)));
        buffvaluenew.add(new Pair(Integer.valueOf(30), Integer.valueOf(0)));   
         */
        //---------------------------------------------------------------
        for (int i = 0; i < mask.length; i++) {
            mplew.writeInt(mask[i]);
        }
        for (Pair<Integer, Integer> i : buffvalue) {
            if (i.right == 3) {
                mplew.writeInt(i.left);
            } else if (i.right == 2) {
                mplew.writeShort(i.left.shortValue());
            } else if (i.right == 1) {
                mplew.write(i.left.byteValue());
            }
        }
        mplew.writeInt(-1);
        if (buffvaluenew.isEmpty()) {
            mplew.writeZeroBytes(10);
        } else {
            //mplew.write(0);
            for (Pair<Integer, Integer> i : buffvaluenew) {
                if ((i.right) == 4) {
                    mplew.writeInt((i.left));
                } else if ((i.right) == 2) {
                    mplew.writeShort((i.left).shortValue());
                } else if ((i.right) == 1) {
                    mplew.write((i.left).byteValue());
                } else if ((i.right) == 0) {
                    mplew.writeZeroBytes((i.left));
                }
            }
        }
        if (buffvaluenew.isEmpty()) {
            mplew.writeShort(0);
        } else {
            mplew.writeZeroBytes(12);
        }
        final int CHAR_MAGIC_SPAWN = Randomizer.nextInt();
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0); //start of dash_speed
        mplew.writeLong(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0); //start of dash_jump
        mplew.writeLong(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);
        int buffSrc = chr.getBuffSource(CharacterTemporaryStat.MonsterRiding);
        if (buffSrc > 0) {
            final Item c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118);
            final Item mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
            if (GameConstants.getMountItem(buffSrc, chr) == 0 && c_mount != null && chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -119) != null) {
                mplew.writeInt(c_mount.getItemId());
            } else if (GameConstants.getMountItem(buffSrc, chr) == 0 && mount != null && chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -19) != null) {
                mplew.writeInt(mount.getItemId());
            } else {
                mplew.writeInt(GameConstants.getMountItem(buffSrc, chr));
            }
            mplew.writeInt(buffSrc);
        } else {
            mplew.writeLong(0);
        }
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeLong(0); //speed infusion behaves differently here
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeInt(1);
        mplew.writeLong(0); //homing beacon
        mplew.write(0); //random
        mplew.writeShort(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeZeroBytes(16); //and finally, something ive no idea
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);
        //////////////////
        ///////////////////////
        mplew.writeShort(chr.getJob());
        mplew.writeShort(0);
        PacketHelper.addCharLook(mplew, chr, true);

        mplew.writeInt(0);
        mplew.writeInt(0);

        int v98 = Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000));
        mplew.writeInt(v98);
        int v99 = chr.getItemEffect(); // 캐쉬 아이템
        mplew.writeInt(v99);
        int v95 = chr.getItemEffect(); // 블링 블링 몽키
        mplew.writeInt(v95);
        int v91 = chr.currentTitle(); // 칭호
        mplew.writeInt(v91);

        mplew.writeInt(0); // getDemonSlayerWings()
        mplew.writeInt(0); // getSetItemEffect()
        mplew.writeInt(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        mplew.writePos(chr.getTruePosition());
        mplew.write(chr.getStance());
        mplew.writeShort(0); // getFH()

        mplew.write(0); // Pet

        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());

        PacketHelper.addAnnounceBox(mplew, chr);

        mplew.write((chr.getChalkboard() != null) && (chr.getChalkboard().length() > 0) ? 1 : 0);
        if ((chr.getChalkboard() != null) && (chr.getChalkboard().length() > 0)) {
            mplew.writeMapleAsciiString(chr.getChalkboard());
        }

        Triple rings = chr.getRings(false);
        addRingInfo(mplew, (List) rings.getLeft());
        addRingInfo(mplew, (List) rings.getMid());
        addMRingInfo(mplew, (List) rings.getRight(), chr);

        mplew.write(chr.getStat().Berserk ? 1 : 0);
        mplew.writeInt(0);

        boolean pvp = chr.inPVP();
        if (pvp) {
            mplew.write(Integer.parseInt(chr.getEventInstance().getProperty("type")));
        }
        if (MapConstants.isDeathCountMap(chr.getMapId()) != -1) {
            mplew.write(-1);
        }
        if (chr.getCarnivalParty() != null) {
            mplew.write(chr.getCarnivalParty().getTeam());
        } else if (GameConstants.isTeamMap(chr.getMapId())) {
            mplew.write(chr.getTeam() + (pvp ? 1 : 0));
        }
        return mplew.getPacket();

    }

    public static byte[] removePlayerFromMap(int cid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_LEAVE_FIELD.getValue());
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] getChatText(int cidfrom, String text, boolean whiteBG, int show) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CHAT.getValue());
        mplew.writeInt(cidfrom); // CRC
        mplew.writeInt(whiteBG ? 1 : 0);
        mplew.writeMapleAsciiString(text);
        mplew.write(show);

        return mplew.getPacket();
    }

    public static byte[] getScrollEffect(int chr, Equip.ScrollResult scrollSuccess, boolean legendarySpirit, boolean whiteScroll, int item_id, int scroll_id) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_UPGRADE_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.write(scrollSuccess == Equip.ScrollResult.SUCCESS ? 1 : scrollSuccess == Equip.ScrollResult.CURSE ? 2 : 0);
        mplew.write(legendarySpirit ? 1 : 0);
        mplew.writeInt(scroll_id);
        mplew.writeInt(item_id);
        return mplew.getPacket();
    }

    public static byte[] showMagnifyingEffect(int chr, short pos) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SHOW_MAGNIFYING_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.writeShort(pos);
        //mplew.write(0); 160에선사용안하는거같
        return mplew.getPacket();
    }

    public static byte[] showPotentialReset(int chr, boolean success, int itemid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SHOW_POTENTIAL_RESET.getValue()); //SHOW_FIREWORKS_EFFECT crashes, incorrect opcode.
        mplew.writeInt(chr);
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showBattleAttack(MapleCharacter user, int skillID, int direction, int stance, int attackSpeed, int mastery, List<AttackPair> attack, int attackCount) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.PVP_ATTACK.getValue());
        oPacket.EncodeShort(user.getId());
        oPacket.EncodeByte(user.getLevel());
        oPacket.EncodeInt(skillID);
        oPacket.EncodeByte(user.getTotalSkillLevel(skillID));
        oPacket.EncodeInt(0);       // linkSkill != skill ? linkSkill : 0
        oPacket.EncodeByte(0);      // linkSkillLevel != skillLevel ? linkSkillLevel : 0
        oPacket.EncodeByte(direction);
        oPacket.EncodeByte(0);      // movementSkill ? 1 : 0
        oPacket.EncodeByte(0);      // pushTarget ? 1 : 0
        oPacket.EncodeByte(0);      // pullTarget ? 1 : 0
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(stance);
        oPacket.EncodeByte(attackSpeed);
        oPacket.EncodeByte(mastery);
        oPacket.EncodeInt(0);       // projectile
        oPacket.EncodeInt(0);       // chargeTime
        oPacket.EncodeInt(0);       // range
        oPacket.EncodeByte(attack.size());
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(attackCount);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        for (AttackPair attackPair : attack) {
            oPacket.EncodeInt(attackPair.objectid);
            oPacket.EncodeInt(0x07);
            oPacket.EncodeShort(user.getTruePosition().x);
            oPacket.EncodeShort(user.getTruePosition().y);
            for (Pair pair : attackPair.attack) {
                oPacket.EncodeInt(((Integer) pair.left).intValue());
                oPacket.EncodeByte(0);
            }
        }
        return oPacket.getPacket();
    }

    public static byte[] pvpAttack(int cid, int playerLevel, int skill, int skillLevel, int speed, int mastery, int projectile, int attackCount, int chargeTime, int stance, int direction, int range, int linkSkill, int linkSkillLevel, boolean movementSkill, boolean pushTarget, boolean pullTarget, List<AttackPair> attack) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(playerLevel);
        mplew.writeInt(skill);
        mplew.write(skillLevel);

        mplew.writeInt(linkSkill != skill ? linkSkill : 0);
        mplew.write(linkSkillLevel != skillLevel ? linkSkillLevel : 0);
        mplew.write(direction);
        mplew.write(movementSkill ? 1 : 0);
        mplew.write(pushTarget ? 1 : 0);
        mplew.write(pullTarget ? 1 : 0);
        mplew.write(0);
        mplew.writeShort(stance);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(projectile);
        mplew.writeInt(chargeTime);
        mplew.writeInt(range);
        mplew.write(attack.size());
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(attackCount);
        mplew.write(0);
        mplew.write(0);
        for (AttackPair p : attack) {
            mplew.writeInt(p.objectid);
            mplew.writeInt(0);
            mplew.writePos(p.point);
            mplew.write(0);
            mplew.writeInt(0);
            for (Pair atk : p.attack) {
                mplew.writeInt(((Integer) atk.left).intValue());
                mplew.writeInt(0);
                mplew.write(((Boolean) atk.right).booleanValue() ? 1 : 0);
                mplew.write(0);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPMist(int cid, int mistSkill, int mistLevel, int damage) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_MIST.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(mistSkill);
        mplew.write(mistLevel);
        mplew.writeInt(damage);
        mplew.write(8);
        mplew.writeInt(1000);

        return mplew.getPacket();
    }

    public static byte[] pvpCool(int cid, List<Integer> attack) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_COOL.getValue());
        mplew.writeInt(cid);
        mplew.write(attack.size());
        for (Iterator i$ = attack.iterator(); i$.hasNext();) {
            int b = ((Integer) i$.next()).intValue();
            mplew.writeInt(b);
        }

        return mplew.getPacket();
    }

    public static byte[] OnDamageByUser(int characterID, int attackCount, int userDamage) {
        OutPacket mplew = new OutPacket();
        mplew.EncodeShort(206);
        mplew.EncodeInt(characterID);
        mplew.EncodeByte(attackCount);
        for (int i = 0; i < attackCount; i++) {
            mplew.EncodeInt(userDamage);
        }
        return mplew.getPacket();
    }

    public static byte[] OnDamageByUser(int characterID, int attackCount, List<AttackPair> userDamage) {
        OutPacket mplew = new OutPacket();
        mplew.EncodeShort(206);
        mplew.EncodeInt(characterID);
        mplew.EncodeByte(attackCount);
        for (AttackPair aPair : userDamage) {
            if (aPair.attack != null) {
                for (Pair<Integer, Boolean> eachd : aPair.attack) {
                    mplew.EncodeInt(eachd.left);
                }
            }
        }
        return mplew.getPacket();
    }

    public static byte[] OnHitByUser(int characterID, int hitDamage) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(214);
        mplew.writeInt(characterID);
        mplew.writeInt(hitDamage);
        return mplew.getPacket();
    }

    public static byte[] teslaTriangle(int cid, int sum1, int sum2, int sum3) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.TESLA_TRIANGLE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(sum1);
        mplew.writeInt(sum2);
        mplew.writeInt(sum3);

        return mplew.getPacket();
    }

    public static byte[] followEffect(int initiator, int replier, Point toMap) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.FOLLOW_CHARACTER.getValue());
        mplew.writeInt(initiator);
        mplew.writeInt(replier);
        if (replier == 0) {
            mplew.write(toMap == null ? 0 : 1);
            if (toMap != null) {
                mplew.writeInt(toMap.x);
                mplew.writeInt(toMap.y);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] showPQReward(int cid) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.SHOW_PQ_REWARD.getValue());
        mplew.writeInt(cid);
        for (int i = 0; i < 6; i++) {
            mplew.write(i);
        }
        return mplew.getPacket();
    }

    public static byte[] craftMake(int cid, int something, int time) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CRAFT_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(something);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] craftFinished(int cid, int craftID, int ranking, int itemId, int quantity, int exp) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CRAFT_COMPLETE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(craftID);
        mplew.writeInt(ranking);
        mplew.writeInt(itemId);
        mplew.writeInt(quantity);
        mplew.writeInt(exp);

        return mplew.getPacket();
    }

    public static byte[] harvestResult(int cid, boolean success) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.HARVESTED.getValue());
        mplew.writeInt(cid);
        mplew.write(success ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] playerDamaged(int cid, int dmg) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.HIT_BY_USER.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(dmg);

        return mplew.getPacket();
    }

    public static byte[] showPyramidEffect(int chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.NETT_PYRAMID.getValue());
        mplew.writeInt(chr);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] spawnDragon(MapleDragon d) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.DRAGON_CREATED.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writeInt(d.getPosition().x);
        mplew.writeInt(d.getPosition().y);
        mplew.write(d.getStance());
        mplew.writeShort(0);
        mplew.writeShort(d.getJobId());

        return mplew.getPacket();
    }

    public static byte[] removeDragon(int chrid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.DRAGON_DELETED.getValue());
        mplew.writeInt(chrid);

        return mplew.getPacket();
    }

    public static byte[] moveDragon(MapleDragon d, Point startPos, List<LifeMovementFragment> moves) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.DRAGON_MOVE.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] spawnAndroid(MapleCharacter cid, MapleAndroid android) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.ANDROID_SPAWN.getValue());
        mplew.writeInt(cid.getId());
        mplew.write(android.getItemId() == 1662198 ? 79 : android.getItemId() == 1662006 ? 5 : android.getItemId() - 1661999);
        mplew.writePos(android.getPos());
        mplew.write(android.getStance());
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.writeShort(android.getHair() - 30000);
        mplew.writeShort(android.getFace() - 20000);
        mplew.writeMapleAsciiString(android.getName());
        for (short i = -1200; i > -1207; i = (short) (i - 1)) {
            Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
            mplew.writeInt(item != null ? item.getItemId() : 0);
        }

        return mplew.getPacket();
    }

    public static byte[] moveAndroid(int cid, Point pos, List<LifeMovementFragment> res) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.ANDROID_MOVE.getValue());
        mplew.writeInt(cid);
        mplew.writePos(pos);
        mplew.writeInt(Integer.MAX_VALUE); //time left in milliseconds? this appears to go down...slowly 1377440900
        PacketHelper.serializeMovementList(mplew, res);
        return mplew.getPacket();
    }

    public static byte[] showAndroidEmotion(int cid, int animation) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.ANDROID_ACTION_SET.getValue());
        mplew.writeInt(cid);
        mplew.write(0);
        mplew.write(animation);

        return mplew.getPacket();
    }

    public static byte[] updateAndroidLook(boolean itemOnly, MapleCharacter cid, MapleAndroid android) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.ANDROID_UPDATE.getValue());
        mplew.writeInt(cid.getId());
        mplew.write(itemOnly ? 1 : 0);
        if (itemOnly) {
            for (short i = -1200; i > -1207; i = (short) (i - 1)) {
                Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
                mplew.writeInt(item != null ? item.getItemId() : 0);
            }
        } else {
            mplew.writeShort(0);
            mplew.writeShort(android.getHair() - 30000);
            mplew.writeShort(android.getFace() - 20000);
            mplew.writeMapleAsciiString(android.getName());
        }

        return mplew.getPacket();
    }

    public static byte[] deactivateAndroid(int cid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.ANDROID_DEACTIVATED.getValue());
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] addStolenSkill(int jobNum, int index, int skill, int level) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnChangeStealMemoryResult.getValue());
        mplew.write(1);
        mplew.write(0);
        mplew.writeInt(jobNum);
        mplew.writeInt(index);
        mplew.writeInt(skill);
        mplew.writeInt(level);
        mplew.writeInt(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] removeStolenSkill(int jobNum, int index) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnChangeStealMemoryResult.getValue());
        mplew.write(1);
        mplew.write(3);
        mplew.writeInt(jobNum);
        mplew.writeInt(index);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] movePlayer(int cid, List<LifeMovementFragment> moves, Point startPos) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_MOVE.getValue());
        mplew.writeInt(cid);
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] closeRangeAttack(int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackPair> damage, boolean energy, int lvl, byte mastery, byte attackType, byte unk, Point pos, int charge, int ultLevel) {
        return addAttackInfo(energy ? 4 : 0, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, attackType, unk, 0, pos, ultLevel);
    }

    public static byte[] rangedAttack(int cid, byte tbyte, int skill, int level, int display, byte speed, int itemid, List<AttackPair> damage, Point pos, int lvl, byte mastery, byte attackType, byte unk, int ultLevel) {
        return addAttackInfo(1, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, attackType, unk, itemid, pos, ultLevel);
    }

    public static byte[] strafeAttack(int cid, byte tbyte, int skill, int level, int display, byte speed, int itemid, List<AttackPair> damage, Point pos, int lvl, byte mastery, byte attackType, byte unk, int ultLevel) {
        return addAttackInfo(2, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, attackType, unk, itemid, pos, ultLevel);
    }

    public static byte[] magicAttack(int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackPair> damage, int charge, int lvl, byte attackType, byte unk, int ultLevel) {
        return addAttackInfo(3, cid, tbyte, skill, level, display, speed, damage, lvl, (byte) 0, attackType, unk, charge, null, ultLevel);
    }

    public static byte[] OnAttack(int aType, MapleCharacter user, List<AttackPair> aDamage, int SkillID, int tByte, int unk, int uDisplay, int aSpeed, int aCharge, Point uPosition) {
        final OutPacket oPacket = new OutPacket();
        switch (aType) {
            case 0: {
                oPacket.EncodeShort(SendPacketOpcode.USER_MELEEATTACK.getValue());
                break;
            }
            case 1:
            case 2: {
                oPacket.EncodeShort(SendPacketOpcode.USER_SHOOTATTACK.getValue());
                break;
            }
            case 3: {
                oPacket.EncodeShort(SendPacketOpcode.USER_MAGICATTACK.getValue());
                break;
            }
            default: {
                oPacket.EncodeShort(SendPacketOpcode.USER_BODYATTACK.getValue());
                break;
            }
        }
        oPacket.EncodeInt(user.getId());
        oPacket.EncodeByte(tByte);
        oPacket.EncodeByte(user.getLevel());
        if (SkillID > 0 || aType == 3) {
            oPacket.EncodeByte(user.getTotalSkillLevel(SkillID));
            oPacket.EncodeInt(SkillID);
        } else {
            oPacket.EncodeByte(0);
        }
        if (aType == 2) {
            int strafeAttack = user.getTotalSkillLevel(GameConstants.getBonusAttackCount(SkillID));
            oPacket.EncodeByte(strafeAttack);
            if (strafeAttack > 0) {
                oPacket.EncodeInt(3220010);
            }
        }
        oPacket.EncodeByte(unk);
        oPacket.EncodeShort(uDisplay);
        oPacket.EncodeByte(aSpeed);
        oPacket.EncodeByte(user.getStat().passive_mastery());
        oPacket.EncodeInt(aCharge);
        for (AttackPair aPair : aDamage) {
            if (aPair.attack != null) {
                oPacket.EncodeInt(aPair.objectid);
                oPacket.EncodeByte(0x07);
                if (SkillID == 4211006) {
                    oPacket.EncodeByte(aPair.attack.size());
                }
                for (Pair<Integer, Boolean> eachd : aPair.attack) {
                    if (eachd.right) {
                        oPacket.EncodeInt(eachd.left | 0x80000000);
                    } else {
                        oPacket.EncodeInt(eachd.left);
                    }
                }
            }
        }
        if (aType == 1 || aType == 2) {
            oPacket.EncodeShort(uPosition.x);
            oPacket.EncodeShort(uPosition.y);
        }
        if (SkillID == 2121001 || SkillID == 2221001 || SkillID == 2321001) {
            oPacket.EncodeInt(aCharge);
        }
        if (SkillID == 33101007) {
            oPacket.EncodeInt(0);
        }
        return oPacket.getPacket();
    }

    public static byte[] addAttackInfo(final int type, final int cid, final int tbyte, final int skill, final int level, final int display, final byte speed, final List<AttackPair> damage, final int lvl, final byte mastery, byte attackType, final byte unk, final int charge, final Point pos, final int ultLevel) {
        final OutPacket mplew = new OutPacket();
        if (type == 0) {
            mplew.writeShort(SendPacketOpcode.USER_MELEEATTACK.getValue());
        } else if (type == 1 || type == 2) {
            mplew.writeShort(SendPacketOpcode.USER_SHOOTATTACK.getValue());
        } else if (type == 3) {
            mplew.writeShort(SendPacketOpcode.USER_MAGICATTACK.getValue());
        } else {
            mplew.writeShort(SendPacketOpcode.USER_BODYATTACK.getValue());
        }
        mplew.writeInt(cid);
        mplew.write(tbyte);
        mplew.write(lvl);
        if (skill > 0 || type == 3) {
            mplew.write(level);
            mplew.writeInt(skill);
        } else {
            mplew.write(0);
        }
        if (type == 2) {
            mplew.write(ultLevel);
            if (ultLevel > 0) {
                mplew.writeInt(3220010);
            }
        }
        mplew.write(unk);
        mplew.writeShort(display);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(charge);
        for (AttackPair oned : damage) {
            if (oned.attack != null) {
                mplew.writeInt(oned.objectid);
                mplew.write(0x07);
                if (skill == 4211006) {
                    mplew.write(oned.attack.size());
                }
                for (Pair<Integer, Boolean> eachd : oned.attack) {
                    if (eachd.right) {
                        mplew.writeInt(eachd.left | 0x80000000);
                    } else {
                        mplew.writeInt(eachd.left);
                    }
                }
            }
        }
        if (type == 1 || type == 2) {
            mplew.writePos(pos);
        } else if (type == 3 && charge > 0) {
            mplew.writeInt(charge);
        }
        return mplew.getPacket();
    }

    public static byte[] OnAttack(final int type, final int cid, final int tbyte, final int skill, final int level, final int display, final byte speed, final List<AttackPair> damage, final int lvl, final byte mastery, byte attackType, final byte unk, final int charge, final Point pos, final int ultLevel) {
        final OutPacket mplew = new OutPacket();
        //if (type == 0) {
        mplew.writeShort(205);
        mplew.writeInt(cid);
        mplew.write(tbyte);
        mplew.write(lvl);
        if (skill > 0 || type == 3) {
            mplew.write(level);
            mplew.writeInt(skill);
        } else {
            mplew.write(0);
        }
        if (type == 2) {
            mplew.write(ultLevel);
            if (ultLevel > 0) {
                mplew.writeInt(3220010);
            }
        }
        mplew.write(unk);
        mplew.writeShort(display);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(charge);
        for (AttackPair oned : damage) {
            if (oned.attack != null) {
                mplew.writeInt(oned.objectid);
                mplew.write(0x07);
                if (skill == 4211006 || skill == 62111002) {
                    mplew.write(oned.attack.size());
                }
                for (Pair<Integer, Boolean> eachd : oned.attack) {
                    if (eachd.right) {
                        mplew.writeInt(eachd.left | 0x80000000);
                    } else {
                        mplew.writeInt(eachd.left);
                    }
                }
            }
        }
        if (type == 1 || type == 2) {
            mplew.writePos(pos);
        } else if (type == 3 && charge > 0) {
            mplew.writeInt(charge);
        }
        return mplew.getPacket();
    }

    public static byte[] OnSkillPrepare(MapleCharacter from, int skillId, byte level, short display, byte unk) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_SKILL_PREPARE.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        mplew.write(level);
        mplew.writeShort(display);
        mplew.write(unk);
        return mplew.getPacket();
    }

    public static byte[] skillCancel(MapleCharacter from, int skillId) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_SKILL_CANCEL.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);

        return mplew.getPacket();
    }

    public static byte[] damagePlayer(int cid, int type, int damage, int monsteridfrom, byte direction, int skillid, int pDMG, boolean pPhysical, int pID, byte pType, Point pPos, byte offset, int offset_d, int fake) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_HIT.getValue());
        mplew.writeInt(cid);
        mplew.write(type);
        mplew.writeInt(damage);
        mplew.write(0);
        if (type >= -1) {
            mplew.writeInt(monsteridfrom);
            mplew.write(direction);
            mplew.writeInt(skillid);
            mplew.writeInt(pDMG);
            mplew.write(0);
            if (pDMG > 0) {
                mplew.write(pPhysical ? 1 : 0);
                mplew.writeInt(pID);
                mplew.write(pType);
                mplew.writePos(pPos);
            }
            mplew.write(offset);
            if (offset == 1) {
                mplew.writeInt(offset_d);
            }
        }
        mplew.writeInt(damage);
        if ((damage <= 0) || (fake > 0)) {
            mplew.writeInt(fake);
        }

        return mplew.getPacket();
    }

    public static byte[] itemEffect(int characterid, int itemid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_ACTIVE_EFFECT_ITEM.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showTitle(int characterid, int itemid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SHOW_TITLE.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showChair(int characterid, int itemid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_SIT.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] updateCharLook(MapleCharacter chr) {
        return updateCharLook(chr, chr.isDressUp());
    }

    public static byte[] updateCharLook(MapleCharacter chr, boolean isAngel) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_AVATAR_MODIFIED.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        PacketHelper.addCharLook(mplew, chr, false, isAngel);
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(mplew, rings.getLeft());
        addRingInfo(mplew, rings.getMid());
        addMRingInfo(mplew, rings.getRight(), chr);
        mplew.writeInt(0); // -> charid to follow (4)
        return mplew.getPacket();
    }

    public static byte[] updatePartyMemberHP(int cid, int curhp, int maxhp) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_RECEIVE_HP.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(curhp);
        mplew.writeInt(maxhp);

        return mplew.getPacket();
    }

    public static byte[] loadGuildName(MapleCharacter chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_GUILDNAME_CHANGED.getValue());
        mplew.writeInt(chr.getId());
        if (chr.getGuildId() <= 0) {
            mplew.writeShort(0);
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
            } else {
                mplew.writeShort(0);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] loadGuildIcon(MapleCharacter chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_GUILDMARK_CHANGED.getValue());
        mplew.writeInt(chr.getId());
        if (chr.getGuildId() <= 0) {
            mplew.writeZeroBytes(6);
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeZeroBytes(6);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] changeTeam(int cid, int type) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.LOAD_TEAM.getValue());
        mplew.writeInt(cid);
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] showHarvesting(MapleClient c, int tool) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SHOW_HARVEST.getValue());
        mplew.writeInt(c.getPlayer().getId());
        mplew.writeInt(1); // 160 이거 바이트인거같은데 팅기면 바이트로 변경
        mplew.writeInt(tool);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getPVPHPBar(int cid, int hp, int maxHp) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SHOW_PVP_HP.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(hp);
        mplew.writeInt(maxHp);

        return mplew.getPacket();
    }

    public static byte[] getFollowMessage(String msg) {
        return getGameMessage(msg, true);
    }

    public static byte[] getGameMessage(String msg, boolean white) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.GAME_MESSAGE.getValue());
        mplew.writeShort(white ? 11 : 6);
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] moveFollow(Point otherStart, Point myStart, Point otherEnd, List<LifeMovementFragment> moves) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PASSIVE_MOVE.getValue());
        mplew.writePos(otherStart);
        mplew.writePos(myStart);
        PacketHelper.serializeMovementList(mplew, moves);
        mplew.write(17);
        for (int i = 0; i < 8; i++) {
            mplew.write(0);
        }
        mplew.write(0);
        mplew.writePos(otherEnd);
        mplew.writePos(otherStart);

        return mplew.getPacket();
    }

    public static byte[] getPVPIceGage(int score) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PVP_ICEGAGE.getValue());
        mplew.writeInt(score);

        return mplew.getPacket();
    }

    public static byte[] dropItemFromMapObject(MapleMapItem drop, Point dropfrom, Point dropto, byte mod) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.DROP_ENTER_FIELD.getValue());
        mplew.write(mod);
        mplew.writeInt(drop.getObjectId());
        mplew.write(drop.getMeso() > 0 ? 1 : 0);
        mplew.writeInt(drop.getItemId());
        mplew.writeInt(drop.getOwner());
        mplew.write(drop.getDropType());
        mplew.writeInt(dropto.x); // CRC
        mplew.writeInt(dropto.y); // CRC
        mplew.writeInt(0);
        if (mod == 0 || mod == 1 || mod == 3) {
            mplew.writePos(dropfrom);
            mplew.writeShort(0);
        }
        //mplew.write(drop.getDropType() == 3 ? 1 : 0);//true/false 1로설정했을때 아이템존나높게날라감ㅋㅋㅋ        
        if (drop.getMeso() == 0) {
            //mplew.write(0);
            PacketHelper.addExpirationTime(mplew, drop.getItem().getExpiration());
        }
        mplew.write(drop.isPlayerDrop() ? 0 : 1);
        mplew.write(0);
        mplew.writeShort(0);
        //mplew.write(0); //?
        return mplew.getPacket();
    }

    public static byte[] explodeDrop(int oid) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.DROP_LEAVE_FIELD.getValue());
        mplew.write(4);
        mplew.writeInt(oid);
        mplew.writeShort(655);
        return mplew.getPacket();
    }

    public static byte[] removeItemFromMap(int oid, int animation, int cid) {
        return removeItemFromMap(oid, animation, cid, 0);
    }

    public static byte[] removeItemFromMap(int oid, int animation, int cid, int slot) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.DROP_LEAVE_FIELD.getValue());
        mplew.write(animation);
        mplew.writeInt(oid);
        if (animation >= 2) {
            mplew.writeInt(cid);
            if (animation == 5) {
                mplew.writeInt(slot);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] TimeCapsule() {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.TIME_CAPSULE.getValue());
        return mplew.getPacket();
    }

    public static byte[] spawnMist(MapleMist mist) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.AFFECTED_AREA_CREATED.getValue());
        mplew.writeInt(mist.getObjectId());
        mplew.write(mist.isItemMist() ? 2 : 0);//0 : 유저미스트, 1 : 몹미스트
        mplew.writeInt(mist.getOwnerId());//오너아이디가맞음
        if (mist.isItemMist()) {
            mplew.writeInt(-mist.getItemid());
        } else if (!mist.isMobMist()) {//5281003
            mplew.writeInt(mist.getSourceSkill().getId());
        } else {
            mplew.writeInt(mist.getMobSkill().getSkillId());
        }
        mplew.write(mist.getSkillLevel());
        mplew.writeShort(mist.getSkillDelay());
        mplew.writeRect(mist.getBox());// 160 여기밑에 바이ㅣ트한개필요한디.
        mplew.write(mist.isMobMist() ? 4 : (mist.isPoisonMist() == 2) ? 2 : 0);
        mplew.writeInt(mist.isMobMist() ? 4 : (mist.isPoisonMist() == 2) ? 2 : 0);

        if (!mist.isMobMist() && !mist.isItemMist()) {
            if (mist.getSourceSkill().getId() != 35121052 && mist.getSourceSkill().getId() != 61121105) {
                mplew.writeShort(mist.getPosition().x + 200);
                mplew.writeShort(mist.getPosition().y + 150);
            } else if (mist.getSourceSkill().getId() == 61121105) {
                mplew.writeShort(mist.getPosition().x);
                mplew.writeShort(mist.getPosition().y);
            } else {
                mplew.writeShort(mist.getPosition().x - 100);
                mplew.writeShort(mist.getPosition().y + 250);
            }
        } else {
            mplew.writePos(mist.getPosition());
        }
        return mplew.getPacket();
    }

    public static byte[] removeMist(int oid, boolean eruption) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.AFFECTED_AREA_REMOVED.getValue());
        mplew.writeInt(oid);
        mplew.write(eruption ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] spawnDoor(int oid, Point pos, boolean animation) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.TOWN_PORTAL_CREATED.getValue());
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] removeDoor(int oid, boolean animation) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.TOWN_PORTAL_REMOVED.getValue());
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] spawnMechDoor(MechDoor md, boolean animated) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OPEN_GATE_CREATED.getValue());
        mplew.write(animated ? 0 : 1);
        mplew.writeInt(md.getOwnerId());
        mplew.writePos(md.getTruePosition());
        mplew.write(md.getId());
        mplew.writeInt(md.getPartyId());
        return mplew.getPacket();
    }

    public static byte[] removeMechDoor(MechDoor md, boolean animated) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OPEN_GATE_REMOVED.getValue());
        mplew.write(animated ? 0 : 1);
        mplew.writeInt(md.getOwnerId());
        mplew.write(md.getId());

        return mplew.getPacket();
    }

    public static byte[] triggerReactor(MapleReactor reactor, int stance) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.REACTOR_CHANGE_STATE.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getTruePosition());
        mplew.writeShort(0); // delay
        mplew.write(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] spawnReactor(MapleReactor reactor) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.REACTOR_ENTER_FIELD.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.writeInt(reactor.getReactorId());
        mplew.write(reactor.getState());
        mplew.writeInt(reactor.getTruePosition().x); // CRC
        mplew.writeInt(reactor.getTruePosition().y); // CRC
        mplew.write(reactor.getFacingDirection());
        mplew.writeMapleAsciiString(reactor.getName());

        return mplew.getPacket();
    }

    public static byte[] destroyReactor(MapleReactor reactor) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.REACTOR_LEAVE_FIELD.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getPosition());

        return mplew.getPacket();
    }

    public static byte[] destroyReactor(MapleReactor reactor, int state) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.REACTOR_LEAVE_FIELD.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(state);
        mplew.writePos(reactor.getPosition());

        return mplew.getPacket();
    }

    public static byte[] resetReactor(MapleReactor reactor) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.REACTOR_RESET.getValue());
        oPacket.EncodeInt(reactor.getObjectId());
        return oPacket.getPacket();
    }

    public static byte[] makeExtractor(int cid, String cname, Point pos, int timeLeft, int itemId, int fee) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.EXTRACTOR_ENTER_FIELD.getValue());
        mplew.writeInt(cid);
        mplew.writeMapleAsciiString(cname);
        mplew.writeInt(pos.x);
        mplew.writeInt(pos.y);
        mplew.writeShort(timeLeft);
        mplew.writeInt(itemId);
        mplew.writeInt(fee);

        return mplew.getPacket();
    }

    public static byte[] removeExtractor(int cid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.EXTRACTOR_LEAVE_FIELD.getValue());
        mplew.writeInt(cid);
        /*
        1   : 분해기와의 거리가 멀어져서 분해기가 철거되었습니다.
        2   : 분해기가 철거되었습니다.
        3   : 분해기가 철거되었습니다.
         */
        mplew.writeInt(1);
        return mplew.getPacket();
    }

    public static byte[] rollSnowball(int type, MapleSnowball.MapleSnowballs ball1, MapleSnowball.MapleSnowballs ball2) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SNOWBALL_STATE.getValue());
        mplew.write(type);
        mplew.writeInt(ball1 == null ? 0 : ball1.getSnowmanHP() / 75);
        mplew.writeInt(ball2 == null ? 0 : ball2.getSnowmanHP() / 75);
        mplew.writeShort(ball1 == null ? 0 : ball1.getPosition());
        mplew.write(0);
        mplew.writeShort(ball2 == null ? 0 : ball2.getPosition());
        mplew.writeZeroBytes(11);

        return mplew.getPacket();
    }

    public static byte[] enterSnowBall() {
        return rollSnowball(0, null, null);
    }

    public static byte[] hitSnowBall(int team, int damage, int distance, int delay) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SNOWBALL_HIT.getValue());
        mplew.write(team);
        mplew.writeShort(damage);
        mplew.write(distance);
        mplew.write(delay);

        return mplew.getPacket();
    }

    public static byte[] snowballMessage(int team, int message) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SNOWBALL_MSG.getValue());
        mplew.write(team);
        mplew.writeInt(message);

        return mplew.getPacket();
    }

    public static byte[] leftKnockBack() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SNOWBALL_TOUCH.getValue());

        return mplew.getPacket();
    }

    public static byte[] hitCoconut(boolean spawn, int id, int type) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.COCONUT_HIT.getValue());
        mplew.writeInt(spawn ? 32768 : id);
        mplew.write(spawn ? 0 : type);

        return mplew.getPacket();
    }

    public static byte[] coconutScore(int[] coconutscore) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.COCONUT_SCORE.getValue());
        mplew.writeShort(coconutscore[0]);
        mplew.writeShort(coconutscore[1]);

        return mplew.getPacket();
    }

    public static byte[] updateAriantScore(List<MapleCharacter> players) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.ARIANT_SCORE_UPDATE.getValue());
        mplew.write(players.size());
        for (MapleCharacter i : players) {
            mplew.writeMapleAsciiString(i.getName());
            mplew.writeInt(0);
        }

        return mplew.getPacket();
    }

    public static byte[] sheepRanchInfo(byte wolf, byte sheep) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SHEEP_RANCH_INFO.getValue());
        mplew.write(wolf);
        mplew.write(sheep);

        return mplew.getPacket();
    }

    public static byte[] sheepRanchClothes(int cid, byte clothes) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SHEEP_RANCH_CLOTHES.getValue());
        mplew.writeInt(cid);
        mplew.write(clothes);

        return mplew.getPacket();
    }

    public static byte[] updateWitchTowerKeys(int keys) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.WITCH_TOWER.getValue());
        mplew.write(keys);
        return mplew.getPacket();
    }

    public static byte[] showChaosZakumShrine(boolean spawned, int time) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CHAOS_ZAKUM_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] showChaosHorntailShrine(boolean spawned, int time) {
        return showHorntailShrine(spawned, time);
    }

    public static byte[] showHorntailShrine(boolean spawned, int time) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.HORNTAIL_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getRPSMode(byte mode, int mesos, int selection, int answer) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.RPS_GAME.getValue());
        mplew.write(mode);
        switch (mode) {
            case 6:
                if (mesos == -1) {
                    break;
                }
                mplew.writeInt(mesos);
                break;
            case 8:
                mplew.writeInt(9000019);
                break;
            case 11:
                mplew.write(selection);
                mplew.write(answer);
        }

        return mplew.getPacket();
    }

    public static byte[] messengerInvite(String from, int messengerid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(3);
        mplew.writeMapleAsciiString(from);
        mplew.write(0);
        mplew.writeInt(messengerid);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0);//case
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.write(0);
        mplew.write(channel);
        mplew.writeInt(chr.getJob());
        return mplew.getPacket();
    }

    public static byte[] removeMessengerPlayer(int position) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(2);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static byte[] updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(8);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.writeInt(chr.getJob());
        return mplew.getPacket();
    }

    public static byte[] joinMessenger(int position) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(1);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static byte[] messengerChat(String charname, String text) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(6);
        mplew.writeMapleAsciiString(charname);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] messengerNote(String text, int mode, int mode2) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(text);
        mplew.write(mode2);

        return mplew.getPacket();
    }

    public static byte[] removeItemFromDuey(boolean remove, int Package) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(24);
        mplew.writeInt(Package);
        mplew.write(remove ? 3 : 4);

        return mplew.getPacket();
    }

    public static byte[] checkFailedDuey() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(9);
        mplew.write(-1); // 0xFF = error
        return mplew.getPacket();
    }

    public static byte[] sendDuey(byte operation, List<MapleDueyActions> packages, List<MapleDueyActions> expired) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(operation);
        if (packages == null) {
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);
            return mplew.getPacket();
        }
        switch (operation) {
            case 9: { // Request 13 Digit AS
                mplew.write(1);
                // 0xFF = error
                break;
            }
            case 10: { // Open duey
                mplew.write(0);
                mplew.write(packages.size());
                for (MapleDueyActions dp : packages) {
                    mplew.writeInt(dp.getPackageId());
                    mplew.writeAsciiString(dp.getSender(), 13);
                    mplew.writeInt(dp.getMesos());
                    mplew.writeLong(PacketHelper.getTime(dp.getExpireTime()));
                    mplew.write(dp.isQuick() ? 1 : 0);
                    mplew.writeAsciiString(dp.getContent(), 100);
                    mplew.writeZeroBytes(101);
                    if (dp.getItem() != null) {
                        mplew.write(1);
                        PacketHelper.addItemInfo(mplew, dp.getItem());
                    } else {
                        mplew.write(0);
                    }

                }
                if (expired == null) {
                    mplew.write(0);
                    return mplew.getPacket();
                }
                mplew.write(expired.size());
                for (MapleDueyActions dp : expired) {
                    mplew.writeInt(dp.getPackageId());
                    mplew.writeAsciiString(dp.getSender(), 13);
                    mplew.writeInt(dp.getMesos());
                    if (dp.canReceive()) {
                        mplew.writeLong(PacketHelper.getTime(dp.getExpireTime()));
                    } else {
                        mplew.writeLong(0);
                    }
                    mplew.write(dp.isQuick() ? 1 : 0);
                    mplew.writeAsciiString(dp.getContent(), 100);
                    mplew.writeInt(0);
                    if (dp.getItem() != null) {
                        mplew.write(1);
                        PacketHelper.addItemInfo(mplew, dp.getItem());
                    } else {
                        mplew.write(0);
                    }
                }
                break;
            }
        }
        return mplew.getPacket();
    }

    public static byte[] receiveParcel(String from, boolean quick) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(0x1A);
        mplew.writeMapleAsciiString(from);
        mplew.write(quick ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] getKeymap(MapleKeyLayout layout) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.FUNC_KEY_MAPPED_MAN_INIT.getValue());
        layout.IntData(mplew); // CRC
        return mplew.getPacket();
    }

    public static byte[] petAutoHP(int itemId) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.FUMC_KEY_MAPPED_PET_CONSUME_ITEM_INIT.getValue());
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }

    public static byte[] petAutoMP(int itemId) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.FUMC_KEY_MAPPED_PET_CONSUME_MP_ITEM_INIT.getValue());
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static void addRingInfo(OutPacket mplew, List<MapleRing> rings) {
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
    }

    public static void addMRingInfo(OutPacket mplew, List<MapleRing> rings, MapleCharacter chr) {
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeInt(chr.getId());
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeInt(ring.getItemId());
        }
    }

    public static byte[] viewSkills(MapleCharacter chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnResultStealSkillList.getValue());
        List skillz = new ArrayList();
        for (Skill sk : chr.getSkills().keySet()) {
            if ((sk.canBeLearnedBy(chr.getJob()))/* && (GameConstants.canSteal(sk))*/ && (!skillz.contains(Integer.valueOf(sk.getId())))) {
                skillz.add(Integer.valueOf(sk.getId()));
            }
        }
        mplew.write(1);
        mplew.writeInt(chr.getId());
        mplew.writeInt(skillz.isEmpty() ? 2 : 4);
        mplew.writeInt(chr.getJob());
        mplew.writeInt(skillz.size());
        for (Iterator i$ = skillz.iterator(); i$.hasNext();) {
            int i = ((Integer) i$.next());
            mplew.writeInt(i);
        }

        return mplew.getPacket();

    }

    public static class InteractionPacket {

        public static byte[] getTradeInvite(MapleCharacter c) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.INVITE_TRADE.action);
            mplew.write(3);
            mplew.writeMapleAsciiString(c.getName());
            mplew.writeInt(c.getLevel());
            //mplew.writeInt(c.getJob());
            return mplew.getPacket();
        }

        public static byte[] getTradecCashInvite(MapleCharacter c) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.INVITE_TRADE.action);
            mplew.write(6);
            mplew.writeMapleAsciiString(c.getName());
            mplew.writeInt(c.getLevel());
            //mplew.writeInt(c.getJob());
            return mplew.getPacket();
        }

        public static byte[] getTradeMesoSet(byte number, int meso) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.SET_MESO.action);
            mplew.write(number);
            mplew.writeInt(meso);

            return mplew.getPacket();
        }

        public static byte[] getTradeItemAdd(byte number, Item item) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.SET_ITEMS.action);
            mplew.write(number);
            mplew.write(item.getPosition());
            PacketHelper.addItemInfo(mplew, item);

            return mplew.getPacket();
        }

        public static byte[] getTradeStart(MapleClient c, MapleTrade trade, byte number) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(10);
            mplew.write(3);
            mplew.write(2);
            mplew.write(number);

            if (number == 1) {
                mplew.write(0);
                PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false);
                mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
                mplew.writeShort(trade.getPartner().getChr().getJob());
            }
            mplew.write(number);
            PacketHelper.addCharLook(mplew, c.getPlayer(), false);
            mplew.writeMapleAsciiString(c.getPlayer().getName());
            mplew.writeShort(c.getPlayer().getJob());
            mplew.write(255);

            return mplew.getPacket();
        }

        public static byte[] getTradeCashStart(MapleClient c, MapleTrade trade, byte number) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(10);
            mplew.write(6);
            mplew.write(2);
            mplew.write(number);

            if (number == 1) {
                mplew.write(0);
                PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false);
                mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
                mplew.writeShort(trade.getPartner().getChr().getJob());
            }
            mplew.write(number);
            PacketHelper.addCharLook(mplew, c.getPlayer(), false);
            mplew.writeMapleAsciiString(c.getPlayer().getName());
            mplew.writeShort(c.getPlayer().getJob());
            mplew.write(255);

            return mplew.getPacket();
        }

        public static byte[] getTradeConfirmation() {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.CONFIRM_TRADE.action);

            return mplew.getPacket();
        }

        public static byte[] TradeMessage(byte UserSlot, byte message) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(18);
            mplew.write(UserSlot);
            mplew.write(message);

            return mplew.getPacket();
        }

        public static byte[] getTradeCancel(byte UserSlot, int unsuccessful) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.EXIT.action);
            mplew.write(UserSlot);
            mplew.write(2);

            return mplew.getPacket();
        }
    }

    public static class NPCPacket {

        public static byte[] sub_7171A0(int npcObjId) {
            OutPacket oPacket = new OutPacket();
            oPacket.writeShort(368);
            oPacket.EncodeInt(npcObjId);
            return oPacket.getPacket();
        }

        public static byte[] sub_7171C0(int npcObjId, byte a, byte b, byte c) {
            OutPacket oPacket = new OutPacket();
            oPacket.writeShort(369);
            oPacket.EncodeInt(npcObjId);
            oPacket.EncodeByte(a);
            oPacket.EncodeByte(b);
            oPacket.EncodeByte(c);
            return oPacket.getPacket();
        }

        public static byte[] npcSetForceMove(int npcObjId, String direction, int delay) {
            OutPacket oPacket = new OutPacket();
            oPacket.writeShort(SendPacketOpcode.NPC_SET_FORCE_MOVE.getValue());
            oPacket.EncodeInt(npcObjId);
            oPacket.EncodeInt(direction.equals("left") ? -1 : 1);
            oPacket.EncodeInt(delay / 10);
            return oPacket.getPacket();
        }

        public static byte[] npcSetSpecialAction(int npcObjId, String effectName, int duration, boolean local) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.NPC_SET_SPECIAL_ACTION.getValue()); // NPC_SET_SPECIAL_ACTION

            mplew.writeInt(npcObjId);
            mplew.writeMapleAsciiString(effectName);
            mplew.writeInt(duration); // if  duration > 0  -> Repeat Effect lasts: duration
            mplew.write(true); // showLocal

            return mplew.getPacket();
        }

        public static byte[] spawnNPC(MapleNPC life, boolean show, int direction) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_ENTER_FIELD.getValue());
            mplew.writeInt(life.getObjectId());
            mplew.writeInt(life.getId());
            mplew.writeShort(life.getTruePosition().x);
            mplew.writeShort(life.getCy());
            switch (life.getId()) {
                case 3005827:
                case 3005836: {
                    direction = 0;
                    break;
                }
            }
            if (life.getId() == 1096008) {
                mplew.write(0);
            } else {
                if (direction > -1) {
                    mplew.write(direction);
                } else {
                    mplew.write(life.getF() == 1 ? 0 : 1);
                }
            }
            mplew.writeShort(life.getFh());
            mplew.writeShort(life.getRx0());
            mplew.writeShort(life.getRx1());
            mplew.write(show ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] removeNPC(int objectid) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_LEAVE_FIELD.getValue());
            mplew.writeInt(objectid);

            return mplew.getPacket();
        }

        public static byte[] removeNPCController(int objectid) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_CHANGE_CONTROLLER.getValue());
            mplew.write(0);
            mplew.writeInt(objectid);

            return mplew.getPacket();
        }

        public static byte[] spawnNPCRequestController(MapleNPC life, boolean MiniMap) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_CHANGE_CONTROLLER.getValue());
            mplew.write(1);
            mplew.writeInt(life.getObjectId());
            mplew.writeInt(life.getId());
            mplew.writeShort(life.getPosition().x);
            mplew.writeShort(life.getCy());
            mplew.write(life.getF() == 1 ? 0 : 1);
            mplew.writeShort(life.getFh());
            mplew.writeShort(life.getRx0());
            mplew.writeShort(life.getRx1());
            mplew.write(MiniMap ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] spawnNPCTemp(int value1, int value2) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.NPC_TEMP_SUMMON.getValue());
            mplew.writeShort(value1);
            mplew.writeShort(value2);
            mplew.writeMapleAsciiString("summon");
            mplew.write(0);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] setNPCScriptable(List<Pair<Integer, String>> npcs) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.NPC_SCRIPTABLE.getValue());
            mplew.write(npcs.size());
            for (Pair s : npcs) {
                mplew.writeInt(((Integer) s.left));
                mplew.writeMapleAsciiString((String) s.right);
                mplew.writeInt(0);
                mplew.writeInt(Integer.MAX_VALUE);
            }
            return mplew.getPacket();
        }

        public static byte[] OnAskBoxText(String a4, String v6) {
            
            OutPacket oPacket = new OutPacket();
            
            oPacket.EncodeShort(SendPacketOpcode.NPC_TALK.getValue());
            
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(9010017);
            oPacket.EncodeByte(15);
            oPacket.EncodeByte(0);
            
            oPacket.EncodeString(a4);
            oPacket.EncodeString(v6);
            oPacket.EncodeShort(20);
            oPacket.EncodeShort(5);
            
            return oPacket.getPacket();
        }

        public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type) {
            return getNPCTalk(npc, msgType, talk, endBytes, type, npc);
        }

        public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type, int diffNPC) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(msgType);
            mplew.write(type);

            /* sendSimpleS fix :: ida adress is sub_792760 */
            if (msgType == 5) {
                mplew.writeMapleAsciiString(talk);
                return mplew.getPacket();
            }

            if ((type & 0x4) != 0) {
                mplew.writeInt(diffNPC);
            }
            if (msgType == 15) {
                mplew.writeMapleAsciiString(talk); // nStr
                mplew.writeMapleAsciiString(talk); // nBoxText
                mplew.writeShort(20); // nWide
                mplew.writeShort(3); // nHigh
            } else {
                mplew.writeMapleAsciiString(talk);
                mplew.write(HexTool.getByteArrayFromHexString(endBytes));
            }
            return mplew.getPacket();
        }

        public static byte[] getMulungBuff(String cText) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(2091005);
            mplew.writeShort(16);
            mplew.writeInt(4);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(cText);
            return mplew.getPacket();
        }

        public static byte[] getMapSelection(int npcID, String nStr, int nType) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.NPC_TALK.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(npcID);
            oPacket.EncodeShort(16);
            oPacket.EncodeInt(nType);
            oPacket.EncodeInt(1);
            oPacket.EncodeString(nStr);
            return oPacket.getPacket();
        }

        public static byte[] getMapSelection(int npcid, String sel) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npcid);
            mplew.write(16);
            mplew.write(0);
            mplew.writeInt(GameConstants.isWarpGate(npcid) ? 6 : npcid == 2083006 ? 1 : npcid == 9000089 ? 3 : 0);
            mplew.writeInt((npcid == 9010022 || npcid == 9000089) ? 1 : 0);
            mplew.writeMapleAsciiString(sel);
            return mplew.getPacket();
        }

        public static byte[] tutorialUI(String[] args) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(8);
            mplew.writeInt(0);
            mplew.write(1);
            mplew.write(1);
            mplew.write(args.length);
            for (byte i = 0; i < args.length; i++) {
                mplew.writeMapleAsciiString(args[i]);
            }
            return mplew.getPacket();
        }

        public static byte[] getNPCTalkStyle(int npc, String talk, int[] args) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.writeShort(9);
            mplew.writeMapleAsciiString(talk);
            mplew.write(args.length);
            for (int i = 0; i < args.length; i++) {
                mplew.writeInt(args[i]);
            }
            return mplew.getPacket();
        }

        public static byte[] OnAskPet(int nNpc, String nStr) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.NPC_TALK.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(nNpc);
            oPacket.EncodeByte(11);
            oPacket.EncodeByte(0);
            oPacket.EncodeString(nStr);
            oPacket.EncodeByte(1); // pet count...?
            oPacket.EncodeByte(11);
            oPacket.EncodeLong(1);
            oPacket.EncodeByte(1);
            return oPacket.getPacket();
        }

        public static byte[] getNPCTalkNum(int npc, String talk, int def, int min, int max) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.writeShort(4);
            mplew.writeMapleAsciiString(talk);
            mplew.writeInt(def);
            mplew.writeInt(min);
            mplew.writeInt(max);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] getNPCTalkText(int npc, String talk) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.writeShort(3);
            mplew.writeMapleAsciiString(talk);
            mplew.writeInt(0);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] getSelfTalkText(String text) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.write(17);
            mplew.writeMapleAsciiString(text);
            return mplew.getPacket();
        }

        public static byte[] getNPCTutoEffect(String effect) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.write(1);
            mplew.writeShort(257);
            mplew.writeMapleAsciiString(effect);
            return mplew.getPacket();
        }

        public static byte[] 보스랭킹(List<MapleCharacter> players) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAggroRankInfoName.getValue());
            mplew.writeInt(players.size() > 5 ? 5 : players.size());

            //   if (players.size() != 1) {
            for (MapleCharacter chr : players) {
                // for(int i = 0; i < (players.size() > 5 ? 5 : players.size()); i++){
                if (chr.getName() != chr.getName() && chr.getRankingatt() > chr.getRankingatt()) {
                    mplew.writeMapleAsciiString(chr.getName());
                } else {
                    mplew.writeMapleAsciiString(chr.getName());
                }
                //  }
            }
            /*  } else {
                for (MapleCharacter chr : players) {
                    mplew.writeMapleAsciiString(chr.getName());
                }

            }*/

            return mplew.getPacket();
        }

        public static byte[] getEvanTutorial(String data) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());

            mplew.write(8);
            mplew.writeInt(0);
            mplew.write(1);
            mplew.write(1);
            mplew.write(1);
            mplew.writeMapleAsciiString(data);
            return mplew.getPacket();
        }

        public static final byte[] getSpeedQuiz(int npc, byte type, int oid, int points, int questionNo, int time) { //스피드 퀴즈
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.writeShort(7); // Speed quiz
            mplew.write(0); // 1 = close
            mplew.writeInt(type); // Type: 0 = NPC, 1 = Mob, 2 = Item
            mplew.writeInt(oid); // Object id
            mplew.writeInt(points); // points
            mplew.writeInt(questionNo); // questions 
            mplew.writeInt(time); // time in seconds

            return mplew.getPacket();
        }

        public static byte[] getEnglishQuiz(int npc, byte type, int diffNPC, String talk, String endBytes) { // 자음퀴즈
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(10); // not sure
            mplew.write(type);
            if ((type & 0x4) != 0) {
                mplew.writeInt(diffNPC);
            }
            mplew.writeMapleAsciiString(talk);
            mplew.write(HexTool.getByteArrayFromHexString(endBytes));

            return mplew.getPacket();
        }

        public static byte[] getNPCShop(int sid, MapleShop shop, MapleClient c) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OPEN_NPC_SHOP.getValue());
            mplew.writeInt(0);
            mplew.writeInt(sid);
            PacketHelper.addShopInfo(mplew, shop, c);

            return mplew.getPacket();
        }

        public static byte[] result(int mode, int mode2) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.ADMIN_SHOP_RESULT.getValue());
            mplew.write(mode);// ? always a 4
            mplew.write(mode2);
            // 3 취급하지않는다네 4 나한테 구걸하는건가? 5 주머니 6 무반응 7오늘 취급?
            return mplew.getPacket();
        }

        // Apparently the list of items is per gm, they can customize.
        // SN is used for recv packet.
        public static byte[] getAdminShop(int sid, MapleShop shop, MapleClient c) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.ADMIN_SHOP_COMMODITY.getValue());
            mplew.writeInt(sid);// dwNpctemplateID
            PacketHelper.addAdminShopInfo(mplew, shop, c);
            mplew.write(0);// bAskItemWishList

            return mplew.getPacket();
        }

        public static byte[] confirmShopTransaction(byte code, MapleShop shop, MapleClient c, int indexBought) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.CONFIRM_SHOP_TRANSACTION.getValue());
            mplew.write(code);
            if (code == 4) {
                mplew.writeInt(0);
                mplew.writeInt(shop.getNpcId());
                PacketHelper.addShopInfo(mplew, shop, c);
            } else {
                mplew.write(indexBought >= 0 ? 1 : 0);
                if (indexBought >= 0) {
                    mplew.writeInt(indexBought);
                }
            }

            return mplew.getPacket();
        }

        public static byte[] getStorage(int npcId, byte slots, Collection<Item> items, int meso) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(22);
            mplew.writeInt(npcId);
            mplew.write(slots);
            mplew.writeShort(126);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeInt(meso);
            mplew.writeShort(0);
            mplew.write((byte) items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            mplew.writeShort(0);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] getStorageFull() {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(17);

            return mplew.getPacket();
        }

        public static byte[] mesoStorage(byte slots, int meso) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(19);
            mplew.write(slots);
            mplew.writeShort(2);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeInt(meso);

            return mplew.getPacket();
        }

        public static byte[] arrangeStorage(byte slots, Collection<Item> items, boolean changed) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(15);
            mplew.write(slots);
            mplew.write(124);
            mplew.writeZeroBytes(10);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] storeStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(13);
            mplew.write(slots);
            mplew.writeShort(type.getBitfieldEncoding());
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            return mplew.getPacket();
        }

        public static byte[] takeOutStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(9);
            mplew.write(slots);
            mplew.writeShort(type.getBitfieldEncoding());
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            return mplew.getPacket();
        }
    }

    public static class SummonPacket {

        public static byte[] spawnSummon(MapleSummon summon, boolean animated) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.SPAWN_SUMMON.getValue());
            mplew.writeInt(summon.getOwnerId());
            mplew.writeInt(summon.getObjectId());
            mplew.writeInt(summon.getSkill());
            mplew.write(summon.getOwnerLevel());
            mplew.write(summon.getSkillLevel());
            mplew.writePos(summon.getPosition());
            mplew.write(summon.getSkill() == 32111006 || summon.getSkill() == 33101005 ? 5 : 4);
            if ((summon.getSkill() == 35121003) && (summon.getOwner().getMap() != null)) {
                mplew.writeShort(summon.getOwner().getMap().getFootholds().findBelow(summon.getPosition()).getId());
            } else {
                mplew.writeShort(0);
            }
            mplew.write(summon.getMovementType().getValue());
            mplew.write(summon.getSummonType());
            mplew.write(animated ? 1 : 0);
            mplew.write(1);
            MapleCharacter chr = summon.getOwner();
            mplew.write((summon.getSkill() == 4341006) && (chr != null) ? 1 : 0);
            if ((summon.getSkill() == 4341006) && (chr != null)) {
                PacketHelper.addCharLook(mplew, chr, true);
            }
            if (summon.getSkill() == 35111002) {
                mplew.write(0);
            }
            return mplew.getPacket();
        }

        public static byte[] test396(MapleSummon summon) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(399);
            mplew.writeInt(summon.getOwner().getId());
            mplew.writeInt(36121002);
            mplew.writeInt(36121002);
            //mplew.writePos(pos);
            //mplew.writeInt(36121002);
            return mplew.getPacket();
        }

        public static byte[] XenonBall(int v1, int cid, short v3, short v4, short v5, short v6, short v7, short v8, short v9, short monkeyf, short v10, int sid, short slevel, MapleCharacter chr, byte unk) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.XENONBALL.getValue());
            mplew.writeShort(v1);
            mplew.writeInt(cid);
            mplew.writeInt(chr.getMapId());
            mplew.writeInt(cid);//뭘까..
            mplew.write(v3);
            mplew.write(unk);
            mplew.writeShort(v4);
            mplew.writeShort(v5);
            if (v3 == 4) {
                mplew.writeShort(v6);
                mplew.writeShort(v7);
            }
            mplew.writeShort(v8);
            mplew.writeShort(v9);
            mplew.writeShort(monkeyf);
            mplew.writeShort(v10);
            mplew.writeInt(sid);
            mplew.writeShort(slevel);
            return mplew.getPacket();
        }

        public static byte[] XenonBall2(int v1, int cid, byte posx, byte unk, short t1, short t2, short t3, short unk30000, short wtf, short v10, int sid, short slevel, MapleCharacter chr) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.XENONBALL.getValue());
            mplew.writeShort(v1);
            mplew.writeInt(cid);
            mplew.writeInt(chr.getMapId());
            mplew.writeInt(cid);//뭘까..
            mplew.write(posx);
            mplew.write(unk);
            mplew.writeShort(t1);
            mplew.writeShort(t2);
            mplew.writeShort(t3);
            mplew.writeShort(unk30000);
            mplew.writeShort(wtf);
            mplew.writeShort(v10);
            mplew.writeInt(sid);
            mplew.writeShort(slevel);
            return mplew.getPacket();
        }

        public static byte[] XenonBall3(int v1, int unk, int cid, int sid, short wtf, short wtf2, MapleCharacter chr) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.XENONBALL.getValue());
            mplew.writeShort(v1);
            mplew.writeInt(cid);
            mplew.writeInt(chr.getMapId());
            mplew.writeInt(sid);
            mplew.writeInt(unk);
            mplew.writeShort(wtf);
            mplew.writeShort(wtf2);
            return mplew.getPacket();
        }

        public static byte[] XenonBall5(int sid, int slevel) {//화면깨지면서 캐릭터 풀체력달음
            OutPacket mplew = new OutPacket();

            mplew.writeShort(387);
            mplew.writeInt(sid);
            return mplew.getPacket();
        }

        public static byte[] XenonBall4() {//화면깨지면서 캐릭터 풀체력달음
            OutPacket mplew = new OutPacket();

            mplew.writeShort(384);
            mplew.write(1);
            return mplew.getPacket();
        }

        public static byte[] removeSummon(int ownerId, int objId) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
            mplew.writeInt(ownerId);
            mplew.writeInt(objId);
            mplew.write(10);

            return mplew.getPacket();
        }

        public static byte[] removeSummon(MapleSummon summon, boolean animated) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
            mplew.writeInt(summon.getOwnerId());
            mplew.writeInt(summon.getObjectId());
            if (animated) {
                switch (summon.getSkill()) {
                    case 35121003:
                        mplew.write(10);
                        break;
                    case 33101008:
                    case 35111001:
                    case 35111002:
                    case 35111005:
                    case 35111009:
                    case 35111010:
                    case 35111011:
                    case 35121009:
                    case 35121010:
                    case 35121011:
                        mplew.write(5);
                        break;
                    default:
                        mplew.write(4);
                        break;
                }
            } else {
                mplew.write(1);
            }

            return mplew.getPacket();
        }

        public static byte[] moveSummon(int cid, int oid, Point startPos, List<LifeMovementFragment> moves) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.MOVE_SUMMON.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(oid);
            mplew.writePos(startPos);
            mplew.writeInt(0);
            PacketHelper.serializeMovementList(mplew, moves);

            return mplew.getPacket();
        }

        public static byte[] summonAttackSpecial(int cid, int summonSkillId, byte animation, List<Pair<Integer, Integer>> allDamage, int level, byte tbyte, boolean darkFlare) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.SUMMON_ATTACK.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(level);
            mplew.write(animation);
            mplew.write(allDamage.size());
            for (Pair attackEntry : allDamage) {
                mplew.writeInt(((Integer) attackEntry.left));
                mplew.write(7);
                mplew.writeInt(((Integer) attackEntry.right));
            }
            mplew.write(darkFlare ? 1 : 0);
            return mplew.getPacket();
        }

        public static byte[] summonAttack(int cid, int summonSkillId, byte animation, List<AttackPair> damage, int level, byte tbyte, boolean darkFlare) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.SUMMON_ATTACK.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(level);
            mplew.write(animation);
            mplew.write(tbyte);
            for (AttackPair oned : damage) {
                mplew.writeInt(oned.objectid);
                mplew.write(oned.hitAction);
                for (Pair<Integer, Boolean> eachd : oned.attack) {
                    mplew.writeInt((eachd.left));
                }
            }
            mplew.write(darkFlare ? 1 : 0);
            return mplew.getPacket();
        }

        public static byte[] pvpSummonAttack(int cid, int playerLevel, int oid, int animation, Point pos, List<AttackPair> attack) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.PVP_SUMMON.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(oid);
            mplew.write(playerLevel);
            mplew.write(animation);
            mplew.writePos(pos);
            mplew.writeInt(0);
            mplew.write(attack.size());
            for (AttackPair p : attack) {
                mplew.writeInt(p.objectid);
                mplew.writePos(p.point);
                mplew.write(p.attack.size());
                mplew.write(0);
                for (Pair atk : p.attack) {
                    mplew.writeInt(((Integer) atk.left).intValue());
                }
            }

            return mplew.getPacket();
        }

        public static byte[] summonSkill(int cid, int summonSkillId, int newStance) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.SUMMON_SKILL.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(newStance);

            return mplew.getPacket();
        }

        public static byte[] damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.DAMAGE_SUMMON.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(unkByte);
            mplew.writeInt(damage);
            mplew.writeInt(monsterIdFrom);
            mplew.write(0);

            return mplew.getPacket();
        }
    }

    public static class UIPacket {

        public static final byte[] showSpecialMapEffect(int type, int action, String music, String back) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.SPECIAL_MAP_EFFECT.getValue());
            mplew.writeInt(type);
            mplew.writeInt(action);
            mplew.writeMapleAsciiString(music);
            if (back != null) {
                mplew.writeMapleAsciiString(back);
            }
            return mplew.getPacket();
        }

        public static final byte[] cancelSpecialMapEffect() {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.SPECIAL_MAP_EFFECT.getValue());
            mplew.writeLong(0);

            return mplew.getPacket();
        }

        public static final byte[] playSpecialMapSound(String sound) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.SPECIAL_MAP_SOUND.getValue());
            mplew.writeMapleAsciiString(sound);

            return mplew.getPacket();
        }

        public static byte[] 게임(int a) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnBoardGameResult.getValue());
            mplew.write(a);
            return mplew.getPacket();
        }

        public static byte[] 주사위(int a) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnBoardGameResult.getValue());
            mplew.write(5);
            mplew.writeInt(a); // 주사위값
            return mplew.getPacket();
        }

        public static byte[] 메소마켓(int cid) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnMesoExchangeResult.getValue());

            mplew.write(0x1);
            mplew.write(0);
            mplew.write(1);
            mplew.write(1);

            return mplew.getPacket();
        }

        public static byte[] 메소마켓2(int cid) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnMesoExchangeResult.getValue());

            mplew.write(0x2);
            mplew.write(0);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);

            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.write(0);

            /*
  *this = CInPacket::Decode1(a2) != 0;
  LOBYTE(v3) = CInPacket::Decode4(a2);
  *((_DWORD *)v2 + 1) = v3;
  LOBYTE(v4) = CInPacket::Decode4(a2);
  *((_DWORD *)v2 + 2) = v4;
  LOBYTE(v5) = CInPacket::Decode4(a2);
  *((_DWORD *)v2 + 3) = v5;
  LOBYTE(v6) = CInPacket::Decode4(a2);
  *((_DWORD *)v2 + 4) = v6;
  LOBYTE(v7) = CInPacket::Decode4(a2);
  *((_DWORD *)v2 + 5) = v7;
  LOBYTE(v8) = CInPacket::Decode4(a2);
  *((_DWORD *)v2 + 6) = v8;
  LOBYTE(v9) = CInPacket::Decode4(a2);
  *((_DWORD *)v2 + 7) = v9;
  v2[32] = CInPacket::Decode1(a2) != 0;
  result = CInPacket::Decode1(a2);
            
 this->bOnOff = CInPacket::Decode1(iPacket) != 0;
  v2->nAveragePrice = CInPacket::Decode4(iPacket);
  v2->nMaxBuyPrice = CInPacket::Decode4(iPacket);
  v2->nMinBuyPrice = CInPacket::Decode4(iPacket);
  v2->nAutoBuyPrice = CInPacket::Decode4(iPacket);
  v2->nMinSellPrice = CInPacket::Decode4(iPacket);
  v2->nMaxSellPrice = CInPacket::Decode4(iPacket);
  v2->nAutoSellPrice = CInPacket::Decode4(iPacket);
  v2->bBuyRegFull = CInPacket::Decode1(iPacket) != 0;
  v2->bSellRegFull = CInPacket::Decode1(iPacket) != 0;
             */
            return mplew.getPacket();
        }

        public static byte[] OnMentoring(byte v11, int v14) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnMentoring.getValue());
            mplew.write(v11);
            switch (v11) {
                case 4: {
                    break;
                }
                case 5: {
                    mplew.writeInt(v14);
                    break;
                }
                case 8: {
                    break;
                }
                case 9: {
                    break;
                }
                case 10: {
                    break;
                }
            }
            //mplew.writeMapleAsciiString("아카링");
            //mplew.writeInt(200); // 레벨
            //mplew.writeInt(2712); // 직업
            //mplew.writeInt(0); // 서브직업
            return mplew.getPacket();
        }

        public static byte[] getPCRoomCheck(byte status, int limitMinutes) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.PCROOM_CHECK.getValue());
            mplew.write(status);
            mplew.writeInt(limitMinutes);
            return mplew.getPacket();
        }

        public static byte[] saintSaver(MapleCharacter chr) {  // 160에없는듯
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnStatChanged.getValue());
            mplew.write(0);
            mplew.writeInt(MapleStat.VIRTUE.getValue());
            mplew.writeInt(1);
            mplew.writeShort(0);
            return mplew.getPacket();
        }

        public static byte[] UpdateCrossHunter(String a, int b) { // 크로스 헌터 패킷 updateInfoQuest 하고 다른건 없음
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(0x0C);
            mplew.writeShort(b);
            mplew.writeMapleAsciiString(a);
            return mplew.getPacket();
        }

        public static byte[] getDeathCount(int count) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.DEATH_COUNT.getValue());
            mplew.writeInt(count);
            return mplew.getPacket();
        }

        public static byte[] getEventList() {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(12);
            mplew.writeShort(7784);
            mplew.writeMapleAsciiString("플랜시아");
            /*
            mplew.writeShort(7790);
            mplew.writeMapleAsciiString("플랜시아");
            mplew.write(0);
            mplew.write(1);

            mplew.writeInt(0);
            mplew.writeMapleAsciiString("플랜시아");
            System.out.print("플랜시아");
            mplew.writeInt(2);
            byte b = 0;
            for (byte a = 0; a <= 1; a++) {
                mplew.writeInt(a);
                mplew.writeMapleAsciiString("최고의 답변자 3명을 찾습니다!");
                mplew.writeLong(0);
                mplew.writeShort(0);
                mplew.writeInt(20160116);
                b++;
                mplew.writeInt(20301231);
                b++;
                mplew.writeInt(0);
                mplew.writeLong(a + 4 < 5 ? 6 : 4);
                mplew.write(1);
                mplew.writeInt(1);
                for (byte bbbb = 0; bbbb <= 1; bbbb++) {
                    if (a == 0) {
                        mplew.writeInt(2049100);
                    }
                }
                mplew.writeInt(0);
                mplew.writeShort(0);//종료
            }
             */
            return mplew.getPacket();
        }

        public static byte[] getDirectionStatus(boolean enable) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.DIRECTION_REQUEST.getValue());
            mplew.write(enable ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] sendRepairWindow(int npc) {
            OutPacket mplew = new OutPacket(10);
            mplew.writeShort(SendPacketOpcode.OPEN_UI_WITH_OPTION.getValue());
            mplew.writeInt(33);
            mplew.writeInt(npc);
            return mplew.getPacket();
        }

        public static byte[] startAzwan(int npc) {
            OutPacket mplew = new OutPacket(10);
            mplew.writeShort(SendPacketOpcode.OPEN_UI_WITH_OPTION.getValue());
            mplew.writeInt(70);
            mplew.writeInt(npc);

            return mplew.getPacket();
        }

        //Useless, just opens the ui. Not able to pass through any args for it to populate data.
        public static byte[] AzwanRewards(int npc) {
            OutPacket mplew = new OutPacket(10);

            mplew.writeShort(SendPacketOpcode.OPEN_UI_WITH_OPTION.getValue());
            mplew.writeInt(70);
            mplew.writeInt(npc);

            return mplew.getPacket();
        }

        public static byte[] sendRedLeaf(int npc) {
            OutPacket mplew = new OutPacket(10);

            mplew.writeShort(SendPacketOpcode.OPEN_UI_WITH_OPTION.getValue());
            mplew.writeInt(66); // 70 azwan
            mplew.writeInt(npc);

            return mplew.getPacket();
        }

        public static byte[] sendDelay(int v8) {
            return sendDirectionInfo((byte) 1, 0, 0, v8, null, 0, null, false, 0, false, 0, (byte) 0, (byte) 0, 0, 0, 0, false, 0);
        }

        public static byte[] forcedInput(int v42) {
            return sendDirectionInfo((byte) 3, 0, 0, 0, null, 0, null, false, 0, false, 0, (byte) 0, (byte) 0, v42, 0, 0, false, 0);
        }

        public static byte[] forcedAction(int v3, int v5) {
            return sendDirectionInfo((byte) 0, v3, v5, 0, null, 0, null, false, 0, false, 0, (byte) 0, (byte) 0, 0, 0, 0, false, 0);
        }

        public static byte[] patternInputRequest(String v67, int v43, int v59, int v44) {
            return sendDirectionInfo((byte) 5, 0, 0, 0, v67, v59, null, false, 0, false, 0, (byte) 0, (byte) 0, 0, v43, v44, false, 0);
        }

        public static byte[] effectPlay(String v67, int v59, Point v57, boolean v14, int v65, boolean v15, int v17, byte v61, byte v20) {
            return sendDirectionInfo((byte) 2, 0, 0, 0, v67, v59, v57, v14, v65, v15, v17, (byte) v61, (byte) v20, 0, 0, 0, false, 0);
        }

        public static byte[] cameraMove(Point v57, boolean v64, int v9) {
            return sendDirectionInfo((byte) 6, 0, 0, 0, null, 0, v57, false, 0, false, 0, (byte) 0, (byte) 0, 0, 0, 0, v64, v9);
        }

        public static byte[] sendDirectionInfo(byte type, int v3, int v5, int v8, String v67,
                int v59, Point v57, boolean v14, int v65, boolean v15, int v17, byte v61, byte v20, int v42, int v43, int v44, boolean v64, int v9) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.DIRECTION_INFO.getValue());
            oPacket.EncodeByte(type);
            switch (type) {
                case 0: {   // ForcedAction
                    oPacket.EncodeInt(v3);
                    oPacket.EncodeInt(v5);
                    break;
                }
                case 1: {   // Delay
                    oPacket.EncodeLong(v8);
                    break;
                }
                case 2: {   // EffectPlay
                    oPacket.EncodeString(v67);
                    oPacket.EncodeInt(v59);
                    oPacket.writeIntPos(v57);
                    oPacket.EncodeByte(v14);
                    if (v14 == true) {
                        oPacket.EncodeInt(v65);
                    }
                    oPacket.EncodeByte(v15);
                    if (v15 == true) {
                        oPacket.EncodeInt(v17);
                        oPacket.EncodeByte(v61);
                        oPacket.EncodeByte(v20);
                    }
                    break;
                }
                case 3: {   // ForcedInput
                    /*
                        Stop(0),
                        WalkLeft(1)
                        WalkRight(2)
                        ClimbUp(3)
                        ClimbDown(4)
                        JumpLeft(5)
                        JumpRight(6)
                        JumpUp(7)
                        JumpDown(8)
                     */
                    oPacket.EncodeInt(v42);
                    break;
                }
                case 4: {
                    oPacket.EncodeInt(0);
                    break;
                }
                case 5: {   // PatternInputRequest
                    oPacket.EncodeString(v67);
                    oPacket.EncodeInt(v43);
                    oPacket.EncodeInt(v59);
                    oPacket.EncodeInt(v44);
                    break;
                }
                case 6: {   // CameraMove
                    oPacket.EncodeByte(v64); // false
                    oPacket.EncodeInt(v9); // MoveSpeed
                    if (v64 == false) {
                        oPacket.writeIntPos(v57);
                    }
                    break;
                }
            }
            return oPacket.getPacket();
        }

        public static byte[] getDirectionInfoTest(byte type, int value) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.DIRECTION_INFO.getValue());
            mplew.write(type);
            mplew.writeInt(value); // long -> int
            return mplew.getPacket();
        }

        public static byte[] getDirectionInfo(int type, int value, int x) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.DIRECTION_INFO.getValue());
            if (x > 0) {
                mplew.write(x);
            }
            mplew.write((byte) type);
            mplew.writeInt(value);

            return mplew.getPacket();
        }

        public static byte[] getDirectionInfo(int type, int value) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.DIRECTION_INFO.getValue());
            mplew.write((byte) type);
            mplew.writeLong(value); // 수정
            return mplew.getPacket();
        }

        public static byte[] getDirectionInfo(String data, int value, int x, int y, int a, int b) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.DIRECTION_INFO.getValue());
            mplew.write(2);
            mplew.writeMapleAsciiString(data);
            mplew.writeInt(value);
            mplew.writeInt(x);
            mplew.writeInt(y);
            mplew.write(a);
            if (a > 0) {
                mplew.writeInt(0);
            }
            mplew.write(b);
            if (b > 1) {
                mplew.writeInt(0);
            }
            return mplew.getPacket();
        }

        public static final byte[] getWheelNotice(byte type, int[] items, String randomstring) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(0x43);
            mplew.write(type); //5 = end 7 = not enough inventory slots
            switch (type) {
                case 3:
                    mplew.write(items.length);
                    for (int i = 0; i < items.length; i++) {
                        mplew.writeInt(items[i]);
                    }
                    mplew.writeMapleAsciiString(randomstring); // no idea (random 6 length strings)
                    mplew.write(5); //the wheel slot it stops on?
                    break;
                default:
                    break;
            }
            return mplew.getPacket();
        }

        public static final byte[] setRedLeafStatus(int joejoe, int hermoninny, int littledragon, int ika) {
            //packet made to set status
            //should remove it and make a handler for it, it's a recv opcode
            /*
             * slea:
             * E2 9F 72 00
             * 5D 0A 73 01
             * E2 9F 72 00
             * 04 00 00 00
             * 00 00 00 00
             * 75 96 8F 00
             * 55 01 00 00
             * 76 96 8F 00
             * 00 00 00 00
             * 77 96 8F 00
             * 00 00 00 00
             * 78 96 8F 00
             * 00 00 00 00
             */
            OutPacket mplew = new OutPacket();

            //mplew.writeShort();
            mplew.writeInt(7512034); //no idea
            mplew.writeInt(24316509); //no idea
            mplew.writeInt(7512034); //no idea
            mplew.writeInt(4); //no idea
            mplew.writeInt(0); //no idea
            mplew.writeInt(9410165); //joe joe
            mplew.writeInt(joejoe); //amount points added
            mplew.writeInt(9410166); //hermoninny
            mplew.writeInt(hermoninny); //amount points added
            mplew.writeInt(9410167); //little dragon
            mplew.writeInt(littledragon); //amount points added
            mplew.writeInt(9410168); //ika
            mplew.writeInt(ika); //amount points added

            return mplew.getPacket();
        }

        public static byte[] sendRedLeaf(int points, boolean viewonly) {
            /*
             * slea:
             * 73 00 00 00
             * 0A 00 00 00
             * 01
             */
            OutPacket mplew = new OutPacket(10);

            mplew.writeShort(SendPacketOpcode.OPEN_UI_WITH_OPTION.getValue());
            mplew.writeInt(0x73);
            mplew.writeInt(points);
            mplew.write(viewonly ? 1 : 0); //if view only, then complete button is disabled

            return mplew.getPacket();
        }
    }

    public static class EffectPacket {

        public static byte[] showBlueGatherMsg(String msg, int type, int gather, int itemID, int ItemQuantity) {
            OutPacket oPacket = new OutPacket();
            oPacket.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            oPacket.write(0x5);
            oPacket.write(type);
            if (type > 0) {
                oPacket.EncodeInt(itemID);
                oPacket.EncodeInt(ItemQuantity);
            }
            oPacket.writeMapleAsciiString(msg + " " + "(+" + gather + ")");
            oPacket.writeInt(-1);
            return oPacket.getPacket();
        }

        public static byte[] showSmellEffect(int cid, int effectid, int itemid) {
            OutPacket mplew = new OutPacket();
            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(effectid);
            mplew.writeInt(itemid);
            return mplew.getPacket();
        }

        public static byte[] showForeignEffect(int effect) {
            return showForeignEffect(-1, effect);
        }

        public static byte[] showForeignEffect(int cid, int effect) {
            OutPacket mplew = new OutPacket();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());

            } else {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(effect);

            if (effect == 15) {
                mplew.writeMapleAsciiString("Effect/BasicEff.img/Flame/SquibEffect");
            }
            return mplew.getPacket();
        }

        public static byte[] showItemLevelupEffect() {
            return showForeignEffect(18);
        }

        public static byte[] showForeignItemLevelupEffect(int cid) {
            return showForeignEffect(cid, 18);
        }

        public static byte[] showOwnDiceEffect(int skillid, int effectid, int effectid2, int level, byte diceNum) {
            return showDiceEffect(-1, skillid, effectid, effectid2, level, diceNum);
        }

        public static byte[] showDiceEffect(int cid, int skillid, int effectid, int effectid2, int level, byte diceNum) {
            OutPacket mplew = new OutPacket();
            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(3);
            mplew.writeInt(effectid);
            mplew.writeInt(-1);
            mplew.writeInt(skillid);
            mplew.write(level);
            mplew.write(diceNum);//for double down, this is 0 for left die, 1 for right

            return mplew.getPacket();
        }

        public static byte[] showOwnRecoverHP(int hp) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(0x1E); // 해보고안되면 29
            mplew.writeInt(hp);
            return mplew.getPacket();
        }

        public static byte[] fadeInOut(int v278, int v381, int v378, int v298) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            oPacket.EncodeByte(UserEffectType.FADE_IN_OUT.getValue());
            oPacket.EncodeInt(v278);
            oPacket.EncodeInt(v381);
            oPacket.EncodeInt(v378);
            oPacket.EncodeByte(v298);
            return oPacket.getPacket();
        }

        public static byte[] useCharm(byte charmsleft, byte daysleft, boolean safetyCharm) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(8); // 8 -> 9 확인
            mplew.write(safetyCharm ? 1 : 0);
            mplew.write(charmsleft);
            mplew.write(daysleft);
            if (!safetyCharm) {
                mplew.writeInt(0);
            }

            return mplew.getPacket();
        }

        public static byte[] Mulung_DojoUp2() {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(10);

            return mplew.getPacket();
        }

        public static byte[] showOwnHpHealed(int amount) {
            return showHpHealed(-1, amount);
        }

        public static byte[] showHpHealed(int cid, int amount) {
            OutPacket mplew = new OutPacket();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(30); // 안쓰이나봄 없음
            mplew.writeInt(amount);

            return mplew.getPacket();
        }

        public static byte[] failedRoulette(int fcase, int itemId) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.ROULETTE_FAILED.getValue());
            mplew.writeShort(fcase);
            mplew.writeInt(itemId);
            return mplew.getPacket();
        }

        public static byte[] startRoulette(int roulett_code) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.ROULETTE_START.getValue());
            mplew.writeInt(roulett_code);
            return mplew.getPacket();
        }

        public static byte[] showRewardItemAnimation(int itemId, String effect) {
            return showRewardItemAnimation(itemId, effect, -1);
        }

        public static byte[] showRewardItemAnimation(int itemId, String effect, int from_playerid) {
            OutPacket mplew = new OutPacket();

            if (from_playerid == -1) {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
                mplew.writeInt(from_playerid);
            }
            mplew.write(17);
            mplew.writeInt(itemId);
            mplew.write((effect != null) && (effect.length() > 0) ? 1 : 0);
            if ((effect != null) && (effect.length() > 0)) {
                mplew.writeMapleAsciiString(effect);
            }

            return mplew.getPacket();
        }

        public static byte[] showCashItemEffect(int itemId) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(23);//이거아님
            mplew.writeInt(itemId);

            return mplew.getPacket();
        }

        public static byte[] ItemMaker_Success() {
            return ItemMaker_Success_3rdParty(-1);
        }

        public static byte[] ItemMaker_Success_3rdParty(int from_playerid) {
            OutPacket mplew = new OutPacket();

            if (from_playerid == -1) {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
                mplew.writeInt(from_playerid);
            }
            mplew.write(19);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] useWheel(byte charmsleft) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(22);
            mplew.write(charmsleft);

            return mplew.getPacket();
        }

        public static byte[] showDualBlade(int cid, int skillid, int effectid, int playerLevel, int skillLevel, byte direction, int mobID) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
            mplew.writeInt(cid);
            mplew.write(effectid); //ehh?
            mplew.writeInt(skillid);
            mplew.write(playerLevel); //player level
            mplew.write(skillLevel); //skill level
            mplew.write(0);
            mplew.writeInt(mobID);
            return mplew.getPacket();
        }

        public static byte[] showBuffeffect(int cid, int skillid, int effectid, int playerLevel, int skillLevel) {
            return showBuffeffect(cid, skillid, effectid, playerLevel, skillLevel, (byte) 3);
        }

        public static byte[] showBuffeffect(int cid, int skillid, int effectid, int playerLevel, int skillLevel, byte direction) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
            mplew.writeInt(cid);
            mplew.write(effectid); //ehh?
            mplew.writeInt(skillid);
            mplew.write(playerLevel); //player level
            mplew.write(skillLevel); //skill level
            if (direction != (byte) 3 || skillid == 1320006 || skillid == 30001062 || skillid == 30001061 || skillid == 22160000 || skillid == 4331006) { // 22160000확인.
                mplew.write(direction);
                // 0: 포획에 성공하였습니다.
                // 1: 몬스터의 체력이 높아 포획이 실패하였습니다.
                // 2: 포획할 수 없는 몬스터 입니다.
            }
            if (skillid == 4331006) {
                mplew.writeInt(0);
            }
            if (skillid == 30001062) { // 헌터의 부름
                mplew.writeShort(0); // x [헌터의 부름으로 나오는 몬스터 x좌표]
                mplew.writeShort(0); // y [헌터의 부름으로 나오는 몬스터 y좌표]
            }
            /*if (skillid == 4331006) { // 필요없는듯 showDualBlade 에서 사용
                mplew.writeZeroBytes(4);
            }*/
            return mplew.getPacket();
        }

        public static byte[] showOwnBuffEffect(int skillid, int effectid, int playerLevel, int skillLevel) {
            return showOwnBuffEffect(skillid, effectid, playerLevel, skillLevel, (byte) 3);
        }

        public static byte[] showOwnBuffEffect(int skillid, int effectid, int playerLevel, int skillLevel, byte direction) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(effectid);
            mplew.writeInt(skillid);
            mplew.write(playerLevel); //player level
            mplew.write(skillLevel); //skill level
            if (direction != (byte) 3) {
                mplew.write(direction);
            }
            if (skillid == 31111003) {
                mplew.writeInt(0);
            }
            return mplew.getPacket();
        }

        public static byte[] testowl(int skillid, int effectid, int playerLevel, Point pos) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(effectid);
            mplew.writeInt(skillid);
            mplew.write(1);
            mplew.write(playerLevel); //player level
            mplew.writePos(pos);
            return mplew.getPacket();
        }

        public static byte[] testbuff() {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(7);
            mplew.writeInt(36110005);
            return mplew.getPacket();
        }

        public static byte[] ShowSoulSteelEffect(int cid, int buffid, int skillid, int skillLevel) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
            mplew.writeInt(cid);
            mplew.write(3);//이펙트아이디
            mplew.writeInt(buffid); //1= 공격반사 2 = 무적 3 = 방어력버프 4 = 공격력버프
            mplew.writeInt(-1);
            mplew.writeInt(skillid);//스킬아이디
            mplew.writeShort(skillLevel);//스킬레벨
            return mplew.getPacket();
        }

        public static byte[] ShowOwnSoulSteelEffect(int buffid, int skillid, int skillLevel) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(3);//이펙트아이디
            mplew.writeInt(buffid); //1= 공격반사 2 = 무적 3 = 방어력버프 4 = 공격력버프
            mplew.writeInt(-1);
            mplew.writeInt(skillid);//스킬아이디
            mplew.writeShort(skillLevel);//스킬레벨
            return mplew.getPacket();
        }

        public static byte[] ShowWZEffect(String data) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(21);
            mplew.writeMapleAsciiString(data);
            //mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] ShowWZEffectTest(String data, int a) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(a);
            mplew.writeMapleAsciiString(data);
            //  mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] showOwnCraftingEffect(String effect, int time, int mode) {
            return showCraftingEffect(-1, effect, time, mode);
        }

        public static byte[] showCraftingEffect(int cid, String effect, int time, int mode) {
            OutPacket mplew = new OutPacket();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(31); // 33-> 31
            mplew.writeMapleAsciiString(effect);
            mplew.write(0);
            mplew.writeInt(time);
            mplew.writeInt(mode);
            if (mode == 2) {
                mplew.writeInt(0);
            }
            return mplew.getPacket();
        }

        public static byte[] AranTutInstructionalBalloon(String data) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(24); // ?? 이팩트

            mplew.writeMapleAsciiString(data);
            mplew.writeInt(1);

            return mplew.getPacket();
        }

        public static byte[] showOwnPetLevelUp(byte index) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            mplew.write(6);
            mplew.write(0);
            mplew.writeInt(index);

            return mplew.getPacket();
        }

        public static byte[] showOwnChampionEffect() {
            return showChampionEffect(-1);
        }

        public static byte[] showChampionEffect(int from_playerid) {
            OutPacket mplew = new OutPacket();

            if (from_playerid == -1) {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
                mplew.writeInt(from_playerid);
            }
            mplew.write(32); // 34 -> 32
            mplew.writeInt(30000);

            return mplew.getPacket();
        }
    }

    public static byte[] sendZakumAltar(boolean v2, int v18) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.ZAKUM_ALTAR.getValue());
        /*
        true    : 자쿰의 제단이 닫힐 때까지 %d분 남았습니다.
        false   : %d분내에 자쿰을 소환하지 않으면 자쿰의 제단이 닫힙니다.
         */
        oPacket.EncodeByte(v2);
        /*
        v18 < 0 : 자쿰의 제단이 닫혔습니다.
         */
        oPacket.EncodeInt(v18);
        return oPacket.getPacket();
    }

    public static byte[] testPacket() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(132);
        oPacket.EncodeShort(1);
        oPacket.EncodeShort(1);
        oPacket.EncodeByte(1);
        return oPacket.getPacket();
    }

    // closeDamageMeter : 491
    public static byte[] setDamageMeter(MapleCharacter user) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(488);
        oPacket.EncodeInt(1000);
        oPacket.EncodeInt(2000);
        oPacket.EncodeByte(true);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);

        /*
        oPacket.EncodeShort(488);
        oPacket.EncodeInt(100); // burnedInfo.getDamage()
        oPacket.EncodeInt(3); // count
        oPacket.EncodeByte(true); // burnedInfo.getDotTickDamR() > 0
        oPacket.EncodeInt(10); // burnedInfo.getDotTickDamR()
        oPacket.EncodeInt(1121000); // burnedInfo.getSkillId()
         */
        return oPacket.getPacket();
    }

    // 버려도 되는 옵코드 : 418, 
    public static byte[] spawnHaku(MapleCharacter user) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.ANDROID_SPAWN.getValue());
        oPacket.EncodeInt(user.getId());
        byte nType = 1;
        oPacket.EncodeByte(nType);
        oPacket.EncodeShort(user.getTruePosition().x - 50);
        oPacket.EncodeShort(user.getTruePosition().y);
        oPacket.EncodeByte(user.getStance());
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(8920);
        oPacket.EncodeShort(4065);
        oPacket.EncodeString("하쿠");
        for (short i = -1200; i > -1207; i = (short) (i - 1)) {
            oPacket.EncodeInt(i == -1203 ? 1052949 : 0);
        }
        return oPacket.getPacket();
    }
}
