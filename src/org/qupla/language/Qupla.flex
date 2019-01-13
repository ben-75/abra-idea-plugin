package org.qupla.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.qupla.language.psi.QuplaTypes.*;

%%

%{
  public QuplaLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class QuplaLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

WHITE_SPACE=\s+
COMMENT=("//")[^\r\n]*
WHITE_SPACE=(\r|[ \t\n\x0B\f\r]+)+

MINUS=(-)
PLUS=(\+)
SLASH=("/")
TIMES=(\*)
MODULO=(%)
AMP=(&)
AT=(@)
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
JOIN_KEYWORD=(join)
LIMIT_KEYWORD=(limit)
AFFECT_KEYWORD=(affect)
DELAY_KEYWORD=(delay)
TEST_KEYWORD=(test)
EVAL_KEYWORD=(eval)
NULL_KEYWORD=(null)
IDENTIFIER=([A-Za-z_])([A-Za-z0-9_])*
OPEN_BRACKET=(\[)
CLOSE_BRACKET=(\])
OPEN_BRACE=(\{)
CLOSE_BRACE=(\})
OPEN_PAR=(\()
CLOSE_PAR=(\))
OPEN_TAG=(<)
CLOSE_TAG=(>)
DIGIT=[0-9]
DIGITS=[0-9]+
TRIT=[-01]
RANGE_OPERATOR=(\.\.)
SMART_RANGE_OPERATOR=(:)
ASSIGN=[=]
QUESTION_MARK=(\?)

%state LUT_BODY

%%
  {WHITE_SPACE}               { return WHITE_SPACE; }

  {COMMENT}                   { return COMMENT; }

  {OPEN_BRACE}                { return OPEN_BRACE; }
  {OPEN_BRACKET}              { return OPEN_BRACKET; }

<LUT_BODY> {TRIT}             { return TRIT; }
<LUT_BODY> {CLOSE_BRACE}      { yybegin(YYINITIAL); return CLOSE_BRACE; }
//  {ZERO}                      { return ZERO; }
//  {ONE}                       { return ONE; }
  {MINUS}                     { return MINUS; }
  {PLUS}                      { return PLUS; }
  {TIMES}                     { return TIMES; }
  {MODULO}                    { return MODULO; }
  {AMP}                       { return AMP; }
  {AT}                        { yybegin(YYINITIAL); return AT; }
  {DOT}                       { return DOT; }
  {COMMENT}                   { return COMMENT; }
  {WHITE_SPACE}               { return WHITE_SPACE; }
  {MERGE_OPERATOR}            { return MERGE_OPERATOR; }
  {COMMA}                     { return COMMA; }
  {IMPORT_KEYWORD}            { return IMPORT_KEYWORD; }
  {TYPE_KEYWORD}              { return TYPE_KEYWORD; }
  {LUT_KEYWORD}               { yybegin(LUT_BODY); return LUT_KEYWORD; }
  {FUNC_KEYWORD}              { return FUNC_KEYWORD; }
  {TEMPLATE_KEYWORD}          { return TEMPLATE_KEYWORD; }
  {USE_KEYWORD}               { return USE_KEYWORD; }
  {STATE_KEYWORD}             { return STATE_KEYWORD; }
  {RETURN_KEYWORD}            { return RETURN_KEYWORD; }
  {JOIN_KEYWORD}              { return JOIN_KEYWORD; }
  {LIMIT_KEYWORD}             { return LIMIT_KEYWORD; }
  {AFFECT_KEYWORD}            { return AFFECT_KEYWORD; }
  {DELAY_KEYWORD}             { return DELAY_KEYWORD; }
  {TEST_KEYWORD}              { return TEST_KEYWORD; }
  {EVAL_KEYWORD}              { return EVAL_KEYWORD; }
  {NULL_KEYWORD}              { return NULL_KEYWORD; }
  {IDENTIFIER}                { return IDENTIFIER; }
  {CLOSE_BRACKET}             { return CLOSE_BRACKET; }
  {CLOSE_BRACE}               { return CLOSE_BRACE; }
  {OPEN_PAR}                  { return OPEN_PAR; }
  {CLOSE_PAR}                 { return CLOSE_PAR; }
  {OPEN_TAG}                  { return OPEN_TAG; }
  {CLOSE_TAG}                 { return CLOSE_TAG; }
  {QUESTION_MARK}             { return QUESTION_MARK; }
  {DIGITS}                    { return DIGITS; }
  {TRIT}                      { return TRIT; }
  {RANGE_OPERATOR}            { return RANGE_OPERATOR; }
  {SMART_RANGE_OPERATOR}      { return SMART_RANGE_OPERATOR; }
  {ASSIGN}                    { return ASSIGN; }
  {SLASH}                     { return SLASH; }


[^] { return BAD_CHARACTER; }
