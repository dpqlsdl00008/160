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
            qm.sendSimple("당신은 모험가요? 이상할 정도로 강한 기운이 느껴지는군. 당신같은 사람이 이 마을에는 어쩐 일이시오? 음? 나에게 볼 일이 있는 모양이군. 무슨 일이시오?\r\n#L0##b설귀도의 지도를 갖고 계신가요?#k");
            break;
        }
        case 1: {
            qm.sendSimple("설귀도...? 예전에 한 참 먼 바다까지 고기 잡이를 하던 시절에 본 섬 말이로군. 맞소. 내가 가지고 있소. 해적들 때문에 멀리 나가지는 못하지만 지도들을 버리지는 않았으니.\r\n#L0##b설귀도의 지도를 주시면 안 될까요?#k");
            break;
        }
        case 2: {
            qm.sendSimple("그 섬 주변은 암초가 많고 파도는 거칠며 바람마저 강해 가기 힘들다오. 나 역시 그 섬에 직접 들어가 본 적은 없으니. 눈으로 덮인 거북이와 생김새가 비슷해 그런 이름이 붙었는데, 사실 그 이름을 아는 자 조차 거의 없다오. 그런데도?\r\n#L0##b그래도 지도를 받고 싶어요.#k");
            break;
        }
        case 3: {
            qm.sendAcceptDecline("...흠. 그렇게까지 말한다면 하는 수 없지. 내 부탁을 하나 들어주시거든 지도를 드리겠소. 부탁은 다름 아니라 백초 마을을 위협하는 빨간 코 해적단의 #r선장의 캡틴#k과 #r선장의 크루#k를 각 각 #r100마리#k씩 해치우는 것이라오. 하실 수 있겠소?");
            break;
        }
        case 4: {
            qm.sendYesNo("원한다면 해적들이 있는 소굴까지 보내드리리다. 지금 바로 출발해보시겠소?");
            break;
        }
        case 5: {
            qm.dispose();
            qm.warp(925110001, 0);
            qm.forceStartQuest();
            break;
        }
    }
}