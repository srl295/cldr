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

public class ParseFrom extends Grammar{

    // public API
    /** Called to get a singleton instance of this class.
     * @return a singleton instance of this class, cast as the base class, Grammar. */
    public static Grammar getInstance(){
        if(factoryInstance == null){
            factoryInstance = new ParseFrom(getRules(), getUdts(), getOpcodes());
        }
        return factoryInstance;
    }

    // rule name enum
    /** The number of rules in the grammar */
    public static int ruleCount = 60;
    /** This enum provides easy to remember enum constants for locating the rule identifiers and names.
     * The enum constants have the same spelling as the rule names rendered in all caps with underscores replacing hyphens. */
    public enum RuleNames{
        /** id = <code>50</code>, name = <code>"ALPHA"</code> */
        ALPHA("ALPHA", 50, 207, 3),
        /** id = <code>46</code>, name = <code>"ASCII-CTRLS"</code> */
        ASCII_CTRLS("ASCII-CTRLS", 46, 190, 4),
        /** id = <code>47</code>, name = <code>"ASCII-PUNCT"</code> */
        ASCII_PUNCT("ASCII-PUNCT", 47, 194, 9),
        /** id = <code>4</code>, name = <code>"atom"</code> */
        ATOM("atom", 4, 15, 5),
        /** id = <code>2</code>, name = <code>"atoms"</code> */
        ATOMS("atoms", 2, 6, 8),
        /** id = <code>43</code>, name = <code>"backslash"</code> */
        BACKSLASH("backslash", 43, 179, 1),
        /** id = <code>14</code>, name = <code>"bounded-quantifier"</code> */
        BOUNDED_QUANTIFIER("bounded-quantifier", 14, 56, 6),
        /** id = <code>17</code>, name = <code>"capturing-group"</code> */
        CAPTURING_GROUP("capturing-group", 17, 69, 4),
        /** id = <code>19</code>, name = <code>"catom"</code> */
        CATOM("catom", 19, 77, 5),
        /** id = <code>18</code>, name = <code>"catoms"</code> */
        CATOMS("catoms", 18, 73, 4),
        /** id = <code>36</code>, name = <code>"char-range"</code> */
        CHAR_RANGE("char-range", 36, 142, 4),
        /** id = <code>30</code>, name = <code>"class"</code> */
        CLASS("class", 30, 108, 3),
        /** id = <code>13</code>, name = <code>"codepoint"</code> */
        CODEPOINT("codepoint", 13, 50, 6),
        /** id = <code>12</code>, name = <code>"codepointseq"</code> */
        CODEPOINTSEQ("codepointseq", 12, 44, 6),
        /** id = <code>41</code>, name = <code>"content-char"</code> */
        CONTENT_CHAR("content-char", 41, 166, 6),
        /** id = <code>22</code>, name = <code>"cphex"</code> */
        CPHEX("cphex", 22, 89, 2),
        /** id = <code>21</code>, name = <code>"cphexseq"</code> */
        CPHEXSEQ("cphexseq", 21, 83, 6),
        /** id = <code>20</code>, name = <code>"cquark"</code> */
        CQUARK("cquark", 20, 82, 1),
        /** id = <code>54</code>, name = <code>"CR"</code> */
        CR("CR", 54, 213, 1),
        /** id = <code>49</code>, name = <code>"DIGIT"</code> */
        DIGIT("DIGIT", 49, 206, 1),
        /** id = <code>3</code>, name = <code>"disjunction"</code> */
        DISJUNCTION("disjunction", 3, 14, 1),
        /** id = <code>42</code>, name = <code>"escaped-char"</code> */
        ESCAPED_CHAR("escaped-char", 42, 172, 7),
        /** id = <code>31</code>, name = <code>"fixed-class"</code> */
        FIXED_CLASS("fixed-class", 31, 111, 3),
        /** id = <code>32</code>, name = <code>"fixed-class-char"</code> */
        FIXED_CLASS_CHAR("fixed-class-char", 32, 114, 15),
        /** id = <code>0</code>, name = <code>"from-match"</code> */
        FROM_MATCH("from-match", 0, 0, 5),
        /** id = <code>10</code>, name = <code>"group"</code> */
        GROUP("group", 10, 38, 3),
        /** id = <code>55</code>, name = <code>"HEXDIG"</code> */
        HEXDIG("HEXDIG", 55, 214, 8),
        /** id = <code>52</code>, name = <code>"HTAB"</code> */
        HTAB("HTAB", 52, 211, 1),
        /** id = <code>45</code>, name = <code>"IDCHAR"</code> */
        IDCHAR("IDCHAR", 45, 186, 4),
        /** id = <code>53</code>, name = <code>"LF"</code> */
        LF("LF", 53, 212, 1),
        /** id = <code>56</code>, name = <code>"LHEXDIG"</code> */
        LHEXDIG("LHEXDIG", 56, 222, 8),
        /** id = <code>28</code>, name = <code>"marker-id"</code> */
        MARKER_ID("marker-id", 28, 105, 1),
        /** id = <code>24</code>, name = <code>"match-any-codepoint"</code> */
        MATCH_ANY_CODEPOINT("match-any-codepoint", 24, 96, 1),
        /** id = <code>26</code>, name = <code>"match-any-marker"</code> */
        MATCH_ANY_MARKER("match-any-marker", 26, 100, 1),
        /** id = <code>25</code>, name = <code>"match-marker"</code> */
        MATCH_MARKER("match-marker", 25, 97, 3),
        /** id = <code>27</code>, name = <code>"match-named-marker"</code> */
        MATCH_NAMED_MARKER("match-named-marker", 27, 101, 4),
        /** id = <code>58</code>, name = <code>"NAMECHAR"</code> */
        NAMECHAR("NAMECHAR", 58, 246, 8),
        /** id = <code>57</code>, name = <code>"NAMESTARTCHAR"</code> */
        NAMESTARTCHAR("NAMESTARTCHAR", 57, 230, 16),
        /** id = <code>59</code>, name = <code>"NMTOKEN"</code> */
        NMTOKEN("NMTOKEN", 59, 254, 2),
        /** id = <code>48</code>, name = <code>"NON-ASCII"</code> */
        NON_ASCII("NON-ASCII", 48, 203, 3),
        /** id = <code>16</code>, name = <code>"non-capturing-group"</code> */
        NON_CAPTURING_GROUP("non-capturing-group", 16, 63, 6),
        /** id = <code>6</code>, name = <code>"non-group"</code> */
        NON_GROUP("non-group", 6, 23, 4),
        /** id = <code>15</code>, name = <code>"optional-quantifier"</code> */
        OPTIONAL_QUANTIFIER("optional-quantifier", 15, 62, 1),
        /** id = <code>11</code>, name = <code>"quantifier"</code> */
        QUANTIFIER("quantifier", 11, 41, 3),
        /** id = <code>5</code>, name = <code>"quark"</code> */
        QUARK("quark", 5, 20, 3),
        /** id = <code>40</code>, name = <code>"range-char"</code> */
        RANGE_CHAR("range-char", 40, 158, 8),
        /** id = <code>37</code>, name = <code>"range-edge"</code> */
        RANGE_EDGE("range-edge", 37, 146, 3),
        /** id = <code>33</code>, name = <code>"set-class"</code> */
        SET_CLASS("set-class", 33, 129, 5),
        /** id = <code>35</code>, name = <code>"set-member"</code> */
        SET_MEMBER("set-member", 35, 138, 4),
        /** id = <code>34</code>, name = <code>"set-members"</code> */
        SET_MEMBERS("set-members", 34, 134, 4),
        /** id = <code>38</code>, name = <code>"set-negator"</code> */
        SET_NEGATOR("set-negator", 38, 149, 3),
        /** id = <code>9</code>, name = <code>"set-variable"</code> */
        SET_VARIABLE("set-variable", 9, 34, 4),
        /** id = <code>23</code>, name = <code>"simple-matcher"</code> */
        SIMPLE_MATCHER("simple-matcher", 23, 91, 5),
        /** id = <code>51</code>, name = <code>"SP"</code> */
        SP("SP", 51, 210, 1),
        /** id = <code>1</code>, name = <code>"start-context"</code> */
        START_CONTEXT("start-context", 1, 5, 1),
        /** id = <code>8</code>, name = <code>"string-variable"</code> */
        STRING_VARIABLE("string-variable", 8, 30, 4),
        /** id = <code>39</code>, name = <code>"text-char"</code> */
        TEXT_CHAR("text-char", 39, 152, 6),
        /** id = <code>29</code>, name = <code>"var-id"</code> */
        VAR_ID("var-id", 29, 106, 2),
        /** id = <code>7</code>, name = <code>"variable"</code> */
        VARIABLE("variable", 7, 27, 3),
        /** id = <code>44</code>, name = <code>"ws"</code> */
        WS("ws", 44, 180, 6);
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
    private static ParseFrom factoryInstance = null;
    private ParseFrom(Rule[] rules, Udt[] udts, Opcode[] opcodes){
        super(rules, udts, opcodes);
    }

