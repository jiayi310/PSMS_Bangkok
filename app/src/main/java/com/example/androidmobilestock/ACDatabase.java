package com.example.androidmobilestock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.example.androidmobilestock.ui.main.Hash;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ACDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 62;

    private static final String DATABASE_NAME = "AutoCountDatabase";

    private static ACDatabase instance;
    private static final String PASS_PHARSE = "123ABC";
    static public synchronized ACDatabase getInstance(Context context){
        if(instance == null)
            instance = new ACDatabase(context);
        return instance;
    }

    private static final String TABLE_NAME_DEBTOR = "Debtor";

    private static final String TABLE_NAME_ITEM = "Item";
    private static final String COL1_ITEM = "ItemCode";
    private static final String COL2_ITEM = "Description";
    private static final String COL3_ITEM = "Desc2";
    private static final String COL4_ITEM = "ItemGroup";
    private static final String COL5_ITEM = "ItemType";
    private static final String COL6_ITEM = "TaxType";
    private static final String COL7_ITEM = "PurchaseTaxType";
    private static final String COL8_ITEM = "BaseUOM";
    private static final String COL9_ITEM = "Price";
    private static final String COL10_ITEM = "Price2";
    private static final String COL11_ITEM = "BarCode";
    private static final String COL12_ITEM = "Image";

    private static final String TABLE_NAME_LOCATION = "Location";
    private static final String COL1_LOC = "Location";
    private static final String COL2_LOC = "Description";

    private static final String COL1_INV = "DocNo";
    private static final String COL2_INV = "CreatedTimeStamp";
    private static final String COL3_INV = "DocDate";
    private static final String COL4_INV = "DebtorCode";
    private static final String COL5_INV = "DebtorName";
    private static final String COL6_INV = "SalesAgent";
    private static final String COL7_INV = "DocType";
    private static final String COL8_INV = "Uploaded";

    // Upload (JOIN with sales)
    private static final String TABLE_NAME_INV_DTL = "SalesDtl";
    private static final String COL1_INV_DTL = "DocNo";
    private static final String COL2_INV_DTL = "Location";
    private static final String COL3_INV_DTL = "ItemCode";
    private static final String COL4_INV_DTL = "ItemDescription";
    private static final String COL5_INV_DTL = "UOM";
    private static final String COL6_INV_DTL = "Qty";
    private static final String COL7_INV_DTL = "UPrice";
    private static final String COL8_INV_DTL = "Discount";
    private static final String COL9_INV_DTL = "SubTotal";
    private static final String COL10_INV_DTL = "TaxType";
    private static final String COL11_INV_DTL = "TaxRate";
    private static final String COL12_INV_DTL = "TaxValue";
    private static final String COL13_INV_DTL = "Totalex";
    private static final String COL14_INV_DTL = "Totalin";

    // Upload
    private static final String TABLE_NAME_PAYMENT = "Payment";
    private static final String COL1_PAYMENT = "DocNo";
    private static final String COL2_PAYMENT = "PaymentTime";
    private static final String COL3_PAYMENT = "PaymentType";
    private static final String COL4_PAYMENT = "PaymentMethod";
    private static final String COL5_PAYMENT = "OriginalAmt";
    private static final String COL6_PAYMENT = "PaymentAmt";
    private static final String COL7_PAYMENT = "CashChanges";
    private static final String COL8_PAYMENT = "CCType";
    private static final String COL9_PAYMENT = "CardNo";
    private static final String COL10_PAYMENT = "CCExpiryDate";
    private static final String COL11_PAYMENT = "CCApprovalCode";
    private static final String COL12_PAYMENT = "ChequeNo";

    private static final String TABLE_NAME_HP = "HistoryPrice";
    private static final String COL1_HIS = "DocType";
    private static final String COL2_HIS = "AccNo";
    private static final String COL3_HIS = "ItemCode";
    private static final String COL4_HIS = "DocNo";
    private static final String COL5_HIS = "DocDate";
    private static final String COL6_HIS = "Location";
    private static final String COL7_HIS = "Agent";
    private static final String COL8_HIS = "Description";
    private static final String COL9_HIS = "Qty";
    private static final String COL10_HIS = "UnitPrice";
    private static final String COL11_HIS = "Discount";
    private static final String COL12_HIS = "SubTotal";

    // Upload
    private static final String TABLE_NAME_SC = "StockCount";
    private static final String COL1_SC = "DocDate";
    private static final String COL2_SC = "ItemCode";
    private static final String COL3_SC = "Description";
    private static final String COL4_SC = "Location";
    private static final String COL5_SC = "UOM";
    private static final String COL6_SC = "Qty";
    private static final String COL7_SC = "Exported";

    private static final String TABLE_NAME_AN = "AutoNumbering";
    private static final String COL2_AN = "Type";
    private static final String COL3_AN = "NextNumber";
    private static final String COL4_AN = "Prefix";
    private static final String COL5_AN = "Length";

    private static final String TABLE_NAME_TT = "TaxType";
    private static final String COL0_TT = "TaxType";
    private static final String COL1_TT = "Description";
    private static final String COL2_TT = "TaxRate";

    private static final String TABLE_NAME_SL = "ServerList";
    private static final String COL1_SL = "AddressURL";
    private static final String COL2_SL = "AddressStr";

    private static final String TABLE_NAME_USERS = "Users";

    private static final String TABLE_NAME_SB = "StockBalance";
    private static final String COL0_SB = "ItemCode";
    private static final String COL1_SB = "UOM";
    private static final String COL2_SB = "Location";
    private static final String COL3_SB = "BalQty";

    private static final String TABLE_NAME_ULDL = "DownloadUpload";
    private static final String COL0_ULDL = "tableName";
    private static final String COL1_ULDL = "rowCount";
    private static final String COL2_ULDL = "lastDate";
    private static final String COL3_ULDL = "type";

    private static final String TABLE_NAME_PEM = "Permission";
    private static final String COL0_PEM = "permissionName";
    private static final String COL1_PEM = "status";

    private static final String TABLE_NAME_Printer = "Printer";
    private static final String TABLE_NAME_SR = "StockReceive";

    public ACDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String TABLE_NAME_ITEM_IMAGE = "ItemImage";


    @Override
    public void onCreate(SQLiteDatabase db) {

        String query_Modules = "CREATE TABLE Modules( Name VARCHAR(100), Activate VARCHAR(100))";
        db.execSQL(query_Modules);

        String query_DEB = "CREATE TABLE " + TABLE_NAME_DEBTOR + "( AccNo VARCHAR(12), " +
                "CompanyName VARCHAR(80), Desc2 VARCHAR(100), Address1 VARCHAR(40), " +
                "Address2 VARCHAR(40), Address3 VARCHAR(40), Address4 VARCHAR(40), " +
                "SalesAgent VARCHAR(12), TaxType VARCHAR(14), Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), EmailAddress VARCHAR(80), DebtorType VARCHAR(20), AreaCode VARCHAR(12), CurrencyCode VARCHAR(5), DisplayTerm VARCHAR(30), Phone2 VARCHAR(25), PriceCategory VARCHAR(30), PRIMARY KEY(AccNo))";
        db.execSQL(query_DEB);

        String query_ITEM = "CREATE TABLE " + TABLE_NAME_ITEM + "( ItemCode VARCHAR(30), " +
                "Description VARCHAR(100), Desc2 VARCHAR(100), ItemGroup VARCHAR(12), " +
                "ItemType VARCHAR(12), TaxType VARCHAR(14), PurchaseTaxType VARCHAR(14), " +
                "BaseUOM VARCHAR(8),SalesUOM VARCHAR(8),PurchaseUOM VARCHAR(8), Price DECIMAL(25, 8), Price2 DECIMAL(25, 8), " +
                "BarCode VARCHAR(30), Image VARCHAR(4000),ItemCode2 VARCHAR(30), PRIMARY KEY(ItemCode))";
        db.execSQL(query_ITEM);

        String query_LOC = "CREATE TABLE " + TABLE_NAME_LOCATION + "( Location VARCHAR(12), " +
                "Description VARCHAR(80), PRIMARY KEY(Location)) ";
        db.execSQL(query_LOC);

        String query_UOM = "CREATE TABLE ItemUOM ( ItemCode VARCHAR(30), " +
                "UOM VARCHAR(8), Price DECIMAL(25,8), Price2 DECIMAL(25,8), Price3 DECIMAL(25,8), Price4 DECIMAL(25,8), Price5 DECIMAL(25,8), Price6 DECIMAL(25,8), Shelf VARCHAR(20)," +
                "BarCode VARCHAR(30), BalQty DECIMAL(25,8), Rate DECIMAL(25,8), MinPrice DECIMAL(25,8), MaxPrice DECIMAL(25,8))";
        db.execSQL(query_UOM);

        String query_SA = "CREATE TABLE Sales_Agent ( SalesAgent VARCHAR(12), " +
                "Description VARCHAR(40), PRIMARY KEY(SalesAgent)) ";
        db.execSQL(query_SA);

        String query_SC = "CREATE TABLE " + TABLE_NAME_SC + "( DocDate TEXT, ItemCode VARCHAR(30), " +
                "Description VARCHAR(80), Location VARCHAR(12), UOM VARCHAR(8), Qty DECIMAL(25,8), " +
                "Exported INTEGER DEFAULT 0)";
        db.execSQL(query_SC);

        String query_ST = "CREATE TABLE StockTake ( ID VARCHAR(30), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "DocDate TEXT, Location VARCHAR(12), SalesAgent VARCHAR(12), Remarks VARCHAR(80)," +
                "Exported INTEGER DEFAULT 0, LastModifiedDateTime DATETIME, CreatedUser VARCHAR(20), LastModifiedUser VARCHAR(20))";
        db.execSQL(query_ST);

        String query_STD = "CREATE TABLE StockTakeDetail ( ItemCode VARCHAR(30), " +
                "Description VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), " +
                "StockDocNo INTEGER(30),CreatedTimeStamp TEXT , LineNo INTEGER, BatchNo VARCHAR(20))";
        db.execSQL(query_STD);

        String query_INV = "CREATE TABLE Sales (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " DocNo VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "DocDate TEXT, DebtorCode VARCHAR(12), DebtorName VARCHAR(80), Address1 VARCHAR(40), Address2 VARCHAR(40), Address3 VARCHAR(40), Address4 VARCHAR(40)," +
                "SalesAgent VARCHAR(12), DocType TEXT, Uploaded INTEGER DEFAULT 0 NOT NULL, " +
                "Signature VARCHAR(3000), Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), " +
                "Remarks VARCHAR(250),Remarks2 VARCHAR(250),Remarks3 VARCHAR(250),Remarks4 VARCHAR(250), Status VARCHAR(20), CreatedUser VARCHAR(20),LastModifiedDateTime DATETIME, LastModifiedUser VARCHAR(20))";
        db.execSQL(query_INV);

        String query_INV_DTL = "CREATE TABLE SalesDtl" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + " DocNo VARCHAR(20), " +
                "Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), " +
                "UOM VARCHAR(8), Qty DECIMAL(25,8), UPrice DECIMAL(25,8), Discount DECIMAL(25,8), " +
                "SubTotal DECIMAL(25,8), TaxType VARCHAR(14), TaxRate DECIMAL(25,8), " +
                "TaxValue DECIMAL(25, 8), Totalex DECIMAL(19,2), TotalIn DECIMAL(19,2), LineNo INTEGER, Remarks VARCHAR(250), Remarks2 VARCHAR(250), BatchNo VARCHAR(20), DiscountText VARCHAR(100))";
        db.execSQL(query_INV_DTL);

        String query_PAY = "CREATE TABLE " + TABLE_NAME_PAYMENT +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + " DocNo VARCHAR(20), PaymentTime TEXT, " +
                "PaymentType VARCHAR(50), PaymentMethod VARCHAR(50), OriginalAmt DECIMAL(19,2), " +
                "PaymentAmt DECIMAL(19,2), CashChanges DECIMAL(25,8), CCType VARCHAR(10), " +
                "CardNo VARCHAR(20), CCExpiryDate VARCHAR(6), CCApprovalCode VARCHAR(10), " +
                "ChequeNo VARCHAR(20), Uploaded INTEGER DEFAULT 0 NOT NULL)";
        db.execSQL(query_PAY);

        String query_TR = "CREATE TABLE TRANSFER (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, DocDate TEXT, Reason VARCHAR(12), LocationFrom VARCHAR(8), LocationTo VARCHAR(8), CreatedUser VARCHAR(8), Uploaded INTEGER DEFAULT 0 NOT NULL, " +
                "Remarks VARCHAR(250),LastModifiedDateTime DATETIME, LastModifiedUser VARCHAR(20))";
        db.execSQL(query_TR);

        String query_TR_DTL = "CREATE TABLE TRANSFERDTL (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + " DocNo VARCHAR(20), ItemCode VARCHAR(30), Description VARCHAR(100), UOM VARCHAR(8), Qty DECIMAL(25,8), LineNo INTEGER, Remarks VARCHAR(250), BatchNo VARCHAR(20))";
        db.execSQL(query_TR_DTL);

        String query_HP = "CREATE TABLE " + TABLE_NAME_HP + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " DocType VARCHAR(20), AccNo VARCHAR(12), ItemCode VARCHAR(30), " +
                "Description VARCHAR(100), DocNo VARCHAR(20), DocDate TEXT, Location VARCHAR(12), " +
                "Agent VARCHAR(12), Qty DECIMAL(25,8), UOM VARCHAR(8), UnitPrice DECIMAL(25,8), " +
                "Discount DECIMAL(25,8), SubTotal DECIMAL(25,8))";
        db.execSQL(query_HP);

        String query_AN = "CREATE TABLE " + TABLE_NAME_AN + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " Name VARCHAR(20), Type VARCHAR(2), NextNumber INTEGER, Prefix VARCHAR(12), Length INTEGER)";
        db.execSQL(query_AN);
        db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) " +
                "VALUES ('Sales', 'S', 1, 'MIN-', 5 )");
        db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) " +
                "VALUES ('Transfer', 'T', 1, 'MTR-', 5 )");
        db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) " +
                "VALUES ('Purchase', 'P', 1, 'MGR-', 5 )");
        db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) " +
                "VALUES ('StockTake', 'ST', 1, 'MST-', 5 )");
        db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) " +
                "VALUES ('PackingList', 'PL', 1, 'MPL-', 5 )");

        String query_TT = "CREATE TABLE " + TABLE_NAME_TT + "( TaxType VARCHAR(14), " +
                "Description VARCHAR(120), TaxRate DECIMAL (18, 6), PRIMARY KEY(TaxType)) ";
        db.execSQL(query_TT);

        String query_SL = "CREATE TABLE " + TABLE_NAME_SL +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + " AddressURL TEXT, AddressStr TEXT)";
        db.execSQL(query_SL);

        String query_USERS = "CREATE TABLE " + TABLE_NAME_USERS + "( Username VARCHAR(10), Pwd VARCHAR(100), EnableSetting INTEGER DEFAULT 0 NOT NULL, FilterByAgent INTEGER DEFAULT 0 NOT NULL, StockTake INTEGER DEFAULT 0 NOT NULL, StockInquiry INTEGER DEFAULT 0 NOT NULL, Sales INTEGER DEFAULT 0 NOT NULL, Purchase INTEGER DEFAULT 0 NOT NULL, Transfer INTEGER DEFAULT 0 NOT NULL, PackingList INTEGER DEFAULT 0 NOT NULL, Catalog INTEGER DEFAULT 0 NOT NULL, Collection INTEGER DEFAULT 0 NOT NULL, StockAssembly INTEGER DEFAULT 0 NOT NULL, Analytics INTEGER DEFAULT 0 NOT NULL, SellingPrice INTEGER DEFAULT 0 NOT NULL , Cost INTEGER DEFAULT 0 NOT NULL , PurchasePackingList INTEGER DEFAULT 0 NOT NULL, JobSheet INTEGER DEFAULT 0 NOT NULL, StockReceive INTEGER DEFAULT 0 NOT NULL)";

        //String query_USERS = "CREATE TABLE " + TABLE_NAME_USERS + "( Username VARCHAR(10), Pwd VARCHAR(100), EnableSetting INTEGER DEFAULT 0 NOT NULL, FilterByAgent INTEGER DEFAULT 0 NOT NULL, StockTake INTEGER DEFAULT 0 NOT NULL, StockInquiry INTEGER DEFAULT 0 NOT NULL, Sales INTEGER DEFAULT 0 NOT NULL, Purchase INTEGER DEFAULT 0 NOT NULL, Transfer INTEGER DEFAULT 0 NOT NULL, PackingList INTEGER DEFAULT 0 NOT NULL, Catalog INTEGER DEFAULT 0 NOT NULL, Collection INTEGER DEFAULT 0 NOT NULL, StockAssembly INTEGER DEFAULT 0 NOT NULL, Analytics INTEGER DEFAULT 0 NOT NULL, SellingPrice INTEGER DEFAULT 0 NOT NULL , Cost INTEGER DEFAULT 0 NOT NULL , PurchasePackingList INTEGER DEFAULT 0 NOT NULL, Jobsheet INTEGER DEFAULT 1 NOT NULL, StockReceive INTEGER DEFAULT 1 NOT NULL)";
        db.execSQL(query_USERS);

        String query_REG = "CREATE TABLE Reg (ID INTEGER PRIMARY KEY, " + " Value VARCHAR(4000))";
        db.execSQL(query_REG);

        String query_SB = "CREATE TABLE " + TABLE_NAME_SB + "(ItemCode VARCHAR(30), UOM VARCHAR(8), " +
                "Location VARCHAR(12), BalQty DECIMAL(25,8))";
        db.execSQL(query_SB);

        String query_ULDL = "CREATE TABLE " + TABLE_NAME_ULDL +
                "(" + COL0_ULDL + " VARCHAR(12), " +
                COL1_ULDL + " DECIMAL(19,2), " +
                COL2_ULDL + " VARCHAR(12), " +
                COL3_ULDL + " VARCHAR(12))";
        db.execSQL(query_ULDL);

        String query_PEM = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PEM + "(" + COL0_PEM + " VARCHAR(200), " +
                COL1_PEM + " VARCHAR(20)) ";
        db.execSQL(query_PEM);

        String query1 = "INSERT INTO Permission (permissionName, status) VALUES ('RFID Permission','Denied')";
        db.execSQL(query1);

        //Version No.
        String query_1 = "INSERT INTO REG (ID, Value) VALUES ('1', '3.1.6')";
        db.execSQL(query_1);

        //URL
        String query_2 = "INSERT INTO REG (ID, Value) VALUES ('2', 'URL')";
        db.execSQL(query_2);

        //URLStr
        String query_3 = "INSERT INTO REG (ID, Value) VALUES ('3','')";
        db.execSQL(query_3);

        //Username
        String query_4 = "INSERT INTO REG (ID, Value) VALUES ('4', 'ADMIN')";
        db.execSQL(query_4);

        //PW
        String query_5 = "INSERT INTO REG (ID, Value) VALUES ('5','')";
        db.execSQL(query_5);

        //Currency
        String query_6 = "INSERT INTO REG (ID, Value) VALUES ('6','RM')";
        db.execSQL(query_6);

        //Location
        String query_7 = "INSERT INTO REG (ID, Value) VALUES ('7','HQ')";
        db.execSQL(query_7);

        //UUID
        String query_8 = "INSERT INTO REG (ID, Value) VALUES ('8', NULL)";
        db.execSQL(query_8);

        String query_9 = "INSERT INTO REG (ID, Value) VALUES ('9','1')";
        db.execSQL(query_9);

        String query_10 = "INSERT INTO REG (ID, Value) VALUES ('10','1')";
        db.execSQL(query_10);

        String query_11 = "INSERT INTO REG (ID, Value) VALUES ('11','0')";
        db.execSQL(query_11);

        String query_12 = "INSERT INTO REG (ID, Value) VALUES ('12','1')";
        db.execSQL(query_12);

        String query_13 = "INSERT INTO REG (ID, Value) VALUES ('13','0')";
        db.execSQL(query_13);

        String query_14 = "INSERT INTO REG (ID, Value) VALUES ('14','0')";
        db.execSQL(query_14);

        //PrinterName
        String query_15 = "INSERT INTO REG (ID, Value) VALUES ('15','None')";
        db.execSQL(query_15);

        String query_16 = "INSERT INTO REG (ID, Value) VALUES ('16','CompanyHeader')";
        db.execSQL(query_16);

        String query_17 = "INSERT INTO REG (ID, Value) VALUES ('17','None')";
        db.execSQL(query_17);

        String query_18 = "INSERT INTO REG (ID, Value) VALUES ('18','None')";
        db.execSQL(query_18);

        //Packing List Module
        String query_19 = "INSERT INTO REG (ID, Value) VALUES ('19','0')";
        db.execSQL(query_19);

        //AutoPrice Enabled
        String query_20 = "INSERT INTO REG (ID, Value) VALUES ('20','0')";
        db.execSQL(query_20);

        //Default TaxType
        String query_21 = "INSERT INTO REG (ID, Value) VALUES ('21','None')";
        db.execSQL(query_21);

        //Tax Enabled
        String query_22 = "INSERT INTO REG (ID, Value) VALUES ('22','0')";
        db.execSQL(query_22);

        //Default Creditor
        String query_23 = "INSERT INTO REG (ID, Value) VALUES ('23','None')";
        db.execSQL(query_23);

        //Default Purchase Agent
        String query_24 = "INSERT INTO REG (ID, Value) VALUES ('24','None')";
        db.execSQL(query_24);

        //Enable Custom Barcode
        String query_25 = "INSERT INTO REG (ID, Value) VALUES ('25','0')";
        db.execSQL(query_25);

        //Barcode Length
        String query_26 = "INSERT INTO REG (ID, Value) VALUES ('26','0')";
        db.execSQL(query_26);

        //Barcode Item Start
        String query_27 = "INSERT INTO REG (ID, Value) VALUES ('27','0')";
        db.execSQL(query_27);

        //Barcode Item Length
        String query_28 = "INSERT INTO REG (ID, Value) VALUES ('28','0')";
        db.execSQL(query_28);

        //Barcode Qty Start
        String query_29 = "INSERT INTO REG (ID, Value) VALUES ('29','0')";
        db.execSQL(query_29);

        //Barcode Qty Length
        String query_30 = "INSERT INTO REG (ID, Value) VALUES ('30','0')";
        db.execSQL(query_30);

        //Barcode Qty Decimal
        String query_31 = "INSERT INTO REG (ID, Value) VALUES ('31','0')";
        db.execSQL(query_31);

        //DO Table
        String query_DO = "CREATE TABLE DO (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), FromDocNo VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, DocDate TEXT, DebtorCode VARCHAR(12), DebtorName VARCHAR(80), SalesAgent VARCHAR(12), CreatedUser VARCHAR(8), " +
                "Uploaded INTEGER DEFAULT 0 NOT NULL, Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), Remarks VARCHAR(250), DocType VARCHAR(20), IsTally Boolean, LastModifiedDateTime DATETIME, LastModifiedUser VARCHAR(20))";
        db.execSQL(query_DO);

        //DODTL Table
        String query_DODTL = "CREATE TABLE DODTL (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), LineNo INTEGER, DtlKey INTEGER, Remarks VARCHAR(250), BatchNo VARCHAR(20))";
        db.execSQL(query_DODTL);

        String query_Printer = "CREATE TABLE IF NOT EXISTS Printer (ID INTEGER PRIMARY KEY,  " + " PrinterName VARCHAR(30))";
        db.execSQL(query_Printer);

        String query_p1 = "INSERT INTO Printer (ID, PrinterName) VALUES ('1', 'PTP-III')";
        db.execSQL(query_p1);

        String query_p2 = "INSERT INTO Printer (ID, PrinterName) VALUES ('2', 'Feasycom')";
        db.execSQL(query_p2);

        String query_p3 = "INSERT INTO Printer (ID, PrinterName) VALUES ('3', 'BluetoothPrinter')";
        db.execSQL(query_p3);

        //create SO
        db.execSQL("CREATE TABLE SO (ID INTEGER PRIMARY KEY , DocNo VARCHAR(20), DocDate TEXT, DebtorCode VARCHAR(12), DebtorName VARCHAR(80), SalesAgent VARCHAR(12), Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), LineNo INTEGER, BatchNo VARCHAR(20), Remarks VARCHAR(250), RemarksDtl VARCHAR(250), DocType VARCHAR(20))");

        //create PurchaseAgent
        db.execSQL("CREATE TABLE PurchaseAgent (PurchaseAgent VARCHAR(12), Description VARCHAR(40), PRIMARY KEY(PurchaseAgent)) ");

        //Create Creditor
        db.execSQL("CREATE TABLE Creditor ( AccNo VARCHAR(12), CompanyName VARCHAR(80), Desc2 VARCHAR(100), Address1 VARCHAR(40), Address2 VARCHAR(40), Address3 VARCHAR(40), Address4 VARCHAR(40), PurchaseAgent VARCHAR(12), TaxType VARCHAR(14), Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), EmailAddress VARCHAR(80), CreditorType VARCHAR(20), AreaCode VARCHAR(12), CurrencyCode VARCHAR(5), DisplayTerm VARCHAR(30), Phone2 VARCHAR(25), PRIMARY KEY(AccNo))");

        //create Purchase
        db.execSQL("CREATE TABLE Purchase (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, DocDate TEXT, CreditorCode VARCHAR(12), CreditorName VARCHAR(80), PurchaseAgent VARCHAR(12), Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), Uploaded INTEGER DEFAULT 0 NOT NULL, Remarks VARCHAR(250), LastModifiedDateTime DATETIME, CreatedUser VARCHAR(20), Remarks2 VARCHAR(250), Remarks3 VARCHAR(250), Remarks4 VARCHAR(250), LastModifiedUser VARCHAR(20))");

        //Create PurchaseDtl
        db.execSQL("CREATE TABLE PurchaseDtl (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), UPrice DECIMAL(25,8), Discount DECIMAL(25,8), SubTotal DECIMAL(25,8), TaxType VARCHAR(14), TaxRate DECIMAL(25,8), TaxValue DECIMAL(25, 8), Totalex DECIMAL(19,2), TotalIn DECIMAL(19,2), LineNo INTEGER, Remarks VARCHAR(250), Remarks2 VARCHAR(250), BatchNo VARCHAR(20))");

        //Hybrid Mode Enabled
        String query_32 = "INSERT INTO REG (ID, Value) VALUES ('32','0')";
        db.execSQL(query_32);

        //History Day
        String query_33 = "INSERT INTO REG (ID, Value) VALUES ('33','30')";
        db.execSQL(query_33);

        //AddPrinter
        String query_p4 = "INSERT INTO Printer (ID, PrinterName) VALUES ('4', 'Printer001')";
        db.execSQL(query_p4);

        //Create SalesFormat
        db.execSQL("CREATE TABLE Report (ID INTEGER PRIMARY KEY AUTOINCREMENT, ReportType VARCHAR(2), ReportName VARCHAR(100))");

        //SalesFormat 01
        db.execSQL("INSERT INTO Report (ReportType, ReportName) VALUES ('S', 'Invoice')");
        db.execSQL("INSERT INTO Report (ReportType, ReportName) VALUES ('S', 'Tax Invoice')");
        db.execSQL("INSERT INTO Report (ReportType, ReportName) VALUES ('S', 'Invoice Long')");
        db.execSQL("INSERT INTO Report (ReportType, ReportName) VALUES ('S', 'Tax Invoice Long')");

        //Default SalesFormat
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('34', 'Invoice')");

        //Logo
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('35','0')");

        //Catalog
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('36','0')");

        //ItemImage Table
        String query_ItemImage = "CREATE TABLE ItemImage ( ItemCode VARCHAR(30), " +
                "Image VARCHAR(4000))";
        db.execSQL(query_ItemImage);

        //DefaultBatchNoType
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('37','')");

        //BatchNoEnabled
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('38','0')");

        //Batch No Table
        db.execSQL("CREATE TABLE  ItemBatch " + "( ItemCode VARCHAR(30), " +
                "BatchNo VARCHAR(20), Description VARCHAR(100), ManufacturedDate Date, " +
                "ExpiryDate Date)");

        db.execSQL("ALTER TABLE StockBalance ADD COLUMN BatchNo VARCHAR(20);");
        db.execSQL("ALTER TABLE Item ADD COLUMN HasBatchNo VARCHAR(20)");

        //AutoSalesBatchEnable
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('39','0')");
        //AutoPurchaseBatchEnable
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('40','0')");
        //BatchSalesOrderType
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('41','0')");
        //NegativeInventory
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('42','true')");
        //OnlyTallyUploaded
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('43','0')");

        //Batch Comparison
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('44','0')");

        //AR Payment Table
        db.execSQL("CREATE TABLE ARPayment " + "(DocNo VARCHAR(12),Date Date, DebtorCode VARCHAR(12),DebtorName VARCHAR(50)," +
                "Amount DECIMAL(19,2), Image VARCHAR(4000),Uploaded INTEGER DEFAULT 0 NOT NULL, CreatedTimeStamp TEXT, CreatedUser VARCHAR(8), Remark VARCHAR(20),LastModifiedDateTime DATETIME, LastModifiedUser VARCHAR(20))");

        //AR PaymentDtl Table
        db.execSQL("CREATE TABLE ARPaymentDtl " + "(DocNo VARCHAR(12), DocNumber VARCHAR(20), DocDate Date, OrgAmt DECIMAL(19,2),  " +
                "PaymentAmount DECIMAL(19,2))");

        //db.execSQL("ALTER TABLE Payment ADD COLUMN Image VARCHAR(4000);");
        db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) SELECT 'ARPayment', 'AR', 1, 'MAR-', 5");

        //ItemBOM Table
        db.execSQL("CREATE TABLE  ItemBOM " + "( ItemCode VARCHAR(30), " +
                "SubItemCode VARCHAR(20), Qty DECIMAL(25,8))");

        //Analytics
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('45','0')");

        //ARPayment
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('46','0')");

        //StockAssembly
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('47','0')");

        //Stock Assembly Table
        db.execSQL("CREATE TABLE StockAssembly " + "(DocNo VARCHAR(12), Date Date, Description VARCHAR(50),FGItemCode VARCHAR(12)," +
                "FGQty DECIMAL(19,2), FGLocation VARCHAR(12), FGBatchNo VARCHAR(12), Remark VARCHAR(20), Uploaded INTEGER DEFAULT 0 NOT NULL, CreatedTimeStamp TEXT, CreatedUser VARCHAR(8), LastModifiedDateTime DATETIME, LastModifiedUser VARCHAR(20))");

        //Stock AssemblyDtl Table
        db.execSQL("CREATE TABLE StockAssemblyDtl " + "(DocNo VARCHAR(12), SubItemCode VARCHAR(20)," +
                "Qty DECIMAL(19,2))");

        //db.execSQL("ALTER TABLE Payment ADD COLUMN Image VARCHAR(4000);");
        db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) SELECT 'StockAssembly', 'SA', 1, 'MSA-', 5");

        //SellingPrice
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('48','0')");

        //LocationComparison
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('49','0')");

        //create PO
        db.execSQL("CREATE TABLE PO (ID INTEGER PRIMARY KEY , DocNo VARCHAR(20), DocDate TEXT, CreditorCode VARCHAR(12), CreditorName VARCHAR(80), PurchaseAgent VARCHAR(12), Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), LineNo INTEGER, BatchNo VARCHAR(20), Remarks VARCHAR(250), RemarksDtl VARCHAR(250), DocType VARCHAR(20))");

        //PI Table
        String query_PI = "CREATE TABLE PI (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), FromDocNo VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, DocDate TEXT, CreditorCode VARCHAR(12), CreditorName VARCHAR(80), PurchaseAgent VARCHAR(12), CreatedUser VARCHAR(8), Uploaded INTEGER DEFAULT 0 NOT NULL, Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), Remarks VARCHAR(250), DocType VARCHAR(20), IsTally Boolean, LastModifiedDateTime DATETIME, LastModifiedUser VARCHAR(20))";
        db.execSQL(query_PI);

        //PIDTL Table
        String query_PIDTL = "CREATE TABLE PIDTL (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), LineNo INTEGER, DtlKey INTEGER, Remarks VARCHAR(250), BatchNo VARCHAR(20))";
        db.execSQL(query_PIDTL);

