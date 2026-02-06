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
            if (qm.isQuestActive(24097) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("이것은 아르콘의 피...");
            }
            break;
        }
        case 1: {
            qm.sendNextPrev("...뭐? 윈스턴이 내가 뭘 줄거라고 했다고? ...딱히 줄 건 없는데. 인기도라도 올려주지.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,742 exp\r\n\r\n#fUI/UIWindow.img/QuestIcon/6/0# 3");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainExp(1742);
            qm.getPlayer().addFame(3);
            qm.forceCompleteQuest();
            break;
        }
    }
}