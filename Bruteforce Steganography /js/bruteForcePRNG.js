var testmsg = 'Hello, world.';
var seedMatches = [];
var debug = false;
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
    compareCompressed();
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

    var RGBLen = RGB.length;

    for (var i = 0; i < 1000000; i++) {

        var randGen = new Math.seedrandom(i);

        for (var j = 0; j < binStr.length; j++) {
            let randIndex = Math.floor(RGBLen * randGen());
            let pos0 = MSB(RGB[randIndex]);

            if (pos0 !== binStr[j]) break;
        }
        if (j === binStr.length - 1) return i;
    }
    return 'no seed match';
}

function MSB(num) {
    return ("00000000" + num.toString(2)).slice(-8).charAt(0);
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

function compareCompressed() {

    var errors = [];
    var compressed = document.getElementsByTagName("img");
    console.log('print compressed' + compressed[0]);
    console.log('print compressed' + compressed[1]);

    for (var i = 0; i < compressed.length; i++) {

        const cvs = document.getElementById('compressed');
        cvs.width = cvs.height = 512;
        const ctx = cvs.getContext( '2d' );
        if (debug) {
            console.log('compressed[i] => ' + compressed[i]);
        }
        ctx.drawImage(compressed[i], 0, 0);

        let imgData_RGBA = ctx.getImageData(0, 0, 512, 512);
        let imgData_RGB = RGBA_2_RGB(imgData_RGBA.data);
        let OG_RGB = RGBA_2_RGB(imgData.data);

        var counter = 0;
        for (var j = 0; j < imgData_RGB.length; j++) {
            if (MSB(OG_RGB[j]) !== MSB(imgData_RGB[j])){
                counter++;
            }
        }
        errors.push(counter);
    }
    document.getElementById('compression-test').innerText = printErrorRates(errors);
}

function printErrorRates(errors){
    var str = '';
    for (k = 0; k < errors.length; k++) {
        str += '\ncompression quality metric = ' + (90 - (5 * k)) +
            '\ntotal error: ' + errors[k] +
            '\nerror rate: ' + Math.round(errors[k] / (512 * 512 * 3) * 100) + '%\n';
    }
    return str;
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
