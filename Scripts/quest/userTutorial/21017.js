var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 5) {
            qm.sendNextS("#b(두려운 마음에 거절을 눌러 버렸다. 하지만 이대로 도망칠 수도 없는데... 마음을 진정시키고 다시 말을 걸자.)#k", 2);
            qm.dispose();
            return;       
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("이제 얼추 몸이 풀리신 것 같네요. 이럴 때야말로 더욱 혹독하게 단련을 해줘야 완벽한 기초 체력을 갖게 되는 법. 자, 그럼 계속해서 기초 체력 단련을 해보죠.");
            break;
        }
        case 1: {
            qm.sendNextPrev("그럼 이번에는 #b눈 덮인 벌판3#k에 가서 #r무루무루#k를 퇴치해 보죠. 한... #r20마리#k 정도 퇴치하면 체력 단련에 도움이 될 거예요. 자, 어서 가서... 응? 뭔가 하시고 싶은 말씀이라도 있으신가요?");
            break;
        }
        case 2: {
            qm.sendNextPrevS("...왠지 점점 숫자가 늘어나고 있지 않아?", 2);
            break;
        }
        case 3: {
            qm.sendNextPrev("늘어나고 있어요. 어머, 혹시 20마리로는 부족하신 건가요? 그럼 한 100마리쯤 퇴치해 볼까요? 아니, 아니지. 이왕 수련하는 건데 슬리피우드의 누구처럼 999마리 잡기 정도는...");
            break;
        }
        case 4: {
            qm.sendNextPrevS("아, 아냐. 이대로도 충분하다.", 2);
            break;
        }
        case 5: {
            qm.sendAcceptDecline("어머어머, 그렇게 사양하실 거 없어요. 빨리 강해지고 싶으신 영웅님의 마음이라면 충★분★히★ 알고 있는 걸요. 역시 영웅님은 대단하시다니...");
            break;
        }
        case 6: {
            qm.sendNextS("#b(더 이상 듣고 있다가는 정말 999마리 퇴치를 하게 될 것 같아 수락해 버렸다.)#k", 2);
            break;
        }
        case 7: {
            qm.sendNextPrev("그럼 무루무루 20마리 퇴치를 부탁 드릴게요.");
            break;
        }
        case 8: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}