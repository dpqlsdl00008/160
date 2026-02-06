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
            var say = "　 #e#Cgray#<보스 : 매그너스>#k#n";
            say += "\r\n";
            say += "\r\n　 #Cgray#매그너스 퇴치를 위해 폭군의 왕좌로 이동 하시겠습니까\r\n　 ?";
            say += "\r\n#L0##e#d폭군의 왕좌로 이동한다.#k#n";
            say += "\r\n#L1##Cgray#이동하지 않는다.#k";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (selection == 1) {
                return;
            }
            if (cm.getParty() == null || cm.isLeader() == false) {
                cm.sendNext("\r\n#e#b1인 이상 파티#k#n를 맺어야만 입장 할 수 있습니다.");
                return;
            }
            var col = true;
            if (cm.getPlayerCount(401060100) > 0) {
                col = false;
            }
            if (!col) {
                cm.sendNext("\r\n다른 파티가 매그너스에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                return;
            }
            var mUser = cm.enterLimitUserByMap(cm.getPlayer(), cm.getMapId());
            if (mUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + mUser + "#k#n 님을 현재 위치해 있는 #e#r" + cm.getMap().getStreetName() + " : " + cm.getMap().getMapName() + "#k#n 에서 찾을 수 없습니다.");
                return;
            }
            var cUser = cm.enterLimitUserByCount(cm.getPlayer(), "magnus", 3);
            if (cUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + cUser + "#k#n 님이 일일 #e#r도전 횟수 제한 횟수를 초과#k#n 했습니다. 매그너스는 #e#b1일 1회#k#n 입장이 가능합니다.");
                return;
            }
            var tUser = cm.enterLimitUserByTime(cm.getPlayer(), "magnus");
            if (tUser != null) {
                var rUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(tUser);
                cm.sendNext("\r\n파티원 중 #e#b" + tUser + "#k#n 님이 #e#r" + rUser.handleTimeLimit("magnus") + "#k#n 후에 입장이 가능합니다. 매그너스의 입장 제한 시간은 #e#b5분#k#n 입니다.");
                return;
            }
            cm.handleEnter(false, cm.getPlayer(), "magnus", 0, 401060100);
            break;
        }
    }
}