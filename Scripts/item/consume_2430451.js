var status = -1;
var aReward = [[2001513, 300], [2001514, 300]];

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
            var say = "#r60 레벨 달성#k을 축하드립니다.\r\n\r\n";
            say += "#b쭈쭈바 300개#k와 #b팥빙수 300개#k를 받으시겠어요?\r\n\r\n#fUI/UIWindow.img/Quest/reward#";
            for (i = 0; i < aReward.length; i++) {
                say += "\r\n#i" + aReward[i][0] + "# #b#z" + aReward[i][0] + "# " + aReward[i][1] + "개#k";
            }
            cm.sendAcceptDeclineS(say, 4);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getInventory(2).getNumFreeSlot() < 2) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2430451, -1);
            for (i = 0; i < aReward.length; i++) {
                cm.gainItem(aReward[i][0], aReward[i][1]);
            }
            break;
        }
    }
}