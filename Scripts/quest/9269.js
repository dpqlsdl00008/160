var status = -1;

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
	if (qm.isQuestActive(9269) == false) {
	    qm.forceStartQuest();
	    qm.dispose();
	} else {
	    if (qm.haveItem(4032529, 5) == true) {
		if (qm.getInvSlots(1) < 1) {
		    qm.playerMessage(1, "장비 인벤토리 공간을 한 칸 이상 확보하여 주시길 바랍니다.");
		    qm.dispose();
		    return;
		}
		var chat = "냐옹~ 벌써 다 모아 온 거야? 정말 대단한데?\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
		for (i = 0; i < 10; i++) {
		    chat += "\r\n#L" + i + "##i" + (2040110 + i) + "# #b#z" + (2040110 + i) + "##k";
		}
		qm.sendSimple(chat);
	    } else {
		qm.dispose();
	    }
	}
    } else if (status == 1) {
	qm.sendOk("냐옹~ 그럼 잘가라 냥~\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i" + (2040110 + selection) + "# #b#z" + (2040110 + selection) + "# 1개#k");
	qm.forceCompleteQuest();
	qm.gainItem(4032529, -5);
	qm.gainItem(2040110 + selection, 1);
	qm.dispose();
    }
}