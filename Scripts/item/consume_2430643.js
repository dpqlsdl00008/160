var status = -1;

var timer = 12500000;
var value = 30000000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

function action(mode, type, selection) {
    cm.dispose();
    if (cm.getPlayer().getOneInfoQuest(timer, "vonleon_timer") == null) {
        cm.getPlayer().updateOneInfoQuest(timer, "vonleon_timer", "0");
    }
    if (cm.getPlayer().getOneInfoQuest(timer, "vonleon_timer").equals("")) {
        cm.getPlayer().updateOneInfoQuest(timer, "vonleon_timer", "0");
    }
    if (!canTimer()) {
        checkTimer();
        return;
    }
    if (!cm.getPlayer().getOneInfoQuest(value, "vonleon_enter").equals("2") && !cm.getPlayer().getOneInfoQuest(value, "vonleon_date").equals(date)) {
        message("반 레온 추가 입장권을 사용 할 수 없습니다. 일일 입장 횟수를 모두 소진 한 후에 사용 할 수 있습니다.");
        return;
    }
    cm.gainItem(2430643, -1);
    setTimer(24);
    cm.getPlayer().updateOneInfoQuest(value, "vonleon_enter", "1");
    message("반 레온 추가 입장권을 사용하였습니다.");
}

function message(say) {
    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CUserLocal.chatMsg(Packages.handling.ChatType.GameDesc, say));
}

function setTimer(hour) {
    cm.getPlayer().updateOneInfoQuest(timer, "vonleon_timer", (cm.getCurrentTime() + hour * 3600000) + "");
}

function canTimer() {
    return (java.lang.Long.parseLong(cm.getPlayer().getOneInfoQuest(timer, "vonleon_timer")) < cm.getCurrentTime());
}

function checkTimer() {
    var text = "";
    var start = cm.getCurrentTime(); 
    var end = java.lang.Long.parseLong(cm.getPlayer().getOneInfoQuest(timer, "vonleon_timer"));
    var elapsedSeconds = (end - start) / 1000;
    var elapsedSecs = parseInt(elapsedSeconds) % 60;
    var elapsedMinutes = parseInt((elapsedSeconds / 60.0));
    var elapsedMins = parseInt(elapsedMinutes % 60);
    var elapsedHrs = parseInt(elapsedMinutes / 60);
    var elapsedHours = parseInt(elapsedHrs % 24);
    if (elapsedHours > 0) {
        var mins = elapsedMins > 0;
        text += elapsedHours + "";
        text += "시간 ";
        if (mins) {
            var secs = elapsedSecs > 0;
            text += elapsedMins + "";
            text += "분 ";
            if (secs) {
                text += elapsedSecs + "";
                text += "초";
            }
        }
    } else if (elapsedMinutes > 0) {
        var secs = elapsedSecs > 0;
        text += elapsedMinutes + "";
        text += "분 ";
        if (secs) {
            text += elapsedSecs + "";
            text += "초";
        }
    } else if (elapsedSeconds > 0) {
        text += elapsedSeconds + "";
        text += "초";
    } else {
        text += "";
    }
    message("이미 반 레온 추가 입장권을 사용하였습니다. (잔여 시간 : " + text + ")");
}