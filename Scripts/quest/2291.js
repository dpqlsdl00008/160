var status = -1;
//
function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else if (mode == 0 && type == 0) {
		status--;
	} else {
		qm.dispose();
		return;
	}
	qm.dispose();
}

function end(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else if (mode == 0 && type == 0) {
		status--;
	} else {
		qm.dispose();
		return;
	}
	if (status == 0) {
		if (qm.isQuestActive(2291)) {
			qm.sendYesNo("소지하신 VIP티켓은 모두 모아 온건가요? 그렇다면 어서 제게 주세요. #bVIP 존#k으로 입장시켜 드릴게요. 참고로 VIP 존은 #b30분에 한번씩만 이용이 가능#k합니다.");
		} else {
			qm.forceStartQuest();
			qm.dispose();
		}
	} else if (status == 1) {
		var em = qm.getEventManager("KernigSVIP");
		if (em == null) {
			qm.sendOk("VIP존 이벤트 불가능");
			qm.dispose();
		} else {
			if (!em.getProperty("state").equals("1")) {
				if (qm.haveItem(4032521, 10)) {
					qm.forceCompleteQuest();
					em.startInstance(qm.getPlayer());
					qm.gainItem(4032521, -10);
				} else {
					qm.sendOk("VIP티켓이 부족한데요?");
					qm.dispose();
				}
			} else {
				qm.sendOk("누군가 이미 VIP존을 이용 중이야.");
				qm.dispose();
			}
		}
	}
}