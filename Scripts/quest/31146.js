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
	if (qm.getMonsterCount(qm.getMapId()) == 0) {
	    qm.sendNext("저를 구해주셔서 감사합니다. 하지만 저는 이곳에 남아 있겠습니다. 제가 없어진 것이 알려지면 더 큰 화를 부를 수도 있을 테니까요. 이곳에 남아 할 일이 있을 것 같군요.");
	} else {
	    qm.sendNext("d");
	    qm.dispose();
	}
    } else if (status == 1) {
	qm.sendNext("알렉스에게 이런 저의 뜻을 잘 전해주시기 바랍니다.");
    } else if (status == 2) {
	qm.sendNext("그리고... 그녀를 막아주세요. 시그너스를 원래대로 되돌릴 방법은 없습니다. 이것은 어쩔 수 없는 결정입니다...");
    } else if (status == 3) {
	qm.forceCompleteQuest();
	qm.dispose();
    }
}