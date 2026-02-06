var status = -1;

var jobName = "도적#k#n을";
var jobMap = 910310000;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 2) {
            if (status == 2) {
                cm.sendNext("\r\n언제라도 피로를 풀고 싶으시면 찾아오세요.");
            }
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendNext("\r\n안녕하세요~ 슬리피우드 호텔에 오신것을 환영합니다.");
            break;
        }
        case 1: {
            var say = "두 가지 종류의 사우나가 준비되어 있습니다. 어디로 가보시겠어요?#b";
            say += "\r\n#L0#일반 사우나 입장 (499 메소)";
            say += "\r\n#L1#고급 사우나 입장 (999 메소)";
            cm.sendSimple(say);
            break;
        }
        case 2: {
            v1 = selection;
            var say = "선택하신 사우나로 입장 하시겠습니까? 평소보다 더 많은 HP와 MP가 회복됩니다.";
            if (selection == 1) {
                say = "고급 사우나를 선택하셨습니다. 정말 그곳으로 입장 하시겠습니까? 평소보다 더 많은 HP와 MP가 회복되며, 특별한 아이템을 구매 할 수도 있습니다.";
            }
            cm.sendYesNo(say);
            break;
        }
        case 3: {
            cm.dispose();
            if (cm.getMeso() < (v1 == 0 ? 499 : 999)) {
                cm.sendNext("\r\n메소가 부족합니다.");
                return;
            }
            cm.gainMeso(-(v1 == 0 ? 499 : 999));
            cm.warp(105000011 + v1, "out00");
            break;
        }
    }
}