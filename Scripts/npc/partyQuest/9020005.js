var status = -1;

var minLevel = 120;
var minUser = 2;
var maxUser = 6;
var questID = 7928;
var dailyChallenge = 10;
var value = 10200000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
var itemID = [1132094, 1132095, 1132096, 1132097, 1132098];

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
            if (!cm.getPlayer().getOneInfoQuest(value, "pq_escape_date").equals(date)) {
                cm.getPlayer().updateQuest(questID, "0");
            }
            var say = "#Cgray##e<파티 퀘스트 : 탈출>#n#k\r\n\r\n솔직히 이대로 당장 도망가고 싶긴 하지만... 그의 부탁을 외면 할 수 없었어. 이 성 안, 공중 감옥에 갇혀 있는 사람이 이 곳을 탈출하게 도와 줄 사람을 찾고 있어.#d";
            say += "\r\n#L0#성에 갇힌 모험가를 도와주러 가겠어요!";
            say += "\r\n#L1#성의 감옥에 대해 알려주세요.";
            //say += "\r\n#L2#교도관의 열쇠에 대해 알려 주세요.";
            say += "\r\n#L3#오늘 남은 도전 횟수를 알고 싶어요.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            a1 = selection;
            switch (selection) {
                case 0: {
                    cm.sendNext("\r\n보기보다 용감하군. 공중 감옥으로 안내해 줄테니 나를 따라 와.");
                    break;
                }
                case 1: {
                    var say = "\r\n이 성 안에는 숨겨진 탑이 있어. 탑에 있는 공중 감옥에는 많은 사람들이 갇혀 있지. 이들을 구해줘야 해...\r\n";
                    say += "\r\n#e- 레벨#n : 120 레벨 이상 #r(추천 레벨 : 120 ~ 169)#k";
                    say += "\r\n#e- 제한 시간#n : 30분";
                    say += "\r\n#e- 참가 인원#n : 2 ~ 6명";
                    say += "\r\n#e- 획득 아이템#n :\r\n#i1132094# #d#z1132094##k";
                    say += "\r\n#i1132095# #d#z1132095##k";
                    say += "\r\n#i1132096# #d#z1132096##k";
                    say += "\r\n#i1132097# #d#z1132097##k";
                    say += "\r\n#i1132098# #d#z1132098##k";
                    cm.sendNext(say);
                    break;
                }
                case 2: {
                    var say = "#r교도관의 열쇠#k는 숨겨진 탑의 교도관이 가지고 있던 열쇠야. #b50개#k 정도를 모아오면 내가 작은 선물을 줄께. 50개를 모았다는 것은 그만큼 억울하게 성에 갇혀 있는 사람들을 많이 도와줬다는 뜻이기도 하니까.#d\r\n";
                    if (cm.itemQuantity(4001534) < 50) {
                        cm.dispose();
                        for (i = 0; i < itemID.length; i++) {
                            say += "\r\n#i" + itemID[i] + "# #z" + itemID[i] + "#";
                        }
                        cm.sendNext("\r\n" + say);
                        return;
                    }
                    say = "성에 갇혀 있는 사람들의 탈출을 열심히 돕고 있는 모양이네. 보기보다 용감한 모양이야. 나는 여전히 두려운데... #r교도관의 열쇠#k를 #b50개#k 이상 가지고 있군. #b50개#k로 지금 교환 할 수 있는 물건 중 어느 것을 원하지?#d\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
                    for (i = 0; i < itemID.length; i++) {
                        say += "\r\n#L" + i + "##i" + itemID[i] + "# #z" + itemID[i] + "#";
                    }
                    cm.sendSimple(say);
                    break;
                }
                case 3: {
                    cm.dispose();
                    cm.sendNext("\r\n오늘 남은 도전 횟수는 " + (dailyChallenge - cm.getPlayer().getIntNoRecord(questID)) + "회야.");
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
                        cm.sendNext("\r\n이곳은 너무 위험해. 무서운 녀석들 투성이야. 혼자서는 아무래도 힘들텐데... 안에 들어가고 싶다면, 당신이 속한 파티의 파티장이 나에게 말을 걸도록 해.");
                        return;
                    }
                    var v1 = true;
                    var pMember = cm.getParty().getMembers();
                    if (pMember.size() < minUser) {
                        cm.sendNext("\r\n파티원이 " + minUser + "명 미만이로군. 이곳은 위험한 곳이니, 적어도 레벨 " + minLevel + " 이상의 " + minUser + "명 이상 파티원이 있어야 입장 할 수 있으니 다시 확인해 봐.");
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
                                if (ccUser.getIntNoRecord(questID) > (dailyChallenge - 1) && ccUser.getOneInfoQuest(value, "pq_escape_date").equals(date)) {
                                    cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 탈출은 #b1일 10회#k 도전이 가능합니다.");
                                    return;
                                }
                                if (ccUser.isGM() == true) {
                                    v1 = true;
                                }
                                if (v1 == false) {
                                    cm.sendNext("\r\n당신이 속한 파티의 파티원이 #e#b2명 이상#k#n이 아니거나 자신 혹은 파티원 중에서 #e#b" + minLevel + " 레벨 미만#k#n인 캐릭터가 있습니다. 혹은 #e#r파티원 전원이 현재 맵#k#n에 모여있는지 다시 한 번 확인해 주세요.");
                                    return;
                                }
                                for (i = 0; i < 700; i+=100) {
                                    if (cm.getPlayerCount(921160100 + i) > 0) {
                                        cm.sendNext("\r\n다른 파티가 탈출에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                                        return;
                                    }
                                }
                                if (ccUser.getOneInfoQuest(value, "pq_escape_date").equals(date) == false) {
                                    ccUser.updateQuest(questID, "1");
                                    ccUser.updateOneInfoQuest(value, "pq_escape_date", date);
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
                                ccUser.removeAll(4001528);
                                ccUser.updateQuest(7929, "0");
                                ccUser.dropMessage(5, "오늘 탈출을 진행 한 횟수는 총 " + ccUser.getIntNoRecord(questID) + "회입니다. 앞으로 " + (dailyChallenge - ccUser.getIntNoRecord(questID)) + "회 더 입장 할 수 있습니다.");
                            }
                        }
                    }
                    cm.getMap(921160600).setMobGen(9300452, true);
                    cm.getMap(921160600).setMobGen(9300453, true);
                    cm.resetMap(921160100);
                    cm.resetMap(921160200);
                    cm.resetMap(921160300);
                    cm.resetMap(921160400);
                    cm.resetMap(921160500);
                    cm.resetMap(921160600);
                    cm.resetMap(921160700);
                    cm.spawnMobOnMap(9300454, 1, -633, -181, 921160700);
                    cm.warpParty(921160100);
                    break;
                }
                case 1: {
                    var say = "\r\n감옥에 들어가게 되면 이것만은 꼭 알아 둬.#d\r\n";
                    say += "\r\n1. 장애물을 피해 탑을 벗어나야 해.";
                    say += "\r\n2. 맵 내의 모든 겸비병들을 제거해야 해.";
                    say += "\r\n3. 감옥으로의 접근을 막기 위해 만든 미로를 벗어나야 해.";
                    say += "\r\n4. 문을 지키고 있는 경비병을 모두 처치해야 해.";
                    say += "\r\n5. 공중 감옥으로 가는 마지막 장애물을 통과해야 해.";
                    say += "\r\n6. 경비병을 처치하고 감옥 열쇠를 되찾아 감옥 문을 열어야 해.";
                    say += "\r\n7. 교도관 아니를 물리치고 갇혀있던 이들에게 자유를 되찾아줘야 해.";
                    cm.sendNextPrev(say);
                    break;
                }
                case 2: {
                    cm.dispose();
                    if (cm.haveItem(4001534, 50) == false) {
                        return;
                    }
                    cm.gainItem(4001534, -50);
                    cm.gainItem(itemID[selection], 1);
                    cm.sendNext("\r\n내가 직접 돕진 못했지만 이 위험한 곳에서 나 대신 갇혀 있는 이들을 도와 준 용기있는 행동에 대한 보답이라고 생각해 줘.");
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