var status = -1;
function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		qm.dispose();
		return;
	}
	if (status == 0) {
qm.sendNext("너 혹시 오르비스 하늘 위를 올려다 본적이 있어?\r\n\r\n언제부턴가 하늘위의 크리세가 보이지 않아...");
	} else if (status == 1) {
		qm.sendAcceptDecline("크리세가 뭐냐고? 이런-- 아주 오래전부터 오르비스 상공위에 떠다니는 섬 말이야. 혹시 처음 들어보는거야? \r\n\r\n설명은 나중에 하기로 하고.. 지금 나를 좀 찾아와 줄 수 있겠어?");
	} else if (status == 2) {
		qm.forceStartQuest(31000);
		qm.dispose();
	}
}
function end(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		qm.dispose();
		return;
	}
	if (status == 0) {
qm.sendNext("이제 오는거야? 얼마나 기달렸는지 모른다고.");
} else if (status == 1) {
qm.sendNextPrev("오르비스의 하늘 위에는 크리세라고 불리우는 천상의 섬이 있어.거기엔 덩치는 큰 거인들이지만 착하고 여린 심성을 가진 거인족이 살고 있고.\r\n그런데 얼마전 부터 크리세가 점점 멀어지기 시작하더니 연락이 되질 않고 있어.\r\n무슨 일이 생긴게 분명한데... 마음 같아서는 당장이라도 찾아가 보고 싶지만 너도 알다시피 난 여기를 비울 수 없는 입징이라..");
} else if (status == 2) {
qm.sendNextPrev("그래서 하는 말인데 네가 크리세에 무슨일이 생긴건 아닌지 확인 해줄래?\r\n그럼 내가 크리세로 갈 수 있도록 도와줄게. 다녀와서 무슨일인지 꼭 나에게 전달해줘.");
} else if (status == 3) {
qm.sendYesNo("출발할 준비는 모두 된거야?\r\m먼 길이 될 테니 착실히 준비해 가는게 좋을거야. 지금 보내줄게.");
} else if (status == 4) {
qm.sendNext("좋았어. 지금 바로 보내줄게. 쉽지 않은 여정이 될 수 있으니 마음 단단히 먹으라고.");
} else if (status == 5) {
		qm.warp(200100001, 0); 
		qm.forceCompleteQuest();
		qm.dispose();
}
}
