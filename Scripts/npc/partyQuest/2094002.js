var status = -1;

function start() {
    if (cm.getMapId() != 925100100) {
        status = 0;
        action (1, 0, 1);
        return;
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
            var say = "무엇을 하겠나?#d";
            if (cm.isLeader() == true) {
                if (cm.getMonsterCount(cm.getMapId()) == 0) {
                    if (cm.getPlayer().getIntNoRecord(7046) == 0) {
                        say += "\r\n#L0#1. 퀘스트를 진행한다.";
                    }
                } else {
                    cm.dispose();
                    if (cm.haveItem(4001120, 20) == true) {
                        var pMember = cm.getParty().getMembers();
                        if (pMember != null) {
                            var it = pMember.iterator();
                            while (it.hasNext()) {
                                var cUser = it.next();
                                var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                                if (ccUser != null) {
                                    ccUser.removeAll(4001120);
                                }
                            }
                        }
                        cm.getMap().setMobGen(9300114, false);
                        cm.getMap().setMobGen(9300115, true);
                        cm.getMap().setMobGen(9300116, false);
                        cm.getMap().resetFully();
                        cm.topMessage("구옹이 포탈의 첫 번째 봉인을 풀었습니다.");
                        cm.floatMessage("신입 해적의 증표를 모두 구했군. 이제 중급 해적의 증표를 구해야 하네.", 5120020);
                        return;
                    }
                    if (cm.haveItem(4001121, 20) == true) {
                        var pMember = cm.getParty().getMembers();
                        if (pMember != null) {
                            var it = pMember.iterator();
                            while (it.hasNext()) {
                                var cUser = it.next();
                                var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                                if (ccUser != null) {
                                    ccUser.removeAll(4001121);
                                }
                            }
                        }
                        cm.getMap().setMobGen(9300114, false);
                        cm.getMap().setMobGen(9300115, false);
                        cm.getMap().setMobGen(9300116, true);
                        cm.getMap().resetFully();
                        cm.topMessage("구옹이 포탈의 두 번째 봉인을 풀었습니다.");
                        cm.floatMessage("중급 해적의 증표를 모두 구했군. 이제 마지막 증표를 구할 차례네.", 5120020);
                        return;
                    }
                    if (cm.haveItem(4001122, 20) == true) {
                        var pMember = cm.getParty().getMembers();
                        if (pMember != null) {
                            var it = pMember.iterator();
                            while (it.hasNext()) {
                                var cUser = it.next();
                                var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                                if (ccUser != null) {
                                    ccUser.removeAll(4001122);
                                    ccUser.updateQuest(7046, "1");
                                }
                            }
                        }
                        cm.getMap().setMobGen(9300114, false);
                        cm.getMap().setMobGen(9300115, false);
                        cm.getMap().setMobGen(9300116, false);
                        cm.getMap().resetFully();
                        cm.showFieldEffect(true, "quest/party/clear");
                        cm.playSound(true, "Party1/Clear");
                        cm.topMessage("구옹이 포탈의 세 번째 봉인을 풀었습니다.");
                        cm.floatMessage("고참 해적의 증표를 모두 구했군. 이제 마지막 봉인을 풀겠네. 포탈을 타고 이동해 주게.", 5120020);
                        return;
                    }
                }
            }
            say += "\r\n#L1#" + ((cm.getMonsterCount(cm.getMapId()) > 0 || cm.getPlayer().getIntNoRecord(7046) > 0) ? "1" : "2") + ". 해적선에서 내린다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            a1 = selection;
            switch (selection) {
                case 0: {
                    cm.sendNext("\r\n이제 곧 해적들이 몰려 나올 걸세. 그들을 물리치고 #b신입 해적의 증표#k를 20개 이상 구해오게나.");
                    break;
                }
                case 1: {
                    if (cm.getMapId() == 925100500) {
                        if (cm.getMap().getMonsterById(9300119) == null) {
                            cm.dispose();
                            cm.getPlayer().dropMessage(5, "데비존에게서 우양을 구출했습니다.");
                            cm.warp(925100600, 0);
                            return;
                        }
                    }
                    cm.sendYesNo("퀘스트를 중단하고 이곳에서 나가겠나?");
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            switch (a1) {
                case 0: {
                    cm.getMap().setMobGen(9300114, true);
                    cm.getMap().setMobGen(9300115, false);
                    cm.getMap().setMobGen(9300116, false);
                    break;
                }
                case 1: {
                    cm.warp(251010404, 0);
                    break;
                }
            }
            break;
        }
    }
}