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
            cm.forcedInput(2);
            cm.sendDelay(3000);
            break;
        }
        case 1: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.showFieldEffect(false, "JPKanna/magicCircle1");
            cm.sendDelay(7000);
            break;
        }
        case 3: {
            cm.sendNextS("The barrier is weakening. It must have worked.", 3);
            break;
        }
        case 4: {
            cm.sendNextPrevS("Now I just have to destroy the altar in the basement.", 3);
            break;
        }
        case 5: {
            cm.dispose();
            cm.warp(507100102, 0);
            break;
        }
    }
}