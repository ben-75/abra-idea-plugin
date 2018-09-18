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
            new AttributesDescriptor("Type definition", AbraSyntaxHighlighter.TYPE_DECLARATION),
            new AttributesDescriptor("Field definition", AbraSyntaxHighlighter.FIELD_DECLARATION),
            new AttributesDescriptor("Functions definition and usage", AbraSyntaxHighlighter.FCT_DECLARATION),
            new AttributesDescriptor("LUT definition and usage", AbraSyntaxHighlighter.LUT_DECLARATION),
            new AttributesDescriptor("Local variables", AbraSyntaxHighlighter.LOCAL_VAR),
            new AttributesDescriptor("Trits", AbraSyntaxHighlighter.TRIT),
            new AttributesDescriptor("Template definition and usage", AbraSyntaxHighlighter.TEMPLATE_DECLARATION),
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
                "  0 = 1;\n" +
                "  1 = 0;\n" +
                "];\n" +
                "\n" +
                "\n" +
                "<funcdef>digest</funcdef>(state [State]) =\n" +
                "  // single statement function\n" +
                "  <local_var>state</local_var>, transform(<local_var>state</local_var>, 81);\n" +
                "\n" +
                "// This a function\n" +
                "<funcdef>nullOrTryte</funcdef>(t [Bool], val [Tryte]) = {\n" +
                "  // concatenate the 3 separate trits via the LUT\n" +
                "  nullOrTrit[<local_var>t</local_var>, <local_var>val</local_var>[0]],\n" +
                "  nullOrTrit[<local_var>t</local_var>, <local_var>val</local_var>[1]],\n" +
                "  nullOrTrit[<local_var>t</local_var>, <local_var>val</local_var>[2]];\n" +
                "};\n" +
                "//this is a template\n" +
                "template <funcdef>addFunc</funcdef><T> add<T> (<local_var>lhs</local_var> [T], <local_var>rhs</local_var> [T]) = {\n" +
                "  // use full adder but don't return the carry trit\n" +
                "  sum = fullAdd<T>(<local_var>lhs</local_var>, <local_var>rhs</local_var>, 0);\n" +
                "  return sum[1..];\n" +
                "};";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        if(ANNOTATOR_MAP.size()==0){
            ANNOTATOR_MAP.put("typedef",AbraSyntaxHighlighter.TYPE_DECLARATION);
            ANNOTATOR_MAP.put("lutdef",AbraSyntaxHighlighter.LUT_DECLARATION);
            ANNOTATOR_MAP.put("fielddef",AbraSyntaxHighlighter.FIELD_DECLARATION);
            ANNOTATOR_MAP.put("funcdef",AbraSyntaxHighlighter.FCT_DECLARATION);
            ANNOTATOR_MAP.put("local_var",AbraSyntaxHighlighter.LOCAL_VAR);
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
