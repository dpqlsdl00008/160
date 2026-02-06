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
            qm.sendNext("오래 기다린 것은 아니겠지? 노인의 기억은 걸음이 빠르지 않으니 어쩌겠나 허허.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("뭔가 기억나는 것이 있나요?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("예전에 우리 하프링 일족 중 #b코르바#k라는 용 조련사가 마을에 있었는데, 그에게 시간에 대한 묘한 말을 들은 적이 있네. 아마 무슨 봉인이 어쩌고 하는 이야기였는데... 그 이상은 잘 기억이 나지 않는구만.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("코르바라는 분은 어디에 있죠? 근처에 있다면 찾아가보고 싶어요.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("그게... 사실 그가 어디 있는지, 혹은 살아는 있는지도 잘 모르겠네. 오래 전에 마을을 떠난 뒤로 소식이 끊겼어. 미안하구만.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b(어쩌지. 더 이상 알아볼 방법이... 앗? 타임 다이버에서 빛이...?!)#k", 2);
            break;
        }
        case 6: {
            qm.dispose();
            qm.warp(240000110, 3);
            qm.forceStartQuest();
            break;
        }
    }
}