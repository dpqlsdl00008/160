var status = -1;

var minLevel = 120;
var minUser = 2;
var maxUser = 6;
var questID = 7926;
var dailyChallenge = 10;
var value = 10200000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

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
        if (status == 2) {
            status = 0;
            action(1, 0, a1);
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (!cm.getPlayer().getOneInfoQuest(value, "pq_kenta_date").equals(date)) {
                cm.getPlayer().updateQuest(questID, "0");
            }
            var say = "#Cgray##e<파티 퀘스트 : 위험에 빠진 켄타>#n#k\r\n\r\n큰일났어요!! 켄타가 위험에 빠진 것 같아요. 바다 생물들의 이상 행동에 대해 직접 조사하겠다며 나간 뒤 돌아오지 않고 있는데 무슨 일에 생긴 것이 틀림없어요. 켄타를 찾아야 해요. 도와주세요!#d";
            say += "\r\n#L0#켄타를 찾으러 가겠습니다.";
            //say += "\r\n#L1#켄타의 새 물안경을 갖고 싶습니다.";
            say += "\r\n#L2#어떻게 된 일인지 자세히 이야기 해주세요.";
            say += "\r\n#L3#오늘 남은 도전 횟수를 알고 싶어요.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            a1 = selection;
            switch (selection) {
                case 0: {
                    cm.sendNext("\r\n고맙습니다! 저와 함께 위험한 바다 갈림길로 가요.");
                    break;
                }
                case 1: {
                    cm.sendNext("\r\n#i1022175# #b#z1022175##k에 관심이 있으시군요... 켄탄의 새 물안경은 켄타가 자신의 바다 생물 조사를 도운 분들을 위해 준비한 답례입니다. 연구에 필요한 #b피아누스의 비늘 10개#k 정도 가져오시면 드릴께요. 피아누스의 비늘은 켄타를 찾은 뒤 함께 피아누스를 처치하면 얻을 수 있어요. 10개가 부담스러우시면 #b피아누스의 비늘 5개#k로 펫 장비 주문서도 받을 수 있으니, 켄타를 도와주세요.");
                    break;
                }
                case 2: {
                    cm.sendNext("켄타를 찾아 주세요! 조심하셔야 해요. 저 곳은 위험하니까. 제가 몇가지 부탁들 좀 드려도 될까요?#d\r\n\r\n1. 켄타를 찾으러 가시는 길에 위협적인 바다 생물들이 있다면 그들을 물리쳐주세요.\r\n2. 켄타가 나간 지 오래 되서 준비해 간 산소가 부족할 지도 몰라요. 공기 방울을 모아주세요.\r\n3. 켄타를 발견 하시거든 난폭한 바다 생물들로부터 지켜주세요.\r\n4. 그리고 켄타가 혹시라도 조사를 계속 하겠다면, 켄타를 도와서 조사를 마치고 무사히 집으로 돌아올 수 있도록 도와주세요.");
                    break;
                }
                case 3: {
                    cm.dispose();
                    cm.sendNext("\r\n오늘 남은 도전 횟수는 " + (dailyChallenge - cm.getPlayer().getIntNoRecord(questID)) + "회입니다.");
                    break;
                }
            }
            break;
        }
        case 2: {
            a2 = a1;
            switch (a1) {
                case 0: {
                    cm.dispose();
                    if (cm.getParty() == null || cm.isLeader() == false) {
                        cm.sendNext("\r\n아쿠아로드의 동물들이 난폭해 졌어요. 위험해서 혼자서는 들어 갈 수 없어요. 이 안에 들어가고 싶다면, 당신이 속한 파티의 파티장이 저에게 말을 걸어 주셔야 해요.");
                        return;
                    }
                    var v1 = true;
                    var pMember = cm.getParty().getMembers();
                    if (pMember.size() < minUser) {
                        cm.sendNext("\r\n여행자님이 속한 파티의 파티원이 " + minUser + "명 이상이 아니라서 입장하실 수 없어요. 레벨 " + minLevel + " 이상, " + minUser + "명 이상의 파티원이 있어야 입장하실 수 있으니 확인하시고 다시 저에게 말을 걸어주세요.");
                        return;
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
                                if (ccUser.getIntNoRecord(questID) > (dailyChallenge - 1) && ccUser.getOneInfoQuest(value, "pq_kenta_date").equals(date)) {
                                    cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 위험에 빠진 켄타는 #b1일 10회#k 도전이 가능합니다.");
                                    return;
                                }
                                if (ccUser.isGM() == true) {
                                    v1 = true;
                                }
                                if (v1 == false) {
                                    cm.sendNext("\r\n당신이 속한 파티의 파티원이 #e#b2명 이상#k#n이 아니거나 자신 혹은 파티원 중에서 #e#b" + minLevel + " 레벨 미만#k#n인 캐릭터가 있습니다. 혹은 #e#r파티원 전원이 현재 맵#k#n에 모여있는지 다시 한 번 확인해 주세요.");
                                    return;
                                }
                                for (i = 0; i < 400; i+=100) {
                                    if (cm.getPlayerCount(923040100 + i) > 0) {
                                        cm.sendNext("\r\n다른 파티가 위험에 빠진 켄타에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                                        return;
                                    }
                                }
                                if (ccUser.getOneInfoQuest(value, "pq_kenta_date").equals(date) == false) {
                                    ccUser.updateQuest(questID, "1");
                                    ccUser.updateOneInfoQuest(value, "pq_kenta_date", date);
                                } else {
                                    ccUser.updateQuest(questID, (ccUser.getIntNoRecord(questID) + 1) + "");
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
                                ccUser.removeAll(2430364);
                                ccUser.updateQuest(7927, "0");
                                ccUser.dropMessage(5, "오늘 위험에 빠진 켄타를 진행 한 횟수는 총 " + ccUser.getIntNoRecord(questID) + "회입니다. 앞으로 " + (dailyChallenge - ccUser.getIntNoRecord(questID)) + "회 더 입장 할 수 있습니다.");
                            }
                        }
                    }
                    cm.getMap(923040200).setMobGen(9300446, true);
                    cm.getMap(923040200).setMobGen(9300447, true);
                    cm.getMap(923040300).setMobGen(9300443, true);
                    cm.getMap(923040300).setMobGen(9300444, true);
                    cm.getMap(923040300).setMobGen(9300445, true);
                    cm.getMap(923040300).setMobGen(9300448, true);
                    cm.getMap(923040300).setMobGen(9300462, true);
                    cm.getMap(923040300).setMobGen(9300463, true);
                    cm.resetMap(923040100);
                    cm.resetMap(923040200);
                    cm.resetMap(923040300);
                    cm.resetMap(923040400);
                    cm.spawnMobOnMap(9300460, 1, 0, 626, 923040300);
                    cm.spawnMobOnMap(9300461, 1, 400, 123, 923040400);
                    cm.spawnMobOnMap(9300468, 1, -1000, 123, 923040400);
                    cm.warpParty(923040100);
                    break;
                }
                case 1: {
                    if (cm.itemQuantity(4001535) < 5) {
                        cm.sendNextPrev("\r\n하지만 지금은 그렇게 연구를 도울 때가 아니에요. 켄타가 연락이 되지 않고 있어요. 무언가 위험에 빠진 것이 분명해요! 그를 찾아서 도와주세요!");
                    } else {
                        cm.sendSimple("피아누스의 비늘을 켄타의 연구를 위해 쓰시겠어요?#d\r\n#L0#1. 피아누스의 비늘 5개 - 펫 관련 주문서\r\n#L1#2. 피아누스의 비늘 10개 - #z1022175#");
                    }
                    break;
                }
                case 2: {
                    var say = "켄타가 모험가들이 구해다 준 샘플로 연구하는 데는 한계를 느끼고, 자신이 직접 바다 생물들의 이상 행동을 조사해야겠다며 위험한 바다 쪽으로 갔어요. 바다로 나간 후로 연락도 없고, 돌아오지도 않고 있어요. 무슨 일이 생긴 것이 분명해요.\r\n";
                    say += "\r\n#e- 레벨#n : 120 레벨 이상 #r(추천 레벨 : 120 ~ 169)#k";
                    say += "\r\n#e- 제한 시간#n : 30분";
                    say += "\r\n#e- 참가 인원#n : 2 ~ 6명";
                    say += "\r\n#e- 최종 보상#n : #i1022175# #d#z1022175##k";
                    cm.sendNextPrev(say);
                    break;
                }
            }
            break;
        }
        case 3: {
            switch (a2) {
                case 1: {
                    switch (selection) {
                        case 0: {
                            cm.sendSimple("피아누스의 비늘을 켄타의 연구를 위해 쓰시겠어요?#d\r\n#L0#1. #z2048018#\r\n#L1#2. #z2048019#");
                            break;
                        }
                        case 1: {
                            cm.dispose();
                            if (cm.haveItem(4001535, 10) == false) {
                                return;
                            }
                            cm.gainItem(4001535, -10);
                            cm.gainItem(1022175, 1);
                            cm.sendNext("\r\n물안경이 잘 어울리실 것 같네요.");
                            break;
                        }
                    }
                    break;
                }
                default: {
                    cm.dispose();
                    break;
                }
            }
            break;
        }
        case 4: {
            cm.dispose();
            if (cm.haveItem(4001535, 5) == false) {
                return;
            }
            cm.gainItem(4001535, -5);
            cm.gainItem(selection == 0 ? 2048018 : 2048019, 1);
            break;
        }
    }
}