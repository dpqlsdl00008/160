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
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/9", 2000, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 1: {
            cm.forcedInput(1);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(910150005, 0);
            break;
        }
    }
}