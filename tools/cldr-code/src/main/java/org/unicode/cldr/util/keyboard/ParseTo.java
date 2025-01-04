// This class has been generated automatically
// from an SABNF grammar by Java APG, Verision 1.1.0.
// Copyright (c) 2021 Lowell D. Thomas, all rights reserved.
// Licensed under the 2-Clause BSD License.

package org.unicode.cldr.util.keyboard;

import apg.Grammar;
import java.io.PrintStream;
/** This class has been generated automatically from an SABNF grammar by
 * the {@link apg.Generator} class of Java APG, Version 1.1.0.<br>
 * It is an extension of the {@link apg.Grammar}
 * class containing additional members and enums not found
 * in the base class.<br>
 * The function {@link #getInstance()} will return a reference to a static,
 * singleton instance of the class.
 */

public class ParseTo extends Grammar{

    // public API
    /** Called to get a singleton instance of this class.
     * @return a singleton instance of this class, cast as the base class, Grammar. */
    public static Grammar getInstance(){
        if(factoryInstance == null){
            factoryInstance = new ParseTo(getRules(), getUdts(), getOpcodes());
        }
        return factoryInstance;
    }

    // rule name enum
    /** The number of rules in the grammar */
    public static int ruleCount = 32;
    /** This enum provides easy to remember enum constants for locating the rule identifiers and names.
     * The enum constants have the same spelling as the rule names rendered in all caps with underscores replacing hyphens. */
    public enum RuleNames{
        /** id = <code>22</code>, name = <code>"ALPHA"</code> */
        ALPHA("ALPHA", 22, 102, 3),
        /** id = <code>18</code>, name = <code>"ASCII-CTRLS"</code> */
        ASCII_CTRLS("ASCII-CTRLS", 18, 85, 4),
        /** id = <code>19</code>, name = <code>"ASCII-PUNCT"</code> */
        ASCII_PUNCT("ASCII-PUNCT", 19, 89, 9),
        /** id = <code>2</code>, name = <code>"atom"</code> */
        ATOM("atom", 2, 3, 8),
        /** id = <code>1</code>, name = <code>"atoms"</code> */
        ATOMS("atoms", 1, 1, 2),
        /** id = <code>15</code>, name = <code>"backslash"</code> */
        BACKSLASH("backslash", 15, 74, 1),
        /** id = <code>6</code>, name = <code>"codepointseq"</code> */
        CODEPOINTSEQ("codepointseq", 6, 22, 6),
        /** id = <code>13</code>, name = <code>"content-char"</code> */
        CONTENT_CHAR("content-char", 13, 61, 6),
        /** id = <code>8</code>, name = <code>"cphex"</code> */
        CPHEX("cphex", 8, 34, 2),
        /** id = <code>7</code>, name = <code>"cphexseq"</code> */
        CPHEXSEQ("cphexseq", 7, 28, 6),
        /** id = <code>26</code>, name = <code>"CR"</code> */
        CR("CR", 26, 108, 1),
        /** id = <code>21</code>, name = <code>"DIGIT"</code> */
        DIGIT("DIGIT", 21, 101, 1),
        /** id = <code>14</code>, name = <code>"escaped-char"</code> */
        ESCAPED_CHAR("escaped-char", 14, 67, 7),
        /** id = <code>4</code>, name = <code>"group-reference"</code> */
        GROUP_REFERENCE("group-reference", 4, 15, 3),
        /** id = <code>27</code>, name = <code>"HEXDIG"</code> */
        HEXDIG("HEXDIG", 27, 109, 8),
        /** id = <code>24</code>, name = <code>"HTAB"</code> */
        HTAB("HTAB", 24, 106, 1),
        /** id = <code>17</code>, name = <code>"IDCHAR"</code> */
        IDCHAR("IDCHAR", 17, 81, 4),
        /** id = <code>25</code>, name = <code>"LF"</code> */
        LF("LF", 25, 107, 1),
        /** id = <code>28</code>, name = <code>"LHEXDIG"</code> */
        LHEXDIG("LHEXDIG", 28, 117, 8),
        /** id = <code>5</code>, name = <code>"mapped-set"</code> */
        MAPPED_SET("mapped-set", 5, 18, 4),
        /** id = <code>10</code>, name = <code>"marker-id"</code> */
        MARKER_ID("marker-id", 10, 40, 1),
        /** id = <code>30</code>, name = <code>"NAMECHAR"</code> */
        NAMECHAR("NAMECHAR", 30, 141, 8),
        /** id = <code>9</code>, name = <code>"named-marker"</code> */
        NAMED_MARKER("named-marker", 9, 36, 4),
        /** id = <code>29</code>, name = <code>"NAMESTARTCHAR"</code> */
        NAMESTARTCHAR("NAMESTARTCHAR", 29, 125, 16),
        /** id = <code>31</code>, name = <code>"NMTOKEN"</code> */
        NMTOKEN("NMTOKEN", 31, 149, 2),
        /** id = <code>20</code>, name = <code>"NON-ASCII"</code> */
        NON_ASCII("NON-ASCII", 20, 98, 3),
        /** id = <code>12</code>, name = <code>"replacement-char"</code> */
        REPLACEMENT_CHAR("replacement-char", 12, 43, 18),
        /** id = <code>23</code>, name = <code>"SP"</code> */
        SP("SP", 23, 105, 1),
        /** id = <code>3</code>, name = <code>"string-variable"</code> */
        STRING_VARIABLE("string-variable", 3, 11, 4),
        /** id = <code>0</code>, name = <code>"to-replacement"</code> */
        TO_REPLACEMENT("to-replacement", 0, 0, 1),
        /** id = <code>11</code>, name = <code>"var-id"</code> */
        VAR_ID("var-id", 11, 41, 2),
        /** id = <code>16</code>, name = <code>"ws"</code> */
        WS("ws", 16, 75, 6);
        private String name;
        private int id;
        private int offset;
        private int count;
        RuleNames(String string, int id, int offset, int count){
            this.name = string;
            this.id = id;
            this.offset = offset;
            this.count = count;
        }
        /** Associates the enum with the original grammar name of the rule it represents.
        * @return the original grammar rule name. */
        public  String ruleName(){return name;}
        /** Associates the enum with an identifier for the grammar rule it represents.
        * @return the rule name identifier. */
        public  int    ruleID(){return id;}
        private int    opcodeOffset(){return offset;}
        private int    opcodeCount(){return count;}
    }

