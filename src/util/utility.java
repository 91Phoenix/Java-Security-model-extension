package util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import model.MyContext;

public class utility {


	public static Method takeGetter(MyContext mC,String localName)
	{
		try 
		{
			return mC.getClass().getMethod("get"+firstToUpper(localName),null);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}

	}

	//recupera il metodo set per un particolare attributo di una particolare classe
	@SuppressWarnings("rawtypes")
	public static Method takeSetter(MyContext mC,String localName,Type c)
	{
		try 
		{
			Class current= null;
			try
			{
				current= Class.forName(c.getTypeName());
			}
			catch(Exception e)
			{
				current=Double.TYPE;
			}

			return mC.getClass().getMethod("set"+firstToUpper(localName),current);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	//ritorna una stringa con la prima lettera maiuscola
	public static String firstToUpper(String localName)
	{
		return localName.substring(0,1).toUpperCase()+
				localName.substring(1, localName.length());
	}

}
