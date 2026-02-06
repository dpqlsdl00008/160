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
            qm.sendNext("미나르 숲 남부는 예로부터 이상한 일이 일어나기로 유명했지. 하지만 이번처럼 이상한 일은 처음이야. 암벽 산이 살아서 벌떡 일어나다니 말이야...");
            break;
        }
        case 1: {
            qm.sendNextPrev("자네도 언뜻 들으면 무슨 말인지 이해가 잘 가지 않지? 하지만 그런 일이 실제로 일어났다네.");
            break;
        }
        case 2: {
            qm.sendNextPrev("이러한 초 자연적인 현상을 가장 잘 설명해줄 수 있는 자라면 딱 하나 있지. 그는 바로 #b대정령 구와르#k... 한 때 검은 마법사에게 현혹되어 군단장이 된 적도 있었지만, 지금은 더 이상 악한 존재가 아니야. 이 곳이 아닌 다른 어딘가에서 휴식을 취하고 있다네.");
            break;
        }
        case 3: {
            qm.askAcceptDecline("우리 하프링족은 대대로 하늘과 바람과 숲의 친구였지. 부족대대로 내려오는 비술을 사용하면 일시적으로 #b대정령 구와르#k와 접촉 할 수 있네만... 지금 그를 만나보겠는가?");
            break;
        }
        case 4: {
            qm.sendNext("좋아. 그럼 의식을 집중하게... 그의 목소리가 들리는가?");
            break;
        }
        case 5: {
            qm.sendNextPrevS("암벽 거인이라... 그런 터무니 없는 것이 만들어질 줄이야. 모든 것이 나의 과오로다.\r\n\r\n#b(구와르의 전음이 들린다.)#k", 4, 2210011, 2210011);
            break;
        }
        case 6: {
            qm.sendNextPrevS("이런 일이 일어날 것을 짐작하지 못한 것은 아니지. 수백 년 전, 과거의 나는 분명 검은 마법사의 세력에 동참하였고 그 일원에게 배신을 당하여 힘을 흡수당하였다...\r\n모든 일은 그로부터 시작 된 것이다. 내가 오랜 기간 동안 정령들에 대한 지배력을 상실했기 때문에, 그 동안 이렇게 이상한 일이 일어나고 말았지.", 4, 2210011, 2210011);
            break;
        }
        case 7: {
            qm.sendNextPrevS("이러한 일은 본래 잘못을 저질렀던 내가 책임을 져야 하지만 지금의 나는 힘이 없는 상태... 부디 #b미나르 숲 남부#k에서 암벽 거인이라는 존재를 조사해주게.", 4, 2210011, 2210011);
            break;
        }
        case 8: {
            qm.sendNextPrevS("일반적인 방법으로는 암벽 거인과 대화 할 수 없겠지. 하지만 내가 방금 내 힘의 일부를 나누어 주었으니 분명 암벽 거인과 대화 할 수 있을 것이다. 나의 추측이 맞다면 말이다...\r\n\r\n#b(구와르의 신비로운 힘의 일부가 몸 속으로 스며들었다.)#k", 4, 2210011, 2210011);
            break;
        }
        case 9: {
            qm.sendNextPrevS("그럼 이만. 필요 할 때 내가 다시 접촉 할 것이다.", 4, 2210011, 2210011);
            break;
        }
        case 10: {
            qm.dispose();
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
            qm.sendSimple("대정령 구와르에게 들었겠지만, 이건 중대한 일일세. 하지만 자네같은 용사들에게는 흥미가 동하는 일이기도 하겠지. 어때, 준비가 되었나?\r\n#L0##b암벽 거인에게는 어떻게 가면 되죠?#k");
            break;
        }
        case 1: {
            qm.sendSimple("허허허, 바로 떠나려는가? 성미가 급하군. 이미 우리 하프링들 중 탐사 대원 몇 몇이 그 곳에 머물고 있으니 도움을 받으면 될걸세.\r\n#L0##b하프링들이?#k");
            break;
        }
        case 2: {
            qm.askAcceptDecline("그래, 우리 종족은 대부분 조용하고 평화롭고 순박한 삶을 좋아하는 편이지만... 가끔은 탐험가의 피를 가지고 태어나는 녀석들이 있단 말일세. 그런 녀석들은 도무지 말릴 수가 없지.\r\n\r\n#r원한다면 지금 바로 자네를 그 곳으로 이동 시켜 주지. 어떠한가?#k");
            break;
        }
        case 3: {
            qm.sendNext("좋아. 바로 옮겨주겠네. 가는 김에 그 곳의 하프링들의 안부도 살펴주게.");
            break;
        }
        case 4: {
            qm.dispose();
            qm.warp(240090000, 0);
            qm.forceCompleteQuest();
            break;
        }
    }
}