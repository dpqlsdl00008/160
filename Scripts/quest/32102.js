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
            qm.sendNext("여기가 어디냐고? 어딘지도 모르고 따라왔냐옹? 이 곳은 #b요정 학원 엘리넬#k로 가는 숲 속이다냥.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("요정 학원 엘리넬?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("그렇다냥. #b엘리넬#k은 오랜 시간동안 요정 아이들에게 마법을 가르쳐 온 일종의 교육 기관이다옹.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("그런데 왜 이렇게 깊은 숲 속에 숨어있는 거죠?", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("표정을 보니 아무 것도 모르는구냥. #b엘리니아#k가 원래 요정들의 마을이 었다는 것은 알고 있냥? 수백 년 전 검은 마법사와의 큰 전쟁이 있고 난 후, 인간들이 들어와서 마을을 일구어 지금의 #b엘리니아#k가 된 것이다냥.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("그렇다면 엘리니아 바깥에도 요정이 사는 곳이 있겠군요.", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("인가들을 받아들인 요정들도 있었지만, 그렇지 않은 보수적인 요정들도 있었다냥. #b요정 학원 엘리넬#k도 마찬가지였다옹. 그들은 인간과 섞이길 거부하고 스스로 밤의 영역으로 사라졌다냥. 그리고 외부인의 출입을 거부하기 위해 이 호수의 건너편에 지어져 있다냥.");
            break;
        }
        case 7: {
            qm.sendNextPrevS("그런 엘리넬의 요정들에게 마법사 쿠디가 사로 잡혔다는 말인가요?", 2);
            break;
        }
        case 8: {
            qm.askAcceptDecline("그렇다냥. 나도 당최 어떻게 된 일인지 감이 잡히질 않는다냥. #b하인즈#k님과 내가 수차례 접선을 시도했지만 그들은 우리를 싫어하기 때문에 우리의 말을 들으려고 하지 않는다옹. 그래서 너의 도움이 필요하다옹. 그런데 #b#h ##k, 혹시 헤엄은 잘 치냥?");
            break;
        }
        case 9: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}