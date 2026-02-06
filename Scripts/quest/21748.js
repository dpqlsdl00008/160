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
	if (qm.getQuestStatus(21748) == 0) {
		qm.forceStartQuest(21748, "1");
	} else {
		if (status == 0) {
			qm.sendNext("블랙윙과 관련된 사건을 찾아 무릉으로 갔다는 말은 들었어요, 아란. 고생이 많군요. 그런데... 이번에도 블랙윙이 뒤통수를 쳤나요?");
		} else if (status == 1) {
			qm.sendNextPrevS("#b(무릉의 봉인석에 관해 이야기 했다.)#k", 2);
		} else if (status == 2) {
			qm.sendNextPrev("...네? 영웅이... 과거의 당신이 봉인석을 맡긴 장본인이라고요? 무릉의 봉인석을 빼앗긴 건 아무래도 좋아요! 이건 굉장한 정보라고요!");
		} else if (status == 3) {
			qm.sendNextPrevS("굉장한 정보?", 2);
		} else if (status == 4) {
			qm.sendNextPrev("과거에 영웅이 봉인석을 갖고 있었다는건, #b영웅에 관한 아주 단편적이고 작은 정보들을 잘 조사하다 보면 봉인석이 있는 곳을 알 수 있을지도#k 모르잖아요? 그렇다면 블랙윙보다 먼저 봉인석을 손에 넣을 수 있을 거예요!");
		} else if (status == 5) {
			qm.sendNextPrevS("그렇군! 그런 방법이 있었어!", 2);
		} else if (status == 6) {
			qm.sendYesNo("후후후... 좋아요! 의욕이 마구마구 생기는 걸요? 아란,그렇죠?");
		} else if (status == 7) {
			qm.sendOk("영웅의 행적에 대해 처음부터 다시 조사해 봐야겠어요! 블랙윙의 정보는 진실 아저씨가 계속 알아봐 주실테니 당신은 계속해서 수련해 주세요! 곧 블랙윙의 코를 납작하게 해줄 수 있을거예요!");
		} else if (status == 8) {
			qm.forceCompleteQuest();
			qm.teachSkill(21100002, qm.getPlayer().getSkillLevel(21100002), 20);
			qm.dispose();
		}
	}
}