function enter(pi) {
    if (pi.getInfoQuest(22013).equals("mo00=o") == true) {
        return false;
    }
    pi.updateInfoQuest(22013, "mo00=o");
    pi.delayEffect("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon00", 0);
    return true;
}