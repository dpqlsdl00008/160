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
	if (status == 0) {
		qm.sendNext("오랜만이야, 영웅님. 못 본 새 레벨이 많이 오른 것 같은걸? 열심히 수련하는 모양이야. 역시 부지런하군. 영웅다운 훌륭한 자세야. 리린이 좋아하겠는걸?");
	} else if (status == 1) {
		qm.sendNextPrev("아, 이런 얘기할 때가 아니지. 정보 수집 범위가 빅토리아 아일랜드 만으로는 부족한 것 같아서, 좀 넓혀볼까 하고 오시리아 대륙 조사를 시작했어. 처음은 #b오르비스#k였는데 바로 빙고였지.");
	} else if (status == 2) {
		qm.askAcceptDecline("오시리아 대륙의 오르비스에서 뭔가 이상한 일이 벌어지는 모양이야. 인형사 때와는 다르지만, 아무래도 분위기가 수상한 것이 블랙윙과 관련이 있는 것 같아. 오르비스로 가줘.");
	} else if (status == 3) {
		qm.sendNextPrev("오랜만에 거창한 일이네. 좀 두근거리지 않아?");
		qm.forceStartQuest();
	} else if (status == 7) {
		qm.dispose();
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
	qm.dispose();
}