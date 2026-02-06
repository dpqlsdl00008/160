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
		qm.askAcceptDecline("영웅의 후예가 수백년이 세월이 흘러 다시 나타날 줄이야... 메이플 월드의 복이 될 것인가, 아니면 해가 될 것인가... 아니 어느 쪽이건 상관 없겠지. 좋네... 자네에게 무릉의 봉인석에 대해 알려주지.");
	} else if (status == 1) {
		qm.sendNext("무릉의 보인석이 있는 곳은 봉인된 사원이라네. 무릉사원의 깊숙한 곳에 그 입구가 있지. 상급수련장의 기둥들 중 입구라는 글자가 새겨진 기둥을 찾으면 봉인된 사원으로 갈 수 있을 걸세. 암호는#b도가도비상도#k라네.");
	} else if (status == 2) {
		qm.sendNextPrev("어쩌면 그림자 무사라는 자는 이미 봉인된 사원에 와 있을지도 모르네. 하지만 이런 예고장을 보낸 자이니 아직 물건을 훔쳐가진 않았겠지. 나를 기다리고 있을게야... 하지만 나보다는 영웅의 후예 가 그림자 무사를 맞아주는 게 좋을 것 같군.");
	} else if (status == 3) {
		qm.sendNextPrev("온 능력을 다해 그림자 무사를 막아주게. 영웅의 후예여... 예전 영웅의 업적을 이어주게.");
	} else if (status == 4) {
		qm.sendNextPrevS("#b(날 영웅의 후예라고 착각하고 있는 것 같다. 그런데 예전 영웅의 업적을 이어달라니... 무슨 말일까? 일단 그림자 무사를 저지한 후 물어보자.)#k", 2);
	} else if (status == 5) {
		qm.forceStartQuest();
		qm.dispose();
	} else if (status == 6) {
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
		qm.sendYesNo("그림자 무사를 퇴치하는데 성공했는가? 왠지 표정이 어둡군... 설마 실패한 건 아닐텐데...");
	} else if (status == 1) {
		qm.sendNext("그렇군. 무릉의 봉인석을 빼앗기고 말았군... 아쉽지만 어쩔 수 없는 일이지. 나 역시 영웅들이 왜 무릉에 봉인석을 맡겼는지 이유를 알지 못하니까.");
	} else if (status == 2) {
		qm.sendNextPrevS("영웅이 무릉에 봉인석을 맡긴 게 확실 합니까?", 2);
	} else if (status == 3) {
		qm.sendNextPrev("그렇다네... 자네 역시 그건 몰랐던 모양이군. #b아주 오래 전 영웅들이 무릉에 봉인석을 맡겼고, 장로님은 봉인된 사원을 만들어 엄중하게 보관했지.#k");
	} else if (status == 4) {
		qm.sendNextPrevS("...영웅이...", 2);
	} else if (status == 5) {
		qm.sendNextPrev("이젠 그런 게 있다는 걸 아는 사람도 적다네. 사실 #b봉인석이 없어 진다고 해도 무릉에 해가 가는지 아닌지도 몰라.#k 그저 영웅이 맡긴 것이기에 중요하게 여긴 것이니.");
	} else if (status == 6) {
		qm.sendNextPrevS("#b(영웅이 봉인석을 무릉에 맡겼다..)#k", 2);
	} else if (status == 7) {
		qm.sendNextPrev("영웅이 맡긴 것을 잃어버린 것은 아쉽지만, 영웅의 후예가 있으니 마음은 든든하군. 부디 영우이 못다한 일을 해내길 바라네.");
	} else if (status == 8) {
		qm.sendNextPrevS("#b(무릉의 봉인석도 빼앗겨 버렸다... 트루에게 상담해 보자.)#k", 2);
	} else if (status == 9) {
		qm.forceCompleteQuest();
		qm.dispose();
	}
}