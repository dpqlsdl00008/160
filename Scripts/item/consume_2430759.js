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
            if (cm.getPlayer().getItemQuantity(2430759, false) < 9) {
                cm.dispose();
                cm.sendNext("\r\n#b#z2430759##k #r9개#k를 모으면 #i2049501# #b#z2049501##k, #r18개#k를 모으면 #i2049300# #b#z2049300##k, #r27개#k를 모으면 #i2049701# #b#z2049701##k를 받을 수 있습니다.");
                return;
            }
            var say = "#b#z2430759# #c2430759#개#k를 가지고 있습니다. #z2049501#은 #r9개#k, #z2049300#는 #r18개#k, #z2049701#는 #r27개#k를 사용하여 교환 할 수 있습니다.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            say += "\r\n#L0##i2049501# #b#z2049501##k";
            say += "\r\n#L1##i2049300# #b#z2049300##k";
            say += "\r\n#L2##i2049701# #b#z2049701##k";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            if (cm.getPlayer().getItemQuantity(2430759, false) < (selection == 0 ? 9 : selection == 1 ? 18 : 27)) {
                cm.dispose();
                cm.sendNext("\r\n죄송하지만 #b#z2430759##k이 충분하지 않으신 것 같네요.");
                return;
            }
            if (cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.getByType(2)).getNumFreeSlot() < 1) {
                cm.dispose();
                cm.sendNext("\r\n죄송하지만 인벤토리 공간이 충분하지 않으신 것 같네요. #b소비#k탭의 인벤토리 공간을 비워주세요.");
                return;
            }
            cm.askAcceptDecline("#b#z2430759# #c2430759#개#k를 가지고 있습니다. #z2430759# " + (selection == 0 ? 9 : selection == 1 ? 18 : 27) + "개를 사용하여 #i" + (selection == 0 ? 2049501 : selection == 1 ? 2049300 : 2049701) + "# #b#z" + (selection == 0 ? 2049501 : selection == 1 ? 2049300 : 2049701) + "##k" + (selection == 0 ? "으" : "") + "로 교환 하시겠습니까?");
            beatrice = selection;
            break;
        }
        case 2: {
            cm.dispose();
            beatrice = selection;
            cm.gainItem(2430759, -(beatrice == 0 ? 9 : beatrice == 1 ? 18 : 27));
            cm.gainItem((beatrice == 0 ? 2049501 : beatrice == 1 ? 2049300 : 2049701), 1);
            break;
        }
    }
}