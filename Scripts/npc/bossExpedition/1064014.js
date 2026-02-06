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
            var say = "#e#Cgray#<루타비스 남쪽 정원 입구>#k#n";
            say += "\r\n";
            say += "\r\n루타비스 남쪽 봉인의 수호자인 #r블러디 퀸#k이 지키고 있는 정원으로 가는 문이다.";
            say += "\r\n#L0##e#d보스 : 블러디 퀸 입장을 신청한다.#n";
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
            if (cm.getPlayerCount(105200300) > 0) {
                col = false;
            }
            if (cm.getPlayerCount(105200310) > 0) {
                col = false;
            }
            if (!col) {
                cm.sendNext("\r\n다른 파티가 블러디 퀸에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
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
            var cUser = cm.enterLimitUserByCount(cm.getPlayer(), "blodyqueen", 10);
            if (cUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + cUser + "#k#n 님이 일일 #e#r도전 횟수 제한 횟수를 초과#k#n 했습니다. 블러디 퀸은 #e#b1일 1회#k#n 입장이 가능합니다.");
                return;
            }
            var tUser = cm.enterLimitUserByTime(cm.getPlayer(), "blodyqueen");
            if (tUser != null) {
                var rUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(tUser);
                cm.sendNext("\r\n파티원 중 #e#b" + tUser + "#k#n 님이 #e#r" + rUser.handleTimeLimit("blodyqueen") + "#k#n 후에 입장이 가능합니다. 블러디 퀸의 입장 제한 시간은 #e#b5분#k#n 입니다.");
                return;
            }
            //cm.gainPartyItem(cm.getPlayer(), 4033611, -1);
            cm.handleEnter(false, cm.getPlayer(), "blodyqueen", 15, 105200300);
            break;
        }
    }
}