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
            var say = "...너는 누구냐?";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "...메르세데스?! ...내 눈이 침침해진 건가. 메르세데스가 둘이라니... 그렇군. 어떻게 미래에서 이 시대로 올 수 있었지? 그건 너나 다른 영웅들의 힘으로도 불가능 한 일이야.";
            }
            qm.sendNext(say);
            break;
        }
        case 1: {
            var say = "#b(우와, 진짜 크다.)#k\r\n\r\n전 #h #입니다. 마하에게서 안부를 전해 달라는 부탁을 받았어요.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "역시 아프리엔다워. 솔직히 조금은 놀랄 것이라고 기대했는데. 아카이럼이 이 시대로 오는 균열을 뚫었어. 나는 아카이럼이 꾸미는 음모를 막기 위해 쫓아 왔지. 여기서 너를 만나게 될 줄은 몰랐어.";
            }
            qm.sendNextPrevS(say, 2);
            break;
        }
        case 2: {
            var say = "마하... 그는 어떻게 되었지? 아란과 함께 있었나?";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "...그런가. 일이 그렇게 되었군. 과거의 네가 정신을 차리지 못하는 시간대에 미래의 너를 만나다니. 언제 생각해도 시간의 짜임새는 놀랍기 그지 없군. 잠시 생각 할 것이 있으니 조금 뒤에 말을 걸어 주겠나?";
            }
            qm.sendNextPrev(say);
            break;
        }
        case 3: {
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                qm.dispose();
                qm.forceCompleteQuest();
                return;
            }
            qm.sendNextPrevS("아란을 데리고 리엔 섬으로 갔어요. 그 곳에서 아란을 봉인한다고 하더군요.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("그렇군. 내 친우를 구해줘서 고맙네. 미래의 존재여.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("...!! 어떻게 아셨죠?", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("이래 봬도 오닉스 드래곤의 왕이니까. 자네라면 아란 뿐만 아니라 우리 또한 도울 수 있을 것 같군. 나와 프리드, 아란과 마하, 그리고 메르세데스 등은 힘겨운 난관을 돌파 한 끝에 마침내 검은 마법사 앞에 섰네. 검은 마법사의 실체는 그 때 까지 그 누구도 본 적이 없었지만 그에게서 느껴지는 강렬한 악의만으로도 그가 바로 검은 마법사라는 것을 확신 할 수 있었지.");
            break;
        }
        case 7: {
            qm.sendNextPrev("하지만 그의 압도적인 마력이 어딘가 크게 흩어져 있었네. 바로 검은 마법사의 오른 팔이었던 데몬 슬레이어가 한 발 앞서 검은 마법사와의 사투를 벌였기 때문이었어. 데몬 슬레이어는 자신의 가족을 해친 검은 마법사를 용서 할 수 없었던 거지. 우리는 그 틈을 놓치지 않고 바로 전투에 들어섰네.");
            break;
        }
        case 8: {
            qm.sendNextPrev("그럼에도 불구하고 검은 마법사는 정말 강력하더군. 무수한 전투를 승리로 이끈 영웅들이 힘을 하나로 모으지 않았더라면 결코 그를 쓰러뜨릴 수 없었겠지. 우리 모두 큰 피해를 입었지만 간신히 그를 봉인하는 데 성공했어.");
            break;
        }
        case 9: {
            qm.sendNextPrevS("마하의 말로는 검은 마법사가 봉인의 순간 강력한 저주를 걸었다고 하던데...", 2);
            break;
        }
        case 10: {
            qm.sendNextPrev("맞네. 검은 마법사는 봉인의 순간 그 힘을 역 이용하여 나와 프리드를 포함 한 주위의 모든 이에게 저주를 걸었지. 이 저주는 너무나 강력해서 프리드의 저주를 대신 받아주는 것이 고작이었어. 그 덕분에 나도 서서히 얼어 붙고 있지.");
            break;
        }
        case 11: {
            qm.sendNextPrevS("저주를 풀 만한 다른 방법이 없는 건가요?", 2);
            break;
        }
        case 12: {
            qm.sendNextPrev("검은 마법사의 봉인이 풀린다면 이 저주 또한 풀리겠지. 하지만 그럴 일은 없을 거야. 나와 프리드가 고안 한 봉인은 그를 평생 묶어 놓을 테니까. 길게 말했더니 힘이 드는군. 잠시 후에 다시 말을 걸어주겠나? 숨을 좀 골라야겠네.");
            break;
        }
        case 13: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}