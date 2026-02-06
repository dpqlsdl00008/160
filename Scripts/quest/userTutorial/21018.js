var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 1) {
            qm.sendNext("어라, 혹시 5마리로는 부족하신 건가요? 혹시 수련을 위해 더 퇴치하고 싶으시다면 하셔도 상관은 없어요. 영웅님을 위해 이번만은 마음이 아프지만 좀 더 사냥하더라도 눈 감아 드릴...");
            qm.dispose();
            return;       
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("자, 그럼 이때까지 기초 체력을 한 번 테스트 해보도록 할게요. 방법은 간단해요. 이 섬에서 가장 강하고 흉폭한 몬스터, 무루쿤을 퇴치하면 돼요! 한 #r50마리#k퇴치해 주시면 좋겠지만...");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("몇 마리 없는 무루쿤을 다 퇴치해 버리는 건 생태계에 좋지않은 것 같으니 5마리만 퇴치하도록 할게요. 자연과 환경을생각하는 단련! 아, 아름답기도 해라...");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}