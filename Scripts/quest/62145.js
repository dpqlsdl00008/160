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
            qm.sendNext("하지만 괜찮습니다! 제가 두 번째 비기를 알려드리죠! 반드시 성공 할 겁니다!!");
            break;
        }
        case 1: {
            qm.askAcceptDecline("한 번만 더 저를 믿어보시지 않겠습니까?");
            break;
        }
        case 2: {
            qm.sendNext("오오 역시 영웅님 이십니다! 그렇다면 #r강시의 낡은 부적 50개#k와 #r닭 피 50개#k 그리고 #r50,000 메소#k를 구해오시기 바랍니다!");
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
            qm.sendNext("메소, 메소는 어디에 있습니까?!! 아..아니 강시를 퇴치하기 위한 재료를 구해오셨군요!");
            break;
        }
        case 1: {
            qm.sendNextPrev("잠시만 기다려 주십시요!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 2,047,180 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainMeso(-50000);
            qm.gainItem(4009373, -50);
            qm.gainItem(4009360, -50);
            qm.gainExp(2047180);
            break;
        }
    }
}