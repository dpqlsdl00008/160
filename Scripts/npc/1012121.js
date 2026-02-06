var status = -1;

var dItem = [
[1, "장비"],
[2, "소비"],
[3, "설치"],
[4, "기타"],
[5, "캐쉬"],
];

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status > 3) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
//cm.teachSkill(24121001, 0, 20);
            cm.sendYesNoS("의류 수거함을 오픈할까요?", 4);
            break;
        }
        case 1: {
            var say = "버리고자 하는 아이템의 분류를 선택하여 주세요.#d";
            for (i = 0; i < dItem.length; i++) {
                say += "\r\n#L" + i + "#" + dItem[i][0] + ". " + dItem[i][1];
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            if (selection < 0) {
                return;
            }
            v1 = selection;
            getList(dItem[selection][0]);
            break;
        }
        case 3: {
            if (selection < 0) {
                return;
            }
            if (v1 < 0) {
                return;
            }
            v2 = v1;
            v3 = selection;
            var say = "버리고자 하는 아이템은 아래와 같습니다.\r\n#r버린 아이템은 회수 할 수 없습니다.#d";
            var a4 = getItem(dItem[v1][0], selection).getItemId();
            if (dItem[v1][0] > 1 && !Packages.constants.GameConstants.isPet(a4)) {
                status = 5;
                var say = "버리고자 하는 아이템은 아래와 같습니다.#d";
                say += "\r\n\r\n#i" + a4 + "# #z" + a4 + "#";
                say += "\r\n\r\n#r버리고자 하는 아이템의 수량을 입력하여 주세요.";
                cm.sendGetNumber(say, 1, 1, cm.itemQuantity(a4));
                return;
            }
            say += "\r\n\r\n#i" + a4 + "# #z" + a4 + "# 1개";
            cm.sendYesNo(say);
            break;
        }
        case 4: {
            if (v2 < 0) {
                return;
            }
            if (v3 < 0) {
                return;
            }
            cm.dispose();
            var a5 = getItem(dItem[v2][0], v3).getItemId();
            cm.removeSlot(dItem[v2][0], v3, 1);
            cm.sendNext("\r\n#d#z" + a5 + "# 1개#k가 정상적으로 수거 되었습니다.");
            cm.getClient().sendPacket(Packages.tools.packet.CWvsContext.InfoPacket.getShowItemGain(a5, -1, true));
            break;
        }
        case 5: {
            cm.dispose();
            break;
        }
        case 6: {
            if (selection < 0) {
                return;
            }
            if (v2 < 0) {
                return;
            }
            if (v3 < 0) {
                return;
            }
            v4 = v2;
            v5 = v3;
            v6 = selection;
            var say = "버리고자 하는 아이템은 아래와 같습니다.\r\n#r버린 아이템은 회수 할 수 없습니다.#d";
            var a5 = getItem(dItem[v2][0], v3).getItemId();
            say += "\r\n\r\n#i" + a5 + "# #z" + a5 + "# " + selection + "개";
            cm.sendYesNo(say);
            break;
        }
        case 7: {
            if (v4 < 0) {
                return;
            }
            if (v5 < 0) {
                return;
            }
            if (v6 < 0) {
                return;
            }
            cm.dispose();
            var a6 = getItem(dItem[v4][0], v5).getItemId();
            cm.removeSlot(dItem[v4][0], v5, v6);
            cm.sendNext("\r\n#d#z" + a6 + "# " + v6 + "개#k가 정상적으로 수거 되었습니다.");
            cm.getClient().sendPacket(Packages.tools.packet.CWvsContext.InfoPacket.getShowItemGain(a6, -v6, true));
            break;
        }
    }
}

function getList(type) {
    var say = "버리고자 하는 아이템을 의류 수거함에서 선택하여 주세요. 버린 아이템은 회수 할 수 없습니다.";
    say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0##d";
    var a1 = false;
    for (var i = 1; i < cm.getInventory(type).getSlotLimit(); i++) {
        if (getItem(type, i)) {
            var a2 = cm.getInventory(type).getItem(i);
            if (a2 == null) {
                continue;
            }
            a1 = true;
            var a3 = getItem(type, i).getItemId();
            say += "\r\n#L" + i + "#" + i + ". #i" + a3 + "# #z" + a3 + "#";
            if (type > 1 && Packages.constants.GameConstants.isPet(a3) == false) {
                say += " (" + cm.getPlayer().getNum(cm.itemQuantity(a3)) + ")";
            }
        }
    }
    if (!a1) {
        cm.dispose();
        cm.sendNext(say);
        return;
    }
    cm.sendSimple(say);
}

function getItem(type, slot) {
    return cm.getInventory(type).getItem(slot);
}