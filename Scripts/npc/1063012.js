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
            var qRecord = cm.getQuestRecord(2236);
            var qData = qRecord.getCustomData();
            if (cm.isQuestActive(2236) == false) {
                return;
            }
            if (qData == null) {
                qRecord.setCustomData("000000");
            }
cm.getPlayer().dropMessage(5, "" + qRecord.getCustomData());
            cm.gainItem(4032263, -1);
            switch (cm.getMapId()) {
                case 105010100: {
                    if (qData.substring(0, 1) != 0) {
                        cm.sendNext("이미 부적을 붙였다.");
                        return;
                    }
                    cm.sendNext("아직 부적의 힘이 남아 있는 것 같다.");
                    cm.forceCustomDataQuest(2236, 1 + qData.substring(1) + "");
                    break;
                }
                case 105020000: {
                    if (qData.substring(1, 2) != 0) {
                        cm.sendNext("이미 부적을 붙였다.");
                        return;
                    }
                    cm.sendNext("아직 부적의 힘이 남아 있는 것 같다.");
                    cm.forceCustomDataQuest(2236, qData.substring(0, 1) + "1" + qData.substring(2));
                    break;
                }
                case 105020300: {
                    if (qData.substring(2, 3) != 0) {
                        cm.sendNext("이미 부적을 붙였다.");
                        return;
                    }
                    cm.sendNext("아직 부적의 힘이 남아 있는 것 같다.");
                    cm.forceCustomDataQuest(2236, qData.substring(0, 2) + "1" + qData.substring(3));
                    break;
                }
            }
            break;
        }
    }
}