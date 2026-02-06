var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == -1) {
            qm.sendNext("앗, 안 할 거야? 안 할 거야? 아... 아쉽다.");
            qm.dispose();
        }
        if (status == 0) {
            qm.sendYesNo("안녕하세요 #h0#님~ 레전드 페스티발을 기념해서 50레벨을 달성하신 모든 유저에게 선물을 드리고 있어요. 지금 받아 보시겠어요?"); 
        } else if (status == 1) {
	    qm.sendOk("선물은 잘 받으셨죠? 60레벨을 달성하시면 다른 선물을 받을 수 있으니 노력해주세요.");
	    qm.gainItem(2430450, 1);
	    qm.forceCompleteQuest();
	    qm.dispose();
        } else {
            qm.dispose();
        }
    }
}