var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.sendNext("\r\n안 가면 말고... 척박하긴 해도 펭귄에겐 참 좋은 섬인데.");
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            cm.sendYesNo("혹시 내가 사는 섬인 리엔으로 갈 생각인 거야? 그렇다면 가격은 #b800 메소#k야. 어때? #b리엔#k으로 가고 싶어? 시간은 대략 #b10초#k정도 걸릴 거야. 출발하겠어?");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getMeso() < 800) {
                cm.sendNext("\r\n뭐야... 돈이 부족하잖아? 필요한 메소는 #b800 메소#k라고.");
                return;
            }
            cm.gainMeso(-800);
            cm.warp(200090060, 0);
            break;
        }
    }
}