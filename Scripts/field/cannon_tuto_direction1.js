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
            cm.effectPlay("Effect/Direction4.img/effect/cannonshooter/balloon/0", 9000, new java.awt.Point(0, 0), false, 0, false, 0, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 1: {
            cm.effectPlay("Effect/Direction4.img/effect/cannonshooter/balloon/1", 9000, new java.awt.Point(0, 0), false, 0, false, 0, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 2: {
            cm.dispose();
            cm.effectPlay("Effect/Direction4.img/effect/cannonshooter/balloon/2", 9000, new java.awt.Point(0, 0), false, 0, false, 0, 0, 0);
            cm.reservedEffect("Effect/Direction4.img/cannonshooter/face04");
            cm.reservedEffect("Effect/Direction4.img/cannonshooter/out01");
            break;
        }
    }
}