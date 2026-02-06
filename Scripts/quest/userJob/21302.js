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
            if (qm.isQuestActive(21302) == false) {
                qm.dispose();
                qm.forceStartQuest();
                qm.forceStartQuest(21203, "1");
            } else {
                qm.sendNext("앗! 그, 그것은... 홍주옥 만드는 법을 기억해낸 거야? 아아... 아무리 네가 바보에 건망증 환자라고 하더라도 이래서 널 버릴 수 없다니까... 아, 이럴 때가 아니지! 어서 보석을 건네줘!");
            }
            break;
        }
        case 1: {
            qm.sendAcceptDecline("좋아, 홍주옥의 힘도 되찾았겠다, 네 능력을 좀 더 깨워줄게. 안그래도 전보다 레벨이 많이 올랐으니 꽤 많은 영역까지 깨울 수 있을 거야!");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(4032312, -1);
            qm.changeJob(2111);
            qm.forceCompleteQuest();
	 qm.sendNext("어서 예전의 능력을 모두 되찾아줘. 예전처럼 함께 모험을 다니자고...");
            break;
        }
    }
}