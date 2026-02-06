var status = -1;
function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		qm.dispose();
		return;
	}
	if (status == 0) {
qm.sendNext("안녕하세여~ 음. #h0#님? 조만간 커닝스퀘어에 신나는 이벤트가 벌어지는데 관심 없으세여? 무슨 일이냐고여? 아이참, 천상의 목소리 혁이 오빠의 사인회가 열리잖아여~ 소식이 참 느리시네여.");

	} else if (status == 1) {
		qm.sendAcceptDecline("어쨌든, 곧 사인회 시작이에여~ #h0#님도 꼭 오셔서 혁이 오빠를 응원해 주세여~ 수락하시면 바로 이곳 커닝스퀘어로 이동하실 수 있으실 거에여!");
	} else if (status == 2) {
		qm.warp(103020000, 0); // 찾았노시발; 커닝지하철에서 이동시키네 미친련들
                        qm.showWZEffect("Effect/Direction5.img/Kerning/Scene0");
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
function end(mode, type, selection) {
	qm.dispose();
}
