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
            qm.sendNext("영웅이었다면서 뭘 그렇게 밍기적거리세요? 쇠뿔도 단김에 빼라잖아요? 강해지려면 일단 팍! 시작하고 보는 거예요.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("자, 그럼 이제 설명은 여기까지로 해 두고 다음 단계로 넘어가죠, 다음 단계가 뭐냐고요? 방금 말씀 드렸잖아요. 검은 마법사를 한 방에 해치울 수 있을 정도로 강해질 때까지 수련하는 거예요.");
            break;
        }
        case 1: {
            qm.sendNextPrev("당신이 과거에 영웅이었던 건 확실하지만 그거야 수백 년 전 얘기, 검은 마법사의 저주가 아니래도 얼음 속에 그렇게 오래있었으니 몸이 굳었을 게 당연하잖아요? 일단은 그 굳은 몸부터 풀어봐야겠어요. 어떻게 하느냐고요?");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("체력이 국력이다! 영웅도 기초 체력부터! ...라는 말도 모르세요? 당연히 #b기초 체력 단련#k을 해야죠. ...아, 기억을 잃었으니 모르시겠군요. 뭐 모르셔도 괜찮아요. 직접 해보시면 알 테니까. 그럼 바로 기초 체력 단련에 들어갈까요?");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}