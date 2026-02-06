var status = -1;

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    qm.dispose();
	}
	status--;
    }
    switch (status) {
        case 0: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.warp(927000070, 0);
            break;
        }
    }
}