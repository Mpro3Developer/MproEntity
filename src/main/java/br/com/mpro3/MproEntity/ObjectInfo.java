package br.com.mpro3.MproEntity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matheus Castello on 18/03/15.
 */
public class ObjectInfo
{
    public int Cod;
    public int CodRef;
    public int Ix = 1;
    public String FieldId = "";
    public String Name = "";
    public String NameRef = "";
    public String Where = "";
    public String OrderBy= "";
    public int[] Limiter = new int[0];
    public ArrayList<String> Fields = new ArrayList<String>();
    public ArrayList<String> FieldsValues = new ArrayList<String>();
    public ArrayList<String> Types = new ArrayList<String>();
    public HashMap<String, String> References = new HashMap<String, String>();
    public ArrayList<String> NamesRef = new ArrayList<String>();
    public ArrayList<String> FieldsRef = new ArrayList<String>();
    public ArrayList<String> Comparators = new ArrayList<String>();
    public ArrayList<Value> LogicVals = new ArrayList<Value>();
    public ArrayList<String> LogicNexts = new ArrayList<String>();
}
