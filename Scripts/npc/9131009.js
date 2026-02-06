var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            cm.lockInGameUI(true);
            cm.sendDelay(1000);
            break;
        }
        case 1: {
            cm.sendNextS("훌륭하군. 더욱 더 내 수하로 거두고 싶단 말 일세...", 9, 9131007, 9131007);
            break;
        }
        case 2: {
            cm.sendNextPrevS("이미 전투가 벌어지고 있는 상황이기에 간단하게 제육 천마 왕의 수하들을 제압을 할 수 있었습니다.", 3);
            break;
        }
        case 3: {
            cm.sendNextS("침투는 각 방향에서 동시에 개시하는 것으로 작전을 짰으니, 누군가 작전을 실패 시키려는 것이 아닌 한, 실패는 없을 걸세. 짐작이 가지 않는 것은 아니지만...", 9, 9131007, 9131007);
            break;
        }
        case 4: {
            cm.sendNextS("혼노지의 혼란은 침투를 하기에도 용이하니, 지금의 상황은 꽤나 도움이 될 것 같군... 진상의 확인은 노부가나를 쓰러뜨린 다음에 하여도 늦지 않을 걸세.", 9, 9131007, 9131007);
            break;
        }
        case 5: {
            cm.sendNextPrevS("네, 알겠습니다.", 3);
            break;
        }
        case 6: {
            cm.dispose();
            cm.warp(507100003, 0);
            break;
        }
    }
}