    private static Rule[] getRules(){
    	Rule[] rules = new Rule[60];
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
    	Opcode[] op = new Opcode[256];
    	addOpcodes00(op);
        return op;
    }

    private static void addOpcodes00(Opcode[] op){
        {int[] a = {1,4}; op[0] = getOpcodeAlt(a);}
        {int[] a = {2,3}; op[1] = getOpcodeCat(a);}
        op[2] = getOpcodeRnm(1, 5); // start-context
        op[3] = getOpcodeRnm(2, 6); // atoms
        op[4] = getOpcodeRnm(2, 6); // atoms
        {char[] a = {94}; op[5] = getOpcodeTls(a);}
        {int[] a = {7,8}; op[6] = getOpcodeCat(a);}
        op[7] = getOpcodeRnm(4, 15); // atom
        op[8] = getOpcodeRep((char)0, Character.MAX_VALUE, 9);
        {int[] a = {10,13}; op[9] = getOpcodeAlt(a);}
        {int[] a = {11,12}; op[10] = getOpcodeCat(a);}
        op[11] = getOpcodeRnm(3, 14); // disjunction
        op[12] = getOpcodeRnm(4, 15); // atom
        op[13] = getOpcodeRnm(4, 15); // atom
        {char[] a = {124}; op[14] = getOpcodeTls(a);}
        {int[] a = {16,19}; op[15] = getOpcodeAlt(a);}
        {int[] a = {17,18}; op[16] = getOpcodeCat(a);}
        op[17] = getOpcodeRnm(5, 20); // quark
        op[18] = getOpcodeRnm(11, 41); // quantifier
        op[19] = getOpcodeRnm(5, 20); // quark
        {int[] a = {21,22}; op[20] = getOpcodeAlt(a);}
        op[21] = getOpcodeRnm(6, 23); // non-group
        op[22] = getOpcodeRnm(10, 38); // group
        {int[] a = {24,25,26}; op[23] = getOpcodeAlt(a);}
        op[24] = getOpcodeRnm(23, 91); // simple-matcher
        op[25] = getOpcodeRnm(12, 44); // codepointseq
        op[26] = getOpcodeRnm(7, 27); // variable
        {int[] a = {28,29}; op[27] = getOpcodeAlt(a);}
        op[28] = getOpcodeRnm(8, 30); // string-variable
        op[29] = getOpcodeRnm(9, 34); // set-variable
        {int[] a = {31,32,33}; op[30] = getOpcodeCat(a);}
        {char[] a = {36,123}; op[31] = getOpcodeTls(a);}
        op[32] = getOpcodeRnm(29, 106); // var-id
        {char[] a = {125}; op[33] = getOpcodeTls(a);}
        {int[] a = {35,36,37}; op[34] = getOpcodeCat(a);}
        {char[] a = {36,91}; op[35] = getOpcodeTls(a);}
        op[36] = getOpcodeRnm(29, 106); // var-id
        {char[] a = {93}; op[37] = getOpcodeTls(a);}
        {int[] a = {39,40}; op[38] = getOpcodeAlt(a);}
        op[39] = getOpcodeRnm(17, 69); // capturing-group
        op[40] = getOpcodeRnm(16, 63); // non-capturing-group
        {int[] a = {42,43}; op[41] = getOpcodeAlt(a);}
        op[42] = getOpcodeRnm(14, 56); // bounded-quantifier
        op[43] = getOpcodeRnm(15, 62); // optional-quantifier
        {int[] a = {45,46,47,48,49}; op[44] = getOpcodeCat(a);}
        op[45] = getOpcodeRnm(43, 179); // backslash
        {char[] a = {117}; op[46] = getOpcodeTls(a);}
        {char[] a = {123}; op[47] = getOpcodeTls(a);}
        op[48] = getOpcodeRnm(21, 83); // cphexseq
        {char[] a = {125}; op[49] = getOpcodeTls(a);}
        {int[] a = {51,52,53,54,55}; op[50] = getOpcodeCat(a);}
        op[51] = getOpcodeRnm(43, 179); // backslash
        {char[] a = {117}; op[52] = getOpcodeTls(a);}
        {char[] a = {123}; op[53] = getOpcodeTls(a);}
        op[54] = getOpcodeRnm(21, 83); // cphexseq
        {char[] a = {125}; op[55] = getOpcodeTls(a);}
        {int[] a = {57,58,59,60,61}; op[56] = getOpcodeCat(a);}
        {char[] a = {123}; op[57] = getOpcodeTls(a);}
        op[58] = getOpcodeRnm(49, 206); // DIGIT
        {char[] a = {44}; op[59] = getOpcodeTls(a);}
        op[60] = getOpcodeRnm(49, 206); // DIGIT
        {char[] a = {125}; op[61] = getOpcodeTls(a);}
        {char[] a = {63}; op[62] = getOpcodeTls(a);}
        {int[] a = {64,65,66,67,68}; op[63] = getOpcodeCat(a);}
        {char[] a = {40}; op[64] = getOpcodeTls(a);}
        {char[] a = {63}; op[65] = getOpcodeTls(a);}
        {char[] a = {58}; op[66] = getOpcodeTls(a);}
        op[67] = getOpcodeRnm(2, 6); // atoms
        {char[] a = {41}; op[68] = getOpcodeTls(a);}
        {int[] a = {70,71,72}; op[69] = getOpcodeCat(a);}
        {char[] a = {40}; op[70] = getOpcodeTls(a);}
        op[71] = getOpcodeRnm(18, 73); // catoms
        {char[] a = {41}; op[72] = getOpcodeTls(a);}
        {int[] a = {74,75}; op[73] = getOpcodeCat(a);}
        op[74] = getOpcodeRnm(19, 77); // catom
        op[75] = getOpcodeRep((char)0, Character.MAX_VALUE, 76);
        op[76] = getOpcodeRnm(19, 77); // catom
        {int[] a = {78,79}; op[77] = getOpcodeAlt(a);}
        op[78] = getOpcodeRnm(20, 82); // cquark
        {int[] a = {80,81}; op[79] = getOpcodeCat(a);}
        op[80] = getOpcodeRnm(20, 82); // cquark
        op[81] = getOpcodeRnm(11, 41); // quantifier
        op[82] = getOpcodeRnm(6, 23); // non-group
        {int[] a = {84,85}; op[83] = getOpcodeCat(a);}
        op[84] = getOpcodeRnm(22, 89); // cphex
        op[85] = getOpcodeRep((char)0, Character.MAX_VALUE, 86);
        {int[] a = {87,88}; op[86] = getOpcodeCat(a);}
        op[87] = getOpcodeRnm(51, 210); // SP
        op[88] = getOpcodeRnm(22, 89); // cphex
        op[89] = getOpcodeRep((char)1, (char)6, 90);
        op[90] = getOpcodeRnm(56, 222); // LHEXDIG
        {int[] a = {92,93,94,95}; op[91] = getOpcodeAlt(a);}
        op[92] = getOpcodeRnm(39, 152); // text-char
        op[93] = getOpcodeRnm(30, 108); // class
        op[94] = getOpcodeRnm(24, 96); // match-any-codepoint
        op[95] = getOpcodeRnm(25, 97); // match-marker
        {char[] a = {46}; op[96] = getOpcodeTls(a);}
        {int[] a = {98,99}; op[97] = getOpcodeAlt(a);}
        op[98] = getOpcodeRnm(26, 100); // match-any-marker
        op[99] = getOpcodeRnm(27, 101); // match-named-marker
        {char[] a = {92,109,123,46,125}; op[100] = getOpcodeTls(a);}
        {int[] a = {102,103,104}; op[101] = getOpcodeCat(a);}
        {char[] a = {92,109,123}; op[102] = getOpcodeTls(a);}
        op[103] = getOpcodeRnm(28, 105); // marker-id
        {char[] a = {125}; op[104] = getOpcodeTls(a);}
        op[105] = getOpcodeRnm(59, 254); // NMTOKEN
        op[106] = getOpcodeRep((char)1, (char)32, 107);
        op[107] = getOpcodeRnm(45, 186); // IDCHAR
        {int[] a = {109,110}; op[108] = getOpcodeAlt(a);}
        op[109] = getOpcodeRnm(31, 111); // fixed-class
        op[110] = getOpcodeRnm(33, 129); // set-class
        {int[] a = {112,113}; op[111] = getOpcodeCat(a);}
        op[112] = getOpcodeRnm(43, 179); // backslash
        op[113] = getOpcodeRnm(32, 114); // fixed-class-char
        {int[] a = {115,116,117,118,119,120,121,122,123,124,125,126,127,128}; op[114] = getOpcodeAlt(a);}
        {char[] a = {115}; op[115] = getOpcodeTls(a);}
        {char[] a = {83}; op[116] = getOpcodeTls(a);}
        {char[] a = {116}; op[117] = getOpcodeTls(a);}
        {char[] a = {114}; op[118] = getOpcodeTls(a);}
        {char[] a = {110}; op[119] = getOpcodeTls(a);}
        {char[] a = {102}; op[120] = getOpcodeTls(a);}
        {char[] a = {118}; op[121] = getOpcodeTls(a);}
        op[122] = getOpcodeRnm(43, 179); // backslash
        {char[] a = {36}; op[123] = getOpcodeTls(a);}
        {char[] a = {100}; op[124] = getOpcodeTls(a);}
        {char[] a = {119}; op[125] = getOpcodeTls(a);}
        {char[] a = {68}; op[126] = getOpcodeTls(a);}
        {char[] a = {87}; op[127] = getOpcodeTls(a);}
        {char[] a = {48}; op[128] = getOpcodeTls(a);}
        {int[] a = {130,131,132,133}; op[129] = getOpcodeCat(a);}
        {char[] a = {91}; op[130] = getOpcodeTls(a);}
        op[131] = getOpcodeRnm(38, 149); // set-negator
        op[132] = getOpcodeRnm(34, 134); // set-members
        {char[] a = {93}; op[133] = getOpcodeTls(a);}
        {int[] a = {135,136}; op[134] = getOpcodeCat(a);}
        op[135] = getOpcodeRnm(35, 138); // set-member
        op[136] = getOpcodeRep((char)0, Character.MAX_VALUE, 137);
        op[137] = getOpcodeRnm(35, 138); // set-member
        {int[] a = {139,140,141}; op[138] = getOpcodeAlt(a);}
        op[139] = getOpcodeRnm(39, 152); // text-char
        op[140] = getOpcodeRnm(36, 142); // char-range
        op[141] = getOpcodeRnm(25, 97); // match-marker
        {int[] a = {143,144,145}; op[142] = getOpcodeCat(a);}
        op[143] = getOpcodeRnm(37, 146); // range-edge
        {char[] a = {45}; op[144] = getOpcodeTls(a);}
        op[145] = getOpcodeRnm(37, 146); // range-edge
        {int[] a = {147,148}; op[146] = getOpcodeAlt(a);}
        op[147] = getOpcodeRnm(13, 50); // codepoint
        op[148] = getOpcodeRnm(40, 158); // range-char
        {int[] a = {150,151}; op[149] = getOpcodeAlt(a);}
        {char[] a = {94}; op[150] = getOpcodeTls(a);}
        {char[] a = {}; op[151] = getOpcodeTls(a);}
        {int[] a = {153,154,155,156,157}; op[152] = getOpcodeAlt(a);}
        op[153] = getOpcodeRnm(41, 166); // content-char
        op[154] = getOpcodeRnm(44, 180); // ws
        op[155] = getOpcodeRnm(42, 172); // escaped-char
        {char[] a = {45}; op[156] = getOpcodeTls(a);}
        {char[] a = {58}; op[157] = getOpcodeTls(a);}
        {int[] a = {159,160,161,162,163,164,165}; op[158] = getOpcodeAlt(a);}
        op[159] = getOpcodeRnm(41, 166); // content-char
        op[160] = getOpcodeRnm(44, 180); // ws
        op[161] = getOpcodeRnm(42, 172); // escaped-char
        {char[] a = {46}; op[162] = getOpcodeTls(a);}
        {char[] a = {124}; op[163] = getOpcodeTls(a);}
        {char[] a = {123}; op[164] = getOpcodeTls(a);}
        {char[] a = {125}; op[165] = getOpcodeTls(a);}
        {int[] a = {167,168,169,170,171}; op[166] = getOpcodeAlt(a);}
        op[167] = getOpcodeRnm(46, 190); // ASCII-CTRLS
        op[168] = getOpcodeRnm(47, 194); // ASCII-PUNCT
        op[169] = getOpcodeRnm(50, 207); // ALPHA
        op[170] = getOpcodeRnm(49, 206); // DIGIT
        op[171] = getOpcodeRnm(48, 203); // NON-ASCII
        {int[] a = {173,174}; op[172] = getOpcodeCat(a);}
        op[173] = getOpcodeRnm(43, 179); // backslash
        {int[] a = {175,176,177,178}; op[174] = getOpcodeAlt(a);}
        op[175] = getOpcodeRnm(43, 179); // backslash
        {char[] a = {123}; op[176] = getOpcodeTls(a);}
        {char[] a = {124}; op[177] = getOpcodeTls(a);}
        {char[] a = {125}; op[178] = getOpcodeTls(a);}
        {char[] a = {92}; op[179] = getOpcodeTbs(a);}
        {int[] a = {181,182,183,184,185}; op[180] = getOpcodeAlt(a);}
        op[181] = getOpcodeRnm(51, 210); // SP
        op[182] = getOpcodeRnm(52, 211); // HTAB
        op[183] = getOpcodeRnm(54, 213); // CR
        op[184] = getOpcodeRnm(53, 212); // LF
        {char[] a = {12288}; op[185] = getOpcodeTbs(a);}
        {int[] a = {187,188,189}; op[186] = getOpcodeAlt(a);}
        op[187] = getOpcodeRnm(50, 207); // ALPHA
        op[188] = getOpcodeRnm(49, 206); // DIGIT
        {char[] a = {95}; op[189] = getOpcodeTls(a);}
        {int[] a = {191,192,193}; op[190] = getOpcodeAlt(a);}
        op[191] = getOpcodeTrg((char)1, (char)8);
        op[192] = getOpcodeTrg((char)11, (char)12);
        op[193] = getOpcodeTrg((char)14, (char)31);
        {int[] a = {195,196,197,198,199,200,201,202}; op[194] = getOpcodeAlt(a);}
        op[195] = getOpcodeTrg((char)33, (char)35);
        op[196] = getOpcodeTrg((char)37, (char)39);
        {char[] a = {44}; op[197] = getOpcodeTbs(a);}
        {char[] a = {47}; op[198] = getOpcodeTbs(a);}
        op[199] = getOpcodeTrg((char)59, (char)62);
        {char[] a = {95}; op[200] = getOpcodeTbs(a);}
        {char[] a = {96}; op[201] = getOpcodeTbs(a);}
        op[202] = getOpcodeTrg((char)126, (char)127);
        {int[] a = {204,205}; op[203] = getOpcodeAlt(a);}
        op[204] = getOpcodeTrg((char)126, (char)55295);
        op[205] = getOpcodeTrg((char)57344, Character.MAX_VALUE);
        op[206] = getOpcodeTrg((char)48, (char)57);
        {int[] a = {208,209}; op[207] = getOpcodeAlt(a);}
        op[208] = getOpcodeTrg((char)65, (char)90);
        op[209] = getOpcodeTrg((char)97, (char)122);
        {char[] a = {32}; op[210] = getOpcodeTbs(a);}
        {char[] a = {63744}; op[211] = getOpcodeTbs(a);}
        {char[] a = {10}; op[212] = getOpcodeTbs(a);}
        {char[] a = {13}; op[213] = getOpcodeTbs(a);}
        {int[] a = {215,216,217,218,219,220,221}; op[214] = getOpcodeAlt(a);}
        op[215] = getOpcodeRnm(49, 206); // DIGIT
        {char[] a = {65}; op[216] = getOpcodeTls(a);}
        {char[] a = {66}; op[217] = getOpcodeTls(a);}
        {char[] a = {67}; op[218] = getOpcodeTls(a);}
        {char[] a = {68}; op[219] = getOpcodeTls(a);}
        {char[] a = {69}; op[220] = getOpcodeTls(a);}
        {char[] a = {70}; op[221] = getOpcodeTls(a);}
        {int[] a = {223,224,225,226,227,228,229}; op[222] = getOpcodeAlt(a);}
        op[223] = getOpcodeRnm(55, 214); // HEXDIG
        {char[] a = {97}; op[224] = getOpcodeTls(a);}
        {char[] a = {98}; op[225] = getOpcodeTls(a);}
        {char[] a = {99}; op[226] = getOpcodeTls(a);}
        {char[] a = {100}; op[227] = getOpcodeTls(a);}
        {char[] a = {101}; op[228] = getOpcodeTls(a);}
        {char[] a = {102}; op[229] = getOpcodeTls(a);}
        {int[] a = {231,232,233,234,235,236,237,238,239,240,241,242,243,244,245}; op[230] = getOpcodeAlt(a);}
        {char[] a = {58}; op[231] = getOpcodeTls(a);}
        op[232] = getOpcodeRnm(50, 207); // ALPHA
        {char[] a = {95}; op[233] = getOpcodeTls(a);}
        op[234] = getOpcodeTrg((char)192, (char)214);
        op[235] = getOpcodeTrg((char)216, (char)246);
        op[236] = getOpcodeTrg((char)248, (char)767);
        op[237] = getOpcodeTrg((char)880, (char)893);
        op[238] = getOpcodeTrg((char)895, (char)8191);
        op[239] = getOpcodeTrg((char)8204, (char)8205);
        op[240] = getOpcodeTrg((char)8304, (char)8591);
        op[241] = getOpcodeTrg((char)11264, (char)12271);
        op[242] = getOpcodeTrg((char)12289, (char)55295);
        op[243] = getOpcodeTrg((char)63744, (char)64975);
        op[244] = getOpcodeTrg((char)65008, (char)65533);
        op[245] = getOpcodeTrg((char)0, Character.MAX_VALUE);
        {int[] a = {247,248,249,250,251,252,253}; op[246] = getOpcodeAlt(a);}
        op[247] = getOpcodeRnm(57, 230); // NAMESTARTCHAR
        {char[] a = {45}; op[248] = getOpcodeTls(a);}
        {char[] a = {46}; op[249] = getOpcodeTls(a);}
        op[250] = getOpcodeRnm(49, 206); // DIGIT
        {char[] a = {183}; op[251] = getOpcodeTbs(a);}
        op[252] = getOpcodeTrg((char)768, (char)879);
        op[253] = getOpcodeTrg((char)8255, (char)8256);
        op[254] = getOpcodeRep((char)1, Character.MAX_VALUE, 255);
        op[255] = getOpcodeRnm(58, 246); // NAMECHAR
    }

