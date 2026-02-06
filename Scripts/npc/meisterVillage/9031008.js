var status = -1;

function start() {
    if (cm.isQuestActive(3195) == true || cm.isQuestActive(3196) == true || cm.isQuestActive(3197) == true) {
        status = 2;
    }
    action(1, 0, 0);
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
            var say = "어디로 가고 싶나?#d";
            say += "\r\n#L0#1. 초심자의 채집 농장";
            say += "\r\n#L1#2. 중급자의 채집 농장";
            say += "\r\n#L2#3. 상급자의 채집 농장";
            say += "\r\n#L3#4. 전문가의 채집 농장";
            say += "\r\n#L4#5. 초심자의 비밀 광산";
            say += "\r\n#L5#6. 중급자의 비밀 광산";
            say += "\r\n#L6#7. 상급자의 비밀 광산";
            say += "\r\n#L7#8. 전문가의 비밀 광산";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            var warpID = 910001003;
            switch (selection) {
                case 1: {
                    warpID = 910001004;
                    break;
                }
                case 2: {
                    warpID = 910001007;
                    break;
                }
                case 3: {
                    warpID = 910001009;
                    break;
                }
                case 4: {
                    warpID = 910001005;
                    break;
                }
                case 5: {
                    warpID = 910001006;
                    break;
                }
                case 6: {
                    warpID = 910001008;
                    break;
                }
                case 7: {
                    warpID = 910001010;
                    break;
                }
            }
            cm.warp(warpID, "in00");
            break;
        }
        case 2: {
            cm.dispose();
            break;
        }
        case 3: {
            var say = "어디로 가고 싶나?#d";
            say += "\r\n#L0#1. 스타첼의 약초 밭";
            say += "\r\n#L1#2. 노붐의 광산";
            cm.sendSimple(say);
            break;
        }
        case 4: {
            cm.dispose();
            cm.warp(910001001 + selection, "in00");
            break;
        }
    }
}