package org.qupla.runtime.debugger.action;

import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.impl.actions.ResumeAction;
import org.qupla.runtime.debugger.QuplaDebugSession;

import javax.swing.*;

public class QuplaDebugAction extends ResumeAction {

    protected final QuplaDebugSession session;

    public QuplaDebugAction(QuplaDebugSession session, Icon icon) {
        super();
        this.session = session;
        getTemplatePresentation().setIcon(icon);
    }
}
