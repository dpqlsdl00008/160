var status = -1;
var weapon = [
1302020, // 메이플 소드
1382009, // 메이플 스태프
1452016, // 메이플 보우
1462014, // 메이플 크로우
1472030, // 메이플 스론즈
1492020, // 메이플 건
1482020, // 메이플 너클
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
            var say = "#b메이플 35 레벨 무기#k를 선택해 주세요.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            for (i = 0; i < weapon.length; i++) {
                say += "\r\n#L" + i + "##i" + weapon[i] + "# #d(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(weapon[i]).get("reqLevel") + ") #d#z" + weapon[i] + "##k";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            cm.sendAcceptDeclineS("#b#h ##k님께서 선택 한 메이플 35 레벨 무기는 #i" + weapon[selection] + "# #r(Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(weapon[selection]).get("reqLevel") + ") #z" + weapon[selection] + "##k입니다.", 4);
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getInventory(1).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2431090, -1);
            cm.gainItem(weapon[v1], 1);
            break;
        }
    }
}