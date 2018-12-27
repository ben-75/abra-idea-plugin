package org.abra.language;

import com.intellij.psi.tree.IElementType;

import java.util.Stack;

public class QuplaContextualLexer {

    public IElementType lastIdCategory = null;

    public final Stack<String> contextStack = new Stack();

    public boolean isStackEmpty(){
        return contextStack.isEmpty();
    }
}
