var status = -1;

var minLevel = 70;
var minUser = 4;
var maxUser = 6;
var questID = 7922;
var dailyChallenge = 10;
var value = 10200000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
var mapID = [926110000, 926110001, 926110100, 926110200, 926110201, 926110202, 926110203, 926110300, 926110301, 926110302, 926110303, 926110304, 926110400, 926110401, 926110500, 926110600, 926110700];

function start() {
    switch (cm.getMapId()) {
        case 261000021: {
            status = -1;
            break;
        }
        case 926110000: {
            cm.dispose();
            cm.sendNext("\r\n이 연구실에서 가끔 수상한 소리가 들린다는 소문이 있었습니다. 분명 이 곳에 뭔가 있을 거에요!! 주변을 샅샅이 수색해 주세요.");
            return;
        }
        case 926100401:
        case 926110401: {
            cm.dispose();
            cm.warpParty(cm.getMapId() + 99);
            return;
        }
        case 926100600:
        case 926110600: {
            status = 5;
            break;
        }
        default: {
            cm.dispose();
            return;
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
        if (status == 2) {
            status = 0;
            action(1, 0, a1);
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (!cm.getPlayer().getOneInfoQuest(value, "pq_romeo_julliet_date").equals(date)) {
                cm.getPlayer().updateQuest(questID, "0");
            }
            var say = "#Cgray##e<파티 퀘스트 : 로미오와 줄리엣>#n#k\r\n\r\n마가티아는 지금 크나 큰 위기를 맞이하고 있습니다. 용감한 메이플의 모험가님들께서 저희를 도와주시지 않으시겠어요?#d";
            say += "\r\n#L0#줄리엣의 이야기를 듣는다.";
            say += "\r\n#L1#퀘스트를 시작한다.";
            //say += "\r\n#L2#알카드노의 구슬로 목걸이를 만들고 싶어요.";
            //say += "\r\n#L3#두 목걸이를 하나로 합치고 싶어요.";
            say += "\r\n#L4#오늘의 남은 도전 횟수를 알고 싶어요.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            a1 = selection;
            switch (selection) {
                case 0: {
                    cm.sendNext("\r\n저, 줄리엣과 로미오는 서로 사랑하는 사이입니다. 하지만 저는 알카드노, 로미오는 제뉴미스트 소속이기 때문에 서로 이루어질 수 없는 운명이에요...");
                    break;
                }
                case 1: {
                    cm.dispose();
                    if (cm.getParty() == null || cm.isLeader() == false) {
                        cm.sendNext("\r\n파티장을 통해 진행하실 수 있습니다.");
                        return;
                    }
                    var v1 = true;
                    var pMember = cm.getParty().getMembers();
                    minUser = (cm.getPlayer().isGM() ? 1 : minUser);
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
                                if (ccUser.getIntNoRecord(questID) > (dailyChallenge - 1) && ccUser.getOneInfoQuest(value, "pq_romeo_julliet_date").equals(date)) {
                                    cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 로미오와 줄리엣은 #b1일 10회#k 도전이 가능합니다.");
                                    return;
                                }
                                if (ccUser.isGM() == true) {
                                    v1 = true;
                                }
                                if (v1 == false) {
                                    cm.sendNext("\r\n당신이 속한 파티의 파티원이 #e#b2명 이상#k#n이 아니거나 자신 혹은 파티원 중에서 #e#b" + minLevel + " 레벨 미만#k#n인 캐릭터가 있습니다. 혹은 #e#r파티원 전원이 현재 맵#k#n에 모여있는지 다시 한 번 확인해 주세요.");
                                    return;
                                }
                                for (i = 0; i < mapID.length; i++) {
                                    if (cm.getPlayerCount(mapID[i]) > 0) {
                                        cm.sendNext("\r\n다른 파티가 로미오와 줄리엣에 도전 중에 있습니다. 잠시 후에 다시 시도해 주세요.");
                                        return;
                                    }
                                }
                                if (ccUser.getOneInfoQuest(value, "pq_romeo_julliet_date").equals(date) == false) {
                                    ccUser.updateQuest(questID, "1");
                                    ccUser.updateOneInfoQuest(value, "pq_romeo_julliet_date", date);
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
                                ccUser.removeAll(4001132);
                                ccUser.removeAll(4001133);
                                ccUser.removeAll(4001134);
                                ccUser.removeAll(4001135);
                                ccUser.updateQuest(7923, "0");
                                ccUser.updateQuest(7930, "0");
                                ccUser.dropMessage(5, "오늘 로미오와 줄리엣을 진행 한 횟수는 총 " + ccUser.getIntNoRecord(questID) + "회입니다. 앞으로 " + (dailyChallenge - ccUser.getIntNoRecord(questID)) + "회 더 입장 할 수 있습니다.");
                            }
                        }
                    }
                    cm.resetMap(926110000);
                    cm.resetMap(926110001);
                    cm.resetMap(926110100);
                    cm.resetMap(926110200);
                    cm.resetMap(926110300);
                    cm.resetMap(926110301);
                    cm.resetMap(926110302);
                    cm.resetMap(926110303);
                    cm.resetMap(926110304);
                    cm.resetMap(926110400);
                    cm.resetMap(926110401);
                    cm.resetMap(926110500);
                    cm.resetMap(926110600);
                    cm.resetMap(926110700);
                    cm.getMap(926110401).spawnNpc(2112010, new java.awt.Point(250, 150));
                    cm.warpParty(926110000);
                    break;
                }
                case 2: {
                    if (cm.haveItem(4001160, 2) == false) {
                        cm.dispose();
                        cm.sendNext("\r\n줄리엣의 펜던트를 만들기 위해서는 알카드노의 구슬 2개가 필요해요. 이걸로는 부족한 걸요?");
                        return;
                    }
                    cm.dispose();
                    cm.askAcceptDecline("#b알카드노 구슬 2개#k를 가지고 오셨군요. #i1122117# #b#z1122117##k를 만들 수 있어요. 지금 만들어 드릴까요?");
                    break;
                }
                case 3: {
                    if (cm.haveItem(1122116, 1) == false && cm.haveItem(1122117, 1) == false) {
                        cm.dispose();
                        cm.sendNext("\r\n로미오의 펜던트와 줄리엣의 펜던트가 하나씩 있어야 하나로 합칠 수 있어요.");
                        return;
                    }
                    cm.dispose();
                    cm.askAcceptDecline("제 #b줄리엣의 펜던트#k 뿐만 아니라, 로미오의 #b로미오의 펜던트#k도 가지고 오셨군요. 이 두 목걸이를 하나로 합쳐 #i1122118# #b#z1122118##k를 만들 수 있어요. 지금 만들어 드릴까요?");
                    break;
                }
                case 4: {
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
                    cm.sendNextPrev("\r\n지금까지 많은 노력을 해왔지만, 안타깝게도 지금 마가티아는 #b전쟁의 위기#k에 빠져 있습니다. 얼마 전, #b제뉴미스트와 알카드노의 에너지원이 모두 도난#k당하고 말았기 때문이죠. 알카드노와 제뉴미스트는 지금 서로에게 책임을 지우고 있는 상황입니다.");
                    break;
                }
            }
            break;
        }
        case 3: {
            a3 = a2;
            switch (a2) {
                case 0: {
                    cm.sendNextPrev("\r\n저는 익명의 제보를 통해, 이것이 #b제 3의 인물#k의 짓임을 알게 되었습니다. 마가티아의 전쟁을 막고 로미오와의 사랑을 이루기 위해서는 #b제 3의 인물#k을 찾아 내, 그의 음모를 막아야만 합니다.");
                    break;
                }
            }
            break;
        }
        case 4: {
            switch (a3) {
                case 0: {
                    var say = "\r\n용감한 메이플의 모험가 여러분!! 마가티아의 평화를 위해서 부디 저희들을 도와주세요!!\r\n";
                    say += "\r\n#e- 레벨#n : 70 레벨 이상 #r(추천 레벨 : 70 ~ 99)#k";
                    say += "\r\n#e- 제한 시간#n : 30분";
                    say += "\r\n#e- 참가 인원#n : 4명";
                    say += "\r\n#e- 획득 아이템#n : #i1122117# #d#z1122117##k";
                    say += "\r\n　　　　　　　  #i1122118# #d#z1122118##k";
                    cm.sendNextPrev(say);
                    break;
                }
            }
            break;
        }
        case 5: {
            cm.dispose();
            break;
        }
        case 6: {
            cm.sendNext("\r\n저희를 도와주셔서 정말 감사합니다! 여전히 마가티아에는 위험히 도사리고 있지만, 급한 불은 끌 수 있을 것 같습니다.");
            break;
        }
        case 7: {
            cm.sendNextPrev("\r\n그럼 여러분께 제가 간직하던 #b알카드노의 구슬#k과 함께 작은 보답을 드리고 밖으로 내보내 드리겠습니다.");
            break;
        }
        case 8: {
            cm.dispose();
            //Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4001160, 2, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310050, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            cm.getClient().sendPacket(Packages.tools.packet.CField.receiveParcel("로미오와 줄리엣", true));
            cm.gainExp(999999);
            cm.warp(cm.getMapId() + 100, 0);
            break;
        }
    }
}