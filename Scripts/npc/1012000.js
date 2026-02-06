/*
 본스크립트의 저작권은 아래 지인에게 있습니다.
 포딜(4_dill) / 허락없이 배포할시 나뻐할꼬얌.
*/

var status = 0;
var maps = new Array("100040000", "100040400", "106000110", "103040400", "222010000", "260020700", "261020300", "261020400", "261010102", "251010402", "240020000", "240020400", "240020200", "240030000", "211060100", "211060300", "270020100", "240040510", "240040511");
var mapNames = new Array("#b[추천 Lv 10 ~ 18] 골렘의 사원", "#b[추천 Lv 18 ~ 22] 골렘의 사원4", "#b[추천 Lv 22 ~ 30] 불타버린땅2", "#b[추천 Lv 30 ~ 45] 7층 8층 A구역", "#b[추천 Lv 45 ~ 62] 까막산입구", "#b[추천 Lv 62 ~ 70] 사헬지대1", "#d[추천 Lv 70 ~ 77] 연구소 C-1 구역", "#d[추천 Lv 77 ~ 85] 연구소 C-2 구역", "#d[추천 Lv 85 ~ 100] 연구소 202호", "#d[추천 Lv 100 ~ 110] 빨간코 해적단 소굴2", "#d[추천 Lv 110 ~ 120] 붉은 켄타우로스의 영역 ", "#d[추천 Lv 110 ~ 120] 푸른 켄타우로스의 영역", "#d[추천 Lv 110 ~ 120] 검은 켄타우로스의 영역", "#r[추천 Lv 120 ~ 130] 용의 숲 입구", "#r[추천 Lv 130 ~ 140] 성벽 아래1", "#r[추천 Lv 130 ~ 140] 성벽 아래2", "#r[추천 Lv 140 ~ 150] 후회의길 1", "#r[추천 Lv 140 ~ 150] 죽은 용의 둥지", "#r[추천 Lv 150 ~ 160] 남겨진 용의 둥지");
var maplist = new Array("104000000", "120000000", "100000000", "102000000", "101000000", "103000000", "105000000", "130000000", "310000000", "200000000", "220000000", "222000000", "250000000", "240000000", "260000200", "261000000", "230000000", "211000000", "251000000", "270000100");
var map = new Array("리스항구", "노틸러스", "헤네시스", "페리온", "엘리니아", "커닝시티", "슬리피우드", "에레브", "에델슈타인", "오르비스", "루디브리엄", "아랫마을", "무릉", "리프레", "아리안트", "마가티아", "아쿠아리움", "엘나스", "백초마을", "시간의 신전");

var warp = new Array("마을워프", "사냥터워프");
var selectedMap = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status == 0 && mode == 0) {
            cm.dispose();
            return;
        } else if (status == 1 && mode == 0) {
            cm.sendNext("또 오세요~");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendNext("\r\n안녕하세요?\r\n저는 무료로 어디든지 이동시켜드리니 많이 이용해주세요!");
	} else if (status == 1) {
		var msg = "#h #님 반가워요! 어떤 기능을 이용하시겠어요?#n";
		for (var i = 100; i < 102; i++) {
		msg += "\r\n#b#L" + i + "# " + warp[i-100] + "#l";
		}
		cm.sendSimple(msg);
	} else if (status == 4) {
          	var place = "#h #님. 어디로 이동하시겠어요?#n";
		for (var i = 0; i < maplist.length; i++) {
		place += "\r\n#b#L" + i + "# " + map[i] + "#l";
                }
           	cm.sendSimple(place);
        } else if (status == 5) {
		    selectedMap = selection;
                    cm.sendYesNo("정말로 이 마을로 이동하시겠습니까?");
        } else if (status == 6) {
                        cm.warp(maplist[selectedMap], 0);
                        cm.dispose();
	} else if (status == 7) {
          	var where = "#h #님. 어디로 이동하시겠어요?#n";
		for (var i = 0; i < maps.length; i++) {
		where += "\r\n#b#L" + i + "# " + mapNames[i] + "#l";
                }
           	cm.sendSimple(where);
        } else if (status == 8) {
		    selectedMap = selection;
                    cm.sendYesNo("정말로 이 사냥터로 이동하시겠습니까?");
        } else if (status == 9) {
                        cm.warp(maps[selectedMap], 0);
                        cm.dispose();
	} else if (selection == 100) {
		cm.sendNext("\r\n마을 워프 리스트를 불러옵니다.")
		status++;
	} else if (selection == 101) {
		cm.sendNext("\r\n사냥터 워프 리스트를 불러옵니다.")
		status++;
		status++;
		status++;
		status++;
		}
	}
}