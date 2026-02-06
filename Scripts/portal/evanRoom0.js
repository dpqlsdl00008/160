function enter(pi) {
    if (pi.getInfoQuest(22013).equals("mo30=o") == false) {
	pi.updateInfoQuest(22013, "mo30=o");
	pi.delayEffect("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon30", 0);
    }
}