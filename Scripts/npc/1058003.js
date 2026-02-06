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
            if (cm.isQuestActive(2635) == false) {
                cm.dispose();
                return;
            }
            cm.sendNext("\r\n(커다란 상자 안에서 트리스탄의 유품으로 보이는 물건을 꺼냈다.)");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.haveItem(4033188, 1) == false) {
                cm.gainItem(4033188, 1);
            }
            break;
        }
    }
}