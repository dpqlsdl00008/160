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
            cm.lockInGameUI(true);
            cm.forcedInput(1);
            cm.sendDelay(30);
            break;
        }
        case 1: {
            cm.showFieldEffect(false, "demonSlayer/text13");
            cm.forcedInput(0);
            cm.sendDelay(500);
            break;
        }
        case 2: {
            cm.showFieldEffect(false, "demonSlayer/text14");
            cm.sendDelay(4000);
            break;
        }
        case 3: {
            cm.dispose();
            cm.warp(927000020, 0);
            break;
        }
    }
}