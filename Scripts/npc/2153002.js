function action(mode, type, selection) {
    cm.dispose();
    var enter = false;
    var cHat = cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIPPED).getItem(-1);
    if (cHat != null) {
        if (cHat.getItemId() == 1003134) {
            enter = true;
        }
    }
    if (enter == true) {
        cm.warp(310050000, "west00");
    } else {
        cm.getPlayer().dropMessage(5, "블랙윙을 증명하는 증표 없이는 입장이 불가능합니다. 블랙윙의 모자를 머리에 착용해 주세요.");
    }
}