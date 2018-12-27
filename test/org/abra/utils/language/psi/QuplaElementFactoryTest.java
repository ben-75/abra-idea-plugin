package org.abra.utils.language.psi;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.abra.language.psi.*;
import org.junit.Assert;

public class QuplaElementFactoryTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testCreateAbraTypeName(){
        QuplaTypeName ref = QuplaElementFactory.createAbraTypeName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateAbraFieldName(){
        QuplaFieldName ref = QuplaElementFactory.createAbraFieldName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateAbraFuncName(){
        QuplaFuncName ref = QuplaElementFactory.createAbraFuncName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateAbraParamName(){
        QuplaParamName ref = QuplaElementFactory.createAbraParamName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateAbraVarName(){
        QuplaVarName ref = QuplaElementFactory.createAbraVarName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraLutName(){
        QuplaLutName ref = QuplaElementFactory.createAbraLutName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraTemplateName(){
        QuplaTemplateName ref = QuplaElementFactory.createAbraTemplateName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraPlaceHolderName(){
        QuplaPlaceHolderTypeName ref = QuplaElementFactory.createAbraPlaceHolderName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }


    public void testCreateAbraFunctionReference(){
        QuplaFuncNameRef ref = QuplaElementFactory.createAbraFunctionReference(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraTypeNameRef(){
        QuplaTypeNameRef ref = QuplaElementFactory.createAbraTypeNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraVarOrParamNameRef(){
        QuplaParamOrVarNameRef ref = QuplaElementFactory.createAbraVarOrParamNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraLutOrParamOrVarRef(){
        QuplaLutOrParamOrVarNameRef ref = QuplaElementFactory.createAbraLutOrParamOrVarRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraTemplateNameRef(){
        QuplaTemplateNameRef ref = QuplaElementFactory.createAbraTemplateNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraTypeOrPlaceHolderNameRef(){
        QuplaTypeOrPlaceHolderNameRef ref = QuplaElementFactory.createAbraTypeOrPlaceHolderNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateAbraLutNameRef(){
        QuplaLutNameRef ref = QuplaElementFactory.createAbraLutNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }


}
