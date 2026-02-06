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
            var say = "내 오랜 계획을 물거품으로 만든 녀석들이 이렇게 제 발로 찾아와주니 정말 기쁘기 그지 없군.\r\n\r\n#r그 댓가로 세상에서 제일 고통스러운 죽음을 선사해주마.";
            if (cm.isLeader() == false) {
                cm.dispose();
                cm.sendNext("\r\n" + say);
                return;
            } else {
                cm.sendAcceptDecline(say);
            }
            break;
        }
        case 1: {
            cm.dispose();
            cm.removeNpc(2144010);
            cm.spawnMonster(8860000, 288, -181);
            break;
        }
    }
}