//        //Create User - Purchase Packing List
//        String query_editUser2 = "ALTER TABLE Users ADD PurchasePackingList INTEGER DEFAULT 0 NOT NULL";
//        db.execSQL(query_editUser2);

        //Create AN - Purchase Packing List
        db.execSQL("INSERT INTO AutoNumbering ( Name, Type, NextNumber, Prefix, Length) SELECT 'PurchasePackingList', 'PPL', 1, 'MPPL-', 5 WHERE NOT EXISTS (SELECT * FROM AutoNumbering WHERE Name = 'PurchasePackingList')");

        //Purchase PL
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('50','0')");
        //Barcode DtlRemark Start
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('51','0')");
        //Barcode DtlRemark Length
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('52','0')");
        //Barcode DtlRemark2 Start
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('53','0')");
        //Barcode DtlRemark2 Length
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('54','0')");
        //Terminal Device
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('55','new')");
        //Agent List
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('56','0')");
        //EnableCollectionDetails
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('57','0')");
        //FilterbyItemGroup
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('58','0')");
        //ItemGroupList
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('59','0')");
        //FilterbyItemType
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('60','0')");
        //ItemTypeList
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('61','0')");
        //StockInquiry
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('62','0')");
        //CollectionReceiptFormat
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('63','The official receipt is onliy valid upon the transaction been shown as cleared in our bank account.'|| char(10) ||'NB'|| char(10) ||'Validity of This Receipt'|| char(10) ||'Subject to Clearing of Cheque')");
        //SalesReceiptFormat
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('64','1. All cheques should be crossed and made payable to '|| char(10) || 'PRESOFT (M) SDN BHD '|| char(10) ||'2. Goods sold are neither returnable nor refundable. Otherwise '|| char(10) || 'a cancellation fee of 20% on purchase price will be imposed.')");

        //PO -reDownload
        db.execSQL("ALTER TABLE PO ADD COLUMN reDownload INTEGER DEFAULT 0 NOT NULL");

        //AllowEditPO
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('65','0')");

        db.execSQL("ALTER TABLE PO ADD COLUMN PurchaseLocation VARCHAR(20);");

        //OnlyTallyUploadedPR
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('66','0')");


        //UTD Cost
        db.execSQL("CREATE TABLE  UTDCost " + "( ItemCode VARCHAR(30), " +
                "BatchNo VARCHAR(20), UOM VARCHAR(8), Location VARCHAR(12), " +
                "UTDQty DECIMAL(25,8), UTDCost DECIMAL(25,8))");

        //Cost Permission
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('69','0')");

        //Job Sheet
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('70','0')");

        //Stock Receive
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('71','0')");

        //Minimum price exception
        db.execSQL("INSERT INTO REG (ID, Value) VALUES ('73','1')");

        db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) SELECT 'JobSheet', 'JS', 1, 'MJS-', 5");

        db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) SELECT 'StockReceive', 'SR', 1, 'MSR-', 5");

        //Stock Receive
        db.execSQL("CREATE TABLE StockReceive (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), DocType VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, CreatedUser VARCHAR(20), DocDate TEXT, Uploaded INTEGER DEFAULT 0 NOT NULL, Remarks VARCHAR(250), Description VARCHAR(250), TaxType VARCHAR(3000), LastModifiedDateTime DATETIME, LastModifiedUser VARCHAR(20))");

        //Stock Receive Detail
        db.execSQL("CREATE TABLE StockReceiveDtl (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), DocDate TEXT, Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), UTD_Cost DECIMAL(25,8), SubTotal DECIMAL(25,8), BatchNo VARCHAR(20), Remarks VARCHAR(250), Remarks2 VARCHAR(250), LineNo INTEGER)");

        //Job Sheet
        String query_JS = "CREATE TABLE JobSheet (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DocNo VARCHAR(20), DocDate TEXT, DocType VARCHAR(20), DebtorCode VARCHAR(12), DebtorName VARCHAR(80), DebtorName2 VARCHAR(50), Address1 VARCHAR(40), Address2 VARCHAR(40), Address3 VARCHAR(40), Address4 VARCHAR(40)," +
                "SalesAgent VARCHAR(12), Uploaded INTEGER DEFAULT 0 NOT NULL, TaxType VARCHAR(3000), " +
                "Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), " +
                "Remarks VARCHAR(250),Remarks2 VARCHAR(250),Remarks3 VARCHAR(250),Remarks4 VARCHAR(250), Status VARCHAR(20), " +
                "WorkType VARCHAR(250), ReplacementType VARCHAR(250), TimeIn TEXT, TimeOut TEXT, Problem VARCHAR(250), Resolution VARCHAR(250), JobSheetRemarks VARCHAR(250), " +
                "SalesNo TEXT, TaxValue DECIMAL(25,8), TotalEx DECIMAL(19,2), TotalIn DECIMAL(19,2), Discount DECIMAL(25,8), " +
                "CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, CreatedUser VARCHAR(20), LastModifiedDateTime DATETIME, LastModifiedUser VARCHAR(20), Signature VARCHAR(3000), Image VARCHAR(4000))";
        db.execSQL(query_JS);

        //Job Sheet Details
        db.execSQL("CREATE TABLE IF NOT EXISTS JobSheetDetails (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "DocNo VARCHAR(20)," +
                "DocDate TEXT," +
                "Location VARCHAR(12)," +
                "ItemCode VARCHAR(30)," +
                "ItemDescription VARCHAR(80)," +
                "UOM VARCHAR(8)," +
                "Quantity DECIMAL(25,8)," +
                "UPrice DECIMAL(25,8)," +
                "Discount DECIMAL(25,8)," +
                "SubTotal DECIMAL(25,8)," +
                "TaxType VARCHAR(14)," +
                "TaxRate DECIMAL(25,8)," +
                "TaxValue DECIMAL(25,8)," +
                "TotalEx DECIMAL(19,2)," +
                "TotalIn DECIMAL(19,2)," +
                "LineNo INTEGER," +
                "Remarks VARCHAR(250)," +
                "Remarks2 VARCHAR(250)," +
                "BatchNo VARCHAR(20)" +
        ");");

        db.execSQL("ALTER TABLE Debtor ADD COLUMN DetailDiscount VARCHAR(100);");

        db.execSQL("CREATE TABLE CreditTermMaintenance ( ID INTEGER PRIMARY KEY AUTOINCREMENT, DisplayTerm VARCHAR(80), Terms VARCHAR(80), TermType VARCHAR(80), TermDays VARCHAR(80), DiscountDays VARCHAR(80), DiscountPercent VARCHAR(80) ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE TRANSFER (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, DocDate TEXT, Reason VARCHAR(12), LocationFrom VARCHAR(8), LocationTo VARCHAR(8), CreatedUser VARCHAR(8), Uploaded INTEGER DEFAULT 0 NOT NULL)");

            db.execSQL("CREATE TABLE TRANSFERDTL (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + " DocNo VARCHAR(20), ItemCode VARCHAR(30), Description VARCHAR(100), UOM VARCHAR(8), Qty DECIMAL(25,8), LineNo VARCHAR (20))");

            db.execSQL("INSERT INTO AutoNumbering ( Name, Type, NextNumber, Prefix, Length) SELECT 'Transfer', 'T', 1, 'TR-', 5 WHERE NOT EXISTS (SELECT * FROM AutoNumbering WHERE Name = 'Transfer')");

            db.execSQL("INSERT INTO AutoNumbering ( Name, Type, NextNumber, Prefix, Length) SELECT 'Purchase', 'P', 1, 'GR-', 5 WHERE NOT EXISTS (SELECT * FROM AutoNumbering WHERE Name = 'Purchase')");
        }

        if (oldVersion < 4) {
            String query_PEM = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PEM + "(" + COL0_PEM + " VARCHAR(200), " +
                    COL1_PEM + " VARCHAR(20)) ";
            db.execSQL(query_PEM);

            String query1 = "INSERT INTO Permission (permissionName, status) VALUES ('RFID Permission','Denied')";
            db.execSQL(query1);


            String query_ST = "CREATE TABLE IF NOT EXISTS StockTake ( ID VARCHAR(30), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, " +
                    "DocDate TEXT, Location VARCHAR(12), SalesAgent VARCHAR(12), Remarks VARCHAR(80)," +
                    "Exported INTEGER DEFAULT 0)";
            db.execSQL(query_ST);

            String query_STD = "CREATE TABLE IF NOT EXISTS StockTakeDetail ( ItemCode VARCHAR(30), " +
                    "Description VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), " +
                    "StockDocNo INTEGER(30),CreatedTimeStamp TEXT , LineNo VARCHAR (20))";
            db.execSQL(query_STD);

            db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) SELECT 'StockTake', 'ST', 1, 'ST-', 5 WHERE NOT EXISTS (SELECT * FROM AutoNumbering WHERE Name = 'Transfer')");

            String query_editItemUOM = "ALTER TABLE ItemUOM ADD Rate DECIMAL(25,8)";
            db.execSQL(query_editItemUOM);
        }

        if (oldVersion < 5) {
            String query_REG = "CREATE TABLE IF NOT EXISTS Reg (ID INTEGER PRIMARY KEY,  " + " Value VARCHAR(4000))";
            db.execSQL(query_REG);

            String query_editItemUOM2 = "ALTER TABLE ItemUOM ADD MinPrice DECIMAL(25,8)";
            db.execSQL(query_editItemUOM2);

            String query_editItemUOM3 = "ALTER TABLE ItemUOM ADD MaxPrice DECIMAL(25,8)";
            db.execSQL(query_editItemUOM3);

            String query_editItemUOM4 = "ALTER TABLE ItemUOM ADD Price3 DECIMAL(25,8)";
            db.execSQL(query_editItemUOM4);

            String query_editItemUOM5 = "ALTER TABLE ItemUOM ADD Price4 DECIMAL(25,8)";
            db.execSQL(query_editItemUOM5);

            String query_editItemUOM6 = "ALTER TABLE ItemUOM ADD Price5 DECIMAL(25,8)";
            db.execSQL(query_editItemUOM6);

            String query_editItemUOM7 = "ALTER TABLE ItemUOM ADD Price6 DECIMAL(25,8)";
            db.execSQL(query_editItemUOM7);

            String query_editItem = "ALTER TABLE Item ADD ItemCode2 Varchar(30)";
            db.execSQL(query_editItem);

            String query_editDebtor = "ALTER TABLE Debtor ADD Phone2 VARCHAR(25)";
            db.execSQL(query_editDebtor);

            String query_editDebtor2 = "ALTER TABLE Debtor ADD PriceCategory VARCHAR(30)";
            db.execSQL(query_editDebtor2);

            String query_editINVDtl = "ALTER TABLE SalesDtl ADD LineNo INTEGER";
            db.execSQL(query_editINVDtl);

            String query_editTRANSDTL = "ALTER TABLE TRANSFERDTL ADD LineNo INTEGER";
            db.execSQL(query_editTRANSDTL);

            String query_editStockDTL = "ALTER TABLE StockTakeDetail ADD LineNo INTEGER";
            db.execSQL(query_editStockDTL);

            String query_1 = "INSERT INTO REG (ID, Value) VALUES ('1', '3.1.6')";
            db.execSQL(query_1);

            String query_2 = "INSERT INTO REG (ID, Value) VALUES ('2', 'URL')";
            db.execSQL(query_2);

            String query_3 = "INSERT INTO REG (ID, Value) VALUES ('3','')";
            db.execSQL(query_3);

            String query_4 = "INSERT INTO REG (ID, Value) VALUES ('4', 'ADMIN')";
            db.execSQL(query_4);

            String query_5 = "INSERT INTO REG (ID, Value) VALUES ('5','')";
            db.execSQL(query_5);

            String query_6 = "INSERT INTO REG (ID, Value) VALUES ('6','RM')";
            db.execSQL(query_6);

            String query_7 = "INSERT INTO REG (ID, Value) VALUES ('7','')";
            db.execSQL(query_7);

            String query_8 = "INSERT INTO REG (ID, Value) VALUES ('8', NULL)";
            db.execSQL(query_8);

            String query_9 = "INSERT INTO REG (ID, Value) VALUES ('9','1')";
            db.execSQL(query_9);

            String query_10 = "INSERT INTO REG (ID, Value) VALUES ('10','1')";
            db.execSQL(query_10);

            String query_11 = "INSERT INTO REG (ID, Value) VALUES ('11','0')";
            db.execSQL(query_11);

            String query_12 = "INSERT INTO REG (ID, Value) VALUES ('12','1')";
            db.execSQL(query_12);

            String query_13 = "INSERT INTO REG (ID, Value) VALUES ('13','0')";
            db.execSQL(query_13);

            String query_14 = "INSERT INTO REG (ID, Value) VALUES ('14','0')";
            db.execSQL(query_14);

            String query_15 = "INSERT INTO REG (ID, Value) VALUES ('15','PrinterName')";
            db.execSQL(query_15);

            String query_16 = "INSERT INTO REG (ID, Value) VALUES ('16','CompanyHeader')";
            db.execSQL(query_16);

            String query_17 = "INSERT INTO REG (ID, Value) VALUES ('17', 'None')";
            db.execSQL(query_17);

            String query_18 = "INSERT INTO REG (ID, Value) VALUES ('18', 'None')";
            db.execSQL(query_18);

            String query_Printer = "CREATE TABLE IF NOT EXISTS Printer (ID INTEGER PRIMARY KEY,  " + " PrinterName VARCHAR(30))";
            db.execSQL(query_Printer);

            String query_p1 = "INSERT INTO Printer (ID, PrinterName) VALUES ('1', 'PTP-III')";
            db.execSQL(query_p1);

            String query_p2 = "INSERT INTO Printer (ID, PrinterName) VALUES ('2', 'Feasycom')";
            db.execSQL(query_p2);

            String query_p3 = "INSERT INTO Printer (ID, PrinterName) VALUES ('3', 'BluetoothPrinter')";
            db.execSQL(query_p3);
        }

        if (oldVersion < 6) {
            //Create SO
            db.execSQL("CREATE TABLE SO (ID INTEGER PRIMARY KEY , DocNo VARCHAR(20), DocDate TEXT, DebtorCode VARCHAR(12), DebtorName VARCHAR(80), SalesAgent VARCHAR(12), Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), LineNo INTEGER)");

            //Packing List Module
            String query_19 = "INSERT INTO REG (ID, Value) VALUES ('19','0')";
            db.execSQL(query_19);

            //Default TaxType
            String query_21 = "INSERT INTO REG (ID, Value) VALUES ('21','None')";
            db.execSQL(query_21);

            //Tax Enabled
            String query_22 = "INSERT INTO REG (ID, Value) VALUES ('22','0')";
            db.execSQL(query_22);

            //Default Creditor
            String query_23 = "INSERT INTO REG (ID, Value) VALUES ('23','None')";
            db.execSQL(query_23);

            //Default Purchase Agent
            String query_24 = "INSERT INTO REG (ID, Value) VALUES ('24','None')";
            db.execSQL(query_24);

            //Enable Custom Barcode
            String query_25 = "INSERT INTO REG (ID, Value) VALUES ('25','0')";
            db.execSQL(query_25);

            //Barcode Length
            String query_26 = "INSERT INTO REG (ID, Value) VALUES ('26','0')";
            db.execSQL(query_26);

            //Barcode Item Start
            String query_27 = "INSERT INTO REG (ID, Value) VALUES ('27','0')";
            db.execSQL(query_27);

            //Barcode Item Length
            String query_28 = "INSERT INTO REG (ID, Value) VALUES ('28','0')";
            db.execSQL(query_28);

            //Barcode Qty Start
            String query_29 = "INSERT INTO REG (ID, Value) VALUES ('29','0')";
            db.execSQL(query_29);

            //Barcode Qty Length
            String query_30 = "INSERT INTO REG (ID, Value) VALUES ('30','0')";
            db.execSQL(query_30);

            //Barcode Qty Decimal
            String query_31 = "INSERT INTO REG (ID, Value) VALUES ('31','0')";
            db.execSQL(query_31);

            //Create DO
            String query_DO = "CREATE TABLE DO (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), FromDocNo VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, DocDate TEXT, DebtorCode VARCHAR(12), DebtorName VARCHAR(80), SalesAgent VARCHAR(12), CreatedUser VARCHAR(8), Uploaded INTEGER DEFAULT 0 NOT NULL, Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), DocType VARCHAR(20), IsTally Boolean)";
            db.execSQL(query_DO);

            //Create DODTL
            String query_DODTL = "CREATE TABLE DODTL (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), LineNo INTEGER, DtlKey INTEGER)";
            db.execSQL(query_DODTL);

            //Create User - PackingList
            String query_editUser2 = "ALTER TABLE Users ADD PackingList INTEGER DEFAULT 0 NOT NULL";
            db.execSQL(query_editUser2);

            //Create AN - Packing List
            db.execSQL("INSERT INTO AutoNumbering ( Name, Type, NextNumber, Prefix, Length) SELECT 'PackingList', 'PL', 1, 'PL-', 5 WHERE NOT EXISTS (SELECT * FROM AutoNumbering WHERE Name = 'PackingList')");

            //Create PurchaseAgent
            db.execSQL("CREATE TABLE PurchaseAgent (PurchaseAgent VARCHAR(12), Description VARCHAR(40), PRIMARY KEY(PurchaseAgent)) ");

            //Create Creditor
            db.execSQL("CREATE TABLE Creditor ( AccNo VARCHAR(12), CompanyName VARCHAR(80), Desc2 VARCHAR(100), Address1 VARCHAR(40), Address2 VARCHAR(40), Address3 VARCHAR(40), Address4 VARCHAR(40), PurchaseAgent VARCHAR(12), TaxType VARCHAR(14), Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), EmailAddress VARCHAR(80), CreditorType VARCHAR(20), AreaCode VARCHAR(12), CurrencyCode VARCHAR(5), DisplayTerm VARCHAR(30), Phone2 VARCHAR(25), PRIMARY KEY(AccNo))");

            //Create Purchase
            db.execSQL("CREATE TABLE Purchase (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, DocDate TEXT, CreditorCode VARCHAR(12), CreditorName VARCHAR(80), PurchaseAgent VARCHAR(12), Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), Uploaded INTEGER DEFAULT 0 NOT NULL)");

            //Create PurchaseDtl
            db.execSQL("CREATE TABLE PurchaseDtl (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), UPrice DECIMAL(25,8), Discount DECIMAL(25,8), SubTotal DECIMAL(25,8), TaxType VARCHAR(14), TaxRate DECIMAL(25,8), TaxValue DECIMAL(25, 8), Totalex DECIMAL(19,2), TotalIn DECIMAL(19,2), LineNo INTEGER)");
        }

        if (oldVersion < 7) {
            //Hybrid Mode Enabled
            String query_32 = "INSERT INTO REG (ID, Value) VALUES ('32','0')";
            db.execSQL(query_32);
        }

        if (oldVersion < 8) {
            //AutoPrice Enabled
            String query_20 = "INSERT INTO REG (ID, Value) VALUES ('20','0')";
            db.execSQL(query_20);

            //History Day
            String query_33 = "INSERT INTO REG (ID, Value) VALUES ('33','30')";
            db.execSQL(query_33);
        }

        if (oldVersion < 9) {
            //AddPrinter
            String query_p4 = "INSERT INTO Printer (ID, PrinterName) VALUES ('4', 'Printer001')";
            db.execSQL(query_p4);
        }

        if (oldVersion < 10) {
            //Create SalesFormat
            db.execSQL("CREATE TABLE Report (ID INTEGER PRIMARY KEY AUTOINCREMENT, ReportType VARCHAR(2), ReportName VARCHAR(100))");

            //SalesFormat
            db.execSQL("INSERT INTO Report (ReportType, ReportName) VALUES ('S', 'Invoice')");
            db.execSQL("INSERT INTO Report (ReportType, ReportName) VALUES ('S', 'Tax Invoice')");

            //Default SalesFormat
            db.execSQL("INSERT INTO Reg (ID, Value) VALUES ('34', 'Invoice')");
        }

        if (oldVersion < 11) {
            //Logo
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('35','0')");

            //Cart
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('36','0')");

        }

        if (oldVersion < 12) {
            //Debtor Address
            db.execSQL("ALTER TABLE Sales ADD COLUMN Address1 VARCHAR(40);");
            db.execSQL("ALTER TABLE Sales ADD COLUMN Address2 VARCHAR(40);");
            db.execSQL("ALTER TABLE Sales ADD COLUMN Address3 VARCHAR(40);");
            db.execSQL("ALTER TABLE Sales ADD COLUMN Address4 VARCHAR(40);");


            //ItemImage Table
            String query_ItemImage = "CREATE TABLE ItemImage ( ItemCode VARCHAR(30), " +
                    "Image VARCHAR(4000))";
            db.execSQL(query_ItemImage);

        }

        if (oldVersion < 13) {
            //Create User - PackingList
            db.execSQL("ALTER TABLE Users ADD Catalog INTEGER DEFAULT 0 NOT NULL");
        }

        if (oldVersion < 15) {
            //Add Remarks
            db.execSQL("ALTER TABLE Sales ADD COLUMN Remarks VARCHAR(250);");
            db.execSQL("ALTER TABLE SalesDtl ADD COLUMN Remarks VARCHAR(250);");
            db.execSQL("ALTER TABLE Transfer ADD COLUMN Remarks VARCHAR(250);");
            db.execSQL("ALTER TABLE TransferDtl ADD COLUMN Remarks VARCHAR(250);");
            db.execSQL("ALTER TABLE Purchase ADD COLUMN Remarks VARCHAR(250);");
            db.execSQL("ALTER TABLE PurchaseDtl ADD COLUMN Remarks VARCHAR(250);");
            db.execSQL("ALTER TABLE DO ADD COLUMN Remarks VARCHAR(250);");
            db.execSQL("ALTER TABLE DODTL ADD COLUMN Remarks VARCHAR(250);");
            db.execSQL("ALTER TABLE SO ADD COLUMN Remarks VARCHAR(250);");
            db.execSQL("ALTER TABLE SO ADD COLUMN RemarksDtl VARCHAR(250);");


            //Add BatchNo
            db.execSQL("ALTER TABLE SalesDtl ADD COLUMN BatchNo VARCHAR(20);");
            db.execSQL("ALTER TABLE TransferDtl ADD COLUMN BatchNo VARCHAR(20);");
            db.execSQL("ALTER TABLE PurchaseDtl ADD COLUMN BatchNo VARCHAR(20);");
            db.execSQL("ALTER TABLE DODTL ADD COLUMN BatchNo VARCHAR(20);");
            db.execSQL("ALTER TABLE StockTakeDetail ADD COLUMN BatchNo VARCHAR(20);");
            db.execSQL("ALTER TABLE SO ADD COLUMN BatchNo VARCHAR(20);");

        }
        if (oldVersion < 16) {
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('37','')");
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('38','0')");

            //Batch No Table
            db.execSQL("CREATE TABLE  ItemBatch " + "( ItemCode VARCHAR(30), " +
                    "BatchNo VARCHAR(20), Description VARCHAR(100), ManufacturedDate Date, " +
                    "ExpiryDate Date)");

        }
        if (oldVersion < 17) {
            db.execSQL("ALTER TABLE Item ADD COLUMN HasBatchNo VARCHAR(20)");
            db.execSQL("ALTER TABLE StockBalance ADD COLUMN BatchNo VARCHAR(20);");
        }
        if (oldVersion < 18) {
            //AutoSalesBatchEnable
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('39','0')");
            //AutoPurchaseBatchEnable
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('40','0')");
            //BatchSalesOrderType
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('41','0')");
        }
        if (oldVersion < 19) {
            db.execSQL("ALTER TABLE DO ADD COLUMN DocType VARCHAR(20);");
            db.execSQL("ALTER TABLE SO ADD COLUMN DocType VARCHAR(20);");
            db.execSQL("UPDATE SO SET DocType='SO' WHERE DocType IS NULL;");
        }
        if (oldVersion < 20) {
            db.execSQL("ALTER TABLE DO ADD COLUMN IsTally boolean;");
            db.execSQL("UPDATE DO SET IsTally=0 WHERE IsTally IS NULL;");
            //NegativeInventory
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('42','false')");
            //OnlyTallyUploaded
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('43','0')");

            //db.execSQL("ALTER TABLE Payment ADD COLUMN Image VARCHAR(4000);");
            db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) SELECT 'ARPayment', 'AR', 1, 'AR-', 5");

            //AR Payment Table
            db.execSQL("CREATE TABLE ARPayment " + "(DocNo VARCHAR(12),Date Date, DebtorCode VARCHAR(12),DebtorName VARCHAR(50)," +
                    "Amount DECIMAL(19,2), Image VARCHAR(4000),Uploaded INTEGER DEFAULT 0 NOT NULL, CreatedTimeStamp TEXT, CreatedUser VARCHAR(8), Remark VARCHAR(20))");

            //AR PaymentDtl Table
            db.execSQL("CREATE TABLE ARPaymentDtl " + "(DocNo VARCHAR(12), DocNumber VARCHAR(20)," +
                    "PaymentAmount DECIMAL(19,2))");

            db.execSQL("ALTER TABLE Sales ADD COLUMN Status VARCHAR(20);");
        }
        if (oldVersion < 24) {
            //3.2.4
            db.execSQL("ALTER TABLE Sales ADD COLUMN CreatedUser VARCHAR(20);");
            db.execSQL("UPDATE Sales SET CreatedUser = 'ADMIN' WHERE CreatedUser IS NULL;");
            //Batch Comparison
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('44','0')");
        }
        if (oldVersion < 27) {

            //3.4
            //Analytics
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('45','0')");
            //ItemBOM Table
            db.execSQL("CREATE TABLE  ItemBOM " + "( ItemCode VARCHAR(30), " +
                    "SubItemCode VARCHAR(20), Qty DECIMAL(25,8))");
            //ARPayment
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('46','0')");

            //StockAssembly
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('47','0')");
            //Stock Assembly Table
            db.execSQL("CREATE TABLE StockAssembly " + "(DocNo VARCHAR(12), Date Date, Description VARCHAR(50),FGItemCode VARCHAR(12)," +
                    "FGQty DECIMAL(19,2), FGLocation VARCHAR(12), FGBatchNo VARCHAR(12), Remark VARCHAR(20), Uploaded INTEGER DEFAULT 0 NOT NULL, CreatedTimeStamp TEXT, CreatedUser VARCHAR(8))");

            //Stock AssemblyDtl Table
            db.execSQL("CREATE TABLE StockAssemblyDtl " + "(DocNo VARCHAR(12), SubItemCode VARCHAR(20)," +
                    "Qty DECIMAL(19,2))");

            //db.execSQL("ALTER TABLE Payment ADD COLUMN Image VARCHAR(4000);");
            db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) SELECT 'StockAssembly', 'SA', 1, 'SA-', 5");

            //ADD Collection and StockAssembly User
            db.execSQL("ALTER TABLE Users ADD COLUMN Collection INTEGER DEFAULT 0 NOT NULL;");
            db.execSQL("ALTER TABLE Users ADD COLUMN StockAssembly INTEGER DEFAULT 0 NOT NULL;");

        }
        if (oldVersion < 33) {
            db.execSQL("ALTER TABLE Users ADD COLUMN Analytics INTEGER DEFAULT 0 NOT NULL;");
            db.execSQL("ALTER TABLE Sales ADD COLUMN Remarks2 VARCHAR(250);");
            db.execSQL("ALTER TABLE Sales ADD COLUMN Remarks3 VARCHAR(250);");
            db.execSQL("ALTER TABLE Sales ADD COLUMN Remarks4 VARCHAR(250);");
            db.execSQL("ALTER TABLE SalesDtl ADD COLUMN Remarks2 VARCHAR(250);");
        }
        if (oldVersion < 34) {
            db.execSQL("ALTER TABLE Users ADD COLUMN StockTake INTEGER DEFAULT 0 NOT NULL");
        }
        //mobile stock newland
//        if(oldVersion < 35 ){
//            db.execSQL("ALTER TABLE ARPayment ADD COLUMN Remark VARCHAR(20) ");
//
//        }

