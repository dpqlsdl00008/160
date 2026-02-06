var status = -1;
var aList = false;
var nEthernell = [
1302152, // 라이온 하트 커틀러스
1312065, // 라이온 하트 챔피온 엑스
1322096, // 라이온 하트 배틀 해머
1402095, // 라이온 하트 배틀 시미터
1412065, // 라이온 하트 배틀 엑스
1422066, // 라이온 하트 기간틱 모울
1432086, // 라이온 하트 푸스키나
1442116, // 라이온 하트 파르티잔
1542015, // 라이온 하트 사자왕환
1372084, // 드래곤 테일 아크 완드
1382104, // 드래곤 테일 워 스태프
1552015, // 드래곤 테일 사자왕선
1252014, // 키튼 테일 세이지 스틱
1452111, // 팔콘 윙 컴포지트 보우
1462099, // 팔콘 윙 헤비 크로스 보우
1522018, // 팔콘 윙 그란 듀얼 보우건
1472122, // 레이븐 혼 메탈 피스트
1332130, // 레이븐 혼 바젤라드
1362019, // 레이븐 혼 크림슨 케인
1342036, // 레이븐 혼 섀도우 블레이드
1482084, // 샤크투스 와일드 탈론
1492085, // 샤크투스 샤프 슈터
1532018, // 샤크투스 플람
1403015, // 샤크투스 무권

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
            if (cm.itemQuantity(2434588) < 10) {
                cm.dispose();
                return;
            }
            var say = "　 #Cgray#현재 직업이 착용 가능 한 장비를 우선 추천해 드립니다.\r\n　 받으실 방어구를 선택해 주세요.";
            ii = Packages.server.MapleItemInformationProvider.getInstance();
            v1 = false;
            v2 = false;
            v3 = false;
            v4 = false;
            v5 = false;
            switch (cm.getPlayer().getJob()) {
                case 112: case 122: case 132: case 1112: case 2112: case 3112: case 4112: case 5112: case 6112: {
                    v1 = true;
                    break;
                }
                case 212: case 222: case 232: case 1212: case 2218: case 3212: case 4212: case 5212: case 6212: {
                    v2 = true;
                    break;
                }
                case 312: case 322: case 1312: case 2312: case 3312: {
                    v3 = true;
                    break;
                }
                case 412: case 422: case 434: case 1412: case 2412: {
                    v4 = true;
                    break;
                }
                case 512: case 522: case 1512: case 3512:  case 6512: {
                    v5 = true;
                    break;
                }
            }
            for (i = 0; i < nEthernell.length; i++) {
               if (!aList && v1 && !ii.getName(nEthernell[i]).contains("라이온하트")) {
                   continue;
               }
               if (!aList && v2 && !ii.getName(nEthernell[i]).contains("드래곤테일")) {
                   continue;
               }
               if (!aList && v3 && !ii.getName(nEthernell[i]).contains("팔콘윙")) {
                   continue;
               }
               if (!aList && v4 && !ii.getName(nEthernell[i]).contains("레이븐혼")) {
                   continue;
               }
               if (!aList && v5 && !ii.getName(nEthernell[i]).contains("샤크투스")) {
                   continue;
               }
               say += "\r\n#L" + i + "##i" + nEthernell[i] + "# #Cgreen##z" + nEthernell[i] + "##k";
            }
            say += "\r\n";
            if (!aList) {
                say += "\r\n#L15##Cyellow#전체 장비 리스트를 본다.#k";
            }
            say += "\r\n#L16##Cgray#사용을 취소한다.#k";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            s = selection;
            if (selection == 16) {
                cm.dispose();
                return;
            }
            if (selection == 15) {
                aList = true;
                status = -1;
                action(1, 0, 0);
                return;
            }
            var say = "　 #Cgray#받고 싶은 장비가 이 장비가 확실한가요?";
            say += "\r\n";
            say += "\r\n　 #i" + nEthernell[selection] + "# #Cgreen##z" + nEthernell[selection] + "##k";
            say += "\r\n";
            say += "\r\n　 #Cgray#한 번 조각을 아이템으로 교환하면 #r10개의 조각이 차감#Cgray#되\r\n　 며, 다시 다른 아이템으로 교환 할 수 없습니다.#k";
            say += "\r\n#L0##Cyellow#네. 맞습니다.";
            say += "\r\n#L1##Cgray#아닙니다. 다시 선택하겠습니다.";
            cm.sendSimple(say);
            break;
        }
        case 2: {
            cm.dispose();
            if (selection == 1) {
                return;
            }
            if (cm.getInventory(1).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2434588, -10);
            cm.gainItem(nEthernell[s], 1);
            break;
        }
    }
}