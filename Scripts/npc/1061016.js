var status = -1;
var itemID = [2040732, 2040733, 2040734, 2040735, 2040736, 2040737, 2040738, 2040728, 2040729, 2040730, 2040731, 2040739];

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            var say = "또 왔어? 자주 보네? 꽤 한가 한가 봐? 나한테 부탁할 게 있는 눈치네? 뭐 발록의 가죽이라도 구해 온 거야?#d";
            say += "\r\n#L0#발록의 가죽 조각으로 주문서를 만들어 주세요.";
            say += "\r\n#L1#발록의 가죽 조각 20개로 가죽 신발을 만들어 주세요.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            reqItem = 5;
            switch (selection) {
                case 0: {
                    var say = "또 왔어? 자주 보네? 꽤 한가 한가 봐? 나한테 부탁할 게 있는 눈치네? 뭐 발록의 가죽이라도 구해 온 거야?#d";
                    for (i = 0; i < itemID.length; i++) {
                        if (i > 6) {
                            reqItem = 10;
                        }
                        say += "\r\n#L" + i + "##z" + 4001261 + "# " + reqItem + "개 - #i" + itemID[i] + "# #z" + itemID[i] + "#";
                    }
                    cm.sendSimple(say);
                    break;
                }
                case 1: {
                    cm.dispose();
                    reqItem = 20;
                    if (cm.haveItem(4001261, reqItem) == false) {
                        cm.sendNext("\r\n#i4001261# #d#z4001261# (" + cm.itemQuantity(4001261) + "/" + reqItem + ")#k");
                        return;
                    }
                    cm.gainItem(4001261, -reqItem);
                    var cRand = Packages.server.Randomizer.nextInt(100);
                    if (cRand < 30) {
                        cm.sendNext("\r\n껄껄껄. 앞으로도 운을 시험하고 싶다면 찾아 와.");
                        return;
                    }
                    cm.gainItem(1072375, 1);
                    cm.sendNext("\r\n어때? 멋진 신발이지? 보기엔 평범해 보여도 발록의 가죽으로 만든 신발이라서 굉장히 튼튼하다고.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1072375# #d#z1072375# 1개#k");
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            reqItem = 5;
            if (selection > 6) {
                reqItem = 10;
            }
            if (cm.haveItem(4001261, reqItem) == false) {
                cm.sendNext("\r\n#i4001261# #d#z4001261# (" + cm.itemQuantity(4001261) + "/" + reqItem + ")#k");
                return;
            }
            cm.gainItem(4001261, -reqItem);
            cm.gainItem(itemID[selection], 1);
            cm.sendNext("\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i" + itemID[selection] + "# #d#z" + itemID[selection] + "# 1개#k"); 
            break;
        }
    }
}