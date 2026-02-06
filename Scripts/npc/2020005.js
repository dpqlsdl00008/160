var status = -1;

var value = 10000002;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

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
            if (cm.getPlayer().getOneInfoQuest(value, "nElixer_date").equals(date) == false) {
                cm.getPlayer().updateOneInfoQuest(value, "nElixer_buy", "0");
                cm.getPlayer().updateOneInfoQuest(value, "nElixer_date", date);
            }
            if (cm.getPlayer().getOneInfoQuest(value, "pElixer_date").equals(date) == false) {
                cm.getPlayer().updateOneInfoQuest(value, "pElixer_buy", "0");
                cm.getPlayer().updateOneInfoQuest(value, "pElixer_date", date);
            }
            nElixer = parseInt(cm.getPlayer().getOneInfoQuest(value, "nElixer_buy"));
            pElixer = parseInt(cm.getPlayer().getOneInfoQuest(value, "pElixer_buy"));
            itemID = [
            [2050003, 300, 999, "저주와 봉인 상태 이상을 해제하는"],
            [2050004, 400, 999, "모든 상태 이상을 해제하는"], 
            [4006000, 3000, 999, "강력한 마법을 사용하는 데 필요한"],
            [4006001, 3000, 999, "강력한 소환 마법 아이템을 사용하는 데 필요한"],
            [2000004, 3800, (200 - nElixer), "HP와 MP를 최대 HP/MP의 50% 만큼 회복하는"],
            [2000005, 6800, (70 - pElixer), "HP와 MP를 모두 회복하는"],
            ];
            if (cm.isQuestFinished(3035) == false) {
                cm.dispose();
                cm.sendNext("\r\n나는 이곳에서 30년 이상을 살아온 연금술사, 알케스터라고 한다네.");
                return;
            }
            var say = "오오, 자네는 저번에 #b#t4031056##k을 찾아 준 고마운 젊은이가 아닌가! 어떻게 찾아왔는가? 무엇인가 필요한 아이템이라도 있는가?";
            for (i = 0; i < itemID.length; i++) {
                say += "\r\n#L" + i + "##d#z" + itemID[i][0] + "# (가격 : " + cm.getPlayer().getNum(itemID[i][1]) + " 메소)";
                if (itemID[i][2] != 999) {
                    say += " (오늘 남은 수량 : " + itemID[i][2] + "개)";
                }
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            cm.sendGetNumber("\r\n흐음... #d#z" + itemID[selection][0] + "##k 아이템을 구매하고 싶단 말이지...? " + itemID[selection][3] + " 아이템일세. 몇 개를 구매하고 싶은가? 가격은 #d" + cm.getPlayer().getNum(itemID[selection][1]) + " 메소#k일세.\r\n　\r\n", 1, 1, itemID[selection][2]);
            break;
        }
        case 2: {
            v2 = v1;
            v3 = selection;
            if (selection < 1) {
                cm.dispose();
                return;
            }
            if (itemID[v1][2] != 999) {
                if ((itemID[v1][2] - selection) < 1) {
                    cm.dispose();
                    cm.sendNext("\r\n흐음. 오늘 구매 할 수 있는 양보다 적게 선택하게나. 오늘은 #d" + itemID[v1][2] + "개#k의 #z" + itemID[v1][0] + "##k를 더 구매 할 수 있다네.");
                    return;
                }
            }
            cm.sendYesNo("흠... #d" + selection + "개의 #z" + itemID[v1][0] + "##k 아이템을 구매하고 싶다 이건가? 가격은 개당 " + cm.getPlayer().getNum(itemID[v1][1]) + " 메소일세. 총 가격은 #d" + cm.getPlayer().getNum((itemID[v1][1] * selection)) + " 메소#k라네. 정말 구매하고 싶은가?");
            break;
        }
        case 3: {
            cm.dispose();
            if (cm.getMeso() < (itemID[v2][1] * v3)) {
                cm.sendNext("\r\n자네... 분명 메소는 제대로 갖고 있는건가? 아니면 인벤토리 공간이 부족한 건 아닌가?");
                return;
            }
            if (itemID[v2][0] == 2000004) {
                var v4 = parseInt(cm.getPlayer().getOneInfoQuest(value, "nElixer_buy"));
                cm.getPlayer().updateOneInfoQuest(value, "nElixer_buy", (v4 + v3) + "");
            }
            if (itemID[v2][0] == 2000005) {
                var v4 = parseInt(cm.getPlayer().getOneInfoQuest(value, "pElixer_buy"));
                cm.getPlayer().updateOneInfoQuest(value, "pElixer_buy", (v4 + v3) + "");
            }
            cm.gainMeso(-(itemID[v2][1] * v3));
            cm.gainItem(itemID[v2][0], v3);
            cm.sendNext("자, 여기있네. 또 필요하면 언제든지 다시 찾아오게나.");
            break;
        }
    }
}