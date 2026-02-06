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
            cm.sendYesNo("뭐? 발록의 봉인에 도전하고 싶다고? 너 같은 조무래기가 함부로 도전하다간 아까운 목숨을 잃을 수도 있을텐데... 뭐 내가 상관 할 바가 아니지. 수수료료 #b0 메소#k가 필요한데, 그 정도는 가지고 있겠지?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(105100100, 0);
            break;
        }
    }
}