var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.sendNextS("\r\n정말로 머쉬킹 왕국에 도움을 줘볼 생각이 없나? 언제든지 생각이 바뀌면 나를 찾아오게나.", 4, 1300003);
            qm.dispose();
            return;
        }
        if (status == 3) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.askAcceptDeclineS("이제 전직도 했고, 충분한 자격을 갖춘 것 같군. 자네에게 긴히 부탁 하고 싶은 것이 있는데 도와줄 수 있겠나?", 4, 1300003);
            break;
        }
        case 1: {
            qm.sendNextS("다름이 아니라 #b머쉬킹 왕국#k에 지금 큰 위기가 닥쳤다네. 머쉬킹 왕국은 헤네시스 근처에 위치한 버섯 왕국으로서 선왕인 머쉬킹은 평화를 사랑하고 현명한 정치를 펼치는 분으로 명망이 높은 분이셨지. 그런데 최근에 건강이 악화되어 그의 하나 뿐인 딸 #b비올레타 공주#k에게 왕위를 물려주고 요양 중이라고 알고 있었는데 무슨 변고가 생긴 모양이네.", 4, 1300003);
            break;
        }
        case 2: {
            qm.sendNextS("무슨 일인지 자세한 사정은 모르겠지만 큰 어려움이 닥친것 같네. 자네 정도면 위기에 빠진 머쉬킹 왕국을 구해낼 수 있을 걸세. 자, 여기 #b추천서#k를 줄테니 어서 머쉬킹 왕국으로 가보게나.#b경호대장#k을 찾아가면 될걸세.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i4032375# #z4032375#", 4, 1300003);
            break;
        }
        case 3: {
            if (qm.haveItem(4032375, 1) == false) {
                qm.gainItem(4032375, 1);
            }
            qm.sendYesNoS("그런데, 자네 머쉬킹 왕국의 위치를 알고 있나? 혼자 찾아 올 수 있으면 상관 없지만, 괜찮다면 입구까지 보내주겠네.", 4, 1300003);
            qm.forceStartQuest();
            break;
        }
        case 4: {
            qm.sendNextS("좋아, 머쉬킹 왕국으로 보내 주지. 행운을 빌겠네.", 4, 1300003);
            break;
        }
        case 5: {
            qm.dispose();
            qm.warp(106020001, 0);
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
            qm.sendNext("응? #b전직관의 추천서#k!!! 뭐야 네가.. 아니 당신이 우리 머쉬킹 왕국을 구해주러 온 용사란 말이오?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("그... 그런데요.");
            break;
        }
        case 2: {
            qm.sendNextPrev("음... 알겠소. 전직관들이 인정했을 정도면 용사님이 맞겠지요. 인사가 늦었군요. 저는 머쉬킹 왕실의 경호를 맡고있는 #b경호대장#k이라고 합니다. 보시다시피 지금은 이 곳 임시 거처의 경비와 요인들의 경호를 책임지고 있습니다. 상황은 좋지 않지만 어쨌든 머쉬킹 왕국에 오신 것을 환영합니다.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.gainItem(4032375, -1);
            qm.forceStartQuest(2311, "1");
            qm.forceCompleteQuest();
            break;
        }
    }
}