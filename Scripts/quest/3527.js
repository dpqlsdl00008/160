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
            qm.sendNext("안정된 자세와 기운, 하지만 당장이라도 싸울 수 있는 폭풍 같은 폭발력이 감춰진 신체. 훌륭한 해적이 되었군 #h #, 오랜만이다.");
            break;
        }
        case 1: {
            qm.sendNextPrev("처음 봤을 땐 바다에 제대로 적응도 못하던 초보자였는데, 어느새 이렇게 강해졌군... 하긴. 너라면 그럴 줄 알았어. 네 재능이라면 말이야. 하지만, 예상했던 일이라도 기쁜 걸?");
            break;
        }
        case 2: {
            qm.sendNextPrev("계속 정진하도록 해. 널 해적으로 만든 사람으로써 확신하는데, 넌 여기서 그칠 사람이 아니야. 더 강력한 해적으로 거듭날 수 있어. 그 때를 기다리지.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}