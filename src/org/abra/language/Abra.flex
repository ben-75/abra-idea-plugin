package org.abra.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.abra.language.psi.AbraTypes.*;

%%

%{
  public AbraLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class AbraLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

WHITE_SPACE=\s+
NO_CRLF=(.)+[^\r\n]*
COMMENT=("//")[^\r\n]*
TEST_CMT=("//?")[^\r\n]*
EXPR_CMT=("//=")[^\r\n]*
WHITE_SPACE=(\r|[ \t\n\x0B\f\r]+)+
IMPORT_KEYWORD=(import)
TYPE_KEYWORD=(type)
LUT_KEYWORD=(lut)
FUNC_KEYWORD=(func)
TEMPLATE_KEYWORD=(template)
USE_KEYWORD=(use)
STATE_KEYWORD=(state)
RETURN_KEYWORD=(return)
FILENAME=[A-Za-z0-9_]+
DIGIT=[0-9]
IDENTIFIER=([A-Za-z_])([A-Za-z0-9_])*
SEMICOLON=[;]
COMMA=[,]
AMP=[&]
DOT=[\.]
MERGE_OPERATOR=(\\)
OPEN_BRACKET=(\[)
CLOSE_BRACKET=(\])
OPEN_BRACE=(\{)
CLOSE_BRACE=(\})
OPEN_PAR=(\()
CLOSE_PAR=(\))
OPEN_TAG=(<)
CLOSE_TAG=(>)
RANGE_OPERATOR=(\.\.)
SMART_RANGE_OPERATOR=(\:)
MINUS=\-(\ )*([^,\ \]\)\}=;])
PLUS=(\+)
DIV_TIMES_MODULO=(\/|\*|\%)
ASSIGN = [=]
TRIT_CHAR = (0|1|-)
LUT_START=\[([^\];])+;   //detect start of LUT statement by matching '[' followed by a ';' before the closing ']'
SLASH=(\/)
//  \[([^\];])+\]  // this one is matching NOT_LUT

%state LUT_BODY
%state WAITING_PATH
%state WAITING_TEMPLATE_PARAM
%state TEMPLATE_PARAM
%state TEMPLATE_BODY
%state WAIT_TEST_ASSERT
%state WAIT_EXPR_ASSERT

%%
  {WHITE_SPACE}         { return WHITE_SPACE; }

  "\\r"                 { return CRLF; }
  {TEST_CMT}        { yybegin(WAIT_TEST_ASSERT);zzMarkedPos=zzStartRead+3; return TEST_COMMENT;}
<WAIT_TEST_ASSERT> {NO_CRLF}  { yybegin(YYINITIAL); return TEST_ASSERTION;}
  {EXPR_CMT}        { yybegin(WAIT_EXPR_ASSERT);zzMarkedPos=zzStartRead+3; return EXPR_COMMENT;}
<WAIT_EXPR_ASSERT> {NO_CRLF}  { yybegin(YYINITIAL); return EXPR_ASSERTION;}
  {COMMENT}             { return COMMENT; }
  {WHITE_SPACE}         { return WHITE_SPACE; }
  {IMPORT_KEYWORD}      { yybegin(WAITING_PATH); return IMPORT_KEYWORD; }
  {TYPE_KEYWORD}        { return TYPE_KEYWORD; }
  {LUT_KEYWORD}         { return LUT_KEYWORD; }
  {FUNC_KEYWORD}        { return FUNC_KEYWORD; }
  {TEMPLATE_KEYWORD}    { return TEMPLATE_KEYWORD; }
  {USE_KEYWORD}         { return USE_KEYWORD; }
  {RETURN_KEYWORD}      { return RETURN_KEYWORD; }
  {STATE_KEYWORD}       { return STATE_KEYWORD; }
  {TEMPLATE_KEYWORD}    { return TEMPLATE_KEYWORD; }

<WAITING_PATH> {SLASH}        { return SLASH; }
<WAITING_PATH> {FILENAME}     { return FILENAME; }  //filename can start by a digit (i.e. less restrictive than an IDENTIFIER)
<WAITING_PATH> {SEMICOLON}    { yybegin(YYINITIAL); return SEMICOLON; }

<LUT_BODY>  {TRIT_CHAR}       { return TRIT_CHAR; }
<LUT_BODY>  {CLOSE_BRACKET}   { yybegin(YYINITIAL); return CLOSE_BRACKET; }

  {DIGIT}               { return DIGIT; }
  {PLUS}                { return PLUS_OR_MINUS; }
  {MINUS}               { zzMarkedPos=zzStartRead+1; return PLUS_OR_MINUS; }
  {TRIT_CHAR}           { return TRIT_CHAR; }
  {IDENTIFIER}          { return IDENTIFIER; }
  {SEMICOLON}           { return SEMICOLON; }
  {COMMA}               { return COMMA; }
  {AMP}                 { return AMP; }
  {ASSIGN}              { return ASSIGN; }
  {MERGE_OPERATOR}      { return MERGE_OPERATOR; }

  //when matching LUT_START, we need to go backward
  //this tricky rule allow us to make the distinction between LutStmt and TypeStmt
  //additionnaly : we change the state to LUT_BODY to ensure that any incomming 0 or 1 will be seen as a trit.
  {LUT_START}           { yybegin(LUT_BODY);zzMarkedPos=zzStartRead+1;return OPEN_BRACKET; }
  {OPEN_BRACKET}        { return OPEN_BRACKET; }
  {CLOSE_BRACKET}       { return CLOSE_BRACKET; }
  {OPEN_BRACE}          { return OPEN_BRACE; }
  {CLOSE_BRACE}         { yybegin(YYINITIAL); return CLOSE_BRACE; }
  {OPEN_PAR}            { return OPEN_PAR; }
  {CLOSE_PAR}           { return CLOSE_PAR; }
  {RANGE_OPERATOR}      { return RANGE_OPERATOR; }
  {SMART_RANGE_OPERATOR}      { return SMART_RANGE_OPERATOR; }
  {DIV_TIMES_MODULO}    { return DIV_TIMES_MODULO; }
  {DOT}                 { return DOT; }
  {OPEN_TAG}            { return OPEN_TAG; }
  {CLOSE_TAG}           { return CLOSE_TAG; }



[^] { return BAD_CHARACTER; }
