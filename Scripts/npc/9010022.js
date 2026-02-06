var say = "";

var value = 10200000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

var mapList = [
[09, 7910, "월묘의 떡", "pq_henesys_date"],
[10, 7912, "첫 번째 동행", "pq_kerningcity_date"],
[12, 7916, "독 안개의 숲", "pq_elin_date"],
[14, 7044, "해적 데비존", "pq_pirate_date"],
[15, 7922, "로미오와 줄리엣", "pq_romeo_julliet_date"],
[17, 7814, "드래곤 라이더", "pq_dragonrider_date"],
[21, 7926, "위험에 빠진 켄타", "pq_kenta_date"],
[22, 7928, "탈출", "pq_escape_date"],
];

function start() {
    for (i = 0; i < mapList.length; i++) {
        if (!cm.getPlayer().getOneInfoQuest(value, mapList[i][3]).equals(date)) {
            cm.getPlayer().updateQuest(mapList[i][1], "0");
        }
        say += "#" + mapList[i][0] + "#" + mapList[i][2];
    }
    cm.askMapSelection(say);
}

function action(mode, type, selection) {
    if (mode == 1) {
		switch (selection) {
			case 0: // Boss Party Quest / Ariant Coliseum
				cm.saveReturnLocation("MULUNG_TC");
				cm.warp(980010000, 3);
				break;
			case 1: // Mu Lung Training Center
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(925020000, 4);
				break;
			case 2: // Monster Carnival 1
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(980000000, 4);
				break;
			case 3: // Monster Carnival 2
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(980030000, 4);
				break;
			case 4: // Dual Raid
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(923020000, 0);
				break;
			case 5: // Nett's Pyramid
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(926010000, 4);
				break;
			case 6: // Kerning Subway
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(910320000, 2);
				break;
			case 7: // Happyville
				cm.saveReturnLocation("MULUNG_TC");
				cm.warp(209000000, 0);
				break;
			case 8: // Golden Temple
				cm.saveReturnLocation("MULUNG_TC");
				cm.warp(252000000, 0);
				break;
			case 9: // Moon Bunny
				cm.saveReturnLocation("MULUNG_TC");
				cm.warp(910010500, 0);
				break;
			case 10: // First Time Together
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(910340700, 0);
				break;
			case 11: // Dimensional Crack
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(221023300, 2);
				break;
			case 12: // Forest of Poison Haze
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(300030100, 1);
				break;
			case 13: // Remnant of the Goddess
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(200080101, 1);
				break;
			case 14: // Lord Pirate
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(251010404, 2);
				break;
			case 15: // Romeo and Juliet
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(261000021, 5);
				break;
			case 16: // Resurrection of the Hoblin King
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(211000002, 0);
				break;
			case 17: // Dragon's Nest
					cm.saveReturnLocation("MULUNG_TC");
					cm.warp(240080000, 2);
				break;
case 21:
cm.saveReturnLocation("MULUNG_TC");
cm.warp(923040000, 0);
break;
case 22:
cm.saveReturnLocation("MULUNG_TC");
cm.warp(921160000, 0);
break;
		}
    }
    cm.dispose();
}