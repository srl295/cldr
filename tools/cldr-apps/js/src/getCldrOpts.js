import * as cldrLoad from "./esm/cldrLoad.js";
import * as cldrStatus from "./esm/cldrStatus.js";
import { ref } from "vue";

// Refs
// set up event listeners for any refs we want to be reactive
const locale = ref(null);
cldrStatus.on("locale", () => (locale.value = cldrStatus.getCurrentLocale()));

const id = ref(null);
cldrStatus.on("id", () => (id.value = cldrStatus.getCurrentId()));

/**
 *
 * @returns values for $cldrOpts
 */
function getCldrOpts() {
  return {
    locmap: cldrLoad.getTheLocaleMap(),
    localeDir: cldrLoad.getLocaleDir(locale),
    sessionId: cldrStatus.getSessionId(),
    locale,
    id,
  };
}

export { getCldrOpts };
