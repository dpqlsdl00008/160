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
            qm.sendNext("안녕하신가? 여행하기 참 좋은 날씨라고 생각하지 않는가? 얼굴이 낯선 걸 보니 자네는 새로운 여행을 떠나는 모험가인가 보군. 메이플 아일랜드로 떠나는 이 배의 선장인 스키퍼라고 하네. 만나서 반갑네.");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("아직 출발 시간이 아니라네. 지금은 출발 준비를 하고 있으니 배 구경이라도 하면서 조금만 기다려 주게.");
            break;
        }
        case 2: {
            qm.sendNext("오~! 벌써 출발 준비가 완료된 모양이군. 준비가 빠른걸? 왠지 조짐이 좋은 시작이야~ 좋은 항해가 될 것만 같군. 자, 그럼 출발하도록 하지.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.warp(3000000, 0);
            qm.forceCompleteQuest();
        }
    }
}