var status = -1;

function start() {
    action(1, 0, 0);
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
            cm.sendNext("\r\n전문 기술에 대해 궁금하다... 내가 간단하게 알려주도록 합세. 지금 이 마을에는 #Cgreen#약초 채집, 채광, 장비 제작, 장신구 제작, 연금술#k 이렇게 총 5명의 장인이 있네. 우리 장인 협회에서는 함께 배울 때 시너지 효과가 나는 전문 기술 2개를 배우도록 하는 룰을 만들었지. 자네는 이 룰을 따라 #Cyellow#2개의 전문 기술#k을 선택하여 배울 수 있다네.");
            break;
        }
    }
}