var status = -1;

function start() {
    if (cm.getInfoQuest(23007).indexOf("vel00=1") == -1 && cm.getInfoQuest(23007).indexOf("vel01=1") == -1) {
        status = -1;
    } else {
        status = 4;
    }
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
            cm.sendNext("더 이상 가까이 가면 안 돼..!");
            break;
        }
        case 1: {
            cm.sendNextPrev("어떻게 여기까지 사람이 온 거지? 여기는 출입이 금지되어 있는데..");
            break;
        }
        case 2: {
            cm.sendNextPrevS("넌 누구야?!", 2);
            break;
        }
        case 3: {
            cm.sendNextPrev("난.. 여기 있어. 위쪽을 봐.");
            break;
        }
        case 4: {
            cm.dispose();
            cm.updateInfoQuest(23007, "vel00=1");
            cm.reservedEffect("Effect/Direction4.img/Resistance/ClickVel");
            break;
        }
        case 5: {
            cm.dispose();
            break;
        }
    }
}