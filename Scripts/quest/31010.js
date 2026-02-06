var status = -1;
function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		qm.dispose();
		return;
	}
	if (status == 0) {
qm.sendNext("미카엘을 많이 도와 주셨다고요? 여행자님의 도움에 마을을 대표해 큰 감사를 드립니다.\r\n\r\n여행자님 덕분에 미카엘도 큰 힘을 얻은 것 같습니다. 전과 다르게 의욕이 넘치는군요.");
	} else if (status == 1) {
		qm.sendAcceptDecline("하지만 큰일은 의욕만으로 해결 되지는 않는 법이이죠. 전장의 상황을 살펴 미카엘에게 전해 주시지 않겠습니까?");
	} else if (status == 2) {
		qm.forceStartQuest(31010);
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
if (!qm.isQuestActive(31010)) {
qm.forceStartQuest(31010);
qm.getQuestRecord(202007230).setCustomData("1");
qm.dispose();
} else {
if (qm.getQuestRecord(202007230).getCustomData().equals("1")) {
qm.sendNext("적의 상황을 알아야 승리할 수 있을텐데...");
} else {
qm.sendNext("무슨일이시죠? 콜로세움 내부에서는 어떤 적들이 기다리고 있는지 확인 해봐야 할텐데...");
qm.dispose();
}
}

} else if (status == 1) {
qm.sendNextPrev("네? 콜로세움까지 가셔서 크세르크세스 일당의 병력을 확인하고 오셨다고요? 후아.. 역시 대단 하시네요. 그럼 몇가지 질문을 드려도 될까요?");
} else if (status == 2) {
qm.sendNextPrev("콜로세움 내부에는 어떤 몬스터가 있었나요?\r\n\r\n#b#L0#저빌#l\r\n#L1#스코피#l\r\n#L2#페넥#l\r\n#L3#헤라크#l\r\n#L4#맘무트#l\r\n");
} else if (status == 3) {
  if (selection == 4) {
qm.sendYesNo("맘무트..맘무트라.. 굉장한 힘을 가진 녀석들인데...그럼 어떻게 대비를 하는게 좋을까요?\r\n\r\n#b#L0#나도 어떻게 해야할 지 모르겠어.#l\r\n#L1#공격에 큰 피해를 입지 않도록 방어구에 신경을 써야 할 것 같아.\r\n#L2#대비 같은건 필요 없어. 나 혼자서도 충분하다고.");
} else {
qm.sendNext("정말로 그 몬스터가 콜로세움에 있었다고요?");
qm.dispose();
}
} else if (status == 4) {
  if (selection == 1) {
qm.sendNext("맞는 말씀이에요. 준비를 더욱 열심히 해야겠네요.");
} else {
qm.sendNext("다시 한번 생각해보는게 좋을거같아요.");
qm.dispose();
}
} else if (status == 5) {
	//	qm.warp(200100001, 0); 
		qm.completeQuest(31010);
		qm.dispose();
}
}
