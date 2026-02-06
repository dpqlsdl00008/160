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
            if (cm.isQuestActive(24078) == true && cm.haveItem(4032967, 1) == false) {
                cm.sendNext("나뭇결 사이에서 깨끗한 물을 채취했다.");
            } else {
                cm.dispose();
            }
            break;
        }
        case 1: {
            cm.dispose();
            cm.gainItem(4032967, 1);
            break;
        }
    }
}