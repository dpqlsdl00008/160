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
            qm.sendAcceptDecline("그런 일을 막은 당신의 공로를 높이 사, 여제께서 당신께 새로운 직위를 수여하셨어요. 직위를 받으시겠어요?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainItem(4032179, -1);
            qm.gainItem(4032101, -1);
            qm.changeJob(1211);
            qm.forceCompleteQuest();
            qm.sendNext("#h #. 당신을 상급 기사로 임명합니다. 지금 이 시간부터 당신은 시그너스 기사단이 상급 기사로서 더 열정적으로 자신을 단련하길 바래요. 그 열정이 당신에게 두려움이 아닌 용기를 줄 거예요.");
            break;
        }
    }
}