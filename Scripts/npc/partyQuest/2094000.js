var status = -1;

var minLevel = 70;
var minUser = 2;
var maxUser = 6;
var questID = 7044;
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
        if (status == 3) {
            status = 0;
            action(1, 0, 2);
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (!cm.getPlayer().getOneInfoQuest(value, "pq_pirate_date").equals(date)) {
                cm.getPlayer().updateQuest(questID, "0");
            }
            var say = "#Cgray##e<파티 퀘스트 : 해적 데비존>#n#k\r\n\r\n무엇을 원하는 건가?#d";
            say += "\r\n#L0#파티 퀘스트를 하고 싶어요.";
            say += "\r\n#L1#설명을 듣고 싶어요.";
            //say += "\r\n#L2#데비존의 모자를 받고 싶어요.";
            say += "\r\n#L3#오늘 남은 도전 횟수를 알고 싶어요.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    var say = "해적 데비존이 습격해 왔다네. 도라지들의 왕인 #b우양#k님이 납치 되셨어. 데비존을 몰아내고 #b우양#k님을 구출해 주시게. 부탁하네.#d";
                    say += "\r\n#L0#1. 구옹의 부탁을 들어준다.";
                    cm.sendSimple(say);
                    break;
                }
                case 1: {
                    cm.dispose();
                    var say = "도라지들이 사는 #b백초 마을#k에 #r해적 데비존#k이 습격해 왔다네. 도라지들의 왕인 #b우양#k님이 납치되셨어. 동료들과 해적선에 침투 해 데비존을 몰아내 주시오. 부탁하네.\r\n";
                    say += "\r\n#e- 레벨#n : 70 레벨 이상 #r(추천 레벨 : 70 ~ 149)#k";
                    say += "\r\n#e- 제한 시간#n : 30분";
                    say += "\r\n#e- 참가 인원#n : 2 ~ 6명";
                    say += "\r\n#e- 최종 보상#n : #i1003856# #d#z1003856##k";
                    say += "\r\n　　　　　　  #i1003857# #d#z1003857##k";
                    say += "\r\n　　　　　　  #i1003858# #d#z1003858##k";
                    cm.sendNext(say);
                    break;
                }
                case 2: {
                    var say = "#b데비존#k을 물리치고 #b우양#k님을 구해줘서 정말 고맙네. 답례로 모자 조각을 내게 가져 오면 #b데비존의 모자#k로 만들어 주지. 어떤 모자를 원하는가?\r\n";
                    say += "\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
                    say += "\r\n#L0##i1003856# #d#z1003856##k";
                    say += "\r\n#L1##i1003857# #d#z1003857##k";
                    say += "\r\n#L2##i1003858# #d#z1003858##k";
                    cm.sendSimple(say);
                    break;
                }
                case 3: {
                    cm.dispose();
                    cm.sendNext("\r\n오늘 남은 도전 횟수는 " + (10 - cm.getPlayer().getIntNoRecord(questID)) + "회일세.");
                    break;
                }
            }
            a1 = selection;
            break;
        }
        case 2: {
            if (a1 == 2) {
                a2 = selection;
                if (selection == 0) {
                    cm.sendNext("\r\n#d#z" + 1003856 + "##k를 만들겠는가? 모자 조각은 충분히 가져왔겠지?");
                } else {
                    cm.sendNext("\r\n가지고 있는 #d#z" + (1003855 + selection) + "##k를 좀 더 강화하겠나? 강화 시, 잠재 옵션이 다시 초기화 되는데, 상관 없겠지?");
                }
                return;
            }
            cm.dispose();
            if (cm.getParty() == null || cm.isLeader() == false) {
                cm.sendNext("\r\n자네의 대표가 말을 걸어 주게나.");
                return;
            }
            var v1 = true;
            var pMember = cm.getParty().getMembers();
            if (pMember.size() < 2) {
                cm.sendNext("\r\n자네가 속한 파티의 파티원이 " + minUser + "명 이상이 아니라서 퀘스트를 진행 할 수 없네. " + minUser + "명 이상으로 맞춰 주게. 파티원을 모으려면 파티 찾기를 이용해 보는 건 어떤가?");
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
                        if (ccUser.getIntNoRecord(questID) > (dailyChallenge - 1) && ccUser.getOneInfoQuest(value, "pq_pirate_date").equals(date)) {
                            cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 해적 데비존은 #b1일 10회#k 도전이 가능합니다.");
                            return;
                        }
                        if (ccUser.isGM() == true) {
                            v1 = true;
                        }
                        if (v1 == false) {
                            cm.sendNext("\r\n자네, 혹은 파티원 중에서 레벨 " + minLevel + " 미만이거나 255 레벨이 넘은 친구가 있구만. " + minLevel + " 이상, 255 레벨 이하인 사람만 도전 할 수 있다네. 레벨을 맞춰 주게나.");
                            return;
                        }
                        for (i = 0; i < 600; i+=100) {
                            if (cm.getPlayerCount(925100000 + i) > 0) {
                                cm.sendNext("\r\n다른 파티가 해적 데비존에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                                return;
                            }
                        }
                        if (ccUser.getOneInfoQuest(value, "pq_pirate_date").equals(date) == false) {
                            ccUser.updateQuest(questID, "1");
                            ccUser.updateOneInfoQuest(value, "pq_pirate_date", date);
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
                        ccUser.dropMessage(5, "오늘 해적 데비존을 진행 한 횟수는 총 " + ccUser.getIntNoRecord(questID) + "회입니다. 앞으로 " + (dailyChallenge - ccUser.getIntNoRecord(questID)) + "회 더 입장 할 수 있습니다.");
                        ccUser.removeAll(4001117);
                        ccUser.removeAll(4001120);
                        ccUser.removeAll(4001121);
                        ccUser.removeAll(4001122);
                        ccUser.updateQuest(7046, "0");
                    }
                }
            }
            cm.getMap(925100100).setMobGen(9300114, false);
            cm.getMap(925100100).setMobGen(9300115, false);
            cm.getMap(925100100).setMobGen(9300116, false);
            cm.getMap(925100400).setMobGen(9300120, true);
            cm.getMap(925100400).setMobGen(9300121, true);
            cm.getMap(925100400).setMobGen(9300122, true);
            cm.getMap(925100400).setMobGen(9300126, true);
            cm.resetMap(925100000);
            cm.resetMap(925100100);
            cm.resetMap(925100400);
            cm.resetMap(925100500);
            cm.spawnMobOnMap(9300119, 1, 524, 238, 925100500);
            cm.warpParty(925100000);
            break;
        }
        case 3: {
            switch (a2) {
                case 0: {
                    if (cm.haveItem(4001455, 30) == false) {
                        cm.sendNextPrev("\r\n으음? 그걸로는 흔한 데비존의 모자를 만들 수가 없네. 흔한 데비존의 모자를 원한다면 모자 조각 30개가 필요하네.");
                        return;
                    }
                    cm.dispose();
                    cm.gainItem(4001455, -30);
                    cm.gainItem(1003856, 1);
                    break;
                }
                default: {
                    if (cm.haveItem(4001455, (a2 == 1 ? 60 : 90)) == false || cm.haveItem(1003855 + a2, 1) == false) {
                        cm.sendNextPrev("\r\n으음? 그걸로는 #z" + (1003855 + a2) + "#를 강화 할 수 없네. #z" + (1003855 + a2) + "#를 강화하길 원한다면 모자 조각 " + (a2 == 1 ? 60 : 90) + "개와 #i" + (1003855 + a2) + "# #z" + (1003855 + a2) + "# 1개가 필요하네.");
                        return;
                    }
                    cm.dispose();
                    cm.gainItem(4001455, -(a2 == 1 ? 60 : 90));
                    cm.gainItem((1003855 + a2), -1);
                    cm.gainItem((1003856 + a2), 1);
                    break;
                }
            }
            break;
        }
        case 4: {
            cm.dispose();
            break;
        }
    }
}