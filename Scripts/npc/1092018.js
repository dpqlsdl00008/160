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
            cm.sendNext("\r\n쓰다가 만 편지가 보인다... 중요해 보이는 것 같다. 종이를 주워본다.");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.haveItem(4031839) == true) {
                cm.sendNext("\r\n이미 편지를 주웠다. 또 다시 주울 필요는 없다.");
                return;
            }
            cm.gainItem(4031839, 1);
            cm.sendNext("\r\n뭐라 적혀있는지는 모르겠다...");
            break;
        }
    }
}