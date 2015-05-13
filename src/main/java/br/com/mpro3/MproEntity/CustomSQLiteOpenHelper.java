package br.com.mpro3.MproEntity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A helper class to manage database creation and version management.
 */
public class CustomSQLiteOpenHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "MproEntity.db";
    private static final int DATABASE_VERSION = 1;

    public CustomSQLiteOpenHelper ( Context context )
    {
        super (context , DATABASE_NAME, null, DATABASE_VERSION) ;
    }

    @Override
    public void onCreate (SQLiteDatabase database )
    {
    }

    @Override
    public void onUpgrade (SQLiteDatabase db , int oldVersion , int newVersion )
    {
    }
}
