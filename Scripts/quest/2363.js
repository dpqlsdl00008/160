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
		qm.sendYesNo("훌륭하군요. 혜안이 당신을 선택했네요. 지금 바로 듀얼블레\r\n이드로써 각성하겠어요?");
	} else if (status == 1) {
		qm.forceCompleteQuest();
		qm.gainItem(1342000, 1);
		qm.changeJobById(430);
		qm.sendNext("이제부터 당신은 #b#e세미듀어러#n#k예요. 항상 자부심을 갖고 생활하세요.");
		qm.dispose();
	}
}