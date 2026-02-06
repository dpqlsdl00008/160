var status = -1;

function start() {
    switch (cm.getMapId()) {
        case 926100203:
        case 926110203: {
            status = 2;
            break;
        }
        default: {
            status = -1;
            break;
        }
    }
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
            var oNpc = cm.getPlayer().getMap().getNPCByOid(cm.getObjectId());
            if (oNpc.getPosition().distanceSq(cm.getPlayer().getPosition()) > 10000) {
                cm.dispose();
                cm.sendNext("\r\n조사 하기에는 너무 멀리 있다.");
                break;
            }
            if (cm.getPlayer().getIntNoRecord(7923) > 0) {
                cm.dispose();
                break;
            }
            var cRand = Packages.server.Randomizer.nextInt(100);
            cm.sendSimple("자세히 살펴보자 수상한 스위치가 보인다.\r\n#d#L0#1. 스위치를 누른다.\r\n#L1#2. 그냥 둔다.");
            break;
        }
        case 1: {
            cm.dispose();
            if (selection == 1) {
                return;
            }
            var pMember = cm.getParty().getMembers();
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser != null) {
                        ccUser.updateQuest(7923, "1");
                    }
                }
            }
            cm.getMap().setReactorState();
            cm.showFieldEffect(true, "quest/party/clear");
            cm.playSound(true, "Party1/Clear");
            cm.mapMessage(6, cm.getPlayer().getName() + "님이 스위치를 누르자 특수한 포탈이 나타났다.");
            break;
        }
        case 2: {
            cm.dispose();
            break;
        }
        case 3: {
            if (cm.getPlayer().getIntNoRecord(7930) == 2) {
                cm.dispose();
                return;
            }
            if (cm.getPlayer().getIntNoRecord(7930) == 1) {
                cm.dispose();
                if (cm.getMonsterCount(cm.getMapId()) == 0) {
                    cm.getMap().setReactorState();
                    cm.showFieldEffect(true, "quest/party/clear");
                    cm.playSound(true, "Party1/Clear");
                    var pMember = cm.getParty().getMembers();
                    if (pMember != null) {
                        var it = pMember.iterator();
                        while (it.hasNext()) {
                            var cUser = it.next();
                            var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                            if (ccUser != null) {
                                ccUser.updateQuest(7930, 2);
                            }
                        }
                    }
                }
                return;
            }
            if (cm.getPlayer().getIntNoRecord(7923) > 4) {
                cm.dispose();
                return;
            }
            cm.sendSimple("자세히 살펴보자 수상한 스위치가 보인다.\r\n#d#L0#1. 스위치를 누른다.\r\n#L1#2. 그냥 둔다.");
            break;
        }
        case 4: {
            cm.dispose();
            if (selection == 1) {
                return;
            }
            var pMember = cm.getParty().getMembers();
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser != null) {
                        ccUser.updateQuest(7923, "5");
                        ccUser.dropMessage(6, "유테레의 중얼거림을 들었다.");
                        return;
                    }
                }
            }
            break;
        }
    }
}