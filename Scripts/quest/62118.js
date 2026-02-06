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
            qm.sendNext("오! #d#h ##k님 아니십니까?");
            break;
        }
        case 1: {
            qm.sendNextPrev("네? 경찰들이 어떤 일을 하는지 궁금해서 다시 오셨다구요?");
            break;
        }
        case 2: {
            qm.askAcceptDecline("그 누구도 제가 일하는 것을 궁금해 한 적은 없었답니다. 한번 #r도전#k 해보시겠어요?");
            break;
        }
        case 3: {
            qm.sendNextPrev("작은 것 부터 알려 드리자면 #r자전거 유령 100마리#k, #r표지판 유령 100마리#k, 그리고 #r신호등 유령 100마리#k를 퇴치하는 일입니다. 이 일은 다른 일에 비하면 아주 쉬운 일이랍니다.");
            break;
        }
        case 4: {
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
            qm.sendNext("벌써 끝내셨나요? 대단하군요.");
            break;
        }
        case 1: {
            qm.sendNextPrev("이제 저는 다른 곳에 가 봐야해요. 또 뵙겠습니다!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,535,385 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(1535385);
            break;
        }
    }
}