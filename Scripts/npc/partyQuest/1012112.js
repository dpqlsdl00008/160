var status = -1;

var minLevel = 50;
var minUser = 2;
var maxUser = 6;
var questID = 7910;
var dailyChallenge = 10;
var value = 10200000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

function start() {
    switch (cm.getMapId()) {
        case 910010400: {
            status = 2;
            break;
        }
        default: {
            status = -1;
            break;
        }
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
            if (!cm.getPlayer().getOneInfoQuest(value, "pq_henesys_date").equals(date) || cm.getPlayer().isGM()) {
                cm.getPlayer().updateQuest(questID, "0");
            }
            var say = "#Cgray##e<파티 퀘스트 : 월묘의 떡>#n#k\r\n\r\n안녕하세요? 저는 토리라고 해요. 달맞이 꽃 언덕에 가보셨나요? 이 안은 달맞이 꽃이 피어나는 아름다운 언덕이에요. 그런데 그 곳에 살고 있는 어흥이라는 호랑이가 몹시 배가 고파 먹을 것을 찾고 있다고 하네요. 여행자님께서 달맞이 꽃 언덕으로 가서 파티원들과 함께 힘을 모아 어흥이를 도와주지 않으시겠어요?#d";
            say += "\r\n#L0#1. 달맞이 꽃 언덕으로 간다.";
            say += "\r\n#L1#2. 달맞이 꽃 언덕에 대해 설명을 듣는다.";
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
                                if (ccUser.getIntNoRecord(questID) > (dailyChallenge - 1) && ccUser.getOneInfoQuest(value, "pq_henesys_date").equals(date)) {
                                    cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 월묘의 떡은 #b1일 10회#k 도전이 가능합니다.");
                                    return;
                                }
                                if (ccUser.isGM() == true) {
                                    v1 = true;
                                }
                                if (v1 == false) {
                                    cm.sendNext("\r\n당신이 속한 파티의 파티원이 #e#b" + minUser + "명 이상#k#n이 아니거나 자신 혹은 파티원 중에서 #e#b" + minLevel + " 레벨 미만#k#n인 캐릭터가 있습니다. 혹은 #e#r파티원 전원이 현재 맵#k#n에 모여있는지 다시 한 번 확인해 주세요.");
                                    return;
                                }
                                if (cm.getPlayerCount(910010000) > 0) {
                                    cm.sendNext("\r\n다른 파티가 월묘의 떡에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                                    return;
                                }
                                if (ccUser.getOneInfoQuest(value, "pq_henesys_date").equals(date) == false) {
                                    ccUser.updateQuest(questID, "1");
                                    ccUser.updateOneInfoQuest(value, "pq_henesys_date", date);
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
                                ccUser.removeAll(4001101);
                                ccUser.removeAll(4001453);
                                ccUser.dropMessage(5, "오늘 월묘의 떡을 진행 한 횟수는 총 " + ccUser.getIntNoRecord(questID) + "회입니다. 앞으로 " + (dailyChallenge - ccUser.getIntNoRecord(questID)) + "회 더 입장 할 수 있습니다.");
                            }
                        }
                    }
                    cm.getMap(910010000).setMobGen(9300058, true);
                    cm.getMap(910010000).setMobGen(9300059, true);
                    cm.getMap(910010000).setMobGen(9300064, false);
                    cm.getMap(910010000).setMobGen(9300062, false);
                    cm.getMap(910010000).setMobGen(9300063, false);
                    cm.getMap(910010000).setMobGen(9300064, false);
                    cm.resetMap(910010000);
                    cm.warpParty(910010000);
                    break;
                }
                case 1: {
                    var say = "\r\n만월의 #b달맞이 꽃 언덕#k에만 등장한다는 신비한 토끼 월묘. #b어흥이#k에게 #r월묘의 떡#k을 구해줄 모험가들을 #b헤네시스 공원#k의 #b토리#k가 찾고 있어요. 월묘를 만나고 싶으면 달맞이 꽃 씨앗을 심어 보름달을 불러내야 해요. #r떡 10개#k를 다 만들 때까지 난폭한 동물들에게서 월묘를 안전하게 지켜주세요.\r\n";
                    say += "\r\n#e- 레벨#n : 50 레벨 이상 #r(추천 레벨 : 50 ~ 99)#k";
                    say += "\r\n#e- 제한 시간#n : 30분";
                    say += "\r\n#e- 참가 인원#n : 2 ~ 6명";
                    say += "\r\n#e- 획득 아이템#n : #i1003979# #d#z1003979#";
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
            var say = "어머! 절 위해 월묘의 떡을 가져 오신 건가요? 감사함을 표현하기 위해 작은 선물도 준비했어요. 제게 몇 개를 주실 건가요?#d";
            if (cm.haveItem(4001101, 10) == false) {
                status = 3;
                action(1, 0, -1);
                return;
            }
            if (cm.haveItem(4001101, 10) == true) {
                say += "\r\n#L0#월묘의 떡 10개 - #z1003418#";
            }
            if (cm.haveItem(4001101, 50) == true) {
                say += "\r\n#L1#월묘의 떡 50개 - #z1003419#";
            }
            if (cm.haveItem(4001101, 99) == true) {
                say += "\r\n#L2#월묘의 떡 99개 - #z1003979#";
            }
            cm.sendSimple(say);
            break;
        }
        case 4: {
            cm.dispose();
            itemID = 0;
            switch (selection) {
                case 0: {
                    cm.gainItem(4001101, -10);
                    itemID = 1003418;
                    break;
                }
                case 1: {
                    cm.gainItem(4001101, -50);
                    itemID = 1003419;
                    break;
                }
                case 2: {
                    cm.gainItem(4001101, -99);
                    itemID = 1003979;
                    break;
                }
            }
            if (itemID != 0) {
                Packages.handling.channel.handler.DueyHandler.addNewItemToDb(itemID, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            }
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310050, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            cm.getClient().sendPacket(Packages.tools.packet.CField.receiveParcel("월묘의 떡", true));
            cm.gainExp(499999);
            cm.warp(910010500, "out00");
            break;
        }
    }
}