var status = -1;

var minLevel = 120;
var minUser = 3;
var maxUser = 6;
var questID = 7814;
var dailyChallenge = 10;
var value = 10200000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

function start() {
    if (cm.getMapId() != 240080000) {
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
            if (!cm.getPlayer().getOneInfoQuest(value, "pq_dragonrider_date").equals(date)) {
                cm.getPlayer().updateQuest(questID, "0");
            }
            var say = "#Cgray##e<파티 퀘스트 : 드래곤 라이더>#n#k\r\n\r\n천공의 지역으로 향하는 입구입니다. 무엇을 원하십니까?#d";
            say += "\r\n#L0#1. 천공의 지역으로 입장한다. (Lv. " + minLevel + "이상 / " + minUser + "명 이상)";
            say += "\r\n#L1#2. 설명을 듣고 싶습니다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            switch (selection) {
                case 0: {
                    if (cm.getParty() == null || cm.isLeader() == false) {
                        cm.sendNext("\r\n도전해 보고 싶다면 #r당신이 속한 파티의 파티장#k을 통해 말을 걸어 주세요.");
                        return;
                    }
                    var v1 = true;
                    var pMember = cm.getParty().getMembers();
                    if (pMember.size() < 3) {
                        v1 = false;
                    }
                    if (cm.allMembersHere() == false) {
                        v1 = false;
                    }
                    if (pMember != null) {
                        var it = pMember.iterator();
                        while (it.hasNext()) {
                            var cUser = it.next();
                            var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                            if (ccUser != null) {
                                if (ccUser.getLevel() < minLevel) {
                                    v1 = false;
                                }
                                if (ccUser.getIntNoRecord(questID) > (dailyChallenge - 1) && ccUser.getOneInfoQuest(value, "pq_dragonrider_date").equals(date)) {
                                    cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 드래곤 라이더는 #b1일 10회#k 도전이 가능합니다.");
                                    return;
                                }
                                if (ccUser.isGM() == true) {
                                    v1 = true;
                                }
                                if (v1 == false) {
                                    cm.sendNext("\r\n당신이 속한 파티의 파티원이 #e#b3명 이상#k#n이 아니거나 자신 혹은 파티원 중에서 #e#b" + minLevel + " 레벨 미만#k#n인 캐릭터가 있습니다. 혹은 #e#r파티원 전원이 현재 맵#k#n에 모여있는지 다시 한 번 확인해 주세요.");
                                    return;
                                }
                                for (i = 100; i < 500; i+=100) {
                                    if (cm.getPlayerCount(240080100 + i) > 0) {
                                        cm.sendNext("\r\n다른 파티가 드래곤 라이더에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                                        return;
                                    }
                                }
                                if (ccUser.getOneInfoQuest(value, "pq_dragonrider_date").equals(date) == false) {
                                    ccUser.updateQuest(7814, "1");
                                    ccUser.updateOneInfoQuest(value, "pq_dragonrider_date", date);
                                } else {
                                    ccUser.updateQuest(7814, (ccUser.getIntNoRecord(7814) + 1) + "");
                                }
                            }
                        }
                    }
                    if (pMember != null) {
                        var it = pMember.iterator();
                        while (it.hasNext()) {
                            var cUser = it.next();
                            var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                            if (ccUser != null) {
                                ccUser.dropMessage(5, "오늘 드래곤 라이더를 진행 한 횟수는 총 " + ccUser.getIntNoRecord(7814) + "회입니다. 앞으로 " + (dailyChallenge - ccUser.getIntNoRecord(7814)) + "회 더 입장 할 수 있습니다.");
                            }
                        }
                    }
                    cm.resetMap(240080100);
                    cm.resetMap(240080200);
                    cm.resetMap(240080300);
                    cm.resetMap(240080400);
                    cm.resetMap(240080500);
                    cm.spawnMobOnMap(8300006, 1, 533, 25, 240080300);
                    cm.spawnMobOnMap(8300007, 1, 324, -10, 240080500);
                    cm.warpParty(240080100);
                    break;
                }
                case 1: {
                    var say = "\r\n#b천공의 문#k으로 가서 #r드래곤 라이더#k의 정체를 알아보자. #b플라잉#k 스킬로 하늘을 날아 와이번을 물리치며 쫓아가면 그를 발견할 수 있을 것이다.\r\n";
                    say += "\r\n#e- 레벨#n : 120 레벨 이상 #r(추천 레벨 : 120 ~ 255)#k";
                    say += "\r\n#e- 제한 시간#n : 30분";
                    say += "\r\n#e- 참가 인원#n : 3 ~ 6명";
                    say += "\r\n#e- 참가 조건#n : 플라잉 스킬 습득";
                    cm.sendNext(say);
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            break;
        }
        case 3: {
            cm.sendYesNo("도전을 마치고 #b천공의 나루터#k로 귀환하시겠습니까?");
            break;
        }
        case 4: {
            cm.dispose();
            if (cm.getMapId() == 240080500) {
                var itemID = 2022652;
                switch (cm.getJob()) {
                    case 112:
                    case 122:
                    case 132:
                    case 2112:
                    case 3112:
                    case 5112:
                    case 6112: {
                        itemID = 2022652;
                        break;
                    }
                    case 212:
                    case 222:
                    case 232:
                    case 2216:
                    case 2217:
                    case 2218:
                    case 3212:
                    case 6212: {
                        itemID = 2022653;
                        break;
                    }
                    case 312:
                    case 322:
                    case 2312:
                    case 3312: {
                        itemID = 2022655;
                        break;
                    }
                    case 422:
                    case 412:
                    case 434:
                    case 2412: {
                        itemID = 2022654;
                        break;
                    }
                    case 512:
                    case 522:
                    case 532:
                    case 3512: {
                        itemID = 2022656;
                        break;
                    }
                }
                var v1 = 1;
                if (cm.getParty() != null) {
                    v1 = cm.getParty().getMembers().size();
                }
                Packages.handling.channel.handler.DueyHandler.addNewItemToDb(itemID, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
                Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310050, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
                cm.getClient().sendPacket(Packages.tools.packet.CField.receiveParcel("드래곤 라이더", true));
                cm.gainExp(2999999);
            }
            cm.warp(240080000, "enter00");
            break;
        }
    }
}