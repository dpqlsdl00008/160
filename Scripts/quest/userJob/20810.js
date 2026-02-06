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
            qm.sendYesNo("미하일, 수습 기사가 되기 위한 모든 시험에 통과한 것을 축하해요. 언제라도 수습 기사로 임명이 가능하답니다. 지금 바로 수습 기사의 절차를 밟겠어요?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.changeJob(5110);
            qm.forceCompleteQuest();
            qm.sendNext("당신은 이제 수습 기사의 자격을 획득하셨습니다. 축하드려요. SP를 조금 드렸는데 한 번 사용해 보세요.");
            break;
        }
    }
}