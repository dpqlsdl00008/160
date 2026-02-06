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
            var v1 = false;
            if (cm.isQuestFinised(3925) == true) {
                v1 = true;
            }
            if (cm.isQuestActive(3946) == true) {
                v1 = true;
            }
            if (cm.getQuestStatus(3926) == 0) {
                v1 = true;
            }
            if (!v1) {
                cm.dispose();
                cm.getPlayer().dropMessage(5, "동굴 문은 꿈쩍도 하지 않는다.");
                return;
            }
            cm.sendGetText("동굴의 문을 열고 싶다면 암호를 말해라...");
            break;
        }
        case 1: {
            cm.dispose();
            var v2 = false;
            if (cm.getText().contains("열려라") && cm.getText().contains("참깨")) {
                v2 = true;
            }
            if (!v2) {
                cm.getPlayer().dropMessage(5, "동굴 문은 꿈쩍도 하지 않는다.");
                return;
            }
            cm.getPlayer().dropMessage(5, "암호를 말하자 신비한 힘이 동굴 안으로 인도한다.");
            cm.warp(260010402, 1);
            break;
        }
    }
}