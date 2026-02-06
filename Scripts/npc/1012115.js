var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            cm.dispose();
            if (cm.isQuestActive(20706) == false) {
                return;
            }
            if (cm.isQuestActive(20731) == true) {
                cm.sendNext("\r\n이미 그림자를 발견했다. 로카에게 가서 보고 하자.");
                return;
            }
            cm.forceStartQuest(20731, 1);
            cm.sendNext("\r\n그림자를 발견했다. 로카에게 가서 보고하자.");
            break;
        }
    }
}