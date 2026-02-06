var status = -1;

function start() {
    action(1, 0, 0);
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
            if (cm.haveItem(4031508, 5) && cm.haveItem(4031507, 5)) {
                cm.sendYesNo("#b#t4031508##k와 #b#t4031507##k 각 각 5개를 모으는 데 성공하셨군요! 그럼 동물원으로 보내드리겠습니다. 그 곳에서 다시 말을 걸어 주세요.");
            } else {
                cm.sendYesNo("#b#t4031508##k와 #b#t4031507##k 각 각 5개를 모으는 데 성공하셨군요! 그럼 동물원으로 보내드리겠습니다. 그 곳에서 다시 말을 걸어 주세요.");
            }
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(230000003, "out01");
            break;
        }
    }
}