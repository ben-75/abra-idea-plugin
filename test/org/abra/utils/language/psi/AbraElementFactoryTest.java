package org.abra.utils.language.psi;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.abra.language.psi.*;
import org.junit.Assert;

public class AbraElementFactoryTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testCreateAbraTypeName(){
        AbraTypeName ref = AbraElementFactory.createAbraTypeName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateAbraFieldName(){
        AbraFieldName ref = AbraElementFactory.createAbraFieldName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateAbraFuncName(){
        AbraFuncName ref = AbraElementFactory.createAbraFuncName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateAbraParamName(){
        AbraParamName ref = AbraElementFactory.createAbraParamName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateAbraVarName(){
        AbraVarName ref = AbraElementFactory.createAbraVarName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraLutName(){
        AbraLutName ref = AbraElementFactory.createAbraLutName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraTemplateName(){
        AbraTemplateName ref = AbraElementFactory.createAbraTemplateName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraPlaceHolderName(){
        AbraPlaceHolderTypeName ref = AbraElementFactory.createAbraPlaceHolderName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }


    public void testCreateAbraFunctionReference(){
        AbraFuncNameRef ref = AbraElementFactory.createAbraFunctionReference(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraTypeNameRef(){
        AbraTypeNameRef ref = AbraElementFactory.createAbraTypeNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraVarOrParamNameRef(){
        AbraParamOrVarNameRef ref = AbraElementFactory.createAbraVarOrParamNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraLutOrParamOrVarRef(){
        AbraLutOrParamOrVarNameRef ref = AbraElementFactory.createAbraLutOrParamOrVarRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraTemplateNameRef(){
        AbraTemplateNameRef ref = AbraElementFactory.createAbraTemplateNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraTypeOrPlaceHolderNameRef(){
        AbraTypeOrPlaceHolderNameRef ref = AbraElementFactory.createAbraTypeOrPlaceHolderNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraLutNameRef(){
        AbraLutNameRef ref = AbraElementFactory.createAbraLutNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }


}
