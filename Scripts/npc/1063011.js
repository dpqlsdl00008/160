var status = -1;

function start() {
    action (1, 0, 0);
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
            var say = "이상한 목소리가 들린다. #b들어가고 싶으면 암호를 말해!#k";
            if (cm.getQuestStatus(21728) < 2) {
                cm.sendNext("\r\n" + say);
            } else {
                cm.sendGetText(say);
            }
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getQuestStatus(21728) < 2) {
                if (cm.isQuestActive(21728) == true) {
                    cm.forceStartQuest(21761, "0");
                    cm.sendNextPrevS("\r\n#b(암호가 뭘까? 일단 프란시스 목소리는 확실한 것 같은데... 일단 일지에게 돌아가자.)#k", 2);
                }
                return;
            }
            if (!cm.getText().equals("프란시스는 천재 인형사!")) {
                cm.sendNext("\r\n이상한 목소리가 비웃는다.\r\n\r\n#b바보 아냐? 암호가 틀렸잖아? 띄어쓰기하고 느낌표까지 딱 맞추라고!");
                return;
            }
            cm.warp(910510001, "out00");
            break;
        }
    }
}