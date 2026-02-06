var status = -1;

var pensalirEqp = new Array();

var hatID = [
1004229, // 펜살리르 배틀 헬름
1004230, // 펜살리르 메이지 샐릿
1004231, // 펜살리르 센티널 캡
1004232, // 펜살리르 체이서 햇
1004233, // 펜살리르 스키퍼 햇
];

var coatID = [
1052799, // 펜살리르 배틀 메일
1052800, // 펜살리르 메이지 로브
1052801, // 펜살리르 센티널 슈트
1052802, // 펜살리르 체이서 아머
1052803, // 펜살리르 스키퍼 코트
];

var gloveID = [
1082608, // 펜살리르 배틀 글러브
1082609, // 펜살리르 메이지 글러브
1082610, // 펜살리르 센티널 글러브
1082611, // 펜살리르 체이서 글러브
1082612, // 펜살리르 스키퍼 글러브
];

var shoesID = [
1072967, // 펜살리르 배틀 부츠
1072968, // 펜살리르 메이지 부츠
1072969, // 펜살리르 센티널 부츠
1072970, // 펜살리르 체이서 부츠
1072971, // 펜살리르 스키퍼 부츠
];

var capeID = [
1102718, // 펜살리르 배틀 케이프
1102719, // 펜살리르 메이지 케이프
1102720, // 펜살리르 센티널 케이프
1102721, // 펜살리르 체이서 케이프
1102722, // 펜살리르 스키퍼 케이프
];

var weaponID = [
1302315, // 우트가르드 세이버
1312185, // 우트가르드 엑스
1322236, // 우트가르드 해머
1402236, // 우트가르드 투 핸드 소드
1412164, // 우트가르드 투 핸드 엑스
1422171, // 우트가르드 투 핸드 해머
1432200, // 우트가르드 스피어
1442254, // 우트가르드 핼버드
1542101, // 우트가르드 카타나
1372207, // 우트가르드 완드
1382245, // 우트가르드 스태프
1552102, // 우트가르드 채선
1252086, // 우트가르드 스틱
1452238, // 우트가르드 보우
1462225, // 우트가르드 크로스 보우
1522124, // 우트가르드 듀얼 보우건
1332260, // 우트가르드 대거
1342100, // 우트가르드 블레이드
1472247, // 우트가르드 가즈
1362121, // 우트가르드 케인
1482202, // 우트가르드 클로
1492212, // 우트가르드 피스톨
1532130, // 우트가르드 시즈건
1403014, // 우트가르드 무권
];

var itemID = [
[1122334, 1], // 정령의 펜던트
[2430058, 1], // 마족 메투스 패키지
[2430059, 1], // 마족 모스 패키지
[2430060, 1], // 마족 디아 패키지
[1182069, 1], // 빛나는 U&I 뱃지
[1112663, 1], // 화이트 엔젤릭 블레스 링
[1112400, 1], // 연금술사의 반지
[1112400, 1], // 연금술사의 반지
[5062002, 300], // 마스터 미라클 큐브
[2049100, 20], // 혼돈의 주문서 60%
[2450000, 10], // 사냥꾼의 행운
[2049700, 3], // 에픽 잠재 능력 부여 주문서 100%
[2049750, 3], // 유니크 잠재 능력 부여 주문서 100%
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
            var say = "지급 받을 #d펜살리르 모자#k를 선택해 주세요.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < hatID.length; i++) {
                say += "\r\n#L" + i + "##i" + hatID[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(hatID[i]).get("reqLevel") + ") #d#z" + hatID[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            pensalirEqp.push(hatID[selection]);
            var say = "지급 받을 #d펜살리르 한벌 옷#k을 선택해 주세요.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < coatID.length; i++) {
                say += "\r\n#L" + i + "##i" + coatID[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(coatID[i]).get("reqLevel") + ") #d#z" + coatID[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            pensalirEqp.push(coatID[selection]);
            var say = "지급 받을 #d펜살리르 장갑#k을 선택해 주세요.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < gloveID.length; i++) {
                say += "\r\n#L" + i + "##i" + gloveID[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(gloveID[i]).get("reqLevel") + ") #d#z" + gloveID[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 3: {
            pensalirEqp.push(gloveID[selection]);
            var say = "지급 받을 #d펜살리르 신발#k을 선택해 주세요.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < shoesID.length; i++) {
                say += "\r\n#L" + i + "##i" + shoesID[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(shoesID[i]).get("reqLevel") + ") #d#z" + shoesID[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 4: {
            pensalirEqp.push(shoesID[selection]);
            var say = "지급 받을 #d펜살리르 망토#k를 선택해 주세요.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < capeID.length; i++) {
                say += "\r\n#L" + i + "##i" + capeID[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(capeID[i]).get("reqLevel") + ") #d#z" + capeID[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 5: {
            pensalirEqp.push(capeID[selection]);
            var say = "지급 받을 #d우트가르드 무기#k를 선택해 주세요.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < weaponID.length; i++) {
                say += "\r\n#L" + i + "##i" + weaponID[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(weaponID[i]).get("reqLevel") + ") #z" + weaponID[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 6: {
            pensalirEqp.push(weaponID[selection]);
            var say = "#d#h ##k님께서 선택 한 #d펜살리르 장비#k와 #d우트가르드 무기#k는 아래와 같습니다.";
            say += "\r\n\r\n#fUI/UIWindow.img/Quest/reward#";
            for (i = 0; i < pensalirEqp.length; i++) {
                say += "\r\n#i" + pensalirEqp[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(pensalirEqp[i]).get("reqLevel") + ") #z" + pensalirEqp[i] + "##k";
            }
            cm.sendAcceptDecline(say);
            break;
        }
        case 7: {
            cm.dispose();
            if (cm.getInventory(1).getNumFreeSlot() < 11) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            if (cm.getInventory(2).getNumFreeSlot() < 7) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            if (cm.getInventory(5).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2438914, -1);
            for (i = 0; i < itemID.length; i++) {
                cm.gainItem(itemID[i][0], itemID[i][1]);
            }
            for (i = 0; i < pensalirEqp.length; i++) {
                cm.gainItem(pensalirEqp[i], 1);
            }
            cm.gainItem(weaponID[s1], 1);
            break;
        }
    }
}