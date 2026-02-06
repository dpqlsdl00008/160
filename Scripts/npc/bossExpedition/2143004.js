var status = -1;

function start() {
    switch (cm.getMapId()) {
        case 271040000: {
            status = -1;
            break;
        }
        case 271040100: {
            status = 2;
            break;
        }
        default: {
            status = 5;
            break;
        }
    }
    action(1, 0, 0);
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
            var say = "#e#Cgray#<보스 : 시그너스>#k#n";
            say += "\r\n";
            say += "\r\n타락한 시그너스에게 맞설 준비는 되셨습니까?";
            say += "\r\n#L0##b시그너스 입장을 신청한다.#k";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            if (cm.getParty() == null || cm.isLeader() == false) {
                cm.sendNext("\r\n#e#b1인 이상 파티#k#n를 맺어야만 입장 할 수 있습니다.");
                cm.dispose();
                return;
            }
            var col = true;
            if (cm.getPlayerCount(271040100) > 0) {
                col = false;
            }
            if (cm.getPlayerCount(271040200) > 0) {
                col = false;
            }
            if (cm.getPlayerCount(271040210) > 0) {
                col = false;
            }
            if (!col) {
                cm.sendNext("\r\n다른 파티가 시그너스에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                cm.dispose();
                return;
            }
            var mUser = cm.enterLimitUserByMap(cm.getPlayer(), cm.getMapId());
            if (mUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + mUser + "#k#n 님을 현재 위치해 있는 #e#r" + cm.getMap().getStreetName() + " : " + cm.getMap().getMapName() + "#k#n 에서 찾을 수 없습니다.");
                cm.dispose();
                return;
            }
            var cUser = cm.enterLimitUserByCount(cm.getPlayer(), "cygnus", 3);
            if (cUser != null) {
                cm.sendNext("\r\n파티원 중 #e#b" + cUser + "#k#n 님이 일일 #e#r도전 횟수 제한 횟수를 초과#k#n 했습니다. 시그너스는 #e#b1일 1회#k#n 입장이 가능합니다.");
                cm.dispose();
                return;
            }
            var tUser = cm.enterLimitUserByTime(cm.getPlayer(), "cygnus");
            if (tUser != null) {
                var rUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(tUser);
                cm.sendNext("\r\n파티원 중 #e#b" + tUser + "#k#n 님이 #e#r" + rUser.handleTimeLimit("cygnus") + "#k#n 후에 입장이 가능합니다. 시그너스의 입장 제한 시간은 #e#b5분#k#n 입니다.");
                cm.dispose();
                return;
            }
            // 입장 시간 저장 (밀리초 단위), keyType=0 사용
            cm.getPlayer().setKeyValue(0, "Cygnus_Enter_Time", java.lang.System.currentTimeMillis() + "");
            cm.handleEnter(false, cm.getPlayer(), "cygnus", 0, 271040100);
            cm.dispose();
            break;
        }
        case 2: {
            cm.dispose();
            break;
        }
        case 3: {
            cm.sendYesNo("전투를 마치고 퇴장하시겠습니까?");
            break;
        }
        case 4: {
            var timeStr = cm.getPlayer().getKeyValue(0, "Cygnus_Enter_Time");
            var now = java.lang.System.currentTimeMillis();
            if (timeStr != null) {
                var entered = parseInt(timeStr);
                var elapsed = now - entered;
                if (elapsed >= 30000) { // 1분 이상 경과
                    cm.warp(271040200);
                } else {
                    cm.warp(271040000, "in00");
                }
            } else {
                cm.warp(271040000, "in00");
            }
            cm.dispose();
            break;
        }
        case 5: {
            cm.dispose();
            break;
        }
        case 6: {
            var say = "#e#r시그너스의 정원에 입장 할까?#k#n";
            say += "\r\n#L0##e#b시그너스를 물리치기 위해 이동한다.#k#n";
            cm.sendSimpleS(say, 16);
            break;
        }
        case 7: {
            cm.dispose();
            cm.warp(271040000, "out00");
            break;
        }
    }
}