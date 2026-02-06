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
	if (qm.isQuestActive(9268) == false) {
	    qm.forceStartQuest();
	} else {
	    if (qm.haveItem(4032528, 5) == true) {
		if (qm.getInvSlots(1) < 1) {
		    qm.playerMessage(1, "장비 인벤토리 공간을 한 칸 이상 확보하여 주시길 바랍니다.");
		    qm.dispose();
		    return;
		}
		qm.sendOk("오~ 드디어 실종됐던 크리스마스 요정들을 찾아 왔군. 이제 밀렸던 일들을 할 수 있겠어 정말 고맙네.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1002728# #b#z1002728# 1개#k");
		qm.forceCompleteQuest();
		qm.gainItem(4032528, -5);
		qm.gainItem(1002728, 1);
	    }
	}
	qm.dispose();
    }
}