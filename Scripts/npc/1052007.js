var status = -1;
var quest = [[4031036, 910360000, "B1"], [4031037, 910360100, "B2"], [4031038, 910360200, "B3"]];

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
            var say = "가시고자 하는 행선지를 선택해 주세요.";
            say += "\r\n#L0##e#r정비 중인 지하철#n#b";
            say += "\r\n#L1#커닝시티 지하철 (주의 : 스티지, 레이스 등 서식)";
            say += "\r\n#L2#커닝 스퀘어 백화점 (지하철 탑승)";
            say += "\r\n\r\n#L3#공사장 진입";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    if (cm.getPlayerCount(931050400) != 0 || cm.getPlayerCount(931050401) != 0 || cm.getPlayerCount(931050402) != 0) {
                        cm.dispose();
                        cm.getPlayer().dropMessage(5, "정비 중인 지하철에 누군가 있는 것 같습니다. 잠시 후에 다시 입장해주세요.");
                        return;
                    }
                    cm.resetMap(931050400);
                    cm.resetMap(931050401);
                    cm.resetMap(931050402);
                    cm.warp(931050400, 0);
                    break;
                }
                case 1: {
                    cm.dispose();
                    cm.warp(103020100, "out00");
                    break;
                }
                case 2: {
                    cm.dispose();
                    cm.warp(103020010, 0);
                    break;
                }
                case 3: {
                    var say = "표를 넣는 투입구가 있습니다. 입장권을 사용하면 곧바로 안으로 이동됩니다. 어느 표를 사용하시겠습니까?#b";
                    for (i = 0; i < quest.length; i++) {
                        say += "\r\n#L" + i + "##b공사장 " + quest[i][2] + "#k";
                    }
                    cm.sendSimple(say);
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.haveItem(quest[selection][0]) == false) {
                cm.dispose();
                cm.sendNext("\r\n표를 넣는 투입구가 있습니다. 티켓이 없으면 안으로 들어갈 수 없습니다.");
                return;
            }
            cm.gainItem(quest[selection][0], -1);
            cm.warp(quest[selection][1], 0);
            break;
        }
    }
}