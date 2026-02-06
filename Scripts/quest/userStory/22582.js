var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendAcceptDecline("당신께 드릴 세 번째 임무는 비밀의 마스터 소울 테니의 영혼 수집입니다. 루디브리엄 시계 탑의 깊은 곳에 존재하는 #r마스터 소울 테니#k를 사냥하면 #b비밀의 마스터 소울 테니의 영혼#k이라는 것이 간혹 나오는데, 그 것을 #b100개#k 모아 가져다 주시면 됩니다.");
            break;
        }
        case 1: {
            qm.sendNext("저는 계속 여기 있을 예정이니, 바로 저에게 오시면 됩니다... 그나저나 참 특이하게 생긴 도마 뱀을 기르시는군요. 마치 드래곤같아요. 하지만 드래곤 일 리는 없으니... 흠.");
            break;
        }
        case 2: {
            qm.sendYesNo("뭐, 어쨌든 #r마스터 소울 테니#k가 있는 곳은 제가 보내드릴 수 있는데, 지금 가보시겠어요?");
            break;
        }
        case 3: {
            qm.dispose();
            qm.warp(922030002, 0);
            qm.forceStartQuest();
            break;
        }
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("아아, 임무는 마치신 모양이군요. 그럼 당신이 구해 온 비밀의 마스터 소울 테니의 영혼을 확인해보죠.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 63,300 exp");
            break;
        }
        case 1: {
            qm.sendNextPrev("후훗... 좋습니다. 이 정도면 계획을 실행하기에 충분 할 것 같군요...");
            break;
        }
        case 2: {
            qm.sendNextPrevS("#b저기... 한 가지 궁금한 게...#k", 2);
            break;
        }
        case 3: {
            qm.sendNextPrev("죄송합니다만 구해오신 자유로운 영혼 처리 때문에 바쁠 것 같군요. 물건이 완성 된 후에 다시 찾아와 주시겠습니까? 시간이 좀 걸릴 것 같습니다만...");
            break;
        }
        case 4: {
            qm.dispose();
            qm.gainItem(4000594, -100);
            qm.gainExp(63300);
            qm.sendOkS("#b(사람이 다니지도 않을 저 시계 탑 깊은 곳에 있는 몬스터를 잡는 게 무슨 도움이 되나? 물어보고 싶지만 바빠 보인다. 나중에 찾아와서 물어보자.)#k", 2);
            qm.forceCompleteQuest();
            break;
        }
    }
}