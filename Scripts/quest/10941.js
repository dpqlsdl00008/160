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
		qm.sendAcceptDecline("안녕하세요, #h #님! 벌써 레벨 30이신가요? 쑥쑥 성장하는 당신을 보니 정말 기쁘네요. 그럼 당신을 위한 선물을 드릴게요.");
	} else if (status == 1) {
		qm.forceStartQuest();
		qm.forceCompleteQuest();
		qm.gainItem(1002006, 1);
		qm.gainItem(1032008, 1);
		qm.gainItem(2030000, 10);
		qm.gainItem(2000006, 100);
		qm.gainItem(2000002, 100);
		qm.gainItem(2430192, 1);
		//qm.sendOk("인벤토리를 열어 선물을 더블클릭해 보세요! 당신을 꾸며줄 멋진 선물들이 기다리고 있을 테니까요. 아참, 한 가지 더! 선물은 여기서 끝이 아니랍니다. 레벨 30이 되면 또다른 선물이 기다리고 있으니 그때까지 열렙! 잊지 마세요~");
		qm.dispose();
	}
}

function end(mode, type, selection) {

}