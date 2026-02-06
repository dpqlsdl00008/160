function start() {
    var itemID = 0;
    switch (cm.getJob()) {
        case 110:
        case 111:
        case 112:
        case 130:
        case 131:
        case 132:
        case 310:
        case 311:
        case 312:
        case 320:
        case 321:
        case 322:
        case 410:
        case 411:
        case 412:
        case 510:
        case 511:
        case 512:
        case 520:
        case 521:
        case 522:
        case 1100:
        case 1110:
        case 1111:
        case 1112:
        case 1300:
        case 1310:
        case 1311:
        case 1312:
        case 1400:
        case 1410:
        case 1411:
        case 1412:
        case 1500:
        case 1510:
        case 1511:
        case 1512:
        case 2100:
        case 2110:
        case 2111:
        case 2112:
        case 2300:
        case 2310:
        case 2311:
        case 2312:
        case 2400:
        case 2410:
        case 2411:
        case 2412:
        case 3300:
        case 3310:
        case 3311:
        case 3312:
        case 3500:
        case 3510:
        case 3511:
        case 3512: {
            itemID = 2046146;
            break;
        }
        case 120:
        case 121:
        case 122:
        case 420:
        case 421:
        case 422:
        case 430:
        case 431:
        case 432:
        case 433:
        case 434:
        case 3100:
        case 3110:
        case 3111:
        case 3112:
        case 5100:
        case 5110:
        case 5111:
        case 5112: {
            itemID = 2046070;
            break;
        }
        case 210:
        case 211:
        case 212:
        case 220:
        case 221:
        case 222:
        case 230:
        case 231:
        case 232:
        case 1200:
        case 1210:
        case 1211:
        case 1212:
        case 2200:
        case 2210:
        case 2211:
        case 2212:
        case 2213:
        case 2214:
        case 2215:
        case 2216:
        case 2217:
        case 2218:
        case 3200:
        case 3210:
        case 3211:
        case 3212: {
            itemID = 2046071;
            break;
        }
    }
    if (itemID == 0) {
        cm.getPlayer().dropMessage(1, "알 수 없는 오류가 발생하였습니다.");
        cm.dispose();
        return;
    }
    cm.gainItem(2430669, -1);
    cm.gainItem(itemID, 1);
    cm.dispose();
}