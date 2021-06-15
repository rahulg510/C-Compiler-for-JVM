package intermediate.symtab;

import java.util.ArrayList;

import intermediate.symtab.SymtabEntry.Kind;
import intermediate.symtab.SymtabEntry.Routine;
import intermediate.type.*;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.symtab.SymtabEntry.Routine.*;
import static intermediate.type.Typespec.Form.*;

/**
 * <h1>Predefined</h1>
 *
 * <p>Enter the predefined Pascal types, identifiers, and constants
 * into the symbol table.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class Predefined
{
    // Predefined types.
    public static Typespec integerType;
    public static Typespec realType;
    public static Typespec charType;
    public static Typespec stringType;
    public static Typespec booleanType;
    public static Typespec voidType;
    public static Typespec undefinedType;

    // Predefined identifiers.
    public static SymtabEntry integerId;
    public static SymtabEntry realId;
    public static SymtabEntry booleanId;
    public static SymtabEntry charId;
    public static SymtabEntry stringId;
    public static SymtabEntry voidId;
    public static SymtabEntry printId;

    /**
     * Initialize a symbol table stack with predefined identifiers.
     * @param symTab the symbol table stack to initialize.
     */
    public static void initialize(SymtabStack symTabStack)
    {
        initializeTypes(symTabStack);
        initializeStandardRoutines(symTabStack);
    }

    /**
     * Initialize the predefined types.
     * @param symTabStack the symbol table stack to initialize.
     */
    private static void initializeTypes(SymtabStack symTabStack)
    {
        // Type integer.
        integerId = symTabStack.enterLocal("int", TYPE);
        integerType = new Typespec(SCALAR);
        integerType.setIdentifier(integerId);
        integerId.setType(integerType);

        // Type real.
        realId = symTabStack.enterLocal("real", TYPE);
        realType = new Typespec(SCALAR);
        realType.setIdentifier(realId);
        realId.setType(realType);

        // Type char.
        charId = symTabStack.enterLocal("char", TYPE);
        charType = new Typespec(SCALAR);
        charType.setIdentifier(charId);
        charId.setType(charType);

        // Type string.
        stringId = symTabStack.enterLocal("string", TYPE);
        stringType = new Typespec(SCALAR);
        stringType.setIdentifier(stringId);
        stringId.setType(stringType);
        
        
        // Type string.
        booleanId = symTabStack.enterLocal("bool", TYPE);
        booleanType = new Typespec(SCALAR);
        booleanType.setIdentifier(booleanId);
        booleanId.setType(booleanType);

        // Type void.
        voidId = symTabStack.enterLocal("void", TYPE);
        voidType = new Typespec(VOID);
        voidType.setIdentifier(voidId);
        voidId.setType(voidType);
       
        // Undefined type.
        undefinedType = new Typespec(SCALAR);
    }

    /**
     * Initialize the standard procedures and functions.
     * @param symTabStack the symbol table stack to initialize.
     */
    private static void initializeStandardRoutines(SymtabStack symTabStack)
    {
        printId   = enterStandard(symTabStack, FUNCTION, "print",   PRINT);
    }

    /**
     * Enter a standard procedure or function into the symbol table stack.
     * @param symTabStack the symbol table stack to initialize.
     * @param kind either PROCEDURE or FUNCTION.
     * @param name the procedure or function name.
     * @param routineCode the routine code.
     */
    private static SymtabEntry enterStandard(SymtabStack symTabStack,
                                             Kind kind, String name,
                                             Routine routineCode)
    {
        SymtabEntry routineId = symTabStack.enterLocal(name, kind);
        routineId.setRoutineCode(routineCode);

        return routineId;
    }
}
