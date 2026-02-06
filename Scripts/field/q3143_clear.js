var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.dispose();
            if (cm.isQuestFinished(3143) == true) {
                cm.dispose();
                return;
            }
            cm.getPlayer().updateInfoQuest(3143, "expl=1");
            break;
        }
    }
}