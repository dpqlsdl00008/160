var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 1) {
            cm.sendNext("\r\n안 가면 말고.");
        }
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            var say = "이곳을 떠날 생각이야? 어느 곳으로 가고 싶어?#b";
            say += "\r\n#L0##e테마 던전 : 리에나 해협#n으로 간다. (비용 : 0 메소)";
            say += "\r\n#L1#빅토리아 아일랜드로 간다. (비용 : 800 메소)";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            if (selection == 0) {
                cm.sendNext("\r\n오오, 빙하를 가로 지르는 항해자가 되어 보려는 거야? 좋아, 리에나 해협까지는 특별히 공짜로 태워 주지! 어서 타라구.");
                return;
            }
            cMeso = true;
            if (cm.haveItem(4032338, 1) == true) {
                cMeso = false;
                cm.sendYesNo("앗? 그건 리린의 편지잖아? 리린의 부탁으로 가는 거라면 무료로 태워 줄 수 있지. 어때 지금 바로 출발하겠어?");
                return;
            }
            cm.sendYesNo("원한다면 바로 리스 항구까지 태워다 주도록 하지. 필요한 메소는 있는지 확실히 잘 확인해 보라고. 그럼 출발한다?");
            break;
        }
        case 2: {
            cm.dispose();
            if (v1 == 0) {
                cm.getPlayer().dropMessage(1, "'리에나 해협' 테마 던전은 현재 구현 준비 중에 있습니다.");
                return;
            }
            if (cMeso) {
                if (cm.getMeso() < 800) {
                    cm.sendNext("\r\n뭐야... 돈이 부족하잖아? 필요한 메소는 #b800 메소#k라고.");
                    return;
                }
                cm.gainMeso(-800);
            }
            cm.warp(200090070, 0);
            break;
        }
    }
}