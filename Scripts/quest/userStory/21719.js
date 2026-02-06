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
            qm.sendNext("혹시 네가 얼마 전까지 #m101000000#에 있던 그 사람인가? 드디어 찾았군! 널 찾으려고 얼마나 헤맸는지 알아?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("넌 누구냐?", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("나? 알고 싶다면 나의 동굴로 와. 널 초대하도록 하지. 수락하기 버튼을 누르면 바로 이동될 거야. 그럼 기다릴게.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.warp(910510200, 0);
            qm.forceCompleteQuest();
            break;
        }
    }
}