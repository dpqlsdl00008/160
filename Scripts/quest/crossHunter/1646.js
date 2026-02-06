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
            qm.sendNext("놀랍군요. #h #님이 이 정도까지 임무를 훌륭하게 수행하실 줄 이야... #h #님에게 #b<전문 크로스 헌터>#k 칭호를 수여하도록 하겠습니다.");
            break;
        }
        case 1: {
            qm.sendNextPrev("#b<전문 크로스 헌터>#k가 되신 걸 축하드립니다. 다음 임무를 위해서라도 더욱 강해지시길 바랍니다.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1142353# #z1142353# 1개");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(1142353, 1);
            qm.forceCompleteQuest();
            break;
        }
    }
}