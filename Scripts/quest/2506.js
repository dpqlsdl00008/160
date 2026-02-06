importPackage(Packages.client);

var status = -1;

function start(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else {
			qm.dispose();
			return;
		}
	}
	if (status == 0) {
		qm.sendNext("#b#h0##k님! 많이 성장하셨군요. 약속대로 선물을 드리러 찾아왔습니다. 선물을 받으시려면 장비창에 빈칸이 5개 이상 있어야 합니다.");
	} else if (status == 1) {
		if (!qm.isQuestFinished(2506) && qm.canHold(1003160) && qm.canHold(1052305) && qm.canHold(1082291) && qm.canHold(1072477) && qm.canHold(1332127)) {
		    qm.forceStartQuest();
		    qm.forceCompleteQuest();
		    qm.gainItem(1003160, 1);
		    qm.gainItem(1082291, 1);
		    qm.gainItem(1052305, 1);
		    qm.gainItem(1072477, 1);
		    if (qm.getJobId() == 110 || qm.getJobId() == 120)
			qm.gainItem(1402093, 1);
		    else if (qm.getJobId() == 130)
			qm.gainItem(1432083, 1);
		    else if (qm.getJobId() == 210 || qm.getJobId() == 220 || qm.getJobId() == 230)
			qm.gainItem(1372082, 1);
		    else if (qm.getJobId() == 310)
			qm.gainItem(1452109, 1);
		    else if (qm.getJobId() == 320)
			qm.gainItem(1462096, 1);
		    else if (qm.getJobId() == 410)
			qm.gainItem(1472120, 1);
		    else if (qm.getJobId() == 420)
			qm.gainItem(1332128, 1);
		    else if (qm.getJobId() == 510)
			qm.gainItem(1482082, 1);
		    else if (qm.getJobId() == 520)
			qm.gainItem(1492083, 1);
		}
		qm.sendNextPrev("앞으로 더욱더 힘을 내주세요.");
		qm.dispose();
	}
}

function end(mode, type, selection) {

}