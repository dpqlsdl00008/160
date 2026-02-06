function enter(pi) {
    var warpID = 0;
    switch (pi.getMapId()) {
        case 270010100: {
            warpID = 270010110;
            break;
       }
        case 270010200: {
            warpID = 270010300;
            break;
        }
        case 270010300: {
            warpID = 270010400;
            break;
        }
        case 270010400: {
            warpID = 270010500;
            break;
        }
        case 270010500: {
            warpID = 270020000;
            break;
        }
        case 270020100: {
            warpID = 270020200;
            break;
        }
        case 270020200: {
            warpID = 270020210;
            break;
        }
        case 270020300: {
            warpID = 270020400;
            break;
        }
        case 270020400: {
            warpID = 270020500;
            break;
        }
        case 270020500: {
            warpID = 270030000;
            break;
        }
        case 270030100: {
            warpID = 270030200;
            break;
        }
        case 270030200: {
            warpID = 270030300;
            break;
        }
        case 270030300: {
            warpID = 270030400;
            break;
        }
        case 270030400: {
            warpID = 270030410;
            break;
        }
        case 270030500: {
            warpID = 270040000;
            break;
        }
        case 270040000: {
            warpID = 270040100;
            break;
        }
    }
    if (warpID > 0) {
        pi.warp(warpID, 0);
    }
}