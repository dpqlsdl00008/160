var status=-1;

function end(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
	return;
    } else {
        if (mode == 1)
            status++;
	else if (mode == 0 && status == 3) {
	    cm.sendNext("뭐 마음이 바뀌면 다시 오라구.");
	    cm.dispose();
	    return;
	} else
	    status--;
	if (status == 0) {
	    qm.sendNext("네가 레지스탕스가 된 거야? 우와, 정말? 생각지도 못했는데 의외인걸? 아무리 우리가 인원이 부족하다지만 아무나 막 받아주는 거 아닌가 모르겠어. 헨리테 녀석, 보기와 달리 의외로 물러서 말이야.");
        } else if (status == 1) {
	    qm.sendNextPrev("뭐, 이왕 레지스탕스가 되었으니 열심히 수련해서 레벨을 올려야겠지? 걱정하지 마. 내가 레지스탕스가 갖춰야 할 모든 것을 뼛속까지 새겨지도록 진지하게 가르쳐 줄 테니까. 후후, 기대해도 좋아.");
	} else if (status == 2) {
	    qm.sendNextPrev("레지스탕스 교육생을 위한 교육 프로그램은 총 세 단계로 이루어져 있어. 모두 레지스탕스에게 필요한 능력을 기를 수 있도록 만들었지. 모든 프로그램을 종료하면 네 레벨은 14가 될거야.");
	} else if (status == 3) {
	    qm.sendAcceptDecline("그럼 첫 번째 프로그램을 시작해 볼까? 첫 번째 프로그램은 순발력을 강화하는 거야. 치고 빠지기에 능한 레지스탕스라면 당연히 갖춰야 할 기본적인 능력이지.");
	} else if (status == 4) {
	    qm.completeQuest(23102);
	    qm.startQuest(23105);
	    qm.dispose();
	}
    }
}