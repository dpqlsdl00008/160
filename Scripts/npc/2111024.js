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
            if (cm.getQuestStatus(3360) == 0) {
                cm.dispose();
                return;
            }
            c = 0;
            if (cm.getMapId() == 261020200) {
                c = 1;
            }
            var qr7062 = cm.getQuestRecord(7062);
            var curCode = qr7062.getCustomData().substring(c, c + 1);
            if (!curCode.equals("0")) {
                cm.warp(261030000, c == 0 ? 2 : 1);
                return;
            }
            cm.sendGetText("비밀번호를 입력하시오.");
            break;
        }
        case 1: {
            cm.dispose();
            var qr7061 = cm.getQuestRecord(7061);
            var qr7062 = cm.getQuestRecord(7062);
            if (!cm.getText().equals(qr7061.getCustomData())) {
                cm.sendNext("\r\n... 비밀 번호가 틀렸습니다.");
                return;
            }
            if (c == 0) {
                qr7062.setCustomData("1" + qr7062.getCustomData().substring(1, 2));
            } else {
                qr7062.setCustomData(qr7062.getCustomData().substring(0, 1) + "1");
            }
            cm.getPlayer().dropMessage(5, "보안 장치가 해제 되었습니다. 출입 허가 명단에 등록되었습니다.");
            if (qr7062.getCustomData().equals("11")) {
                cm.forceCustomDataQuest(3360, "1");
            }
            break;
        }
    }
}