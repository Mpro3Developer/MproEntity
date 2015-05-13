package br.com.mpro3.MproEntity;

/**
 * Utility class overloaded methods with different values types for use with reflection by
 * setting a value type that corresponds to the type of field in {@see br.com.mpro3.MproEntity.Entity} noted class.
 */
public class Value
{
    private String valueString;
    private double valueDouble;
    private int valueInt;
    private long valueLong;
    private short valueShort;

    private boolean isString = false;
    private boolean isNumber = false;
    private boolean isInt = false;
    private boolean isShort = false;
    private boolean isLong = false;

    /**
     * Receive one value in string and store the cast value in the type passed as parameter
     * @param type java.lang type name string
     * @param val the string representation of value
     */
    public Value(String type, String val)
    {
        if(type.equals("String"))
        {
            isString = true;
            valueString = val.toString();
        }
        else if(type.equals("int"))
        {
            isInt = true;
            valueInt = Integer.parseInt(val);
        }
        else if(type.equals("short"))
        {
            isShort = true;
            valueShort = Short.parseShort(val);
        }
        else if(type.equals("long"))
        {
            isLong = true;
            valueLong = Long.parseLong(val);
        }
        else
        {
            isNumber = true;
            valueDouble = Double.parseDouble(val);
        }
    }

    /**
     * Receive one Object value
     * @param val Object value
     */
    public Value(Object val)
    {
        if(val.getClass().getSimpleName().equals("String"))
        {
            isString = true;
            valueString = val.toString();
        }
        else
        {
            isNumber = true;
            valueDouble = Double.parseDouble(val.toString());
        }
    }

    /**
     * Receive one String value
     * @param val String value
     */
    public Value(String val)
    {
        isString = true;
        this.valueString = val;
    }

    /**
     * Receive one Object value
     * @param val Object value
     */
    public Value(int val)
    {
        isInt = true;
        this.valueInt = val;
    }

    /**
     * Receive one Object value
     * @param val Object value
     */
    public Value(short val)
    {
        isShort = true;
        this.valueShort = val;
    }

    /**
     * Receive one Object value
     * @param val Object value
     */
    public Value(long val)
    {
        isLong = true;
        this.valueLong = val;
    }

    /**
     * Receive one Object value
     * @param val Object value
     */
    public Value(float val)
    {
        isNumber = true;
        this.valueDouble = val;
    }

    /**
     * Receive one Object value
     * @param val Object value
     */
    public Value(double val)
    {
        isNumber = true;
        this.valueDouble = val;
    }

    /**
     * Tes if the type of value stored is String
     * @return true if the value stored is one String
     */
    public boolean isString()
    {
        return isString;
    }

    /**
     * Return the Object generic value stored
     * @return the value stored
     */
    public Object getValue()
    {
        if(isNumber)
            return this.valueDouble;
        else if(isInt)
            return this.valueInt;
        else if(isShort)
            return this.valueShort;
        else if(isLong)
            return this.valueLong;

        return this.valueString;
    }

    @Override
    public String toString()
    {
        if(isNumber)
            return Double.toString(this.valueDouble);
        return this.valueString;
    }
}
