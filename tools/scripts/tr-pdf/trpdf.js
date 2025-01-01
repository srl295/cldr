const fs = require('node:fs/promises');
const path = require('node:path');
const puppeteer = require('puppeteer');
const HTMLToPDF = require('convert-html-to-pdf').default;
const { createDataUri } = HTMLToPDF;
const { MultiBar, Presets } = require('cli-progress');
// requires run here
const INDIR = "../tr-archive/dist";
// by default: write PDF to same dir
const OUTDIR = "../tr-archive/dist";

async function writePdf(htmlFile, multibar, pp) {
    const baseName = path.basename(htmlFile, '.html');
    const outName = path.join(OUTDIR, `${baseName}.pdf`);
    const bar = multibar.create(4, 0);
    bar.update(0, { step: 'load  ', filename: path.basename(outName) });

    // read infile
    const data = await fs.readFile(htmlFile, 'utf-8');

    bar.increment(1, { step: 'chrome' });

    const browser = await pp;

    bar.increment(1, {step: 'page  '});
    const page = await browser.newPage();

    bar.increment(1, { step: 'render' });

    const uri = createDataUri(data, "text/html");
    await page.goto(uri, {
        waitUntil: 'networkidle2'
    });
    // // convert
    // const htmlToPdf = new HTMLToPDF(data, {
    //     waitForNetworkIdle: true,
    //     pdfOptions: {
            // format: 'letter',
            // displayHeaderFooter: true,
            // headerTemplate: `<header>LDML<span class="pageNumber"/>/<span class="totalPages"/></header>`,
            // footerTemplate: `<footer>Hello</footer>`,
    //     },
    // });
    // bar.increment(1, { step: 'render' });

    // const pdf = await htmlToPdf.convert();

    const headerTemplate = `<div style="display: inline-block; width: 75%; margin: 0 2cm">
                            <div style="float: right; font-size: 7pt; text-align: right;">unicode.org/reports/tr35/${baseName}.html</div>
                        </div>`
const footerTemplate = `<div style="margin: 0 2cm; width: 75%; font-size: 7px; text-align: center;"><span class="pageNumber"/>/<span class="totalPages"/> © 2024 Unicode, Inc.</div>`;

    const pdf = await page.pdf({
        path: outName,
        format: 'letter',
        margin: {
            left: '15',
            right: '15',
            top: '30',
            bottom: '40',
        },
        displayHeaderFooter: true,
        headerTemplate,
        footerTemplate,
    });
    // bar.increment(1, { step: 'write ' });

    // fs.writeFile(outName, pdf);
    bar.increment(1, { step: 'done  ' });

    bar.stop();

    return outName;
}

async function main() {
    // check that the dir exists
    try {
        await fs.readdir(INDIR);
    } catch(cause) {
        throw Error(`Could not read ${INDIR} - please run 'npm i' and 'npm run build' in the tr-archive dir first.`,{cause});
    }

    const browser = puppeteer.launch({ headless: true });

    // ensure outdir exists if different
    await fs.mkdir(OUTDIR,{recursive:true});

    // now build the list
    const htmlList = (await fs.readdir(INDIR))
        .filter((f) => /^tr35-.*\.html$/.test(f))
        .filter((f) => /^tr35-keyboards.html$/.test(f)) // to just convert one
        .map((f) => path.join(INDIR, f));

    const multibar = new MultiBar({
        barsize: 10,
        clearOnComplete: false,
        hideCursor: true,
        format: ' {bar} | {step}\t | {duration_formatted}\t | {filename}',
    }, Presets.legacy);

    const results = await Promise.all(htmlList.map(f => writePdf(f, multibar, browser)));

    await (await browser).close();

    multibar.stop();

    return results;
}

main().then((result)=>console.dir(result), (e) => {
    console.error(e);
    process.exitCode=1;
});
