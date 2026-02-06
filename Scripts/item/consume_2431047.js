var status = -1;
var nEqp = [
1003561, // 템페스트 깃털 모자
1052467, // 템페스트 후드 로브
1082438, // 템페스트 장갑
1102467, // 템페스트 망토
1072672, // 템페스트 슈즈
1032148, // 템페스트 이어링
1122200, // 템페스트 펜던트
1132161, // 템페스트 벨트
1152099, // 템페스트 견장
];
var nWeapon = [
1302249, // 템페스트 글라디우스
1312136, // 템페스트 카운터
1322182, // 템페스트 골든 해머
1402174, // 템페스트 클레이모어
1412123, // 템페스트 버터플라이
1422125, // 템페스트 빅 모울
1432151, // 템페스트 올 피어스
1442203, // 템페스트 하프문
1542061, // 템페스트 카타나

1372162, // 템페스트 더블 윙
1382193, // 템페스트 페인 킬러
1552061, // 템페스트 채선

1452190, // 템페스트 애쉬 로드
1462178, // 템페스트 록
1522079, // 템페스트 아르젠트

1472198, // 템페스트 저스티스
1332207, // 템페스트 칸자르
1362075, // 템페스트 아르칸시엘

1482152, // 템페스트 블러디 클로
1492163, // 템페스트 퀸즈 핑거
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
            var say = "　 #Cgreen#100 레벨 달성#Cgray#을 축하드립니다.\r\n\r\n　 #fUI/UIWindow.img/Quest/reward#";
            for (i = 0; i < nEqp.length; i++) {
                say += "\r\n　 #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(nEqp[i]).get("reqLevel") + ") #d#z" + nEqp[i] + "##k";
            }
            say += "\r\n#L0##Cyellow#(Lv.100) 템페스트 무기를 선택한다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            var say = "\r\n　 #Cgray#지급을 원하는 #Cgreen#템페스트 무기#Cgray#를 선택해 주세요.\r\n\r\n　 #fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < nWeapon.length; i++) {
                say += "\r\n#L" + i + "##d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(nWeapon[i]).get("reqLevel") + ") #d#z" + nWeapon[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            s = selection;
            var say = "#Cgray#지급 받으실 #Cgreen#템페스트 무기#Cgray#는 아래와 같습니다.\r\n";
            say += "\r\n#i" + nWeapon[selection] + "# #Cyellow##z" + nWeapon[selection] + "##k";
            cm.sendAcceptDecline(say);
            break;
        }
        case 3: {
            cm.dispose();
            if (cm.getInventory(1).getNumFreeSlot() < 9) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2431047, -1);
            for (i = 0; i < nEqp.length; i++) {
                cm.gainItem(nEqp[i], 1);
            }
            cm.gainItem(nWeapon[s], 1);
            break;
        }
    }
}