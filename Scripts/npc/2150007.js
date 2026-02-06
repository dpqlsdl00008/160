var status = -1;

function start() {
    action (1, 0, 0);
}

function cHat() {
    var gHat = cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIPPED).getItem(-1);
    if (gHat != null) {
        if (gHat.getItemId() == 1003134) {
            return true;
        }
    }
    return false;
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            var say = "에델슈타인에서만 영업하는 에델슈타인 전용 택시입니다. 블랙윙 여러분을 보다 안전하고 신속하게 목적지까지 모셔다 드리고 있습니다. 블랙윙이 아니신 분요...? 뭐, 메소만 내시면 태워다 드리긴 합니다. 어디로 가실 건가요?#Cgreen#";
            if (cm.getMapId() != 310040200) {
                say += "\r\n#L0#레벤 광산 입구";
                say += "\r\n#L1#기계 무덤 헤이븐";
            } else {
                say += "\r\n#L2#에델슈타인";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            if (selection == 1) {
                cm.dispose();
                cm.getPlayer().dropMessage(1, "'기계 무덤 헤이븐'은 현재 구현 준비 중에 있습니다.");
                return;
            }
            var say = "#Cgreen#" + (selection == 1 ? "레벤 광산 입구" : "에델슈타인") +  "#k까지 가시려면 #Cgreen#10,000메소#k만 내십시오. 네? 비싸다고요? 블랙윙의 멤버가 아니면 할인가는 적용되지 않습니다. 정가로 모두 내시고도 가시겠나요?";
            if (cHat() == true) {
                say = "오오~ 당신은 블랙윙의 멤버시군요. 블랙윙의 멤버라면 특별히 할인해서 #Cgreen#3,000 메소#k에 모시고 있습니다. 어서 타시죠.";
            }
            cm.sendYesNo(say);
            jgys = selection;
            break;
        }
        case 2: {
            cm.dispose();
            var rMeso = (cHat() ? 3000 : 10000);
            if (cm.getMeso() < rMeso) {
                cm.sendNext("\r\n메소가 부족하시군요. 죄송하지만 요금을 지불하지 않으면 저희 택시를 이용하실 수 없습니다.");
                return;
            }
            cm.gainMeso(-rMeso);
            cm.warp(jgys == 0 ? 310040200 : 310000000, 1);
            break;
        }
    }
}