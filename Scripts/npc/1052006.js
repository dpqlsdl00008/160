var status = -1;

var mTicket = [
[4031036, 500, "B1"],
[4031037, 1200, "B2"],
[4031038, 2000, "B3"],
];

function start() {
    action (1, 0, 0);
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
            if (cm.getLevel() < 20) {
                cm.dispose();
                cm.sendNext("\r\n안녕하세요~ 커닝시티 지하철 입니다. 아직 공사중인 구간이 많아 위험하답니다~ 흐음.. 아직 공사중 구간에 들어가시긴 너무 약해보이시는데요~");
                return;
            }
            var say = "안으로 들어가고 싶으시다면 표를 구입하셔야 해요. 표를 구입하신 후 오른쪽에 있는 개찰구를 통해 안으로 들어가실 수 있죠. 어떤 표를 구입하시겠어요?#b";
            for (i = 0; i < mTicket.length; i++) {
                say += "\r\n#L" + i + "##b공사장 " + mTicket[i][2] + "#k";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            cm.sendYesNo("#b공사장 " + mTicket[selection][2] + "#k을 구입하시겠어요? 가격은 " + cm.getPlayer().getNum(mTicket[selection][1]) + " 메소랍니다. 구입 하시기 전에 아이템의 기타창에 빈 칸이 있는지 먼저 확인해 주세요.");
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getMeso() < mTicket[v1][1] || cm.canHold(mTicket[v1][0]) == false) {
                cm.sendNext("\r\n요금이 부족하신것 같은데요. 혹은 인벤토리 공간이 부족하신 것 같은데요? 다시 한번 확인해 보세요.");
                return;
            }
            cm.gainMeso(-(mTicket[v1][1]));
            cm.gainItem(mTicket[v1][0], 1);
            break;
        }
    }
}