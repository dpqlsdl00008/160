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
            qm.sendAcceptDecline("사건을 미연에 방지한 그대의 공로를 높이 사, 여제께서 그대에게 새로운 직위를 부여하셨다. 직위를 받아들이겠는가?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainItem(4032179, -1);
            qm.gainItem(4032101, -1);
            qm.changeJob(1111);
            qm.forceCompleteQuest();
            qm.sendNext("#h #. 그대를 상급 기사로 임명한다. 지금 이 시간부터 그대는 시그너스 기사단의 상급 기사로서 더욱 정진하여 여제와 자신에게 부끄럽지 않도록 노력해야 할 것이다. 그대의 명예가 항상 빛나길 바란다.");
            break;
        }
    }
}