    // UDT name enum
    /** The number of UDTs in the grammar */
    public static int udtCount = 0;
    /** This enum provides easy to remember enum constants for locating the UDT identifiers and names.
     * The enum constants have the same spelling as the UDT names rendered in all caps with underscores replacing hyphens. */
    public enum UdtNames{
    }

    // private
    private static ParseTo factoryInstance = null;
    private ParseTo(Rule[] rules, Udt[] udts, Opcode[] opcodes){
        super(rules, udts, opcodes);
    }

    private static Rule[] getRules(){
    	Rule[] rules = new Rule[32];
        for(RuleNames r : RuleNames.values()){
            rules[r.ruleID()] = getRule(r.ruleID(), r.ruleName(), r.opcodeOffset(), r.opcodeCount());
        }
        return rules;
    }

    private static Udt[] getUdts(){
    	Udt[] udts = new Udt[0];
        return udts;
    }

        // opcodes
    private static Opcode[] getOpcodes(){
    	Opcode[] op = new Opcode[151];
    	addOpcodes00(op);
        return op;
    }

    private static void addOpcodes00(Opcode[] op){
        op[0] = getOpcodeRnm(1, 1); // atoms
        op[1] = getOpcodeRep((char)0, Character.MAX_VALUE, 2);
        op[2] = getOpcodeRnm(2, 3); // atom
        {int[] a = {4,5,6,7,8,9,10}; op[3] = getOpcodeAlt(a);}
        op[4] = getOpcodeRnm(12, 43); // replacement-char
        op[5] = getOpcodeRnm(14, 67); // escaped-char
        op[6] = getOpcodeRnm(4, 15); // group-reference
        op[7] = getOpcodeRnm(6, 22); // codepointseq
        op[8] = getOpcodeRnm(9, 36); // named-marker
        op[9] = getOpcodeRnm(3, 11); // string-variable
        op[10] = getOpcodeRnm(5, 18); // mapped-set
        {int[] a = {12,13,14}; op[11] = getOpcodeCat(a);}
        {char[] a = {36,123}; op[12] = getOpcodeTls(a);}
        op[13] = getOpcodeRnm(11, 41); // var-id
        {char[] a = {125}; op[14] = getOpcodeTls(a);}
        {int[] a = {16,17}; op[15] = getOpcodeCat(a);}
        {char[] a = {36}; op[16] = getOpcodeTls(a);}
        op[17] = getOpcodeRnm(21, 101); // DIGIT
        {int[] a = {19,20,21}; op[18] = getOpcodeCat(a);}
        {char[] a = {36,91,49,58}; op[19] = getOpcodeTls(a);}
        op[20] = getOpcodeRnm(11, 41); // var-id
        {char[] a = {93}; op[21] = getOpcodeTls(a);}
        {int[] a = {23,24,25,26,27}; op[22] = getOpcodeCat(a);}
        op[23] = getOpcodeRnm(15, 74); // backslash
        {char[] a = {117}; op[24] = getOpcodeTls(a);}
        {char[] a = {123}; op[25] = getOpcodeTls(a);}
        op[26] = getOpcodeRnm(7, 28); // cphexseq
        {char[] a = {125}; op[27] = getOpcodeTls(a);}
        {int[] a = {29,30}; op[28] = getOpcodeCat(a);}
        op[29] = getOpcodeRnm(8, 34); // cphex
        op[30] = getOpcodeRep((char)0, Character.MAX_VALUE, 31);
        {int[] a = {32,33}; op[31] = getOpcodeCat(a);}
        op[32] = getOpcodeRnm(23, 105); // SP
        op[33] = getOpcodeRnm(8, 34); // cphex
        op[34] = getOpcodeRep((char)1, (char)6, 35);
        op[35] = getOpcodeRnm(28, 117); // LHEXDIG
        {int[] a = {37,38,39}; op[36] = getOpcodeCat(a);}
        {char[] a = {92,109,123}; op[37] = getOpcodeTls(a);}
        op[38] = getOpcodeRnm(10, 40); // marker-id
        {char[] a = {125}; op[39] = getOpcodeTls(a);}
        op[40] = getOpcodeRnm(31, 149); // NMTOKEN
        op[41] = getOpcodeRep((char)1, (char)32, 42);
        op[42] = getOpcodeRnm(17, 81); // IDCHAR
        {int[] a = {44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60}; op[43] = getOpcodeAlt(a);}
        op[44] = getOpcodeRnm(13, 61); // content-char
        op[45] = getOpcodeRnm(16, 75); // ws
        op[46] = getOpcodeRnm(14, 67); // escaped-char
        {char[] a = {45}; op[47] = getOpcodeTls(a);}
        {char[] a = {58}; op[48] = getOpcodeTls(a);}
        {char[] a = {40}; op[49] = getOpcodeTls(a);}
        {char[] a = {41}; op[50] = getOpcodeTls(a);}
        {char[] a = {46}; op[51] = getOpcodeTls(a);}
        {char[] a = {42}; op[52] = getOpcodeTls(a);}
        {char[] a = {43}; op[53] = getOpcodeTls(a);}
        {char[] a = {63}; op[54] = getOpcodeTls(a);}
        {char[] a = {91}; op[55] = getOpcodeTls(a);}
        {char[] a = {93}; op[56] = getOpcodeTls(a);}
        {char[] a = {94}; op[57] = getOpcodeTls(a);}
        {char[] a = {123}; op[58] = getOpcodeTls(a);}
        {char[] a = {125}; op[59] = getOpcodeTls(a);}
        {char[] a = {124}; op[60] = getOpcodeTls(a);}
        {int[] a = {62,63,64,65,66}; op[61] = getOpcodeAlt(a);}
        op[62] = getOpcodeRnm(18, 85); // ASCII-CTRLS
        op[63] = getOpcodeRnm(19, 89); // ASCII-PUNCT
        op[64] = getOpcodeRnm(22, 102); // ALPHA
        op[65] = getOpcodeRnm(21, 101); // DIGIT
        op[66] = getOpcodeRnm(20, 98); // NON-ASCII
        {int[] a = {68,73}; op[67] = getOpcodeAlt(a);}
        {int[] a = {69,70}; op[68] = getOpcodeCat(a);}
        op[69] = getOpcodeRnm(15, 74); // backslash
        {int[] a = {71,72}; op[70] = getOpcodeAlt(a);}
        op[71] = getOpcodeRnm(15, 74); // backslash
        {char[] a = {36}; op[72] = getOpcodeTls(a);}
        {char[] a = {36,36}; op[73] = getOpcodeTls(a);}
        {char[] a = {92}; op[74] = getOpcodeTbs(a);}
        {int[] a = {76,77,78,79,80}; op[75] = getOpcodeAlt(a);}
        op[76] = getOpcodeRnm(23, 105); // SP
        op[77] = getOpcodeRnm(24, 106); // HTAB
        op[78] = getOpcodeRnm(26, 108); // CR
        op[79] = getOpcodeRnm(25, 107); // LF
        {char[] a = {12288}; op[80] = getOpcodeTbs(a);}
        {int[] a = {82,83,84}; op[81] = getOpcodeAlt(a);}
        op[82] = getOpcodeRnm(22, 102); // ALPHA
        op[83] = getOpcodeRnm(21, 101); // DIGIT
        {char[] a = {95}; op[84] = getOpcodeTls(a);}
        {int[] a = {86,87,88}; op[85] = getOpcodeAlt(a);}
        op[86] = getOpcodeTrg((char)1, (char)8);
        op[87] = getOpcodeTrg((char)11, (char)12);
        op[88] = getOpcodeTrg((char)14, (char)31);
        {int[] a = {90,91,92,93,94,95,96,97}; op[89] = getOpcodeAlt(a);}
        op[90] = getOpcodeTrg((char)33, (char)35);
        op[91] = getOpcodeTrg((char)37, (char)39);
        {char[] a = {44}; op[92] = getOpcodeTbs(a);}
        {char[] a = {47}; op[93] = getOpcodeTbs(a);}
        op[94] = getOpcodeTrg((char)59, (char)62);
        {char[] a = {95}; op[95] = getOpcodeTbs(a);}
        {char[] a = {96}; op[96] = getOpcodeTbs(a);}
        op[97] = getOpcodeTrg((char)126, (char)127);
        {int[] a = {99,100}; op[98] = getOpcodeAlt(a);}
        op[99] = getOpcodeTrg((char)126, (char)55295);
        op[100] = getOpcodeTrg((char)57344, Character.MAX_VALUE);
        op[101] = getOpcodeTrg((char)48, (char)57);
        {int[] a = {103,104}; op[102] = getOpcodeAlt(a);}
        op[103] = getOpcodeTrg((char)65, (char)90);
        op[104] = getOpcodeTrg((char)97, (char)122);
        {char[] a = {32}; op[105] = getOpcodeTbs(a);}
        {char[] a = {63744}; op[106] = getOpcodeTbs(a);}
        {char[] a = {10}; op[107] = getOpcodeTbs(a);}
        {char[] a = {13}; op[108] = getOpcodeTbs(a);}
        {int[] a = {110,111,112,113,114,115,116}; op[109] = getOpcodeAlt(a);}
        op[110] = getOpcodeRnm(21, 101); // DIGIT
        {char[] a = {65}; op[111] = getOpcodeTls(a);}
        {char[] a = {66}; op[112] = getOpcodeTls(a);}
        {char[] a = {67}; op[113] = getOpcodeTls(a);}
        {char[] a = {68}; op[114] = getOpcodeTls(a);}
        {char[] a = {69}; op[115] = getOpcodeTls(a);}
        {char[] a = {70}; op[116] = getOpcodeTls(a);}
        {int[] a = {118,119,120,121,122,123,124}; op[117] = getOpcodeAlt(a);}
        op[118] = getOpcodeRnm(27, 109); // HEXDIG
        {char[] a = {97}; op[119] = getOpcodeTls(a);}
        {char[] a = {98}; op[120] = getOpcodeTls(a);}
        {char[] a = {99}; op[121] = getOpcodeTls(a);}
        {char[] a = {100}; op[122] = getOpcodeTls(a);}
        {char[] a = {101}; op[123] = getOpcodeTls(a);}
        {char[] a = {102}; op[124] = getOpcodeTls(a);}
        {int[] a = {126,127,128,129,130,131,132,133,134,135,136,137,138,139,140}; op[125] = getOpcodeAlt(a);}
        {char[] a = {58}; op[126] = getOpcodeTls(a);}
        op[127] = getOpcodeRnm(22, 102); // ALPHA
        {char[] a = {95}; op[128] = getOpcodeTls(a);}
        op[129] = getOpcodeTrg((char)192, (char)214);
        op[130] = getOpcodeTrg((char)216, (char)246);
        op[131] = getOpcodeTrg((char)248, (char)767);
        op[132] = getOpcodeTrg((char)880, (char)893);
        op[133] = getOpcodeTrg((char)895, (char)8191);
        op[134] = getOpcodeTrg((char)8204, (char)8205);
        op[135] = getOpcodeTrg((char)8304, (char)8591);
        op[136] = getOpcodeTrg((char)11264, (char)12271);
        op[137] = getOpcodeTrg((char)12289, (char)55295);
        op[138] = getOpcodeTrg((char)63744, (char)64975);
        op[139] = getOpcodeTrg((char)65008, (char)65533);
        op[140] = getOpcodeTrg((char)0, Character.MAX_VALUE);
        {int[] a = {142,143,144,145,146,147,148}; op[141] = getOpcodeAlt(a);}
        op[142] = getOpcodeRnm(29, 125); // NAMESTARTCHAR
        {char[] a = {45}; op[143] = getOpcodeTls(a);}
        {char[] a = {46}; op[144] = getOpcodeTls(a);}
        op[145] = getOpcodeRnm(21, 101); // DIGIT
        {char[] a = {183}; op[146] = getOpcodeTbs(a);}
        op[147] = getOpcodeTrg((char)768, (char)879);
        op[148] = getOpcodeTrg((char)8255, (char)8256);
        op[149] = getOpcodeRep((char)1, Character.MAX_VALUE, 150);
        op[150] = getOpcodeRnm(30, 141); // NAMECHAR
    }

