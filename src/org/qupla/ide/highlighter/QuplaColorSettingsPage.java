package org.qupla.ide.highlighter;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.qupla.ide.ui.QuplaIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.KEYWORD;

public class QuplaColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comments", QuplaSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Keywords", KEYWORD),
            new AttributesDescriptor("Type definition", QuplaSyntaxHighlighter.QUPLA_TYPE_DECLARATION),
            new AttributesDescriptor("Type reference", QuplaSyntaxHighlighter.QUPLA_TYPE_REFERENCE),
            new AttributesDescriptor("Field definition", QuplaSyntaxHighlighter.QUPLA_FIELD_DECLARATION),
            new AttributesDescriptor("Functions definition and usage", QuplaSyntaxHighlighter.QUPLA_FCT_DECLARATION),
            new AttributesDescriptor("LUT definition and usage", QuplaSyntaxHighlighter.QUPLA_LUT_DECLARATION),
            new AttributesDescriptor("Local variables", QuplaSyntaxHighlighter.QUPLA_LOCAL_VAR),
            new AttributesDescriptor("Trits", QuplaSyntaxHighlighter.QUPLA_TRIT),
            new AttributesDescriptor("Template definition and usage", QuplaSyntaxHighlighter.QUPLA_TEMPLATE_DECLARATION),
            new AttributesDescriptor("Test assertion prefix", QuplaSyntaxHighlighter.QUPLA_TEST_ASSERTION_PREFIX),
            new AttributesDescriptor("Test assertion", QuplaSyntaxHighlighter.QUPLA_TEST_ASSERTION),
            new AttributesDescriptor("Expression assertion prefix", QuplaSyntaxHighlighter.QUPLA_EXPR_ASSERTION_PREFIX),
            new AttributesDescriptor("Expression assertion", QuplaSyntaxHighlighter.QUPLA_EXPR_ASSERTION),
            new AttributesDescriptor("State variable", QuplaSyntaxHighlighter.QUPLA_STATE_VAR_REFERENCE),
            //new AttributesDescriptor("Environment expression", QuplaSyntaxHighlighter.ENV_EXPR),
            new AttributesDescriptor("Environment keywords", QuplaSyntaxHighlighter.ENV_KEYWORD),
            new AttributesDescriptor("Environment name", QuplaSyntaxHighlighter.ENV_NAME),
            new AttributesDescriptor("Environment attribute value", QuplaSyntaxHighlighter.ENV_VALUE),

    };

    private static final Map<String,TextAttributesKey> ANNOTATOR_MAP = new HashMap();
    @Nullable
    @Override
    public Map<String, ColorKey> getAdditionalHighlightingTagToColorKeyMap() {
        return null;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return QuplaIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new QuplaSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "// This a comment\n" +
                "type <typedef>Trit</typedef> [1]  //size 1\n" +
                "type <typedef>Tryte</typedef> [3]\n" +
                "type <typedef>Trint</typedef> [9]\n" +
                "type <typedef>Int</typedef> [27]\n" +
                "type <typedef>Long</typedef> [81]\n" +
                "type <typedef>Hash</typedef> [243]\n" +
                "type <typedef>State</typedef> [<typeref>Hash</typeref> * 3]\n" +
                "\n" +
                "// Named structured trio vector\n" +
                "type <typedef>Transaction</typedef> {\n" +
                "  <fielddef>signature</fielddef> [27 * <typeref>Hash</typeref>]\n" +
                ", <fielddef>extradatadigest</fielddef> [<typeref>Hash</typeref>]\n" +
                ", <fielddef>address</fielddef> [<typeref>Hash</typeref>]\n" +
                ", <fielddef>value</fielddef> [<typeref>Long</typeref>]\n" +
                ", <fielddef>issuancetimestamp</fielddef> [<typeref>Int</typeref>]\n" +
                ", <fielddef>timelocklowerbound</fielddef> [<typeref>Int</typeref>]\n" +
                ", <fielddef>timelockupperbound</fielddef> [<typeref>Int</typeref>]\n" +
                ", <fielddef>bundle</fielddef> [<typeref>Long</typeref>]\n" +
                ", <fielddef>trunk</fielddef> [<typeref>Hash</typeref>]\n" +
                ", <fielddef>branch</fielddef> [<typeref>Hash</typeref>]\n" +
                ", <fielddef>tag</fielddef> [<typeref>Long</typeref>]\n" +
                ", <fielddef>attachmenttimestamp</fielddef> [<typeref>Int</typeref>]\n" +
                ", <fielddef>attachmenttimestamplowerbound</fielddef> [<typeref>Int</typeref>]\n" +
                ", <fielddef>attachmenttimestampupperbound</fielddef> [<typeref>Int</typeref>]\n" +
                ", <fielddef>nonce</fielddef> [<typeref>Long</typeref>]\n" +
                "}\n" +
                "\n" +
                "\n" +
                "// This a LUT (lookup table)\n" +
                "lut <lutdef>not</lutdef> [\n" +
                "  <trit>0</trit> = <trit>1</trit>\n" +
                "  <trit>1</trit> = <trit>0</trit>\n" +
                "]\n" +
                "//Following line is a unit test\n" +
                "//? not[0]=1\n" +
                "//Following line is an expression to evaluate and print the result on the console\n" +
                "//= not[1]\n" +
                "\n" +
                "func <funcdef>digest</funcdef>(stt [<typeref>State</typeref>]) =\n" +
                "  // single statement function\n" +
                "  <local_var>stt</local_var>, transform(<local_var>state</local_var>, 81)\n" +
                "\n" +
                "// This a function\n" +
                "func <funcdef>nullOrTryte</funcdef>(<local_var>t</local_var> [Bool], <local_var>val</local_var> [<typeref>Tryte</typeref>]) = {\n" +
                "  // concatenate the 3 separate trits via the LUT\n" +
                "  <lutdef>nullOrTrit</lutdef>[<local_var>t</local_var>, <local_var>val</local_var>[0]],\n" +
                "  <lutdef>nullOrTrit</lutdef>[<local_var>t</local_var>, <local_var>val</local_var>[1]],\n" +
                "  <lutdef>nullOrTrit</lutdef>[<local_var>t</local_var>, <local_var>val</local_var>[2]]\n" +
                "}\n" +
                "//this is a template\n" +
                "template <templatedef>addFunc</templatedef><T> <funcdef>add</funcdef><T> (<local_var>lhs</local_var> [T], <local_var>rhs</local_var> [T]) = {\n" +
                "  // use full adder but don't return the carry trit\n" +
                "  sum = <funcdef>fullAdd</funcdef><T>(<local_var>lhs</local_var>, <local_var>rhs</local_var>, 0)\n" +
                "  return sum[1..]\n" +
                "}\n" +
                "\n" +
                "//this is a use statement\n" +
                "use <templatedef>addFunc</templatedef><<typeref>Trytes</typeref>>\n" +
                "\n" +
                "func [<typeref>Transaction</typeref>] leaf (<local_var>param</local_var> [<typeref>TxCmd</typeref>]) = {\n" +
                "  <envexpr><envkeyword>join</envkeyword> <envname>myEnv</envname> <envkeyword>limit</envkeyword> <envvalue>10</envvalue></envexpr>\n" +
                "  <envexpr><envkeyword>affect</envkeyword> <envname>myEnv</envname> <envkeyword>delay</envkeyword> <envvalue>200</envvalue></envexpr>\n" +
                "  state <statevar>data</statevar> [<typeref>Transaction</typeref>]\n" +
                "  <local_var>oldData</local_var> = <statevar>data</statevar>\n" +
                "  <local_var>set = <funcdef>nullOr</funcdef><<typeref>Transaction</typeref>>(<lutdef>equal</lutdef>[<trit>1</trit>, <local_var>param</local_var>.<fielddef>cmd</fielddef>], <local_var>param</local_var>.<fielddef>data</fielddef>)\n" +
                "  <local_var>get = <funcdef>nullOr</funcdef><<typeref>Transaction</typeref>>(<lutdef>equal</lutdef>[<trit>0</trit>, <local_var>param</local_var>.<fielddef>cmd</fielddef>], <statevar>data</statevar>)\n" +
                "  <local_var>del = <funcdef>nullOr</funcdef><<typeref>Transaction</typeref>>(<lutdef>equal</lutdef>[<trit>-</trit>, <local_var>param</local_var>.<fielddef>cmd</fielddef>], 0)\n" +
                "  <statevar>data</statevar> = <local_var>set</local_var> | <local_var>get</local_var> | <local_var>del</local_var>\n" +
                "  return <local_var>oldData</local_var>\n" +
                "}";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        if(ANNOTATOR_MAP.size()==0){
            ANNOTATOR_MAP.put("typedef", QuplaSyntaxHighlighter.QUPLA_TYPE_DECLARATION);
            ANNOTATOR_MAP.put("lutdef", QuplaSyntaxHighlighter.QUPLA_LUT_DECLARATION);
            ANNOTATOR_MAP.put("fielddef", QuplaSyntaxHighlighter.QUPLA_FIELD_DECLARATION);
            ANNOTATOR_MAP.put("funcdef", QuplaSyntaxHighlighter.QUPLA_FCT_DECLARATION);
            ANNOTATOR_MAP.put("local_var", QuplaSyntaxHighlighter.QUPLA_LOCAL_VAR);
            ANNOTATOR_MAP.put("trit", QuplaSyntaxHighlighter.QUPLA_TRIT);
            ANNOTATOR_MAP.put("templatedef", QuplaSyntaxHighlighter.QUPLA_TEMPLATE_DECLARATION);
            ANNOTATOR_MAP.put("typeref", QuplaSyntaxHighlighter.QUPLA_TYPE_REFERENCE);
            ANNOTATOR_MAP.put("statevar", QuplaSyntaxHighlighter.QUPLA_STATE_VAR_REFERENCE);
            ANNOTATOR_MAP.put("envkeyword", QuplaSyntaxHighlighter.ENV_KEYWORD);
            ANNOTATOR_MAP.put("envname", QuplaSyntaxHighlighter.ENV_NAME);
            ANNOTATOR_MAP.put("envexpr", QuplaSyntaxHighlighter.ENV_EXPR);
            ANNOTATOR_MAP.put("envvalue", QuplaSyntaxHighlighter.ENV_VALUE);
        }

        return ANNOTATOR_MAP;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Qupla";
    }
}
