var status = -1;

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
            qm.sendNext("오, 달걀을 가져온 거야? 그럼 달걀을 건네줘. 그럼 부화기를 줄게.");
            break;
        }
        case 1: {
            qm.sendYesNo("자, 받으라고. 도대체 어디에 쓰려는 건지는 모르겠지만...\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 360 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainExp(360);
            qm.sendTutorialUI(["UI/tutorial/evan/9/0"]);
            qm.forceCompleteQuest();
            break;
        }
    }
}