    /** Displays the original SABNF grammar on the output device.
     * @param out the output device to display on.*/
    public static void display(PrintStream out){
        out.println(";");
        out.println("; org.unicode.cldr.util.keyboard.ParseTo");
        out.println(";");
        out.println("; Copyright (c) 2025 Unicode, Inc.");
        out.println("; For terms of use, see http://www.unicode.org/copyright.html");
        out.println("; SPDX-License-Identifier: Unicode-3.0");
        out.println("; CLDR data files are interpreted according to the LDML specification (http://unicode.org/reports/tr35/)");
        out.println("");
        out.println("; This is an ABNF grammar for the CLDR Keyboard spec transform to= (replacement) match syntax.");
        out.println("; Note that there are sample matching/failing data files in tools/scripts/keyboard-abnf-tests/");
        out.println("");
        out.println("; An entire <transform to=\"...\" /> string.");
        out.println("; An empty string is valid, meaning deletion.");
        out.println("; Also note that a string may match this ABNF but be invalid according to the spec - which see.");
        out.println("to-replacement          = atoms");
        out.println("");
        out.println("atoms             = *(atom)");
        out.println("");
        out.println("atom = replacement-char / escaped-char / group-reference / codepointseq / named-marker / string-variable / mapped-set");
        out.println("");
        out.println("string-variable = \"${\" var-id \"}\"");
        out.println("");
        out.println("group-reference = \"$\" DIGIT");
        out.println("");
        out.println("mapped-set = \"$[1:\" var-id \"]\"");
        out.println("");
        out.println("codepointseq           = backslash \"u\" \"{\" cphexseq \"}\"");
        out.println("");
        out.println("; multiple hex codepoints");
        out.println("cphexseq = cphex *(SP cphex)");
        out.println("");
        out.println("; one hex codepoint (1-6 digits)");
        out.println("cphex =  1*6LHEXDIG");
        out.println("");
        out.println("named-marker = \"\\m{\" marker-id \"}\"");
        out.println("");
        out.println("; marker id is nmtoken, but may be UAX31 in the future.");
        out.println("marker-id = NMTOKEN");
        out.println("; variable ID");
        out.println("var-id = 1*32IDCHAR");
        out.println("");
        out.println("; fixed-class = backslash fixed-class-char");
        out.println("");
        out.println("; fixed-class-char = \"s\" / \"S\" / \"t\" / \"r\" / \"n\" / \"f\" / \"v\" / backslash / \"$\" / \"d\" / \"w\" / \"D\" / \"W\" / \"0\"");
        out.println("");
        out.println("; normal text");
        out.println("replacement-char         = content-char / ws / escaped-char / \"-\" / \":\" / \"(\" / \")\" / \".\" / \"*\" / \"+\" / \"?\" / \"[\" / \"]\" / \"^\" / \"{\" / \"}\" / \"|\"");
        out.println("; group for everything BUT syntax chars.");
        out.println("content-char      = ASCII-CTRLS / ASCII-PUNCT / ALPHA / DIGIT / NON-ASCII");
        out.println("");
        out.println("; Character escapes");
        out.println("escaped-char = backslash ( backslash / \"$\" ) / \"$$\"");
        out.println("");
        out.println("backslash    = %x5C ; U+005C REVERSE SOLIDUS \"\\\"");
        out.println("ws = SP / HTAB / CR / LF / %x3000");
        out.println("");
        out.println("IDCHAR = ALPHA / DIGIT / \"_\"");
        out.println("; below is same as transform-from for maintenance");
        out.println("ASCII-CTRLS        = %x01-08       ; omit NULL (%x00), HTAB (%x09) and LF (%x0A)");
        out.println("                  / %x0B-0C        ; omit CR (%x0D)");
        out.println("                  / %x0E-1F        ; omit SP (%x20)");
        out.println("ASCII-PUNCT        = %x21-23       ; omit DOLLAR");
        out.println("                  / %x25-27        ; omit () * +");
        out.println("                  / %x2C           ; omit . (%x2E) and - (%x2D)");
        out.println("                  / %x2F           ; skip over digits and :");
        out.println("                  / %x3B-3E        ; omit ? 3f");
        out.println("                  / %x5F           ; omit upper A-Z and [\\]^");
        out.println("                  / %x60           ; omit a-z {|}");
        out.println("                  / %x7E-7F        ; just for completeness");
        out.println("NON-ASCII =         %x7E-D7FF      ; omit surrogates");
        out.println("                  / %xE000-10FFFF  ; that's the rest. (TODO: omit other non-characters)");
        out.println("");
        out.println("; from STD-68");
        out.println("DIGIT          =  %x30-39             ; 0-9");
        out.println("ALPHA          =  %x41-5A / %x61-7A   ; A-Z / a-z");
        out.println("SP             =  %x20");
        out.println("HTAB           =  %xF900              ; horizontal tab");
        out.println("LF             =  %x0A                ; linefeed");
        out.println("CR             =  %x0D                ; carriage return");
        out.println("HEXDIG         =  DIGIT / \"A\" / \"B\" / \"C\" / \"D\" / \"E\" / \"F\"");
        out.println("; like HEXDIG but lowercase also");
        out.println("LHEXDIG         =  HEXDIG / \"a\" / \"b\" / \"c\" / \"d\" / \"e\" / \"f\"");
        out.println("");
        out.println("; from XML");
        out.println("NAMESTARTCHAR   =   	\":\" / ALPHA / \"_\" / %xC0-D6 / %xD8-F6 / %xF8-2FF / %x370-37D / %x37F-1FFF / %x200C-200D / %x2070-218F / %x2C00-2FEF / %x3001-D7FF / %xF900-FDCF / %xFDF0-FFFD");
        out.println("NAMESTARTCHAR   =/  %x10000-10FFFF    ; SKIP-NODE-ABNF: TODO: <https://github.com/hildjj/node-abnf/issues/25>");
        out.println("");
        out.println("NAMECHAR	   =   	NAMESTARTCHAR / \"-\" / \".\" / DIGIT / %xB7 / %x0300-036F / %x203F-2040");
        out.println("; NAME	   =   	NAMESTARTCHAR *(NAMECHAR)");
        out.println("NMTOKEN	   =   	1*NAMECHAR");
        out.println("; NMTOKENS	   =   	NMTOKEN *(SP NMTOKEN)");
    }
}
