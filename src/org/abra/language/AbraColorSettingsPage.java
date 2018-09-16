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

public class AbraColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comments", AbraSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Type definition", AbraSyntaxHighlighter.TYPE_DECLARATION),
            new AttributesDescriptor("Field definition", AbraSyntaxHighlighter.FIELD_DECLARATION),
            new AttributesDescriptor("Functions definition", AbraSyntaxHighlighter.FCT_DECLARATION),
            new AttributesDescriptor("LUT definition", AbraSyntaxHighlighter.LUT_DECLARATION),
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
                "  state, transform(state, 81);\n" +
                "\n" +
                "// This a function\n" +
                "<funcdef>nullOrTryte</funcdef>(t [Bool], val [Tryte]) = {\n" +
                "  // concatenate the 3 separate trits via the LUT\n" +
                "  nullOrTrit[t, val[0]],\n" +
                "  nullOrTrit[t, val[1]],\n" +
                "  nullOrTrit[t, val[2]];\n" +
                "};\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        if(ANNOTATOR_MAP.size()==0){
            ANNOTATOR_MAP.put("typedef",AbraSyntaxHighlighter.TYPE_DECLARATION);
            ANNOTATOR_MAP.put("lutdef",AbraSyntaxHighlighter.LUT_DECLARATION);
            ANNOTATOR_MAP.put("fielddef",AbraSyntaxHighlighter.FIELD_DECLARATION);
            ANNOTATOR_MAP.put("funcdef",AbraSyntaxHighlighter.FCT_DECLARATION);
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
