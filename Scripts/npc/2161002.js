var status = -1;

var value = 20000000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

function start() {
    switch (cm.getMapId()) {
        case 211060200: {
            status = -1;
            break;
        }
        case 211060400: {
            status = 11;
            break;
        }
        case 211060600: {
            status = 20;
            break;
        }
        case 211061000: {
            status = 33;
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
        if (status == 0 || status == 12 || status == 21 || status == 34) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (cm.isQuestFinished(3139) == true) {
                cm.dispose();
                cm.warp(211060300, "west00");
                return;
            }
            if (cm.getPlayer().getInfoQuest(3139).contains("clear=1")) {
                status = 7;
                cm.sendNextS("\r\n레드 크로키를 물리치고 첫번째 봉인을 푸셨군요. 모험가님께서는 제 생각보다 강인한 분인 모양이로군요. 하지만 앞으로도 이러한 봉인을 두 개나 더 풀어야만 제가 있는 곳으로 올 수 있답니다. 지금이라도 돌아가는 것이 어떻겠습니까?", 4);
            }
            cm.sendNextS("\r\n오오, 이 성에 사람이 들어온 것이 얼마만인지... 모험가님. 이 곳은 정말 위험한 곳입니다. 몸을 피하세요.", 4);
            break;
        }
        case 1: {
            cm.sendNextPrevS("\r\n레드 크로키를 물리치고 첫 번째 봉인을 푸셨군요. 모험가님께서는 제 생각보다 강인한 분인 모양이로군요. 하지만 앞으로도 #r이러한 봉인을 두 개나 더 풀어야만#k 제가 있는 곳으로 올 수 있답니다. 지금이라도 돌아가는 것이 어떻겠습니까?", 4);
            break;
        }
        case 2: {
            cm.sendNextPrevS("\r\n...누구...?! 귀신인가???", 16);
            break;
        }
        case 3: {
            cm.sendNextPrevS("\r\n놀라게 해드려서 죄송합니다. 저는 이 성을 지켜온 기사 #b루덴#k입니다. 오래 전에 죽었어야 할 몸이지만 이렇게 유령이 되어서도 성을 떠나지 못하고 있죠.", 4);
            break;
        }
        case 4: {
            cm.sendNextPrevS("\r\n어째서 유령인 채로 이 성에 머물러 있는 거죠? 그 만큼 지켜야 할 것이 있나요?", 16);
            break;
        }
        case 5: {
            cm.sendNextPrevS("\r\n자세한 것은 만나면 말씀 드리겠습니다. 일단 이 문을 넘어서려면 첫번째 탑루를 지키는 사악한 #b레드 크로키#k를 잡고 봉인을 풀어야 합니다. 근처에 뛰어난 열쇠공을 본 적이 있으니 그에게 #r첫 번째 탑루로 들어갈 열쇠#k를 부탁해보세요.", 4);
            break;
        }
        case 6: {
            cm.dispose();
            cm.forceStartQuest(3139);
            break;
        }
        case 7: {
            cm.dispose();
            break;
        }
        case 8: {
            cm.sendNextPrevS("\r\n그런 말을 들으니 더욱 오기가 생기네요. 금방 갈테니 기다려 주세요.", 16);
            break;
        }
        case 9: {
            cm.sendNextPrevS("\r\n...그렇다면 승리의 축복을 기원하겠습니다. 부디 타락한 이들을 구원하시길.", 4);
            break;
        }
        case 10: {
            cm.dispose();
            cm.gainItem(4032832, -1);
            cm.forceCompleteQuest(3139);
            break;
        }
        case 11: {
            cm.dispose();
            break;
        }
        case 12: {
            if (cm.isQuestFinished(3140) == true) {
                cm.dispose();
                if (cm.getPlayer().getTruePosition().y < 100) {
                    cm.warp(211060410, "in00");
                } else {
                    cm.warp(211060500, "west00");
                }
                return;
            }
            if (cm.getPlayer().getInfoQuest(3140).contains("clear=1")) {
                status = 16;
                cm.sendNextS("\r\n교도관 보어마저도 물리치셨군요. 마지막 봉인을 풀기 위해서는 더욱 위험한 난관을 넘어서야 하지만, 모험가님이라면 왠지 해낼 것만 같은 믿음이 듭니다.", 4);
            }
            cm.sendNextS("\r\n벌써 두 번째 관문에 도착하셨군요. 거두 절미하고 말씀드리겠습니다. 두 번째 관문의 봉인은 두 번째 탑루에 있는 #r교도관 보어#k를 모두 잡아야 풀립니다.", 4);
            break;
        }
        case 13: {
            cm.sendNextPrevS("\r\n교도관 보어라... 꼭 멧돼지가 생각나는 이름이네요.", 16);
            break;
        }
        case 14: {
            cm.sendNextPrevS("\r\n맞습니다. 그 이름 그대로 성난 멧돼지처럼 난폭하고 무서운 몬스터입니다. 두 번째 탑루 역시 전에 도움을 받았던 열쇠공을 찾아가면 입장 할 수 있는 열쇠를 만들어 줄 테니 찾아가 보세요.", 4);
            break;
        }
        case 15: {
            cm.dispose();
            cm.forceStartQuest(3140);
            break;
        }
        case 16: {
            cm.dispose();
            break;
        }
        case 17: {
            cm.sendNextPrevS("\r\n네, 꼭 갈테니 기다려주세요.", 16);
            break;
        }
        case 18: {
            cm.sendNextPrevS("\r\n그럼 세 번째 봉인 너머에서 기다리고 있겠습니다. 부디 몸조심 하시길...", 4);
            break;
        }
        case 19: {
            cm.dispose();
            cm.gainItem(4032833, -1);
            cm.forceCompleteQuest(3140);
            break;
        }
        case 20: {
            cm.dispose();
            break;
        }
        case 21: {
            if (cm.isQuestFinished(3141) == true) {
                cm.dispose();
                if (cm.getPlayer().getTruePosition().y < -200 && cm.getPlayer().getTruePosition().y > -400) {
                    cm.warp(211060620, "in00");
                    return;
                }
                if (cm.getPlayer().getTruePosition().y < -100 && cm.getPlayer().getTruePosition().y > -200) {
                    cm.warp(211060610, "in00");
                    return;
                }
                cm.warp(211060700, "west00");
                return;
            }
            if (cm.isQuestActive(3167) == true) {
                status = 26;
                cm.sendNextS("\r\n루덴, 세 번째 탑루의 열쇠를 얻기 위해서는 #r베어 울프#k를 잡아야 하는데 보이질 않아요. 어떻게 하죠?", 16);
            }
            if (cm.getPlayer().getInfoQuest(3141).contains("clear=1")) {
                status = 29;
                cm.sendNextS("\r\n정말... 세 번째 봉인까지 모두 풀어내셨군요. 이제 그토록 오랫 동안 기다려왔던 제 충성의 맹세를 이어 나갈 수 있는 길이 열렸습니다.", 4);
            }
            cm.sendNextS("\r\n드디어 마지막 관문입니다. 세 번째 탑루를 지키는 #r교도관 라이노#k는 성을 배회하는 몬스터들 중 가장 난폭하고 무서운 놈들입니다.", 4);
            break;
        }
        case 22: {
            cm.sendNextPrevS("\r\n역시나 모두 잡고 나면 봉인이 풀리게 되는 건가요?", 16);
            break;
        }
        case 23: {
            cm.sendNextPrevS("\r\n네. 하지만 지금까지 잘해 오신 모험가님 이시라도 이번 만큼은 절대 긴장의 끈을 늦추면 안됩니다.", 4);
            break;
        }
        case 24: {
            cm.sendNextPrevS("\r\n걱정하지 마세요. 얼른 열쇠공 젠에게 열쇠를 받아 세 번째 봉인을 얼른 풀겠습니다.", 16);
            break;
        }
        case 25: {
            cm.dispose();
            cm.forceStartQuest(3141);
            break;
        }
        case 26: {
            cm.dispose();
            break;
        }
        case 27: {
            cm.sendNextPrevS("\r\n음... 베어 울프는 이 문 다음 맵인 #b성벽 아래 4#k에 자리를 튼 몬스터죠. 어쩔 수 없군요. 제가 잠시 나마 봉인의 힘을 약화 시켜 보겠습니다. 그 사이 모험가님께서 열쇠의 재료를 구해오시면 되겠죠.", 4);
            break;
        }
        case 28: {
            cm.dispose();
            cm.warp(211060700, "west00");
            break;
        }
        case 29: {
            cm.dispose();
            break;
        }
        case 30: {
            cm.sendNextPrevS("\r\n충성의 맹세라... 혹시 사자 왕에 대한 이야기인가요?", 16);
            break;
        }
        case 31: {
            cm.sendNextPrevS("\r\n... 저는 네 번째 탑에 있습니다. 이제 더 이상 모험가님의 길을 막아서는 봉인은 없으니 몬스터들을 주의하면서 찾아와 주세요. 그럼 직접 만나길 빌면서 이만...", 4);
            break;
        }
        case 32: {
            cm.dispose();
            cm.gainItem(4032834, -1);
            cm.forceCompleteQuest(3141);
            break;
        }
        case 33: {
            cm.dispose();
            break;
        }
        case 34: {
            var say = "이 곳은 #Cgreen#아니의 수감실#k로 가는 길 입니다.";
            say += "\r\n#L0##Cyellow#1. 교도관 아니 타도 (파티(1~3인)만 입장 가능)";
            cm.sendSimple(say);
            break;
        }
        case 35: {
            cm.dispose();
            if (cm.getParty() == null || cm.isLeader() == false) {
                cm.sendNext("\r\n파티장만이 입장을 신청 할 수 있습니다.");
                return;
            }
            if (cm.allMembersHere() == false) {
                cm.sendNext("\r\n모든 파티원이 모여야 입장을 진행 할 수 있습니다.");
                return;
            }
            if (cm.getPlayerCount(211061100) != 0) {
                cm.sendNext("\r\n현재 접속 한 채널에서는 다른 파티가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            var pMember = cm.getParty().getMembers();
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser.getOneInfoQuest(value, "ani_enter").equals("3") && ccUser.getOneInfoQuest(value, "ani_date").equals(date)) {
                        cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 아니의 수감실은 #b1일 3회#k 입장이 가능합니다.");
                        return;
                    }
                    if (ccUser.getOneInfoQuest(value, "ani_date").equals(date) == false) {
                        ccUser.updateOneInfoQuest(value, "ani_enter", "1");
                        ccUser.updateOneInfoQuest(value, "ani_date", date);
                    } else {
                        var v5 = parseInt(ccUser.getOneInfoQuest(value, "ani_enter"));
                        ccUser.updateOneInfoQuest(value, "ani_enter", (v5 + 1) + "");
                    }
                }
            }
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser != null) {
                        var v5 = parseInt(ccUser.getOneInfoQuest(value, "ani_enter"));
                        ccUser.dropMessage(5, "아니의 수감실로 입장합니다. (" + v5 + "/3)");
                    }
                }
            }
            cm.resetMap(211061100);
            cm.warpParty(211061100, "west00");
            break;
        }
    }
}