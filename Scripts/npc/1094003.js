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
            if (cm.isQuestActive(2186) == false) {
                return;
            }
            if (cm.haveItem(4031853, 1) == true) {
                cm.sendNext("\r\n아벨의 안경을 찾았다.");
                return;
            }
            var cRand = cm.rand(0, 100);
            if (cRand < 30) {
                cm.gainItem(4031853, 1);
                cm.sendNext("\r\n아벨의 안경을 찾았다.");
                return;
            }
            if (cRand < 50) {
                cm.gainItem(4031854, 1);
            } else {
                cm.gainItem(4031855, 1);
            }
            cm.sendNext("\r\n안경을 찾았다. 하지만 아벨의 안경이 아닌 듯 하다. 아벨의 안경은 검은 뿔테라는데...");
            break;
        }
    }
}