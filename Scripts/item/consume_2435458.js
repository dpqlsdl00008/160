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
            if (cm.getInventory(2).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            if (cm.itemQuantity(2435458) < 10) {
                cm.sendNext("\r\n#b골드 애플 조각 #r10개#k를 모으면 #i2435457# #b#z2435457# 1개#k를 받을 수 있습니다.");
                return;
            }
            cm.gainItem(2435458, -10);
            cm.gainItem(2435457, 1);
            break;
        }
    }
}