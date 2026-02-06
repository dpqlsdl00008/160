var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            if (cm.isQuestActive(3927) == false) {
                cm.dispose();
                return;
            }
            cm.sendYesNo("평범한 벽이지만 자세히 들여다보니 이상한 문양이 그려져 있습니다. 벽을 살피시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.sendNext("\r\n벽 뒤에는 이상한 단어들이 쓰여져 있다.\r\n#b철퇴와 단검, 활과 화살만 있다면...#k");
            cm.forceStartQuest(3927, "1");
            break;
        }
    }
}