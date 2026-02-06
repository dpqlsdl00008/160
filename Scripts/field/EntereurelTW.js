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
            if (isQuestActive(24009) == true) {
                cm.dispose();
            } else {
                cm.lockInGameUI(true);
                cm.sendDelay(100);
            }
            break;
        }
        case 1: {
            cm.lockInGameUI(true);
            cm.forcedInput(8);
            cm.sendDelay(0);
            break;
        }
        case 2: {
            cm.lockInGameUI(true);
            cm.forcedInput(1);
            cm.sendDelay(1400);
            break;
        }
        case 3: {
            cm.forcedInput(0);
            cm.sendNextS("장로들?", 17);
            break;
        }
        case 4: {
            cm.forcedInput(2);
            cm.sendDelay(800);
            break;
        }
        case 5: {
            cm.forcedInput(8);
            cm.sendDelay(0);
            break;
        }
        case 6: {
            cm.forcedInput(2);
            cm.sendDelay(2000);
            break;
        }
        case 7: {
            cm.forcedInput(0);
            cm.sendNextS("아이들도...", 17);
            break;
        }
        case 8: {
            cm.forcedInput(1);
            cm.sendDelay(500);
            break;
        }
        case 9: {
            cm.forcedInput(0);
            cm.sendNextS("모두들 아직도 그대로 얼어붙어 있잖아.", 17);
            break;
        }
        case 10: {
            cm.dispose();
            cm.forceStartQuest(24009, "1");
            cm.lockInGameUI(false);
            break;
        }
    }
}