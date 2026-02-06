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
		qm.sendNext("방금 도서관 안에서 뭔가 소리가 난 것 같았는데... 당신이었나요, 아란? 봉인석은 찾으셨어요?");
	} else if (status == 1) {
		qm.sendNextPrevS("#b(도서관에서 있었던 일을 말해 주었다.)#k", 2);
	} else if (status == 2) {
		qm.sendNextPrev("그런... 여기까지 그런 자가 나타날 줄이야... 미안해요, 아란. 제가 좀 더 보관을 잘 했어야 하는 건데...");
	} else if (status == 3) {
		qm.sendNextPrevS("헬레나의 잘못이 아니다.", 2);
	} else if (status == 4) {
		qm.sendNextPrev("당신은 여전하시네요. 그보다... 아란에게 봉인석에 관한 이야기를 듣고 생각해 보니 단서가 될 만한 것이 떠올랐어요.");
	} else if (status == 5) {
		qm.sendNextPrevS("단서?", 2);
	} else if (status == 6) {
		qm.askAcceptDecline("네. 예전에 아란이 쓰던 편지를 발견했는데, 거기에 봉인석이라는 단어가 들어가 있었거든요. 한 번 보시겠어요?");
	} else if (status == 7) {
		qm.forceStartQuest();
		qm.sendNext("...어머? 편지가...");
	} else if (status == 8) {
		qm.sendNextPrevS("#i4032327#\r\n#b(편지를 받을 수 없었다. 편지는 손을 통과해 바닥으로 떨어졌다.)#K", 2);
	} else if (status == 9) {
		qm.sendNextPrev("...시간의 법칙에 대해서는 잘 모르지만... 아마도 이 편지를 전해 줄 수 없는 이유는, #b우리가 이미 다른 시간대의 사람이기 때문#k인 것 같네요... 왠지 슬프네요. 얼마 전까지만 해도 아란은 우리의 동료였는데...");
	} else if (status == 10) {
		qm.sendNextPrev("...아란도 알겠지만 전 아주 긴 시간을 사는 요정이에요. 아란이 수백 년 뒤의 시간대 사람이 되었더라고, 아마 저 역시 그 시간대까지 살아있을 가능성이 커요. 그러니 아란, #b이 편지는 제가 소중히 보관할 테니 당신의 시간대에서 찾으러 오세요.#k");
	} else if (status == 11) {
		qm.sendNextPrev("수백 년의 시간이 흐른대도 아란도, 이 야속도 잊지 않을 거예요. 그러니 나중에 다시 만나요. 기다릴게요.");
	} else if (status == 12) {
		qm.sendNextPrevS("#b(리린이 존재하는 시간으로 돌아가서 헬레나를 찾아 보자. 트루에게 부탁하면 분명 헬레나를 찾아줄 것이다.)#K", 2);
	} else {
		qm.dispose();
	}
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