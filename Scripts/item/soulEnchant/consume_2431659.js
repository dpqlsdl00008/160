var status = -1;
var v1 = 2431659;
var v2 = 2571117;

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
            if (cm.itemQuantity(v1) < 10) {
                cm.dispose();
                //cm.sendNextS("\r\n", 0, 9000174, 9000174);
                return;
            }
            cm.askAcceptDecline("#Cgreen##z" + v1 + "##k 10개를 소비하여 소울 아이템으로 교환 할 텐가?", 9000174);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getInventory(2).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            // 오호~ #Cgreen#xx#k가 나왔군.\r\n요긴하게 쓰게나. 크크크...
            // 오호~ #Cgreen#xx#k가 나왔군!\r\n이건 다른 소울보다 강력 한 아이템이니 소중히 간직하도록 하게나. 크크크...
            cm.gainItem(v1, -10);
            cm.gainItem(v2, 1);
            break;
        }
    }
}