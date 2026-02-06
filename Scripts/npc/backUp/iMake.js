var status = -1;
var mCategory = ["활 제작", "석궁 제작", "장갑 제작", "장갑 합성", "재료 제작", "화살 제작"];

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            var say = "흐음... 궁수의 아이템이 필요한 건가? 그렇다면 내가 도와 줄 수 있는데... 필요한 물건이라도 있어? 어떤 물건이 필요하지?#d";
            for (i = 0; i < mCategory.length; i++) {
                say += "\r\n#L" + i + "#" + mCategory[i];
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            var say = "난 저격수였지. 하지만 활과 석궁은 그다지 차이가 많이 나진 않아... 어쨌든, 뭘 만들고 싶지?#d";
            itemID = [1452002, 1452003, 1452001, 1452000, 1452005, 1452006, 1452007];
            switch (selection) {
                case 1: {
                    say = "나는 한때 저격수였어. 석궁은 내 전문이지. 어떤 것을 만들어줄까?#d";
                    itemID = [1462001, 1462002, 1462003, 1462000, 1462004, 1462005, 1462006, 1462007];
                    break;
                }
                case 2: {
                    say = "좋아. 어떤 장갑을 만들어줬으면 좋겠지?#d";
                    itemID = [1082012, 1082013, 1082016, 1082048, 1082068, 1082071, 1082084, 1082089];
                    break;
                }
                case 3: {
                    say = "좋아. 어떤 장갑을 만들어줬으면 좋겠지?#d";
                    itemID = [1082015, 1082014, 1082017, 1082018, 1082049, 1082050, 1082069, 1082070, 1082072, 1082073, 1082085, 1082083, 1082090, 1082091];
                    break;
                }
                case 4: {
                    say = "재료? 몇 가지 재료 제작 방법을 알고 있으니 만들어 줄 수 있겠어.#d";
                    itemID = [2060000, 2061000, 2060001, 2061001, 2060002, 2061002];
                    break;
                }
                case 5: {
                    say = "화살? 문제 없지.#d";
                    itemID = [2060000, 2061000, 2060001, 2061001, 2060002, 2061002];
                    break;
                }
            }
            if (selection != 4 && selection != 5) {
                say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
            }
            if (selection == 4) {
                status = 99;
                var itemStr = ["나뭇가지로 가공된 나무 제작", "장작으로 가공된 나무 제작", "나사 15개 제작"];
                for (i = 0; i < itemStr.length; i++) {
                    say += "\r\n#L" + i + "#" + itemStr[i];
                }
                cm.sendSimple(say);
                return;
            }
            for (i = 0; i < itemID.length; i++) {
                say += "\r\n#L" + i + "#";
                if (selection != 5) {
                    say += "#i" + itemID[i] + "#" + " (Lv." + Packages.server.MapleItemInformationProvider.getInstance().getEquipStats(itemID[i]).get("reqLevel") + ") ";
                }
                say += "#z" + itemID[i] + "#";
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            itemReq = [4003001, 4000000, 800, 5, 30];
            switch (itemID[selection]) {
                case 1452003: {
                    itemReq = [4011001, 4003000, 2000, 1, 3];
                    break;
                }
                case 1452001: {
                    itemReq = [4003001, 4000016, 3000, 30, 50];
                    break;
                }
                case 1452000: {
                    itemReq = [4011001, 4021006, 4003000, 5000, 2, 2, 8];
                    break;
                }
                case 1452005: {
                    itemReq = [4011001, 4011006, 4021003, 4021006, 4003000, 30000, 5, 5, 3, 3, 30];
                    break;
                }
                case 1452006: {
                    itemReq = [4011004, 4021000, 4021004, 4003000, 40000, 7, 6, 3, 35];
                    break;
                }
                case 1452007: {
                    itemReq = [4021008, 4011001, 4011006, 4003000, 4000014, 80000, 1, 10, 3, 40, 50];
                    break;
                }
            }
            var say = "만들고 싶은 아이템이 #b#z" + itemID[selection] + "##k인가? 재료는 아래를 참조 해.\r\n#d";
            say += "\r\n#i" + itemReq[0] + "# #z" + itemReq[0] + "# " + itemReq[3] + "개";
            say += "\r\n#i" + itemReq[1] + "# #z" + itemReq[1] + "# " + itemReq[4] + "개";
            if (itemReq.length == 7) {
                say += "\r\n#i" + itemReq[2] + "# #z" + itemReq[2] + "# " + itemReq[3] + "개";
            }
            say += "\r\n#i4031138# " + cm.getPlayer().getNum(itemReq[2]) + " 메소";
            cm.sendYesNo(say);
            break;
        }
    }
}