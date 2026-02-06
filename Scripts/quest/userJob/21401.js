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
            qm.sendNext("...왜 이 꼴이냐고? ...별로 말하고 싶지 않은데... 아니, 주인인 너에게 숨길 수는 없겠지...");
            break;
        }
        case 1: {
            qm.sendNextPrev("...네가 얼음 속에 갇히고 나서 수백 년이 흐르는 동안... 나도 얼음 속에서 있었어. 긴 세월이었지. 주인 없이 지내기에는 너무... 그래서 마음 속에 어둠이 생기고 만 거야.");
            break;
        }
        case 2: {
            qm.sendNextPrev("하지만 네가 다시 깨어난 후로 어둠은 완전히 사라졌어. 주인이 돌아왔으니 이제 아쉬울 거 없다고 생각했지. 그렇게 털어버렸다고 생각하고 있었는데... 착각이었던 모양이야.");
            break;
        }
        case 3: {
            qm.sendAcceptDecline("부탁해. 아란... 나를 막아줘. 내 폭주를 멈출 수 있는 사람은 주인인 너뿐이야. 이제 더 이상 억누를 수 없어! 어떻게 해서든 폭주한 날 쓰러뜨려줘!");
            break;
        }
        case 4: {
            qm.dispose();
            if (qm.getPlayerCount(914020000) < 1) {
                qm.resetMap(914020000);
                qm.warp(914020000, 0);
                qm.forceStartQuest();
            } else {
                var quest = Packages.server.quest.MapleQuest.getInstance(21401).getName();
                qm.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            }
            break;
        }
    }
}

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
            qm.sendNext("고마워. 아란. 네 덕분에 폭주가 멈췄어. 정말 다행이야...라고 할 것 같냐! 주인이면 당연히 이 정도는 해줘야지!");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("그나저나 이제 보니 너 레벨이 정말 많이 높아졌네. 폭주한 나를 쓰러뜨릴 수 있을 정도면... 예전에 너의 능력들을 깨워 줘도 충분히 감당할 수 있을 것 같아.");
            break;
        }
        case 2: {
            if (qm.isQuestActive(21401) == true) {
                qm.changeJob(2112);
                qm.forceCompleteQuest();
            }
            qm.sendNext("잠자고 있던 네 스킬을 모두 깨웠어... 오랫동안 잊고 있던 것이니 다시 수련해야겠지만, 해내기만 한다면 도움이 될 거야.");
            break;
        }
        case 3: {
            qm.sendNextPrev("아, 하는 김에 그 동안 알게 된 메이플 용사라는 스킬을 스킬북으로 만들어서 너에게 전해 줬어. 예전에 너에겐 없던 스킬이지만 도움이 되지 않을까?");
            break;
        }
        case 4: {
            qm.sendNextPrev("하지만 그것만으로도 예전 너의 힘에는 아직 멀었어. 네가 잊어버린 스킬들이 스킬북으로 돌아다닌다는 소문을 들었는데... 그것들을 모두 찾아 익히면 진짜 너에 가까워질 거야.");
            break;
        }
        case 5: {
            qm.dispose();
            break;
        }
    }
}