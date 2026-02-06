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
            qm.sendAcceptDecline("이 일을 해결한 네 공로를 높이 사서 여제께서 네게 새로운 직위를 내리셨지. 지위를 받겠어?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainItem(4032179, -1);
            qm.gainItem(4032101, -1);
            qm.changeJob(1411);
            qm.forceCompleteQuest();
            qm.sendNext("#h #. 널 상급 기사로 임명한다. 지금 이 시간부터 넌 시그너스 기사단의 상급 기사로서 더 큰 신념을 관찰해 나가게 된다. 어둠 속에 있는 수많은 유혹들이 널 흔들겠지만 네 신념만을 향해 나아가도록.");
            break;
        }
    }
}