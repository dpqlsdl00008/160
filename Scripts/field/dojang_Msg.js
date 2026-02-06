var status = -1;
var msg = [
"무릉 도장에 도전한 것을 후회하게 해주겠다! 어서 들어와 봐!",
"기다리고 있었다! 용기가 남았다면 들어와 보시지!",
"배짱 하나는 두둑하군! 현명함과 무모함을 혼동하지 말라고!",
"무릉 도장에 도전하다니 용기가 가상하군!",
"패배의 길을 걷고 싶다면 들어오라고!",
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
            cm.dispose();
            cm.floatMessage(msg[Packages.server.Randomizer.nextInt(msg.length)], 5120024);
            break;
        }
    }
}