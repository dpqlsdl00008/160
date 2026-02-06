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
            qm.sendAcceptDecline("그간 레벨 업은 순조로우셨습니까? 지금쯤이면 커닝시티에서 파티 퀘스트를 하고 계실지도 모르겠군요. 레벨 업도 좋지만, 잠시 기사단의 임무를 맡아 주셔야겠습니다. 새로운 정보가 들어왔거든요.");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}