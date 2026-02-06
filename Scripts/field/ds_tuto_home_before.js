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
            cm.forcedInput(0);
            cm.sendDelay(90);
            break;
        }
        case 2: {
            cm.showFieldEffect(false, "demonSlayer/text11");
            cm.sendDelay(4000);
            break;
        }
        case 3: {
            cm.dispose();
            cm.reservedEffect("Effect/Direction6.img/DemonTutorial/Scene2");
            break;
        }
    }
}