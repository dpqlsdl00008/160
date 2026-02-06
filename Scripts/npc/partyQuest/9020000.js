var status = -1;

var minLevel = 20;
var minUser = 3;
var maxUser = 6;
var questID = 7912;
var dailyChallenge = 10;
var value = 10200000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

function start() {
    status = -1;
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
            if (!cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_date").equals(date)) {
                cm.getPlayer().updateQuest(questID, "0");
            }
            var say = "#e#Cgray#<파티 퀘스트 : 첫 번째 동행>#k#n\r\n\r\n파티원들과 함께 힘을 모아 퀘스트를 해결해 보시지 않겠습니까? 이 안에는 서로 힘을 합치지 않으면 해결 할 수 없는 장애물들이 많이 있답니다.";
            if (cm.getMapId() == 910340700) {
                say += ".. 도전해 보고 싶다면 #r당신이 속한 파티의 파티장#k을 통해 저에게 말을 걸어 주세요.";
            }
            if (cm.getMapId() == 910340700) {
                say += "\r\n#L0##d파티 퀘스트를 하고 싶어요.#k";
                say += "\r\n#L1##d설명을 듣고 싶어요.#k";
                //say += "\r\n#L2##d말랑 말랑한 신발을 갖고 싶어요.#k";
                say += "\r\n#L3##d오늘의 남은 도전 횟수를 알고 싶어요.#k";
            } else {
                say += "\r\n#L4##d첫 번째 동행 대기실로 갑니다.#k";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            switch (selection) {
                case 0: {
                    if (cm.getParty() == null || cm.isLeader() == false) {
                        cm.sendNext("\r\n파티원들과 함께 힘을 모아 퀘스트를 해결해 보시지 않겠습니까? 이 안에는 서로 힘을 합치지 않으면 해결 할 수 없는 장애물들이 많이 있답니다... 도전해 보고 싶다면 #r당신이 속한 파티의 파티장#k을 통해 저에게 말을 걸어 주세요.");
                        return;
                    }
                    var v1 = true;
                    var pMember = cm.getParty().getMembers();
                    if (pMember.size() < minUser) {
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
                                if (ccUser.getIntNoRecord(questID) > (dailyChallenge - 1) && ccUser.getOneInfoQuest(value, "pq_kerningcity_date").equals(date)) {
                                    cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 첫 번째 동행은 #b1일 10회#k 도전이 가능합니다.");
                                    return;
                                }
                                var eScript = cm.getEventManager("partyquest_kerningcity");
                                if (eScript == null) {
                                    cm.getPlayer().dropMessage(1, "error");
                                    return;
                                }
                                if (ccUser.isGM() == true) {
                                    v1 = true;
                                }
                                for (i = 0; i < 500; i+=100) {
                                    if (cm.getPlayerCount(910340100 + i) > 0) {
                                        cm.sendNext("\r\n다른 파티가 첫 번째 동행에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                                        return;
                                    }
                                }
                                if (v1 == false) {
                                    cm.sendNext("\r\n당신이 속한 파티의 파티원이 #e#b3명 이상#k#n이 아니거나 자신 혹은 파티원 중에서 #e#b" + minLevel + " 레벨 미만#k#n인 캐릭터가 있습니다. 혹은 #e#r파티원 전원이 현재 맵#k#n에 모여있는지 다시 한 번 확인해 주세요.");
                                    return;
                                }
                                if (ccUser.getOneInfoQuest(value, "pq_kerningcity_date").equals(date) == false) {
                                    ccUser.updateQuest(questID, "1");
                                    ccUser.updateOneInfoQuest(value, "pq_kerningcity_date", date);
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
                                ccUser.removeAll(4001007);
                                ccUser.removeAll(4001008);
                                ccUser.updateOneInfoQuest(value, "pq_kerningcity_clear", "0");
                                ccUser.updateOneInfoQuest(value, "pq_kerningcity_answer", "0");
                                ccUser.updateOneInfoQuest(value, "pq_kerningcity_question", "" + (3 + Packages.server.Randomizer.nextInt(5)));
                                ccUser.dropMessage(5, "오늘 첫 번째 동행을 진행 한 횟수는 총 " + ccUser.getIntNoRecord(questID) + "회입니다. 앞으로 " + (dailyChallenge - ccUser.getIntNoRecord(questID)) + "회 더 입장 할 수 있습니다.");
                            }
                        }
                    }
                    cm.resetMap(910340100);
                    cm.resetMap(910340200);
                    cm.resetMap(910340300);
                    cm.resetMap(910340400);
                    cm.resetMap(910340500);
                    cm.spawnMobOnMap(9300003, 1, -45, -435, 910340500);
                    cm.warpParty(910340100);
                    break;
                }
                case 1: {
                    var say = "\r\n용감한 모험가들을 기다리고 있습니다. 함께 힘과 지혜를 모아 과제들을 풀어내고 강력한 #r킹 슬라임#k을 물리쳐 주세요! 퀴즈의 정답 숫자만큼 통행증 얻어 오기, 정답 위치 맞추기 등의 퀴즈들을 해결하면 킹 슬라임이 등장합니다.\r\n";
                    say += "\r\n#e- 레벨#n : 20 레벨 이상 #r(추천 레벨 : 20 ~ 69)#k";
                    say += "\r\n#e- 제한 시간#n : 30분";
                    say += "\r\n#e- 참가 인원#n : 3 ~ 4명";
                    say += "\r\n#e- 획득 아이템#n : #i1072634# #z1072634#";
                    cm.sendNext(say);
                    break;
                }
                case 2: {
                    if (cm.haveItem(4001531, 15) == false) {
                        cm.sendNext("\r\n#i1072533# #z1072533#을 갖고 싶다면 #b#z4001531# 15개#k가 필요합니다. #z4001531#는 #r킹 슬라임#k을 물리치시면 얻으실 수 있습니다.");
                        return;
                    }
                    cm.gainItem(4001531, -15);
                    cm.gainItem(1072533, 1);
                    cm.sendNext("\r\n#z4001531# 15개와 #z1072533#을 교환해드렸습니다.");
                    break;
                }
                case 3: {
                    cm.sendNext("\r\n오늘 남은 도전 횟수는 #e#r" + (dailyChallenge - cm.getPlayer().getIntNoRecord(questID)) + "회#k#n입니다.");
                    break;
                }
                case 4: {
                    cm.warp(910340700, "out00");
                    break;
                }
            }
        }
    }
}