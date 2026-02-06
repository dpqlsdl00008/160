var status = -1;

function start() {
    status = -1;
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
            cm.dispose();
            cm.floatMessage("이곳을 찾아 온 사람을 보는 것은 정말 오랜만이예요. 하지만 무사히 돌아간 분도 없었답니다.", 5120043);
            break;
        }
    }
}