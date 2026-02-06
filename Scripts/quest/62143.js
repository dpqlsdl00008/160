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
            qm.sendNextS("주방장님! 혹시 제가 가져온 이 재료들로 #d팥죽#k을 만들어 주실 수 있나요?", 2);
            break;
        }
        case 1: {
            qm.sendNextPrev("그럼 그럼! 어려운 일도 아니라해!");
            break;
        }
        case 2: {
            qm.sendNextPrev("여기 있다네!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i2022001# #d#z2022001# 10개#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 511,795 exp");
            break;
        }
        case 3: {
            qm.dispose();
            if (qm.getInventory(2).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.forceCompleteQuest();
            qm.gainItem(2022001, 10);
            break;
        }
    }
}