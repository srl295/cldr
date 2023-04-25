import { ref } from "vue";

/**
 * The current user locale. Read only.
 */
const locale = ref(null);

/**
 * The current XPath (hex id). Read only.
 */
const xpath = ref(null);

export { locale, xpath };
