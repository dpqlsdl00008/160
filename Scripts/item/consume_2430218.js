function action(mode, type, selection) {
    cm.dispose();
    cm.gainItem(2430218, -1);
    if (cm.getPlayer().getLevel() < 200) {
        cm.getPlayer().levelUp();
    }
}