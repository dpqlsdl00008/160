importPackage(Packages.client);

var status = -1;
var sel = -1;

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
            qm.sendAcceptDecline("메이플 월드에 혼돈이 도래하고 있어요. 진정한 용사는 혼돈 속에서도 자신을 갈고 닦아 빛을 낼 줄 알아야 하는 법이죠. #b#h0##k님, 당신이 진정한 용사인지 증명할 기회를 드리겠어요. 당신이 자신을 증명한다면 용사에게 어울리는 선물도 드리겠어요. 자신을 증명하는 시험에 도전해 보시겠어요?"); 
        } else if (status == 1) {
	    qm.forceStartQuest();
            qm.sendOk("#b2011년 7월 31일#k까지 #b#e80#n#k을 달성한다면 쉽게 얻을 수 없는 아이템을 드리고 있습니다. 꼭 도전에 성공하시길 바래요. \r\n#i1032095# 홀리윙 이어링");
	    qm.dispose();
        } else {
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
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
		qm.sendNext("벌써 도전에 성공하신 건가요? 그럼 어디 살펴볼까요?");
	} else if (status == 1) {
	    if (qm.getLevel() < 80) {
	        qm.sendOk("레벨 80을 달성하고 다시 와줘.");
	        qm.dispose();
	        return;
	    }
	    	qm.gainItem(1032095, 1);	
		qm.sendNextPrev("목표 레벨 달성에 성공하셨군요! 당신이야 말로 혼돈속에서 더욱 빛나는 용사라고 할만 하네요. 약속한 선물을 드리겠어요. \r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1032095# 홀리윙 이어링");
        } else if (status == 2) {
	    if (!qm.isQuestCompleted(10947)) {
	        qm.forceCompleteQuest();
	    	qm.gainItem(1032095, 1);	
	    }
	    qm.sendOk("잊지마세요. 당신이 이 혼돈을 잠재울 용사라는 것을...모두 당신에게 큰 기대를 걸고 있습니다.");
	    qm.dispose();
        } else {
            qm.dispose();
        }
    }
}