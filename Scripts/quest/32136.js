var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 3) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (qm.getPlayer().isGM() == false) {
                cm.dispose();
                return;
            }
            qm.askAcceptDecline("아, 마침 잘됐군. 외부에서 긴급 요청이 들어왔는데 반드시 그대가 필요하다네.");
            break;
        }
        case 1: {
            qm.sendNext("다름이 아니라 #b요정 학원 엘리넬#k에 지금 커다란 소동이 일어났다고 하네. 요정 학원 엘리넬이 있는 곳은 엘리니아와는 달리 인간들의 손길이 닿지 않은 요정의 땅이지. 그렇기에 요정의 땅 태초의 모습을 그대로 간직하고 있다네. 하지만 최근 #r인간 마법사 한 명#k이 그 곳에 발을 들여놓은 모양이야.");
            break;
        }
        case 2: {
            qm.sendNextPrev("무슨 일인지 자세한 사정은 모르겠지만 더 이상 소동이 커진다면 요정과 인간의 관계가 더 악화 될 수 있으니 한 번 가서 사정을 들어보고 도와주는 게 좋을 것 같네. 자네 정도면 이 소동을 현명히 해결 할 수 있겠지. 그럼 먼저 엘리니아 근처의 북쪽 숲에 있는 #b펜시#k를 찾아보도록 하게.");
            break;
        }
        case 3: {
            qm.sendYesNo("그런데, 자네 펜시가 있는 위치를 알고 있나? 혼자서 찾아갈 수 있다면 상관 없지만, 괜찮다면 바로 앞까지 보내 주도록 하지.");
            break;
        }
        case 4: {
            qm.sendNext("좋아. 지금 바로 펜시 앞으로 보내주겠네. 행운을 빌겠네.");
            break;
        }
        case 5: {
            qm.dispose();
            qm.forceStartQuest();
            qm.warp(101030000, 0);
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
            qm.sendNext("냐~옹 니가 바로 요정 학원 엘리넬에 일어난 소동을 해결하기 위해 초대 받은 용사냐옹??");
            break;
        }
        case 1: {
            qm.sendNextPrevS("그... 그렇습니다... 만", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("생각보다 강해보이지는 않는다냥. 하지만 이름 난 용사라니 어디 한 번 믿어보겠다옹.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest(32147, "1");
            qm.forceCompleteQuest();
            break;
        }
    }
}