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
            cm.blindEffect(false);
            cm.blindEffect(true);
            cm.showInfoOnScreen("비 내리는 어느 날");
            cm.showInfoOnScreen("비화원 심처");
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 1: {
            cm.forcedInput(2);
            cm.sendDelay(7000);
            break;
        }
        case 2: {
            cm.dispose();
            cm.forcedInput(0);
            cm.blindEffect(false);
            cm.lockInGameUI(false);
            break;
        }
    }
}