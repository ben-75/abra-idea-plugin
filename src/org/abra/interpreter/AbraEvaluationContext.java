package org.abra.interpreter;

import org.abra.language.psi.AbraNamedElement;
import org.abra.language.psi.AbraParamName;
import org.abra.language.psi.AbraPlaceHolderName;
import org.abra.utils.TRIT;

import java.util.HashMap;
import java.util.Map;

public class AbraEvaluationContext {

    private Map<String,TRIT[]> localNameMap = new HashMap<>();
    private Map<String,Integer> typeMap = new HashMap<>();
    private AbraEvaluationContext childContext;
    private AbraEvaluationContext parentContext;

    public void pushChildContext(AbraEvaluationContext childContext){
        if(this.childContext==null){
            childContext.parentContext = this;
            this.childContext = childContext;
        }else{
            this.childContext.pushChildContext(childContext);
        }
    }

    public void add(AbraNamedElement paramName, TRIT[] value){
        localNameMap.put(paramName.getText(),value);
    }

    public void popContext(){
        if(childContext!=null) {
            childContext.parentContext = null;
            childContext = null;
        }
    }

    public TRIT[] get(AbraNamedElement key){
        TRIT[] resp = localNameMap.get(key.getText());
        if(resp==null && parentContext!=null){
            return localNameMap.get(key.getText());
        }
        return resp;
    }

    public void add(AbraPlaceHolderName phn, int resolvedSize) {
        typeMap.put(phn.getText(),resolvedSize);
    }

    public Integer getType(AbraPlaceHolderName phn){
        Integer resp = typeMap.get(phn.getText());
        if(resp==null && parentContext!=null){
            return parentContext.getType(phn);
        }
        return resp;
    }

}
