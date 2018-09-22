package org.abra.language;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.KEYWORD;

public class AbraColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comments", AbraSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Keywords", KEYWORD),
            new AttributesDescriptor("Type definition", AbraSyntaxHighlighter.ABRA_TYPE_DECLARATION),
            new AttributesDescriptor("Field definition", AbraSyntaxHighlighter.ABRA_FIELD_DECLARATION),
            new AttributesDescriptor("Functions definition and usage", AbraSyntaxHighlighter.ABRA_FCT_DECLARATION),
            new AttributesDescriptor("LUT definition and usage", AbraSyntaxHighlighter.ABRA_LUT_DECLARATION),
            new AttributesDescriptor("Local variables", AbraSyntaxHighlighter.ABRA_LOCAL_VAR),
            new AttributesDescriptor("Trits", AbraSyntaxHighlighter.ABRA_TRIT),
            new AttributesDescriptor("Template definition and usage", AbraSyntaxHighlighter.ABRA_TEMPLATE_DECLARATION),
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
        return AbraIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new AbraSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "// This a comment\n" +
                "<typedef>Trit</typedef> [1];  //size 1\n" +
                "<typedef>Tryte</typedef> [3];\n" +
                "<typedef>Trint</typedef> [9];\n" +
                "<typedef>Int</typedef> [27];\n" +
                "<typedef>Long</typedef> [81];\n" +
                "<typedef>Hash</typedef> [243];\n" +
                "<typedef>State</typedef> [Hash * 3];\n" +
                "\n" +
                "// Named structured trio vector\n" +
                "<typedef>Transaction</typedef> {\n" +
                "  <fielddef>signature</fielddef> [27 * Hash]\n" +
                ", <fielddef>extradatadigest</fielddef> [Hash]\n" +
                ", <fielddef>address</fielddef> [Hash]\n" +
                ", <fielddef>value</fielddef> [Long]\n" +
                ", <fielddef>issuancetimestamp</fielddef> [Int]\n" +
                ", <fielddef>timelocklowerbound</fielddef> [Int]\n" +
                ", <fielddef>timelockupperbound</fielddef> [Int]\n" +
                ", <fielddef>bundle</fielddef> [Long]\n" +
                ", <fielddef>trunk</fielddef> [Hash]\n" +
                ", <fielddef>branch</fielddef> [Hash]\n" +
                ", <fielddef>tag</fielddef> [Long]\n" +
                ", <fielddef>attachmenttimestamp</fielddef> [Int]\n" +
                ", <fielddef>attachmenttimestamplowerbound</fielddef> [Int]\n" +
                ", <fielddef>attachmenttimestampupperbound</fielddef> [Int]\n" +
                ", <fielddef>nonce</fielddef> [Long]\n" +
                "};\n" +
                "\n" +
                "\n" +
                "// This a LUT (lookup table)\n" +
                "<lutdef>not</lutdef> [\n" +
                "  <trit>0</trit> = <trit>1</trit>;\n" +
                "  <trit>1</trit> = <trit>0</trit>;\n" +
                "];\n" +
                "\n" +
                "\n" +
                "<funcdef>digest</funcdef>(state [State]) =\n" +
                "  // single statement function\n" +
                "  <local_var>state</local_var>, transform(<local_var>state</local_var>, 81);\n" +
                "\n" +
                "// This a function\n" +
                "<funcdef>nullOrTryte</funcdef>(<local_var>t</local_var> [Bool], <local_var>val</local_var> [Tryte]) = {\n" +
                "  // concatenate the 3 separate trits via the LUT\n" +
                "  <lutdef>nullOrTrit</lutdef>[<local_var>t</local_var>, <local_var>val</local_var>[0]],\n" +
                "  <lutdef>nullOrTrit</lutdef>[<local_var>t</local_var>, <local_var>val</local_var>[1]],\n" +
                "  <lutdef>nullOrTrit</lutdef>[<local_var>t</local_var>, <local_var>val</local_var>[2]];\n" +
                "};\n" +
                "//this is a template\n" +
                "template <templatedef>addFunc</templatedef><T> <funcdef>add</funcdef><T> (<local_var>lhs</local_var> [T], <local_var>rhs</local_var> [T]) = {\n" +
                "  // use full adder but don't return the carry trit\n" +
                "  sum = <funcdef>fullAdd</funcdef><T>(<local_var>lhs</local_var>, <local_var>rhs</local_var>, 0);\n" +
                "  return sum[1..];\n" +
                "};" +
                "" +
                "//this is a use statement\n" +
                "use <templatedef>addFunc</templatedef><Trytes>;";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        if(ANNOTATOR_MAP.size()==0){
            ANNOTATOR_MAP.put("typedef",AbraSyntaxHighlighter.ABRA_TYPE_DECLARATION);
            ANNOTATOR_MAP.put("lutdef",AbraSyntaxHighlighter.ABRA_LUT_DECLARATION);
            ANNOTATOR_MAP.put("fielddef",AbraSyntaxHighlighter.ABRA_FIELD_DECLARATION);
            ANNOTATOR_MAP.put("funcdef",AbraSyntaxHighlighter.ABRA_FCT_DECLARATION);
            ANNOTATOR_MAP.put("local_var",AbraSyntaxHighlighter.ABRA_LOCAL_VAR);
            ANNOTATOR_MAP.put("trit",AbraSyntaxHighlighter.ABRA_TRIT);
            ANNOTATOR_MAP.put("templatedef",AbraSyntaxHighlighter.ABRA_TEMPLATE_DECLARATION);
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
        return "Abra";
    }
}
