var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendNext("\r\n석상에서 손을 떼자 아무 일도 없던 것처럼 원래대로 돌아왔습니다.");
        }
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            if (cm.haveItem(4031025, 10) == true) {
                cm.dispose();
                cm.sendNext("\r\n이미 #b#z4031025##k를 가지고 있는 것 같습니다.");
                return;
            }
            if (cm.haveItem(4031028, 30) == true) {
                cm.dispose();
                cm.sendNext("\r\n이미 #b#z4031028##k를 가지고 있는 것 같습니다.");
                return;
            }
            if (cm.haveItem(4031026, 20) == true) {
                cm.dispose();
                cm.sendNext("\r\n이미 #b#z4031026##k를 가지고 있는 것 같습니다.");
                return;
            }
            var cUse = false;
            cMove = 0;
            if (cm.getQuestStatus(2052) > 0) {
                cUse = true;
                cMove = 910530000;
            }
            if (cm.getQuestStatus(2053) > 0) {
                cUse = true;
                cMove = 910530100;
            }
            if (cm.getQuestStatus(2054) > 0) {
                cUse = true;
                cMove = 910530200;
            }
            if (!cUse) {
                cm.dispose();
                return;
            }
            cm.sendYesNo("석상에 손을 대자 어디론가 빨려 드는 듯한 느낌이 듭니다. 이대로 이동 하시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(cMove, 0);
            break;
        }
    }
}