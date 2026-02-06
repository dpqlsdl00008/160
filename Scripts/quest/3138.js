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
            qm.sendNextS("누가 내게 편지를 보냈지? 보낼 사람이 없는데... 한 번 열어 볼까?", 16);
            break;
        }
        case 1: {
            qm.sendNextPrev("이 편지를 읽는 모험가에게.\r\n\r\n나는 크로스 헌터의 요원 #b비스트#k라고 하네. 거두 절미하고 말하지. 우리 크로스 헌터 요원들은 메이플 월드 각지를 돌아다니며 어둠의 몬스터를 처치하는 것을 주 임무로 하고 있지.");
            break;
        }
        case 2: {
            qm.sendNextPrev("그런데 지금 내가 있는 이 곳, #r사자 왕의 성#k에 그 동안 볼 수 없었던 강력한 어둠의 기운이 넘실대고 있어. 엘나스의 차디 찬 바람보다 더욱 시린 공포가 느껴지네.");
            break;
        }
        case 3: {
            qm.sendAcceptDecline("내 임무를 완수하기 위해서는 자네와 같은 뛰어 난 모험가가 필요 한 시점이야. 내게 도움을 주고 싶다면 다음 장을 읽게.");
            break;
        }
        case 4: {
            qm.sendNext("고맙군. 이 장을 보고 있다면 나를 돕기로 마음 먹은 것이겠지. 시간이 없기 때문에 편지에 작은 마법을 동봉해 두었어. 편지를 다 읽으면 내가 있는 곳으로 한 번에 이동 할 수 있을 것 일세. 그럼, 조금 뒤에 보자고.");
            break;
        }
        case 5: {
            qm.dispose();
            qm.warp(211060000, "out00");
            qm.forceStartQuest();
            break;
        }
    }
}