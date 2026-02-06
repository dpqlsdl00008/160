function start() {
    cm.askAcceptDecline("시험의 증표를 다 찾았어? 여기서 나가길 원해?");
}

function action(mode, type, selection) {
    cm.dispose();
    if (mode == 1) {
        cm.warp(130020000, 0);
    }
}