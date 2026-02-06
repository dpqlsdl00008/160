var status = -1;

var dropData = new Array();
var mobName = [
[8800002, "(노멀) 자쿰", 3], 
[8810018, "(노멀) 혼테일", 3], 
[8820001, "(노멀) 핑크빈", 5], 
[8840000, "(노멀) 반 레온", 5], 
[8870000, "(노멀) 힐라", 5], 
[8860000, "(노멀) 아카이럼", 5], 
[8850011, "(노멀) 시그너스", 5], 
[8800102, "(카오스) 자쿰", 5], 
[8810118, "(카오스) 혼테일", 5], 
];

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        qm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            var say = "#Cgreen#아이템 획득 및 확률#k에 대한 정보를 알고 싶은 #Cyellow#몬스터 타입#k을 선택해 주세요.";
            say += "\r\n#L0##Cgreen#일반 몬스터#d를 확인한다.#k";
            say += "\r\n#L1##Cgreen#보스 몬스터#d를 확인한다.#k";
            qm.sendSimple(say);
            break;
        }
        case 1: {
            s1 = selection;
            switch (selection) {
                case 0: {
                    var say = "#Cgreen#리첸시아#k의 #Cyellow#아이템 획득 확률과 조건#k은 아래와 같습니다. 조건을 충족 시에 구분 없이 리첸시아 내 #Cyellow#모든 몬스터#k에게 #r동일한 확률#k로 획득 할 수 있습니다.\r\n\r\n";
                    say += "#Cgreen#전리품 (기타)#k : #Cyellow#60%#k\r\n\r\n#Cgreen#전리품 (퀘스트)#k : #Cyellow#5%#k\r\n\r\n#Cgreen#전리품 (전문 기술)#k : #Cyellow#1%#k\r\n";
                    say += "\r\n#Cgreen#포션#k : #Cyellow#2%#k\r\n\r\n#Cgreen#표창 및 불릿#k : #Cyellow#0.2%#k\r\n\r\n#Cgreen#주문서#k : #Cyellow#0.4%#k\r\n#Cgray#- 획득 조건 : 레벨 차이 : ±20 / 몬스터 140 레벨 이상#k\r\n\r\n#Cgreen#마스터리 북#k : #Cyellow#0.1%#k\r\n#Cgray#- 획득 조건 : 몬스터 100 레벨 이상#k\r\n";
                    say += "\r\n#Cgreen#무기 및 방어구#k : #Cyellow#0.3%#k\r\n";
                    say += "\r\n#Cgreen#아이스 박스#k : #Cyellow#3%#k\r\n#Cgray#- 획득 조건 : 레벨 차이 : ±20 / 몬스터 140 레벨 이상#k\r\n\r\n#Cgreen#수상한 큐브#k : #Cyellow#2%#k\r\n#Cgray#- 획득 조건 : 레벨 차이 : ±20 / 몬스터 140 레벨 이상#k\r\n\r\n#Cgreen#미라클 큐브#k : #Cyellow#0.3%#k\r\n#Cgray#- 획득 조건 : 레벨 차이 : ±20 / 몬스터 140 레벨 이상#k\r\n\r\n#Cgreen#리첸시아 리프#k : #Cyellow#1%\r\n#Cgray#- 획득 조건 : 레벨 차이 : ±20 / 몬스터 140 레벨 이상#k";
                    qm.sendNext("\r\n" + say);
                    break;
                }
                case 1: {
                    dropData = new Array();
                    var say = "#Cgreen#아이템 획득 및 확률#k에 대한 정보를 알고 싶은 #Cyellow#몬스터 이름#k을 선택해 주세요.";
                    for (i = 0; i < mobName.length; i++) {
                        say += "\r\n#L" + i + "##d" + (i + 1) + ". " + mobName[i][1];
                    }
                    qm.sendSimple(say);
                    break;
                }
            }
            break;
        }
        case 2: {
            switch (s1) {
                case 0: {
                    status = -1;
                    start(1, 0, 0);
                    break;
                }
                case 1: {
                    var say = "선택하신 #Cgreen#" + mobName[selection][1] + "#k의 #Cyellow#아이템 획득 및 확률#k은 아래와 같으며,";
                    for (i = 0; i < Packages.constants.drop.DropBoss.drop_boss.length; i++) {
                        if (mobName[selection][0] != Packages.constants.drop.DropBoss.drop_boss[i][1]) {
                            continue;
                        }
                        dropData.push(Packages.constants.drop.DropBoss.drop_boss[i][0]);
                    }
                    say += " #Cyellow#100% 획득 가능 한 아이템을 제외#k 한 #r총 " + mobName[selection][2] + "개#k의 아이템을 추가적으로 획득 할 수 있습니다.\r\n";
                    say += "\r\n#fUI/UIWindow.img/Quest/reward#";
                    switch (mobName[selection][0]) {
                        case 8800002: {
                            say += "\r\n#d(Lv. 50) #z1002357# #Cgray#[100%]#k";
                            say += "\r\n#d(Lv. 10) #z1372049# #Cgray#[100%]#k";
                            break;
                        }
                        case 8800102: {
                            say += "\r\n#d(Lv. 100) #z1003112# #Cgray#[100%]#k";
                            say += "\r\n#d(Lv. 50) #z1372073# #Cgray#[100%]#k";
                            break;
                        }
                        case 8810018: {
                            say += "\r\n#d(Lv. 0) #z2041200# #Cgray#[100%]#k";
                            say += "\r\n#d(Lv. 120) #z1122000# #Cgray#[100%]#k";
                            break;
                        }
                        case 8810118: {
                            say += "\r\n#d(Lv. 0) #z2041200# #Cgray#[100%]#k";
                            say += "\r\n#d(Lv. 120) #z1122076# #Cgray#[100%]#k";
                            break;
                        }
                        case 8840000: {
                            say += "\r\n#d(Lv. 0) #z2430158# #Cgray#[100%]#k";
                            say += "\r\n#d(Lv. 0) #z4310009# / #z4310010# #Cgray#[100%]#k";
                            break;
                        }
                        case 8870000: {
                            say += "\r\n#d(Lv. 0) #z4310036# #Cgray#[100%]#k";
                            break;
                        }
                    }
                    for (j = 0; j < dropData.length; j++) {
                        var reqLevel = Packages.server.MapleItemInformationProvider.getInstance().getReqLevel(dropData[j]);
                        var dropRate = (100 / dropData.length);
                        say += "\r\n#d(Lv." + reqLevel + ") #z" + dropData[j] + "##k";
                    }
                    qm.sendNext("\r\n" + say);
                    break;
                }
            }
            break;
        }
        case 3: {
            status = -1;
            start(1, 0, 0);
            break;
        }
    }
}