//        if(oldVersion < 36){
//            db.execSQL("Drop Table ARPayment");
//            db.execSQL("CREATE TABLE ARPayment " + "(DocNo VARCHAR(12),Date Date, DebtorCode VARCHAR(12),DebtorName VARCHAR(50)," +
//                    "Amount DECIMAL(19,2), Image VARCHAR(4000),Uploaded INTEGER DEFAULT 0 NOT NULL, CreatedTimeStamp TEXT, CreatedUser VARCHAR(8), Remark VARCHAR(20))");
//
//        }

        //3.6
        if (oldVersion < 37) {
            //SellingPrice
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('48','0')");
            db.execSQL("ALTER TABLE Users ADD COLUMN SellingPrice INTEGER DEFAULT 0 NOT NULL;");
            //LocationComparison
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('49','0')");
        }
        //3.6.3
        if (oldVersion < 38) {
            boolean exist = existsColumnInTable(db, "Users", "Collection");
            if (!exist) {
                db.execSQL("ALTER TABLE Users ADD COLUMN Collection INTEGER DEFAULT 0 NOT NULL;");

            }
            boolean exist2 = existsColumnInTable(db, "Users", "StockAssembly");
            if (!exist2) {
                db.execSQL("ALTER TABLE Users ADD COLUMN StockAssembly INTEGER DEFAULT 0 NOT NULL;");
            }
        }
        //3.7
        if (oldVersion < 45) {

            boolean exist3 = existsColumnInTable(db, "ARPayment", "Remark");
            boolean exist4 = existsColumnInTable(db, "ARPayment", "CreatedTimeStamp");
            boolean exist5 = existsColumnInTable(db, "ARPayment", "CreatedUser");

            if (!exist3) {
                db.execSQL("ALTER TABLE ARPayment ADD COLUMN  Remark VARCHAR(20);");
            }
            if (!exist4) {
                db.execSQL("ALTER TABLE ARPayment ADD COLUMN CreatedTimeStamp TEXT;");
            }

            if (!exist5) {
                db.execSQL("ALTER TABLE ARPayment ADD COLUMN CreatedUser VARCHAR(8);");
            }

            boolean exist = existsColumnInTable(db, "ItemBOM", "ItemCode");
            if (!exist) {
                //Analytics
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('45','0')");
                //ItemBOM Table
                db.execSQL("CREATE TABLE  ItemBOM " + "( ItemCode VARCHAR(30), " +
                        "SubItemCode VARCHAR(20), Qty DECIMAL(25,8))");
                //ARPayment
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('46','0')");
            }

            boolean exist2 = existsColumnInTable(db, "StockAssembly", "DocNo");
            if (!exist2) {
                //Stock Assembly Table
                db.execSQL("CREATE TABLE StockAssembly " + "(DocNo VARCHAR(12), Date Date, Description VARCHAR(50),FGItemCode VARCHAR(12)," +
                        "FGQty DECIMAL(19,2), FGLocation VARCHAR(12), FGBatchNo VARCHAR(12), Remark VARCHAR(20), Uploaded INTEGER DEFAULT 0 NOT NULL, CreatedTimeStamp TEXT, CreatedUser VARCHAR(8))");
                //Stock AssemblyDtl Table
                db.execSQL("CREATE TABLE StockAssemblyDtl " + "(DocNo VARCHAR(12), SubItemCode VARCHAR(20)," +
                        "Qty DECIMAL(19,2))");
                //db.execSQL("ALTER TABLE Payment ADD COLUMN Image VARCHAR(4000);");
                db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) SELECT 'StockAssembly', 'SA', 1, 'SA-', 5");

                //StockAssembly
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('47','0')");
            }


            boolean exist6 = existsColumnInTable(db, "PO", "DocNo");

            if (!exist6) {
                //create PO
                db.execSQL("CREATE TABLE PO (ID INTEGER PRIMARY KEY , DocNo VARCHAR(20), DocDate TEXT, CreditorCode VARCHAR(12), CreditorName VARCHAR(80), PurchaseAgent VARCHAR(12), Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), LineNo INTEGER, BatchNo VARCHAR(20), Remarks VARCHAR(250), RemarksDtl VARCHAR(250), DocType VARCHAR(20))");

                //PI Table
                String query_DO = "CREATE TABLE PI (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), FromDocNo VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, DocDate TEXT, CreditorCode VARCHAR(12), CreditorName VARCHAR(80), PurchaseAgent VARCHAR(12), CreatedUser VARCHAR(8), Uploaded INTEGER DEFAULT 0 NOT NULL, Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), Remarks VARCHAR(250), DocType VARCHAR(20), IsTally Boolean)";
                db.execSQL(query_DO);

                //PIDTL Table
                String query_DODTL = "CREATE TABLE PIDTL (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), LineNo INTEGER, DtlKey INTEGER, Remarks VARCHAR(250), BatchNo VARCHAR(20))";
                db.execSQL(query_DODTL);

                //Create User - Purchase Packing List
                String query_editUser2 = "ALTER TABLE Users ADD PurchasePackingList INTEGER DEFAULT 0 NOT NULL";
                db.execSQL(query_editUser2);

                //Create AN - Purchase Packing List
                db.execSQL("INSERT INTO AutoNumbering ( Name, Type, NextNumber, Prefix, Length) SELECT 'PurchasePackingList', 'PPL', 1, 'MPPL-', 5 WHERE NOT EXISTS (SELECT * FROM AutoNumbering WHERE Name = 'PurchasePackingList')");

                //Purchase PL
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('50','0')");
            }

        }

        //3.8
        if (oldVersion < 46) {

            boolean exist7 = existsColumnInTable(db, "ARPayment", "LastModifiedDateTime");
            if (!exist7) {
                db.execSQL("ALTER TABLE ARPayment ADD COLUMN LastModifiedDateTime DATETIME;");
                db.execSQL("ALTER TABLE DO ADD COLUMN LastModifiedDateTime DATETIME;");
                db.execSQL("ALTER TABLE PI ADD COLUMN LastModifiedDateTime DATETIME;");
                db.execSQL("ALTER TABLE Purchase ADD COLUMN LastModifiedDateTime DATETIME;");
                db.execSQL("ALTER TABLE Sales ADD COLUMN LastModifiedDateTime DATETIME;");
                db.execSQL("ALTER TABLE StockAssembly ADD COLUMN LastModifiedDateTime DATETIME;");
                db.execSQL("ALTER TABLE StockTake ADD COLUMN LastModifiedDateTime DATETIME;");
                db.execSQL("ALTER TABLE TRANSFER ADD COLUMN LastModifiedDateTime DATETIME;");

                db.execSQL("ALTER TABLE Purchase ADD COLUMN CreatedUser VARCHAR(20);");
                db.execSQL("ALTER TABLE StockTake ADD COLUMN CreatedUser VARCHAR(20);");

                db.execSQL("ALTER TABLE Purchase ADD COLUMN Remarks2 VARCHAR(250);");
                db.execSQL("ALTER TABLE Purchase ADD COLUMN Remarks3 VARCHAR(250);");
                db.execSQL("ALTER TABLE Purchase ADD COLUMN Remarks4 VARCHAR(250);");
                db.execSQL("ALTER TABLE PurchaseDtl ADD COLUMN Remarks2 VARCHAR(250);");

                //Barcode DtlRemark Start
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('51','0')");
                //Barcode DtlRemark Length
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('52','0')");
                //Barcode DtlRemark2 Start
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('53','0')");
                //Barcode DtlRemark2 Length
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('54','0')");

                db.execSQL("ALTER TABLE ARPayment ADD COLUMN LastModifiedUser VARCHAR(20);");
                db.execSQL("ALTER TABLE DO ADD COLUMN LastModifiedUser VARCHAR(20);");
                db.execSQL("ALTER TABLE PI ADD COLUMN LastModifiedUser VARCHAR(20);");
                db.execSQL("ALTER TABLE Purchase ADD COLUMN LastModifiedUser VARCHAR(20);");
                db.execSQL("ALTER TABLE Sales ADD COLUMN LastModifiedUser VARCHAR(20);");
                db.execSQL("ALTER TABLE StockAssembly ADD COLUMN LastModifiedUser VARCHAR(20);");
                db.execSQL("ALTER TABLE StockTake ADD COLUMN LastModifiedUser VARCHAR(20);");
                db.execSQL("ALTER TABLE TRANSFER ADD COLUMN LastModifiedUser VARCHAR(20);");

                //Terminal Device
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('55','old')");
                //Agent List
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('56','0')");
            }
        }
            //3.9
        if (oldVersion < 47){
            boolean exist = existsColumnInTable(db, "Item", "SalesUOM");
            if (!exist) {
                db.execSQL("ALTER TABLE Item ADD COLUMN SalesUOM VARCHAR(8)");
                db.execSQL("ALTER TABLE Item ADD COLUMN PurchaseUOM VARCHAR(8)");
                db.execSQL("ALTER TABLE ARPaymentDtl ADD COLUMN DocDate Date");
                db.execSQL("ALTER TABLE ARPaymentDtl ADD COLUMN OrgAmt DECIMAL(19,2)");
                //ARPayment Settings
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('57','0')");
                //FilterbyItemGroup
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('58','0')");
                //ItemGroupList
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('59','0')");
                //FilterbyItemType
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('60','0')");
                //ItemTypeList
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('61','0')");
                //StockInquiry
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('62','0')");
                //Create User - StockInquiry
                String query_editUser2 = "ALTER TABLE Users ADD StockInquiry INTEGER DEFAULT 0 NOT NULL";
                db.execSQL(query_editUser2);
                //CollectionReceiptFormat
                db.execSQL(
                        "INSERT INTO REG (ID, Value) VALUES ('63','The official receipt is onliy valid upon the transaction been shown as cleared in our bank account.'|| char(10) ||'NB'|| char(10) ||'Validity of This Receipt'|| char(10) ||'Subject to Clearing of Cheque')");
                //SalesReceiptFormat
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('64','1. All cheques should be crossed and made payable to '|| char(10) || 'AUTOCOUNT POS SDN BHD '|| char(10) ||'2. Goods sold are neither returnable nor refundable. Otherwise '|| char(10) || 'a cancellation fee of 20% on purchase price will be imposed.')");

            }
        }

        //3.9.5
        if (oldVersion < 48){
            boolean exist = existsColumnInTable(db, "PO", "reDownload");
            if (!exist) {
                db.execSQL("ALTER TABLE PO ADD COLUMN reDownload INTEGER DEFAULT 0 NOT NULL");
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('65','0')");
            }
        }

        //3.9.6
        if (oldVersion < 49){
            boolean exist = existsColumnInTable(db, "PO", "PurchaseLocation");
            if (!exist) {
                db.execSQL("ALTER TABLE PO ADD COLUMN PurchaseLocation VARCHAR(20);");
            }
        }

        //3.9.10
        if (oldVersion < 50){
                //OnlyTallyUploadedPR
                db.execSQL("INSERT INTO REG (ID, Value) VALUES ('66','0')");
        }
        //3.9.11
        if(oldVersion < 51){
            db.execSQL("DELETE FROM DownloadUpload WHERE tableName = 'Purchase Packing List'");
        }

        //3.9.28
        if(oldVersion < 52){
            //UTD Cost
            db.execSQL("CREATE TABLE  UTDCost " + "( ItemCode VARCHAR(30), " +
                    "BatchNo VARCHAR(20), UOM VARCHAR(8), Location VARCHAR(12), " +
                    "UTDQty DECIMAL(25,8), UTDCost DECIMAL(25,8))");

            //Cost Permission
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('69','0')");
            db.execSQL("ALTER TABLE Users ADD COLUMN Cost INTEGER DEFAULT 0 NOT NULL;");
        }

        if(oldVersion < 53){

            boolean exist6 = existsColumnInTable(db, "Payment", "Uploaded");
            if (!exist6) {
                db.execSQL("ALTER TABLE Payment ADD COLUMN Uploaded INTEGER DEFAULT 0 NOT NULL");

            }
        }

        if (oldVersion < 58) {

            //Stock Receive
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('71','0')");

            db.execSQL("ALTER TABLE USERS ADD COLUMN StockReceive INTEGER DEFAULT 0 NOT NULL");

            db.execSQL("CREATE TABLE StockReceive (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), DocType VARCHAR(20), CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, CreatedUser VARCHAR(20), DocDate TEXT, Uploaded INTEGER DEFAULT 0 NOT NULL, Remarks VARCHAR(250), Description VARCHAR(250), TaxType VARCHAR(3000), LastModifiedDateTime DATETIME, LastModifiedUser VARCHAR(20))");

            db.execSQL("CREATE TABLE StockReceiveDtl (ID INTEGER PRIMARY KEY AUTOINCREMENT, DocNo VARCHAR(20), DocDate TEXT, Location VARCHAR(12), ItemCode VARCHAR(30), ItemDescription VARCHAR(80), UOM VARCHAR(8), Qty DECIMAL(25,8), UTD_Cost DECIMAL(25,8), SubTotal DECIMAL(25,8), BatchNo VARCHAR(20), Remarks VARCHAR(250), Remarks2 VARCHAR(250), LineNo INTEGER)");

            //Job Sheet
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('70','0')");

            db.execSQL("ALTER TABLE USERS ADD COLUMN JobSheet INTEGER DEFAULT 0 NOT NULL");

            String query_JS = "CREATE TABLE JobSheet (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "DocNo VARCHAR(20), DocDate TEXT, DocType VARCHAR(20), DebtorCode VARCHAR(12), DebtorName VARCHAR(80), DebtorName2 VARCHAR(50), Address1 VARCHAR(40), Address2 VARCHAR(40), Address3 VARCHAR(40), Address4 VARCHAR(40)," +
                    "SalesAgent VARCHAR(12), Uploaded INTEGER DEFAULT 0 NOT NULL, TaxType VARCHAR(3000), " +
                    "Phone VARCHAR(25), Fax VARCHAR(25), Attention VARCHAR(40), " +
                    "Remarks VARCHAR(250),Remarks2 VARCHAR(250),Remarks3 VARCHAR(250),Remarks4 VARCHAR(250), Status VARCHAR(20), " +
                    "WorkType VARCHAR(250), ReplacementType VARCHAR(250), TimeIn TEXT, TimeOut TEXT, Problem VARCHAR(250), Resolution VARCHAR(250), JobSheetRemarks VARCHAR(250), " +
                    "SalesNo TEXT, TaxValue DECIMAL(25,8), TotalEx DECIMAL(19,2), TotalIn DECIMAL(19,2), Discount DECIMAL(25,8), " +
                    "CreatedTimeStamp TEXT DEFAULT CURRENT_TIMESTAMP, CreatedUser VARCHAR(20), LastModifiedDateTime DATETIME, LastModifiedUser VARCHAR(20), Signature VARCHAR(3000), Image VARCHAR(4000))";
            db.execSQL(query_JS);

            //Job Sheet Details
            db.execSQL("CREATE TABLE IF NOT EXISTS JobSheetDetails (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "DocNo VARCHAR(20)," +
                    "DocDate TEXT," +
                    "Location VARCHAR(12)," +
                    "ItemCode VARCHAR(30)," +
                    "ItemDescription VARCHAR(80)," +
                    "UOM VARCHAR(8)," +
                    "Quantity DECIMAL(25,8)," +
                    "UPrice DECIMAL(25,8)," +
                    "Discount DECIMAL(25,8)," +
                    "SubTotal DECIMAL(25,8)," +
                    "TaxType VARCHAR(14)," +
                    "TaxRate DECIMAL(25,8)," +
                    "TaxValue DECIMAL(25,8)," +
                    "TotalEx DECIMAL(19,2)," +
                    "TotalIn DECIMAL(19,2)," +
                    "LineNo INTEGER," +
                    "Remarks VARCHAR(250)," +
                    "Remarks2 VARCHAR(250)," +
                    "BatchNo VARCHAR(20)" +
                    ");");
        }

        if (oldVersion < 59) {

            db.execSQL("ALTER TABLE SalesDtl ADD DiscountText VARCHAR(100)");
        }

        if (oldVersion < 60) {
        //quotation
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('72','0')");
        //min price
            db.execSQL("INSERT INTO REG (ID, Value) VALUES ('73','1')");
        }

        if (oldVersion < 61) {
            db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) SELECT 'JobSheet', 'JS', 1, 'MJS-', 5");

            db.execSQL("INSERT INTO " + TABLE_NAME_AN + "( Name, Type, NextNumber, Prefix, Length) SELECT 'StockReceive', 'SR', 1, 'MSR-', 5");
        }

        if (oldVersion < 62) {
            db.execSQL("ALTER TABLE Debtor ADD COLUMN DetailDiscount VARCHAR(100);");

            db.execSQL("CREATE TABLE CreditTermMaintenance ( ID INTEGER PRIMARY KEY AUTOINCREMENT, DisplayTerm VARCHAR(80), Terms VARCHAR(80), TermType VARCHAR(80), TermDays VARCHAR(80), DiscountDays VARCHAR(80), DiscountPercent VARCHAR(80) ) ");

            db.execSQL("ALTER TABLE Sales ADD COLUMN CreditTerm VARCHAR(80);");

        }

    }

    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    private boolean existsColumnInTable(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
        Cursor mCursor = null;
        try {
            mCursor = inDatabase.rawQuery("SELECT * FROM " + inTable + " LIMIT 0", null);
            if (mCursor.getColumnIndex(columnToCheck) != -1) //exist
                return true;
            else  //-1 doesnot exist
                return false;
        } catch (Exception Exp) {
            Log.d("exists", "Exist Column" + Exp.getMessage());
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

    public void resetST() {
        SQLiteDatabase db = this.getWritableDatabase();
        String drop = "DROP TABLE IF EXISTS StockTake";
        db.execSQL(drop);

        String drop2 = "DROP TABLE IF EXISTS StockTakeDetail";
        db.execSQL(drop2);
    }

    public void resetPermissionTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query_PEM_drop = "DROP TABLE IF EXISTS " + TABLE_NAME_PEM;
        db.execSQL(query_PEM_drop);
    }

    public void resetREGTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query_drop = "DROP TABLE IF EXISTS Reg";
        db.execSQL(query_drop);
    }

    public void resetPurchaseTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query_PR = "DROP TABLE IF EXISTS Purchase";
        db.execSQL(query_PR);

        String query_PRD = "DROP TABLE IF EXISTS PurchaseDetail";
        db.execSQL(query_PRD);
    }

    //GET All Reg
    public Cursor getReg(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT Value FROM Reg WHERE ID = '" + id + "'", null);
        return data;
    }

    public Cursor insertUUID(String uuid, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("Update Reg SET Value = '" + uuid + "' WHERE ID = '" + id + "'", null);
        return data;
    }

    public void insertPermission(String pem_name, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query_PEM_insert = "INSERT INTO " + TABLE_NAME_PEM + " (" + COL0_PEM + "," + COL1_PEM + ")" +
                " VALUES ('" + pem_name + "','" + status + "') ";
        db.execSQL(query_PEM_insert);
    }

    public void AddPrinter(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query_PEM_insert = "INSERT INTO Printer (PrinterName) VALUES ('" + name + "') ";
        db.execSQL(query_PEM_insert);
    }

    //GET All Stock Count
    public Cursor getPermission() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME_PEM, null);
        return data;
    }

    public Cursor getSelectedPermission(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT status FROM " + TABLE_NAME_PEM + " WHERE permissionName='" + name + "'", null);
        return data;
    }

    //GET All Stock Count
    public void editPermissionDeny(String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query_PEM_edit = "UPDATE " + TABLE_NAME_PEM + " SET " + COL1_PEM + "='Denied' WHERE permissionName='" + name + "'";
        database.execSQL(query_PEM_edit);
    }


    public void editPermissionGranted(String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query_PEM_edit = "UPDATE " + TABLE_NAME_PEM + " SET " + COL1_PEM + " ='Granted' WHERE permissionName='" + name + "'";
        database.execSQL(query_PEM_edit);
    }

    //GET Permission Status
    public Cursor getPermissionStatus(String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor data = database.rawQuery("SELECT status FROM " + TABLE_NAME_PEM + " WHERE permissionName='" + name + "'", null);
        return data;
    }

    public void deletePermission(String pem_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query_PEM_del = "DELETE FROM " + TABLE_NAME_PEM + " WHERE " + COL0_PEM + "='" + pem_name + "'";
        db.execSQL(query_PEM_del);
    }

    // Set AN prefix
    public boolean setANPrefix(String prefix) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL4_AN, prefix);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"S"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set AN length
    public boolean setANLength(String length) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL5_AN, length);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"S"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set AN nextNumber
    public boolean setANNextNumber(String nextNumber) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL3_AN, nextNumber);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"S"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set Transfer AN prefix
    public boolean setStockTakeANPrefix(String prefix) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL4_AN, prefix);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"ST"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set Transfer AN length
    public boolean setStockTakeANLength(String length) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL5_AN, length);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"ST"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set Transfer AN nextNumber
    public boolean setStockTakeANNextNumber(String nextNumber) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL3_AN, nextNumber);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"ST"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set Transfer AN prefix
    public boolean setTransferANPrefix(String prefix) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL4_AN, prefix);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"T"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set Transfer AN length
    public boolean setTransferANLength(String length) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL5_AN, length);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"T"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set Transfer AN nextNumber
    public boolean setTransferANNextNumber(String nextNumber) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL3_AN, nextNumber);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"T"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set Purchase AN prefix
    public boolean setPurchaseANPrefix(String prefix) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL4_AN, prefix);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"P"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set Purchase AN length
    public boolean setPurchaseANLength(String length) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL5_AN, length);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"P"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set Purchase AN nextNumber
    public boolean setPurchaseANNextNumber(String nextNumber) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL3_AN, nextNumber);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"P"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set PL  AN prefix
    public boolean setPLANPrefix(String prefix) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL4_AN, prefix);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"PL"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set PL  AN prefix
    public boolean setARANPrefix(String prefix) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL4_AN, prefix);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"AR"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set PL  AN prefix
    public boolean setSAANPrefix(String prefix) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL4_AN, prefix);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"SA"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    public boolean setPPLANPrefix(String prefix) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL4_AN, prefix);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"PPL"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set PL  AN length
    public boolean setPLANLength(String length) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL5_AN, length);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"PL"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set PL  AN length
    public boolean setSAANLength(String length) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL5_AN, length);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"SA"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Set PL  AN length
    public boolean setARANLength(String length) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL5_AN, length);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"AR"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    public boolean setPPLANLength(String length) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL5_AN, length);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"PPL"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }


    // Set PL AN nextNumber
    public boolean setPLANNextNumber(String nextNumber) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL3_AN, nextNumber);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"PL"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }
    // Set PL AN nextNumber
    public boolean setARANNextNumber(String nextNumber) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL3_AN, nextNumber);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"AR"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    public boolean setPPLANNextNumber(String nextNumber) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL3_AN, nextNumber);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"PPL"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }


    // Set PL AN nextNumber
    public boolean setSAANNextNumber(String nextNumber) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL3_AN, nextNumber);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"SA"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    public boolean setJSANPrefix(String prefix) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL4_AN, prefix);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"JS"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    public boolean setJSANLength(String length) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL5_AN, length);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"JS"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    public boolean setJSANNextNumber(String nextNumber) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL3_AN, nextNumber);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"JS"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    public boolean setSRANPrefix(String prefix) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL4_AN, prefix);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"SR"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    public boolean setSRANLength(String length) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL5_AN, length);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"SR"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    public boolean setSRANNextNumber(String nextNumber) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL3_AN, nextNumber);
            database.update(TABLE_NAME_AN, cv, COL2_AN + " = ?", new String[]{"SR"});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "resetAN: " + e.getMessage());
            return false;
        }
    }

    // Reset Stock
    public boolean resetSC() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            int sqlResult = database.delete(TABLE_NAME_SC, null, null);
            return (sqlResult > 0);
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }
        return false;
    }

    //INSERT URL
    public boolean insertData(String urlFP, String portFP) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_SL, "http://" + urlFP + ":" + portFP + "/MobileStock/web");
        cv.put(COL2_SL, urlFP);
        long results = db.insert(TABLE_NAME_SL, null, cv);
        return results != -1;
    }

    public int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    //INSERT MODULES
    public boolean insertModules(String nameFP, String activateFP) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", nameFP);
        cv.put("Activate", activateFP);

        long results = db.insert("Modules", null, cv);
        return results != -1;
    }


    //INSERT USERS
    public boolean insertUsers(String UsernameFP, String PwdFP, Boolean EnableSettingFP, Boolean FilterByAgentFP, Boolean SalesFP, Boolean PurchaseFP, Boolean TransferFP, Boolean PackingListFP, Boolean CatalogFP, Boolean CollectionFP, Boolean StockAssemblyFP, Boolean AnalyticsFP, Boolean StockTakeFP, Boolean SellingPriceFP, Boolean PurchasePackingListFP, Boolean StockInquiryFP, Boolean CostFP, Boolean JobSheetFP, Boolean StockReceiveFP) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Username", UsernameFP);
        cv.put("Pwd", Hash.bin2hex(Hash.getHash(PwdFP)));
        cv.put("EnableSetting", boolToInt(EnableSettingFP));
        cv.put("FilterByAgent", boolToInt(FilterByAgentFP));
        cv.put("Sales", boolToInt(SalesFP));
        cv.put("Purchase", boolToInt(PurchaseFP));
        cv.put("Transfer", boolToInt(TransferFP));
        cv.put("PackingList", boolToInt(PackingListFP));
        cv.put("Catalog", boolToInt(CatalogFP));
        cv.put("Collection", boolToInt(CollectionFP));
        cv.put("StockAssembly", boolToInt(StockAssemblyFP));
        cv.put("Analytics", boolToInt(AnalyticsFP));
        cv.put("StockTake", boolToInt(StockTakeFP));
        cv.put("SellingPrice", boolToInt(SellingPriceFP));
        cv.put("PurchasePackingList", boolToInt(PurchasePackingListFP));
        cv.put("StockInquiry", boolToInt(StockInquiryFP));
        cv.put("Cost", boolToInt(CostFP));
        cv.put("JobSheet", boolToInt(JobSheetFP));
        cv.put("StockReceive", boolToInt(StockReceiveFP));

        long results = db.insert(TABLE_NAME_USERS, null, cv);
        return results != -1;
    }

    //INSERT Debtor
    public boolean insertDebtor(String AccNoFP, String CompanyNameFP, String Desc2FP, String Address1FP, String Address2FP, String Address3FP, String Address4FP, String SalesAgentFP, String TaxTypeFP, String PhoneFP, String FaxFP, String AttentionFP, String EmailAddressFP, String DebtorTypeFP, String AreaCodeFP, String CurrencyCodeFP, String DisplayTermFP, String Phone2, String PriceCategory, String DetailDiscount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("AccNo", AccNoFP);
        cv.put("CompanyName", CompanyNameFP);
        cv.put("Desc2", escapeChar(Desc2FP));
        cv.put("Address1", escapeChar(Address1FP));
        cv.put("Address2", escapeChar(Address2FP));
        cv.put("Address3", escapeChar(Address3FP));
        cv.put("Address4", escapeChar(Address4FP));
        cv.put("SalesAgent", escapeChar(SalesAgentFP));
        cv.put("TaxType", escapeChar(TaxTypeFP));
        cv.put("Phone", escapeChar(PhoneFP));
        cv.put("Fax", escapeChar(FaxFP));
        cv.put("Attention", escapeChar(AttentionFP));
        cv.put("EmailAddress", escapeChar(EmailAddressFP));
        cv.put("DebtorType", escapeChar(DebtorTypeFP));
        cv.put("AreaCode", escapeChar(AreaCodeFP));
        cv.put("CurrencyCode", escapeChar(CurrencyCodeFP));
        cv.put("DisplayTerm", escapeChar(DisplayTermFP));
        cv.put("Phone2", escapeChar(Phone2));
        cv.put("PriceCategory", escapeChar(PriceCategory));
        cv.put("DetailDiscount", escapeChar(DetailDiscount));
//        Log.i("custDebug", cv.toString());
        long results = db.insert(TABLE_NAME_DEBTOR, null, cv);

//        Log.i("custDebug",DatabaseUtils.dumpCursorToString(db.rawQuery("SELECT *  FROM "+TABLE_NAME_DEBTOR, null)));

        return results != -1;
    }

    //INSERT Debtor
    public boolean insertCreditor(String AccNoFP, String CompanyNameFP, String Desc2FP, String Address1FP, String Address2FP, String Address3FP, String Address4FP, String PurchaseAgentFP, String TaxTypeFP, String PhoneFP, String FaxFP, String AttentionFP, String EmailAddressFP, String CreditorTypeFP, String AreaCodeFP, String CurrencyCodeFP, String DisplayTermFP, String Phone2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("AccNo", AccNoFP);
        cv.put("CompanyName", CompanyNameFP);
        cv.put("Desc2", escapeChar(Desc2FP));
        cv.put("Address1", escapeChar(Address1FP));
        cv.put("Address2", escapeChar(Address2FP));
        cv.put("Address3", escapeChar(Address3FP));
        cv.put("Address4", escapeChar(Address4FP));
        cv.put("PurchaseAgent", escapeChar(PurchaseAgentFP));
        cv.put("TaxType", escapeChar(TaxTypeFP));
        cv.put("Phone", escapeChar(PhoneFP));
        cv.put("Fax", escapeChar(FaxFP));
        cv.put("Attention", escapeChar(AttentionFP));
        cv.put("EmailAddress", escapeChar(EmailAddressFP));
        cv.put("CreditorType", escapeChar(CreditorTypeFP));
        cv.put("AreaCode", escapeChar(AreaCodeFP));
        cv.put("CurrencyCode", escapeChar(CurrencyCodeFP));
        cv.put("DisplayTerm", escapeChar(DisplayTermFP));
        cv.put("Phone2", escapeChar(Phone2));
        long results = db.insert("Creditor", null, cv);

        return results != -1;
    }

    //INSERT Item
    public boolean insertItem(String ItemCode, String Description, String Desc2, String ItemGroup,
                              String ItemType, String TaxType, String PurchaseTaxType, String BaseUOM,
                              Float Price, Float Price2, String BarCode, String Image, String ItemCode2,
                              String hasBatchno, String SalesUOM, String PurchaseUOM) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_ITEM, ItemCode);
        cv.put(COL2_ITEM, Description);
        cv.put(COL3_ITEM, Desc2);
        cv.put(COL4_ITEM, ItemGroup);
        cv.put(COL5_ITEM, ItemType);
        cv.put(COL6_ITEM, TaxType);
        cv.put(COL7_ITEM, PurchaseTaxType);
        cv.put(COL8_ITEM, BaseUOM);
        cv.put(COL9_ITEM, Price);
        cv.put(COL10_ITEM, Price2);
        cv.put(COL11_ITEM, BarCode);
        cv.put(COL12_ITEM, Image);
        cv.put("ItemCode2", ItemCode2);
        cv.put("HasBatchNo", hasBatchno);
        cv.put("SalesUOM", SalesUOM);
        cv.put("PurchaseUOM", PurchaseUOM);
        long results = db.insert(TABLE_NAME_ITEM, null, cv);
        return results != -1;
    }

    public boolean insertItemImage(String ItemCode, String Image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_ITEM, ItemCode);
        cv.put(COL12_ITEM, Image);
        long results = db.insert(TABLE_NAME_ITEM_IMAGE, null, cv);
        return results != -1;
    }

    public boolean insertPaymentImage(String docNo, String Image) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Payment SET Image='" + Image + "' WHERE DocNo='" + docNo + "'");
        return true;
    }

    //INSERT Location
    public boolean insertLocation(String Location, String Description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_LOC, Location);
        cv.put(COL2_LOC, Description);
        long results = db.insert(TABLE_NAME_LOCATION, null, cv);
        return results != -1;
    }

    //INSERT SalesAgent
    public boolean insertSalesAgent(String SalesAgent, String Description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("SalesAgent", SalesAgent);
        cv.put("Description", Description);
        long results = db.insert("Sales_Agent", null, cv);
        return results != -1;
    }

    //INSERT PurchaseAgent
    public boolean insertPurchaseAgent(String PurchaseAgent, String Description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PurchaseAgent", PurchaseAgent);
        cv.put("Description", Description);
        long results = db.insert("PurchaseAgent", null, cv);
        return results != -1;
    }

    //Insert UOM
    public boolean insertUOM(String ItemCode, String UOM, Float Price, Float Price2, String Shelf,
                             String BarCode, Float BalQty, Float Rate, Float MinPrice, Float MaxPrice, Float Price3, Float Price4, Float Price5, Float Price6) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ItemCode", ItemCode);
        cv.put("UOM", UOM);
        cv.put("Price", Price);
        cv.put("Price2", Price2);
        cv.put("Shelf", Shelf);
        cv.put("Barcode", BarCode);
        cv.put("BalQty", BalQty);
        cv.put("Rate", Rate);
        cv.put("MinPrice", MinPrice);
        cv.put("MaxPrice", MaxPrice);
        cv.put("Price3", Price3);
        cv.put("Price4", Price4);
        cv.put("Price5", Price5);
        cv.put("Price6", Price6);
        long results = db.insert("ItemUOM", null, cv);
        return results != -1;
    }

    //INSERT Stock Balance
    public boolean insertStockBalance(String ItemCode, String UOM, String Location, Float BalQty, String batchNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL0_SB, ItemCode);
        cv.put(COL1_SB, UOM);
        cv.put(COL2_SB, Location);
        cv.put(COL3_SB, BalQty);
        cv.put("BatchNo", batchNo);
        long results = db.insert(TABLE_NAME_SB, null, cv);
        return results != -1;
    }

    //INSERT Item Batch
    public boolean insertItemBatch(String ItemCode, String BatchNo, String Description, String ManuDate, String Expidate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ItemCode", ItemCode);
        cv.put("BatchNo", BatchNo);
        cv.put("Description", Description);
        cv.put("ManufacturedDate", ManuDate);
        cv.put("ExpiryDate", Expidate);
        long results = db.insert("ItemBatch", null, cv);
        return results != -1;
    }

    public boolean insertUTDCost(String ItemCode, String BatchNo, String UOM, String Location, Double UTDQty, Double UTDCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ItemCode", ItemCode);
        cv.put("BatchNo", BatchNo);
        cv.put("UOM", UOM);
        cv.put("Location", Location);
        cv.put("UTDQty", UTDQty);
        cv.put("UTDCost", UTDCost);
        long results = db.insert("UTDCost", null, cv);
        return results != -1;
    }

    //INSERT Item Batch
    public boolean insertItemBOM(String ItemCode, String SubItemCode, Double qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ItemCode", ItemCode);
        cv.put("SubItemCode", SubItemCode);
        cv.put("Qty", qty);
        long results = db.insert("ItemBOM", null, cv);
        return results != -1;
    }

    //INSERT Sales Order
    public boolean insertSalesOrder(Integer IDFP, String DocNoFP, String DocDateFP, String DebtorCodeFP, String DebtorNameFP, String SalesAgentFP, String PhoneFP, String FaxFP, String AttentionFP, Integer LineNoFP, String ItemCodeFP, String ItemDescriptionFP, String LocationFP, Double QtyFP, String UOMFP, String RemarkFP, String RemarkDtlFP, String DocTypeFP, String batch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ID", IDFP);
        cv.put("DocNo", DocNoFP);
        cv.put("DocDate", DocDateFP);
        cv.put("DebtorCode", DebtorCodeFP);
        cv.put("DebtorName", DebtorNameFP);
        cv.put("SalesAgent", SalesAgentFP);
        cv.put("Phone", PhoneFP);
        cv.put("Fax", FaxFP);
        cv.put("Attention", AttentionFP);
        cv.put("LineNo", LineNoFP);
        cv.put("ItemCode", ItemCodeFP);
        cv.put("ItemDescription", ItemDescriptionFP);
        cv.put("Location", LocationFP);
        cv.put("Qty", QtyFP);
        cv.put("UOM", UOMFP);
        cv.put("Remarks", RemarkFP);
        cv.put("RemarksDtl", RemarkDtlFP);
        cv.put("DocType", DocTypeFP);
        cv.put("BatchNo", batch);
        long results = db.insert("SO", null, cv);
        return results != -1;
    }

    //INSERT Purchase Order
    public boolean insertPurchaseOrder(Integer IDFP, String DocNoFP, String DocDateFP, String DebtorCodeFP, String DebtorNameFP, String SalesAgentFP, String PhoneFP, String FaxFP, String PurchaseLocationFP, String AttentionFP, Integer LineNoFP, String ItemCodeFP, String ItemDescriptionFP, String LocationFP, Double QtyFP, String UOMFP, String RemarkFP, String RemarkDtlFP, String DocTypeFP, String batch, int reDwl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ID", IDFP);
        cv.put("DocNo", DocNoFP);
        cv.put("DocDate", DocDateFP);
        cv.put("CreditorCode", DebtorCodeFP);
        cv.put("CreditorName", DebtorNameFP);
        cv.put("PurchaseAgent", SalesAgentFP);
        cv.put("Phone", PhoneFP);
        cv.put("Fax", FaxFP);
        cv.put("PurchaseLocation", PurchaseLocationFP);
        cv.put("Attention", AttentionFP);
        cv.put("LineNo", LineNoFP);
        cv.put("ItemCode", ItemCodeFP);
        cv.put("ItemDescription", ItemDescriptionFP);
        cv.put("Location", LocationFP);
        cv.put("Qty", QtyFP);
        cv.put("UOM", UOMFP);
        cv.put("Remarks", RemarkFP);
        cv.put("RemarksDtl", RemarkDtlFP);
        cv.put("DocType", DocTypeFP);
        cv.put("BatchNo", batch);
        cv.put("reDownload", reDwl);
        long results = db.insert("PO", null, cv);
        return results != -1;
    }

    //INSERT History Price
    public boolean insertHistoryPrice(String DocType, String AccNo, String ItemCode, String DocNo, String DocDate, String Location, String Agent, String Description, Float Qty, String UOM, Float UnitPrice, Float Discount, Float SubTotal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_HIS, DocType);
        cv.put(COL2_HIS, AccNo);
        cv.put(COL3_HIS, ItemCode);
        cv.put(COL4_HIS, DocNo);
        cv.put(COL5_HIS, DocDate);
        cv.put(COL6_HIS, Location);
        cv.put(COL7_HIS, Agent);
        cv.put(COL8_HIS, Description);
        cv.put(COL9_HIS, Qty);
        cv.put("UOM", UOM);
        cv.put(COL10_HIS, UnitPrice);
        cv.put(COL11_HIS, Discount);
        cv.put(COL12_HIS, SubTotal);
        long results = db.insert(TABLE_NAME_HP, null, cv);
        return results != -1;
    }

    //INSERT TaxType
    public boolean insertTaxType(String TaxType, String Description, Float TaxRate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL0_TT, TaxType);
        cv.put(COL1_TT, Description);
        cv.put(COL2_TT, TaxRate);
        long results = db.insert(TABLE_NAME_TT, null, cv);
        return results != -1;
    }

    //INSERT Stock Count
    public boolean insertSC(String DocDate, String ItemCode, String Description, String Location,
                            String UOM, Float Qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_SC, DocDate);
        cv.put(COL2_SC, ItemCode);
        cv.put(COL3_SC, Description);
        cv.put(COL4_SC, Location);
        cv.put(COL5_SC, UOM);
        cv.put(COL6_SC, Qty);
        long results = db.insert(TABLE_NAME_SC, null, cv);
        return results != -1;
    }

    //Insert Credit terms
    public boolean insertCreditTerms(String DisplayTerm, String Terms, String TermType, String TermDays, String DiscountDays, String DiscountPercent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DisplayTerm", escapeChar(DisplayTerm));
        cv.put("Terms", escapeChar(Terms));
        cv.put("TermType", escapeChar(TermType));
        cv.put("TermDays", escapeChar(TermDays));
        cv.put("DiscountDays", escapeChar(DiscountDays));
        cv.put("DiscountPercent", escapeChar(DiscountPercent));
        long results = db.insert("CreditTermMaintenance", null, cv);

        return results != -1;
    }

    public boolean insertInv(AC_Class.Invoice invoice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_INV, invoice.getDocNo());
        cv.put(COL2_INV, invoice.getCreatedTimeStamp());
        cv.put(COL3_INV, invoice.getDocDate());
        cv.put(COL4_INV, invoice.getDebtorCode());
        cv.put(COL5_INV, invoice.getDebtorName());
        cv.put(COL6_INV, invoice.getAgent());
        cv.put(COL7_INV, invoice.getDocType());
        cv.put("Signature", invoice.getSignature());
        cv.put("Phone", invoice.getPhone());
        cv.put("Fax", invoice.getFax());
        cv.put("Attention", invoice.getAttention());
        cv.put("Address1", invoice.getAddress1());
        cv.put("Address2", invoice.getAddress2());
        cv.put("Address3", invoice.getAddress3());
        cv.put("Address4", invoice.getAddress4());
        cv.put("Remarks", invoice.getRemarks());
        cv.put("Remarks2", invoice.getRemarks2());
        cv.put("Remarks3", invoice.getRemarks3());
        cv.put("Remarks4", invoice.getRemarks4());
        cv.put("Status", invoice.getStatus());
        cv.put("CreatedUser", invoice.getCreateduser());
        cv.put("LastModifiedDateTime", invoice.getLastModifiedDateTime());
        cv.put("LastModifiedUser", invoice.getLastModifiedUser());
        cv.put("CreditTerm", invoice.getCreditTerm());
        long results = db.insert("Sales", null, cv);
        return (results != -1);
    }

    public boolean insertPurchase(AC_Class.Purchase purchase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DocNo", purchase.getDocNo());
        cv.put("CreatedTimeStamp", purchase.getCreatedTimeStamp());
        cv.put("DocDate", purchase.getDocDate());
        cv.put("CreditorCode", purchase.getCreditorCode());
        cv.put("CreditorName", purchase.getCreditorName());
        cv.put("PurchaseAgent", purchase.getAgent());
        cv.put("Phone", purchase.getPhone());
        cv.put("Fax", purchase.getFax());
        cv.put("Attention", purchase.getAttention());
        cv.put("Remarks", purchase.getRemarks());
        cv.put("Remarks2", purchase.getRemarks2());
        cv.put("Remarks3", purchase.getRemarks3());
        cv.put("Remarks4", purchase.getRemarks4());
        cv.put("LastModifiedDateTime", purchase.getLastModifiedDateTime());
        cv.put("LastModifiedUser", purchase.getLastModifiedUser());
        long results = db.insert("Purchase", null, cv);
        return (results != -1);
    }

    public boolean insertAR(AC_Class.ARPayment arpayment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DocNo", arpayment.getDocNo());
        cv.put("Date", arpayment.getDate());
        cv.put("DebtorCode", arpayment.getDebtorCode());
        cv.put("DebtorName", arpayment.getDebtorName());
        cv.put("Amount", arpayment.getAmount());
        cv.put("Image", arpayment.getImage());
        cv.put("Uploaded", 0);
        cv.put("Remark", arpayment.getRemark());
        cv.put("CreatedUser", arpayment.getCreatedUser());
        cv.put("CreatedTimeStamp", arpayment.getCreatedTimeStamp());
        cv.put("LastModifiedUser", arpayment.getLastModifiedUser());
        cv.put("LastModifiedDateTime", arpayment.getLastModifiedDateTime());
        long results = db.insert("ARPayment", null, cv);
        return (results != -1);
    }


    //INSERT Invoice Details
    public boolean insertSalesDtl(String DocNo, String Location, String ItemCode, String Description,
                                  String UOM, Double Qty, Double UPrice, Double Discount, Double SubTotal,
                                  String TaxType, Double TaxRate, Double TaxValue, Double TotalEx, Double TotalIn,
                                  Integer LineNo, String Remarks, String BatchNo, String Remarks2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_INV_DTL, DocNo);
        cv.put(COL2_INV_DTL, Location);
        cv.put(COL3_INV_DTL, ItemCode);
        cv.put(COL4_INV_DTL, Description);
        cv.put(COL5_INV_DTL, UOM);
        cv.put(COL6_INV_DTL, Qty);
        cv.put(COL7_INV_DTL, UPrice);
        cv.put(COL8_INV_DTL, Discount);
        cv.put(COL9_INV_DTL, SubTotal);
        cv.put(COL10_INV_DTL, TaxType);
        cv.put(COL11_INV_DTL, TaxRate);
        cv.put(COL12_INV_DTL, TaxValue);
        cv.put(COL13_INV_DTL, TotalEx);
        cv.put(COL14_INV_DTL, TotalIn);
        cv.put("LineNo", LineNo);
        cv.put("Remarks", Remarks);
        cv.put("BatchNo", BatchNo);
        cv.put("Remarks2", Remarks2);

        long results = db.insert(TABLE_NAME_INV_DTL, null, cv);
        return results != -1;
    }

    public boolean insertPurchaseDtl(String DocNo, String Location, String ItemCode, String Description,
                                     String UOM, Double Qty, Double UPrice, Double Discount, Double SubTotal,
                                     String TaxType, Double TaxRate, Double TaxValue, Double TotalEx, Double TotalIn,
                                     Integer LineNo, String BatchNo, String Remarks, String Remarks2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DocNo", DocNo);
        cv.put("Location", Location);
        cv.put("ItemCode", ItemCode);
        cv.put("ItemDescription", Description);
        cv.put("UOM", UOM);
        cv.put("Qty", Qty);
        cv.put("UPrice", UPrice);
        cv.put("Discount", Discount);
        cv.put("SubTotal", SubTotal);
        cv.put("TaxType", TaxType);
        cv.put("TaxRate", TaxRate);
        cv.put("TaxValue", TaxValue);
        cv.put("Totalex", TotalEx);
        cv.put("Totalin", TotalIn);
        cv.put("LineNo", LineNo);
        cv.put("BatchNo", BatchNo);
        cv.put("Remarks", Remarks);
        cv.put("Remarks2", Remarks2);

        long results = db.insert("PurchaseDtl", null, cv);
        return results != -1;
    }

    public boolean insertARDtl(String DocNo, String docNumber, String docdate, Double netTotal, Double PaymentAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DocNo", DocNo);
        cv.put("DocNumber", docNumber);
        cv.put("DocDate", docdate);
        cv.put("OrgAmt", netTotal);
        cv.put("PaymentAmount", PaymentAmount);

        long results = db.insert("ARPaymentDtl", null, cv);
        return results != -1;
    }

    //INSERT Upload/Download blank param
    public void insertULDL(String tableName, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_ULDL + " WHERE " + COL0_ULDL + "=?" +
                " AND " + COL3_ULDL + "=?", new String[]{tableName, type});
        boolean checkExists = cursor.getCount() > 0;
        cursor.close();
        if (!checkExists) {
            ContentValues cv = new ContentValues();
            cv.put(COL0_ULDL, tableName);
            cv.put(COL1_ULDL, 0);
            cv.put(COL2_ULDL, "");
            cv.put(COL3_ULDL, type);
            db.insert(TABLE_NAME_ULDL, null, cv);
        }
    }

    //INSERT Upload/Download
    public boolean insertULDL(String tableName, int rowCount, String lastDate, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean checkExists = db.rawQuery("SELECT * FROM " + TABLE_NAME_ULDL + " WHERE " + COL0_ULDL + "=?" +
                " AND " + COL3_ULDL + "=?", new String[]{tableName, type}).getCount() > 0;
        if (checkExists) {
            UpdateUploadDownload(new AC_Class.UploadDownload(tableName, rowCount, lastDate, type));

            return true;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COL0_ULDL, tableName);
            cv.put(COL1_ULDL, rowCount);
            cv.put(COL2_ULDL, lastDate);
            cv.put(COL3_ULDL, type);
            long results = db.insert(TABLE_NAME_ULDL, null, cv);

            return results != -1;
        }
    }

    //INSERT Upload Download
    public boolean insertPayment(String DocNo, String PaymentTime, String PaymentType, String PaymentMethod, Double OriginalAmt, Double PaymentAmt, Double CashChanges, String CCType, String CardNo, String CCExpiryDate, String CCApprovalCode, String ChequeNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_PAYMENT, DocNo);
        cv.put(COL2_PAYMENT, PaymentTime);
        cv.put(COL3_PAYMENT, PaymentType);
        cv.put(COL4_PAYMENT, PaymentMethod);
        cv.put(COL5_PAYMENT, OriginalAmt);
        cv.put(COL6_PAYMENT, PaymentAmt);
        cv.put(COL7_PAYMENT, CashChanges);
        cv.put(COL8_PAYMENT, CCType);
        cv.put(COL9_PAYMENT, CardNo);
        cv.put(COL10_PAYMENT, CCExpiryDate);
        cv.put(COL11_PAYMENT, CCApprovalCode);
        cv.put(COL12_PAYMENT, ChequeNo);
        long results = db.insert(TABLE_NAME_PAYMENT, null, cv);
        if (results == -1) {
            return false;
        } else {
            return true;
        }
    }

    //GET
    public Cursor getModules() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM Modules WHERE Activate='True'", null);
        return data;
    }

    public Cursor getUsers() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM " + TABLE_NAME_USERS, null);
        return data;
    }

    public Cursor getUsersST(String login_ID) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT Username, StockTake  FROM " + TABLE_NAME_USERS + " WHERE Username =?", new String[]{login_ID});
        return data;
    }

    //Login Validation
    public Cursor loginValidate(String login_ID, String Password) {
        SQLiteDatabase database = this.getReadableDatabase();
        Password = Hash.bin2hex(Hash.getHash(Password));
//        Log.i("custDebug", "Hashed input: "+Password);
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_USERS +
                " WHERE Username =? AND Pwd =?", new String[]{login_ID, Password});

        return data;
    }

    //GET ALL URL
    public Cursor getUrl() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM " + TABLE_NAME_SL, null);
        return data;
    }

    //GET ALL printerName
    public Cursor getPrinter() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM Printer", null);
        return data;
    }

    //GET ALL salesFormat
    public Cursor getSalesFormat() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM Report WHERE ReportType='S'", null);
        return data;
    }

    //GET ALL Debtors
    public Cursor getDebtor() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM " + TABLE_NAME_DEBTOR, null);
        return data;
    }

    public Cursor getDebtorLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM Debtor WHERE CompanyName LIKE ? OR Desc2 LIKE ? OR Phone LIKE ? OR Attention LIKE ? OR AccNo LIKE ?", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});
        return data;
    }

    public Cursor getDebtorLikeByAgent(String substring, String AgentFP) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery(
                "SELECT *  FROM Debtor WHERE (CompanyName LIKE ? OR Desc2 LIKE ? OR Phone LIKE ? OR Attention LIKE ?) AND SalesAgent IN (" + AgentFP + ")", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});
        return data;
    }

    public Cursor getCreditorLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM Creditor WHERE CompanyName LIKE ? OR Desc2 LIKE ? OR Phone LIKE ? OR Attention LIKE ?OR AccNo LIKE ?", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" +  substring + "%", "%" + substring + "%"});
        return data;
    }

    //GET ALL Creditors
    public Cursor getCreditor() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM Creditor", null);
        return data;
    }

    //GET Debtor from
