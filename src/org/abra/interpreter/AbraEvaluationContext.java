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
        AbraEvaluationContext ec = this;
        while(ec.childContext!=null)ec = ec.childContext;
        TRIT[] resp = ec.localNameMap.get(key.getText());
        while(resp==null && ec.parentContext!=null){
            resp = ec.localNameMap.get(key.getText());
            ec=ec.parentContext;
        }
        return resp==null?ec.localNameMap.get(key.getText()):resp;
//
//        if(childContext==null) return localNameMap.get(key);
//        TRIT[] resp = childContext.get(key);
//        if(resp==null){
//            return getUpper(key);
//        }else{
//            return resp;
//        }
    }

    public TRIT[] getUpper(AbraNamedElement key){
        TRIT[] resp = localNameMap.get(key);
        if(resp==null){
            return parentContext==null?null:parentContext.getUpper(key);
        }else{
            return resp;
        }
    }

    public void add(AbraPlaceHolderName phn, int resolvedSize) {
        typeMap.put(phn.getText(),resolvedSize);
    }

    public Integer getType(AbraPlaceHolderName phn){
        AbraEvaluationContext ec = this;
        while(ec.childContext!=null)ec = ec.childContext;
        Integer resp = ec.typeMap.get(phn.getText());
        while(resp==null && ec.parentContext!=null){
            resp = ec.typeMap.get(phn.getText());
            ec=ec.parentContext;
        }
        return resp==null?ec.typeMap.get(phn.getText()):resp;
//        if(childContext==null) return typeMap.get(phn);
//        Integer resp = childContext.getType(phn);
//        if(resp==null){
//            return getTypeUpper(phn);
//        }else{
//            return resp;
//        }
    }

    public Integer getTypeUpper(AbraPlaceHolderName phn){
        Integer resp = typeMap.get(phn);
        if(resp==null){
            return parentContext==null?null:parentContext.getTypeUpper(phn);
        }else{
            return resp;
        }
    }

}
