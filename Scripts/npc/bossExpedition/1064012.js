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
            var say = "#e#Cgray#<루타비스 서쪽 정원 입구>#k#n";
            say += "\r\n";
            say += "\r\n루타비스 서쪽 봉인의 수호자인 #r피에르#k가 지키고 있는 정원으로 가는 문이다.";
            //say += "\r\n#L0##i4033611# #b#z4033611#를 사용하여 이동한다.#k";
            say += "\r\n#L0##e#d보스 : 피에르 입장을 신청한다.#k#n";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getParty() == null || cm.isLeader() == false) {
                cm.sendNext("\r\n#e#b1인 이상 파티#k#n를 맺어야만 입장 할 수 있습니다.");
                return;
            }
            var col = true;
            if (cm.getPlayerCount(105200200) > 0) {
                col = false;
            }
            if (cm.getPlayerCount(105200210) > 0) {
                col = false;
            }
            if (!col) {
                cm.sendNext("\r\n다른 파티가 피에르에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                return;
            }
            var mUser = cm.enterLimitUserByMap(cm.getPlayer(), cm.getMapId());
            if (mUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + mUser + "#k#n 님을 현재 위치해 있는 #e#r" + cm.getMap().getStreetName() + " : " + cm.getMap().getMapName() + "#k#n 에서 찾을 수 없습니다.");
                return;
            }
/*
            var iUser = cm.enterLimitUserByItem(cm.getPlayer(), 4033611, 1);
            if (iUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + iUser + "#k#n 님이 #i4033611# #e#r고목 나무 열쇠#k#n 를 소지 중에 있지 않습니다.");
                return;
            }
*/
            var cUser = cm.enterLimitUserByCount(cm.getPlayer(), "pierre", 10);
            if (cUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + cUser + "#k#n 님이 일일 #e#r도전 횟수 제한 횟수를 초과#k#n 했습니다. 피에르는 #e#b1일 1회#k#n 입장이 가능합니다.");
                return;
            }
            var tUser = cm.enterLimitUserByTime(cm.getPlayer(), "pierre");
            if (tUser != null) {
                var rUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(tUser);
                cm.sendNext("\r\n파티원 중 #e#b" + tUser + "#k#n 님이 #e#r" + rUser.handleTimeLimit("pierre") + "#k#n 후에 입장이 가능합니다. 피에르의 입장 제한 시간은 #e#b5분#k#n 입니다.");
                return;
            }
            //cm.gainPartyItem(cm.getPlayer(), 4033611, -1);
            cm.handleEnter(false, cm.getPlayer(), "pierre", 15, 105200200);
            break;
        }
    }
}