package org.abra.language;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.abra.language.AbraSyntaxHighlighter;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DeclarationAnnotator  implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof AbraFuncName) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.FCT_DECLARATION);
        } else if (element instanceof AbraTypeName) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            if(element.getParent().getParent().getParent() instanceof AbraFieldName){
                annotation.setTextAttributes(AbraSyntaxHighlighter.FIELD_DECLARATION);
            }else {
                annotation.setTextAttributes(AbraSyntaxHighlighter.TYPE_DECLARATION);
            }
        } else if (element instanceof AbraLutName) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.LUT_DECLARATION);
        }
    }

    private static char[] T={'-','0','1'};
    public static void main(String[] args){
        OrderedString[] result = new OrderedString[729];
        int i = 0;
        for(int a=-1;a<2;a++){
            for(int b=-1;b<2;b++){
                for(int c=-1;c<2;c++){
                    for(int d=-1;d<2;d++){
                        for(int e=-1;e<2;e++){
                            for(int f=-1;f<2;f++){
                                i++;
                                int r = (a*1+b*3+c*9) * (d*1+e*3+f*9);
                                String s = ("    "+T[a+1]+","+T[b+1]+","+T[c+1]+","+T[d+1]+","+T[e+1]+","+T[f+1] +" = "+intToTrit(r)+";  \t// "+(a*1+b*3+c*9)+" * "+(d*1+e*3+f*9)+" = "+r);
                                result[i-1] = new OrderedString((a*1+b*3+c*9), (d*1+e*3+f*9), s);
                            }
                        }
                    }
                }
            }
        }
        Arrays.sort(result);
        for(int j=0;j<729;j++){
            System.out.println(result[j].s);
        }
    }

    private static class OrderedString implements Comparable<OrderedString> {
        int f1,f2;
        String s;

        public OrderedString(int f1, int f2, String s){
            this.f1 = f1;
            this.f2 = f2;
            this.s = s;
        }

        @Override
        public int compareTo(OrderedString o) {
            // usually toString should not be used,
            // instead one of the attributes or more in a comparator chain
            return f1==o.f1?f2-o.f2:f1-o.f1;
        }
    }
    private static String intToTrit(int v){
        if(v==0)return "0,0,0,0,0,0";
        for(int a=-1;a<2;a++){
            for(int b=-1;b<2;b++){
                for(int c=-1;c<2;c++){
                    for(int d=-1;d<2;d++){
                        for(int e=-1;e<2;e++){
                            for(int f=-1;f<2;f++){
                                int r = (a*1+b*3+c*9+d*27+e*81+f*243);
                                if(r==v)return T[a+1]+","+T[b+1]+","+T[c+1]+","+T[d+1]+","+T[e+1]+","+T[f+1];
                            }
                        }
                    }
                }
            }
        }
        return "error";
    }
}