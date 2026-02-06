var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 12) {
            qm.sendNext("뭐야, 날 못 믿겠다는 거야? 우오오오~ 화난다!");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("드디어 깨어났다! 흐읍~ 이게 바로 세상의 공기! 오옷, 저것이 바로 태양이라는 것이군! 저게 나무! 저게 풀! 그리고 저게 꽃! 굉장해! 알 속에서 생각했던 것보다 훨씬 멋져! 그리고... 응? 네가 내 마스터인가? ...이건 또 기대와는 다른 얼굴인데?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b우우우우와아악! 말을 한다?!#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("...내 마스터는 이상한 사람이네. 계약은 이미 이루어졌으니 이제 와서 다른 주인을 택할 수도 없지. 에효, 앞으로 잘 부탁해.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b엥? 그게 무슨말이야? 앞으로 잘 부탁한다니...? 계약? 그건 뭐야?#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("무슨 말이라니... 네가 나를 알에서 깨워서 계약했잖아? 그러니 넌 내 마스터야. 그럼 당연히 나를 잘 돌봐줘서 내가 더 강한 드래곤이 되도록 해줘야지. 안 그래?");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b에에엑? 드래곤?! 네가 드래곤이라는 거야? 무슨 말인지 전혀 모르겠어! 도대체 계약이라는 게 뭔데? 마스터는 또 뭐고?#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("응? 무슨 말을 하는 거야. 넌 나하고 드래곤과 인간의 영혼을 하나로 묶는 계약을 했잖아. 그러니 내 마스터가 된 거고. 그것도 모르고 나와 계약한 거야? 하지만 이미 늦었어. 계약은 절대로 풀리지 않으니까.");
            break;
        }
        case 7: {
            qm.sendNextPrevS("#b에에에에엑! 자, 잠깐! 잘은 모르겠지만 그 말은... 내가 무조건 널 돌봐줘야 한다는 거야?#k", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("당연하지! ...잉? 뭐야? 그 억울하다는 얼굴은? 내 마스터가 된 게 싫다는 거야?!");
            break;
        }
        case 9: {
            qm.sendNextPrevS("#b아니, 싫은 건 아니지만 애완동물을 길러도 될지 모르겠는데...#k", 2);
            break;
        }
        case 10: {
            qm.sendNextPrev("애, 애완동무울~?! 나, 나를 애완동물로 생각한 거야?! 나를 뭘로 보는거야? 난 이래봬도 지상 최강의 생명체, 드래곤이라고!");
            break;
        }
        case 11: {
            qm.sendNextPrevS("#b...(그래봤자 쬐끄만 도마 뱀으로 밖에 안 보이는데.)#k", 2);
            break;
        }
        case 12: {
            qm.sendAcceptDecline("뭐야 그 눈길은? 마치 날 쬐끄만 도마 뱀이라도 되는 것처럼 보고 있잖아! 에이잇, 참을 수 없어! 내 힘을 증명해주마! 자, 각오는 되어 있겠지?");
            break;
        }
        case 13: {
            qm.sendNext("당장 내게 #r돼지#k들을 공격하라는 명령을 내려! 그럼 단번에 돼지를 잡아 보이겠어! 드래곤으로서의 내 능력을 증명해 보일 테다! 자, 돌격!");
            break;
        }
        case 14: {
            qm.sendNextPrev("아, 아니지 잠깐! 그 전에 먼저 AP 분배는 한 거야? 난 만스터가 가진 #bINT와 LUK#k의 영향을 받아! 내 진정한 능력을 보고 싶거든 AP를 제대로 분배하고 #b마법사 장비 착용#k 후에 명령 해!");
            break;
        }
        case 15: {
            qm.dispose();
	    qm.gainItem(2431876, 1);
            qm.gainItem(2431877, 1);
            qm.forceStartQuest();
            break;
        }
    }
}