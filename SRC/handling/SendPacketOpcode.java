package handling;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public enum SendPacketOpcode implements WritableIntValueHolder {
    
    PING,
    LOGIN_STATUS,
    CHECK_SPW,
    REGISTER_SECONDPW,
    PIN_OPERATION,
    SECONDPW_ERROR,
    SERVERLIST,
    SERVERSTATUS,
    SERVER_IP,
    CHARLIST,
    PCROOM_CHECK,
    EVENT_LIST,
    CHAR_NAME_RESPONSE,
    ADD_NEW_CHAR_ENTRY,
    DELETE_CHAR_RESPONSE,
    ENABLE_RECOMMENDED,
    SEND_RECOMMENDED,
    PART_TIME,
    CHANGE_CHANNEL,
    
    /* CWvsContext */
    OnInventoryOperation,
    OnInventoryGrow,
    OnStatChanged,
    OnTemporaryStatSet,
    OnTemporaryStatReset,
    OnForcedStatSet,
    OnForcedStatReset,
    OnChangeSkillRecordResult,
    OnChangeStealMemoryResult,
    OnResultStealSkillList,
    OnExclRequest,
    OnGivePopularityResult,
    OnMessage,
    OnMemoResult,
    OnMapTransferResult,
    OnAntiMacroResult,
    OnEveryChannelAntiMacroResult,
    OnClaimResult,
    OnSetClaimSvrAvailableTime,
    OnClaimSvrStatusChanged,
    OnSetTamingMobInfo,
    OnQuestClear,
    OnEntrustedShopCheckResult,
    OnSkillLearnItemResult,
    OnSkillResetItemResult,
    OnAbilityResetIemResult,
    OnExpConsumeItemResult,
    OnExpItemGetResult,
    OnIncCharactorSlotResult,
    OnGatherItemResult,
    OnSortItemResult,
    OnCharacterInfo,
    OnPartyResult,
    OnPartyMemberCandidateResult,
    OnPartyCandidateResult,
    OnExpedtionResult,
    OnFriendResult,
    OnGuildResult,
    OnAllianceResult,
    OnTownPortal,
    OnOpenGate,
    OnBroadcastMsg,
    OnAswanMsg,
    OnIncubatorResult,
    OnShopScannerResult,
    OnShopLinkResult,
    OnMarriageRequest,
    OnMarriageResult,
    OnWeddingGiftResult,
    OnNotifyMarriedPartnerMapTransfer,
    OnCashPetFoodResult,
    OnCashPetPickUpOnOffResult,
    OnCashLookChangeResult,
    OnSetWeekEventMessage,
    OnSetPotionDiscountRate,
    OnBridleMobCatchFail,
    OnImitatedNPCResult,
    OnImitatedNPCData,
    OnLimitedNPCDisableInfo,
    OnMonsterBookSetCard,
    OnMonsterBookSetCover,
    OnHourChanged,
    OnMiniMapOnOff,
    OnConsultAuthkeyUpdate,
    OnClassCompetitionAuthkeyUpdate,
    OnWebBoardAuthkeyUpdate,
    OnSessionValue,
    OnPartyValue,
    OnFieldSetVariable,
    OnEventVariable,
    OnBonusExpRateChanged,
    OnFamilyChartResult,
    OnFamilyInfoResult,
    OnFamilyResult,
    OnFamilyJoinRequest,
    OnFamilyJoinRequestResult,
    OnFamilyJoinAccepted,
    OnFamilyPrivilegeList,
    OnFamilyFamousPointIncResult,
    OnFamilyNotifyLoginOrLogout,
    OnFamilySetPrivilege,
    OnFamilySummonRequest,
    OnNotifyLevelUp,
    OnNotifyWedding,
    OnNotifyJobChange,
    OnSetBuyEquipExt,
    OnSetPassenserRequest,
    OnScriptProgressMessage,
    OnStaticScreenMessageOn,
    OnStaticScreenMessageOff,
    OnDataCRCCheckFailed,
    OnAdminScriptMessage,
    OnShowSlotMessage,
    OnWildHunterInfo,
    OnCreatePremiumAdeventurer,
    OnClearAnnouncedQuest,
    OnResultInstanceTable,
    OnCoolTimeSet,
    OnItemPotChanged,
    OnItemCoolTimeSet,
    OnLinkSkillResult,
    OnUnLinkedSkillInfo,
    OnLinkedSkillInfo,
    sub_BFF8B0,
    sub_BE78D0,
    sub_BEBE80,
    OnDojangRankingResult,
    sub_916360,
    sub_916AD0,
    OnShutDownMsg,
    OnSetStealSkillResult,
    sub_C21ED0,
    sub_C12010,
    OnCharacterPotentialSet,
    OnCharacterPotentialReset,
    OnCharacterHonorExp,
    sub_8A64F0,
    sub_C0A440,
    sub_BEBEE0,
    OnCrossHunterQuestResult,
    OnCrossHunterShopResult,
    sub_C0E700,
    sub_BEC0A0,
    OnMacroSysDataInit,
    
    DEATH_COUNT, // 데스카운터
    OnMentoring,
    OnBoardGameResult,
    SPECIAL_MAP_EFFECT,
    SPECIAL_MAP_SOUND,
    EVOLVING_RESULT,
    SET_CLAIM_SVR_AVAILABLE_TIME,
    SHOW_MAPLE_POINT,
    UNLOCK_SKILL,
    LOCK_SKILL,
    EVENT_PIECE,
    //148
    //149
    //나머지다모르고
    //159 이벤트진행패킷?
    //161 더블미라클타임세팅 (이벤트)?
    //162 하이퍼스킬초기화 169에는 필요없어보임
    //164 리턴스크롤 메시지 *구현예정*
    //CWVSCONTEXTONPACKET end
    SET_FIELD,//171
    SET_CASHSHOP, //172

    LOGIN_WELCOME,//191
    CLEAR_BACK_EFFECT,//175
    TRANSFER_FIELD_REQ_IGNORED,//176
    TRANSFER_CHANNEL_REQ_IGNORED,//177
    TRANSFER_PVP_REQ_IGNORED,//178
    FIELD_SPECIFIC_DATA,
    GROUP_MESSAGE,
    WHISPER,
    SUMMONITEM_INAVAILABLE,
    FIELD_EFFECT,
    FLOAT_NOTICE,
    PLAY_JUKEBOX,
    ADMIN_RESULT,//ADMIN명령어관련 패킷임
    QUIZ,
    DESC,
    CLOCK,//189
    CONTIMOVE,//190 보트무브
    CONTISTATE,//191 보트스테잍
    SET_QUEST_CLEAR,
    SET_QUEST_TIME,
    SET_OBJECT_STATE,
    DESTROY_CLOCK,
    ARIANTARENA_SHOW_RESULT,//196
    STALK_RESULT,
    PYRAMID_1,//패킷찾아봐야함198
    PYRAMID_2,//패킷찾아봐야함199
    QUICKSLOT_KEY,
    FOOTHOLD_INFO,
    REQUEST_FOOT_HOLD_INFO,
    SMART_MOB_NOTICE,
    //203 언노운
    //204 언노운
    //205 언노운
    //206언노운
    PVP_INFO,//207
    DIRECTION_REQUEST,
    CREATE_FORCE_ATOM,
    ACHIEVEMENT_RATIO,//210
    SET_QUICK_MOVE_INFO,
    ASWAN1,//시그너스가드스톤 212
    ASWAN2,//213
    ASWAN3,//214
    OnMesoExchangeResult,
    OnMixerResult,
    SendMixerRequest,
    OnUserEnterField,
    USER_LEAVE_FIELD,//216
    CHAT,//
    AD_BOARD,//218
    MINIROOM_BALLOON,
    SET_CONSUMEITEM_EFFECT,
    SHOW_ITEM_UPGRADE_EFFECT,
    //222 언노운
    SHOW_MAGNIFYING_EFFECT,
    SHOW_POTENTIAL_RESET,
    SHOW_FIREWORKS_EFFECT,//225
    //226 잠재능력전승(아마사용안할걸)
    PVP_ATTACK,
    PVP_MIST,
    //229언노운
    PVP_COOL,
    TESLA_TRIANGLE,
    FOLLOW_CHARACTER,
    SHOW_PQ_REWARD,
    CRAFT_EFFECT,
    CRAFT_COMPLETE,
    HARVESTED,
    //237언노운
    HIT_BY_USER,
    NETT_PYRAMID,//239
    SPAWN_PET,
    PET_MOVE,
    PET_ACTION,
    PET_NAME_CHANGED,
    PET_LOAD_EXCEPTION_LIST,
    //245 펫관련패킷 INT하나
    PET_ACTION_COMMAND,//246
    DRAGON_CREATED,//247
    DRAGON_MOVE,//248
    DRAGON_DELETED,//249
    ANDROID_SPAWN,//250
    ANDROID_MOVE,//251
    ANDROID_ACTION_SET,//252
    ANDROID_UPDATE,//253
    ANDROID_DEACTIVATED,//254
    //255 언노운
    USER_MOVE,//256
    //257 NULLSUB
    USER_MELEEATTACK,
    USER_SHOOTATTACK,
    USER_MAGICATTACK,
    USER_BODYATTACK,//261
    USER_SKILL_PREPARE,
    USER_MOVINGSHOOT_ATTACK_PREPARE,//263
    USER_SKILL_CANCEL,
    USER_HIT,
    REMOTE_EMOTION,
    //267 언노운
    USER_ACTIVE_EFFECT_ITEM,//268
    SHOW_TITLE,
    //270 언노운
    UPDATE_DRESS,//271
    //272
    //273
    USER_SIT,
    USER_AVATAR_MODIFIED,
    USER_EFFECT,
    USER_SET_TEMPORARY_STAT,
    USER_RESET_TEMPORARY_STAT,//278
    USER_RECEIVE_HP,
    USER_GUILDNAME_CHANGED,//280
    USER_GUILDMARK_CHANGED,//281
    LOAD_TEAM,//282 PVP관련 팀패킷....
    SHOW_HARVEST,//283
    SHOW_PVP_HP,//284
    //285~290까지스킵
    VOYD_PRESSURE,
    SPECTRAL_LIGHT,
    SKILL_EFFECT,
    /* CUserLocal::OnPacket */
    SIT_RESULT,
    EMOTION,
    ANDROID_EMOTION,
    USER_EFFECT2,
    TELEPORT,
    LUCKY_BAG_USE_SUCCEDED,
    LUCKY_BAG_USE_FAILED,
    QUEST_RESULT,
    USE_HP_AUTO_POTION_BY_PET,
    PET_FLAG_CHANGED,
    BALLOON_MSG,
    PLAY_EVENT_SOUND,
    PLAY_MINIGAME_SOUND,
    MAKER_SKILL_RESULT,
    OPEN_CONSULT_BOARD,
    OPEN_CLASS_COMPETITION_PAGE,
    OPEN_UI,
    OPEN_UI_WITH_OPTION,
    SET_DIRECTION_MODE,
    SET_IN_GAME_DIRECTION_MODE,
    SET_STAND_ALONE_MODE,
    HIRE_TUTOR,
    TUTOR_MSG,
    MOD_COMBO_RESPONSE,
    INC_COMBO_RESPONSE_BY_COMBO_RECHARGE,
    RADIO_SCHEDULE,
    OPEN_SKILL_GUIDE,
    NOTICE_MSG,
    CHAT_MSG,
    SET_UTIL_DLG,
    SET_ITEM_EFFECT,
    TIMEBOMB_ATTACK,
    EXPLOSION_ATTACK,
    SET_NEXT_SHOOT_EX_JABLIN,
    PASSIVE_MOVE,
    FOLLOW_CHARACTER_FAILED,
    GAME_MESSAGE,
    ULTIMATE_CHARACTER_RESULT,
    GATHER_REQUEST_RESULT,
    BAG_ITEM_USE_RESULT,
    RANDOM_TELEPORT_KEY,
    PVP_ICEGAGE, //360
    DIRECTION_INFO,
    MEDAL_REISSUE_RESULT,//336
    OnDodgeSkillReady,
    //337
    //338
    VIDEO_BY_SCRIPT,//339
    QUEST_ITEM_SEARCH,//340
    INC_JUDGEMENT_STACK_RESPONSE,
    INC_CHARM_BY_CASH_SHOP_ENTRANCE,
    SET_BUFF_PROTECTOR,
    INC_LARKNESS_RESPONSE,//344
    XENONBALL, //383
    ROULETTE_FAILED,
    ROULETTE_START,
    TIME_CAPSULE,
    OnAggroRankInfoName,
    //345
    //346
    //347
    SKILL_COOLTIME_SET,//348
    //349
    OnEventReward,
    OnEventRank,
    FLIP_THE_COIN,
    SPAWN_SUMMON,//350
    REMOVE_SUMMON,//351
    MOVE_SUMMON,//352
    SUMMON_ATTACK,//353
    PVP_SUMMON,//354
    //355
    //356
    SUMMON_SKILL,//357
    //358
    DAMAGE_SUMMON,//359
    
    OnMobEnterField,
    OnMobLeaveField,
    OnMobChangeController,
    OnMove,
    OnCtrlAck,
    OnStatSet,
    OnStatReset,
    OnSuspendReset,
    OnAffected,
    OnDamaged,
    OnSpecialEffectBySkill,
    OnMobCrcKeyChanged,
    OnHPIndicator,
    OnCatchEffect,
    OnStealEffect,
    OnEffectByItem,
    OnMobSpeaking,
    OnMobSkillDelay,
    OnEscortFullPath,
    OnEscortStopEndPermmision,
    OnEscortStopSay,
    OnEscortReturnBefore,
    OnNextAttack,
    OnMobTeleport,
    OnMobForcedAttack,
    OnMobTimeResist,
    OnMobAttackedByMob,
    
    MOB_REACTION,
    //389~393까지 아스완관련패킷인듯
    NPC_ENTER_FIELD,
    NPC_LEAVE_FIELD,
    //396
    NPC_CHANGE_CONTROLLER,//397
    NPC_MOVE,//398
    NPC_UPDATE_LIMITED_INFO,//399
    NPC_TEMP_SUMMON,
    NPC_RESET_SPECIAL_ACTION,
    NPC_SET_FORCE_MOVE,
    NPC_SET_FORCE_FLIP,
    NPC_SET_SPECIAL_ACTION,
    //400
    //401
    //402
    NPC_SPAWN_EFFECT,//403
    NPC_SCRIPTABLE,//404
    //405
    EMPLOYEE_ENTER_FIELD,
    EMPLOYEE_LEAVE_FIELD,
    EMPLOYEE_MINIROOM_BALLON,
    DROP_ENTER_FIELD,
    DROP_LEAVE_FIELD,//410
    MESSAGEBOX_CREATE_FAILED,
    MESSAGEBOX_ENTER_FIELD,
    MESSAGEBOX_LEAVE_FIELD,//413
    AFFECTED_AREA_CREATED,//414
    AFFECTED_AREA_REMOVED,//415
    TOWN_PORTAL_CREATED,//416
    TOWN_PORTAL_REMOVED,//417
    OPEN_GATE_CREATED,//418
    OPEN_GATE_REMOVED,//419
    REACTOR_CHANGE_STATE,//420
    //421
    REACTOR_ENTER_FIELD,//422]
    //423 인트 1개?:?
    REACTOR_LEAVE_FIELD,//424
    EXTRACTOR_ENTER_FIELD,//425
    REACTOR_RESET,
    EXTRACTOR_LEAVE_FIELD,//426
    SNOWBALL_STATE,//427
    SNOWBALL_HIT,//428
    SNOWBALL_MSG,//429
    SNOWBALL_TOUCH,//430
    COCONUT_HIT,//431
    COCONUT_SCORE,//432
    //433
    //434
    MONSTER_CARNIVAL_ENTER,//435
    MONSTER_CARNIVAL_PERSONAL_CP,//436
    MONSTER_CARNIVAL_TEAM_CP,//437
    MONSTER_CARNIVAL_STATS,//438
    MONSTER_CARNIVAL_REQUEST_RESULT,//439
    //440 위와같은 소환 결과....
    MONSTER_CARNIVAL_PROCESS_FOR_DEATH,//441
    MONSTER_CARNIVAL_SHOW_MEMBER_OUT_MSG,//442
    MONSTER_CARNIVAL_SHOW_GAME_RESULT,//443
    MONSTER_CARNIVAL_RANKING,
    ARIANT_SCORE_UPDATE,
    SHEEP_RANCH_INFO,
    SHEEP_RANCH_CLOTHES,
    WITCH_TOWER,
    ZAKUM_SHRINE,
    CHAOS_ZAKUM_SHRINE,
    PVP_TYPE,
    PVP_TRANSFORM,
    PVP_DETAILS,
    PVP_ENABLED,
    PVP_SCORE,
    PVP_RESULT,
    PVP_TEAM,
    PVP_SCOREBOARD,
    PVP_POINTS,
    PVP_KILLED,
    PVP_MODE,
    PVP_ICEKNIGHT,
    HORNTAIL_SHRINE,
    CAPTURE_FLAGS,
    CAPTURE_POSITION,
    CAPTURE_RESET,
    NPC_TALK,//482
    OPEN_NPC_SHOP,//483
    CONFIRM_SHOP_TRANSACTION,//484
    //485
    //486
    OPEN_STORAGE,//487
    MERCH_ITEM_MSG,//488
    MERCH_ITEM_STORE,//489
    RPS_GAME,//490
    MESSENGER,//491
    PLAYER_INTERACTION,//492
    DUEY,//502
    CS_CHARGE_CASH,
    CS_UPDATE,
    CS_OPERATION,
    CS_GIFT,
    FUNC_KEY_MAPPED_MAN_INIT,//511
    FUMC_KEY_MAPPED_PET_CONSUME_ITEM_INIT,//512
    FUMC_KEY_MAPPED_PET_CONSUME_MP_ITEM_INIT,//513
    ADMIN_SHOP_COMMODITY,
    ADMIN_SHOP_RESULT,
    ZAKUM_ALTAR,
    PSYCHIC_ULTIMATE, PSYCHIC_DAMAGE, PSYCHIC_ATTACK, CANCEL_PSYCHIC_GREP,
    MATRIX_SKILL, MATRIX_SKILL2, SET_DEAD, BIND_SKILL, UNLINK_SKILL_UNLOCK, LINK_SKILL,
    LUCID2_WELCOME_BARRAGE, LUCID_BUTTERFLY_ACTION, LUCID_DRAGON_CREATE, LUCID_DO_SKILL, LUCID2_STAINED_GLASS_ON_OFF,
    LUCID2_SET_FLYING_MODE, LUCID_STATUE_STATE_CHANGE, LUCID2_STAINED_GLASS_BREAK, LUCID_BUTTERFLY_CREATE,
    SHOW_SOULEFFECT_RESPONSE, ENTER_AUCTION, SHOW_CUBE_EFFECT, DAILY_GIFT, ALARM_AUCTION, MOMENT_AREA_ON_OFF_ALL,
    REMOVE_FIELDATTACK_OBJ_LIST, CHANGE_PHASE, CHANGE_MOBZONE, CHANNEL_BACK_IMG, SOUL_MATCHING, CLOSE_UI, KEEP_DRESSUP,
    DROP_STONE, BLOCK_ATTACK, TELEPORT_MONSTER, SHADOW_SERVENT_EXTEND, HAMMER_OF_TODD, CREATE_OBSTACLE, DEBUFF_OBJECT,
    UNION_PRESET, OPEN_UNION, HAKU_SPAWN, HAKU_MOVE, BEHOLDER_REVENGE, ENABLE_LOGIN, FINAL_ATTACK_REQUEST,
    ABSORBENT_EDIFICEPS, DEATH_ATTACK, ROYAL_DAMAGE, CHAINARTS_CHASE, BONUS_ATTACK_REQUEST, ENTER_FIELD_PSYCHIC_INFO,
    CHATTEXTITEM, UPDATE_SUMMON, ENFORCE_MSG, MOBSKILL_DELAY, SPAWN_SUB_SUMMON, JAGUAR_ATTACK, AIR_ATTACK,
    ZERO_MUlTITAG_REMOVE, B2BODY_RESULT, MATRIX_MULTI, LIGHTING_ATTACK, GRAND_CROSS, COMBO_INSTICT,
    SHOW_PROJECTILE_EFFECT, BLACKJACK, RANGE_ATTACK, CREATE_MAGIC_WRECK, REMOVE_MAGIC_WRECK,
    ENERGY_SPHERE, FORCE_ATOM_ATTACK, FLYING_SWORD_CREATE, STIGMA_INCINERATE_OBJECT, FLYING_SWORD_NODE, FLYING_SWORD_TARGET,
    SCREEN_ATTACK, SPEAK_MONSTER, ZAKUM_ATTACK, VIEW_CORE, DELETE_CORE, MIRACLE_CIRCULATOR, HUGNRY_MUTO, CHAINARTS_FURY, ICBM,
    BOSS_REWARD, UNSTABLE_MEMORIZE, SPIRIT_FLOW, AIRBONE, WP_UPDATE, MAPLE_EVENTLIST, FORCE_TRAINING, POISON_NOVA,
    WILL_CREATE_BULLETEYE, WILL_SET_MOONGAUGE, WILL_SET_HP, WILL_SPIDER_ATTACK, WILL_MOONGAUGE, WILL_SET_HP2, WILL_UNK, WILL_STUN, BLOCK_MOVING,
    WILL_COOLTIME_MOONGAUGE, WILL_THIRD_ONE, WILL_SPIDER, WILL_TELEPORT, WILL_POISON, WILL_POSION_REMOVE, SHAPE_VARIATION,
    VICIOUS_HAMMER;//522
    public short code = -2;

    @Override
    public void setValue(short code) {
        this.code = code;
    }
    
    public static String getOpcodeName(int value) {
        for (SendPacketOpcode opcode : SendPacketOpcode.values()) {
            if (opcode.getValue() == value) {
                return opcode.name();
            }
        }
        return "UNKNOWN";
    }

    @Override
    public short getValue() {
        return code;
    }

    public static Properties getDefaultProperties() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        FileInputStream fileInputStream = new FileInputStream("config/opcode/sendops.properties");
        props.load(fileInputStream);
        fileInputStream.close();
        return props;
    }

    static {
        reloadValues();
    }

    public static final void reloadValues() {
        try {
            ExternalCodeTableGetter.populateValues(getDefaultProperties(), values());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sendops", e);
        }
    }
}
