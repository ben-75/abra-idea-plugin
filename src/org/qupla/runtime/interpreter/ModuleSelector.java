package org.qupla.runtime.interpreter;


import com.intellij.openapi.project.Project;
import javafx.scene.control.CheckBox;
import org.qupla.language.module.QuplaModule;
import org.qupla.language.module.QuplaModuleManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ModuleSelector extends JPanel {

    private Project project;
    private QuplaModuleManager moduleManager;
    private ArrayList<QuplaModule> selectedModules = new ArrayList<>();
    private ArrayList<Listener> listeners = new ArrayList<>();

    public ModuleSelector() {
        super(new GridLayout(0,4));

    }

    public void setProject(Project project) {
        this.project = project;
        moduleManager = project.getComponent(QuplaModuleManager.class);
        for(QuplaModule module:moduleManager.allModules()){
            addModule(module.getName());
        }
    }

    public List<QuplaModule> getSelectedModules(){
        return selectedModules;
    }

    private void addModule(String moduleName){
        final JCheckBox cb = new JCheckBox(moduleName);
        cb.setName(moduleName);
        cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cb.isSelected()){
                    selectedModules.add(moduleManager.getModule(cb.getName()));
                }else{
                    selectedModules.remove(moduleManager.getModule(cb.getName()));
                }
                updateUIWithSelectedModules();
            }
        });
        add(cb);
    }

    public void applySelectedModules(List<QuplaModule> modules){
        selectedModules.clear();
        for(QuplaModule module:modules){
            QuplaModule module2 = moduleManager.getModule(module.getName());
            if(module2!=null){
                selectedModules.add(module2);
            }
        }
        updateUIWithSelectedModules();
    }

    private void updateUIWithSelectedModules(){
        for(Component cb:getComponents()){
            ((JCheckBox)cb).setSelected(false);
            ((JCheckBox)cb).setEnabled(true);
        }

        for(QuplaModule m:selectedModules){
            JCheckBox cb = findCheckBox(m.getName());
            if(cb!=null){
                if(!cb.isSelected()) {
                    cb.setSelected(true);
                    //select and disable all imported modules
                    List<QuplaModule> importedModules = moduleManager.getImportedModules(moduleManager.getModule(m.getName()));
                    for (QuplaModule quplaModule : importedModules) {
                        JCheckBox m_cb = findCheckBox(quplaModule.getName());
                        m_cb.setSelected(true);
                        m_cb.setEnabled(false);
                    }
                }else{
                    selectedModules.remove(moduleManager.getModule(cb.getName()));
                }
            }
        }
        fireChange();
    }
    private JCheckBox findCheckBox(String name){
        for(Component cb:getComponents()){
            if(name.equals(((JCheckBox)cb).getName())){
                return (JCheckBox) cb;
            }
        }
        return null;
    }

    public void fireChange(){
        for(Listener l:listeners)l.onSelectionChanged(selectedModules);
    }
    public void addListener(Listener l){
        if(!listeners.contains(l))listeners.add(l);
    }
    public void removeListener(Listener l){
        listeners.remove(l);
    }

    public void clearListeners() {
        listeners.clear();
    }

    interface Listener {
        void onSelectionChanged(List<QuplaModule> selectedModules);
    }
}
