package intermediate.type;

import intermediate.symtab.*;

/**
 * <h1>Typespec</h1>
 *
 * <p>The type specification object for various datatypes.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class Typespec
{
    private Form form;               // type form
    private SymtabEntry identifier;  // type identifier
    private TypeInfo info;           // type information
    
    public enum Form
    {
        SCALAR,ARRAY,VOID;

        public String toString() { return super.toString().toLowerCase(); }
    }
    
    /**
     * Type information interface.
     */
    private interface TypeInfo {}
    
    /**
     * Array type information.
     */
    private class ArrayInfo implements TypeInfo
    {
        private Typespec indexType;
        private Typespec elementType;
        private int elementCount;
    }
    

    /**
     * Constructor.
     * @param form the type form.
     */
    public Typespec(Form form)
    {
        this.form = form;
        this.identifier = null;
        
        // Initialize the appropriate type information.
        switch (form)
        {
            case ARRAY:
                info = new ArrayInfo();
                ((ArrayInfo) info).indexType = null;
                ((ArrayInfo) info).elementType = null;
                ((ArrayInfo) info).elementCount = 0;
                break;
                
            default: break;
        }
    }
    
    /**
     * Determine whether or not the type is structured (array or record).
     * @return true if structured, false if not.
     */
    public boolean isStructured() 
    { 
        return form == Form.ARRAY;
    }

    /**
     * Get the type form.
     * @return the form.
     */
    public Form getForm() { return form; }

    /**
     * Get the type identifier.
     * @return the identifier's symbol table entry.
     */
    public SymtabEntry getIdentifier() { return identifier; }

    /**
     * Setter.
     * @param identifier the type identifier (symbol table entry).
     */
    public void setIdentifier(SymtabEntry identifier)
    {
        this.identifier = identifier;
    }

    /**
     * Get the base type of this type.
     * @return the base type.
     */
    public Typespec baseType()
    {
        return this;
    }

    /**
     * Get the array index data type.
     * @return the data type.
     */
    public Typespec getArrayIndexType()
    {
        return ((ArrayInfo) info).indexType;
    }

    /**
     * Set the array index data type.
     * @parm index_type the data type to set.
     */
    public void setArrayIndexType(Typespec indexType)
    {
        ((ArrayInfo) info).indexType = indexType;
    }

    /**
     * Get the array element data type.
     * @return the data type.
     */
    public Typespec getArrayElementType()
    {
        return ((ArrayInfo) info).elementType;
    }

    /**
     * Set the array element data type.
     * @return elmt_type the data type to set.
     */
    public void setArrayElementType(Typespec elementType)
    {
        ((ArrayInfo) info).elementType = elementType;
    }

    /**
     * Get the array element count.
     * @return the count.
     */
    public int getArrayElementCount() { return ((ArrayInfo) info).elementCount; }

    /**
     * Set the array element count.
     * @parm elmt_count the count to set.
     */
    public void setArrayElementCount(int elementCount)
    {
        ((ArrayInfo) info).elementCount = elementCount;
    }
    
    /**
     * Get the base type of an array.
     * @return the base type of its final dimension.
     */
    public Typespec getArrayBaseType()
    {
        Typespec elmtType = this;
        
        while (elmtType.form == Form.ARRAY)
        {
            elmtType = elmtType.getArrayElementType();
        }
        
        return elmtType.baseType();
    }

    
}
