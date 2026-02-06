var status = -1;

function start(mode, type, selection) {
    if (mode == 0) {
	if (status == 0 || status == 4) {
	    qm.sendNext("더 늦으면 타이밍을 맞추기 어려울지도 모릅니다.");
	    qm.safeDispose();
	    return;
	}
	status--;
    } else {
	status++;
    }
    if (status == 0) {
	qm.sendNextS("준비는 모두 마치셨습니까? 주인님. 이 크리스탈 가든은 에레브의 기사의 전당 위에 정확히 정박해 있습니다. 물론 아래에서 알차라니 기색은 없습니다.", 8);
    } else if (status == 1) {
	qm.sendNextPrevS("허나 지금 에레브는 무척 경계가 삼엄합니다. 평소와는 상황이 다르다 보니 역시... 기사의 전당에 많은 경비 기사들이 대기하고 있습니다.", 8);
    } else if (status == 2) {
	qm.sendNextPrevS("경비 기사들만 피하면 이후는 그리 어렵지 않을 것으로 보입니다. 주인님의 계획대로 진행된다면 환영 마법의 타이밍만 맞추면 될 것 입니다.", 8);
    } else if (status == 3) {
	qm.sendNextPrevS("주인님께서 모든 일을 마치고 돌아오실 때까지 크리스탈 가든은 계속 대기하고 있을 것 입니다. 엔진을 끄지 않을 테니 추적은 염려하지 않으셔도 됩니다.", 8);
    } else if (status == 4) {
	qm.sendYesNoS("모든 준비는 끝났습니다. 이제 남은 것은 주인님의 결단 뿐... 어떠십니까? 바로 에레브 잠입 작전을 개시하시겠습니까?", 8);
    } else if (status == 5) {
	qm.sendNextS("무운을 빕니다. 주인님.", 8);
    } else if (status == 6) {
	qm.forceStartQuest();
        qm.gainItem(2431876, 1);
        qm.gainItem(2431877, 1);
	qm.dispose();
    }
}