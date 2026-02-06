function start() {
    cm.sendYesNo("#b(빛나는 수정을 통해 원래 세계로 돌아 갈 수 있습니다. 다시 돌아가겠습니까?)#k");
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.warp(211040401, 0);
        cm.dispose();
    }
}