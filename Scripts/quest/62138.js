var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("그래요. 이 사태의 장본인을 조사 해야겠습니다.");
            break;
        }
        case 1: {
            qm.askAcceptDecline("#r쇼핑객 강시#k와 #r여행자 강시 100마리씩을 퇴치#k하고 의심스러운 점이 있다면 저에게 보고해 주시겠습니까?");
            break;
        }
        case 2: {
            qm.sendNext("그들은 #d쇼핑가#k와 #d강변길#k에서 어슬렁 거리고 있을 것 입니다!");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("그래서 뭐 의심스러운 점이라도 발견하셨나요?");
            break;
        }
        case 1: {
            qm.sendNextPrev("단 하나도 없다구요...?? 당신을 따라가서 사진이라도 찍었어야 했는데... 으으... 하지만 강시는 너무 무서워요...\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,705,983 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(1705983);
            break;
        }
    }
}