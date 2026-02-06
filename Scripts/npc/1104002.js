var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        if (status == 5) {
            cm.dispose();
            return;
        }
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        if (status == 5) {
            status++;
        } else {
            status--;
        }
    }
    switch (status) {
        case 0: {
            cm.sendNext("\r\n아하하하! 시그너스 기사단이라고 해서 대단한 줄 알았는데... 별 거 아니잖아? 신수의 힘이 아무리 강해도 소용 없어. 단단한 댐도 작은 구멍 하나에 무너지는 법. 머리만 잘 쓰면 이렇게 쉽게 함락 할 수 있는 법이지.");
            break;
        }
        case 1: {
            cm.sendNextPrev("\r\n뭘 어떻게 했냐고? 간단해. 똑똑하지만 고지식 한 기사님에게 가짜 정보를 흘렸지. 검은 마녀가 용의 숲에서 여제에게 저주를 걸고 있다고 말이야. 뭐, 완전히 가짜는 아니었어. 저주를 건 것은 사실이었으니까. 다만...");
            break;
        }
        case 2: {
            cm.sendNextPrev("\r\n저주가 발동 되는 건, 저주의 매개체가 에레브 안으로 들어 갔을 때, 즉, 듀나미스가 조사를 완료하고 저주의 매개체를 이 섬으로 들여 온 순간이었거든.");
            break;
        }
        case 3: {
            cm.sendNextPrev("\r\n에레브 밖에서 아무리 저주를 걸어도 신수의 보호에 의해 튕겨 나가는 걸 뻔히 아는 데, 괜히 힘을 뺄 필요는 없잖아. 이렇게 친절하게 저주의 매개체를 운반해 주는 사람이 있는 데 말이야. 아하하하!");
            break;
        }
        case 4: {
            cm.sendNextPrev("\r\n기사단이라고 해도 고작 열 몇 살 짜리 어린 애를 떠 받드는 경험 부족 한 애송이들이 대 부분. 수 백년 간 검은 마법사님을 기다려 온 우리 블랙 윙의 앞을 가로 막기엔 턱 없이 부족 해.");
            break;
        }
        case 5: {
            cm.askAcceptDecline("자, 그만 너도 물러서. 괜히 힘 빼긴 싫으니까 말이야.");
            break;
        }
        case 6: {
            cm.sendNext("\r\n호오... 겨우 너 혼자서 싸우겠다 말이야? 기사 단장도 아닌, 일반 기사인 주제에? 감히... 블랙 윙의 에레오노르님을 어떻게 보고!");
            break;
        }
        case 7: {
            cm.dispose();
            cm.resetMap(cm.getMapId());
            cm.spawnMonster(9001010, -273, 88);
            break;
        }
    }
}