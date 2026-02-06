var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            // 충분한 설명이 되었길 바라며 자네에게 몇 가지 선물을 줍세. 받을 준비는 되었는가?
            qm.sendYesNo("자네에게 몇 가지 선물을 줍세. 받을 준비는 되었는가?");
            break;
        }
        case 1: {
            qm.sendNext("자네에게 10급 서큘레이터와 컨쿼러스 코인 1개를 주었다네. 혹시, 얻을 수 있는 것들이 잘 기억 나지 않거든 오른 쪽에 서 있는 #b재무 대신 우드완#k을 찾아 가 다시 물어 보게. 그럼 성실히 다시 알려 줄 그럼, 건투를 비네.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i2700000# #d#z2700000# 1개#k\r\n#i4310036#  #d#z4310036# 1개#k");
            break;
        }
        case 2: {
            qm.dispose();
            if (qm.getInventory(2).getNumFreeSlot() < 1 || qm.getInventory(4).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.gainItem(2700000, 1);
            qm.gainItem(4310036, 1);
            qm.forceCompleteQuest();
            break;
        }
    }
}