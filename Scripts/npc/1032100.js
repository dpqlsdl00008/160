var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 2) {
            if (status == 2) {
                cm.sendNext("\r\n#b#z" + mItem[v1] + "##k을 제작하는건 쉬운 일이 아니랍니다... 재료를 제게 가져와 주세요.");
            }
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (cm.getLevel() < 40) {
                cm.dispose();
                cm.sendNext("\r\n저만이 만들 수 있는 희귀한 물건은 있지만... 아직 약하시고... 누군지도 모르는 당신에게는 만들어 드릴 수 없어요.");
                return;
            }
            cm.sendNext("\r\n음... 제가 바로 소문의 최고의 연금술사입니다. 음... 오랜 시간동안 인간과 접촉한 요정은 없었지만... 당신같이 강한 사람은 괜찮아 보이는군요... 특별히 당신을 위해서는 아이템을 제작해 드리도록 하지요.");
            break;
        }
        case 1: {
            mItem = [4011007, 4021009, 4031042];
            var say = "무얼 만들어 보고 싶으신가요?#d";
            for (i = 0; i < mItem.length; i++) {
                say += "\r\n#L" + i + "##t" + mItem[i] + "#";
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            v1 = selection;
            switch (selection) {
                case 0: {
                    rItem = [[4011000, 1], [4011001, 1], [4011002, 1], [4011003, 1], [4011004, 1], [4011005, 1], [4011006, 1]];
                    rPrice = 10000;
                    break;
                }
                case 1: {
                    rItem = [[4021000, 1], [4021001, 1], [4021002, 1], [4021003, 1], [4021004, 1], [4021005, 1], [4021006, 1], [4021007, 1], [4021008, 1]];
                    rPrice = 15000;
                    break;
                }
                case 2: {
                    rItem = [[4011007, 1], [4001006, 1], [4021008, 1]];
                    rPrice = 30000;
                    break;
                }
            }
            var say = "만들고 싶은 아이템이 #i" + mItem[selection] + "# #b#z" + mItem[selection] + "##k인가요? 재료는 다음과 같아요.\r\n";
            for (i = 0; i < rItem.length; i++) {
                say += "\r\n#d#i" + rItem[i][0] + "# #z" + rItem[i][0] + "# " + rItem[i][1] + "개";
            }
            say += "\r\n#i4031138# #d" + cm.getPlayer().getNum(rPrice) + " 메소#k";
            cm.sendYesNo(say);
            break;
        }
        case 3: {
            cm.dispose();
            var cDo = true;
            if (!cm.canHold(mItem[v1])) {
                cDo = false;
            }
            for (i = 0; i < rItem.length; i++) {
                if (!cm.haveItem(rItem[i][0], rItem[i][1])) {
                    cDo = false;
                }
            }
            if (cm.getMeso() < rPrice) {
                cDo = false;
            }
            if (!cDo) {
                cm.sendNext("\r\n메소는 충분히 갖고 계신지, 또는 재료가 부족한건 아닌지, 인벤토리 공간이 충분한지 다시 한번 확인해 주세요.");
                return;
            }
            for (j = 0; j < rItem.length; j++) {
                cm.gainItem(rItem[j][0], -rItem[j][1]);
            }
            cm.gainMeso(-rPrice);
            cm.gainItem(mItem[v1], 1);
            cm.sendNext("\r\n다 되었답니다. 더 필요한 물건이 있으시면 언제라도 다시 찾아와주세요..");
            break;
        }
    }
}