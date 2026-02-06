var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 3 || status == 6) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("어떻게든 이 일을 수습해야 하는데...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(누군가 갑자기 나의 어께를 치고 지나갔다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrevS("누구세요?", 2);
            break;
        }
        case 3: {
            qm.sendAcceptDeclineS("...\r\n\r\n#b(내 말은 들은 척도 않은 채 어디론가 빠르게 사라지는 남자의 등 뒤로 무언가 반짝이는 물건이 떨어진 것 같다. 주워볼까?)#k", 2);
            break;
        }
        case 4: {
            qm.sendNextS("#b(가까이 가 보니 반짝이던 것은 다름이 아니라 낡은 회중시계였다. 주워서 이리 저리 살펴보았지만 시계 바늘이 움직이지 않는 것을 빼면 특별한 부분은 보이지 않는다.)#k", 2);
            break;
        }
        case 5: {
            qm.sendNextPrevS("왠지 위에 달린 커다란 버튼을 누르면 시계가 다시 움직일 것 같은데...", 2);
            break;
        }
        case 6: {
            qm.sendYesNoS("버튼을 눌러서 시계를 다시 작동시키겠습니까?\r\n\r\n#b(#k#r예#k#b를 누르면 바로 테라 숲 타임 게이트로 이동합니다.)", 4);
            break;
        }
        case 7: {
            qm.dispose();
            qm.forceStartQuest();
            qm.warp(240070000, "west00");
            break;
        }
    }
}