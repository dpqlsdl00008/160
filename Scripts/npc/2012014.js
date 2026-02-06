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
            var say = "오르비스 탑의 여행자를 위한 마법석입니다. 요금을 내면 원하는 층으로 이동 시켜 드립니다.\r\n\r\n#r(마법석 활성화 주문서를 가지고 있을 경우 메소 대신 마법석 활성화 주문서를 사용 할 수 있습니다.)#b";
            say += "\r\n#L200080600#오르비스 탑 <16층> (5,000 메소)";
            say += "\r\n#L200081400#오르비스 탑 <8층> (5,000 메소)";
            say += "\r\n#L200082100#오르비스 탑 <1층> (5,000 메소)";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.haveItem(4001019) == false) {
                if (cm.getMeso() < 5000) {
                    return;
                }
                cm.gainMeso(-5000);
            } else {
                cm.gainItem(4001019, -1);
            }
            cm.warp(selection, 0);
            break;
        }
    }
}