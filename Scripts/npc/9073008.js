var status = -1;
var dayByDay = 30;
var value = 20220000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
var cMap = [
[102040600, 931050410],
[200080600, 931050411],
[220040200, 931050413],
[221040400, 931050414],
[260010201, 931050415],
[250020300, 931050416],
[261020500, 931050417],
[251010500, 931050418],
[240010200, 931050419],
[240010600, 931050420],
[240010500, 931050421],
[240020200, 931050422],
[261020200, 931050424],
[240020401, 931050425],
[240020101, 931050426],
[220080000, 931050427],
[211041400, 931050428],
[230040410, 931050429],
[240040400, 931050430],
[270010500, 931050431],
[270020500, 931050432],
[270030500, 931050433],
[261010002, 931050434],
[261010103, 931050435],
[250010502, 931050436],
];

function start() {
    action(1, 0, 0);
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
            cm.sendYesNo("미스틱 게이트에 손을 대자 몸이 빨려들어가는 것 같은 느낌이 듭니다. 미스틱 게이트 안의 공간으로 이동하시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.isQuestActive(1628) == true) {
                if (cm.getPlayerCount(931050417) != 0) {
                    cm.sendNext("\r\n잠시 후에 다시 시도하여 주시길 바랍니다.");
                    return;
                }
                cm.warp(931050417, "out00");
                return;
            }
            if (cm.getLevel() < 75) {
                cm.sendNext("\r\n미스틱 게이트에 손을 대도 반응이 없습니다.");
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "mystic_gate_enter").equals(dayByDay + "") && cm.getPlayer().getOneInfoQuest(value, "mystic_gate_date").equals(date)) {
                cm.sendNext("\r\n미스틱 게이트의 알 수 없는 힘 때문에 하루에 " + dayByDay + "회를 초과하여 들어 갈 수 없습니다.");
                return;
            }
            for (i = 0; i < cMap.length; i++) {
                if (cm.getMapId() == cMap[i][0]) {
                    if (cm.getPlayerCount(cMap[i][1]) != 0) {
                        cm.sendNext("\r\n잠시 후에 다시 시도하여 주시길 바랍니다.");
                        return;
                    }
                    if (cm.getPlayer().getOneInfoQuest(value, "mystic_gate_date").equals(date) == false) {
                        cm.getPlayer().updateOneInfoQuest(value, "mystic_gate_enter", "1");
                        cm.getPlayer().updateOneInfoQuest(value, "mystic_gate_date", date);
                    } else {
                        var v5 = parseInt(cm.getPlayer().getOneInfoQuest(value, "mystic_gate_enter"));
                        cm.getPlayer().updateOneInfoQuest(value, "mystic_gate_enter", (v5 + 1) + "");
                    }
                    var v6 = parseInt(cm.getPlayer().getOneInfoQuest(value, "mystic_gate_enter"));
                    cm.getPlayer().dropMessage(5, "오늘 미스틱 게이트 안으로 입장한 횟수는 총 " + v6 + "회입니다. 앞으로 " + (dayByDay - v6) + "회 더 입장 할 수 있습니다.");
                    cm.resetMap(cMap[i][1]);
                    cm.warp(cMap[i][1], 0);
                }
            }
            break;
        }
    }
}