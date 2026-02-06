var status = -1;

function start() {
    status = -1;
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
            cm.sendYesNo("정말 이곳에서 나가시겠습니까? 만일 이곳에서 나가시게 된다면, 처음부터 다시 도전해야 합니다. 당신이 파티장이라면 전부 나가지게 됩니다. 정말 나가시고 싶으세요?");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.isLeader() == true) {
                cm.warpParty(910340700, "out00");
            }
            cm.warp(910340700, "out00");
            break;
        }
    }
}