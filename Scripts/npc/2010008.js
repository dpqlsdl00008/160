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
            cm.sendSimple("저는 길드 마크 제작 업무를 맡고 있습니다. 길드 마크는 길드장만 변경할 수 있답니다. 원하는 것이 있으세요?#Cgreen#\r\n#L0#길드 마크 추가 / 변경");
            break;
        }
        case 1: {
            if (cm.getPlayerStat("GRANK") != 1) {
                cm.dispose();
                cm.sendNext("\r\n길드장만 길드 마크를 추가하거나 변경 할 수 있답니다. 당신은 길드장이 아닌 것 같군요.");
                return;
            }
            cm.sendYesNo("길드 마크를 생성하는 데는 #r5,000,000 메소#k의 비용이 듭니다. 좀 더 설명을 하자면, 길드 마크는 길드마다 가질 수 있는 고유의 문장이라고 할 수 있죠. 현재 길드명이 표시되는 곳의 좌측에 붙게 됩니다. 어때요? 길드 마크를 만드시겠습니까?");
            // 현재 갖고 계신 길드 마크를 삭제하시면 새로운 길드 마크로 등록이 가능합니다. 길드 마크를 삭제하시겠습니까?
            break;
        }
        case 2: {
            cm.dispose();
            cm.genericGuildMessage(18);
            break;
        }
    }
}