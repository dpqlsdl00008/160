var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.sendNext("\r\n아직 준비가 덜 된 모양인가? 준비가 되는 대로 나를 찾아오게.");
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            if (cm.isQuestActive(3310) == false) {
                cm.dispose();
                cm.sendNext("\r\n제뉴미스트... 진리를 탐구하는 생명 연금의 자랑스러운 학파라네.");
                return;
            }
            if (cm.haveItem(4031709, 1) == true) {
                cm.dispose();
                cm.sendNext("\r\n이미 #b#t4031709##k를 구해 온 것 같구만. 그렇다면 이 안에 다시 들어 갈 필요는 없어보이네.");
                return;
            }
            cm.askAcceptDecline("흐음, 지금 바로 폐쇄된 연구실로 가보겠는가?");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getPlayerCount(926120100) != 0) {
                cm.sendNext("\r\n흐음, 이미 이 안에 다른 누군가가 퀘스트에 도전하고 있네. 나중에 다시 시도해보게.");
                return;
            }
            cm.warp(926120100, 0);
            break;
        }
    }
}