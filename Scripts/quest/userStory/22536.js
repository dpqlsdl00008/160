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
            qm.sendSimple("응? 넌 못 보던 얼굴 같은데. 커닝시티에는 무슨 일이야? 혹시 도적이 되려고 온 거야?\r\n#L0##b(혹시 약초냄새 나는 사람을 보지 못했냐고 물었다.)#k");
            break;
        }
        case 1: {
            qm.sendSimple("약초냄새? 글쎄. 약초를 들고 다니는 사람이 있나? 다들 포션만 쓰잖아? 그런데 약초는 왜 물어보는 건데? 사려고?\r\n#L0##b(사비트라마의 일에 대해 설명해 주었다.)#k");
            break;
        }
        case 2: {
            qm.sendSimple("흠... 약초 도둑이라. 그래서 물어보는 거였구나... 음?! 잠깐잠깐잠깐! 그럼 너 혹시 도둑이 커닝시티 사람일 거라고 생각하는 거야?\r\n#L0##b커닝시티는 도둑의 마을이잖아?#k");
            break;
        }
        case 3: {
            qm.sendSimple("도둑이 아니야! 도적의 마을이라고! 와악! 열 받아! 너 우리 커닝시티 도적들을 뭘로 보는 거야? 물론 도적들이 좀 비열하고 좀 치사하고 좀 야비하고 좀 음흉한 건 사실이야! 하지만 다른 사람의 생계를 위협해 가며 훔치지는 않는다고!\r\n#L0##b정말이야?#k");
            break;
        }
        case 4: {
            qm.sendSimple("당연하지! 원래부터 오해를 받고 있는 건 알았지만 이런 말을 듣게 되다니... 커닝시티에서 태어나 지금까지 자란 나로서는 참을 수 없는 모욕이야! #r너는 나에게 모욕감을 줬어!#k 절대 커닝시티 사람은 범인이 아니야! 절대로 !\r\n#L0##b그, 그래? 그럼 도둑은 어디 있을까? 장동민씨.#k");
            break;
        }
        case 5: {
            qm.sendAcceptDecline("나도 몰라! 하지만 감히 커닝시티 사람에게 누명을 씌우다니, 절대 용서할 수 없어, 그 도둑! 내가 직접 사비트라마의 약초를 훔쳐간 도둑을 찾겠어! 그래서 커닝시티의 명예를 되찾고 말겠다! 알았어? 내가 찾을 거라고!\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 4000 exp");
            break;
        }
        case 6: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.sendOk("좋아! 그럼 이제부터 #b내가 그 도둑이라는 녀석의 조사일을 맡을테니, 넌 얌전히 기다리고 있어.#k 조사 결과가 나오면 연락 줄게. 찾아내고야 말겠다!");
            break;
        }
    }
}