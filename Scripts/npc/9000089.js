var status = -1;

var value = 10200000;

var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

var nList = [
[00, "헤네시스", 100000000],
[01, "엘리니아", 101000000],
[02, "페리온", 102000000],
[03, "커닝시티", 103000000],
[04, "리스 항구", 104000000],
[05, "슬리피우드", 105000000],
[06, "노틸러스 선착장", 120000000],
[07, "에레브", 130000000],
[08, "리엔", 140000000],
[09, "오르비스", 200000000],
[10, "엘나스", 211000000],
[11, "루디브리엄", 220000000],
[12, "지구 방위 본부", 221000000],
[13, "아랫 마을", 222000000],
[14, "아쿠아리움", 230000000],
[15, "리프레", 240000000],
[16, "무릉", 250000000],
[17, "백초 마을", 251000000],
[18, "아리안트", 260000000],
[19, "마가티아", 261000000],
[20, "에델슈타인", 310000000],
];

var dList = [
[10, 100020000, "#Cyellow#(Lv.010) #Cgreen#포자 언덕"],
[20, 100040000, "#Cyellow#(Lv.020) #Cgreen#골렘의 사원 입구"],
[40, 103040303, "#Cyellow#(Lv.045) #Cgreen#5층 6층 D 구역"],
[55, 102030000, "#Cyellow#(Lv.055) #Cgreen#와일드 보어의 땅"],
[57, 102030200, "#Cyellow#(Lv.057) #Cgreen#철갑 돼지의 땅"],
[62, 102040301, "#Cyellow#(Lv.062) #Cgreen#제 1군영"],
[65, 310050800, "#Cyellow#(Lv.065) #Cgreen#갱도 <4>"],
[66, 105010000, "#Cyellow#(Lv.066) #Cgreen#조용한 습지"],
[66, 105010100, "#Cyellow#(Lv.066) #Cgreen#축축한 습지"],
[70, 200010000, "#Cyellow#(Lv.070) #Cgreen#구름 공원 <1>"],
[71, 200010200, "#Cyellow#(Lv.071) #Cgreen#하늘 계단 <1>"],
[81, 211040200, "#Cyellow#(Lv.081) #Cgreen#얼음 골짜기 <2>"],
[86, 260010700, "#Cyellow#(Lv.086) #Cgreen#아리안트 동문 밖"],
[89, 260020700, "#Cyellow#(Lv.089) #Cgreen#사헬 지대 <1>"],
[90, 541010010, "#Cyellow#(Lv.090) #Cgreen#유령선 <2>"],
[103, 240010000, "#Cyellow#(Lv.103) #Cgreen#리프레 서쪽 숲"],
[104, 240010200, "#Cyellow#(Lv.104) #Cgreen#심술쟁이의 숲"],
[106, 230040400, "#Cyellow#(Lv.106) #Cgreen#난파선의 무덤"],
[107, 240010600, "#Cyellow#(Lv.107) #Cgreen#[★] 하늘 둥지 <2>"],
[113, 220020300, "#Cyellow#(Lv.113) #Cgreen#장난감 공장 <메인 공정 - 1>"],
[113, 220020600, "#Cyellow#(Lv.113) #Cgreen#장난감 공장 <기계실>"],
[118, 211060300, "#Cyellow#(Lv.118) #Cgreen#성벽 아래 <2>"],
[120, 220060400, "#Cyellow#(Lv.120) #Cgreen#[★] 뒤틀링 회랑"],
[121, 211040600, "#Cyellow#(Lv.121) #Cgreen#날카로운 절벽 <4>"],
[122, 220070400, "#Cyellow#(Lv.122) #Cgreen#[★] 잊혀진 회랑"],
[128, 240030100, "#Cyellow#(Lv.128) #Cgreen#용의 숲 <1>"],
[130, 222010000, "#Cyellow#(Lv.130) #Cgreen#까막산 입구"],
[130, 270010100, "#Cyellow#(Lv.130) #Cgreen#추억의 길 <1>"],
[130, 270020100, "#Cyellow#(Lv.130) #Cgreen#후회의 길 <1>"],
[130, 270030100, "#Cyellow#(Lv.130) #Cgreen#망각의 길 <1>"],
[134, 211042100, "#Cyellow#(Lv.134) #Cgreen#[★] 시련의 동굴 <2>"],
[136, 250020300, "#Cyellow#(Lv.136) #Cgreen#상급 수련장"],
[138, 222010401, "#Cyellow#(Lv.138) #Cgreen#깊은 산 흉가"],
[141, 240040400, "#Cyellow#(Lv.141) #Cgreen#와이번의 협곡"],
[147, 103041110, "#Cyellow#(Lv.147) #Cgreen#2층 카페 <1>"],
[150, 240040521, "#Cyellow#(Lv.150) #Cgreen#위험한 용의 둥지"],
[155, 103041119, "#Cyellow#(Lv.155) #Cgreen#[★] 2층 카페 <4>"],
[157, 103041120, "#Cyellow#(Lv.157) #Cgreen#[★] 3층 팬시샵 <1>"],
[161, 103041149, "#Cyellow#(Lv.161) #Cgreen#[★] 5층 화장품 매장 <4>"],
[164, 271020000, "#Cyellow#(Lv.164) #Cgreen#어두운 포자 언덕"],
[165, 221030200, "#Cyellow#(Lv.165) #Cgreen#로스웰 초원 <2>"],
[168, 271030102, "#Cyellow#(Lv.168) #Cgreen#[★] 제 2 연무장"],
/*
[170, 241000215, "#Cyellow#(Lv.170) #Cgreen#시작되는 비극의 숲 <1>"],
[176, 241000205, "#Cyellow#(Lv.176) #Cgreen#깊어지는 비극의 숲 <1>"],
[180, 241000201, "#Cyellow#(Lv.180) #Cgreen#깊어지는 비극의 숲 <5>"],
[182, 241000225, "#Cyellow#(Lv.182) #Cgreen#끝나지 않는 비극의 숲 <1>"],
[187, 241000226, "#Cyellow#(Lv.187) #Cgreen#타락한 마력의 숲 <3>"],
*/
[190, 273010000, "#Cyellow#(Lv.190) #Cgreen#인적이 끊긴 남쪽령"],
[195, 273040100, "#Cyellow#(Lv.195) #Cgreen#버려진 발굴 지역 <2>"],
[196, 273060100, "#Cyellow#(Lv.196) #Cgreen#거친 황야"],
[198, 102040500, "#Cyellow#(Lv.198) #Cgreen#[★] 폐쇄 지역"],
[200, 310070210, "#Cyellow#(Lv.200) #Cgreen#스카이 라인 <1>"],
[204, 310070130, "#Cyellow#(Lv.204) #Cgreen#기계 무덤 언덕 <3>"],
[204, 450001012, "#Cyellow#(Lv.201) #Cgreen#풍화된 분노의 땅"],
[205, 450001112, "#Cyellow#(Lv.205) #Cgreen#화염의 영토"],
[207, 450001216, "#Cyellow#(Lv.207) #Cgreen#동굴 아래 쪽"],
[207, 450001210, "#Cyellow#(Lv.207) #Cgreen#세 갈래 길 <1>"],
[208, 450014160, "#Cyellow#(Lv.208) #Cgreen#지하 열차 <3>"],
[209, 450014210, "#Cyellow#(Lv.209) #Cgreen#M 타워 <2>"],
[209, 450001260, "#Cyellow#(Lv.209) #Cgreen#숨겨진 호숫가"],
[210, 450014300, "#Cyellow#(Lv.210) #Cgreen#숨겨진 연구 열차"],
[210, 450002001, "#Cyellow#(Lv.210) #Cgreen#동산 입구"],
[211, 450002002, "#Cyellow#(Lv.211) #Cgreen#알록 달록 숲 지대 <1>"],
[212, 450002007, "#Cyellow#(Lv.212) #Cgreen#길쭉 동글 숲 <2>"],
[213, 450002010, "#Cyellow#(Lv.213) #Cgreen#츄릅 포레스트 깊은 곳"],
[215, 450002014, "#Cyellow#(Lv.215) #Cgreen#격류 지대 <3>"],
[216, 450015090, "#Cyellow#(Lv.216) #Cgreen#머쉬 버드 숲 <6>"],
[217, 450002016, "#Cyellow#(Lv.217) #Cgreen#고래산 초입"],
[217, 450015150, "#Cyellow#(Lv.217) #Cgreen#일리야드 들판 <4>"],
[219, 450015260, "#Cyellow#(Lv.219) #Cgreen#펑고스 숲 <6>"],
[220, 450003200, "#Cyellow#(Lv.220) #Cgreen#무법자들의 거리 <1>"],
[221, 450003320, "#Cyellow#(Lv.221) #Cgreen#닭이 뛰노는 곳 <3>"],
[224, 450003410, "#Cyellow#(Lv.224) #Cgreen#본색을 드러내는 곳 <2>"],
[225, 450003450, "#Cyellow#(Lv.225) #Cgreen#춤추는 구두 점령지 <2>"],
[232, 450005130, "#Cyellow#(Lv.232) #Cgreen#흙의 숲"],
[234, 450005220, "#Cyellow#(Lv.234) #Cgreen#번개 구름의 숲"],
[237, 450005430, "#Cyellow#(Lv.237) #Cgreen#동굴 아랫 길"],
[238, 450005500, "#Cyellow#(Lv.238) #Cgreen#다섯 갈래 동굴"],
[238, 450006140, "#Cyellow#(Lv.238) #Cgreen#형님들 구역"],
[239, 450006210, "#Cyellow#(Lv.239) #Cgreen#그림자가 춤추는 곳 <2>"],
[241, 450006310, "#Cyellow#(Lv.241) #Cgreen#폐쇄 구역 <2>"],
[243, 450006420, "#Cyellow#(Lv.243) #Cgreen#그날의 트뤼에페 <3>"],
[242, 450007050, "#Cyellow#(Lv.242) #Cgreen#생명이 시작되는 곳 <5>"],
[244, 450007110, "#Cyellow#(Lv.244) #Cgreen#거울 빛에 물든 바다 <2>"],
[248, 450007220, "#Cyellow#(Lv.248) #Cgreen#거울에 비친 빛의 신전 <3>"],
[245, 450016010, "#Cyellow#(Lv.245) #Cgreen#빛이 마지막으로 닿는 곳 <1>"],
[246, 450016060, "#Cyellow#(Lv.246) #Cgreen#빛이 마지막으로 닿는 곳 <6>"],
[248, 450016130, "#Cyellow#(Lv.248) #Cgreen#끝 없이 추락하는 심해 <3>"],
[250, 450016210, "#Cyellow#(Lv.250) #Cgreen#별이 삼켜진 심해 <1>"],
[250, 450009160, "#Cyellow#(Lv.250) #Cgreen#사상의 경계 <6>"],
[251, 450009120, "#Cyellow#(Lv.251) #Cgreen#사상의 경계 <2>"],
[253, 450009250, "#Cyellow#(Lv.253) #Cgreen#미지의 안개 <5>"],
[254, 450009310, "#Cyellow#(Lv.254) #Cgreen#공허의 파도 <1>"],
[255, 450011420, "#Cyellow#(Lv.255) #Cgreen#고통의 미궁 내부 <1>"],
[255, 450011560, "#Cyellow#(Lv.258) #Cgreen#고통의 미궁 중심부 <6>"],
[255, 450011600, "#Cyellow#(Lv.259) #Cgreen#고통의 미궁 최상부 <1>"],
[255, 450012020, "#Cyellow#(Lv.260) #Cgreen#세계의 눈물 하단 <1>"],
[255, 450012330, "#Cyellow#(Lv.262) #Cgreen#세계가 끝나는 곳 <1-4>"],
[255, 450012420, "#Cyellow#(Lv.263) #Cgreen#세계가 끝나는 곳 <2-3>"],
[255, 450012440, "#Cyellow#(Lv.264) #Cgreen#세계가 끝나는 곳 <2-5>"],
];

