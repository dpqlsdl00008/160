var status = -1;
var v1 = [100000000, 101000000, 102000000, 103000000, 104000000, 120000000, 310000000];

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
            var say = "다른 대륙의 마을로 이동 하실 수 있습니다. 어느 지역으로 이동 하시겠습니까?#Cgreen#";
            for (i = 1; i < v1.length; i++) {
                say += "\r\n#L" + i + "##m" + v1[i] + "#";
            }
            say += "\r\n#L0#사용하지 않는다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (selection == 0) {
                return;
            }
            cm.gainItem(2430238, -1);
            cm.warp(v1[selection], 0);
            break;
        }
    }
}