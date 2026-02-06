var status = -1;

function end(mode, type, selection) {
	if (mode == -1) {
        qm.dispose();
    } else {
        if (status == 0 && mode == 0) {
         qm.dispose();
        }
        if (mode == 1)
            status++;
        else
            status--;
	if (status == 0) {
		qm.sendNext("호오~ 약은 다 먹은 모양이군. 어때? 말 그대로 최고의 효과 아니야? 역시 이 몸의 약이란 완벽해!");
	} else if (status == 1) {
		qm.sendNextPrev("뭐? ...그냥 체력이 왕창 떨어지면 되는 거 아니냐고? 흠흠. 누구야? 그런 헛소리를 하는 게... 그럴 리가 없잖아? 하하하하!");
	} else if (status == 2) {
		qm.forceCompleteQuest();
		qm.dispose();
		}
	}
}