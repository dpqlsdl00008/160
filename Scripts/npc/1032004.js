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
            cm.sendYesNo("정말 이곳에서 나가고 #b엘리니아#k로 돌아가고 싶어?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(101000000, 0);
            break;
        }
    }
}	