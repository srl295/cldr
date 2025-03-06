import { Version3Client } from 'jira.js';
import fs from 'node:fs';
import process from 'node:process';

const { JIRA_HOST, JIRA_EMAIL, JIRA_APITOKEN, JIRA_FIELD, PR_TITLE, MERGED_TO } = process.env;

// DEBUG&&console.dir({JIRA_HOST, JIRA_EMAIL, JIRA_APITOKEN});
if (!JIRA_HOST || !JIRA_EMAIL || !JIRA_APITOKEN) {
  throw Error(`Configuration error: set JIRA_HOST, JIRA_EMAIL, JIRA_APITOKEN`);
}

if (!PR_TITLE) throw Error(`PR_TITLE unset, something is wrong`);
if (!MERGED_TO) throw Error(`MERGED_TO unset, something is wrong`);

const DEBUG = false;

DEBUG && console.log(`Logging into ${config.host} as ${config.email}`);

const client = new Version3Client({
  host: JIRA_HOST,
  authentication: {
    basic: {
      email: JIRA_EMAIL,
      apiToken: JIRA_APITOKEN,
    },
  },
  newErrorHandling: true,
});

async function main() {
  const resp = /^([A-Z]+-[0-9]+)/.exec(PR_TITLE);
  if (!resp || !resp[1]) throw Error(`Could not find JIRA ticket in ${PR_TITLE}`);
  const issueIdOrKey = resp[1];
  const mergedTo = MERGED_TO;

  console.log(`Updating ${issueIdOrKey} on Jira with a merge to ${mergedTo}…`);

  throw Error('- stop here -');

  try {
    await client.myself.getCurrentUser();
  } catch (e) {
    DEBUG && console.error(e);
    throw Error(`JIRA Authentication error: ${e}`);
  }


  const ourField = JIRA_FIELD || 'Merged'; // could be a config option later

  DEBUG && console.dir({ issueIdOrKey, mergedTo, ourField }); // our test setup

  const fields = (await client.issueFields.getFields())
    .filter(({ name }) => name === ourField);
  if (fields.length !== 1) {
    throw Error(`Looking for 1 custom field named ${ourField} but found ${fields.length}`);
  }
  const { id: fieldId } = fields[0];
  DEBUG && console.dir({ fieldId });
  try {
    const resp = await client.issues.editIssue({
      issueIdOrKey,
      update: {
        [fieldId]: [
          { add: mergedTo },
        ],
      },
    });
    DEBUG && console.dir(resp);
  } catch (e) {
    DEBUG && console.error(e);
    throw Error(`Error updating ${ourField}+${mergedTo} on ${issueIdOrKey}: ${e}`);
  }
  console.log(`Updated ${ourField} += ${mergedTo} on ${JIRA_HOST}/browse/${issueIdOrKey}`);
}

main();
