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
            if (cm.isQuestActive(2358) == false) {
                cm.dispose();
                return;
            }
            cm.sendYesNo("벽보를 붙일만한 빈 자리가 있습니다. 이 자리에 벽보를 붙이시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            var qRecord = cm.getQuestRecord(2358);
            var qData = qRecord.getCustomData();
            if (qData == null) {
                qData = "000";
                qRecord.setCustomData("000");
            }
            qData = qData.substr(0, 2) + "1";
            if (cm.getMapId() != 102040000) {
                qData = qData.substr(0, 1) + "1" + qData.substr(2, 1);
            }
            qRecord.setCustomData(qData);
            cm.getPlayer().updateQuest(qRecord, true);
            cm.sendNext("\r\n벽보를 붙였습니다.");
            break;
        }
    }
}