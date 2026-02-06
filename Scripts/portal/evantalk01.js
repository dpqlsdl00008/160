function enter(pi) {
    if (pi.getInfoQuest(22013).equals("dt00=o;mo00=o;mo01=o") == true) {
        return false;
    }
    pi.updateInfoQuest(22013, "dt00=o;mo00=o;mo01=o");
    pi.delayEffect("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon01", 0);
    return true;
}