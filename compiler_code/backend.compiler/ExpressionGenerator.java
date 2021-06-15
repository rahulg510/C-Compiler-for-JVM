package backend.compiler;

import antlr4.SubCParser;

import intermediate.symtab.*;
import intermediate.type.*;
import intermediate.type.Typespec.Form;

import static intermediate.type.Typespec.Form.*;
import static backend.compiler.Instruction.*;

/**
 * <h1>ExpressionGenerator</h1>
 *
 * <p>Generate code for an expression.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ExpressionGenerator extends CodeGenerator
{
    /**
     * Constructor.
     * @param the parent executor.
     */
    public ExpressionGenerator(CodeGenerator parent, Compiler compiler)
    {
        super(parent, compiler);
    }
    
    /**
     * Emit code for an expression.
     * @param ctx the ExpressionContext.
     */
    public void emitExpression(SubCParser.ExpressionContext ctx)
    {
        SubCParser.SimpleExpressionContext simpleCtx1 = 
                                                ctx.simpleExpression().get(0);
        SubCParser.RelOpContext relOpCtx = ctx.relOp();
        Typespec type1 = simpleCtx1.type;
        emitSimpleExpression(simpleCtx1);
        
        // More than one simple expression?
        if (relOpCtx != null)
        {
            String op = relOpCtx.getText();
            SubCParser.SimpleExpressionContext simpleCtx2 = 
                                                ctx.simpleExpression().get(1);
            Typespec type2 = simpleCtx2.type;

            boolean integerMode   = false;
            boolean realMode      = false;
            boolean characterMode = false;

            if (   (type1 == Predefined.integerType)
                && (type2 == Predefined.integerType)) 
            {
                integerMode = true;
            }
            else if (   (type1 == Predefined.realType) 
                     || (type2 == Predefined.realType))
            {
                realMode = true;
            }
            else if (   (type1 == Predefined.charType) 
                     && (type2 == Predefined.charType))
            {
                characterMode = true;
            }

            Label trueLabel = new Label();
            Label exitLabel = new Label();

            if (integerMode || characterMode) 
            {
                emitSimpleExpression(simpleCtx2);
                
                if      (op.equals("==" )) emit(IF_ICMPEQ, trueLabel);
                else if (op.equals("!=")) emit(IF_ICMPNE, trueLabel);
                else if (op.equals("<" )) emit(IF_ICMPLT, trueLabel);
                else if (op.equals("<=")) emit(IF_ICMPLE, trueLabel);
                else if (op.equals(">" )) emit(IF_ICMPGT, trueLabel);
                else if (op.equals(">=")) emit(IF_ICMPGE, trueLabel);
            }
            else if (realMode)
            {
                if (type1 == Predefined.integerType) emit(I2F);
                emitSimpleExpression(simpleCtx2);
                if (type2 == Predefined.integerType) emit(I2F);
                
                emit(FCMPG);

                if      (op.equals("==" )) emit(IFEQ, trueLabel);
                else if (op.equals("!=")) emit(IFNE, trueLabel);
                else if (op.equals("<" )) emit(IFLT, trueLabel);
                else if (op.equals("<=")) emit(IFLE, trueLabel);
                else if (op.equals(">" )) emit(IFGT, trueLabel);
                else if (op.equals(">=")) emit(IFGE, trueLabel);
            }
            else  // stringMode
            {
                emitSimpleExpression(simpleCtx2);
                emit(INVOKEVIRTUAL,
                     "java/lang/String.compareTo(Ljava/lang/String;)I");
                localStack.decrease(1);
                
                if      (op.equals("==" )) emit(IFEQ, trueLabel);
                else if (op.equals("!=")) emit(IFNE, trueLabel);
                else if (op.equals("<" )) emit(IFLT, trueLabel);
                else if (op.equals("<=")) emit(IFLE, trueLabel);
                else if (op.equals(">" )) emit(IFGT, trueLabel);
                else if (op.equals(">=")) emit(IFGE, trueLabel);
            }

            emit(ICONST_0); // false
            emit(GOTO, exitLabel);
            emitLabel(trueLabel);
            emit(ICONST_1); // true
            emitLabel(exitLabel);
            
            localStack.decrease(1);  // only one branch will be taken
        }
    }
    
    
    public void emitforControlExpression(SubCParser.ExpressionContext ctx)
    {
        SubCParser.SimpleExpressionContext simpleCtx1 = 
                                                ctx.simpleExpression().get(0);
        SubCParser.RelOpContext relOpCtx = ctx.relOp();
        Typespec type1 = simpleCtx1.type;
        emitSimpleExpression(simpleCtx1);
        
        // More than one simple expression?
        if (relOpCtx != null)
        {
            String op = relOpCtx.getText();
            SubCParser.SimpleExpressionContext simpleCtx2 = 
                                                ctx.simpleExpression().get(1);
            Typespec type2 = simpleCtx2.type;

            boolean integerMode   = false;
            boolean realMode      = false;
            boolean characterMode = false;

            if (   (type1 == Predefined.integerType)
                && (type2 == Predefined.integerType)) 
            {
                integerMode = true;
            }
            else if (   (type1 == Predefined.realType) 
                     || (type2 == Predefined.realType))
            {
                realMode = true;
            }
            else if (   (type1 == Predefined.charType) 
                     && (type2 == Predefined.charType))
            {
                characterMode = true;
            }

            Label trueLabel = new Label();
            Label exitLabel = new Label();

            if (integerMode || characterMode) 
            {
                emitSimpleExpression(simpleCtx2);
                
                if      (op.equals("==" )) emit(IF_ICMPEQ, trueLabel);
                else if (op.equals("!=")) emit(IF_ICMPNE, trueLabel);
                else if (op.equals("<" )) emit(IF_ICMPLT, trueLabel);
                else if (op.equals("<=")) emit(IF_ICMPLE, trueLabel);
                else if (op.equals(">" )) emit(IF_ICMPGT, trueLabel);
                else if (op.equals(">=")) emit(IF_ICMPGE, trueLabel);
            }
            else if (realMode)
            {
                if (type1 == Predefined.integerType) emit(I2F);
                emitSimpleExpression(simpleCtx2);
                if (type2 == Predefined.integerType) emit(I2F);
                
                emit(FCMPG);

                if      (op.equals("==" )) emit(IFEQ, trueLabel);
                else if (op.equals("!=")) emit(IFNE, trueLabel);
                else if (op.equals("<" )) emit(IFLT, trueLabel);
                else if (op.equals("<=")) emit(IFLE, trueLabel);
                else if (op.equals(">" )) emit(IFGT, trueLabel);
                else if (op.equals(">=")) emit(IFGE, trueLabel);
            }
            else  // stringMode
            {
                emitSimpleExpression(simpleCtx2);
                emit(INVOKEVIRTUAL,
                     "java/lang/String.compareTo(Ljava/lang/String;)I");
                localStack.decrease(1);
                
                if      (op.equals("==" )) emit(IFEQ, trueLabel);
                else if (op.equals("!=")) emit(IFNE, trueLabel);
                else if (op.equals("<" )) emit(IFLT, trueLabel);
                else if (op.equals("<=")) emit(IFLE, trueLabel);
                else if (op.equals(">" )) emit(IFGT, trueLabel);
                else if (op.equals(">=")) emit(IFGE, trueLabel);
            }

            emit(ICONST_0); // false
            emit(GOTO, exitLabel);
            emitLabel(trueLabel);
            emit(ICONST_1); // true
            emitLabel(exitLabel);
            
            localStack.decrease(1);  // only one branch will be taken
        }
    }
    
    
    /**
     * Emit code for a simple expression.
     * @param ctx the SimpleExpressionContext.
     */
    public void emitSimpleExpression(SubCParser.SimpleExpressionContext ctx)
    {
        int count = ctx.term().size();
        Boolean negate =    (ctx.sign() != null) 
                         && ctx.sign().getText().equals("-");
        
        // First term.
        SubCParser.TermContext termCtx1 = ctx.term().get(0);
        Typespec type1 = termCtx1.type;
        emitTerm(termCtx1);
        
        if (negate) emit(type1 == Predefined.integerType ? INEG : FNEG);
        
        // Loop over the subsequent terms.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.addOp().get(i-1).getText().toLowerCase();
            SubCParser.TermContext termCtx2 = ctx.term().get(i);
            Typespec type2 = termCtx2.type;

            boolean integerMode = false;
            boolean realMode    = false;

            if (   (type1 == Predefined.integerType)
                && (type2 == Predefined.integerType)) 
            {
                integerMode = true;
            }
            else if (   (type1 == Predefined.realType) 
                     || (type2 == Predefined.realType))
            {
                realMode = true;
            }
                            
            if (integerMode)
            {
                emitTerm(termCtx2);
                
                if (op.equals("+")) emit(IADD);
                else                emit(ISUB);
            }
            else if (realMode)
            {
                if (type1 == Predefined.integerType) emit(I2F);
                emitTerm(termCtx2);
                if (type2 == Predefined.integerType) emit(I2F);
                
                if (op.equals("+")) emit(FADD);
                else                emit(FSUB);
            }
            else  // stringMode
            {
                emit(NEW, "java/lang/StringBuilder");
                emit(DUP_X1);             
                emit(SWAP);                  
                emit(INVOKESTATIC, "java/lang/String/valueOf(Ljava/lang/Object;)" +
                                   "Ljava/lang/String;");
                emit(INVOKESPECIAL, "java/lang/StringBuilder/<init>" +
                                    "(Ljava/lang/String;)V");
                localStack.decrease(1);
                
                emitTerm(termCtx2);
                emit(INVOKEVIRTUAL, "java/lang/StringBuilder/append(Ljava/lang/String;)" +
                                    "Ljava/lang/StringBuilder;");
                localStack.decrease(1);
                emit(INVOKEVIRTUAL, "java/lang/StringBuilder/toString()" +
                                    "Ljava/lang/String;");
                localStack.decrease(1);
            }
        }
    }
    
    /**
     * Emit code for a term.
     * @param ctx the TermContext.
     */
    public void emitTerm(SubCParser.TermContext ctx)
    {
        int count = ctx.factor().size();
        
        // First factor.
        SubCParser.FactorContext factorCtx1 = ctx.factor().get(0);
        Typespec type1 = factorCtx1.type;
        compiler.visit(factorCtx1);
        
        // Loop over the subsequent factors.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.mulOp().get(i-1).getText().toLowerCase();
            SubCParser.FactorContext factorCtx2 = ctx.factor().get(i);
            Typespec type2 = factorCtx2.type;

            boolean integerMode = false;
            boolean realMode    = false;

            if (   (type1 == Predefined.integerType)
                && (type2 == Predefined.integerType)) 
            {
                integerMode = true;
            }
            else if (   (type1 == Predefined.realType) 
                     || (type2 == Predefined.realType))
            {
                realMode = true;
            }
                
            if (integerMode)
            {
                compiler.visit(factorCtx2);            

                if      (op.equals("*"))   emit(IMUL);
                else if (op.equals("/"))   emit(FDIV);
                else if (op.equals("div")) emit(IDIV);
                else if (op.equals("mod")) emit(IREM);
            }
            else if (realMode)
            {
                if (type1 == Predefined.integerType) emit(I2F);
                compiler.visit(factorCtx2); 
                if (type2 == Predefined.integerType) emit(I2F);
                
                if      (op.equals("*")) emit(FMUL);
                else if (op.equals("/")) emit(FDIV);
            }
            else  // booleanMode
            {
                compiler.visit(factorCtx2);                 
                emit(IAND);
            }
        }
    }
    
    /**
     * Emit code for NOT.
     * @param ctx the NotFactorContext.
     */
    public void emitNotFactor(SubCParser.NotFactorContext ctx)
    {
        compiler.visit(ctx.factor());
        emit(ICONST_1);
        emit(IXOR);
    }

    /**
     * Emit code to load a scalar variable's value 
     * or a structured variable's address.
     * @param ctx the VariableContext.
     */
    public void emitLoadValue(SubCParser.VariableContext varCtx)
    {
        // Load the scalar value or structure address.
        Typespec variableType = emitLoadVariable(varCtx);
        

    }

    /**
     * Emit code to load a scalar variable's value 
     * or a structured variable's address.
     * @param variableNode the variable node.
     * @return the datatype of the variable.
     */
    public Typespec emitLoadVariable(SubCParser.VariableContext varCtx)
    {
        SymtabEntry variableId = varCtx.entry;
        Typespec variableType = variableId.getType();

        
        // Scalar value or structure address.
        emitLoadValue(variableId);


        return variableType;
    }

    /**
     * Emit code to access an array element by loading the array address
     * and the subscript value. This can subsequently be followed by code
     * to load the array element's value or to store into the array element. 
     * @param subscriptsNode the SUBSCRIPTS node.
     * @param elmtType the array element type.
     * @param lastModifier true if this is the variable's last modifier.
     * @return the type of the element.
     */
