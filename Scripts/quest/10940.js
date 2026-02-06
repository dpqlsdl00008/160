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
		qm.sendAcceptDecline("안녕하세요, #h #님~ 메이플 월드에 오신 것을 환영합니다! 지금은 즐거운 이벤트 기간! 새로 캐릭터를 생성해 들어오신 뉴비분들께 선물을 드리고 있답니다. 자, 선물 받을 준비는 되셨죠?");
	} else if (status == 1) {
		qm.forceStartQuest();
		qm.forceCompleteQuest();
		qm.gainItem(1002019, 1);
		qm.gainItem(1032003, 1);
		qm.gainItem(1082002, 1);
		qm.gainItem(2000000, 100);
		qm.gainItem(2000003, 100);
		qm.gainItem(2430191, 1);
		//qm.sendOk("인벤토리를 열어 선물을 더블클릭해 보세요! 당신을 꾸며줄 멋진 선물들이 기다리고 있을 테니까요. 아참, 한 가지 더! 선물은 여기서 끝이 아니랍니다. 레벨 30이 되면 또다른 선물이 기다리고 있으니 그때까지 열렙! 잊지 마세요~");
		qm.dispose();
	}
}

function end(mode, type, selection) {

}