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
            if (cm.isQuestActive(2621) == false) {
                cm.dispose();
                return;
            }
            cm.sendNext("\r\n(비밀스러운 문자로 쓰여진 책을 가져 갔다.)");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.haveItem(4033183, 1) == false) {
                cm.gainItem(4033183, 1);
            }
            break;
        }
    }
}