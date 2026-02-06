var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 1) {
            if (status == 1) {
                cm.sendNext("\r\n아직 이곳에서 볼일이 남으신 모양이지요?");
            }
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            var say = "배는 매 시간 정각 기준으로 15분 마다 출발하고 있으며, 출발 5분 전부터 표를 받고 있답니다.";
            if (cm.getLevel() < 15) {
                cm.dispose();
                say += " 흐음... 그런데 당신은 아직 오시리아 대륙으로 가보시기엔 너무 약해보이시는군요. 조금 더 수련을 하신 후 다시 찾아오세요.";
                cm.sendNext("\r\n" + say);
                break;
            }
            say += "\r\n#L0##b배에 탑승하고 싶습니다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            var eManager = cm.getEventManager("move_ship");
            if (eManager == null) {
                cm.dispose();
                return;
            }
            if (eManager.getProperty("ready").equals("false")) {
                cm.dispose();
                cm.sendNext("\r\n아직 배가 출항 준비 중에 있습니다. 배는 매 시간 정각 기준 10, 25, 40, 55분에 출항을 준비하며, 최장 30분 이내에 출항 준비가 완료됩니다.");
                return;
            }
            if (eManager.getProperty("entry").equals("false") && eManager.getProperty("docked").equals("true")) {
                cm.dispose();
                cm.sendNext("\r\n이미 배가 출항 준비 중에 있습니다. 죄송하지만 지금은 배에 탑승하실 수 없답니다. 표는 출발하기 1분 이전에만 받고 있답니다. 다음 배를 기다려보세요.");
                return;
            }
            if (eManager.getProperty("entry").equals("false")) {
                cm.dispose();
                cm.sendNext("\r\n이미 배가 오르비스로 출발했답니다. 배는 매 시간 기준으로 15분 마다 출발하니 잠시만 기다려 보세요.");
                return;
            }
            cm.sendYesNo("아직 배에 탑승 할 여유가 있다고 합니다. 정말 배에 탑승하고 싶으세요?");
            break;
        }
        case 2: {
            cm.dispose();
            if (!cm.haveItem(4031045, 1)) {
                cm.sendNext("\r\n흐음... #b#z4031045#k은 분명 제대로 갖고 계신건가요? 티켓이 없으시다면 왼쪽에서 티켓을 구매 하실 수 있답니다.");
                return;
            }
            cm.gainItem(4031045, -1);
            cm.warp(cm.getMapId() + 1, 0);
            break;
        }
    }
}