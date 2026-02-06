var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 3 || status == 4) {
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNext("남쪽 숲에 그 인형을 든 꼬마가 나타났대요. 말하는 나무가 한 말이니 틀림없을 거예요. 던전 안에 들어갈 때는 좌절한 초록버섯 인형이 있었는데 나올 때는 없어졌다니, 이번에도 초록버섯을 포악하게 만들려는 게 틀림 없어요.");
    } else if (status == 1) {
	qm.sendYesNo("범인을 잡으면 좋겠지만 워낙 신출귀몰해서 잡기는 어려울 것 같다고 말하는 나무가 그러더군요. 하지만 그렇다고 초록버섯들을 그냥 둘 수는 없어요 하루 빨리 인형을 회수해서 남쪽 숲을 원래대로 돌려놔야 해요. 도와주시겠어요?");
    } else if (status == 2) {
	if (qm.isQuestActive(21717) == false) {
	    qm.forceStartQuest();
	}
	qm.sendNext("그럼 비밀의 숲으로 가서 초록버섯들을 퇴치해 주세요. 예전에 음침하게 변해버린 초록버섯 하고, 인형의 영향은 받고 있지만 아직 변하지 않은 초록버섯들이 뒤섞여 어떤 녀석에게 인형이 있을지 알 수 없어요.");
    } else if (status == 3) {
	qm.sendNextPrev("그러니 성격불문 #r아무 초록버섯#k이나 #r100마리#k 퇴치해 주세요. 어떤 녀석이든 인형의 영향을 받고 있는 건 사실이니 상관 없어요. 되도록 많이 퇴치한 후에, 꼬마가 두고 간 #b좌절한 초록버섯 인형#k을 회수해 주시면 돼요.");
    } else if (status == 4) {
	qm.sendYesNo("물론 비밀의 숲으로 제가 마법으로 보내드릴 수 있어요. 지금 가실거죠?");
    } else if (status == 5) {
	qm.warp(910100002, 0);
	qm.dispose();
    }
}