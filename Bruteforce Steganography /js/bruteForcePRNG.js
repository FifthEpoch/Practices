var testmsg = 'Hello, world.';
var seedMatches = [];
var debug = true;
var imgData;

document.addEventListener('DOMContentLoaded', init);

function init() {
    document.getElementById('h1').innerText = "message:  " + testmsg;
    var img = new Image();
    img.onload = draw;
    img.src = "images/Peppers/peppers_OG.png";
}

function load() {
    var img_RGB = RGBA_2_RGB(imgData.data);
    for (var i = 0; i < testmsg.length; i++) {
        var binStr = str_2_bin(testmsg[i]);
        seedMatches.push(seedMatching(binStr, img_RGB));
    }
    printSeedMatches(testmsg, seedMatches);
}

function draw() {
    var width = this.naturalWidth;
    var height = this.naturalHeight;
    var cvs = document.getElementById('bruteForce_img');
    cvs.width = width;
    cvs.height = height;
    var ctx = cvs.getContext('2d');
    ctx.drawImage(this, 0, 0);

    imgData = ctx.getImageData(0, 0, width, height);
}

function seedMatching(binStr, RGB) {

    if (debug) {
        console.log('_SEED MATCHING_');
    }

    var RGBLen = RGB.length;

    for (var i = 0; i < 1000000; i++) {

        if (debug) {
            console.log('i: ' + i);
        }

        var randGen = new Math.seedrandom(i);

        for (var j = 0; j < binStr.length; j++) {
            let randIndex = Math.floor(RGBLen * randGen());
            let pos0 = ("00000000" + RGB[randIndex].toString(2)).slice(-8).charAt(0);
            if (debug) {
                console.log('j: ' + j);
                console.log('pos0 = ' + pos0);
                console.log('binStr[j] = ' + binStr[j]);
            }
            if (pos0 !== binStr[j]) break;
        }
        if (j === binStr.length - 1) return i;
    }
    return 'no seed match';
}

function str_2_bin(str) {
    var binBucket = '';
    for (var i = 0; i < str.length; i++) {
        var binChar = str.charCodeAt(i).toString(2);
        binBucket += ("00000000" + binChar).slice(-8);
    }
    return binBucket;
}

function printSeedMatches(testmsg, seedMatches) {
    for (var i = 0; i < seedMatches.length; i++) {
        document.getElementById('h2').innerText =
            document.getElementById('h2').innerText + testmsg[i] + ' (' + str_2_bin(testmsg[i]) + '): ' + seedMatches[i] + '\n';
        console.log(testmsg[i] + ': ' + seedMatches[i]);
    }
}

function RGBA_2_RGB(RGBA) {
    let RGB = [];
    for (var i = 0; i < (RGBA.length)/4; i+=4) {
        RGB.push(RGBA[i]);
        RGB.push(RGBA[i+1]);
        RGB.push(RGBA[i+2]);
    }
    if (debug) {
        console.log('RGB[0]: ' + RGB[0]);
        console.log('RGB[600]: ' + RGB[600]);
    }
    return RGB;
}
