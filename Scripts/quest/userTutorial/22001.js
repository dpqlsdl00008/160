var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 2) {
            qm.sendNext("너어, 귀찮다고 거절할래? 형이 꼭 개한테 물리는 걸 보고 싶냐? 얼른 다시 말을 걸어서 퀘스트를 수락해!");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNextPrev("아침부터 한참 웃어네. 하하하. 자, 이상한 소리는 그만하고 불독한테 아침밥이나 좀 줘.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b엥? 그건 유타가 할 일이지않아?#k", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("이 녀석! 형이라고 부르라니까! 불독이 나를 얼마나 싫어하는지는 너도 잘 알잖아. 다가가면 분명히 물리고 말거야. 불독이 넌 좋아하니까 네가 가져다 줘.");
            break;
        }
        case 3: {
            if (qm.isQuestActive(22001) == false) {
                qm.gainItem(4032447, 1);
                qm.forceStartQuest();
            }
            qm.sendNext("얼른 #b왼쪽#k으로 가서 #b불독#k에게 사료를 주고 돌아와. 그 녀석 아까부터 배가 고픈지 짖어대고 있어.");
            break;
        }
        case 4: {
            qm.sendNextPrev("불독에게 먹이를 준 다음에 빨리 돌아와.");
            break;
        }
        case 5: {
            qm.dispose();
            break;
        }
    }
}