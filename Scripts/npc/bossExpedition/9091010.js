var status = -1;

function start() {
    if (cm.getMapId() != 350060300) {
        status = 2;
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
            if (cm.getParty() == null) {
                cm.dispose();
                cm.sendNextS("\r\n#e#b1인 이상 파티#k#n를 맺어야만 입장 할 수 있습니다.", 2);
                return;
            }
            var say = "　 #Cgray#스우를 쓰러뜨리기 위해 블랙 헤븐 코어로 이동할까?";
            say += "\r\n#L0##e#d블랙 헤븐 코어로 이동한다. #r(레벨 255 이상)#k#n";
            //say += "\r\n#L1##Cgray#획득 가능 한 보상을 확인한다.";
            say += "\r\n#L1##Cgray#이동하지 않는다.";
            cm.sendSimpleS(say, 2);
            break;
        }
        case 1: {
            cm.dispose();
            if (selection == 1) {
                cm.dispose();
/*
                var nList = Packages.constants.drop.DropBoss.drop_boss;
                var say = "\r\n　 #Cgray#스우에서 획득 가능 한 보상은 아래와 같습니다.#d\r\n";
                say += "\r\n　 #z1012632#";
                for (i = 0; i < nList.length; i++) {
                    if (nList[i][1] != 8240099) {
                        continue;
                    }
                    say += "\r\n　 #z" + nList[i][0] + "#";
                }
                cm.sendNextS(say, 16);
*/
                return;
            }
            if (cm.isLeader() == false) {
                cm.dispose();
                return;
            }
            if (cm.getPlayerCount(350060500) != 0 || cm.getPlayerCount(350060600) != 0) {
                cm.sendNextS("\r\n현재 접속한 채널에서는 다른 파티가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.", 2);
                return;
            }
            var mUser = cm.enterLimitUserByMap(cm.getPlayer(), cm.getMapId());
            if (mUser != null) {
                cm.sendNextS("\r\n파티원 중 #e#b" + mUser + "#k#n 님을 현재 위치해 있는 #e#r" + cm.getMap().getStreetName() + " : " + cm.getMap().getMapName() + "#k#n 에서 찾을 수 없습니다.", 2);
                return;
            }
            var cUser = cm.enterLimitUserByCount(cm.getPlayer(), "lotus", 1);
            if (cUser != null) {
                cm.sendNextS("\r\n파티원 중 #e#b" + cUser + "#k#n 님이 일일 #e#r도전 횟수 제한 횟수를 초과#k#n 했습니다. 블랙 헤븐 코어는 #e#b1일 1회#k#n 입장이 가능합니다.", 2);
                return;
            }
            var tUser = cm.enterLimitUserByTime(cm.getPlayer(), "lotus");
            if (tUser != null) {
                var rUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(tUser);
                cm.sendNextS("\r\n파티원 중 #e#b" + tUser + "#k#n 님이 #e#r" + rUser.handleTimeLimit("lotus") + "#k#n 후에 입장이 가능합니다. 블랙 헤븐 코어의 입장 제한 시간은 #e#b15분#k#n 입니다.", 2);
                return;
            }
            cm.handleEnter(false, cm.getPlayer(), "lotus", 15, 350060500);
            break;
        }
        case 2: {
            cm.dispose();
            break;
        }
        case 3: {
            cm.sendYesNoS("전투를 마치고 밖으로 나갈까?", 2);
            break;
        }
        case 4: {
            cm.dispose();
            cm.warp(350060300, 1);
            break;
        }
    }
}