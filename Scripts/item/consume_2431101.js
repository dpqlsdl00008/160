var status = -1;
var weapon = [
1302064, // 메이플 글로리 소드
1312032, // 메이플 스틸 엑스
1322054, // 메이플 해버크 해머
1402039, // 메이플 소울 로헨
1412027, // 메이플 데몬 엑스
1422029, // 메이플 벨제트
1432040, // 메이플 베리트 스피어
1442051, // 메이플 카르스탄
1372034, // 메이플 샤이니 완드
1452045, // 메이플 간디바 보우
1462040, // 메이플 니샤다
1382039, // 메이플 위덤 스태프
1332055, // 메이플 다크 메이트
1472055, // 메이플 스칸다
1332056, // 메이플 아수라
1482022, // 메이플 골든 크로
1492022, // 메이플 캐논 슈터
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
            var say = "#b메이플 64 레벨 무기#k를 선택해 주세요.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < weapon.length; i++) {
                say += "\r\n#L" + i + "##i" + weapon[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(weapon[i]).get("reqLevel") + ") #d#z" + weapon[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            cm.sendAcceptDeclineS("#b#h ##k님께서 선택 한 메이플 64 레벨 무기는 #i" + weapon[selection] + "# #r(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(weapon[selection]).get("reqLevel") + ") #z" + weapon[selection] + "##k입니다.", 4);
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getInventory(1).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2431101, -1);
            cm.gainItem(weapon[v1], 1);
            break;
        }
    }
}