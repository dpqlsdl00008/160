var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendNext("\r\n흠. 마음이 바뀌면 다시 이야기 해.");
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            nPrice = 0;
            q2050 = cm.getQuestStatus(2050);
            q2051 = cm.getQuestStatus(2051);
            switch (q2051) {
                case 0: {
                    switch (q2050) {
                        case 0: {
                            cm.dispose();
                            cm.sendNext("\r\n이 안에는 진귀한 약초가 있는 모양이야. 하지만 아무나 들여보내줄 순 없지.");
                            break;
                        }
                        case 1: {
                            nPrice = (cm.getLevel() * 100);
                            cm.sendYesNo("사비트라마의 부탁을 받고 온거야? 그냥 들여 보내 줄 순 없고, 약간의 입장료만 내면 들여 보내 줄게. 입장료는 #b" + cm.getPlayer().getNum(nPrice) + " 메소#k야.");
                            break;
                        }
                        case 2: {
                            cm.sendNext("\r\n다시 왔구나? 요금은 받지 않을게.");
                            break;
                        }
                    }
                    break;
                }
                case 1: {
                    nPrice = (cm.getLevel() * 200);
                    cm.sendYesNo("사비트라마의 부탁을 받고 온거야? 그냥 들여 보내 줄 순 없고, 약간의 입장료만 내면 들여 보내 줄게. 입장료는 #b" + cm.getPlayer().getNum(nPrice) + " 메소#k야.");
                    break;
                }
                case 2: {
                    cm.sendNext("\r\n다시 왔구나? 요금은 받지 않을게.");
                    break;
                }
            }
            break;
        }
        case 1: {
            cm.dispose();
            if (nPrice > 0) {
                if (cm.getMeso() < nPrice) {
                    return;
                }
                cm.gainMeso(-nPrice);
            }
            switch (q2051) {
                case 0: {
                    cm.warp(910130000, 0);
                    break;
                }
                default: {
                    cm.warp(910130100, 0);
                    break;
                }
            }
            break;
        }
    }
}