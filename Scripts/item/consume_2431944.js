var status = -1;

var a1 = [
1004234, // 로얄 반 레온 워리어 헬름
1102713, // 로얄 반 레온 워리어 케이프
1082613, // 로얄 반 레온 워리어 핸드
1052804, // 로얄 반 레온 워리어 슈트
1072972, // 로얄 반 레온 워리어 부츠
];
var a2 = [
1004235, // 로얄 반 레온 메이지 헬름
1102714, // 로얄 반 레온 메이지 케이프
1082614, // 로얄 반 레온 메이지 핸드
1052805, // 로얄 반 레온 메이지 슈트
1072973, // 로얄 반 레온 메이지 부츠
];
var a3 = [
1004236, // 로얄 반 레온 센티널 헬름
1102715, // 로얄 반 레온 센티널 케이프
1082615, // 로얄 반 레온 센티널 핸드
1052806, // 로얄 반 레온 센티널 슈트
1072974, // 로얄 반 레온 센티널 부츠
];
var a4 = [
1004237, // 로얄 반 레온 체이서 헬름
1102716, // 로얄 반 레온 체이서 케이프
1082616, // 로얄 반 레온 체이서 핸드
1052807, // 로얄 반 레온 체이서 슈트
1072975, // 로얄 반 레온 체이서 부츠
];
var a5 = [
1004238, // 로얄 반 레온 스키퍼 헬름
1102717, // 로얄 반 레온 스키퍼 케이프
1082617, // 로얄 반 레온 스키퍼 핸드
1052808, // 로얄 반 레온 스키퍼 슈트
1072976, // 로얄 반 레온 스키퍼 부츠
];
var w1 = [
1302316, // 로얄 반 레온 세이버
1312186, // 로얄 반 레온 엑스
1322237, // 로얄 반 레온 해머
1402237, // 로얄 반 레온 스워드
1402237, // 로얄 반 레온 투 핸드 엑스
1422186, // 로얄 반 레온 투 핸드 해머
1432201, // 로얄 반 레온 스피어
1442255, // 로얄 반 레온 핼버드
1542102, // 로얄 반 레온 카타나
];
var w2 = [
1372208, // 로얄 반 레온 완드
1382246, // 로얄 반 레온 스태프
1552103, // 로얄 반 레온 채선
1252087, // 로얄 반 레온 스틱
];
var w3 = [
1452239, // 로얄 반 레온 보우
1462226, // 로얄 반 레온 크로스보우
1522125, // 로얄 반 레온 듀얼 보우건
];
var w4 = [
1332261, // 로얄 반 레온 대거
1472248, // 로얄 반 레온 가즈
1362122, // 로얄 반 레온 케인
];
var w5 = [
1482203, // 로얄 반 레온 클로
1492213, // 로얄 반 레온 피스톨
1532131, // 로얄 반 레온 시즈건
];

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            nEqp = a1;
            nWea = w1;
            switch (cm.getPlayer().getJob()) {
                case 200:
                case 210:
                case 211:
                case 212:
                case 220:
                case 221:
                case 222:
                case 230:
                case 231:
                case 232:
                case 1200:
                case 1210:
                case 1211:
                case 1212:
                case 2200:
                case 2210:
                case 2211:
                case 2212:
                case 2213:
                case 2214:
                case 2215:
                case 2216:
                case 2217:
                case 2218:
                case 3200:
                case 3210:
                case 3211:
                case 3212:
                case 4200:
                case 4210:
                case 4211:
                case 4212:
                case 5200:
                case 5210:
                case 5211:
                case 5212:
                case 6200:
                case 6210:
                case 6211:
                case 6212: {
                    nEqp = a2;
                    nWea = w2;
                    break;
                }
                case 300:
                case 310:
                case 311:
                case 312:
                case 320:
                case 321:
                case 322:
                case 1300:
                case 1310:
                case 1311:
                case 1312:
                case 2300:
                case 2310:
                case 2311:
                case 2312:
                case 3300:
                case 3310:
                case 3311:
                case 3312: {
                    nEqp = a3;
                    nWea = w3;
                    break;
                }
                case 400:
                case 410:
                case 411:
                case 412:
                case 420:
                case 421:
                case 422:
                case 430:
                case 431:
                case 432:
                case 433:
                case 434:
                case 1400:
                case 1410:
                case 1411:
                case 1412:
                case 2400:
                case 2410:
                case 2411:
                case 2412: {
                    nEqp = a4;
                    nWea = w4;
                    break;
                }
                case 500:
                case 510:
                case 511:
                case 512:
                case 520:
                case 521:
                case 522:
                case 530:
                case 531:
                case 532:
                case 1500:
                case 1510:
                case 1511:
                case 1512:
                case 3500:
                case 3510:
                case 3511:
                case 3512:
                case 6500:
                case 6510:
                case 6511:
                case 6512: {
                    nEqp = a5;
                    nWea = w5;
                    break;
                }
            }
            var say = "　 #Cgreen#120 레벨 달성#Cgray#을 축하드립니다.\r\n\r\n　 #fUI/UIWindow.img/Quest/reward#";
            for (i = 0; i < nEqp.length; i++) {
                say += "\r\n　 #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(nEqp[i]).get("reqLevel") + ") #d#z" + nEqp[i] + "##k";
            }
            say += "\r\n#L0##Cyellow#(Lv.130) 로얄 반 레온 무기를 선택한다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            var say = "\r\n　 #Cgray#지급을 원하는 #Cgreen#로얄 반 레온 무기#Cgray#를 선택해 주세요.\r\n\r\n　 #fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < nWea.length; i++) {
                say += "\r\n#L" + i + "##d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(nWea[i]).get("reqLevel") + ") #d#z" + nWea[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            s = selection;
            var say = "#Cgray#지급 받으실 #Cgreen#로얄 반 레온 무기#Cgray#는 아래와 같습니다.\r\n";
            say += "\r\n#i" + nWea[selection] + "# #Cyellow##z" + nWea[selection] + "##k";
            cm.sendAcceptDecline(say);
            break;
        }
        case 3: {
            cm.dispose();
            if (cm.getInventory(1).getNumFreeSlot() < 6) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2431944, -1);
            for (i = 0; i < nEqp.length; i++) {
                cm.gainItem(nEqp[i], 1);
            }
            cm.gainItem(nWea[s], 1);
            break;
        }
    }
}