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
            qm.sendNext("이대로 포기할 순 없어요... #d#h ##k님도 남경로에서 많은 유령들을 보셨죠?");
            break;
        }
        case 1: {
            qm.askAcceptDecline("그들의 정체를 밝혀내려면, 모두 잡아 봐야 할 것 같아요!  남경로에서 #r자전거 유령#k, #r표지판 유령#k, 그리고 #r신호등 유령#k을 #r각 각 50마리씩 퇴치#k하고 그들이 남긴 흔적이 없는지 저에게 보고해 주세요!");
            break;
        }
        case 2: {
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
            qm.sendNext("돌아 오셨군요! ...어라? 왜 빈 손이시죠??");
            break;
        }
        case 1: {
            qm.sendNextPrev("네?? 그들이 아무런 흔적도 남기지 않았다구요?? 정말 미치겠군요.. 다른 방법을 생각해 보죠.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,364,787 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(1364787);
            break;
        }
    }
}