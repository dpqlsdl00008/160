package tools.packet;

import client.BuddylistEntry;
import client.CharacterTemporaryStat;
import client.MapleBuffStatStackedValueHolder;
import client.MapleCharacter;
import client.MonsterSkill;
import client.MapleQuestStatus;
import client.MapleStat;
import client.MapleTrait;
import client.Skill;
import client.SkillEntry;
import client.inventory.Item;
import client.inventory.MapleImp;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MapleMount;
import client.inventory.MaplePet;
import constants.GameConstants;
import constants.MapConstants;
import enums.PartyType;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.exped.MapleExpedition;
import handling.world.exped.PartySearch;
import handling.world.exped.PartySearchType;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import handling.world.guild.MapleGuildCharacter;
import handling.world.guild.MapleGuildSkill;
import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import server.MapleCarnivalChallenge;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.PlayerNPC;
import server.shops.HiredMerchant;
import server.shops.MaplePlayerShopItem;
import tools.HexTool;
import tools.ModifiedQuestTime;
import tools.Pair;
import tools.StringUtil;
import tools.data.OutPacket;

public class CWvsContext {

    public static byte[] itemCooldown(int v2, long v3) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnItemCoolTimeSet.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(v2);
        oPacket.EncodeLong(v3);
        return oPacket.getPacket();
    }

    public static byte[] 대충패킷(int cid, int s, int n, int type, int itemid) { // 작동함
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnMixerResult.getValue());
        mplew.writeInt(cid); // n -> 0
        //mplew.writeInt(s); // 시너지 아이템 코드? 사용할수없다
        mplew.writeInt(type); // 시너지 아이템 코드? 인벤토리에 없는아이템
        mplew.writeInt(1000 + s);
        mplew.writeInt(2048400); // 시너지 아이템 코드?
        // 100 1001 성공
        // 101 2001 마스터리북 사용할수없다?
        // 100 2001 사용할수없는 마북
        // 101 2002 인벤토리 공간
        return mplew.getPacket();
    }

    public static byte[] 테스트(int cid) { // 작동함
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnMixerResult.getValue());
        mplew.writeInt(cid); // n -> 0
        mplew.writeInt(100); // 시너지 아이템 코드? 사용할수없다
        mplew.writeInt(1000); // 시너지 아이템 코드? 인벤토리에 없는아이템
        mplew.writeInt(2048400); // 시너지 아이템 코드?

        // 100 1001 성공
        // 101 2001 마스터리북 사용할수없다?
        // 100 2001 사용할수없는 마북
        // 101 2002 인벤토리 공간
        //         mplew.writeInt(cid); // n -> 0
        // mplew.write(2);
        // mplew.writeShort(1);
        // mplew.write(2);
        // mplew.writeShort(2);
        /*
               mplew.writeShort(SendPacketOpcode.CRAFT_COMPLETE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(craftID);
        mplew.writeInt(ranking);
        mplew.writeInt(itemId);
        mplew.writeInt(quantity);
        mplew.writeInt(exp);
mplew.writeInt(2048400); // 시너지 아이템 코드?
         */
        //   mplew.write(2); // 0 메시지 2 
        // mplew.writeMapleAsciiString("    ");
        //mp//lew.writeLong(0);
        return mplew.getPacket();
    }

    public static byte[] enableActions() {
        return OnPlayerStatChanged(new EnumMap(MapleStat.class), true, null);
    }

    public static byte[] OnPlayerStatChanged(Map<MapleStat, Integer> stats, MapleCharacter chr) {
        return OnPlayerStatChanged(stats, false, chr);
    }

    public static byte[] OnPlayerStatChanged(Map<MapleStat, Integer> mystats, boolean itemReaction, MapleCharacter chr) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnStatChanged.getValue());
        oPacket.EncodeByte(itemReaction ? 1 : 0);
        int updateMask = 0;
        if (mystats.containsKey(MapleStat.MaxMP) && GameConstants.isDemon(chr.getJob())) {
            mystats.remove(MapleStat.MaxMP);
            mystats.put(MapleStat.MaxMP, 10);
        }
        for (MapleStat statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        oPacket.EncodeInt(updateMask);
        for (final Entry<MapleStat, Integer> statupdate : mystats.entrySet()) {
            switch (statupdate.getKey()) {
                case Skin: {
                    oPacket.EncodeByte(statupdate.getValue());
                    break;
                }
                case Face: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case Hair: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case Level: {
                    oPacket.EncodeByte(statupdate.getValue()); // 레벨 제한
                    break;
                }
                case Job: {
                    oPacket.EncodeShort(statupdate.getValue());
                    break;
                }
                case Str: {
                    oPacket.EncodeShort(statupdate.getValue());
                    break;
                }
                case Dex: {
                    oPacket.EncodeShort(statupdate.getValue());
                    break;
                }
                case Int: {
                    oPacket.EncodeShort(statupdate.getValue());
                    break;
                }
                case Luk: {
                    oPacket.EncodeShort(statupdate.getValue());
                    break;
                }
                case HP: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case MP: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case MaxHP: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case MaxMP: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case AP: {
                    oPacket.EncodeShort(statupdate.getValue());
                    break;
                }
                case SP: { // sub_520810
                    int a1 = chr.getJob();
                    if (a1 / 1000 == 3
                            || a1 / 100 == 22 || a1 == 2001
                            || a1 / 100 == 23 || a1 == 2002
                            || a1 / 100 == 24 || a1 == 2003
                            || a1 / 100 == 51 || a1 == 5000
                            || a1 / 100 == 27 || a1 == 2004
                            || a1 / 100 == 61 || a1 == 6000
                            || a1 / 100 == 62 || a1 == 6001
                            || a1 / 100 == 65 || a1 == 6002
                            || a1 / 100 == 42 || a1 == 4000
                            || a1 / 100 == 52 || a1 == 5001) {
                        oPacket.EncodeByte(chr.getRemainingSpSize());
                        for (int i = 0; i < chr.getRemainingSps().length; i++) {
                            if (chr.getRemainingSp(i) > 0) {
                                oPacket.EncodeByte(i + 1);
                                oPacket.EncodeByte(chr.getRemainingSp(i));
                            }
                        }
                    } else {
                        oPacket.EncodeShort(chr.getRemainingSp());
                    }
                    break;
                }
                case Exp: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case Fame: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case Meso: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case Fatigue: {
                    oPacket.EncodeByte(statupdate.getValue());
                    break;
                }
                case Charisma: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case Insight: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case Will: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case Craft: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case DayLimit: {
                    oPacket.EncodeInt(statupdate.getValue());
                    oPacket.EncodeInt(statupdate.getValue());
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case PvpExp: {
                    oPacket.EncodeInt(statupdate.getValue());
                    oPacket.EncodeByte(statupdate.getValue());
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case PvpRank: {
                    oPacket.EncodeByte(statupdate.getValue());
                    oPacket.EncodeByte(statupdate.getValue());
                    break;
                }
                case PvpPoint: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
                case IceGuage: {
                    oPacket.EncodeByte(statupdate.getValue());
                    break;
                }
                case Pet: {
                    oPacket.EncodeLong(statupdate.getValue());
                    oPacket.EncodeLong(statupdate.getValue());
                    oPacket.EncodeLong(statupdate.getValue());
                    break;
                }
                default: {
                    oPacket.EncodeInt(statupdate.getValue());
                    break;
                }
            }
        }
        if ((updateMask == 0L) && (!itemReaction)) {
            oPacket.EncodeByte(1);
        }
        oPacket.EncodeByte(0); // SetSecondaryStatChangedPoint

        boolean battleRecord = false;
        oPacket.EncodeByte(false);
        if (battleRecord) {
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
        }

        return oPacket.getPacket();
    }

    public static byte[] temporaryStats_Aran() {
        Map stats = new EnumMap(MapleStat.Temp.class);

        stats.put(MapleStat.Temp.STR, Integer.valueOf(999));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(999));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(999));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(999));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(255));
        stats.put(MapleStat.Temp.ACC, Integer.valueOf(999));
        stats.put(MapleStat.Temp.AVOID, Integer.valueOf(999));
        stats.put(MapleStat.Temp.SPEED, Integer.valueOf(140));
        stats.put(MapleStat.Temp.JUMP, Integer.valueOf(120));

        return temporaryStats(stats);
    }

    public static byte[] temporaryStats_Balrog(MapleCharacter chr) {
        Map stats = new EnumMap(MapleStat.Temp.class);

        int offset = 1 + (chr.getLevel() - 90) / 20;
        stats.put(MapleStat.Temp.STR, Integer.valueOf(chr.getStat().getTotalStr() / offset));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(chr.getStat().getTotalDex() / offset));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(chr.getStat().getTotalInt() / offset));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(chr.getStat().getTotalLuk() / offset));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(chr.getStat().getTotalWatk() / offset));
        stats.put(MapleStat.Temp.MATK, Integer.valueOf(chr.getStat().getTotalMagic() / offset));

        return temporaryStats(stats);
    }

    public static byte[] temporaryStats(Map<MapleStat.Temp, Integer> mystats) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnForcedStatSet.getValue());
        int updateMask = 0;
        for (MapleStat.Temp statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        mplew.writeInt(updateMask);
        for (final Entry<MapleStat.Temp, Integer> statupdate : mystats.entrySet()) {
            switch (statupdate.getKey()) {
                case SPEED:
                case JUMP:
                case UNKNOWN:
                    mplew.write(((Integer) statupdate.getValue()).byteValue());
                    break;
                default:
                    mplew.writeShort(((Integer) statupdate.getValue()).shortValue());
            }

        }

        return mplew.getPacket();
    }

    public static byte[] temporaryStats_Reset() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnForcedStatReset.getValue());

        return mplew.getPacket();
    }

    public static byte[] Unlinkskill(int a2) {
        final OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnUnLinkedSkillInfo.getValue());
        mplew.write(a2);
        return mplew.getPacket();
    }

    public static byte[] Unlinkskillunlock(int skillid, int unlock) {
        final OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.UNLINK_SKILL_UNLOCK.getValue());
        mplew.writeInt(skillid);
        mplew.writeInt(unlock); // ??
        return mplew.getPacket();
    }

    public static byte[] Unlocklinkskill(int oriskillid, Map<Integer, Integer> unlock) {
        final OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnUnLinkedSkillInfo.getValue());
        mplew.writeInt(unlock.size() + 1);
        mplew.writeInt(oriskillid);
        mplew.writeInt(unlock.size());
        for (Entry<Integer, Integer> skill : unlock.entrySet()) {
            mplew.writeInt(skill.getKey());
            mplew.writeInt(skill.getValue());
        }
        return mplew.getPacket();
    }

    public static byte[] LinkedSkillAction(int skillid, String charname, String already, int charid, int check) {
        OutPacket packet = new OutPacket();
        packet.writeShort(SendPacketOpcode.OnLinkedSkillInfo.getValue());
        packet.writeInt(check);
        packet.writeInt(skillid);
        if (check == 0) {
            packet.writeInt(charid);
            packet.writeMapleAsciiString(charname);
        } else {
            packet.writeMapleAsciiString(already);
            packet.writeMapleAsciiString(charname);
        }
        return packet.getPacket();
    }

    public static byte[] updateLinkSkills(int skillid, int level, int masterlevel, long expiration) {
        // 여기는 자기가 가진 링크 스킬을 누구에게 전수 해주었냐 표시해주는 부분
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnChangeSkillRecordResult.getValue());
        mplew.write(0);
        mplew.write(0);// Boolean (스킬활성,비활성)
        mplew.writeShort(1);
        mplew.writeInt(skillid);
        mplew.writeInt(level);
        mplew.writeInt(masterlevel);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.write(4);
        return mplew.getPacket();
    }

    public static byte[] updateSkills(Map<Skill, SkillEntry> update) {
        OutPacket mplew = new OutPacket(7 + update.size() * 20);

        mplew.writeShort(SendPacketOpcode.OnChangeSkillRecordResult.getValue());
        mplew.write(1);
        mplew.write(0);// Boolean (스킬활성,비활성)
        mplew.writeShort(update.size());
        for (Map.Entry z : update.entrySet()) {
            mplew.writeInt(((Skill) z.getKey()).getId());
            mplew.writeInt(((SkillEntry) z.getValue()).skillevel);
            mplew.writeInt(((SkillEntry) z.getValue()).masterlevel);
            PacketHelper.addExpirationTime(mplew, ((SkillEntry) z.getValue()).expiration);
        }
        mplew.write(4);
        return mplew.getPacket();
    }

    public static byte[] activeupdateSkills(Map<Skill, SkillEntry> update) {
        OutPacket mplew = new OutPacket(7 + update.size() * 20);

        mplew.writeShort(SendPacketOpcode.OnChangeSkillRecordResult.getValue());
        mplew.write(1);
        mplew.write(1);// Boolean (스킬활성,비활성)
        mplew.writeShort(update.size());
        for (Map.Entry z : update.entrySet()) {
            mplew.writeInt(((Skill) z.getKey()).getId());
            mplew.writeInt(((SkillEntry) z.getValue()).skillevel);
            mplew.writeInt(((SkillEntry) z.getValue()).masterlevel);
            PacketHelper.addExpirationTime(mplew, ((SkillEntry) z.getValue()).expiration);
        }
        mplew.write(4);
        return mplew.getPacket();
    }

    public static byte[] giveFameErrorResponse(int op) {
        return OnFameResult(op, null, true, 0);
    }

    public static byte[] OnFameResult(int op, String charname, boolean raise, int newFame) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnGivePopularityResult.getValue());
        mplew.write(op);
        if ((op == 0) || (op == 5)) {
            mplew.writeMapleAsciiString(charname == null ? "" : charname);
            mplew.write(raise ? 1 : 0);
            if (op == 0) {
                mplew.writeInt(newFame);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] OnClaimResult(int mode) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnClaimResult.getValue());
        mplew.write(mode);
        if (mode == 2) {
            mplew.write(0);
            mplew.writeInt(1);
        }
        return mplew.getPacket();
    }

    public static byte[] OnSetClaimSvrAvailableTime(int from, int to) {
        OutPacket mplew = new OutPacket(4);
        mplew.writeShort(SendPacketOpcode.SET_CLAIM_SVR_AVAILABLE_TIME.getValue());
        mplew.write(from);
        mplew.write(to);
        return mplew.getPacket();
    }

    public static byte[] OnClaimSvrStatusChanged(boolean enable) {
        OutPacket mplew = new OutPacket(3);
        mplew.writeShort(SendPacketOpcode.OnClaimSvrStatusChanged.getValue());
        mplew.write(enable ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] updateMount(MapleCharacter chr, boolean levelup) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnSetTamingMobInfo.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());
        mplew.write(levelup ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] getShowQuestCompletion(int id) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnQuestClear.getValue());
        mplew.writeShort(id);

        return mplew.getPacket();
    }

    public static byte[] useSkillBook(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnSkillLearnItemResult.getValue());
        mplew.write(0);
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(skillid);
        mplew.writeInt(maxlevel);
        mplew.write(canuse ? 1 : 0);
        mplew.write(success ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] useAPSPReset(boolean spReset, int cid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(spReset ? SendPacketOpcode.OnSkillResetItemResult.getValue() : SendPacketOpcode.OnAbilityResetIemResult.getValue());
        mplew.write(1);
        mplew.writeInt(cid);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] expandCharacterSlots(int mode) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnIncCharactorSlotResult.getValue());
        mplew.writeInt(mode);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] finishedGather(int type) {
        return gatherSortItem(true, type);
    }

    public static byte[] finishedSort(int type) {
        return gatherSortItem(false, type);
    }

    public static byte[] gatherSortItem(boolean gather, int type) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(gather ? SendPacketOpcode.OnGatherItemResult.getValue() : SendPacketOpcode.OnSortItemResult.getValue());
        mplew.write(1);
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] charInfo(MapleCharacter chr, boolean isSelf) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnCharacterInfo.getValue());
        mplew.writeInt(chr.getId());
        mplew.EncodeByte(chr.getLevel()); // 레벨 제한
        mplew.writeShort(chr.getJob());

        mplew.writeShort(chr.getSubcategory());
        mplew.write(chr.getStat().pvpRank);

        mplew.writeInt(chr.getFame());
        mplew.write(chr.getMarriageId() > 0 ? 1 : 0);
        int Profession_Count = 0;
        int Profession_Skill[] = {92000000, 92010000, 92020000, 92030000, 92040000};
        for (int i = 0; i < Profession_Skill.length; i++) {
            if (chr.getProfessionLevel(Profession_Skill[i]) > 0) {
                Profession_Count++;
            }
        }
        mplew.write(Profession_Count);
        for (int i = 0; i < Profession_Skill.length; i++) {
            if (chr.getProfessionLevel(Profession_Skill[i]) > 0) {
                mplew.writeShort(Profession_Skill[i] / 10000);
            }
        }
        if (chr.getGuildId() <= 0) {
            mplew.writeMapleAsciiString("-");
            mplew.writeMapleAsciiString("");
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                if (gs.getAllianceId() > 0) {
                    MapleGuildAlliance allianceName = World.Alliance.getAlliance(gs.getAllianceId());
                    if (allianceName != null) {
                        mplew.writeMapleAsciiString(allianceName.getName());
                    } else {
                        mplew.writeMapleAsciiString("");
                    }
                } else {
                    mplew.writeMapleAsciiString("");
                }
            } else {
                mplew.writeMapleAsciiString("-");
                mplew.writeMapleAsciiString("");
            }
        }

        mplew.write(isSelf ? 1 : 0);
        mplew.write(0);
        mplew.write(chr.getPet(0) != null ? 1 : 0);
        byte index = 0;
        for (MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                mplew.write(1);
                mplew.writeInt(index);
                mplew.writeInt(pet.getPetItemId());
                mplew.writeMapleAsciiString(pet.getName());
                mplew.write(pet.getLevel());
                mplew.writeShort(pet.getCloseness());
                mplew.write(pet.getFullness());
                mplew.writeShort(pet.getFlags());
                final Item inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) (chr.getPetIndex(pet) == 0 ? -114 : (chr.getPetIndex(pet) == 1 ? -130 : -138)));
                mplew.writeInt(inv == null ? 0 : inv.getItemId());
                //mplew.writeInt(-1);                
                index = (byte) (index + 1);
            }
        }
        mplew.write(0);
        if ((chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18) != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -19) != null)) {
            MapleMount mount = chr.getMount();
            mplew.write(1);
            mplew.writeInt(mount.getLevel());
            mplew.writeInt(mount.getExp());
            mplew.writeInt(mount.getFatigue());
        } else {
            mplew.write(0);
        }

        int wishlistSize = chr.getWishlistSize();
        mplew.write(wishlistSize);
        if (wishlistSize > 0) {
            int[] wishlist = chr.getWishlist();
            for (int x = 0; x < wishlistSize; x++) {
                mplew.writeInt(wishlist[x]);
            }
        }

        Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -21);
        mplew.writeInt(medal == null ? 0 : medal.getItemId());
        List<Pair<Integer, Long>> medalQuests = chr.getCompletedMedals();
        mplew.writeShort(medalQuests.size());
        for (Pair x : medalQuests) {
            mplew.writeShort(((Integer) x.left));
            mplew.writeLong(((Long) x.right));
        }
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            mplew.write(chr.getTrait(t).getLevel());
        }
        return mplew.getPacket();
    }

    public static byte[] spawnPortal(int townId, int targetId, int skillId, Point pos) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnTownPortal.getValue());
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        if ((townId != 999999999) && (targetId != 999999999)) {
            mplew.writeInt(skillId);
            mplew.writePos(pos);
        }

        return mplew.getPacket();
    }

    public static byte[] mechPortal(Point pos) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnOpenGate.getValue());
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] showQuestMsg(String msg) {
        return serverNotice(5, msg);
    }

    public static byte[] Mulung_Pts(int recv, int total) {
        return showQuestMsg(new StringBuilder().append("수련 점수를 ").append(recv).append("점 받았습니다. 총 수련 점수가 ").append(total).append("점이 되었습니다.").toString());
    }

    public static byte[] OnBroadcastMsg(int type, int channel, String name, String msg, boolean megaEar, Item item) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnBroadcastMsg.getValue());
        oPacket.EncodeByte(type);
        if (type == 4) {
            oPacket.EncodeByte(1);
        }
        if (type != 13 && type != 14) {
            oPacket.EncodeString(msg);
        }
        switch (type) {
            case 3: {
                oPacket.EncodeByte(channel - 1);
                oPacket.EncodeByte(megaEar);
                break;
            }
            case 8: {
                oPacket.EncodeByte(channel - 1);
                oPacket.EncodeByte(megaEar);
                oPacket.EncodeByte(item != null);
                if (item != null) {
                    PacketHelper.addItemInfo(oPacket, item);
                }
                break;
            }
            case 9: {
                oPacket.EncodeByte(channel - 1);
                break;
            }
            case 10: {
                int msgLine = 0;
                oPacket.EncodeByte(msgLine);
                if (msgLine > 1) {
                    String msg2 = "";
                    oPacket.EncodeString(msg2);
                }
                if (msgLine > 2) {
                    String msg3 = "";
                    oPacket.EncodeString(msg3);
                }
                oPacket.EncodeByte(channel - 1);
                oPacket.EncodeByte(megaEar);
                break;
            }
        }
        switch (type) {
            case 0: {
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            case 3:
            case 8:
            case 9:
            case 10: {
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                break;
            }
            case 6: {
                int itemID = channel;
                oPacket.EncodeInt(itemID);
                break;
            }
            case 7: {
                int npcID = channel;
                oPacket.EncodeInt(npcID);
                break;
            }
            case 11: {
                int itemID = channel;
                oPacket.EncodeInt(itemID);
                oPacket.EncodeByte(item != null);
                if (item != null) {
                    PacketHelper.addItemInfo(oPacket, item);
                }
                break;
            }
            case 12: {
                break;
            }
            case 13: {
                int v4 = 0;
                int v89 = 0;
                oPacket.EncodeShort(v4);
                oPacket.EncodeInt(v89);
                break;
            }
            case 14: {
                int v91 = 0;
                oPacket.EncodeShort(v91);
                break;
            }
        }
        return oPacket.getPacket();
    }

    public static byte[] serverMessage(String message) {
        return serverMessage(4, 0, message, false);
    }

    public static byte[] serverNotice(int type, String message) {
        return serverMessage(type, 0, message, false);
    }

    public static byte[] serverNotice(int type, int channel, String message) {
        return serverMessage(type, channel, message, false);
    }

    public static byte[] serverNotice(int type, int channel, String message, boolean smegaEar) {
        return serverMessage(type, channel, message, smegaEar);
    }

    private static byte[] serverMessage(int type, int channel, String message, boolean megaEar) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnBroadcastMsg.getValue());
        mplew.write(type);
        if (type == 4) {
            mplew.write(1);
        }
        if ((type != 23) && (type != 24)) {
            mplew.writeMapleAsciiString(message);
        }
        switch (type) {
            case 3:
            case 22:
            case 25:
            case 26:
                mplew.write(channel - 1);
                mplew.write(megaEar ? 1 : 0);
                break;
            case 9:
                mplew.write(channel - 1);
                break;
            case 12:
                mplew.writeInt(channel);
                break;
            case 6:
            case 11:
            case 20:
                mplew.writeInt((channel >= 1000000) && (channel < 6000000) ? channel : 0);
                break;
            case 24:
                mplew.writeShort(0);
            case 4:
            case 5:
            case 7:
            case 8: //뭐있음
            case 10: // 뭐있음
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 21:
            case 23:
        }
        return mplew.getPacket();
    }

    public static byte[] getGachaponMega(String name, String message, Item item, byte rareness, String gacha) {
        return getGachaponMega(name, message, item, rareness, false, gacha);
    }

    public static byte[] getGachaponMega(String name, String message, Item item, byte rareness, boolean dragon, String gacha) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnBroadcastMsg.getValue());
        mplew.write(13);
        mplew.writeMapleAsciiString(new StringBuilder().append(name).append(message).toString());
        if (!dragon) {
            mplew.writeInt(0);
            mplew.writeInt(item.getItemId());
        }
        mplew.writeMapleAsciiString(gacha);
        PacketHelper.addItemInfo(mplew, item);

        return mplew.getPacket();
    }

    public static byte[] getAniMsg(int questID, int time) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnBroadcastMsg.getValue());
        mplew.write(23);
        mplew.writeShort(questID);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] tripleSmega(List<String> message, boolean ear, int channel) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnBroadcastMsg.getValue());
        mplew.write(10);
        if (message.get(0) != null) {
            mplew.writeMapleAsciiString((String) message.get(0));
        }
        mplew.write(message.size());
        for (int i = 1; i < message.size(); i++) {
            if (message.get(i) != null) {
                mplew.writeMapleAsciiString((String) message.get(i));
            }
        }
        mplew.write(channel - 1);
        mplew.write(ear ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] itemMegaphone(String msg, boolean whisper, int channel, Item item) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnBroadcastMsg.getValue());
        mplew.write(8);
        mplew.writeMapleAsciiString(msg);
        mplew.write(channel - 1);
        mplew.write(whisper ? 1 : 0);
        PacketHelper.addItemPosition(mplew, item, true, false);
        if (item != null) {
            PacketHelper.addItemInfo(mplew, item);
        }

        return mplew.getPacket();
    }

    public static byte[] getPeanutResult(int itemId, short quantity, int itemId2, short quantity2, int ourItem) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnIncubatorResult.getValue());
        mplew.writeInt(itemId);
        mplew.writeShort(quantity);
        mplew.writeInt(ourItem);
        mplew.writeInt(itemId2);
        mplew.writeInt(quantity2);
        mplew.write(0); // 피그미 관련 바이트 + 2
        mplew.write(0); // 나중에해보고 팅기면 지우는걸로 V.160

        return mplew.getPacket();
    }

    public static byte[] getOwlOpen() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnShopScannerResult.getValue());
        mplew.write(9);
        mplew.write(GameConstants.owlItems.length);
        for (int i : GameConstants.owlItems) {
            mplew.writeInt(i);
        }

        return mplew.getPacket();
    }

    public static byte[] getOwlSearched(int itemSearch, List<HiredMerchant> hms) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnShopScannerResult.getValue());
        mplew.write(8);
        mplew.writeInt(0);
        mplew.writeInt(itemSearch);
        int size = 0;

        for (HiredMerchant hm : hms) {
            size += hm.searchItem(itemSearch).size();
        }

        mplew.writeInt(size);
        for (HiredMerchant hm : hms) {
            for (Iterator i = hms.iterator(); i.hasNext();) {
                hm = (HiredMerchant) i.next();
                final List<MaplePlayerShopItem> items = hm.searchItem(itemSearch);
                for (MaplePlayerShopItem item : items) {
                    mplew.writeMapleAsciiString(hm.getOwnerName());
                    mplew.writeInt(hm.getMap().getId());
                    mplew.writeMapleAsciiString(hm.getDescription());
                    mplew.writeInt(item.item.getQuantity());
                    mplew.writeInt(item.bundles);
                    mplew.writeInt(item.price);
                    switch (2) {
                        case 0:
                            mplew.writeInt(hm.getOwnerId());
                            break;
                        case 1:
                            mplew.writeInt(hm.getStoreId());
                            break;
                        default:
                            mplew.writeInt(hm.getObjectId());
                    }

                    mplew.write(hm.getFreeSlot() == -1 ? 1 : 0);
                    mplew.write(GameConstants.getInventoryType(itemSearch).getType());
                    if (GameConstants.getInventoryType(itemSearch) == MapleInventoryType.EQUIP) {
                        PacketHelper.addItemInfo(mplew, item.item);
                    }
                }
            }
        }
        return mplew.getPacket();
    }

    public static byte[] getOwlMessage(int msg) {
        OutPacket mplew = new OutPacket(3);

        mplew.writeShort(SendPacketOpcode.OnShopLinkResult.getValue());
        mplew.write(msg);

        return mplew.getPacket();
    }

    public static byte[] sendEngagementRequest(String name, int cid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnMarriageRequest.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(name);
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] sendEngagement(byte msg, int item, MapleCharacter male, MapleCharacter female) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnMarriageResult.getValue());
        mplew.write(msg);
        if ((msg == 11) || (msg == 12)) {
            mplew.writeInt(0);
            mplew.writeInt(male.getId());
            mplew.writeInt(female.getId());
            mplew.writeShort(1);
            mplew.writeInt(item);
            mplew.writeInt(item);
            mplew.writeAsciiString(male.getName(), 13);
            mplew.writeAsciiString(female.getName(), 13);
        } else if (msg == 15) {
            mplew.writeAsciiString("Male", 13);
            mplew.writeAsciiString("Female", 13);
            mplew.writeShort(0);
        }

        return mplew.getPacket();
    }

    public static byte[] sendWeddingGive() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnWeddingGiftResult.getValue());
        mplew.write(9);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] sendWeddingReceive() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnWeddingGiftResult.getValue());
        mplew.write(10);
        mplew.writeLong(-1L);
        mplew.writeInt(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] giveWeddingItem() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnWeddingGiftResult.getValue());
        mplew.write(11);
        mplew.write(0);
        mplew.writeLong(0L);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] receiveWeddingItem() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnWeddingGiftResult.getValue());
        mplew.write(15);
        mplew.writeLong(0L);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] sendCashPetFood(boolean success, byte index) {
        OutPacket mplew = new OutPacket(3 + (success ? 1 : 0));

        mplew.writeShort(SendPacketOpcode.OnCashPetFoodResult.getValue());
        mplew.write(success ? 0 : 1);
        if (success) {
            mplew.write(index);
        }

        return mplew.getPacket();
    }

    public static byte[] yellowChat(String msg) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnSetWeekEventMessage.getValue());
        mplew.write(-1);
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] OnSetPotionDiscountRate(int percent) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnSetPotionDiscountRate.getValue());
        mplew.write(percent);
        return mplew.getPacket();
    }

    public static byte[] catchMob(int mobid, int itemid, byte success) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnBridleMobCatchFail.getValue());
        mplew.write(success);
        mplew.writeInt(itemid);
        mplew.writeInt(mobid);

        return mplew.getPacket();
    }

    public static byte[] spawnPlayerNPC(PlayerNPC npc) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnImitatedNPCData.getValue());
        mplew.write(1);
        mplew.writeInt(npc.getId());
        mplew.writeMapleAsciiString(npc.getName());
        mplew.write(npc.getGender());
        mplew.write(npc.getSkin());
        mplew.writeInt(npc.getFace());
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(npc.getHair());
        Map<Short, Integer> equip = npc.getEquips();
        Map<Short, Integer> myEquip = new LinkedHashMap<>();
        Map<Short, Integer> maskedEquip = new LinkedHashMap<>();
        for (Entry<Short, Integer> position : equip.entrySet()) {
            short pos = (short) (position.getKey() * -1);
            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, position.getValue());
            } else if (pos > 100 && pos != 111) { // don't ask. o.o
                pos = (short) (pos - 100);
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, position.getValue());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, position.getValue());
            }
        }
        for (Entry<Short, Integer> entry : myEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF);
        for (Entry<Short, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF);
        Integer cWeapon = equip.get(Short.valueOf((short) -111));
        Integer weapon = equip.get(Short.valueOf((short) -11));
        Integer sWeapon = equip.get(Short.valueOf((short) -10));
        mplew.writeInt(cWeapon != null ? cWeapon : 0);
        mplew.writeInt(weapon != null ? weapon : 0);
        mplew.writeInt(sWeapon != null ? sWeapon : 0); // 데몬 쉴드, 미하일 쉴드, 메르세데스 쉴드, 카이저, 루미너스   
        mplew.write(GameConstants.isMercedes(npc.getJob()) ? 1 : 0);
        //펫부분
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(0);
        }
        /*
        int v9 = chr.getJob();
        if (v9 / 100 != 31 && v9 != 3001) 
        {
            if (v9 / 100 == 36 || v9 == 3002) {
                mplew.writeInt(chr.getDemonMarking());
            }
        }
        else
        {
            mplew.writeInt(chr.getDemonMarking()); // ?????????
        }
         */
        if (GameConstants.isDemon(npc.getJob()) || GameConstants.isXenon(npc.getJob()) || GameConstants.isDemonAvenger(npc.getJob())) {
            mplew.writeInt(npc.getDemonMarking());
        }
        mplew.writeInt(0); //얘도 아닌거같음 해보고팅기면 삭제 V.160
        return mplew.getPacket();
    }

    public static byte[] disabledNPC(List<Integer> ids) {
        OutPacket mplew = new OutPacket(3 + ids.size() * 4);

        mplew.writeShort(SendPacketOpcode.OnLimitedNPCDisableInfo.getValue());
        mplew.write(ids.size());
        for (Integer i : ids) {
            mplew.writeInt(i.intValue());
        }

        return mplew.getPacket();
    }

    public static byte[] MonsterBookSetCard(int itemID, int lv) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnMonsterBookSetCard.getValue());
        oPacket.EncodeByte(itemID > 0 ? 1 : 0);
        if (itemID > 0) {
            oPacket.EncodeInt(itemID);
            oPacket.EncodeInt(lv);
        }
        return oPacket.getPacket();
    }

    public static byte[] MonsterBookSetCover(int unk) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnMonsterBookSetCover.getValue());
        oPacket.EncodeInt(unk);
        return oPacket.getPacket();
    }

    public static byte[] MulungEnergy(int energy) {
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }

    public static byte[] sendPyramidEnergy(String type, String amount) {
        return sendString(1, type, amount);
    }

    public static byte[] sendGhostPoint(String type, String amount) {
        return sendString(2, type, amount);
    }

    public static byte[] sendGhostStatus(String type, String amount) {
        return sendString(3, type, amount);
    }

    public static byte[] sendString(int type, String object, String amount) {
        OutPacket mplew = new OutPacket();
        switch (type) {
            case 1:
                mplew.writeShort(SendPacketOpcode.OnSessionValue.getValue());
                break;
            case 2:
                mplew.writeShort(SendPacketOpcode.OnPartyValue.getValue());
                break;
            case 3:
                mplew.writeShort(SendPacketOpcode.OnFieldSetVariable.getValue());
                break;
            case 4:
                mplew.writeShort(SendPacketOpcode.OnEventVariable.getValue());
                break;
        }
        mplew.writeMapleAsciiString(object);
        mplew.writeMapleAsciiString(amount);
        return mplew.getPacket();
    }

    public static byte[] setDeathCount(String currentDeathCount, String maxDeathCount) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnEventVariable.getValue());
        oPacket.EncodeString(currentDeathCount);
        oPacket.EncodeString(maxDeathCount);
        return oPacket.getPacket();
    }

    public static byte[] fairyPendantMessage(int termStart, int incExpR) {
        OutPacket mplew = new OutPacket(14);

        mplew.writeShort(SendPacketOpcode.OnBonusExpRateChanged.getValue());
        mplew.writeInt(17);
        mplew.writeInt(0);

        mplew.writeInt(incExpR);

        return mplew.getPacket();
    }

    public static byte[] sendLevelup(boolean family, int level, String name) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnNotifyLevelUp.getValue());
        mplew.write(family ? 1 : 2);
        mplew.writeInt(level);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] sendMarriage(boolean family, String name) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnNotifyWedding.getValue());
        mplew.write(family ? 1 : 0);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] sendJobup(boolean family, int jobid, String name) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnNotifyJobChange.getValue());
        mplew.write(family ? 1 : 0);
        mplew.writeInt(jobid);
        mplew.writeMapleAsciiString(new StringBuilder().append(!family ? "> " : "").append(name).toString());

        return mplew.getPacket();
    }

    public static byte[] pendantSlot(boolean p) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnSetBuyEquipExt.getValue());
        mplew.write(p ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] followRequest(int chrid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnSetPassenserRequest.getValue());
        mplew.writeInt(chrid);

        return mplew.getPacket();
    }

    public static byte[] getTopMsg(String msg) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnScriptProgressMessage.getValue());
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    /* 115 */
    public static byte[] staticScreenMessage(String chatMsg, boolean fixMsg) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnStaticScreenMessageOn.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeString(chatMsg);
        oPacket.EncodeByte(!fixMsg);
        return oPacket.getPacket();
    }

    /* 116 */
    public static byte[] offStaticScreenMessage() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnStaticScreenMessageOff.getValue());
        return oPacket.getPacket();
    }

    public static byte[] updateJaguar(MapleCharacter from) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnWildHunterInfo.getValue());
        PacketHelper.addJaguarInfo(mplew, from);

        return mplew.getPacket();
    }

    public static byte[] pamSongUI() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnClearAnnouncedQuest.getValue());

        return mplew.getPacket();
    }

    public static byte[] ultimateExplorer() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnCreatePremiumAdeventurer.getValue());

        return mplew.getPacket();
    }

    public static byte[] professionInfo(String skillID, int type, int subType, boolean rightResult, int perc) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnResultInstanceTable.getValue());
        oPacket.EncodeString(skillID);
        oPacket.EncodeInt(type);
        oPacket.EncodeInt(subType);
        oPacket.EncodeByte(rightResult);
        oPacket.EncodeInt(perc);
        return oPacket.getPacket();
    }

    public static byte[] updateImpTime() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnCoolTimeSet.getValue());
        mplew.writeInt(0);
        mplew.writeLong(0L);
        return mplew.getPacket();
    }

    public static byte[] itemPotChanged(MapleImp imp, int mask, int index, boolean clear) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnItemPotChanged.getValue());
        oPacket.EncodeByte(clear ? 0 : 1);
        oPacket.EncodeInt(index + 1);
        oPacket.EncodeInt(mask);
        if ((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) {
            Pair i = MapleItemInformationProvider.getInstance().getPot(imp.getItemId());
            if (i == null) {
                return enableActions();
            }
            oPacket.EncodeInt(((Integer) i.left).intValue());
            oPacket.EncodeByte(imp.getLevel());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.STATE.getValue()) != 0)) {
            oPacket.EncodeByte(imp.getState());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS.getValue()) != 0)) {
            oPacket.EncodeInt(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS.getValue()) != 0)) {
            oPacket.EncodeInt(imp.getCloseness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS_LEFT.getValue()) != 0)) {
            oPacket.EncodeInt(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MINUTES_LEFT.getValue()) != 0)) {
            oPacket.EncodeInt(0);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.LEVEL.getValue()) != 0)) {
            oPacket.EncodeByte(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS_2.getValue()) != 0)) {
            oPacket.EncodeInt(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.UPDATE_TIME.getValue()) != 0)) {
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CREATE_TIME.getValue()) != 0)) {
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.AWAKE_TIME.getValue()) != 0)) {
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.SLEEP_TIME.getValue()) != 0)) {
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_CLOSENESS.getValue()) != 0)) {
            oPacket.EncodeInt(100);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_DELAY.getValue()) != 0)) {
            oPacket.EncodeInt(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_FULLNESS.getValue()) != 0)) {
            oPacket.EncodeInt(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_ALIVE.getValue()) != 0)) {
            oPacket.EncodeInt(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_MINUTES.getValue()) != 0)) {
            oPacket.EncodeInt(10);
        }
        oPacket.EncodeByte(0);
        return oPacket.getPacket();
    }

    public static byte[] getMulungRanks(ResultSet rs) throws SQLException {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnDojangRankingResult.getValue());
        /*
        v2  : Character Size
        v20 : Character Rank
        v5  : Character Name
        v22 : Clear Time
         */
        if (!rs.last()) {
            oPacket.EncodeInt(1);
            oPacket.EncodeShort(1);
            oPacket.EncodeString("");
            oPacket.EncodeLong(0);
            return oPacket.getPacket();
        }
        oPacket.EncodeInt(rs.getRow());
        rs.beforeFirst();
        int rank = 1;
        while (rs.next()) {
            oPacket.EncodeShort(rank);
            oPacket.EncodeString(rs.getString("name"));
            oPacket.EncodeLong(rs.getInt("time"));
            rank++;
        }
        return oPacket.getPacket();
    }

    public static byte[] getMulungMessage(boolean v2, String v1) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnShutDownMsg.getValue());
        /*
        v2 is Only False (Maybe...)
         */
        oPacket.EncodeByte(v2 ? 1 : 0);
        oPacket.EncodeString(v1);
        return oPacket.getPacket();
    }

    public static byte[] showCardDeck(int cardAmount) {
        OutPacket mplew = new OutPacket();
        //writer.writeShort(SendPacketOpcode.UPDATE_CARD.getValue());
        mplew.write(cardAmount);
        return mplew.getPacket();
    }

    public static byte[] sub_C0B3F0(int i, int i0, int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //For some reason this can only be called one time per session. 
    //It may throw script errors with GTops Ads.
    /*public static byte[] ingameVote() {
     OutPacket mplew = new OutPacket();
     mplew.write(HexTool.getByteArrayFromHexString("A2 00 01 00 07 7C 28 00 68 74 74 70 3a 2f 2f 77 77 77 2e 67 74 6f 70 31 30 30 2e 63 6f 6d 2f 69 6e 2e 70 68 70 3f 73 69 74 65 3d 58 58 58 58 58")); // REPLACE 58 58 58 58 with ID.
     return mplew.getPacket();
     }*/
    public static class AlliancePacket {

        public static byte[] getAllianceInfo(MapleGuildAlliance alliance) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(12);
            mplew.write(alliance == null ? 0 : 1);
            if (alliance != null) {
                addAllianceInfo(mplew, alliance);
            }

            return mplew.getPacket();
        }

        private static void addAllianceInfo(OutPacket mplew, MapleGuildAlliance alliance) {
            mplew.writeInt(alliance.getId());
            mplew.writeMapleAsciiString(alliance.getName());
            for (int i = 1; i <= 5; i++) {
                mplew.writeMapleAsciiString(alliance.getRank(i));
            }
            mplew.write(alliance.getNoGuilds());
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                mplew.writeInt(alliance.getGuildId(i));
            }
            mplew.writeInt(alliance.getCapacity());
            mplew.writeMapleAsciiString(alliance.getNotice());
        }

        public static byte[] getGuildAlliance(MapleGuildAlliance alliance) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(13);
            if (alliance == null) {
                mplew.writeInt(0);
                return mplew.getPacket();
            }
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            mplew.writeInt(noGuilds);
            for (MapleGuild gg : g) {
                CWvsContext.GuildPacket.getGuildInfo(mplew, gg);
            }
            return mplew.getPacket();
        }

        public static byte[] allianceMemberOnline(int alliance, int gid, int id, boolean online) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(14);
            mplew.writeInt(alliance);
            mplew.writeInt(gid);
            mplew.writeInt(id);
            mplew.write(online ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(16);
            addAllianceInfo(mplew, alliance);
            CWvsContext.GuildPacket.getGuildInfo(mplew, expelledGuild);
            mplew.write(expelled ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(18);
            addAllianceInfo(mplew, alliance);
            mplew.writeInt(newGuild.getId());
            CWvsContext.GuildPacket.getGuildInfo(mplew, newGuild);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] sendAllianceInvite(String allianceName, MapleCharacter inviter) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(3);
            mplew.writeInt(inviter.getGuildId());
            mplew.writeMapleAsciiString(inviter.getName());
            mplew.writeMapleAsciiString(allianceName);

            return mplew.getPacket();
        }

        public static byte[] getAllianceUpdate(MapleGuildAlliance alliance) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(23);
            addAllianceInfo(mplew, alliance);

            return mplew.getPacket();
        }

        public static byte[] createGuildAlliance(MapleGuildAlliance alliance) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(15);
            addAllianceInfo(mplew, alliance);
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            for (MapleGuild gg : g) {
                CWvsContext.GuildPacket.getGuildInfo(mplew, gg);
            }
            return mplew.getPacket();
        }

        public static byte[] updateAlliance(MapleGuildCharacter mgc, int allianceid) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(24);
            mplew.writeInt(allianceid);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getJobId());

            return mplew.getPacket();
        }

        public static byte[] updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(25);
            mplew.writeInt(allianceid);
            mplew.writeInt(oldLeader);
            mplew.writeInt(newLeader);

            return mplew.getPacket();
        }

        public static byte[] allianceRankChange(int aid, String[] ranks) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(26);
            mplew.writeInt(aid);
            for (String r : ranks) {
                mplew.writeMapleAsciiString(r);
            }

            return mplew.getPacket();
        }

        public static byte[] updateAllianceRank(MapleGuildCharacter mgc) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(27);
            mplew.writeInt(mgc.getId());
            mplew.write(mgc.getAllianceRank());

            return mplew.getPacket();
        }

        public static byte[] changeAllianceNotice(int allianceid, String notice) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(28);
            mplew.writeInt(allianceid);
            mplew.writeMapleAsciiString(notice);

            return mplew.getPacket();
        }

        public static byte[] disbandAlliance(int alliance) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(29);
            mplew.writeInt(alliance);

            return mplew.getPacket();
        }

        public static byte[] changeAlliance(MapleGuildAlliance alliance, boolean in) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(1);
            mplew.write(in ? 1 : 0);
            mplew.writeInt(in ? alliance.getId() : 0);
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < noGuilds; i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            mplew.write(noGuilds);
            for (int i = 0; i < noGuilds; i++) {
                mplew.writeInt(g[i].getId());

                Collection<MapleGuildCharacter> members = g[i].getMembers();
                mplew.writeInt(members.size());
                for (MapleGuildCharacter mgc : members) {
                    mplew.writeInt(mgc.getId());
                    mplew.write(in ? mgc.getAllianceRank() : 0);
                }
            }

            return mplew.getPacket();
        }

        public static byte[] changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(2);
            mplew.writeInt(allianceid);
            mplew.writeInt(oldLeader);
            mplew.writeInt(newLeader);

            return mplew.getPacket();
        }

        public static byte[] changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, boolean add) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(4);
            mplew.writeInt(add ? alliance.getId() : 0);
            mplew.writeInt(guild.getId());
            Collection<MapleGuildCharacter> members = guild.getMembers();
            mplew.writeInt(members.size());
            for (MapleGuildCharacter mgc : members) {
                mplew.writeInt(mgc.getId());
                mplew.write(add ? mgc.getAllianceRank() : 0);
            }

            return mplew.getPacket();
        }

        public static byte[] changeAllianceRank(int allianceid, MapleGuildCharacter player) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnAllianceResult.getValue());
            mplew.write(5);
            mplew.writeInt(allianceid);
            mplew.writeInt(player.getId());
            mplew.writeInt(player.getAllianceRank());

            return mplew.getPacket();
        }
    }

    public static class FamilyPacket {

        public static byte[] getFamilyData() {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFamilyPrivilegeList.getValue());
            MapleFamilyBuff[] entries = MapleFamilyBuff.values();
            mplew.writeInt(entries.length);
            for (MapleFamilyBuff entry : entries) {
                mplew.write(entry.type);
                mplew.writeInt(entry.rep);
                mplew.writeInt(1);
                mplew.writeMapleAsciiString(entry.name);
                mplew.writeMapleAsciiString(entry.desc);
            }
            return mplew.getPacket();
        }

        public static byte[] getFamilyInfo(final MapleCharacter user) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.OnFamilyInfoResult.getValue());
            oPacket.EncodeInt(user.getCurrentRep());
            oPacket.EncodeInt(user.getTotalRep());
            oPacket.EncodeInt(user.getTodayRep());
            oPacket.EncodeShort(user.getNoJuniors());
            oPacket.EncodeShort(2);
            oPacket.EncodeShort(user.getNoJuniors());
            MapleFamily getFamily = World.Family.getFamily(user.getFamilyId());
            if (getFamily != null) {
                oPacket.EncodeInt(getFamily.getLeaderId());
                oPacket.EncodeString(getFamily.getLeaderName());
                oPacket.EncodeString(getFamily.getNotice());
            } else {
                oPacket.EncodeLong(0L);
            }
            List b = user.usedBuffs();
            oPacket.EncodeInt(b.size());
            for (Iterator i$ = b.iterator(); i$.hasNext();) {
                int ii = ((Integer) i$.next()).intValue();
                oPacket.EncodeInt(ii);
                oPacket.EncodeInt(user.getFamilyBuffDayByDay(ii));
            }
            return oPacket.getPacket();
        }

        public static void addFamilyCharInfo(MapleFamilyCharacter ldr, OutPacket mplew) {
            mplew.writeInt(ldr.getId());
            mplew.writeInt(ldr.getSeniorId());
            mplew.writeShort(ldr.getJobId());
            mplew.writeShort(0);
            mplew.write(ldr.getLevel());
            mplew.write(ldr.isOnline() ? 1 : 0);

            mplew.writeInt(ldr.getCurrentRep());
            mplew.writeInt(ldr.getTotalRep());
            mplew.writeInt(ldr.getTodayRep());//오늘 엘더에게 적립해준 명성도
            mplew.writeInt(ldr.getTotalRep());

            /*   for (int i=1; i<=4; i++) {
                mplew.writeInt(i);
            }*/
            mplew.writeInt(Math.max(ldr.getChannel(), 0));
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(ldr.getName());
        }

        public static byte[] getFamilyPedigree(MapleCharacter chr) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFamilyChartResult.getValue());
            mplew.writeInt(chr.getId());
            MapleFamily family = World.Family.getFamily(chr.getFamilyId());

            int gens = 0;
            int generations = 0;
            if (family == null) {
                mplew.writeInt(2);
                addFamilyCharInfo(new MapleFamilyCharacter(chr, 0, 0, 0, 0), mplew);
            } else {
                mplew.writeInt(family.getMFC(chr.getId()).getPedigree().size() + 1);
                addFamilyCharInfo(family.getMFC(family.getLeaderId()), mplew);

                if (chr.getSeniorId() > 0) {
                    MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                    if (senior != null) {
                        if (senior.getSeniorId() > 0) {
                            addFamilyCharInfo(family.getMFC(senior.getSeniorId()), mplew);
                        }
                        addFamilyCharInfo(senior, mplew);
                    }
                }
            }
            addFamilyCharInfo(chr.getMFC() == null ? new MapleFamilyCharacter(chr, 0, 0, 0, 0) : chr.getMFC(), mplew);
            if (family != null) {
                if (chr.getSeniorId() > 0) {
                    MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                    if (senior != null) {
                        if ((senior.getJunior1() > 0) && (senior.getJunior1() != chr.getId())) {
                            addFamilyCharInfo(family.getMFC(senior.getJunior1()), mplew);
                        } else if ((senior.getJunior2() > 0) && (senior.getJunior2() != chr.getId())) {
                            addFamilyCharInfo(family.getMFC(senior.getJunior2()), mplew);
                        }

                    }

                }

                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        addFamilyCharInfo(junior, mplew);
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        addFamilyCharInfo(junior, mplew);
                    }
                }
                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior1()), mplew);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior2()), mplew);
                        }
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior1()), mplew);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior2()), mplew);
                        }
                    }
                }
                generations = family.getMemberSize();
            }
            mplew.writeLong(gens);
            mplew.writeInt(0);
            mplew.writeInt(-1);
            mplew.writeInt(generations);

            if (family != null) {
                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            mplew.writeInt(junior.getJunior1());
                            mplew.writeInt(family.getMFC(junior.getJunior1()).getDescendants());
                        } else {
                            mplew.writeInt(0);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            mplew.writeInt(junior.getJunior2());
                            mplew.writeInt(family.getMFC(junior.getJunior2()).getDescendants());
                        } else {
                            mplew.writeInt(0);
                        }
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            mplew.writeInt(junior.getJunior1());
                            mplew.writeInt(family.getMFC(junior.getJunior1()).getDescendants());
                        } else {
                            mplew.writeInt(0);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            mplew.writeInt(junior.getJunior2());
                            mplew.writeInt(family.getMFC(junior.getJunior2()).getDescendants());
                        } else {
                            mplew.writeInt(0);
                        }
                    }
                }
            }

            List b = chr.usedBuffs();
            mplew.writeInt(b.size());
            for (Iterator i$ = b.iterator(); i$.hasNext();) {
                int ii = ((Integer) i$.next()).intValue();
                mplew.writeInt(ii);
                mplew.writeInt(1);
            }
            mplew.writeShort(2);
            return mplew.getPacket();
        }

        public static byte[] getFamilyMsg(int a2, int v3) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFamilyResult.getValue());
            /*
            01 : %s님과 결별했습니다.\r\n패밀리 관계가 끊어졌습니다.
            64 : 주니어로 등록할 수 없는 캐릭터입니다.
            65 : 접속 중인 캐릭터가 아니거나 정확한 이름이 아닙니다.
            66 : 같은 패밀리입니다.
            67 : 같은 패밀리가 아닙니다.
            69 : 같은 맵에 있는 캐릭터만 주니어로\r\n등록할 수 있습니다.
            70 : 이미 다른 캐릭터의 주니어입니다.
            71 : 자신보다 레벨이 낮은 캐릭터만\r\n주니어로 등록할 수 있습니다.
            72 : 레벨 차이가 20이 넘어가면 주니어로\r\n등록할 수 없습니다.
            73 : 다른 캐릭터가 주니어 등록을 요청한 상태입니다.\r\n잠시 후 다시 시도해보세요.
            74 : 다른 캐릭터가 이미 소환을 요청한 상태입니다.\r\n잠시 후 다시 시도해보세요.
            75 : 소환에 실패했습니다. 소환할 수 없는 곳에 있거나 소환가능한 상태가 아닙니다.
            76 : 주니어를 등록할 수 없습니다. 패밀리의 규모는 위아래로 1000단계를 넘을 수 없습니다.
            77 : 레벨 10 이상의 캐릭터만 주니어로\r\n등록할 수 있습니다.
            78 : 메소가 부족하여 결별할 수 없습니다.\r\n엘더와 결별하기 위해서는\r\n%d메소가 필요합니다.
            79 : 메소가 부족하여 결별할 수 없습니다.\r\n주니어와 결별하기 위해서는\r\n%d메소가 필요합니다.
            80 : 해당 지역과 레벨이 맞지 않아 특권을 사용할 수 없습니다
             */
            mplew.writeInt(a2);
            if (a2 == 78 || a2 == 79) {
                mplew.writeInt(v3);
            }
            return mplew.getPacket();
        }

        public static byte[] sendFamilyInvite(int cid, int otherLevel, int otherJob, String inviter) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFamilyJoinRequest.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(otherLevel);
            mplew.writeInt(otherJob);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(inviter);
            return mplew.getPacket();
        }

        public static byte[] sendFamilyJoinResponse(boolean accepted, String added) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFamilyJoinRequestResult.getValue());
            /*
            true    : %s님을 주니어로 등록했습니다. 최선을 다해 주니어의 게임 플레이를 도와주세요.
            false   : %s님이 주니어 등록을 거절하셨습니다.
             */
            mplew.write(accepted ? 1 : 0);
            mplew.writeMapleAsciiString(added);
            return mplew.getPacket();
        }

        public static byte[] getSeniorMessage(String name) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFamilyJoinAccepted.getValue());
            /*
            %s님이 엘더가 되었습니다. 게임 진행에 여러 가지 도움을 받을 수 있을 것 같습니다.
             */
            mplew.writeMapleAsciiString(name);
            return mplew.getPacket();
        }

        public static byte[] changeRep(int v2, String name) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFamilyFamousPointIncResult.getValue());
            /*
            v2 < 0  : 명성도를 %d만큼 소비하였습니다.
            v2 > 0  : 명성도가 올랐습니다 (%s, +%d)
             */
            mplew.writeInt(v2);
            mplew.writeMapleAsciiString(name);
            return mplew.getPacket();
        }

        public static byte[] familyLoggedIn(boolean online, String name) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFamilyNotifyLoginOrLogout.getValue());
            mplew.write(online ? 1 : 0);
            mplew.writeMapleAsciiString(name);
            return mplew.getPacket();
        }

        public static byte[] familyBuff(int type, int buffnr, int amount, int time) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFamilySetPrivilege.getValue());
            mplew.write(type);
            if ((type >= 2) && (type <= 4)) {
                mplew.writeInt(buffnr);
                mplew.writeInt(type == 3 ? 0 : amount);
                mplew.writeInt(type == 2 ? 0 : amount);
                mplew.write(0);
                mplew.writeInt(time);
            }
            return mplew.getPacket();
        }

        public static byte[] cancelFamilyBuff() {
            return familyBuff(0, 0, 0, 0);
        }

        public static byte[] familySummonRequest(String name, String mapname) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFamilySummonRequest.getValue());
            mplew.writeMapleAsciiString(name);
            mplew.writeMapleAsciiString(mapname);
            return mplew.getPacket();
        }
    }

    public static class BuddylistPacket {

        public static byte[] updateBuddylist(Collection<BuddylistEntry> buddylist) {
            return updateBuddylist(buddylist, 7);
        }

        public static byte[] updateBuddylist(Collection<BuddylistEntry> buddylist, int deleted) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFriendResult.getValue());
            mplew.write(deleted);
            mplew.write(buddylist.size());
            for (BuddylistEntry buddy : buddylist) {
                mplew.writeInt(buddy.getCharacterId());
                mplew.writeAsciiString(buddy.getName(), 13);
                mplew.write(buddy.isVisible() ? 0 : 1);
                mplew.writeInt(buddy.getChannel() == -1 ? -1 : buddy.getChannel() - 1);
                mplew.writeAsciiString(buddy.getGroup(), 17);
            }
            for (int x = 0; x < buddylist.size(); x++) {
                mplew.writeInt(0);
            }

            return mplew.getPacket();
        }

        public static byte[] updateBuddyChannel(int characterid, int channel) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnFriendResult.getValue());
            mplew.write(20);
            mplew.writeInt(characterid);
            mplew.write(0);
            mplew.writeInt(channel);
            return mplew.getPacket();
        }

        public static byte[] requestBuddylistAdd(int cidFrom, String nameFrom, int levelFrom, int jobFrom) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnFriendResult.getValue());
            mplew.write(9);
            mplew.writeInt(cidFrom);
            mplew.writeMapleAsciiString(nameFrom);
            mplew.writeInt(levelFrom);
            mplew.writeInt(jobFrom);
            mplew.writeInt(0);//v115
            mplew.writeInt(cidFrom);
            mplew.writeAsciiString(nameFrom, 13);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeAsciiString("그룹미지정", 16);
            mplew.writeShort(1);

            return mplew.getPacket();
        }

        public static byte[] updateBuddyCapacity(int capacity) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.OnFriendResult.getValue());
            oPacket.EncodeByte(21);
            oPacket.EncodeByte(capacity);
            return oPacket.getPacket();
        }

        public static byte[] buddylistMessage(byte message) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnFriendResult.getValue());
            mplew.write(message);

            return mplew.getPacket();
        }
    }

    public static byte[] giveKilling(int x) { // 잘모르겟음
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
        PacketHelper.writeSingleMask(mplew, CharacterTemporaryStat.KillingPoint);
        mplew.writeShort(0); //??
        mplew.write(0);
        mplew.writeInt(x);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] GiveFlipTheCoin(int buffid, int bufflength, List<Pair<CharacterTemporaryStat, Integer>> statups, MapleCharacter chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
        PacketHelper.writeBuffMask(mplew, statups);
        //sort해주기 귀찮아서 걍 인트처리 -ㅅ- ... by몽키프
        mplew.writeShort(chr.currentFTC());
        mplew.writeInt(buffid);
        mplew.writeInt(bufflength);

        mplew.writeZeroBytes(3); // 
        mplew.writeInt(1);
        mplew.writeInt(buffid);
        mplew.writeLong(5 * chr.currentFTC());//데미지증가량
        mplew.writeInt(bufflength);

        mplew.writeInt(1);
        mplew.writeInt(buffid);
        mplew.writeLong(1000000 * chr.currentFTC());//맥뎀증가량
        mplew.writeInt(bufflength);

        mplew.writeInt(1);
        mplew.writeInt(buffid);
        mplew.writeLong(5 * chr.currentFTC());//크리티컬증가량
        mplew.writeInt(bufflength);

        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeLong(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] GiveUnityOfPower(int buffid, int bufflength, List<Pair<CharacterTemporaryStat, Integer>> statups, MapleCharacter chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
        PacketHelper.writeBuffMask(mplew, statups);
        //sort해주기 귀찮아서 걍 인트처리 -ㅅ- ... by몽키프
        mplew.writeShort(chr.currentFTC());
        mplew.writeInt(buffid);
        mplew.writeInt(bufflength);
        mplew.writeZeroBytes(3); // 변경완료
        mplew.writeInt(1);
        mplew.writeInt(buffid);
        mplew.writeLong(3000000 * chr.currentFTC());//맥뎀증가량
        mplew.writeInt(bufflength);
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeLong(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static class ExpeditionPacket {

        public static byte[] expeditionStatus(MapleExpedition me, boolean created, boolean silent) { //확실한지아닌지 잘모르겠음
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnExpedtionResult.getValue());
            mplew.write(created ? 73 : silent ? 71 : 75);
            mplew.writeInt(me.getType().exped);
            mplew.writeInt(0);
            for (int i = 0; i < 6; i++) {
                if (i < me.getParties().size()) {
                    MapleParty party = World.Party.getParty(((Integer) me.getParties().get(i)).intValue());
                    CWvsContext.PartyPacket.addPartyStatus(-1, party, mplew, false, true);
                } else {
                    CWvsContext.PartyPacket.addPartyStatus(-1, null, mplew, false, true);
                }

            }

            return mplew.getPacket();
        }

        public static byte[] expeditionError(int errcode, String name) { //인트스트링확실
            OutPacket mplew = new OutPacket();

            // 0 : '<Name>' could not be found in the current server.
            // 1 : Admins can only invite other admins.
            // 2 : '<Name>' is already in a party.
            // 3 : '<Name>' does not meet the level requirement for the expedition.
            // 4 : '<Name>' is currently not accepting any expedition invites.
            // 5 : '<Name>' is taking care of another invitation.
            // 6 : You have already invited '<Name>' to the expedition.
            // 7 : '<Name>' has been invited to the expedition.
            mplew.writeShort(SendPacketOpcode.OnExpedtionResult.getValue());
            mplew.write(87);
            mplew.writeInt(errcode);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] expeditionMessage(int code) {
            OutPacket mplew = new OutPacket();

            // 73 : Silent remove
            // 74 : A new expedition has been created
            // 75 : '<Name>' has joined the expedition.
            // 76 : You have joined the expedition.
            // 77 : You have joined the expedition.
            // 79 : '<Name>' has left the expedition.
            // 80 : You have left the expedition.
            // 81 : '<Name>' has been kicked out of the expedition
            // 82 : You have been kicked out of the expedition.
            // 83 : The Expedition has been disbanded.
            // 89 : You cannot create a Cygnus Expedition because you have reached the limit on Cygnus Clear Points.\r\nCygnus can be defeated up to 3 times a week, and the Clear
            // 90 : You cannot invite this character to a Cygnus Expedition because he has reached the limit on Cygnus Clear Points. Cygnus can be defeated up to 3 times a week, and the Clear Points reset on Wednesdays at midnight.
            // 91 : You cannot join the Cygnus Expedition because you have reached the limit on Cygnus Clear Points.\r\nCygnus can be defeated up to 3 times a week, and the Clear Points reset on Wednesdays at midnight.
            mplew.writeShort(SendPacketOpcode.OnExpedtionResult.getValue());
            mplew.write(code);

            return mplew.getPacket();
        }

        public static byte[] expeditionJoined(String name) { //확실
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnExpedtionResult.getValue());
            mplew.write(74);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] expeditionLeft(String name) { //확실?
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnExpedtionResult.getValue());
            mplew.write(78);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] expeditionKick(String name) { //확실?
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnExpedtionResult.getValue());
            mplew.write(80);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] expeditionLeaderChanged(int newLeader) { //확실
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnExpedtionResult.getValue());
            mplew.write(83);
            mplew.writeInt(newLeader);

            return mplew.getPacket();
        }

        public static byte[] expeditionUpdate(int partyIndex, MapleParty party) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnExpedtionResult.getValue());
            mplew.write(84);
            mplew.writeInt(0);
            mplew.writeInt(partyIndex);

            CWvsContext.PartyPacket.addPartyStatus(-1, party, mplew, false, false);

            return mplew.getPacket();
        }

        public static byte[] expeditionInvite(MapleCharacter from, int exped) { //확실
            OutPacket mplew = new OutPacket();
            //81 00 87 52 00 00 00 41 01 00 00 06 00 4D 61 6B 69 6E 61 D2 07 00 00 00 00 00 00]
            //81 00 Header.
            //87 Mode?
            //52 00 00 00 = Level (82d)
            //41 01 00 00 = Job (321d)
            //06 00 4d 61 6b 69 6e 61 = name, (6 length, Makina)
            // d2 07 00 00  = Exped
            // 00 00 00 00
            mplew.writeShort(SendPacketOpcode.OnExpedtionResult.getValue());
            mplew.write(86);
            mplew.writeInt(from.getLevel());
            mplew.writeInt(from.getJob());
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(from.getName());
            mplew.writeInt(exped);
            return mplew.getPacket();
        }
    }

    public static class PartyPacket {

        public static byte[] partyCreated(int partyid) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.OnPartyResult.getValue());
            oPacket.EncodeByte(PartyType.PartyReq_CreateParty.getVal());
            oPacket.EncodeInt(partyid);
            MapleParty party = World.Party.getParty(partyid);
            oPacket.EncodeInt(party.getLeader().getDoorTown());
            oPacket.EncodeInt(party.getLeader().getDoorTarget());
            oPacket.EncodeInt(0);
            oPacket.EncodeShort(party.getLeader().getDoorPosition().x);
            oPacket.EncodeShort(party.getLeader().getDoorPosition().y);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(1);
            return oPacket.getPacket();
        }

        public static byte[] partyInvite(MapleCharacter from) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.OnPartyResult.getValue());
            oPacket.EncodeByte(PartyType.PartyReq_InviteParty.getVal());
            oPacket.EncodeInt(from.getParty() == null ? 0 : from.getParty().getId());
            oPacket.EncodeString(from.getName());
            oPacket.EncodeInt(from.getLevel());
            oPacket.EncodeInt(from.getJob());
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);
            return oPacket.getPacket();
        }

        public static byte[] partyRequestInvite(MapleCharacter from) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.OnPartyResult.getValue());
            oPacket.EncodeByte(PartyType.PartyReq_ApplyParty.getVal());
            oPacket.EncodeInt(from.getId());
            oPacket.EncodeString(from.getName());
            oPacket.EncodeInt(from.getLevel());
            oPacket.EncodeInt(from.getJob());
            oPacket.EncodeInt(0);
            return oPacket.getPacket();
        }

        public static byte[] partyStatusMessage(int message, String charname) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnPartyResult.getValue());
            mplew.write(message);
            if ((message == 26) || (message == 52)) {
                mplew.writeMapleAsciiString(charname);
            } else if (message == 45) {
                mplew.write(0);
            }
            return mplew.getPacket();
        }

        public static void addPartyStatus(int forchannel, MapleParty party, OutPacket lew, boolean leaving) {
            addPartyStatus(forchannel, party, lew, leaving, false);
        }

        public static void addPartyStatus(int forchannel, MapleParty party, OutPacket lew, boolean leaving, boolean exped) {
            List<MaplePartyCharacter> partymembers;
            if (party == null) {
                partymembers = new ArrayList();
            } else {
                partymembers = new ArrayList(party.getMembers());
            }
            while (partymembers.size() < 6) {
                partymembers.add(new MaplePartyCharacter());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(partychar.getId());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                String showDeathCount = "";
                if (MapConstants.isDeathCountMap(partychar.getMapid()) > -1) {
                    MapleCharacter pMember = ChannelServer.getInstance(partychar.getChannel()).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (pMember != null) {
                        showDeathCount = " (" + pMember.getDeathCount() + ")";
                    }
                }
                lew.writeAsciiString(showDeathCount + partychar.getName(), 13);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(partychar.getJobId());
            }

            for (MaplePartyCharacter partychar : partymembers) {
                if (partychar.isOnline()) {
                    lew.writeInt(1);
                } else {
                    lew.writeInt(0);
                }
            }

            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(partychar.getLevel());
            }

            for (MaplePartyCharacter partychar : partymembers) {
                if (partychar.isOnline()) {
                    lew.writeInt(partychar.getChannel() - 1);
                } else {
                    lew.writeInt(-2);
                }
            }

            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(0);
            }

            lew.writeInt(party == null ? 0 : party.getLeader().getId());
            if (exped) {
                return;
            }

            for (MaplePartyCharacter partychar : partymembers) {
                if (partychar.getChannel() == forchannel) {
                    lew.writeInt(partychar.getMapid());
                } else {
                    lew.writeInt(0);
                }
            }
            for (MaplePartyCharacter partychar : partymembers) {
                if (partychar.getChannel() == forchannel && !leaving) {
                    lew.writeInt(partychar.getDoorTown());
                    lew.writeInt(partychar.getDoorTarget());
                    lew.writeInt(partychar.getDoorSkill());
                    lew.writeInt(partychar.getDoorPosition().x);
                    lew.writeInt(partychar.getDoorPosition().y);
                } else {
                    lew.writeInt(leaving ? 999999999 : 0);
                    lew.writeInt(leaving ? 999999999 : 0);
                    lew.writeInt(0);
                    lew.writeInt(-1);
                    lew.writeInt(-1);
                }
            }
            for (MaplePartyCharacter partychar : partymembers) {
                if (partychar.getId() > 0) {
                    lew.writeInt(255);
                } else {
                    lew.writeInt(0);
                }
            }
            for (int i = 0; i < 4; i++) {
                lew.writeLong(0);
            }
        }

        public static byte[] updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnPartyResult.getValue());
            switch (op) {
                case DISBAND:
                case EXPEL:
                case LEAVE:
                    mplew.write(16);
                    mplew.writeInt(party.getId());
                    mplew.writeInt(target.getId());
                    mplew.write(op == PartyOperation.DISBAND ? 0 : 1);
                    if (op == PartyOperation.DISBAND) {
                        mplew.writeInt(target.getId());
                    } else {
                        mplew.write(op == PartyOperation.EXPEL ? 1 : 0);
                        mplew.writeMapleAsciiString(target.getName());
                        addPartyStatus(forChannel, party, mplew, op == PartyOperation.LEAVE);
                    }
                    break;
                case JOIN:
                    mplew.write(19);
                    mplew.writeInt(party.getId());
                    mplew.writeMapleAsciiString(target.getName());
                    addPartyStatus(forChannel, party, mplew, false);
                    break;
                case SILENT_UPDATE:
                case LOG_ONOFF:
                    mplew.write(11);
                    mplew.writeInt(party.getId());
                    addPartyStatus(forChannel, party, mplew, op == PartyOperation.LOG_ONOFF);
                    break;
                case CHANGE_LEADER:
                case CHANGE_LEADER_DC:
                    mplew.write(35);
                    mplew.writeInt(target.getId());
                    mplew.write(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
                    break;
            }
            return mplew.getPacket();
        }

        public static byte[] partyPortal(int townId, int targetId, int skillId, Point position, boolean animation) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.OnPartyResult.getValue());
            oPacket.EncodeByte(PartyType.PartyReq_CreatePortal.getVal());
            oPacket.EncodeByte(animation ? 0 : 1);
            oPacket.EncodeInt(townId);
            oPacket.EncodeInt(targetId);
            oPacket.EncodeInt(skillId);
            oPacket.writePos(position);
            return oPacket.getPacket();
        }

        public static byte[] getPartyListing(PartySearchType pst) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnPartyResult.getValue());
            mplew.write(147);
            mplew.writeInt(pst.id);
            final List<PartySearch> parties = World.Party.searchParty(pst);
            mplew.writeInt(parties.size());
            for (PartySearch party : parties) {
                mplew.writeInt(0);
                mplew.writeInt(2);
                if (pst.exped) {
                    MapleExpedition me = World.Party.getExped(party.getId());
                    mplew.writeInt(me.getType().maxMembers);
                    mplew.writeInt(party.getId());
                    mplew.writeAsciiString(party.getName(), 48);
                    for (int i = 0; i < 5; i++) {
                        if (i < me.getParties().size()) {
                            MapleParty part = World.Party.getParty(((Integer) me.getParties().get(i)).intValue());
                            if (part != null) {
                                addPartyStatus(-1, part, mplew, false, true);
                            } else {
                                mplew.writeZeroBytes(202);
                            }
                        } else {
                            mplew.writeZeroBytes(202);
                        }
                    }
                } else {
                    mplew.writeInt(0);
                    mplew.writeInt(party.getId());
                    mplew.writeAsciiString(party.getName(), 48);
                    addPartyStatus(-1, World.Party.getParty(party.getId()), mplew, false, true);
                }

                mplew.writeShort(0);
            }
            System.out.println("getPartlyListing : " + mplew.toString());
            return mplew.getPacket();
        }

        public static byte[] partyListingAdded(PartySearch ps) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnPartyResult.getValue());
            mplew.write(93);
            mplew.writeInt(ps.getType().id);
            mplew.writeInt(0);
            mplew.writeInt(1);
            if (ps.getType().exped) {
                MapleExpedition me = World.Party.getExped(ps.getId());
                mplew.writeInt(me.getType().maxMembers);
                mplew.writeInt(ps.getId());
                mplew.writeAsciiString(ps.getName(), 48);
                for (int i = 0; i < 5; i++) {
                    if (i < me.getParties().size()) {
                        MapleParty party = World.Party.getParty(((Integer) me.getParties().get(i)).intValue());
                        if (party != null) {
                            addPartyStatus(-1, party, mplew, false, true);
                        } else {
                            mplew.writeZeroBytes(202);
                        }
                    } else {
                        mplew.writeZeroBytes(202);
                    }
                }
            } else {
                mplew.writeInt(0);
                mplew.writeInt(ps.getId());
                mplew.writeAsciiString(ps.getName(), 48);
                addPartyStatus(-1, World.Party.getParty(ps.getId()), mplew, false, true);
            }
            mplew.writeShort(0);
            System.out.println("PartlyListingadded : " + mplew.toString());
            return mplew.getPacket();
        }

        public static byte[] showMemberSearch(List<MapleCharacter> chr) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnPartyMemberCandidateResult.getValue());
            mplew.write(chr.size());
            for (MapleCharacter c : chr) {
                mplew.writeInt(c.getId());
                mplew.writeMapleAsciiString(c.getName());
                mplew.writeShort(c.getJob());
                mplew.writeShort(0);//unknown
                mplew.write(c.getLevel());
            }
            return mplew.getPacket();
        }

        public static byte[] showPartySearch(List<MapleParty> chr) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnPartyCandidateResult.getValue());
            mplew.write(chr.size());
            for (MapleParty c : chr) {
                mplew.writeInt(c.getId());
                mplew.writeMapleAsciiString(c.getLeader().getName());
                mplew.write(c.getLeader().getLevel());
                mplew.write(c.getLeader().isOnline() ? 1 : 0);
                mplew.write(c.getMembers().size());
                for (MaplePartyCharacter ch : c.getMembers()) {
                    mplew.writeInt(ch.getId());
                    mplew.writeMapleAsciiString(ch.getName());
                    mplew.writeShort(ch.getJobId());
                    mplew.writeShort(0); //Unknown
                    mplew.write(ch.getLevel());
                    mplew.write(ch.isOnline() ? 1 : 0);
                }
            }
            return mplew.getPacket();
        }

        public static byte[] recievePQrewardFail(byte celino) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnPartyResult.getValue());
            mplew.write(62);
            //mplew.write(celino);

            return mplew.getPacket();
        }

        public static byte[] recievePQrewardSuccess(byte box, byte celino) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnPartyResult.getValue());
            mplew.write(49);

            mplew.write(0);
            mplew.writeInt(1002140);
            mplew.write(1);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.writeShort(0);

            mplew.write(1);
            mplew.writeInt(1432082);
            mplew.write(0);//잠재
            mplew.writeShort(1);
            mplew.writeShort(2);
            mplew.writeShort(3);

            mplew.write(2);
            mplew.writeInt(1432082);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.writeShort(0);

            mplew.write(3);
            mplew.writeInt(2028009);
            /*mplew.write(celino);
         mplew.writeShort(celino);
         mplew.writeShort(celino);
         mplew.writeShort(celino);*/

            mplew.write(4);
            mplew.writeInt(5530068);
            /* mplew.write(celino);
         mplew.writeShort(celino);
         mplew.writeShort(celino);
         mplew.writeShort(celino);*/

            mplew.write(5);
            mplew.writeInt(2430144);
            /*mplew.write(celino);
         mplew.writeShort(celino);
         mplew.writeShort(celino);
         mplew.writeShort(celino);*/

            return mplew.getPacket();
        }
    }

    public static class GuildPacket {

        public static byte[] guildInvite(int gid, String charName, int levelFrom, int jobFrom) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(5);
            mplew.writeInt(gid);
            mplew.writeMapleAsciiString(charName);
            mplew.writeInt(levelFrom);
            mplew.writeInt(jobFrom);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] showGuildInfo(MapleCharacter c) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(32);
            if ((c == null) || (c.getMGC() == null)) {
                mplew.write(0);
                return mplew.getPacket();
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) {
                mplew.write(0);
                return mplew.getPacket();
            }
            mplew.write(1);
            getGuildInfo(mplew, g);

            return mplew.getPacket();
        }

        public static void getGuildInfo(OutPacket mplew, MapleGuild guild) {
            mplew.writeInt(guild.getId());
            mplew.writeMapleAsciiString(guild.getName());
            for (int i = 1; i <= 5; i++) {
                mplew.writeMapleAsciiString(guild.getRankTitle(i));
            }
            guild.addMemberData(mplew);
            mplew.writeInt(guild.getCapacity());
            mplew.writeShort(guild.getLogoBG());
            mplew.write(guild.getLogoBGColor());
            mplew.writeShort(guild.getLogo());
            mplew.write(guild.getLogoColor());
            mplew.writeMapleAsciiString(guild.getNotice());
            mplew.writeInt(guild.getGP());
            mplew.writeInt(guild.getGP());
            mplew.writeInt(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);
            mplew.write(guild.getLevel());
            mplew.writeShort(0);
            mplew.writeShort(guild.getSkills().size());
            for (MapleGuildSkill i : guild.getSkills()) {
                mplew.writeInt(i.skillID);
                mplew.writeShort(i.level);
                if ((PacketHelper.getTime(i.timestamp) / 60000L) < System.currentTimeMillis()) {
                    mplew.writeLong(0);
                } else {
                    mplew.writeLong(PacketHelper.getTime(i.timestamp));
                }
                mplew.writeMapleAsciiString(i.purchaser);
                mplew.writeMapleAsciiString(i.activator);
            }
        }

        public static byte[] newGuildInfo(MapleCharacter c) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(38);
            if ((c == null) || (c.getMGC() == null)) {
                return genericGuildMessage((byte) 37);
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) {
                return genericGuildMessage((byte) 37);
            }
            getGuildInfo(mplew, g);

            return mplew.getPacket();
        }

        public static byte[] newGuildMember(MapleGuildCharacter mgc) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(45);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeAsciiString(mgc.getName(), 13);
            mplew.writeInt(mgc.getJobId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getGuildRank());
            mplew.writeInt(mgc.isOnline() ? 1 : 0);
            mplew.writeInt(mgc.getAllianceRank());
            mplew.writeInt(mgc.getGuildContribution());

            return mplew.getPacket();
        }

        public static byte[] memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(bExpelled ? 50 : 53);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeMapleAsciiString(mgc.getName());

            return mplew.getPacket();
        }

        public static byte[] guildDisband(int gid) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(56);
            mplew.writeInt(gid);
            mplew.write(1);

            return mplew.getPacket();
        }

        public static byte[] guildCapacityChange(int gid, int capacity) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(64);
            mplew.writeInt(gid);
            mplew.write(capacity);

            return mplew.getPacket();
        }

        public static byte[] guildContribution(int gid, int cid, int c) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(72);
            mplew.writeInt(gid);
            mplew.writeInt(cid);
            mplew.writeInt(c);

            return mplew.getPacket();
        }

        public static byte[] changeRank(MapleGuildCharacter mgc) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(70);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.write(mgc.getGuildRank());

            return mplew.getPacket();
        }

        public static byte[] rankTitleChange(int gid, String[] ranks) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(68);
            mplew.writeInt(gid);
            for (String r : ranks) {
                mplew.writeMapleAsciiString(r);
            }

            return mplew.getPacket();
        }

        public static byte[] guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(73);
            mplew.writeInt(gid);
            mplew.writeShort(bg);
            mplew.write(bgcolor);
            mplew.writeShort(logo);
            mplew.write(logocolor);

            return mplew.getPacket();
        }

        public static byte[] updateGP(int gid, int GP, int glevel) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(79);
            mplew.writeInt(gid);
            mplew.writeInt(GP);
            mplew.writeInt(glevel);

            return mplew.getPacket();
        }

        public static byte[] guildNotice(int gid, String notice) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(75);
            mplew.writeInt(gid);
            mplew.writeMapleAsciiString(notice);

            return mplew.getPacket();
        }

        public static byte[] guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(66);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getJobId());

            return mplew.getPacket();
        }

        public static byte[] guildMemberOnline(int gid, int cid, boolean bOnline) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(67);
            mplew.writeInt(gid);
            mplew.writeInt(cid);
            mplew.write(bOnline ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] showGuildRanks(int npcid, List<MapleGuildRanking.GuildRankingInfo> all) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.OnGuildResult.getValue());
            oPacket.EncodeByte(0x50);
            oPacket.EncodeInt(npcid);
            oPacket.EncodeInt(all.size());
            int rank = 1;
            for (MapleGuildRanking.GuildRankingInfo info : all) {
                oPacket.EncodeShort(rank);
                oPacket.EncodeString(info.getName());
                oPacket.EncodeInt(info.getGP());
                oPacket.EncodeInt(info.getLogo());
                oPacket.EncodeInt(info.getLogoColor());
                oPacket.EncodeInt(info.getLogoBg());
                oPacket.EncodeInt(info.getLogoBgColor());
                rank++;
            }
            return oPacket.getPacket();
        }

        public static byte[] guildSkillPurchased(int gid, int sid, int level, long expiration, String purchase, String activate) {
            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.OnGuildResult.getValue());
            oPacket.EncodeByte(0x55);
            oPacket.EncodeInt(gid);
            oPacket.EncodeInt(sid);
            oPacket.EncodeShort(level);
            oPacket.EncodeLong(PacketHelper.getTime(expiration));
            oPacket.EncodeString(purchase);
            oPacket.EncodeString(activate);
            return oPacket.getPacket();
        }

        public static byte[] guildLeaderChanged(int gid, int oldLeader, int newLeader, int allianceId) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(89);
            mplew.writeInt(gid);
            mplew.writeInt(oldLeader);
            mplew.writeInt(newLeader);
            mplew.write(1);
            mplew.writeInt(allianceId);

            return mplew.getPacket();
        }

        public static byte[] denyGuildInvitation(String charname) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(61);
            mplew.writeMapleAsciiString(charname);

            return mplew.getPacket();
        }

        public static byte[] genericGuildMessage(byte code) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnGuildResult.getValue());
            mplew.write(code);
            if (code == 87) {
                mplew.writeInt(0);
            }
            if ((code == 3) || (code == 59) || (code == 60) || (code == 61) || (code == 84) || (code == 87)) {
                mplew.writeMapleAsciiString("");
            }

            return mplew.getPacket();
        }
    }

    public static byte[] OnGuildResult(byte type) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnGuildResult.getValue());
        oPacket.EncodeByte(type);
        return oPacket.getPacket();
    }

    public static class InfoPacket {

        public static byte[] showMesoGain(int gain, boolean inChat) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            if (!inChat) {
                mplew.write(0); // 없음
                mplew.write(gain < 0 ? 2 : 1); // 
                mplew.write(0); // 운지
                mplew.writeInt(gain);
                mplew.writeShort(0); // pc
            } else {
                mplew.write(6);
                mplew.writeInt(gain);
                mplew.writeInt(-1);
            }

            return mplew.getPacket();
        }

        public static byte[] getShowInventoryStatus(int mode) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(0);
            mplew.write(mode);
            mplew.writeInt(0);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] getShowItemGain(int itemId, short quantity) {
            return getShowItemGain(itemId, quantity, false);
        }

        public static byte[] getShowItemGain(int itemId, int quantity, boolean inChat) {
            OutPacket mplew = new OutPacket();

            if (inChat) {
                mplew.writeShort(SendPacketOpcode.USER_EFFECT2.getValue());
                mplew.write(5);
                mplew.write(1); // 0이하면 메이플 아스키 스트링 한개 필요
                mplew.writeInt(itemId);
                mplew.writeInt(quantity);
            } else {
                mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
                mplew.writeShort(0);
                mplew.writeInt(itemId);
                mplew.writeInt(quantity);
            }

            return mplew.getPacket();
        }

        public static byte[] setQuestTime() {
            OutPacket mplew = new OutPacket();
            mplew.EncodeShort(SendPacketOpcode.SET_QUEST_TIME.getValue());
            ModifiedQuestTime[] modifiedQuestTimes = ModifiedQuestTime.values();
            byte disablesize = 83;
            mplew.write(modifiedQuestTimes.length + disablesize);
            //disable
            for (int i = 0; i <= 24; ++i) {
                try {
                    mplew.writeInt(50000 + i);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd/hh/mm");
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2099/06/01/00/00").getTime()));
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2099/06/01/00/00").getTime()));
                } catch (ParseException e) {
                }
            }
            for (int i = 0; i <= 4; ++i) {
                try {
                    mplew.writeInt(8167 + i);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd/hh/mm");
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2020/12/25/00/00").getTime()));
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2099/06/01/00/00").getTime()));
                } catch (ParseException e) {
                }
            }
            for (int i = 0; i <= 3; ++i) {
                try {
                    mplew.writeInt(8531 + i);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd/hh/mm");
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2099/06/01/00/00").getTime()));
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2099/06/01/00/00").getTime()));
                } catch (ParseException e) {
                }
            }
            for (int i = 0; i <= 9; ++i) {
                try {
                    mplew.writeInt(50100 + i);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd/hh/mm");
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2021/01/05/00/00").getTime()));
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2099/06/01/00/00").getTime()));
                } catch (ParseException e) {
                }
            }
            for (int i = 0; i <= 33; ++i) {
                try {
                    mplew.writeInt(4401 + i);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd/hh/mm");
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2099/06/01/00/00").getTime()));
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2099/06/01/00/00").getTime()));
                } catch (ParseException e) {
                }
            }
            for (int i = 0; i <= 4; ++i) {
                try {
                    mplew.writeInt(4526 + i);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd/hh/mm");
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2020/12/18/00/00").getTime()));
                    mplew.writeLong(PacketHelper.getTime(sdf.parse("2099/06/01/00/00").getTime()));
                } catch (ParseException e) {
                }
            }
            for (ModifiedQuestTime modifiedQuestTime : modifiedQuestTimes) {
                mplew.writeInt(modifiedQuestTime.getQuestID());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd/hh/mm");
                try {
                    if (modifiedQuestTime.getStart() != null) {
                        mplew.writeLong(PacketHelper.getTime(sdf.parse(modifiedQuestTime.getStart()).getTime()));
                    } else {
                        mplew.writeLong(0);
                    }
                    if (modifiedQuestTime.getEnd() != null) {
                        mplew.writeLong(PacketHelper.getTime(sdf.parse(modifiedQuestTime.getEnd()).getTime()));
                    } else {
                        mplew.writeLong(0);
                    }
                } catch (ParseException e) {
                    System.err.println(String.format("Couldn't parse modified quest time [%d]", modifiedQuestTime.getQuestID()));
                }
            }
            return mplew.getPacket();
        }

        public static byte[] updateQuest(MapleQuestStatus quest) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(1);
            mplew.writeShort(quest.getQuest().getId());
            mplew.write(quest.getStatus());
            switch (quest.getStatus()) {
                case 0:
                    mplew.writeInt(0); // CRC
                    break;
                case 1:
                    if (quest.getCustomData() != null) {
                        if (quest.getCustomData().startsWith("time_")) {
                            mplew.writeShort(9);
                            mplew.write(1);
                            mplew.writeLong(PacketHelper.getTime(Long.parseLong(quest.getCustomData().substring(5))));
                        } else {
                            mplew.writeMapleAsciiString(quest.getCustomData());
                        }
                    } else {
                        mplew.writeZeroBytes(2);
                    }
                    break;
                case 2:
                    mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
                    mplew.writeInt(0);
            }
            return mplew.getPacket();
        }

        public static byte[] updateQuestMobKills(MapleQuestStatus status) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(1);
            mplew.writeShort(status.getQuest().getId());
            mplew.write(1);
            StringBuilder sb = new StringBuilder();
            for (Iterator i$ = status.getMobKills().values().iterator(); i$.hasNext();) {
                int kills = ((Integer) i$.next()).intValue();
                sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
            }
            mplew.writeMapleAsciiString(sb.toString());
            mplew.writeLong(0L);

            return mplew.getPacket();
        }

        public static byte[] itemExpired(int itemid) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(2);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] GainEXP_Others(final int gain, final boolean inChat, final boolean white) {
            return GainEXP_Monster(gain, inChat, white, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }

        public static byte[] GainEXP_Monster(final int gain, final boolean inChat, final boolean white,
                final int SelectedMobBonusExp,
                final int PartyBonusPercentage,
                final int WeddingBonusExp,
                final int PartyBonusExp,
                final int ItemBonusExp,
                final int PremiumIPBonusExp,
                final int RainbowWeekEventBonusExp,
                final int BoomUpEventBonusExp,
                final int PlusExpBuffBonusExp,
                final int PsdBonusExpRate,
                final int IndieBonusExp,
                final int RelaxBonusExp,
                final int InstallItemBonusExp,
                final int AswanWinnerBonusExp,
                final int ExpByIncExpR,
                final int ValuePackBonusExp,
                final int ExpByIncPQExpR,
                final int BaseAddExp,
                final int BloodAllianceBonusExp,
                final int FreezeHotEventBonusExp,
                final int UserHPRateBonusExp,
                final int burninginc,
                final int burningEXP) {
            // final WritingPacket packet = new WritingPacket();
            OutPacket packet = new OutPacket();
            packet.writeShort(SendPacketOpcode.OnMessage.getValue());
            packet.write(0x03); // 0x04 = exp, 0x05 = fame, 0x06 = mesos, 0x07 = guildpoints
            packet.write(white ? 1 : 0);
            packet.writeInt(gain);
            packet.write(inChat ? 1 : 0); // Not in chat

            packet.writeInt(BaseAddExp); // 이벤트 보너스 경험치
            packet.write(0); // 0 == 아무값없음 1 >= 일때 웨딩보너스밑에 추가패킷필요
            packet.write(0); // 없는값
            packet.writeInt(WeddingBonusExp);// 웨딩보너스

            //packet.write(1); // "몇시간 이상 사냥보너스 경험치"
            packet.write(0); // 없는값?
            //packet.write(PartyBonusExp); // 파티 그냥 값 
            packet.writeInt(PartyBonusExp); //

            packet.writeInt(InstallItemBonusExp); // 아이템 장착 보너스
            packet.writeInt(PremiumIPBonusExp); // pc방 보너스 경험치
            packet.writeInt(RainbowWeekEventBonusExp); // 레인보우 위크 보너스 경험치

            packet.writeInt(BoomUpEventBonusExp); // 붐 업 보너스 경험치
            packet.writeInt(0); // 비약 보너스 경험치
            packet.writeInt(PsdBonusExpRate); // (null)보너스 경험치
            packet.writeInt(PlusExpBuffBonusExp); // 버프 보너스 경험치
            packet.writeInt(RelaxBonusExp); // 휴식 보너스 경험치
            packet.writeInt(ItemBonusExp); // 아이템 보너스 경험치
            packet.writeInt(AswanWinnerBonusExp); // 아스완 승자 보너스

            packet.writeInt(0); // 없는값
            return packet.getPacket();
        }

        public static byte[] getSPMsg(byte sp, short job) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(4);
            mplew.writeShort(job);
            mplew.write(sp);

            return mplew.getPacket();
        }

        public static byte[] getShowFameGain(int gain) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(5);
            mplew.writeInt(gain);

            return mplew.getPacket();
        }

        public static byte[] getGPMsg(int itemid) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(7);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] getGPContribution(int itemid) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(8);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] getStatusMsg(int itemid) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(9);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] updateInfoQuest(int quest, String data) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(0x0C);
            mplew.writeShort(quest);
            mplew.writeMapleAsciiString(data);

            return mplew.getPacket();
        }

        public static byte[] updateClientInfoQuest(int quest, String data) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(13);
            mplew.writeShort(quest);
            mplew.writeMapleAsciiString(data);

            return mplew.getPacket();
        }

        public static byte[] showItemReplaceMessage(List<String> message) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(14);
            mplew.write(message.size());
            for (String x : message) {
                mplew.writeMapleAsciiString(x);
            }

            return mplew.getPacket();
        }

        public static byte[] updateTodayTrait(MapleCharacter chr) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnStatChanged.getValue());
            mplew.write(0);
            mplew.writeInt(MapleStat.DayLimit.getValue());
            mplew.writeShort(chr.getTodayCharisma());     //오늘의 카리스마
            mplew.writeShort(chr.getTodayInsight());     //오늘의 통찰력
            mplew.writeShort(chr.getTodayWillpower());     //오늘의 의지
            mplew.writeShort(chr.getTodayCraft());     //오늘의 손재주
            mplew.writeShort(chr.getTodaySense());     //오늘의 감성
            mplew.writeShort(chr.getTodayCharm());     //오늘의 매력
            mplew.write(0);
            mplew.writeLong(PacketHelper.getTime(-2)); // 카리스마부터 여기까지 21Bytes..
            mplew.writeShort(0);
            return mplew.getPacket();
        }

        public static byte[] showTraitGain(MapleTrait.MapleTraitType trait, int amount, boolean limited) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            //오늘하루 다채우면 0x11
            mplew.write(limited ? 0x11 : 0x10);
            mplew.writeInt(trait.getStat().getValue());
            mplew.writeInt(amount);

            return mplew.getPacket();
        }

        public static byte[] showTraitMaxed(MapleTrait.MapleTraitType trait) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(0x11);
            mplew.writeInt(trait.getStat().getValue());

            return mplew.getPacket();
        }

        public static byte[] getBPMsg(int amount) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(21);
            mplew.writeInt(amount);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] showExpireMessage(byte type, List<Integer> item) {
            OutPacket mplew = new OutPacket(4 + item.size() * 4);

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(type);
            mplew.write(item.size());
            for (Integer it : item) {
                mplew.writeInt(it.intValue());
            }

            return mplew.getPacket();
        }

        public static byte[] showStatusMessage(int mode, String info, String data) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(mode);
            if (mode == 22) {
                mplew.writeMapleAsciiString(info);
                mplew.writeMapleAsciiString(data);
            }

            return mplew.getPacket();
        }

        public static byte[] showReturnStone(int act) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnMessage.getValue());
            mplew.write(23);
            mplew.write(act);

            return mplew.getPacket();
        }
    }

    public static class BuffPacket {

        public static byte[] giveDoubleDice(int buffid, int skillid, int duration, Map<CharacterTemporaryStat, Integer> statups) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            mplew.writeShort(buffid);
            mplew.writeInt(skillid);
            mplew.writeInt(duration);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeInt(GameConstants.getDiceHPMP(buffid, 3));
            mplew.writeInt(GameConstants.getDiceHPMP(buffid, 3));
            mplew.writeInt(GameConstants.getDiceCrit(buffid, 4));
            mplew.writeZeroBytes(20); //idk
            mplew.writeInt(GameConstants.getDiceDef(buffid, 2));
            mplew.writeZeroBytes(12); //idk
            mplew.writeInt(GameConstants.getDiceDamage(buffid, 5));
            mplew.writeZeroBytes(16); //idk
            mplew.writeInt(GameConstants.getDiceExp(buffid, 6));
            mplew.writeZeroBytes(16);
            mplew.writeInt(1000);//Unk
            mplew.write(0);//Unk
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] giveDice(int buffid, int skillid, int duration, Map<CharacterTemporaryStat, Integer> statups) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            mplew.writeShort(Math.max(buffid / 100, Math.max(buffid / 10, buffid % 10))); //1-6
            mplew.writeInt(skillid); // skillid
            mplew.writeInt(duration);
            //mplew.writeShort(0); 변경완료
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeInt(GameConstants.getDiceHPMP(buffid, 3));
            mplew.writeInt(GameConstants.getDiceHPMP(buffid, 3));
            mplew.writeInt(GameConstants.getDiceCrit(buffid, 4));
            mplew.writeZeroBytes(20); //idk
            mplew.writeInt(GameConstants.getDiceDef(buffid, 2));
            mplew.writeZeroBytes(12); //idk
            mplew.writeInt(GameConstants.getDiceDamage(buffid, 5));
            mplew.writeZeroBytes(16); //idk
            mplew.writeInt(GameConstants.getDiceExp(buffid, 6));
            mplew.writeZeroBytes(16);
            mplew.writeInt(1000);//Unk
            mplew.write(0);//Unk
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] giveHoming(int skillid, int mobid, int x) { //잘모르겟음
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(mplew, CharacterTemporaryStat.GuidedBullet);
            mplew.writeZeroBytes(3);//스택했을때 제로바이트 수인가..?
            mplew.writeInt(1);
            mplew.writeLong(skillid);
            mplew.write(0);
            mplew.writeInt(mobid);
            mplew.writeZeroBytes(13);
            return mplew.getPacket();
        }

        public static byte[] giveMount(int buffid, int skillid, Map<CharacterTemporaryStat, Integer> statups) {
            return showMonsterRiding(-1, statups, buffid, skillid);
        }

        public static byte[] showMonsterRiding(int cid, Map<CharacterTemporaryStat, Integer> statups, int buffid, int skillId) {
            OutPacket mplew = new OutPacket();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.USER_SET_TEMPORARY_STAT.getValue());
                mplew.writeInt(cid);
            }
            PacketHelper.writeBuffMask(mplew, statups);
            mplew.writeZeroBytes(/*cid != -1 ? 19 :*/3);
            mplew.writeInt(buffid);
            mplew.writeInt(skillId);
            mplew.writeInt(0);
            mplew.writeShort(0); // 안되면 바이트로 변경 
            mplew.write(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(4);
            return mplew.getPacket();
        }

        public static byte[] givePirate(Map<CharacterTemporaryStat, Integer> statups, int duration, int skillid) { //잘모르겟음
            final boolean infusion = skillid == 5121009 || skillid == 15121005 || skillid % 10000 == 8006;
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);

            //mplew.writeShort(0);
            mplew.write(0);
            mplew.writeShort(0);
            for (Map.Entry statup : statups.entrySet()) {
                mplew.writeInt(((Integer) statup.getValue()).intValue());
                mplew.writeLong(skillid);
                mplew.writeZeroBytes(infusion ? 6 : 1);
                mplew.writeShort(duration);//duration... seconds
            }
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.write(1);
            mplew.write(1); //does this only come in dash?
            return mplew.getPacket();
        }

        public static byte[] giveForeignPirate(Map<CharacterTemporaryStat, Integer> statups, int duration, int cid, int skillid) {
            final boolean infusion = skillid == 5121009 || skillid == 15121005 || (skillid % 10000 == 8006);
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_SET_TEMPORARY_STAT.getValue());
            mplew.writeInt(cid);
            PacketHelper.writeBuffMask(mplew, statups);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeShort(0);
            mplew.write(0);
            //mplew.writeShort(0);
            for (Map.Entry statup : statups.entrySet()) {
                mplew.writeInt(((Integer) statup.getValue()).intValue());
                mplew.writeLong(skillid);
                mplew.writeZeroBytes(infusion ? 6 : 1);
                mplew.writeShort(duration);//duration... seconds
            }
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(1);
            return mplew.getPacket();
        }

        public static byte[] giveArcane(int x, int skillid, int duration) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(mplew, CharacterTemporaryStat.ArcaneAim);
            mplew.writeShort(x);
            mplew.writeInt(skillid);
            mplew.writeInt(duration);
            //mplew.writeShort(0); 변경완료
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] cancelExeed() {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatReset.getValue());
            mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 00 04")); //익시드랑 익시드공격스킬
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeLong(0);
            return mplew.getPacket();
        }

        public static byte[] giveEnergyChargeTest(int bar, int bufflength) {
            return giveEnergyChargeTest(-1, bar, bufflength);
        }

        public static byte[] giveEnergyChargeTest(int cid, int bar, int bufflength) {
            OutPacket mplew = new OutPacket();
            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.USER_SET_TEMPORARY_STAT.getValue());
                mplew.writeInt(cid);
            }
            PacketHelper.writeSingleMask(mplew, CharacterTemporaryStat.EnergyCharge);
            mplew.writeZeroBytes(/*cid != -1 ? 19 : */3);
            mplew.writeShort(Math.min(bar, 10000));
            mplew.writeShort(0);
            mplew.writeLong(0);
            mplew.write(0);
            mplew.writeInt(bar >= 10000 ? bufflength : 0);
            mplew.writeZeroBytes(10);
            return mplew.getPacket();
        }

        public static byte[] giveJudgement(int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect, int value) { //잘모르겠음
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            for (Map.Entry statup : statups.entrySet()) {
                mplew.writeShort(((Integer) statup.getValue()).intValue());
                mplew.writeInt(buffid);
                mplew.writeInt(bufflength);
            }
            mplew.writeShort(0);//ㄴㄴ
            mplew.writeShort(1000);//ㄴㄴ
            mplew.write(0);//ㄴㄴ 
            mplew.writeInt(value);//퍼센트
            mplew.write(0);//ㄴㄴ
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] giveLifeTidal(boolean isHpBetter, int value) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(mplew, CharacterTemporaryStat.LifeTidal);
            mplew.writeShort(isHpBetter ? 2 : 1);
            mplew.writeInt(27110007);// 라이프 타이달
            mplew.writeInt(2100000000);
            mplew.writeZeroBytes(3); //변경완료
            mplew.writeInt(value);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] giveDemonWatk(int hp) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(mplew, CharacterTemporaryStat.LifeTidal);
            mplew.writeShort(3);
            mplew.writeInt(0);
            mplew.writeInt(2100000000);
            mplew.writeZeroBytes(3);  //변경완료
            mplew.writeInt(hp);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] cancelUnionAura() { // 잘모르겟음
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatReset.getValue());
            mplew.writeZeroBytes(8); // 192 Maybe
            mplew.write(HexTool.getByteArrayFromHexString("00 00 00 40 00 00 00 00"));
            mplew.write(HexTool.getByteArrayFromHexString("00 60 00 00 00 10 00 00"));
            mplew.write(HexTool.getByteArrayFromHexString("00 F0 00 00 00 00 00 00"));
            mplew.writeZeroBytes(32); // 192 Maybe
            return mplew.getPacket();
        }

        public static byte[] giveBuff(int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            int extra = 0;
            for (Map.Entry stat : statups.entrySet()) {
                if (((CharacterTemporaryStat) stat.getKey()).canStack()) {
                    continue;
                }

                if (stat.getKey() == CharacterTemporaryStat.SpiritInfusion
                        || stat.getKey() == CharacterTemporaryStat.Unk_0x400000_8
                        || stat.getKey() == CharacterTemporaryStat.MonsterRiding
                        || (stat.getKey() == CharacterTemporaryStat.CriticalBuff && buffid == 23101003)) {
                    mplew.writeInt(((Integer) stat.getValue()).intValue());
                } else {
                    mplew.writeShort(((Integer) stat.getValue()).shortValue());
                }
                mplew.writeInt(buffid);
                mplew.writeInt(bufflength);
                if (stat.getKey() == CharacterTemporaryStat.Judgement) {
                    extra = ((Integer) stat.getValue()).intValue();
                }
            }
            if (statups.containsKey(CharacterTemporaryStat.SuperBody)) {
                mplew.writeInt(0);
            }
            //mplew.write(statups.containsKey(CharacterTemporaryStat.DefenseAtt) == false ? 0 : statups.get(CharacterTemporaryStat.DefenseAtt).byteValue());
            //mplew.write(statups.containsKey(CharacterTemporaryStat.DefenseState) == false ? 0 : statups.get(CharacterTemporaryStat.DefenseState).byteValue());
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);
            for (Map.Entry stat : statups.entrySet()) {
                if (!((CharacterTemporaryStat) stat.getKey()).canStack()) {
                    continue;
                }
                mplew.writeInt(1);
                mplew.writeInt(buffid);
                mplew.writeInt(((Integer) stat.getValue()).intValue());
                mplew.writeInt(0);
                mplew.writeInt(bufflength);
            }
            if ((statups.containsKey(CharacterTemporaryStat.HowlingAttackDamage)) && (statups.containsKey(CharacterTemporaryStat.HowlingMaxMp)) && (statups.containsKey(CharacterTemporaryStat.HowlingCritical)) && (statups.containsKey(CharacterTemporaryStat.HowlingDefence)) && (statups.containsKey(CharacterTemporaryStat.HowlingEva))) {
                mplew.write(0);
            } else if (statups.containsKey(CharacterTemporaryStat.Dice)) {
                mplew.writeInt(GameConstants.getDiceStat(buffid, 3));
                mplew.writeInt(GameConstants.getDiceStat(buffid, 3));
                mplew.writeInt(GameConstants.getDiceStat(buffid, 4));
                mplew.writeZeroBytes(20);
                mplew.writeInt(GameConstants.getDiceStat(buffid, 2));
                mplew.writeZeroBytes(12);
                mplew.writeInt(GameConstants.getDiceStat(buffid, 5));
                mplew.writeZeroBytes(16);
                mplew.writeInt(GameConstants.getDiceStat(buffid, 6));
                mplew.writeZeroBytes(16);
            } else if (statups.containsKey(CharacterTemporaryStat.BlessingArmor)) {
                mplew.writeInt(0);
            } else if (statups.containsKey(CharacterTemporaryStat.DarkAura)) {
                mplew.write(0);
            } else if (statups.containsKey(CharacterTemporaryStat.BlueAura)) {
                mplew.write(0);
            } else if (statups.containsKey(CharacterTemporaryStat.YellowAura)) {
                mplew.write(0);
            } else if (statups.containsKey(CharacterTemporaryStat.SuperBody)) {
                mplew.writeInt(0);
            } else if (statups.containsKey(CharacterTemporaryStat.Judgement)) {
                mplew.writeInt(GameConstants.getJudgmentStat(buffid, extra));
                
            } else if (statups.containsKey(CharacterTemporaryStat.StackBuff)) {
                mplew.write(statups.get(CharacterTemporaryStat.StackBuff).byteValue());
                
            } else if (statups.containsKey(CharacterTemporaryStat.LifeTidal)) {
                mplew.writeInt(0); // mOption
            } else if (statups.containsKey(CharacterTemporaryStat.AntiMagicShell)) {
                mplew.write(3);
            } else if (statups.containsKey(CharacterTemporaryStat.IgnoreTargetDEF)) {
                mplew.writeInt(0); // mOption
            } else if (statups.containsKey(CharacterTemporaryStat.VampDeath)) {
                mplew.writeInt(0);
            } else if ((statups.containsKey(CharacterTemporaryStat.ComboCounter)) || (statups.containsKey(CharacterTemporaryStat.Summon))) {
                mplew.writeShort(258);
                mplew.writeShort(600);
            } else {
                mplew.write(0);
            }
            mplew.writeShort(GameConstants.getBuffDelay(buffid));
            mplew.write(0);
            if (isMovementAffectingStat(statups)) {
                mplew.write(4);
            }
            mplew.writeZeroBytes(60);
            return mplew.getPacket();
        }

        private static boolean isMovementAffectingStat(Map<CharacterTemporaryStat, Integer> statups) {
            /* IDA : sub_00822FE0 */
            return (statups.containsKey(CharacterTemporaryStat.Jump))
                    || (statups.containsKey(CharacterTemporaryStat.DragonRoar))
                    || (statups.containsKey(CharacterTemporaryStat.Weakness))
                    || (statups.containsKey(CharacterTemporaryStat.Slow))
                    || (statups.containsKey(CharacterTemporaryStat.Morph))
                    || (statups.containsKey(CharacterTemporaryStat.Ghost))
                    || (statups.containsKey(CharacterTemporaryStat.MapleWarrior))
                    || (statups.containsKey(CharacterTemporaryStat.Attract))
                    || (statups.containsKey(CharacterTemporaryStat.MonsterRiding))
                    || (statups.containsKey(CharacterTemporaryStat.GuidedBullet))
                    || (statups.containsKey(CharacterTemporaryStat.DashJump))
                    || (statups.containsKey(CharacterTemporaryStat.Flying))
                    || (statups.containsKey(CharacterTemporaryStat.Frozen))
                    || (statups.containsKey(CharacterTemporaryStat.EnhancedFrozen))
                    || (statups.containsKey(CharacterTemporaryStat.Lapidification))
                    || (statups.containsKey(CharacterTemporaryStat.IndieSpeed))
                    || (statups.containsKey(CharacterTemporaryStat.IndieJump))
                    || (statups.containsKey(CharacterTemporaryStat.FixedSpeed))
                    || (statups.containsKey(CharacterTemporaryStat.EnergyCharge))
                    || (statups.containsKey(CharacterTemporaryStat.Mechanic))
                    || (statups.containsKey(CharacterTemporaryStat.Magnet))
                    || (statups.containsKey(CharacterTemporaryStat.MagnetArea))
                    || (statups.containsKey(CharacterTemporaryStat.VampDeath))
                    || (statups.containsKey(CharacterTemporaryStat.VampDeathSummon));
        }

        public static byte[] giveDualBrid(int buffid, int bufflength, List<Pair<CharacterTemporaryStat, Integer>> statups, MapleStatEffect effect, MapleCharacter chr) { //잘모르겠음
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            for (Pair<CharacterTemporaryStat, Integer> stat : statups) {
                mplew.writeShort((stat.right));
                mplew.writeInt(buffid);
                mplew.writeInt(bufflength);
            }
            mplew.writeShort(0);//ㄴㄴ
            mplew.writeShort(1000);//ㄴㄴ]
            mplew.write(0);//ㄴㄴ 
            mplew.write(effect.getX() - chr.currentBattleshipHP());
            mplew.write(0);//ㄴㄴ
            mplew.writeInt(0);
            mplew.writeLong(0);
            return mplew.getPacket();
        }

        public static byte[] giveWillOfSword(int buffid, int bufflength, List<Pair<CharacterTemporaryStat, Integer>> statups, MapleStatEffect effect, MapleCharacter chr) {//카이저사용x
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            for (Pair<CharacterTemporaryStat, Integer> stat : statups) {
                mplew.writeShort((stat.right));
                mplew.writeInt(buffid);
                mplew.writeInt(bufflength);
            }
            MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
            Item weapon = equip.getItem((byte) -11);
            mplew.writeZeroBytes(5);//ㄴㄴ
            if ((buffid != 61101002) && (buffid != 61110211)) {
                mplew.writeInt(buffid == 61121217 ? 4 : 2);
                mplew.writeInt(5);
                mplew.writeInt(weapon.getItemId());
                mplew.writeInt(5);
                mplew.writeZeroBytes(24);
            } else {
                mplew.writeInt(buffid == 61110211 ? 3 : 1);
                mplew.writeInt(3);
                mplew.writeInt(weapon.getItemId());
                mplew.writeInt(3);
                mplew.writeZeroBytes(16);
            }
            return mplew.getPacket();
        }

        public static byte[] giveForeignWillofSword(MapleCharacter chr, int skillid, List<Pair<CharacterTemporaryStat, Integer>> statups) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.USER_SET_TEMPORARY_STAT.getValue());
            mplew.writeInt(chr.getId());
            PacketHelper.writeBuffMask(mplew, statups);
            for (Pair<CharacterTemporaryStat, Integer> statup : statups) {
                mplew.writeShort(statup.right.shortValue());
                mplew.writeInt(skillid);
            }
            mplew.writeShort(0);
            mplew.write(0);
            MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
            Item weapon = equip.getItem((byte) -11);
            if ((skillid != 61101002) && (skillid != 61110211)) {
                mplew.writeInt(skillid == 61121217 ? 4 : 2);
                mplew.writeInt(5);
                mplew.writeInt(weapon.getItemId());
                mplew.writeInt(5);
                mplew.writeZeroBytes(22);
            } else {
                mplew.writeInt(skillid == 61110211 ? 3 : 1);
                mplew.writeInt(3);
                mplew.writeInt(weapon.getItemId());
                mplew.writeInt(3);
                mplew.writeZeroBytes(15);
            }
            return mplew.getPacket();
        }

        public static byte[] ItemSkillFromButton() {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnAswanMsg.getValue());
            mplew.writeLong(9L);
            mplew.writeLong(Randomizer.nextLong());
            mplew.writeLong(Randomizer.nextLong());
            return mplew.getPacket();
        }

        public static byte[] giveAranBuff(int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            for (Map.Entry statup : statups.entrySet()) {
                mplew.writeShort(((Integer) statup.getValue()).shortValue());
                mplew.writeInt(buffid);
                mplew.writeInt(bufflength);
            }
            mplew.writeInt(0);
            mplew.writeInt(6);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(1);
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] giveEvanBuff(int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            for (Map.Entry statup : statups.entrySet()) {
                mplew.writeShort(((Integer) statup.getValue()).shortValue());
                mplew.writeInt(buffid);
                mplew.writeInt(bufflength);
            }
            mplew.writeInt(0);
            mplew.writeInt(6);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(1);
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] fakegiveBuff(int buffid, int bufflength, List<Pair<CharacterTemporaryStat, Integer>> statups, MapleStatEffect effect) {// 진짜모르겟음
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            boolean stacked = false;
            boolean isAura = false;
            for (Pair<CharacterTemporaryStat, Integer> stat : statups) {
                if ((stat.left == CharacterTemporaryStat.YellowAura) || (stat.left == CharacterTemporaryStat.BlueAura) || (stat.left == CharacterTemporaryStat.DarkAura)) {
                    isAura = true;
                }
                if (((CharacterTemporaryStat) stat.left).canStack()) {
                    if (!stacked) {
                        mplew.writeZeroBytes(3);
                        stacked = true;
                    }
                    mplew.writeInt(1);
                    mplew.writeInt(buffid);
                    mplew.writeLong((stat.right).longValue());
                } else {
                    mplew.writeShort((stat.right));
                    mplew.writeInt(buffid);
                }
                mplew.writeInt(bufflength);
            }
            if (!isAura) {
                //mplew.writeShort(0);
                if ((effect != null) && (effect.isDivineShield())) {
                    mplew.writeInt(effect.getEnhancedWatk());
                } else if ((effect != null) && (effect.getCharColor() > 0)) {
                    mplew.writeInt(effect.getCharColor());
                } else if ((effect != null) && (effect.isInflation())) {
                    mplew.writeInt(effect.getInflation());
                }
            }
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.write(effect != null && effect.isShadow() ? 1 : 2);
            mplew.write(0);
            mplew.writeInt(0);
            if (isAura) {
                mplew.writeInt(0);
            }
            return mplew.getPacket();
        }

        public static byte[] soulSteel(int buffid, int bufflength, int buffeffect, Map<CharacterTemporaryStat, Integer> statups) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            for (Map.Entry statup : statups.entrySet()) {
                mplew.writeShort(((Integer) statup.getValue()).shortValue());
                mplew.writeInt(buffid);
                mplew.writeInt(bufflength);
            }
            mplew.writeShort(0);
            mplew.writeShort(1000);
            mplew.write(0);
            mplew.write(buffeffect);
            mplew.write(0);
            mplew.write(0);//버프아이디
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] EnergyFakeBuff(int buffid, int bufflength) { //얘는 모르겟다 나중에
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            mplew.writeInt(0);
            mplew.writeInt(0x10000000);
            mplew.writeInt(0);
            mplew.writeInt(0x80000000);
            mplew.writeZeroBytes(24);
            mplew.writeShort(1);//rightValue
            mplew.writeInt(buffid);
            mplew.writeInt(bufflength);
            mplew.writeZeroBytes(5);
            mplew.writeInt(1);
            mplew.writeInt(buffid);
            mplew.writeLong(20);
            mplew.writeInt(bufflength);
            mplew.writeZeroBytes(5);
            mplew.write(2);
            mplew.writeZeroBytes(5);
            return mplew.getPacket();
        }

        /*
            mplew.writeShort(x);
            mplew.writeShort(skillid);
            mplew.writeShort(level);
            mplew.writeInt(duration);
            mplew.writeZeroBytes(1); // 변경완료 (아마 잘될거임..) 안되면 3으로
            mplew.writeShort(effectdelay);//이펙트딜레이
            mplew.writeLong(0);
            mplew.write(0);
            mplew.write(0x13);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);
         */
        public static byte[] giveDebuff(MonsterSkill statups, int x, int skillid, int level, int duration, int effectdelay) {
            OutPacket oPacket = new OutPacket();
            oPacket.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, statups);
            oPacket.EncodeShort(x);
            oPacket.EncodeShort(skillid);
            oPacket.EncodeShort(level);
            oPacket.EncodeShort(level);
            oPacket.EncodeShort((int) (duration / 500));
            oPacket.EncodeShort(effectdelay);
            oPacket.EncodeByte(0);
            oPacket.writeZeroBytes(99);
            return oPacket.getPacket();
        }

        public static byte[] cancelBuff(List<CharacterTemporaryStat> statups) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatReset.getValue());
            PacketHelper.writeMask(mplew, statups);
            for (CharacterTemporaryStat z : statups) {
                if (z.canStack()) {
                    mplew.writeInt(0);
                }
            }
            mplew.write(3);
            mplew.write(1);
            return mplew.getPacket();
        }

        /*
        public static byte[] cancelBuff(List<MapleBuffStat> statups, Map<MapleBuffStat, List<MapleBuffStatStackedValueHolder>> stacks, int skillId) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.TEMPORARY_STAT_RESET.getValue());
            PacketHelper.writeMask(mplew, statups);
            int x = 0;
            if (statups != null) {
                if (!statups.contains(MapleBuffStat.MONSTER_RIDING)) {
                    for (MapleBuffStat buffs : statups) {
                        if (buffs.canStack()) {
                            x += 1;
                            if (stacks.containsKey((MapleBuffStat) buffs)) {
                                x -= 1;
                            }
                        }
                    }
                    if (stacks.size() == 1 && x == 1) {
                        x = 0;
                    }
                    mplew.writeZeroBytes(4 * x);
                    for (MapleBuffStat buffs : statups) {
                        if (stacks.containsKey((MapleBuffStat) buffs)) {
                            mplew.writeInt(stacks.get((MapleBuffStat) buffs).size());
                            for (MapleBuffStatStackedValueHolder sse : stacks.get((MapleBuffStat) buffs)) {
                                mplew.writeInt(sse.getSkillId());
                                mplew.writeInt(sse.getValue());
                                mplew.writeInt(sse.getTime());
                                mplew.writeInt(sse.getBuffLength());
                            }
                        }
                    }
                    mplew.writeZeroBytes(statups.size() * 4);
                    mplew.write(statups.size());
                } else {
                    if (statups.contains(MapleBuffStat.MONSTER_RIDING) && statups.contains(MapleBuffStat.ANGEL_SPEED) && statups.contains(MapleBuffStat.ENHANCED_MDEF)) {
                        mplew.writeInt(0);
                        mplew.write(7);
                    } else {
                        mplew.write(3);
                    }
                    mplew.write(1);
                    mplew.writeLong(0);
                    mplew.writeLong(0);
                    mplew.writeLong(0);
                    mplew.write(0);
                }
            }
            return mplew.getPacket();
        }
         */
        public static byte[] cancelDebuff(MonsterSkill mask) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnTemporaryStatReset.getValue());

            PacketHelper.writeSingleMask(mplew, mask);
            mplew.write(3);
            mplew.write(1);
            mplew.writeLong(0);
            mplew.write(0);//v112
            return mplew.getPacket();
        }

        public static byte[] cancelHoming() {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnTemporaryStatReset.getValue());
            PacketHelper.writeSingleMask(mplew, CharacterTemporaryStat.GuidedBullet);
            return mplew.getPacket();
        }

        public static byte[] giveForeignBuff(int cid, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.USER_SET_TEMPORARY_STAT.getValue());
            mplew.writeInt(cid);
            PacketHelper.writeBuffMask(mplew, statups);
            for (Map.Entry statup : statups.entrySet()) {
                if (statup.getKey() == CharacterTemporaryStat.ShadowPartner || statup.getKey() == CharacterTemporaryStat.Mechanic
                        || statup.getKey() == CharacterTemporaryStat.DarkAura || statup.getKey() == CharacterTemporaryStat.YellowAura
                        || statup.getKey() == CharacterTemporaryStat.BlueAura || statup.getKey() == CharacterTemporaryStat.Aura
                        || statup.getKey() == CharacterTemporaryStat.GiantPotion
                        || statup.getKey() == CharacterTemporaryStat.SpiritLink || statup.getKey() == CharacterTemporaryStat.RepeatEffect
                        || statup.getKey() == CharacterTemporaryStat.WeaponCharge || statup.getKey() == CharacterTemporaryStat.SpiritInfusion
                        || statup.getKey() == CharacterTemporaryStat.Morph || statup.getKey() == CharacterTemporaryStat.DamAbsorbShield
                        || statup.getKey() == CharacterTemporaryStat.DevilishPower
                        || statup.getKey() == CharacterTemporaryStat.Unk_0x100000_8
                        || statup.getKey() == CharacterTemporaryStat.KeyDownAreaMoving
                        || statup.getKey() == CharacterTemporaryStat.Unk_0x400000_8
                        || effect.getSourceId() == 1221054
                        || statup.getKey() == CharacterTemporaryStat.KillingPoint
                        || statup.getKey() == CharacterTemporaryStat.FinalAttackWindBreaker
                        || statup.getKey() == CharacterTemporaryStat.Invisible
                        || statup.getKey() == CharacterTemporaryStat.Larkness
                        || statup.getKey() == CharacterTemporaryStat.SaintSaver
                        || statup.getKey() == CharacterTemporaryStat.AffectedEffect) {
                    mplew.writeShort(((Integer) statup.getValue()).shortValue());
                    mplew.writeInt(effect.isSkill() ? effect.getSourceId() : -effect.getSourceId());
                } else {
                    mplew.writeShort(((Integer) statup.getValue()).shortValue());
                }
            }
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeZeroBytes(13);
            mplew.writeShort(0);
            mplew.writeZeroBytes(30);
            return mplew.getPacket();
        }

        public static byte[] giveForeignBuffRing(int cid, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.USER_SET_TEMPORARY_STAT.getValue());
            mplew.writeInt(cid);
            PacketHelper.writeBuffMask(mplew, statups);
            for (Map.Entry statup : statups.entrySet()) {
                if (statup.getKey() == CharacterTemporaryStat.RepeatEffect) {
                    mplew.writeShort(((Integer) statup.getValue()).shortValue());
                    mplew.writeInt(effect.isSkill() ? effect.getSourceId() : -effect.getSourceId());
                }
            }
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(2);
            mplew.writeZeroBytes(13);
            mplew.writeShort(600);
            mplew.writeZeroBytes(30);
            return mplew.getPacket();
        }

        public static byte[] giveForeignDebuff(int cid, final MonsterSkill statups, int skillid, int level, int x) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_SET_TEMPORARY_STAT.getValue());
            mplew.writeInt(cid);

            PacketHelper.writeSingleMask(mplew, statups);
            if (skillid == 125) {
                mplew.writeShort(0);
            }
            mplew.writeShort(x);
            mplew.writeShort(skillid);
            mplew.writeShort(level);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeShort(900); //Delay
            return mplew.getPacket();
        }

        public static byte[] cancelForeignBuff(int cid, List<CharacterTemporaryStat> statups) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_RESET_TEMPORARY_STAT.getValue());
            mplew.writeInt(cid);
            PacketHelper.writeMask(mplew, statups);
            mplew.write(3);
            mplew.write(1);
            mplew.write(0);//v112

            return mplew.getPacket();
        }

        public static byte[] cancelForeignDebuff(int cid, MonsterSkill mask) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.USER_RESET_TEMPORARY_STAT.getValue());
            mplew.writeInt(cid);

            PacketHelper.writeSingleMask(mplew, mask);
            mplew.write(3);
            mplew.write(1);
            mplew.write(0);//v112
            return mplew.getPacket();
        }
    }

    public static byte[] characterPotentialSet(byte indexID, int skillID, int skillLevel, int grade, boolean exclRequest) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnCharacterPotentialSet.getValue());
        oPacket.EncodeByte(exclRequest);
        oPacket.EncodeByte(1);
        oPacket.EncodeShort(indexID);
        oPacket.EncodeInt(skillID);
        oPacket.EncodeShort(skillLevel);
        oPacket.EncodeShort(grade);
        oPacket.EncodeByte(1); // updatePassive
        return oPacket.getPacket();
    }

    public static byte[] characterPotentialReset(int prt, int arg) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnCharacterPotentialReset.getValue());
        oPacket.EncodeByte(0);
        switch (prt) {
            /* skillID */
            case 1: {
                oPacket.EncodeInt(arg);
                break;
            }
            /* Unk */
            case 2: {
                break;
            }
            /* indexID */
            case 3: {
                oPacket.EncodeShort(arg);
                break;
            }
        }
        return oPacket.getPacket();
    }

    public static byte[] characterHonorExp(int hLevel, int hExp, boolean lvUP) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnCharacterHonorExp.getValue());
        oPacket.EncodeInt(hLevel);
        oPacket.EncodeInt(hExp);
        oPacket.EncodeByte(lvUP);
        return oPacket.getPacket();
    }

    public static class InventoryPacket {

        public static byte[] addInventorySlot(MapleInventoryType type, Item item) {
            return addInventorySlot(type, item, false);
        }

        public static byte[] addInventorySlot(MapleInventoryType type, Item item, boolean fromDrop) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(fromDrop ? 1 : 0);
            mplew.write(1);
            mplew.write(1);

            mplew.write(GameConstants.isInBag(item.getPosition(), type.getType()) ? 9 : 0);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());

            PacketHelper.addItemInfo(mplew, item);
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] updateExpItemInventorySlot(MapleInventoryType type, Item item) {

            OutPacket oPacket = new OutPacket();
            oPacket.EncodeShort(SendPacketOpcode.OnInventoryOperation.getValue());

            oPacket.EncodeByte(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(0);

            oPacket.EncodeByte(4);
            oPacket.EncodeByte(type.getType());
            oPacket.EncodeShort(item.getPosition());

            oPacket.EncodeInt(100);
            oPacket.EncodeByte(10);

            return oPacket.getPacket();
        }

        public static byte[] updateInventorySlot(MapleInventoryType type, Item item, boolean fromDrop) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(fromDrop ? 1 : 0);
            mplew.write(1);
            mplew.write(0);
            mplew.write(GameConstants.isInBag(item.getPosition(), type.getType()) ? 6 : 1);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());
            mplew.writeShort(item.getQuantity());
            mplew.write(0);
            ///System.out.println("22");
            return mplew.getPacket();
        }

        public static byte[] moveInventoryItem(MapleInventoryType type, short src, short dst, boolean bag, boolean bothBag) {
            return moveInventoryItem(type, src, dst, (byte) -1, bag, bothBag);
        }

        public static byte[] moveInventoryItem(MapleInventoryType type, short src, short dst, short equipIndicator, boolean bag, boolean bothBag) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);

            mplew.write(bag ? (bothBag ? 8 : 5) : 2);
            mplew.write(type.getType());
            mplew.writeShort(src);
            mplew.writeShort(dst);
            if (bag) {
                mplew.writeShort(0);
            }
            if (equipIndicator != -1) {
                mplew.write(equipIndicator);
            }
            return mplew.getPacket();
        }

        public static byte[] moveAndMergeInventoryItem(MapleInventoryType type, short src, short dst, short total, boolean bag, boolean switchSrcDst, boolean bothBag) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(1);
            mplew.write(2);
            mplew.write(0);

            mplew.write((bag) && ((switchSrcDst) || (bothBag)) ? 7 : 3);
            mplew.write(type.getType());
            mplew.writeShort(src);

            mplew.write((bag) && ((!switchSrcDst) || (bothBag)) ? 6 : 1);
            mplew.write(type.getType());
            mplew.writeShort(dst);
            mplew.writeShort(total);
            //System.out.println("44");
            return mplew.getPacket();
        }

        public static byte[] moveAndMergeWithRestInventoryItem(MapleInventoryType type, short src, short dst, short srcQ, short dstQ, boolean bag, boolean switchSrcDst, boolean bothBag) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(1);
            mplew.write(2);
            mplew.write(0);

            mplew.write((bag) && ((switchSrcDst) || (bothBag)) ? 6 : 1);
            mplew.write(type.getType());
            mplew.writeShort(src);
            mplew.writeShort(srcQ);

            mplew.write((bag) && ((!switchSrcDst) || (bothBag)) ? 6 : 1);
            mplew.write(type.getType());
            mplew.writeShort(dst);
            mplew.writeShort(dstQ);
            //System.out.println("55");
            return mplew.getPacket();
        }

        public static byte[] clearInventoryItem(MapleInventoryType type, short slot, boolean fromDrop) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(fromDrop ? 1 : 0);
            mplew.write(1);
            mplew.write(0);

            mplew.write((slot > 100) && (type == MapleInventoryType.ETC) ? 7 : 3);
            mplew.write(type.getType());
            mplew.writeShort(slot);
            //System.out.println("66");
            return mplew.getPacket();
        }

        public static byte[] removearanpol(short slot) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(0);
            mplew.write(1);
            mplew.write(0);

            mplew.write(3);
            mplew.write(1);
            mplew.writeShort(slot);
            mplew.write(3);
            return mplew.getPacket();
        }

        public static byte[] updateSpecialItemUse(Item item, byte invType, MapleCharacter chr) {
            return updateSpecialItemUse(item, invType, item.getPosition(), false, chr);
        }

        public static byte[] updateSpecialItemUse(Item item, byte invType, short pos, boolean theShort, MapleCharacter chr) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(0);
            mplew.write(2);
            mplew.write(0);

            mplew.write(GameConstants.isInBag(pos, invType) ? 7 : 3);
            mplew.write(invType);
            mplew.writeShort(pos);
            mplew.write(0);
            mplew.write(invType);
            if ((item.getType() == 1) || (theShort)) {
                mplew.writeShort(pos);
            } else {
                mplew.write(pos);
            }
            PacketHelper.addItemInfo(mplew, item, chr);
            if (pos < 0) {
                mplew.write(2);
            }

            return mplew.getPacket();
        }

        public static byte[] updateSpecialItemUse_(Item item, byte invType, MapleCharacter chr) {
            return updateSpecialItemUse_(item, invType, item.getPosition(), chr);
        }

        public static byte[] updateSpecialItemUse_(Item item, byte invType, short pos, MapleCharacter chr) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(0);
            mplew.write(1);
            mplew.write(0);

            mplew.write(0);
            mplew.write(invType);
            if (item.getType() == 1) {
                mplew.writeShort(pos);
            } else {
                mplew.write(pos);
            }
            PacketHelper.addItemInfo(mplew, item, chr);
            if (pos < 0) {
                mplew.write(1);
            }

            return mplew.getPacket();
        }

        public static byte[] scrolledItem(Item scroll, Item item, boolean destroyed, boolean potential) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(1);
            mplew.write(destroyed ? 2 : 3);
            mplew.write(0);

            mplew.write(scroll.getQuantity() > 0 ? 1 : 3);
            mplew.write(GameConstants.getInventoryType(scroll.getItemId()).getType());
            mplew.writeShort(scroll.getPosition());
            if (scroll.getQuantity() > 0) {
                mplew.writeShort(scroll.getQuantity());
            }

            mplew.write(3);

            mplew.write(MapleInventoryType.EQUIP.getType());
            mplew.writeShort(item.getPosition());
            if (!destroyed) {
                mplew.write(0);
                mplew.write(MapleInventoryType.EQUIP.getType());
                mplew.writeShort(item.getPosition());
                PacketHelper.addItemInfo(mplew, item);
            }
            if (!potential) {
                mplew.write(7);
            } else {
                mplew.write(11);
            }

            return mplew.getPacket();
        }

        public static byte[] moveAndUpgradeItem(MapleInventoryType type, Item item, short oldpos, short newpos, MapleCharacter chr) {
            OutPacket mplew = new OutPacket();
            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(1);
            mplew.write(3);
            mplew.write(0);

            mplew.write(GameConstants.isInBag(newpos, type.getType()) ? 7 : 3);
            mplew.write(type.getType());
            mplew.writeShort(oldpos);

            mplew.write(0);
            mplew.write(1);
            mplew.writeShort(oldpos);
            PacketHelper.addItemInfo(mplew, item, chr);

            mplew.write(2);
            mplew.write(type.getType());
            mplew.writeShort(oldpos);
            mplew.writeShort(newpos);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] dropInventoryItem(MapleInventoryType type, short src) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);

            mplew.write(3);
            mplew.write(type.getType());
            mplew.writeShort(src);
            if (src < 0) {
                mplew.write(1);
            }

            return mplew.getPacket();
        }

        public static byte[] dropInventoryItemUpdate(MapleInventoryType type, Item item) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);

            mplew.write(1);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());
            mplew.writeShort(item.getQuantity());

            return mplew.getPacket();
        }

        public static byte[] getInventoryFull() {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(1);
            mplew.write(0);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] getInventoryStatus() {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] getSlotUpdate(byte invType, byte newSlots) {
            OutPacket mplew = new OutPacket();

            mplew.writeShort(SendPacketOpcode.OnInventoryGrow.getValue());
            mplew.write(invType);
            mplew.write(newSlots);

            return mplew.getPacket();
        }

        public static byte[] getShowInventoryFull() {
            return CWvsContext.InfoPacket.getShowInventoryStatus(255);
        }

        public static byte[] showItemUnavailable() {
            return CWvsContext.InfoPacket.getShowInventoryStatus(254);
        }
    }

    public static byte[] SetLinkSkill(int code, int sid, int skillid, String Name, String Playername, int cid) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnLinkedSkillInfo.getValue());
        oPacket.EncodeInt(code);
        oPacket.EncodeInt(sid);
        if (code == 0) {
            oPacket.EncodeInt(cid);
            oPacket.EncodeString(Name);
        } else if (code == 1) {
            oPacket.EncodeString(Playername);
            oPacket.EncodeString(Name);
        }
        return oPacket.getPacket();
    }

    public static byte[] giveProtoType(int skillid, int bufflength, List<Pair<CharacterTemporaryStat, Integer>> statups, int mountid, Map<CharacterTemporaryStat, List<MapleBuffStatStackedValueHolder>> stacks) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
        PacketHelper.writeBuffMask(mplew, statups);
        for (Pair<CharacterTemporaryStat, Integer> stat : statups) {
            if (!stat.left.canStack()) {
                if (stat.left != CharacterTemporaryStat.MonsterRiding) {
                    mplew.writeInt((stat.right));
                    mplew.writeInt(skillid);
                    mplew.writeInt(bufflength);
                }
            }
        }
        mplew.writeZeroBytes(3);
        mplew.writeInt(mountid);
        mplew.writeInt(skillid);
        mplew.writeInt(0);
        mplew.write(0);
        for (Pair<CharacterTemporaryStat, Integer> stat : statups) {
            if (((CharacterTemporaryStat) stat.left).canStack()) {
                mplew.writeInt(stacks.get(stat.left).size());
                for (MapleBuffStatStackedValueHolder sse : stacks.get(stat.left)) {
                    mplew.writeInt(sse.getSkillId());
                    mplew.writeInt(sse.getValue());
                    mplew.writeInt(sse.getTime());
                    mplew.writeInt(sse.getBuffLength());
                }
            }
        }
        mplew.writeInt(0);
        mplew.write(5);
        return mplew.getPacket();
    }

    public static byte[] giveMisileToHeavy(int skillId) {
        OutPacket mplew = new OutPacket();
        List statups = Collections.singletonList(new Pair<>(CharacterTemporaryStat.Mechanic, Integer.valueOf(1)));
        mplew.writeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
        PacketHelper.writeBuffMask(mplew, statups);
        mplew.writeShort(20);
        mplew.writeInt(skillId);
        mplew.writeInt(210000000);
        mplew.writeZeroBytes(7);
        mplew.write(12);
        return mplew.getPacket();
    }

    public static byte[] ChannelLieDetector(boolean v2, int v3, int v4) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnEveryChannelAntiMacroResult.getValue());
        /*
        v3                          : MAP_CODE
        v4                          : CHANNEL_NUMBER
        
        v2 == true && v4 == 255     : 모든채널의 필드 %s 에 폭탄 거짓말탐지기를 사용합니다.
        v2 == true && v4 != 255     : [%2d] 번 채널의 필드 %s 에 폭탄 거짓말탐지기를 사용합니다.
        v2 == false                 : %s 필드에 폭탄 거짓말탐지기를 사용할 수 없습니다.
         */
        oPacket.EncodeByte(v2);
        oPacket.EncodeInt(v3);
        oPacket.EncodeInt(v4);
        return oPacket.getPacket();
    }

    public static byte[] LieDetector(String user, int v4, int v34, final byte[] image) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnAntiMacroResult.getValue());
        oPacket.EncodeByte(v4);
        switch (v4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 12: {
                /*
                    00 : 캐릭터를 찾을 수 없습니다.
                    01 : 공격하지 않는 캐릭터에게는 사용할 수 없습니다.
                    02 : 이미 거짓말 탐지기로 테스트를 받은 적이 있는 캐릭터입니다.
                    03 : 현재 거짓말 탐지기로 테스트를 받고 있는 캐릭터입니다.
                    12 : 거짓말 탐지기 테스트 결과 테스트 대상자가 매크로로 적발되었습니다. 대상자의 소지 금액중 7,000메소를 보상으로 지급받게 됩니다.
                 */
                oPacket.EncodeByte(4);
                break;
            }
            case 5: {
                /*
                    01 : %s_스크린샷이 저장되었습니다. 매크로 단속 시작을 공지하였습니다.
                 */
                oPacket.EncodeString(user);
                oPacket.EncodeByte(0);
                break;
            }
            case 6: {
                /*
                    01 : %s님께 거짓말 탐지기를 사용하였습니다.
                    02 : %s_스크린샷이 저장되었습니다. 거짓말 탐지기가 발동 되었습니다.
                 */
                oPacket.EncodeByte(v34);
                oPacket.EncodeString(user);
                oPacket.EncodeByte(0);
                break;
            }
            case 7: {
                /*
                    02 : NORMAL
                    ELSE : NEW
                 */
                oPacket.EncodeByte(v34);
                oPacket.EncodeByte(1);
                oPacket.EncodeByte(0);
                if (image == null) {
                    oPacket.EncodeInt(0);
                    return oPacket.getPacket();
                }
                oPacket.EncodeInt(image.length);
                oPacket.EncodeByte(image);
                break;
            }
            case 8: {
                /*
                    01 : 거짓말 탐지기 테스트 결과 매크로로 적발되었습니다. 적발이 누적될 경우 운영자의 제재를 받을 수 있습니다.
                    02 : 운영자 단속 결과 불법 자동 사냥 프로그램 사용자로 적발되어 제재 됩니다.
                 */
                oPacket.EncodeByte(v34);
                break;
            }
            case 9: {
                /*
                    02 : %s_스크린샷이 저장되었습니다. 매크로로 적발 되었습니다.
                 */
                oPacket.EncodeByte(v34);
                oPacket.EncodeString(user);
                oPacket.EncodeByte(0);
                break;
            }
            case 10: {
                /*
                    01 : 거짓말 탐지기 테스트에 협조해 주셔서 감사합니다. 매크로가 아닌 것으로 판명되며 5,000메소를 드립니다.
                    02 : 운영자 단속에 협조해 주셔서 감사합니다.
                    03 : 거짓말 탐지기 테스트에 무사히 통과하셨습니다. 협조해 주셔서 감사합니다. Enjoy Your Life 베아트리스!
                    04 : 거짓말 탐지기 테스트에 무사히 통과하셨습니다. 협조해 주셔서 감사합니다. Enjoy Your Life 베아트리스!
                    05 : 거짓말 탐지기 테스트에 무사히 통과하셨습니다. 협조해 주셔서 감사합니다. Enjoy Your Life 베아트리스!
                 */
                oPacket.EncodeByte(v34);
                oPacket.EncodeByte(0);
                break;
            }
            case 11: {
                /*
                    02 : %s_거짓말 탐지기 테스트를 통과하였습니다.
                 */
                oPacket.EncodeByte(v34);
                oPacket.EncodeString(user);
                oPacket.EncodeByte(0);
                break;
            }
        }
        return oPacket.getPacket();
    }

    public static byte[] testCode() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 603);
        return oPacket.getPacket();
    }

    public static byte[] HD(int value) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) value);
        return oPacket.getPacket();
    }
    
    public static byte[] OnPetAutoRoot(int value) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) value);
        return oPacket.getPacket();
    }

    public static byte[] sub_BFB600(String a1) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 118);
        oPacket.EncodeString(a1);
        return oPacket.getPacket();
    }

    public static byte[] sub_C21ED0(String a1) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 138);
        oPacket.EncodeString(a1);
        return oPacket.getPacket();
    }

    public static byte[] sub_C12010(boolean v1, int v2, int v4) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 139);
        oPacket.EncodeByte(v1);
        oPacket.EncodeInt(v2);
        if (v1 == true) {
            oPacket.EncodeInt(v4);
        }
        return oPacket.getPacket();
    }

    /* 49 */
    public static byte[] expConsumeItemResult(byte iType, int userID, int itemID, int minLevel, int maxLevel, int expByUsed) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnExpConsumeItemResult.getValue());
        oPacket.EncodeByte(iType);
        oPacket.EncodeByte(true);
        oPacket.EncodeInt(userID);
        oPacket.EncodeByte(0);
        if (iType == 1) {
            oPacket.EncodeInt(0);
        }
        if (iType == 2) {
            oPacket.EncodeByte(false);
            oPacket.EncodeInt(itemID);
            if (itemID > 0) {
                oPacket.EncodeInt(minLevel);
                oPacket.EncodeInt(maxLevel);
                oPacket.EncodeInt(expByUsed);
            }
        }
        return oPacket.getPacket();
    }

    /* 57 */
    public static byte[] resultPQReward(byte type, String nameBySelected, byte indexBySelected, byte typeByFailed, int v1, int v2, int v3, int v4, int v5, int v6) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnPartyResult.getValue());
        oPacket.EncodeByte(type);
        switch (type) {
            case 47: {
                oPacket.EncodeInt(1);
                oPacket.EncodeString(nameBySelected);
                oPacket.EncodeByte(indexBySelected);
                break;
            }
            case 48: {
                oPacket.EncodeByte(typeByFailed);
                break;
            }
            case 49: {
                for (int i = 0; i < 6; i++) {
                    oPacket.EncodeByte(i);
                    int itemID = (i == 0 ? v1 : i == 1 ? v2 : i == 2 ? v3 : i == 3 ? v4 : i == 4 ? v5 : v6);
                    oPacket.EncodeInt(itemID);
                    if (itemID > 0 && (int) (itemID / 1000000) == 1) {
                        oPacket.EncodeByte(Randomizer.nextInt(100) < 30 ? 1 : 0);
                        oPacket.EncodeShort(0);
                        oPacket.EncodeShort(0);
                        oPacket.EncodeShort(0);
                    }
                }
            }
        }
        return oPacket.getPacket();
    }

    /* 79 */
    public static byte[] cashLookChangeResult(boolean success, int itemID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnCashLookChangeResult.getValue());
        oPacket.EncodeByte(success);
        oPacket.EncodeInt(itemID);
        return oPacket.getPacket();
    }

    /* 137 */
    public static byte[] resultSetStealSkill(int impecMemSkilLID, int skillId) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnSetStealSkillResult.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(skillId > 0 ? 1 : 0);
        oPacket.EncodeInt(impecMemSkilLID);
        if (skillId > 0) {
            oPacket.EncodeInt(skillId);
        }
        return oPacket.getPacket();
    }

    /* 146 */
    public static byte[] crossHunterQuestResult(int typeID, int tabID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnCrossHunterQuestResult.getValue());
        oPacket.EncodeByte(typeID);
        oPacket.EncodeShort(tabID);
        return oPacket.getPacket();
    }

    /* 147 */
    public static byte[] crossHunterShopResult(int typeID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnCrossHunterShopResult.getValue());
        oPacket.EncodeByte(typeID);
        return oPacket.getPacket();
    }

    public static byte[] cashPetPickUpOnOffResult(boolean changed, boolean on) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnCashPetPickUpOnOffResult.getValue());
        oPacket.EncodeByte(changed);
        oPacket.EncodeByte(on);
        return oPacket.getPacket();
    }
}
