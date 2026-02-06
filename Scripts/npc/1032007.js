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
            cm.sendNext("\r\n안녕하세요? 저는 정거장 안내원 죠엘이라고 합니다. 빅토리아 아일랜드를 떠나 다른 지역으로 가실 생각이신가요? 이 역에서는 오시리아 대륙의 #b오르비스 스테이션#k으로 향하는 배가 #b정각을 기준으로 15분마다 출발#k하고 있습니다.");
            break;
        }
        case 1: {
            cm.sendNextPrev("\r\n오르비스로 가실거라면 오른편에 있는 #b체리#k에게 말을 걸어 주세요.");
            break;
        }
        case 2: {
            cm.sendNextPrev("\r\n사실, 얼마 전까지만 해도 비행선을 타려면 일정한 메소를 내고 표를 사야 했지만 마가티아 연금술사들이 획기적으로 마력이 절약되는 부유석을 개발해서 무료로 비행선을 운행하기로 결정했답니다. 네? 그럼 직원은 뭘 먹고 사냐고요? 걱정 마세요. 정부에서 지원해 주거든요.");
            break;
        }
        case 3: {
            cm.dispose();
            break;
        }
    }
}