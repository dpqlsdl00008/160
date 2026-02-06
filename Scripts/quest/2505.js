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
		qm.sendNext("어때요? 궁극의 모험가로 태어난 기분이... 메이플 월드를 위해 검은마법사와 싸울 수 있도록 특별할 스킬을 드렸으니, 스킬창의 모험의 기술창을 확인해 보시기 바랍니다. 또한 필요한 장비들을 드리니 유용하게 사용하시기 바랍니다.");
	} else if (status == 1) {
		if (!qm.isQuestFinished(2505)) {
		    qm.forceStartQuest();
		    qm.forceCompleteQuest();
		    qm.gainItem(1003159, 1);
		    qm.gainItem(1052304, 1);
		    qm.gainItem(1082290, 1);
		    qm.gainItem(1072476, 1);
		    if (qm.getJobId() == 110 || qm.getJobId() == 120)
			qm.gainItem(1402092, 1);
		    else if (qm.getJobId() == 130)
			qm.gainItem(1432082, 1);
		    else if (qm.getJobId() == 210 || qm.getJobId() == 220 || qm.getJobId() == 230)
			qm.gainItem(1372081, 1);
		    else if (qm.getJobId() == 310)
			qm.gainItem(1452108, 1);
		    else if (qm.getJobId() == 320)
			qm.gainItem(1462095, 1);
		    else if (qm.getJobId() == 410)
			qm.gainItem(1472119, 1);
		    else if (qm.getJobId() == 420)
			qm.gainItem(1332127, 1);
		    else if (qm.getJobId() == 510)
			qm.gainItem(1482081, 1);
		    else if (qm.getJobId() == 520)
			qm.gainItem(1492082, 1);
		    qm.gainItem(1142257, 1);
		    qm.gainItem(2001504, 200);
		}
		qm.sendNextPrev("70레벨이 되면 훌륭한 아이템을 드리러 찾아오겠습니다.");
		qm.dispose();
	}
}

function end(mode, type, selection) {

}