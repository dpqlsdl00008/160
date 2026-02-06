var status = 0;
var coins = [
    { coinId: 4310009, required: 10, rewardId: 2450167, rewardQuantity: 1, name: "경험치 2배 쿠폰 (15분)" },
    { coinId: 4310010, required: 10, rewardId: 2450153, rewardQuantity: 1, name: "경험치 2배 쿠폰 (30분)" },
    { coinId: 4310334, required: 10, rewardId: 2435457, rewardQuantity: 1, name: "골드 애플" },
    { coinId: 4310036, required: 30, rewardId: 4310184, rewardQuantity: 10, name: "세계여행자의 코인" },
    { coinId: 4310059, required: 30, rewardId: 4310184, rewardQuantity: 10, name: "세계여행자의 코인" },
    { coinId: 4310058, required: 10, rewardId: 4310184, rewardQuantity: 10, name: "세계여행자의 코인" },
    { coinId: 4310064, required: 5, rewardId: 4310184, rewardQuantity: 500, name: "세계여행자의 코인" },
    { coinId: 4310065, required: 5, rewardId: 4310184, rewardQuantity: 500, name: "세계여행자의 코인" },
    { coinId: 4310031, required: 100, rewardId: 4310184, rewardQuantity: 10, name: "세계여행자의 코인" },
    { coinId: 4310031, required: 1000, rewardId: 4310184, rewardQuantity: 100, name: "세계여행자의 코인" },
    { coinId: 4310031, required: 50, rewardId: 1022232, rewardQuantity: 1, name: "블랙빈 마크" },
    { coinId: 4310031, required: 50, rewardId: 1012478, rewardQuantity: 1, name: "응축된 힘의 결정석" },
    { coinId: 4310031, required: 50, rewardId: 1132272, rewardQuantity: 1, name: "골든 클로버 벨트" },
    { coinId: 4310031, required: 70, rewardId: 1132296, rewardQuantity: 1, name: "분노한 자쿰의 벨트" },
    { coinId: 4310031, required: 50, rewardId: 1122076, rewardQuantity: 1, name: "카오스 혼테일의 목걸이" },
    { coinId: 4310031, required: 50, rewardId: 1122254, rewardQuantity: 1, name: "매커네이터 펜던트" },
    { coinId: 4310031, required: 50, rewardId: 1032241, rewardQuantity: 1, name: "데아 시두스 이어링" },
    { coinId: 4310031, required: 50, rewardId: 1113149, rewardQuantity: 1, name: "실버블라썸 링" },
    { coinId: 4310031, required: 70, rewardId: 1113282, rewardQuantity: 1, name: "고귀한 이피아의 반지" },
    { coinId: 4310031, required: 50, rewardId: 1162025, rewardQuantity: 1, name: "핑크빛 성배" },
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            cm.sendNext("안녕하세요? 보스를 처치 후 얻는 코인들을 모아오시면 여러 쿠폰으로 교환해드리고 있답니다~");
        } else if (status == 1) {
            var msg = "교환하실 코인을 선택해주세요.\r\n\r\n";
            for (var i = 0; i < coins.length; i++) {
                msg += "#L" + i + "#";
                msg += "#Cyellow##z" + coins[i].coinId + "# " + coins[i].required + "개#k 를\r\n\t\t";
                msg += "#Cblue##z" + coins[i].rewardId + "# " + coins[i].rewardQuantity + "개#k로 교환합니다.#l\r\n\r\n";
            }
            cm.sendSimple(msg);
        } else if (status == 2) {
            var selected = coins[selection];
            if (cm.haveItem(selected.coinId, selected.required)) {
                cm.gainItem(selected.coinId, -selected.required);
                cm.gainItem(selected.rewardId, selected.rewardQuantity);
                cm.sendOk("교환이 완료되었습니다!\r\n즐거운 메이플 되세요~");
            } else {
                cm.sendOk("코인이 부족하여 교환할 수 없습니다.");
            }
            cm.dispose();
        }
    }
}