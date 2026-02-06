package server.maps;

import client.MapleCharacter;
import java.awt.Point;

import client.MapleClient;
import client.MapleQuestStatus;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import client.messages.MessageType;
import constants.GameConstants;
import java.util.HashMap;
import java.util.Map;
import scripting.EventManager;
import scripting.NPCScriptManager;
import server.Randomizer;
import server.MapleItemInformationProvider;
import server.Timer;
import server.Timer.EventTimer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.OverrideMonsterStats;
import server.quest.MapleQuest;
import server.quest.MapleQuest.MedalQuest;
import tools.FileoutputUtil;
import tools.packet.CField;
import server.maps.MapleNodes.DirectionInfo;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.CMobPool;

public class MapScriptMethods {

    private static final Point witchTowerPos = new Point(-60, 184);
    private static final String[] mulungEffects = {
        "무릉도장에 도전한 것을 후회하게 해주겠다! 어서 들어와봐!",
        "기다리고 있었다! 용기가 남았다면 들어와 보시지!",
        "배짱 하나는 두둑하군! 현명함과 무모함을 혼동하지말라고!",
        "무릉도장에 도전하다니 용기가 가상하군!",
        "패배의 길을 걷고싶다면 들어오라고!"};

    private static enum onFirstUserEnter {

        //new Stuff
        PTtutor000,
        dojang_Eff,
        dojang_Msg,
        PinkBeen_before,
        onRewordMap,
        StageMsg_together,
        StageMsg_crack,
        StageMsg_davy,
        StageMsg_goddess,
        party6weatherMsg,
        StageMsg_juliet,
        StageMsg_romio,
        moonrabbit_mapEnter,
        astaroth_summon,
        boss_Ravana,
        boss_Ravana_mirror,
        killing_BonusSetting,
        killing_MapSetting,
        metro_firstSetting,
        balog_bonusSetting,
        balog_summon,
        easy_balog_summon,
        Sky_TrapFEnter,
        shammos_Fenter,
        PRaid_D_Fenter,
        PRaid_B_Fenter,
        summon_pepeking,
        Xerxes_summon,
        VanLeon_Before,
        cygnus_Summon,
        storymap_scenario,
        shammos_FStart,
        kenta_mapEnter,
        iceman_FEnter,
        iceman_Boss,
        prisonBreak_mapEnter,
        Visitor_Cube_poison,
        Visitor_Cube_Hunting_Enter_First,
        VisitorCubePhase00_Start,
        visitorCube_addmobEnter,
        Visitor_Cube_PickAnswer_Enter_First_1,
        visitorCube_medicroom_Enter,
        visitorCube_iceyunna_Enter,
        Visitor_Cube_AreaCheck_Enter_First,
        visitorCube_boomboom_Enter,
        visitorCube_boomboom2_Enter,
        CubeBossbang_Enter,
        MalayBoss_Int,
        mPark_summonBoss,
        NULL;

