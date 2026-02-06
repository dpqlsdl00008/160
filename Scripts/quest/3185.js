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
            qm.sendNextS("그렇게 몸이 안 좋으신 건 가요?", 2);
            break;
        }
        case 1: {
            qm.sendNextPrev("아니 뭐 그런 건 아닌데... 으음...");
            break;
        }
        case 2: {
            qm.sendNextPrevS("그럼 무슨 일 이신가요? 이제 보니 그렇게 아프신 것 같지도 않은데...", 2);
            break;
        }
        case 3: {
            qm.sendNextPrev("그래. 차라리 속 시원하게 말해야 겠군. 사실 이거 사냥꾼으로서는 참 부끄러운 이야기지만...");
            break;
        }
        case 4: {
            qm.sendNextPrev("며칠 전의 일 일세. 난 평소대로 차디 찬 벌판에서 헥터를 사냥하고 있었지. 그런데 갑자기 바람이 몰아치더니, 내 앞에 #b설산의 마녀#k가 나타났다네.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("설산의 마녀? 그게 뭐죠?", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("설산의 마녀는 엘나스에서 오래 전 부터 전설로만 전해지던 몬스터인데, 몸이 얼음으로 되어 있다고 하지. 난 그저 전설로만 여기고 있었는데... 실제로 보게 될 줄이야. 아무튼 처음 봤을 때는 사냥꾼의 오기로 겨우 겨우 물리쳤다네. 하지만 그 다음 사냥에서 멀쩡 한 모습으로 다시 내 앞에 나타나더군. 난 그 때 정말 겁이 났었네. 그래서 몸이 좋지 않다는 핑계를 대고 사냥을 나가지 않았지...");
            break;
        }
        case 7: {
            qm.sendAcceptDecline("하지만 이젠 이러고만 있을 수는 없네. 앞으로 계속 다른 사람에게 부탁 할 수만은 없으니... 그래서 말인데, 알케스터님이라면 어떻게 해결해 줄 수 있지 않을까 해서 말일세. 자네가 나 대신 #b알케스터#k님께 말씀드려 주지 않겠나? 내가 직접 찾아 뵙기엔... 사람들 눈도 있고 해서 말야... 그리 어려운 부탁은 아닐거라 생각하네.");
            break;
        }
        case 8: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}