package tools.packet;

import client.CharacterTemporaryStat;
import handling.ChatType;
import handling.QuestType;
import handling.SendPacketOpcode;
import handling.UIType;
import handling.channel.handler.ItemMakerHandler;
import java.util.List;
import java.util.Map;
import tools.Pair;
import tools.data.OutPacket;

public class CUserLocal {

    /* 263 */
    public static byte[] setChair(boolean stand, int itemID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SIT_RESULT.getValue());
        oPacket.EncodeByte(stand);
        if (stand) {
            oPacket.EncodeShort(itemID);
        }
        return oPacket.getPacket();
    }

    /* 264 */
    public static byte[] setEmotion(int emotion, int duration, boolean byItemOption) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.EMOTION.getValue());
        oPacket.EncodeInt(emotion);
        oPacket.EncodeInt(duration);
        oPacket.EncodeByte(byItemOption);
        return oPacket.getPacket();
    }

    /* 265 */
    public static byte[] setAndroidEmotion(int emotion, int duration) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.ANDROID_EMOTION.getValue());
        oPacket.EncodeInt(emotion);
        oPacket.EncodeInt(duration);
        return oPacket.getPacket();
    }

    /* 267 */
    public static byte[] teleport(byte portal) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.TELEPORT.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(portal);
        return oPacket.getPacket();
    }

    /* 269 */
    public static byte[] useLuckyBagSucceded(int amount) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.LUCKY_BAG_USE_SUCCEDED.getValue());
        oPacket.EncodeInt(amount);
        return oPacket.getPacket();
    }

    /* 270 */
    public static byte[] useLuckyBagFailed() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.LUCKY_BAG_USE_FAILED.getValue());
        return oPacket.getPacket();
    }

    /* 271 */
    public static byte[] questResult(QuestType type, int questID, int npcTemplateID, int secondQuestID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.QUEST_RESULT.getValue());
        oPacket.EncodeByte(type.getValue());
        switch (type) {
            case QuestRes_Start_QuestTimer: {
                oPacket.EncodeShort(1);
                oPacket.EncodeShort(questID);
                oPacket.EncodeInt(1000 * 60 * 60);
                break;
            }
            case QuestRes_End_QuestTimer: {
                oPacket.EncodeShort(1);
                oPacket.EncodeShort(questID);
                break;
            }
            case QuestRes_Start_TimeKeepQuestTimer: {
                oPacket.EncodeShort(questID);
                oPacket.EncodeInt(1000 * 60 * 60);
                break;
            }
            case QuestRes_End_TimeKeepQuestTimer: {
                oPacket.EncodeShort(1);
                oPacket.EncodeShort(questID);
                break;
            }
            case QuestRes_Act_Success: {
                oPacket.EncodeShort(questID);
                oPacket.EncodeInt(npcTemplateID);
                oPacket.EncodeShort(secondQuestID);
                break;
            }
            case QuestRes_Act_Failed_Inventory:
            case Unk_18: {
                oPacket.EncodeShort(0);
                break;
            }
            case QuestRes_Act_Failed_TimeOver: {
                oPacket.EncodeShort(0);
                break;
            }
            case Unk_19: {
                oPacket.EncodeShort(0);
                break;
            }
        }
        return oPacket.getPacket();
    }

    /* 272 */
    public static byte[] useHpAutoPotionByPet(int petID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.USE_HP_AUTO_POTION_BY_PET.getValue());
        oPacket.EncodeInt(petID);
        return oPacket.getPacket();
    }

    /* 273 */
    public static byte[] setPetFlag(int uniqueID, boolean wear, int value) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.PET_FLAG_CHANGED.getValue());
        oPacket.EncodeLong(uniqueID);
        oPacket.EncodeByte(wear);
        oPacket.EncodeShort(value);
        return oPacket.getPacket();
    }

    /* 274 */
    public static byte[] balloonMsg(String message, int width, int height) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.BALLOON_MSG.getValue());
        oPacket.EncodeString(message);
        oPacket.EncodeShort(width < 1 ? Math.max(message.length() * 10, 40) : width);
        oPacket.EncodeShort(Math.max(height, 5));
        oPacket.EncodeByte(1);
        return oPacket.getPacket();
    }

    /* 275 */
    public static byte[] playEventSound(String UOL) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.PLAY_EVENT_SOUND.getValue());
        oPacket.EncodeString(UOL);
        return oPacket.getPacket();
    }

    /* 276 */
    public static byte[] playMiniGameSound(String UOL) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.PLAY_MINIGAME_SOUND.getValue());
        oPacket.EncodeString(UOL);
        return oPacket.getPacket();
    }

    /* 277 */
    public static byte[] makerSkillResult(boolean success, int type, int itemID, int itemQuantity, List<Pair<Integer, Integer>> ReqItem, int enchantCT, int[] index, boolean catalyst, int catalystID, int reqMeso) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.MAKER_SKILL_RESULT.getValue());
        oPacket.EncodeInt(success ? 0 : 1);
        oPacket.EncodeInt(type);
        switch (type) {
            case 1:
            case 2: {
                oPacket.EncodeByte(success);
                if (!success) {
                    oPacket.EncodeInt(itemID);
                    oPacket.EncodeInt(itemQuantity);
                }
                oPacket.EncodeInt(ReqItem.size());
                for (Pair<Integer, Integer> req : ReqItem) {
                    oPacket.EncodeInt(req.getLeft());
                    oPacket.EncodeInt(req.getRight());
                }
                oPacket.EncodeInt(enchantCT);
                if (index != null) {
                    for (int enchant : index) {
                        oPacket.EncodeInt(enchant);
                    }
                }
                oPacket.EncodeByte(catalyst);
                if (catalyst) {
                    oPacket.EncodeInt(catalystID);
                }
                oPacket.EncodeInt(reqMeso);
                break;
            }
            case 3: {
                oPacket.EncodeInt(ItemMakerHandler.createCrystal(itemID));
                oPacket.EncodeInt(itemID);
                break;
            }
            case 4: {
                oPacket.EncodeInt(itemID);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(reqMeso);
                break;
            }
        }
        return oPacket.getPacket();
    }

    /* 278 */
    public static byte[] openConsultBoard() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OPEN_CONSULT_BOARD.getValue());
        return oPacket.getPacket();
    }

    /* 279 */
    public static byte[] openClassCompetitionPage() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OPEN_CLASS_COMPETITION_PAGE.getValue());
        return oPacket.getPacket();
    }

    /* 280 */
    public static byte[] openUI(UIType type) {
        OutPacket oPacket = new OutPacket(3);
        oPacket.EncodeShort(SendPacketOpcode.OPEN_UI.getValue());
        oPacket.EncodeByte(type.getValue());
        return oPacket.getPacket();
    }

    /* 281 */
    public static byte[] closeUI(UIType type) {
        OutPacket oPacket = new OutPacket(3);
        oPacket.EncodeShort(SendPacketOpcode.CLOSE_UI.getValue());
        oPacket.EncodeByte(type.getValue());
        return oPacket.getPacket();
    }

    /* 281 */
    public static byte[] closeUI(int type) {
        OutPacket oPacket = new OutPacket(3);
        oPacket.EncodeShort(SendPacketOpcode.CLOSE_UI.getValue());
        oPacket.EncodeByte(type);
        return oPacket.getPacket();
    }

    /* 282 */
    public static byte[] openUIWithOption(UIType type, int option) {
        OutPacket oPacket = new OutPacket(10);
        oPacket.EncodeShort(SendPacketOpcode.OPEN_UI_WITH_OPTION.getValue());
        oPacket.EncodeInt(type.getValue());
        oPacket.EncodeInt(option);
        return oPacket.getPacket();
    }

    /* 283 */
    public static byte[] setDirectionMode(boolean show) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SET_DIRECTION_MODE.getValue());
        oPacket.EncodeByte(show ? 1 : 0);
        oPacket.EncodeInt(0);
        return oPacket.getPacket();
    }

    /* 284 */
    public static byte[] setInGameDirectionMode(int show) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
        oPacket.EncodeByte(show > 0 ? 1 : 0);
        if (show > 0) {
            oPacket.EncodeShort(show);
        }
        return oPacket.getPacket();
    }

    /* 285 */
    public static byte[] setStandAloneMode(boolean show) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SET_STAND_ALONE_MODE.getValue());
        oPacket.EncodeByte(show ? 1 : 0);
        return oPacket.getPacket();
    }

    /* 286 */
    public static byte[] hireTutor(boolean set) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.HIRE_TUTOR.getValue());
        oPacket.EncodeByte(set ? 1 : 0);
        return oPacket.getPacket();
    }

    /* 287 */
    public static byte[] tutorMsg(int id, int duration) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.TUTOR_MSG.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(id);
        oPacket.EncodeInt(duration);
        return oPacket.getPacket();
    }

    /* 287 */
    public static byte[] tutorMsg(String message, int width, int duration) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.TUTOR_MSG.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeString(message);
        oPacket.EncodeInt(width);
        oPacket.EncodeInt(duration);
        return oPacket.getPacket();
    }

    /* 288 */
    public static byte[] modComboResponse(int combo) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.MOD_COMBO_RESPONSE.getValue());
        oPacket.EncodeInt(combo);
        return oPacket.getPacket();
    }

    /* 289 */
    public static byte[] incComboResponseByComboRecharge(int value) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.INC_COMBO_RESPONSE_BY_COMBO_RECHARGE.getValue());
        oPacket.EncodeInt(value);
        return oPacket.getPacket();
    }

    /* 290 */
    public static byte[] sub_B13F80(String userName, int v2) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(290);
        oPacket.EncodeString(userName);
        oPacket.EncodeInt(v2);
        return oPacket.getPacket();
    }

    /* 291 */
    public static byte[] openSkillGuide(int value) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OPEN_SKILL_GUIDE.getValue());
        return oPacket.getPacket();
    }

    /* 292 */
    public static byte[] noticeMsg(String msg, boolean autoSeparated) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.NOTICE_MSG.getValue());
        oPacket.EncodeString(msg);
        oPacket.EncodeByte(autoSeparated);
        return oPacket.getPacket();
    }

    /* 293 */
    public static byte[] chatMsg(ChatType colour, String msg) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.CHAT_MSG.getValue());
        oPacket.EncodeShort(colour.getValue());
        oPacket.EncodeString(msg);
        return oPacket.getPacket();
    }

    /* 294 */
    public static byte[] setUtilDlg(String text, int npcID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SET_UTIL_DLG.getValue());
        oPacket.EncodeString(text);
        oPacket.EncodeInt(npcID);
        return oPacket.getPacket();
    }

    /* 295 */
    public static byte[] setItemEffect(int itemID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SET_ITEM_EFFECT.getValue());
        oPacket.EncodeInt(itemID);
        return oPacket.getPacket();
    }

    /* 296 */
    public static byte[] setDamageMeter(int tClock) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(296);
        oPacket.EncodeInt(tClock);
        return oPacket.getPacket();
    }

    /* 297 */
    public static byte[] setTimeBombAttack(int skillID, int x, int y, int mobID, int count) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.EXPLOSION_ATTACK.getValue());
        oPacket.EncodeInt(skillID); // 스킬 코드
        oPacket.EncodeInt(x); // 어택 카운트
        oPacket.EncodeInt(y); // 몹 카운트?
        oPacket.EncodeInt(mobID); // 0이면 데미지없이 이펙트만하고 비활성화, 1이면 날라가면서 데미지 활성화
        oPacket.EncodeInt(count); // 데미지
        return oPacket.getPacket();
    }

    /* 298 */
    public static byte[] sub_AAF0F0() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(298);
        return oPacket.getPacket();
    }

    /* 299 */
    public static byte[] applyFollowResult(int mode, int type) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.FOLLOW_CHARACTER_FAILED.getValue());
        oPacket.EncodeInt(mode);
        if (mode != -2 && mode > -1) {
            oPacket.EncodeInt(type);
        }
        return oPacket.getPacket();
    }

    /* 300 */
    public static byte[] setNextShootExJablin() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SET_NEXT_SHOOT_EX_JABLIN.getValue());
        return oPacket.getPacket();
    }

    /* 301 */
    public static byte[] ultimateCharacterResult(int type) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.ULTIMATE_CHARACTER_RESULT.getValue());
        oPacket.EncodeInt(type);
        return oPacket.getPacket();
    }

    /* 302 */
    public static byte[] gatherRequestResult(int reactorOID, int msgType) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.GATHER_REQUEST_RESULT.getValue());
        oPacket.EncodeInt(reactorOID);
        oPacket.EncodeInt(msgType);
        return oPacket.getPacket();
    }

    /* 303 */
    public static byte[] useBagResult(int index, int itemID, boolean unk1, boolean unk2) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.BAG_ITEM_USE_RESULT.getValue());
        oPacket.EncodeInt(index);
        oPacket.EncodeInt(itemID);
        oPacket.EncodeByte(unk1);
        oPacket.EncodeByte(unk2);
        return oPacket.getPacket();
    }

    /* 304 */
    public static byte[] randomTeleport(byte potalID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.RANDOM_TELEPORT_KEY.getValue());
        oPacket.EncodeByte(potalID);
        return oPacket.getPacket();
    }

    /* 305 */
    public static byte[] sub_B13D30(int v2) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(305);
        oPacket.EncodeInt(v2);
        return oPacket.getPacket();
    }

    /* 307 */
    public static byte[] medalReissueResult(int itemID, int type) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.MEDAL_REISSUE_RESULT.getValue());
        oPacket.EncodeByte(type);
        oPacket.EncodeInt(itemID);
        return oPacket.getPacket();
    }

    /* 308 */
    public static byte[] dodgeSkillReady() {
        OutPacket oPacket = new OutPacket();
        //oPacket.EncodeShort(SendPacketOpcode.OnDodgeSkillReady.getValue());
        oPacket.EncodeShort(308);
        return oPacket.getPacket();
    }

    /* 309 */
    public static byte[] sub_B3D8F0(int v2) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(309);
        oPacket.EncodeInt(v2);
        return oPacket.getPacket();
    }

    /* 310 */
    public static byte[] videoByScript(String videoPath, boolean isMuted) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.VIDEO_BY_SCRIPT.getValue());
        oPacket.EncodeString(videoPath);
        oPacket.EncodeByte(isMuted);
        return oPacket.getPacket();
    }

    /* 312 */
    public static byte[] incJudgementStack(byte amount) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.INC_JUDGEMENT_STACK_RESPONSE.getValue());
        oPacket.EncodeByte(amount);
        return oPacket.getPacket();
    }

    /* 313 */
    public static byte[] incCharmByCashShopEntrance(boolean gain) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.INC_CHARM_BY_CASH_SHOP_ENTRANCE.getValue());
        oPacket.EncodeShort(313);
        oPacket.EncodeInt(gain ? 1 : 0);
        return oPacket.getPacket();
    }

    /* 314 */
    public static byte[] setBuffProtector(int itemID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SET_BUFF_PROTECTOR.getValue());
        oPacket.EncodeInt(itemID);
        return oPacket.getPacket();
    }

    /* 315 */
    public static byte[] OnIncLarknessResponse(int gage, byte type) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.INC_LARKNESS_RESPONSE.getValue());
        oPacket.EncodeInt(gage);
        oPacket.EncodeByte(type);
        return oPacket.getPacket();
    }

    public static byte[] OnSunfireGauge(int gauge) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.INC_LARKNESS_RESPONSE.getValue());
        oPacket.EncodeInt(Math.min(gauge, 9999));
        oPacket.EncodeByte(gauge <= 1 ? 1 : 2);
        return oPacket.getPacket();
    }

    public static byte[] OnEclipseGauge(int gauge) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.INC_LARKNESS_RESPONSE.getValue());
        oPacket.EncodeInt(Math.min(gauge, 9999));
        oPacket.EncodeByte(gauge >= 9999 ? 2 : 1);
        return oPacket.getPacket();
    }

    public static byte[] OnSunfire(Map<CharacterTemporaryStat, Integer> statups, int gauge, int bufflength) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
        PacketHelper.writeBuffMask(oPacket, statups);
        for (Map.Entry statup : statups.entrySet()) {
            oPacket.EncodeShort(((Integer) statup.getValue()).intValue());
            oPacket.EncodeInt(20040216);
            oPacket.EncodeInt(2100000000);
        }
        oPacket.writeZeroBytes(5);
        oPacket.EncodeInt(20040216);
        oPacket.EncodeInt(bufflength);
        oPacket.writeZeroBytes(8);
        oPacket.EncodeInt(Math.min(gauge, 10000));
        oPacket.EncodeInt(-1);
        oPacket.writeZeroBytes(4);
        return oPacket.getPacket();
    }

    public static byte[] OnEclipse(Map<CharacterTemporaryStat, Integer> statups, int gauge, int bufflength) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnTemporaryStatSet.getValue());
        PacketHelper.writeBuffMask(oPacket, statups);
        for (Map.Entry statup : statups.entrySet()) {
            oPacket.EncodeShort(((Integer) statup.getValue()).intValue());
            oPacket.EncodeInt(20040217);
            oPacket.EncodeInt(2100000000);
        }
        oPacket.writeZeroBytes(5);
        oPacket.EncodeInt(20040217);
        oPacket.EncodeInt(bufflength);
        oPacket.writeZeroBytes(8);
        oPacket.EncodeInt(Math.max(gauge, -1));
        oPacket.EncodeInt(10000);
        oPacket.writeZeroBytes(4);
        return oPacket.getPacket();
    }

    /* 316 */
    public static byte[] skillCooltimeSet(int skillID, int cooldown) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SKILL_COOLTIME_SET.getValue());
        oPacket.EncodeInt(skillID);
        oPacket.EncodeInt(cooldown);
        return oPacket.getPacket();
    }
}
