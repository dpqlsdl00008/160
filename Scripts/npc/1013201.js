var status = -1;

function start() {
    status = -1;
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
            cm.dispose();
            if (cm.getMonsterCount(cm.getMapId()) > 0) {
                cm.sendNext("저 골렘이 나를 여기까지 끌고 왔어... 골렘을 없애지 않고는 이 곳에서 나갈 수 없어...!");
            } else {
                if (cm.getQuestRecord(22598).getCustomData().equals("2") == false) {
                    cm.forceStartQuest(22598, "2");
                }
                cm.sendNext("나, 날 구해준 거야? 고마워... 어서 돌아가자.");
            }
            break;
        }
    }
}