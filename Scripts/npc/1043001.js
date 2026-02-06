var status = -1;

function start() {
    action(1, 0, 0);
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
            if (cm.isQuestActive(2051) == true) {
                if (cm.haveItem(4031032, 1) == false) {
                    cm.gainItem(4031032, 1);
                    cm.warp(101000000, 0);
                    return;
                }
            }
            var cRand = (1 + Math.floor(Math.random() * 3));
            cm.gainItem((4020005 + cRand), 2);
            cm.warp(101000000, 0);
            break;
        }
    }
}