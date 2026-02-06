var status = -1;
var weapon = [
1302030, // 메이플 켈트 소드
1412011, // 메이플 투핸디드 엑스
1422014, // 메이플 빅 모울
1432012, // 메이플 스피어
1442024, // 메이플 폴암
1382012, // 메이플 라마 스태프
1452022, // 메이플 헌터스
1462019, // 메이플 크로스 보우
1332025, // 메이플 바그나
1472032, // 메이플 칸데오
1482021, // 메이플 스톰 핑거
1492021, // 메이플 스톰 피스톨
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
            var say = "#b메이플 43 레벨 무기#k를 선택해 주세요.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < weapon.length; i++) {
                say += "\r\n#L" + i + "##i" + weapon[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(weapon[i]).get("reqLevel") + ") #d#z" + weapon[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            cm.sendAcceptDeclineS("#b#h ##k님께서 선택 한 메이플 43 레벨 무기는 #i" + weapon[selection] + "# #r(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(weapon[selection]).get("reqLevel") + ") #z" + weapon[selection] + "##k입니다.", 4);
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getInventory(1).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2431100, -1);
            cm.gainItem(weapon[v1], 1);
            break;
        }
    }
}