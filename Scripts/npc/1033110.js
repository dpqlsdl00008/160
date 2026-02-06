var status = -1;
var music = [
["risingStar", "BgmEvent2/risingStar"],
["MoonlightShadow", "Bgm01/MoonlightShadow"], 
["When The Morning Comes", "Bgm02/WhenTheMorningComes"], 
["Flying In A Blue Dream", "Bgm06/FlyingInABlueDream"], 
["Fantasia", "Bgm07/Fantasia"],
["FairyTalediffvers", "Bgm09/FairyTalediffvers"], 
["Minar'sDream", "Bgm13/Minar'sDream"], 
["ElinForest", "Bgm15/ElinForest"], 
["TimeTemple", "Bgm16/TimeTemple"], 
["QueensGarden", "Bgm18/QueensGarden"], 
];

function start() {
    status = -1;
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
            var say = "에우렐에서 만들어 낸 아름다운 꽃 모양 오르골이다. 다양한 종류의 평화로운 음악을 들을 수 있는 모양인데...#b";
            for (i = 0; i < music.length; i++) {
                say += "\r\n#L" + i + "#" + music[i][0];
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.isQuestActive(24101) == true) {
                cm.forceStartQuest(24103, "1");
            }
            cm.changeMusic(music[selection][1]);
            break;
        }
    }
}