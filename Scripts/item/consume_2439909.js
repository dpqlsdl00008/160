var status = -1;
var itemID = [
[1122334, 1], // 정령의 펜던트
[1114000, 1], // 혈맹의 반지
[2450000, 10], // 사냥꾼의 행운
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
            cm.dispose();
            if (cm.getInventory(1).getNumFreeSlot() < 2) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            if (cm.getInventory(2).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2439909, -1);
            for (i = 0; i < itemID.length; i++) {
                cm.gainItem(itemID[i][0], itemID[i][1]);
            }
            break;
        }
    }
}