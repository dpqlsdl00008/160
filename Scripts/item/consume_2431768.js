var status = -1;

var text = [
["아무래도 옆 반의 트레시는 나를 좋아하는 것 같아. 이놈의 인기란..."], 
["졸려... 수업 언제 끝나?"], 
["에포니, 오후 수업 땡땡이 치지 않을래? 같이 놀러가자."], 
["네가 더 멍청이다! 무지개 반사."], 
["이 쪽지를 받는 사람은 멍청이다! 무한 반사."], 
["눈부셔... 교감 선생님 머리 완전 눈부셔..."], 
["승부다, 파이니! 내 날개가 더 빠르다는 것을 증명해주마. 오후에 한 판 붙자."], 
["얼레리 꼴레리. 도시는 여자애들이랑 손 잡았대요."], 
];

function start() {
    cm.gainItem(2431768, -1);
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
            cm.sendNextS(text[tRand], 8, 1500029, 1500029);
            break;
        }
        case 1: {
            cm.dispose();
            cm.sendNextPrevS("... 아무래도 이건 별로 쓸만한 정보가 아닌 것 같다. 다른 쪽지를 열어보자.", 2);
            break;
        }
        case 2: {
            cm.sendNextS("#e#b우리의 비밀스러운 물건은 책장 밑에 잘 챙겨놨음. 혹시라도 교감 선생님께 들키면 절대 안 됨.#k#n", 8, 1500029, 1500029);
            break;
        }
        case 3: {
            cm.dispose();
            cm.forceStartQuest(32133, "1");
            cm.sendNextPrevS("책장 밑, 비밀스러운 물건...? 이건 단서가 될 것 같다. #b마법사 쿠디#k와 상의해보자.", 2);
            break;
        }
    }
}