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
            cm.dispose();
            if (cm.isQuestActive(3926) == false) {
                return;
            }
            var nCount = (cm.getNpc() - 2103003);
            var qRecord = cm.getQuestRecord(3929);
            var qData = qRecord.getCustomData();
            if (qData == null) {
                qData = "0000";
                qRecord.setCustomData("0000");
            }
            if (qData.equals("3333")) {
                return;
            }
            var nInvite = qData.substr(nCount, 1);
            if (nInvite.equals("3")) {
                return;
            }
            if (cm.haveItem(4031580) == false) {
                cm.sendNext("\r\n내려 놓을 음식이 없다.");
                return;
            }
            cm.gainItem(4031580, -1);
            cm.sendNext("\r\n가져 온 음식을 살며시 내려 놓았다.");
            var v1 = "";
            for (i = 0; i < 4; ++i) {
                if (nCount == i) {
                    v1 += "3";
                } else {
                    v1 += qData.substr(i, 1);
                }
            }
            qRecord.setCustomData(v1);
            cm.getPlayer().updateQuest(qRecord, true);
            break;
        }
    }
}