var status = -1;
//
function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else if (mode == 0 && type == 0) {
		status++;
	} else {
		status++;
		return;
	}
	if (status == 0) {
		qm.sendNext("엇 그런데 당신...!!!");
	} else if (status == 1) {
		var em = qm.getEventManager("tristan");
		if (em == null) {
			qm.sendOk("트리스탄 이벤트 불가능");
			qm.dispose();
		} else {
			if (!em.getProperty("state").equals("1")) {
				em.startInstance(qm.getPlayer());
				qm.forceStartQuest();
				qm.dispose();
			} else {
				qm.sendOk("트리스탄 이벤트 진행중. 다른 채널을 이용 하세요.");
				qm.dispose();
			}
		}
	}
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
	//qm.sendOk("?");
	qm.dispose();
}