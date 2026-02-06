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
            if (qm.isQuestActive(24031) == true) {
                qm.sendNext("아... 이건... 정말 오랜만에 먹어보네요. 엘프들이 만들어 준 음식... 정말 옛날 생각이 나는 걸요? 검은 마법사와 싸우겠다면서 떠난 당신과 함께하지 못한 것이 어찌나 후회됐는지... 당신은 상상도 하지 못할 거예요.");
            } else {
                qm.dispose();
                qm.forceStartQuest();
            }
            break;
        }
        case 1: {
            qm.sendNextPrevS("돌아오겠다고 약속 했잖아요.", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("맞아요. 당신의 그 말, 그 한 마디만을 믿고 황폐해진 메이플 월드에서 엘프들의 귀환을 기다렸어요. 언젠가 돌아와 다시 한 번 손 내밀어 주리라 믿으며... 다시 한 번 당신과 함께 빅토리아를 달리겠다고...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("아직... 아직은 안 돼요. 메이플 월드는 아직 평화로워지지 않았어요. 싸움은 끝나지 않았고, 거기에 당신을 끌어들이고 싶지 않아요, 실피디아. 조금만 더 기다려 주겠어요?", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("기다릴 수 없어요. 메르세데스... 말했잖아요? 당신을 떠나 보낸 후 함께 하지 못한 것을 후회했다고... 수백 년 동안 얼어붙어 있는 엘프 마을을 보면서, 돌아오지 않는 메르세데스 당신을 기다리면서... 몇 번이고 생각했어요.");
            break;
        }
        case 5: {
            qm.sendNextPrev("검은 마법사가 두렵더라도 싸운다면 함께 싸웠어야 한다고... 당신 혼자 가게 내버려 두지 말았어야 한다고... 마지막까지 함께 했어야 한다고... 그렇게 말이예요.");
            break;
        }
        case 6: {
            qm.sendNextPrev("그러니 이번에는 놓치지 않겠어요. 검은 마법사와의 싸움이 끝나지 않았다면, 그 싸움의 끝까지 함께해요. 나의 친구, 메르세데스.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#s20021160# #b실피디아#k");
            break;
        }
        case 7: {
            qm.dispose();
            qm.gainItem(4032977, -1);
            qm.getPlayer().changeSingleSkillLevel(Packages.client.SkillFactory.getSkill(20021161), 1, 1, -1);
            qm.forceCompleteQuest();
            break;
        }
    }
}