var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (cm.isQuestActive(31174) == false) {
                cm.dispose();
                return;
            }
            cm.forceStartQuest(31186, "1");
            cm.sendNextS("\r\n#b(갑자기 빛이 나더니 메르세데스의 표정이 좋아졌어. 이걸로 된 건가?)#k", 2);
            break;
        }
        case 1: {
            cm.sendNextPrevS("\r\n이로서 영웅들의 안전은 모두 보장되었네. 이게 다 자네 덕분이야. 다만, 한 가지 걸리는 점이 있으니 내게 다시 말을 걸어주면 좋겠군.", 4, 2144006);
            break;
        }
        case 2: {
            cm.dispose();
            cm.forceCompleteQuest(31174);
            break;
        }
    }
}