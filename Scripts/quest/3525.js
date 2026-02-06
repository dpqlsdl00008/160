var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("... 이 기운은... 날카로운 매의 눈빛이로군요. 어서오세요, #h #. 활을 다룰줄도 모르고 화살도 무서워 하던 풋풋한 초보자였던 당신이 여기까지 성장 할 줄이야.");
            break;
        }
        case 1: {
            qm.sendNextPrev("당신이 이렇게 강력한 궁수가 될 줄은 이미 느끼고 있었답니다.");
            break;
        }
        case 2: {
            qm.sendNextPrev("계속 더 정진하세요. 자네를 궁수로 만들어준 사람으로써 확신해요. 더 강한 궁수가 될 거란 걸...");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}