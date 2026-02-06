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
            cm.gainItem(4032263, -1);
            switch (cm.getMapId()) {
                case 105020100: {
                    if (qData.substring(3, 4) != 0) {
                        cm.sendNext("이미 부적을 붙였다.");
                        return;
                    }
                    cm.sendNext("아직 부적의 힘이 남아 있는 것 같다.");
                    cm.forceCustomDataQuest(2236, qData.substring(0, 3) + "1" + qData.substring(4));
                    break;
                }
                case 105020200: {
                    if (qData.substring(4, 5) != 0) {
                        cm.sendNext("이미 부적을 붙였다.");
                        return;
                    }
                    cm.sendNext("아직 부적의 힘이 남아 있는 것 같다.");
                    cm.forceCustomDataQuest(2236, qData.substring(0, 4) + "1" + qData.substring(5));
                    break;
                }
                case 105020400: {
                    if (qData.substring(5, 6) != 0) {
                        cm.sendNext("이미 부적을 붙였다.");
                        return;
                    }
                    cm.sendNext("아직 부적의 힘이 남아 있는 것 같다.");
                    cm.forceCustomDataQuest(2236, qData.substring(0, 5) + "1" + qData.substring(6));
                    break;
                }
            }
            break;
        }
    }
}