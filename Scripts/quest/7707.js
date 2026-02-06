var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        qm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}