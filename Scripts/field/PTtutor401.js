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
            cm.sendNextS("이런... 벌써 시작한건가? 조금이라도 늦으면 안되는데.", 17);
            break;
        }
        case 1: {
            cm.lockInGameUI(true);
            cm.forcedInput(2);
            cm.sendDelay(3000);
            break;
        }
        case 2: {
            cm.dispose();
            cm.lockInGameUI(false);
            cm.warp(915000500, 0);
            break;
        }
    }
}