function enter(pi) {
    if (pi.getInfoQuest(22013).equals("mo30=o;mo40=o;mo41=o;mo50=o;mo42=o;mo60=o") == true) {
        return false;
    }
    pi.updateInfoQuest(22013, "mo30=o;mo40=o;mo41=o;mo50=o;mo42=o;mo60=o");
    pi.delayEffect("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon60", 0);
    return true;
}