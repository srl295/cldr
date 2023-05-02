import * as cldrStatus from "./esm/cldrStatus.js";
import { ref } from "vue";

// Refs
// set up event listeners for any refs we want to be reactive

/**
 * The current locale
 * See cldrStatus.getCurrentLocale()
 */
const locale = ref(null);
cldrStatus.on("locale", () => (locale.value = cldrStatus.getCurrentLocale()));

/**
 * The current id (usually a string xpath)
 * See cldrStatus.getCurrentId()
 */
const id = ref(null);
cldrStatus.on("id", () => (id.value = cldrStatus.getCurrentId()));

export {
  locale,
  id,
};
