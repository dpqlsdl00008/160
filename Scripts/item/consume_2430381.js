function action(mode, type, selection) {
    cm.dispose();
    if (cm.getInventory(2).getNumFreeSlot() < 2) {
        cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
        return;
    }
    cm.gainItem(2430381, -1);
    cm.gainItem(2441003, 1);
    var itemID = 2022791;
    if (cm.getPlayer().getLevel() < 71) {
        itemID = 2022790;
    }
    if (cm.getPlayer().getLevel() < 31) {
        itemID = 2022789;
    }
    cm.useItem(itemID);
}