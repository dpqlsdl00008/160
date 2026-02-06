var status = -1;
var aReward = [[5000046, 1]];

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
            var say = "#r15 레벨 달성#k을 축하드립니다.\r\n\r\n";
            say += "#b고성능 확성기 150개#k와 #b아이템 확성기 150개#k, 그리고 #b키노 1개#k를 받으시겠어요?\r\n\r\n#fUI/UIWindow.img/Quest/reward#";
            for (i = 0; i < aReward.length; i++) {
                say += "\r\n#i" + aReward[i][0] + "# #b#z" + aReward[i][0] + "# " + aReward[i][1] + "개#k";
            }
            cm.sendAcceptDeclineS(say, 4);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getInventory(5).getNumFreeSlot() < 3) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2430444, -1);
            cm.gainItem(5072000, 150);
            cm.gainItem(5076000, 150);
            for (i = 0; i < aReward.length; i++) {
                if (aReward[i][0] == 5000046) {
                    cm.gainPet(5000046, "이노시스", 1, 0, 100, 24 * 60 * 365, 101);
                } else {
                    cm.gainItem(aReward[i][0], aReward[i][1]);
                }
            }
            break;
        }
    }
}