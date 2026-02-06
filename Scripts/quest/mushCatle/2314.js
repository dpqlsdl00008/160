var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 2) {
            qm.sendNext("제발 저희 머쉬킹 왕국을 도와주시길...");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("오! 당신이 경호대장이 말한 그 용사님이로군요. 지금 우리 머쉬킹 왕국에 큰 위기에 닥쳤습니다.부끄럽지만 아이스 랜드의 #b페페킹#k의 침공에 왕궁을 빼았기고, 비올레타 공주 마저 인질로 잡혀 있는 최악의 상황입니다.");
            break;
        }
        case 1: {
            qm.sendNext("간사한 페페킹 녀석! 머쉬킹 왕의 건강이 악화되어 비올레타 공주에게 왕권을 이양하려는 이 때를 노려 침공한 것이 분명합니다. 페페킹의 멍청한 아들 주페 왕자와 비올레타 공주님을 강제로 결혼시켜 머쉬킹 왕국을 손에 넣으려 하는 것이 최종목표... 그러니 일단 위기에 빠진 비올레타 공주를 구하는 것이 급선무입니다. 용사님 공주를 부탁합니다.");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("공주를 구하려면 우선, 버섯 숲을 조사해 보아야 해요. 페페킹 녀석이 무슨 술수를 부려 강력한 결계를 쳐놔 성으로의 진입을 차단해 놓았지요. 지금 바로 조사를 해 주세요.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}