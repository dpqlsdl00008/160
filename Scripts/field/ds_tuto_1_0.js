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
            cm.forcedInput(1);
            cm.sendDelay(30);
            break;
        }
        case 1: {
            cm.sendNextS("임무 때문에 외부에 나가있는 군단장들을 빼면 모두 온 건가... 그럼 회의를 시작하지.", 5, 2159310);
            cm.forcedInput(0);
            break;
        }
        case 2: {
            cm.sendNextPrevS("위대한 그 분, 검은 마법사님의 일이 끝날 때까지 우리는 우리가 할 일을 해야겠지. 그 분이 안 본다고 게으름을 부릴 수야 없잖나? 그러고보니 #h #... 흥미로운 정보를 입수했다고 들었는데?", 5, 2159308);
            break;
        }
        case 3: {
            cm.sendNextPrevS("...저항군들이 집결하고 있다는 정보를 입수했습니다.", 17);
            break;
        }
        case 4: {
            cm.sendNextPrevS("저항군이라... 오합지졸 패잔병들이 모여 무얼 하겠다고. 클클클... 사람들은 그들을 #r영웅#k이라고 부른다지? 우습기 짝이 없도다.", 5, 2159308);
            break;
        }
        case 5: {
            cm.sendNextPrevS("난 기대되는데? 마지막 발악이라는 거잖아? 얼마나 열심히 반항 할지 궁금해. 에레브 점령전도 맥빠졌잖아~ 황제란 녀석을 없애는 건 너무 쉬워서 시시했다고.", 5, 2159339);
            break;
        }
        case 6: {
            cm.sendNextPrevS("그 전투가 쉬웠던 건 검은 마법사님의 권능 덕분이지. 네 능력은 아니었을텐데, 오르카? 건방진 말은 삼가거라.", 5, 2159308);
            break;
        }
        case 7: {
            cm.sendNextPrevS("으... 하지만 난 할 일도 없었단 말이야!", 5, 2159339);
            break;
        }
        case 8: {
            cm.sendNextPrevS("스우님께서는 바쁘신 것 같던데요. 오르카님께서는 여기 계셔도 됩니까?", 17);
            break;
        }
        case 9: {
            cm.sendNextPrevS("스우는 쓸데없이 성실하니까 안 해도 될 일까지 사서 하는 것 뿐이야! 안 그래도 나도 이제 스우한테 가려고 했어! 흥! 하여간에 군단장들은 다들 너무 딱딱해. 재미없어.", 5, 2159339);
            break;
        }
        case 10: {
            cm.sendNextPrevS("으... 하지만 난 할 일도 없었단 말이야!", 5, 2159339);
            break;
        }
        case 11: {
            cm.sendNextPrevS("...회의는?", 5, 2159310);
            break;
        }
        case 12: {
            cm.sendNextPrevS("이런. 오르카가 떠들면 회의가 영 진행이 안 된다니까. 쯧쯧... 영웅들의 이야기를 하는 중이었던가? 영웅이라... 뭐, 그들도 고대하신 #h #께서 알아서 잘 처리하겠지.", 5, 2159308);
            break;
        }
        case 13: {
            cm.sendNextPrevS("시간의 여신도 제압한 자가 그깟 영웅들이 대수겠는가? 안 그런가? 하하하하....", 5, 2159308);
            break;
        }
        case 14: {
            cm.sendNextPrevS("...만만하게 볼 상대가 아닙니다. 목숨을 건 자들은 능력 이상의 힘을 발휘하는 법이니까요. 그리고 저는 시간의 여신을 행동 불능에 빠뜨렸을 뿐... 최종적으로 유폐시킨 것은 그 분이셨지요.", 17);
            break;
        }
        case 15: {
            cm.sendNextPrevS("이런, 이런. 겸손하시기도 하셔라. 어쨋든 그 공으로 그 분께 인정 받지 않았나? 그에 비해 내가 먼저 신전에서 펼쳤던 수많은 뒷공작들은 너무 소소해서 부끄러울 따름이군.", 5, 2159308);
            break;
        }
        case 16: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 0, 0, -110, 1, 0);
            cm.sendDelay(1500);
            break;
        }
        case 17: {
            cm.sendNextS("...둘 다 그만하지.", 5, 2159310);
            break;
        }
        case 18: {
            cm.sendNextPrevS("왜? 재밌어지는데? 계속 해봐. 아카이럼.", 5, 2159339);
            break;
        }
        case 19: {
            cm.sendNextPrevS("난 그저 우리 군단 최고의 공로자인 #h #님을 칭송하고 있는 것 뿐이라네. 클클클...", 5, 2159308);
            break;
        }
        case 20: {
            cm.sendNextPrevS("아카이럼. 신전 점령을 마지막으로 곧 모든 것이 끝난다... 그런 의미에서 시간의 여신을 묶어둔 #h #의 공은 절대적이지.", 5, 2159310);
            break;
        }
        case 21: {
            cm.sendNextPrevS("그리고 네가 여신의 눈을 가린 것은 이미 보상 받지 않았나? 뭘 더 바라는거지, 아카이럼?", 5, 2159310);
            break;
        }
        case 22: {
            cm.sendNextPrevS("내가 바라긴 뭘 바란다고... 흠. 자, 그럼 이 얘기는 여기서 끝내고 회의를 계속하지. 하잘 것 없는 영웅들 얘기는 됐고, 다른 저항 세력들은 어떻게 되었나?", 5, 2159308);
            break;
        }
        case 23: {
            cm.sendNextPrevS("...명령대로, 모두 파괴 되었다는 것을 확인하였다.", 5, 2159310);
            break;
        }
        case 24: {
            cm.sendNextPrevS("오오, 그렇군.", 5, 2159308);
            break;
        }
        case 25: {
            cm.sendNextPrevS("근데 말이야~ 검은 마법사님께서는 왜 갑자기 계획을 바꾸신 걸까? 원래는 신전 점령까지 아니었나? 뭐 상관은 없다만, 다 파괴해버리면 가지고 놀 것도 없어지잖아?", 5, 2159339);
            break;
        }
        case 26: {
            cm.sendNextPrevS("...파괴? 그건... 무슨 명령입니까? 전 듣지 못했습니다만.", 17);
            break;
        }
        case 27: {
            cm.sendNextPrevS("아하, 그러고보니 자네는 여신과의 전투로 피로할 것 같아 명령을 전하지 않았지. 어떤 명령이냐고? 간단하네.", 5, 2159308);
            break;
        }
        case 28: {
            cm.sendNextPrevS("위대한 그 분께서 이제 모든 전쟁의 종결을 원하시더군. 지지부진하게 저항하는 세력들을 와전히 없애 버리라는 명령을 받들어 자네 외의 군단장들이 나선 거지.", 5, 2159308);
            break;
        }
        case 29: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 0, 0, -110, 1, 0);
            cm.sendDelay(1500);
            break;
        }
        case 30: {
            cm.sendNextS("...리프레는 풀 한 포기, 나무 한 그루 남기지 않고 소멸시켰더군...", 5, 2159310);
            break;
        }
        case 31: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/18", 0, 0, -110, 1, 0);
            cm.sendDelay(1500);
            break;
        }
        case 32: {
            cm.sendNextS("(리프레가 있는 남부 지역에는 가족들이 있는데...!)", 17);
            break;
        }
        case 33: {
            cm.sendNextPrevS("검은 마법사님께서 바라시는 게 이 일이 저항군에게 본보기가 되는 것이 아닐까 해서... 좀 더 철저히 처리한 모양이더군. 뭐, 좋은 일 아니겠나?", 5, 2159308);
            break;
        }
        case 34: {
            cm.sendNextPrevS("그래... 드래곤이라는 종은 이제 정말 몇 남지 않았겠군.", 5, 2159310);
            break;
        }
        case 35: {
            cm.sendNextPrevS("...잠시만요. 남부 지역은 건드리지 않기로 하지 않았습니까? 남부 지역 어디까지 파괴된 겁니까?! 자세한 지역을 알려 주십시오...!", 17);
            break;
        }
        case 36: {
            cm.sendNextPrevS("어디까지냐니 글쎄... 그 분의 명을 그리 허술하게 처리했겠나? 완전한 파괴를 명 받았다면 당연히 모조리 없애 버렸겠지. 클클클... 왜 그렇게 민감하게 반응하나? 뭔가 마음에 걸리는 일이라도 있나?", 5, 2159308);
            break;
        }
        case 37: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/3", 0, 0, -110, 1, 0);
            cm.sendDelay(1500);
            break;
        }
        case 38: {
            cm.sendNextPrevS("저는 확인할 것이 있어서 자리를 비우겠습니다...!", 17);
            break;
        }
        case 39: {
            cm.sendNextPrevS("이런~ 자네가 검은 마법사님의 총애를 받는다고 마음대로 행동해도 된다는 건 아니라네. 우리는 우리의 일을 해야 한다고 하지 않았나? 지금 떠난다면 명령 불복종이라네.", 5, 2159308);
            break;
        }
        case 40: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/11", 0, 0, -110, 1, 0);
            cm.sendDelay(1500);
            break;
        }
        case 41: {
            cm.sendNextPrevS("(데미안, 어머니... 무사하기를...)", 17);
            break;
        }
        case 42: {
            cm.sendNextPrevS("들은 척도 않는군. 에잉... 하긴 #r가족#k들이 그 곳에 있다던가? 클클클... 행운을 빌지!", 5, 2159308);
            break;
        }
        case 43: {
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 44: {
            cm.dispose();
            cm.warp(924020010, 0);
            break;
        }
    }
}