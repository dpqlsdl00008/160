function enter(pi) {
    var pID = pi.getPortal().getId();
    var nMap = 0;
    var nPortal = 0;
    switch (pi.getMapId()) {
        case 863010100: {
            if (pID == 6) {
                nMap = 863010200;
                nPortal = 1;
            }
            if (pID == 7) {
                nMap = 863010220;
                nPortal = 1;
            }
            break;
        }
        case 863010200: {
            if (pID == 1) {
                nMap = 863010100;
                nPortal = 6;
            }
            if (pID == 2) {
                nMap = 863010210;
                nPortal = 1;
            }
            break;
        }
        case 863010210: {
            if (pID == 1) {
                nMap = 863010200;
                nPortal = 2;
            }
            if (pID == 2) {
                nMap = 863010240;
                nPortal = 2;
            }
            break;
        }
        case 863010220: {
            if (pID == 1) {
                nMap = 863010100;
                nPortal = 7;
            }
            if (pID == 2) {
                nMap = 863010230;
                nPortal = 1;
            }
            break;
        }
        case 863010230: {
            if (pID == 1) {
                nMap = 863010220;
                nPortal = 2;
            }
            if (pID == 2) {
                nMap = 863010240;
                nPortal = 1;
            }
            break;
        }
        case 863010240: {
            if (pID == 1) {
                nMap = 863010230;
                nPortal = 2;
            }
            if (pID == 2) {
                nMap = 863010210;
                nPortal = 2;
            }
            if (pID == 3) {
                nMap = 863010500;
                nPortal = 2;
            }
            break;
        }
        case 863010300: {
            if (pID == 1) {
                nMap = 863010310;
                nPortal = 1;
            }
            if (pID == 2) {
                nMap = 863010100;
                nPortal = 6;
            }
            break;
        }
        case 863010310: {
            if (pID == 1) {
                nMap = 863010300;
                nPortal = 1;
            }
            if (pID == 2) {
                nMap = 863010320;
                nPortal = 2;
            }
            break;
        }
        case 863010320: {
            if (pID == 1) {
                nMap = 863010310;
                nPortal = 2;
            }
            if (pID == 2) {
                nMap = 863010310;
                nPortal = 2;
            }
            if (pID == 3) {
                nMap = 863010500;
                nPortal = 1;
            }
            break;
        }
        case 863010330: {
            if (pID == 1) {
                nMap = 863010500;
                nPortal = 4;
            }
            break;
        }
        case 863010400: {
            if (pID == 1) {
                nMap = 863010410;
                nPortal = 1;
            }
            if (pID == 2) {
                nMap = 863010100;
                nPortal = 7;
            }
            break;
        }
        case 863010410: {
            if (pID == 1) {
                nMap = 863010400;
                nPortal = 1;
            }
            if (pID == 2) {
                nMap = 863010420;
                nPortal = 2;
            }
            break;
        }
        case 863010420: {
            if (pID == 1) {
                nMap = 863010410;
                nPortal = 2;
            }
            if (pID == 2) {
                nMap = 863010410;
                nPortal = 2;
            }
            if (pID == 3) {
                nMap = 863010500;
                nPortal = 5;
            }
            break;
        }
        case 863010430: {
            if (pID == 1) {
                nMap = 863010500;
                nPortal = 6;
            }
            break;
        }
        case 863010500: {
            if (pID == 1) {
                nMap = 863010320;
                nPortal = 3;
            }
            if (pID == 2) {
                nMap = 863010240;
                nPortal = 3;
            }
            if (pID == 3) {
                nMap = 863010600;
                nPortal = 2;
            }
            if (pID == 4) {
                nMap = 863010330;
                nPortal = 1;
            }
            if (pID == 5) {
                nMap = 863010420;
                nPortal = 3;
            }
            if (pID == 6) {
                nMap = 863010430;
                nPortal = 1;
            }
            break;
        }
        case 863010600: {
            if (pID == 2) {
                nMap = 863010500;
                nPortal = 3;
            }
            break;
        }
    }
    if (nMap > 0) {
        pi.warpParty(nMap, nPortal);
    }
}