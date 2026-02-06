var status = -1;
var aReward = [[5000232, 1], [1802370, 1]];

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
            var say = "#b마족 모스 패키지#k를 받으시겠어요?";
            say += "\r\n\r\n#fUI/UIWindow.img/Quest/reward#";
            say += "\r\n#i5000232# #b#z5000232# 1개#k";
            say += "\r\n#i1802370# #b#z1802370# 1개#k";
            say += "\r\n#i2048051# #b#z2048051# 3개#k";
            say += "\r\n#i2048052# #b#z2048052# 3개#k";
            cm.sendAcceptDeclineS(say, 4);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getInventory(1).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            if (cm.getInventory(2).getNumFreeSlot() < 2) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            if (cm.getInventory(5).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2430059, -1);
            cm.gainPet(5000232, "마족 모스", 1, 0, 100, 24 * 60 * 365, 101);
            cm.gainItem(1802370, 1);
            cm.gainItem(2048051, 3);
            cm.gainItem(2048052, 3);
            break;
        }
    }
}