package backend.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import antlr4.*;
import antlr4.SubCParser;
import intermediate.symtab.*;
import intermediate.type.*;
import intermediate.type.Typespec.Form;

import static intermediate.type.Typespec.Form.*;
import static backend.compiler.Instruction.*;

/**
 * <h1>StatementGenerator</h1>
 *
 * <p>Emit code for executable statements.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class StatementGenerator extends CodeGenerator
{
    /**
     * Constructor.
     * @param parent the parent generator.
     * @param compiler the compiler to use.
     */
    public StatementGenerator(CodeGenerator parent, Compiler compiler)
    {
        super(parent, compiler);
    }

    /**
     * Emit code for an assignment statement.
     * @param ctx the AssignmentStatementContext.
     */
    public void emitAssignment(SubCParser.AssignmentStatementContext ctx)
    {
        SubCParser.VariableContext   varCtx  = ctx.lhs().variable();
        SubCParser.ExpressionContext exprCtx = ctx.rhs().expression();
        SymtabEntry varId = varCtx.entry;
        Typespec varType  = varCtx.type;
        Typespec exprType = exprCtx.type;



        
        // Emit code to evaluate the expression.
        compiler.visit(exprCtx);
        
        // float variable := integer constant
        if (   (varType == Predefined.realType)
            && (exprType.baseType() == Predefined.integerType)) emit(I2F);
        
        // Emit code to store the expression value into the target variable.
        // The target variable has no subscripts or fields.
        emitStoreValue(varId, varId.getType());


    }

    /**
     * Emit code for an IF statement.
     * @param ctx the IfStatementContext.
     */
    public void emitIf(SubCParser.IfStatementContext ctx)
    {
        boolean elseExists = ctx.falseStatement() != null;
        Label nextLabel = new Label();
        Label falseLabel = new Label();

        compiler.visit(ctx.expression());
        if(elseExists)
            emit(IFEQ, falseLabel);
        else
            emit(IFEQ, nextLabel);

        compiler.visit(ctx.trueStatement());


        if(elseExists)
        {
            emit(GOTO, nextLabel);
            emitLabel(falseLabel);
            compiler.visit(ctx.falseStatement());
        }

        emitLabel(nextLabel);

    }
    
    /**
     * Emit code for a CASE statement.
     * @param ctx the CaseStatementContext.
     */
    public void emitSwitch(SubCParser.SwitchStatementContext ctx)
    {
        compiler.visit(ctx.expression());
        emit(LOOKUPSWITCH);
        ArrayList<Integer> caseConsts = new ArrayList<>();
        HashMap<Integer, Label> cases = new HashMap<>();
        HashMap<Label, SubCParser.CaseBranchContext> branches = new HashMap<>();
        for(SubCParser.CaseBranchContext branch : ctx.switchBranchList().caseBranch()){
            if(branch.caseConstantList() != null){
                Label newLabel = new Label();
                for(SubCParser.CaseConstantContext constant:  branch.caseConstantList().caseConstant())
                {
                    caseConsts.add(constant.value);
                    cases.put(constant.value,newLabel);
                }
                branches.put(newLabel, branch);
            }
        }
        
        Label defaultLabel = new Label();
        Collections.sort(caseConsts);
        for(Integer x: caseConsts){
            emitLabel(x,cases.get(x));
        }
        emitLabel("default", defaultLabel);
        Label breakLabel = new Label();

        for(Map.Entry<Label, SubCParser.CaseBranchContext> entry : branches.entrySet()){
            emitLabel(entry.getKey());
            compiler.visit(entry.getValue());
            if(entry.getValue().caseCompound().BREAK() != null) {
            	emit(GOTO, breakLabel);
            }
            
        }

        emitLabel(defaultLabel);
        SubCParser.DefaultBranchContext defCtx = ctx.switchBranchList().defaultBranch();
        compiler.visit(defCtx.caseCompound());
        
        emitLabel(breakLabel);
    }
    
    /**
     * Emit code for a WHILE statement.
     * @param ctx the WhileStatementContext.
     */
    public void emitWhile(SubCParser.WhileStatementContext ctx)
    {
        Label loopTopLabel = new Label();
        Label loopExitLabel = new Label();

        emitLabel(loopTopLabel);
        compiler.visit(ctx.expression());        
        emit(IFEQ, loopExitLabel);
        compiler.visit(ctx.compoundStatement());
        emit(GOTO, loopTopLabel);
        emitLabel(loopExitLabel);
    }
    
    /**
     * Emit code for a FOR statement.
     * @param ctx the ForStatementContext.
     */
    public void emitFor(SubCParser.ForStatementContext ctx)
    {
    	
    	compiler.visit(ctx.forInitialization().assignmentStatement());
    	
    	Label cmpLabel = new Label();
    	emitLabel(cmpLabel);
    	
    	SubCParser.ExpressionContext expCtx1 = ctx.forControl().expression();
    	SubCParser.SimpleExpressionContext simpleCtx1 = expCtx1.simpleExpression().get(0);
		SubCParser.RelOpContext relOpCtx = expCtx1.relOp();
			
		compiler.visit(simpleCtx1);
			
		String op = relOpCtx.getText();
		SubCParser.SimpleExpressionContext simpleCtx2 = 
								expCtx1.simpleExpression().get(1);
			
		Label trueLabel = new Label();
		compiler.visit(simpleCtx2);

//		compiler.visit(simpleCtx2);
		if      (op.equals("==" )) emit(IF_ICMPEQ, trueLabel);
		else if (op.equals("!=")) emit(IF_ICMPNE, trueLabel);
		else if (op.equals("<" )) emit(IF_ICMPLT, trueLabel);
		else if (op.equals("<=")) emit(IF_ICMPLE, trueLabel);
		else if (op.equals(">" )) emit(IF_ICMPGT, trueLabel);
		else if (op.equals(">=")) emit(IF_ICMPGE, trueLabel);
		
		Label exitLabel = new Label();
		emit(GOTO, exitLabel);
			 
		
		emitLabel(trueLabel);
		
		compiler.visit(ctx.compoundStatement());
		SubCParser.ForIncrementStatementContext incCtx = ctx.forIncrementStatement();
		compiler.visit(incCtx);
		String name = compiler.getProgramName() + "/" + incCtx.lhs().variable().entry.getName();
		emit(PUTSTATIC,name,"I");
		emit(POP);
		emit(GOTO, cmpLabel);

	     emitLabel(exitLabel);
	     
	     localStack.decrease(1); 
    	
    }
    
    /**
     * Emit code for a procedure call statement.
     * @param ctx the ProcedureCallStatementContext.
     */
    public void emitFunctionCall(SubCParser.FunctionCallContext ctx)
    {
        String argTypes = "";
        SymtabEntry routineId = ctx.functionName().entry;
		ArrayList<SymtabEntry> paramIds = routineId.getRoutineParameters();
        int i = 0;
        if(ctx.argumentList() != null)
        {
            for(SymtabEntry param : paramIds)
            {
                argTypes += typeDescriptor(param);


            }

            for (SubCParser.ArgumentContext argCtx : ctx.argumentList().argument())
            {
                SubCParser.ExpressionContext exprCtx = argCtx.expression();
                compiler.visit(exprCtx);
                if(typeDescriptor(paramIds.get(i)).equals("F") && TypeChecker.isInteger(exprCtx.type))
                    emit(I2F);
                i++;
            }


        }
        String returnType = typeDescriptor(routineId);
        emit(INVOKESTATIC, programName + "/" + ctx.functionName().IDENTIFIER().getText() + "("
                + argTypes + ")" + returnType);
    }
    
    /**
     * Emit code for a function call statement.
     * @param ctx the FunctionCallContext.
     */
    public void emitFunctionCall(SubCParser.FunctionCallStatementContext ctx)
    {
        emitFunctionCall(ctx.functionCall());
    }
    
    /**
     * Emit a call to a procedure or a function.
     * @param routineId the routine name's symbol table entry.
     * @param argListCtx the ArgumentListContext.
     */
    private void emitCall(SymtabEntry routineId,
                          SubCParser.ArgumentListContext argListCtx)
    {
        /***** Complete this method. *****/
    }

    /**
     * Emit code for a WRITE statement.
     * @param ctx the WriteStatementContext.
     */
    public void emitPrint(SubCParser.PrintStatementContext ctx)
    {
        emitPrint(ctx.formatString(), ctx.writeArguments(), false);
    }

    /**
     * Emit code for a call to WRITE or WRITELN.
     * @param argsCtx the WriteArgumentsContext.
     * @param needLF true if need a line feed.
     */
    private void emitPrint(SubCParser.FormatStringContext format, SubCParser.WriteArgumentsContext argsCtx,
                           boolean needLF)
    {
        emit(GETSTATIC, "java/lang/System/out", "Ljava/io/PrintStream;");

        // WRITELN with no arguments.
        if (argsCtx == null) 
        {
        	emit(LDC, format.getText());
            emit(INVOKEVIRTUAL, "java/io/PrintStream/print(Ljava/lang/String;)V");
            localStack.decrease(1);
        }
            
        // Generate code for the arguments.
        else
        {
            int exprCount = argsCtx.children.size();           
            
            // Load the format string.
            emit (LDC, format.getText());
            
            // Emit the arguments array.
            if (exprCount > 0)
            {
                emitArgumentsArray(argsCtx, exprCount);

                emit(INVOKEVIRTUAL,
                     "java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Ljava/io/PrintStream;");
                localStack.decrease(2);
                emit(POP);
            }
            else
            {
                emit(INVOKEVIRTUAL,
                     "java/io/PrintStream/print(Ljava/lang/String;)V");
                localStack.decrease(2);
            }
        }
    }
    
    
    /**
     * Emit the printf arguments array.
     * @param argsCtx
     * @param exprCount
     */
    private void emitArgumentsArray(SubCParser.WriteArgumentsContext argsCtx,
                                    int exprCount)
    {
        // Create the arguments array.
        emitLoadConstant(exprCount);
        emit(ANEWARRAY, "java/lang/Object");

        int index = 0;

        // Loop over the write arguments to fill the arguments array.
        for (SubCParser.WriteArgumentContext argCtx : 
                                                    argsCtx.writeArgument())
        {
            String argText = argCtx.getText();
            SubCParser.ExpressionContext exprCtx = argCtx.expression();
            Typespec type = exprCtx.type.baseType();
            
            // Skip string constants, which were made part of
            // the format string.
            if (argText.charAt(0) != '\'') 
            {
                emit(DUP);
                emitLoadConstant(index++);

                compiler.visit(exprCtx);

                Form form = type.getForm();
                if (    ((form == SCALAR))
                     && (type != Predefined.stringType))
                {
                    emit(INVOKESTATIC, valueOfSignature(type));
                }

                // Store the value into the array.
                emit(AASTORE);
            }
        }
    }
}