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
		qm.sendNext("빅토리아 아일랜드의 봉인석이라면 벌써 구했어. 봐. 후후후.#i4032323#");
	} else if (status == 1) {
		qm.sendNextPrevS("!!\r\n어떻게 구한건가?!", 2);
	} else if (status == 2) {
		qm.askAcceptDecline("저번에 인형사 녀석에게 공격당한 후에, 정보력을 총 동원해서 빅토리아 아일랜드를 싹 뒤져서 찾아냈지. 당하고만 있을 수는 없잖아? 녀석들이 노리던 걸 먼저 가로챈다... 이 정도면 괜찮은 복수겠지?");
	} else if (status == 3) {
		if (!qm.canHold(4032323, 1)) {
			qm.sendOk("장비창이 부족 합니다.");
			return;
		}
		qm.sendNext("하지만 블랙윙 녀석들은 날 이미 알고 있어. 내가 계속 이걸 가지고 있는 건 그리 좋은 생각이 아냐. 영웅님이 들고 다니다간 잃어 버릴 수도 있고... 역시 #b리린#k에게 맡기는 게 좋겠어.");
		qm.forceStartQuest();
		if (!qm.haveItem(4032323, 1)) {
			qm.gainItem(4032323, 1);
		}
	} else if (status == 4) {
		qm.sendNext("리엔 섬은 오랫동안 리엔 일족만 살던 곳인 데다가, 사람들이 들어오지 못하도록 온갖 주술이 걸려 있으니 블랙윙이라고 해도 쉽게 찾을 수 없을거야. 리린에게 이걸 맡겨줘.");
	} else if (status == 5) {
		qm.sendNext("앞으로는 정보수집 일은 시키지 않을 생각이야. 당신은 이미 메이플 월드에 어느 정도 익숙해졌으니, 이제는 스스로도 경험을 쌓을 수 있잖아?");
	} else if (status == 6) {
		qm.sendNext("그보다는 전력을 다해 블랙윙과 관련된 일의 정보를 모아볼 생각이야. 덧붙여, #b봉인석이라는 것에 대해서도 계속해서 알아보고. 뭔가 정보가 생기면 연락할게.#k 나중에봐, 영웅님.");
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
	if (status == 0) {
		qm.sendNext("블랙윙의 행동에 대해서는 진실 아저씨한테 계속 듣고 있었어요. 얼마 전에 습격도 당했다던데.. 당신은 괜찮으신가요? 음? 이건... 이게 바로 빅토리아 아일랜드의 봉인석인가요? 결국 진실 아저씨가 녀석들보다 먼저 빅토리아 아일랜드의 봉인석을 찾아 냈군요.");
	} else if (status == 1) {
		qm.sendYesNo("이 보석이 어떤 역활을 할 지는 모르지만... 분명한 건 이게 검은 마법사와 관련이 있을 거라는 사실뿐이에요. 녀석들이 이걸 노리는 한, 우리는 이걸 지켜내야만 해요. 무슨 일이 있어도 더 강해지셔야겠네요.");
	} else if (status == 2) {
		qm.sendNextPrev("블랙윙... 그들의 음모는 이걸로 끝이 아닐 거예요. 진실 아저씨께 블랙윙에 대해 계속 조사해 달라고 부탁 드릴게요. 당신도 계속해서 수련해 주세요.");
	} else if (status == 3) {
		qm.forceCompleteQuest();
		qm.teachSkill(21100005, qm.getPlayer().getSkillLevel(21100005), 20);
		qm.dispose();
	}
}