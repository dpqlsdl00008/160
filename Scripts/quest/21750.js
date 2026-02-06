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
	qm.sendOk("?");
	qm.dispose();
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
	if (qm.getQuestStatus(21750) == 0) {
		qm.forceStartQuest();
		qm.dispose();
	} else {
		if (status == 0) {
			qm.sendNext("...아란? ...제가 환상을 보고 있는 건가요? 아란 당신... 정말 살아 있는 거죠? 아아... 감사합니다. 감사합니다...");
		} else if (status == 1) {
			qm.sendNextPrevS("...저, 미안하지만 난 당신을 기억하지 못한다.", 2);
		} else if (status == 2) {
			qm.sendNextPrev("...네? 무슨 말이에요, 아란? 당신은... 아란이 맞잖아요? 우리를 지켜주던 영웅, 아란... 그게 당신이잖아요?");
		} else if (status == 3) {
			qm.sendNextPrevS("#b(깨어난 후의 상황에 대해 자세히 설명했다.)#k", 2);
		} else if (status == 4) {
			qm.sendYesNo("...그렇군요. 당신은 기억을 잃었군요. 게다가 수백 년 후의 세상에서 깨어났다니 그렇다면 당신에게 이곳은 과거의 세계이겠군요...");
		} else if (status == 5) {
			qm.sendNext("...그럼 처음부터 다시 인사해야겠네요. 제 이름은 헬레나. #b아란의 친구 헬레나#k예요. 몇 달 전, 검은 마법사의 공격에서 당신을 희생해서 도망치고만...");
		} else if (status == 6) {
			qm.sendNextPrev("당신이 검은 마법사를 막고 있을 때, 우리는 비행선을 타고 빅토리아 아일랜드로 도망칠 수 있었어요. 용의 공격으로 원하던 남쪽의 평지가 아니라 이런 숲에 불시착하고 말았지만요.");
		} else if (status == 7) {
			qm.sendNextPrev("하지만 거기서 포기할 수는 없어서... 우리는 여기에 일단 정착했어요. 쉬면서 사람들을 추스르며 새로운 마을을 만들기 위한 준비 중 이에요.");
		} else if (status == 8) {
			qm.sendNextPrev("아무도 알지 못하는 빅토리아 아일랜드를 개척하느라, 함께 온 청년들은 밖에 나가 있어요. 덕분에 마을 안에는 부상자가 아니면 여자와 어린아이들뿐이죠.");
		} else if (status == 9) {
			qm.sendPrev("그런데 아란은 어떻게 여기에 오신 건가요?");
		} else if (status == 10) {
			qm.forceCompleteQuest();
			qm.dispose();
		}
	}

}