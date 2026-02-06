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
            if (cm.getPlayer().getItemQuantity(2430112, false) < 9) {
                cm.dispose();
                cm.sendNext("\r\n#b#z2430112##k #r5개#k를 모으면 #i5062000# #b#z5062000##k, #b#z2430112##k #r10개#k를 모으면 #i2049401# #b#z2049401##k, #r20개#k를 모으면 #i2049400# #b#z2049400##k를 받을 수 있습니다.");
                return;
            }
            var say = "#b#z2430112# #c2430112#개#k를 가지고 있습니다. #z5062000#는 #r5개#k, #z2049401#은 #r10개#k, #z2049400#는 #r20개#k를 사용하여 교환 할 수 있습니다.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            say += "\r\n#L2##i5062000# #b#z5062000##k";
            say += "\r\n#L0##i2049401# #b#z2049401##k";
            say += "\r\n#L1##i2049400# #b#z2049400##k";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            if (cm.getPlayer().getItemQuantity(2430112, false) < (selection == 2 ? 5 : selection == 0 ? 10 : 20)) {
                cm.dispose();
                cm.sendNext("\r\n죄송하지만 #b#z2430112##k이 충분하지 않으신 것 같네요.");
                return;
            }
            if (cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.getByType(2)).getNumFreeSlot() < 1) {
                cm.dispose();
                cm.sendNext("\r\n죄송하지만 인벤토리 공간이 충분하지 않으신 것 같네요. #b소비#k탭의 인벤토리 공간을 비워주세요.");
                return;
            }
            if (cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.getByType(5)).getNumFreeSlot() < 1) {
                cm.dispose();
                cm.sendNext("\r\n죄송하지만 인벤토리 공간이 충분하지 않으신 것 같네요. #b캐쉬#k탭의 인벤토리 공간을 비워주세요.");
                return;
            }
            cm.askAcceptDecline("#b#z2430112# #c2430112#개#k를 가지고 있습니다. #z2430112# " + (selection == 2 ? 5 : selection == 0 ? 10 : 20) + "개를 사용하여 #i" + (selection == 2 ? 5062000 : selection == 0 ? 2049401 : 2049400) + "# #b#z" + (selection == 2 ? 5062000 : selection == 0 ? 2049401 : 2049400) + "##k로 교환 하시겠습니까?");
            beatrice = selection;
            break;
        }
        case 2: {
            cm.dispose();
            cm.gainItem(2430112, -(beatrice == 2 ? 5 : beatrice == 0 ? 10 : 20));
            cm.gainItem((beatrice == 2 ? 5062000 : beatrice == 0 ? 2049401 : 2049400), 1);
            break;
        }
    }
}