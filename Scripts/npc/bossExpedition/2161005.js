var status = -1;

var level = "이지";

function start() {
    if (cm.getMapId() != 211070100) {
        status = 1;
    }
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
            cm.sendYesNo("도전을 마치고 알현실에서 퇴장하시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(211070000, "lionCastleenter");
            break;
        }
        case 2: {
            if (cm.getParty() == null) {
                cm.dispose();
                cm.sendNext("\r\n#e#b1인 이상 원정대#k#n를 맺어야만 입장 할 수 있습니다.");
                return;
            }
            var say = "#e<보스 : 반 레온>#n\r\n\r\n위대한 용사여, 타락한 사자왕에게 맞설 준비를 마치셨습니까?";
            if (cm.isLeader() == false) {
                cm.dispose();
                cm.sendNext("\r\n" + say);
                return;
            }
            say += "\r\n\r\n#L0##b반 레온 원정대 입장을 신청한다.";
            cm.sendSimple(say);
            break;
        }
        case 3: {
            var say = "#e<보스 : 반 레온>#n\r\n\r\n원하시는 모드를 선택하여 주세요.";
            say += "\r\n#L0##b이지 모드 #r(125 레벨 이상)#k";
            say += "\r\n#L1##b노멀 모드 #r(125 레벨 이상)#k";
            cm.sendSimple(say);
            break;
       }
        case 4: {
            cm.dispose();
            var enter = true;
            if (selection == 0) {
                if (cm.getPlayerCount(211070100) != 0) {
                    enter = false;
                }
            } else {
                if (cm.getPlayerCount(211070100) != 0) {
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
            var cUser = cm.enterLimitUserByCount(cm.getPlayer(), "vonleon", 3);
            if (cUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + cUser + "#k#n 님이 일일 #e#r도전 횟수 제한 횟수를 초과#k#n 했습니다. 알현실은 #e#b1일 1회#k#n 입장이 가능합니다.");
                return;
            }
            var tUser = cm.enterLimitUserByTime(cm.getPlayer(), "vonleon");
            if (tUser != null) {
                var rUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(tUser);
                cm.sendNext("\r\n파티원 중 #e#b" + tUser + "#k#n 님이 #e#r" + rUser.handleTimeLimit("vonleon") + "#k#n 후에 입장이 가능합니다. 알현실의 입장 제한 시간은 #e#b5분#k#n 입니다.");
                return;
            }
            cm.handleEnter(false, cm.getPlayer(), "vonleon", 0, selection == 0 ? 211070100 : 211070100);
            break;
        }
    }
}