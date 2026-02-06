var status = -1;

function end(mode, type, selection) {
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
            qm.sendNext("정말 힘든 하루였습니다... 모험가님께 은혜를 갚고 싶습니다만... 가진 거라곤...\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i4310177# #d#z4310177# 3개#k");
            break;
        }
        case 1: {
            qm.dispose();
            if (qm.getInventory(4).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.forceCompleteQuest();
            qm.gainItem(4310177, 3);
            break;
        }
    }
}