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
            qm.askAcceptDecline("#h #, 정말 너의 실력에 감탄했어. 결국 우리가 모든 아이들을 구출하는 데 성공한 거야! 이제 엘리넬로 돌아가서 교장 선생님에게 보고하는 일만 남았어. 지금 나와 함께 돌아 가자.\r\n#b(수락하면 요정 학원 엘리넬로 이동합니다.)#k");
            break;
        }
        case 1: {
            qm.sendNext("좋아. 아이들은 모두 돌아와 있겠지?");
            break;
        }
        case 2: {
            qm.warp(101072000, 0);
            qm.forceStartQuest();
            break;
        }
    }
}