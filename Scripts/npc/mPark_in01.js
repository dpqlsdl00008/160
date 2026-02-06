var status = -1;

var value = 20000000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

var min = 120;
var max = 162;
var itemID = 4001516;

var mapname = [
["자동 경비 구역(Lv.120~126)", 953020000], 
["이끼나무 숲(Lv.128~137)", 953030000], 
["하늘 숲 수련장(Lv.132~142)", 953040000], 
["금지된 시간(Lv.142~152)", 953050000], 
["폐허가 된 도시(Lv.153~162)", 954000000], 
];

var nowDay = [
[1, "창조의 월요일"], 
[2, "강화의 화요일"], 
[3, "성향의 수요일"], 
[4, "명예의 목요일"], 
[5, "황금의 금요일"], 
[6, "축제의 토요일"], 
[0, "성장의 일요일"], 
];

function start() {
    if (new Date().getHours() == 11 && new Date().getMinutes() > 40) {
        cm.sendYesNo("#e#b현재 시간 오후 " + new Date().getHours() + "시 " + new Date().getMinutes() + "분...#k#n\r\n몬스터 파크 안에서 날짜가 바뀔 수 있는 애매한 시간이군요!\r\n\r\n몬스터 파크 안에서 날짜가 바뀌어 버리면 #e#b퇴장 시 날짜와 요일#k#n을 기준으로 보상 지급 및 클리어 횟수 이용이 이루어집니다.\r\n\r\n이 점을 숙지하시고 몬스터 파크에 입장 하시겠습니까?");
    } else {
        action(1, 0, 0);
    }
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            if (cm.getPlayer().getOneInfoQuest(value, "mPark_date").equals(date) == false) {
                cm.getPlayer().updateOneInfoQuest(value, "mPark_enter", "0");
            }
            for (i = 0; i < nowDay.length; i++) {
                if (new Date().getDay() == nowDay[i][0]) {
                    var say = "#e- 오늘은 #d" + nowDay[i][1] + "#k입니다.#n";
                }
            }
            say += "\r\n#e- 오늘 입장 횟수 : #d" + cm.getPlayer().getOneInfoQuest(value, "mPark_enter") + " / 10회#k#n";
            say += "\r\n#e- 나의 몬스터 파크 기념 주화 : #d" + cm.getPlayer().getNum(cm.itemQuantity(4310020)) + "개#k#n";
            for (i = 0; i < mapname.length; i++) {
                say += "#d\r\n#L" + i + "#" + (i + 1) + ". " + mapname[i][0];
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            for (i = 0; i < nowDay.length; i++) {
                if (new Date().getDay() == nowDay[i][0]) {
                    var say = "#e<오늘은 #d" + nowDay[i][1] + "#k입니다>#n";
                }
            }
            say += "\r\n\r\n#e선택 던전 : #d" + mapname[selection][0] + "#k#n";
            say += "\r\n#e던전 입장 시 오늘의 #d입장 횟수 1회#k 사용#n";
            say += "\r\n\r\n#e던전에 입장 하시겠습니까?#n";
            cm.sendYesNo(say);
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getParty() == null || cm.isLeader() == false) {
                cm.sendNext("\r\n입장하시려는 지역은 파티 플레이 존입니다. #d파티장#k을 통해 입장을 진행하실 수 있습니다.");
                return;
            }
            if (cm.allMembersHere() == false) {
                cm.sendNext("\r\n몬스터 파크 내의 시설물을 이용하시려면 #d모든 파티원#k이 이곳에 모여 있어야 합니다.");
                return;
            }
            for (i = 0; i <= 500; i+=100) {
                if (cm.getPlayerCount(mapname[v1][1]) != 0) {
                    cm.sendNext("\r\n이미 다른 파티가 몬스터 파크 시설물을 이용 중에 있습니다.");
                    return;
                }
            }
            var pMember = cm.getParty().getMembers();
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser != null) {
                        if (ccUser.getLevel() < min || ccUser.getLevel() > max) {
                            cm.sendNext("\r\n파티원 중 레벨이 맞지 않는 파티원이 있습니다.\r\n#r" + min + "레벨 이상 " + (max + 1) + "레벨 미만#k의 파티원만 입장 할 수 있습니다.");
                            return;
                        }
                        if (ccUser.haveItem(itemID, 1) == false) {
                            cm.sendNext("\r\n몬스터 파크 내의 시설물을 이용하시려면 #r모든 파티원#k이 #b#z" + itemID + "##k을 소지하고 있어야 합니다.");
                            return;
                        }
                        if (ccUser.getOneInfoQuest(value, "mPark_enter").equals("10") && ccUser.getOneInfoQuest(value, "mPark_date").equals(date)) {
                            cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 몬스터 파크는 #b1일 10회#k 도전이 가능합니다.");
                            return;
                        }
                        if (ccUser.getOneInfoQuest(value, "mPark_date").equals(date) == false) {
                            ccUser.updateOneInfoQuest(value, "mPark_enter", "1");
                            ccUser.updateOneInfoQuest(value, "mPark_date", date);
                        } else {
                            var a1 = parseInt(ccUser.getOneInfoQuest(value, "mPark_enter"));
                            ccUser.updateOneInfoQuest(value, "mPark_enter", (a1 + 1) + "");
                        }
                    }
                }
            }
            cm.givePartyItems(itemID, -1);
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser != null) {
                        var a1 = parseInt(ccUser.getOneInfoQuest(value, "mPark_enter"));
                        ccUser.dropMessage(5, "몬스터 파크 시설물을 이용 한 횟수는 총 " + a1 + "회입니다. 앞으로 " + (10 - a1) + "회 더 입장 할 수 있습니다.");
                    }
                }
            }
            for (i = 0; i <= 500; i+=100) {
                cm.resetMap(mapname[v1][1] + i);
            }
            cm.warpParty(mapname[v1][1]);
            break;
        }
    }
}