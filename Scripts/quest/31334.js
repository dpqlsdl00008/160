var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("코나로부터 이야기는 들었어. 이것 참 신세를 졌군. 하하하! 좌우지간 고맙네. 안 그래도 몬스터들 때문에 골치가 아팠거든. 그래, 코나의 말로는 탐험을 위해서 이 곳에 왔다고 하던데... 원하는 것이 무엇인가?");
            break;
        }
        case 1: {
            qm.sendNextPrev("이야기를 듣고 싶은 건가? 그런 거라면 얼마든지 해주지.");
            break;
        }
        case 2: {
            qm.sendNextPrev("촌장님에게 들었겠지만, 이 곳은 우리 하프링들의 탐사 현장이야. 저 암벽 거인이 나타나기 전에도 쭈욱 탐사 현장이었고, 원래는 지금보다 몇 배는 더 많은 하프링들이 모여있었지.");
            break;
        }
        case 3: {
            qm.sendNextPrev("무엇을 탐사하고 있었냐고? 놀라지 말게. 이 곳의 산은 움직이고 있었어! 수 백년에 걸쳐서 조금씩 조금씩, 해마다 움직이고 있었단 말이야. 그 사실을 알면 누구라도 호기심이 동하지 않겠어? 내가 열 다섯 살이 되던 해부터 지금까지, 하얀 털이 회색 털이 되도록, 나는 탐사 단원들을 이끌고 이 곳을 연구해왔지.");
            break;
        }
        case 4: {
            qm.sendNextPrev("그러던 어느 날, 그 일이 벌어지고 만 거야. 우리가 그 동안 산이라고 믿어왔던 것이 사실은 산이 아니였던 것이야.");
            break;
        }
        case 5: {
            qm.sendNextPrev("정말로 끝이구나 싶었지. 대재앙이 벌어질 것이라 예상했지만 누구도 다치지 않았어. 저 암벽 거인은 최초의 움직임 이후로 더 이상 움직이지 않았거든. 무어라 입을 열어 말을 하는 것 같기도 한데 도대체 뭐라고 말하는지 알 수가 없어.");
            break;
        }
        case 6: {
            qm.sendNextPrev("그리고 그 날 이후, 해로운 몬스터들이 득실거리기 시작하더니 탐사 대원들은 하나 둘씩 떠나고 남은 것은 이 정도 뿐... 어때, 이 정도면 설명이 되었나?");
            break;
        }
        case 7: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}