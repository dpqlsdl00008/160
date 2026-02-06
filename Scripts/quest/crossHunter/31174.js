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
            var say = "아란과 우리 종족을 구했으니 이제 메르세데스의 차례네. 오는 길에 그녀가 쓰러져 있는 것을 보았겠지?";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "메르세데스여. 이젠 네 자신을 구할 때야.";
            }
            qm.sendNext(say);
            break;
        }
        case 1: {
            var say = "네. 아란보다도 더 안 좋아 보였어요. 그녀를 어떻게 도우면 되죠?";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "그게 무슨 소리지? 내 기억이 틀리지 않는다면 난 그냥 일어났던 것 같은데.";
            }
            qm.sendNextPrevS(say, 2);
            break;
        }
        case 2: {
            var say = "메르세데스는 이 세상의 모든 엘프들을 어깨에 짊어 진 만큼 더 강력한 저주에 시달리고 있네. 그녀가 이대로 쓰러진다면 엘프들 역시 모두 사라지겠지. 이를 막기 위해서 최소한 메르세데스가 다시 일어설 수 있도록 도와줘야 하네. 그러기 위해 자네의 힘을 잠시 빌려도 좋은가?";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "이 곳에 올 때 까지의 기억은 있나?";
            }
            qm.sendNextPrev(say);
            break;
        }
        case 3: {
            var say = "네. 기꺼이 돕겠어요.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "... 아니.";
            }
            qm.sendNextPrevS(say, 2);
            break;
        }
        case 4: {
            var say = "그렇다면 자네의 기운을 좀 빌리도록 하지. 갑자기 자네의 체력이 떨어져도 놀라지 말게.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "그렇다면 잠자코 내 말을 듣게. 내 꼴이 이 모양이니 일단 자네의 기운을 좀 빌리도록 하지. 갑자기 체력이 떨어져도 놀라지 말게.";
            }
            qm.sendNextPrev(say);
            break;
        }
        case 5: {
            var say = "놀랍군. 자네 안의 능력은 이제 것 만나 온 어떤 영웅들 못지 않아. 잠시나마 내 힘의 대부분을 되 찾았어. 이제 이 힘으로 메르세데스를 구할 수 있는 결정을 만들어 주겠네. 잠시만 기다리게.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "잠시만 기다리게. 자네의 힘이 큰 만큼 저주의 힘도 강하니까 말야.";
            }
            qm.sendNextPrev(say);
            break;
        }
        case 6: {
            var say = "내가 건내 준 아프리엔의 정수를 가지고 가서 메르세데스를 깨우도록 하게. 그녀를 가볍게 한 번 건드리기만 하면 될 거야.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "내가 건내 준 아프리엔의 정수를 가지고 가서 가볍게 한 번 건드리기만 하면 될 거야. 너무 세게 해서 바로 깨워 버리면 곤란 해. 과거의 자신과 직접 마주 보는 것은 좋지 않아.";
            }
            qm.sendNextPrev(say);
            break;
        }
        case 7: {
            qm.dispose();
            qm.gainItem(4033082, 1);
            qm.forceStartQuest();
            break;
        }
    }
}