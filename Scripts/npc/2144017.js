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
            var say = "#e<시간의 균열>#n\r\n\r\n과거와 미래, 그리고 그 사이의 어딘가... 가고자 하는 곳은 어디인가?\r\n#b";
            say += "\r\n#L0#과거의 리프레";
            say += "\r\n#L1#차원의 틈";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(selection == 0 ? 272000100 : 272030000, selection == 0 ? "west00" : "out00");
            break;
        }
    }
}