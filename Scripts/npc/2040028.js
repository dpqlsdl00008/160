var status = -1;

function start() {
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
            if (cm.haveItem(4031145) == false) {
                cm.dispose();
                cm.warp(221023200, "top00");
                return;
            }
            cm.sendYesNo("오오~! 모양이 다른 인형의 집 하나를 찾아내어 녀석을 부수고 #b시계 추#k를 찾아 오셨군요. 정말 대단합니다. 이걸로 루디브리엄 시계 탑도 다시 정상 가동 될 수 있겠군요. 좋습니다! 루디브리엄을 위해 이렇게 애써 주셨으니 좋은 선물을 드리도록 하겠습니다. 그 전에 아이템의 소비 창에 빈 칸이 하나 이상 있는 지 확인해 주세요.");
            break;
        }
        case 1: {
            if (cm.isQuestActive(3230) == true) {
                cm.gainItem(2000010, 100);
            }
            cm.gainExp(2400);
            cm.gainItem(2000010, 100);
            cm.forceCompleteQuest(3230);
            cm.sendNextPrev("어떤가요? #b파란 포션 알약#k 100개는 잘 받으셨죠? 이렇게 도와주셔서 정말 고마웠습니다. 덕분에 이제는 루디브리엄 시계 탑이 원활하게 돌아가고 다른 세계의 몬스터도 사라진 것 같습니다. 그럼 다시 밖으로 내보내 드리겠습니다. 안녕히 가세요~!");
            break;
        }
        case 2: {
            cm.dispose();
            cm.removeAll(4031145);
            cm.warp(221023200, "top00");
            break;
        }
    }
}