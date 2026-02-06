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
		qm.sendOk("두123");
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
		qm.sendOk("23");
	qm.dispose();
}