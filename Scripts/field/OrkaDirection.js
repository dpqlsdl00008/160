var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.lockInGameUI(true);
            cm.sendNextS("여긴... 이런 동굴에 방이라니... 대체 뭐지? 아이는, 뮤네는 어디있지?", 3);
            break;
        }
        case 1: {
            cm.sendNextPrevS("하나에 집중하면 다른 건 안 보이는 건 여전하구나? 하긴, 넌 원래 단순했지.", 1, 0, 1033230);
            break;
        }
        case 2: {
            cm.sendNextPrevS("누구냐?!", 3);
            break;
        }
        case 3: {
            cm.sendNextPrevS("어라? 벌써 날 잊어버린 거야?", 1, 0, 1033230);
            break;
        }
        case 4: {
            cm.sendNextPrevS("넌...", 3);
            break;
        }
        case 5: {
            cm.sendNextPrevS("오랜만이야, 메르세데스.", 1, 0, 1033230);
            break;
        }
        case 6: {
            cm.sendNextPrevS("블랙 마스터... 오르카?!", 3);
            break;
        }
        case 7: {
            cm.reservedEffect("Effect/Direction5.img/mersedesQuest/Scene0");
            cm.sendDelay(2500);
            break;
        }
        case 8: {
            cm.dispose();
            if (cm.getMap().containsNPC(1033230) == false) {
                cm.spawnNpc(1033230, 382, 18);
            }
            cm.lockInGameUI(false);
            break;
        }
    }
}