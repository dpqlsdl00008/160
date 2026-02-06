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
            if (qm.isQuestActive(3184) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("#b(다음 보상을 받습니다.)#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 6,271 exp");
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainExp(6271);
            qm.forceCompleteQuest();
            break;
        }
    }
}