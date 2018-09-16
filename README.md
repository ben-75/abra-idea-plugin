# abra-idea-plugin
Intellij plugin for the Abra language.

The Abra language is not completely defined yet, so consider this plugin as highly experimental.

The plugin should be compatible with any Intellij based platform. (minimal build : 171.0)


<H3>Manual Install</H3>
<ul>
<li>Copy the latest release in &lt;IDEA_INSTALL&gt;/plugins/abra-language/lib/
<li>Restart IDEA
</ul>
<H3>Building from sources</H3>

Building the plugin from sources requires to setup an intellij-plugin-development-environment suitable 
for custom language plugin.
Instructions can be found here : http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/prerequisites.html

Once your environment is ready, you can open the project and generate the parser and the lexer.

<li>To generate the parser : right-click on the file Abra.bnf and select "Generate Parser Code"
<li>To generate the lexer : right-click on the file Abra.flex and select "Run JFlex Generator"

Now you should have a gen/ directory at the root of the project with all the generated code.

Ensure that the gen/ directory is in the source path of your project 
(Open "Project Structure > Modules > abra-language-plugin > tab "Sources")

You can now run the plugin. If you plan to modify the bnf or the lexer: it can be usefull to enable the plugin 
PsiViewer in the instance of IntelliJ running the plugin (If you followed carrefully the instructions to 
setup your environment the PsiViewer is already enabled in your main instance of IntelliJ).