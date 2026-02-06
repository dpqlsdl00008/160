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
	qm.sendNext("또다시 남쪽 숲에 그 꼬마가 나타났대요. 이번에는 말하는 나무가 하지 말라고 말까지 걸엇지만, 막았다가는 나뭇가지를 꺾어버린다는 협박만 들은 모양이에요. 그리고 순식간에 사라졌다고 하네요.");
    } else if (status == 1) {
	qm.sendYesNo("범인을 잡으면 좋겠지만 워낙 신출귀몰해서 잡기는 어려울 것 같다고 말하는 나무가 그러더군요. 하지만 그렇다고 초록버섯들을 그냥 둘 수는 없어요 하루 빨리 인형을 회수해서 남쪽 숲을 원래대로 돌려놔야 해요. 도와주시겠어요?");
    } else if (status == 2) {
	qm.forceStartQuest();
	qm.sendNext("그럼 다시 한 번 비밀의 숲으로 가 주세요.. 그곳에서 #r성격불문 초록버섯 100마리를 퇴치#k하고 #b좌절한 초록버섯 인형#k을 회수해 주세요. 이미 했던 일이니 그리 오래 걸리진 않을 거예요.");
    } else if (status == 3) {
	qm.sendYesNo("물론 비밀의 숲으로 제가 마법으로 보내드릴 수 있어요. 지금 가실거죠?");
    } else if (status == 4) {
	qm.warp(910100002, 0);
	qm.dispose();
    }
}