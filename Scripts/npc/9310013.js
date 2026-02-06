var status = -1;
var map = [
["송산 마을", 701210000], 
["예원 정원", 701100000], 
];

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
	var chat = "어디로 가고 싶은가?";
	for (i = 0; i < map.length; i++) {
	    chat += "\r\n#L" + i + "##r" + map[i][0] + "#b(으)로 가고 싶습니다.#k";
	}
	cm.sendSimple(chat);
    } else if (status == 1) {
	jgys = selection;
	cm.sendYesNo("정말 #b" + map[selection][0] + "#k(으)로 가볼텐가? 요금은 무료라네!");
    } else if (status == 2) {
	cm.warp(map[jgys][1], 0);
	cm.dispose();
    }
}