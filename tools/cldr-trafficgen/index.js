const bent = require('bent');
const puppeteer = require('puppeteer');
const cookie = require('simple-cookie');
const {URL} = require('url');
const delay = require('delay');

const config = require('./config.json');

const createAndLoginUrl = new URL(`createAndLogin.jsp?vap=${config.vap}`, config.url);
const createAndLoginButton = 'body > form > table:nth-child(3) > tbody > tr.submit > td > button';
const createAndLoginLoginUrl = 'body > div:nth-child(2) > p:nth-child(3) > a';
console.log(createAndLoginUrl.toString());

// http://127.0.0.1:8080/cldr-apps/SurveyAjax?what=getrow&_=af&x=Gregorian&strid=&s=${sessionId}
let browser;

async function getBrowser() {
    if(!browser) {
        console.log('Starting browser');
        browser = await puppeteer.launch({
            // Uncomment the following to watch the browser
            // headless: false
        });
    }
    return browser;
}

async function stopBrowser() {
    if(browser) {
        console.log('Stopping browser');
        await browser.close();
    }
}

async function getLoginDetails() {
    const browser = await getBrowser();
    const page = await browser.newPage();
    await(delay(3000));
    await page.goto(createAndLoginUrl.toString());
    await page.click(createAndLoginButton);
    await page.waitForNavigation();
    const loginUrlElement = await page.$(createAndLoginLoginUrl);
    if(loginUrlElement == null) {
        throw Error('could not get login url from ' + createAndLoginLoginUrl);
    } else {
        const prop = await loginUrlElement.getProperty('href');
        const urlLogin = await prop.jsonValue();
        // console.log('Login URL:', urlLogin);

        const resp = await(bent(302)(urlLogin));
        const cookieHeader = resp.headers['set-cookie'][0];
        // console.log('COOKIE:', cookieHeader);

        // console.log(href.jsonValue());
        // console.log(loginUrlElement.toString());
        // await page.screenshot({path: 'example.png'});
        const parsed = cookie.parse(cookieHeader);
        const {name,value} = parsed;
        if(name !== 'JSESSIONID') {
            throw Error(`Expected JSESSIONID but got cookie named ${name}!`);
        }
        // console.dir(parsed);
        return {
            JSESSIONID: value,
            urlLogin
        }
    }  
}


(async function() {
    const login1 = await getLoginDetails();
    const login2 = await getLoginDetails();
    const login3 = await getLoginDetails();
    const login4 = await getLoginDetails();
    const login5 = await getLoginDetails();
    const login6 = await getLoginDetails();
    console.dir({login1, login2});

    console.log('Generating some traffic.');
    for(let i=0;i<20;i++) {
        console.log('#'+i);
        await Promise.all([
            bent(200)(new URL(`SurveyAjax?what=getrow&_=af&x=Gregorian&strid=&s=${login1.JSESSIONID}`, config.url)),
            bent(200)(new URL(`SurveyAjax?what=getrow&_=de&x=Gregorian&strid=&s=${login2.JSESSIONID}`, config.url)),
            bent(200)(new URL(`SurveyAjax?what=getrow&_=mt&x=Gregorian&strid=&s=${login3.JSESSIONID}`, config.url)),
            bent(200)(new URL(`SurveyAjax?what=getrow&_=es&x=Gregorian&strid=&s=${login4.JSESSIONID}`, config.url)),
            bent(200)(new URL(`SurveyAjax?what=getrow&_=ja&x=Gregorian&strid=&s=${login5.JSESSIONID}`, config.url)),
            bent(200)(new URL(`SurveyAjax?what=getrow&_=fi&x=Gregorian&strid=&s=${login6.JSESSIONID}`, config.url))
        ]);
        await delay(20);
    }
    http://127.0.0.1:8080/cldr-apps/SurveyAjax?what=getrow&_=af&x=Gregorian&strid=&s=${sessionId}
    console.log('Done');
    await stopBrowser();
})();