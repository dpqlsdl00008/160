var status = -1;

function start() {
    action(1, 0, 0);
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
            cm.sendYesNo("이곳에서 나가 매표소로 돌아가고 싶으세요? 입장권은 반환되지 않습니다.");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(103020000, "in00");
            break;
        }
    }
}