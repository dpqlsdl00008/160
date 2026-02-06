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
            qm.sendNext("흠... 정말 특이하게 생긴 도마 뱀이네요. 마치 드래곤 같아요. 하지만 설마하니 드래곤을 타고 다니는 사람이 있을 리가 없죠. 참 독특한 라이딩을 하시네요.");
            break;
        }
        case 1: {
            qm.sendNextPrev("제가 지금까지 새나 돼지, 늑대 등을 타고 다니는 분들은 많이 봐서 그 생물들의 안장 사이즈라면 알고 있는데, 이 특이한 생물의 사이즌느 잘 모르겠네요. 일단 사이즈를 좀 재볼게요.");
            break;
        }
        case 2: {
            qm.sendNextPrev("#b(켄타는 미르의 허리 사이즈와 날개 간격, 머리 크기와 꼬리 둘레 등등을 꼼꼼하게 재었다. 그런데 발톱 길이는 왜 재는 거지? 입은 또 왜 벌려 보는 걸까?)#k");
            break;
        }
        case 3: {
            qm.sendAcceptDecline("흠... 기존에 있는 안장과는 사이즈가 전혀 달라서, 이 생물을 타고 다니시려면 아무래도 #b안장을 특별히 주문 제작#k 하셔야 할 것 같아요. 그래도 괜찮으시겠어요?");
            break;
        }
        case 4: {
            qm.sendNext("안장의 재료인 #b켄타 프리져의 물개 가죽 50개#k와 #b세프프의 진주 1개#k를 가져오시면 안장을 만들어 드릴게요. 수고비는... 저도 처음으로 도전하는 흥미로운 작업이니 특별히 세일해 드려서 #b1,000만 메소#k만 받도록 할게요.");
            break;
        }
        case 5: {
            qm.sendYesNo("재료와 수고비를 가져오시면 안장을 만들어 드리겠어요. 재료는 특별한 곳에서만 구할 수 있는데, 제가 보내드릴 수 있어요. 지금 떠나보시겠어요?");
            break;
        }
        case 6: {
            qm.dispose();
            qm.warp(923030000, 0);
            qm.forceStartQuest();
            break;
        }
    }
}