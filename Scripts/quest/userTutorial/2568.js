var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

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
            qm.sendAcceptDecline("왔나? 자네가 일을 보고 오는 동안에 대포에 발화장치가 장착 되었다네. 자, 그럼 더 이상 지체할 필요 없겠지! 바로 출발하지!");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceStartQuest();
            qm.warp(912060200, 0);
            break;
        }
    }
}