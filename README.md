
# Abra plugin  
Intellij plugin for the Qupla language.  
  
The Qupla language is not completely defined yet, so consider this plugin as highly experimental.  
The plugin should be compatible with any IntelliJ based platform (minimal build : 171.0)  
  
  ## Getting started
 
 - If you don't already an IDE based on IntelliJ platform installed on your computer, you can download and install 
 IntelliJ Community from [Jetbrains website](https://www.jetbrains.com/idea/download/)
 - Start IntelliJ (or any other Jetbrains IDE) and install the Abra Plugin ([details here](#installation))
 - After plugin installation it is recommended to restart IntelliJ
 - Create a new "Empty project" and copy the `AbraLib/` and `Examples/` at the root of the project (todo : add Link)
 - Start writing Qupla code
 
  ![Qupla Plugin in Action](https://github.com/ben-75/qupla-idea-plugin/blob/interpreter/doc/ready.png?raw=true)
  
  ## Installation
  
  The Qupla plugin can be installed either from Jetbrains plugin repository, either manually, or it can be build from sources.
  
  ### Install from plugins repository
  
 - Open Settings from the menu File (Ctrl+Alt+S)
 - Select "Plugins" on the left pane
 - Search "Qupla" in the search box
 - Click "Search in repositories" and install `Qupla Plugin`
 - Restart IntelliJ
  
  ### Manual Install    

 - Copy the [latest release](https://github.com/ben-75/qupla-idea-plugin/releases) 
 in &lt;INTELLIJ_INSTALL&gt;/plugins/qupla-language/lib/ 
 - Restart IntelliJ  

  ### Building from sources  
  
Building the plugin from sources requires to setup an intellij-plugin-development-environment suitable for custom 
language plugin development. Instructions can be found 
[here](http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/prerequisites.html)  
  
Once your environment is ready, clone this repository, open the project and generate the parser and the lexer:  
  
 - To generate the parser : right-click on the file Abra.bnf and select "Generate Parser Code"  
 - To generate the lexer : right-click on the file Abra.flex and select "Run JFlex Generator"  
  
Now you should have a `gen/` directory at the root of the project with all the generated code.  
  
Make sure that the `gen/` directory is in the source path of your project   
(Open "Project Structure > Modules > abra-language-plugin > tab "Sources")  
  
You can now run the plugin. If you plan to modify the bnf or the lexer: it can be useful to enable the plugin 
PsiViewer in the instance of IntelliJ running the plugin (If you followed carefully the instructions to 
setup your environment the PsiViewer is already enabled in your main instance of IntelliJ).
