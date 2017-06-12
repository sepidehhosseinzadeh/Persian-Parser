
%{
#include <string.h>
#include <stdio.h>
%}


Digit	[0-9]
Keyword	"if"|"else"|"for"|"while"|"Define"|"MainProcess"
Logic	"<>"|"and"|"or"
ID	{Letter}({Letter}|{Digit}|"-"|"_")*
Letter	[a-zA-Z]
Assignment ":="
Mark	":"|"("|")"|","|";"|"{"|"}"
MathOperation	"+"|"-"|"*"|"/"|"log"|"sin"|"cos"
CompareOperation	"=="|"<="|">="|">"|"<"
SingleLineComment	("//")(.)*("\n")
MultiLineComment	("'''")(.|"\n")*("'''")
Space " "|"\t"

%%

{Digit}+ {
			printf("%s" , yytext );
			printf("%s" , " -->this token is Number \n");
}

{Keyword}	{	
	printf("%s" , yytext );
	printf("%s" , " -->this token is Keyword \n");
}

{Logic} {	
	printf("%s" , yytext );
	printf("%s" , " -->this token is Logic \n");
}

{ID} {			
	printf("%s" , yytext );
	printf("%s" ,  " -->this token is ID\n");			
}

{Assignment} {
	printf("%s" , yytext );
	printf("%s" , " -->this token is Assignment Operation \n");
}

{Mark} {
	printf("%s" , yytext );
	printf("%s" , " -->this token is Mark \n");
}

{MathOperation} {	
	printf("%s" , yytext );
	printf("%s" , " -->this token is Math Operation \n");
}

{CompareOperation} {	
	printf("%s" , yytext );
	printf("%s" , " -->this token is Compare Operation \n");
}

{SingleLineComment}	{
	printf("%s" , yytext );
	printf("%s" , " -->this line is Comment \n");	
}

{MultiLineComment} {
	
	printf("%s" , yytext );
	printf("%s" , " -->these lines are Comments \n");	
}

{Space}	{	
	printf("%s" , yytext );
}

. {	
	printf("%s" , yytext );
	printf("%s" ,  " -->this token is undefined \n");
}



%%		

int main()
{
	printf("%s","Your Program :\n");
	yylex();
	return 0;
}

