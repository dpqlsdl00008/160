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
            cm.sendSimple("안녕하세요~ 돌고래 택시입니다! 바닷 길은 모두 이어져 있기에 어디로든지 갈 수 있답니다. 초보자는 요금을 90% 할인해 드립니다.\r\n#b#L1#10,000 메소를 내고 #m230000000#까지 갑니다.");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getMeso() < 10000) {
                cm.sendNext("\r\n메소가 부족하신건 아닌지 확인해 보세요.");
                return;
            }
            cm.gainMeso(-10000);
            cm.warp(230000000, "west00");
            break;
        }
    }
}