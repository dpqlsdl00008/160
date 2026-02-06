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
            qm.sendNext("이것이 바로 그 균열이로군요. 퍼즐을 풀 시간이 된 것 같습니다. 아카이럼, 미스틱 게이트, 그리고 시간의 신전의 균열...");
            break;
        }
        case 1: {
            qm.sendNextPrev("호랑이를 잡으려면 호랑이 굴로 가야 한다는 말이 있습니다. 이 모든 문제를 풀려면 균열 안으로 직접 이동하는 방법 밖에 없겠군요.");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("크로우와 셰릴은 이미 이 새로운 임무를 위해 준비 중 입니다. 당신도 임무를 수행하실 준비가 되셨습니까?");
            break;
        }
        case 3: {
            qm.dispose();
            qm.sendOk("균열 안에 무엇이 있는지 확인해 주세요. 만약 균열 안에서 아카이럼을 마주치게 된다면... 아, 아닙니다. 오른쪽에 있는 포탈을 타고 가면 됩니다. 그럼 부디 조심하세요.");
            qm.forceStartQuest();
            break;
        }
    }
}