var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 3 || status == 4) {
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNext("무슨 일이십니까? 네? 이족자를 복원할 수 있냐고요? 어디 한 번 보죠. ...이런. 도대체 어떻게 하면 족자를 끓는 물에 푹 삶을 수 있는 겁니까? 도외진 같은 바보가 아니면 이런 실수는 하지 않을 텐데. 종이가 질긴 재질이어서 다행입니다.");
    } else if (status == 1) {
	qm.askAcceptDecline("뭐 족자를 복원하는 게 불가능하지는 않습니다. 특수한 먹물만 있으면 원상태로 만들 수 있지요. 마침 제가 특수 먹물을 만들 수 있으니 재료를 구해 오시면 만들어 드리도록 하죠. 물론 약간의 수수료는 받고요.");
    } else if (status == 2) {
	qm.forceStartQuest();
	qm.sendNext("그럼, 재료를 구해 오십시오. 재료는 #b짚 인형 50개, 나무인형 50개, 깨진 항아리 조각 100개#k 입니다. 거기에 수수료 #b50만 메소#k를 가져오시면 이 족자를 복원하는데 필요한 만큼의 먹물 ... #b특수 먹물 8개#k를 드리도록 하겠습니다.");
	qm.dispose();
    }
}