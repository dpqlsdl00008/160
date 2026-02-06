var status = -1;

function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
	if (status == 0) {
            if (qm.getQuestStatus(3862) == 0) {
                qm.forceStartQuest();
                qm.dispose();
	    } else if (qm.getQuestStatus(3862) == 1 && qm.haveItem(4033176)) {
                qm.sendNext("#b(머리 속에 또 한 번 음성이 울러퍼졌다.)\r\n'이 제물이 너의 영혼을 지켜줄 것이다. 하지만 황금제단 안으로 발을 들여놓는 순간 제물이 파괴되버린다는 것을 명심하라. 황금 제단을 향하지 않고 되돌아 나온다면 제물은 다시 너의 손으로 되돌아갈 것이다.'\r\n(육중한 돌문이 열리면서 라바나의 제단 입구로 걸어들어갔다.)#k");
	    }
        } else if (status == 1) {
	    qm.forceCompleteQuest();
	    qm.gainItem(4033176,-1);
	    qm.warp(252030000,0);
            qm.dispose();
        }
    }
}