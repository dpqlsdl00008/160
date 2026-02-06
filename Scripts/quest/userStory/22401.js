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
            qm.sendNext("마스터, 무슨 일이야? 뭔가 하고 싶은 말이라도 있어? 응? 라이딩? 라이딩이라면 돼지나 새나 늑대 같은 걸 타고 다니는 걸 말하는 거지? 그런데 그게 왜?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b오닉스 드래곤도 라이딩을 할 수 있나 궁금해서. 어때? 가능 할 것 같아?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("오닉스 드래곤도 라이딩... 에엑?! 날 타고 다니겠다는 거야? 마스터의 파트너인 나를? 우와악, 마스터 너무해~ 난 애완 동물이 아니라고!");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b파트너니까 타고 다닐 수도 있지 뭐.#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("헉. 그렇게 쿨하게 말해버리다니. 좋아! 그럼 내가 힘들 때는 반대로 나도 마스터를 타고 다닐 거야! 그래도 좋아? 그럼 태워줄게.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b...하나 밖에 없는 마스터를 죽일 생각이야?#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("칫, 농담이였어. 예전의 나라면 모를까 지금 내가 마스터에게 타면 아마 마스터를 납짝쿵으로 만들어 버릴거야. 하지만 반대라면 별 문제 없겠지. 마스터는 덩치가 큰 것도 아니고...");
            break;
        }
        case 7: {
            qm.sendNextPrevS("#b그럼 라이딩 가능한 거야?#k", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("응. 마스터보다 내가 날아가는 게 훨씬 빠르니까 효율로 따져봐도 좋은 것 같아. 하지만 그냥은 안 되고 일단 두 가지 준비가 필요해.");
            break;
        }
        case 9: {
            qm.sendAcceptDecline("#b안장#k과 #b라이딩을 할 수 있는 스킬#k!. 내 등 위에 아무것도 없이 앉았다가는 엉덩이가 무사하지 않을 걸? 게다가 라이딩 스킬이 없으면 떨어질 수도 있고. 이 두 가지는 꼭 준비돼야 해. 준비 할 수 있겠어?");
            break;
        }
        case 10: {
            qm.dispose();
            qm.sendOkS("#b하인즈#k씨가 말한 드래곤 라이더에게 물어보면 안장과 스킬을 구할 방법을 알게 되지 않을까? 일단 #b하인즈#k씨에게 가보자.", 2);
            qm.forceCompleteQuest();
            break;
        }
    }
}