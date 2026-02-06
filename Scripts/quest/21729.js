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
		qm.askAcceptDecline("그러고 보니, 얼마 전에 그 꼬마가 낙서하는 걸 봤어요. 낙서 하지 말라고 말하려고 했더니 크리슈마씨가 먼저 말해서 못 말렸지만... 무슨 낙서인가 들여다 보려고 했더니 막 감추려고 하더라고요. 혹시 그게 #b암호#k아닐까요...?");
	} else if (status == 1) {
		qm.sendNext("일단... 좀비버섯 퇴치는 이걸로 끝이에요. 아직도 흉악한 녀석이 있는 것 같긴 하지만... 아란님이 해내시는 걸 보니 왠지 자신감이 생겼어요. 앞으로 개미굴4는 문제없이 지나갈 수 있을 것 같아요. 트루님께도 감사하다 전해 주세요.");
	} else if (status == 2) {
		qm.sendPrevS("#b(이상한 모양의 석상에 인형사의 동굴로 들어가는 암호가 있는 것 같다. 암호를 알아내면 바로 인형사를 공격할까? 아니야, 함부로 움직이지 말고 그보다 암호를 알아낸 후 트루에게가서 이번 일을 전해주자.)#k", 2);
		qm.forceStartQuest();
	}
}

function end(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status == 6) {
			qm.dispose();
			return;
		}
		status--;
	}
}