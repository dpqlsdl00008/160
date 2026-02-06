var status = -1;

var cUse = [
[2000002, 310, 300, "HP"], 
[2022003, 1060, 1000, "HP"], 
[2022000, 1600, 800, "MP"], 
[2001000, 3120, 1000, "HP와 MP"], 
];

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 3) {
            if (status == 3) {
                cm.sendNext("\r\n아직 재료는 많이 남아 있답니다. 천천히 생각해 보시고 다시 말을 걸어주세요.");
            }
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (cm.isQuestFinished(2013) == false) {
                cm.dispose();
                cm.sendNext("\r\n제 꿈은 이곳 저곳 여행을 다니는 거랍니다. 여행자님처럼 말이죠... 하지만 저희 아버진 위험하다며 계속 허락해 주시지를 않아요...");
                return;
            }
            cm.sendNext("\r\n당신이군요. 당신 덕분에 많은 것들을 해낼 수 있었어요. 여행자님을 위해서 물약을 만들어 드리겠어요.");
            break;
        }
        case 1: {
            var say = "어떤 아이템을 구매하시고 싶으세요?\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0##b";
            for (i = 0; i < cUse.length; i++) {
                say += "\r\n#L" + i + "##i" + cUse[i][0] + "# #z" + cUse[i][0] + "# (" + cm.getPlayer().getNum(cUse[i][1]) + " 메소)";
            }
            cm.sendSimple(say);
            break;
        }
        case 2: {
            v1 = selection;
            cm.sendGetNumber("#b#z" + cUse[selection][0] + "##k 아이템을 구매하고 싶으세요? #z" + cUse[selection][0] + "# 아이템은 #r" + cm.getPlayer().getNum(cUse[selection][2]) + " " + cUse[selection][3] + "를 회복#k시켜 줍니다. 몇 개를 구매하시고 싶으세요?", 1, 1, 999);
            break;
        }
        case 3: {
            v2 = v1;
            v3 = selection;
            if (selection < 1) {
                return;
            }
            cm.sendYesNo("#b#z" + cUse[v1][0] + "##k 아이템을 #b" + selection + "개#k 구매하시고 싶으세요? 개당 가격은 #r" + cm.getPlayer().getNum(cUse[v1][1]) + " 메소#k 이며, 총 가격은 #r" + cm.getPlayer().getNum(cUse[v1][1] * selection) + " 메소#k입니다.");
            break;
        }
        case 4: {
            cm.dispose();
            if (cm.getMeso() < (cUse[v2][1] * v3)) {
                cm.sendNext("\r\n메소가 부족하신건 아닌가요? 혹은 인벤토리 공간이 충분한 지 확인해 주세요.");
                return;
            }
            if (!cm.canHold(cUse[v2][0])) {
                cm.sendNext("\r\n메소가 부족하신건 아닌가요? 혹은 인벤토리 공간이 충분한 지 확인해 주세요.");
                return;
            }
            cm.gainMeso(-(cUse[v2][1] * v3));
            cm.gainItem(cUse[v2][0], v3);
            cm.sendNext("\r\n와주셔서 고마워요. 더 도와 드릴 일이 있다면 언제든지 다시 찾아와 주세요.");
            break;
        }
    }
}