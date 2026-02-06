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
            if (cm.isQuestActive(2620) == false) {
                cm.dispose();
                return;
            }
            cm.sendNext("\r\n(쓰레기 통에서 문서 하나를 꺼냈다.)");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.haveItem(4033182, 1) == false) {
                cm.gainItem(4033182, 1);
            }
            break;
        }
    }
}