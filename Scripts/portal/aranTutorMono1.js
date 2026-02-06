function enter(pi) {
    if (pi.getInfoQuest(21002).equals("mo1=o")) {
        pi.updateInfoQuest(21002, "mo1=o;mo2=o");
        pi.delayEffect("Effect/OnUserEff.img/guideEffect/aranTutorial/legendBalloon2", 0);
    }
}