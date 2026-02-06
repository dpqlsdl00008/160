var status = -1;

var nLevel = "노멀";

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
    if (cm.getMapId() == 211042401) {
        nLevel = "카오스";
    }
    switch (status) {
        case 0: {
            if (cm.getParty() == null) {
                cm.dispose();
                cm.sendNext("\r\n#e#b1인 이상 파티#k#n를 맺어야만 입장 할 수 있습니다.");
                return;
            }
            var say = "#e<자쿰 : " + nLevel + " 모드>#n\r\n\r\n자쿰이 부활했다네. 이대로 둔다면 화산 폭발을 일으켜서 엘나스 산맥 전체를 지옥으로 만들어 버릴거야.\r\n\r\n#r(자쿰의 제단에는 #e하루에 2회 입장#n 할 수 있으며 입장 기록은 #e매일 자정에 초기화#n됩니다.)";
            if (cm.isLeader() == false) {
                cm.dispose();
                cm.sendNext("\r\n" + say);
                return;
            }
            say += "\r\n#L0##b" + (cm.getMapId() == 211042401 ? "카오스 " : "") + "자쿰 입장을 신청한다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getPlayerCount(cm.getMapId() == 211042401 ? 280030001 : 280030000) != 0) {
                cm.sendNext("\r\n현재 접속한 채널에서는 다른 파티가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            var mUser = cm.enterLimitUserByMap(cm.getPlayer(), cm.getMapId());
            if (mUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + mUser + "#k#n 님을 현재 위치해 있는 #e#r" + cm.getMap().getStreetName() + " : " + cm.getMap().getMapName() + "#k#n 에서 찾을 수 없습니다.");
                return;
            }
            var iUser = cm.enterLimitUserByLevel(cm.getPlayer(), 50);
            if (iUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + iUser + "#k#n 님이 #e#r50 레벨 미만#k#n 입니다. 자쿰의 제단은 #e#b50 레벨 이상#k#n 입장이 가능합니다.");
                return;
            }
            var cUser = cm.enterLimitUserByCount(cm.getPlayer(), "zakum", 3);
            if (cUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + cUser + "#k#n 님이 일일 #e#r도전 횟수 제한 횟수를 초과#k#n 했습니다. 자쿰의 제단은 #e#b1일 2회#k#n 입장이 가능합니다.");
                return;
            }
            var tUser = cm.enterLimitUserByTime(cm.getPlayer(), "zakum");
            if (tUser != null) {
                var rUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(tUser);
                cm.sendNext("\r\n파티원 중 #e#b" + tUser + "#k#n 님이 #e#r" + rUser.handleTimeLimit("zakum") + "#k#n 후에 입장이 가능합니다. 자쿰의 제단의 입장 제한 시간은 #e#b5분#k#n 입니다.");
                return;
            }
            cm.handleEnter(false, cm.getPlayer(), "zakum", 0, cm.getMapId() == 211042401 ? 280030001 : 280030000);
            break;
        }
    }
}