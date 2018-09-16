package org.abra.language;

import com.intellij.psi.tree.IElementType;

import java.util.Stack;

public class AbraContextualLexer {

    public IElementType lastIdCategory = null;

    public Stack<String> contextStack = new Stack();

    public boolean isStackEmpty(){
        return contextStack.isEmpty();
    }
}
