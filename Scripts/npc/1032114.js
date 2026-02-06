var status = -1;

var jobName = "마법사#k#n를";
var jobMap = 910120000;

function start() {
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
            var say = "모험가들이여! 이곳은 #e#r20 레벨 이하의 " + jobName + " 위한 수련장입니다. 혼자서 수련하는 것도 강해지는 방법 중 하나지만 다른 사람과 같이 수련을 하면 좀 더 빠르게 강해 질 수 있을 것 입니다. 수련하고 싶은 방을 골라 주세요.#b";
            say += "\r\n#L0##m" + jobMap + "# (" + cm.getPlayerCount(jobMap) + " / 5)";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getPlayerCount(jobMap) > 4) {
                return;
            }
            cm.warp(jobMap, "outportal");
            break;
        }
    }
}