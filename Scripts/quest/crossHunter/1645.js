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
            qm.sendNext("로라로부터 얘기는 들었습니다. 훌륭하게 #r요괴 지네#k를 처치 하셨다구요? 이제 #h #님에게 #b<정식 크로스 헌터>#k 칭호를 수여해도 될 만큼 강해지신 것 같군요.");
            break;
        }
        case 1: {
            qm.sendNextPrev("#b<정식 크로스 헌터>#k가 되신 걸 축하드립니다. 다음 임무를 위해서라도 더욱 강해지시길 바랍니다.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1142352# #z1142352# 1개");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(1142352, 1);
            qm.forceCompleteQuest();
            break;
        }
    }
}