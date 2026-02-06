var status = -1;
function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		qm.dispose();
		return;
	}
	if (status == 0) {
qm.sendYesNo("이제 준비가 완료되고 있는 것 같아요. 여행자님은 어떠세요?\r\n슬슬 때가 오지 않았나 생각하는데... #b크세르크세스#k를 퇴치해 주시겠습니까?");


	} else if (status == 1) {
qm.forceStartQuest(31013);
		qm.dispose();
	}
}
function end(mode, type, selection) {
	qm.dispose();
}
