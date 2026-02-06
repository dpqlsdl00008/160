var status = -1;
var aList = false;
var nEthernell = [
1003172, // 라이온 하트 배틀 헬름
1003173, // 드래곤 테일 메이지 샐릿
1003174, // 팔콘 윙 센티널 캡
1003175, // 레이븐 혼 체이서 햇
1003176, // 샤크투스 스키퍼 햇

1052314, // 라이온 하트 배틀 메일
1052315, // 드래곤 테일 메이지 로브
1052316, // 팔콘 윙 센티널 슈트
1052317, // 레이븐 혼 체이서 아머
1052318, // 샤크투스 스키퍼 코트

1072485, // 라이온 하트 배틀 부츠
1072486, // 드래곤 테일 메이지 슈즈
1072487, // 팔콘 윙 센티널 부츠
1072488, // 레이븐 혼 체이서 부츠
1072489, // 샤크투스 스키퍼 부츠

1082295, // 라이온 하트 배틀 브레이서
1082296, // 드래곤 테일 메이지 피스트
1082297, // 팔콘 윙 센티널 글러브
1082298, // 레이븐 혼 체이서 글러브
1082299, // 샤크투스 스키퍼 글러브

1102275, // 라이온 하트 배틀 케이프
1102276, // 드래곤 테일 메이지 케이프
1102277, // 팔콘 윙 센티널 케이프
1102278, // 레이븐 혼 체이서 케이프
1102279, // 샤크투스 스키퍼 케이프

1152108, // 라이온 하트 배틀 숄더
1152110, // 드래곤 테일 메이지 숄더
1152111, // 팔콘 윙 센티널 숄더
1152112, // 레이븐 혼 체이서 숄더
1152113, // 샤크투스 스키퍼 숄더
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
            if (cm.itemQuantity(2434589) < 10) {
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
            cm.gainItem(2434589, -10);
            cm.gainItem(nEthernell[s], 1);
            break;
        }
    }
}