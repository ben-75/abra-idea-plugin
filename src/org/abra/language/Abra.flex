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
CRLF=[\r\n]+
TEST_CMT=("//?")[^\r\n]*
EXPR_CMT=("//=")[^\r\n]*
COMMENT=("//")[^\r\n]*
WHITE_SPACE=(\r|[ \t\n\x0B\f\r]+)+

ZERO=(0)
ONE=(1)
MINUS=(-)
PLUS=(\+)
SLASH=("/")
TIMES=(\*)
MODULO=(%)
AMP=(&)
DOT=(\.)
MERGE_OPERATOR=(\|)
COMMA=(,)
IMPORT_KEYWORD=(import)
TYPE_KEYWORD=(type)
LUT_KEYWORD=(lut)
FUNC_KEYWORD=(func)
TEMPLATE_KEYWORD=(template)
USE_KEYWORD=(use)
STATE_KEYWORD=(state)
RETURN_KEYWORD=(return)
AFFECT_KEYWORD=(affect)
DELAY_KEYWORD=(delay)
IDENTIFIER=([A-Za-z_])([A-Za-z0-9_])*
OPEN_BRACKET=(\[)
CLOSE_BRACKET=(\])
OPEN_BRACE=(\{)
CLOSE_BRACE=(\})
OPEN_PAR=(\()
CLOSE_PAR=(\))
OPEN_TAG=(<)
CLOSE_TAG=(>)
DIGIT=[2-9]
RANGE_OPERATOR=(\.\.)
SMART_RANGE_OPERATOR=(:)
ASSIGN=[=]

%state WAIT_TEST_ASSERT
%state WAIT_EXPR_ASSERT

%%
  {WHITE_SPACE}               { return WHITE_SPACE; }
  {CRLF}                      { return CRLF; }

  {TEST_CMT}        { yybegin(WAIT_TEST_ASSERT);zzMarkedPos=zzStartRead+3; return TEST_COMMENT;}
<WAIT_TEST_ASSERT> {NO_CRLF}  { yybegin(YYINITIAL); return TEST_ASSERTION;}
  {EXPR_CMT}        { yybegin(WAIT_EXPR_ASSERT);zzMarkedPos=zzStartRead+3; return EXPR_COMMENT;}
<WAIT_EXPR_ASSERT> {NO_CRLF}  { yybegin(YYINITIAL); return EXPR_ASSERTION;}
  {COMMENT}             { return COMMENT; }

  {OPEN_BRACE}                { return OPEN_BRACE; }

  {ZERO}                      { return ZERO; }
  {ONE}                       { return ONE; }
  {MINUS}                     { return MINUS; }
  {PLUS}                      { return PLUS; }
  {TIMES}                     { return TIMES; }
  {MODULO}                    { return MODULO; }
  {AMP}                       { return AMP; }
  {DOT}                       { return DOT; }
  {COMMENT}                   { return COMMENT; }
  {WHITE_SPACE}               { return WHITE_SPACE; }
  {MERGE_OPERATOR}            { return MERGE_OPERATOR; }
  {COMMA}                     { return COMMA; }
  {IMPORT_KEYWORD}            { return IMPORT_KEYWORD; }
  {TYPE_KEYWORD}              { return TYPE_KEYWORD; }
  {LUT_KEYWORD}               { return LUT_KEYWORD; }
  {FUNC_KEYWORD}              { return FUNC_KEYWORD; }
  {TEMPLATE_KEYWORD}          { return TEMPLATE_KEYWORD; }
  {USE_KEYWORD}               { return USE_KEYWORD; }
  {STATE_KEYWORD}             { return STATE_KEYWORD; }
  {RETURN_KEYWORD}            { return RETURN_KEYWORD; }
  {AFFECT_KEYWORD}            { return AFFECT_KEYWORD; }
  {DELAY_KEYWORD}             { return DELAY_KEYWORD; }
  {IDENTIFIER}                { return IDENTIFIER; }
  {OPEN_BRACKET}              { return OPEN_BRACKET; }
  {CLOSE_BRACKET}             { return CLOSE_BRACKET; }
  {CLOSE_BRACE}               { return CLOSE_BRACE; }
  {OPEN_PAR}                  { return OPEN_PAR; }
  {CLOSE_PAR}                 { return CLOSE_PAR; }
  {OPEN_TAG}                  { return OPEN_TAG; }
  {CLOSE_TAG}                 { return CLOSE_TAG; }
  {DIGIT}                     { return DIGIT; }
  {RANGE_OPERATOR}            { return RANGE_OPERATOR; }
  {SMART_RANGE_OPERATOR}      { return SMART_RANGE_OPERATOR; }
  {ASSIGN}                    { return ASSIGN; }
  {SLASH}                     { return SLASH; }


[^] { return BAD_CHARACTER; }
