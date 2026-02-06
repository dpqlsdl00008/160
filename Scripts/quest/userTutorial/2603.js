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
            if (qm.isQuestActive(2603) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("하아... 느려, 느리다고. 미안하지만, 너 정말 재능이 없는 것 같다. 이대로 수련을 해봐야 과연 이도류를 제대로 배울 수 있을지 모르겠어.");
            }
            break;
        }
        case 1: {
            qm.sendNextPrev("....라고 말하기까지 했는데 여전히 무표정? 너 정말 재밌는 녀석인걸? 이봐, 홍아! 나와봐. 이 녀석 꽤나 흥미롭다고?");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainExp(50);
	    if (qm.getMap().containsNPC(1057001) == false) {
		qm.spawnNpc(1057001, -932, 152);
	    }
            qm.forceCompleteQuest();
            break;
        }
    }
}