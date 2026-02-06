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
            var say = "빅토리아 아일랜드로 돌아가겠나? 원하는 지역으로 보내줄테니, 편하게 말해 봐.#d";
            say += "\r\n#L0#1. 빅토리아 나무 승강장";
            say += "\r\n#L1#2. 골드 비치에 계속 남는다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (selection == 1) {
                return;
            }
            cm.warp(104020100, 0);
            break;
        }
    }
}