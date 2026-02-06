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
            if (cm.isQuestActive(2332) == true) {
                cm.forceCustomDataQuest(2332, "1");
                cm.getPlayer().dropMessage(5, "퀘스트 <비올레타는 어디에?> 를 완료하였습니다.");
            }
            break;
        }
    }
}