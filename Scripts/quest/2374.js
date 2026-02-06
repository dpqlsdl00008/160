var status = -1;

function start(mode, type, selection) {
}

function end(mode, type, selection) {
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
		qm.sendNext("아레크님의 답변을 받아 왔군요. 기다리고 있었답니다. 그 분의 편지를 내게 주세요.");
	} else if (status == 1) {
		qm.sendNextPrev("드디어 우리가 아레크님에게 정식으로 인정받게 되었군요. 정말 기쁜일이에요. 그리고 당신도 이제 새로운 변화를 겪을 때가 된 것 같군요.");
	} else if (status == 2) {
		if (qm.haveItem(4032619)) {
		    qm.forceCompleteQuest();
		    qm.gainItem(4032619, -1);
		    qm.gainItem(1132021, 1);
		    qm.gainItem(5620001, 1);
		    qm.changeJobById(432);
		    qm.sendPrev("이제부터 우리도 아레크님에게 인정을 받았으니까 70레벨이 되면 아레크님의 찾아가서 전직하도록 하세요. 이제 우리 듀얼블레이드에게도 새로운 미래가 열린거죠.");
		}
		qm.dispose();
	}
}