//    public String getDebtorName(String accNo)
//    {
//        String debtorName = "";
//
//        SQLiteDatabase database = this.getReadableDatabase();
//        Cursor data = database.rawQuery("SELECT CompanyName FROM "+TABLE_NAME_DEBTOR+
//                " WHERE "+COL2_HIS+" = ?", new String[]{accNo});
//        if (data.moveToNext()) {
//            debtorName = data.getString(0);
////            Log.i("custDebug", "debtorName: "+debtorName);
//        }
//        return debtorName;
//    }
//
//    public String getDebtorAgent(String accNo)
//    {
//        String agent = "";
//
//        SQLiteDatabase database = this.getReadableDatabase();
//        Cursor data = database.rawQuery("SELECT SalesAgent FROM "+TABLE_NAME_DEBTOR+
//                " WHERE "+COL2_HIS+" = ?", new String[]{accNo});
//        if (data.moveToNext()) {
//            agent = data.getString(0);
//        }
//        if (agent.equals("")) {
//            agent = null;
//        }
//        return agent;
//    }
//
//    public String getDebtorTax(String accNo)
//    {
//        String taxType = "";
//
//        SQLiteDatabase database = this.getReadableDatabase();
//        Cursor data = database.rawQuery("SELECT TaxType FROM "+TABLE_NAME_DEBTOR+
//                " WHERE "+COL2_HIS+" = ?", new String[]{accNo});
//        if (data.moveToNext()) {
//            taxType = data.getString(0);
//        }
//        if (taxType.equals("")) {
//            taxType = null;
//        }
//        return taxType;
//    }

    public AC_Class.Invoice getDebtorInfo(String accNo) {
        AC_Class.Invoice myInvoice = null;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT CompanyName, SalesAgent, Taxtype, Phone, Fax, Attention, Address1, Address2, Address3, Address4, DetailDiscount FROM " + TABLE_NAME_DEBTOR +
                " WHERE " + COL2_HIS + " = ?", new String[]{accNo});
        if (data.moveToNext()) {
            myInvoice = new AC_Class.Invoice();
            myInvoice.setDebtorName(data.getString(data.getColumnIndex("CompanyName")));
            myInvoice.setAgent(data.getString(data.getColumnIndex("SalesAgent")));
            myInvoice.setTaxType(data.getString(data.getColumnIndex("TaxType")));
            myInvoice.setPhone(data.getString(data.getColumnIndex("Phone")));
            myInvoice.setFax(data.getString(data.getColumnIndex("Fax")));
            myInvoice.setAttention(data.getString(data.getColumnIndex("Attention")));
            myInvoice.setAddress1(data.getString(data.getColumnIndex("Address1")));
            myInvoice.setAddress2(data.getString(data.getColumnIndex("Address2")));
            myInvoice.setAddress3(data.getString(data.getColumnIndex("Address3")));
            myInvoice.setAddress4(data.getString(data.getColumnIndex("Address4")));
            myInvoice.setDetailDiscount(data.getString(data.getColumnIndex("DetailDiscount")));
        }

        return myInvoice;
    }

    public AC_Class.Purchase getPurchaseInfo(String accNo) {
        AC_Class.Purchase myPurchase = null;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT CompanyName, PurchaseAgent, Taxtype, Phone, Fax, Attention FROM Creditor WHERE AccNo = ?", new String[]{accNo});
        if (data.moveToNext()) {
            myPurchase = new AC_Class.Purchase();
            myPurchase.setCreditorName(data.getString(data.getColumnIndex("CompanyName")));
            myPurchase.setAgent(data.getString(data.getColumnIndex("PurchaseAgent")));
            myPurchase.setTaxType(data.getString(data.getColumnIndex("TaxType")));
            myPurchase.setPhone(data.getString(data.getColumnIndex("Phone")));
            myPurchase.setFax(data.getString(data.getColumnIndex("Fax")));
            myPurchase.setAttention(data.getString(data.getColumnIndex("Attention")));
        }

        return myPurchase;
    }


    //GET ALL Items
