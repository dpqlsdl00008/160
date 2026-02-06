var status = -1;
function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		qm.dispose();
		return;
	}
	if (status == 0) {
qm.sendNext("아... 생각나요! 지금까지 있었던 일이 전부! 노래 연습을 하다 잘 되지 않아서 기타를 쳤는데 갑자기 이상한 기운이 들어왔어요. 그 이후부터는 갑자기 기분이 나빠지고 사람들에게 짜증을 내게 되었어요. 노래도 예전하고 다른 스타일로 하고요.");

	} else if (status == 1) {
		qm.sendNextPrev("잠깐 초심을 잃었나봐요. 데뷔할 때부터 줄곧 초심을 잃지 않고 처음 그 순간처럼 노래하기로 했었는데... 정말 부끄럽네요. 라나님과 티나, 마샬씨, 노마에님, 그리고 다른 분들에게도 죄송하고요.");
	} else if (status == 2) {
qm.sendOk("당신께도 정말 폐를 끼쳤네요. 진심으로 사과드리겠습니다. 앞으로는 이런 일 없을거에요. 다시 멋지게 노래하는 모습 보여 드릴게요."); // 데이터가 정확하지않아서 그냥 ok처리함

		//qm.warp(103020000, 0); // 찾았노시발; 커닝지하철에서 이동시키네 미친련들
                       // qm.showWZEffect("Effect/Direction5.img/Kerning/Scene0");
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
function end(mode, type, selection) {
	qm.dispose();
}
