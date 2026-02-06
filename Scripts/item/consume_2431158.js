var status = -1;

function action(mode, type, selection) {
    cm.dispose();
    if (cm.itemQuantity(2431158) < 10) {
        cm.dispose();
        return;
    }
    if (cm.getInventory(2).getNumFreeSlot() < 1) {
        cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
        return;
    }
    cm.gainItem(2431158, -10);
    cm.gainItem(2431156, 1);
}