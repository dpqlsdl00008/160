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
            qm.sendNext("사실 리프레에 주민들에게 드릴 선물이 담긴 가방을 남경로 주변에서 잃어버리고 말았습니다...");
            break;
        }
        case 1: {
            qm.askAcceptDecline("그 곳은 무서운 유령들 때문에 도저히 찾으러 갈 수가 없어요. 혹시 저 대신에 가방을 찾아 줄 수 있으신가요?");
            break;
        }
        case 2: {
            qm.sendNext("#i4034654# #d#z4034654##k\r\n\r\n도와주시겠다니 정말 감사합니다... 아! 제가 잃어버린 가방은 이렇게 생겼습니다! 꼭 좀 부탁드리겠습니다!");
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
            qm.sendNext("잃어버린 저의 가방을 찾아주셔서 정말 감사해요!\r\n\r\n#d(가방은 되찾았지만 표정은 어쩐지 침울해 보인다.)#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,364,787 exp");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainItem(4034654, -1);
            qm.gainExp(1364787);
            break;
        }
    }
}