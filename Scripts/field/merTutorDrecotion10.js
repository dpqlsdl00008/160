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
            if (cm.isQuestActive(24007) == true) {
                cm.dispose();
                break;
            }
            cm.lockInGameUI(true);
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/6", 2000, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 1: {
            cm.forcedInput(2);
            cm.sendDelay(3000);
            break;
        }
        case 2: {
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/8", 2000, 0, -110, 0, 0);
            cm.forcedInput(0);
            cm.sendDelay(2000);
            break;
        }
        case 3: {
            cm.dispose();
            cm.lockInGameUI(false);
            break;
        }
    }
}