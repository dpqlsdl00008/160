var status = -1;

var value = 20230000;
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
            if (cm.getExpedition() == null) {
                cm.dispose();
                cm.sendNext("\r\n#e#b1인 이상 원정대#k#n를 맺어야만 입장 할 수 있습니다.");
                return;
            }
            var say = "#e<발록 : 노멀 모드>#n\r\n\r\n이 너머에는 발록이 봉인되어 있어. 하지만 최근 발록의 봉인이 불안정해 졌어. 가능 한 빨리 발록의 봉인을 강화시켜야 해.\r\n\r\n#r(발록의 무덤에는 #e하루에 2회 입장#n 할 수 있으며 입장 기록은 #e매일 자정에 초기화#n됩니다.)";
            if (cm.isLeader_Expedition() == false) {
                cm.dispose();
                cm.sendNext("\r\n" + say);
                return;
            }
            say += "\r\n\r\n#L0##b<보스 : 발록> 입장을 신청한다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getPlayerCount(105100300) != 0) {
                cm.sendNext("\r\n현재 접속한 채널에서는 다른 원정대가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            if (cm.isType_Expedition(2000) == false) {
                cm.sendNext("\r\n#e#b발록 원정대 : 노멀 모드#k#n만이 입장을 신청 할 수 있습니다.");
                return;
            }
            if (cm.allMembersHere_Expedition() == false) {
                cm.sendNext("\r\n#e#b발록 원정대 : 노멀 모드#k#n의 모든 원정대원이 모여야 입장을 진행 할 수 있습니다.");
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "balrog_enter").equals("2") && cm.getPlayer().getOneInfoQuest(value, "balrog_date").equals(date)) {
                cm.sendNext("\r\n오늘 하루 2번이나 입장했었네? 그렇다면 오늘은 더 이상 들어갈 수 없어. 내일 다시 시도해줘.");
                return;
            }
            var expedition = cm.getExpedition();
            if (expedition != null) {
                for (i = 0; i < 6; i++) {
                    if (i < expedition.getParties().size()) {
                        var party = Packages.handling.world.World.Party.getParty(expedition.getParties().get(i));
                        if (party != null) {
                            var pMember = party.getMembers();
                            if (pMember != null) {
                                var it = pMember.iterator();
                                while (it.hasNext()) {
                                    var cUser = it.next();
                                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                                    if (ccUser != null) {
                                        if (ccUser.getOneInfoQuest(value, "balrog_enter").equals("2") && ccUser.getOneInfoQuest(value, "balrog_date").equals(date)) {
                                            cm.sendNext("\r\n원정대원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 발록의 무덤은 #b1일 2회#k 입장이 가능합니다.");
                                            return;
                                        }
                                        var eScript = cm.getEventManager("expedition_balrog");
                                        if (eScript == null) {
                                            cm.getPlayer().dropMessage(1, "error");
                                            return;
                                        }
                                        if (ccUser.getOneInfoQuest(value, "balrog_date").equals(date) == false) {
                                            ccUser.updateOneInfoQuest(value, "balrog_enter", "1");
                                            ccUser.updateOneInfoQuest(value, "balrog_date", date);
                                        } else {
                                            var v5 = parseInt(ccUser.getOneInfoQuest(value, "balrog_enter"));
                                            ccUser.updateOneInfoQuest(value, "balrog_enter", (v5 + 1) + "");
                                        }
                                        cm.getMap(105100300).resetFully();
                                        eScript.setProperty("map", "" + 105100300);
                                        eScript.startInstance_Expedition("" + 105100300, cm.getPlayer());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            break;
        }
    }
}