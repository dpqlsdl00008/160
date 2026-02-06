var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.sendNext("아직 무루피아를 사냥할 준비가 안 되신 건가요? 준비할 수 있는 건 모두 준비한 후에 사냥하는 게 좋아요. 괜히 대충 잡다가 비석 세우면 억울해지니까요.");
            qm.dispose();
            return;       
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendAcceptDecline("그럼 계속해서 기초 체력 단련을 할까요? 준비는 되셨어요? 검은 제대로 장비 하셨는지 스킬과 물약은 퀵슬롯에 올려 놓았는지 다시 한 번 확인하신 후 수락해 주세요.");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}