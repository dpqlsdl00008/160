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
            var uSlot = cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.USE).findById(2430144);
            if (uSlot == null) {
                return;
            }
            var sPos = uSlot.getPosition();
            Packages.handling.channel.handler.InventoryHandler.UseRewardItem(sPos, 2290285, cm.getClient(), cm.getPlayer());
            break;
         }
    }
}