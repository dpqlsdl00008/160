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
	if (qm.isQuestActive(2244)) {
		var q = qm.getQuestRecord(2244);
		var check = q.getForfeited();
		if (check >= 200) {
			if (qm.canHold(1142079, 1)) {
				qm.forceCompleteQuest();
				qm.gainItem(1142079, 1, true);
			} else {
				qm.sendOk("인벤토리 한칸을 비워주게.");
				qm.dispose();
			}
		} else {
			qm.sendOk("아직 200마리를 잡지 못했나 보군..\r\n" + check + "마리 사냥했다네");
		}
		qm.dispose();
	} else {
		if (status == 0) {
			qm.sendOk("지금 부터 그대가 해야 할 일은 발록의 부활을 저지하는 것이오. 발록의 부활을 200번 저지해 낸다면 진정한 메이플월드의 용사로 거듭나게 될 것이오. 그때가 되면 그대를 내 후계자로 인정하겠소. 용기를 내시오. 젊은 모험가여...");
		} else if (status == 1) {
			qm.forceStartQuest();
			qm.dispose();
		}
	}
}