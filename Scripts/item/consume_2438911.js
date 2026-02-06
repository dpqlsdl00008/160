var status = -1;

var itemID = [
[1003552, 1], // 프리미엄 메이플 이올렛 골드 햇
[1052461, 1], // 프리미엄 메이플 이올렛 슈트
[1072666, 1], // 프리미엄 메이플 이올렛 슈즈
[1102441, 1], // 프리미엄 메이플 이올렛 스타 클록
[1152089, 1], // 프리미엄 메이플 이올렛 견장
[1082433, 1], // 프리미엄 메이플 이올렛 타투
[1132154, 1], // 프리미엄 메이플 이올렛 벨트

[1142985, 1], // 원티드 엘리트
[1112663, 1], // 화이트 엔젤릭 블레스 링
[1112400, 1], // 연금술사의 반지
[5062002, 200], // 마스터 미라클 큐브
[2049100, 20], // 혼돈의 주문서 60%
[2049700, 2], // 에픽 잠재 능력 부여 주문서 100%
[2049750, 1], // 유니크 잠재 능력 부여 주문서 100%
];

var weaponID = [
1302227, // 프미리엄 메이플 이올렛 글라디우스
1312116, // 프리미엄 메이플 이올렛 카운터
1322162, // 프리미엄 메이플 이올렛 골든 해머
1402151, // 프리미엄 메이플 이올렛 클레이모어
1412104, // 프리미엄 메이플 이올렛 버터 플라이
1422107, // 프리미엄 메이플 이올렛 빅 모울
1432138, // 프리미엄 메이플 이올렛 올 피어스
1442182, // 프리미엄 메이플 이올렛 하프 문
1542059, // 프리미엄 메이플 이올렛 카타나
1372139, // 프리미엄 메이플 이올렛 더블 윙
1382168, // 프리미엄 메이플 이올렛 페인 킬러
1552059, // 프리미엄 메이플 이올렛 채선
1452170, // 프리미엄 메이플 이올렛 애쉬 로드
1462159, // 프리미엄 메이플 이올렛 록
1522071, // 프리미엄 메이플 이올렛 아르젠트
1472179, // 프리미엄 메이플 이올렛 블랙 저스티스
1332193, // 프리미엄 메이플 이올렛 칸자르
1362067, // 프리미엄 메이플 이올렛 아르칸시엘
1482140, // 프리미엄 메이플 이올렛 블러디 클로
1492152, // 프리미엄 메이플 이올렛 퀸즈 핑거
1532074, // 프리미엄 메이플 이올렛 크래쉬
1403049, // 프리미엄 메이플 이올렛 무권
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
            var say = "지급 받을 #d메이플 트레져 무기#k를 선택해 주세요.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < weaponID.length; i++) {
                say += "\r\n#L" + i + "##i" + weaponID[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(weaponID[i]).get("reqLevel") + ") #d#z" + weaponID[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            s1 = selection;
            cm.sendAcceptDecline("#d#h ##k님께서 선택 한 #d메이플 트레져 무기#k는 #i" + weaponID[selection] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(weaponID[selection]).get("reqLevel") + ") #z" + weaponID[selection] + "##k입니다.");
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getInventory(1).getNumFreeSlot() < 10) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            if (cm.getInventory(2).getNumFreeSlot() < 3) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            if (cm.getInventory(5).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2438911, -1);
            for (i = 0; i < itemID.length; i++) {
                cm.gainItem(itemID[i][0], itemID[i][1]);
            }
            cm.gainItem(weaponID[s1], 1);
            break;
        }
    }
}