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
            cm.sendDelay(500);
            break;
        }
        case 1: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.sendNextS("(어디선가 나작하게 주문을 외우는 소리가 들린다.)", 5, 2144019);
            break;
        }
        case 3: {
            cm.sendNextPrevS("이건...!! 설마 아카이럼이 벌써 봉인을 해제하려는 건가?", 17);
            break;
        }
        case 4: {
            cm.sendNextPrevS("아카이럼!! 당장 멈춰라!", 17);
            break;
        }
        case 5: {
            cm.sendNextPrevS("...마지막에 와서 약간의 시간이 모자라다니... 시간을 뒤틀은 댓가 치고는 너무 가혹하군.", 5, 2144019);
            break;
        }
        case 6: {
            cm.sendNextPrevS("감히 내 일을 방해 한 댓가를 치루게 해주지. 도망 칠 곳 없는 이 곳에 뼈를 묻을 시간이다.", 5, 2144019);
            break;
        }
        case 7: {
            cm.dispose();
            cm.spawnMonster(8860001, 874, 71);
            cm.lockInGameUI(false);
            break;
        }
    }
}