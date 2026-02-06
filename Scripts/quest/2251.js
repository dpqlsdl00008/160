var status = -1;

function end(mode, type, selection) {
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
	if (qm.isQuestActive(2251) == false) {
	    qm.forceStartQuest();
	    qm.dispose();
	} else {
	    qm.sendOk("아 이 소리야!! 잠시만 기다리게!");
	}
    } else if (status == 1) {
	qm.sendOk("#b#e버섯의 성의 머쉬킹이 위험하다. 페페킹을 조심하라! 레벨이 30이 넘는 베아트리스 용사에게 도움을 청하라.#n#k\r\n\r\n\r\n버섯의 성이라... 나는 처음 듣는 곳인걸?");
	qm.gainItem(4032399, -20);
	qm.forceCompleteQuest();
	qm.dispose();
    }
}



