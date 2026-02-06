var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            var say = "포션이 가득 들어있는 먼지가 뽀얗게 내려앉은 포션 상자다.";
            if (cm.isQuestActive(20031) == false) {
                cm.dispose();
                cm.sendNext(say);
            } else {
                if (cm.haveItem(4033194) == true && cm.haveItem(4033195) == true) {
                    cm.dispose();
                    cm.sendNextS("포션 상자를 챙겼으니 그만 1층으로 내려가야 겠다. 서둘러야지. 또 꾸물거리다가 늦으면 람버트 아저씨가 화를 내실테니까...", 6);
                } else {
                    cm.sendYesNo(say + " 정말 판매해도 되는 것인지 의심스러울 정도다. 포션 상자를 꺼내시겠습니까?");
                }
            }
            break;
        }
        case 1: {
            cm.dispose();
            cm.gainItem(4033194, 1);
            cm.gainItem(4033195, 1);
            cm.sendNextS("어? 이 편지는 뭐지? 람버트 아저씨한테 온 편지인가? 오래되어 보이는데? '크롬'으로부터...라. 크롬이 누구지? 어디선가 들어본 것 같은데? 기억이 나질 않네? 받는 사람이 적혀있지 않은데...일단 람버트 아저씨께 가져가 보자.", 6);
            break;
        }  
    }
}