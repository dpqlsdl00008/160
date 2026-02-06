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
            if (cm.itemQuantity(2434732) < 1) {
                cm.dispose();
                return;
            }
            cm.gainItem(2434732, -1);
            cm.getPlayer().modifyCSPoints(2, 50000, true);
            cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CUserLocal.chatMsg(Packages.handling.ChatType.GameDesc, "메이플 포인트를 얻었습니다. (+50,000)"));
            break;
        }
    }
}