var status = -1;

function start() {
    if (cm.getMapId() == 262030000) {
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
            cm.sendYesNo("원정을 마치고 퇴장하시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(262030000, "in00");
            break;
        }
        case 2: {
            if (cm.getParty() == null) {
                cm.dispose();
                cm.sendNext("\r\n#e#b1인 이상 파티#k#n를 맺어야만 입장 할 수 있습니다.");
                return;
            }
            var say = "#e<보스 : 힐라>#n\r\n\r\n힐라를 처치하고, 아스완의 진정한 해방을 이뤄 낼 준비는 되셨습니까? 다른 지역에 있는 파티원이 있다면, 모두 모여주세요.";
            if (cm.isLeader() == false) {
                cm.dispose();
                cm.sendNext("\r\n" + say);
                return;
            }
            say += "\r\n#L0##b<보스 : 힐라> 입장을 신청한다.";
            cm.sendSimple(say);
            break;
        }
        case 3: {
            var say = "#e<보스 : 힐라>#n\r\n\r\n원하시는 모드를 선택하여 주세요.";
            say += "\r\n#L0##b노멀 모드 #r(120 레벨 이상)#k";
            say += "\r\n#L1##b하드 모드 #r(170 레벨 이상)#k";
            cm.sendSimple(say);
            break;
       }
        case 4: {
            cm.dispose();
            var enter = true;
            if (selection == 0) {
                if (cm.getPlayerCount(262030100) != 0 || cm.getPlayerCount(262030200) != 0 || cm.getPlayerCount(262030300) != 0) {
                    enter = false;
                }
            } else {
                if (cm.getPlayerCount(262030100) != 0 || cm.getPlayerCount(262030200) != 0 || cm.getPlayerCount(262030300) != 0) {
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
            var cUser = cm.enterLimitUserByCount(cm.getPlayer(), "hillah", 3);
            if (cUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + cUser + "#k#n 님이 일일 #e#r도전 횟수 제한 횟수를 초과#k#n 했습니다. 힐라의 탑은 #e#b1일 1회#k#n 입장이 가능합니다.");
                return;
            }
            var tUser = cm.enterLimitUserByTime(cm.getPlayer(), "hillah");
            if (tUser != null) {
                var rUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(tUser);
                cm.sendNext("\r\n파티원 중 #e#b" + tUser + "#k#n 님이 #e#r" + rUser.handleTimeLimit("hillah") + "#k#n 후에 입장이 가능합니다. 힐라의 탑의 입장 제한 시간은 #e#b5분#k#n 입니다.");
                return;
            }
            cm.handleEnter(false, cm.getPlayer(), "hillah", 0, selection == 0 ? 262030100 : 262030100);
            break;
        }
    }
}