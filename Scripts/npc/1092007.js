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
            if (cm.isQuestActive(2175) == false) {
                cm.sendNext("\r\n흐음, 나에게 무슨 볼 일 이라도 있는 건가?");
                cm.dispose();
                return;
            }
            cm.sendNext("\r\n준비가 다 되었나? 좋아! 바로 검은 마법사 수하들이 있는 곳으로 보내주지. 내가 보내 주는 곳에 있는 돼지들을 잘 살펴보면 찾을 수 있을 거야.");
            break;
        }
        case 1: {
            cm.sendNext("\r\n그들은 힘이 약해지면 본 모습으로 나타나니 반드시 의심되는 녀석이 있거든 힘이 약해지도록 싸울 수 밖에 없네. 그럼 좋은 소식을 가지고 오게.");
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getPlayerCount(912000000) != 0) {
                cm.sendNext("\r\n이미 이 안에 다른 누군가가 들어가 있네. 나중에 다시 와주게.");
                return;
            }
            cm.warp(912000000, "ntq1");
            break;
        }
    }
}