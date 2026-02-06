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
            qm.sendNext("비록 견습이지만, 일단은 크로스 헌터 멤버가 되신 걸 축하드립니다. #b<견습 크로스 헌터>#k의 칭호를 수여하도록 하겠습니다.");
            break;
        }
        case 1: {
            qm.sendNextPrev("크로스 헌터 멤버가 되신 걸 축하하며 #h #님에게 #b<견습 크로스 헌터>#k의 칭호를 수여합니다.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1142351# #z1142351# 1개");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(1142351, 1);
            qm.forceCompleteQuest();
            break;
        }
    }
}