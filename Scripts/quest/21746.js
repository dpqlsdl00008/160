var status = -1;

function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else if (mode == 0 && type == 0) {
		status--;
	} else {
		qm.dispose();
		return;
	}
	if (status == 0) {
		qm.sendNext("봉인석... 그것은 아주 오래 전부터 무릉이 지켜왔던 것... 그걸 노리는 자가 이제와서야 나타나다니...");
	} else if (status == 1) {
		qm.sendNextPrevS("봉인석에 대해 알려 주십시오.", 2);
	} else if (status == 2) {
		qm.askAcceptDecline("그럴 수는 없다네. 이 그림자 무사란 자가 위험한 것은 사실이나 자네가 그보다 위험할지 어찌 알겠나? 그러니 시험이 필요하겠군... #b시험#k을 치르겠나?");
	} else if (status == 3) {
		qm.forceStartQuest();
		qm.warp(925040001, 0);
		qm.getPlayer().getMap().resetFully();
		qm.dispose();
	} else if (status == 4) {
		qm.dispose();
	}
	//
}

function end(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else if (mode == 0 && type == 0) {
		status--;
	} else {
		qm.dispose();
		return;
	}
	if (status == 0) {
		qm.sendYesNo("워떤감? 족자 복원은 잘 되어가는감? 어디, 그럼 뭐라고 쓰여 있는지 한 번 볼꺼나?");
	} else if (status == 1) {
		qm.sendNext("히에에엑! 이게 뭐시여!");
	} else if (status == 2) {
		qm.forceCompleteQuest();
		qm.gainItem(4032343, -1);
		qm.dispose();
	}
}