var status = -1;

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
            if (cm.getParty() == null) {
                cm.dispose();
                cm.sendNext("\r\n#e#b1인 이상 파티#k#n를 맺어야만 입장 할 수 있습니다.");
                return;
            }
            var say = "#e<보스 : 혼테일>#n\r\n\r\n혼테일이 부활했다. 이대로 둔다면 화산 폭발을 일으켜서 미나르 일대를 지옥으로 만들어 버릴거야.";
            if (cm.isLeader() == false) {
                cm.dispose();
                cm.sendNext("\r\n" + say);
                return;
            }
            say += "\r\n#L0##b<보스 : 혼테일> 입장을 신청한다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            var say = "#e<보스 : 혼테일>#n\r\n\r\n원하는 모드를 선택하라.";
            say += "\r\n#L0##b노멀 모드 #r(130 레벨 이상)#k";
            say += "\r\n#L1##b카오스 모드 #r(135 레벨 이상)#k";
            cm.sendSimple(say);
            break;
       }
        case 2: {
            cm.dispose();
            var enter = true;
            if (selection == 0) {
                if (cm.getPlayerCount(240060000) != 0 || cm.getPlayerCount(240060100) != 0 || cm.getPlayerCount(240060200) != 0) {
                    enter = false;
                }
            } else {
                if (cm.getPlayerCount(240060001) != 0 || cm.getPlayerCount(240060101) != 0 || cm.getPlayerCount(240060201) != 0) {
                    enter = false;
                }
            }
            if (enter == false) {
                cm.sendNext("\r\n현재 접속한 채널에서는 다른 파티가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            var mUser = cm.enterLimitUserByMap(cm.getPlayer(), cm.getMapId());
            if (mUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + mUser + "#k#n 님을 현재 위치해 있는 #e#r" + cm.getMap().getStreetName() + " : " + cm.getMap().getMapName() + "#k#n 에서 찾을 수 없습니다.");
                return;
            }
            var cUser = cm.enterLimitUserByCount(cm.getPlayer(), "horntail", 3);
            if (cUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + cUser + "#k#n 님이 일일 #e#r도전 횟수 제한 횟수를 초과#k#n 했습니다. 혼테일의 동굴은 #e#b1일 2회#k#n 입장이 가능합니다.");
                return;
            }
            var tUser = cm.enterLimitUserByTime(cm.getPlayer(), "horntail");
            if (tUser != null) {
                var rUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(tUser);
                cm.sendNext("\r\n파티원 중 #e#b" + tUser + "#k#n 님이 #e#r" + rUser.handleTimeLimit("horntail") + "#k#n 후에 입장이 가능합니다. 혼테일의 동굴의 입장 제한 시간은 #e#b5분#k#n 입니다.");
                return;
            }
            cm.handleEnter(false, cm.getPlayer(), "horntail", 0, selection == 0 ? 240060000 : 240060001);
            break;
        }
    }
}