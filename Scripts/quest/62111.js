var status = -1;

function end(mode, type, selection) {
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
            qm.sendNext("거기 계신 분! 잠시 이리로 와보시겠어요?");
            break;
        }
        case 1: {
            qm.sendNextPrev("처음 보는 얼굴인데 여긴 무슨일이시죠?");
            break;
        }
        case 2: {
            qm.sendNextPrevS("전 부탁을 받고 실종 된 하프링을 찾아 방문한 것 뿐이에요! 수상한 사람이 아니라구요.", 2);
            break;
        }
        case 3: {
            qm.askAcceptDecline("죄송하지만 이게 제 일이랍니다, 이곳에 머물기 위해서는 모든 방문객은 간단한 테스트를 통과해야 통행 허가를 내 드릴 수 있습니다. 준비가 되면 저에게 다시 말을 걸어주세요.");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}