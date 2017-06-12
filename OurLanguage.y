%{
#include <iostream>
#include <string>
#include <map>
#include <cstdlib> //-- I need this for atoi
using namespace std;
extern int line_no;
//-- Lexer prototype required by bison, aka getNextToken()
int yylex(); 
int yyerror(const char *p) { cerr << "Parse error in line " <<line_no<< endl; }
%}

%token  number add sub Define MainProcess OpenBR CloseBR ID simicolon CompareOP MathOP IF ELSE OpenPR ClosePR colon WHILE MultiComm singleComm NOT AndOr MathFunc Assignment
%start Programm

//-- GRAMMAR RULES ---------------------------------------
%%
Programm : Function MainProcess OpenBR Commentchooser MainBody CloseBR|error;   // check
Function : Define ID OpenBR Commentchooser Statements CloseBR Function |;		//check

MainBody : ID simicolon MainBody  | MultiComm MainBody | singleComm MainBody | ;

//MainBody : ID simicolon AfterID  | MultiComm AfterID | singleComm AfterID; //check
//AfterID : MainBody | ;		//check


Commentchooser : MultiComm  | singleComm ;

Statements : IFB Statements | WhileCondition Statements | MultiComm  Statements| singleComm Statements|NormalStatements| ; // Check

WhileCondition : WHILE OpenPR Condition ClosePR OpenBR Statements CloseBR ;

IFB : IF OpenPR Condition ClosePR colon OpenBR Statements CloseBR ElseB ;
ElseB : ELSE OpenPR Condition ClosePR colon OpenBR Statements CloseBR | ;

Condition : Compare RemainC | NOT Choose  | Logical RemainL ; 

Logical : Exp AndOr Exp ;
RemainL : AndOr Choose | ;

Compare : Exp CompareOP Exp ;
RemainC : AndOr Choose | ;
Choose : Condition | Exp ;
 


Exp : ID ChooseNext |number ChooseNext | MathFunc OpenPR Exp ClosePR ChooseNext ;
ChooseNext : MathOP Exp | ;


NormalStatements : Exp Assignment Exp simicolon ; //chechk  --- nemidunam aya tamame halata ro dar nazar gereftam ya na 
	
%%
//-- FUNCTION DEFINITIONS ---------------------------------
int main()
{
  yyparse();
  return 0;
}