var pList = [
[00, 7921, "루디브리엄 메이즈", "pq_maze_date", 221023300],
[01, 7216, "무릉 도장", "pq_dojang_date", 925020000],
[05, 7242, "네트의 피라미드", "pq_ariant_date", 926010000],
[08, 7923, "황금 사원의 라바나", "pq_ravana_date", 252030000],
[09, 7910, "월묘의 떡", "pq_henesys_date", 910010500],
[10, 7912, "첫 번째 동행", "pq_kerningcity_date", 910340700],
[12, 7916, "독 안개의 숲", "pq_elin_date", 300030100],
[14, 7044, "해적 데비존", "pq_pirate_date", 251010404],
[15, 7922, "로미오와 줄리엣", "pq_romeo_julliet_date", 261000021],
[17, 7814, "드래곤 라이더", "pq_dragonrider_date", 240080000],
[21, 7926, "위험에 빠진 켄타", "pq_kenta_date", 923040000],
[22, 7928, "탈출", "pq_escape_date", 921160000],
];

function start() {
    action (1, 0, 0);
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
            var say = "　#Cgray#각종 마을과 던전, 몬스터 파크로 이동을 할\r\n　수 있습니다.";
            say += "\r\n#L0##Cgreen#마을로 이동한다.";
            say += "\r\n#L1##Cgreen#던전으로 이동한다.";	    
            say += "\r\n\r\n#L3##Cgreen#몬스터 파크로 이동한다.";            
            say += "\r\n\r\n#L4##Cyellow#광장으로 이동한다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            a1 = selection;
            var nType = 0;
            var say = "";
            switch (selection) {
                case 0: {
                    nType = 3;
                    for (i = 0; i < nList.length; i++) {
                        say += "#" + nList[i][0] + "#" + nList[i][1];
                    }
                    break;
                }
                case 1: {
                    var nLV = cm.getPlayer().getLevel();
                    say += "　#Cgray#Lv." + nLV + " 레벨의 적정 던전은 아래와 같습니다.#k";
                    for (i = 0; i < dList.length; i++) {
                        var pLV = dList[i][0];
                        if (nLV < pLV) {
                            continue;
                        }
                        if (Math.abs(nLV - pLV) > 15) {
                            continue;
                        }
                        if (nLV == 255 && pLV != 255) {
                            continue;
                        }
                        say += "\r\n#L" + dList[i][1] + "#" + dList[i][2];
                    }
                    say += "\r\n\r\n#L0##r이동 가능 한 전체 던전을 확인한다.#k";
                    cm.sendSimple(say);
                    return;
                }
                case 2: {
                    nType = 0;
                    for (i = 0; i < pList.length; i++) {
                        if (!cm.getPlayer().getOneInfoQuest(value, pList[i][3]).equals(date)) {
                            cm.getPlayer().updateQuest(pList[i][1], "0");
                        }
                        say += "#" + pList[i][0] + "#" + pList[i][2];
                    }
                    break;
                }
                case 3: {
                    cm.sendYesNo("사랑합니다 고객님. 항상 새로운 즐거움이 가득한 슈피겔만의 몬스터 파크로 이동하시겠습니까?", 9071003);
                    return;
                }
                case 4: {
                    cm.warp(910000000); // 바로 광장으로 이동
    cm.dispose();       // 대화 종료
    return;
                }
            }
            cm.askMapSelection(say, nType);
            break;
        }
        case 2: {
            if (a1 == 1 && selection == 0) {
                //
            } else {
                cm.dispose();
            }
            var mapID = 100000000;
            switch (a1) {
                case 0: {
                    mapID = nList[selection][2];
                    break;
                }
                case 1: {
                    if (selection == 0) {
                        var nLV = cm.getPlayer().getLevel();
                        var say = "　#Cgray#이동 가능 한 전체 던전은 아래와 같습니다.#k";
                        for (i = 0; i < dList.length; i++) {
                            var pLV = dList[i][0];
                            if (nLV < pLV) {
                                continue;
                            }
                            say += "\r\n#L" + dList[i][1] + "#" + dList[i][2];
                        }
                        cm.sendSimple(say);
                        return;
                    }
                    mapID = selection;
                    break;
                }
                case 2: {
                    switch (selection) {
                        case 0: {
                            selection = 0;
                            break;
                        }
                        case 1: {
                            selection = 1;
                            break;
                        }
                        case 5: {
                            selection = 2;
                            break;
                        }
                        case 8: {
                            selection = 3;
                            break;
                        }
                        case 9: {
                            selection = 4;
                            break;
                        }
                        case 10: {
                            selection = 5;
                            break;
                        }
                        case 12: {
                            selection = 6;
                            break;
                        }
                        case 14: {
                            selection = 7;
                            break;
                        }
                        case 15: {
                            selection = 8;
                            break;
                        }
                        case 17: {
                            selection = 9;
                            break;
                        }
                        case 21: {
                            selection = 10;
                            break;
                        }
                        case 22: {
                            selection = 11;
                            break;
                        }
                    }
                    mapID = pList[selection][4];
                    break;
                }
                case 3: {
                    mapID = 951000000;
                    break;
                }
                case 4: {
                    mapID = 910000000;
                    break;
                }
            }
            cm.warp(mapID, 0);
            var nMap = cm.getPlayer().getClient().getChannelServer().getMapFactory().getMap(mapID);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.packet.CWvsContext.serverNotice(6, "[" + Packages.constants.ServerConstants.server_Name_Source + "] " + cm.getPlayer().getName() + "님께서 [" + nMap.getStreetName() + " : " + nMap.getMapName() + "] 던전으로 이동하였습니다."));
            break;
        }
        case 3: {
            cm.dispose();
            cm.warp(selection, 0);
            var nMap = cm.getPlayer().getClient().getChannelServer().getMapFactory().getMap(selection);
            Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.packet.CWvsContext.serverNotice(6, "[" + Packages.constants.ServerConstants.server_Name_Source + "] (Lv." + cm.getPlayer().getLevel() + ") " + cm.getPlayer().getName() + "님께서 [" + nMap.getStreetName() + " : " + nMap.getMapName() + "] 던전으로 이동하였습니다."));
            break;
        }
    }
}