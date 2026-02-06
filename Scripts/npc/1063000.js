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
            if (cm.isQuestActive(2052) == true) {
                if (cm.haveItem(4031025, 1) == false) {
                    cm.gainItem(4031025, 20);
		    cm.gainItem(2435457, 1);
                    cm.warp(105000000, 0);
                    return;
                }
            }
            var cRand = (1 + Math.floor(Math.random() * 6));
            cm.gainItem(2435457, 1);
            cm.warp(105000000, 0);
            break;
        }
    }
}