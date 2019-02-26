package org.qupla.runtime.debugger.requestor;

import com.intellij.debugger.InstanceFilter;
import com.intellij.debugger.settings.DebuggerSettings;
import com.intellij.debugger.ui.breakpoints.FilteredRequestor;
import com.intellij.ui.classFilter.ClassFilter;
import org.qupla.runtime.debugger.QuplaDebugSession;
import org.qupla.runtime.debugger.QuplaPositionManager;

public abstract class QuplaRequestor implements FilteredRequestor {

    protected QuplaDebugSession session;

    protected QuplaRequestor(QuplaDebugSession session){
        this.session = session;
    }

    @Override
    public boolean isInstanceFiltersEnabled() {
        return false;
    }

    @Override
    public InstanceFilter[] getInstanceFilters() {
        return new InstanceFilter[0];
    }

    @Override
    public boolean isCountFilterEnabled() {
        return false;
    }

    @Override
    public int getCountFilter() {
        return 0;
    }

    @Override
    public boolean isClassFiltersEnabled() {
        return true;
    }

    @Override
    public ClassFilter[] getClassFilters() {
        return new ClassFilter[]{new ClassFilter(QuplaPositionManager.QUPLA_CONTEXT_CLASSNAME)};
    }

    @Override
    public ClassFilter[] getClassExclusionFilters() {
        return new ClassFilter[0];
    }

    @Override
    public String getSuspendPolicy() {
        return DebuggerSettings.SUSPEND_ALL;
    }

    @Override
    public boolean isConditionEnabled() {
        return false;
    }
}
