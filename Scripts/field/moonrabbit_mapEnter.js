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
            cm.floatMessage("달맞이꽃 씨앗을 심고 보름달이 차오르면 월묘를 지켜내세요!", 5120016);
            cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(0));
            break;
        }
    }
}