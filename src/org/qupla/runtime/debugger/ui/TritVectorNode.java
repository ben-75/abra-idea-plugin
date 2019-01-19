package org.qupla.runtime.debugger.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class TritVectorNode  extends DefaultMutableTreeNode {

    private final TreeNode parent;
    private final TritVectorView tritVector;
    public TritVectorNode(TreeNode parent, TritVectorView tritVector) {
        this.parent = parent;
        this.tritVector = tritVector;
    }

    @Override
    public String toString() {
        return "<html><font color=#43CC84>["+tritVector.size+"]</font> "+tritVector.name+" = "+tritVector.displayTrits()+" ("+ tritVector.displayDecimals()+")";
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public int getIndex(TreeNode node) {
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration children() {
        return null;
    }
}
