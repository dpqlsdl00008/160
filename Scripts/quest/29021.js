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
            if (qm.isQuestActive(29021) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("");
            break;
        }
        case 1: {
            qm.dispose();
            break;
        }
    }
}