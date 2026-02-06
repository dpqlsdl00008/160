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
            cm.lockInGameUI(true);
            cm.showFieldEffect(false, "phantom/mapname3");
            cm.sendNextS("(역시 다들 모여 있군. 아직 시작한 건 아닌 것 같지만... 아직 그녀가 등장하지 않았으니까 말이야. 적당히 자리가 보이니 앉아볼까.)", 17);
            break;
        }
        case 1: {
            cm.forcedInput(2);
            cm.sendDelay(1650);
            break;
        }
        case 2: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 3: {
            cm.sendNextS("(현재의 여제와 그 기사들...인가? 분위기가 좋지 않아. 다들 험악한 표정들. 하긴. 상황이 상황이니 당연한 일이겠지만.)", 17);
            break;
        }
        case 4: {
            cm.sendNextPrevS("(피요 의원들의 분위기도 좋은 것 같지는 않아. 그들은 이 상황을 어떻게 판단하고 있을까? 슬쩍 들어 볼까?)", 17);
            break;
        }
        case 5: {
            cm.forcedInput(0);
            cm.sendDelay(2000);
            break;
        }
        case 6: {
            cm.sendNextS("시그너스님이 진짜 황제가 아니라니... 정말일까요?", 5, 1402200);
            break;
        }
        case 7: {
            cm.sendNextPrevS("자네. 말이 과하군. 진짜 황제가 아니라니. 그럼 우리가 진짜 황제가 아닌 분을 모시고 있다는 말인가? 시그너스님은 지금도 황제로서 계시네.", 5, 1402201);
            break;
        }
        case 8: {
            cm.sendNextPrevS("진짜 황제시기는 하지만... 정통성이 의심받고 있는 건 사실이지 않습니까? 만약 정말로 에레브의 보물을 가진 사람이 있다면...", 5, 1402203);
            break;
        }
        case 9: {
            cm.sendNextPrevS("아리아 선황님이 남긴 보물... 그 기록은 확실하오.", 5, 1402202);
            break;
        }
        case 10: {
            cm.sendNextPrevS("휴우... 어렵네요. 정말 그 보물이 진정한 황제를 증명한다면 진정한 황제의 혈통이 시그너스님 외의 다른 사람으로 존재한다면... 우리는 어떻게 해야 하는 걸까요?", 5, 1402200);
            break;
        }
        case 11: {
            cm.sendNextPrevS("지금까지 에레브를 이끌어 오신 시그너스님을 배신 할 수는 없습니다만... 진정한 황제의 혈통을 모르는 척 하는 것도 도리가 아니고... 정말 답답합니다.", 5, 1402203);
            break;
        }
        case 12: {
            cm.sendNextPrevS("이제야 메이플 월드를 하나로 모을 연합이 탕생했는데... 그들은 모두 시그너스님을 믿고 연합에 가입한 사람들이오. 시그너스님이 아닌 다른 사람이 황제가 된다면 연합도 흔들리고 말 것이오.", 5, 1402202);
            break;
        }
        case 13: {
            cm.sendNextPrevS("우리끼리 떠들어 봐야 답이 나오겠는가. 진정한 황제의 혈통이라 주장하는 그 자가 어떤 자일지... 일단 그것부터 확인해보는 우선이겠지.", 5, 1402201);
            break;
        }
        case 14: {
            cm.sendNextPrevS("쉿... 드디어 도착한 모양이네.", 5, 1402201);
            break;
        }
        case 15: {
            cm.sendNextS("(... 드디어, 이 웃기지도 않는 연극의 각본가가 등장하는군.)", 17);
            break;
        }
        case 16: {
            cm.showFieldEffect(false, "phantom/hillah");
            cm.forcedInput(0);
            cm.sendDelay(6000);
            break;
        }
        case 17: {
            cm.sendNextS("많은 분들이 모이셨군요. 제 말에 귀 기울여 주셨다는 의미겠지요? 정말 감사합니다. 제가 바로 진정한 황제의 혈통이라 감히 주장하는 #b힐라#k입니다.", 5, 1402400);
            break;
        }
        case 18: {
            cm.sendNextPrevS("... ...", 5, 1402100);
            break;
        }
        case 19: {
            cm.sendNextPrevS("... 그 말이 거짓임을 증명하기 위해 모였을 뿐. 그 이상도 이하도 아닙니다.", 5, 1402101);
            break;
        }
        case 20: {
            cm.sendNextPrevS("아아, 물론... 제 말을 단박에 믿어주실 거라고는 생각하지 않았어요. 하지만 진실은 밝혀지는 법. 지금부터 에레브의 많은 사람들이 잊은 오래 된 이야기를 하고자 한답니다. 검은 마법사가 메이플 월드를 지배하던 그 당시의 황제, #b아리아#k에 대해...", 5, 1402400);
            break;
        }
        case 21: {
            cm.sendNextPrevS("(... 아리아...)", 17);
            break;
        }
        case 22: {
            cm.sendNextPrevS("다들 알고 계셨겠지만. 그 당시는 검은 마법사로 인해 에레브의 많은 것들이 파괴되어 제대로 남아 있는 기록이 거의 없답니다. 하지만 그래도 알려져 있는 사실이 하나 있지요. 당시의 황제 아리아가 #b스카이아#k라는 보물을 갖고 있었다는 것 말이에요.", 5, 1402400);
            break;
        }
        case 23: {
            cm.sendNextPrevS("아리아 황제가 갖고 있던 에레브의 보물, #b스카이아#k... 그건 메이플 월드의 황제에게만 대대로 내려오던 신비한 보섭이랍니다. 황제를 보호하고, 황제의 능력을 강화시키는 힘을 갖고 있지요.", 5, 1402400);
            break;
        }
        case 24: {
            cm.sendNextPrevS("#b스카이아#k에 대한 기록은 확실히 존재하지만, 그 보석이 어떤 힘을 갖고 있는지에 대해서는 알려진 바가 없어요.", 5, 1402104);
            break;
        }
        case 25: {
            cm.sendNextPrevS("물론 그러시겠지요. 시그너스님께서는 #b스카이아#k를 갖고 있지 않으시니까요. 하지만 전 다르답니다. 저에게는 #b스카이아#k가 전해 내려오고 있거든요.", 5, 1402400);
            break;
        }
        case 26: {
            cm.sendNextPrevS("검은 마법사와 군단장들의 공격으로 인해 파괴 된 에레브에서 #b스카이아#k가 사라졌다...가 여러분께서 알고 계신 전부겠지요. 하지만 황제의 신물인 #b스카이아#k가 그렇게 쉽게 사라질 물건인가요? 그렇게 중요한 물건을 선조들께서 잃어버리게 놔뒀을까요?", 5, 1402400);
            break;
        }
        case 27: {
            cm.sendNextPrevS("그럴 리가 없죠. #b스카이아#k는 아주 조심스럽고 은밀하게 옮겨졌답니다. 검은 마법사와 그 수하들의 공격을 받지 않도록 비밀스럽게... 진정한 황제의 혈통에게. 그렇게 수백 년을 조용히 전해져왔지요.", 5, 1402400);
            break;
        }
        case 28: {
            cm.sendNextPrevS("그게 너라고 주장하고 싶은 건가?", 5, 1402105);
            break;
        }
        case 29: {
            cm.sendNextPrevS("사실일 뿐이랍니다.", 5, 1402400);
            break;
        }
        case 30: {
            cm.sendNextPrevS("하, 하지만... 당신이 갖고 있다는 그 #b스카이아#k가 진짜인건 어떻게 증명하죠? 당신이 가짜를 가지고 온 것일 수도 있잖아요.", 5, 1402103);
            break;
        }
        case 31: {
            cm.sendNextPrevS("후훗, 잘 말씀 해주셨어요. #b스카이아#k는 이름만 널리 알려졌을 뿐, 본 사람이 거의 없는 비밀스러운 보석이지요. 현재 메이플 월드에 #b스카이아#k가 어떻게 생겼는지 알고 있는 분은 #b스카이아#k의 그림을 본 적이 있는 여기 에레브의 여러분들 뿐이죠.", 5, 1402400);
            break;
        }
        case 32: {
            cm.sendNextPrevS("제가 갖고 있는 #b스카이아#k가 여러분이 알고 계신 그 모양과 일치한다면 답은 간단한 거 아니겠어요?", 5, 1402400);
            break;
        }
        case 33: {
            cm.sendNextPrevS("이봐. 무슨 말을 하는 거야? 보석 생긴 거야 어차피 거기서 거기고. 다른 지역에 #b스카이아#k에 대한 기록이 남아있지 않다고 단정 할 수도 없는 거잖아?", 5, 1402106);
            break;
        }
        case 34: {
            cm.sendNextPrevS("수백 년 전의 사람도 아니고. 아니 수백 년 전의 사람이라 하더라도 #b스카이아#k를 본 사람은 거의 없는데... 솔직히 가능성이 적은 이야기이지 않나요?", 5, 1402400);
            break;
        }
        case 35: {
            cm.sendNextPrevS("증거는 또 있어요. 시그너스님의 연약한 몸도 그렇죠. 시그너스님께서 진정한 황제의 혈통이라면 신수님의 힘에 눌리지 않으셨겠지만... 그렇지 않기에 몸이 약하신거지요. 시그너스님, 당신도 알고 계시지 않나요? 당신이 그토록 약한 이유를...", 5, 1402400);
            break;
        }
        case 36: {
            cm.sendNextPrevS("무례하다!", 5, 1402102);
            break;
        }
        case 37: {
            cm.sendNextPrevS("어머... 무례하게 들렸다니, 정말 죄송합니다. 허나 틀린 말은 아니지 않나요?", 5, 1402400);
            break;
        }
        case 38: {
            cm.sendNextPrevS("지금 당장 저의 말을 다 믿어 달라고 주장하는 것은 결코 아니에요. 하지만, 제 말에 신빙성이 있다면 최소한 듣고 한 번쯤 깊게 논의를 해보셔야 하지 않나요? 그것이 당신의 역할 아닌가요? 시그너스?", 5, 1402400);
            break;
        }
        case 39: {
            cm.sendNextPrevS("...맞아요. 지금의 내가 이 자리에 오를 수 있었던 건... 내가 특별해서가 아니야. 그저 그렇게 타고났기 때문이지요.", 5, 1402100);
            break;
        }
        case 40: {
            cm.sendNextPrevS("그런 내가 정통성에서 티끌만한 의심이라도 받는다는 건... 있을 수 없는 일이에요. 필요하다면... 얼마든지 이야기를 해야 해요.", 5, 1402100);
            break;
        }
        case 41: {
            cm.sendNextPrevS("시그너스님!", 5, 1402101);
            break;
        }
        case 42: {
            cm.sendNextPrevS("옳은 일이라는 이유로 다른 사람들을 끊임없이 싸움에 끌어들였어요. 그러면서도 난 이곳에서 모두의 보호만을 받고 있지요. 그건 다 내가 황제이기 때문. 그 이상도 이하도 아니에요. 그런데 만약 내가 황제가 될 자격이 없다면...", 5, 1402100);
            break;
        }
        case 43: {
            cm.sendNextPrevS("감히 메이플 월드의 많은 사람들을 움직일 자격이... 없어요.", 5, 1402100);
            break;
        }
        case 44: {
            cm.sendNextPrevS("(목소리는 흔들리고 있지만 눈빛은 단단하군. 연약해 보여도 사실은 심지가 굳어. 절대 받아들일 수 없다는 표정의 책사나 당장 싸울 기세인 기사들을 보니 인망도 있는걸? 과연 아리아의...)", 17);
            break;
        }
        case 45: {
            cm.sendNextPrevS("자, 그렇다면 더 이상 구구절절 한 설명을 할 필요 없겠군요. 이 자리에서 진정한 황제의 혈통이 누구인지 증명해 보도록 하지요. #b스카이아#k는 진정한 주인의 손에서 빛을 낸다고 합니다. 에레브의 여제 시그너스... 당신이 진정 황제의 혈통이라면, 이 #b스카이아#k를 들어 보세요.", 5, 1402400);
            break;
        }
        case 46: {
            cm.sendNextPrevS("당신이 진정 메이플 월드의 황제라면 #b스카이아#k도 분명 빛을 낼 겁니다.", 5, 1402400);
            break;
        }
        case 47: {
            cm.sendNextS("시그너스님이 진짜 황제가 아니라니... 정말일까요?", 5, 1402200);
            break;
        }
        case 48: {
            cm.showFieldEffect(false, "phantom/skaia");
            cm.forcedInput(0);
            cm.sendDelay(7750);
            break;
        }
        case 49: {
            cm.sendNextS("아...", 5, 1402100);
            break;
        }
        case 50: {
            cm.sendNextPrevS("역시 조금도 빛나지 않는군요. 이걸로 증명은 충분하지 않나요?", 5, 1402400);
            break;
        }
        case 51: {
            cm.sendNextPrevS("... ...", 5, 1402100);
            break;
        }
        case 52: {
            cm.sendNextPrevS("결론을 내리기엔 아직 이릅니다. 여제님.", 5, 1402102);
            break;
        }
        case 53: {
            cm.sendNextPrevS("그래요, 여제님. 솔직히 저 빛이 가짜인지 아닌지 어떻게 알겠어?", 5, 1402106);
            break;
        }
        case 54: {
            cm.sendNextPrevS("마, 맞아요! 그냥 빛을 내는 마법은 저도 쓸 수 있는걸요?", 5, 1402103);
            break;
        }
        case 55: {
            cm.sendNextPrevS("신수님이 귀환하시면 진실을 밝혀 주실 겁니다. 시그너스님. 절대로 저 여자의 말을 믿어서는 안돼요.", 5, 1402104);
            break;
        }
        case 56: {
            cm.sendNextPrevS("당신이 흔들리면 우리 기사들도 흔들릴 수 밖에 없어. 정신을 차려.", 5, 1402105);
            break;
        }
        case 57: {
            cm.sendNextPrevS("연합의 결성이 확정되고 이제야 메이플 월드의 모두가 힘을 합칠 초석이 마련 된 시기입니다, 시그너스님. 이 시기를 노려 우리를 흔들고 그 간의 신뢰를 무너뜨리려는 음모일지 모릅니다. 정체도 알 수 없는 여자의 확인되지 않은 말에 현혹되지 마십시오.", 5, 1402101);
            break;
        }
        case 58: {
            cm.sendNextPrevS("모두들...", 5, 1402100);
            break;
        }
        case 59: {
            cm.sendNextPrevS("이런... 당신의 기사들이 진실을 부정하려 하는군요.", 5, 1402400);
            break;
        }
        case 60: {
            cm.sendNextPrevS("지금까지 에레브에서 기사단을 이끌며 메이플 월드를 현명하게 이끌어 온 시그너스... 당신의 노고를 부정하는 것은 아니에요. 그러나 현명한 당신이기에 더 늦기 전에 올바른 선택을 하기 바랍니다.", 5, 1402400);
            break;
        }
        case 61: {
            cm.sendNextPrevS("진정한 황제가 누구인지 인정하고. 황제의 자리에서 물러나세요.", 5, 1402400);
            break;
        }
        case 62: {
            cm.sendNextPrevS("그리고 그 사실을 연합에 알리시고요.", 5, 1402400);
            break;
        }
        case 63: {
            cm.sendNextPrevS("물론 재촉하는 건 아니에요. 혼란스러우실테니 그 혼란을 수습 할 시간은 얼마든지 드리도록 하죠. 제가 의심스럽다면 그에 관한 조사를 계속해도 좋아요.", 5, 1402400);
            break;
        }
        case 64: {
            cm.sendNextPrevS("하지만 결국 알게 될 거예요. 메이플 월드의 황제는 바로 저 힐라라는 사실을...", 5, 1402400);
            break;
        }
        case 65: {
            cm.sendNextPrevS("(슬슬 알프레드의 준비도 끝났겠고... 이제 이 몸이 나설 타이밍인가? 자, 그럼 심호흡을 하고, 하나, 둘, 셋!)", 17);
            break;
        }
        case 66: {
            cm.showFieldEffect(false, "phantom/phantom");
            cm.forcedInput(0);
            cm.sendDelay(3000);
            break;
        }
        case 67: {
            cm.sendNextS("결론을 내리기엔 아직 이른 것 아닌가?", 17);
            break;
        }
        case 68: {
            cm.dispose();
            cm.lockInGameUI(false);
            while (cm.getPlayer().getLevel() < 10) {
                cm.getPlayer().levelUp();
            }
            cm.forceCompleteQuest(25000);
            cm.getPlayer().changeJob(2400);
            cm.getPlayer().resetStats(4, 4, 4, 4);
            cm.warp(150000000, 0);
            //cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.UIPacket.playMovie("???.avi", true));
            break;
        }
    }
}