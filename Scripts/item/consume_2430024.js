function action(mode, type, selection) {
    cm.dispose();
    if (cm.getInventory(5).getNumFreeSlot() < 1) {
        cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
        return;
    }
    cm.gainItem(2430024, -1);
    cm.gainItem(5060004, 10);
}