//    public Cursor getItem()
//    {
//        SQLiteDatabase database = this.getReadableDatabase();
//        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_ITEM, null);
//        return data;
//    }

    //GET ALL Items
    public AC_Class.StockCount getItemBC(String barcode, AC_Class.StockCount stockCount) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = null;

        data = database.rawQuery("SELECT a.ItemCode, b.Description, a.UOM, a.BarCode, a.Price FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode WHERE a.BarCode = ?", new String[]{barcode});
        if (data.getCount() == 0) {
            data = database.rawQuery("SELECT a.ItemCode, b.Description, a.UOM, a.BarCode, a.Price FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode AND a.UOM=b.BaseUOM WHERE a.ItemCode=?", new String[]{barcode});
        }

        if (data.getCount() != 0) {
            if (data.moveToNext()) {
                stockCount.setItemCode(data.getString(data.getColumnIndex("ItemCode")));
                stockCount.setDescription(data.getString(data.getColumnIndex("Description")));
                stockCount.setUOM(data.getString(data.getColumnIndex("UOM")));
            }
        } else {
            stockCount.setItemCode(null);
            stockCount.setDescription(null);
            stockCount.setUOM(null);
        }
        return stockCount;
    }

    //GET ALL Items
    public Cursor getItemBC(String barcode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = null;

        data = database.rawQuery("SELECT a.ItemCode, b.Description, a.UOM, a.BarCode, a.Price, a.Price2, a.Price3, a.Price4, a.Price5, a.Price6, b.Desc2, b.TaxType, b.HasBatchNo FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode WHERE a.BarCode = ?", new String[]{barcode});

        if (data.getCount() == 0) {
            data = database.rawQuery("SELECT a.ItemCode, b.Description, a.UOM, a.BarCode, a.Price, a.Price2, a.Price3, a.Price4, a.Price5, a.Price6, b.Desc2, b.TaxType, b.HasBatchNo FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode AND a.UOM=b.BaseUOM WHERE a.ItemCode=?", new String[]{barcode});
        }

        return data;
    }



    public Cursor getBarcodeItemDetail(String itemcode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = null;
        data = database.rawQuery("SELECT * FROM Item WHERE ItemCode='" + itemcode + "'", null);

        return data;
    }

    public Cursor getItemBOM(String itemcode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = null;
        data = database.rawQuery("SELECT * FROM ItemBOM WHERE ItemCode='" + itemcode + "'", null);

        return data;
    }

    public Cursor getBarcodeItem(String barcode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = null;
        data = database.rawQuery("SELECT * FROM ItemUOM WHERE BarCode='" + barcode + "' OR ItemCode='" + barcode + "'", null);

        return data;
    }
    public Cursor getSOItem(String substring,String docNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = null;
        data = database.rawQuery(
                "SELECT b.ItemCode, a.UOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.BaseUOM, a.BarCode, b.HasBatchNo, SUM(c.Qty), c.Location FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode JOIN SO c ON b.ItemCode = c.ItemCode and a.UOM = c.UOM WHERE (a.BarCode LIKE ? OR b.ItemCode LIKE ? OR b.Description LIKE ?) AND c.DocNo= '" + docNo + "' Group by a.ItemCode", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});

        return data;
    }


    public Cursor getPOItem(String docNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = null;
        data = database.rawQuery(
                "SELECT b.ItemCode, a.UOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.BaseUOM, a.BarCode, b.HasBatchNo, SUM(c.Qty), c.Location FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode JOIN PO c ON b.ItemCode = c.ItemCode and a.UOM = c.UOM WHERE c.DocNo= '" + docNo + "' Group by a.ItemCode", null);

        return data;
    }

    public Cursor getPOItem_hasBatch(String itemcode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = null;
        data = database.rawQuery(
                "SELECT b.HasBatchNo FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode JOIN PO c ON b.ItemCode = c.ItemCode and a.UOM = c.UOM WHERE a.ItemCode= '" + itemcode + "'", null);

        return data;
    }

    //GET SIMILAR Item(s)////////////////////////////////////////////////
    public Cursor getItemLike(String substring, int mode, String INCLAUSE) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();
        String[] itemCols = new String[]{"ItemCode", "Description", "Desc2", "ItemGroup",
                "ItemType", "TaxType", "PurchaseTaxType", "BaseUOM", "Price", "Price2",
                "BarCode", "ItemCode2"};

        switch (mode) {
            case 0:
                data = database.rawQuery("SELECT b.ItemCode, a.UOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType, b.BaseUOM, a.Price, a.Price2, a.Price3, a.Price4, a.Price5, a.Price6, a.BarCode, a.Shelf, a.Rate, b.ItemCode2, a.MinPrice, a.MaxPrice, b.HasBatchNo FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode WHERE (a.BarCode LIKE ? OR b.ItemCode LIKE ? OR b.Description LIKE ?  OR b.Desc2 LIKE ?) " + INCLAUSE  , new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});
                break;
            case 1:
                data = database.rawQuery("SELECT b.ItemCode,b.ItemCode2, BaseUOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType, b.BaseUOM, b.Price, b.Price2, a.Price3, a.Price4, a.Price5, a.Price6, b.BarCode, a.Shelf, a.Rate, a.MinPrice, a.MaxPrice, b.HasBatchNo FROM Item b JOIN ItemUOM a ON a.ItemCode=b.ItemCode AND a.UOM = b.BaseUOM WHERE (b.BarCode LIKE ? OR b.ItemCode LIKE ? OR b.Description LIKE ? OR b.Desc2 LIKE ?  ) "+ INCLAUSE, new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});
                break;
            case 3:
                data = database.rawQuery("SELECT b.ItemCode, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType , a.UOM AS BaseUOM, a.Price, a.Price2, a.BarCode, b.ItemCode2 FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode WHERE (a.BarCode = ? ) " + INCLAUSE, new String[]{substring});

                if (data.getCount() == 0) {
                    data = database.rawQuery("SELECT b.ItemCode, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType , b.baseUOM, a.Price, a.Price2, a.BarCode, b.ItemCode2 FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode AND a.UOM=b.BaseUOM WHERE a.ItemCode = ? " + INCLAUSE, new String[]{substring});
                }
                break;
                //SalesUOM
            case 4:
                data = database.rawQuery("SELECT b.ItemCode, b.SalesUOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType, b.BaseUOM, a.Price, a.Price2, a.Price3, a.Price4, a.Price5, a.Price6, a.BarCode, a.Shelf, a.Rate,b.ItemCode2, a.MinPrice, a.MaxPrice, b.HasBatchNo FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode  AND a.UOM = b.BaseUOM WHERE (b.BarCode LIKE ? OR b.ItemCode LIKE ? OR b.Description LIKE ? OR b.Desc2 LIKE ? ) " + INCLAUSE, new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});
                break;
                //PurchaseUOM
            case 5:
                data = database.rawQuery("SELECT b.ItemCode, b.ItemCode2,b.PurchaseUOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType, b.BaseUOM, b.Price, b.Price2, b.BarCode, b.HasBatchNo FROM Item b WHERE (b.BarCode LIKE ? OR b.ItemCode LIKE ? OR b.Description LIKE ? OR b.Desc2 LIKE ? ) " + INCLAUSE, new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});
                break;

            case 6:
                data = database.rawQuery("SELECT b.ItemCode, BaseUOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType, b.BaseUOM, b.Price, b.Price2, b.BarCode, b.HasBatchNo FROM Item b WHERE (b.BarCode LIKE ? OR b.ItemCode LIKE ? OR b.Description LIKE ? OR b.Desc2 LIKE ?  ) "+ INCLAUSE, new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});
                break;

        }
        return data;
    }

    public Cursor getBatchLike(String item, String substring, String uom, String location, int mode) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();
        String[] itemCols = new String[]{"BatchNo"};

        switch (mode) {
            case 0:
                data = database.rawQuery("SELECT a.ItemCode,a.BatchNo,a.Description,a.ManufacturedDate,a.ExpiryDate,b.BalQty, b.UOM, b.Location FROM ItemBatch a, StockBalance b WHERE a.BatchNo=b.BatchNo and a.ItemCode=b.ItemCode AND BalQty>0 AND a.ItemCode=? AND upper(b.UOM) = upper(?) AND b.Location =? AND a.BatchNo LIKE ? AND (a.ExpiryDate >=  strftime('%Y/%m/%d', date('now')) OR a.ExpiryDate IS NULL)  ORDER BY a.ManufacturedDate DESC", new String[]{item, uom, location ,"%" + substring + "%"});
                break;

            case 1:
                data = database.rawQuery("SELECT a.ItemCode,a.BatchNo,a.Description,a.ManufacturedDate,a.ExpiryDate FROM ItemBatch a WHERE (a.ExpiryDate >=  strftime('%Y/%m/%d', date('now'))OR a.ExpiryDate IS NULL) AND a.ItemCode = ? AND a.BatchNo LIKE ? ORDER BY a.ManufacturedDate DESC", new String[]{ item ,"%" + substring + "%"});
                break;

           }
        return data;
    }

    public Cursor getFGLike(String substring, int mode) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();
        String[] itemCols = new String[]{"BatchNo"};

        switch (mode) {
            case 0:
                data = database.rawQuery("SELECT a.ItemCode, b.Description FROM ItemBOM a join Item b ON a.ItemCode = b.ItemCode group by a.ItemCode, b.Description HAVING a.ItemCode LIKE ? OR b.Description LIKE ?", new String[]{"%" + substring + "%","%" + substring + "%"});
                break;
        }
        return data;
    }

    public Cursor getLocationLike(String substring, int mode) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();
        String[] itemCols = new String[]{"BatchNo"};

        switch (mode) {
            case 0:
                data = database.rawQuery("SELECT Location, Description FROM Location Where Location LIKE ? OR Description LIKE ?", new String[]{"%" + substring + "%","%" + substring + "%"});
                break;
        }
        return data;
    }

    public Cursor getPLLocationLike(String substring, int mode, String itemcode) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();
        String[] itemCols = new String[]{"BatchNo"};

        switch (mode) {
            case 0:
                data = database.rawQuery("SELECT a.Location, a.Description, SUM(b.BalQty)  FROM Location a INNER JOIN StockBalance b ON a.Location = b.Location Where (a.Location LIKE ? OR a.Description LIKE ?) AND b.ItemCode= ? GROUP by a.Location", new String[]{"%" + substring + "%","%" + substring + "%", itemcode});
                break;
        }
        return data;
    }

    public Bitmap getItemImage(String itemCode) {
        Cursor data;
        SQLiteDatabase database = this.getReadableDatabase();
        String[] itemCols = new String[]{"Image"};
        data = null;

        try {
            data = database.query(TABLE_NAME_ITEM_IMAGE, itemCols, "ItemCode = ?",
                    new String[]{itemCode}, "", "", "");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }
        if (data.getCount() > 0) {
            if (data.moveToNext()) {
                if (data.getString(0) != null) {
                    byte[] decodedString = null;
                    decodedString = Base64.decode(data.getString(0), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
                            decodedString.length);
                    //Bitmap.createScaledBitmap(decodedByte, 20, 20, true);
                    return decodedByte;
                }
            }
        }
        return null;
    }

    public Bitmap getARImage(String docNo) {
        Cursor data;
        SQLiteDatabase database = this.getReadableDatabase();
        String[] itemCols = new String[]{"Image"};
        data = null;

        try {
            data = database.query("ARPayment", itemCols, "DocNo = ?",
                    new String[]{docNo}, "", "", "");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }
        if (data.getCount() > 0) {
            if (data.moveToNext()) {
                if (data.getString(0) != null) {
                    byte[] decodedString = null;
                    byte[] b = data.getString(data.getColumnIndex("Image")).getBytes();
                    b = Base64.decode(b, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(b, 0,
                            b.length);
                    //Bitmap.createScaledBitmap(decodedByte, 20, 20, true);
                    return decodedByte;
                }
            }
        }
        return null;
    }

    //GET ALL Invoice Numbering
    public Cursor getRef(String typeFP) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN + " WHERE Type = '" + typeFP + "' LIMIT 1", null);
        return data;
    }

    //Get Invoice Number
    public String getNextNo() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN +
                " WHERE Type = 'S' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }

    //Get Invoice Number
    public String getNextARNo() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN +
                " WHERE Type = 'AR' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }


    //Get Invoice Number
    public String getNextNoPurchase() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN +
                " WHERE Type = 'P' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }

    public String getNextTransferNo() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN +
                " WHERE Type = 'T' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }

    public String getNextStockTakeNo() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN +
                " WHERE Type = 'ST' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }

    public String getNextStockAssemblyNo() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN +
                " WHERE Type = 'SA' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }

    //Get Invoice Number
    public String getNextPackingListNo() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM AutoNumbering WHERE Type = 'PL' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }

    //Get Invoice Number
    public String getNextPurchasePackingListNo() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM AutoNumbering WHERE Type = 'PPL' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }

    //Get Invoice Number
    public String getNextNoAR() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN +
                " WHERE Type = 'AR' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }

    public String getNextNoSA() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN +
                " WHERE Type = 'SA' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }


    //GET ALL Tax Type
    public Cursor getTaxType() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_TT, null);
        return data;
    }

    //GET ALL Agent
    public Cursor getAgent() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM Sales_Agent", null);
        return data;
    }

    //GET ALL Agent
    public Cursor getFG() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.ItemCode, b.Description FROM ItemBOM a join Item b ON a.ItemCode = b.ItemCode group by a.ItemCode, b.Description", null);
        return data;
    }

    //GET ALL PurchaseAgent
    public Cursor getPurchaseAgent() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM PurchaseAgent", null);
        return data;
    }

    //GET ALL Location
    public Cursor getLocation() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_LOCATION, null);
        return data;
    }

    //GET ALL ItemUOM by ItemCode
    public Cursor getAllItemUOM(String ItemCode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM ItemUOM WHERE ItemCode =?" , new String[]{ItemCode});
        return data;
    }

    //GET ALL Invoice Header
    public Cursor getSales() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM Sales", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    //GET ALL Invoice Header by doc no
    public Cursor getSales(String DocNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM Sales WHERE DocNo =?" , new String[]{DocNo});
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    //Set all Stock Count == uploaded
    public boolean setStockCountUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE StockCount SET Exported=1 WHERE Exported=0");
        return true;
    }

    //Set all Stock Count == uploaded
    public boolean setStockTakeUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE StockTake SET Exported=1 WHERE Exported=0");
        return true;
    }

    public void setStockTakeUploadedTo(String docNo, Integer no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE StockTake SET Exported=" + no + " WHERE ID='" + docNo + "'");
    }

    public void setStockAssemblyUploadedTo(String docNo, Integer no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE StockAssembly SET Uploaded= " + no + " WHERE DocNo='" + docNo + "'");
    }

    //Set all Invoice header == uploaded
    public boolean setSalesUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Sales SET Uploaded=1 WHERE Uploaded=0");
        return true;
    }

    public boolean setPaymentUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Payment SET Uploaded=1 WHERE Uploaded=0");
        return true;
    }

    public void setSalesUploadedTo(String docNo, Integer no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Sales SET Uploaded=" + no + " WHERE DocNo='" + docNo + "'");
    }

    public boolean setTransferUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE TRANSFER SET Uploaded=1 WHERE Uploaded=0");
        return true;
    }

    public void setTransferUploadedTo(String docNo, Integer no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE TRANSFER SET Uploaded=" + no + " WHERE DocNo='" + docNo + "'");
    }

    public boolean setPackingListUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE DO SET Uploaded=1 WHERE Uploaded=0");
        return true;
    }

    public boolean setPurPackingListUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE PI SET Uploaded=1 WHERE Uploaded=0");
        return true;
    }

    public boolean setSAUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE StockAssembly SET Uploaded=1 WHERE Uploaded=0");
        return true;
    }

    public boolean setPackingListUploadedTally() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE DO SET Uploaded=1 WHERE Uploaded=0 and IsTally = 1");
        return true;
    }

    public boolean setPurPackingListUploadedTally() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE PI SET Uploaded=1 WHERE Uploaded=0 and IsTally = 1");
        return true;
    }

    public void setPackingListUploadedTo(String docNo, Integer no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE DO SET Uploaded=" + no + " WHERE DocNo='" + docNo + "'");
    }

    public void setPurchasePackingListUploadedTo(String docNo, Integer no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE PI SET Uploaded=" + no + " WHERE DocNo='" + docNo + "'");
    }

    public void setARUploadedTo(String docNo, Integer no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE ARPayment SET Uploaded=" + no + " WHERE DocNo='" + docNo + "'");
    }

    public boolean setPurchaseUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Purchase SET Uploaded=1 WHERE Uploaded=0");
        return true;
    }

    public void setPurchaseUploadedTo(String docNo, Integer no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Purchase SET Uploaded=" + no + " WHERE DocNo='" + docNo + "'");
    }

    public void setJobSheetUploadedTo(String docNo, Integer no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE JobSheet SET Uploaded=" + no + " WHERE DocNo='" + docNo + "'");
    }

    public void setStockReceiveUploadedTo(String docNo, Integer no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE StockReceive SET Uploaded=" + no + " WHERE DocNo='" + docNo + "'");
    }

    //GET ALL Invoice Detail
    public Cursor getSales_Dtl() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM " + TABLE_NAME_INV_DTL, null);
        return data;
    }

    public Cursor getSalesDebtor() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("select DebtorName from Sales", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getDebtorDocNo(String debtorName) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("select DocNo from Sales WHERE DebtorName='" + debtorName + "'", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getDebtorDocNobyQty() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("select DebtorName,COUNT(*) AS NUM from Sales GROUP BY DebtorName", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getTop10DebtorQty() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DebtorName,SUM(Qty) AS [TotalQty] from Sales INNER JOIN SalesDtl ON Sales.DocNo=SalesDtl.DocNo GROUP BY DebtorName ORDER BY TotalQty Desc LIMIT 10", null);
        return data;
    }

    public Cursor getTopSales() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("select a.DebtorName, SUM(b.TotalIn) AS Total from Sales a, SalesDtl b where a.DocNo = b.DocNo group by a.DebtorName order by Total DESC LIMIT 10", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getTop10ItemsBySales() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("select ItemCode, SUM(Totalin) AS Total from SalesDtl group by ItemCode order by Total DESC LIMIT 10", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getTopStockBalance() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT ItemCode,BalQty FROM StockBalance GROUP BY ItemCode ORDER BY BalQty DESC LIMIT 10",null);
        return data;
    }

    public Cursor getLowStockBalance() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT ItemCode,BalQty FROM StockBalance WHERE BalQty > 0 GROUP BY ItemCode ORDER BY BalQty ASC LIMIT 10",null);
        return data;
    }

    public Cursor getTop10ItemsByQty() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("select ItemCode,SUM(Qty) AS Total from SalesDtl group by ItemCode order by Total DESC LIMIT 10", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getAllItemsInSales() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("select ItemCode,ItemDescription from SalesDtl", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getTopItemsBySales(String itemCode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("select SUM(Totalin) from SalesDtl WHERE ItemCode='" + itemCode + "'", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getTopItemsByQty(String itemCode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("select SUM(Qty) from SalesDtl WHERE ItemCode='" + itemCode + "'", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getAllDocumentsST() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor data = db.rawQuery("SELECT ID, CreatedTimeStamp, DocDate, SalesAgent, Location, Remarks, Exported from StockTake ORDER BY DocDate ASC", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getAllDocumentsSTDesc() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor data = db.rawQuery("SELECT ID as DocNo, CreatedTimeStamp, DocDate as Date, SalesAgent, Location, Remarks as Remark, Exported as Uploaded from StockTake ORDER BY (substr(DocDate,7,4)||substr(DocDate,4,2)||substr(DocDate,1,2)) Desc, ID DESC", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getAllDocumentsSADesc() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor data = db.rawQuery("SELECT CreatedTimeStamp, DocNo,Date, FGLocation, Remark, Uploaded from StockAssembly ORDER BY (substr(Date,7,4)||substr(Date,4,2)||substr(Date,1,2)) desc, DocNo DESC", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getFilteredDocumentsST(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor data = db.rawQuery("select ID, CreatedTimeStamp, DocDate, Location, SalesAgent, Remarks, Exported from StockTake WHERE ID LIKE '%" + id + "%' OR Remarks LIKE '%" +
                id + "%' OR SalesAgent LIKE '%" + id + "%'", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }


    public Cursor getFilteredDocumentsSA(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor data = db.rawQuery("select DocNo, CreatedTimeStamp, Date, Remark, Uploaded from StockAssembly WHERE DocNo LIKE '%" + id + "%' OR Remark LIKE '%" +
                id + "%' OR Date LIKE '%" + id + "%'", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    public Cursor getTodaySalesDocNo(String date) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("select DocNo from Sales WHERE date(CreatedTimeStamp) = date('" + date + "')", null);
        Log.i("custDebug", DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    //GET Invoice Detail by DocNo
    public Cursor getSalesDtl(String DocNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_INV_DTL + " WHERE DocNo =?" + "ORDER BY LineNo ASC", new String[]{DocNo});
        return data;
    }

    //GET Total Price by DocNo
    public Cursor getTotalPrice(String DocNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo, SUM(Totalex), SUM(TaxValue), SUM(Totalin) FROM " + TABLE_NAME_INV_DTL + " WHERE DocNo =?", new String[]{DocNo});
        return data;
    }

    public float getSalesDtlQty(AC_Class.StockBalance stockBalance) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT -SUM(SalesDtl.Qty) FROM Sales JOIN SalesDtl ON Sales.DocNo = SalesDtl.DocNo WHERE Sales.Uploaded=0 AND SalesDtl.ItemCode=? AND SalesDtl.UOM=? AND SalesDtl.Location=?", new String[]{stockBalance.getItemCode(), stockBalance.getUOM(), stockBalance.getLocation()});
        if (data.moveToNext()) {
            return data.getFloat(0);
        } else {
            return 0.00f;
        }
    }

    public float getSalesDtlQtyBatch(AC_Class.StockBalance stockBalance) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT -SUM(SalesDtl.Qty) FROM Sales JOIN SalesDtl ON Sales.DocNo = SalesDtl.DocNo WHERE Sales.Uploaded=0 AND SalesDtl.ItemCode=? AND SalesDtl.UOM=? AND SalesDtl.Location=? AND SalesDtl.BatchNo=?", new String[]{stockBalance.getItemCode(), stockBalance.getUOM(), stockBalance.getLocation(), stockBalance.getBatchNo()});
        if (data.moveToNext()) {
            return data.getFloat(0);
        } else {
            return 0.00f;
        }
    }


    //GET SalesDtl History Price by ItemCode by UOM in Stock Inquiry
    public Cursor getSalesDtlHistoryPrice(String ItemCode, String UOM) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT SUM(SalesDtl.Qty) AS [Qty], ItemUOM.Rate FROM Sales JOIN SalesDtl ON Sales.DocNo = SalesDtl.DocNo INNER JOIN ItemUOM ON SalesDtl.ItemCode=ItemUOM.ItemCode AND SalesDtl.UOM=ItemUOM.UOM WHERE Sales.Uploaded=0 AND SalesDtl.ItemCode=? AND SalesDtl.UOM=? GROUP BY ItemUOM.Rate", new String[]{ItemCode, UOM});
        return data;
    }
    public Cursor getSalesDtlHistoryPricebyItemCode(String ItemCode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT SUM(SalesDtl.Qty*ItemUOM.Rate) FROM Sales JOIN SalesDtl ON Sales.DocNo = SalesDtl.DocNo INNER JOIN ItemUOM ON SalesDtl.ItemCode=ItemUOM.ItemCode AND SalesDtl.UOM=ItemUOM.UOM WHERE Sales.Uploaded=0 AND SalesDtl.ItemCode=? GROUP BY ItemUOM.UOM", new String[]{ItemCode});
        return data;
    }


    //GET SalesDtl History Price by ItemCode
    public Cursor getUOMbyItemCode(String ItemCode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.UOM,b.Description FROM ItemUOM a INNER JOIN Item b ON a.ItemCode=b.ItemCode LEFT JOIN StockBalance c ON b.ItemCode =  c.ItemCode WHERE a.ItemCode=? group by a.UOM", new String[]{ItemCode});
        return data;
    }

    public Cursor getHistoryPriceLike(String ItemCode, String UOM, String DebtorName) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT * FROM HistoryPrice a JOIN Debtor b ON a.AccNo = b.AccNo WHERE a.ItemCode =? AND a.UOM=? AND CompanyName LIKE ?", new String[]{ItemCode, UOM, "%" + DebtorName + "%"});

        return data;
    }

    public Cursor getHistoryPriceByDebtorByAgent(String ItemCode, String UOM, String DebtorCode, String Agent) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery(
                "SELECT * FROM HistoryPrice a JOIN Debtor b ON a.AccNo = b.AccNo WHERE a.ItemCode =? AND a.UOM=? AND a.AccNo=? AND Agent IN  (" + Agent+ ")", new String[]{ItemCode, UOM, DebtorCode});

        return data;
    }

    public Cursor getItem(String itemcodeFP, String uomFP) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT b.ItemCode, a.UOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType, b.BaseUOM, a.Price, a.Price2, a.Price3, a.Price4, a.Price5, a.Price6, a.BarCode, a.Shelf, a.Rate, b.ItemCode2, a.MinPrice, a.MaxPrice, b.HasBatchNo FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode WHERE b.ItemCode=? AND a.UOM=?", new String[]{itemcodeFP, uomFP});

        return data;
    }

    public Cursor getHistoryPriceByDebtor(String ItemCode, String UOM, String DebtorCode) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT * FROM HistoryPrice a JOIN Debtor b ON a.AccNo = b.AccNo WHERE a.ItemCode =? AND a.UOM=? AND a.AccNo=?", new String[]{ItemCode, UOM, DebtorCode});

        return data;
    }

    public Cursor getHistoryPriceByAgentLike(String ItemCode, String UOM, String DebtorName, String Agent) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery(
                "SELECT * FROM HistoryPrice a JOIN Debtor b ON a.AccNo = b.AccNo WHERE a.ItemCode =? AND a.UOM=? AND CompanyName LIKE ? AND Agent IN (" + Agent+ ")", new String[]{ItemCode, UOM, "%" + DebtorName + "%"});

        return data;
    }

    //GET All Stock Count
    public Cursor getStockCount() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocDate, ItemCode, Description, Location, UOM, Qty, Exported FROM StockCount WHERE Exported=0", null);
        return data;
    }

    //GET All Stock Take Document
    public Cursor getStockTakeToShowInDetail(String id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocDate, Location, SalesAgent, Remarks FROM StockTake WHERE ID='" + id + "'", null);
        return data;
    }

    //GET All Stock Take Document
    public Cursor getStockAssemblyToShowInDetail(String id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM StockAssembly WHERE DocNo='" + id + "'", null);
        return data;
    }

    public Cursor getStockTakeItemTotalQty(String id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT SUM(Qty) FROM StockTakeDetail WHERE StockDocNo='" + id + "'", null);
        return data;
    }

    //GET Stock Count like _
    public Cursor getStockCountLike(String substring, int mode) {
        Cursor data;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();
        String[] itemCols = new String[]{"DocDate", "ItemCode", "Description", "Location", "UOM",
                "Qty", "Exported"};
        switch (mode) {
            case 0:
                data = database.query(TABLE_NAME_SC, itemCols, COL2_SC + " LIKE ? OR " +
                        COL3_SC + " LIKE ?", new String[]{"%" + substring + "%",
                        "%" + substring + "%"}, "", "", "ItemCode ASC");
                return data;
//                temp = "ItemCode";
//                break;

            case 1:
                temp = "ItemCode";
                break;

            case 2:
                temp = "Description";
                break;
        }
        data = null;
        try {
            data = database.query(TABLE_NAME_SC, itemCols, temp + " LIKE ?",
                    new String[]{"%" + substring + "%"}, "", "", "ItemCode ASC");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }
        return data;
    }

    //GET ALL Taxrate by TaxType
    public Cursor getTaxTypeValue(String taxtype) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT TaxRate FROM " + TABLE_NAME_TT + " WHERE TaxType =?", new String[]{taxtype});
        return data;
    }

    //Get ALL Payment Details
    public Cursor getPaymentToUpload(String DocNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM " + TABLE_NAME_PAYMENT + " WHERE DocNo =?", new String[]{DocNo});
        return data;
    }

    //Get Printing Details (Inv Header) by DocNo
    public Cursor getInvoiceHeaderPrint(String docNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT Sales.DocNo, Sales.CreatedTimeStamp, Sales.DocDate, Sales.DebtorCode, Sales.DebtorName, Sales.SalesAgent, Sales.DocType, Sales.Signature, Sales.Phone, Sales.Fax, Sales.Attention, Debtor.Address1, Debtor.Address2, Debtor.Address3, Debtor.Address4, Sales.Remarks FROM Sales INNER JOIN Debtor ON Sales.DebtorCode=Debtor.AccNo WHERE DocNo =?", new String[]{docNo});
        return data;
    }

    //Get Printing Details (Inv Header) by DocNo
    public Cursor getPurchaseHeaderPrint(String docNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT Purchase.DocNo, Purchase.CreatedTimeStamp, Purchase.DocDate, Purchase.CreditorCode, Purchase.CreditorName, Purchase.PurchaseAgent, Purchase.Phone, Purchase.Fax, Purchase.Attention, Creditor.Address1, Creditor.Address2, Creditor.Address3, Creditor.Address4, Purchase.Remarks, Purchase.Remarks2, Purchase.Remarks3, Purchase.Remarks4 FROM Purchase INNER JOIN Creditor ON Purchase.CreditorCode=Creditor.AccNo WHERE DocNo =?", new String[]{docNo});
        return data;
    }

    //Get Printing Details (AR Header) by DocNo
    public Cursor getARHeaderPrint(String docNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM ARPayment WHERE DocNo =?", new String[]{docNo});
        return data;
    }

    //Get Printing Details (Inv Detail) by DocNo
    public Cursor getInvoiceDetailsPrint(String docNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM SalesDtl WHERE DocNo =? ", new String[]{docNo});
        return data;
    }

    //Get Printing Details (Inv Detail) by DocNo
    public Cursor getPurchaseDetailsPrint(String docNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM PuchaseDetail WHERE DocNo =? ", new String[]{docNo});
        return data;
    }

    //Get Printing Details (Payment) by DocNo
    public Cursor getInvoicePaymentsPrint(String docNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_PAYMENT + " WHERE DocNo =?", new String[]{docNo});
        return data;
    }

    //Get SalesDtl
    public Cursor getSalesSub() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT b.DocNo, a.DocNo, a.Doc.Date, a.SalesAgent, b.SubTotal FROM Sales a JOIN SalesDtl b ON b.DocNo = a.DocNo", new String[]{"0"});
        return data;
    }

    // Get Item Batch by ItemCode
    public Cursor getAllItemBatch(String ItemCode, String UOM, String Location)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.ItemCode,a.BatchNo,a.Description,a.ManufacturedDate,a.ExpiryDate,b.BalQty, b.UOM, b.Location FROM ItemBatch a, StockBalance b WHERE a.BatchNo=b.BatchNo and a.ItemCode=b.ItemCode AND BalQty>0 AND a.ItemCode=? AND upper(b.UOM) = upper(?) AND b.Location =? AND (a.ExpiryDate >=  strftime('%Y/%m/%d', date('now')) OR a.ExpiryDate IS NULL)  ORDER BY a.ManufacturedDate DESC" ,
                new String[]{ItemCode, UOM, Location});
        return data;
    }

    public Cursor getItemBatch(String ItemCode)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.ItemCode,a.BatchNo,a.Description,a.ManufacturedDate,a.ExpiryDate FROM ItemBatch a WHERE a.ItemCode=? AND (a.ExpiryDate >=  strftime('%Y/%m/%d', date('now')) OR a.ExpiryDate IS NULL) ORDER BY a.ManufacturedDate DESC" ,
                new String[]{ItemCode});
        return data;
    }

    public Cursor getEarManuBatch(String ItemCode, String UOM, String Location)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.BatchNo,a.ManufacturedDate, b.BalQty, b.UOM FROM ItemBatch a, StockBalance b WHERE a.BatchNo=b.BatchNo and a.ItemCode=b.ItemCode AND BalQty>0 AND a.ItemCode=? AND upper(b.UOM) = upper(?) AND b.Location =? AND a.ExpiryDate >=  strftime('%Y/%m/%d', date('now')) AND a.ManufacturedDate is not null order by a.ManufacturedDate ASC" ,
                new String[]{ItemCode, UOM, Location});
        return data;
    }

    public Cursor getLateManuBatch(String ItemCode, String UOM, String Location)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.BatchNo,a.ManufacturedDate, b.BalQty, b.UOM FROM ItemBatch a, StockBalance b WHERE a.BatchNo=b.BatchNo and a.ItemCode=b.ItemCode AND BalQty>0 AND a.ItemCode=? AND upper(b.UOM) = upper(?) AND b.Location =? AND a.ExpiryDate >=  strftime('%Y/%m/%d', date('now')) AND a.ManufacturedDate is not null order by a.ManufacturedDate DESC" ,
                new String[]{ItemCode, UOM, Location});
        return data;
    }

    public Cursor getEarExpBatch(String ItemCode, String UOM, String Location)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.BatchNo,a.ExpiryDate, b.BalQty, b.UOM FROM ItemBatch a, StockBalance b WHERE a.BatchNo=b.BatchNo and a.ItemCode=b.ItemCode AND BalQty>0 AND a.ItemCode=? AND upper(b.UOM) = upper(?) AND b.Location =? AND a.ExpiryDate >=  strftime('%Y/%m/%d', date('now')) AND a.ExpiryDate is not null order by a.ExpiryDate ASC" ,
                new String[]{ItemCode, UOM, Location});
        return data;
    }

    public Cursor getLateExpBatch(String ItemCode, String UOM, String Location)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.BatchNo,a.ExpiryDate, b.BalQty, b.UOM FROM ItemBatch a, StockBalance b WHERE a.BatchNo=b.BatchNo and a.ItemCode=b.ItemCode AND BalQty>0 AND a.ItemCode=? AND upper(b.UOM) = upper(?) AND b.Location =? AND a.ExpiryDate >=  strftime('%Y/%m/%d', date('now')) AND a.ExpiryDate is not null order by a.ExpiryDate DESC" ,
                new String[]{ItemCode, UOM, Location});
        return data;
    }

    // Get Stock Count by itemcode and uom
    public Cursor getAllStockCount(String itemCode, String uom) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM " + TABLE_NAME_SC + " WHERE " + COL2_SC + "=? " +
                "AND " + COL5_SC + "=?", new String[]{itemCode, uom});
        return data;
    }

    // Get Stock Count by itemcode, uom and location
    public float getAllStockCount(AC_Class.StockBalance stockBalance) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM " + TABLE_NAME_SC + " WHERE " + COL2_SC + "=? " +
                        "AND " + COL5_SC + "=? AND " + COL4_SC + "=?",
                new String[]{stockBalance.getItemCode(), stockBalance.getUOM(), stockBalance.getLocation()});
        if (data.moveToNext()) {
            return data.getFloat(5);
        } else {
            return 0.00f;
        }
    }

    // Get Stock Count by itemcode and uom
    public Cursor getAllStockCount(String itemCode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT BalQty FROM StockBalance WHERE ItemCode =?",
                new String[]{itemCode});
        return data;
    }

    // Get Stock Balance by ItemCode
    public Cursor getAllStockBalance(String ItemCode, String UOM) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.ItemCode, a.UOM, a.Location,a.BalQty,a.BatchNo FROM StockBalance a INNER JOIN ItemBatch b ON a.BatchNo = b.BatchNo  AND a.ItemCode = b.ItemCode WHERE a.ItemCode =? AND upper(a.UOM) = upper(?) ORDER BY a.Location,b.ManufacturedDate DESC", new String[]{ItemCode, UOM});
        return data;
    }

    public Cursor getAllUTDCost(String ItemCode, String UOM) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT ItemCode, UOM, Location, BatchNo, UTDQty, ROUND(UTDCost,2)/ROUND(UTDQty, 2) as UTDCost FROM UTDCost WHERE ItemCode =? AND upper(UOM) = upper(?) ", new String[]{ItemCode, UOM});
        return data;
    }

    public Cursor getAllStockBalance2(String ItemCode, String UOM) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *, SUM(BalQty) AS Total FROM StockBalance WHERE ItemCode =? AND upper(UOM) = upper(?) group by location", new String[]{ItemCode, UOM});
        return data;
    }

    // Get Stock Balance by ItemCode
    public Cursor getStockBalance(String ItemCode, String UOM) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT SUM(BalQty) FROM StockBalance WHERE ItemCode =? AND upper(UOM) = upper(?)", new String[]{ItemCode, UOM});
        return data;
    }


    public Cursor getStockBalanceBatch(String ItemCode, String BatchNo, String Location, String uom) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT BalQty FROM StockBalance WHERE ItemCode =? AND BatchNo =? AND Location =? AND upper(UOM) = upper(?)", new String[]{ItemCode, BatchNo, Location, uom});
        return data;
    }

    // Get Stock Balance by ItemCode
    public Cursor getDebtorName2(String debtorcode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT Desc2 FROM Debtor WHERE AccNo = ?", new String[]{debtorcode});
        return data;
    }

    public Cursor getUploadchkInvoice() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo"
                + " FROM Sales WHERE Uploaded=?", new String[]{"0"});
        return data;
    }

    public Cursor getUploadchkPayment() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo"
                + " FROM Payment WHERE Uploaded=?", new String[]{"0"});
        return data;
    }


    public Cursor getUploadchkPurchase() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo"
                + " FROM Purchase WHERE Uploaded=?", new String[]{"0"});
        return data;
    }

    public Cursor getUploadchkARPayment() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo"
                + " FROM ARPayment WHERE Uploaded=?", new String[]{"0"});
        return data;
    }

    public Cursor getUploadchkStockAssembly() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo"
                + " FROM StockAssembly WHERE Uploaded=?", new String[]{"0"});
        return data;
    }

    public Cursor getUploadchkStockTake() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT ID as DocNo"
                + " FROM StockTake WHERE Exported=?", new String[]{"0"});
        return data;
    }


    public Cursor getUploadchkTransfer() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo"
                + " FROM TRANSFER WHERE Uploaded=?", new String[]{"0"});
        return data;
    }

    public Cursor getUploadchkPL() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo"
                + " FROM DO WHERE Uploaded=?", new String[]{"0"});
        return data;
    }

    public Cursor getUploadchkPPL() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo"
                + " FROM PI WHERE Uploaded=?", new String[]{"0"});
        return data;
    }



    // Get Stock Balance by ItemCode, Batch No
    public Cursor getStockBalanceBatchNo(String ItemCode, String UOM, String BatchNo) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT SUM(BalQty) FROM StockBalance WHERE ItemCode =? AND upper(UOM) = upper(?) AND BatchNo = ? AND BatchNo IS NOT NULL", new String[]{ItemCode, UOM, BatchNo});
        return data;
    }

    // Get Stock Balance by ItemCode
    public Cursor getBaseStockBalance(String ItemCode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT SUM(a.BalQty) FROM StockBalance a INNER JOIN Item b ON a.ItemCode=b.ItemCode AND upper(a.UOM)=upper(b.BaseUOM) WHERE a.ItemCode =?", new String[]{ItemCode});
        return data;
    }

    // Get Stock Balance by ItemCode and UOM
    public Cursor getBaseStockBalancebyUOM(String ItemCode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT SUM(a.BalQty*b.rate) FROM StockBalance a INNER JOIN ItemUOM b ON a.ItemCode=b.ItemCode AND upper(a.UOM)=upper(b.UOM) WHERE a.ItemCode =?", new String[]{ItemCode});
        return data;
    }

    //Get Data For Invoice Menu
    public Cursor getInvoiceMenuLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT " + "a." + COL7_INV + ", " + "a." + COL3_INV + ", "
                + "a." + COL1_INV + ", " + "a." + COL4_INV + ", " + "a." + COL5_INV + ", "
                + "a." + COL6_INV + ", " + "SUM " + "( " + "b." + COL13_INV_DTL + ") " + ", "
                + "SUM " + "( " + "b." + COL12_INV_DTL + ") " + ", "
                + "SUM " + "( " + "b." + COL14_INV_DTL + "), " + COL8_INV + ", Status, a.Remarks, a.Remarks2, a.Remarks3, a.Remarks4 FROM Sales a JOIN SalesDtl b "
                + " ON a. " + COL1_INV + " = b. " + COL1_INV_DTL
                + " WHERE a.DocNo LIKE ? OR a.SalesAgent LIKE ? OR a.DebtorName LIKE ? OR a.Status LIKE ?GROUP BY " + "a." + COL1_INV + " ORDER BY a.DocNo", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});
        // DocType, DocDate, DocNo, DebtorCode, DebtorName, SalesAgent, TotalEx,
        // TotalValue, TotalIn, Uploaded
        return data;
    }

    public Cursor getInvoiceMenuDescLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT " + "a.DocType, " + "a.DocDate, "
                + "a.DocNo, " + "a.DebtorCode, " + "a.DebtorName, c.Desc2,"
                + "a.SalesAgent, SUM(b.Totalex), SUM(b.TaxValue), SUM(b.Totalin), Uploaded, Status, a.Remarks, a.Remarks2, a.Remarks3, a.Remarks4 FROM Sales a JOIN SalesDtl b ON a.DocNo = b.DocNo INNER JOIN Debtor c ON a.DebtorCode = c.AccNo WHERE a.DocNo LIKE ? OR a.SalesAgent LIKE ? OR a.DebtorName LIKE ? OR a.Status LIKE ? OR a.Remarks LIKE ? OR a.Remarks2 LIKE ? OR a.Remarks3 LIKE ? OR a.Remarks4 LIKE ? GROUP BY " + "a.DocNo ORDER BY (substr(a.DocDate,7,4)||substr(a.DocDate,4,2)||substr(a.DocDate,1,2)) desc ,a.DocNo DESC", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});
        // DocType, DocDate, DocNo, DebtorCode, DebtorName, SalesAgent, TotalEx,
        // TotalValue, TotalIn, Uploaded
        return data;
    }


    //Get Data For Invoice Menu
    public Cursor getARPaymentMenuLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT DocNo, Date, DebtorCode, DebtorName, Amount, Uploaded, CreatedTimeStamp, CreatedUser, Remark FROM ARPayment WHERE DocNo LIKE ? OR DebtorCode LIKE ? OR DebtorName LIKE ? OR Remark LIKE ? ORDER BY DocNo",new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%" , "%" + substring + "%"});

        return data;
    }

    public Cursor getARPaymentMenuDescLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT DocNo, Date, DebtorCode, DebtorName, Amount, Uploaded, CreatedTimeStamp, CreatedUser, Remark FROM ARPayment WHERE DocNo LIKE ? OR DebtorCode LIKE ? OR DebtorName LIKE ? OR Remark LIKE ? ORDER BY (substr(Date,7,4)||substr(Date,4,2)||substr(Date,1,2)) desc,DocNo DESC",new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%" , "%" + substring + "%"});

        return data;
    }

    //Get Data For Invoice Menu
    public Cursor getPurchaseMenuLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT a.DocDate, a.DocNo, a.CreditorCode, a.CreditorName, a.PurchaseAgent, SUM(b.TotalEx), SUM(b.TaxValue), SUM(b.TotalIn), Uploaded, a.Remarks, a.Remarks2, a.Remarks3, a.Remarks4 FROM Purchase a JOIN PurchaseDtl b ON a.DocNo = b.DocNo WHERE a.DocNo LIKE ? OR a.PurchaseAgent LIKE ? OR a.CreditorName LIKE ? GROUP BY a.DocNo ORDER BY a.DocNo", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});

        return data;
    }

    public Cursor getPurchaseMenuDescLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT a.DocDate, a.DocNo, a.CreditorCode, a.CreditorName, a.PurchaseAgent, SUM(b.TotalEx), SUM(b.TaxValue), SUM(b.TotalIn), Uploaded, a.Remarks, a.Remarks2, a.Remarks3, a.Remarks4 FROM Purchase a JOIN PurchaseDtl b ON a.DocNo = b.DocNo WHERE a.DocNo LIKE ? OR a.PurchaseAgent LIKE ? OR a.CreditorName LIKE ? GROUP BY a.DocNo ORDER BY (substr(a.DocDate,7,4)||substr(a.DocDate,4,2)||substr(a.DocDate,1,2)) DESC, a.DocNo desc", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});

        return data;
    }

    //Get Data For Transfer Menu
    public Cursor getTransferMenuLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.DocNo, a.DocDate, a.Reason, a.LocationFrom, a.LocationTo, COALESCE(SUM(b.Qty),0) AS TotalQty, a.Uploaded FROM TRANSFER a INNER JOIN TRANSFERDTL b ON a.DocNo = b.DocNo WHERE a.DocNo LIKE ? OR a.Reason LIKE ? GROUP BY a.DocNo, a.DocDate, a.Reason, a.LocationFrom, a.LocationTo ORDER BY a.DocNo ASC", new String[]{"%" + substring + "%", "%" + substring + "%"});
        return data;
    }

    public Cursor getTransferMenuDescLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.DocNo, a.DocDate, a.Reason, a.LocationFrom, a.LocationTo, COALESCE(SUM(b.Qty),0) AS TotalQty, a.Uploaded FROM TRANSFER a INNER JOIN TRANSFERDTL b ON a.DocNo = b.DocNo WHERE a.DocNo LIKE ? OR a.Reason LIKE ? GROUP BY a.DocNo, a.DocDate, a.Reason, a.LocationFrom, a.LocationTo ORDER BY (substr(a.DocDate,7,4)||substr(a.DocDate,4,2)||substr(a.DocDate,1,2)) DESC, a.DocNo DESC", new String[]{"%" + substring + "%", "%" + substring + "%"});
        return data;
    }

    //Get Data For Transfer Menu
    public Cursor getStockTakeMenuLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM StockTake", null);
        return data;
    }

    public Cursor getJobSheetDescLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery(
                "SELECT a.DocType, a.DocDate, a.DocNo, a.DebtorCode, a.DebtorName, c.Desc2, " +
                        "a.SalesAgent, SUM(b.Totalex), SUM(b.TaxValue), SUM(b.Totalin), " +
                        "Uploaded, Status, a.Remarks, a.Remarks2, a.Remarks3, a.Remarks4 " +
                        "FROM Sales a " +
                        "JOIN SalesDtl b ON a.DocNo = b.DocNo " +
                        "INNER JOIN Debtor c ON a.DebtorCode = c.AccNo " +
                        "WHERE a.DocNo LIKE ? " +
                        "OR a.SalesAgent LIKE ? " +
                        "OR a.DebtorName LIKE ? " +
                        "OR a.Status LIKE ? " +
                        "OR a.Remarks LIKE ? " +
                        "OR a.Remarks2 LIKE ? " +
                        "OR a.Remarks3 LIKE ? " +
                        "OR a.Remarks4 LIKE ? " +
                        "GROUP BY a.DocNo " +
                        "ORDER BY (substr(a.DocDate, 7, 4) || substr(a.DocDate, 4, 2) || substr(a.DocDate, 1, 2)) DESC, a.DocNo DESC",
                new String[]{
                        "%" + substring + "%",
                        "%" + substring + "%",
                        "%" + substring + "%",
                        "%" + substring + "%",
                        "%" + substring + "%",
                        "%" + substring + "%",
                        "%" + substring + "%",
                        "%" + substring + "%"}
        );
        return data;
    }


    public AC_Class.Transfer getTransfer(String docNoFP) {
        AC_Class.Transfer myTransfer = null;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor header = database.rawQuery("SELECT * FROM Transfer WHERE DocNo= '"  + docNoFP + "'" , null);
        if (header.getCount() == 1) {
            header.moveToNext();
            myTransfer = new AC_Class.Transfer();
            myTransfer.setDocNo(header.getString(header.getColumnIndex("DocNo")));
            myTransfer.setDocDate(header.getString(header.getColumnIndex("DocDate")));
            myTransfer.setLocationFrom(header.getString(header.getColumnIndex("LocationFrom")));
            myTransfer.setLocationTo(header.getString(header.getColumnIndex("LocationTo")));
            if (header.getString(header.getColumnIndex("Reason")) != null)
                myTransfer.setReason(header.getString(header.getColumnIndex("Reason")));
            myTransfer.setCreatedUser(header.getString(header.getColumnIndex("CreatedUser")));
        }

        Cursor detail = database.rawQuery("SELECT * FROM TransferDtl WHERE DocNo= '" +
                docNoFP + "'"+ "ORDER BY LineNo ASC", null);

        if (detail.getCount() > 0) {
            List<AC_Class.TransferDtl> myTransferDtlList = new ArrayList<>();

            while (detail.moveToNext()) {
                AC_Class.TransferDtl myTransferDtl = new AC_Class.TransferDtl(myTransfer.getDocNo());
                myTransferDtl.setID(detail.getInt(detail.getColumnIndex("ID")));
                myTransferDtl.setItemCode(detail.getString(detail.getColumnIndex("ItemCode")));
                myTransferDtl.setDescription(detail.getString(detail.getColumnIndex("Description")));
                myTransferDtl.setUOM(detail.getString(detail.getColumnIndex("UOM")));
                myTransferDtl.setQuantity(detail.getFloat(detail.getColumnIndex("Qty")));
                myTransferDtl.setLineNo(detail.getString(detail.getColumnIndex("LineNo")));
                myTransferDtl.setBatchNo(detail.getString(detail.getColumnIndex("BatchNo")));

                myTransferDtlList.add(myTransferDtl);
            }

            myTransfer.setTransferDtlList(myTransferDtlList);
        }
        return myTransfer;
    }



    public AC_Class.StockTake getStockTake(String docNoFP) {
        AC_Class.StockTake myST = null;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor header = database.rawQuery("SELECT * FROM StockTake WHERE ID= '" + docNoFP + "'", null);
        if (header.getCount() == 1) {
            header.moveToNext();
            myST = new AC_Class.StockTake(header.getString(header.getColumnIndex("ID")), header.getString(header.getColumnIndex("CreatedTimeStamp")),
                    header.getString(header.getColumnIndex("DocDate")),header.getString(header.getColumnIndex("Location")),
                    header.getString(header.getColumnIndex("SalesAgent")),
                    header.getString(header.getColumnIndex("Remarks")), header.getString(header.getColumnIndex("CreatedUser")));

//            myST = new AC_Class.StockTake();
//            myST.setDocNo(header.getString(header.getColumnIndex("ID")));
//            myST.setCreatedTimeStamp(header.getString(header.getColumnIndex("CreatedTimeStamp")));
//            myST.setDocDate(header.getString(header.getColumnIndex("DocDate")));
//            myST.setLocation(header.getString(header.getColumnIndex("Location")));
//            myST.setAgent(header.getString(header.getColumnIndex("SalesAgent")));
//            myST.setRemarks(header.getString(header.getColumnIndex("Remarks")));
            myST.setUploaded(header.getInt(header.getColumnIndex("Exported")));
        }

        Cursor detail = database.rawQuery("SELECT * FROM StockTakeDetail WHERE StockDocNo= '" +
                docNoFP + "'"+ "ORDER BY LineNo ASC", null);

        if (detail.getCount() > 0) {
            List<AC_Class.StockTakeDetails> mySTDtlList = new ArrayList<>();

            while (detail.moveToNext()) {
                AC_Class.StockTakeDetails mySTDtl = new AC_Class.StockTakeDetails(myST.getDocNo());
                mySTDtl.setItemCode(detail.getString(detail.getColumnIndex("ItemCode")));
                mySTDtl.setCreatedTimeStamp(detail.getString(detail.getColumnIndex("CreatedTimeStamp")));
                mySTDtl.setItemDescription(detail.getString(detail.getColumnIndex("Description")));
                mySTDtl.setUOM(detail.getString(detail.getColumnIndex("UOM")));
                mySTDtl.setBatchNo(detail.getString(detail.getColumnIndex("BatchNo")));
                mySTDtl.setQuantity(detail.getDouble(detail.getColumnIndex("Qty")));
                mySTDtl.setStockDocNo(detail.getString(detail.getColumnIndex("StockDocNo")));
                mySTDtl.setLineNo(detail.getString(detail.getColumnIndex("LineNo")));

                mySTDtlList.add(mySTDtl);
            }

            myST.setStockTakeDtlList(mySTDtlList);
        }
        return myST;
    }

    public AC_Class.StockAssembly getStockAssembly(String docNoFP) {
        AC_Class.StockAssembly myST = null;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor header = database.rawQuery("SELECT * FROM StockAssembly WHERE DocNo= '" + docNoFP + "'", null);
        if (header.getCount() == 1) {
            header.moveToNext();
            myST = new AC_Class.StockAssembly();
            myST.setDocNo(header.getString(header.getColumnIndex("DocNo")));
            myST.setCreatedTimeStamp(header.getString(header.getColumnIndex("CreatedTimeStamp")));
            myST.setDocDate(header.getString(header.getColumnIndex("Date")));
            myST.setFGItemCode(header.getString(header.getColumnIndex("FGItemCode")));
            myST.setDescription(header.getString(header.getColumnIndex("Description")));
            myST.setFGBatchNo(header.getString(header.getColumnIndex("FGBatchNo")));
            myST.setFGQty(header.getDouble(header.getColumnIndex("FGQty")));
            myST.setLocation(header.getString(header.getColumnIndex("FGLocation")));
            myST.setRemarks(header.getString(header.getColumnIndex("Remark")));
            myST.setUploaded(header.getInt(header.getColumnIndex("Uploaded")));
        }

        return myST;
    }

    public boolean setARUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE ARPayment SET Uploaded=1 WHERE Uploaded=0");
        return true;
    }

    //Get AR Data for Uploading
    public Cursor getUploadARList() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.DocNo, a.Date, a.DebtorCode, a.DebtorName, a.Amount, a.Image, a.CreatedUser, a.CreatedTimeStamp, b.DocNumber, b.PaymentAmount, a.Remark, a.LastModifiedUser, a.LastModifiedDateTime FROM ARPayment a LEFT JOIN ARPaymentDTL b ON b.DocNo = a.DocNo  WHERE a.Uploaded=?", new String[]{"0"});
        return data;
    }


    //Get Data For Invoice History for a customer
    public Cursor getInvoiceHist(String code, Integer key) {
        SQLiteDatabase database = this.getReadableDatabase();
        String sortable = "";
        switch (key) {
            case 0:
                sortable = "a." + COL1_INV;
                break;
            case 1:
                sortable = "TotalCash";
                break;
        }
        Cursor data = database.rawQuery("SELECT " + "a." + COL7_INV + ", " + "a." + COL3_INV
                        + ", " + "a." + COL1_INV + ", " + "a." + COL4_INV + ", " + "a." + COL5_INV
                        + ", " + "a." + COL6_INV + ", " + "SUM " + "( " + "b." + COL13_INV_DTL + ") "
                        + ", " + "SUM " + "( " + "b." + COL12_INV_DTL + ") " + ", " + "SUM "
                        + "( " + "b." + COL14_INV_DTL + ") as TotalCash, a. " + COL8_INV + " ,a.Remarks, a.Status,a.Remarks2,a.Remarks3,a.Remarks4 FROM Sales a JOIN SalesDtl b " + " ON a. " + COL1_INV + " = b. " + COL1_INV_DTL
                        + " WHERE a." + COL4_INV + " LIKE ?"
                        + " GROUP BY " + "a." + COL1_INV + " ORDER BY ? DESC"
                , new String[]{code, sortable});  //No actual errors
        return data;
    }

    public Cursor getInvoiceHist2(String debtorCode) {
        SQLiteDatabase database = this.getReadableDatabase();

        // Construct the query without ORDER BY
        String query = "SELECT DocType, AccNo, ItemCode, Description, DocNo, DocDate, Location, Agent, Qty, UOM, UnitPrice, Discount, Subtotal "
                + "FROM HistoryPrice "
                + "WHERE AccNo LIKE ?";

        // Execute the query with the debtorCode parameter
        return database.rawQuery(query, new String[]{debtorCode});
    }

    public Cursor getPurchaseHist(String code, Integer key) {
        SQLiteDatabase database = this.getReadableDatabase();
        String sortable = "";
        switch (key) {
            case 0:
                sortable = "a." + COL1_INV;
                break;
            case 1:
                sortable = "TotalCash";
                break;
        }
        Cursor data = database.rawQuery("SELECT " + "a." + COL7_INV + ", " + "a." + COL3_INV
                        + ", " + "a." + COL1_INV + ", " + "a.CreditorCode, " + "a.CreditorName"
                        + ", " + "a.PurchaseAgent , " + "SUM " + "( " + "b." + COL13_INV_DTL + ") "
                        + ", " + "SUM " + "( " + "b." + COL12_INV_DTL + ") " + ", " + "SUM "
                        + "( " + "b." + COL14_INV_DTL + ") as TotalCash, a. " + COL8_INV + " FROM Purchase a " + " JOIN "
                        + " PuchaseDetail b " + " ON a. " + COL1_INV + " = b. " + COL1_INV_DTL
                        + " WHERE a.CreditorCode LIKE ?"
                        + " GROUP BY " + "a." + COL1_INV + " ORDER BY ? DESC"
                , new String[]{code, sortable});  //No actual errors
        return data;
    }

    //Get Invoice Data for Uploading
    public Cursor getUploadInvoice() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT b.DocNo, a.DocDate, a.DocType, a.DebtorCode, a.DebtorName, "
                + "a.SalesAgent, a.Phone, a.Fax, a.Attention, b.Location, b.ItemCode, b.ItemDescription, b.Qty, b.UOM, b.UPrice, b.SubTotal, "
                + "b.Discount, b.TaxType, b.TaxRate, b.TaxValue, b.Totalex, b.TotalIn, b.LineNo, a.Address1, a.Address2, " +
                "a.Address3, a.Address4, a.Remarks, a.Remarks2, a.Remarks3, a.Remarks4, b.Remarks AS [DtlRemarks], b.Remarks2 AS [DtlRemarks2], " +
                "b.BatchNo, a.CreatedUser, a.CreatedTimeStamp, a.LastModifiedUser, a.LastModifiedDateTime"
                + " FROM Sales a JOIN SalesDtl b ON b.DocNo = a.DocNo" +
                " WHERE a.Uploaded=?", new String[]{"0"});
