var status = -1;
var sCode = "004455433221104433221443322100445543322110";

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
            cm.playSound(false, "orbis/si");
            if (cm.isQuestActive(3114) == false) {
                return;
            }
            var qRecord = cm.getQuestRecord(3114);
            if (qRecord.getCustomData() == null) {
                qRecord.setCustomData("");
            }
            if (qRecord.getCustomData().equals("42")) {
                return;
            }
            qRecord.setCustomData(qRecord.getCustomData() + (cm.getNpc() - 2012027));
            if (qRecord.getCustomData().equals(sCode)) {
                qRecord.setCustomData("42");
                cm.getPlayer().updateQuest(qRecord, true);
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.getShowQuestCompletion(3114));
                cm.getPlayer().dropMessage(5, "성공적으로 연주를 완료했다. 엘리쟈의 고른 숨소리가 들린다.");
            } else {
                if (!java.lang.String.valueOf(sCode).startsWith(qRecord.getCustomData())) {
                    qRecord.setCustomData("");
                    cm.getPlayer().dropMessage(5, "연주에 실패했다. 엘리쟈의 기분이 언짢아 보인다.");
                }
            }
            break;
        }
    }
}