    /** Displays the original SABNF grammar on the output device.
     * @param out the output device to display on.*/
    public static void display(PrintStream out){
        out.println(";");
        out.println("; org.unicode.cldr.util.keyboard.ParseFrom");
        out.println(";");
        out.println("; Copyright (c) 2025 Unicode, Inc.");
        out.println("; For terms of use, see http://www.unicode.org/copyright.html");
        out.println("; SPDX-License-Identifier: Unicode-3.0");
        out.println("; CLDR data files are interpreted according to the LDML specification (http://unicode.org/reports/tr35/)");
        out.println("");
        out.println("; This is an ABNF grammar for the CLDR Keyboard spec transform match syntax.");
        out.println("; Note that there are sample matching/failing data files in tools/scripts/keyboard-abnf-tests/");
        out.println("");
        out.println("; An entire <transform from=\"...\" /> string.");
        out.println("; Note that the empty string is not a match.");
        out.println("; Also note that a string may match this ABNF but be invalid according to the spec - which see.");
        out.println("");
        out.println("from-match        = start-context atoms / atoms");
        out.println("");
        out.println("start-context  = \"^\"");
        out.println("");
        out.println("; an empty match is not allowed.");
        out.println("atoms             = atom *(disjunction atom / atom)");
        out.println("");
        out.println("disjunction = \"|\"");
        out.println("");
        out.println("atom = quark quantifier / quark");
        out.println("");
        out.println("quark  = non-group / group");
        out.println("");
        out.println("non-group = simple-matcher / codepointseq / variable");
        out.println("");
        out.println("variable = string-variable / set-variable");
        out.println("");
        out.println("string-variable = \"${\" var-id \"}\"");
        out.println("set-variable = \"$[\" var-id \"]\"");
        out.println("");
        out.println("group = capturing-group / non-capturing-group");
        out.println("");
        out.println("quantifier    =  bounded-quantifier / optional-quantifier");
        out.println("");
        out.println("codepointseq           = backslash \"u\" \"{\" cphexseq \"}\"");
        out.println("codepoint           = backslash \"u\" \"{\" cphexseq \"}\"");
        out.println("");
        out.println("bounded-quantifier = \"{\" DIGIT \",\" DIGIT \"}\"");
        out.println("optional-quantifier =  \"?\"");
        out.println("");
        out.println("non-capturing-group = \"(\" \"?\" \":\" atoms \")\"");
        out.println("");
        out.println("; a capturing group may not contain other capturing groups.");
        out.println("capturing-group = \"(\" catoms \")\"");
        out.println("");
        out.println("; capturing atoms can't include any groups");
        out.println("catoms = catom *(catom)");
        out.println("; capturing atoms can't include any groups");
        out.println("catom = cquark / cquark quantifier");
        out.println("");
        out.println("; capturing atoms can't include groups");
        out.println("cquark = non-group");
        out.println("");
        out.println("; multiple hex codepoints");
        out.println("cphexseq = cphex *(SP cphex)");
        out.println("");
        out.println("; one hex codepoint (1-6 digits)");
        out.println("cphex =  1*6LHEXDIG");
        out.println("");
        out.println("simple-matcher      = text-char / class / match-any-codepoint / match-marker");
        out.println("");
        out.println("match-any-codepoint = \".\"");
        out.println("");
        out.println("match-marker = match-any-marker / match-named-marker");
        out.println("match-any-marker = \"\\m{.}\"");
        out.println("match-named-marker = \"\\m{\" marker-id \"}\"");
        out.println("");
        out.println("; marker id is nmtoken, but may be UAX31 in the future.");
        out.println("marker-id = NMTOKEN");
        out.println("; variable ID");
        out.println("var-id = 1*32IDCHAR");
        out.println("");
        out.println("class = fixed-class / set-class");
        out.println("");
        out.println("fixed-class = backslash fixed-class-char");
        out.println("");
        out.println("fixed-class-char = \"s\" / \"S\" / \"t\" / \"r\" / \"n\" / \"f\" / \"v\" / backslash / \"$\" / \"d\" / \"w\" / \"D\" / \"W\" / \"0\"");
        out.println("");
        out.println("set-class = \"[\" set-negator set-members \"]\"");
        out.println("set-members = set-member *(set-member)");
        out.println("set-member = text-char / char-range / match-marker");
        out.println("char-range = range-edge \"-\" range-edge");
        out.println("range-edge = codepoint / range-char");
        out.println("set-negator = \"^\" / \"\"");
        out.println("");
        out.println("; Restrictions on characters in various contexts");
        out.println("");
        out.println("; normal text");
        out.println("text-char         = content-char / ws / escaped-char / \"-\" / \":\"");
        out.println("; text in a range sequence");
        out.println("range-char        = content-char / ws / escaped-char / \".\"/ \"|\" / \"{\" / \"}\"");
        out.println("; group for everything BUT syntax chars.");
        out.println("content-char      = ASCII-CTRLS / ASCII-PUNCT / ALPHA / DIGIT / NON-ASCII");
        out.println("");
        out.println("; Character escapes");
        out.println("escaped-char = backslash ( backslash / \"{\" / \"|\" / \"}\" )");
        out.println("");
        out.println("backslash    = %x5C ; U+005C REVERSE SOLIDUS \"\\\"");
        out.println("ws = SP / HTAB / CR / LF / %x3000");
        out.println("");
        out.println("IDCHAR = ALPHA / DIGIT / \"_\"");
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
