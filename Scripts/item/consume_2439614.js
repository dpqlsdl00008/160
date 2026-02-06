var status = -1;
var nGenesis = [
1302354, // 봉인 된 제네시스 세이버
1312212, // 봉인 된 제네시스 엑스
1322263, // 봉인 된 제네시스 해머
1402267, // 봉인 된 제네시스 투 핸드 소드
1412188, // 봉인 된 제네시스 투 핸드 엑스
1422196, // 봉인 된 제네시스 투 핸드 해머
1432226, // 봉인 된 제네시스 스피어
1442284, // 봉인 된 제네시스 폴암

1372236, // 봉인 된 제네시스 완드
1382273, // 봉인 된 제네시스 스태프

1452265, // 봉인 된 제네시스 보우
1462251, // 봉인 된 제네시스 크로스보우
1522151, // 봉인 된 제네시스 듀얼 보우건

1472274, // 봉인 된 제네시스 가즈
1332288, // 봉인 된 제네시스 대거
1362148, // 봉인 된 제네시스 케인

1482231, // 봉인 된 제네시스 클로
1492244, // 봉인 된 제네시스 피스톨
1532156, // 봉인 된 제네시스 시즈건
1403021, // 봉인 된 제네시스 무권
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
            var say = "　 #d봉인 된 제네시스 무기#k를 획득 할 수 있다.\r\n　 어떤 무기를 받을까?";
            var v1 = 0;
            var v2 = nGenesis.length;
            switch (cm.getPlayer().getJob()) {
                case 112:
                case 122:
                case 132:
                case 1112:
                case 2112:
                case 3112:
                case 4112:
                case 5112:
                case 6112: {
                    v1 = 0;
                    v2 = 7;
                    break;
                }
                case 212:
                case 222:
                case 232:
                case 1212:
                case 2218:
                case 3212:
                case 4212:
                case 5212:
                case 6212: {
                    v1 = 8;
                    v2 = 9;
                    break;
                }
                case 312:
                case 322:
                case 1312:
                case 2312:
                case 3312: {
                    v1 = 10;
                    v2 = 12;
                    break;
                }
                case 412:
                case 422:
                case 434:
                case 1412:
                case 2412: {
                    v1 = 13;
                    v2 = 15;
                    break;
                }
                case 512:
                case 522:
                case 1512:
                case 3512: 
                case 6512: {
                    v1 = 16;
                    v2 = 19;
                    break;
                }
            }
            for (i = v1; i < (v2 + 1); i++) {
               say += "\r\n#L" + i + "##d#z" + nGenesis[i] + "##k";
            }
            cm.sendSimpleS(say, 16);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getInventory(1).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            cm.gainItem(2439614, -1);
            cm.gainItem(nGenesis[selection], 1);
            break;
        }
    }
}