importPackage(Packages.client);

var status = -1;
var sel = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == -1) {
            qm.sendNext("앗, 안 할 거야? 안 할 거야? 아... 아쉽다.");
            qm.dispose();
        }
        if (status == 0) {
            qm.sendNext("반 레온의 성의 교도관 아니를 아십니까? \r\n\r\n마을 주민들을 납치해서는 강제 노역을 시킨다고 하는데.. 가만히 보고만 있을 수 없군요. \r\n\r\n교도관 아니는 오전 10시부터 오후 10시 사이 매시각 정시에 성밖으로 순찰을 하러 나온다는 정보를 입수 했습니다. \r\n\r\n은밀히 침투해야 하는일인 만큼 정해진 인원만 함께하고 있습니다. 150명 정도가 적당하겠군요."); 
        } else if (status == 1) {
            qm.sendYesNo("지금 당장 이동하시겠습니까?");
        } else if (status == 2) {
            qm.warp(921130000, 0);
	    qm.Boat(60, 921130000, 921133000);
	    qm.dispose();
        } else {
            qm.dispose();
        }
    }
}
