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
            cm.showFieldEffect(false, "maplemap/enter/310000000");
            if (cm.isQuestActive(25406) == true) {
                cm.forceCompleteQuest(25406);
                cm.forceStartQuest(25443, "1");
            }
            break;
        }
    }
}