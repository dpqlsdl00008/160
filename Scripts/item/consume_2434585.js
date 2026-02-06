var status = -1;
var aList = false;
var nEqp = [
1062165, // 트릭스터 워리어 팬츠
1062166, // 트릭스터 던위치 팬츠
1062167, // 트릭스터 레인져 팬츠
1062168, // 트릭스터 어새신 팬츠
1062169, // 트릭스터 원더러 팬츠
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
            if (cm.itemQuantity(2434585) < 30) {
                cm.dispose();
                return;
            }
            var say = "　 #Cgray#현재 직업이 착용 가능 한 장비를 우선 추천해 드립니다.\r\n　 받으실 방어구를 선택해 주세요.";
            if (aList) {
                say = "　 #Cgray#받으실 방어구를 선택해 주세요.";
            }
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
            for (i = 0; i < nEqp.length; i++) {
               if (!aList && v1 && !ii.getName(nEqp[i]).contains("워리어")) {
                   continue;
               }
               if (!aList && v2 && !ii.getName(nEqp[i]).contains("던위치")) {
                   continue;
               }
               if (!aList && v3 && !ii.getName(nEqp[i]).contains("레인져")) {
                   continue;
               }
               if (!aList && v4 && !ii.getName(nEqp[i]).contains("어새신")) {
                   continue;
               }
               if (!aList && v5 && !ii.getName(nEqp[i]).contains("원더러")) {
                   continue;
               }
               say += "\r\n#L" + i + "##i" + nEqp[i] + "# #Cgreen##z" + nEqp[i] + "##k";
            }
            say += "\r\n";
            if (!aList) {
                say += "\r\n#L15##Cyellow#전체 장비 리스트를 본다.#k";
            }
            say += "\r\n#L16##Cyellow#사용을 취소한다.#k";
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
            var say = "　 받고 싶은 장비가 이 장비가 확실한가요?";
            say += "\r\n";
            say += "\r\n　 #i" + nEqp[selection] + "# #Cgreen##z" + nEqp[selection] + "##k";
            say += "\r\n#L0##Cyellow#네, 맞습니다.";
            say += "\r\n#L1##Cyellow#아닙니다, 다시 선택하겠습니다.";
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
            cm.gainItem(2434585, -30);
            cm.gainItem(nEqp[s], 1);
            break;
        }
    }
}