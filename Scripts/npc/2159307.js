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
            cm.sendNextS("군단장님! 그 동안 연락도 없이 어디 가셨던 겁니까? 안 그래도 아카이럼님이 트집을 잡으려고 얼마나 벼르고 있는지 누구보다 잘 아시는 분이...", 5, 2159307);
            break;
        }
        case 1: {
            cm.sendNextPrevS("정말 분위기가 심상치 않습니다. 시간의 신전의 여신을 붙잡은 군단장님의 공을 시기하는 거겠죠. 흥! 아카이럼님이 한 일은 여신의 눈을 잠깐 가린 것 뿐인데도요. 그것도 원래의 지위를 이용한 거면서!", 5, 2159307);
            break;
        }
        case 2: {
            cm.sendNextPrevS("어... 평소 같으면 쓸데없는 소리 말라고 핀잔을 주셨을텐데... 무슨 일이라도 있으신 건가요? 이제보니 무척 안색이 안 좋으신데... 어디 아프신 건가요? 혹시 이전 싸움에서 다친 곳이라도?!", 5, 2159307);
            break;
        }
        case 3: {
            cm.sendNextPrevS("...마스테마. 당신은... 검은 마법사와 저 둘 중 누구의 부하입니까.", 17);
            break;
        }
        case 4: {
            cm.sendNextPrevS("네? 갑자기 무슨 그런 질문을...?", 5, 2159307);
            break;
        }
        case 5: {
            cm.sendNextPrevS("대답하십시오.", 17);
            break;
        }
        case 6: {
            cm.sendNextPrevS("그... 그야 당연히 위대한 그 분께 충성하고 있습니다. 하지만 당신이 제 목숨을 구해주신 날부터 당신을 위해 목숨을 바치겠다 결심 했습니다...! 알고 계시지 않습니까?! 그런데 왜 이런 걸...?", 5, 2159307);
            break;
        }
        case 7: {
            cm.sendNextPrevS("...당신에게 부탁하고 싶은 게 있습니다.", 17);
            break;
        }
        case 8: {
            cm.sendNextPrevS("이 서신을... 그들, #r영웅#k이라 불리는 자들에게 전해주십시오.", 17);
            break;
        }
        case 9: {
            cm.sendNextPrevS("네? 이걸 왜 그들에게... 자리를 비우신 걸로도 말이 많을 겁니다. 그런데 만약 그들과 접촉했다는 사실이 알려지기라도 한다면 검은 마법사님께 거역하려 한다는 오명을 쓰실 겁니다! 분명 그럴거라고요! 무슨 생각이십니까, 군단장님!", 5, 2159307);
            break;
        }
        case 10: {
            cm.sendNextPrevS("...저는 더 이상 군단장이 아닙니다.", 17);
            break;
        }
        case 11: {
            cm.sendNextPrevS("설마... 검은 마법사님을 배신하시는 건가요?! 그 분께 누구보다 충성하던 당신이?! 시간의 신전을 점령한 게 바로 얼마 전이에요! 이제 포상 받으실 일만 남았는데... 어째서... 어째서요?!", 5, 2159307);
            break;
        }
        case 12: {
            cm.sendNextPrevS("...시간이 없습니다. 무리한 부탁을 한 거라면 거둬 들이겠습니다... 당신과는 싸우고 싶지 않습니다.", 17);
            break;
        }
        case 13: {
            cm.sendNextPrevS("무리한 부탁이라서가 아니예요! 전 다만 당신이 걱정되서...!", 5, 2159307);
            break;
        }
        case 14: {
            cm.sendNextPrevS("......", 17);
            break;
        }
        case 15: {
            cm.sendNextPrevS("가족분들은 어떻게 하시려고 그러세요?! 이러시면 가족분들에게 위해가 갈지도 모릅니다...!", 5, 2159307);
            break;
        }
        case 16: {
            cm.sendNextPrevS("그 얘기는 그만! 이야기는 여기까지 하겠습니다!", 17);
            break;
        }
        case 17: {
            cm.sendNextPrevS("...왜... 설마... 설마 그 분들에게 무슨 일이라도...?", 5, 2159307);
            break;
        }
        case 18: {
            cm.sendNextPrevS("......", 17);
            break;
        }
        case 19: {
            cm.sendNextPrevS("또 그렇게... 입을 다무시네요... 그래요. 당신은 원래 구구절절 말하는 분이 아니시죠.", 5, 2159307);
            break;
        }
        case 20: {
            cm.sendNextPrevS("좋습니다. 이 서신, 목숨을 걸고 그들에게 전달하겠습니다.", 5, 2159307);
            break;
        }
        case 21: {
            cm.sendNextPrevS("미안합니다. 마스테마...", 17);
            break;
        }
        case 22: {
            cm.sendNextPrevS("사과하지 말아주세요. 제 목숨은 당신을 위해 있는 것. 오히려 이런 부탁을 받을 수 있어 기쁠 따름입니다.", 5, 2159307);
            break;
        }
        case 23: {
            cm.sendNextPrevS("명령을 받들어 떠나겠습니다. 부디 뜻을 이루시길...", 5, 2159307);
            break;
        }
        case 24: {
            cm.sendNextPrevS("#b(그 동안 고마웠다. 마스테마.)#k", 17);
            break;
        }
        case 25: {
            cm.dispose();
            cm.warp(927000080, 0);
            break;
        }
    }
}