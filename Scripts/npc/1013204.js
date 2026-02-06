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
            if (cm.getMonsterCount(cm.getMapId()) > 0) {
                cm.sendNext("...?! 당신이 왜 여기에? 나가라고 분명 말씀드렸을 텐데요?");
            } else {
                status = 2;
                cm.forceStartQuest(22604, "1");
                cm.sendNext("이런... 당신의 힘이 이 정도일 줄이야. 하는 수 없죠. 이번에는 여기서 퇴각하겠습니다. 다음에 만날 땐... 적이겠군요.");
            }
            break;
        }
        case 1: {
            cm.sendNextPrevS("이, 이베흐씨? 여기서 뭘 하시는 거죠? 왜 여기에... 지금 뭘 하시려고... 혹시 저기 있는 잠든 드래곤을 괴롭히시려는 건가요?!", 2);
            break;
        }
        case 2: {
            cm.dispose();
            cm.sendNextPrev("쳇... 귀찮게 됐군... 안 됐지만 당신은 여기서 사라져 주셔야겠습니다.");
            break;
        }
        case 3: {
            cm.dispose();
            cm.warp(914100021, "out00");
            break;
        }
    }
}