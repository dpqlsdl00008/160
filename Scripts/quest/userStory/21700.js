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
            qm.sendNext("뭔가 떠오르신 듯한 얼굴이시네요. 역시 폴암이 당신을 알아본 모양이군요. 그렇다면 당신은 폴암을 사용하던 영웅, 아란이 틀림없어요. 혹시 그 외에 떠오르신 건 없나요? 폴암과 관련된 스킬이라던가...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(스킬 몇 개를 기억해냈다고 알려주었다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("많은 수는 아니지만 충분한 성과네요. 그럼 이제부터는 예전의 능력을 되찾는데 총력을 기울여야 겠네요. 비록 기억은 잃었지만, 이미 한 번 갔던 길이니만큼 금방 능력을 찾을 수 있을 거예요.");
            break;
        }
        case 3: {
            qm.sendNextPrevS('어떻게 능력을 찾지?', 2);
            break;
        }
        case 4: {
            qm.sendAcceptDecline("그거야... 한 가지 방법밖에 없죠. 수련! 수련! 수련! 수련하고 또 수련하다 보면 언젠가 잊고 있던 몸의 감각을 되찾을 수 있을 거예요! 그런 의미에서 당신을 수련시킬 스승님을 한 분 소개해 드릴게요.");
            break;
        }
        case 5: {
            qm.sendNext("좀 더 익숙한 무기를 사용하시는 게 좋을 것 같아, #b폴암#k 한 자루를 드렸어요. 수련하는데 유용하게 사용하실 수 있을 거예요. 그 무기를 가지고...");
            break;
        }
        case 6: {
            qm.sendNextPrev("마을에서 #b왼쪽#k으로 나가면 작은 수련장이 있는데, 그곳에 계신 #b푸오#k님을 만나 보세요. 치매가 오신 것 같아 걱정일 때도 있지만... 오랫동안 영웅을 만나길 고대하며 스킬을 연구해온 분이니 그 분의 도움을 받으면 느껴지는게 있을거예요.");
            break;
        }
        case 7: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}