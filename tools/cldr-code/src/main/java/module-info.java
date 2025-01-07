module org.unicode.cldr {
    requires com.ibm.icu;
    // exports com.ibm.icu;
    exports com.ibm.icu.util;
    exports com.ibm.icu.dev;
    exports com.ibm.icu.dev.test;
    exports com.ibm.icu.dev.util;
    exports com.ibm.icu.text;
    // exports org.unicode.cldr;
    exports org.unicode.cldr.draft;
    exports org.unicode.cldr.posix;
    exports org.unicode.cldr.test;
    exports org.unicode.cldr.util;
    exports org.unicode.cldr.util.props;
    exports org.unicode.cldr.util.personname;
    exports org.unicode.cldr.json;
    exports org.unicode.cldr.api;
    exports org.unicode.cldr.icu;
    exports org.unicode.cldr.tool;
    exports org.unicode.cldr.tool.resolver;
}
