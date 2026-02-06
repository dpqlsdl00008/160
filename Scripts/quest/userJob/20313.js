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
            qm.sendAcceptDecline("그 일을 막은 당신의 공로를 높이 사서 여제께서 당신께 새로 직위를 수여하셨습니다. 직위를 받으시겠습니까?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainItem(4032179, -1);
            qm.gainItem(4032101, -1);
            qm.changeJob(1311);
            qm.forceCompleteQuest();
            qm.sendNext("#h #. 당신을 상급 기사로 임명합니다. 지금 이 시간부터 당신은 시그너스 기사단이 상급 기사로서 더 많은 책임을 짊어지게 됩니다. 항상 자유로운 눈으로 세계를 바라보되, 당신이 가진 의무를 잊지 마시길...");
            break;
        }
    }
}