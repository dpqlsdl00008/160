var status = -1;

function start(mode, type, selection) {
	if (mode == 0) {
		qm.dispose();
		return;
	}
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
}

//qm.forceStartQuest();
//qm.dispose();
function end(mode, type, selection) {
	if (mode == 0) {
		qm.dispose();
		return;
	}
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (status == 0) {
		if (qm.getQuestStatus(21739) != 1) {
			qm.forceStartQuest();
			qm.dispose();
		} else {
			qm.sendYesNo("침입자는 막아내었나? 하지만 그런 것 치고는 표정이 좋지 않은데... 뭐? 봉인석을 빼앗겼다고?");
		}
	} else if (status == 1) {
		qm.sendNext("그런가. 봉인석을 빼앗겼는가... 그렇다면 어쩔 수 없지. 봉인석이 어떤 역활을 하는 것인지는 나 역시도 알지 못해. 사실 그것이 없어졌다고 해서 #b당장 오르비스에 직접적인 피해가 오지는 않을 거야.#k 그것만은 확실해.");
	} else if (status == 2) {
		qm.sendNextPrev("하지만 무언가 거대한 일의 전초가 될 것 같군. 점도 아니고 예언도 아닌, 그저 감일 뿐이지만... 행운을 빌지. 당신에게는 아주 많은 행운이 필요할 거야");
	} else if (status == 3) {
		qm.sendNextPrevS("#b(오르비스 봉인석을 빼앗긴 채이다... 어떻게 하지? 트루와 상담해 보자.)#k", 2);
	} else if (status == 4) {
		qm.forceCompleteQuest();
		qm.dispose();
	}
}