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
            var say = "에오스 탑의 여행자를 위한 마법석입니다. 요금을 내면 원하는 층으로 이동 시켜 드립니다.\r\n\r\n#r(에오스 돌 활성화 주문서를 가지고 있을 경우 메소 대신 에오스 돌 활성화 주문서를 사용 할 수 있습니다.)#b";
            say += "\r\n#L221020000#에오스 탑 <1층> (15,000 메소)";
            say += "\r\n#L221020900#에오스 탑 <10층> (15,000 메소)";
            say += "\r\n#L221021100#에오스 탑 <31층> (15,000 메소)";
            say += "\r\n#L221021500#에오스 탑 <35층> (15,000 메소)";
            say += "\r\n#L221021700#에오스 탑 <66층> (15,000 메소)";
            say += "\r\n#L221022100#에오스 탑 <70층> (15,000 메소)";
            say += "\r\n#L221022300#에오스 탑 <91층> (15,000 메소)";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.haveItem(4001020) == false) {
                if (cm.getMeso() < 15000) {
                    return;
                }
                cm.gainMeso(-15000);
            } else {
                cm.gainItem(4001020, -1);
            }
            cm.warp(selection, "top00");
            break;
        }
    }
}