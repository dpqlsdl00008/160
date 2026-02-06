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
            cm.showFieldEffect(false, "phantom/mapname1");
            cm.forcedInput(1);
            cm.sendDelay(1000);
            break;
        }
        case 1: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 3: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 4: {
            cm.forcedInput(1);
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 0, 0, -110, 1, 0);
            cm.sendDelay(1000);
            break;
        }
        case 5: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 6: {
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 7: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 8: {
            cm.forcedInput(1);
            cm.sendDelay(1000);
            break;
        }
        case 9: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 10: {
            cm.sendNextS("드디어 결전의 시간인가...", 17);
            break;
        }
        case 11: {
            cm.sendNextPrevS("의외로 긴장되는걸? 오랜만의 활동이라 그런가? 뭐, 자신 없는 건 아니지만.",17);
            break;
        }
        case 12: {
            cm.sendNextPrevS("준비는 모두 끝났겠지? 더 이상 여유 부리다가 타이밍을 못 맞추기라도 하면 창피도 그런 창피도 없을 테니, 조금 서두르는 느낌이지만 그만 나가볼까나.", 17);
            break;
        }
        case 13: {
            cm.dispose();
            cm.lockInGameUI(false);
            cm.teachSkill(20031211, 1, 1);
            cm.teachSkill(20031212, 1, 1);
            break;
        }
    }
}