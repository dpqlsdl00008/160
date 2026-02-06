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
            qm.askAcceptDecline("그럼 출발할까? 승강기를 타고 암벽 거인의 몸을 타고 올라가는 거야. 워낙 거대한 몸 위에 올라가는 것이니 만큼 시간이 좀 걸려. 준비를 단단히 하도록 해.");
            break;
        }
        case 1: {
            qm.sendNext("혹 벌떼들의 습격을 받게 될 수도 있어. 그럴 땐 총이 최고야.\r\n\r\n#r(총은 마우스 클릭으로 사용 할 수 있다.)#k");
            break;
        }
        case 2: {
            qm.sendNextPrev("#fMob/8147005.img/stand/0#\r\n호넷이 덤벼들거든 망설이지 말고 처치하도록 해. 물론 시간과 총알에는 제한이 있다는 걸 명심해.");
            break;
        }
        case 3: {
            qm.sendNextPrev("#fMob/8147006.img/stand/0#\r\n만일 포이즌 호넷이 덤벼들거든 절대로 쏘지 마. 바람을 타고 독이 이쪽으로 날아오게 되면 치명적이니까 말이야.");
            break;
        }
        case 4: {
            qm.sendNextPrev("#fMob/8147007.img/stand/0#\r\n제너럴 호넷은 높은 고도에서는 깜빡거리며 잘 보이지 않아. 하지만 대장인 제너럴 호넷들을 처치하면 나머지들은 확실히 겁을 먹고 물러나게 되겠지.");
            break;
        }
        case 5: {
            qm.sendNextPrev("자, 그럼 더 이상 긴 말 않고 출발한다.");
            break;
        }
        case 6: {
            qm.sendNextPrev("고지대로 올라가면 컨디션에 신경쓰도록 해. 귀가 좀 먹먹해지기 시작할 거야.");
            break;
        }
        case 7: {
            qm.sendNextPrev("...? 무언가 온다!");
            break;
        }
        case 8: {
            qm.dispose();
            qm.warp(240091600, 0);
            qm.forceStartQuest();
            break;
        }
    }
}