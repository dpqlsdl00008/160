var status = -1;

var text = [
["요즘 살 찐 것 같아... 내 날개가 점점 과부하를 받고 있어."], 
["졸업생 언니들 너무 예쁜 것 같아. 물론 내가 더 예쁘지만. 호호홋!"], 
["하아, 나도 어서 남자 친구가 생겼으면..."], 
["남자 친구 생길 것 같지? 안 생겨"], 
["옆 반의 파이니는 내가 자기를 좋아하는 줄 아는 것 같아. 바보 아냐?"], 
["수업 끝나면 꿀 버섯 먹으러 갈래? 아무도 모르는 나만의 장소가 있어."], 
];

function start() {
    cm.gainItem(2431769, -1);
    var cRand = Packages.server.Randomizer.rand(0, 100);
    if (cRand > 50) {
        status = 1;
    }
    action (1, 0, 0);
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
            var tRand = Math.floor(Math.random() * text.length);
            cm.sendNextS(text[tRand], 8, 1500030, 1500030);
            break;
        }
        case 1: {
            cm.dispose();
            cm.sendNextPrevS("... 아무래도 이건 별로 쓸만한 정보가 아닌 것 같다. 다른 쪽지를 열어보자.", 2);
            break;
        }
        case 2: {
            cm.sendNextS("#e#b드디어 완성했어! 만드는 데만 한 달이나 걸렸지 뭐야. 나의 사랑이 그려진 초상화 뒤쪽에 꼭꼭 숨겨뒀지. 이따가 너한테만 살짝 보여줄게.#k#n", 8, 1500030, 1500030);
            break;
        }
        case 3: {
            cm.dispose();
            cm.forceStartQuest(32134, "1");
            cm.sendNextPrevS("무언가를 만들고 있었다, 그리고 그 위치는 초상화 뒤쪽? 이건 단서가 될 것 같다. #b마법사 쿠디#k와 상의해보자.", 2);
            break;
        }
    }
}