package org.qupla.runtime.debugger.ui;

import com.intellij.debugger.engine.DebugProcess;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.qupla.runtime.debugger.QuplaDebugSession;
import org.qupla.runtime.interpreter.QuplaInterpreterRunConfiguration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class QuplaDebuggerManagerImpl implements QuplaDebuggerManager {

    private final Project myProject;
    private final Map<ProcessHandler, QuplaDebugSession> mySessions = Collections.synchronizedMap(new LinkedHashMap<>());

    public QuplaDebuggerManagerImpl(Project myProject) {
        this.myProject = myProject;
    }

    public QuplaDebugSession createSession(DebugProcess debugProcess, QuplaInterpreterRunConfiguration runConfiguration){
        QuplaDebugSession session = new QuplaDebugSession(debugProcess, runConfiguration, runConfiguration.getContextClassName());
        mySessions.put(debugProcess.getProcessHandler(),session);


        ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(QuplaDebuggerToolWindow.ID);
        if(toolWindow==null){
            toolWindow = ToolWindowManager.getInstance(myProject).registerToolWindow(QuplaDebuggerToolWindow.ID, true, ToolWindowAnchor.BOTTOM);
        }
        ContentManager contentManager = toolWindow.getContentManager();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        QuplaDebuggerToolWindow quplaDebuggerToolWindow = new QuplaDebuggerToolWindow(myProject, session);
        Content content = contentFactory.createContent(quplaDebuggerToolWindow.getContent(), session.getRunConfiguration().getName(), false);
        contentManager.addContent(content,0);
        contentManager.setSelectedContent(content);
        Content deadContent = contentManager.findContent(session.getRunConfiguration().getName()+" (DEAD)");
        if(deadContent!=null){
            contentManager.removeContent(deadContent,true);
        }

        session.setContent(content);
        session.setQuplaDebuggerToolWindow(quplaDebuggerToolWindow);
        return session;
    }

    public void forgetSession(ProcessHandler processHandler){
        mySessions.remove(processHandler);
    }

    public QuplaDebugSession getSession(ProcessHandler processHandler){
        return mySessions.get(processHandler);
    }

    public QuplaDebugSession getSession(DebugProcess debugProcess){
        return mySessions.get(debugProcess.getProcessHandler());
    }

}
