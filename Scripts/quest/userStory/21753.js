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
            qm.sendNext("방금 도서관 안에서 뭔가 소리가 난 것 같았는데... 당신이었나요, 아란? 봉인석은 찾으셨어요?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(도서관에서 있었던 일을 말해 주었다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("그런... 여기까지 그런 자가 나타날 줄이야... 미안해요, 아란. 제가 좀 더 보관을 잘 했어야 하는 건데...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("헬레나의 잘못이 아니다.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("당신은 여전하시네요. 그보다... 아란에게 봉인석에 관한 이야기를 듣고 생각해 보니 단서가 될 만한 것이 떠올랐어요.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("단서?", 2);
            break;
        }
        case 6: {
            qm.sendAcceptDecline("네. 예전에 아란이 쓰던 편지를 발견했는데, 거기에 봉인석이라는 단어가 들어가 있었거든요. 한 번 보시겠어요?");
            break;
        }
        case 7: {
            qm.sendNext("...어머? 편지가...");
            break;
        }
        case 8: {
            qm.sendNextPrevS("#i4032327#\r\n#b(편지를 받을 수 없었다. 편지는 손을 통과해 바닥으로 떨어졌다.)#K", 2);
            break;
        }
        case 9: {
            qm.sendNextPrev("...시간의 법칙에 대해서는 잘 모르지만... 아마도 이 편지를 전해 줄 수 없는 이유는, #b우리가 이미 다른 시간대의 사람이기 때문#k인 것 같네요... 왠지 슬프네요. 얼마 전까지만 해도 아란은 우리의 동료였는데...");
            break;
        }
        case 10: {
            qm.sendNextPrev("...아란도 알겠지만 전 아주 긴 시간을 사는 요정이에요. 아란이 수백 년 뒤의 시간대 사람이 되었더라고, 아마 저 역시 그 시간대까지 살아있을 가능성이 커요. 그러니 아란, #b이 편지는 제가 소중히 보관할 테니 당신의 시간대에서 찾으러 오세요.#k");
            break;
        }
        case 11: {
            qm.sendNextPrev("수백 년의 시간이 흐른대도 아란도, 이 야속도 잊지 않을 거예요. 그러니 나중에 다시 만나요. 기다릴게요.");
            break;
        }
        case 12: {
            qm.sendNextPrevS("#b(리린이 존재하는 시간으로 돌아가서 헬레나를 찾아 보자. 트루에게 부탁하면 분명 헬레나를 찾아줄 것이다.)#K", 2);
            break;
        }
        case 13: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}