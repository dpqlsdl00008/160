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
            qm.sendNext("");
            break;
        }
        case 1: {
            qm.sendNextPrev("");
            break;
        }
        case 2: {
            qm.sendNextPrev("");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}