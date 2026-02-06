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
            if (cm.itemQuantity(2434733) < 10) {
                cm.dispose();
                cm.sendNext("\r\n#Cgreen##z2434733##Cgray#을 일정 갯수 이상 모을 시에 아래의 아이템을 획득 할 수 있습니다.\r\n\r\n#i1113313# #z1113313# #Cyellow#(10개 필요)#Cgray#\r\n#i2539000# #z2539000# #Cyellow#(30개 필요)#Cgray#");
                return;
            }
            var say = "　 #Cgray#가디언 엔젤 링은 10개, 가디언 엔젤 링 세트 변환 주문서\r\n　 는 30개를 사용하여 교환 할 수 있습니다.";
            say += "\r\n\r\n　 #fUI/UIWindow.img/QuestIcon/3/0##Cgreen#";
            say += "\r\n#L0##i1113313# #z1113313#";
            say += "\r\n#L1##i2539000# #z2539000#";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            if (!cm.haveItem(2434733, selection == 0 ? 10 : 30)) {
                cm.dispose();
                cm.sendNext("\r\n#Cgray#죄송하지만 #Cgreen##z2434733##Cgray#이 충분하지 않으신 것 같네요.");
                return;
            }
            if (cm.getInventory(selection == 0 ? 1 : 2).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.askAcceptDecline("#Cyellow##z2434733# #c2434733#개#Cgray#를 가지고 있습니다. #z2434733# " + (selection == 0 ? 10 : 30) + "개를 사용하여 #Cgreen##z" + (selection == 0 ? 1113313 : 2539000) + "##Cgray#" + (selection == 0 ? "으" : "") + "로 교환 하시겠습니까?");
            beatrice = selection;
            break;
        }
        case 2: {
            cm.dispose();
            cm.gainItem(2434733, -(beatrice == 0 ? 10 : 30));
            cm.gainItem((beatrice == 0 ? 1113313 : 2539000), 1);
            break;
        }
    }
}