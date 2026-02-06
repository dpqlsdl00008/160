var status = -1;

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
            var say = "몸이 근질거리는 모험가라면 몬스터 카니발에 참가해 보라구.";
            say += "\r\n#L0##b몬스터 카니발에 대해서 알고 싶어요.#k";
            say += "\r\n#L1##b반짝이는 메이플 코인을 다른 아이템과 교환하고 싶어요.#k";
            say += "\r\n#L2##b메이플 코인을 반짝이는 메이플 코인으로 바꾸고 싶어요.#k";
            say += "\r\n#L3##b슈피겔만의 목걸이를 받고 싶어요.#k";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    cm.dispose();
                    cm.sendNext("#b몬스터 카니발#k은 다른 사람들과 파티를 맺어서 누가 더 몬스터를 빠르게 많이 잡아 높은 점수를 얻는 지를 겨루는 곳이지. 다른 사람들과 한 팀이 되서, 몬스터를 소환하거나, 스킬을 사용해서 몬스터를 잡으면 돼. 예전에는 내가 직접 보내주었지만, 이제는 #b15분#k, #b45분#k마다 초대장을 보내주고 있어. 자네도, 관심이 있다면, 놓치지 말고 초대장을 잘 받으라구.");
                    break;
                }
                case 1: {
                    cm.dispose();
                    var say = "반짝이는 메이플 코인을 모아왔군? 어떤 아이템을 줄까?";
                    say += "\r\n#L0##i1122162# #b#z1122162# #r(반짝이는 메이플 코인 100개 필요)#k";
                    say += "\r\n#L1##i1102327# #b#z1102327# #r(반짝이는 메이플 코인 150개 필요)#k";
                    cm.sendSimple(say);
                    break;
                }
                case 2: {
                    cm.dispose();
                    var say = "자넨 #b메이플 코인#k을 " + cm.itemQuantity(4001129) + "개 만큼 가지고 있군. 이 정도면 #b반짝이는 메이플 코인#k을 " + cm.itemQuantity(4001129) + "개 만큼 바꿔줄 수 있는데 얼만큼 받고 싶어?　\r\n　";
                    cm.sendGetNumber(say, 1, 0, cm.itemQuantity(4001129));
                    break;
                }
                case 3: {
                    cm.dispose();
                    var say = "어떤 걸 받고 싶어?";
                    say += "\r\n#L0##i1122007# #b#z1122007# #r(메이플 코인 50개 필요)#k";
                    say += "\r\n#L1##i1122058# #b#z1122058# #r(반짝이는 메이플 코인 150개 필요)#k";
                    cm.sendSimple(say);
                    break;
                }
            }
        }
    }
}