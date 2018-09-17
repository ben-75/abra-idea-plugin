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
            new AttributesDescriptor("Field definition", AbraSyntaxHighlighter.TYPE_DECLARATION),
            new AttributesDescriptor("Functions definition and usage", AbraSyntaxHighlighter.FCT_DECLARATION),
            new AttributesDescriptor("LUT definition and usage", AbraSyntaxHighlighter.LUT_DECLARATION),
            new AttributesDescriptor("Local variables", AbraSyntaxHighlighter.LOCAL_VAR),
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
                "<typedef>Trit</typedef> [<sizedef>1</sizedef>];  //size 1\n" +
                "<typedef>Tryte</typedef> [<sizedef>3</sizedef>];\n" +
                "<typedef>Trint</typedef> [<sizedef>9</sizedef>];\n" +
                "<typedef>Int</typedef> [<sizedef>27</sizedef>];\n" +
                "<typedef>Long</typedef> [<sizedef>81</sizedef>];\n" +
                "<typedef>Hash</typedef> [<sizedef>243</sizedef>];\n" +
                "<typedef>State</typedef> [<sizedef>Hash * 3</sizedef>];\n" +
                "\n" +
                "// Named structured trio vector\n" +
                "<typedef>Transaction</typedef> {\n" +
                "  <fielddef>signature</fielddef> [<sizedef>27 * Hash</sizedef>]\n" +
                ", <fielddef>extradatadigest</fielddef> [<sizedef>Hash</sizedef>]\n" +
                ", <fielddef>address</fielddef> [<sizedef>Hash</sizedef>]\n" +
                ", <fielddef>value</fielddef> [<sizedef>Long</sizedef>]\n" +
                ", <fielddef>issuancetimestamp</fielddef> [<sizedef>Int</sizedef>]\n" +
                ", <fielddef>timelocklowerbound</fielddef> [<sizedef>Int</sizedef>]\n" +
                ", <fielddef>timelockupperbound</fielddef> [<sizedef>Int</sizedef>]\n" +
                ", <fielddef>bundle</fielddef> [<sizedef>Long</sizedef>]\n" +
                ", <fielddef>trunk</fielddef> [<sizedef>Hash</sizedef>]\n" +
                ", <fielddef>branch</fielddef> [<sizedef>Hash</sizedef>]\n" +
                ", <fielddef>tag</fielddef> [<sizedef>Long</sizedef>]\n" +
                ", <fielddef>attachmenttimestamp</fielddef> [<sizedef>Int</sizedef>]\n" +
                ", <fielddef>attachmenttimestamplowerbound</fielddef> [<sizedef>Int</sizedef>]\n" +
                ", <fielddef>attachmenttimestampupperbound</fielddef> [<sizedef>Int</sizedef>]\n" +
                ", <fielddef>nonce</fielddef> [<sizedef>Long</sizedef>]\n" +
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
                "<funcdef>digest</funcdef>(state [<sizedef>State</sizedef>]) =\n" +
                "  // single statement function\n" +
                "  <local_var>state</local_var>, transform(<local_var>state</local_var>, 81);\n" +
                "\n" +
                "// This a function\n" +
                "<funcdef>nullOrTryte</funcdef>(t [<sizedef>Bool</sizedef>], val [<sizedef>Tryte</sizedef>]) = {\n" +
                "  // concatenate the 3 separate trits via the LUT\n" +
                "  nullOrTrit[<local_var>t</local_var>, <local_var>val</local_var>[0]],\n" +
                "  nullOrTrit[<local_var>t</local_var>, <local_var>val</local_var>[1]],\n" +
                "  nullOrTrit[<local_var>t</local_var>, <local_var>val</local_var>[2]];\n" +
                "};\n" +
                "//this is a template\n" +
                "template <funcdef>addFunc</funcdef><T> add<T> (<local_var>lhs</local_var> [<sizedef>T</sizedef>], <local_var>rhs</local_var> [<sizedef>T</sizedef>]) = {\n" +
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
            ANNOTATOR_MAP.put("sizedef",AbraSyntaxHighlighter.SIZE_DEF);
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
