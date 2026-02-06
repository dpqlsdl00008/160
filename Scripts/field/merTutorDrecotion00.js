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
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 1: {
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/0", 0, 0, -110, 0, 0);
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 3: {
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/1", 0, 0, -110, 0, 0);
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 4: {
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 5: {
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/2", 0, 0, -110, 0, 0);
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 6: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 7: {
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/3", 0, 0, -110, 0, 0);
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 8: {
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 9: {
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/4", 0, 0, -110, 0, 0);
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 10: {
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 11: {
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/5", 0, 0, -110, 0, 0);
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 12: {
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 13: {
            cm.dispose();
            cm.lockInGameUI(false);
            cm.teachSkill(20021166, 1, 1);
            cm.teachSkill(20021181, 1, 1);
            break;
        }
    }
}