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
	qm.sendNext("이번 사태의 원인은 아무래도 #b리치#k인 것 같네. 금지된 마법의 흔적도 비슷하고...");
    } else if (status == 1) {
	if (qm.isQuestActive(3191) == false) {
	    qm.forceStartQuest();
	}
	qm.sendPrev("결계를 치기 위해선 먼저, #b결계의 토템#k이란게 필요하네. 결계의 토템은 내 마법으로 만들 수 있지만, 그러기 위해선 리치가 가지고 있는 #b붉은 계약 구슬#k, #b타락한 자의 성서#k가 각 각 1개씩 필요하지. 이것들을 구해오도록 하게.");
	qm.dispose();
    }
}