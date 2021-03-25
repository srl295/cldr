const { markdownDiff } = require('markdown-diff');
const fs = require('fs').promises;
const { Remarkable } = require('remarkable');
const jsdom = require("jsdom");
const { JSDOM } = jsdom;

async function doit() {
    const f1 = fs.readFile('/Users/srl295/src/cldr-initial-md/docs/ldml/tr35.md');
    const f2 = fs.readFile('/Users/srl295/src/cldr-maint-39/docs/ldml/tr35.md');
    const res = markdownDiff((await f1).toString(), (await f2).toString());
    console.error('Diff done, rendering..');
    await fs.writeFile('out.md', res);

    var md = new Remarkable();

    md.set({
        html: true,
        breaks: true
    });
    md.inline.ruler.enable([ 'ins', 'del' ]);
    const rawHtml = md.render(res);

    // now fix
    const dom = new JSDOM(rawHtml);
    const document = dom.window.document;
    const head = dom.window.document.getElementsByTagName('head')[0];
    head.innerHTML = head.innerHTML + `<meta charset="utf-8"><link rel='stylesheet' type='text/css' media='screen' href='mddiff.css'>`;

    await fs.writeFile('out.html', dom.serialize());
}

doit().then(x => console.dir(x), e => console.error(e));
