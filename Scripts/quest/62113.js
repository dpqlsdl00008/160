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
            qm.sendNext("당신은 해낼 줄 알았어요. 의심해서 미안해요. 하지만 이해해 주세요. 이 곳을 지키기 위해 꼭 필요한 절차였을 뿐이에요.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("괜찮아요. 그런데 이곳에서 무슨 일이 일어나고 있는거죠?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("어느 날이었어요. 갑자기 상해에 먹구름이 덮쳐와 낮이 사라지고 긴 긴 밤이 지속되기 시작했죠. 그리고 100일째 되던 날에 갑자기 가축들이 난폭해져서는 날뛰기 시작했고, 급기야 마을 간판이나 신호등이 살아 움직이기 시작했습니다. 더군다나 전설로 전해져 오는 #d살아 움직이는 시체#k 강시가 나타났습니다.");
            break;
        }
        case 3: {
            qm.sendNextPrev("상해 주민들은 예원으로 몸을 피했고 다행히 제가 지키고 있는 이 구곡교는 강시가 넘을 수 없었습니다. 다리가 9번 굽어 있으면 다가 올 수 없다는 전설이 사실이었던 것이죠.");
            break;
        }
        case 4: {
            qm.sendNextPrev("하지만, 시간이 지나면서 인간의 말까지 흉내내고 관절을 굽힐 수 있는 강시까지 생겼다는 소문이 돌기 시작했고, 강시가 마을로 들어오지 못하게 강시가 싫어한다고 알려진 마늘과 팥죽을 들고 오게 시험한 것입니다. 아무튼 상해에 오신 것을 환영합니다. 마을 사람들과 이야기를 나눠보세요.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 170,598 exp");
            break;
        }
        case 5: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainItem(4034643, -1);
            qm.gainItem(4034656, -1);
            qm.gainExp(170598);
            break;
        }
    }
}