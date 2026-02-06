var status = -1;

var eDate = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

var expedition = [
[30, "마왕 발록", 20230000, "balog", 3, 105100100],
//[1306, "자쿰", 20230001, "zakum", 2, 211042300],
//[1309, "혼테일", 20230002, "horntail", 2, 240050400],
//[1310, "핑크빈", 20230003, "pinkbean", 1, 270050000],
//[1305, "반 레온", 20230004, "vonleon", 1, 211070000],
//[1316, "힐라", 20230006, "hillah", 1, 262000000],
//[1315, "아카이럼", 20230005, "akayrum", 1, 272020110],
];

function start() {
    status = -1;
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
            say = "";
            for (i = 0; i < expedition.length; i++) {
                if (!cm.getPlayer().getOneInfoQuest(expedition[i][2], expedition[i][3] + "_date").equals(eDate)) {
                    cm.getPlayer().updateOneInfoQuest(expedition[i][2], expedition[i][3] + "_enter", "0");
                }
                var eCount = (expedition[i][4] - parseInt(cm.getPlayer().getOneInfoQuest(expedition[i][2], expedition[i][3] + "_enter")));
                say += "#" + expedition[i][0] + "# " + expedition[i][1] + "";
            }
            cm.askMapSelection(say);
            break;
        }
        case 1: {
            v5 = selection;
            cm.sendAcceptDeclineS("#Cgreen#" + expedition[selection][5] + "#k(으)로 이동하겠습니다.", 2);
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(eMap[v5][0], 0);
            break;
        }
    }
}