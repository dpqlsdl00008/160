var status = -1;

var gMap = [
    [801000000, "일본 - 쇼와 마을"],
    [800000000, "일본 - 버섯 신사"],
    [231000000, "일본 - 벚꽃 성"],
    [807000000, "일본 - 모미지 언덕"],

    [701000000, "중국 - 상해 와이탄"],
    [701210000, "중국 - 송산 마을"],
    [701100000, "중국 - 예원 정원"],

    [540000000, "싱가포르 - 중심 업무 지구"],
    [541020000, "싱가포르 - 울루 시티"],
    [541010000, "싱가포르 - 유령선"],

    [550000000, "말레이시아 - 유행 지역 도시"],
    [551000000, "말레이시아 - 캄풍 마을"],
];

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 1) {
            cm.sendNext("\r\n아직 이곳에 볼 일이 남아 있으신가 보죠? 여행을 떠나고 싶으시다면 언제든지 제게 다시 찾아와 주세요.");
        }
        cm.dispose();
        return;
    }

    switch (status) {
        case 0: {
            var say = "안녕하세요~ 새로운 세계로 여행을 떠나보고 싶으세요?\r\n그렇다면 저희 세계 여행 서비스를 이용해보시기 바랍니다!\r\n#Cblue#(작동 안되는 포탈 및 NPC가 다수 있으니 양해 바랍니다.)";
            for (i = 0; i < gMap.length; i++) {
                var color = "#k"; // 기본값
                if (gMap[i][1].startsWith("일본")) {
                    if (i == 0) say += "\r\n"; // 일본 처음 등장 시 줄바꿈
                    color = "#d";
                } else if (gMap[i][1].startsWith("중국")) {
                    if (gMap[i - 1][1].startsWith("일본")) say += "\r\n";
                    color = "#Cyellow#";
                } else if (gMap[i][1].startsWith("싱가포르")) {
                    if (gMap[i - 1][1].startsWith("중국")) say += "\r\n";
                    color = "#Cgreen#";
                } else if (gMap[i][1].startsWith("말레이시아")) {
                    if (gMap[i - 1][1].startsWith("싱가포르")) say += "\r\n";
                    color = "#Cgray#";
                }
                say += "\r\n#L" + i + "#" + color + gMap[i][1] + "#k";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            uPrice = 3000;
            if (Packages.constants.GameConstants.isBeginnerJob(cm.getJob())) {
                uPrice = 300;
            }
            var v2 = (selection == 2 || selection == 3 || selection == 4 || selection == 6 || selection == 9) ? "으" : "";
            cm.sendYesNo("정말 #b" + gMap[selection][1] + "#k" + v2 + "로 여행을 떠나 보시고 싶으세요? 요금은 #b" + cm.getPlayer().getNum(uPrice) + " 메소#k이며 저를 통해 언제든지 이곳으로 다시 돌아 오실 수 있습니다.");
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getMeso() < uPrice) {
                cm.sendNext("\r\n흐음. 메소가 부족하신 것 같은데요? 요금이 없으시다면 선택 하신 곳으로 보내 드릴 수 없답니다.");
                return;
            }
            cm.gainMeso(-uPrice);
            cm.saveLocation("WORLDTOUR");
            cm.warp(gMap[v1][0], 0);
            break;
        }
    }
}