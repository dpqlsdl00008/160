var status = -1;

function start() {
    action (1, 0, 0);
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
            var say = "즐거움이 가득한 환상의 세계! 판타스틱 테마 파크로 이동 하시겠습니까?#b";
            say += "\r\n#L0#네, 지금 바로 판타스틱 테마 파크로 가겠습니다.";
            say += "\r\n#L1#다음 기회에 가겠습니다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (selection == 1) {
                return;
            }
            cm.warp(223000000, "Ludi00");
            break;
        }
    }
}