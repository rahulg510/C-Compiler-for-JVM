grammar SubC;

@header {
    package antlr4;
    import java.util.HashMap;
    import intermediate.symtab.SymtabEntry;
    import intermediate.type.Typespec;
}

program: programHeader functionDefinitions mainProgram  ;
programHeader: PROGRAM programIdentifier ';' ;
programIdentifier locals [ SymtabEntry entry = null ]
   : IDENTIFIER ;
mainProgram: INT MAIN '(' parameterList? ')' compoundStatement;

functionName locals [ Typespec type = null, SymtabEntry entry = null ]
    : IDENTIFIER ;
functionDefinitions:  functionDefinition* ;
functionDefinition : TYPE = (INT | STRING | CHAR | DOUBLE | VOID) functionName '(' parameterList? ')' compoundStatement ;
parameterList : parameter ( ',' parameter )* ;
parameter : TYPE = (INT | STRING | CHAR | DOUBLE) parameterIdentifier ;

parameterIdentifier   locals [ Typespec type = null, SymtabEntry entry = null ]
    : IDENTIFIER ;

functionCallStatement : functionCall SEMICOLON ;
functionCall : functionName '(' argumentList? ')' ;
argumentList : argument ( ',' argument)* ;
argument     : expression ;
returnStatement: RETURN (expression | functionCall)? SEMICOLON ;

statement : compoundStatement
          | declarationStatement
          | assignmentStatement
          | ifStatement
          | switchStatement
          | whileStatement
          | forStatement
          | printStatement
          | functionCallStatement
          | returnStatement
          ;

statementList       : statement* ;
compoundStatement : '{' statementList '}' ;

declarationStatement: TYPE = (INT | STRING | CHAR | DOUBLE)  variable SEMICOLON;
assignmentStatement : TYPE = (INT | STRING | CHAR | DOUBLE)? lhs '=' rhs SEMICOLON 
                    | variable OP = ('++' | '--') SEMICOLON
                    ;
lhs locals [ Typespec type = null ] 
    : variable ;
rhs : expression ;


ifStatement    : IF '(' expression ')' trueStatement (ELSE falseStatement )? ;
trueStatement  : compoundStatement ;
falseStatement : compoundStatement ;

switchStatement
    locals [ HashMap<Integer, SubCParser.StatementContext> jumpTable = null ]
    : SWITCH '(' expression ')' '{' switchBranchList '}' ;
switchBranchList : caseBranch+ defaultBranch?;
caseBranch       : CASE caseConstantList ':' caseCompound ;
caseConstantList : caseConstant ( ',' caseConstant )* ;
caseCompound : statement* (BREAK SEMICOLON)?;
defaultBranch    : 'default' ':' caseCompound;
caseConstant    locals [ Typespec type = null, int value = 0 ]
    : constant ;


whileStatement  : WHILE '(' expression ')' compoundStatement ;

forStatement : FOR '(' forInitialization forControl forIncrementStatement ')' compoundStatement ;
forInitialization : assignmentStatement;
forControl  : expression SEMICOLON;
forIncrementStatement : lhs '=' rhs
                      | variable OP = ('++' | '--')
                      ;

printStatement   : PRINT '(' formatString (',' writeArguments)? ')' SEMICOLON ;
formatString     : stringConstant;
writeArguments   : writeArgument (',' writeArgument)* ;
writeArgument    : expression ;

expression          locals [ Typespec type = null ] 
    : simpleExpression (relOp simpleExpression)? ;
    
simpleExpression    locals [ Typespec type = null ] 
    : sign? term (addOp term)* ;
    
term                locals [ Typespec type = null ]
    : factor (mulOp factor)* ;

factor              locals [ Typespec type = null ] 
    : variable             # variableFactor
    | number               # numberFactor
    | characterConstant    # characterFactor
    | stringConstant       # stringFactor
    | functionCall         # functionCallFactor
    | NOT factor           # notFactor
    | '(' expression ')'   # parenthesizedFactor
    ;

variableIdentifier  locals [ Typespec type = null, SymtabEntry entry = null ] 
    : IDENTIFIER ;

variable            locals [ Typespec type = null, SymtabEntry entry = null ] 
    : variableIdentifier ;

constant   locals [ Typespec type = null, Object value = null ]  
    : sign? ( IDENTIFIER | unsignedNumber )
    | characterConstant
    | stringConstant
    ;

sign              : '-' | '+' ;
number            : sign? unsignedNumber ;
unsignedNumber    : integerConstant | realConstant ;
integerConstant   : INTEGER ;
realConstant      : REAL;
characterConstant : CHARACTER ;
stringConstant    : STR ;
       
relOp : '==' | '!='| '<' | '<=' | '>' | '>=' ;
addOp :  '+' | '-' | OR ;
mulOp :  '*' | '/' | MOD | AND ;

MOD       : '%' ;
AND       : '&&' ;
OR        : '||' ;
NOT       : '!' ;
IF        : 'if' ;
ELSE      : 'else' ;
SWITCH    : 'switch';
WHILE     : 'while' ;
FOR       : 'for' ;
CASE      : 'case';
PRINT     : 'print' ;
FUNCTION  : 'function' ;
INT       : 'int' ;
STRING    : 'string' ;
CHAR      : 'char' ;
MAIN      : 'main';
RETURN    : 'return';
DOUBLE    : 'double' ;
VOID      : 'void' ;
BREAK     : 'break';
PROGRAM   : 'Program';

IDENTIFIER : [a-zA-Z][a-zA-Z0-9]* ;
INTEGER    : [0-9]+ ;

REAL       : INTEGER '.' INTEGER
           | INTEGER ('e' | 'E') ('+' | '-')? INTEGER
           | INTEGER '.' INTEGER ('e' | 'E') ('+' | '-')? INTEGER
           ;

NEWLINE : '\r'? '\n' -> skip  ;
WS      : [ \t]+ -> skip ; 

QUOTE     : '\'' ;
CHARACTER : QUOTE CHARACTER_CHAR QUOTE ;
STR       : '"' STRING_CHAR* '"' ;
SEMICOLON : ';' ;

fragment CHARACTER_CHAR : ~('\'')   // any non-quote character
                        ;

fragment STRING_CHAR    : ~('"')      // any non-quote character
                        ;

COMMENT : '/*' COMMENT_CHARACTER* '*/' -> skip ;

fragment COMMENT_CHARACTER : ~('*') ;
                     
                     