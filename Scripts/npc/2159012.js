var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendNext("흠.. 실험은 순조롭게 진행되고 있는 것 같군. 역시 루를 제대로 공급받으니 잘 되는걸? 블랙윙과 손을 잡기로 한 건 역시 현명한 선택이었어... 후후후");
            break;
        }
        case 1: {
            cm.sendNextPrevS("역시 겔리메르님은 선견지명이 있으시군요..", 4, 2159008);
            break;
        }
        case 2: {
            cm.sendNextPrev("블랙윙이 말한 수준의 안드로이드는 이제 곧 완성된다. 이제 그 다음 단계의 실험을 시작해 봐야겠어. 그들이 말하는 것보다 훨씬 더 재미있는 걸 말이야");
            break;
        }
        case 3: {
            cm.sendNextPrevS("그 다음 단계요?", 4, 2159008);
            break;
        }
        case 4: {
            cm.sendNextPrev("후후.. 아직도 모르겟나? 이 실험실을 보면 알 수 있을텐데? 내가 만들고자 하는 것이 무엇인지 말이야 기계만 가지고 하는 재미없는 안드로이드보다 흥미진진한 ...");
            break;
        }
        case 5: {
            cm.sendNextPrevS("네? 이 실험실이요? 이 실험체들로 뭔가 하실 생각 이신 겁니까?", 4, 2159008);
            break;
        }
        case 6: {
            cm.sendNextPrev("뭐, 자네 눈에는 이 실험의 위대함이 보이지 않을 수도 있을 테니 이해하지. 자네는 자네의 임무만 잘 하면 되네. 여기 있는 실험체들이 도망치지 못하도록 잘 지키기만 하면 돼.");
            break;
        }
        case 7: {
            cm.sendNextPrev(".. 음? 뭔가 이상한 소리가 들리지 않는가?");
            break;
        }
        case 8: {
            cm.sendNextPrevS("네? 이상한 소리요? 그러고보니 뭔가 울리는 듯한데요..?", 4, 2159008);
            break;
        }
        case 9: {
            cm.dispose();
            cm.updateInfoQuest(23007, "vel00=2;vel01=1");
            cm.trembleEffect(0, 500);
            cm.movieClipInGameUI(true);
            cm.reservedEffect("Effect/Direction4.img/Resistance/TalkInLab");
            break;
        }
    }
}