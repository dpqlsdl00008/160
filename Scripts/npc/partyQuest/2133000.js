var status = -1;

var minLevel = 70;
var minUser = 4;
var maxUser = 6;
var questID = 7916;
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
            if (!cm.getPlayer().getOneInfoQuest(value, "pq_elin_date").equals(date)) {
                cm.getPlayer().updateQuest(questID, "0");
            }
            var say = "#Cgray##e<파티 퀘스트 : 독 안개의 숲>#n#k\r\n\r\n괴인에 의해 오염되어 버린 숲을 구해야 해! 하지만 알테어 캠프의 용사들은 모두 생업에 바빠서 움직 일 수 없어. 네가 도와주지 않을래? #b70 레벨 이상의 모험가#k라면, 도와 줄 수 있을 거야!#d";
            say += "\r\n#L0#1. 독 안개의 숲으로 들어간다.";
            say += "\r\n#L1#2. 엘린의 이야기를 듣는다.";
            say += "\r\n#L2#3. 오늘 남은 도전 횟수를 알고 싶어요.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            a1 = selection;
            switch (selection) {
                case 0: {
                    cm.dispose();
                    if (cm.getParty() == null || cm.isLeader() == false) {
                        cm.sendNext("\r\n혼자서는 들어 갈 수 없답니다. 이 안에 들어가고 싶다면, 당신이 속한 파티의 파티장이 저에게 말을 걸어 주셔야 해요. 파티장에게 부탁해 보세요.");
                        return;
                    }
                    var v1 = true;
                    var pMember = cm.getParty().getMembers();
                    if (pMember.size() != minUser) {
                        cm.sendNext("\r\n여행자님이 속한 파티의 파티원이 " + minUser + "명이 아니라서 입장하실 수 없어요. 레벨 " + minLevel + " 이상의 파티원이 " + minUser + "명 있어야 입장하실 수 있으니 확인하시고 다시 저에게 말을 걸어주세요.");
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
                                if (ccUser.getIntNoRecord(questID) > (dailyChallenge - 1) && ccUser.getOneInfoQuest(value, "pq_elin_date").equals(date)) {
                                    cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 독 안개의 숲은 #b1일 10회#k 도전이 가능합니다.");
                                    return;
                                }
                                if (ccUser.isGM() == true) {
                                    v1 = true;
                                }
                                if (v1 == false) {
                                    cm.sendNext("\r\n당신이 속한 파티의 파티원이 #e#b2명 이상#k#n이 아니거나 자신 혹은 파티원 중에서 #e#b" + minLevel + " 레벨 미만#k#n인 캐릭터가 있습니다. 혹은 #e#r파티원 전원이 현재 맵#k#n에 모여있는지 다시 한 번 확인해 주세요.");
                                    return;
                                }
                                for (i = 0; i < 900; i+=100) {
                                    if (cm.getPlayerCount(930000000 + i) > 0) {
                                        cm.sendNext("\r\n다른 파티가 독 안개의 숲에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                                        return;
                                    }
                                }
                                if (cm.getPlayerCount(930000010) > 0) {
                                    cm.sendNext("\r\n다른 파티가 독 안개의 숲에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                                    return;
                                }
                                if (ccUser.getOneInfoQuest(value, "pq_elin_date").equals(date) == false) {
                                    ccUser.updateQuest(questID, "1");
                                    ccUser.updateOneInfoQuest(value, "pq_elin_date", date);
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
                                ccUser.removeAll(2270004);
                                ccUser.removeAll(4001161);
                                ccUser.removeAll(4001162);
                                ccUser.removeAll(4001163);
                                ccUser.removeAll(4001169);
                                ccUser.updateQuest(7917, "0");
                                ccUser.dropMessage(5, "오늘 독 안개의 숲을 진행 한 횟수는 총 " + ccUser.getIntNoRecord(questID) + "회입니다. 앞으로 " + (dailyChallenge - ccUser.getIntNoRecord(questID)) + "회 더 입장 할 수 있습니다.");
                            }
                        }
                    }
                    cm.resetMap(930000000);
                    cm.resetMap(930000010);
                    cm.resetMap(930000100);
                    cm.resetMap(930000200);
                    cm.resetMap(930000300);
                    cm.resetMap(930000400);
                    cm.resetMap(930000500);
                    cm.resetMap(930000600);
                    cm.resetMap(930000700);
                    cm.resetMap(930000800);
                    cm.warpParty(930000000);
                    break;
                }
                case 1: {
                    cm.sendNext("\r\n원래 이 숲은 요정들이 사는 맑고 고요한 숲이었어. 그런데 얼마 전부터 #r검은 로브를 입은 괴인#k이 들어와 요정들을 내쫓고 이상한 연구를 시작했어. 그가 하는 연구 때문인지 숲이 독에 점점 오염되어 가고 있어. 당장 숲을 구해야 해!");
                    break;
                }
                case 2: {
                    cm.dispose();
                    cm.sendNext("\r\n오늘 남은 도전 횟수는 " + (dailyChallenge - cm.getPlayer().getIntNoRecord(questID)) + "회입니다.");
                    break;
                }
            }
            break;
        }
        case 2: {
            switch (a1) {
                case 1: {
                    var say = "\r\n하지만 알테어 캠프에 있는 용사들은 캠프 유저와 새로운 지역 발굴 때문에 너무 바빠서 도와 줄 수 없다고 해. 그러니, 네가 도와 주지 않을래? 용사들을 대신해, 네가 그들이 되어서...\r\n";
                    say += "\r\n#e- 레벨#n : 70 레벨 이상 #r(추천 레벨 : 70 ~ 99)#k";
                    say += "\r\n#e- 제한 시간#n : 30분";
                    say += "\r\n#e- 참가 인원#n : 4명";
                    cm.sendNextPrev(say);
                    break;
                }
            }
            break;
        }
        case 3: {
            cm.dispose();
            break;
        }
    }
}