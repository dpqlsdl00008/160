var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 2) {
            qm.sendNext("아직도 무슨말인지 모르겠는가? 다시 한 번 나에게 말을 걸면 다시 설명해 주겠네.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("#t4032315#... #r이 인형에서는 아주 이상한 소리가 나고 있다네#k. 물론 자네의 귀로 들을 수는 없네. 주황버섯에게만 통하는 소리야. 이 영향으로 주황버섯들의 성격이 변해 버린 것 같군.");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("성격이 시니컬하게 변해 버린 주황버섯이 변하지 않은 다른 주황버섯들과 싸우고, 그러면서 주황버섯 전체가 전투 준비를 하게 된거네. #b#o1210102#들의 변화 원인은 바로 이 인형#k이야! 알아들었나?");
            break;
        }
        case 2: {
            qm.sendNextS("어쩌다 이런 일이 생긴 건지 원... 자연적으로 이런 인형이 생길 리는 없고, 누군가의 분명 일부러 저지른 짓 같은데... 한 동안 주황버섯 관리에 힘을 써야겠군.", 2);
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b(#o1210102#의 변화 원인을 알아 내었다. 이제 트루에게 가서 수집한 정보를 알려주자.)#k", 2);
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}