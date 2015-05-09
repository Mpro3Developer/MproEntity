package br.com.mpro3.MproEntity;

/**
 * Created by matheus on 21/03/15.
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

    public Value(String val)
    {
        isString = true;
        this.valueString = val;
    }

    public Value(int val)
    {
        isInt = true;
        this.valueInt = val;
    }

    public Value(short val)
    {
        isShort = true;
        this.valueShort = val;
    }

    public Value(long val)
    {
        isLong = true;
        this.valueLong = val;
    }

    public Value(float val)
    {
        isNumber = true;
        this.valueDouble = val;
    }

    public Value(double val)
    {
        isNumber = true;
        this.valueDouble = val;
    }

    public boolean isString()
    {
        return isString;
    }

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
