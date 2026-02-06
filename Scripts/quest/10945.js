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
	    if (qm.getJobId() <= 512 && qm.getPlayer().getSpecialChar() != 1)
                qm.sendOk("#b2011년 7월 31일#k까지 #b#e80#n#k을 달성한다면 쉽게 얻을 수 없는 아이템을 드리고 있습니다. 꼭 도전에 성공하시길 바래요. #i1112427# 모험가의 크루얼 링#i1112428# 모험가의 크리티컬 링#i1112429# 모험가의 매직컬 링 중 택1");
	    else if (qm.getJobId() != 2001 && qm.getJobId() >= 2000 && qm.getJobId() <= 2112)
                qm.sendOk("#b2011년 7월 31일#k까지 #b#e80#n#k을 달성한다면 쉽게 얻을 수 없는 아이템을 드리고 있습니다. 꼭 도전에 성공하시길 바래요. #i1112592# 리린의 아우라반지");
	    else if (qm.getJobId() == 2001 || (qm.getJobId() >= 2200 && qm.getJobId() <= 2218))
                qm.sendOk("#b2011년 7월 31일#k까지 #b#e80#n#k을 달성한다면 쉽게 얻을 수 없는 아이템을 드리고 있습니다. 꼭 도전에 성공하시길 바래요. #i1022117# 오닉스 드래곤 안경");
	    else if (qm.getJobId() >= 3000 && qm.getJobId() <= 3512)
                qm.sendOk("#b2011년 7월 31일#k까지 #b#e80#n#k을 달성한다면 쉽게 얻을 수 없는 아이템을 드리고 있습니다. 꼭 도전에 성공하시길 바래요. #i1112591# 레지스탕스 럭스링");
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
	    if (qm.getJobId() <= 512) {
                qm.sendSimple("목표 레벨 달성에 성공하셨군요! 당신의야 말로 혼돈속에서 더욱 빛나는 용사라고 할만 하네요. 약속한 선물을 드리겠어요. \r\n#fUI/UIWindow.img/QuestIcon/3/0#\r\n#L0##i1112427# 모험가의 크루얼 링#l\r\n#L1##i1112428# 모험가의 크리티컬 링#l\r\n#L2##i1112429# 모험가의 매직컬 링#l"); 
	    } else if (qm.getJobId() != 2001 && qm.getJobId() >= 2000 && qm.getJobId() <= 2112) {
		qm.gainItem(1112592, 1);
		qm.sendNextPrev("목표 레벨 달성에 성공하셨군요! 당신이야 말로 혼돈속에서 더욱 빛나는 용사라고 할만 하네요. 약속한 선물을 드리겠어요. \r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1112592# 리린의 아우라반지");
	    } else if (qm.getJobId() == 2001 || (qm.getJobId() >= 2200 && qm.getJobId() <= 2218)) {
		qm.gainItem(1022117, 1);
		qm.sendNextPrev("목표 레벨 달성에 성공하셨군요! 당신이야 말로 혼돈속에서 더욱 빛나는 용사라고 할만 하네요. 약속한 선물을 드리겠어요. \r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1022117# 오닉스 드래곤 안경");	    
	    } else if (qm.getJobId() >= 3000 && qm.getJobId() <= 3512) {
		qm.gainItem(1112591, 1);
		qm.sendNextPrev("목표 레벨 달성에 성공하셨군요! 당신이야 말로 혼돈속에서 더욱 빛나는 용사라고 할만 하네요. 약속한 선물을 드리겠어요. \r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1112591# 레지스탕스 럭스링");
	    }
        } else if (status == 2) {
	    if (!qm.isQuestCompleted(10945)) {
	        qm.forceCompleteQuest();
	    if (qm.getJobId() <= 512 && qm.getPlayer().getSpecialChar() != 1) {
	        if (selection == 0)
		    qm.gainItem(1112427, 1);
	        else if (selection == 1)
		    qm.gainItem(1112428, 1);
	        else if (selection == 2)
		    qm.gainItem(1112429, 1);
	    } else if (qm.getJobId() != 2001 && qm.getJobId() >= 2000 && qm.getJobId() <= 2112) {
		qm.gainItem(1112592, 1);
	    } else if (qm.getJobId() == 2001 || (qm.getJobId() >= 2200 && qm.getJobId() <= 2218)) {
		qm.gainItem(1022117, 1);
	    } else if (qm.getJobId() >= 3000 && qm.getJobId() <= 3512) {
		qm.gainItem(1112591, 1);
	    }
	    }
	    qm.sendOk("잊지마세요. 당신이 이 혼돈을 잠재울 용사라는 것을...모두 당신에게 큰 기대를 걸고 있습니다.");
	    qm.dispose();
        } else {
            qm.dispose();
        }
    }
}