var status = -1;
var aList = false;
var nEthernell = [
1042433, // 에테르넬 나이트 아머
1042434, // 에테르넬 메이지 로브
1042435, // 에테르넬 아처 후드
1042436, // 에테르넬 시프 셔츠
1042437, // 에테르넬 파이렛 코트
        
1062285, // 에테르넬 나이트 팬츠
1062286, // 에테르넬 메이지 팬츠
1062287, // 에테르넬 아처 팬츠
1062288, // 에테르넬 시프 팬츠
1062289, // 에테르넬 파이렛 팬츠

1152212, // 에테르넬 나이트 숄더
1152213, // 에테르넬 메이지 숄더
1152214, // 에테르넬 아처 숄더
1152215, // 에테르넬 시프 숄더
1152216, // 에테르넬 파이렛 숄더
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
            if (cm.itemQuantity(2436232) < 10) {
                cm.dispose();
                return;
            }
            var say = "　 #d뒤엉킨 흉수의 고리#Cgray#에 손을 대자 강력한 힘이 느껴진다.　 강력한 힘 속에서 새로운 장비가 탄생하려 한다.";
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
               if (!aList && v1 && !ii.getName(nEthernell[i]).contains("나이트")) {
                   continue;
               }
               if (!aList && v2 && !ii.getName(nEthernell[i]).contains("메이지")) {
                   continue;
               }
               if (!aList && v3 && !ii.getName(nEthernell[i]).contains("아처")) {
                   continue;
               }
               if (!aList && v4 && !ii.getName(nEthernell[i]).contains("시프")) {
                   continue;
               }
               if (!aList && v5 && !ii.getName(nEthernell[i]).contains("파이렛")) {
                   continue;
               }
               say += "\r\n#L" + i + "##i" + nEthernell[i] + "# #Cgreen##z" + nEthernell[i] + "##k";
            }
            say += "\r\n";
            if (!aList) {
                say += "\r\n#L15##e#Cyellow#전체 장비 리스트를 본다.#k#n";
            }
            say += "\r\n#L16##Cgray#사용을 취소한다.#k";
            cm.sendSimpleS(say, 0, 3006199);
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
            var say = "　 #d뒤엉킨 흉수의 고리#Cgray#에서 #d에테르넬 장비#Cgray#가 탄생하려 한\r\n　 다.";
            say += "\r\n";
            say += "\r\n　 #i" + nEthernell[selection] + "# #Cgreen##z" + nEthernell[selection] + "##k";
            say += "\r\n#L0##e#Cyellow#해당 장비를 선택한다.#n";
            say += "\r\n#L1##Cgray#다시 생각해 본다.";
            cm.sendSimpleS(say, 0, 3006199);
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
            cm.gainItem(2436232, -10);
            cm.gainItem(nEthernell[s], 1);
            var say = "\r\n#d뒤엉킨 흉수의 고리#Cgray#에서 #d에테르넬 장비#Cgray#가 탄생했다.";
            say += "\r\n";
            say += "\r\n#fUI/UIWindow.img/QuestIcon/4/0#";
            say += "\r\n#i" + nEthernell[s] + "# #Cgreen##z" + nEthernell[s] + "##k";
            cm.sendNextS(say, 0, 0, 3006199);
            break;
        }
    }
}