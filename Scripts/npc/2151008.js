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
            var say = "나의 형제들이 있는 곳으로 가고 싶은건가?";
            for (var i = 0; i < 10; i++) {
                say += "\r\n#b#L" + i + "#재규어 서식지로 이동#k #r(" +  cm.getPlayerCount(931000500 + i) + "/1)#k";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getPlayerCount(931000500 + selection) != 0) {
                return;
            }
            cm.warp(931000500 + selection, 0);
            break;
        }
    }
}