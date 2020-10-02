const Browser = require('zombie');
const process = require('process');
const {pathToFileURL} = require('url');
const xmlserializer = require('xmlserializer');

class XMLSerializer {
    serializeToString(x) {
        return xmlserializer.serializeToString(x);
    }
}

console.log('Running client side test');

async function runTest() {
    console.log('Creating the browser');
    const browser = new Browser();
    //https://github.com/assaf/zombie/issues/1061#issuecomment-235244949
    browser.on('loaded', () => 
        (browser.window.XMLSerializer = browser.window.XMLSerializer || XMLSerializer));
    const furl = pathToFileURL('./Test.html').toString();
    console.log('Visiting', furl);
    await browser.visit(furl);
}

runTest().then( () => console.log('PASS'), (err) => {
    console.error(err);
    console.error('FAIL');
    process.exitCode = 1;
});


// browser.visit('./Test.html', (err) => {
//     if(err) throw err;
//     bro
// });

// describe('Some test suite', function () {
//     it('should do what you expect', function (done) {
//         var browser = new Browser();
//         browser.visit('http://localhost:3000', function (err) {
//             // Let's say this is a log in page
//             should.not.exist(err);
//             browser
//                 .fill('#username', 'TestUser')
//                 .fill('#password', 'TestPassword')
//                 .pressButton('#login', function (err) {
