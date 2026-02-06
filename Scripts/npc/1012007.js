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
            cm.dispose();
            if (cm.haveItem(4031035) == false) {
                cm.sendNext("\r\n형이 펫 장애물 시설을 관리하라고 했지만... 이렇게 형이랑 멀리 떨어져 있으니 자꾸만 놀고 싶어진단 말야... 흐흐... 형이 안보는 것 같으니 조금만 놀아야 겠는걸~");
                return;
            }
            if (cm.getPlayer().getPet(0) == null && cm.getPlayer().getPet(1) == null && cm.getPlayer().getPet(2) == null) {
                cm.sendNext("\r\n음... 너 펫이랑 같이 온 거 맞아? 이건 펫을 위해 설치해 둔 장애물 이라고. 펫도 없이 여기까지 뭐하러 온거야. 어서 돌아가라고~!");
                return;
            }
            cm.gainItem(4031035, -1);
            cm.gainClosenessAll(2);
            cm.sendNext("\r\n어때? 이걸로 펫과 좀 더 친해진 것 같지? 다음에 또 시간이 난다면 몇 번이고 장애물 연습을 시켜 보라고~ 물론 우리 형에게 먼저 허락을 받은후에 말야.");
            break;
        }
    }
}