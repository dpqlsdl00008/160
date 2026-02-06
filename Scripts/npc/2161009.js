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
            cm.dispose();
            cm.sendNext("\r\n레온! 레온! 내 목소리가 들리지 않는 건가요? #h #님, 절 대신해서 레온에게 말을 걸어 주세요! 아아, 이렇게 눈 앞에 있는데...");
            break;
        }
    }
}