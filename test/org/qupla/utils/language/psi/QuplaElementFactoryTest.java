package org.qupla.utils.language.psi;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.qupla.language.psi.*;
import org.junit.Assert;

public class QuplaElementFactoryTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testCreateQuplaTypeName(){
        QuplaTypeName ref = QuplaElementFactory.createQuplaTypeName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateQuplaFieldName(){
        QuplaFieldName ref = QuplaElementFactory.createQuplaFieldName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateQuplaFuncName(){
        QuplaFuncName ref = QuplaElementFactory.createQuplaFuncName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateQuplaParamName(){
        QuplaParamName ref = QuplaElementFactory.createQuplaParamName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }
    public void testCreateQuplaVarName(){
        QuplaVarName ref = QuplaElementFactory.createQuplaVarName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateQuplaLutName(){
        QuplaLutName ref = QuplaElementFactory.createQuplaLutName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateQuplaTemplateName(){
        QuplaTemplateName ref = QuplaElementFactory.createQuplaTemplateName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateQuplaPlaceHolderName(){
        QuplaPlaceHolderTypeName ref = QuplaElementFactory.createQuplaPlaceHolderName(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }


    public void testCreateQuplaFunctionReference(){
        QuplaFuncNameRef ref = QuplaElementFactory.createQuplaFunctionReference(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateQuplaTypeNameRef(){
        QuplaTypeNameRef ref = QuplaElementFactory.createQuplaTypeNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateQuplaVarOrParamNameRef(){
        QuplaParamOrVarNameRef ref = QuplaElementFactory.createQuplaVarOrParamNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateQuplaLutOrParamOrVarRef(){
        QuplaLutOrParamOrVarNameRef ref = QuplaElementFactory.createQuplaLutOrParamOrVarRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateQuplaTemplateNameRef(){
        QuplaTemplateNameRef ref = QuplaElementFactory.createQuplaTemplateNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateQuplaTypeOrPlaceHolderNameRef(){
        QuplaTypeOrPlaceHolderNameRef ref = QuplaElementFactory.createQuplaTypeOrPlaceHolderNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }

    public void testCreateQuplaLutNameRef(){
        QuplaLutNameRef ref = QuplaElementFactory.createQuplaLutNameRef(getProject(),"elem");
        Assert.assertEquals("elem",ref.getText());
    }


}
