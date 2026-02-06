var status = -1;

function end(mode, type, selection) {
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
            if (qm.isQuestActive(22564) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("음... 자네는 인간인 것 같은데 이 하플링의 마을까지는 어쩐 일... 허억! 자, 자네 옆에 있는 그 드래곤은 오닉스 드래곤?! 오닉스 드래곤이 맞는 겐가? 그렇다면 자네가 하인즈가 말한 바로 오닉스 드래곤을 가지고 있다던 인간인가?");
            }
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(헉, 하플링답게 타타모는 미르가 오닉스 드래곤임을 단번에 알아보았다. 하지만 하플링이니 미르에게 해를 끼칠 것 같지는 않다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("노, 놀랍군... 아직까지 살아있는 오닉스 드래곤이 정말 존재하다니, 아니, 그냥 살아있는 게 아니군. 아직 어린 걸 보면 깨어난지 얼마 되지 않은 것 같아. 이런 녀석이 정말로 존재 할 줄이야...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b(...그나저나 하인즈씨는 역시 미르가 오닉스 드래곤임을 눈치챘으면서 모르는 척 해주는 모양이다...)#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("그러고 보니! 오닉스 드래곤은 계약을 통해 완성되는 특수한 드래곤! 계약자가 없으면 드래곤은 커녕, 드레이크만도 못한 존재일 뿐인데, 자네의 드래곤은 무척 강해 보이는군! 설마 자네가...");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b네, 제가 드래곤 마스터입니다. 미르, 너도 인사해.#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrevS("난 마스터가 아닌 사람하고는 말하기 싫어.", 4, 1013000);
            break;
        }
        case 7: {
            qm.sendNextPrevS("#b윽... 죄송해요. 얘가 아직 낯을 가리거든요.#k", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("아닐세. 허허허... 오닉스 드래곤은 조심성이 많다더니 과연 그런 모양이군. 이렇게 실제로 눈으로 보게 될 줄이야...");
            break;
        }
        case 9: {
            qm.sendNextPrevS("#b...조심성이 많다면서 왜 오닉스 드래곤이 멸종했나요?#k", 2);
            break;
        }
        case 10: {
            qm.sendNextPrev("그건... 흠... 이미 잊혀진 이야기라 자네는 모르겠지만, 수백 년 전, 메이플 월드를 멸망시키려던 강하고도 무서운 자가 있었다네. 그가 오닉스 드래곤을 멸종시켰다고 들었네.");
            break;
        }
        case 11: {
            qm.sendNextPrevS("#b...왜 멸종시킨 건가요?#k", 2);
            break;
        }
        case 12: {
            qm.sendNextPrev("그것까지는 나로서도 알 수 없군. 오닉스 드래곤들은 그 자에 대항해 싸웠고, 그 결과 멸종했다고 들었네. 나 역시 그 당시에는 아직 어린 하플링이었던지라 그 이상은 듣지 못했지.");
            break;
        }
        case 13: {
            qm.sendNextPrev("하지만 멸종은 아니로군. 이렇게 살아있는 드래곤이 존재하니... 혹시 살아가는 게 불편하지는 않나? 이 리프레 마을에는 용을 기르기 위한 편의 시설이 잘 되어 있네. 이 곳에 살지 않겠나?");
            break;
        }
        case 14: {
            qm.sendNextPrevS("싫어. 난 마스터 옆이 좋아.", 4, 1013000);
            break;
        }
        case 15: {
            qm.sendNextPrev("오닉스 드래곤은 계약자와의 관계가 그 어떤 본능보다 중요하다더니 그것도 역시 맞는 말인 모양이군... 하긴, 계약자가 있는 오닉스 드래곤은 그 어떤 드래곤보다 큰 잠재력을 갖게 되니...");
            break;
        }
        case 16: {
            qm.sendNextPrev("알고 있나? 오닉스 드래곤은 계약자와 영혼부터 연결되어 있다네. 계약자의 힘으로 오닉스 드래곤은 강해지고, 계약자는 그 강해진 오닉스 드래곤의 힘을 자유롭게 사용 할 수 있지.");
            break;
        }
        case 17: {
            qm.sendNextPrev("허나, 계약자가 되고 싶다고 마음대로 될 수 있는 게 아니야. 오닉스 드래곤의 눈은 아주 날카로워, 강한 영혼을 가진 사람이 아니면 계약하지 않거든. 자네는 아주 강한 영혼을 가진 사람인 모양이군.");
            break;
        }
        case 18: {
            qm.sendNextPrev("멸종했다고 믿던 오닉스 드래곤을 다시 보게 되어 정말 기쁘군. 이왕이면 리프레에 몸을 맡겨 주었으면 했지만, 괜한 참견이겠지. 자네가 있는 걸 보면 어딘가에 또 다른 오닉스 드래곤이 있을 지도 몰라. 하인즈와 함께 포기하지 말고 찾아보도록 하지.");
            break;
        }
        case 19: {
            qm.sendNextPrev("뭔가 발견하면 하인즈가 연락 줄 걸세.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 340 exp\r\n#fUI/UIWindow.img/QuestIcon/10/0# 1 sp");
            break;
        }
        case 20: {
            qm.dispose();
            qm.gainExp(340);
            qm.getPlayer().gainSP(1, Packages.constants.GameConstants.getSkillBook(qm.getJob()));
            qm.forceCompleteQuest();
            break;
        }
    }
}