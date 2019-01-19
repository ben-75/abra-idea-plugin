package org.qupla.runtime.debugger.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class VariablesNode extends DefaultMutableTreeNode {

    private ArrayList<TritVectorNode> children;

    public VariablesNode() {
        this.children = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Variables";
    }

}
