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
            var itemID = 0;
            switch (cm.getJob()) {
                case 112: {
                    itemID = 2290448;
                    break;
                }
                case 122: {
                    itemID = 2290449;
                    break;
                }
                case 132: {
                    itemID = 2290450;
                    break;
                }
                case 212: {
                    itemID = 2290451;
                    break;
                }
                case 222: {
                    itemID = 2290452;
                    break;
                }
                case 232: {
                    itemID = 2290453;
                    break;
                }
                case 312: {
                    itemID = 2290454;
                    break;
                }
                case 322: {
                    itemID = 2290455;
                    break;
                }
                case 422: {
                    itemID = 2290456;
                    break;
                }
                case 412: {
                    itemID = 2290457;
                    break;
                }
                case 434: {
                    itemID = 2290458;
                    break;
                }
                case 512: {
                    itemID = 2290459;
                    break;
                }
                case 522: {
                    itemID = 2290460;
                    break;
                }
                case 532: {
                    itemID = 2290461;
                    break;
                }
                case 2112: {
                    itemID = 2290462;
                    break;
                }
                case 2216: 
                case 2217: 
                case 2218: {
                    itemID = 2290463;
                    break;
                }
                case 2312: {
                    itemID = 2290464;
                    break;
                }
                case 2412: {
                    itemID = 2290571;
                    break;
                }
                case 3112: {
                    itemID = 2290465;
                    break;
                }
                case 3212: {
                    itemID = 2290466;
                    break;
                }
                case 3312: {
                    itemID = 2290467;
                    break;
                }
                case 3512: {
                    itemID = 2290468;
                    break;
                }
                case 5112: {
                    itemID = 2290602;
                    break;
                }
            }
            if (itemID == 0) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CUserLocal.chatMsg(Packages.handling.ChatType.GameDesc, "크로스 헌터 마스터리 북 사용에 실패하였습니다."));
                return;
            }
            var uSlot = cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.USE).findById(2430668);
            if (uSlot == null) {
                return;
            }
            var sPos = uSlot.getPosition();
            Packages.handling.channel.handler.InventoryHandler.UseRewardItem(sPos, itemID, cm.getClient(), cm.getPlayer());
            break;
         }
    }
}