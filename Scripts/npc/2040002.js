var status = -1;

function start() {
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
            if (cm.isQuestFinished(3230) == true) {
                cm.dispose();
                cm.sendNext("\r\n#h #님 덕에 소중한 #b시계 추#k도 되 찾고 다른 세계의 몬스터도 퇴치 할 수 있었습니다. 다행히 지금은 더 이상의 다른 세계의 몬스터는 발견 되지 않고 있지요. 이렇게 도와주셔서 정말 감사했습니다. 그럼 루디브리엄에서 좋은 시간 보내시길 바랍니다!");
                return;
            }
            if (cm.isQuestActive(3230) == false) {
                cm.dispose();
                return;
            }
            cm.sendYesNo("흐음... 제 옆에 있는 #b장난감 병정 델브#k에게서 말씀 많이 들었습니다. 지루한 경비 업무를 위해 #b맛있는 호두#k를 구해다 주셨다고요. 흐음... 좋습니다. 이 안에는 아주 위험한 녀석이 숨어 들어가 있습니다. 녀석을 찾아내는 일을 당신께 맡기고 싶은데 한 번 들어 보시겠어요?");
            break;
        }
        case 1: {
            cm.sendYesNo("얼마 전 생긴 시간의 균열을 통해 침입 한 다른 세계의 몬스터가 루디브리엄 시계 탑의 시계 추를 훔쳐 도망가서는 이 문 너머에 있는 방에서 인형의 집으로 변신해 버렸습니다. 하지만 저희에게는 전부 똑같아 보여서 도저히 녀석을 찾아 낼 수가 없군요. 그럼 안으로 들어가 보시겠습니까?");
            break;
        }
        case 2: {
            cm.sendNext("\r\n좋습니다! 지금부터 제가 들여 보내 주는 방 안에는 여러 개의 인형의 집이 있습니다. 그 중 미묘하게 모양이 다른 인형의 집 하나를 찾아 때려서 부숴 주세요. 만일 정확히 찾았다면 안에서 #b시계 추#k를 찾을 수 있을 겁니다. 틀릴 경우에는 이곳으로 강제 이동 될 수 있으니 주의하세요.");
            break;
        }
        case 3: {
            cm.dispose();
            if (cm.getPlayerCount(922000010) != 0) {
                cm.sendNext("\r\n이런... 이미 다른 분이 방 안에 들어가셔서 모양이 다른 하나의 인형의 집을 찾고 계신 모양이군요. 죄송하지만 이 안은 한 번에 한 사람 밖에 들여 보내 줄 수 없습니다. 나중에 다시 찾아와 주세요.");
                return;
            }
            cm.removeAll(4031093);
            cm.resetMap(922000010);
            cm.warp(922000010, 0);
            break;
        }
    }
}