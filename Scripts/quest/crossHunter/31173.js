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
            var say = "검은 마법사의 저주는 우리 오닉스 드래곤 일족 전체에 영향을 미쳤네. 이 땅에 존재하는 모든 오닉스 드래곤은 이제 서서히 죽어 갈 것이네. 물론, 더 이상 후손을 얻을 수도 없지.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "엘프의 왕이자 나의 친우인 메르세데스. 자네에게 부탁이 하나 있네. 길게 이야기 할 시간이 없으니 본론만 말하지. 우리의 마지막 아이를 구해주게.";
            }
            qm.sendNext(say);
            break;
        }
        case 1: {
            var say = "어떻게 그런 일이 있을 수 있죠?";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "마지막 아이...? #b(에반과 함께 다니는 그 작은 오닉스 드래곤인가?)#k";
            }
            qm.sendNextPrevS(say, 2);
            break;
        }
        case 2: {
            var say = "우리야 말로 검은 마법사의 가장 큰 적수였으니까. 검은 마법사는 우리를 같은 편으로 끌어들이기 위해 안간 힘을 썼었지. 하지만 우린 그 제안을 거절하고 검은 마법사와 맞섰네. 그 뒤로 그는 우리를 가장 껄끄러워 해 왔거든. 하지만 그의 저주가 완벽하지 않아. 덕분에 내 친우 프리드의 저주를 비틀어 내게 향하도록 바꿀 수 있었지.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "검은 마법사와의 마지막 전투를 벌이는 사이 리프레 어딘가에서 잃어버렸지. 자네가 그 아이를 구해주게. 유일하게 검은 마법사의 저주를 피한 마지막 일족이니 반드시 지켜야 해.";
            }
            qm.sendNextPrev(say);
            break;
        }
        case 3: {
            var say = "무엇 때문이죠?";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "친우의 부탁을 거절 할 리 없잖아? 다녀오겠어.";
            }
            qm.sendNextPrevS(say, 2);
            break;
        }
        case 4: {
            var say = "종족을 잃은 왕이 무슨 체면으로 떳떳하게 이 세상을 걸어 다니겠나. 그 보다 내 친우의 생명을 구하는 것이 백 번 나은 일이 아닐까? 물론, 그에게 부탁 할 것도 있었고 말이지.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "만약 오닉스 드래곤의 알을 도중에 잃어버리면 퀘스트를 포기하고 다시 내게 말을 걸도록 하게.";
            }
            qm.sendNextPrev(say);
            break;
        }
        case 5: {
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNextPrevS("어떤 부탁인지 물어봐도 될까요?", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("괜찮네. 원래 우리 종족은 무한에 가까운 삶을 살 수 있는 대신 후손이 귀하지. 그런데 이번 전쟁으로 인해 몇 안되는 우리 종족이 거의 다 죽고 남은 아이들도 저주에서 벗어 날 수 없는 처지가 되었네. 하지만 다행스럽게도 얼마 전 축복받은 새 생명이 태어났지. 그 아이는 아직 알에서 부화하지 않아 유일하게 검은 마법사의 저주에서 벗어 날 수 있었어.");
            break;
        }
        case 7: {
            qm.sendNextPrev("하지만 검은 마법사와 마지막 사투를 벌이는 와중에 리프레 어딘가에서 그 아이를 잃어버렸네. 그래서 나는 원래 프리드에게 그 아이를 되 찾아 안전한 곳에서 지켜줘 달라고 부탁 할 생각이었지. 프리드가 이렇게 오랫동안 정신을 차리지 못 할 줄은 예상하지 못했군.");
            break;
        }
        case 8: {
            qm.sendNextPrev("그래서 자네에게 부탁하고 싶네. 우리의 마지막 아이를 찾아서 데려 와 주게.");
            break;
        }
        case 9: {
            qm.sendNextPrev("만약 마지막 오닉스 드래곤의 알을 도중에 잃어버리면 퀘스트를 포기하고 다시 내게 말을 걸도록 하게.");
            break;
        }
        case 10: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}

function end(mode, type, selection) {
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
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == false) {
                qm.sendNext("고맙네. 우리의 마지막 아이를 건네 주겠나?");
            } else {
                qm.sendNextS("여기 무사히 알을 가져 왔어.", 2);
            }
            break;
        }
        case 1: {
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == false) {
                qm.sendNextPrev("이 알은 프리드가 정신을 차리면 그와 함께 안전한 곳으로 떠날 터. 프리드에겐 미안하지만, 친우의 부탁이라면 남은 평생을 바쳐 지킬테지. 이것으로 우리의 희망은 미래로 전해졌네.");
            } else {
                qm.sendNextPrev("고맙네. 반드시 데려 올 수 있을 거라 믿었어. 이 아이가 얼마나 소중한 존재인지, 엘프의 왕인 자네라면 나만큼 이해 할 테니까.");
            }
            break;
        }
        case 2: {
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == false) {
                qm.dispose();
                qm.forceCompleteQuest();
                return;
            }
            qm.sendNextPrevS("아프리엔. 이제 이 알은 프리드가 맡게 되는 건가?", 2);
            break;
        }
        case 3: {
            qm.sendNextPrev("시간의 흐름을 거슬러도 자네의 현명함은 빛을 잃지 않는군. 다만 프리드가 언제 일어날 지는 나도 모르네. 늦지 않게 일어나길 바랄 뿐.");
            break;
        }
        case 4: {
            qm.sendNextPrevS("걱정하지 마. 오닉스 드래곤의 의지는 반드시 미래로 이어 질 테니까.", 2);
            break;
        }
        case 5: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}