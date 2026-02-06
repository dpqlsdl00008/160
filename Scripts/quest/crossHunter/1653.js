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
            qm.sendNextS("#b(어디선가 날아 온 작은 편지가 내 손에 들어왔다.)#k\r\n\r\n이건 뭐지? 십자 무늬를 봐서는 크로스 헌터 측에서 보낸 것 같은데... 한 번 열어 보자.", 16);
            break;
        }
        case 1: {
            qm.sendNextPrev("안녕하세요. 이것을 읽고 있다면 빠른 시간 안에 저희의 아지트로 와 주세요. 그 동안 임무를 성공적으로 수행했던 당신의 능력이 필요한 일이 생겼어요. 자세한 사항은 만나서 말씀 드릴게요.\r\n\r\n#d- 셰릴 -");
            break;
        }
        case 2: {
            qm.sendNextPrev("추신 : 편지 안에 아지트로 이동 할 수 있는 티켓을 한 장 넣었어요. 오실 때 사용하시면 편할 거에요. 잃어버리면 직접 찾아오셔야 하니 잘 간수하세요.");
            break;
        }
        case 3: {
            qm.sendAcceptDeclineS("얼마나 급한 일이길래 티켓까지 같이 넣은 거지? 그럼 찾아가 볼까?", 2);
            break;
        }
        case 4: {
            qm.sendNextS("#i2030026# 이게 티켓이구나. 이걸 사용해서 크로스 헌터 아지트로 한 번에 이동하면 되겠어.", 16);
            break;
        }
        case 5: {
            qm.dispose();
            qm.gainItem(2030026, 1);
            qm.forceStartQuest();
            break;
        }
    }
}