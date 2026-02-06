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
            if (cm.isQuestActive(31165) == false) {
                cm.dispose();
                return;
            }
            cm.forceCompleteQuest(31165);
            cm.lockInGameUI(true);
            cm.forcedInput(2);
            cm.sendDelay(500);
            break;
        }
        case 1: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.sendNextS("여기가 게렉터님이 말한 장소인 것 같은데... 저 앞에 보이는 건 뭐지?", 17);
            break;
        }
        case 3: {
            cm.forcedInput(2);
            cm.sendDelay(2000);
            break;
        }
        case 4: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 5: {
            cm.sendNextPrevS("이럴 수가, 저 분은 말로만 들었던 시간의 여신...?!", 17);
            break;
        }
        case 6: {
            cm.dispose();
            cm.lockInGameUI(false);
            break;
        }
    }
}