        private static onFirstUserEnter fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    private static enum onUserEnter {

        PTtutor000,
        PTtutor100,
        PTtutor200,
        PTtutor300,
        PTtutor301,
        PTtutor400,
        PTtutor500,
        babyPigMap,
        crash_Dragon,
        evanleaveD,
        getDragonEgg,
        meetWithDragon,
        go1010100,
        go1010200,
        go1010300,
        go1010400,
        evanPromotion,
        PromiseDragon,
        evanTogether,
        incubation_dragon,
        TD_MC_Openning,
        TD_MC_gasi,
        TD_MC_title,
        TD_LC_title,
        q31165e,
        cygnusJobTutorial,
        cygnusTest,
        startEreb,
        dojang_Msg,
        dojang_1st,
        reundodraco,
        undomorphdarco,
        explorationPoint,
        goAdventure,
        dublTuto21,
        dublTuto23,
        go10000,
        go20000,
        go30000,
        go40000,
        go50000,
        go1000000,
        go1010000,
        go1020000,
        go2000000,
        goArcher,
        goPirate,
        goRogue,
        goMagician,
        goSwordman,
        goLith,
        iceCave,
        mirrorCave,
        aranDirection,
        rienArrow,
        rien,
        check_count,
        Massacre_first,
        Massacre_result,
        aranTutorAlone,
        evanAlone,
        dojang_QcheckSet,
        Sky_StageEnter,
        outCase,
        balog_buff,
        balog_dateSet,
        Sky_BossEnter,
        Sky_GateMapEnter,
        shammos_Enter,
        shammos_Result,
        shammos_Base,
        dollCave00,
        dollCave01,
        dollCave02,
        Sky_Quest,
        enterBlackfrog,
        onSDI,
        blackSDI,
        summonIceWall,
        metro_firstSetting,
        start_itemTake,
        findvioleta,
        pepeking_effect,
        TD_MC_keycheck,
        TD_MC_gasi2,
        in_secretroom,
        sealGarden,
        TD_NC_title,
        TD_neo_BossEnter,
        PRaid_D_Enter,
        PRaid_B_Enter,
        PRaid_Revive,
        PRaid_W_Enter,
        PRaid_WinEnter,
        PRaid_FailEnter,
        Resi_tutor10,
        Resi_tutor20,
        Resi_tutor30,
        Resi_tutor40,
        Resi_tutor50,
        Resi_tutor60,
        Resi_tutor70,
        Resi_tutor80,
        Resi_tutor50_1,
        summonSchiller,
        q31102e,
        q31103s,
        jail,
        VanLeon_ExpeditionEnter,
        cygnus_ExpeditionEnter,
        knights_Summon,
        TCMobrevive,
        mPark_stageEff,
        moonrabbit_takeawayitem,
        StageMsg_crack,
        shammos_Start,
        iceman_Enter,
        prisonBreak_1stageEnter,
        VisitorleaveDirectionMode,
        visitorPT_Enter,
        VisitorCubePhase00_Enter,
        visitor_ReviveMap,
        cannon_tuto_01,
        cannon_tuto_direction,
        cannon_tuto_direction1,
        cannon_tuto_direction2,
        map_913070000,
        userInBattleSquare,
        merTutorDrecotion00,
        merTutorDrecotion10,
        merTutorDrecotion20,
        merStandAlone,
        merOutStandAlone,
        merTutorSleep00,
        merTutorSleep01,
        merTutorSleep02,
        EntereurelTW,
        ds_tuto_ill0,
        ds_tuto_0_0,
        ds_tuto_1_0,
        ds_tuto_3_0,
        ds_tuto_3_1,
        ds_tuto_4_0,
        ds_tuto_5_0,
        ds_tuto_2_prep,
        ds_tuto_1_before,
        ds_tuto_2_before,
        enter_edelstein,
        standbyAswan,
        patrty6_1stIn,
        ds_tuto_home_before,
        ds_tuto_ani,
        np_tuto_0_0,
        lightning_tuto_1_0,
        lightning_tuto_2_0,
        kaiser_tuto_0_0,
        angelic_tuto0,
        enter_23639,
        henesys_first,
        hillah_ExpeditionEnter,
        aswan_stageEff,
        AswanSuppotEnter,
        enterAswanField,
        magicLibrary,
        NULL;

        private static onUserEnter fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    private static enum directionInfo {

        merTutorDrecotion01,
        merTutorDrecotion02,
        merTutorDrecotion03,
        merTutorDrecotion04,
        merTutorDrecotion05,
        merTutorDrecotion12,
        merTutorDrecotion21,
        ds_tuto_0_1,
        ds_tuto_0_2,
        ds_tuto_0_3,
        NULL;

        private static directionInfo fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    public static void startScript_FirstUser(MapleClient c, String scriptName) {
        if (c.getPlayer() == null) {
            return;
        }
        if (c.getPlayer() != null) {
            return;
        }
        switch (onFirstUserEnter.fromString(scriptName)) {
            case dojang_Eff: {
                int temp = (c.getPlayer().getMapId() - 925000000) / 100;
                int stage = (int) (temp - ((temp / 100) * 100));
                sendDojoClock(c, getTiming(stage) * 60);
                sendDojoStart(c, stage - getDojoStageDec(stage));
                break;
            }
            case PinkBeen_before: {
                handlePinkBeanStart(c);
                break;
            }
            case onRewordMap: {
                reloadWitchTower(c);
                break;
            }
            case StageMsg_goddess: {
                switch (c.getPlayer().getMapId()) {
                    case 920010100:
                        c.getPlayer().getMap().floatNotice("아래의 포탈을 통해 휴게실로 들어가세요!", 5120019, false);
                        break;
                    case 920010200:
                        c.getPlayer().getMap().floatNotice("몬스터를 퇴치하고 작은 조각 30개를 모아주세요!", 5120019, false);
                        break;
                    case 920010300:
                        int chance1 = (int) Math.floor(Math.random() * 3) + 1;
                        int chance2 = (int) Math.floor(Math.random() * 3) + 1;
                        int chance3 = (int) Math.floor(Math.random() * 3) + 1;
                        c.getChannelServer().getEventSM().getEventManager("PQ_Orbis").setProperty("2stage_1", chance1 + "");
                        c.getChannelServer().getEventSM().getEventManager("PQ_Orbis").setProperty("2stage_2", chance2 + "");
                        c.getChannelServer().getEventSM().getEventManager("PQ_Orbis").setProperty("2stage_3", chance3 + "");
                        c.getPlayer().getMap().floatNotice("샐리온 " + c.getChannelServer().getEventSM().getEventManager("PQ_Orbis").getProperty("2stage_1") + "마리, 그류핀 " + c.getChannelServer().getEventSM().getEventManager("PQ_Orbis").getProperty("2stage_2") + "마리, 라이오너 " + c.getChannelServer().getEventSM().getEventManager("PQ_Orbis").getProperty("2stage_3") + "마리만 남기세요.", 5120019, false);
                        break;
                    case 920010400:
                        c.getPlayer().getMap().floatNotice("알맞은 여신의 LP를 찾아서 측음기를 작동시키세요!", 5120019, false);
                        break;
                    case 920010600:
                        if (c.getChannelServer().getEventSM().getEventManager("PQ_Orbis").getProperty("6stage").equals("1")) {
                            c.getPlayer().getMap().floatNotice("여신이 부활하였습니다. 여신 미네르바와 대화해 주세요.", 5120019, false);
                            break;
                        } else {
                            c.getPlayer().getMap().floatNotice("수고하셨습니다. 지금까지 수집한 조각상의 파편으로 중앙 홀의 여신상을 복원해주시고 시종 아크에게 말을 걸어 주세요.", 5120019, false);
                            break;
                        }
                    case 920010700:
                        c.getPlayer().getMap().floatNotice("정답 발판을 찾아서 꼭대기로 올라가서 레버를 조작하세요!", 5120019, false);
                        break;
                    case 920010800:
                        c.getPlayer().getMap().floatNotice("네펜데스의 씨앗을 화분에 심고, 다크 네펜데스가 자라는 화분을 찾으세요!", 5120019, false);
                        break;
                }
                break;
            }
            case StageMsg_romio: {
                switch (c.getPlayer().getMapId()) {
                    case 926100000:
                        c.getPlayer().getMap().floatNotice("연구실을 수색해서 숨겨진 문을 찾아주세요!", 5120021, false);
                        break;
                    case 926100001:
                        c.getPlayer().getMap().floatNotice("모든 몬스터를 퇴치해 주세요!", 5120021, false);
                        break;
                    case 926100100:
                        c.getPlayer().getMap().floatNotice("몬스터를 퇴치하고 얻은 액체로 금이 간 비커를 채워주세요!", 5120021, false);
                        break;
                    case 926100200:
                        if (c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Romeo").getProperty("data_a").equals("1") && c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Romeo").getProperty("data_j").equals("1")) {
                            c.getPlayer().getMap().floatNotice("알카드노와 제뉴미스트의 실험 자료를 획득하셨습니다. 로미오에게 실험 자료를 가져다 주세요.", 5120021, false);
                        } else if (c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Romeo").getProperty("data_a").equals("1")) {
                            c.getPlayer().getMap().floatNotice("알카드노의 실험 자료를 획득하셨습니다. 로미오에게 실험 자료를 가져다 주세요.", 5120021, false);
                        } else if (c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Romeo").getProperty("data_j").equals("1")) {
                            c.getPlayer().getMap().floatNotice("제뉴미스트의 실험 자료를 획득하셨습니다. 로미오에게 실험 자료를 가져다 주세요.", 5120021, false);
                        } else {
                            c.getPlayer().getMap().floatNotice("몬스터로부터 카드 키를 얻은 후, 연구실에 들어가서 실험 자료를 찾아주세요!", 5120021, false);
                        }
                        break;
                    case 926100202:
                        c.getPlayer().getMap().floatNotice("연구실에서 실험 자료를 찾아 로미오에게 가져다 주세요.", 5120021, false);
                        break;
                    case 926110202:
                        c.getPlayer().getMap().floatNotice("연구실에서 실험 자료를 찾아 로미오에게 가져다 주세요.", 5120022, false);
                        break;
                    case 926100203:
                        c.getPlayer().getMap().floatNotice("모든 몬스터를 퇴치해 주세요!", 5120021, false);
                        break;
                    case 926100300:
                        c.getPlayer().getMap().floatNotice("4개의 보안 통로를 통과하세요!", 5120021, false);
                        break;
                    case 926100301:
                    case 926110301:
                        c.getPlayer().getMap().floatNotice("정답 발판을 찾아서 통로 꼭대기로 올라가 주세요!", 5120021, false);
                        break;
                    case 926100302:
                    case 926110302:
                        c.getPlayer().getMap().floatNotice("정답 발판을 찾아서 통로 꼭대기로 올라가 주세요!", 5120021, false);
                        break;
                    case 926100303:
                    case 926110303:
                        c.getPlayer().getMap().floatNotice("정답 발판을 찾아서 통로 꼭대기로 올라가 주세요!", 5120021, false);
                        break;
                    case 926100304:
                    case 926110304:
                        c.getPlayer().getMap().floatNotice("정답 발판을 찾아서 통로 꼭대기로 올라가 주세요!", 5120021, false);
                        break;

                }
                break;
            }
            case StageMsg_juliet: {
                switch (c.getPlayer().getMapId()) {
                    case 926110000:
                        c.getPlayer().getMap().floatNotice("연구실을 수색해서 숨겨진 문을 찾아주세요!", 5120022, false);
                        break;
                    case 926110001:
                        c.getPlayer().getMap().floatNotice("모든 몬스터를 퇴치해 주세요!", 5120022, false);
                        break;
                    case 926110100:
                        c.getPlayer().getMap().floatNotice("몬스터를 퇴치하고 얻은 액체로 금이 간 비커를 채워주세요!", 5120022, false);
                        break;
                    case 926110200:
                        if (c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Juliet").getProperty("data_a").equals("1") && c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Juliet").getProperty("data_j").equals("1")) {
                            c.getPlayer().getMap().floatNotice("알카드노와 제뉴미스트의 실험 자료를 획득하셨습니다. 줄리엣에게 실험 자료를 가져다 주세요.", 5120022, false);
                        } else if (c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Juliet").getProperty("data_a").equals("1")) {
                            c.getPlayer().getMap().floatNotice("알카드노의 실험 자료를 획득하셨습니다. 줄리엣에게 실험 자료를 가져다 주세요.", 5120022, false);
                        } else if (c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Juliet").getProperty("data_j").equals("1")) {
                            c.getPlayer().getMap().floatNotice("제뉴미스트의 실험 자료를 획득하셨습니다. 줄리엣에게 실험 자료를 가져다 주세요.", 5120022, false);
                        } else {
                            c.getPlayer().getMap().floatNotice("몬스터로부터 카드 키를 얻은 후, 연구실에 들어가서 실험 자료를 찾아주세요!", 5120022, false);
                        }
                        break;
                    case 926100201:
                        c.getPlayer().getMap().floatNotice("연구실에서 실험 자료를 찾아 로미오에게 가져다 주세요.", 5120021, false);
                        break;
                    case 926110201:
                        c.getPlayer().getMap().floatNotice("연구실에서 실험 자료를 찾아 줄리엣에게 가져다 주세요.", 5120022, false);
                        break;
                    case 926110203:
                        c.getPlayer().getMap().floatNotice("모든 몬스터를 퇴치해 주세요!", 5120022, false);
                        break;
                    case 926110300:
                        c.getPlayer().getMap().floatNotice("4개의 보안 통로를 통과하세요!", 5120022, false);
                        break;
                }
                break;
            }
            case party6weatherMsg: {
                switch (c.getPlayer().getMapId()) {
                    case 930000000:
                        c.getPlayer().getMap().floatNotice("중앙의 포탈을 타고 입장해. 지금 너에게 변신 마법을 걸게.", 5120023, false);
                        break;
                    case 930000010:
                        c.getPlayer().getMap().floatNotice("본인이 누군지 헷갈리지 않도록 자신의 모습을 확인해!", 5120023, false);
                        break;
                    case 930000100:
                        c.getPlayer().getMap().floatNotice("트리로드 때문에 숲이 오염됬어. 트리로드를 모두 없애줘!", 5120023, false);
                        break;
                    case 930000200:
                        c.getPlayer().getMap().floatNotice("중앙의 웅덩이 위에서 몬스터를 없앤 후 웅덩이에서 나온 희석된 독으로 가시덤불을 없애!", 5120023, false);
                        break;
                    case 930000300:
                        c.getPlayer().getMap().floatNotice("다들 어디 가버린거야? 포탈을 타고 내가 있는 곳까지 와!", 5120023, false);
                        break;
                    case 930000400:
                        c.getPlayer().getMap().floatNotice("나에게 정화의 구슬을 받은 다음, 몬스터들을 캐치해서 몬스터 구슬 20개를 파티장이 가져와!", 5120023, false);
                        break;
                    case 930000500:
                        c.getPlayer().getMap().floatNotice("괴인의 책상 앞에 있는 상자들을 열고 보라색 마력석을 가져와!", 5120023, false);
                        break;
                    case 930000600:
                        c.getPlayer().getMap().floatNotice("괴인의 제단 위에 보라색 마력석을 올려놔봐!", 5120023, false);
                        break;
                }
                break;
            }
            case prisonBreak_mapEnter: {
                switch (c.getPlayer().getMapId()) {
                    case 921160100:
                        c.getPlayer().getMap().floatNotice("쉿! 조용히 장애물들을 피해서 탑을 벗어나셔야 합니다.", 5120053, false);
                        break;
                    case 921160200:
                        c.getPlayer().getMap().floatNotice("경비병들을 모두 물리치셔야 해요. 그렇지 않으면 그들이 다른 경비병까지 불러올꺼에요.", 5120053, false);
                        break;
                    case 921160300:
                        c.getPlayer().getMap().floatNotice("감옥으로의 접근을 막기 위해 그들이 미로를 만들어 놨어요. 공중 감옥으로 통하는 문을 찾으세요!", 5120053, false);
                        break;
                    case 921160400:
                        c.getPlayer().getMap().floatNotice("문을 지키고 있는 경비병들을 모두 처치하세요!", 5120053, false);
                        break;
                    case 921160500:
                        c.getPlayer().getMap().floatNotice("이것이 마지막 장애물이군요. 장애물을 통과해 공중 감옥으로 와주세요. ", 5120053, false);
                        break;
                    case 921160600:
                        c.getPlayer().getMap().floatNotice("경비병을 처치하고 감옥 열쇠를 되찾아 감옥 문을 열어주세요.", 5120053, false);
                        break;
                    case 921160700:
                        c.getPlayer().getMap().floatNotice("교도관을 물리치고 우리에게 자유를 되찾아주세요!", 5120053, false);
                        break;
                }
                break;
            }
            case StageMsg_davy: {
                switch (c.getPlayer().getMapId()) {
                    case 925100000:
                        c.getPlayer().getMap().floatNotice("제한된 시간 내에 모든 몬스터를 퇴치하고 해적선에 탑승하게!", 5120020, false);
                        break;
                    case 925100100:
                        c.getPlayer().getMap().floatNotice("이 곳은 데비존의 봉인이 있는 곳이네. 봉인을 풀기 위해서는 내게 말을 걸어주게.", 5120020, false);
                        break;
                    case 925100400:
                        c.getPlayer().getMap().floatNotice("제한된 시간 내에 몬스터로부터 열쇠를 얻어서 모든 문을 닫게!", 5120020, false);
                        break;
                    case 925100500:
                        c.getPlayer().getMap().floatNotice("자, 이제 마지막이네. 빨간코 해적단의 선장 데비존을 물리치게!", 5120020, false);
                        break;
                }
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("Pirate");
                if (c.getPlayer().getMapId() == 925100500 && em != null && em.getProperty("stage5") != null) {
                    int mobId = Randomizer.nextBoolean() ? 9300107 : 9300119; //lord pirate
                    final int st = Integer.parseInt(em.getProperty("stage5"));
                    switch (st) {
                        case 1:
                            mobId = Randomizer.nextBoolean() ? 9300119 : 9300105; //angry
                            break;
                        case 2:
                            mobId = Randomizer.nextBoolean() ? 9300106 : 9300105; //enraged
                            break;
                    }
                    final MapleMonster shammos = MapleLifeFactory.getMonster(mobId);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                    }
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(shammos, new Point(411, 236));
                }
                break;
            }
            case astaroth_summon: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400633), new Point(600, -26)); //rough estimate
                break;
            }
            case boss_Ravana_mirror:
            case boss_Ravana: { //event handles this so nothing for now until i find out something to do with it
                c.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(5, "라바나가 소환되었습니다!"));
                break;
            }
            case killing_BonusSetting: { //spawns monsters according to mapid
                //910320010-910320029 = Train 999 bubblings.
                //926010010-926010029 = 30 Yetis
                //926010030-926010049 = 35 Yetis
                //926010050-926010069 = 40 Yetis
                //926010070-926010089 - 50 Yetis (specialized? immortality)
                //TODO also find positions to spawn these at
                c.getPlayer().getMap().resetFully();
                c.getSession().write(CField.showEffect("killing/bonus/bonus"));
                c.getSession().write(CField.showEffect("killing/bonus/stage"));
                Point pos1 = null, pos2 = null, pos3 = null;
                int spawnPer = 0;
                int mobId = 0;
                //9700019, 9700029
                //9700021 = one thats invincible
                if (c.getPlayer().getMapId() >= 910320010 && c.getPlayer().getMapId() <= 910320029) {
                    pos1 = new Point(121, 218);
                    pos2 = new Point(396, 43);
                    pos3 = new Point(-63, 43);
                    mobId = 9700020;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926010010 && c.getPlayer().getMapId() <= 926010029) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926010030 && c.getPlayer().getMapId() <= 926010049) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 15;
                } else if (c.getPlayer().getMapId() >= 926010050 && c.getPlayer().getMapId() <= 926010069) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 20;
                } else if (c.getPlayer().getMapId() >= 926010070 && c.getPlayer().getMapId() <= 926010089) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700029;
                    spawnPer = 20;
                } else {
                    break;
                }
                for (int i = 0; i < spawnPer; i++) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos1));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos2));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos3));
                }
                c.getPlayer().startMapTimeLimitTask(120, c.getPlayer().getMap().getReturnMap());
                break;
            }
            case mPark_summonBoss: {
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getEventInstance().getProperty("boss") != null && c.getPlayer().getEventInstance().getProperty("boss").equals("0")) {
                    for (int i = 9800119; i < 9800125; i++) {
                        final MapleMonster boss = MapleLifeFactory.getMonster(i);
                        c.getPlayer().getEventInstance().registerMonster(boss);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(boss, new Point(c.getPlayer().getMap().getPortal(2).getPosition()));
                    }
                }
                break;
            }
            case shammos_Fenter: {
                if (c.getPlayer().getMapId() >= 921120100 && c.getPlayer().getMapId() <= 921120300) {
                    final MapleMonster shammos = MapleLifeFactory.getMonster(c.getPlayer().getMapId() == 921120300 ? 9300282 : 9300275);
                    if (c.getPlayer().getEventInstance() != null) {
                        int averageLevel = 0, size = 0;
                        for (MapleCharacter pl : c.getPlayer().getEventInstance().getPlayers()) {
                            averageLevel += pl.getLevel();
                            size++;
                        }
                        if (size <= 0) {
                            return;
                        }
                        averageLevel /= size;
                        shammos.changeLevel(averageLevel);
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                            c.getPlayer().getEventInstance().setProperty("HP", "10000");
                        }
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12, 0);
                    shammos.switchController(c.getPlayer(), false);
                    c.getSession().write(CMobPool.escortFullPath(shammos, c.getPlayer().getMap()));
                }
                break;
            }
            case iceman_FEnter: {
                if (c.getPlayer().getMapId() >= 932000100 && c.getPlayer().getMapId() < 932000300) {
                    final MapleMonster shammos = MapleLifeFactory.getMonster(9300438);
                    if (c.getPlayer().getEventInstance() != null) {
                        int averageLevel = 0, size = 0;
                        for (MapleCharacter pl : c.getPlayer().getEventInstance().getPlayers()) {
                            averageLevel += pl.getLevel();
                            size++;
                        }
                        if (size <= 0) {
                            return;
                        }
                        averageLevel /= size;
                        shammos.changeLevel(averageLevel);
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                            c.getPlayer().getEventInstance().setProperty("HP", averageLevel + "000");
                        }
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12, 0);
                    shammos.switchController(c.getPlayer(), false);
                    c.getSession().write(CMobPool.escortFullPath(shammos, c.getPlayer().getMap()));
                }
                break;
            }
            case PRaid_D_Fenter: {
                switch (c.getPlayer().getMapId() % 10) {
                    case 0:
                        c.getPlayer().getMap().floatNotice("몬스터를 모두 퇴치해라!", 5120033, false);
                        break;
                    case 1:
                        c.getPlayer().getMap().floatNotice("상자를 부수고, 나오는 몬스터를 모두 퇴치해라!", 5120033, false);
                        break;
                    case 2:
                        c.getPlayer().getMap().floatNotice("일등 항해사를 퇴치해라!", 5120033, false);
                        break;
                    case 3:
                        c.getPlayer().getMap().floatNotice("몬스터를 모두 퇴치해라!", 5120033, false);
                        break;
                    case 4:
                        c.getPlayer().getMap().floatNotice("몬스터를 모두 퇴치하고, 점프대를 작동시켜서 건너편으로 건너가라!", 5120033, false);
                        break;
                }
                break;
            }
            case PRaid_B_Fenter: {
                c.getPlayer().getMap().floatNotice("상대편보다 먼저 몬스터를 퇴치하라!", 5120033, false);
                break;
            }
            case Xerxes_summon: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(6160003), c.getPlayer().getPosition());
                break;
            }
            case shammos_FStart:
                c.getPlayer().getMap().floatNotice("이런! 렉스가 봉인 된 동굴로 가는 길에 몬스터가 너무 많아. 이것들로부터 모두 처리해줘.", 5120035, false);
                break;
            case kenta_mapEnter:
                switch ((c.getPlayer().getMapId() / 100) % 10) {
                    case 1:
                        c.getPlayer().getMap().floatNotice("제 목소리 들리세요? 흉폭해진 몬스터를 모두 물리쳐주세요!!", 5120052, false);
                        break;
                    case 2:
                        c.getPlayer().getMap().floatNotice("준비해 온 산소가 부족해요. 몬스터를 처치하고 공기방울 20개를 구해주세요!", 5120052, false);
                        break;
                    case 3:
                        c.getPlayer().getMap().floatNotice("여기에요! 여기! 바다 생물들이 갑자기 난폭해지는 바람에 이 곳에 고립되어 있었어요. 와주셔서 다행이에요!", 5120052, false);
                        break;
                    case 4:
                        c.getPlayer().getMap().floatNotice("아니 저렇게 커다란 물고기가... 저것이 바로 피아누스? 그것도 2마리나!! 우리를 공격해요! 물리쳐주세요!!", 5120052, false);
                        break;
                }
                break;
            case iceman_Boss: {
                c.getPlayer().getMap().floatNotice("아이스 나이트가 나타났어요! 그를 물리치고 저의 저주를 풀어주세요!", 5120051, false);
                break;
            }
            case Visitor_Cube_poison: {
                c.getPlayer().getMap().floatNotice("Eliminate all the monsters!", 5120039, false);
                break;
            }
            case Visitor_Cube_Hunting_Enter_First: {
                c.getPlayer().getMap().floatNotice("Eliminate all the Visitors!", 5120039, false);
                break;
            }
            case VisitorCubePhase00_Start: {
                c.getPlayer().getMap().floatNotice("Eliminate all the flying monsters!", 5120039, false);
                break;
            }
            case visitorCube_addmobEnter: {
                c.getPlayer().getMap().floatNotice("Eliminate all the monsters by moving around the map!", 5120039, false);
                break;
            }
            case Visitor_Cube_PickAnswer_Enter_First_1: {
                c.getPlayer().getMap().floatNotice("One of the aliens must have a clue to the way out.", 5120039, false);
                break;
            }
            case visitorCube_medicroom_Enter: {
                c.getPlayer().getMap().floatNotice("Eliminate all of the Unjust Visitors!", 5120039, false);
                break;
            }
            case visitorCube_iceyunna_Enter: {
                c.getPlayer().getMap().floatNotice("Eliminate all of the Speedy Visitors!", 5120039, false);
                break;
            }
            case Visitor_Cube_AreaCheck_Enter_First: {
                c.getPlayer().getMap().floatNotice("The switch at the top of the room requires a heavy weight.", 5120039, false);
                break;
            }
            case visitorCube_boomboom_Enter: {
                c.getPlayer().getMap().floatNotice("The enemy is powerful! Watch out!", 5120039, false);
                break;
            }
            case visitorCube_boomboom2_Enter: {
                c.getPlayer().getMap().floatNotice("This Visitor is strong! Be careful!", 5120039, false);
                break;
            }
            case CubeBossbang_Enter: {
                c.getPlayer().getMap().floatNotice("This is it! Give it your best shot!", 5120039, false);
                break;
            }
            case MalayBoss_Int:
            case storymap_scenario:
            case VanLeon_Before:
            case dojang_Msg:
            case balog_summon:
            case easy_balog_summon: { //we dont want to reset
                break;
            }
            case metro_firstSetting:
            case killing_MapSetting:
            case Sky_TrapFEnter:
            case balog_bonusSetting: { //not needed
                c.getPlayer().getMap().resetFully();
                break;
            }
            default: {
                System.out.println("Unhandled script : " + scriptName + ", type : onFirstUserEnter - MAPID " + c.getPlayer().getMapId());
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Unhandled script : " + scriptName + ", type : onFirstUserEnter - MAPID " + c.getPlayer().getMapId());
                break;
            }
        }
    }

    public static void startScript_User(final MapleClient c, String scriptName) {
        if (c.getPlayer() == null) {
            return;
        }
        if (c.getPlayer() != null) {
            NPCScriptManager.getInstance().start(c, 9000019, scriptName);
            return;
        }
        String data = "";
        switch (onUserEnter.fromString(scriptName)) {
            case cygnusTest: {
                showIntro(c, "Effect/Direction.img/cygnus/Scene" + (c.getPlayer().getMapId() == 913040006 ? 9 : (c.getPlayer().getMapId() - 913040000)));
                break;
            }
            case cygnusJobTutorial: {
                showIntro(c, "Effect/Direction.img/cygnusJobTutorial/Scene" + (c.getPlayer().getMapId() - 913040100));
                break;
            }
            case shammos_Enter: {
                break;
            }
            case iceman_Enter: { //nothing to go on inside the map
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == 932000300) {
                    NPCScriptManager.getInstance().dispose(c); //only boss map.
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 2159020);
                }
                break;
            }
            case start_itemTake: { //nothing to go on inside the map
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("PQ_Orbis");
                if (em != null && em.getProperty("pre").equals("0")) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 2013001);
                }
                break;
            }
            case PRaid_W_Enter: {
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_expPenalty", "0"));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_ElapssedTimeAtField", "0"));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_Point", "-1"));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_Bonus", "-1"));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_Total", "-1"));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_Team", ""));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_IsRevive", "0"));
                c.getPlayer().writePoint("PRaid_Point", "-1");
                c.getPlayer().writeStatus("Red_Stage", "1");
                c.getPlayer().writeStatus("Blue_Stage", "1");
                c.getPlayer().writeStatus("redTeamDamage", "0");
                c.getPlayer().writeStatus("blueTeamDamage", "0");
                break;
            }
            case TD_neo_BossEnter:
            case findvioleta: {
                c.getPlayer().getMap().resetFully();
                break;
            }
            case StageMsg_crack:
                if (c.getPlayer().getMapId() == 922010400) {
                    int q = 0;
                    for (int i = 0; i < 5; i++) {
                        q += c.getChannelServer().getMapFactory().getMap(922010401 + i).getAllMonstersThreadsafe().size();
                    }
                    if (q > 0) {
                        c.getPlayer().dropMessage(-1, "다크아이와 쉐도우아이가 총 " + q + "마리 남았다. 모두 찾아서 퇴치하자!");
                    } else {
                        if (c.getPlayer().getEventInstance().getProperty("stage2status") == null) {
                            c.getPlayer().getEventInstance().setProperty("stage2status", "clear");
                            c.getPlayer().getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                            c.getPlayer().getMap().broadcastMessage(CField.playSound("Party1/Clear"));
                            c.getPlayer().getMap().broadcastMessage(CField.environmentChange("gate", 2));
                        }
                    }
                }
                break;
            case q31102e:
                if (c.getPlayer().getQuestStatus(31102) == 1) {
                    MapleQuest.getInstance(31102).forceComplete(c.getPlayer(), 2140000);
                }
                break;
            case q31103s:
                if (c.getPlayer().getQuestStatus(31103) == 0) {
                    MapleQuest.getInstance(31103).forceComplete(c.getPlayer(), 2142003);
                }
                break;
            case Resi_tutor20:
                c.getSession().write(CField.MapEff("resistance/tutorialGuide"));
                break;
            case Resi_tutor30:
                c.getSession().write(EffectPacket.AranTutInstructionalBalloon("Effect/OnUserEff.img/guideEffect/resistanceTutorial/userTalk"));
                break;
            case Resi_tutor40:
                NPCScriptManager.getInstance().start(c, 2159012);
                break;
            case Resi_tutor50:
                c.sendPacket(CUserLocal.setStandAloneMode(false));
                c.sendPacket(CUserLocal.setDirectionMode(false));
                c.getSession().write(CWvsContext.enableActions());
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 2159006);
                break;
            case Resi_tutor50_1:
                NPCScriptManager.getInstance().start(c, 2159007);
                break;
            case Resi_tutor60:
                NPCScriptManager.getInstance().start(c, 2159007);
                break;
            case Resi_tutor70:
                showIntro(c, "Effect/Direction4.img/Resistance/TalkJ");
                break;
            case prisonBreak_1stageEnter:
            case shammos_Start:
            case moonrabbit_takeawayitem:
            case TCMobrevive:
            case cygnus_ExpeditionEnter:
            case knights_Summon:
            case VanLeon_ExpeditionEnter:
            case Resi_tutor10:
            case sealGarden:
            case in_secretroom:

            case TD_MC_keycheck:

            case userInBattleSquare:
            case summonSchiller:
            case VisitorleaveDirectionMode:
            case visitorPT_Enter:
            case VisitorCubePhase00_Enter:
            case visitor_ReviveMap:
            case PRaid_D_Enter:
            case PRaid_B_Enter:
            case PRaid_WinEnter: //handled by event
            case PRaid_FailEnter: //also
            case PRaid_Revive: //likely to subtract points or remove a life, but idc rly
            case metro_firstSetting:
            case blackSDI:
            case summonIceWall:
            case onSDI:
            case enterBlackfrog:
            case Sky_Quest: //forest that disappeared 240030102
            case dollCave00:
            case dollCave01:
            case dollCave02:
            case shammos_Base:
            case shammos_Result:
            case Sky_BossEnter:
            case Sky_GateMapEnter:
            case balog_dateSet:
            case balog_buff:
            case outCase:
            case Sky_StageEnter:
            case dojang_QcheckSet:
            case evanTogether:
            //case merStandAlone:
            case EntereurelTW:
            case aranTutorAlone:
            case evanAlone:
            case q31165e:
            case henesys_first:
            case TD_LC_title: // Desolate Moor MAP ID: 211060000
            { //no idea
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case TD_MC_gasi2: { //no idea
                c.sendPacket(CUserLocal.setStandAloneMode(false));
                c.sendPacket(CUserLocal.setDirectionMode(false)); // 가시테스트
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case merOutStandAlone: {
                c.getPlayer().getMap().resetFully();
                Point pos1 = null, pos2 = null, pos3 = null, pos4 = null, pos5 = null, pos6 = null, pos7 = null, pos8 = null, pos9 = null, pos10 = null;
                int spawnPer = 0;
                int mobId = 0;
                //9700019, 9700029
                //9700021 = one thats invincible
                if (c.getPlayer().getMapId() == 910150003) {
                    pos1 = new Point(-1904, 1);
                    pos2 = new Point(-2341, 1);
                    pos3 = new Point(-2586, 1);
                    pos4 = new Point(-2924, 1);
                    pos5 = new Point(-3525, 1);
                    pos6 = new Point(-3498, 1);
                    pos7 = new Point(-2331, -330);
                    pos8 = new Point(-2715, -330);
                    pos9 = new Point(-2131, -612);
                    pos10 = new Point(-1893, -612);
                    mobId = 9300422;
                    spawnPer = 1;
                } else {
                    break;
                }
                for (int i = 0; i < spawnPer; i++) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos1));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos2));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos3));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos4));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos5));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos6));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos7));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos8));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos9));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos10));
                }
                //c.getPlayer().startMapTimeLimitTask(120, c.getPlayer().getMap().getReturnMap());
                //if (c.getPlayer().getQuestStatus(24001) == 1) {
                //  MapleQuest.getInstance(24001).forceComplete(c.getPlayer(), 0);
                //c.getPlayer().dropMessage(5, "Quest complete.");
                //}
                break;
            }
            case merTutorDrecotion00: {
                try {
                    c.getSession().write(UIPacket.getDirectionStatus(true));
                    c.sendPacket(CUserLocal.setInGameDirectionMode(1));
                    //c.getSession().write(UIPacket.playMovie("Mercedes.avi", true));
                    final Map<Skill, SkillEntry> sa = new HashMap<>();
                    sa.put(SkillFactory.getSkill(20021181), new SkillEntry((byte) 1, (byte) 1, -1));
                    sa.put(SkillFactory.getSkill(20021166), new SkillEntry((byte) 1, (byte) 1, -1));
                    c.getPlayer().changeSkillsLevel(sa);
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }

                try {
                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/0", 2000, 0, -100, 1, 0));
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }

                try {
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)1, 8000));
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)1, 8000));
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)3, 2));
                    c.getSession().write(UIPacket.getDirectionStatus(true));
                    c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

                try {
                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/1", 2000, 0, -100, 1, 0));
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }

                try {
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)1, 8000));
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)1, 8000));
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)3, 2));
                    c.getSession().write(UIPacket.getDirectionStatus(true));
                    c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

                try {
                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/2", 2000, 0, -100, 1, 0));
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }

                try {
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)1, 8000));
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)1, 8000));
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)3, 2));
                    c.getSession().write(UIPacket.getDirectionStatus(true));
                    c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

                try {
                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/3", 2000, 0, -100, 1, 0));
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }

                try {
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)1, 8000));
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)1, 8000));
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)3, 2));
                    c.getSession().write(UIPacket.getDirectionStatus(true));
                    c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

                try {
                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/4", 2000, 0, -100, 1, 0));
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }

                try {
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)1, 8000));
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)1, 8000));
                    //c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte)3, 2));
                    c.getSession().write(UIPacket.getDirectionStatus(true));
                    c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

                try {
                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/5", 2000, 0, -100, 1, 0));
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                //c.getSession().write(UIPacket.getDirectionInfo(1, 2000));
                c.getSession().write(UIPacket.getDirectionStatus(false));
                c.sendPacket(CUserLocal.setInGameDirectionMode(0));
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                break;
            }
            case merTutorDrecotion10: {
                if (c.getPlayer().getQuestStatus(24001) < 1) {
                    try {
                        c.getSession().write(UIPacket.getDirectionStatus(true));
                        c.sendPacket(CUserLocal.setInGameDirectionMode(1));
                        c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/6", 2000, 0, -100, 1, 0));
                        //c.getSession().write(UIPacket.getDirectionInfoTest((byte)1, 2000));
                        //c.getPlayer().setDirection(0);
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                    try {
                        c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte) 1, 8000));
                        c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte) 1, 8000));
                        c.getPlayer().getClient().getSession().write(CField.UIPacket.getDirectionInfoTest((byte) 3, 2));
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/8", 2000, 0, -100, 1, 0));
                    c.getSession().write(UIPacket.getDirectionStatus(false));
                    c.sendPacket(CUserLocal.setInGameDirectionMode(0));
                    break;
                }
            }
            case merStandAlone: {

                break;
            }

            case merTutorSleep00: {
                showIntro(c, "Effect/Direction5.img/mercedesTutorial/Scene0");
                final Map<Skill, SkillEntry> sa = new HashMap<>();
                sa.put(SkillFactory.getSkill(20021181), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20021166), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20020109), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(20021110), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(20020111), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(20020112), new SkillEntry((byte) 1, (byte) 1, -1));
                c.getPlayer().changeSkillsLevel(sa);
                break;
            }
            case merTutorSleep01: {
                while (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().levelUp();
                }
                c.getPlayer().changeJob((short) 2300);
                showIntro(c, "Effect/Direction5.img/mercedesTutorial/Scene1");
                break;
            }
            case merTutorSleep02: {
                c.sendPacket(CUserLocal.setInGameDirectionMode(0));
                break;
            }
            /*  case merTutorDrecotion00: {
             c.getSession().write(UIPacket.playMovie("Mercedes.avi", true));
             final Map<Skill, SkillEntry> sa = new HashMap<>();
             sa.put(SkillFactory.getSkill(20021181), new SkillEntry((byte) 1, (byte) 1, -1));
             sa.put(SkillFactory.getSkill(20021166), new SkillEntry((byte) 1, (byte) 1, -1));
             c.getPlayer().changeSkillsLevel(sa);
             break;
             }*/
 /*case merTutorDrecotion10: {
             c.getSession().write(UIPacket.getDirectionStatus(true));
             c.sendPacket(CUserLocal.setInGameDirectionMode(1));
             c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/6", 2000, 0, -100, 1));
             c.getSession().write(UIPacket.getDirectionInfo(1, 2000));
             c.getPlayer().setDirection(0);
             break;
             }*/
            case merTutorDrecotion20: {
                try {
                    c.getSession().write(UIPacket.getDirectionStatus(true));
                    c.sendPacket(CUserLocal.setInGameDirectionMode(1));
                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/9", 2000, 0, -100, 1, 0));
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                }
                try {
                    c.getSession().write(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/elf/0", 10000, 0, -200, 0, 0));
                    c.getSession().write(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/base/0", 10000, 0, -200, 0, 0));
                    c.getSession().write(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/text01/0", 4000, 0, -200, 0, 0));
                    c.getSession().write(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/text02/0", 4000, 0, -200, 0, 0));
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                try {
                    c.getSession().write(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/text03/0", 4000, 0, -200, 0, 0));
                    c.getSession().write(CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/text04/0", 4000, 0, -200, 0, 0));
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                final Map<Skill, SkillEntry> sa = new HashMap<>();
                sa.put(SkillFactory.getSkill(20021181), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20021166), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20020109), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(20021110), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(20020111), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(20020112), new SkillEntry((byte) 1, (byte) 1, -1));
                c.getPlayer().changeSkillsLevel(sa);
                while (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().levelUp();
                }
                c.getPlayer().changeJob((short) 2300);
                c.getPlayer().resetSP(0);
                c.getPlayer().gainSP(1);
                c.getPlayer().gainSP((c.getPlayer().getLevel() - 10) * 3);
                c.getPlayer().resetStats(4, 4, 4, 4);
                MapleQuest.getInstance(24005).forceComplete(c.getPlayer(), 0);
                c.getPlayer().changeMap(101050010, 0);
                int shieldId = 1352000;
                if (shieldId != 0) {
                    Equip dShield = (Equip) MapleItemInformationProvider.getInstance().getEquipById(shieldId);
                    if (shieldId == 1099004) {
                        dShield.setPotential((byte) 1, -17);
                    }
                    dShield.setPosition((short) -10);
                    c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(dShield);
                    c.getPlayer().equipChanged();
                }
                c.getSession().write(UIPacket.getDirectionStatus(false));
                c.sendPacket(CUserLocal.setInGameDirectionMode(0));
                c.sendPacket(CUserLocal.setStandAloneMode(false));
                break;
            }
            /*
             final Map<Skill, SkillEntry> sa = new HashMap<>();
             sa.put(SkillFactory.getSkill(20021181), new SkillEntry((byte) -1, (byte) 0, -1));
             sa.put(SkillFactory.getSkill(20021166), new SkillEntry((byte) -1, (byte) 0, -1));
             sa.put(SkillFactory.getSkill(20020109), new SkillEntry((byte) 1, (byte) 1, -1));
             sa.put(SkillFactory.getSkill(20021110), new SkillEntry((byte) 1, (byte) 1, -1));
             sa.put(SkillFactory.getSkill(20020111), new SkillEntry((byte) 1, (byte) 1, -1));
             sa.put(SkillFactory.getSkill(20020112), new SkillEntry((byte) 1, (byte) 1, -1));
             c.getPlayer().changeSkillsLevel(sa);
             c.getPlayer().changeJob((short)2300);
             MapleQuest.getInstance(24005).forceComplete(c.getPlayer(), 0);
             while (c.getPlayer().getLevel() < 10) {
             c.getPlayer().levelUp();
             c.getPlayer().changeMap(101050010, 0);
             c.getSession().write(UIPacket.getDirectionStatus(false));
             c.sendPacket(CUserLocal.setInGameDirectionMode(0));
             break;
             }
             }*/
            case ds_tuto_ani: {
                c.sendPacket(CUserLocal.videoByScript("DemonSlayer1.avi", true));
                break;
            }
            case PTtutor000: {
                c.getPlayer().setDirection(1);
                NPCScriptManager.getInstance().start(c, 9000019, "PTtutor000_0");
                break;
            }
            case PTtutor100: {
                break;
            }
            case PTtutor200: {
                break;
            }
            case PTtutor300: {
                c.getPlayer().changeKeybinding(83, (byte) 1, 20031211);
                c.getPlayer().changeKeybinding(79, (byte) 1, 20031212);
                c.sendPacket(CField.getKeymap(c.getPlayer().getKeyLayout()));
                String[] args = {
                    "UI/tutorial/phantom/0/0",
                    "UI/tutorial/phantom/1/0",
                    "UI/tutorial/phantom/2/0",
                    "UI/tutorial/phantom/3/0"};
                c.getPlayer().getClient().sendPacket(CField.NPCPacket.tutorialUI(args));
                break;
            }
            case PTtutor301: {
                break;
            }
            case PTtutor400: {
                c.getSession().write(CField.showEffect("phantom/mapname2"));
                break;
            }
            case PTtutor500: {
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1402001, "PTtutor500_0");
                break;
            }
            case lightning_tuto_1_0: {
                Timer.EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        NPCScriptManager.getInstance().start(c, 2159353, "LuminusTutorial");
                    }
                }, 2500L);
                break;
            }
            /*    case lightning_tuto_2_0: {
             final Map<Skill, SkillEntry> sa = new HashMap<Skill, SkillEntry>();
             sa.put(SkillFactory.getSkill(20048000), new SkillEntry(1, (byte)1, -1L));
             sa.put(SkillFactory.getSkill(20048001), new SkillEntry(1, (byte)1, -1L));
             sa.put(SkillFactory.getSkill(20048002), new SkillEntry(1, (byte)1, -1L));
             sa.put(SkillFactory.getSkill(20048003), new SkillEntry(1, (byte)1, -1L));
             sa.put(SkillFactory.getSkill(20048004), new SkillEntry(1, (byte)1, -1L));
             sa.put(SkillFactory.getSkill(20048005), new SkillEntry(1, (byte)1, -1L));
             sa.put(SkillFactory.getSkill(20048006), new SkillEntry(1, (byte)1, -1L));
             c.getPlayer().changeSkillsLevel(sa);
             c.getPlayer().changeKeybinding(44, (byte)1, 20048000);
             c.getPlayer().changeKeybinding(45, (byte)1, 20048001);
             c.getPlayer().changeKeybinding(46, (byte)1, 20048002);
             c.getPlayer().changeKeybinding(47, (byte)1, 20048003);
             c.getPlayer().changeKeybinding(48, (byte)1, 20048004);
             c.getPlayer().changeKeybinding(48, (byte)1, 20048005);
             c.getPlayer().changeKeybinding(48, (byte)1, 20048006);
             c.getSession().write(CField.getKeymap(c.getPlayer().getKeyLayout()));
             }*/
            case np_tuto_0_0: {
                //This starts the Jett tutorial.
                try {
                    c.sendPacket(CUserLocal.setInGameDirectionMode(1));
                    c.getSession().write(CField.UIPacket.getDirectionInfoTest((byte) 2, 8000));
                    c.getSession().write(CField.UIPacket.getDirectionInfoTest((byte) 1, 8000));
                    c.getSession().write(CField.UIPacket.getDirectionInfoTest((byte) 3, 2));
                    c.getPlayer().dropMessage(-1, "비 내리는 어느 날");
                    c.getPlayer().dropMessage(-1, "비화원 심처");
                    c.removeClickedNPC();
                    Thread.sleep(11000);
                } catch (InterruptedException e) {
                }
                c.getSession().write(CField.UIPacket.getDirectionStatus(false));
                c.sendPacket(CUserLocal.setInGameDirectionMode(0));
                c.removeClickedNPC();
                c.getPlayer().dropMessage(-1, "click on ryden to get your first quest");
                // Add the following code here:
                // Effect that says: "Interior of Mysterious" 
                // Character walks to the left abit, stopping at the stairway
                // Character walks to the right abit, stopping right before the music player
                // Character walks slightly left while having a chat bubble above head saying: "..."
                // Stops at the Stairway
                // Character walks to the right, stopping at the back-end of the music player
                // Character walks to the left, stopping right infront of the music player
                // NPC Chat pops up with the character talking to him/her-self
                // Then after the NPC chat is over, the cutscene ends
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c.getPlayer().getClient(), 9270083);
                break;
            }
            case Resi_tutor80:
            case startEreb:
            case mirrorCave:
            case babyPigMap:
            case evanleaveD: {
                c.sendPacket(CUserLocal.setStandAloneMode(false));
                c.sendPacket(CUserLocal.setDirectionMode(false));
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case dojang_1st: {
                c.getPlayer().writeMulungEnergy();
                break;
            }
            case undomorphdarco:
            case reundodraco: {
                c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(2210016), -1);
                break;
            }
            case goAdventure: {
                // BUG in MSEA v.91, so let's skip this part.
                //if (GameConstants.GMS) {
                //	c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(10000));
                //} else {
                showIntro(c, "Effect/Direction3.img/goAdventure/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                //}
                break;
            }
            case crash_Dragon:
                showIntro(c, "Effect/Direction4.img/crash/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case getDragonEgg:
                showIntro(c, "Effect/Direction4.img/getDragonEgg/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case meetWithDragon:
                showIntro(c, "Effect/Direction4.img/meetWithDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case PromiseDragon:
                showIntro(c, "Effect/Direction4.img/PromiseDragon/Scene" + ("0"));
                break;
            case evanPromotion:
                switch (c.getPlayer().getMapId()) {
                    case 900090000:
                        data = "Effect/Direction4.img/promotion/Scene0" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900090001:
                        data = "Effect/Direction4.img/promotion/Scene1";
                        break;
                    case 900090002:
                        data = "Effect/Direction4.img/promotion/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900090003:
                        data = "Effect/Direction4.img/promotion/Scene3";
                        break;
                    case 900090004:
                        c.sendPacket(CUserLocal.setStandAloneMode(false));
                        c.sendPacket(CUserLocal.setDirectionMode(false));
                        c.getSession().write(CWvsContext.enableActions());
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(900010000);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                        return;
                }
                showIntro(c, data);
                break;
            case mPark_stageEff:
                c.getPlayer().dropMessage(-1, "몬스터를 모두 잡아야 다음 스테이지로 이동할 수 있습니다.");
                switch ((c.getPlayer().getMapId() % 1000) / 100) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        c.getSession().write(CField.MapEff("monsterPark/stageEff/stage"));
                        c.getSession().write(CField.MapEff("monsterPark/stageEff/number/" + (((c.getPlayer().getMapId() % 1000) / 100) + 1)));
                        break;
                    case 4:
                        if (c.getPlayer().getMapId() / 1000000 == 952) {
                            c.getSession().write(CField.MapEff("monsterPark/stageEff/final"));
                        } else {
                            c.getSession().write(CField.MapEff("monsterPark/stageEff/stage"));
                            c.getSession().write(CField.MapEff("monsterPark/stageEff/number/5"));
                        }
                        break;
                    case 5:
                        c.getSession().write(CField.MapEff("monsterPark/stageEff/final"));
                        break;
                }

                break;
            case TD_MC_title: {
                c.sendPacket(CUserLocal.setStandAloneMode(false));
                c.sendPacket(CUserLocal.setDirectionMode(false));
                c.getSession().write(CWvsContext.enableActions());
                c.getSession().write(CField.MapEff("temaD/enter/mushCatle"));
                break;
            }
            case TD_NC_title: {
                switch ((c.getPlayer().getMapId() / 100) % 10) {
                    case 0:
                        c.getSession().write(CField.MapEff("temaD/enter/teraForest")); // 252000000
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        c.getSession().write(CField.MapEff("temaD/enter/neoCity" + ((c.getPlayer().getMapId() / 100) % 10)));
                        break;
                }
                break;
            }
            case explorationPoint: {
                if (c.getPlayer().getMapId() == 104000000) {
                    c.sendPacket(CUserLocal.setStandAloneMode(false));
                    c.sendPacket(CUserLocal.setDirectionMode(false));
                    c.getSession().write(CWvsContext.enableActions());
                    c.getSession().write(CField.MapNameDisplay(c.getPlayer().getMapId()));
                }
                MedalQuest m = null;
                for (MedalQuest mq : MedalQuest.values()) {
                    for (int i : mq.maps) {
                        if (c.getPlayer().getMapId() == i) {
                            m = mq;
                            break;
                        }
                    }
                }
                if (m != null && c.getPlayer().getLevel() >= m.level && c.getPlayer().getQuestStatus(m.questid) != 2) {
                    if (c.getPlayer().getQuestStatus(m.lquestid) != 1) {
                        MapleQuest.getInstance(m.lquestid).forceStart(c.getPlayer(), 0, "0");
                    }
                    if (c.getPlayer().getQuestStatus(m.questid) != 1) {
                        MapleQuest.getInstance(m.questid).forceStart(c.getPlayer(), 0, null);
                        final StringBuilder sb = new StringBuilder("enter=");
                        for (int i = 0; i < m.maps.length; i++) {
                            sb.append("0");
                        }
                        c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                        MapleQuest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, "0");
                    }
                    String quest = c.getPlayer().getInfoQuest(m.questid - 2005);
                    if (quest.length() != m.maps.length + 6) { //enter= is 6
                        final StringBuilder sb = new StringBuilder("enter=");
                        for (int i = 0; i < m.maps.length; i++) {
                            sb.append("0");
                        }
                        quest = sb.toString();
                        c.getPlayer().updateInfoQuest(m.questid - 2005, quest);
                    }
                    final MapleQuestStatus stat = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(m.questid - 1995));
                    if (stat.getCustomData() == null) { //just a check.
                        stat.setCustomData("0");
                    }
                    int number = Integer.parseInt(stat.getCustomData());
                    final StringBuilder sb = new StringBuilder("enter=");
                    boolean changedd = false;
                    for (int i = 0; i < m.maps.length; i++) {
                        boolean changed = false;
                        if (c.getPlayer().getMapId() == m.maps[i]) {
                            if (quest.substring(i + 6, i + 7).equals("0")) {
                                sb.append("1");
                                changed = true;
                                changedd = true;
                            }
                        }
                        if (!changed) {
                            sb.append(quest.substring(i + 6, i + 7));
                        }
                    }
                    if (changedd) {
                        number++;
                        c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                        MapleQuest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, String.valueOf(number));
                        c.getPlayer().dropMessage(-1, "칭호 - " + String.valueOf(m) + " 도전 중");
                        c.getPlayer().dropMessage(-1, number + "/" + m.maps.length + "개 지역 탐험.");
                        c.getSession().write(CWvsContext.showQuestMsg("칭호 - " + String.valueOf(m) + " 도전 중\r\n" + number + "/" + m.maps.length + "개 지역 탐험"));
                    }
                }
                break;
            }
            /*
             case PTtutor000:
             c.getPlayer().getMap().floatNotice("Please click on the npc to start your adventure", 5120016);
             break;
             */
 /* case dubl2Tuto0: {
             try {
             c.getPlayer().getMap().resetFully();
             c.getSession().write(CField.NPCPacket.getCutSceneSkip());
             Thread.sleep(4000);
             } catch (InterruptedException e) {
             }
             c.sendPacket(CUserLocal.setStandAloneMode(true));
             c.getSession().write(CWvsContext.getTopMsg("The Secret Garden Depths"));
             c.getSession().write(CWvsContext.getTopMsg("On a rainy day..."));
             c.getSession().write(UIPacket.DublStart(false));
             c.getSession().write(UIPacket.DublStart(true));
             Timer.MapTimer.getInstance().schedule(new Runnable() {
             @Override
             public void run() {
             c.getSession().write(UIPacket.DublStart(false));
             c.sendPacket(CUserLocal.setStandAloneMode(false));
             }
             }, 13000);
             break;
             }*/
 /*  case dubl2Tuto10: {
             c.getSession().write(CWvsContext.getTopMsg("The Secret Garden Depths"));
             c.getSession().write(CWvsContext.getTopMsg("On a rainy day..."));
             break;
             }*/

            case dublTuto21: {
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300522), new Point(-578, 152));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300521), new Point(-358, 152));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300522), new Point(-138, 152));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300522), new Point(-82, 152));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300522), new Point(-302, 152));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300522), new Point(-522, 152));
                break;
            }

            case dublTuto23: {
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300523), new Point(-283, 152));
            }

            case go10000:
            case go1020000:
                c.sendPacket(CUserLocal.setStandAloneMode(false));
                c.sendPacket(CUserLocal.setDirectionMode(false));
                c.getSession().write(CWvsContext.enableActions());
            case go20000:
            case go30000:
            case go40000:
            case go50000:
            case go1000000:
            case go2000000:
            case go1010000:
            case go1010100:
            case go1010200:
            case go1010300:
            case enter_edelstein:
            case patrty6_1stIn:
            case go1010400: {
                c.getSession().write(CField.MapNameDisplay(c.getPlayer().getMapId()));
                break;
            }

            case ds_tuto_ill0: {
                c.getSession().write(UIPacket.getDirectionInfo(1, 6300));
                showIntro(c, "Effect/Direction6.img/DemonTutorial/SceneLogo");
                EventTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        c.sendPacket(CUserLocal.setStandAloneMode(false));
                        c.sendPacket(CUserLocal.setDirectionMode(false));
                        c.getSession().write(CWvsContext.enableActions());
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(927000000);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                    }
                }, 6300); //wtf
                break;
            }

            case ds_tuto_home_before: {
                c.getSession().write(UIPacket.getDirectionInfo(3, 1));
                c.getSession().write(UIPacket.getDirectionInfo(1, 30));
                c.getSession().write(UIPacket.getDirectionStatus(true));
                c.getSession().write(UIPacket.getDirectionInfo(3, 0));
                c.getSession().write(UIPacket.getDirectionInfo(1, 90));

                c.getSession().write(CField.showEffect("demonSlayer/text11"));
                c.getSession().write(UIPacket.getDirectionInfo(1, 4000));
                EventTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        showIntro(c, "Effect/Direction6.img/DemonTutorial/Scene2");
                    }
                }, 1000);
                break;
            }
            case ds_tuto_1_0: {
                c.getSession().write(UIPacket.getDirectionInfo(3, 1));
                c.getSession().write(UIPacket.getDirectionInfo(1, 30));
                c.getSession().write(UIPacket.getDirectionStatus(true));

                EventTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        c.getSession().write(UIPacket.getDirectionInfo(3, 0));
                        c.getSession().write(UIPacket.getDirectionInfo(4, 2159310));
                        NPCScriptManager.getInstance().dispose(c);
                        c.removeClickedNPC();
                        NPCScriptManager.getInstance().start(c, 2159310);
                    }
                }, 1000);
                break;
            }
            case ds_tuto_4_0: {
                c.sendPacket(CUserLocal.setStandAloneMode(true));
                c.sendPacket(CUserLocal.setInGameDirectionMode(1));
                c.getSession().write(UIPacket.getDirectionStatus(true));
                c.getSession().write(UIPacket.getDirectionInfo(3, 0));
                c.getSession().write(UIPacket.getDirectionInfo(4, 2159344));
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 2159344);
                break;
            }
            case cannon_tuto_01: {
                c.sendPacket(CUserLocal.setStandAloneMode(true));
                c.sendPacket(CUserLocal.setInGameDirectionMode(1));
                c.getSession().write(UIPacket.getDirectionStatus(true));
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(110), (byte) 1, (byte) 1);
                c.getSession().write(UIPacket.getDirectionInfo(3, 0));
                c.getSession().write(UIPacket.getDirectionInfo(4, 1096000));
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                NPCScriptManager.getInstance().start(c, 1096000);
                break;
            }
            case ds_tuto_5_0: {
                c.sendPacket(CUserLocal.setStandAloneMode(true));
                c.sendPacket(CUserLocal.setInGameDirectionMode(1));
                c.getSession().write(UIPacket.getDirectionStatus(true));
                c.getSession().write(UIPacket.getDirectionInfo(3, 0));
                c.getSession().write(UIPacket.getDirectionInfo(4, 2159314));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 2159314);
                break;
            }
            case ds_tuto_3_0: {
                c.getSession().write(UIPacket.getDirectionInfo(3, 1));
                c.getSession().write(UIPacket.getDirectionInfo(1, 30));
                c.getSession().write(UIPacket.getDirectionStatus(true));
                c.getSession().write(CField.showEffect("demonSlayer/text12"));
                EventTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        c.getSession().write(UIPacket.getDirectionInfo(3, 0));
                        c.getSession().write(UIPacket.getDirectionInfo(4, 2159311));
                        NPCScriptManager.getInstance().dispose(c);
                        c.removeClickedNPC();
                        NPCScriptManager.getInstance().start(c, 2159311);
                    }
                }, 1000);
                break;
            }
            case ds_tuto_3_1: {
                c.sendPacket(CUserLocal.setStandAloneMode(true));
                c.sendPacket(CUserLocal.setInGameDirectionMode(1));
                c.getSession().write(UIPacket.getDirectionStatus(true));
                if (!c.getPlayer().getMap().containsNPC(2159340)) {
                    c.getPlayer().getMap().spawnNpc(2159340, new Point(175, 0));
                    c.getPlayer().getMap().spawnNpc(2159341, new Point(300, 0));
                    c.getPlayer().getMap().spawnNpc(2159342, new Point(600, 0));
                }
                c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/tuto/balloonMsg2/0", 2000, 0, -100, 1, 0));
                c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/tuto/balloonMsg1/3", 2000, 0, -100, 1, 0));
                EventTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        c.getSession().write(UIPacket.getDirectionInfo(3, 0));
                        c.getSession().write(UIPacket.getDirectionInfo(4, 2159340));
                        NPCScriptManager.getInstance().dispose(c);
                        c.removeClickedNPC();
                        NPCScriptManager.getInstance().start(c, 2159340);
                    }
                }, 1000);
                break;
            }
            case ds_tuto_2_before: {
                c.sendPacket(CUserLocal.setInGameDirectionMode(1));
                c.getSession().write(UIPacket.getDirectionInfo(3, 1));
                c.getSession().write(UIPacket.getDirectionInfo(1, 30));
                c.getSession().write(UIPacket.getDirectionStatus(true));
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.getSession().write(UIPacket.getDirectionInfo(3, 0));
                        c.getSession().write(CField.showEffect("demonSlayer/text13"));
                        c.getSession().write(UIPacket.getDirectionInfo(1, 500));
                    }
                }, 1000);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.getSession().write(CField.showEffect("demonSlayer/text14"));
                        c.getSession().write(UIPacket.getDirectionInfo(1, 4000));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() { //맵 go 사이즈 체크 -> 있으면 다른방
                    @Override
                    public void run() {
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(927000020);
                        int i = 0;
                        int mapid = 927000020;
                        while (mapto.getCharactersSize() >= 1) {
                            i++;
                            mapid = mapid + i;
                            final MapleMap mapto1 = c.getChannelServer().getMapFactory().getMap(mapid);
                            if (mapto1.getCharactersSize() == 0) {
                                break;
                            }
                        }
                        c.getPlayer().changeMap(mapto.getCharactersSize() == 0 ? 927000020 : mapid, 0);
                        MapleQuest.getInstance(23204).forceStart(c.getPlayer(), 0, null);
                        MapleQuest.getInstance(23205).forceComplete(c.getPlayer(), 0);
                        c.sendPacket(CUserLocal.setInGameDirectionMode(0));
                        final Map<Skill, SkillEntry> sa = new HashMap<>();
                        sa.put(SkillFactory.getSkill(30011170), new SkillEntry((byte) 1, (byte) 1, -1));
                        sa.put(SkillFactory.getSkill(30011169), new SkillEntry((byte) 1, (byte) 1, -1));
                        sa.put(SkillFactory.getSkill(30011168), new SkillEntry((byte) 1, (byte) 1, -1));
                        sa.put(SkillFactory.getSkill(30011167), new SkillEntry((byte) 1, (byte) 1, -1));
                        sa.put(SkillFactory.getSkill(30010166), new SkillEntry((byte) 1, (byte) 1, -1));
                        c.getPlayer().changeSkillsLevel(sa);
                        /*c.getPlayer().changeKeybinding(44, (byte) 1, 30010166);
                         c.getPlayer().changeKeybinding(45, (byte) 1, 30011167);
                         c.getPlayer().changeKeybinding(46, (byte) 1, 30011168);
                         c.getPlayer().changeKeybinding(47, (byte) 1, 30011169);
                         c.getPlayer().changeKeybinding(48, (byte) 1, 30011170);
                         c.getSession().write(CField.getKeymap(c.getPlayer().getKeyLayout()));*/
                    }
                }, 5500);
                break;
            }
            case ds_tuto_1_before: {
                c.getSession().write(UIPacket.getDirectionInfo(3, 1));
                c.getSession().write(UIPacket.getDirectionInfo(1, 30));
                c.getSession().write(UIPacket.getDirectionStatus(true));
                EventTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        c.getSession().write(UIPacket.getDirectionInfo(3, 0));
                        c.getSession().write(CField.showEffect("demonSlayer/text8"));
                        c.getSession().write(UIPacket.getDirectionInfo(1, 500));
                    }
                }, 1000);
                EventTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        c.getSession().write(CField.showEffect("demonSlayer/text9"));
                        c.getSession().write(UIPacket.getDirectionInfo(1, 3000));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(927000010);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                    }
                }, 4500);
                break;
            }
            case ds_tuto_0_0: { //데몬 튜토
                c.getSession().write(UIPacket.getDirectionStatus(true));
                c.sendPacket(CUserLocal.setInGameDirectionMode(1));
                c.sendPacket(CUserLocal.setStandAloneMode(true));

                final Map<Skill, SkillEntry> sa = new HashMap<>();
                sa.put(SkillFactory.getSkill(30011109), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(30010110), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(30010111), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(30010185), new SkillEntry((byte) 1, (byte) 1, -1));
                c.getPlayer().changeSkillsLevel(sa);
                c.getSession().write(UIPacket.getDirectionInfoTest((byte) 3, 0));
                c.getSession().write(CField.showEffect("demonSlayer/back"));
                c.getSession().write(CField.showEffect("demonSlayer/text0"));
                c.getSession().write(UIPacket.getDirectionInfoTest((byte) 1, 500));
                c.getPlayer().setDirection(0);
                if (!c.getPlayer().getMap().containsNPC(2159307)) {
                    c.getPlayer().getMap().spawnNpc(2159307, new Point(1305, 50));
                }
                break;
            }
            case ds_tuto_2_prep: {
                if (!c.getPlayer().getMap().containsNPC(2159309)) {
                    c.getPlayer().getMap().spawnNpc(2159309, new Point(550, 50));
                }
                break;
            }
            case goArcher: {
                showIntro(c, "Effect/Direction3.img/archer/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goPirate: {
                showIntro(c, "Effect/Direction3.img/pirate/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goRogue: {
                showIntro(c, "Effect/Direction3.img/rogue/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goMagician: {
                showIntro(c, "Effect/Direction3.img/magician/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goSwordman: {
                showIntro(c, "Effect/Direction3.img/swordman/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goLith: {
                showIntro(c, "Effect/Direction3.img/goLith/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case TD_MC_Openning: {
                showIntro(c, "Effect/Direction2.img/open");
                break;
            }
            case TD_MC_gasi: { // 버섯의 성 페페킹
                showIntro(c, "Effect/Direction2.img/gasi/gasi1");
                showIntro(c, "Effect/Direction2.img/gasi/gasi2");
                showIntro(c, "Effect/Direction2.img/gasi/gasi3");
                showIntro(c, "Effect/Direction2.img/gasi/gasi4");
                showIntro(c, "Effect/Direction2.img/gasi/gasi5");
                showIntro(c, "Effect/Direction2.img/gasi/gasi6");
                showIntro(c, "Effect/Direction2.img/gasi/gasi7");
                showIntro(c, "Effect/Direction2.img/gasi/gasi8");
                showIntro(c, "Effect/Direction2.img/gasi/gasi9");
                showIntro(c, "Effect/Direction2.img/gasi/gasi22");

                break;
            }
            case pepeking_effect: { //no idea
                //   c.sendPacket(CUserLocal.setStandAloneMode());
                //   c.getSession().write(UIPacket.IntroLock(false)); // 가시테스트
                c.getSession().write(CWvsContext.enableActions());
                c.getSession().write(CField.showEffect("pepeKing/chat/nugu"));
                c.getSession().write(CField.showEffect("pepeKing/frame/B"));
                c.getSession().write(CField.showEffect("pepeKing/frame/W"));
                c.getSession().write(CField.showEffect("pepeKing/pepe/pepeW")); // 임시로 흰색처리
                break;
            }
            case aranDirection: {
                switch (c.getPlayer().getMapId()) {
                    case 914090010:
                        data = "Effect/Direction1.img/aranTutorial/Scene0";
                        break;
                    case 914090011:
                        data = "Effect/Direction1.img/aranTutorial/Scene1" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090012:
                        data = "Effect/Direction1.img/aranTutorial/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090013:
                        data = "Effect/Direction1.img/aranTutorial/Scene3";
                        break;
                    case 914090100:
                        data = "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090200:
                        data = "Effect/Direction1.img/aranTutorial/Maha";
                        break;
                    case 914090201:
                        data = "Effect/Direction1.img/aranTutorial/PoleArm";
                        break;
                }
                showIntro(c, data);
                break;
            }
            case iceCave: {
                final Map<Skill, SkillEntry> sa = new HashMap<>();
                //sa.put(SkillFactory.getSkill(20000014), new SkillEntry((byte) -1, (byte) 0, -1));
                //sa.put(SkillFactory.getSkill(20000015), new SkillEntry((byte) -1, (byte) 0, -1));
                //sa.put(SkillFactory.getSkill(20000016), new SkillEntry((byte) -1, (byte) 0, -1));
                //sa.put(SkillFactory.getSkill(20000017), new SkillEntry((byte) -1, (byte) 0, -1));
                //sa.put(SkillFactory.getSkill(20000018), new SkillEntry((byte) -1, (byte) 0, -1));
                c.getPlayer().changeSkillsLevel(sa);
                c.getSession().write(EffectPacket.ShowWZEffect("Effect/Direction1.img/aranTutorial/ClickLirin"));
                c.sendPacket(CUserLocal.setStandAloneMode(false));
                c.sendPacket(CUserLocal.setDirectionMode(false));
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case rienArrow: {
                if (c.getPlayer().getInfoQuest(21019).equals("miss=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;helper=clear");
                    c.getSession().write(EffectPacket.AranTutInstructionalBalloon("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3"));
                }
                break;
            }
            case rien: {
                if (c.getPlayer().getQuestStatus(21101) == 2 && c.getPlayer().getInfoQuest(21019).equals("miss=o;arr=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;ck=1;helper=clear");
                }
                c.sendPacket(CUserLocal.setStandAloneMode(false));
                c.sendPacket(CUserLocal.setDirectionMode(false));
                break;
            }
            case check_count: { // 옛날 황금사원 데이터
                //  if (c.getPlayer().getMapId() == 950101010 && (!c.getPlayer().haveItem(4001433, 20) || c.getPlayer().getLevel() < 50)) { //ravana Map
                //      final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(950101100); //exit Map
                //      c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                //  }
                break;
            }
            case Massacre_first: { //sends a whole bunch of shit.
                if (c.getPlayer().getPyramidSubway() == null) {
                    c.getPlayer().setPyramidSubway(new Event_PyramidSubway(c.getPlayer()));
                }
                break;
            }
            case Massacre_result: {
                //Left blank, as PyramidSubway handles everything regarding results.
                break;
            }
            case hillah_ExpeditionEnter: {
                for (MapleMapObject mon : c.getChannelServer().getMapFactory().getMap(262030300).getAllMonstersThreadsafe()) {
                    MapleMonster mob = (MapleMonster) mon;
                    if (mob.getEventInstance() == null) {
                        c.getPlayer().getEventInstance().registerMonster(mob);
                    }
                }
                break;
            }
            case standbyAswan: {
                c.getPlayer().dropMessage(MessageType.YELLOW_FADE, "Please speak through NPC Longorias");
                break;
            }
            //Credit PureMS: http://purems.googlecode.com/svn/trunk/server/maps/PureMapScriptMethods.java
            case aswan_stageEff: {
                switch ((c.getPlayer().getMapId() % 1000) / 100) {
                    case 1:
                    case 2:
                    case 3:
                        c.getSession().write(CField.showEffect("aswan/stageEff/stage"));
                        c.getSession().write(CField.showEffect("aswan/stageEff/number/" + (((c.getPlayer().getMapId() % 1000) / 100))));
                        break;
                }
                synchronized (MapScriptMethods.class) {
                    for (MapleMapObject mon : c.getPlayer().getMap().getAllMonster()) {
                        MapleMonster mob = (MapleMonster) mon;
                        if (mob.getEventInstance() == null) {
                            c.getPlayer().getEventInstance().registerMonster(mob);
                        }
                    }
                }
                break;
            }
            case AswanSuppotEnter: {

                break;
            }
            case enterAswanField: {
                break;
            }
            case magicLibrary: {
                if (c.getPlayer().getQuestStatus(20718) == 1) {
                    c.getPlayer().forceCompleteQuest(20718);
                    c.getPlayer().dropMessage(5, "You see a black shadow lurking in the background..better go tell Hersha!");
                }
                break;
            }
            default: {
                System.out.println("Unhandled script : " + scriptName + ", type : onUserEnter - MAPID " + c.getPlayer().getMapId());
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Unhandled script : " + scriptName + ", type : onUserEnter - MAPID " + c.getPlayer().getMapId());
                break;
            }
        }
    }

    private static final int getTiming(int ids) {
        if (ids <= 5) {
            return 5;
        } else if (ids >= 7 && ids <= 11) {
            return 6;
        } else if (ids >= 13 && ids <= 17) {
            return 7;
        } else if (ids >= 19 && ids <= 23) {
            return 8;
        } else if (ids >= 25 && ids <= 29) {
            return 9;
        } else if (ids >= 31 && ids <= 35) {
            return 10;
        } else if (ids >= 37 && ids <= 38) {
            return 15;
        }
        return 0;
    }

    private static final int getDojoStageDec(int ids) {
        if (ids <= 5) {
            return 0;
        } else if (ids >= 7 && ids <= 11) {
            return 1;
        } else if (ids >= 13 && ids <= 17) {
            return 2;
        } else if (ids >= 19 && ids <= 23) {
            return 3;
        } else if (ids >= 25 && ids <= 29) {
            return 4;
        } else if (ids >= 31 && ids <= 35) {
            return 5;
        } else if (ids >= 37 && ids <= 38) {
            return 6;
        }
        return 0;
    }

    private static void showIntro(final MapleClient c, final String data) {
        c.sendPacket(CUserLocal.setStandAloneMode(true));
        c.sendPacket(CUserLocal.setDirectionMode(true)); // 가시테스트
        c.getSession().write(EffectPacket.ShowWZEffect(data));
    }

    private static void sendDojoClock(MapleClient c, int time) {
        c.getSession().write(CField.getClock(time));
    }

    private static void sendDojoStart(MapleClient c, int stage) {
        c.getSession().write(CField.environmentChange("Dojang/start", 4));
        c.getSession().write(CField.environmentChange("dojang/start/stage", 3));
        c.getSession().write(CField.environmentChange("dojang/start/number/" + stage, 3));
        c.getSession().write(CField.trembleEffect(0, 1));
    }

    private static void handlePinkBeanStart(MapleClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.resetFully();
        if (!map.containsNPC(2141000)) {
            map.spawnNpc(2141000, new Point(-190, -42));
        }
    }

    private static void reloadWitchTower(MapleClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.killAllMonsters(false);

        final int level = c.getPlayer().getLevel();
        int mob;
        if (level <= 10) {
            mob = 9300367;
        } else if (level <= 20) {
            mob = 9300368;
        } else if (level <= 30) {
            mob = 9300369;
        } else if (level <= 40) {
            mob = 9300370;
        } else if (level <= 50) {
            mob = 9300371;
        } else if (level <= 60) {
            mob = 9300372;
        } else if (level <= 70) {
            mob = 9300373;
        } else if (level <= 80) {
            mob = 9300374;
        } else if (level <= 90) {
            mob = 9300375;
        } else if (level <= 100) {
            mob = 9300376;
        } else {
            mob = 9300377;
        }
        MapleMonster theMob = MapleLifeFactory.getMonster(mob);
        OverrideMonsterStats oms = new OverrideMonsterStats();
        oms.setOMp(theMob.getMobMaxMp());
        oms.setOExp(theMob.getMobExp());
        oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * (level / 5.0))); //10k to 4m
        theMob.setOverrideStats(oms);
        map.spawnMonsterOnGroundBelow(theMob, witchTowerPos);
    }

    public static void startDirectionInfo(MapleCharacter chr, boolean start) {
        final MapleClient c = chr.getClient();
        DirectionInfo di = chr.getMap().getDirectionInfo(start ? 0 : chr.getDirection());

        if (di != null && di.eventQ.size() > 0) {
            if (start) {
                c.sendPacket(CUserLocal.setStandAloneMode(true));
                c.getSession().write(UIPacket.getDirectionInfo(3, 4));

            } else {
                for (String s : di.eventQ) {

                    switch (directionInfo.fromString(s)) {
                        case merTutorDrecotion01: //direction info: 1 is probably the time
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/0", 2000, 0, -100, 1, 0));
                            break;
                        case merTutorDrecotion02:
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/1", 2000, 0, -100, 1, 0));
                            break;
                        case merTutorDrecotion03:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                            c.getSession().write(UIPacket.getDirectionStatus(true));
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/2", 2000, 0, -100, 1, 0));
                            break;
                        case merTutorDrecotion04:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                            c.getSession().write(UIPacket.getDirectionStatus(true));
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/3", 2000, 0, -100, 1, 0));
                            break;
                        case merTutorDrecotion05:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                            c.getSession().write(UIPacket.getDirectionStatus(true));
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/4", 2000, 0, -100, 1, 0));
                            EventTimer.getInstance().schedule(new Runnable() {

                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                                    c.getSession().write(UIPacket.getDirectionStatus(true));
                                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/5", 2000, 0, -100, 1, 0));
                                }
                            }, 2000);
                            EventTimer.getInstance().schedule(new Runnable() {

                                public void run() {
                                    c.sendPacket(CUserLocal.setInGameDirectionMode(0));
                                    c.getSession().write(CWvsContext.enableActions());
                                }
                            }, 4000);
                            break;
                        case merTutorDrecotion12:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                            c.getSession().write(UIPacket.getDirectionStatus(true));
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/8", 2000, 0, -100, 1, 0));
                            c.sendPacket(CUserLocal.setInGameDirectionMode(0));
                            break;
                        case merTutorDrecotion21:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 1));
                            c.getSession().write(UIPacket.getDirectionStatus(true));
                            MapleMap mapto = c.getChannelServer().getMapFactory().getMap(910150005);
                            c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                            break;
                        case ds_tuto_0_2:

                            c.getSession().write(CField.showEffect("demonSlayer/text1"));
                            break;
                        case ds_tuto_0_1:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                            break;
                        case ds_tuto_0_3:
                            c.getSession().write(CField.showEffect("demonSlayer/text2"));
                            EventTimer.getInstance().schedule(new Runnable() {

                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(1, 4000));
                                    c.getSession().write(CField.showEffect("demonSlayer/text3"));
                                }
                            }, 2000);
                            EventTimer.getInstance().schedule(new Runnable() {

                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(1, 500));
                                    c.getSession().write(CField.showEffect("demonSlayer/text4"));
                                }
                            }, 6000);
                            EventTimer.getInstance().schedule(new Runnable() {

                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(1, 4000));
                                    c.getSession().write(CField.showEffect("demonSlayer/text5"));
                                }
                            }, 6500);
                            EventTimer.getInstance().schedule(new Runnable() {

                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(1, 500));
                                    c.getSession().write(CField.showEffect("demonSlayer/text6"));
                                }
                            }, 10500);
                            EventTimer.getInstance().schedule(new Runnable() {

                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(1, 4000));
                                    c.getSession().write(CField.showEffect("demonSlayer/text7"));
                                }
                            }, 11000);
                            EventTimer.getInstance().schedule(new Runnable() {

                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(4, 2159307));
                                    NPCScriptManager.getInstance().dispose(c);
                                    NPCScriptManager.getInstance().start(c, 2159307);
                                }
                            }, 15000);
                            break;
                    }
                }
            }
            c.getSession().write(UIPacket.getDirectionInfo(1, 2000));
            chr.setDirection(chr.getDirection() + 1);
            if (chr.getMap().getDirectionInfo(chr.getDirection()) == null) {
                chr.setDirection(-1);
            }
        } else if (start) {
            switch (chr.getMapId()) {
                //hack
                case 931050300:
                    while (chr.getLevel() < 10) {
                        chr.levelUp();
                    }

                    final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(931050000);
                    chr.changeMap(mapto, mapto.getPortal(0));
                    break;
            }
        }
    }
}
