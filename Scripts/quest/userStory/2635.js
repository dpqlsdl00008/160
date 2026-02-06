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
            qm.sendAcceptDecline("스승님의 유품 말이야, 갖고 있는 줄 알았는데 창고에 넣어 놨나 봐. 찾는 사람도 없고 해서 그냥... 뭐, 그렇지. 네가 직접 창고에서 유품을 꺼내 오도록 해.");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}