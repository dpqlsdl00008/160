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
            if (qm.isQuestActive(20746) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}