//    private Typespec emitLoadArrayElementAccess(
//                                    SubCParser.IndexListContext indexListCtx,
//                                    Typespec elmtType, boolean lastModifier)
//    {
//        int indexCount = indexListCtx.index().size();
//        
//        // Loop over the subscripts.
//        for (int i = 0; i < indexCount; i++)
//        {
//            SubCParser.IndexContext indexCtx = indexListCtx.index().get(i);
//            emitExpression(indexCtx.expression());
//
//            if (!lastModifier || (i < indexCount - 1)) emit(AALOAD);
//            elmtType = elmtType.getArrayElementType();
//        }
//
//        return elmtType;
//    }

    /**
     * Emit a load of an array element's value.
     * @param elmtType the element type if character, else null.
     */
    private void emitLoadArrayElementValue(Typespec elmtType)
    {

        // Load a character from a string.
        if (elmtType == Predefined.charType) 
        {
            emit(INVOKEVIRTUAL, "java/lang/StringBuilder.charAt(I)C");
        }

        // Load an array element.
        else 
        {
            emit(  elmtType == Predefined.integerType ? IALOAD
                 : elmtType == Predefined.realType    ? FALOAD
                 : elmtType == Predefined.charType    ? CALOAD
                 :                                      AALOAD);
        }
    }
    
    /**
     * Emit code to load an integer constant.
     * @parm intCtx the IntegerConstantContext.
     */
    public void emitLoadIntegerConstant(SubCParser.NumberContext intCtx)
    {
        int value = Integer.parseInt(intCtx.getText());
        emitLoadConstant(value);
    }
    
    /**
     * Emit code to load real constant.
     * @parm intCtx the IntegerConstantContext.
     */
    public void emitLoadRealConstant(SubCParser.NumberContext realCtx)
    {
        float value = Float.parseFloat(realCtx.getText());
        emitLoadConstant(value);
    }
}
