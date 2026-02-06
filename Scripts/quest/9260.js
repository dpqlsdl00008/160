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
	if (qm.getInvSlots(1) < 1) {
	    qm.playerMessage(1, "장비 인벤토리 공간을 한 칸 이상 확보하여 주시길 바랍니다.");
	    qm.dispose();
	    return;
	}
	qm.sendOk("앗! 이건 내 뿔이잖아! 고마워! 감사의 표시로 이걸 줄게. 아마 너에게 꼭 필요한 물건일거야. 그럼 잘가~\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1012300# #b#z1012300# 1개#k");
	qm.forceCompleteQuest();
	qm.gainItem(4031063, -1);
	qm.gainItem(1012300, 1);
	qm.dispose();
    }
}