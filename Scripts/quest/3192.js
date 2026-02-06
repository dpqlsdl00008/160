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
	qm.sendAcceptDecline("재료를 모두 모아왔군. 잠시만 기다리게.");
    } else if (status == 1) {
	if (qm.getInvSlots(2) < 1) {
	    qm.sendOk("소비 인벤토리 공간을 한 칸 이상 확보하고 다시 말을 걸어주게.");
	    qm.dispose();
	    return;
	}
	qm.gainItem(2430180, 1);
	qm.forceStartQuest();
	qm.dispose();
    }
}