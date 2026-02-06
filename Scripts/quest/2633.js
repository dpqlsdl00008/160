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
            qm.sendNext("...하아... 믿을 수 없어. 우리가 지금까지 믿어 온 진실이 모두 거짓이었다니... 역사를 왜곡한 자? 더 큰 음모가 숨어 있었다고? 믿을 수 없어... 일단... 일단, 좀 더 알아 봐야 겠어.");
            break;
        }
        case 1: {
            qm.sendNextPrev("슬리피우드 깊은 곳에 트리스탄님의 제자가 남아 있어. 전대 다크로드님과 트리스탄님이 목숨을 잃던 그 자리에 있던 것이 아니라 굳이 찾지 않았지만... 이제 그를 만나 봐야 겠어.");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("정말 전대 다크로드님과 트리스탄님, 그리고 진이... 마왕 발록을 없애기 위해서가 아니라 더 큰 적을 위해 싸우고 있었다면 그도 뭔가 알고 있겠지. 그를 만나봐 줘. 수락하면 바로 보내줄께.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.warp(105100101, "in00");
            qm.forceCompleteQuest();
            break;
        }
    }
}