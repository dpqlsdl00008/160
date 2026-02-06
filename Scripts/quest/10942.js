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
		qm.sendAcceptDecline("안녕하세요, #h #님! 벌써 레벨 50이 되셨군요! 정말 빠른걸요? 기다리고 기다리던 당신에게 약속한 선물을 드릴게요. 선물을 받을 준비는 되셨죠?");
	} else if (status == 1) {
		qm.forceStartQuest();
		qm.forceCompleteQuest();
		qm.gainItem(2430193, 1);
		//qm.sendOk("인벤토리를 열어 선물을 더블클릭해 보세요! 당신을 꾸며줄 멋진 선물들이 기다리고 있을 테니까요. 아참, 한 가지 더! 선물은 여기서 끝이 아니랍니다. 레벨 30이 되면 또다른 선물이 기다리고 있으니 그때까지 열렙! 잊지 마세요~");
		qm.dispose();
	}
}

function end(mode, type, selection) {

}