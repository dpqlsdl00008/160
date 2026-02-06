var status = -1;

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        qm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            qm.dispose();
            qm.changeJob(3110);
            qm.forceCompleteQuest();
            break;
        }
    }
}