//        Log.i("custDebug", "Cursor: "+DatabaseUtils.dumpCursorToString(data));
        return data;
    }

    //Get Invoice Data for Uploading
    public Cursor getUploadPurchase() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT b.DocNo, a.DocDate, a.CreditorCode, a.CreditorName, a.PurchaseAgent, a.Phone, a.Fax, a.Attention, b.Location, b.ItemCode, b.ItemDescription, b.Qty, b.UOM, b.UPrice, b.SubTotal, b.Discount, b.TaxType, b.TaxRate, b.TaxValue, b.Totalex, b.TotalIn, b.LineNo, b.BatchNo, a.Remarks, a.Remarks2, a.Remarks3, a.Remarks4, b.Remarks AS DtlRemarks,  b.Remarks2 AS DtlRemarks2, a.LastModifiedUser, LastModifiedDateTime  FROM Purchase a JOIN PurchaseDtl b ON b.DocNo = a.DocNo WHERE a.Uploaded=?", new String[]{"0"});
        return data;
    }

    //Get Transfer Data for Uploading
    public Cursor getUploadTransfer() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT b.DocNo, a.DocDate, a.Reason, a.LocationFrom, a.LocationTo, a.CreatedUser, b.ItemCode, b.Description, b.Qty, b.UOM, b.LineNo,b.BatchNo, a.LastModifiedUser, a.LastModifieddateTime FROM TRANSFER a JOIN TRANSFERDTL b ON b.DocNo = a.DocNo  WHERE a.Uploaded=?", new String[]{"0"});
        return data;
    }

    //Get StockTake Data for Uploading
    public Cursor getUploadStockTake() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT a.ID AS DocNo, a.DocDate, a.Location, a.SalesAgent, a.Remarks, a.CreatedTimeStamp, b.ItemCode, b.Description, " +
                "b.UOM, b.Qty, b.LineNo,b.BatchNo, a.CreatedUser,a.LastModifiedUser, a.LastModifiedDateTime FROM StockTake a JOIN StockTakeDetail b ON a.ID = b.StockDocNo WHERE a.Exported=0", null);
        return data;
    }

    //Get Packing List Data for Uploading
    public Cursor getUploadPackingList() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT b.DocNo, a.FromDocNo, a.DocDate, a.DebtorCode, a.DebtorName, a.SalesAgent, a.Phone, a.Fax, a.Attention, a.CreatedUser, b.Location, b.ItemCode, b.ItemDescription, b.Qty, b.UOM, b.LineNo, b.DtlKey, a.Remarks, b.Remarks AS DtlRemarks, b.BatchNo, a.DocType, a.IsTally, a.LastModifiedUser, a.LastModifiedDateTime FROM DO a JOIN DODTL b ON b.DocNo = a.DocNo  WHERE a.Uploaded=?", new String[]{"0"});
        return data;
    }

    public Cursor getUploadPurPackingList() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT b.DocNo, a.FromDocNo, a.DocDate, a.CreditorCode, a.CreditorName, a.PurchaseAgent, a.Phone, a.Fax, a.Attention, a.CreatedUser, b.Location, b.ItemCode, b.ItemDescription, b.Qty, b.UOM, b.LineNo, b.DtlKey, a.Remarks, b.Remarks AS DtlRemarks, b.BatchNo, a.DocType, a.IsTally, a.LastModifiedUser, a.LastModifiedDateTime FROM PI a JOIN PIDTL b ON b.DocNo = a.DocNo  WHERE a.Uploaded=?", new String[]{"0"});
        return data;
    }

    public Cursor getUploadSAList() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo, Date, Description, FGItemCode, FGQty, FGLocation, FGBatchNo, CreatedTimeStamp, CreatedUser, Remark, LastModifiedUser, LastModifiedDateTime FROM StockAssembly WHERE Uploaded=?", new String[]{"0"});
        return data;
    }

    //Get Upload/Download Data
    public Cursor getULDL(String mode) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT *  FROM " + TABLE_NAME_ULDL + " WHERE " + COL3_ULDL + " =?",
                new String[]{mode});
        return data;
    }

    public void deleteModules() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM Modules", null);
            if (cur.getCount() > 0) {
                database.delete("Modules", null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }


    public void deleteUsers() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_USERS, null);
            if (cur.getCount() > 0) {
                database.delete(TABLE_NAME_USERS, null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteDebtor() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM Debtor", null);
            if (cur.getCount() > 0) {
                database.delete("Debtor", null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public void deleteCreditor() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM Creditor", null);
            if (cur.getCount() > 0) {
                database.delete("Creditor", null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public void deleteItem() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_ITEM, null);
            if (cur.getCount() > 0) {
                database.delete(TABLE_NAME_ITEM, null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public void deleteAgent() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM Sales_Agent", null);
            if (cur.getCount() > 0) {
                database.delete("Sales_Agent", null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deletePurchaseAgent() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM PurchaseAgent", null);
            if (cur.getCount() > 0) {
                database.delete("PurchaseAgent", null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteLocation() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_LOCATION, null);
            if (cur.getCount() > 0) {
                database.delete(TABLE_NAME_LOCATION, null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteItemUOM() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM ItemUOM", null);
            if (cur.getCount() > 0) {
                database.delete("ItemUOM", null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteTaxType() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_TT, null);
            if (cur.getCount() > 0) {
                database.delete(TABLE_NAME_TT, null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public void deleteStockBalance() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_SB, null);
            if (cur.getCount() > 0) {
                database.delete(TABLE_NAME_SB, null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteItemBatch() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM ItemBatch" , null);
            if (cur.getCount() > 0) {
                database.delete("ItemBatch", null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteUTDCost() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM UTDCost" , null);
            if (cur.getCount() > 0) {
                database.delete("UTDCost", null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteItemBOM() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM ItemBOM" , null);
            if (cur.getCount() > 0) {
                database.delete("ItemBOM", null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteHistoryPrice() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_HP, null);
            if (cur.getCount() > 0) {
                database.delete(TABLE_NAME_HP, null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteSalesOrder(String docNoFP) {
        String[] arg = {docNoFP};
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM SO", null);
            if (cur.getCount() > 0) {
                database.delete("SO", "DocNo=?", arg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deletePurchaseOrder(String docNoFP) {
        String[] arg = {docNoFP};
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM PO", null);
            if (cur.getCount() > 0) {
                database.delete("PO", "DocNo=?", arg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Delete ULDL
    public void deleteULDL(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor query_SA = db.rawQuery("DELETE FROM " + TABLE_NAME_ULDL + " WHERE " + COL0_ULDL + " ='" + tableName + "'", null);
    }

    public Boolean isExists(String itemCode, String uom) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_SC + " WHERE " + COL2_SC + " = ? " +
                "AND " + COL5_SC + " = ?", new String[]{itemCode, uom});
        return (data.getCount() > 0);
    }

    //Update StockCount
    public void UpdateStockCount(AC_Class.StockCount stockCount) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_SC, stockCount.DocDate);
        cv.put(COL2_SC, stockCount.ItemCode);
        cv.put(COL3_SC, stockCount.Description);
        cv.put(COL4_SC, stockCount.Location);
        cv.put(COL5_SC, stockCount.UOM);
        cv.put(COL6_SC, stockCount.Qty);
        cv.put(COL7_SC, 0);
        String whereClause = COL2_SC + "=? AND " + COL5_SC + "=?";
        String whereArgs[] = {stockCount.getItemCode(), stockCount.getUOM()};
        database.update(TABLE_NAME_SC, cv, whereClause, whereArgs);
    }

    public boolean deleteURL(String id) {
        boolean result = false;
        String[] arg = {id};
        SQLiteDatabase database = this.getWritableDatabase();
        int sqlResult = database.delete(TABLE_NAME_SL, "ID =?", arg);
        if (sqlResult > 0) {
            result = true;
        }
        return result;
    }

    // Increment auto-numbering
    public boolean IncrementAutoNumbering(String typeFP) {
        SQLiteDatabase database = this.getWritableDatabase();
        String newNum = "";
        // Get number
        Cursor temp = getRef(typeFP);
        while (temp.moveToNext()) {
            newNum = temp.getString(temp.getColumnIndex(COL3_AN));
        }
        // Increment
        newNum = String.format(Locale.getDefault(), "%d", Integer.parseInt(newNum) + 1);
        database.execSQL("UPDATE " + TABLE_NAME_AN + " SET NextNumber=? WHERE Type = '" + typeFP + "'", new String[]{newNum});
        return true;
    }

    //Update Invoice Status for Credit Sales
    public boolean UpdateInvoiceStatusIV(String doc_no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Sales SET DocType = 'IV' WHERE DocNo='" + doc_no + "'");
        return true;
    }

    //Update Invoice Status for Credit Sales
    public boolean UpdateSalesStatusIV(String doc_no, String status, String remark) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Sales SET Status = '"+ status +"' WHERE DocNo='" + doc_no + "'");
        if(remark!= null) {
            database.execSQL("UPDATE Sales SET Remarks = '" + remark + "' WHERE DocNo='" + doc_no + "'");
        }else{
            database.execSQL("UPDATE Sales SET Remarks = null WHERE DocNo='" + doc_no + "'");
        }
        return true;
    }

    //Update Invoice Status for Cash Sales
    public boolean UpdateInvoiceStatusCS(String doc_no) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Sales SET DocType = 'CS' WHERE DocNo='" + doc_no + "'");
        return true;
    }

    //Update Function for Invoice
    public Cursor getInvoiceHeadertoUpdate(String doc_no) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT Sales.*, Debtor.TaxType, Debtor.DisplayTerm FROM Sales INNER JOIN Debtor ON Sales.DebtorCode = Debtor.AccNo WHERE DocNo= '" + doc_no + "'", null);
        return data;
    }

    //Update Function for Purchase
    public Cursor getPurchaseHeadertoUpdate(String doc_no) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT Purchase.*, Creditor.TaxType FROM Purchase INNER JOIN Creditor ON Purchase.CreditorCode = Creditor.AccNo WHERE DocNo= '" + doc_no + "'", null);
        return data;
    }

    //Get Invoice Details For Update
    public Cursor getInvoiceDetailtoUpdate(String doc_no) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_INV_DTL + " WHERE DocNo= '" +
                doc_no + "'", null);
        return data;
    }

    //Get AR Details For Update
    public Cursor getARDetailtoUpdate(String doc_no) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM ARPaymentDtl WHERE DocNo= '" +
                doc_no + "'", null);
        return data;
    }

    //Get Purchase Details For Update
    public Cursor getPurchaseDetailtoUpdate(String doc_no) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM PurchaseDtl WHERE DocNo= '" +
                doc_no + "'", null);
        return data;
    }



    //Get Invoice Details For Update
    public Cursor getInvoiceDetailtoUpdate(String doc_no, Integer id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_INV_DTL + " WHERE ID = '" +
                id + "' AND DocNo= '" + doc_no + "'", null);
        return data;
    }

    public boolean saveStockTake(AC_Class.StockTake stocktakeFP, String typeFP) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            if (typeFP.equals("Edit")) {
                deleteStockTakeDetails(stocktakeFP.getDocNo());
            }

            List<AC_Class.StockTakeDetails> dtlList = stocktakeFP.getStockTakeDtlList();

            for (int i = 0; i < dtlList.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("ItemCode", dtlList.get(i).getItemCode());
                cv.put("Description", dtlList.get(i).getItemDescription());
                cv.put("UOM", dtlList.get(i).getUOM());
                cv.put("Qty", dtlList.get(i).getQuantity());
                cv.put("StockDocNo", dtlList.get(i).getStockDocNo());
                cv.put("BatchNo", dtlList.get(i).getBatchNo());
                cv.put("CreatedTimeStamp", dtlList.get(i).getCreatedTimeStamp());
                cv.put("LineNo", i+1);

                db.insert("StockTakeDetail", null, cv);
            }

            ContentValues cv2 = new ContentValues();
            cv2.put("ID", stocktakeFP.getDocNo());
            cv2.put("CreatedTimeStamp", stocktakeFP.getCreatedTimeStamp());
            cv2.put("DocDate", stocktakeFP.getDocDate());
            cv2.put("Location", stocktakeFP.getLocation());
            cv2.put("SalesAgent", stocktakeFP.getAgent());
            cv2.put("Remarks", stocktakeFP.getRemarks());
            cv2.put("Exported", stocktakeFP.getUploaded());
            cv2.put("CreatedUser", stocktakeFP.getCreateduser());
            cv2.put("LastModifiedUser", stocktakeFP.getLastModifiedUser());
            cv2.put("LastModifiedDateTime", stocktakeFP.getLastModifiedDateTime());

            db.insert("StockTake", null, cv2);

            if (typeFP.equals("New")) {
                String newNum = "";
                // Get number
                Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME_AN + " WHERE Type = 'ST' LIMIT 1", null);
                while (data.moveToNext()) {
                    newNum = data.getString(data.getColumnIndex(COL3_AN));
                }
                // Increment
                newNum = String.format(Locale.getDefault(), "%d", Integer.parseInt(newNum) + 1);
                db.execSQL("UPDATE " + TABLE_NAME_AN + " SET NextNumber=? WHERE Type = 'ST'", new String[]{newNum});
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.i("custDebug", ex.getMessage());
        } finally {
            db.endTransaction();
        }

        return false;
    }

    public boolean saveStockAssembly(AC_Class.StockAssembly stocktakeFP, String typeFP) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            if (typeFP.equals("Edit")) {
                deleteStockTakeDetails(stocktakeFP.getDocNo());
            }

            List<AC_Class.StockAssemblyDetails> dtlList = stocktakeFP.getStockAssemblyDtlList();

            for (int i = 0; i < dtlList.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("SubItemCode", dtlList.get(i).getSubItemCode());
                cv.put("Qty", dtlList.get(i).getQuantity());
                cv.put("LineNo", i+1);

                db.insert("StockTakeDetail", null, cv);
            }

            ContentValues cv2 = new ContentValues();
            cv2.put("ID", stocktakeFP.getDocNo());
            cv2.put("CreatedTimeStamp", stocktakeFP.getCreatedTimeStamp());
            cv2.put("DocDate", stocktakeFP.getDocDate());
            cv2.put("Location", stocktakeFP.getLocation());
            cv2.put("Remarks", stocktakeFP.getRemarks());
            cv2.put("Exported", stocktakeFP.getUploaded());

            db.insert("StockTake", null, cv2);

            if (typeFP.equals("New")) {
                String newNum = "";
                // Get number
                Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME_AN + " WHERE Type = 'ST' LIMIT 1", null);
                while (data.moveToNext()) {
                    newNum = data.getString(data.getColumnIndex(COL3_AN));
                }
                // Increment
                newNum = String.format(Locale.getDefault(), "%d", Integer.parseInt(newNum) + 1);
                db.execSQL("UPDATE " + TABLE_NAME_AN + " SET NextNumber=? WHERE Type = 'ST'", new String[]{newNum});
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.i("custDebug", ex.getMessage());
        } finally {
            db.endTransaction();
        }

        return false;
    }

    public boolean saveTransfer(AC_Class.Transfer transferFP, String typeFP) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            if (typeFP.equals("Edit")) {
                deleteTransferDetails(transferFP.getDocNo());
            }

            List<AC_Class.TransferDtl> dtlList = transferFP.getTransferDtlList();

            for (int i = 0; i < dtlList.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("DocNo", dtlList.get(i).getDocNo());
                cv.put("ItemCode", dtlList.get(i).getItemCode());
                cv.put("Description", dtlList.get(i).getDescription());
                cv.put("UOM", dtlList.get(i).getUOM());
                cv.put("Qty", dtlList.get(i).getQuantity());
                cv.put("BatchNo", dtlList.get(i).getBatchNo());
                cv.put("LineNo", i+1);

                db.insert("TRANSFERDTL", null, cv);
            }

            ContentValues cv2 = new ContentValues();
            cv2.put("DocNo", transferFP.getDocNo());

            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date = sdf2.format(new Date());
            if (typeFP.equals("New")) {
                cv2.put("CreatedTimeStamp", date);
            }
            cv2.put("DocDate", transferFP.getDocDate());
            cv2.put("Reason", transferFP.getReason());
            cv2.put("LocationFrom", transferFP.getLocationFrom());
            cv2.put("LocationTo", transferFP.getLocationTo());
            cv2.put("CreatedUser", transferFP.getCreatedUser());
            cv2.put("Uploaded", transferFP.getUploaded());
            cv2.put("LastModifiedDateTime", date);
            cv2.put("LastModifiedUser", transferFP.getLastModifiedUser());

            db.insert("TRANSFER", null, cv2);

            if (typeFP.equals("New")) {
                String newNum = "";
                // Get number
                Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME_AN + " WHERE Type = 'T' LIMIT 1", null);
                while (data.moveToNext()) {
                    newNum = data.getString(data.getColumnIndex(COL3_AN));
                }
                // Increment
                newNum = String.format(Locale.getDefault(), "%d", Integer.parseInt(newNum) + 1);
                db.execSQL("UPDATE " + TABLE_NAME_AN + " SET NextNumber=? WHERE Type = 'T'", new String[]{newNum});
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.i("custDebug", ex.getMessage());
        } finally {
            db.endTransaction();
        }

        return false;
    }

    //Update Invoice Details
    public boolean UpdateInvoiceDetail(AC_Class.Invoice invoice) {
        try {
            deleteInvoiceDetails(invoice.getDocNo());

            List<AC_Class.InvoiceDetails> temp = invoice.getInvoiceDetailsList();
            for (int i = 0; i < temp.size(); i++) {
                insertSalesDtl(temp.get(i).getDocNo(), temp.get(i).getLocation(),
                        temp.get(i).getItemCode(), temp.get(i).getItemDescription(),
                        temp.get(i).getUOM(), temp.get(i).getQuantity(), temp.get(i).getUPrice(),
                        temp.get(i).getDiscount(), temp.get(i).getSubTotal(), temp.get(i).getTaxType(),
                        temp.get(i).getTaxRate(), temp.get(i).getTaxValue(), temp.get(i).getTotal_Ex(),
                        temp.get(i).getTotal_In(), i+1, temp.get(i).getRemarks(),
                        temp.get(i).getBatchNo(), temp.get(i).getRemarks2());
            }
            return true;
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
            return false;
        }
    }

    //Update Invoice Details
    public boolean UpdatePurchaseDetail(AC_Class.Purchase docFP) {
        try {
            deletePurchaseDetails(docFP.getDocNo());

            List<AC_Class.PurchaseDetails> temp = docFP.getPurchaseDetailsList();
            for (int i = 0; i < temp.size(); i++) {
                insertPurchaseDtl(temp.get(i).getDocNo(), temp.get(i).getLocation(),
                        temp.get(i).getItemCode(), temp.get(i).getItemDescription(),
                        temp.get(i).getUOM(), temp.get(i).getQuantity(), temp.get(i).getUPrice(),
                        temp.get(i).getDiscount(), temp.get(i).getSubTotal(), temp.get(i).getTaxType(),
                        temp.get(i).getTaxRate(), temp.get(i).getTaxValue(), temp.get(i).getTotal_Ex(),
                        temp.get(i).getTotal_In(), i+1, temp.get(i).getBatch_No()
                        , temp.get(i).getRemarks(), temp.get(i).getRemarks2());
            }
            return true;
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
            return false;
        }
    }

    //Update Invoice Details
    public boolean UpdateARDetail(AC_Class.ARPayment docFP) {
        try {
            deleteARDetails(docFP.getDocNo());

            List<AC_Class.ARPaymentDtl> temp = docFP.getARPaymentDtl();
            for (int i = 0; i < temp.size(); i++) {
                insertARDtl(temp.get(i).getDocNo(), temp.get(i).getDocNumber(),temp.get(i).getDocDate(),temp.get(i).getOrgAmt(),
                        temp.get(i).getPaymentAmount());
            }
            return true;
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
            return false;
        }
    }


    public boolean insertStockAssembly(AC_Class.StockAssembly sa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DocNo", sa.getDocNo());
        cv.put("Date", sa.getDocDate());
        cv.put("FGItemCode", sa.getFGItemCode());
        cv.put("FGBatchNo", sa.getFGBatchNo());
        cv.put("FGQty", sa.getFGQty());
        cv.put("FGLocation", sa.getLocation());
        cv.put("Description", sa.getDescription());
        cv.put("Uploaded", 0);
        cv.put("Remark", sa.getRemarks());
        cv.put("CreatedUser", sa.getCreatedUser());
        cv.put("CreatedTimeStamp", sa.getCreatedTimeStamp());
        cv.put("LastModifiedUser", sa.getLastModifiedUser());
        cv.put("LastModifiedDateTime", sa.getLastModifiedDateTime());
        long results = db.insert("StockAssembly", null, cv);
        return (results != -1);
    }

    //Update Upload/Download Row/Date
    private void UpdateUploadDownload(AC_Class.UploadDownload uploadDownload) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL1_ULDL, uploadDownload.getRowCount());
        cv.put(COL2_ULDL, uploadDownload.getLastDate());
        String whereClause = COL0_ULDL + "=? AND " + COL3_ULDL + "=?";
        String[] whereArgs = {uploadDownload.getTableName(), uploadDownload.getType()};
        database.update(TABLE_NAME_ULDL, cv, whereClause, whereArgs);
    }

    public boolean UpdateSADetail(AC_Class.StockAssembly docFP) {
        try {
            deleteSADetails(docFP.getDocNo());

            return true;
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
            return false;
        }
    }

    boolean deleteSADetails(String DocNo) {
        boolean result = false;
        String[] arg = {DocNo};
        SQLiteDatabase database = this.getWritableDatabase();
        int sqlResult = database.delete("StockAssembly", "DocNo =?", arg);
        if (sqlResult > 0 ) {
            result = true;
        }
        return result;
    }

    //Delete Whole Invoice
    boolean deleteInvoiceDetails(String DocNo) {
        boolean result = false;
        String[] arg = {DocNo};
        SQLiteDatabase database = this.getWritableDatabase();
        int sqlResult = database.delete("Sales", "DocNo =?", arg);
        int sqlResult1 = database.delete("SalesDtl", "DocNo =?", arg);
        int sqlResult2 = database.delete(TABLE_NAME_PAYMENT, "DocNo =?", arg);
        if (sqlResult > 0 && sqlResult1 >= 0 && sqlResult2 >= 0) {
            result = true;
        }
        return result;
    }

    //Delete Whole Invoice
    boolean deletePurchaseDetails(String DocNo) {
        boolean result = false;
        String[] arg = {DocNo};
        SQLiteDatabase database = this.getWritableDatabase();
        int sqlResult = database.delete("Purchase", "DocNo =?", arg);
        int sqlResult1 = database.delete("PurchaseDtl", "DocNo =?", arg);
        if (sqlResult > 0 && sqlResult1 >= 0) {
            result = true;
        }
        return result;
    }

    //Delete Whole Invoice
    boolean deleteARDetails(String DocNo) {
        boolean result = false;
        String[] arg = {DocNo};
        SQLiteDatabase database = this.getWritableDatabase();
        int sqlResult = database.delete("ARPayment", "DocNo =?", arg);
        int sqlResult1 = database.delete("ARPaymentDtl", "DocNo =?", arg);
        if (sqlResult > 0 && sqlResult1 >= 0) {
            result = true;
        }
        return result;
    }

    boolean deleteStockTakeDetails(String DocNo) {
        boolean result = false;
        String[] arg = {DocNo};
        SQLiteDatabase database = this.getWritableDatabase();
        int sqlResult = database.delete("StockTake", "ID =?", arg);
        int sqlResult1 = database.delete("StockTakeDetail", "StockDocNo =?", arg);
        if (sqlResult > 0 && sqlResult1 >= 0) {
            result = true;
        }
        return result;
    }

    boolean deleteStockAssembly(String DocNo) {
        boolean result = false;
        String[] arg = {DocNo};
        SQLiteDatabase database = this.getWritableDatabase();
        int sqlResult = database.delete("StockAssembly", "DocNo =?", arg);
        if (sqlResult > 0) {
            result = true;
        }
        return result;
    }

    boolean deleteTransferDetails(String DocNo) {
        boolean result = false;
        String[] arg = {DocNo};
        SQLiteDatabase database = this.getWritableDatabase();
        int sqlResult = database.delete("Transfer", "DocNo =?", arg);
        int sqlResult1 = database.delete("TransferDtl", "DocNo =?", arg);
        int sqlResult2 = database.delete("TransferDtl", "DocNo IS NULL", null);
        if (sqlResult > 0 && sqlResult1 >= 0 && sqlResult2 >= 0) {
            result = true;
        }
        return result;
    }

    //Delete Invoice Items
    public boolean deleteInvoiceDetailsItems(String DocNo, String id) {
        boolean result = false;
        String[] arg = {DocNo, id};
        SQLiteDatabase database = this.getWritableDatabase();
        int sqlResult = database.delete(TABLE_NAME_INV_DTL, "DocNo =? AND ID=?", arg);
        if (sqlResult > 0) {
            result = true;
        }
        return result;
    }

    public Cursor getPaymentToUpload() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT b.ID, b.DocNo, b.PaymentTime, b.PaymentType, " +
                "b.PaymentMethod, b. OriginalAmt, b.PaymentAmt, b.CashChanges, b.CCType, b.CardNo, " +
                "b.CCExpiryDate, b.CCApprovalCode, b.ChequeNo" +
                " FROM Payment b " +
                " WHERE b.Uploaded=?", new String[]{"0"});
        return data;
    }

    public void printItemTables() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor data = db.rawQuery("SELECT *  FROM " + TABLE_NAME_ITEM, null);
        Log.i("custDebug", "ITEM: " + DatabaseUtils.dumpCursorToString(data));

        data = db.rawQuery("SELECT *  FROM ItemUOM", null);
        Log.i("custDebug", "UOM: " + DatabaseUtils.dumpCursorToString(data));
    }

    String escapeChar(String str) {
//        str = str.replace("\'", "\\\'");
//        str = str.replace("\"", "\\\"");
//        str = str.replace("\\", "\\\\");
        return str;
    }

    public void resetAllItemImages() {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Image", "null");
            database.update("TABLE_NAME_ITEM_IMAGE", cv, null, null);
        } catch (Exception e) {
            Log.i("custDebug", "resetItemImages: " + e.getMessage());
        }
    }

    public boolean updateItemImages(String itemCode, String image) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Image", image);
            database.update("ItemImage", cv, "ItemCode = ?", new String[]{itemCode});
            return true;
        } catch (Exception e) {
            Log.i("custDebug", "updateItemImages: " + e.getMessage());
            return false;
        }
    }

    public boolean updateREG(String ID, String Value) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Value", Value);
        String[] args = new String[]{ID};
        database.update("Reg", cv, "ID = ?", args);
        return true;
    }

    public boolean updateREG(String ID, byte[] Value) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Value", Value);
        String[] args = new String[]{ID};
        database.update("Reg", cv, "ID = ?", args);
        return true;
    }

    public Cursor getSO(String substring) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();

        //data = database.rawQuery("SELECT DebtorCode, DebtorName, SalesAgent, Phone, Fax, Attention, Remarks FROM SO WHERE DocNo=? AND NOT EXISTS (SELECT 1 FROM DO WHERE FromDocNo=SO.DocNo)  GROUP BY DocNo", new String[]{substring});
        data = database.rawQuery("SELECT DebtorCode, DebtorName, SalesAgent, Phone, Attention, Fax, Remarks FROM SO WHERE DocNo=? GROUP BY DocNo", new String[]{substring});
        return data;
    }

    public Cursor getPO(String substring) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();

        //data = database.rawQuery("SELECT DebtorCode, DebtorName, SalesAgent, Phone, Fax, Attention, Remarks FROM SO WHERE DocNo=? AND NOT EXISTS (SELECT 1 FROM DO WHERE FromDocNo=SO.DocNo)  GROUP BY DocNo", new String[]{substring});
        data = database.rawQuery("SELECT CreditorCode, CreditorName, PurchaseAgent, Phone, Fax, Attention, Remarks, DocType, PurchaseLocation FROM PO WHERE DocNo=? GROUP BY DocNo", new String[]{substring});
        return data;
    }

    public Cursor getSODtl(String docNoFP) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();

        data = database.rawQuery("SELECT ID, Location, ItemCode, ItemDescription, UOM, SUM(Qty) AS Qty, LineNo,DocType, RemarksDtl, BatchNo FROM SO WHERE DocNo=? group by ItemCode", new String[]{docNoFP});

        return data;
    }

    public Cursor getPODtl(String docNoFP) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();

        data = database.rawQuery("SELECT ID, Location, ItemCode, ItemDescription, UOM, SUM(Qty) AS Qty, LineNo,DocType, RemarksDtl, BatchNo FROM PO WHERE DocNo=? group by ItemCode", new String[]{docNoFP});

        return data;
    }

    public Cursor getSODtl2(String docNoFP) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();

        data = database.rawQuery("SELECT ID, Location, ItemCode, ItemDescription, UOM, Qty, LineNo,DocType, RemarksDtl, BatchNo FROM SO WHERE DocNo=?", new String[]{docNoFP});

        return data;
    }

    public Cursor getPODtl2(String docNoFP) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();

        data = database.rawQuery("SELECT ID, Location, ItemCode, ItemDescription, UOM, Qty, LineNo,DocType, RemarksDtl, BatchNo FROM PO WHERE DocNo=?", new String[]{docNoFP});

        return data;
    }

    public Cursor getSOLike(String substring) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();

        data = database.rawQuery("SELECT a.DocNo, a.DocDate, a.DebtorCode, a.DebtorName, a.DocType, a.Remarks, a.Location FROM SO a LEFT JOIN DO b ON a.DocNo = b.FromDocNo Where b.DocNo is NULL AND (a.DocNo LIKE ? OR a.DebtorName Like ? OR a.DebtorCode LIKE ? OR a.Remarks LIKE ?) GROUP BY a.DocNo, a.DocDate, a.DebtorCode, a.DebtorName, a.DocType ORDER BY (substr(a.DocDate,1,4)||substr(a.DocDate,6,2)||substr(a.DocDate,9,2)) desc, a.DocNo DESC", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});

        return data;
    }

    public Cursor getPOLike(String substring) {
        Cursor data = null;
        String temp = "";
        SQLiteDatabase database = this.getReadableDatabase();

        data = database.rawQuery("SELECT a.DocNo, a.DocDate, a.CreditorCode, a.CreditorName, a.DocType, a.Remarks, a.PurchaseLocation FROM PO a LEFT JOIN PI b ON a.DocNo = b.FromDocNo Where b.DocNo is NULL AND (a.DocNo LIKE ? OR a.CreditorName Like ? OR a.CreditorCode LIKE ? OR a.Remarks LIKE ?) OR a.reDownload ==1  GROUP BY a.DocNo, a.DocDate, a.CreditorCode, a.CreditorName, a.DocType ORDER BY a.DocDate DESC, a.DocNo DESC", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});

        return data;
    }

    //Get Data For DO
    public Cursor getDOLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT DocNo, FromDocNo, DocDate, DebtorCode, DebtorName, SalesAgent, Uploaded, Remarks, DocType, IsTally FROM DO WHERE DocNo LIKE ? OR DebtorName LIKE ? ORDER BY DocNo DESC", new String[]{"%" + substring + "%", "%" + substring + "%"});

        return data;
    }

    //Get Data For PI
    public Cursor getPILike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT DocNo, FromDocNo, DocDate, CreditorCode, CreditorName, PurchaseAgent, Uploaded, Remarks, DocType, IsTally FROM PI WHERE DocNo LIKE ? OR CreditorName LIKE ? ORDER BY DocNo DESC", new String[]{"%" + substring + "%", "%" + substring + "%"});

        return data;
    }

    //Get Data For DO
    public Cursor getDODescLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT DocNo, FromDocNo, DocDate, DebtorCode, DebtorName, SalesAgent, Uploaded, Remarks, DocType, IsTally FROM DO WHERE DocNo LIKE ? OR DebtorName LIKE ? OR FromDocNo LIKE ? ORDER BY (substr(DocDate,7,4)||substr(DocDate,4,2)||substr(DocDate,1,2)) Desc, DocNo Desc", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});

        return data;
    }

    //Get Data For PI
    public Cursor getPIDescLike(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT DocNo, FromDocNo, DocDate, CreditorCode, CreditorName, PurchaseAgent, Uploaded, Remarks, DocType, IsTally FROM PI WHERE DocNo LIKE ? OR CreditorName LIKE ? OR FromDocNo LIKE ? ORDER BY (substr(DocDate,7,4)||substr(DocDate,4,2)||substr(DocDate,1,2)) Desc, DocNo Desc", new String[]{"%" + substring + "%", "%" + substring + "%", "%" + substring + "%"});

        return data;
    }

    public Cursor getSAUploaded(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT Uploaded FROM StockAssembly WHERE DocNo = ? ", new String[]{substring});

        return data;
    }

    public Cursor getSTUploaded(String substring) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT Exported FROM StockTake WHERE ID = ? ", new String[]{substring});

        return data;
    }

    public boolean savePackingList(AC_Class.DO packingListFP, String typeFP) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            if (typeFP.equals("Edit")) {
                deletePackingList(packingListFP.getDocNo());
            }

            List<AC_Class.DODtl> dtlList = packingListFP.getDODtlList();

            for (int i = 0; i < dtlList.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("DocNo", dtlList.get(i).getDocNo());
                cv.put("Location", dtlList.get(i).getLocation());
                cv.put("ItemCode", dtlList.get(i).getItemCode());
                cv.put("ItemDescription", dtlList.get(i).getItemDescription());
                cv.put("UOM", dtlList.get(i).getUOM());
                cv.put("Qty", dtlList.get(i).getQty());
                cv.put("LineNo", i+1);
                cv.put("DtlKey", dtlList.get(i).getDtlKey());
                cv.put("Remarks",dtlList.get(i).getRemarks());
                cv.put("BatchNo",dtlList.get(i).getBatchNo());


                db.insert("DODTL", null, cv);
            }

            ContentValues cv2 = new ContentValues();
            cv2.put("DocNo", packingListFP.getDocNo());
            cv2.put("CreatedTimeStamp", currentDateandTime);
            cv2.put("DocDate", packingListFP.getDocDate());
            cv2.put("FromDocNo", packingListFP.getFromDocNo());
            cv2.put("DebtorCode", packingListFP.getDebtorCode());
            cv2.put("DebtorName", packingListFP.getDebtorName());
            cv2.put("SalesAgent", packingListFP.getSalesAgent());
            cv2.put("Phone", packingListFP.getPhone());
            cv2.put("Fax", packingListFP.getFax());
            cv2.put("Attention", packingListFP.getAttention());
            cv2.put("CreatedUser", packingListFP.getCreatedUser());
            cv2.put("Uploaded", packingListFP.getUploaded());
            cv2.put("Remarks", packingListFP.getRemarks());
            cv2.put("DocType", packingListFP.getDocType());
            cv2.put("IsTally", packingListFP.getIsTally());
            cv2.put("CreatedUser", packingListFP.getCreatedUser());
            cv2.put("LastModifiedUser", packingListFP.getLastModifiedUser());
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date2 = sdf2.format(new Date());
            packingListFP.setLastModifiedDateTime(date2);
            cv2.put("LastModifiedDateTime", packingListFP.getLastModifiedDateTime());

            db.insert("DO", null, cv2);

            if (typeFP.equals("New")) {
                String newNum = "";
                // Get number
                Cursor data = db.rawQuery("SELECT * FROM AutoNumbering WHERE Type = 'PL' LIMIT 1", null);
                while (data.moveToNext()) {
                    newNum = data.getString(data.getColumnIndex(COL3_AN));
                }
                // Increment
                newNum = String.format(Locale.getDefault(), "%d", Integer.parseInt(newNum) + 1);
                db.execSQL("UPDATE AutoNumbering SET NextNumber=? WHERE Type = 'PL'", new String[]{newNum});
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.i("custDebug", ex.getMessage());
        } finally {
            db.endTransaction();
        }

        return false;
    }


    public boolean savePurPackingList(AC_Class.DO packingListFP, String typeFP) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            if (typeFP.equals("Edit")) {
                deletePurchasePackingList(packingListFP.getDocNo());
            }

            List<AC_Class.DODtl> dtlList = packingListFP.getDODtlList();

            for (int i = 0; i < dtlList.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("DocNo", dtlList.get(i).getDocNo());
                cv.put("Location", dtlList.get(i).getLocation());
                cv.put("ItemCode", dtlList.get(i).getItemCode());
                cv.put("ItemDescription", dtlList.get(i).getItemDescription());
                cv.put("UOM", dtlList.get(i).getUOM());
                cv.put("Qty", dtlList.get(i).getQty());
                cv.put("LineNo", i+1);
                cv.put("DtlKey", dtlList.get(i).getDtlKey());
                cv.put("Remarks",dtlList.get(i).getRemarks());
                cv.put("BatchNo",dtlList.get(i).getBatchNo());


                db.insert("PIDTL", null, cv);
            }

            ContentValues cv2 = new ContentValues();
            cv2.put("DocNo", packingListFP.getDocNo());
            cv2.put("CreatedTimeStamp", currentDateandTime);
            cv2.put("DocDate", packingListFP.getDocDate());
            cv2.put("FromDocNo", packingListFP.getFromDocNo());
            cv2.put("CreditorCode", packingListFP.getDebtorCode());
            cv2.put("CreditorName", packingListFP.getDebtorName());
            cv2.put("PurchaseAgent", packingListFP.getSalesAgent());
            cv2.put("Phone", packingListFP.getPhone());
            cv2.put("Fax", packingListFP.getFax());
            cv2.put("Attention", packingListFP.getAttention());
            cv2.put("CreatedUser", packingListFP.getCreatedUser());
            cv2.put("Uploaded", packingListFP.getUploaded());
            cv2.put("Remarks", packingListFP.getRemarks());
            cv2.put("DocType", packingListFP.getDocType());
            cv2.put("IsTally", packingListFP.getIsTally());
            cv2.put("CreatedUser", packingListFP.getCreatedUser());
            cv2.put("LastModifiedUser", packingListFP.getLastModifiedUser());
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date2 = sdf2.format(new Date());
            packingListFP.setLastModifiedDateTime(date2);
            cv2.put("LastModifiedDateTime", packingListFP.getLastModifiedDateTime());

            db.insert("PI", null, cv2);

            db.execSQL("UPDATE PO SET reDownload = 0 WHERE DocNo = ?", new String[]{packingListFP.getFromDocNo()});

            if (typeFP.equals("New")) {
                String newNum = "";
                // Get number
                Cursor data = db.rawQuery("SELECT * FROM AutoNumbering WHERE Type = 'PPL' LIMIT 1", null);
                while (data.moveToNext()) {
                    newNum = data.getString(data.getColumnIndex(COL3_AN));
                }
                // Increment
                newNum = String.format(Locale.getDefault(), "%d", Integer.parseInt(newNum) + 1);
                db.execSQL("UPDATE AutoNumbering SET NextNumber=? WHERE Type = 'PPL'", new String[]{newNum});
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.i("custDebug", ex.getMessage());
        } finally {
            db.endTransaction();
        }

        return false;
    }

    public boolean deletePackingList(String docNoFP) {
        boolean result = false;
        String[] arg = {docNoFP};
        SQLiteDatabase database = this.getWritableDatabase();

        int sqlResult1 = database.delete("DODtl", "DocNo =?", arg);
        int sqlResult2 = database.delete("DODtl", "DocNo IS NULL", null);
        int sqlResult = database.delete("DO", "DocNo =?", arg);
        if (sqlResult > 0 && sqlResult1 >= 0 && sqlResult2 >= 0) {
            result = true;
        }
        return result;
    }

    public boolean deletePurchasePackingList(String docNoFP) {
        boolean result = false;
        String[] arg = {docNoFP};
        SQLiteDatabase database = this.getWritableDatabase();

        int sqlResult1 = database.delete("PIDtl", "DocNo =?", arg);
        int sqlResult2 = database.delete("PIDtl", "DocNo IS NULL", null);
        int sqlResult = database.delete("PI", "DocNo =?", arg);
        if (sqlResult > 0 && sqlResult1 >= 0 && sqlResult2 >= 0) {
            result = true;
        }
        return result;
    }

    public boolean deleteSOPackingList(String docNoFP) {
        boolean result = false;
        String[] arg = {docNoFP};
        SQLiteDatabase database = this.getWritableDatabase();

        int sqlResult1 = database.delete("SO", "DocNo =?", arg);
        if (sqlResult1 >= 0) {
            result = true;
        }
        return result;
    }

    public boolean deletePOPackingList(String docNoFP) {
        boolean result = false;
        String[] arg = {docNoFP};
        SQLiteDatabase database = this.getWritableDatabase();

        int sqlResult1 = database.delete("PO", "DocNo =?", arg);
        if (sqlResult1 >= 0) {
            result = true;
        }
        return result;
    }

    public AC_Class.DO getPackingList(String docNoFP) {
        AC_Class.DO myDO = null;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor header = database.rawQuery("SELECT * FROM DO WHERE DocNo= '"  + docNoFP + "'" , null);
        if (header.getCount() == 1) {
            header.moveToNext();
            myDO = new AC_Class.DO();
            myDO.setDocNo(header.getString(header.getColumnIndex("DocNo")));
            myDO.setFromDocNo(header.getString(header.getColumnIndex("FromDocNo")));
            myDO.setDocDate(header.getString(header.getColumnIndex("DocDate")));
            myDO.setDebtorCode(header.getString(header.getColumnIndex("DebtorCode")));
            if (header.getString(header.getColumnIndex("DebtorName")) != null)
                myDO.setDebtorName(header.getString(header.getColumnIndex("DebtorName")));
            if (header.getString(header.getColumnIndex("SalesAgent")) != null)
                myDO.setSalesAgent(header.getString(header.getColumnIndex("SalesAgent")));
            if (header.getString(header.getColumnIndex("Phone")) != null)
                myDO.setPhone(header.getString(header.getColumnIndex("Phone")));
            if (header.getString(header.getColumnIndex("Fax")) != null)
                myDO.setFax(header.getString(header.getColumnIndex("Fax")));
            if (header.getString(header.getColumnIndex("Attention")) != null)
                myDO.setAttention(header.getString(header.getColumnIndex("Attention")));
            myDO.setCreatedTimeStamp(header.getString(header.getColumnIndex("CreatedTimeStamp")));
            myDO.setCreatedUser(header.getString(header.getColumnIndex("CreatedUser")));
            myDO.setRemarks(header.getString(header.getColumnIndex("Remarks")));
            myDO.setDocType(header.getString(header.getColumnIndex("DocType")));
            Boolean value = header.getInt(header.getColumnIndex("IsTally")) > 0;
            myDO.setIsTally(value);
            myDO.setCreatedUser(header.getString(header.getColumnIndex("CreatedUser")));
        }

        Cursor detail = database.rawQuery("SELECT * FROM DODTL WHERE DocNo= '" +
                docNoFP + "'"+ "ORDER BY LineNo ASC", null);

        if (detail.getCount() > 0) {
            List<AC_Class.DODtl> myList = new ArrayList<>();

            while (detail.moveToNext()) {
                AC_Class.DODtl myDtl = new AC_Class.DODtl();
                myDtl.setID(detail.getInt(detail.getColumnIndex("ID")));
                myDtl.setDocNo(myDO.getDocNo());
                myDtl.setItemCode(detail.getString(detail.getColumnIndex("ItemCode")));
                myDtl.setLocation(detail.getString(detail.getColumnIndex("Location")));
                if (detail.getString(detail.getColumnIndex("ItemDescription")) != null)
                    myDtl.setItemDescription(detail.getString(detail.getColumnIndex("ItemDescription")));
                if (detail.getString(detail.getColumnIndex("UOM")) != null)
                    myDtl.setUOM(detail.getString(detail.getColumnIndex("UOM")));
                myDtl.setQty(detail.getDouble(detail.getColumnIndex("Qty")));
                myDtl.setRemarks(detail.getString(detail.getColumnIndex("Remarks")));
                myDtl.setBatchNo(detail.getString(detail.getColumnIndex("BatchNo")));

                myList.add(myDtl);
            }

            myDO.setDODtlList(myList);
        }
        return myDO;
    }

    public AC_Class.DO getPurchasePackingList(String docNoFP) {
        AC_Class.DO myDO = null;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor header = database.rawQuery("SELECT * FROM PI WHERE DocNo= '"  + docNoFP + "'" , null);
        if (header.getCount() == 1) {
            header.moveToNext();
            myDO = new AC_Class.DO();
            myDO.setDocNo(header.getString(header.getColumnIndex("DocNo")));
            myDO.setFromDocNo(header.getString(header.getColumnIndex("FromDocNo")));
            myDO.setDocDate(header.getString(header.getColumnIndex("DocDate")));
            myDO.setDebtorCode(header.getString(header.getColumnIndex("CreditorCode")));
            if (header.getString(header.getColumnIndex("CreditorName")) != null)
                myDO.setDebtorName(header.getString(header.getColumnIndex("CreditorName")));
            if (header.getString(header.getColumnIndex("PurchaseAgent")) != null)
                myDO.setSalesAgent(header.getString(header.getColumnIndex("PurchaseAgent")));
            if (header.getString(header.getColumnIndex("Phone")) != null)
                myDO.setPhone(header.getString(header.getColumnIndex("Phone")));
            if (header.getString(header.getColumnIndex("Fax")) != null)
                myDO.setFax(header.getString(header.getColumnIndex("Fax")));
            if (header.getString(header.getColumnIndex("Attention")) != null)
                myDO.setAttention(header.getString(header.getColumnIndex("Attention")));
            myDO.setCreatedTimeStamp(header.getString(header.getColumnIndex("CreatedTimeStamp")));
            myDO.setCreatedUser(header.getString(header.getColumnIndex("CreatedUser")));
            myDO.setRemarks(header.getString(header.getColumnIndex("Remarks")));
            myDO.setDocType(header.getString(header.getColumnIndex("DocType")));
            Boolean value = header.getInt(header.getColumnIndex("IsTally")) > 0;
            myDO.setIsTally(value);
        }

        Cursor detail = database.rawQuery("SELECT * FROM PIDTL WHERE DocNo= '" +
                docNoFP + "'"+ "ORDER BY LineNo ASC", null);

        if (detail.getCount() > 0) {
            List<AC_Class.DODtl> myList = new ArrayList<>();

            while (detail.moveToNext()) {
                AC_Class.DODtl myDtl = new AC_Class.DODtl();
                myDtl.setID(detail.getInt(detail.getColumnIndex("ID")));
                myDtl.setDocNo(myDO.getDocNo());
                myDtl.setItemCode(detail.getString(detail.getColumnIndex("ItemCode")));
                myDtl.setLocation(detail.getString(detail.getColumnIndex("Location")));
                if (detail.getString(detail.getColumnIndex("ItemDescription")) != null)
                    myDtl.setItemDescription(detail.getString(detail.getColumnIndex("ItemDescription")));
                if (detail.getString(detail.getColumnIndex("UOM")) != null)
                    myDtl.setUOM(detail.getString(detail.getColumnIndex("UOM")));
                myDtl.setQty(detail.getDouble(detail.getColumnIndex("Qty")));
                myDtl.setRemarks(detail.getString(detail.getColumnIndex("Remarks")));
                myDtl.setBatchNo(detail.getString(detail.getColumnIndex("BatchNo")));

                myList.add(myDtl);
            }

            myDO.setDODtlList(myList);
        }
        return myDO;
    }

    public List<AC_Class.DODtl> getPackingListDtl(String docNoFP) {
        List<AC_Class.DODtl> myDODtlList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor detail = database.rawQuery("SELECT * FROM DODTL WHERE DocNo= '" +
                docNoFP + "'"+ "ORDER BY LineNo ASC", null);

        if (detail.getCount() > 0) {

            while (detail.moveToNext()) {
                AC_Class.DODtl myDtl = new AC_Class.DODtl();
                myDtl.setID(detail.getInt(detail.getColumnIndex("ID")));
                myDtl.setDocNo(docNoFP);
                myDtl.setItemCode(detail.getString(detail.getColumnIndex("ItemCode")));
                myDtl.setLocation(detail.getString(detail.getColumnIndex("Location")));
                if (detail.getString(detail.getColumnIndex("ItemDescription")) != null)
                    myDtl.setItemDescription(detail.getString(detail.getColumnIndex("ItemDescription")));
                if (detail.getString(detail.getColumnIndex("UOM")) != null)
                    myDtl.setUOM(detail.getString(detail.getColumnIndex("UOM")));
                myDtl.setQty(detail.getDouble(detail.getColumnIndex("Qty")));

                myDODtlList.add(myDtl);
            }
        }
        return myDODtlList;
    }

    //GET Price Category
    public Cursor getPriceCategory(String debtorCodeFP) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT PriceCategory FROM Debtor WHERE AccNo = '" + debtorCodeFP + "'", null);
        return data;
    }



    public Cursor getAllItemGroupType(ArrayList<String> ItemGroup, ArrayList<String> ItemType) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selection = " IN ('" + TextUtils.join("', '", ItemGroup) + "')";
        String selection2 = " IN ('" + TextUtils.join("', '", ItemType) + "')";
        String selectQuery = "SELECT b.ItemCode, a.UOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType, b.BaseUOM, a.Price, a.Price2, a.Price3, a.Price4, a.Price5, a.Price6, a.BarCode, a.Shelf, a.Rate, b.ItemCode2, a.MinPrice, a.MaxPrice, b.HasBatchNo FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode WHERE ItemGroup" + selection + " AND ItemType" + selection2 ;
        Cursor data = database.rawQuery(selectQuery, null);
        return data;
    }

    public Cursor getAllItemGroup(ArrayList<String> ItemGroup) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selection = " IN ('" + TextUtils.join("', '", ItemGroup) + "')";
        Cursor data = database.rawQuery(
                "SELECT b.ItemCode, a.UOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType, b.BaseUOM, a.Price, a.Price2, a.Price3, a.Price4, a.Price5, a.Price6, a.BarCode, a.Shelf, a.Rate, b.ItemCode2, a.MinPrice, a.MaxPrice, b.HasBatchNo FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode WHERE ItemGroup" + selection, null);
        return data;
    }

    public Cursor getAllItemType(ArrayList<String> ItemType) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selection2 = " IN ('" + TextUtils.join("', '", ItemType) + "')";
        Cursor data = database.rawQuery("SELECT b.ItemCode, a.UOM, b.Description, b.Desc2, b.ItemGroup, b.ItemType, b.TaxType, b.PurchaseTaxType, b.BaseUOM, a.Price, a.Price2, a.Price3, a.Price4, a.Price5, a.Price6, a.BarCode, a.Shelf, a.Rate, b.ItemCode2, a.MinPrice, a.MaxPrice, b.HasBatchNo FROM ItemUOM a JOIN Item b ON a.ItemCode=b.ItemCode WHERE ItemType"+ selection2 , null);
        return data;
    }

    public List<String>getAllGroup(){
        List<String>Group= new ArrayList<>();
        String selectQuery =("SELECT DISTINCT ItemGroup FROM Item ");
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do{
                Group.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return Group;
    }

    public List<String>getAllType(){
        List<String>Type= new ArrayList<>();
        String selectQuery =("SELECT DISTINCT ItemType FROM Item ");
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {
                Type.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return Type;
    }

    //Get Data For PI
    public Cursor getItemDesc2(String itemcode) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT Desc2 FROM Item WHERE ItemCode = ?", new String[]{itemcode});

        return data;
    }


    //get item type for a given item code
    public Cursor getItemType(String itemcode) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT ItemType FROM Item WHERE ItemCode = ?", new String[]{itemcode});

        return data;
    }

    //get item details for a given code
    public Cursor getItemDetails(String itemcode) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_ITEM + " WHERE ItemCode = ?", new String[]{itemcode});

        return data;
    }

    //get docDate for a given docNo
    public Cursor getDocDate(String docNo) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery("SELECT DocDate FROM Sales WHERE DocNo = ?", new String[]{docNo});

        return data;
    }

    //get item barcode for a given itemCode
    public Cursor getItemBarcode(String itemcode) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor data = database.rawQuery(
                "SELECT a.ItemCode, b.Description, a.UOM, a.BarCode, a.Price, a.Price2, a.Price3, a.Price4, a.Price5, a.Price6, b.Desc2, b.TaxType, b.HasBatchNo " +
                        "FROM ItemUOM a " +
                        "JOIN Item b ON a.ItemCode=b.ItemCode " +
                        "WHERE a.ItemCode = ?",
                new String[]{itemcode}
        );

        return data;
    }


    public boolean insertStockReceive(AC_Class.StockReceive stockReceive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DocNo", stockReceive.getDocNo());
        cv.put("DocType", stockReceive.getDocType());
        cv.put("CreatedTimeStamp", stockReceive.getCreatedTimeStamp());
        cv.put("CreatedUser", stockReceive.getCreatedUser());
        cv.put("DocDate", stockReceive.getDocDate());
        cv.put("Remarks", stockReceive.getRemarks());
        cv.put("Description", stockReceive.getDescription());
        cv.put("TaxType", stockReceive.getTaxType());
        cv.put("Uploaded", 0);
        cv.put("LastModifiedDateTime", stockReceive.getLastModifiedDateTime());
        cv.put("LastModifiedUser", stockReceive.getLastModifiedUser());
        long results = db.insert("StockReceive", null, cv);
        return (results != -1);
    }

    public boolean insertStockReceiveDtl(List<AC_Class.StockReceiveDetails> stockReceiveDetailsList) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean allInserted = true;

        // Use a transaction for bulk insertion to improve performance and atomicity
        db.beginTransaction();
        try {
            int lineNo = 1;

            for (AC_Class.StockReceiveDetails details : stockReceiveDetailsList) {
                ContentValues cv = new ContentValues();

                // Validate required fields before insertion
                if (isValidStockReceiveDetail(details)) {
                    cv.put("DocNo", details.getDocNo());
                    cv.put("DocDate", details.getDocDate());
                    cv.put("Location", details.getLocation());
                    cv.put("ItemCode", details.getItemCode());
                    cv.put("ItemDescription", details.getItemDescription());
                    cv.put("UOM", details.getUOM());
                    cv.put("Qty", details.getQuantity());
                    cv.put("UTD_Cost", details.getUTD_Cost());
                    cv.put("SubTotal", details.getSubTotal());
                    cv.put("BatchNo", details.getBatch_No());
                    cv.put("Remarks", details.getRemarks());
                    cv.put("Remarks2", details.getRemarks2());
                    cv.put("LineNo", lineNo);


                    long result = db.insert("StockReceiveDtl", null, cv);
                    if (result == -1) {
                        allInserted = false;
                        break; // Exit loop if any insertion fails
                    }
                    lineNo++;
                } else {
                    allInserted = false;
                    break; // Exit loop if validation fails
                }
            }

            if (allInserted) {
                db.setTransactionSuccessful(); // Mark transaction as successful
            }
        } catch (Exception e) {
            Log.e("Insert", "Error inserting StockReceiveDetails", e);
            allInserted = false;
        } finally {
            db.endTransaction(); // End transaction whether successful or not
        }

        return allInserted;
    }

    private boolean isValidStockReceiveDetail(AC_Class.StockReceiveDetails details) {
        // Example validation: Check if required fields are not null
        return details.getDocNo() != null
                && details.getItemCode() != null
                && details.getQuantity() != null
                && details.getUTD_Cost() != null;
    }


    public Cursor getStockReceive() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM StockReceive ORDER BY DocNo DESC", null);
    }

    public boolean deleteStockReceive(String docNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("StockReceive", "DocNo=?", new String[]{docNo}) > 0;
    }

    public boolean deleteStockReceiveDetail(String docNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("StockReceiveDtl", "DocNo=?", new String[]{docNo}) > 0;
    }

    //Get stock receive next No
    public String getSRNextDocNo() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN +
                " WHERE Type = 'SR' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
    }

    public Cursor getStockReceiveDetailsByDocNo(String docNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM StockReceiveDtl WHERE DocNo = ?";
        return db.rawQuery(query, new String[]{docNo});
    }


    public boolean updateStockReceive(AC_Class.StockReceive stockReceive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DocNo", stockReceive.getDocNo());
        cv.put("DocType", stockReceive.getDocType());
        cv.put("CreatedTimeStamp", stockReceive.getCreatedTimeStamp());
        cv.put("CreatedUser", stockReceive.getCreatedUser());
        cv.put("DocDate", stockReceive.getDocDate());
        cv.put("Remarks", stockReceive.getRemarks());
        cv.put("Description", stockReceive.getDescription());
        cv.put("TaxType", stockReceive.getTaxType());
        cv.put("Uploaded", 0);
        cv.put("LastModifiedDateTime", stockReceive.getLastModifiedDateTime());
        cv.put("LastModifiedUser", stockReceive.getLastModifiedUser());

        return db.update("StockReceive", cv, "DocNo = ?", new String[]{stockReceive.getDocNo()}) > 0;
    }


    public boolean insertJobSheet(AC_Class.JobSheet jobSheet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("DocType", jobSheet.getDocType());
        values.put("DocNo", jobSheet.getDocNo());
        values.put("DocDate", jobSheet.getDocDate());
        values.put("DebtorCode", jobSheet.getDebtorCode());
        values.put("DebtorName", jobSheet.getDebtorName());
        values.put("DebtorName2", jobSheet.getDebtorName2());
        values.put("Address1", jobSheet.getAddress1());
        values.put("Address2", jobSheet.getAddress2());
        values.put("Address3", jobSheet.getAddress3());
        values.put("Address4", jobSheet.getAddress4());
        values.put("SalesAgent", jobSheet.getAgent());
        values.put("Uploaded", jobSheet.getUploaded());
        values.put("TaxType", jobSheet.getTaxType());
        values.put("Phone", jobSheet.getPhone());
        values.put("Fax", jobSheet.getFax());
        values.put("Attention", jobSheet.getAttention());
        values.put("Remarks", jobSheet.getRemarks());
        values.put("Remarks2", jobSheet.getRemarks2());
        values.put("Remarks3", jobSheet.getRemarks3());
        values.put("Remarks4", jobSheet.getRemarks4());
        values.put("Status", jobSheet.getStatus());
        values.put("WorkType", jobSheet.getWorkType());
        values.put("ReplacementType", jobSheet.getReplacementType());
        values.put("TimeIn", jobSheet.getTimeIn());
        values.put("TimeOut", jobSheet.getTimeOut());
        values.put("Problem", jobSheet.getProblem());
        values.put("Resolution", jobSheet.getResolution());
        values.put("JobSheetRemarks", jobSheet.getJobSheetRemarks());
        values.put("SalesNo", jobSheet.getSalesNo());
        values.put("TaxValue", jobSheet.getTotalTax());
        values.put("Discount", jobSheet.getDiscount());
        values.put("TotalEx", jobSheet.getTotalEx());
        values.put("TotalIn", jobSheet.getTotalIn());
        values.put("CreatedTimeStamp", jobSheet.getCreatedTimeStamp());
        values.put("CreatedUser", jobSheet.getCreatedUser());
        values.put("LastModifiedDateTime", jobSheet.getLastModifiedDateTime());
        values.put("LastModifiedUser", jobSheet.getLastModifiedUser());
        values.put("Signature", jobSheet.getSignature());
        values.put("Image", jobSheet.getImage());

        long results = db.insert("JobSheet", null, values);
        return (results != -1);
    }

    public boolean insertJobSheetDetails(List<AC_Class.JobSheetDetails> jobSheetDetailsList) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean allInserted = true;

        // Use a transaction for bulk insertion to improve performance and atomicity
        db.beginTransaction();
        try {
            int lineNo = 1;

            for (AC_Class.JobSheetDetails details : jobSheetDetailsList) {
                ContentValues values = new ContentValues();

                // Validate required fields before insertion
                if (isValidJobSheetDetail(details)) {
                    values.put("DocNo", details.getDocNo());
                    values.put("DocDate", details.getDocDate());
                    values.put("Location", details.getLocation());
                    values.put("ItemCode", details.getItemCode());
                    values.put("ItemDescription", details.getItemDescription());
                    values.put("UOM", details.getUOM());
                    values.put("Quantity", details.getQuantity());
                    values.put("UPrice", details.getUPrice());
                    values.put("Discount", details.getDiscount());
                    values.put("SubTotal", details.getSubTotal());
                    values.put("TaxType", details.getTaxType());
                    values.put("TaxRate", details.getTaxRate());
                    values.put("TaxValue", details.getTaxValue());
                    values.put("TotalEx", details.getTotal_Ex());
                    values.put("TotalIn", details.getTotal_In());
                    values.put("LineNo", details.getLine_No());
                    values.put("Remarks", details.getRemarks());
                    values.put("Remarks2", details.getRemarks2());
                    values.put("BatchNo", details.getBatchNo());
                    values.put("LineNo", lineNo);

                    long result = db.insert("JobSheetDetails", null, values);
                    if (result == -1) {
                        allInserted = false;
                        break; // Exit loop if any insertion fails
                    }
                    lineNo++;
                } else {
                    allInserted = false;
                    break; // Exit loop if validation fails
                }
            }

            if (allInserted) {
                db.setTransactionSuccessful(); // Mark transaction as successful
            }
        } catch (Exception e) {
            Log.e("Insert", "Error inserting JobSheetDetails", e);
            allInserted = false;
        } finally {
            db.endTransaction(); // End transaction whether successful or not
        }

        return allInserted;
    }

    private boolean isValidJobSheetDetail(AC_Class.JobSheetDetails details) {
        return
                details.getItemCode() != null
                && details.getQuantity() != null
                && details.getUPrice() != null;
    }

    public Cursor getJobSheet() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM JobSheet ORDER BY DocNo DESC", null);
    }

    public Cursor getStockTake() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM StockTake ORDER BY ID DESC", null);
    }



    //Get job sheet next No
    public String getJSNextDocNo() {
        String docNo = null;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME_AN +
                " WHERE Type = 'JS' LIMIT 1", null);
        if (data.moveToNext()) {
            docNo = data.getString(data.getColumnIndex(COL4_AN)) + String.format(Locale.getDefault(),
                    "%0" + data.getInt(data.getColumnIndex(COL5_AN)) + "d",
                    data.getInt(data.getColumnIndex(COL3_AN)));
        }
        return docNo;
        /*
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DocNo FROM JobSheet ORDER BY DocNo DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        String nextDocNo = null;
        if (cursor.moveToFirst()) {
            nextDocNo = cursor.getString(cursor.getColumnIndex("DocNo"));
        }
        cursor.close();
        db.close();
        return nextDocNo;

         */
    }



        public AC_Class.JobSheet getDebtorInfo2(String accNo) {
        AC_Class.JobSheet myJobSheet = null;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT CompanyName, SalesAgent, Taxtype, Phone, Fax, Attention, Address1, Address2, Address3, Address4 FROM " + TABLE_NAME_DEBTOR +
                " WHERE " + COL2_HIS + " = ?", new String[]{accNo});
        if (data.moveToNext()) {
            myJobSheet = new AC_Class.JobSheet();
            myJobSheet.setDebtorName(data.getString(data.getColumnIndex("CompanyName")));
            myJobSheet.setAgent(data.getString(data.getColumnIndex("SalesAgent")));
            myJobSheet.setTaxType(data.getString(data.getColumnIndex("TaxType")));
            myJobSheet.setPhone(data.getString(data.getColumnIndex("Phone")));
            myJobSheet.setFax(data.getString(data.getColumnIndex("Fax")));
            myJobSheet.setAttention(data.getString(data.getColumnIndex("Attention")));
            myJobSheet.setAddress1(data.getString(data.getColumnIndex("Address1")));
            myJobSheet.setAddress2(data.getString(data.getColumnIndex("Address2")));
            myJobSheet.setAddress3(data.getString(data.getColumnIndex("Address3")));
            myJobSheet.setAddress4(data.getString(data.getColumnIndex("Address4")));
        }

        return myJobSheet;
    }

    public Cursor getJobSheetByDocNo(String docNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM JobSheet WHERE DocNo = ?";
        return db.rawQuery(query, new String[]{docNo});
    }

    public Cursor getJobSheetDetailsByDocNo(String docNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM JobSheetDetails WHERE DocNo = ?";
        return db.rawQuery(query, new String[]{docNo});
    }

    public int getJSRecordsByDocNo(String docNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM JobSheetDetails WHERE DocNo = ?";
        Cursor cursor = db.rawQuery(query, new String[]{docNo});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public AC_Class.JobSheet getJobSheetOByDocNo(String docNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM JobSheet WHERE DocNo = ?";
        Cursor cursor = db.rawQuery(query, new String[]{docNo});

        AC_Class.JobSheet jobSheet = null;

        if (cursor != null && cursor.moveToFirst()) {
            jobSheet = new AC_Class.JobSheet();
            jobSheet.setDocNo(docNo);
            jobSheet.setDocDate(cursor.getString(cursor.getColumnIndex("DocDate")));
            jobSheet.setDocType(cursor.getString(cursor.getColumnIndex("DocType")));
            jobSheet.setDebtorCode(cursor.getString(cursor.getColumnIndex("DebtorCode")));
            jobSheet.setDebtorName(cursor.getString(cursor.getColumnIndex("DebtorName")));
            jobSheet.setDebtorName2(cursor.getString(cursor.getColumnIndex("DebtorName2")));
            jobSheet.setAgent(cursor.getString(cursor.getColumnIndex("SalesAgent")));
            jobSheet.setPhone(cursor.getString(cursor.getColumnIndex("Phone")));
            jobSheet.setAddress1(cursor.getString(cursor.getColumnIndex("Address1")));
            jobSheet.setAddress2(cursor.getString(cursor.getColumnIndex("Address2")));
            jobSheet.setAddress3(cursor.getString(cursor.getColumnIndex("Address3")));
            jobSheet.setAddress4(cursor.getString(cursor.getColumnIndex("Address4")));
            jobSheet.setAttention(cursor.getString(cursor.getColumnIndex("Attention")));
            jobSheet.setFax(cursor.getString(cursor.getColumnIndex("Fax")));
            jobSheet.setRemarks(cursor.getString(cursor.getColumnIndex("Remarks")));
            jobSheet.setRemarks2(cursor.getString(cursor.getColumnIndex("Remarks2")));
            jobSheet.setRemarks3(cursor.getString(cursor.getColumnIndex("Remarks3")));
            jobSheet.setRemarks4(cursor.getString(cursor.getColumnIndex("Remarks4")));
            jobSheet.setStatus(cursor.getString(cursor.getColumnIndex("Status")));
            jobSheet.setWorkType(cursor.getString(cursor.getColumnIndex("WorkType")));
            jobSheet.setReplacementType(cursor.getString(cursor.getColumnIndex("ReplacementType")));
            jobSheet.setTimeIn(cursor.getString(cursor.getColumnIndex("TimeIn")));
            jobSheet.setTimeOut(cursor.getString(cursor.getColumnIndex("TimeOut")));
            jobSheet.setProblem(cursor.getString(cursor.getColumnIndex("Problem")));
            jobSheet.setResolution(cursor.getString(cursor.getColumnIndex("Resolution")));
            jobSheet.setJobSheetRemarks(cursor.getString(cursor.getColumnIndex("JobSheetRemarks")));
            jobSheet.setTotalIn(cursor.getDouble(cursor.getColumnIndex("TotalIn")));
            jobSheet.setTotalEx(cursor.getDouble(cursor.getColumnIndex("TotalEx")));
            jobSheet.setTotalTax(cursor.getDouble(cursor.getColumnIndex("TaxValue")));
            jobSheet.setDiscount(cursor.getDouble(cursor.getColumnIndex("Discount")));
            jobSheet.setCreatedUser(cursor.getString(cursor.getColumnIndex("CreatedUser")));
            jobSheet.setCreatedTimeStamp(cursor.getString(cursor.getColumnIndex("CreatedTimeStamp")));
            jobSheet.setLastModifiedUser(cursor.getString(cursor.getColumnIndex("LastModifiedUser")));
            jobSheet.setLastModifiedDateTime(cursor.getString(cursor.getColumnIndex("LastModifiedDateTime")));
            jobSheet.setSignature(cursor.getString(cursor.getColumnIndex("Signature")));
            jobSheet.setImage(cursor.getString(cursor.getColumnIndex("Image")));

            cursor.close();
        }

        return jobSheet;
    }

    public List<AC_Class.JobSheetDetails> getJobSheetDetailsOByDocNo(String docNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM JobSheetDetails WHERE DocNo = ?";
        Cursor cursor = db.rawQuery(query, new String[]{docNo});

        List<AC_Class.JobSheetDetails> jobSheetDetailsList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                AC_Class.JobSheetDetails jobSheetDetail = new AC_Class.JobSheetDetails();
                jobSheetDetail = new AC_Class.JobSheetDetails();
                jobSheetDetail.setDocNo(docNo);
                jobSheetDetail.setDocDate(cursor.getString(cursor.getColumnIndex("DocDate")));
                jobSheetDetail.setLocation(cursor.getString(cursor.getColumnIndex("Location")));
                jobSheetDetail.setItemCode(cursor.getString(cursor.getColumnIndex("ItemCode")));
                jobSheetDetail.setItemDescription(cursor.getString(cursor.getColumnIndex("ItemDescription")));
                jobSheetDetail.setUOM(cursor.getString(cursor.getColumnIndex("UOM")));
                jobSheetDetail.setQuantity(cursor.getDouble(cursor.getColumnIndex("Quantity")));
                jobSheetDetail.setUPrice(cursor.getDouble(cursor.getColumnIndex("UPrice")));
                jobSheetDetail.setDiscount(cursor.getDouble(cursor.getColumnIndex("Discount")));
                jobSheetDetail.setSubTotal(cursor.getDouble(cursor.getColumnIndex("SubTotal")));
                jobSheetDetail.setTaxType(cursor.getString(cursor.getColumnIndex("TaxType")));
                jobSheetDetail.setTaxRate(cursor.getDouble(cursor.getColumnIndex("TaxRate")));
                jobSheetDetail.setTaxValue(cursor.getDouble(cursor.getColumnIndex("TaxValue")));
                jobSheetDetail.setTotal_Ex(cursor.getDouble(cursor.getColumnIndex("TotalEx")));
                jobSheetDetail.setTotal_In(cursor.getDouble(cursor.getColumnIndex("TotalIn")));
                jobSheetDetail.setLine_No(cursor.getString(cursor.getColumnIndex("LineNo")));
                jobSheetDetail.setRemarks(cursor.getString(cursor.getColumnIndex("Remarks")));
                jobSheetDetail.setRemarks2(cursor.getString(cursor.getColumnIndex("Remarks2")));
                jobSheetDetail.setBatchNo(cursor.getString(cursor.getColumnIndex("BatchNo")));

                jobSheetDetailsList.add(jobSheetDetail);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return jobSheetDetailsList;
    }

    public boolean deleteJobSheetDetails(String docNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("JobSheetDetails", "DocNo=?", new String[]{docNo}) > 0;
    }

    public boolean deleteJobSheet(String docNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("JobSheet", "DocNo=?", new String[]{docNo}) > 0;
    }

    public boolean saveInvoiceDetails(List<AC_Class.InvoiceDetails> invoiceDetailsList) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean allInserted = true;

        for (AC_Class.InvoiceDetails invoiceDetails : invoiceDetailsList) {
            ContentValues values = new ContentValues();

            values.put("DocNo", invoiceDetails.getDocNo());
            values.put("Location", invoiceDetails.getLocation());
            values.put("ItemCode", invoiceDetails.getItemCode());
            values.put("ItemDescription", invoiceDetails.getItemDescription());
            values.put("UOM", invoiceDetails.getUOM());
            values.put("Qty", invoiceDetails.getQuantity());
            values.put("UPrice", invoiceDetails.getUPrice());
            values.put("Discount", invoiceDetails.getDiscount());
            values.put("SubTotal", invoiceDetails.getSubTotal());
            values.put("TaxType", invoiceDetails.getTaxType());
            values.put("TaxRate", invoiceDetails.getTaxRate());
            values.put("TaxValue", invoiceDetails.getTaxValue());
            values.put("Totalex", invoiceDetails.getTotal_Ex());
            values.put("TotalIn", invoiceDetails.getTotal_In());
            values.put("LineNo", invoiceDetails.getLine_No());
            values.put("Remarks", invoiceDetails.getRemarks());
            values.put("Remarks2", invoiceDetails.getRemarks2());
            values.put("BatchNo", invoiceDetails.getBatchNo());

            long result = db.insert("SalesDtl", null, values);
            if (result == -1) {
                allInserted = false;
            }
        }

        db.close();
        return allInserted;
    }

    public boolean updateJobSheetStatus(String docNo, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Status", newStatus);

        int rowsAffected = db.update("JobSheet", values, "DocNo = ?", new String[]{docNo});

        db.close();

        return rowsAffected > 0;
    }

    public boolean updateJSInvoiceDocNo(String docNo, String newDocNo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("SalesNo", newDocNo);

        int rowsAffected = db.update("JobSheet", values, "DocNo = ?", new String[]{docNo});

        db.close();

        return rowsAffected > 0;
    }

    public boolean updateJSSalesNo(String docNo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.putNull("SalesNo");
        values.put("Status", "New");

        int rowsAffected = db.update("JobSheet", values, "SalesNo = ?", new String[]{docNo});

        db.close();

        return rowsAffected > 0;
    }

    public Bitmap getJSImage(String docNo) {
        Cursor data;
        SQLiteDatabase database = this.getReadableDatabase();
        String[] itemCols = new String[]{"Image"};
        data = null;

        try {
            data = database.query("JobSheet", itemCols, "DocNo = ?",
                    new String[]{docNo}, "", "", "");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }
        if (data.getCount() > 0) {
            if (data.moveToNext()) {
                if (data.getString(0) != null) {
                    byte[] decodedString = null;
                    byte[] b = data.getString(data.getColumnIndex("Image")).getBytes();
                    b = Base64.decode(b, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(b, 0,
                            b.length);
                    //Bitmap.createScaledBitmap(decodedByte, 20, 20, true);
                    return decodedByte;
                }
            }
        }
        return null;
    }



    public boolean isJobSheetConverted(String docNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM JobSheet WHERE DocNo = ? AND Status = ?";
        String[] selectionArgs = {docNo, "Converted"};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        return count > 0;
    }

    public Cursor getUploadchkJobSheet() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo"
                + " FROM JobSheet WHERE Uploaded=?", new String[]{"0"});
        return data;
    }

    public Cursor getUploadJobSheet() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT  a.DocNo, a.DocDate, a.DocType, a.DebtorCode, a.DebtorName, a.DebtorName2, a.SalesAgent, a.Phone, a.Fax, a.Attention, a.Address1, a.Address2, a.Address3, a.Address4, a.Remarks, a.Remarks2, a.Remarks3, a.Remarks4, a.WorkType, a.ReplacementType, a.TimeIn, a.TimeOut, a.Problem, a.Resolution, a.JobSheetRemarks, a.SalesNo, a.CreatedUser, a.CreatedTimeStamp, a.LastModifiedUser, a.LastModifiedDateTime, a.Signature, a.Image, b.ItemCode, b.ItemDescription, b.Location, b.Quantity, b.UOM, b.UPrice, b.SubTotal, b.Discount, b.TaxType, b.TaxRate, b.TaxValue, b.TotalEx, b.TotalIn, b.LineNo, b.Remarks AS DtlRemarks, b.Remarks2 AS DtlRemarks2, b.BatchNo FROM JobSheet a LEFT JOIN JobSheetDetails b ON b.DocNo = a.DocNo WHERE a.Uploaded = ?", new String[]{"0"});
        return data;
    }


    public boolean setJobSheetUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE JobSheet SET Uploaded=1 WHERE Uploaded=0");
        return true;
    }

    public Cursor getUploadchkStockReceive() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT DocNo"
                + " FROM StockReceive WHERE Uploaded=?", new String[]{"0"});
        return data;
    }

    public Cursor getUploadStockReceive() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT b.DocNo, a.DocDate, a.DocType,a.CreatedUser, a.Description, a.Remarks, b.Location, b.ItemCode, b.ItemDescription, b.Qty, b.UOM, b.LineNo, b.Remarks2 AS DtlRemarks2, b.Remarks AS DtlRemarks, b.BatchNo, b.UTD_Cost, b.SubTotal, a.LastModifiedUser, a.LastModifiedDateTime  FROM StockReceive a JOIN StockReceiveDtl b ON b.DocNo = a.DocNo WHERE a.Uploaded=?", new String[]{"0"});
        return data;
    }

    public boolean setStockReceiveUploaded() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE StockReceive SET Uploaded=1 WHERE Uploaded=0");
        return true;
    }

    public void beginTransaction() {
        getWritableDatabase().beginTransaction();
    }

    public void setTransactionSuccessful() {
        getWritableDatabase().setTransactionSuccessful();
    }

    public void endTransaction() {
        getWritableDatabase().endTransaction();
    }

    public Cursor getCreditTerm() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM CreditTermMaintenance", null);
        return data;
    }

    public void deleteCreditTerm() {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM CreditTermMaintenance", null);
            if (cur.getCount() > 0) {
                database.delete("CreditTermMaintenance", null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }


}

