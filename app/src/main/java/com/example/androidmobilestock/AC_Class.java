package com.example.androidmobilestock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.database.Cursor;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import com.example.androidmobilestock.BluetoothPrinter.PrinterCommands;
import com.example.androidmobilestock.BluetoothPrinter.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class AC_Class {

    public static class Connection extends BaseObservable
    {
        public Integer ID;
        public String URL;
        public String URLStr;

        public Connection(Integer ID, String URLFP, String URLStrFP) {
            this.ID = ID;
            this.URL = URLFP;
            this.URLStr = URLStrFP;
        }

        public Connection() {
        }

        @Bindable
        public Integer getID() {
            return ID;
        }

        public void setID(Integer ID) {
            this.ID = ID;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getURL() {
            return URL;
        }

        public void setURL(String URL) {
            this.URL = URL;
            notifyPropertyChanged(BR.uRL);
        }

        @Bindable
        public String getURLStr() {
            return URLStr;
        }

        public void setURLStr(String URLStr) {
            this.URLStr = URLStr;
        }
    }

    //Printer
    public static class Printer extends BaseObservable implements Parcelable{
        public String ID;
        public String PrinterName;

        public Printer(String id, String printerName){
            this.ID = id;
            this.PrinterName = printerName;
        }

        protected Printer(Parcel in) {
            ID = in.readString();
            PrinterName = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ID);
            dest.writeString(PrinterName);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Printer> CREATOR = new Creator<Printer>() {
            @Override
            public Printer createFromParcel(Parcel in) {
                return new Printer(in);
            }

            @Override
            public Printer[] newArray(int size) {
                return new Printer[size];
            }
        };

        @Bindable
        public String getID() {
            return ID;
        }

        public void setID(String id) {
            ID = id;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getPrinterName() {
            return PrinterName;
        }

        public void setPrinterName(String printerName) {
            PrinterName = printerName;
            notifyPropertyChanged(BR.printerName);
        }
    }


    public static class Debtor extends BaseObservable implements Parcelable {          //Debtors
        public String AccNo;
        public String CompanyName;
        public String Description;
        public String ADD1;
        public String ADD2;
        public String ADD3;
        public String ADD4;
        public String SalesAgent;
        public String TaxType;
        public String phone;
        public String fax;
        public String attention;
        public String EmailAddress;
        public String DebtorType;
        public String AreaCode;
        public String CurrencyCode;
        public String DisplayTerm;
        public String Phone2;
        public String DetailDiscount;

        public Debtor(String _AccNo, String _CompanyName, String _Description, String _ADD1,
                      String _ADD2, String _ADD3, String _ADD4, String _SalesAgent,
                      String _TaxType, String _Phone, String _Fax, String _Attention, String EmailAddressFP, String DebtorTypeFP, String AreaCodeFP, String CurrencyCodeFP, String DisplayTermFP, String phone2, String DetailDiscount) {
            this.AccNo = _AccNo;
            this.CompanyName = _CompanyName;
            this.Description = _Description;
            this.ADD1 = _ADD1;
            this.ADD2 = _ADD2;
            this.ADD3 = _ADD3;
            this.ADD4 = _ADD4;
            this.SalesAgent = _SalesAgent;
            this.TaxType = _TaxType;
            this.phone = _Phone;
            this.fax = _Fax;
            this.attention = _Attention;
            this.EmailAddress = EmailAddressFP;
            this.DebtorType = DebtorTypeFP;
            this.AreaCode = AreaCodeFP;
            this.CurrencyCode = CurrencyCodeFP;
            this.DisplayTerm = DisplayTermFP;
            this.Phone2 = phone2;
            this.DetailDiscount = DetailDiscount;
        }

        protected Debtor(Parcel in) {
            AccNo = in.readString();
            CompanyName = in.readString();
            Description = in.readString();
            ADD1 = in.readString();
            ADD2 = in.readString();
            ADD3 = in.readString();
            ADD4 = in.readString();
            SalesAgent = in.readString();
            TaxType = in.readString();
            phone = in.readString();
            fax = in.readString();
            attention = in.readString();
            EmailAddress = in.readString();
            DebtorType= in.readString();
            AreaCode= in.readString();
            CurrencyCode = in.readString();
            DisplayTerm = in.readString();
            Phone2 = in.readString();
            DetailDiscount = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(AccNo);
            dest.writeString(CompanyName);
            dest.writeString(Description);
            dest.writeString(ADD1);
            dest.writeString(ADD2);
            dest.writeString(ADD3);
            dest.writeString(ADD4);
            dest.writeString(SalesAgent);
            dest.writeString(TaxType);
            dest.writeString(phone);
            dest.writeString(fax);
            dest.writeString(attention);
            dest.writeString(EmailAddress);
            dest.writeString(DebtorType);
            dest.writeString(AreaCode);
            dest.writeString(CurrencyCode);
            dest.writeString(DisplayTerm);
            dest.writeString(Phone2);
            dest.writeString(DetailDiscount);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Debtor> CREATOR = new Creator<Debtor>() {
            @Override
            public Debtor createFromParcel(Parcel in) {
                return new Debtor(in);
            }

            @Override
            public Debtor[] newArray(int size) {
                return new Debtor[size];
            }
        };

        @Bindable
        public String getAccNo() {
            return AccNo;
        }

        public void setAccNo(String accNo) {
            AccNo = accNo;
            notifyPropertyChanged(BR.accNo);
        }

        @Bindable
        public String getCompanyName() {
            return CompanyName;
        }

        public void setCompanyName(String companyName) {
            CompanyName = companyName;
            notifyPropertyChanged(BR.companyName);
        }

        @Bindable
        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
            notifyPropertyChanged(BR.description);
        }

        @Bindable
        public String getADD1() {
            return ADD1;
        }

        public void setADD1(String ADD1) {
            this.ADD1 = ADD1;
            notifyPropertyChanged(BR.aDD1);
        }

        @Bindable
        public String getADD2() {
            return ADD2;
        }

        public void setADD2(String ADD2) {
            this.ADD2 = ADD2;
            notifyPropertyChanged(BR.aDD2);
        }

        @Bindable
        public String getADD3() {
            return ADD3;
        }

        public void setADD3(String ADD3) {
            this.ADD3 = ADD3;
            notifyPropertyChanged(BR.aDD3);
        }

        @Bindable
        public String getADD4() {
            return ADD4;
        }

        public void setADD4(String ADD4) {
            this.ADD4 = ADD4;
            notifyPropertyChanged(BR.aDD4);
        }

        @Bindable
        public String getSalesAgent() {
            return SalesAgent;
        }

        public void setSalesAgent(String salesAgent) {
            SalesAgent = salesAgent;
            notifyPropertyChanged(BR.salesAgent);
        }

        @Bindable
        public String getTaxType() {
            return TaxType;
        }

        public void setTaxType(String taxType) {
            TaxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
            notifyPropertyChanged(BR.phone);
        }

        @Bindable
        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
            notifyPropertyChanged(BR.fax);
        }

        @Bindable
        public String getAttention() {
            return attention;
        }

        public void setAttention(String attention) {
            this.attention = attention;
            notifyPropertyChanged(BR.attention);
        }

        @Bindable
        public String getEmailAddress() {
            return EmailAddress;
        }

        public void setEmailAddress(String EmailAddress) {
            this.EmailAddress = EmailAddress;
            notifyPropertyChanged(BR.emailAddress);
        }

        @Bindable
        public String getDebtorType() {
            return DebtorType;
        }

        public void setDebtorType(String DebtorType) {
            this.DebtorType = DebtorType;
            notifyPropertyChanged(BR.debtorType);
        }

        @Bindable
        public String getAreaCode() {
            return AreaCode;
        }

        public void setAreaCode(String AreaCode) {
            this.AreaCode = AreaCode;
            notifyPropertyChanged(BR.areaCode);
        }

        @Bindable
        public String getCurrencyCode() {
            return CurrencyCode;
        }

        public void setCurrencyCode(String CurrencyCode) {
            this.CurrencyCode = CurrencyCode;
            notifyPropertyChanged(BR.currencyCode);
        }

        @Bindable
        public String getDisplayTerm() {
            return DisplayTerm;
        }

        public void setDisplayTerm(String DisplayTerm) {
            this.DisplayTerm = DisplayTerm;
            notifyPropertyChanged(BR.displayTerm);
        }

        @Bindable
        public String getPhone2() {
            return Phone2;
        }

        public void setPhone2(String phone2) {
            this.Phone2 = phone2;
            notifyPropertyChanged(BR.phone2);
        }

        @Bindable
        public String getDetailDiscount() {
            return DetailDiscount;
        }

        public void setDetailDiscount(String detailDiscount) {
            this.DetailDiscount = detailDiscount;
            notifyPropertyChanged(BR.detailDiscount);
        }


    }

    public static class Creditor extends BaseObservable implements Parcelable {          //Debtors
        public String AccNo;
        public String CompanyName;
        public String Description;
        public String ADD1;
        public String ADD2;
        public String ADD3;
        public String ADD4;
        public String PurchaseAgent;
        public String TaxType;
        public String phone;
        public String fax;
        public String attention;
        public String EmailAddress;
        public String CreditorType;
        public String AreaCode;
        public String CurrencyCode;
        public String DisplayTerm;
        public String Phone2;

        public Creditor(String _AccNo, String _CompanyName, String _Description, String _ADD1,
                      String _ADD2, String _ADD3, String _ADD4, String _PurchaseAgent,
                      String _TaxType, String _Phone, String _Fax, String _Attention, String EmailAddressFP, String CreditorTypeFP, String AreaCodeFP, String CurrencyCodeFP, String DisplayTermFP, String phone2) {
            this.AccNo = _AccNo;
            this.CompanyName = _CompanyName;
            this.Description = _Description;
            this.ADD1 = _ADD1;
            this.ADD2 = _ADD2;
            this.ADD3 = _ADD3;
            this.ADD4 = _ADD4;
            this.PurchaseAgent = _PurchaseAgent;
            this.TaxType = _TaxType;
            this.phone = _Phone;
            this.fax = _Fax;
            this.attention = _Attention;
            this.EmailAddress = EmailAddressFP;
            this.CreditorType = CreditorTypeFP;
            this.AreaCode = AreaCodeFP;
            this.CurrencyCode = CurrencyCodeFP;
            this.DisplayTerm = DisplayTermFP;
            this.Phone2 = phone2;
        }

        protected Creditor(Parcel in) {
            AccNo = in.readString();
            CompanyName = in.readString();
            Description = in.readString();
            ADD1 = in.readString();
            ADD2 = in.readString();
            ADD3 = in.readString();
            ADD4 = in.readString();
            PurchaseAgent = in.readString();
            TaxType = in.readString();
            phone = in.readString();
            fax = in.readString();
            attention = in.readString();
            EmailAddress = in.readString();
            CreditorType= in.readString();
            AreaCode= in.readString();
            CurrencyCode = in.readString();
            DisplayTerm = in.readString();
            Phone2 = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(AccNo);
            dest.writeString(CompanyName);
            dest.writeString(Description);
            dest.writeString(ADD1);
            dest.writeString(ADD2);
            dest.writeString(ADD3);
            dest.writeString(ADD4);
            dest.writeString(PurchaseAgent);
            dest.writeString(TaxType);
            dest.writeString(phone);
            dest.writeString(fax);
            dest.writeString(attention);
            dest.writeString(EmailAddress);
            dest.writeString(CreditorType);
            dest.writeString(AreaCode);
            dest.writeString(CurrencyCode);
            dest.writeString(DisplayTerm);
            dest.writeString(Phone2);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Creditor> CREATOR = new Creator<Creditor>() {
            @Override
            public Creditor createFromParcel(Parcel in) {
                return new Creditor(in);
            }

            @Override
            public Creditor[] newArray(int size) {
                return new Creditor[size];
            }
        };

        @Bindable
        public String getAccNo() {
            return AccNo;
        }

        public void setAccNo(String accNo) {
            AccNo = accNo;
            notifyPropertyChanged(BR.accNo);
        }

        @Bindable
        public String getCompanyName() {
            return CompanyName;
        }

        public void setCompanyName(String companyName) {
            CompanyName = companyName;
            notifyPropertyChanged(BR.companyName);
        }

        @Bindable
        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
            notifyPropertyChanged(BR.description);
        }

        @Bindable
        public String getADD1() {
            return ADD1;
        }

        public void setADD1(String ADD1) {
            this.ADD1 = ADD1;
            notifyPropertyChanged(BR.aDD1);
        }

        @Bindable
        public String getADD2() {
            return ADD2;
        }

        public void setADD2(String ADD2) {
            this.ADD2 = ADD2;
            notifyPropertyChanged(BR.aDD2);
        }

        @Bindable
        public String getADD3() {
            return ADD3;
        }

        public void setADD3(String ADD3) {
            this.ADD3 = ADD3;
            notifyPropertyChanged(BR.aDD3);
        }

        @Bindable
        public String getADD4() {
            return ADD4;
        }

        public void setADD4(String ADD4) {
            this.ADD4 = ADD4;
            notifyPropertyChanged(BR.aDD4);
        }

        @Bindable
        public String getPurchaseAgent() {
            return PurchaseAgent;
        }

        public void setPurchaseAgent(String purchaseAgent) {
            PurchaseAgent = purchaseAgent;
            notifyPropertyChanged(BR.purchaseAgent);
        }

        @Bindable
        public String getTaxType() {
            return TaxType;
        }

        public void setTaxType(String taxType) {
            TaxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
            notifyPropertyChanged(BR.phone);
        }

        @Bindable
        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
            notifyPropertyChanged(BR.fax);
        }

        @Bindable
        public String getAttention() {
            return attention;
        }

        public void setAttention(String attention) {
            this.attention = attention;
            notifyPropertyChanged(BR.attention);
        }

        @Bindable
        public String getEmailAddress() {
            return EmailAddress;
        }

        public void setEmailAddress(String EmailAddress) {
            this.EmailAddress = EmailAddress;
            notifyPropertyChanged(BR.emailAddress);
        }

        @Bindable
        public String getCreditorType() {
            return CreditorType;
        }

        public void setCreditorType(String CreditorTypeFP) {
            this.CreditorType = CreditorTypeFP;
            notifyPropertyChanged(BR.creditorType);
        }

        @Bindable
        public String getAreaCode() {
            return AreaCode;
        }

        public void setAreaCode(String AreaCode) {
            this.AreaCode = AreaCode;
            notifyPropertyChanged(BR.areaCode);
        }

        @Bindable
        public String getCurrencyCode() {
            return CurrencyCode;
        }

        public void setCurrencyCode(String CurrencyCode) {
            this.CurrencyCode = CurrencyCode;
            notifyPropertyChanged(BR.currencyCode);
        }

        @Bindable
        public String getDisplayTerm() {
            return attention;
        }

        public void setDisplayTerm(String DisplayTerm) {
            this.DisplayTerm = DisplayTerm;
            notifyPropertyChanged(BR.displayTerm);
        }

        @Bindable
        public String getPhone2() {
            return Phone2;
        }

        public void setPhone2(String phone2) {
            this.Phone2 = phone2;
            notifyPropertyChanged(BR.phone2);
        }
    }

    public static class Register extends BaseObservable implements Parcelable {
        public Integer ID;
        public String Value;

        public Register(Integer id, String value){
            ID = id;
            Value = value;
        }

        public Register(){

        }

        protected Register(Parcel in) {
            if (in.readByte() == 0) {
                ID = null;
            } else {
                ID = in.readInt();
            }
            Value = in.readString();
        }

        public static final Creator<Register> CREATOR = new Creator<Register>() {
            @Override
            public Register createFromParcel(Parcel in) {
                return new Register(in);
            }

            @Override
            public Register[] newArray(int size) {
                return new Register[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (ID == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(ID);
            }
            dest.writeString(Value);
        }

        @Bindable
        public Integer getID() {
            return ID;
        }

        public void setID(Integer iD) {
            this.ID = iD;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getValue() {
            return Value;
        }

        public void setValue(String value) {
            Value = value;
            notifyPropertyChanged(BR.value);
        }
    }

    public static class Item extends BaseObservable implements Parcelable {
        public String ItemCode;
        public String UOM;
        public String ItemCodeInput;
        public String Description;
        public String Desc2;
        public String ItemGroup;
        public String ItemType;
        public String TaxType;
        public String PurchaseTaxType;
        public String BaseUOM;
        public Float Price;
        public Float Price2;
        public Float Price3;
        public Float Price4;
        public Float Price5;
        public Float Price6;
        public String BarCode;
        public String Shelf;
        public Float Rate;
        public String ItemCode2;
        public Float MinPrice;
        public Float MaxPrice;
        public Bitmap Image;
        public String HasBatchNo;
        public Double Qty;
        public String Location;

        public Item() {}

        //Sales
        public Item(String itemCode, String uom, String description, String desc2, String itemGroup, String itemType, String taxType, String purchaseTaxType, String baseUOM, Float price, Float price2, Float price3, Float price4, Float price5, Float price6, String barCode, String shelf, Float rate,String itemCode2, Float minPrice, Float maxPrice, String hasBatch) {
            ItemCode = itemCode;
            UOM = uom;
            Description = description;
            Desc2 = desc2;
            ItemGroup = itemGroup;
            ItemType = itemType;
            TaxType = taxType;
            PurchaseTaxType = purchaseTaxType;
            BaseUOM = baseUOM;
            Price = price;
            Price2 = price2;
            Price3 = price3;
            Price4 = price4;
            Price5 = price5;
            Price6 = price6;
            BarCode = barCode;
            Shelf = shelf;
            Rate = rate;
            ItemCode2 = itemCode2;
            MinPrice = minPrice;
            MaxPrice = maxPrice;
            HasBatchNo = hasBatch;

        }

        //Purchase
        public Item(String itemCode, String itemCode2, String uom, String description, String desc2, String itemGroup, String itemType, String taxType, String purchaseTaxType, String baseUOM, Float price, Float price2, String barCode, String hasBatch) {
            ItemCode = itemCode;
            ItemCode2 = itemCode2;
            UOM = uom;
            Description = description;
            Desc2 = desc2;
            ItemGroup = itemGroup;
            ItemType = itemType;
            TaxType = taxType;
            PurchaseTaxType = purchaseTaxType;
            BaseUOM = baseUOM;
            Price = price;
            Price2 = price2;
            BarCode = barCode;
            HasBatchNo = hasBatch;

        }


        public Item(String itemCode, String itemCode2, String uom, String description, String desc2, String itemGroup,
                    String itemType, String taxType, String purchaseTaxType, String baseUOM, Float price, Float price2,
                    Float price3,Float price4,Float price5,Float price6,
                    String barCode, String shelf, Float rate, Float minPrice, Float maxPrice, String hasBatch) {
            ItemCode = itemCode;
            ItemCode2 = itemCode2;
            UOM = uom;
            Description = description;
            Desc2 = desc2;
            ItemGroup = itemGroup;
            ItemType = itemType;
            TaxType = taxType;
            PurchaseTaxType = purchaseTaxType;
            BaseUOM = baseUOM;
            Price = price;
            Price2 = price2;
            Price3 = price3;
            Price4 = price4;
            Price5 = price5;
            Price6 = price6;
            BarCode = barCode;
            Shelf = shelf;
            Rate = rate;
            MinPrice = minPrice;
            MaxPrice = maxPrice;
            HasBatchNo = hasBatch;

        }

        public Item(String itemCode, String uom, String description, String desc2, String itemGroup, String itemType, String baseUOM, String barCode, String hasBatch, Double qty, String loc) {
            ItemCode = itemCode;
            UOM = uom;
            Description = description;
            Desc2 = desc2;
            ItemGroup = itemGroup;
            ItemType = itemType;
            BaseUOM = baseUOM;
            BarCode = barCode;
            HasBatchNo = hasBatch;
            Qty = qty;
            Location = loc;
        }

        protected Item(Parcel in) {
            ItemCode = in.readString();
            UOM = in.readString();
            ItemCodeInput = in.readString();
            Description = in.readString();
            Desc2 = in.readString();
            ItemGroup = in.readString();
            ItemType = in.readString();
            TaxType = in.readString();
            PurchaseTaxType = in.readString();
            BaseUOM = in.readString();
            if (in.readByte() == 0) {
                Price = null;
            } else {
                Price = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price2 = null;
            } else {
                Price2 = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price3 = null;
            } else {
                Price3 = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price4 = null;
            } else {
                Price4 = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price5 = null;
            } else {
                Price5 = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price6 = null;
            } else {
                Price6 = in.readFloat();
            }
            BarCode = in.readString();
            Shelf = in.readString();
            if (in.readByte() == 0) {
                Rate = null;
            } else {
                Rate = in.readFloat();
            }
            ItemCode2 = in.readString();
            if (in.readByte() == 0) {
                MinPrice = null;
            } else {
                MinPrice = in.readFloat();
            }
            if (in.readByte() == 0) {
                MaxPrice = null;
            } else {
                MaxPrice = in.readFloat();
            }
            Image = in.readParcelable(Bitmap.class.getClassLoader());
            HasBatchNo = in.readString();
            Location = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ItemCode);
            dest.writeString(UOM);
            dest.writeString(ItemCodeInput);
            dest.writeString(Description);
            dest.writeString(Desc2);
            dest.writeString(ItemGroup);
            dest.writeString(ItemType);
            dest.writeString(TaxType);
            dest.writeString(PurchaseTaxType);
            dest.writeString(BaseUOM);
            if (Price == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price);
            }
            if (Price2 == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price2);
            }
            if (Price3 == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price3);
            }
            if (Price4 == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price4);
            }
            if (Price5 == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price5);
            }
            if (Price6 == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price6);
            }
            dest.writeString(BarCode);
            dest.writeString(Shelf);
            if (Rate == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Rate);
            }
            dest.writeString(ItemCode2);
            if (MinPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(MinPrice);
            }
            if (MaxPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(MaxPrice);
            }
            dest.writeParcelable(Image, flags);
            dest.writeString(HasBatchNo);
            dest.writeString(Location);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Item> CREATOR = new Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public String getItemCodeInput() {
            return ItemCodeInput;
        }

        public void setItemCodeInput(String itemCodeInput) {
            ItemCodeInput = itemCodeInput;
            notifyPropertyChanged(BR.itemCodeInput);
        }

        @Bindable
        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
            notifyPropertyChanged(BR.description);
        }

        @Bindable
        public String getDesc2() {
            return Desc2;
        }

        public void setDesc2(String desc2) {
            Desc2 = desc2;
            notifyPropertyChanged(BR.desc2);
        }

        @Bindable
        public String getItemGroup() {
            return ItemGroup;
        }

        public void setItemGroup(String itemGroup) {
            ItemGroup = itemGroup;
            notifyPropertyChanged(BR.itemGroup);
        }

        @Bindable
        public String getItemType() {
            return ItemType;
        }

        public void setItemType(String itemType) {
            ItemType = itemType;
            notifyPropertyChanged(BR.itemType);
        }

        @Bindable
        public String getTaxType() {
            return TaxType;
        }

        public void setTaxType(String taxType) {
            TaxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public String getPurchaseTaxType() {
            return PurchaseTaxType;
        }

        public void setPurchaseTaxType(String purchaseTaxType) {
            PurchaseTaxType = purchaseTaxType;
            notifyPropertyChanged(BR.purchaseTaxType);
        }

        @Bindable
        public String getBaseUOM() {
            return BaseUOM;
        }

        public void setBaseUOM(String baseUOM) {
            BaseUOM = baseUOM;
            notifyPropertyChanged(BR.baseUOM);
        }

        @Bindable
        public Float getPrice() {
            return Price;
        }

        public void setPrice(Float price) {
            Price = price;
            notifyPropertyChanged(BR.price);
        }

        @Bindable
        public Float getPrice2() {
            return Price2;
        }

        public void setPrice2(Float price2) {
            Price2 = price2;
            notifyPropertyChanged(BR.price2);
        }

        @Bindable
        public Float getPrice3() {
            return Price3;
        }

        public void setPrice3(Float price3) {
            Price3 = price3;
            notifyPropertyChanged(BR.price3);
        }

        @Bindable
        public Float getPrice4() {
            return Price4;
        }

        public void setPrice4(Float price4) {
            Price4 = price4;
            notifyPropertyChanged(BR.price4);
        }

        @Bindable
        public Float getPrice5() {
            return Price5;
        }

        public void setPrice5(Float price5) {
            Price5 = price5;
            notifyPropertyChanged(BR.price5);
        }

        @Bindable
        public Float getPrice6() {
            return Price6;
        }

        public void setPrice6(Float price6) {
            Price6 = price6;
            notifyPropertyChanged(BR.price6);
        }

        @Bindable
        public Double getQty() {
            return Qty;
        }

        public void setQty(Double qty) {
            Qty = qty;
            notifyPropertyChanged(BR.qty);
        }

        @Bindable
        public String getBarCode() {
            return BarCode;
        }

        public void setBarCode(String barCode) {
            BarCode = barCode;
            notifyPropertyChanged(BR.barCode);
        }

        @Bindable
        public String getShelf() {
            return Shelf;
        }

        public void setShelf(String shelf) {
            Shelf = shelf;
            notifyPropertyChanged(BR.shelf);
        }

       @Bindable
        public Float getRate() {
            return Rate;
        }

        public void setRate(Float rate) {
            Rate = rate;
            notifyPropertyChanged(BR.rate);
        }

        @Bindable
        public String getItemCode2() {
            return ItemCode2;
        }

        public void setItemCode2(String itemCode2) {
            ItemCode2 = itemCode2;
            notifyPropertyChanged(BR.itemCode2);
        }

        @Bindable
        public Float getMinPrice() {
            return MinPrice;
        }

        public void setMinPrice(Float minPrice) {
            MinPrice = minPrice;
            notifyPropertyChanged(BR.minPrice);
        }

        @Bindable
        public Float getMaxPrice() {
            return MaxPrice;
        }

        public void setMaxPrice(Float maxPrice) {
            MaxPrice = maxPrice;
            notifyPropertyChanged(BR.maxPrice);
        }

        @Bindable
        public Bitmap getImage() {
            return Image;
        }

        public void setImage(Bitmap image) {
            Image = image;
            notifyPropertyChanged(BR.image);
        }

        @Bindable
        public String getHasBatchNo() {
            return HasBatchNo;
        }

        public void setHasBatchNo(String hasBatchNo) {
            HasBatchNo = hasBatchNo;
            notifyPropertyChanged(BR.hasBatchNo);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }
    }

    public static class ItemUOM extends BaseObservable implements Parcelable {
        public String ItemCode;
        public String UOM;
        public Float Rate;
        public Float Price;
        public Float Price2;
        public Float Price3;
        public Float Price4;
        public Float Price5;
        public Float Price6;
        public String Shelf;
        public String BarCode;
        public Float BalQty;
        public Float BaseBalQty;
        public Float MinPrice;
        public Float MaxPrice;

        public ItemUOM() {
        }

        public ItemUOM(String itemCode, String uom, Float rate, Float price, Float price2, Float price3, Float price4, Float price5, Float price6, String shelf, String barCode, Float minPrice, Float maxPrice) {
            ItemCode = itemCode;
            UOM = uom;
            Rate = rate;
            Price = price;
            Price2 = price2;
            Price3 = price3;
            Price4 = price4;
            Price5 = price5;
            Price6 = price6;
            Shelf = shelf;
            BarCode = barCode;
            MinPrice = minPrice;
            MaxPrice = maxPrice;
        }


        protected ItemUOM(Parcel in) {
            ItemCode = in.readString();
            UOM = in.readString();
            if (in.readByte() == 0) {
                Rate = null;
            } else {
                Rate = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price = null;
            } else {
                Price = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price2 = null;
            } else {
                Price2 = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price3 = null;
            } else {
                Price3 = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price4 = null;
            } else {
                Price4 = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price5 = null;
            } else {
                Price5 = in.readFloat();
            }
            if (in.readByte() == 0) {
                Price6 = null;
            } else {
                Price6 = in.readFloat();
            }
            Shelf = in.readString();
            BarCode = in.readString();
            if (in.readByte() == 0) {
                MinPrice = null;
            } else {
                MinPrice = in.readFloat();
            }
            if (in.readByte() == 0) {
                MaxPrice = null;
            } else {
                MaxPrice = in.readFloat();
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ItemCode);
            dest.writeString(UOM);
            if (Rate == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Rate);
            }
            if (Price == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price);
            }
            if (Price2 == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price2);
            }
            if (Price3 == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price3);
            }
            if (Price4 == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price4);
            }
            if (Price5 == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price5);
            }
            if (Price6 == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Price6);
            }
            dest.writeString(Shelf);
            dest.writeString(BarCode);
            if (MinPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(MinPrice);
            }
            if (MaxPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(MaxPrice);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ItemUOM> CREATOR = new Creator<ItemUOM>() {
            @Override
            public ItemUOM createFromParcel(Parcel in) {
                return new ItemUOM(in);
            }

            @Override
            public ItemUOM[] newArray(int size) {
                return new ItemUOM[size];
            }
        };

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String UOM) {
            this.UOM = UOM;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Float getRate() {
            return Rate;
        }

        public void setRate(Float rate) {
            this.Rate = rate;
            notifyPropertyChanged(BR.rate);
        }

        @Bindable
        public Float getPrice() {
            return Price;
        }

        public void setPrice(Float price) {
            Price = price;
            notifyPropertyChanged(BR.price);
        }

        @Bindable
        public Float getPrice2() {
            return Price2;
        }

        public void setPrice2(Float price2) {
            Price2 = price2;
            notifyPropertyChanged(BR.price2);
        }

        @Bindable
        public Float getPrice3() {
            return Price3;
        }

        public void setPrice3(Float price3) {
            Price3 = price3;
            notifyPropertyChanged(BR.price3);
        }

        @Bindable
        public Float getPrice4() {
            return Price4;
        }

        public void setPrice4(Float price4) {
            Price4 = price4;
            notifyPropertyChanged(BR.price4);
        }

        @Bindable
        public Float getPrice5() {
            return Price5;
        }

        public void setPrice5(Float price5) {
            Price5 = price5;
            notifyPropertyChanged(BR.price5);
        }

        @Bindable
        public Float getPrice6() {
            return Price6;
        }

        public void setPrice6(Float price6) {
            Price6 = price6;
            notifyPropertyChanged(BR.price6);
        }

        @Bindable
        public String getBarCode() {
            return BarCode;
        }

        public void setBarCode(String barCode) {
            BarCode = barCode;
            notifyPropertyChanged(BR.barCode);
        }

        @Bindable
        public String getShelf() {
            return Shelf;
        }

        public void setShelf(String shelf) {
            Shelf = shelf;
            notifyPropertyChanged(BR.shelf);
        }

        @Bindable
        public Float getBalQty() {
            return BalQty;
        }

        public void setBalQty(Float balQty) {
            BalQty = balQty;
            notifyPropertyChanged(BR.balQty);
        }

        @Bindable
        public Float getBaseBalQty() {
            return BaseBalQty;
        }

        public void setBaseBalQty(Float baseBalQty) {
            BaseBalQty = baseBalQty;
            notifyPropertyChanged(BR.baseBalQty);
        }

        @Bindable
        public Float getMinPrice() {
            return MinPrice;
        }

        public void setMinPrice(Float minPrice) {
            MinPrice = minPrice;
            notifyPropertyChanged(BR.minPrice);
        }

        @Bindable
        public Float getMaxPrice() {
            return MaxPrice;
        }

        public void setMaxPrice(Float maxPrice) {
            MaxPrice = maxPrice;
            notifyPropertyChanged(BR.maxPrice);
        }

    }

    public static class SalesAgent implements Parcelable {
        public String SalesAgent;
        public String Description;


        public SalesAgent(String _SalesAgent, String _Description) {
            this.SalesAgent = _SalesAgent;
            this.Description = _Description;
        }

        protected SalesAgent(Parcel in) {
            SalesAgent = in.readString();
            Description = in.readString();
        }

        public static final Creator<SalesAgent> CREATOR = new Creator<SalesAgent>() {
            @Override
            public SalesAgent createFromParcel(Parcel in) {
                return new SalesAgent(in);
            }

            @Override
            public SalesAgent[] newArray(int size) {
                return new SalesAgent[size];
            }
        };

        public String getSalesAgent() {
            return SalesAgent;
        }

        public void setSalesAgent(String salesAgent) {
            SalesAgent = salesAgent;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(SalesAgent);
            dest.writeString(Description);
        }
    }

    public static class PurchaseAgent implements Parcelable {
        public String PurchaseAgent;
        public String Description;


        public PurchaseAgent(String _PurchaseAgent, String _Description) {
            this.PurchaseAgent = _PurchaseAgent;
            this.Description = _Description;
        }

        protected PurchaseAgent(Parcel in) {
            PurchaseAgent = in.readString();
            Description = in.readString();
        }

        public static final Creator<PurchaseAgent> CREATOR = new Creator<PurchaseAgent>() {
            @Override
            public PurchaseAgent createFromParcel(Parcel in) {
                return new PurchaseAgent(in);
            }

            @Override
            public PurchaseAgent[] newArray(int size) {
                return new PurchaseAgent[size];
            }
        };

        public String getPurchaseAgent() {
            return PurchaseAgent;
        }

        public void setPurchaseAgent(String PurchaseAgentFP) {
            PurchaseAgent = PurchaseAgentFP;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(PurchaseAgent);
            dest.writeString(Description);
        }
    }

    public static class Location extends BaseObservable implements Parcelable {
        public String Location;
        public String Description;
        public Double bal;

        public Location(String _Location, String _Description) {
            this.Location = _Location;
            this.Description = _Description;
        }

        public Location(String _Location, String _Description, Double _Bal) {
            this.Location = _Location;
            this.Description = _Description;
            this.bal = _Bal;
        }


        public Location() {
        }


        protected Location(Parcel in) {
            Location = in.readString();
            Description = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(Location);
            dest.writeString(Description);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Location> CREATOR = new Creator<Location>() {
            @Override
            public Location createFromParcel(Parcel in) {
                return new Location(in);
            }

            @Override
            public Location[] newArray(int size) {
                return new Location[size];
            }
        };

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
            notifyPropertyChanged(BR.description);
        }

        public String toString() {
            return Location;
        }

    }

    public static class SellingPrice implements Parcelable {
        public String PriceName;
        public Float Price;

        public SellingPrice(String _PriceName, Float _Price) {
            this.PriceName = _PriceName;
            this.Price = _Price;
        }

        protected SellingPrice(Parcel in) {
            PriceName = in.readString();
            Price = in.readFloat();
        }

        public static final Creator<SellingPrice> CREATOR = new Creator<SellingPrice>() {
            @Override
            public SellingPrice createFromParcel(Parcel in) {
                return new SellingPrice(in);
            }

            @Override
            public SellingPrice[] newArray(int size) {
                return new SellingPrice[size];
            }
        };

        public String getPriceName() {
            return PriceName;
        }

        public void setPriceName(String priceName) {
            PriceName = priceName;
        }

        public Float getPrice() {
            return Price;
        }

        public void setPrice(Float price) {
            Price = price;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(PriceName);
            dest.writeFloat(Price);
        }
    }


    public static class HistoryPrice extends BaseObservable implements Parcelable{
        public String DocType;
        public String AccNo;
        public String DebtorName;
        public String ItemCode;
        public String Description;
        public String DocNo;
        public String DocDate;
        public String Location;
        public String Agent;
        public Float Qty;
        public String UOM;
        public Float UnitPrice;
        public Float Discount;
        public Float SubTotal;

        public HistoryPrice() {
        }

        public HistoryPrice(String docType, String accNo, String debtorName, String itemCode,
                            String description, String docNo, String docDate, String location,
                            String agent, Float qty, String uom, Float unitPrice, Float discount, Float subTotal)
        {
            DocType = docType;
            AccNo = accNo;
            DebtorName = debtorName;
            ItemCode = itemCode;
            Description = description;
            DocNo = docNo;
            DocDate = docDate;
            Location = location;
            Agent = agent;
            Qty = qty;
            UOM = uom;
            UnitPrice = unitPrice;
            Discount = discount;
            SubTotal = subTotal;
        }


        protected HistoryPrice(Parcel in) {
            DocType = in.readString();
            AccNo = in.readString();
            DebtorName = in.readString();
            ItemCode = in.readString();
            Description = in.readString();
            DocNo = in.readString();
            DocDate = in.readString();
            Location = in.readString();
            Agent = in.readString();
            if (in.readByte() == 0) {
                Qty = null;
            } else {
                Qty = in.readFloat();
            }
            UOM = in.readString();
            if (in.readByte() == 0) {
                UnitPrice = null;
            } else {
                UnitPrice = in.readFloat();
            }
            if (in.readByte() == 0) {
                Discount = null;
            } else {
                Discount = in.readFloat();
            }
            if (in.readByte() == 0) {
                SubTotal = null;
            } else {
                SubTotal = in.readFloat();
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocType);
            dest.writeString(AccNo);
            dest.writeString(DebtorName);
            dest.writeString(ItemCode);
            dest.writeString(Description);
            dest.writeString(DocNo);
            dest.writeString(DocDate);
            dest.writeString(Location);
            dest.writeString(Agent);
            if (Qty == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Qty);
            }
            dest.writeString(UOM);
            if (UnitPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(UnitPrice);
            }
            if (Discount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(Discount);
            }
            if (SubTotal == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(SubTotal);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<HistoryPrice> CREATOR = new Creator<HistoryPrice>() {
            @Override
            public HistoryPrice createFromParcel(Parcel in) {
                return new HistoryPrice(in);
            }

            @Override
            public HistoryPrice[] newArray(int size) {
                return new HistoryPrice[size];
            }
        };

        @Bindable
        public String getDocType() {
            return DocType;
        }

        public void setDocType(String docType) {
            DocType = docType;
            notifyPropertyChanged(BR.docType);
        }

        @Bindable
        public String getAccNo() {
            return AccNo;
        }

        public void setAccNo(String accNo) {
            AccNo = accNo;
            notifyPropertyChanged(BR.accNo);
        }

        @Bindable
        public String getDebtorName() { return DebtorName; }

        public void setDebtorName(String debtorName) {
            DebtorName = debtorName;
            notifyPropertyChanged(BR.debtorName);
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
            notifyPropertyChanged(BR.description);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getAgent() {
            return Agent;
        }

        public void setAgent(String agent) {
            Agent = agent;
            notifyPropertyChanged(BR.agent);
        }

        @Bindable
        public Float getQty() {
            return Qty;
        }

        public void setQty(Float qty) {
            Qty = qty;
            notifyPropertyChanged(BR.qty);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Float getDiscount() {
            return Discount;
        }

        public void setDiscount(Float discount) {
            Discount = discount;
            notifyPropertyChanged(BR.discount);
        }

        @Bindable
        public Float getUnitPrice() {
            return UnitPrice;
        }

        public void setUnitPrice(Float unitPrice) {
            UnitPrice = unitPrice;
            notifyPropertyChanged(BR.unitPrice);
        }

        @Bindable
        public Float getSubTotal() {
            return SubTotal;
        }

        public void setSubTotal(Float subTotal) {
            SubTotal = subTotal;
            notifyPropertyChanged(BR.subTotal);
        }
    }

    public static class TaxType extends BaseObservable implements Parcelable {
        public String TaxType;
        public String Description;
        public Float TaxRate;

        public TaxType(String _TaxType, String _Description, Float _TaxRate) {
            this.TaxType = _TaxType;
            this.Description = _Description;
            this.TaxRate = _TaxRate;
        }

        public TaxType() {
        }


        protected TaxType(Parcel in) {
            TaxType = in.readString();
            Description = in.readString();
            if (in.readByte() == 0) {
                TaxRate = null;
            } else {
                TaxRate = in.readFloat();
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(TaxType);
            dest.writeString(Description);
            if (TaxRate == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(TaxRate);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<TaxType> CREATOR = new Creator<TaxType>() {
            @Override
            public TaxType createFromParcel(Parcel in) {
                return new TaxType(in);
            }

            @Override
            public TaxType[] newArray(int size) {
                return new TaxType[size];
            }
        };

        @Bindable
        public String getTaxType() {
            return TaxType;
        }

        public void setTaxType(String taxType) {
            TaxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
            notifyPropertyChanged(BR.description);
        }

        @Bindable
        public Float getTaxRate() {
            return TaxRate;
        }

        public void setTaxRate(Float taxRate) {
            TaxRate = taxRate;
            notifyPropertyChanged(BR.taxRate);
        }

    }

    public static class StockBalance extends BaseObservable implements Parcelable {
        public String ItemCode;
        public String UOM;
        public String Location;
        public Float BalQty;
        public String BatchNo;

        public StockBalance() {
        }

        public StockBalance(String itemCode, String UOM, String location, Float balQty, String batchNo) {
            ItemCode = itemCode;
            this.UOM = UOM;
            Location = location;
            BalQty = balQty;
            BatchNo = batchNo;
        }

        public StockBalance(String itemCode, String UOM, String location, Float balQty) {
            ItemCode = itemCode;
            this.UOM = UOM;
            Location = location;
            BalQty = balQty;
        }

        protected StockBalance(Parcel in) {
            ItemCode = in.readString();
            UOM = in.readString();
            Location = in.readString();
            if (in.readByte() == 0) {
                BalQty = null;
            } else {
                BalQty = in.readFloat();
            }
            BatchNo = in.readString();
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String UOM) {
            this.UOM = UOM;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public Float getBalQty() {
            return BalQty;
        }

        public void setBalQty(Float balQty) {
            BalQty = balQty;
            notifyPropertyChanged(BR.balQty);
        }

        @Bindable
        public String getBatchNo() {
            return BatchNo;
        }

        public void setBatchNo(String batchNo) {
            BatchNo = batchNo;
            notifyPropertyChanged(BR.batchNo);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ItemCode);
            dest.writeString(UOM);
            dest.writeString(Location);
            if (BalQty == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(BalQty);
            }
            dest.writeString(BatchNo);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StockBalance> CREATOR = new Creator<StockBalance>() {
            @Override
            public StockBalance createFromParcel(Parcel in) {
                return new StockBalance(in);
            }

            @Override
            public StockBalance[] newArray(int size) {
                return new StockBalance[size];
            }
        };
    }

    public static class StockCount extends BaseObservable{
        public String DocDate;
        public String ItemCode;
        public String ItemCodeInput;
        public String Description;
        public String Location;
        public String UOM;
        public Float Qty;
        public Integer Exported;

        public StockCount(String docDate, String itemCode, String description, String location,
                          String UOM, Float qty, Integer exported) {
            DocDate = docDate;
            ItemCode = itemCode;
            Description = description;
            Location = location;
            this.UOM = UOM;
            Qty = qty;
            Exported = exported;
        }

        public StockCount() {
            Qty = Float.valueOf(1);
            setLocation(null);
            setItemCode(null);
            setDescription(null);
            setUOM(null);
            setQty(Float.valueOf(1));
            setExported(0);
        }

        public void reset() {
            setItemCode(null);
            setDescription(null);
            setUOM(null);
            setQty(Float.valueOf(1));
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate)
        {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {

            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getItemCodeInput() {
            return ItemCodeInput;
        }

        public void setItemCodeInput(String itemCodeInput) {

            ItemCodeInput = itemCodeInput;
            notifyPropertyChanged(BR.itemCodeInput);
        }

        @Bindable
        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {

            Description = description;
            notifyPropertyChanged(BR.description);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String UOM) {
            this.UOM = UOM;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Float getQty() {
            return Qty;
        }

        public void setQty(Float qty) {
            Qty = qty;
            notifyPropertyChanged(BR.qty);
        }
        @Bindable
        public Integer getExported() {
            return Exported;
        }

        public void setExported(Integer exported) {
            Exported = exported;
            notifyPropertyChanged(BR.exported);
        }

    }

    public static class StockCountDetails extends BaseObservable implements Parcelable{
        public String SalesAgent;

        public StockCountDetails(String salesAgent) {
            SalesAgent = salesAgent;
        }

        public String getSalesAgentStockName() {
            return SalesAgent;
        }

        public void setSalesAgentStockName(String debtorName) {
            SalesAgent = debtorName;
        }

        protected StockCountDetails(Parcel in) {
            SalesAgent = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(SalesAgent);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StockCountDetails> CREATOR = new Creator<StockCountDetails>() {
            @Override
            public StockCountDetails createFromParcel(Parcel in) {
                return new StockCountDetails(in);
            }

            @Override
            public StockCountDetails[] newArray(int size) {
                return new StockCountDetails[size];
            }
        };
    }

    public static class InvoiceDetails extends BaseObservable implements Parcelable {
        public Integer ID;
        public String DocNo;
        public String DocDate;
        public String Location;
        public String ItemCode;
        public String ItemCodeInput;
        public String ItemDescription;
        public String UOM;
        public Double Quantity;
        public Double UPrice;
        public Double Discount;
        public Double SubTotal;
        public String TaxType;
        public Double TaxRate;
        public Double TaxValue;
        public Double Total_Ex;
        public Double Total_In;
        public String Line_No;
        public String Remarks;
        public String BatchNo;
        public String Remarks2;
        public String DiscountText;

        public InvoiceDetails() {
            Quantity = Double.valueOf(1);
            Discount = Double.valueOf(0);
            SubTotal = Double.valueOf(0);
            UPrice = Double.valueOf(0);
            TaxRate = Double.valueOf(0);
            TaxValue = Double.valueOf(0);
            Total_In = Double.valueOf(0);
            Total_Ex = Double.valueOf(0);
            setItemCode(null);
            setQuantity(Double.valueOf(1));
            setDiscount(Double.valueOf(0));
            setRemarks(null);
            setRemarks2(null);
        }


        public InvoiceDetails(Integer id, String docNo, String location, String itemCode, String itemDescription, String UOM, Double quantity, Double UPrice, Double discount, Double subTotal, String taxType, Double taxRate, Double taxValue, Double total_Ex, Double total_In, String line_no, String remarks, String batchno, String remarks2) {
            ID = id;
            DocNo = docNo;
            Location = location;
            ItemCode = itemCode;
            ItemDescription = itemDescription;
            this.UOM = UOM;
            Quantity = quantity;
            this.UPrice = UPrice;
            Discount = discount;
            SubTotal = subTotal;
            TaxType = taxType;
            TaxRate = taxRate;
            TaxValue = taxValue;
            Total_Ex = total_Ex;
            Total_In = total_In;
            Line_No = line_no;
            Remarks = remarks;
            BatchNo = batchno;
            Remarks2 = remarks2;
        }

        protected InvoiceDetails(Parcel in) {
            if (in.readByte() == 0) {
                ID = null;
            } else {
                ID = in.readInt();
            }
            DocNo = in.readString();
            Location = in.readString();
            ItemCode = in.readString();
            ItemDescription = in.readString();
            if (in.readByte() == 0) {
                Quantity = null;
            } else {
                Quantity = in.readDouble();
            }
            if (in.readByte() == 0) {
                UOM = null;
            } else {
                UOM = in.readString();
            }
            if (in.readByte() == 0) {
                UPrice = null;
            } else {
                UPrice = in.readDouble();
            }
            if (in.readByte() == 0) {
                Discount = null;
            } else {
                Discount = in.readDouble();
            }
            if (in.readByte() == 0) {
                SubTotal = null;
            } else {
                SubTotal = in.readDouble();
            }
            TaxType = in.readString();
            if (in.readByte() == 0) {
                TaxRate = null;
            } else {
                TaxRate = in.readDouble();
            }
            if (in.readByte() == 0) {
                TaxValue = null;
            } else {
                TaxValue = in.readDouble();
            }
            if (in.readByte() == 0) {
                Total_Ex = null;
            } else {
                Total_Ex = in.readDouble();
            }
            if (in.readByte() == 0) {
                Total_In = null;
            } else {
                Total_In = in.readDouble();
            }
            Line_No = in.readString();
            Remarks = in.readString();
            BatchNo = in.readString();
            Remarks2 = in.readString();
            DiscountText = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (ID == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(ID);
            }
            dest.writeString(DocNo);
            dest.writeString(Location);
            dest.writeString(ItemCode);
            dest.writeString(ItemDescription);

            if (Quantity == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Quantity);
            }
            if (UOM == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(UOM);
            }
            if (UPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(UPrice);
            }
            if (Discount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Discount);
            }
            if (SubTotal == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(SubTotal);
            }
            dest.writeString(TaxType);
            if (TaxRate == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TaxRate);
            }
            if (TaxValue == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TaxValue);
            }
            if (Total_Ex == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Total_Ex);
            }
            if (Total_In == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Total_In);
            }
            dest.writeString(Line_No);
            dest.writeString(Remarks);
            dest.writeString(BatchNo);
            dest.writeString(Remarks2);
            dest.writeString(DiscountText);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<InvoiceDetails> CREATOR = new Creator<InvoiceDetails>() {
            @Override
            public InvoiceDetails createFromParcel(Parcel in) {
                return new InvoiceDetails(in);
            }

            @Override
            public InvoiceDetails[] newArray(int size) {
                return new InvoiceDetails[size];
            }
        };

        @Bindable
        public Integer getID() {
            return ID;
        }

        public void setID(Integer ID) {
            this.ID = ID;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {

            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getItemCodeInput() {
            return ItemCodeInput;
        }

        public void setItemCodeInput(String itemCodeInput) {
            ItemCodeInput = itemCodeInput;
            notifyPropertyChanged(BR.itemCodeInput);
        }


        @Bindable
        public String getItemDescription() {
            return ItemDescription;
        }

        public void setItemDescription(String itemDescription) {
            ItemDescription = itemDescription;
            notifyPropertyChanged(BR.itemDescription);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Double getQuantity() {
            return Quantity;
        }

        public void setQuantity(Double quantity) {
            Quantity = quantity;
            notifyPropertyChanged(BR.quantity);
        }

        @Bindable
        public Double getUPrice() {
            return UPrice;
        }

        public void setUPrice(Double UPrice) {
            this.UPrice = UPrice;
            notifyPropertyChanged(BR.uPrice);
        }

        @Bindable
        public Double getDiscount() {
            return Discount;
        }

        public void setDiscount(Double discount) {
            Discount = discount;
            notifyPropertyChanged(BR.discount);
        }

        @Bindable
        public Double getSubTotal() {
            return SubTotal;
        }

        public void setSubTotal(Double subTotal) {
            SubTotal = subTotal;
            notifyPropertyChanged(BR.subTotal);
        }

        @Bindable
        public String getTaxType() {
            return TaxType;
        }

        public void setTaxType(String taxType) {
            TaxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public Double getTaxRate() {
            return TaxRate;
        }

        public void setTaxRate(Double taxRate) {
            TaxRate = taxRate;
            notifyPropertyChanged(BR.taxRate);
        }

        @Bindable
        public Double getTaxValue() {
            return TaxValue;
        }

        public void setTaxValue(Double taxValue) {
            TaxValue = taxValue;
            notifyPropertyChanged(BR.taxValue);
        }

        @Bindable
        public Double getTotal_Ex() {
            return Total_Ex;
        }

        public void setTotal_Ex(Double total_Ex) {
            Total_Ex = total_Ex;
            notifyPropertyChanged(BR.total_Ex);
        }

        @Bindable
        public Double getTotal_In() {
            return Total_In;
        }

        public void setTotal_In(Double total_In) {
            Total_In = total_In;
            notifyPropertyChanged(BR.total_In);
        }

        @Bindable
        public String getLine_No() {
            return Line_No;
        }

        public void setLine_No(String line_No) {
            this.Line_No = line_No;
            notifyPropertyChanged(BR.line_No);
        }

        @Bindable
        public String getRemarks() { return Remarks;
        }

        public void setRemarks(String remarks) {
            this.Remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getRemarks2() { return Remarks2;
        }

        public void setRemarks2(String remarks2) {
            this.Remarks2 = remarks2;
            notifyPropertyChanged(BR.remarks2);
        }

        @Bindable
        public String getBatchNo() { return BatchNo;
        }

        public void setBatchNo(String batchNo) {
            this.BatchNo = batchNo;
            notifyPropertyChanged(BR.batch_No);
        }

        @Bindable
        public String getDiscountText() { return DiscountText;
        }

        public void setDiscountText(String discountText) {
            this.DiscountText = discountText;
            notifyPropertyChanged(BR.discountText);
        }
    }

    public static class Invoice extends BaseObservable implements Parcelable {
        private String docNo;
        private String createdTimeStamp;
        private String docDate;
        private String debtorCode;
        private String debtorName;
        private String agent;
        private String taxType;
        private String docType;
        private Integer uploaded;
        private String signature;
        private String phone;
        private String fax;
        private String creditTerm;
        private String attention;
        private String address1;
        private String address2;
        private String address3;
        private String address4;
        private String Remarks;
        private String Status;
        private String Remarks2;
        private String Remarks3;
        private String Remarks4;
        private String Createduser;
        private String LastModifiedDateTime;
        private String LastModifiedUser;

        private String Terms;
        private String DetailDiscount;
        private List<AC_Class.InvoiceDetails> invoiceDetailsList;

        public Invoice() {
            docNo = null;
            createdTimeStamp = null;
            docDate = null;
            debtorCode = null;
            debtorName = null;
            agent = null;
            taxType = null;
            docType = null;
            uploaded = 0;
            signature = null;
            phone = null;
            fax = null;
            attention = null;
            Status = null;
            invoiceDetailsList = new ArrayList<>();
        }



        public Invoice(String docNo, String createdTimeStamp, String docDate, String debtorCode,
                       String debtorName, String agent, String taxType, String docType, String signature,
                       String phone, String fax, String attention, String remarks, String remarks2, String remarks3,
                       String remarks4) {
            this.docNo = docNo;
            this.createdTimeStamp = createdTimeStamp;
            this.docDate = docDate;
            this.debtorCode = debtorCode;
            this.debtorName = debtorName;
            this.agent = agent;
            this.taxType = taxType;
            this.docType = docType;
            this.uploaded = 0;
            this.signature = signature;
            this.phone = phone;
            this.fax = fax;
            this.attention = attention;
            this.Remarks = remarks;
            this.Remarks2 = remarks2;
            this.Remarks3 = remarks3;
            this.Remarks4 = remarks4;
            invoiceDetailsList = new ArrayList<>();
        }

        public Invoice(String docNo, String createdTimeStamp, String docDate, String debtorCode,
                       String debtorName, String agent, String taxType, String docType, String signature,
                       String phone, String fax, String attention,String address1, String address2,
                       String address3, String address4, String remarks, String remarks2, String remarks3,
                       String remarks4, String createduser, String displayterm, String detailDiscount) {
            this.docNo = docNo;
            this.createdTimeStamp = createdTimeStamp;
            this.docDate = docDate;
            this.debtorCode = debtorCode;
            this.debtorName = debtorName;
            this.agent = agent;
            this.taxType = taxType;
            this.docType = docType;
            this.uploaded = 0;
            this.signature = signature;
            this.phone = phone;
            this.fax = fax;
            this.attention = attention;
            this.address1 = address1;
            this.address2 = address2;
            this.address3 = address3;
            this.address4 = address4;
            this.Remarks = remarks;
            this.Remarks2 = remarks2;
            this.Remarks3 = remarks3;
            this.Remarks4 = remarks4;
            this.Createduser = createduser;
            this.Terms = displayterm;
            this.DetailDiscount = detailDiscount;
            invoiceDetailsList = new ArrayList<>();
        }


        protected Invoice(Parcel in) {
            docNo = in.readString();
            createdTimeStamp = in.readString();
            docDate = in.readString();
            debtorCode = in.readString();
            debtorName = in.readString();
            agent = in.readString();
            taxType = in.readString();
            docType = in.readString();
            uploaded = in.readInt();
            signature = in.readString();
            phone = in.readString();
            fax = in.readString();
            creditTerm = in.readString();
            attention = in.readString();
            address1 = in.readString();
            address2 = in.readString();
            address3 = in.readString();
            address4 = in.readString();
            Remarks = in.readString();
            Status = in.readString();
            Remarks2 = in.readString();
            Remarks3 = in.readString();
            Remarks4 = in.readString();
            Createduser = in.readString();
            LastModifiedUser = in.readString();
            LastModifiedDateTime = in.readString();
            invoiceDetailsList = new ArrayList<>();
            Terms =  in.readString();
            DetailDiscount = in.readString();
            in.readTypedList(invoiceDetailsList, InvoiceDetails.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(docNo);
            dest.writeString(createdTimeStamp);
            dest.writeString(docDate);
            dest.writeString(debtorCode);
            dest.writeString(debtorName);
            dest.writeString(agent);
            dest.writeString(taxType);
            dest.writeString(docType);
            dest.writeInt(uploaded);
            dest.writeString(signature);
            dest.writeString(phone);
            dest.writeString(fax);
            dest.writeString(creditTerm);
            dest.writeString(attention);
            dest.writeString(address1);
            dest.writeString(address2);
            dest.writeString(address3);
            dest.writeString(address4);
            dest.writeString(Remarks);
            dest.writeString(Status);
            dest.writeString(Remarks2);
            dest.writeString(Remarks3);
            dest.writeString(Remarks4);
            dest.writeString(Createduser);
            dest.writeString(LastModifiedUser);
            dest.writeString(LastModifiedDateTime);
            dest.writeString(Terms);
            dest.writeString(DetailDiscount);
            dest.writeTypedList((List<InvoiceDetails>) invoiceDetailsList);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Invoice> CREATOR = new Creator<Invoice>() {
            @Override
            public Invoice createFromParcel(Parcel in) {
                return new Invoice(in);
            }

            @Override
            public Invoice[] newArray(int size) {
                return new Invoice[size];
            }
        };

        @Bindable
        public String getDocNo() {
            return docNo;
        }

        public void setDocNo(String docNo) {
            this.docNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getCreatedTimeStamp() {
            return createdTimeStamp;
        }

        public void setCreatedTimeStamp(String createdTimeStamp) {
            this.createdTimeStamp = createdTimeStamp;
            notifyPropertyChanged(BR.createdTimeStamp);
        }

        @Bindable
        public String getDocDate() {
            return docDate;
        }

        public void setDocDate(String docDate) {
            this.docDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getDebtorCode() {
            return debtorCode;
        }

        public void setDebtorCode(String debtorCode) {
            this.debtorCode = debtorCode;
            notifyPropertyChanged(BR.debtorCode);
        }

        @Bindable
        public String getDebtorName() {
            return debtorName;
        }

        public void setDebtorName(String debtorName) {
            this.debtorName = debtorName;
            notifyPropertyChanged(BR.debtorName);
        }

        @Bindable
        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
            notifyPropertyChanged(BR.agent);
        }

        @Bindable
        public String getTaxType() {
            return taxType;
        }

        public void setTaxType(String taxType) {
            this.taxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public String getDocType() {
            return docType;
        }

        public void setDocType(String docType) {
            this.docType = docType;
            notifyPropertyChanged(BR.docType);
        }

        @Bindable
        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
            notifyPropertyChanged(BR.signature);
        }

        @Bindable
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
            notifyPropertyChanged(BR.phone);
        }

        @Bindable
        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
            notifyPropertyChanged(BR.fax);
        }

        @Bindable
        public String getCreditTerm() {
            return creditTerm;
        }

        public void setCreditTerm(String creditTerm) {
            this.creditTerm = creditTerm;
            notifyPropertyChanged(BR.creditTerm);
        }

        @Bindable
        public String getAttention() {
            return attention;
        }

        public void setAttention(String attention) {
            this.attention = attention;
            notifyPropertyChanged(BR.attention);
        }

        @Bindable
        public Integer getUploaded() {
            return uploaded;
        }

        public void setUploaded(Integer uploaded) {
            this.uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1FP) {
            address1 = address1FP;
            notifyPropertyChanged(BR.address1);
        }

        @Bindable
        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2FP) {
            address2 = address2FP;
            notifyPropertyChanged(BR.address2);
        }

        @Bindable
        public String getAddress3() {
            return address3;
        }

        public void setAddress3(String address3FP) {
            address3 = address3FP;
            notifyPropertyChanged(BR.address3);
        }

        @Bindable
        public String getAddress4() { return address4; }

        public void setAddress4(String address4FP) {
            address4 = address4FP;
            notifyPropertyChanged(BR.address4);
        }

        @Bindable
        public String getRemarks() { return Remarks; }

        public void setRemarks(String remarksFP) {
            Remarks = remarksFP;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getRemarks2() { return Remarks2; }

        public void setRemarks2(String remarks2FP) {
            Remarks2 = remarks2FP;
            notifyPropertyChanged(BR.remarks2);
        }

        @Bindable
        public String getRemarks3() { return Remarks3; }

        public void setRemarks3(String remarks3FP) {
            Remarks3 = remarks3FP;
            notifyPropertyChanged(BR.remarks3);
        }

        @Bindable
        public String getRemarks4() { return Remarks4; }

        public void setRemarks4(String remarks4FP) {
            Remarks4 = remarks4FP;
            notifyPropertyChanged(BR.remarks4);
        }

        @Bindable
        public String getStatus() { return Status; }

        public void setStatus(String statusFP) {
            Status = statusFP;
            notifyPropertyChanged(BR.status);
        }

        @Bindable
        public String getCreateduser() {
            return Createduser;
        }

        public void setCreateduser(String createduser) {
            this.Createduser = createduser;
            notifyPropertyChanged(BR.createdUser);
        }


        @Bindable
        public String getLastModifiedUser() {
            return LastModifiedUser;
        }

        public void setLastModifiedUser(String lastModifiedUser) {
            this.LastModifiedUser = lastModifiedUser;
            notifyPropertyChanged(BR.lastModifiedUser);
        }

        @Bindable
        public String getLastModifiedDateTime() {
            return LastModifiedDateTime;
        }

        public void setLastModifiedDateTime(String lastModifiedDateTime) {
            this.LastModifiedDateTime = lastModifiedDateTime;
            notifyPropertyChanged(BR.lastModifiedDateTime);
        }

        @Bindable
        public String getTerms() {
            return Terms;
        }

        public void setTerms(String termsFP) {
            Terms = termsFP;
            notifyPropertyChanged(BR.terms);
        }

        @Bindable
        public String getDetailDiscount() {
            return DetailDiscount;
        }

        public void setDetailDiscount(String detailDiscountFP) {
            DetailDiscount = detailDiscountFP;
            notifyPropertyChanged(BR.detailDiscount);
        }

        @Bindable
        public List<AC_Class.InvoiceDetails> getInvoiceDetailsList() { return invoiceDetailsList; }

        public void setInvoiceDetailsList(List<InvoiceDetails> invoiceDetailsList) {
            this.invoiceDetailsList = invoiceDetailsList;
//            notifyPropertyChanged(BR.);
        }

        public void addInvoiceDetail(InvoiceDetails invoiceDetails) {
            invoiceDetailsList.add(invoiceDetails);
        }

        public void removeInvoiceDetail(int index) {
            invoiceDetailsList.remove(index);
        }

        public AC_Class.InvoiceDetails getInvoiceDetail(int id) {
            int i;
            for (i=0; i<invoiceDetailsList.size(); i++) {
                if (invoiceDetailsList.get(i).getID()==id) {
                    return invoiceDetailsList.get(i);
                }
            }
            return null;
        }
    }

    public static class InvoiceMenu extends BaseObservable implements Parcelable {
        public String DocType;
        public String DocDate;
        public String DocNo;
        public String DebtorCode;
        public String DebtorName;
        public String DebtorName2;
        public String Agent;
        public Double TotalEx;
        public Double TotalTax;
        public Double TotalIn;
        public int Uploaded;
        public String Status;
        public String Remarks;
        public String Remarks2;
        public String Remarks3;
        public String Remarks4;

        public InvoiceMenu() {
        }

        public InvoiceMenu(String docType, String docDate, String docNo, String debtorCode, String debtorName, String debtorName2, String agent, Double totalEx, Double totalTax, Double totalIn, int uploaded, String status, String remarks, String remarks2, String remarks3, String remarks4) {
            DocType = docType;
            DocDate = docDate;
            DocNo = docNo;
            DebtorCode = debtorCode;
            DebtorName = debtorName;
            DebtorName2 = debtorName2;
            Agent = agent;
            TotalEx = totalEx;
            TotalTax = totalTax;
            TotalIn = totalIn;
            Uploaded = uploaded;
            Status =status;
            Remarks = remarks;
            Remarks2 = remarks2;
            Remarks3 = remarks3;
            Remarks4 = remarks4;
        }

        public InvoiceMenu(String docType, String docDate, String docNo, String debtorCode, String debtorName, Double totalIn) {
            DocType = docType;
            DocDate = docDate;
            DocNo = docNo;
            DebtorCode = debtorCode;
            DebtorName = debtorName;
            TotalIn = totalIn;
        }

        @Bindable
        public String getDocType() {
            return DocType;
        }

        public void setDocType(String docType) {
            DocType = docType;
            notifyPropertyChanged(BR.docType);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDebtorCode() {
            return DebtorCode;
        }

        public void setDebtorCode(String debtorCode) {
            DebtorCode = debtorCode;
            notifyPropertyChanged(BR.debtorCode);
        }

        @Bindable
        public String getDebtorName() {
            return DebtorName;
        }

        public void setDebtorName(String debtorName) {
            DebtorName = debtorName;
            notifyPropertyChanged(BR.debtorName);
        }

        @Bindable
        public String getDebtorName2() {
            return DebtorName2;
        }

        public void setDebtorName2(String debtorName2) {
            DebtorName2 = debtorName2;
            notifyPropertyChanged(BR.debtorName2);
        }

        @Bindable
        public String getAgent() {
            return Agent;
        }

        public void setAgent(String agent) {
            Agent = agent;
            notifyPropertyChanged(BR.agent);
        }

        @Bindable
        public Double getTotalEx() {
            return TotalEx;
        }

        public void setTotalEx(Double totalEx) {
            TotalEx = totalEx;
            notifyPropertyChanged(BR.total_Ex);
        }

        @Bindable
        public Double getTotalTax() {
            return TotalTax;
        }

        public void setTotalTax(Double totalTax) {
            TotalTax = totalTax;
            notifyPropertyChanged(BR.taxValue);
        }

        @Bindable
        public Double getTotalIn() {
            return TotalIn;
        }

        public void setTotalIn(Double totalIn) {
            TotalIn = totalIn;
            notifyPropertyChanged(BR.total_In);
        }

        @Bindable
        public int getUploaded() {
            return Uploaded;
        }

        public void setUploaded(int uploaded) {
            Uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
            notifyPropertyChanged(BR.status);
        }

        @Bindable
        public String getRemarks() {
            return Remarks;
        }

        public void setRemarks(String remarks) {
            Remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getRemarks2() { return Remarks2; }

        public void setRemarks2(String remarks2FP) {
            Remarks2 = remarks2FP;
            notifyPropertyChanged(BR.remarks2);
        }

        @Bindable
        public String getRemarks3() { return Remarks3; }

        public void setRemarks3(String remarks3FP) {
            Remarks3 = remarks3FP;
            notifyPropertyChanged(BR.remarks3);
        }

        @Bindable
        public String getRemarks4() { return Remarks4; }

        public void setRemarks4(String remarks4FP) {
            Remarks4 = remarks4FP;
            notifyPropertyChanged(BR.remarks4);
        }

        protected InvoiceMenu(Parcel in) {
            DocType = in.readString();
            DocDate = in.readString();
            DocNo = in.readString();
            DebtorCode = in.readString();
            DebtorName = in.readString();
            DebtorName2 = in.readString();
            Agent = in.readString();
            if (in.readByte() == 0) {
                TotalEx = null;
            } else {
                TotalEx = in.readDouble();
            }
            if (in.readByte() == 0) {
                TotalTax = null;
            } else {
                TotalTax = in.readDouble();
            }
            if (in.readByte() == 0) {
                TotalIn = null;
            } else {
                TotalIn = in.readDouble();
            }
            Uploaded = in.readInt();
            Status =  in.readString();
            Remarks = in.readString();
            Remarks2 = in.readString();
            Remarks3 = in.readString();
            Remarks4 = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocType);
            dest.writeString(DocDate);
            dest.writeString(DocNo);
            dest.writeString(DebtorCode);
            dest.writeString(DebtorName);
            dest.writeString(DebtorName2);
            dest.writeString(Agent);
            if (TotalEx == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TotalEx);
            }
            if (TotalTax == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TotalTax);
            }
            if (TotalIn == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TotalIn);
            }
            dest.writeInt(Uploaded);
            dest.writeString(Status);
            dest.writeString(Remarks);
            dest.writeString(Remarks2);
            dest.writeString(Remarks3);
            dest.writeString(Remarks4);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<InvoiceMenu> CREATOR = new Creator<InvoiceMenu>() {
            @Override
            public InvoiceMenu createFromParcel(Parcel in) {
                return new InvoiceMenu(in);
            }

            @Override
            public InvoiceMenu[] newArray(int size) {
                return new InvoiceMenu[size];
            }
        };
    }

    public static class Cart extends BaseObservable implements Parcelable {
        public String ItemCode;
        public String ItemDescription;
        public String UOM;
        public Double Quantity;
        public Double UPrice;

        public Cart() {
            Quantity = Double.valueOf(1);
            UPrice = Double.valueOf(0);
            setItemCode(null);
            setQuantity(Double.valueOf(1));;

        }

        public Cart( String itemCode, String itemDescription, String UOM, Double quantity, Double UPrice) {
            ItemCode = itemCode;
            ItemDescription = itemDescription;
            this.UOM = UOM;
            Quantity = quantity;
            this.UPrice = UPrice;
        }

        protected Cart(Parcel in) {
            ItemCode = in.readString();
            ItemDescription = in.readString();
            if (in.readByte() == 0) {
                Quantity = null;
            } else {
                Quantity = in.readDouble();
            }
            if (in.readByte() == 0) {
                UOM = null;
            } else {
                UOM = in.readString();
            }
            if (in.readByte() == 0) {
                UPrice = null;
            } else {
                UPrice = in.readDouble();
            }

        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ItemCode);
            dest.writeString(ItemDescription);

            if (Quantity == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Quantity);
            }
            if (UOM == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(UOM);
            }
            if (UPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(UPrice);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Cart> CREATOR = new Creator<Cart>() {
            @Override
            public Cart createFromParcel(Parcel in) {
                return new Cart(in);
            }

            @Override
            public Cart[] newArray(int size) {
                return new Cart[size];
            }
        };


        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }


        @Bindable
        public String getItemDescription() {
            return ItemDescription;
        }

        public void setItemDescription(String itemDescription) {
            ItemDescription = itemDescription;
            notifyPropertyChanged(BR.itemDescription);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Double getQuantity() {
            return Quantity;
        }

        public void setQuantity(Double quantity) {
            Quantity = quantity;
            notifyPropertyChanged(BR.quantity);
        }

        @Bindable
        public Double getUPrice() {
            return UPrice;
        }

        public void setUPrice(Double UPrice) {
            this.UPrice = UPrice;
            notifyPropertyChanged(BR.uPrice);
        }

    }

    public static class TransferDtl extends BaseObservable implements Parcelable {
        public Integer ID;
        public String DocNo;
        public String ItemCode;
        public String Description;
        public String UOM;
        public Float Quantity;
        public String LineNo;
        public String BatchNo;

        public TransferDtl(String docNoFP) {
            setDocNo(docNoFP);
            setQuantity(Float.valueOf(1));
        }

        protected TransferDtl(Parcel in) {
            if (in.readByte() == 0) {
                ID = null;
            } else {
                ID = in.readInt();
            }
            DocNo = in.readString();
            ItemCode = in.readString();
            Description = in.readString();
            UOM = in.readString();
            if (in.readByte() == 0) {
                Quantity = null;
            } else {
                Quantity = in.readFloat();
            }
            LineNo = in.readString();
            BatchNo = in.readString();
        }

        public static final Creator<TransferDtl> CREATOR = new Creator<TransferDtl>() {
            @Override
            public TransferDtl createFromParcel(Parcel in) {
                return new TransferDtl(in);
            }

            @Override
            public TransferDtl[] newArray(int size) {
                return new TransferDtl[size];
            }
        };

        public TransferDtl() {

        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            if (ID == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(ID);
            }
            parcel.writeString(DocNo);
            parcel.writeString(ItemCode);
            parcel.writeString(Description);
            parcel.writeString(UOM);
            if (Quantity == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeFloat(Quantity);
            }
            parcel.writeString(LineNo);
            parcel.writeString(BatchNo);
        }

        @Bindable
        public Integer getID() {
            return ID;
        }

        public void setID(Integer idFP) {
            ID = idFP;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNoFP) {
            DocNo = docNoFP;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCodeFP) {
            ItemCode = itemCodeFP;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getDescription() {
            return Description;
        }

        public void setDescription(String descriptionFP) {
            Description = descriptionFP;
            notifyPropertyChanged(BR.description);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uomFP) {
            UOM = uomFP;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Float getQuantity() {
            return Quantity;
        }

        public void setQuantity(Float quantityFP) {
            Quantity = quantityFP;
            notifyPropertyChanged(BR.quantity);
        }

        @Bindable
        public String getLineNo() {
            return LineNo;
        }

        public void setLineNo(String lineno) {
            this.LineNo = lineno;
            notifyPropertyChanged(BR.lineNo);
        }

        @Bindable
        public String getBatchNo() {
            return BatchNo;
        }

        public void setBatchNo(String batchNoFP) {
            BatchNo = batchNoFP;
            notifyPropertyChanged(BR.batchNo);
        }
    }

    public static class Transfer extends BaseObservable implements Parcelable {
        private String DocNo;
        private String DocDate;
        private String CreatedUser;
        private String Reason;
        private String LocationFrom;
        private String LocationTo;
        private Integer Uploaded;
        private String LastModifiedDateTime;
        private String LastModifiedUser;
        private List<AC_Class.TransferDtl> TransferDtlList;

        public Transfer() {
            TransferDtlList = new ArrayList<>();
            setUploaded(0);
        }

        protected Transfer(Parcel in) {
            DocNo = in.readString();
            DocDate = in.readString();
            CreatedUser = in.readString();
            Reason = in.readString();
            LocationFrom = in.readString();
            LocationTo = in.readString();
            if (in.readByte() == 0) {
                Uploaded = null;
            } else {
                Uploaded = in.readInt();
            }
            LastModifiedUser = in.readString();
            LastModifiedDateTime = in.readString();
            TransferDtlList = in.createTypedArrayList(TransferDtl.CREATOR);
        }

        public static final Creator<Transfer> CREATOR = new Creator<Transfer>() {
            @Override
            public Transfer createFromParcel(Parcel in) {
                return new Transfer(in);
            }

            @Override
            public Transfer[] newArray(int size) {
                return new Transfer[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(DocNo);
            parcel.writeString(DocDate);
            parcel.writeString(CreatedUser);
            parcel.writeString(Reason);
            parcel.writeString(LocationFrom);
            parcel.writeString(LocationTo);
            if (Uploaded == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(Uploaded);
            }
            parcel.writeString(LastModifiedUser);
            parcel.writeString(LastModifiedDateTime);
            parcel.writeTypedList(TransferDtlList);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNoFP) {
            DocNo = docNoFP;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDateFP) {
            DocDate = docDateFP;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getReason() {
            return Reason;
        }

        public void setReason(String reasonFP) {
            Reason = reasonFP;
            notifyPropertyChanged(BR.reason);
        }

        @Bindable
        public String getLocationFrom() {
            return LocationFrom;
        }

        public void setLocationFrom(String locationFromFP) {
            LocationFrom = locationFromFP;
            notifyPropertyChanged(BR.locationFrom);
        }

        @Bindable
        public String getLocationTo() {
            return LocationTo;
        }

        public void setLocationTo(String locationToFP) {
            LocationTo = locationToFP;
            notifyPropertyChanged(BR.locationTo);
        }

        @Bindable
        public String getCreatedUser() {
            return CreatedUser;
        }

        public void setCreatedUser(String createdUserFP) {
            CreatedUser = createdUserFP;
            notifyPropertyChanged(BR.createdUser);
        }

        @Bindable
        public String getLastModifiedUser() {
            return LastModifiedUser;
        }

        public void setLastModifiedUser(String lastModifiedUser) {
            this.LastModifiedUser = lastModifiedUser;
            notifyPropertyChanged(BR.lastModifiedUser);
        }

        @Bindable
        public String getLastModifiedDateTime() {
            return LastModifiedDateTime;
        }

        public void setLastModifiedDateTime(String lastModifiedDateTime) {
            this.LastModifiedDateTime = lastModifiedDateTime;
            notifyPropertyChanged(BR.lastModifiedDateTime);
        }

        @Bindable
        public Integer getUploaded() {
            return Uploaded;
        }

        public void setUploaded(Integer uploadedFP) {
            Uploaded = uploadedFP;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public List<TransferDtl> getTransferDtlList() { return TransferDtlList; }

        public void setTransferDtlList(List<TransferDtl> transferDtlListFP) {
            this.TransferDtlList = transferDtlListFP;
            notifyPropertyChanged(BR.transferDtlList);
        }

        public void removeTransferDetail(int index) {
            TransferDtlList.remove(index);
        }

    }

    public static class AutoNumbering {
        public String Name;
        public String Type;
        public int NextNumber;
        public String Prefix;
        public int Length;


        public AutoNumbering(String name, String type, int nextNumber, String prefix, int length) {
            Name = name;
            Type = type;
            NextNumber = nextNumber;
            Prefix = prefix;
            Length = length;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getType() {
            return Type;
        }

        public void setType(String type) {
            Type = type;
        }

        public int getNextNumber() {
            return NextNumber;
        }

        public void setNextNumber(int nextNumber) {
            NextNumber = nextNumber;
        }

        public String getPrefix() {
            return Prefix;
        }

        public void setPrefix(String prefix) {
            Prefix = prefix;
        }

        public int getLength() {
            return Length;
        }

        public void setLength(int length) {
            Length = length;
        }
    }

    public static class CheckOut extends BaseObservable implements Parcelable {
        public String DocNo;
        public Double SubTotal;
        public Double TotalTax;
        public Double Total;


        public CheckOut() {
        }

        public CheckOut(String docNo, Double subTotal, Double totalTax, Double total) {
            DocNo = docNo;
            SubTotal = subTotal;
            TotalTax = totalTax;
            Total = total;
        }

        protected CheckOut(Parcel in) {
            DocNo = in.readString();
            if (in.readByte() == 0) {
                SubTotal = null;
            } else {
                SubTotal = in.readDouble();
            }
            if (in.readByte() == 0) {
                TotalTax = null;
            } else {
                TotalTax = in.readDouble();
            }
            if (in.readByte() == 0) {
                Total = null;
            } else {
                Total = in.readDouble();
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocNo);
            if (SubTotal == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(SubTotal);
            }
            if (TotalTax == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TotalTax);
            }
            if (Total == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Total);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<CheckOut> CREATOR = new Creator<CheckOut>() {
            @Override
            public CheckOut createFromParcel(Parcel in) {
                return new CheckOut(in);
            }

            @Override
            public CheckOut[] newArray(int size) {
                return new CheckOut[size];
            }
        };

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public Double getSubTotal() {
            return SubTotal;
        }

        public void setSubTotal(double subTotal) {
            SubTotal = subTotal;
            notifyPropertyChanged(BR.subTotal);
        }

        @Bindable
        public Double getTotalTax() {
            return TotalTax;
        }

        public void setTotalTax(double totalTax) {
            TotalTax = totalTax;
            notifyPropertyChanged(BR.totalTax);
        }

        @Bindable
        public Double getTotal() {
            return Total;
        }

        public void setTotal(double total) {
            Total = total;
            notifyPropertyChanged(BR.total);
        }

    }

    public static class Payment extends BaseObservable implements Parcelable {
        public String DocNo;
        public String PaymentTime;
        public String PaymentType;
        public String PaymentMethod;
        public Double OriginalAmt;
        public Double PaymentAmt;
        public Double CashChanges;
        public String CCType;
        public String CCNo;
        public String CCExpiryDate;
        public String CCApprovalCode;
        public String ChequeNo;
        public Bitmap Image;

        public Payment() {
            PaymentAmt = Double.valueOf(0);
            CashChanges = Double.valueOf(0);
        }

        public Payment(String docNo, String paymentTime, String paymentType, String paymentMethod,
                       Double originalAmt, Double paymentAmt, Double cashChanges, String CCType,
                       String CCNo, String CCExpiryDate, String CCApprovalCode, String chequeNo)
        {
            DocNo = docNo;
            PaymentTime = paymentTime;
            PaymentType = paymentType;
            PaymentMethod = paymentMethod;
            OriginalAmt = originalAmt;
            PaymentAmt = paymentAmt;
            CashChanges = cashChanges;
            this.CCType = CCType;
            this.CCNo = CCNo;
            this.CCExpiryDate = CCExpiryDate;
            this.CCApprovalCode = CCApprovalCode;
            ChequeNo = chequeNo;
        }

        public Payment(String docNo, String paymentTime, String paymentType, String paymentMethod,
                       Double originalAmt, Double paymentAmt)
        {
            DocNo = docNo;
            PaymentTime = paymentTime;
            PaymentType = paymentType;
            PaymentMethod = paymentMethod;
            OriginalAmt = originalAmt;
            PaymentAmt = paymentAmt;
            CashChanges = null;
            this.CCType = null;
            this.CCNo = null;
            this.CCExpiryDate = null;
            this.CCApprovalCode = null;
            ChequeNo = null;
        }

        protected Payment(Parcel in) {
            DocNo = in.readString();
            PaymentTime = in.readString();
            PaymentType = in.readString();
            PaymentMethod = in.readString();
            if (in.readByte() == 0) {
                OriginalAmt = null;
            } else {
                OriginalAmt = in.readDouble();
            }
            if (in.readByte() == 0) {
                PaymentAmt = null;
            } else {
                PaymentAmt = in.readDouble();
            }
            if (in.readByte() == 0) {
                CashChanges = null;
            } else {
                CashChanges = in.readDouble();
            }
            CCType = in.readString();
            CCNo = in.readString();
            CCExpiryDate = in.readString();
            CCApprovalCode = in.readString();
            ChequeNo = in.readString();
            Image = in.readParcelable(Bitmap.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocNo);
            dest.writeString(PaymentTime);
            dest.writeString(PaymentType);
            dest.writeString(PaymentMethod);
            if (OriginalAmt == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(OriginalAmt);
            }
            if (PaymentAmt == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(PaymentAmt);
            }
            if (CashChanges == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(CashChanges);
            }
            dest.writeString(CCType);
            dest.writeString(CCNo);
            dest.writeString(CCExpiryDate);
            dest.writeString(CCApprovalCode);
            dest.writeString(ChequeNo);
            dest.writeParcelable(Image, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Payment> CREATOR = new Creator<Payment>() {
            @Override
            public Payment createFromParcel(Parcel in) {
                return new Payment(in);
            }

            @Override
            public Payment[] newArray(int size) {
                return new Payment[size];
            }
        };

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getPaymentTime() {
            return PaymentTime;
        }

        public void setPaymentTime(String paymentTime) {
            PaymentTime = paymentTime;
            notifyPropertyChanged(BR.paymentTime);
        }

        @Bindable
        public String getPaymentType() {
            return PaymentType;
        }

        public void setPaymentType(String paymentType) {
            PaymentType = paymentType;
            notifyPropertyChanged(BR.paymentType);
        }

        @Bindable
        public String getPaymentMethod() {
            return PaymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            PaymentMethod = paymentMethod;
            notifyPropertyChanged(BR.paymentMethod);
        }

        @Bindable
        public Double getOriginalAmt() {
            return OriginalAmt;
        }

        public void setOriginalAmt(Double originalAmt) {
            OriginalAmt = originalAmt;
            notifyPropertyChanged(BR.originalAmt);
        }

        @Bindable
        public Double getPaymentAmt() {
            return PaymentAmt;
        }

        public void setPaymentAmt(Double paymentAmt) {
            PaymentAmt = paymentAmt;
            notifyPropertyChanged(BR.paymentAmt);
        }

        @Bindable
        public Double getCashChanges() {
            return CashChanges;
        }

        public void setCashChanges(Double cashChanges) {
            CashChanges = cashChanges;
            notifyPropertyChanged(BR.cashChanges);
        }

        @Bindable
        public String getCCType() {
            return CCType;
        }

        public void setCCType(String CCType) {
            this.CCType = CCType;
            notifyPropertyChanged(BR.cCType);
        }

        @Bindable
        public String getCCNo() {
            return CCNo;
        }

        public void setCCNo(String CCNo) {
            this.CCNo = CCNo;
            notifyPropertyChanged(BR.cCNo);
        }

        @Bindable
        public String getCCExpiryDate() {
            return CCExpiryDate;
        }

        public void setCCExpiryDate(String CCExpiryDate) {
            this.CCExpiryDate = CCExpiryDate;
            notifyPropertyChanged(BR.cCExpiryDate);
        }

        @Bindable
        public String getCCApprovalCode() {
            return CCApprovalCode;
        }

        public void setCCApprovalCode(String CCApprovalCode) {
            this.CCApprovalCode = CCApprovalCode;
            notifyPropertyChanged(BR.cCApprovalCode);
        }

        @Bindable
        public String getChequeNo() {
            return ChequeNo;
        }

        public void setChequeNo(String chequeNo) {
            ChequeNo = chequeNo;
            notifyPropertyChanged(BR.chequeNo);
        }

        @Bindable
        public Bitmap getImage() {
            return Image;
        }

        public void setImage(Bitmap image) {
            Image = image;
            notifyPropertyChanged(BR.image);
        }


//        @Bindable
//        public Float getChequeAmt() {
//            return ChequeAmt;
//        }
//
//        public void setChequeAmt(Float chequeAmt) {
//            ChequeAmt = chequeAmt;
//            notifyPropertyChanged(BR.chequeAmt);
//        }
//
//        @Bindable
//        public String getPaymentStatus() {
//            return PaymentStatus;
//        }
//
//        public void setPaymentStatus(String paymentStatus) {
//            PaymentStatus = paymentStatus;
//            notifyPropertyChanged(BR.paymentStatus);
//        }
    }
    public static class TopDebtors extends BaseObservable implements Parcelable {

        public String DebtorName;
        public String DocNo;
        public String TotalIn;

        protected TopDebtors(Parcel in) {
            DebtorName = in.readString();
            DocNo = in.readString();
            TotalIn = in.readString();
        }

        public static final Creator<TopDebtors> CREATOR = new Creator<TopDebtors>() {
            @Override
            public TopDebtors createFromParcel(Parcel in) {
                return new TopDebtors(in);
            }

            @Override
            public TopDebtors[] newArray(int size) {
                return new TopDebtors[size];
            }
        };

        public TopDebtors(String debtorName, String docNo,String totalin) {
            this.setDebtorName(debtorName);
            this.setDocNo(docNo);
            this.setTotalIn(totalin);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DebtorName);
            dest.writeString(DocNo);
            dest.writeString(TotalIn);
        }

        public String getDebtorName() {
            return DebtorName;
        }

        public void setDebtorName(String debtorName) {
            DebtorName = debtorName;
        }

        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
        }

        public String getTotalIn() {
            return TotalIn;
        }

        public void setTotalIn(String totalIn) {
            TotalIn = totalIn;
        }
    }

    public static class PurchaseMenu extends BaseObservable implements Parcelable {
        public String DocType;
        public String DocDate;
        public String DocNo;
        public String CreditorCode;
        public String CreditorName;
        public String Agent;
        public Double TotalEx;
        public Double TotalTax;
        public Double TotalIn;
        public int Uploaded;
        public String Remarks;
        public String Remarks2;
        public String Remarks3;
        public String Remarks4;

        public PurchaseMenu() {
        }

        public PurchaseMenu(String docDate, String docNo, String creditorCode, String creditorName, String agent, Double totalEx, Double totalTax, Double totalIn, int uploaded, String remarks , String remarks2, String remarks3, String remarks4) {
            DocDate = docDate;
            DocNo = docNo;
            CreditorCode = creditorCode;
            CreditorName = creditorName;
            Agent = agent;
            TotalEx = totalEx;
            TotalTax = totalTax;
            TotalIn = totalIn;
            Uploaded = uploaded;
            Remarks = remarks;
            Remarks2 = remarks2;
            Remarks3 = remarks3;
            Remarks4 = remarks4;
        }

        @Bindable
        public String getDocType() {
            return DocType;
        }

        public void setDocType(String docType) {
            DocType = docType;
            notifyPropertyChanged(BR.docType);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getCreditorCode() {
            return CreditorCode;
        }

        public void setCreditorCode(String creditorCode) {
            CreditorCode = creditorCode;
            notifyPropertyChanged(BR.creditorCode);
        }

        @Bindable
        public String getCreditorName() {
            return CreditorName;
        }

        public void setCreditorName(String creditorName) {
            CreditorName = creditorName;
            notifyPropertyChanged(BR.creditorName);
        }

        @Bindable
        public String getAgent() {
            return Agent;
        }

        public void setAgent(String agent) {
            Agent = agent;
            notifyPropertyChanged(BR.agent);
        }

        @Bindable
        public Double getTotalEx() {
            return TotalEx;
        }

        public void setTotalEx(Double totalEx) {
            TotalEx = totalEx;
            notifyPropertyChanged(BR.total_Ex);
        }

        @Bindable
        public Double getTotalTax() {
            return TotalTax;
        }

        public void setTotalTax(Double totalTax) {
            TotalTax = totalTax;
            notifyPropertyChanged(BR.taxValue);
        }

        @Bindable
        public Double getTotalIn() {
            return TotalIn;
        }

        public void setTotalIn(Double totalIn) {
            TotalIn = totalIn;
            notifyPropertyChanged(BR.total_In);
        }

        @Bindable
        public int getUploaded() {
            return Uploaded;
        }

        public void setUploaded(int uploaded) {
            Uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public String getRemarks() {
            return Remarks;
        }

        public void setRemarks(String remarks) {
            Remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getRemarks2() { return Remarks2; }

        public void setRemarks2(String remarks2FP) {
            Remarks2 = remarks2FP;
            notifyPropertyChanged(BR.remarks2);
        }

        @Bindable
        public String getRemarks3() { return Remarks3; }

        public void setRemarks3(String remarks3FP) {
            Remarks3 = remarks3FP;
            notifyPropertyChanged(BR.remarks3);
        }

        @Bindable
        public String getRemarks4() { return Remarks4; }

        public void setRemarks4(String remarks4FP) {
            Remarks4 = remarks4FP;
            notifyPropertyChanged(BR.remarks4);
        }

        protected PurchaseMenu(Parcel in) {
            DocDate = in.readString();
            DocNo = in.readString();
            CreditorCode = in.readString();
            CreditorName = in.readString();
            Agent = in.readString();
            if (in.readByte() == 0) {
                TotalEx = null;
            } else {
                TotalEx = in.readDouble();
            }
            if (in.readByte() == 0) {
                TotalTax = null;
            } else {
                TotalTax = in.readDouble();
            }
            if (in.readByte() == 0) {
                TotalIn = null;
            } else {
                TotalIn = in.readDouble();
            }
            Uploaded = in.readInt();
            Remarks = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocDate);
            dest.writeString(DocNo);
            dest.writeString(CreditorCode);
            dest.writeString(CreditorName);
            dest.writeString(Agent);
            if (TotalEx == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TotalEx);
            }
            if (TotalTax == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TotalTax);
            }
            if (TotalIn == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TotalIn);
            }
            dest.writeInt(Uploaded);
            dest.writeString(Remarks);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<PurchaseMenu> CREATOR = new Creator<PurchaseMenu>() {
            @Override
            public PurchaseMenu createFromParcel(Parcel in) {
                return new PurchaseMenu(in);
            }

            @Override
            public PurchaseMenu[] newArray(int size) {
                return new PurchaseMenu[size];
            }
        };
    }

    public static class PurchaseDetails extends BaseObservable implements Parcelable {
        public Integer ID;
        public String DocNo;
        public String DocDate;
        public String Location;
        public String ItemCode;
        public String ItemCodeInput;
        public String ItemDescription;
        public String UOM;
        public Double Quantity;
        public Double UPrice;
        public Double Discount;
        public Double SubTotal;
        public String TaxType;
        public Double TaxRate;
        public Double TaxValue;
        public Double Total_Ex;
        public Double Total_In;
        public String Line_No;
        public String Batch_No;
        public String Remarks;
        public String Remarks2;

        public PurchaseDetails() {
            Quantity = Double.valueOf(1);
            Discount = Double.valueOf(0);
            SubTotal = Double.valueOf(0);
            UPrice = Double.valueOf(0);
            TaxRate = Double.valueOf(0);
            TaxValue = Double.valueOf(0);
            Total_In = Double.valueOf(0);
            Total_Ex = Double.valueOf(0);
            setItemCode(null);
            setQuantity(Double.valueOf(1));
            setDiscount(Double.valueOf(0));

        }

        public PurchaseDetails(Integer id, String docNo, String location, String itemCode, String itemDescription, String UOM, Double quantity, Double UPrice, Double discount, Double subTotal, String taxType, Double taxRate, Double taxValue, Double total_Ex, Double total_In, String line_no, String batch_no, String remarks, String remarks2) {
            ID = id;
            DocNo = docNo;
            Location = location;
            ItemCode = itemCode;
            ItemDescription = itemDescription;
            this.UOM = UOM;
            Quantity = quantity;
            this.UPrice = UPrice;
            Discount = discount;
            SubTotal = subTotal;
            TaxType = taxType;
            TaxRate = taxRate;
            TaxValue = taxValue;
            Total_Ex = total_Ex;
            Total_In = total_In;
            Line_No = line_no;
            Batch_No = batch_no;
            Remarks = remarks;
            Remarks2 = remarks2;
        }

        protected PurchaseDetails(Parcel in) {
            if (in.readByte() == 0) {
                ID = null;
            } else {
                ID = in.readInt();
            }
            DocNo = in.readString();
            Location = in.readString();
            ItemCode = in.readString();
            ItemDescription = in.readString();
            if (in.readByte() == 0) {
                Quantity = null;
            } else {
                Quantity = in.readDouble();
            }
            if (in.readByte() == 0) {
                UOM = null;
            } else {
                UOM = in.readString();
            }
            if (in.readByte() == 0) {
                UPrice = null;
            } else {
                UPrice = in.readDouble();
            }
            if (in.readByte() == 0) {
                Discount = null;
            } else {
                Discount = in.readDouble();
            }
            if (in.readByte() == 0) {
                SubTotal = null;
            } else {
                SubTotal = in.readDouble();
            }
            TaxType = in.readString();
            if (in.readByte() == 0) {
                TaxRate = null;
            } else {
                TaxRate = in.readDouble();
            }
            if (in.readByte() == 0) {
                TaxValue = null;
            } else {
                TaxValue = in.readDouble();
            }
            if (in.readByte() == 0) {
                Total_Ex = null;
            } else {
                Total_Ex = in.readDouble();
            }
            if (in.readByte() == 0) {
                Total_In = null;
            } else {
                Total_In = in.readDouble();
            }
            Line_No = in.readString();
            Batch_No = in.readString();
            Remarks = in.readString();
            Remarks2 = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (ID == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(ID);
            }
            dest.writeString(DocNo);
            dest.writeString(Location);
            dest.writeString(ItemCode);
            dest.writeString(ItemDescription);

            if (Quantity == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Quantity);
            }
            if (UOM == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(UOM);
            }
            if (UPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(UPrice);
            }
            if (Discount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Discount);
            }
            if (SubTotal == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(SubTotal);
            }
            dest.writeString(TaxType);
            if (TaxRate == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TaxRate);
            }
            if (TaxValue == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TaxValue);
            }
            if (Total_Ex == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Total_Ex);
            }
            if (Total_In == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Total_In);
            }
            dest.writeString(Line_No);
            dest.writeString(Batch_No);
            dest.writeString(Remarks);
            dest.writeString(Remarks2);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<PurchaseDetails> CREATOR = new Creator<PurchaseDetails>() {
            @Override
            public PurchaseDetails createFromParcel(Parcel in) {
                return new PurchaseDetails(in);
            }

            @Override
            public PurchaseDetails[] newArray(int size) {
                return new PurchaseDetails[size];
            }
        };

        @Bindable
        public Integer getID() {
            return ID;
        }

        public void setID(Integer ID) {
            this.ID = ID;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {

            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getItemCodeInput() {
            return ItemCodeInput;
        }

        public void setItemCodeInput(String itemCodeInput) {
            ItemCodeInput = itemCodeInput;
            notifyPropertyChanged(BR.itemCodeInput);
        }


        @Bindable
        public String getItemDescription() {
            return ItemDescription;
        }

        public void setItemDescription(String itemDescription) {
            ItemDescription = itemDescription;
            notifyPropertyChanged(BR.itemDescription);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Double getQuantity() {
            return Quantity;
        }

        public void setQuantity(Double quantity) {
            Quantity = quantity;
            notifyPropertyChanged(BR.quantity);
        }

        @Bindable
        public Double getUPrice() {
            return UPrice;
        }

        public void setUPrice(Double UPrice) {
            this.UPrice = UPrice;
            notifyPropertyChanged(BR.uPrice);
        }

        @Bindable
        public Double getDiscount() {
            return Discount;
        }

        public void setDiscount(Double discount) {
            Discount = discount;
            notifyPropertyChanged(BR.discount);
        }

        @Bindable
        public Double getSubTotal() {
            return SubTotal;
        }

        public void setSubTotal(Double subTotal) {
            SubTotal = subTotal;
            notifyPropertyChanged(BR.subTotal);
        }

        @Bindable
        public String getTaxType() {
            return TaxType;
        }

        public void setTaxType(String taxType) {
            TaxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public Double getTaxRate() {
            return TaxRate;
        }

        public void setTaxRate(Double taxRate) {
            TaxRate = taxRate;
            notifyPropertyChanged(BR.taxRate);
        }

        @Bindable
        public Double getTaxValue() {
            return TaxValue;
        }

        public void setTaxValue(Double taxValue) {
            TaxValue = taxValue;
            notifyPropertyChanged(BR.taxValue);
        }

        @Bindable
        public Double getTotal_Ex() {
            return Total_Ex;
        }

        public void setTotal_Ex(Double total_Ex) {
            Total_Ex = total_Ex;
            notifyPropertyChanged(BR.total_Ex);
        }

        @Bindable
        public Double getTotal_In() {
            return Total_In;
        }

        public void setTotal_In(Double total_In) {
            Total_In = total_In;
            notifyPropertyChanged(BR.total_In);
        }

        @Bindable
        public String getLine_No() {
            return Line_No;
        }

        public void setLine_No(String line_No) {
            this.Line_No = line_No;
            notifyPropertyChanged(BR.line_No);
        }

        @Bindable
        public String getBatch_No() {
            return Batch_No;
        }

        public void setBatch_No(String batch_No) {
            this.Batch_No = batch_No;
            notifyPropertyChanged(BR.batch_No);
        }

        @Bindable
        public String getRemarks() {
            return Remarks;
        }

        public void setRemarks(String remarks) {
            this.Remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getRemarks2() { return Remarks2;
        }

        public void setRemarks2(String remarks2) {
            this.Remarks2 = remarks2;
            notifyPropertyChanged(BR.remarks2);
        }
    }

    public static class Purchase extends BaseObservable implements Parcelable {
        private String docNo;
        private String createdTimeStamp;
        private String docDate;
        private String creditorCode;
        private String creditorName;
        private String agent;
        private String taxType;
        private String docType;
        private Integer uploaded;
        private String signature;
        private String phone;
        private String fax;
        private String attention;
        private String address1;
        private String address2;
        private String address3;
        private String address4;
        private String remarks;
        private String Remarks2;
        private String Remarks3;
        private String Remarks4;
        private String Createduser;
        private String LastModifiedDateTime;
        private String LastModifiedUser;

        private List<AC_Class.PurchaseDetails> purchaseDetailsList;

        public Purchase() {
            docNo = null;
            createdTimeStamp = null;
            docDate = null;
            creditorCode = null;
            creditorName = null;
            agent = null;
            taxType = null;
            docType = null;
            uploaded = 0;
            signature = null;
            phone = null;
            fax = null;
            attention = null;
            purchaseDetailsList = new ArrayList<>();
            remarks = null;
            Remarks2 = null;
            Remarks3 = null;
            Remarks4 = null;
        }

        public Purchase(String docNo, String createdTimeStamp, String docDate, String creditorCode,
                       String creditorName, String agent, String taxType, String phone, String fax,
                        String attention, String remarks, String remarks2, String remarks3,
                        String remarks4, String createduser) {
            this.docNo = docNo;
            this.createdTimeStamp = createdTimeStamp;
            this.docDate = docDate;
            this.creditorCode = creditorCode;
            this.creditorName = creditorName;
            this.agent = agent;
            this.taxType = taxType;
            this.uploaded = 0;
            this.phone = phone;
            this.fax = fax;
            this.attention = attention;
            purchaseDetailsList = new ArrayList<>();
            this.remarks = remarks;
            this.Remarks2 = remarks2;
            this.Remarks3 = remarks3;
            this.Remarks4 = remarks4;
            this.Createduser = createduser;
        }

        protected Purchase(Parcel in) {
            docNo = in.readString();
            createdTimeStamp = in.readString();
            docDate = in.readString();
            creditorCode = in.readString();
            creditorName = in.readString();
            agent = in.readString();
            taxType = in.readString();
            docType = in.readString();
            uploaded = in.readInt();
            signature = in.readString();
            phone = in.readString();
            fax = in.readString();
            attention = in.readString();
            address1 = in.readString();
            address2 = in.readString();
            address3 = in.readString();
            address4 = in.readString();

            purchaseDetailsList = new ArrayList<>();
            in.readTypedList(purchaseDetailsList, PurchaseDetails.CREATOR);

            remarks = in.readString();
            Remarks2 = in.readString();
            Remarks3 = in.readString();
            Remarks4 = in.readString();
            Createduser = in.readString();
            LastModifiedUser = in.readString();
            LastModifiedDateTime = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(docNo);
            dest.writeString(createdTimeStamp);
            dest.writeString(docDate);
            dest.writeString(creditorCode);
            dest.writeString(creditorName);
            dest.writeString(agent);
            dest.writeString(taxType);
            dest.writeString(docType);
            dest.writeInt(uploaded);
            dest.writeString(signature);
            dest.writeString(phone);
            dest.writeString(fax);
            dest.writeString(attention);
            dest.writeString(address1);
            dest.writeString(address2);
            dest.writeString(address3);
            dest.writeString(address4);

            dest.writeTypedList((List<PurchaseDetails>) purchaseDetailsList);
            dest.writeString(remarks);
            dest.writeString(Remarks2);
            dest.writeString(Remarks3);
            dest.writeString(Remarks4);
            dest.writeString(Createduser);
            dest.writeString(LastModifiedUser);
            dest.writeString(LastModifiedDateTime);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Purchase> CREATOR = new Creator<Purchase>() {
            @Override
            public Purchase createFromParcel(Parcel in) {
                return new Purchase(in);
            }

            @Override
            public Purchase[] newArray(int size) {
                return new Purchase[size];
            }
        };

        @Bindable
        public String getDocNo() {
            return docNo;
        }

        public void setDocNo(String docNo) {
            this.docNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getCreatedTimeStamp() {
            return createdTimeStamp;
        }

        public void setCreatedTimeStamp(String createdTimeStamp) {
            this.createdTimeStamp = createdTimeStamp;
            notifyPropertyChanged(BR.createdTimeStamp);
        }

        @Bindable
        public String getDocDate() {
            return docDate;
        }

        public void setDocDate(String docDate) {
            this.docDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getCreditorCode() {
            return creditorCode;
        }

        public void setCreditorCode(String creditorCodeFP) {
            this.creditorCode = creditorCodeFP;
            notifyPropertyChanged(BR.creditorCode);
        }

        @Bindable
        public String getCreditorName() {
            return creditorName;
        }

        public void setCreditorName(String creditorNameFP) {
            this.creditorName = creditorNameFP;
            notifyPropertyChanged(BR.creditorName);
        }

        @Bindable
        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
            notifyPropertyChanged(BR.agent);
        }

        @Bindable
        public String getTaxType() {
            return taxType;
        }

        public void setTaxType(String taxType) {
            this.taxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public String getDocType() {
            return docType;
        }

        public void setDocType(String docType) {
            this.docType = docType;
            notifyPropertyChanged(BR.docType);
        }

        @Bindable
        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
            notifyPropertyChanged(BR.signature);
        }

        @Bindable
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
            notifyPropertyChanged(BR.phone);
        }

        @Bindable
        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
            notifyPropertyChanged(BR.fax);
        }

        @Bindable
        public String getAttention() {
            return attention;
        }

        public void setAttention(String attention) {
            this.attention = attention;
            notifyPropertyChanged(BR.attention);
        }

        @Bindable
        public Integer getUploaded() {
            return uploaded;
        }

        public void setUploaded(Integer uploaded) {
            this.uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1FP) {
            address1 = address1FP;
            notifyPropertyChanged(BR.address1);
        }

        @Bindable
        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2FP) {
            address2 = address2FP;
            notifyPropertyChanged(BR.address2);
        }

        @Bindable
        public String getAddress3() {
            return address3;
        }

        public void setAddress3(String address3FP) {
            address3 = address3FP;
            notifyPropertyChanged(BR.address3);
        }

        @Bindable
        public String getAddress4() { return address4; }

        public void setAddress4(String address4FP) {
            address4 = address4FP;
            notifyPropertyChanged(BR.address4);
        }

        @Bindable
        public String getRemarks() {return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getRemarks2() { return Remarks2; }

        public void setRemarks2(String remarks2FP) {
            Remarks2 = remarks2FP;
            notifyPropertyChanged(BR.remarks2);
        }

        @Bindable
        public String getRemarks3() { return Remarks3; }

        public void setRemarks3(String remarks3FP) {
            Remarks3 = remarks3FP;
            notifyPropertyChanged(BR.remarks3);
        }

        @Bindable
        public String getRemarks4() { return Remarks4; }

        public void setRemarks4(String remarks4FP) {
            Remarks4 = remarks4FP;
            notifyPropertyChanged(BR.remarks4);
        }

        @Bindable
        public String getCreateduser() {
            return Createduser;
        }

        public void setCreateduser(String createduser) {
            this.Createduser = createduser;
            notifyPropertyChanged(BR.createdUser);
        }

        @Bindable
        public String getLastModifiedUser() {
            return LastModifiedUser;
        }

        public void setLastModifiedUser(String lastModifiedUser) {
            this.LastModifiedUser = lastModifiedUser;
            notifyPropertyChanged(BR.lastModifiedUser);
        }

        @Bindable
        public String getLastModifiedDateTime() {
            return LastModifiedDateTime;
        }

        public void setLastModifiedDateTime(String lastModifiedDateTime) {
            this.LastModifiedDateTime = lastModifiedDateTime;
            notifyPropertyChanged(BR.lastModifiedDateTime);
        }

        @Bindable
        public List<AC_Class.PurchaseDetails> getPurchaseDetailsList() { return purchaseDetailsList; }

        public void setPurchaseDetailsList(List<PurchaseDetails> purchaseDetailsListFP) {
            purchaseDetailsList = purchaseDetailsListFP;
        }

        public void addPurchaseDetail(PurchaseDetails purchaseDetails) {
            purchaseDetailsList.add(purchaseDetails);
        }

        public void removePurchaseDetail(int index) {
            purchaseDetailsList.remove(index);
        }

        public AC_Class.PurchaseDetails getPurchaseDetail(int id) {
            int i;
            for (i=0; i<purchaseDetailsList.size(); i++) {
                if (purchaseDetailsList.get(i).getID()==id) {
                    return purchaseDetailsList.get(i);
                }
            }
            return null;
        }
    }

    public static class StockTakeMenu extends BaseObservable implements Parcelable {
        public String DocNo;
        public String DocDate;
        public String Agent;
        public String Location;
        public String Remarks;
        public String TotalQty;
        public int Uploaded;

        public StockTakeMenu() {
        }

        public StockTakeMenu(String docNo,String docDate, String agent, String location, String totalqty, String remarks, int uploaded) {
            DocDate = docDate;
            DocNo = docNo;
            Agent = agent;
            Location = location;
            Remarks = remarks;
            TotalQty = totalqty;
            Uploaded = uploaded;
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getAgent() {
            return Agent;
        }

        public void setAgent(String agent) {
            Agent = agent;
            notifyPropertyChanged(BR.agent);
        }

        @Bindable
        public String getRemarks() {
            return Remarks;
        }

        public void setRemarks(String remarks){
            Remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getTotalQty() {
            return TotalQty;
        }

        public void setTotalQty(String totalQty){
            TotalQty = totalQty;
            notifyPropertyChanged(BR.totalQty);
        }


        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location){
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public int getUploaded() {
            return Uploaded;
        }

        public void setUploaded(int uploaded){
            Uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        protected StockTakeMenu(Parcel in) {
            DocDate = in.readString();
            DocNo = in.readString();
            Agent = in.readString();
            Location= in.readString();
            Remarks = in.readString();
            Uploaded = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocDate);
            dest.writeString(DocNo);
            dest.writeString(Agent);
            dest.writeString(Location);
            dest.writeString(Remarks);
            dest.writeInt(Uploaded);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StockTakeMenu> CREATOR = new Creator<StockTakeMenu>() {
            @Override
            public StockTakeMenu createFromParcel(Parcel in) {
                return new StockTakeMenu(in);
            }

            @Override
            public StockTakeMenu[] newArray(int size) {
                return new StockTakeMenu[size];
            }
        };
    }

    public static class RFIDDtlList extends BaseObservable implements Parcelable{
        private int Qty;
        private List<AC_Class.RFIDItemTag> rfidDetailsList;

        public RFIDDtlList() {
            Qty = 0;
            rfidDetailsList = new ArrayList<>();
        }

        public RFIDDtlList(int qty) {
            this.Qty = qty;
            rfidDetailsList  = new ArrayList<>();
        }

        protected RFIDDtlList(Parcel in) {
            Qty= in.readInt();
            rfidDetailsList = new ArrayList<>();
            in.readTypedList(rfidDetailsList, RFIDItemTag.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(Qty);
            dest.writeTypedList((List<RFIDItemTag>) rfidDetailsList);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<RFIDDtlList> CREATOR = new Creator<RFIDDtlList>() {
            @Override
            public RFIDDtlList createFromParcel(Parcel in) {
                return new  RFIDDtlList(in);
            }

            @Override
            public  RFIDDtlList[] newArray(int size) {
                return new RFIDDtlList[size];
            }
        };

        @Bindable
        public int getQuantity() {
            return Qty;
        }

        public void setQuantity(int qty) {
            this.Qty = qty+1;
            notifyPropertyChanged(BR.qty);
        }

        @Bindable
        public List<RFIDItemTag> getRFIDDtlList() { return rfidDetailsList; }

        public void setStockTakeDtlList(List<RFIDItemTag> RFIDDetailsFP) {
            this.rfidDetailsList = RFIDDetailsFP;
            notifyPropertyChanged(BR.rFIDDtlList);
        }

        public void removeRFIDDetail(int index) {
            rfidDetailsList.remove(index);
        }
    }

    public static class RFIDItemTag extends BaseObservable implements Parcelable{
        private String itemCode;
        private String TID;
        private Integer Qty;

        public RFIDItemTag()
        {
            itemCode=null;
            TID=null;
            Qty=null;
        }

        public RFIDItemTag(String itemCode, String TID, Integer Qty) {
            this.itemCode = itemCode;
            this.TID = TID;
            this.Qty = Qty;
        }

        protected RFIDItemTag(Parcel in) {
            itemCode = in.readString();
            TID = in.readString();
            Qty = in.readInt();
        }

        public static final Creator<RFIDItemTag> CREATOR = new Creator<RFIDItemTag>() {
            @Override
            public RFIDItemTag createFromParcel(Parcel in) {
                return new RFIDItemTag(in);
            }

            @Override
            public RFIDItemTag[] newArray(int size) {
                return new RFIDItemTag[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {
            dest.writeString(itemCode);
            dest.writeString(TID);
            dest.writeInt(Qty);
        }

        @Bindable
        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String ic) {
            this.itemCode = ic;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getTID() {
            return TID;
        }

        public void setTID(String tid) {
            this.TID = tid;
            notifyPropertyChanged(BR.tID);
        }

        @Bindable
        public Integer getQuantity() {
            return Qty;
        }

        public void setQuantity(int qty) {
            this.Qty = qty;
            notifyPropertyChanged(BR.qty);
        }


    }

    public static class RFIDItemStatus extends BaseObservable implements Parcelable{
        public String ItemCode;
        public Integer Qty;
        public String Status;

        public RFIDItemStatus(String itemCodeFP, Integer qtyFP, String statusFP) {
            this.ItemCode = itemCodeFP;
            this.Qty = qtyFP;
            this.Status = statusFP;
        }

        protected RFIDItemStatus(Parcel in) {
            ItemCode = in.readString();
            if (in.readByte() == 0) {
                Qty = null;
            } else {
                Qty = in.readInt();
            }
            Status = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ItemCode);
            if (Qty == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(Qty);
            }
            dest.writeString(Status);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<RFIDItemStatus> CREATOR = new Creator<RFIDItemStatus>() {
            @Override
            public RFIDItemStatus createFromParcel(Parcel in) {
                return new RFIDItemStatus(in);
            }

            @Override
            public RFIDItemStatus[] newArray(int size) {
                return new RFIDItemStatus[size];
            }
        };
    }

    public static class StockTake extends BaseObservable implements Parcelable {
        private String docNo;
        private String createdTimeStamp;
        private String docDate;
        private String location;
        private String agent;
        private Integer uploaded;
        private String remarks;
        private String Createduser;
        private String LastModifiedDateTime;
        private String LastModifiedUser;
        private List<AC_Class.StockTakeDetails> stockTakeDetailsList;

        public StockTake() {
            docNo = null;
            createdTimeStamp = null;
            docDate = null;
            location = null;
            agent = null;
            uploaded = 0;
            remarks = null;
            stockTakeDetailsList = new ArrayList<>();
        }

        public StockTake(String docNo, String docDate, String remarks, int uploaded) {
            this.docNo = docNo;
            this.docDate = docDate;
            this.remarks = remarks;
            this.uploaded = uploaded;

        }

        public StockTake(String docNo, String createdTimeStamp, String docDate,String location, String agent,String remarks, String createduser) {
            this.docNo = docNo;
            this.createdTimeStamp = createdTimeStamp;
            this.docDate = docDate;
            this.location = location;
            this.agent = agent;
            this.uploaded = 0;
            this.remarks = remarks;
            this.Createduser = createduser;
            stockTakeDetailsList = new ArrayList<>();
        }

        protected StockTake(Parcel in) {
            docNo = in.readString();
            createdTimeStamp = in.readString();
            docDate = in.readString();
            location = in.readString();
            agent = in.readString();
            uploaded = in.readInt();
            remarks = in.readString();
            Createduser = in.readString();
            LastModifiedUser = in.readString();
            LastModifiedDateTime = in.readString();
            stockTakeDetailsList = new ArrayList<>();
            in.readTypedList(stockTakeDetailsList, StockTakeDetails.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(docNo);
            dest.writeString(createdTimeStamp);
            dest.writeString(docDate);
            dest.writeString(location);
            dest.writeString(agent);
            dest.writeInt(uploaded);
            dest.writeString(remarks);
            dest.writeString(Createduser);
            dest.writeString(LastModifiedUser);
            dest.writeString(LastModifiedDateTime);
            dest.writeTypedList((List<StockTakeDetails>) stockTakeDetailsList);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StockTake> CREATOR = new Creator<StockTake>() {
            @Override
            public StockTake createFromParcel(Parcel in) {
                return new  StockTake(in);
            }

            @Override
            public  StockTake[] newArray(int size) {
                return new  StockTake[size];
            }
        };

        @Bindable
        public String getDocNo() {
            return docNo;
        }

        public void setDocNo(String docNo) {
            this.docNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getCreatedTimeStamp() {
            return createdTimeStamp;
        }

        public void setCreatedTimeStamp(String createdTimeStamp) {
            this.createdTimeStamp = createdTimeStamp;
        }

        @Bindable
        public String getDocDate() {
            return docDate;
        }

        public void setDocDate(String docDate) {
            this.docDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
            notifyPropertyChanged(BR.agent);
        }

        @Bindable
        public Integer getUploaded() {
            return uploaded;
        }

        public void setUploaded(Integer uploaded) {
            this.uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getCreateduser() {
            return Createduser;
        }

        public void setCreateduser(String createduser) {
            this.Createduser = createduser;
            notifyPropertyChanged(BR.createdUser);
        }


        @Bindable
        public String getLastModifiedUser() {
            return LastModifiedUser;
        }

        public void setLastModifiedUser(String lastModifiedUser) {
            this.LastModifiedUser = lastModifiedUser;
            notifyPropertyChanged(BR.lastModifiedUser);
        }

        @Bindable
        public String getLastModifiedDateTime() {
            return LastModifiedDateTime;
        }

        public void setLastModifiedDateTime(String lastModifiedDateTime) {
            this.LastModifiedDateTime = lastModifiedDateTime;
            notifyPropertyChanged(BR.lastModifiedDateTime);
        }

        @Bindable
        public List<StockTakeDetails> getStockTakeDtlList() { return stockTakeDetailsList; }

        public void setStockTakeDtlList(List<StockTakeDetails> StockTakeDetailsFP) {
            this.stockTakeDetailsList = StockTakeDetailsFP;
            notifyPropertyChanged(BR.stockTakeDtlList);
        }

        public void removeStockTakeDetail(int index) {
            stockTakeDetailsList.remove(index);
        }

        public int StockDetailListLength()
        {
            return stockTakeDetailsList.size();
        }

        public void StockDetailListLengthRemove(int newl, int length)
        {
            int i=0;
            while(i < newl)
            {
                stockTakeDetailsList.remove(length-(i+1));
                i++;
            }
        }
    }

    public static class StockTakeDetails extends BaseObservable implements Parcelable {
        public String ItemCode;
        public String ItemDescription;
        public String UOM;
        public Double Quantity;
        public String StockDocNo;
        public String CreatedTimeStamp;
        public String LineNo;
        public String BatchNo;
        private List<AC_Class.StockTakeDetails> stockTakeDetailsList;

        public StockTakeDetails() {
            Quantity = Double.valueOf(1);
            setItemCode(null);
            setQuantity(Double.valueOf(1));
        }

        public StockTakeDetails(String itemCode, String itemDescription, String UOM, Double quantity,String stockDocNo,String createdTimeStamp, String lineNo, String batchno) {
            ItemCode = itemCode;
            ItemDescription = itemDescription;
            this.UOM = UOM;
            Quantity = quantity;
            StockDocNo = stockDocNo;
            CreatedTimeStamp = createdTimeStamp;
            LineNo = lineNo;
            BatchNo = batchno;
        }

        protected StockTakeDetails(Parcel in) {
            if (in.readByte() == 0) {
                ItemCode = null;
            } else {
                ItemCode = in.readString();
            }
            ItemDescription = in.readString();
            if (in.readByte() == 0) {
                Quantity = null;
            } else {
                Quantity = in.readDouble();
            }
            if (in.readByte() == 0) {
                UOM = null;
            } else {
                UOM = in.readString();
            }
            if (in.readByte() == 0) {
                StockDocNo = null;
            } else {
                StockDocNo = in.readString();
            }
            if (in.readByte() == 0) {
                CreatedTimeStamp = null;
            } else {
                CreatedTimeStamp = in.readString();
            }
            if (in.readByte() == 0) {
                LineNo = null;
            } else {
               LineNo = in.readString();
            }
            if (in.readByte() == 0) {
                BatchNo = null;
            } else {
                BatchNo = in.readString();
            }
            stockTakeDetailsList = new ArrayList<>();
            in.readTypedList(stockTakeDetailsList, StockTakeDetails.CREATOR);
        }

        public StockTakeDetails(String docNo) {
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (ItemCode == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(ItemCode);
            }
            dest.writeString(ItemDescription);

            if (Quantity == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Quantity);
            }
            if (UOM == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(UOM);
            }
            if (StockDocNo == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(StockDocNo);
            }
            if (CreatedTimeStamp == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(CreatedTimeStamp);
            }
            if (LineNo == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(LineNo);
            }
            if (BatchNo == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(BatchNo);
            }
            dest.writeTypedList((List<StockTakeDetails>) stockTakeDetailsList);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StockTakeDetails> CREATOR = new Creator<StockTakeDetails>() {
            @Override
            public StockTakeDetails createFromParcel(Parcel in) {
                return new StockTakeDetails(in);
            }

            @Override
            public StockTakeDetails[] newArray(int size) {
                return new StockTakeDetails[size];
            }
        };

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getItemDescription() {
            return ItemDescription;
        }

        public void setItemDescription(String itemDescription) {
            ItemDescription = itemDescription;
            notifyPropertyChanged(BR.itemDescription);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Double getQuantity() {
            return Quantity;
        }

        public void setQuantity(Double quantity) {
            Quantity = quantity;
            notifyPropertyChanged(BR.quantity);
        }

        @Bindable
        public String getStockDocNo() {
            return StockDocNo;
        }

        public void setStockDocNo(String stockDocNo) {
            StockDocNo = stockDocNo;
            notifyPropertyChanged(BR.stockDocNo);
        }

        @Bindable
        public String getCreatedTimeStamp() {
            return CreatedTimeStamp;
        }

        public void setCreatedTimeStamp(String createdTimeStamp) {
            CreatedTimeStamp = createdTimeStamp;
        }

        @Bindable
        public String getLineNo() {
            return LineNo;
        }

        public void setLineNo(String lineno) {
            this.LineNo = lineno;
            notifyPropertyChanged(BR.lineNo);
        }

        @Bindable
        public String getBatchNo() {
            return BatchNo;
        }

        public void setBatchNo(String batchNo) {
            BatchNo = batchNo;
            notifyPropertyChanged(BR.batchNo);
        }

    }


    public static class TransferMenu extends BaseObservable implements Parcelable {
        public String DocDate;
        public String DocNo;
        public String Reason;
        public String LocationFrom;
        public String LocationTo;
        public Float TotalQty;
        public int Uploaded;

        public TransferMenu() {
        }

        public TransferMenu(String docNo, String docDate, String reason, String locationFrom, String locationTo, Float totalQty, int uploaded) {
            DocDate = docDate;
            DocNo = docNo;
            Reason = reason;
            LocationFrom = locationFrom;
            LocationTo = locationTo;
            TotalQty = totalQty;
            Uploaded = uploaded;
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getReason() {
            return Reason;
        }

        public void setReason(String reason) {
            Reason = reason;
            notifyPropertyChanged(BR.reason);
        }

        @Bindable
        public String getLocationFrom() {
            return LocationFrom;
        }

        public void setLocationFrom(String locationFrom) {
            LocationFrom = locationFrom;
            notifyPropertyChanged(BR.locationFrom);
        }

        @Bindable
        public String getLocationTo() {
            return LocationTo;
        }

        public void setLocationTo(String locationTo) {
            LocationTo = locationTo;
            notifyPropertyChanged(BR.locationTo);
        }

        @Bindable
        public Float getTotalQty() {
            return TotalQty;
        }

        public void setTotalQty(Float totalQty) {
            TotalQty = totalQty;
            notifyPropertyChanged(BR.totalQty);
        }

        @Bindable
        public int getUploaded() {
            return Uploaded;
        }

        public void setUploaded(int uploaded) {
            Uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        protected TransferMenu(Parcel in) {
            DocDate = in.readString();
            DocNo = in.readString();
            Reason = in.readString();
            LocationFrom = in.readString();
            LocationTo = in.readString();
            TotalQty = in.readFloat();
            Uploaded = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocDate);
            dest.writeString(DocNo);
            dest.writeString(Reason);
            dest.writeString(LocationFrom);
            dest.writeString(LocationTo);
            dest.writeFloat(TotalQty);
            dest.writeInt(Uploaded);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<TransferMenu> CREATOR = new Creator<TransferMenu>() {
            @Override
            public TransferMenu createFromParcel(Parcel in) {
                return new TransferMenu(in);
            }

            @Override
            public TransferMenu[] newArray(int size) {
                return new TransferMenu[size];
            }
        };
    }

    public static class PrintInvoice extends BaseObservable {
        ACDatabase db;
        public String DocNo;
        public String DocDate;
        public String InvoiceNo;
        public String DebtorCode;
        public String DebtorName;
        public String Agent;
        public String ItemCode;
        public String Description;
        public Float Quantity;
        public String Price;
        public Float Discount;
        public Float Total;
        public Float TotalEx;
        public Float TotalTax;
        public Float TotalIn;
        public String PaymentMethod;
        public Float PaymentAmt;
        public Float CashChanges;

        BluetoothAdapter bluetoothAdapter;
        public BluetoothSocket bluetoothSocket;
        BluetoothDevice bluetoothDevice;

        OutputStream outputStream;
        InputStream inputStream;
        Thread thread;

        byte[] readBuffer;
        int readBufferPositions;
        volatile boolean stopWorker;
        Invoice invoiceHeader = null;
        List<InvoiceDetails> invoiceDetailsList = null;
        List<Payment> paymentList = null;
        Float TotalAmt;

        Context mycontext;
        String companyHeader;
        Bitmap companyLogo;

        public PrintInvoice(Context contextFP, String docNo, String companyHeaderFP)
        {
            companyHeader = companyHeaderFP;
            mycontext = contextFP;

            db = new ACDatabase(contextFP);
            Cursor data = db.getInvoiceHeadertoUpdate(docNo);
            if (data.getCount() > 0)
            {
                data.moveToNext();
                invoiceHeader = new Invoice(data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(10), data.getString(data.getColumnIndex("TaxType")), data.getString(7), data.getString(data.getColumnIndex("Signature")), data.getString(data.getColumnIndex("Phone")), data.getString(data.getColumnIndex("Fax")),
                        data.getString(data.getColumnIndex("Attention")), data.getString(data.getColumnIndex("Address1")), data.getString(data.getColumnIndex("Address2")), data.getString(data.getColumnIndex("Address3")), data.getString(data.getColumnIndex("Address4")), data.getString(data.getColumnIndex("Remarks")), data.getString(data.getColumnIndex("Remarks2")),
                        data.getString(data.getColumnIndex("Remarks3")), data.getString(data.getColumnIndex("Remarks4")), data.getString(data.getColumnIndex("CreatedUser")),data.getString(data.getColumnIndex("DisplayTerm")),data.getString(data.getColumnIndex("DetailDiscount")));
            }
            Cursor data1 = db.getInvoiceDetailsPrint(docNo);
            if (data1.getCount() > 0) {
                invoiceDetailsList = new ArrayList<>();
                while (data1.moveToNext()) {
                     InvoiceDetails invoiceDetails = new InvoiceDetails(data1.getInt(0),
                            data1.getString(1), data1.getString(2),
                            data1.getString(3), data1.getString(4),
                            data1.getString(5), data1.getDouble(6),
                            data1.getDouble(7), data1.getDouble(8),
                            data1.getDouble(9), data1.getString(10),
                            data1.getDouble(11), data1.getDouble(12),
                            data1.getDouble(13), data1.getDouble(14),data1.getString(15),
                            data1.getString(data1.getColumnIndex("Remarks")),data1.getString(data1.getColumnIndex("BatchNo")),
                            data1.getString(data1.getColumnIndex("Remarks2")));
                    invoiceDetailsList.add(invoiceDetails);
                }
            }
            Cursor data2 = db.getInvoicePaymentsPrint(docNo);
            if (data2.getCount() > 0) {
                paymentList = new ArrayList<>();
                while (data2.moveToNext()) {
                    Payment payment = new Payment(data2.getString(1),
                            data2.getString(2), data2.getString(3),
                            data2.getString(4), data2.getDouble(5),
                            data2.getDouble(6), data2.getDouble(7),
                            data2.getString(8), data2.getString(9),
                            data2.getString(10), data2.getString(11),
                            data2.getString(12));
                    TotalAmt= data2.getFloat(5);
                    paymentList.add(payment);
                }
            }

        }

        public boolean FindBluetoothDevice() {
            boolean isFound = false;

            try {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
                    if (pairedDevice.size() > 0) {
                        //The Bluetooth printer name
                        for (BluetoothDevice pairedDev : pairedDevice) {
                            String printName = "";

                            Cursor printer = db.getReg("15");
                            if(printer.moveToFirst()){
                                printName = printer.getString(0);
                            }
                            //String printName = "PTP-III";
                            //String printName1 = "Feasycom";
                            //String printName2 = "BluetoothPrinter";

                            if (pairedDev.getName().equals(printName))
                            {
                                bluetoothDevice = pairedDev;
                                isFound = true;
                                break;
                            }
                        }
                    } else {
//                    Log.i("custDebug", "size == 0");
                        isFound = false;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                isFound = false;
            }
            return isFound;
        }

        //open Bluetooth Printer
        boolean openBluetoothPrinter() {
            try{
                //Standard uuid from string//
                UUID uuidString = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                //UUID uuidString = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidString);
                bluetoothSocket.connect();
                Thread.sleep(1000);
                outputStream = bluetoothSocket.getOutputStream();
                //inputStream = bluetoothSocket.getInputStream();

                //beginListenData();
                return true;
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
        }

//        void beginListenData()
//        {
//            // this is the ASCII code for a newline character
//            try{
//                final Handler handler = new Handler();
//                final byte delimiter = 10;
//                stopWorker = false;
//                readBufferPositions = 0;
//                readBuffer = new byte[1024];
//                thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (!Thread.currentThread().isInterrupted() && !stopWorker)
//                        {
//                            try
//                            {
//                                int byteAvailable = inputStream.available();
//                                if (byteAvailable>0)
//                                {
//                                    byte[] packetByte = new byte[byteAvailable];
//                                    inputStream.read(packetByte);
//
//                                    for (int i=0; i<byteAvailable; i++)
//                                    {
//                                        byte b = packetByte[i];
//                                        if (b == delimiter)
//                                        {
//                                            byte[] encodedByte = new byte[readBufferPositions];
//                                            System.arraycopy(
//                                                    readBuffer,0,
//                                                    encodedByte,0,
//                                                    encodedByte.length
//                                            );
//                                            final String data = new String(encodedByte,
//                                                    "US=ASCII");
//                                            readBufferPositions = 0;
//                                            handler.post(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                }
//                                            });
//                                        }
//                                        else
//                                        {
//                                            readBuffer[readBufferPositions++] = b;
//                                        }
//                                    }
//                                }
//                            }
//                            catch (Exception ex)
//                            {
//                                stopWorker = true;
//                            }
//                        }
//
//                    }
//                });
//
//                thread.start();
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }
//        }

        private void printCustom(String msg, int size, int align) {
            //Print config "mode"
            byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
            //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
            byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
            byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
            byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
            try {
                switch (size){
                    case 0:
                        outputStream.write(cc);
                        break;
                    case 1:
                        outputStream.write(bb);
                        break;
                    case 2:
                        outputStream.write(bb2);
                        break;
                    case 3:
                        outputStream.write(bb3);
                        break;
                }

                switch (align){
                    case 0:
                        //left align
                        outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                        break;
                    case 1:
                        //center align
                        outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                        break;
                    case 2:
                        //right align
                        outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                        break;
                }
                outputStream.write(msg.getBytes());
                outputStream.write(PrinterCommands.LF);
                //outputStream.write(cc);
                //printNewLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //Printing Text to Bluetooth Printer//
        void printData(String reportNameFP)
        {
            try {
                // Defaults to normal Sales Receipt, changes to Credit Invoice if applicable
                String currencyword = "(RM)";
                String title = "                 CASH SALES                \r\n";
                String divider = "---------------------------------------------\r\n";
                String headerdetails = String.format(Locale.getDefault(), "%-9s%-2s%-35s\r\n", "Ref No.", ":" , invoiceHeader.docNo);
                String headerdetails1 = String.format(Locale.getDefault(), "%-9s%-2s%-35s\r\n", "Date", ":" , invoiceHeader.docDate);
                String headerdetails2 = String.format(Locale.getDefault(), "%-9s%-2s%-35s\r\n", "Debtor", ":" , invoiceHeader.debtorCode);
                String headerdetails3 = String.format(Locale.getDefault(), "%-9s%-2s%-35s\r\n", "Name", ":" , (invoiceHeader.debtorName == null ? "" : invoiceHeader.debtorName));
                String headerdetails4 = String.format(Locale.getDefault(), "%-9s%-2s%-35s\r\n", "Address", ":" , (invoiceHeader.address1 == null ? "" : invoiceHeader.address1) + " " + (invoiceHeader.address2 == null ? "" : invoiceHeader.address2) + " " + (invoiceHeader.address3 == null ? "" : invoiceHeader.address3) + " " + (invoiceHeader.address4 == null ? "" : invoiceHeader.address4));
                String headerdetails5 = String.format(Locale.getDefault(), "%-9s%-2s%-35s\r\n", "Agent", ":" , (invoiceHeader.getAgent() == null ? "" : invoiceHeader.getAgent()));
                String headerdetails6 = String.format(Locale.getDefault(), "%-9s%-2s%-35s\r\n", "Attn", ":" , (invoiceHeader.getAttention() == null ? "" : invoiceHeader.getAttention()));
                String headerdetails7 = String.format(Locale.getDefault(), "%-9s%-2s%-35s\r\n", "Tel", ":" , (invoiceHeader.getPhone() == null ? "" : invoiceHeader.getPhone()));
                String detailheader2 = String.format("%-16s", "Item") + String.format("%-4s", "Qty") +
                        String.format("%-10s", " U.Price") + String.format("%-8s", " Disc.") +
                        String.format("%-8s", " SubTotal") + "\r\n";
                String detailheader3 = String.format("%28s", currencyword) +
                        String.format("%17s", currencyword) + "\r\n";

                if (reportNameFP.equals("Tax Invoice"))
                {
                    detailheader2 = String.format("%-10s", "Item") + String.format("%-4s", "Qty") + String.format("%-8s", " U.Price") + String.format("%-10s", " Disc.") + String.format("%-6s", " Tax") + String.format("%-8s", " SubTotal") + "\r\n";

                    detailheader3 = String.format("%22s", currencyword) +
                            String.format("%23s", currencyword) + "\r\n";
                }


                String details1 = "";
                float totalIn = 0.00f;
                float totalEx = 0.00f;
                float totalTax = 0.00f;
                for (int i = 0; i < invoiceDetailsList.size(); i++) {
                    if (reportNameFP.equals("Tax Invoice"))
                    {
                        details1 += String.format(Locale.getDefault(), "%-8s%5s%9s%6s%8s%10s\r\n", (invoiceDetailsList.get(i).ItemCode.length()>8 ? invoiceDetailsList.get(i).ItemCode.substring(0,7) : invoiceDetailsList.get(i).ItemCode), new DecimalFormat("#.###").format(invoiceDetailsList.get(i).Quantity), String.format("%.2f", invoiceDetailsList.get(i).UPrice), String.format("%.2f", invoiceDetailsList.get(i).Discount), String.format("%.2f", invoiceDetailsList.get(i).getTaxValue()), String.format("%.2f", invoiceDetailsList.get(i).Total_In)) + (invoiceDetailsList.get(i).ItemDescription == null ? "" : invoiceDetailsList.get(i).ItemDescription) + "\r\n\r\n";
                    }
                    else {
                        details1 += String.format(Locale.getDefault(), "%-14s%5s%9s%8s%10s\r\n", (invoiceDetailsList.get(i).ItemCode.length()>12 ? invoiceDetailsList.get(i).ItemCode.substring(0,12) : invoiceDetailsList.get(i).ItemCode), new DecimalFormat("#.###").format(invoiceDetailsList.get(i).Quantity), String.format("%.2f", invoiceDetailsList.get(i).UPrice), String.format("%.2f", invoiceDetailsList.get(i).Discount), String.format("%.2f", invoiceDetailsList.get(i).Total_In)) + (invoiceDetailsList.get(i).ItemDescription == null ? "" : invoiceDetailsList.get(i).ItemDescription) + "\r\n\r\n";
                    }
                    totalEx += invoiceDetailsList.get(i).getTotal_Ex();
                    totalIn += invoiceDetailsList.get(i).getTotal_In();
                    totalTax += invoiceDetailsList.get(i).getTaxValue();
                }
                String totalAmount="";

                if (reportNameFP.equals("Tax Invoice"))
                {
                    totalAmount += String.format(Locale.getDefault(), "%-18s%-2s%26s\n", "SubTotal " + currencyword, ":" , String.format("%.2f",totalEx));
                    totalAmount += String.format(Locale.getDefault(), "%-18s%-2s%26s\r\n", "Tax " + currencyword, ":" , String.format("%.2f",totalTax));
                }
                totalAmount += String.format(Locale.getDefault(), "%-18s%-2s%26s\r\n", "Final Total " + currencyword, ":" , String.format("%.2f",totalIn));

                String payments = "";
                String cashChanges = "";
                if (paymentList != null) {
                    Double cc = 0.00d;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < paymentList.size(); i++) {
                        sb.append(String.format(Locale.getDefault(), "%-18s%28s\r\n",
                                paymentList.get(i).PaymentMethod + ":", String.format("%.2f",paymentList.get(i).PaymentAmt)));
                        cc = paymentList.get(i).CashChanges;
                    }
                    payments = sb.toString();
                    cashChanges = String.format(Locale.getDefault(), "%-18s%28s\r\n",
                            "Cash Changes:", String.format("%.2f",cc));
                } else {
                    // Credit Sales
                    title = "                   INVOICE                 \r\n";
                }

                String footer1 = "Thank you.\r\n\n";

                outputStream = bluetoothSocket.getOutputStream();

                outputStream.write(Formatter.ESC_ALIGN_LEFT);
                outputStream.write(Formatter.bb);

                Cursor ch = db.getReg("35");
                if(ch.moveToFirst()){
                    byte[] bt =ch.getBlob(0);
                    companyLogo = BitmapFactory.decodeByteArray(bt, 0, bt.length);
                }

                if(companyLogo!=null){
                    Bitmap bitmapS = Bitmap.createScaledBitmap(companyLogo, 250, 250, false);
                    printLogo(bitmapS);
                }

                outputStream.write(companyHeader.getBytes());
                printNewLine();
                printNewLine();
                outputStream.write(Formatter.ESC_ALIGN_LEFT);
                outputStream.write(Formatter.bb3);
                outputStream.write(divider.getBytes());
                outputStream.write(Formatter.bb);
                outputStream.write(title.getBytes());
                outputStream.write(Formatter.bb3);
                outputStream.write(divider.getBytes());
                outputStream.write(Formatter.cc);
                outputStream.write(headerdetails.getBytes());
                outputStream.write(headerdetails1.getBytes());
                outputStream.write(headerdetails2.getBytes());
                outputStream.write(headerdetails3.getBytes());
                outputStream.write(headerdetails4.getBytes());
                outputStream.write(headerdetails6.getBytes());
                outputStream.write(headerdetails7.getBytes());
                outputStream.write(headerdetails5.getBytes());
                outputStream.write(Formatter.bb3);
                outputStream.write(divider.getBytes());
                outputStream.write(Formatter.cc);
                outputStream.write(detailheader2.getBytes());
                outputStream.write(detailheader3.getBytes());
                outputStream.write(Formatter.bb3);
                outputStream.write(divider.getBytes());
                outputStream.write(Formatter.cc);
                outputStream.write(details1.getBytes("BIG5"));
                outputStream.write(Formatter.bb3);
                outputStream.write(divider.getBytes());
                outputStream.write(Formatter.bb);
                outputStream.write(totalAmount.getBytes());
                printNewLine();
                printNewLine();
                outputStream.write(Formatter.cc);
                outputStream.write(payments.getBytes());
                outputStream.write(Formatter.bb);
                outputStream.write(cashChanges.getBytes());
                outputStream.write(Formatter.bb);
                outputStream.write(divider.getBytes());
                outputStream.write(Formatter.ESC_ALIGN_CENTER);
                //outputStream.write(footer1.getBytes());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (invoiceHeader.getSignature() != null)
                {
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(Base64.decode(invoiceHeader.getSignature(), Base64.DEFAULT), 0,
                            Base64.decode(invoiceHeader.getSignature(), Base64.DEFAULT).length);
                    Bitmap bitmapS = Bitmap.createScaledBitmap(decodedByte, 200, 200, false);

                    //outputStream.write(Base64.decode(invoiceHeader.getSignature(), Base64.DEFAULT));

                    printPhoto(bitmapS);
                    outputStream.write(Formatter.ESC_ALIGN_LEFT);
                    outputStream.write("                   --------------------------".getBytes());
                    outputStream.write("                   Authorised Signature".getBytes());
                }else{
                    printNewLine();
                    printNewLine();
                    printNewLine();
                    outputStream.write(Formatter.ESC_ALIGN_LEFT);
                    outputStream.write("                   --------------------------".getBytes());
                    outputStream.write("                   Authorised Signature".getBytes());
                }
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();

                resetPrint();
                outputStream.flush();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                Log.i("custDebug", ex.getMessage());
            }
        }
        //Disconnect Printer //
        void disconnectBT() throws IOException
        {
            try{
                //stopWorker = true;
                outputStream.close();
                //inputStream.close();
                bluetoothSocket.close();
                bluetoothSocket = null;            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        public void printPhoto(Bitmap bmp) {
            try {
                if(bmp!=null){
                    byte[] command = Utils.decodeBitmap(bmp);
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    printText(command);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }
        }

        public void printLogo(Bitmap bmp) {
            try {
                if(bmp!=null){
                    byte[] command = Utils.decodeBitmap(bmp);
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText(command);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }
        }

        public void printPhoto(int img) {
            try {
                Bitmap bmp = BitmapFactory.decodeResource(mycontext.getResources(),
                        img);
                if(bmp!=null){
                    byte[] command = Utils.decodeBitmap(bmp);
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    printText(command);
                }else{
                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }
        }

        private void printText(String msg) {
            try {
                // Print normal text
                outputStream.write(msg.getBytes());
                printNewLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void printText(byte[] msg) {
            try {
                // Print normal text
                outputStream.write(msg);
                printNewLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void printNewLine() {
            try {
                outputStream.write(PrinterCommands.FEED_LINE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void resetPrint() {
            try{
                outputStream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
                outputStream.write(PrinterCommands.FS_FONT_ALIGN);
                outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                outputStream.write(PrinterCommands.ESC_CANCEL_BOLD);
                outputStream.write(PrinterCommands.LF);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class UploadDownload extends BaseObservable {
        String tableName;
        int rowCount;
        String lastDate;
        String type;

        public UploadDownload() {
            tableName = "";
            rowCount = 0;
            lastDate = "";
            type = "";
        }

        public UploadDownload(String tableName, int rowCount, String lastDate, String type) {
            this.tableName = tableName;
            this.rowCount = rowCount;
            this.lastDate = lastDate;
            this.type = type;
        }

        @Bindable
        public String getTableName() {
            return tableName;
        }
        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        @Bindable
        public int getRowCount() {
            return rowCount;
        }
        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }

        @Bindable
        public String getLastDate() {
            return lastDate;
        }
        public void setLastDate(String lastDate) {
            this.lastDate = lastDate;
        }

        @Bindable
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
    }


    public static class SOMenu extends BaseObservable implements Parcelable{
        public String DocNo;
        public String DocDate;
        public String DebtorCode;
        public String DebtorName;
        public String SalesAgent;
        public String DocType;
        public String Remark;
        public String Location;

        public SOMenu(String docNo, String docDate, String debtorCode, String debtorName, String salesAgent, String docType, String remark, String location) {
            DocDate = docDate;
            DocNo = docNo;
            DebtorCode = debtorCode;
            DebtorName = debtorName;
            SalesAgent = salesAgent;
            DocType = docType;
            Remark = remark;
            Location = location;
        }

        public SOMenu(String docNo, String docDate, String debtorCode, String debtorName, String salesAgent, String docType) {
            DocDate = docDate;
            DocNo = docNo;
            DebtorCode = debtorCode;
            DebtorName = debtorName;
            SalesAgent = salesAgent;
            DocType = docType;
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getRemark() {
            return Remark;
        }

        public void setRemark(String remark) {
            Remark = remark;
            notifyPropertyChanged(BR.remark);
        }

        @Bindable
        public String getDebtorCode() {
            return DebtorCode;
        }

        public void setDebtorCode(String debtorCode) {
            DebtorCode = debtorCode;
            notifyPropertyChanged(BR.debtorCode);
        }

        @Bindable
        public String getDebtorName() {
            return DebtorName;
        }

        public void setDebtorName(String debtorName) {
            DebtorName = debtorName;
            notifyPropertyChanged(BR.debtorName);
        }

        @Bindable
        public String getSalesAgent() {
            return SalesAgent;
        }

        public void setSalesAgent(String salesAgent) {
            SalesAgent = salesAgent;
            notifyPropertyChanged(BR.salesAgent);
        }

        @Bindable
        public String getDocType() {
            return DocType;
        }

        public void setDocType(String docType) {
            DocType = docType;
            notifyPropertyChanged(BR.docType);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        protected SOMenu(Parcel in) {
            DocNo = in.readString();
            DocDate = in.readString();
            DebtorCode = in.readString();
            DebtorName = in.readString();
            SalesAgent = in.readString();
            DocType = in.readString();
            Remark = in.readString();
            Location = in.readString();
        }

        public static final Creator<SOMenu> CREATOR = new Creator<SOMenu>() {
            @Override
            public SOMenu createFromParcel(Parcel in) {
                return new SOMenu(in);
            }

            @Override
            public SOMenu[] newArray(int size) {
                return new SOMenu[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocNo);
            dest.writeString(DocDate);
            dest.writeString(DebtorCode);
            dest.writeString(DebtorName);
            dest.writeString(SalesAgent);
            dest.writeString(DocType);
            dest.writeString(Remark);
            dest.writeString(Location);
        }
    }

    public static class SODtl extends BaseObservable implements Parcelable {
        public Integer ID;
        public String Location;
        public String ItemCode;
        public String ItemDescription;
        public String UOM;
        public Double Qty;
        public Integer Line_No;
        public String BatchNo;

        public SODtl()
        {
        }

        protected SODtl(Parcel in) {
            if (in.readByte() == 0) {
                ID = null;
            } else {
                ID = in.readInt();
            }
            Location = in.readString();
            ItemCode = in.readString();
            ItemDescription = in.readString();
            if (in.readByte() == 0) {
                Qty = null;
            } else {
                Qty = in.readDouble();
            }
            if (in.readByte() == 0) {
                UOM = null;
            } else {
                UOM = in.readString();
            }
            if (in.readByte() == 0) {
                Line_No = null;
            } else {
                Line_No = in.readInt();
            }
            BatchNo = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (ID == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(ID);
            }
            dest.writeString(Location);
            dest.writeString(ItemCode);
            dest.writeString(ItemDescription);

            if (Qty == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Qty);
            }
            if (UOM == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(UOM);
            }
            if (Line_No == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(Line_No);
            }
            dest.writeString(BatchNo);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SODtl> CREATOR = new Creator<SODtl>() {
            @Override
            public SODtl createFromParcel(Parcel in) {
                return new SODtl(in);
            }

            @Override
            public SODtl[] newArray(int size) {
                return new SODtl[size];
            }
        };

        @Bindable
        public Integer getID() {
            return ID;
        }

        public void setID(Integer ID) {
            this.ID = ID;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getItemDescription() {
            return ItemDescription;
        }

        public void setItemDescription(String itemDescription) {
            ItemDescription = itemDescription;
            notifyPropertyChanged(BR.itemDescription);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public String getBatchNo() {
            return BatchNo;
        }

        public void setBatchNo(String batch) {
            BatchNo = batch;
            notifyPropertyChanged(BR.batchNo);
        }

        @Bindable
        public Double getQty() {
            return Qty;
        }

        public void setQty(Double qty) {
            Qty = qty;
            notifyPropertyChanged(BR.qty);
        }

        @Bindable
        public Integer getLine_No() {
            return Line_No;
        }

        public void setLine_No(Integer line_No) {
            this.Line_No = line_No;
            notifyPropertyChanged(BR.line_No);
        }
    }

    public static class SODODtl extends BaseObservable implements Parcelable {
        public Integer ID;
        public String Location;
        public String ItemCode;
        public String ItemDescription;
        public String UOM;
        public Double SOQty;
        public Double DOQty;
        public String SOBatch;
        public String DOBatch;

        public SODODtl()
        {
        }

        protected SODODtl(Parcel in) {
            if (in.readByte() == 0) {
                ID = null;
            } else {
                ID = in.readInt();
            }
            Location = in.readString();
            ItemCode = in.readString();
            ItemDescription = in.readString();
            if (in.readByte() == 0) {
                SOQty = null;
            } else {
                SOQty = in.readDouble();
            }
            if (in.readByte() == 0) {
                DOQty = null;
            } else {
                DOQty = in.readDouble();
            }
            if (in.readByte() == 0) {
                UOM = null;
            } else {
                UOM = in.readString();
            }
            SOBatch = in.readString();
            DOBatch = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (ID == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(ID);
            }
            dest.writeString(Location);
            dest.writeString(ItemCode);
            dest.writeString(ItemDescription);

            if (SOQty == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(SOQty);
            }
            if (DOQty == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(DOQty);
            }
            if (UOM == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(UOM);
            }
            dest.writeString(DOBatch);
            dest.writeString(SOBatch);

        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SODODtl> CREATOR = new Creator<SODODtl>() {
            @Override
            public SODODtl createFromParcel(Parcel in) {
                return new SODODtl(in);
            }

            @Override
            public SODODtl[] newArray(int size) {
                return new SODODtl[size];
            }
        };

        @Bindable
        public Integer getID() {
            return ID;
        }

        public void setID(Integer ID) {
            this.ID = ID;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getItemDescription() {
            return ItemDescription;
        }

        public void setItemDescription(String itemDescription) {
            ItemDescription = itemDescription;
            notifyPropertyChanged(BR.itemDescription);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Double getSOQty() {
            return SOQty;
        }

        public void setSOQty(Double soqty) {
            SOQty = soqty;
            notifyPropertyChanged(BR.sOQty);
        }

        @Bindable
        public Double getDOQty() {
            return DOQty;
        }

        public void setDOQty(Double doqty) {
            DOQty = doqty;
            notifyPropertyChanged(BR.dOQty);
        }

        @Bindable
        public String getSOBatch() {
            return SOBatch;
        }

        public void setSOBatch(String soBatch) {
            SOBatch = soBatch;
            notifyPropertyChanged(BR.sOBatch);
        }

        @Bindable
        public String getDOBatch() {
            return DOBatch;
        }

        public void setDOBatch(String dOBatch) {
            DOBatch = dOBatch;
            notifyPropertyChanged(BR.dOBatch);
        }
    }

    public static class DODtl extends BaseObservable implements Parcelable {
        public Integer ID;
        public String DocNo;
        public String Location;
        public String ItemCode;
        public String ItemDescription;
        public String UOM;
        public Double Qty;
        public Integer Line_No;
        public Integer DtlKey;
        public String Remarks;
        public String BatchNo;

        public DODtl() {
        }

        public DODtl(String docNoFP, String locationFP) {
            setDocNo(docNoFP);
            if (locationFP != null) {
                setLocation(locationFP);
            }
            setQty(Double.valueOf(1));
        }

        public DODtl(String docNo, String location, String itemCode, String itemDescription, String uom, Double qty, String remark, String batchno) {
            DocNo = docNo;
            Location = location;
            ItemCode = itemCode;
            ItemDescription = itemDescription;
            UOM = uom;
            Qty = qty;
            Remarks = remark;
            BatchNo = batchno;
        }

        public DODtl(DODtl dodtl) {
            this.DocNo = dodtl.DocNo;
            this.Location = dodtl.Location;
            this.ItemCode = dodtl.ItemCode;
            this.ItemDescription = dodtl.ItemDescription;
            this.UOM = dodtl.UOM;
            this.Qty = dodtl.Qty;
            this.Remarks = dodtl.Remarks;
            this.BatchNo = dodtl.BatchNo;
        }

        protected DODtl(Parcel in) {
            if (in.readByte() == 0) {
                ID = null;
            } else {
                ID = in.readInt();
            }
            DocNo = in.readString();
            Location = in.readString();
            ItemCode = in.readString();
            ItemDescription = in.readString();
            if (in.readByte() == 0) {
                Qty = null;
            } else {
                Qty = in.readDouble();
            }
            if (in.readByte() == 0) {
                UOM = null;
            } else {
                UOM = in.readString();
            }
            if (in.readByte() == 0) {
                Line_No = null;
            } else {
                Line_No = in.readInt();
            }
            if (in.readByte() == 0) {
                DtlKey = null;
            } else {
                DtlKey = in.readInt();
            }
            Remarks = in.readString();
            BatchNo = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (ID == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(ID);
            }
            dest.writeString(DocNo);
            dest.writeString(Location);
            dest.writeString(ItemCode);
            dest.writeString(ItemDescription);

            if (Qty == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Qty);
            }
            if (UOM == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(UOM);
            }
            if (Line_No == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(Line_No);
            }
            if (DtlKey == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(DtlKey);
            }
            dest.writeString(Remarks);
            dest.writeString(BatchNo);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DODtl> CREATOR = new Creator<DODtl>() {
            @Override
            public DODtl createFromParcel(Parcel in) {
                return new DODtl(in);
            }

            @Override
            public DODtl[] newArray(int size) {
                return new DODtl[size];
            }
        };

        @Bindable
        public Integer getID() {
            return ID;
        }

        public void setID(Integer ID) {
            this.ID = ID;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getItemDescription() {
            return ItemDescription;
        }

        public void setItemDescription(String itemDescription) {
            ItemDescription = itemDescription;
            notifyPropertyChanged(BR.itemDescription);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Double getQty() {
            return Qty;
        }

        public void setQty(Double qty) {
            Qty = qty;
            notifyPropertyChanged(BR.qty);
        }

        @Bindable
        public Integer getLine_No() {
            return Line_No;
        }

        public void setLine_No(Integer line_No) {
            this.Line_No = line_No;
            notifyPropertyChanged(BR.line_No);
        }

        @Bindable
        public Integer getDtlKey() {
            return DtlKey;
        }

        public void setDtlKey(Integer dtlKey) {
            this.DtlKey = dtlKey;
            notifyPropertyChanged(BR.dtlKey);
        }
        @Bindable
        public String getRemarks() {
            return Remarks;
        }

        public void setRemarks(String remarks) {
            Remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getBatchNo() {
            return BatchNo;
        }

        public void setBatchNo(String batchno) {
            BatchNo = batchno;
            notifyPropertyChanged(BR.batchNo);
        }

    }

    public static class DO extends BaseObservable implements Parcelable {
        private String DocNo;
        private String FromDocNo;
        private String CreatedTimeStamp;
        private String DocDate;
        private String DebtorCode;
        private String DebtorName;
        private String SalesAgent;
        private String CreatedUser;
        private Integer Uploaded;
        private String Phone;
        private String Fax;
        private String Location;
        private String Attention;
        private List<AC_Class.DODtl> DODtlList;
        private String Remarks;
        private String DocType;
        private Boolean IsTally = false;
        private String LastModifiedDateTime;
        private String LastModifiedUser;


        public DO() {
            DODtlList = new ArrayList<>();
            Uploaded = 0;
        }

        public DO(String docNo, String fromDocNo, String createdTimeStamp, String docDate, String debtorCode, String debtorName, String salesAgent, String phone, String fax, String loc, String attention, String remarks, String docType, Boolean isTally) {
            DocNo = docNo;
            FromDocNo = fromDocNo;
            CreatedTimeStamp = createdTimeStamp;
            DocDate = docDate;
            DebtorCode = debtorCode;
            DebtorName = debtorName;
            SalesAgent = salesAgent;
            Uploaded = 0;
            Phone = phone;
            Fax = fax;
            Location = loc;
            Attention = attention;
            DODtlList = new ArrayList<>();
            Remarks = remarks;
            DocType = docType;
            IsTally = isTally;
        }

        public DO(String docNo, String fromDocNo, String docDate, String debtorCode, String debtorName, String salesAgent, Integer uploaded, String remarks, String docType, Boolean isTally) {
            DocNo = docNo;
            FromDocNo = fromDocNo;
            DocDate = docDate;
            DebtorCode = debtorCode;
            DebtorName = debtorName;
            SalesAgent = salesAgent;
            Uploaded = uploaded;
            Remarks = remarks;
            DocType = docType;
            IsTally = isTally;
        }

        protected DO(Parcel in) {
            DocNo = in.readString();
            FromDocNo = in.readString();
            CreatedTimeStamp = in.readString();
            DocDate = in.readString();
            DebtorCode = in.readString();
            DebtorName = in.readString();
            SalesAgent = in.readString();
            CreatedUser = in.readString();
            Uploaded = in.readInt();
            Phone = in.readString();
            Fax = in.readString();
            Location = in.readString();
            Attention = in.readString();
            DODtlList = new ArrayList<>();
            in.readTypedList(DODtlList, DODtl.CREATOR);
            Remarks = in.readString();
            DocType = in.readString();
            LastModifiedUser = in.readString();
            LastModifiedDateTime = in.readString();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                IsTally = in.readBoolean();
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocNo);
            dest.writeString(FromDocNo);
            dest.writeString(CreatedTimeStamp);
            dest.writeString(DocDate);
            dest.writeString(DebtorCode);
            dest.writeString(DebtorName);
            dest.writeString(SalesAgent);
            dest.writeString(CreatedUser);
            dest.writeInt(Uploaded);
            dest.writeString(Phone);
            dest.writeString(Fax);
            dest.writeString(Location);
            dest.writeString(Attention);
            dest.writeTypedList((List<DODtl>) DODtlList);
            dest.writeString(Remarks);
            dest.writeString(DocType);
            dest.writeString(LastModifiedUser);
            dest.writeString(LastModifiedDateTime);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                dest.writeBoolean(IsTally);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DO> CREATOR = new Creator<DO>() {
            @Override
            public DO createFromParcel(Parcel in) {
                return new DO(in);
            }

            @Override
            public DO[] newArray(int size) {
                return new DO[size];
            }
        };

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getFromDocNo() {
            return FromDocNo;
        }

        public void setFromDocNo(String fromDocNo) {
            FromDocNo = fromDocNo;
            notifyPropertyChanged(BR.fromDocNo);
        }

        @Bindable
        public String getCreatedTimeStamp() {
            return CreatedTimeStamp;
        }

        public void setCreatedTimeStamp(String createdTimeStamp) {
            CreatedTimeStamp = createdTimeStamp;
            notifyPropertyChanged(BR.createdTimeStamp);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getDebtorCode() {
            return DebtorCode;
        }

        public void setDebtorCode(String debtorCode) {
            DebtorCode = debtorCode;
            notifyPropertyChanged(BR.debtorCode);
        }

        @Bindable
        public String getDebtorName() {
            return DebtorName;
        }

        public void setDebtorName(String debtorName) {
            DebtorName = debtorName;
            notifyPropertyChanged(BR.debtorName);
        }

        @Bindable
        public String getSalesAgent() {
            return SalesAgent;
        }

        public void setSalesAgent(String salesAgent) {
            SalesAgent = salesAgent;
            notifyPropertyChanged(BR.salesAgent);
        }

        @Bindable
        public String getCreatedUser() {
            return CreatedUser;
        }

        public void setCreatedUser(String createdUser) {
            CreatedUser = createdUser;
            notifyPropertyChanged(BR.createdUser);
        }

        @Bindable
        public String getPhone() {
            return Phone;
        }

        public void setPhone(String phone) {
            Phone = phone;
            notifyPropertyChanged(BR.phone);
        }

        @Bindable
        public String getFax() {
            return Fax;
        }

        public void setFax(String fax) {
            Fax = fax;
            notifyPropertyChanged(BR.fax);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String loc) {
            Location = loc;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getAttention() {
            return Attention;
        }

        public void setAttention(String attention) {
            Attention = attention;
            notifyPropertyChanged(BR.attention);
        }

        @Bindable
        public String getRemarks() {
            return Remarks;
        }

        public void setRemarks(String remarks) {
            Remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getDocType() {
            return DocType;
        }

        public void setDocType(String docType) {
            DocType = docType;
            notifyPropertyChanged(BR.docType);
        }

        @Bindable
        public Boolean getIsTally() {
            return IsTally;
        }

        public void setIsTally(Boolean isTally) {
            IsTally = isTally;
            notifyPropertyChanged(BR.isTally);
        }

        @Bindable
        public Integer getUploaded() {
            return Uploaded;
        }

        public void setUploaded(Integer uploaded) {
            Uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public String getLastModifiedUser() {
            return LastModifiedUser;
        }

        public void setLastModifiedUser(String lastModifiedUser) {
            this.LastModifiedUser = lastModifiedUser;
            notifyPropertyChanged(BR.lastModifiedUser);
        }

        @Bindable
        public String getLastModifiedDateTime() {
            return LastModifiedDateTime;
        }

        public void setLastModifiedDateTime(String lastModifiedDateTime) {
            this.LastModifiedDateTime = lastModifiedDateTime;
            notifyPropertyChanged(BR.lastModifiedDateTime);
        }

        @Bindable
        public List<AC_Class.DODtl> getDODtlList() { return DODtlList; }

        public void setDODtlList(List<DODtl> doDtlList) {
            DODtlList = doDtlList;
//            notifyPropertyChanged(BR.);
        }

        public void addDODtl(DODtl dODtl) {
            DODtlList.add(dODtl);
        }

        public void removeDODtl(int index) {
            DODtlList.remove(index);
        }

        public AC_Class.DODtl getDODtl(int id) {
            int i;
            for (i=0; i<DODtlList.size(); i++) {
                if (DODtlList.get(i).getID()==id) {
                    return DODtlList.get(i);
                }
            }
            return null;
        }
    }

    public static class ItemBatch extends BaseObservable {

        public String ItemCode;
        public String BatchNo;
        public String Description;
        public String ManufacturedDate;
        public String ExpiryDate;
        public String Location;
        public String UOM;

        public ItemBatch() {
        }

        public ItemBatch(String itemBatch, String batchNo, String desc, String manufacturedDate, String expiryDate, String location, String uom) {
            ItemCode = itemBatch;
            BatchNo = batchNo;
            Description = desc;
            ManufacturedDate = manufacturedDate;
            ExpiryDate = expiryDate;
            Location = location;
            UOM = uom;
        }

        public ItemBatch(String itemBatch, String batchNo, String desc, String manufacturedDate, String expiryDate) {
            ItemCode = itemBatch;
            BatchNo = batchNo;
            Description = desc;
            ManufacturedDate = manufacturedDate;
            ExpiryDate = expiryDate;
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getBatchNo() {
            return BatchNo;
        }

        public void setBatchNo(String batchNo) {
            BatchNo = batchNo;
            notifyPropertyChanged(BR.batchNo);
        }

        @Bindable
        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
            notifyPropertyChanged(BR.description);
        }

        @Bindable
        public String getManufacturedDate() {
            return ManufacturedDate;
        }

        public void setManufacturedDate(String manufacturedDate) {
            ManufacturedDate = manufacturedDate;
            notifyPropertyChanged(BR.manufacturedDate);
        }

        @Bindable
        public String getExpiryDate() {
            return ExpiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            ExpiryDate = expiryDate;
            notifyPropertyChanged(BR.expiryDate);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

    }

    public static class ARPayment extends BaseObservable implements Parcelable {
        public String DocNo;
        public String Date;
        public String DebtorCode;
        public String DebtorName;
        public Double Amount = 0.00;
        private Integer Uploaded = 0;
        public String Image;
        private String CreatedTimeStamp;
        private String CreatedUser;
        private String Remark;
        private String LastModifiedDateTime;
        private String LastModifiedUser;
        private List<AC_Class.ARPaymentDtl> ARPaymentDtl;

        public ARPayment() {
            ARPaymentDtl = new ArrayList<>();
        }

        public ARPayment(String docNo, String date, String debtorCode, String debtorName, Double amount, Integer uploaded, String createdTimeStamp, String createdUser, String remark) {
            DocNo = docNo;
            Date = date;
            DebtorCode = debtorCode;
            DebtorName = debtorName;
            Amount = amount;
            Uploaded = uploaded;
            CreatedTimeStamp = createdTimeStamp;
            CreatedUser = createdUser;
            Remark = remark;
            ARPaymentDtl = new ArrayList<>();
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDate() {
            return Date;
        }

        public void setDate(String date) {
            Date = date;
            notifyPropertyChanged(BR.date);
        }

        @Bindable
        public String getDebtorCode() {
            return DebtorCode;
        }

        public void setDebtorCode(String debtorCode) {
            DebtorCode = debtorCode;
            notifyPropertyChanged(BR.debtorCode);
        }

        @Bindable
        public String getDebtorName() {
            return DebtorName;
        }

        public void setDebtorName(String debtorName) {
            DebtorName = debtorName;
            notifyPropertyChanged(BR.debtorName);
        }

        @Bindable
        public Double getAmount() {
            return Amount;
        }

        public void setAmount(Double amount) {
            Amount = amount;
            notifyPropertyChanged(BR.amount);
        }

        @Bindable
        public Integer getUploaded() {
            return Uploaded;
        }

        public void setUploaded(Integer uploaded) {
            Uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public String getImage() {
            return Image;
        }

        public void setImage(String image) {
            Image = image;
            notifyPropertyChanged(BR.image);
        }

        @Bindable
        public String getCreatedTimeStamp() {
            return CreatedTimeStamp;
        }

        public void setCreatedTimeStamp(String createdTimeStamp) {
            CreatedTimeStamp = createdTimeStamp;
            notifyPropertyChanged(BR.createdTimeStamp);
        }

        @Bindable
        public String getCreatedUser() {
            return CreatedUser;
        }

        public void setCreatedUser(String createdUser) {
            CreatedUser = createdUser;
            notifyPropertyChanged(BR.createdUser);
        }

        @Bindable
        public String getRemark() {
            return Remark;
        }

        public void setRemark(String remark) {
            Remark = remark;
            notifyPropertyChanged(BR.remark);
        }

        @Bindable
        public String getLastModifiedUser() {
            return LastModifiedUser;
        }

        public void setLastModifiedUser(String lastModifiedUser) {
            this.LastModifiedUser = lastModifiedUser;
            notifyPropertyChanged(BR.lastModifiedUser);
        }

        @Bindable
        public String getLastModifiedDateTime() {
            return LastModifiedDateTime;
        }

        public void setLastModifiedDateTime(String lastModifiedDateTime) {
            this.LastModifiedDateTime = lastModifiedDateTime;
            notifyPropertyChanged(BR.lastModifiedDateTime);
        }

        @Bindable
        public List<AC_Class.ARPaymentDtl> getARPaymentDtl() { return ARPaymentDtl; }

        public void setARPaymentDtl(List<ARPaymentDtl> arDtlList) {
            ARPaymentDtl = arDtlList;
//            notifyPropertyChanged(BR.);
        }

        public void addARPaymentDtl(ARPaymentDtl arpaymentDtl) {
            ARPaymentDtl.add(arpaymentDtl);
        }

        public void removeARDtl(int index) {
            ARPaymentDtl.remove(index);
        }

        protected ARPayment(Parcel in) {
            Date = in.readString();
            DebtorCode = in.readString();
            DebtorName = in.readString();
            DocNo = in.readString();
            Amount = in.readDouble();
            Uploaded = in.readInt();
            ARPaymentDtl = new ArrayList<>();
            Image = in.readString();
            CreatedTimeStamp = in.readString();
            CreatedUser = in.readString();
            LastModifiedUser = in.readString();
            LastModifiedDateTime = in.readString();
            Remark = in.readString();
            in.readTypedList(ARPaymentDtl, AC_Class.ARPaymentDtl.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(Date);
            dest.writeString(DebtorCode);
            dest.writeString(DebtorName);
            dest.writeString(DocNo);
            dest.writeDouble(Amount);
            dest.writeInt(Uploaded);
            dest.writeString(Image);
            dest.writeString(CreatedTimeStamp);
            dest.writeString(CreatedUser);
            dest.writeString(LastModifiedUser);
            dest.writeString(LastModifiedDateTime);
            dest.writeString(Remark);
            dest.writeTypedList((List<ARPaymentDtl>) ARPaymentDtl);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ARPayment> CREATOR = new Creator<ARPayment>() {
            @Override
            public ARPayment createFromParcel(Parcel in) {
                return new ARPayment(in);
            }

            @Override
            public ARPayment[] newArray(int size) {
                return new ARPayment[size];
            }
        };
    }

    public static class ARPaymentDtl extends BaseObservable implements Parcelable {
        public String DocNo;
        public String DocNumber;
        public String DocDate;
        public Double PaymentAmount;
        public Double OrgAmt;

        public ARPaymentDtl() {
        }

        public ARPaymentDtl(String docNoFP) {
            setDocNo(docNoFP);
            setPaymentAmount(0.00);
        }

        public ARPaymentDtl(String docNo, String docNumber, String docDate, Double paymentAmount, Double orgAmt) {
            DocNo = docNo;
            DocNumber = docNumber;
            DocDate = docDate;
            this.PaymentAmount = paymentAmount;
            this.OrgAmt = orgAmt;
        }

        public ARPaymentDtl(ARPaymentDtl ardtl) {
            this.DocNo = ardtl.DocNo;
            this.DocNumber = ardtl.DocNumber;
            this.PaymentAmount = ardtl.PaymentAmount;
        }

        protected ARPaymentDtl(Parcel in) {
            DocNo = in.readString();
            DocNumber = in.readString();
            DocDate = in.readString();
            PaymentAmount = in.readDouble();
            OrgAmt = in.readDouble();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocNo);
            dest.writeString(DocNumber);
            dest.writeString(DocDate);
            dest.writeDouble(PaymentAmount);
            dest.writeDouble(OrgAmt);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ARPaymentDtl> CREATOR = new Creator<ARPaymentDtl>() {
            @Override
            public ARPaymentDtl createFromParcel(Parcel in) {
                return new ARPaymentDtl(in);
            }

            @Override
            public ARPaymentDtl[] newArray(int size) {
                return new ARPaymentDtl[size];
            }
        };


        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDocNumber() {
            return DocNumber;
        }

        public void setDocNumber(String docNumber) {
            DocNumber = docNumber;
            notifyPropertyChanged(BR.docNumber);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public Double getPaymentAmount() {
            return PaymentAmount;
        }

        public void setPaymentAmount(Double paymentAmount) {
            PaymentAmount = paymentAmount;
            notifyPropertyChanged(BR.paymentAmount);
        }

        @Bindable
        public Double getOrgAmt() {
            return OrgAmt;
        }

        public void setOrgAmt(Double orgAmt) {
            OrgAmt = orgAmt;
            notifyPropertyChanged(BR.orgAmt);
        }
    }

    public static class AROutstanding extends BaseObservable {

        public String DocNo;
        public String DocDate;
        public Double NetTotal;
        public Double Outstanding;
        public String DueDate;
        public Boolean Status;

        public AROutstanding() {
        }

        public AROutstanding(String docNo, String docDate, Double netTotal, Double outstanding, String dueDate) {
            DocNo = docNo;
            DocDate = docDate;
            NetTotal = netTotal;
            Outstanding = outstanding;
            DueDate = dueDate;
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public Double getNetTotal() {
            return NetTotal;
        }

        public void setNetTotal(Double netTotal){
            NetTotal = netTotal;
            notifyPropertyChanged(BR.netTotal);
        }

        @Bindable
        public Double getOutstanding() {
            return Outstanding;
        }

        public void setOutstanding(Double outstanding){
            Outstanding = outstanding;
            notifyPropertyChanged(BR.outstanding);
        }

        @Bindable
        public String getDueDate() {
            return DueDate;
        }

        public void setDueDate(String dueDate) {
            DueDate = dueDate;
            notifyPropertyChanged(BR.dueDate);
        }

        @Bindable
        public Boolean getStatus() {
            return Status;
        }

        public void setStatus(Boolean status) {
            Status = status;
            notifyPropertyChanged(BR.status);
        }

    }

    public static class Statement extends BaseObservable {
        public String DocType;
        public String DocDate;
        public String DocNo;
        public String DebtorCode;
        public String DebtorName;
        public String Agent;
        public Double Total;
        public Double Paid;
        public Double Balance;
        public int SourceKey;
        public String SourceType;
        public String Terms;

        public Statement(){

        }
        public Statement(String docType, String docDate, String docNo, String debtorCode, String debtorName, Double balance) {
            DocType = docType;
            DocDate = docDate;
            DocNo = docNo;
            DebtorCode = debtorCode;
            DebtorName = debtorName;
            Balance = balance;
        }

        public Statement(String docType, String docDate, String docNo, String debtorCode, String debtorName, String agent, Double paid, Double balance, Double total, int sourceKey, String sourceType, String terms) {
            DocType = docType;
            DocDate = docDate;
            DocNo = docNo;
            DebtorCode = debtorCode;
            DebtorName = debtorName;
            Agent = agent;
            Paid = paid;
            Balance =balance;
            Total = total;
            SourceKey = sourceKey;
            SourceType = sourceType;
            Terms = terms;
        }

        @Bindable
        public String getDocType() {
            return DocType;
        }

        public void setDocType(String docType) {
            DocType = docType;
            notifyPropertyChanged(BR.docType);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDebtorCode() {
            return DebtorCode;
        }

        public void setDebtorCode(String debtorCode) {
            DebtorCode = debtorCode;
            notifyPropertyChanged(BR.debtorCode);
        }

        @Bindable
        public String getDebtorName() {
            return DebtorName;
        }

        public void setDebtorName(String debtorName) {
            DebtorName = debtorName;
            notifyPropertyChanged(BR.debtorName);
        }

        @Bindable
        public String getAgent() {
            return Agent;
        }

        public void setAgent(String agent) {
            Agent = agent;
            notifyPropertyChanged(BR.agent);
        }

        @Bindable
        public String getSourceType() {
            return SourceType;
        }

        public void setSourceType(String sourceType) {
            SourceType = sourceType;
            notifyPropertyChanged(BR.sourceType);
        }

        @Bindable
        public int getSourceKey() {
            return SourceKey;
        }

        public void setSourceKey(int sourceKey) {
            SourceKey = sourceKey;
            notifyPropertyChanged(BR.sourceKey);
        }

        @Bindable
        public String getTerms() {
            return Terms;
        }

        public void setTerms(String terms) {
            Terms = terms;
            notifyPropertyChanged(BR.terms);
        }

        @Bindable
        public Double getTotal() {
            return Total;
        }

        public void setTotalIn(Double total) {
            Total = total;
            notifyPropertyChanged(BR.total);
        }

        @Bindable
        public Double getBalance() {
            return Balance;
        }

        public void setBalance(Double balance) {
            Balance = balance;
            notifyPropertyChanged(BR.balance);
        }

        @Bindable
        public Double getPaid() {
            return Paid;
        }

        public void setPaid(Double paid) {
            Paid = paid;
            notifyPropertyChanged(BR.paid);
        }

    }

    public static class StockAssemblyMenu extends BaseObservable implements Parcelable {
        public String DocNo;
        public String DocDate;
        public String Location;
        public String Remarks;
        public String TotalQty;
        public int Uploaded;

        public StockAssemblyMenu() {
        }

        public StockAssemblyMenu(String docNo,String docDate, String location, String totalqty, String remarks, int uploaded) {
            DocDate = docDate;
            DocNo = docNo;
            Location = location;
            Remarks = remarks;
            TotalQty = totalqty;
            Uploaded = uploaded;
        }

        public StockAssemblyMenu(String docNo,String docDate, String remarks, int uploaded) {
            DocDate = docDate;
            DocNo = docNo;
            Remarks = remarks;
            Uploaded = uploaded;
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {
            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getRemarks() {
            return Remarks;
        }

        public void setRemarks(String remarks){
            Remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getTotalQty() {
            return TotalQty;
        }

        public void setTotalQty(String totalQty){
            TotalQty = totalQty;
            notifyPropertyChanged(BR.totalQty);
        }


        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location){
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public int getUploaded() {
            return Uploaded;
        }

        public void setUploaded(int uploaded){
            Uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        protected StockAssemblyMenu(Parcel in) {
            DocDate = in.readString();
            DocNo = in.readString();
            Location= in.readString();
            Remarks = in.readString();
            Uploaded = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(DocDate);
            dest.writeString(DocNo);
            dest.writeString(Location);
            dest.writeString(Remarks);
            dest.writeInt(Uploaded);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StockAssemblyMenu> CREATOR = new Creator<StockAssemblyMenu>() {
            @Override
            public StockAssemblyMenu createFromParcel(Parcel in) {
                return new StockAssemblyMenu(in);
            }

            @Override
            public StockAssemblyMenu[] newArray(int size) {
                return new StockAssemblyMenu[size];
            }
        };
    }

    public static class StockAssembly extends BaseObservable implements Parcelable {
        private String docNo;
        private String createdTimeStamp;
        private String docDate;
        private String FGItemCode;
        private Double FGQty;
        private String FGBatchNo;
        private String Description;
        private String location;
        private Integer uploaded;
        private String remarks;
        private String createdUser;
        private String LastModifiedDateTime;
        private String LastModifiedUser;
        private List<AC_Class.StockAssemblyDetails> stockAssemblyDetails;

        public StockAssembly() {
            docNo = null;
            createdTimeStamp = null;
            createdUser = null;
            docDate = null;
            location = null;
            FGQty = 1.00;
            uploaded = 0;
            Description = null;
            remarks = null;
            stockAssemblyDetails = new ArrayList<>();
        }

        public StockAssembly(String docNo, String createdTimeStamp, String docDate, String remarks, int uploaded) {
            this.docNo = docNo;
            this.createdTimeStamp = createdTimeStamp;
            this.docDate = docDate;
            this.remarks = remarks;
            this.uploaded = uploaded;
        }

        public StockAssembly(String docNo, String createdTimeStamp, String docDate, String fgitemcode, Double fgqty, String batchno ,String loc, String remarks) {
            this.docNo = docNo;
            this.createdTimeStamp = createdTimeStamp;
            this.docDate = docDate;
            this.FGItemCode = fgitemcode;
            this.FGQty = Double.valueOf(1);
            this.FGBatchNo = batchno;
            this.location = loc;
            this.uploaded = 0;
            this.remarks = remarks;
            stockAssemblyDetails = new ArrayList<>();
        }

        protected StockAssembly(Parcel in) {
            docNo = in.readString();
            createdTimeStamp = in.readString();
            docDate = in.readString();
            location = in.readString();
            uploaded = in.readInt();
            remarks = in.readString();
            FGItemCode = in.readString();
            FGBatchNo = in.readString();
            FGQty = in.readDouble();
            stockAssemblyDetails = new ArrayList<>();
            in.readTypedList(stockAssemblyDetails, StockAssemblyDetails.CREATOR);
            Description = in.readString();
            createdUser = in.readString();
            LastModifiedUser = in.readString();
            LastModifiedDateTime = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(docNo);
            dest.writeString(createdTimeStamp);
            dest.writeString(docDate);
            dest.writeString(location);
            dest.writeInt(uploaded);
            dest.writeString(remarks);
            dest.writeString(FGItemCode);
            dest.writeString(FGBatchNo);
            dest.writeDouble(FGQty);
            dest.writeTypedList((List<StockAssemblyDetails>) stockAssemblyDetails);
            dest.writeString(Description);
            dest.writeString(createdUser);
            dest.writeString(LastModifiedUser);
            dest.writeString(LastModifiedDateTime);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StockAssembly> CREATOR = new Creator<StockAssembly>() {
            @Override
            public StockAssembly createFromParcel(Parcel in) {
                return new  StockAssembly(in);
            }

            @Override
            public  StockAssembly[] newArray(int size) {
                return new  StockAssembly[size];
            }
        };

        @Bindable
        public String getDocNo() {
            return docNo;
        }

        public void setDocNo(String docNo) {
            this.docNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            this.Description = description;
            notifyPropertyChanged(BR.description);
        }

        @Bindable
        public String getCreatedTimeStamp() {
            return createdTimeStamp;
        }

        public void setCreatedTimeStamp(String createdTimeStamp) {
            this.createdTimeStamp = createdTimeStamp;
        }

        @Bindable
        public String getCreatedUser() {
            return createdUser;
        }

        public void setCreatedUser(String createdUser) {
            this.createdUser = createdUser;
        }

        @Bindable
        public String getDocDate() {
            return docDate;
        }

        public void setDocDate(String docDate) {
            this.docDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getFGItemCode() {
            return FGItemCode;
        }

        public void setFGItemCode(String fgitemcode) {
            this.FGItemCode = fgitemcode;
            notifyPropertyChanged(BR.fGItemCode);
        }

        @Bindable
        public String getFGBatchNo() {
            return FGBatchNo;
        }

        public void setFGBatchNo(String fgbatchno) {
            this.FGBatchNo = fgbatchno;
            notifyPropertyChanged(BR.fGBatchNo);
        }

        @Bindable
        public Double getFGQty() {
            return FGQty;
        }

        public void setFGQty(Double fgqty) {
            this.FGQty = fgqty;
            notifyPropertyChanged(BR.fGQty);
        }

        @Bindable
        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public Integer getUploaded() {
            return uploaded;
        }

        public void setUploaded(Integer uploaded) {
            this.uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }


        @Bindable
        public String getLastModifiedUser() {
            return LastModifiedUser;
        }

        public void setLastModifiedUser(String lastModifiedUser) {
            this.LastModifiedUser = lastModifiedUser;
            notifyPropertyChanged(BR.lastModifiedUser);
        }

        @Bindable
        public String getLastModifiedDateTime() {
            return LastModifiedDateTime;
        }

        public void setLastModifiedDateTime(String lastModifiedDateTime) {
            this.LastModifiedDateTime = lastModifiedDateTime;
            notifyPropertyChanged(BR.lastModifiedDateTime);
        }

        @Bindable
        public List<StockAssemblyDetails> getStockAssemblyDtlList() { return stockAssemblyDetails; }

        public void setStockAssemblyDtlList(List<StockAssemblyDetails> StockTakeDetailsFP) {
            this.stockAssemblyDetails = StockTakeDetailsFP;
        }

        public void removeStockTakeDetail(int index) {
            stockAssemblyDetails.remove(index);
        }

        public int StockDetailListLength()
        {
            return stockAssemblyDetails.size();
        }

    }

    public static class StockAssemblyDetails extends BaseObservable implements Parcelable {
        public String SubItemCode;
        public Double Quantity;


        public StockAssemblyDetails() {
            Quantity = Double.valueOf(1);
            setSubItemCode(null);
            setQuantity(Double.valueOf(1));
        }

        public StockAssemblyDetails(String itemCode, Double quantity) {
            SubItemCode = itemCode;
            Quantity = quantity;
        }

        protected StockAssemblyDetails(Parcel in) {
           SubItemCode = in.readString();
            if (in.readByte() == 0) {
                Quantity = null;
            } else {
                Quantity = in.readDouble();
            }
        }

        public StockAssemblyDetails(String docNo) {
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(SubItemCode);
            if (Quantity == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Quantity);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StockAssemblyDetails> CREATOR = new Creator<StockAssemblyDetails>() {
            @Override
            public StockAssemblyDetails createFromParcel(Parcel in) {
                return new StockAssemblyDetails(in);
            }

            @Override
            public StockAssemblyDetails[] newArray(int size) {
                return new StockAssemblyDetails[size];
            }
        };

        @Bindable
        public String getSubItemCode() {
            return SubItemCode;
        }

        public void setSubItemCode(String itemCode) {
            SubItemCode = itemCode;
            notifyPropertyChanged(BR.subItemCode);
        }


        @Bindable
        public Double getQuantity() {
            return Quantity;
        }

        public void setQuantity(Double quantity) {
            Quantity = quantity;
            notifyPropertyChanged(BR.quantity);
        }
    }

    public static class ItemBOM implements Parcelable {
        public String ItemCode;
        public String SubItemCode;
        public Double Qty;
        public String Description;


        public ItemBOM(String itemcode, String subItemCode, Double qty) {
            this.ItemCode= itemcode;
            this.SubItemCode = subItemCode;
            this.Qty = qty;
        }
        public ItemBOM(String itemcode, String des) {
            this.ItemCode= itemcode;
            this.Description = des;
        }

        protected ItemBOM(Parcel in) {
            ItemCode = in.readString();
            SubItemCode = in.readString();
            if (in.readByte() == 0) {
                Qty = null;
            } else {
                Qty = in.readDouble();
            }
        }

        public static final Creator<ItemBOM> CREATOR = new Creator<ItemBOM>() {
            @Override
            public ItemBOM createFromParcel(Parcel in) {
                return new ItemBOM(in);
            }

            @Override
            public ItemBOM[] newArray(int size) {
                return new ItemBOM[size];
            }
        };

        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String desc) {
            Description = desc;
        }

        public String getSubItemCode() {
            return SubItemCode;
        }

        public void setSubItemCode(String subItemCode) {
            SubItemCode = subItemCode;
        }

        public Double getQty() {
            return Qty;
        }

        public void setQty(Double qty) {
            Qty = qty;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ItemCode);
            dest.writeString(SubItemCode);
            if (Qty == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Qty);
            }
        }
    }

    public static class AgentSalesList{
        private Double ytd_Sales;
        private Double tdy_Sales;
        private Double lmm_Sales;
        private Double tmm_Sales;
        private Double lyy_Sales;
        private Double tyy_Sales;

        AgentSalesList(Double ytd_sales, Double tdy_sales, Double lmm_sales,
                       Double tmm_sales, Double lyy_sales, Double tyy_sales){
            this.ytd_Sales = ytd_sales;
            this.tdy_Sales = tdy_sales;
            this.lmm_Sales = lmm_sales;
            this.tmm_Sales = tmm_sales;
            this.lyy_Sales = lyy_sales;
            this.tyy_Sales = tyy_sales;
        }

        public Double getYtd_Sales() {
            return ytd_Sales;
        }

        public void setYtd_Sales(Double ytd_Sales) {
            this.ytd_Sales = ytd_Sales;
        }

        public Double getTdy_Sales() {
            return tdy_Sales;
        }

        public void setTdy_Sales(Double tdy_Sales) {
            this.tdy_Sales = tdy_Sales;
        }

        public Double getLmm_Sales() {
            return lmm_Sales;
        }

        public void setLmm_Sales(Double lmm_Sales) {
            this.lmm_Sales = lmm_Sales;
        }

        public Double getTmm_Sales() {
            return tmm_Sales;
        }

        public void setTmm_Sales(Double tmm_Sales) {
            this.tmm_Sales = tmm_Sales;
        }

        public Double getLyy_Sales() {
            return lyy_Sales;
        }

        public void setLyy_Sales(Double lyy_Sales) {
            this.lyy_Sales = lyy_Sales;
        }

        public Double getTyy_Sales() {
            return tyy_Sales;
        }

        public void setTyy_Sales(Double tyy_Sales) {
            this.tyy_Sales = tyy_Sales;
        }
    }

    public static class UTDCost extends BaseObservable {

        public String ItemCode;
        public String BatchNo;
        public String UOM;
        public String Location;
        public Double UTDQty;
        public Double UTDCost;

        public UTDCost() {
        }

        public UTDCost(String itemBatch, String batchNo, String location, String uom, Double utdQty, Double utdCost) {
            ItemCode = itemBatch;
            BatchNo = batchNo;
            Location = location;
            UOM = uom;
            UTDQty=utdQty;
            UTDCost = utdCost;
        }


        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getBatchNo() {
            return BatchNo;
        }

        public void setBatchNo(String batchNo) {
            BatchNo = batchNo;
            notifyPropertyChanged(BR.batchNo);
        }


        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Double getUTDCost() {
            return UTDCost;
        }

        public void setUTDCost(Double utdCost) {
            UTDCost = utdCost;
            notifyPropertyChanged(BR.uTDCost);
        }

        @Bindable
        public Double getUTDQty() {
            return UTDQty;
        }

        public void setUTDQty(Double utdQty) {
            UTDQty = utdQty;
            notifyPropertyChanged(BR.uTDQty);
        }

    }

    public static class StockReceive extends BaseObservable implements Parcelable {
        private String docNo;
        private String createdTimeStamp;
        private String docDate;
        private String docType;
        private Integer uploaded;
        private String taxType;
        private String remarks;
        private String description;
        private String createdUser;
        private String lastModifiedDateTime;
        private String lastModifiedUser;

        private List<AC_Class.StockReceiveDetails> stockReceiveDetailsList;

        public StockReceive(String docNo, String docDate, String description, String remarks, String createdTime, String createdUser, int uploaded) {
            this.docNo = docNo;
            this.docDate = docDate;
            this.description = description;
            this.remarks = remarks;
            this.createdTimeStamp = createdTime;
            this.createdUser = createdUser;
            this.uploaded = uploaded;

        }
        public StockReceive() {
            this.docNo = null;
            this.docDate = null;
            this.docType = null;
            this.description = null;
            this.remarks = null;
            this.taxType = null;
            this.uploaded = 0;
            this.createdTimeStamp = null;
            this.createdUser = null;
            this.lastModifiedDateTime = null;
            this.lastModifiedUser = null;
            this.stockReceiveDetailsList = new ArrayList<>();

        }


        protected StockReceive(Parcel in) {
            docNo = in.readString();
            createdTimeStamp = in.readString();
            docDate = in.readString();
            docType = in.readString();
            uploaded = in.readInt();
            taxType = in.readString();
            stockReceiveDetailsList = new ArrayList<>();
            in.readTypedList(stockReceiveDetailsList, StockReceiveDetails.CREATOR);
            remarks = in.readString();
            description = in.readString();
            createdUser = in.readString();
            lastModifiedUser = in.readString();
            lastModifiedDateTime = in.readString();
        }




        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(docNo);
            dest.writeString(createdTimeStamp);
            dest.writeString(docDate);
            dest.writeString(docType);
            dest.writeInt(uploaded != null ? uploaded : 0);
            dest.writeString(taxType);
            dest.writeTypedList((List<StockReceiveDetails>) stockReceiveDetailsList);
            dest.writeString(remarks);
            dest.writeString(description);
            dest.writeString(createdUser);
            dest.writeString(lastModifiedUser);
            dest.writeString(lastModifiedDateTime);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StockReceive> CREATOR = new Creator<StockReceive>() {
            @Override
            public StockReceive createFromParcel(Parcel in) {
                return new StockReceive(in);
            }

            @Override
            public StockReceive[] newArray(int size) {
                return new StockReceive[size];
            }
        };

        @Bindable
        public String getDocNo() {
            return docNo;
        }

        public void setDocNo(String docNo) {
            this.docNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getCreatedTimeStamp() {
            return createdTimeStamp;
        }

        public void setCreatedTimeStamp(String createdTimeStamp) {
            this.createdTimeStamp = createdTimeStamp;
            notifyPropertyChanged(BR.createdTimeStamp);
        }

        @Bindable
        public String getDocDate() {
            return docDate;
        }

        public void setDocDate(String docDate) {
            this.docDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getDocType() {
            return docType;
        }

        public void setDocType(String docType) {
            this.docType = docType;
            notifyPropertyChanged(BR.docType);
        }

        @Bindable
        public Integer getUploaded() {
            return uploaded;
        }

        public void setUploaded(Integer uploaded) {
            this.uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public String getTaxType() {
            return taxType;
        }

        public void setTaxType(String taxType) {
            this.taxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
            notifyPropertyChanged(BR.description);
        }

        @Bindable
        public String getCreatedUser() {
            return createdUser;
        }

        public void setCreatedUser(String createdUser) {
            this.createdUser = createdUser;
            notifyPropertyChanged(BR.createdUser);
        }

        @Bindable
        public String getLastModifiedDateTime() {
            return lastModifiedDateTime;
        }

        public void setLastModifiedDateTime(String lastModifiedDateTime) {
            this.lastModifiedDateTime = lastModifiedDateTime;
            notifyPropertyChanged(BR.lastModifiedDateTime);
        }

        @Bindable
        public String getLastModifiedUser() {
            return lastModifiedUser;
        }

        public void setLastModifiedUser(String lastModifiedUser) {
            this.lastModifiedUser = lastModifiedUser;
            notifyPropertyChanged(BR.lastModifiedUser);
        }

        @Bindable
        public List<AC_Class.StockReceiveDetails> getStockReceiveDetailsList() {
            return stockReceiveDetailsList;
        }

        public void setStockReceiveDetailsList(List<AC_Class.StockReceiveDetails> stockReceiveDetailsList) {
            this.stockReceiveDetailsList = stockReceiveDetailsList;
        }

        public void addStockReceiveDetail(AC_Class.StockReceiveDetails stockReceiveDetails) {
            stockReceiveDetailsList.add(stockReceiveDetails);
        }

        public void removeStockReceiveDetail(int index) {
            stockReceiveDetailsList.remove(index);
        }

        public AC_Class.StockReceiveDetails getStockReceiveDetail(int id) {
            for (AC_Class.StockReceiveDetails detail : stockReceiveDetailsList) {
                if (detail.getID() == id) {
                    return detail;
                }
            }
            return null;
        }
    }

    public static class StockReceiveDetails extends BaseObservable implements Parcelable {
        public Integer ID;
        public String DocNo;
        public String DocDate;
        public String Location;
        public String ItemCode;
        public String ItemDescription;
        public String UOM;
        public Double Quantity;
        public Double UTD_Cost;
        public Double SubTotal;
        public String Batch_No;
        public String Remarks;
        public String Remarks2;
        public String Line_No;

        public StockReceiveDetails() {
            Quantity = Double.valueOf(1);
            SubTotal = Double.valueOf(0);
            UTD_Cost = Double.valueOf(0);
            setItemCode(null);
            setQuantity(Double.valueOf(1));
        }

        public StockReceiveDetails(Integer id, String docNo, String docDate, String location, String itemCode,
                                   String itemDescription, String uom, Double quantity, Double utdCost,
                                   Double subTotal, String batchNo, String remarks, String remarks2, String lineNo) {
            this.ID = id;
            this.DocNo = docNo;
            this.DocDate = docDate;
            this.Location = location;
            this.ItemCode = itemCode;
            this.ItemDescription = itemDescription;
            this.UOM = uom;
            this.Quantity = quantity;
            this.UTD_Cost = utdCost;
            this.SubTotal = subTotal;
            this.Batch_No = batchNo;
            this.Remarks = remarks;
            this.Remarks2 = remarks2;
            this.Line_No = lineNo;
        }

        protected StockReceiveDetails(Parcel in) {
            if (in.readByte() == 0) {
                ID = null;
            } else {
                ID = in.readInt();
            }
            DocNo = in.readString();
            DocDate = in.readString();
            Location = in.readString();
            ItemCode = in.readString();
            ItemDescription = in.readString();
            if (in.readByte() == 0) {
                Quantity = null;
            } else {
                Quantity = in.readDouble();
            }
            if (in.readByte() == 0) {
                UOM = null;
            } else {
                UOM = in.readString();
            }
            if (in.readByte() == 0) {
                UTD_Cost = null;
            } else {
                UTD_Cost = in.readDouble();
            }
            if (in.readByte() == 0) {
                SubTotal = null;
            } else {
                SubTotal = in.readDouble();
            }
            Batch_No = in.readString();
            Remarks = in.readString();
            Remarks2 = in.readString();
            Line_No = in.readString();

        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (ID == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(ID);
            }
            dest.writeString(DocNo);
            dest.writeString(DocDate);
            dest.writeString(Location);
            dest.writeString(ItemCode);
            dest.writeString(ItemDescription);

            if (Quantity == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Quantity);
            }
            if (UOM == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(UOM);
            }
            if (UTD_Cost == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(UTD_Cost);
            }
            if (SubTotal == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(SubTotal);
            }
            dest.writeString(Batch_No);
            dest.writeString(Remarks);
            dest.writeString(Remarks2);
            dest.writeString(Line_No);

        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StockReceiveDetails> CREATOR = new Creator<StockReceiveDetails>() {
            @Override
            public StockReceiveDetails createFromParcel(Parcel in) {
                return new StockReceiveDetails(in);
            }

            @Override
            public StockReceiveDetails[] newArray(int size) {
                return new StockReceiveDetails[size];
            }
        };

        @Bindable
        public Integer getID() {
            return ID;
        }

        public void setID(Integer ID) {
            this.ID = ID;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {

            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }


        @Bindable
        public String getItemDescription() {
            return ItemDescription;
        }

        public void setItemDescription(String itemDescription) {
            ItemDescription = itemDescription;
            notifyPropertyChanged(BR.itemDescription);
        }


        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }


        @Bindable
        public Double getQuantity() {
            return Quantity;
        }

        public void setQuantity(Double quantity) {
            Quantity = quantity;
            notifyPropertyChanged(BR.quantity);
        }

        @Bindable
        public Double getUTD_Cost() {
            return UTD_Cost;
        }

        public void setUTD_Cost(Double UTD_Cost) {
            this.UTD_Cost = UTD_Cost;
            notifyPropertyChanged(BR.uTD_Cost);
        }


        @Bindable
        public Double getSubTotal() {
            return SubTotal;
        }

        public void setSubTotal(Double subTotal) {
            SubTotal = subTotal;
            notifyPropertyChanged(BR.subTotal);
        }

        @Bindable
        public String getBatch_No() {
            return Batch_No;
        }

        public void setBatch_No(String batch_No) {
            this.Batch_No = batch_No;
            notifyPropertyChanged(BR.batch_No);
        }

        @Bindable
        public String getRemarks() {
            return Remarks;
        }

        public void setRemarks(String remarks) {
            this.Remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getRemarks2() { return Remarks2;
        }

        public void setRemarks2(String remarks2) {
            this.Remarks2 = remarks2;
            notifyPropertyChanged(BR.remarks2);
        }

        @Bindable
        public String getLine_No() {
            return Line_No;
        }

        public void setLine_No(String line_No) {
            this.Line_No = line_No;
            notifyPropertyChanged(BR.line_No);
        }

    }


    public static class JobSheet extends BaseObservable implements Parcelable {
        public String docType;
        private String docNo;
        private String docDate;
        private String debtorCode;
        private String debtorName;
        private String debtorName2;
        private String agent;
        private String taxType;
        private Integer uploaded;
        private String phone;
        private String fax;
        private String attention;
        private String address1;
        private String address2;
        private String address3;
        private String address4;
        private String Remarks;
        private String Status;
        private String Remarks2;
        private String Remarks3;
        private String Remarks4;
        private String workType;
        private String replacementType;
        private String timeIn;
        private String timeOut;
        private String problem;
        private String resolution;
        private String jobSheetRemarks;
        private String salesNo;
        public Double TotalEx;
        public Double Discount;
        public Double TotalTax;
        public Double TotalIn;
        private String CreatedTimeStamp;
        private String CreatedUser;
        private String LastModifiedDateTime;
        private String LastModifiedUser;
        private String Signature;
        public String Image;
        private List<AC_Class.JobSheetDetails> jobSheetDetailsList;

        public JobSheet() {
            docType = null;
            docNo = null;
            docDate = null;
            debtorCode = null;
            debtorName = null;
            debtorName2 = null;
            agent = null;
            taxType = null;
            uploaded = 0;
            phone = null;
            fax = null;
            attention = null;
            Status = null;
            workType = null;
            replacementType = null;
            timeIn = null;
            timeOut = null;
            problem = null;
            resolution = null;
            jobSheetRemarks = null;
            salesNo = null;
            TotalEx = null;
            Discount = null;
            TotalTax = null;
            TotalIn = null;
            CreatedTimeStamp = null;
            CreatedUser = null;
            LastModifiedDateTime = null;
            LastModifiedUser = null;
            Signature = null;
            Image = null;
            jobSheetDetailsList = new ArrayList<>();
        }

        public JobSheet(String docNo, String docDate, String debtorCode,
                        String debtorName, String debtorName2, String agent, String taxType,
                        String phone, String fax, String attention, String remarks, String remarks2, String remarks3,
                        String remarks4, String workType, String replacementType, String timeIn, String timeOut, String problem, String resolution, String jobSheetRemarks, String signature, String image) {
            this.docNo = docNo;
            this.docDate = docDate;
            this.debtorCode = debtorCode;
            this.debtorName = debtorName;
            this.debtorName2 = debtorName2;
            this.agent = agent;
            this.taxType = taxType;
            this.uploaded = 0;
            this.phone = phone;
            this.fax = fax;
            this.attention = attention;
            this.Remarks = remarks;
            this.Remarks2 = remarks2;
            this.Remarks3 = remarks3;
            this.Remarks4 = remarks4;
            this.workType = workType;
            this.replacementType = replacementType;
            this.timeIn = timeIn;
            this.timeOut = timeOut;
            this.problem = problem;
            this.resolution = resolution;
            this.jobSheetRemarks = jobSheetRemarks;
            this.Signature = signature;
            this.Image = image;
            jobSheetDetailsList = new ArrayList<>();
        }

        public JobSheet(String docNo, String docDate, String workType, String replacementType, int uploaded, String status, String salesNo, String agent, String debtorCode) {
            this.docNo = docNo;
            this.docDate = docDate;
            this.workType = workType;
            this.replacementType = replacementType;
            this.uploaded = uploaded;
            this.Status = status;
            this.salesNo = salesNo;
            this.agent = agent;
            this.debtorCode = debtorCode;
        }

        public JobSheet(String docType, String docNo, String docDate, String debtorCode,
                        String debtorName, String debtorName2, String agent, String taxType,
                        String phone, String fax, String attention,String address1, String address2,
                        String address3, String address4, String remarks, String remarks2, String remarks3,
                        String remarks4, String workType, String replacementType, String timeIn, String timeOut, String problem, String resolution, String jobSheetRemarks,
                        String salesNo, double totalEx, double discount, double totalTax, double totalIn, String createdTimeStamp, String createdUser, String lastModifiedDateTime, String lastModifiedUser, String signature, String image) {
            this.docType = docType;
            this.docNo = docNo;
            this.docDate = docDate;
            this.debtorCode = debtorCode;
            this.debtorName = debtorName;
            this.debtorName2 = debtorName2;
            this.agent = agent;
            this.taxType = taxType;
            this.uploaded = 0;
            this.phone = phone;
            this.fax = fax;
            this.attention = attention;
            this.address1 = address1;
            this.address2 = address2;
            this.address3 = address3;
            this.address4 = address4;
            this.Remarks = remarks;
            this.Remarks2 = remarks2;
            this.Remarks3 = remarks3;
            this.Remarks4 = remarks4;
            this.workType = workType;
            this.replacementType = replacementType;
            this.timeIn = timeIn;
            this.timeOut = timeOut;
            this.problem = problem;
            this.resolution = resolution;
            this.jobSheetRemarks = jobSheetRemarks;
            this.salesNo = salesNo;
            this.TotalEx = totalEx;
            this.Discount = discount;
            this.TotalTax = totalTax;
            this.TotalIn = totalIn;
            this.CreatedTimeStamp = createdTimeStamp;
            this.CreatedUser = createdUser;
            this.LastModifiedDateTime = lastModifiedDateTime;
            this.LastModifiedUser = lastModifiedUser;
            this.Signature = signature;
            this.Image = image;
            jobSheetDetailsList = new ArrayList<>();
        }


        protected JobSheet(Parcel in) {
            docType = in.readString();
            docNo = in.readString();
            docDate = in.readString();
            debtorCode = in.readString();
            debtorName = in.readString();
            debtorName2 = in.readString();
            agent = in.readString();
            taxType = in.readString();
            uploaded = in.readInt();
            phone = in.readString();
            fax = in.readString();
            attention = in.readString();
            address1 = in.readString();
            address2 = in.readString();
            address3 = in.readString();
            address4 = in.readString();
            Remarks = in.readString();
            Status = in.readString();
            Remarks2 = in.readString();
            Remarks3 = in.readString();
            Remarks4 = in.readString();
            workType = in.readString();
            replacementType = in.readString();
            timeIn = in.readString();
            timeOut = in.readString();
            problem = in.readString();
            resolution = in.readString();
            jobSheetRemarks = in.readString();
            salesNo = in.readString();
            if (in.readByte() == 0) {
                TotalEx = null;
            } else {
                TotalEx = in.readDouble();
            }
            if (in.readByte() == 0) {
                Discount = null;
            } else {
                Discount = in.readDouble();
            }
            if (in.readByte() == 0) {
                TotalTax = null;
            } else {
                TotalTax = in.readDouble();
            }
            if (in.readByte() == 0) {
                TotalIn = null;
            } else {
                TotalIn = in.readDouble();
            }
            CreatedTimeStamp = in.readString();
            CreatedUser = in.readString();
            LastModifiedDateTime = in.readString();
            LastModifiedUser = in.readString();
            Signature = in.readString();
            Image = in.readString();
            jobSheetDetailsList = new ArrayList<>();
            in.readTypedList(jobSheetDetailsList, JobSheetDetails.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(docType);
            dest.writeString(docNo);
            dest.writeString(docDate);
            dest.writeString(debtorCode);
            dest.writeString(debtorName);
            dest.writeString(debtorName2);
            dest.writeString(agent);
            dest.writeString(taxType);
            dest.writeInt(uploaded);
            dest.writeString(phone);
            dest.writeString(fax);
            dest.writeString(attention);
            dest.writeString(address1);
            dest.writeString(address2);
            dest.writeString(address3);
            dest.writeString(address4);
            dest.writeString(Remarks);
            dest.writeString(Status);
            dest.writeString(Remarks2);
            dest.writeString(Remarks3);
            dest.writeString(Remarks4);
            dest.writeString(workType);
            dest.writeString(replacementType);
            dest.writeString(timeIn);
            dest.writeString(timeOut);
            dest.writeString(problem);
            dest.writeString(resolution);
            dest.writeString(jobSheetRemarks);
            dest.writeString(salesNo);
            if (TotalEx == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TotalEx);
            }
            if (Discount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Discount);
            }
            if (TotalTax == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TotalTax);
            }
            if (TotalIn == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TotalIn);
            }
            dest.writeString(CreatedTimeStamp);
            dest.writeString(CreatedUser);
            dest.writeString(LastModifiedDateTime);
            dest.writeString(LastModifiedUser);
            dest.writeString(Signature);
            dest.writeString(Image);
            dest.writeTypedList((List<JobSheetDetails>) jobSheetDetailsList);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<JobSheet> CREATOR = new Creator<JobSheet>() {
            @Override
            public JobSheet createFromParcel(Parcel in) {
                return new JobSheet(in);
            }

            @Override
            public JobSheet[] newArray(int size) {
                return new JobSheet[size];
            }
        };

        @Bindable
        public String getDocType() {
            return docType;
        }

        public void setDocType(String docType) {
            this.docType = docType;
        }

        @Bindable
        public String getDocNo() {
            return docNo;
        }

        public void setDocNo(String docNo) {
            this.docNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDocDate() {
            return docDate;
        }

        public void setDocDate(String docDate) {
            this.docDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getDebtorCode() {
            return debtorCode;
        }

        public void setDebtorCode(String debtorCode) {
            this.debtorCode = debtorCode;
            notifyPropertyChanged(BR.debtorCode);
        }

        @Bindable
        public String getDebtorName() {
            return debtorName;
        }

        public void setDebtorName(String debtorName) {
            this.debtorName = debtorName;
            notifyPropertyChanged(BR.debtorName);
        }

        @Bindable
        public String getDebtorName2() {
            return debtorName2;
        }

        public void setDebtorName2(String debtorName2) {
            this.debtorName2 = debtorName2;
            notifyPropertyChanged(BR.debtorName);
        }

        @Bindable
        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
            notifyPropertyChanged(BR.agent);
        }

        @Bindable
        public String getTaxType() {
            return taxType;
        }

        public void setTaxType(String taxType) {
            this.taxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
            notifyPropertyChanged(BR.phone);
        }

        @Bindable
        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
            notifyPropertyChanged(BR.fax);
        }

        @Bindable
        public String getAttention() {
            return attention;
        }

        public void setAttention(String attention) {
            this.attention = attention;
            notifyPropertyChanged(BR.attention);
        }

        @Bindable
        public Integer getUploaded() {
            return uploaded;
        }

        public void setUploaded(Integer uploaded) {
            this.uploaded = uploaded;
            notifyPropertyChanged(BR.uploaded);
        }

        @Bindable
        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1FP) {
            address1 = address1FP;
            notifyPropertyChanged(BR.address1);
        }

        @Bindable
        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2FP) {
            address2 = address2FP;
            notifyPropertyChanged(BR.address2);
        }

        @Bindable
        public String getAddress3() {
            return address3;
        }

        public void setAddress3(String address3FP) {
            address3 = address3FP;
            notifyPropertyChanged(BR.address3);
        }

        @Bindable
        public String getAddress4() { return address4; }

        public void setAddress4(String address4FP) {
            address4 = address4FP;
            notifyPropertyChanged(BR.address4);
        }

        @Bindable
        public String getRemarks() { return Remarks; }

        public void setRemarks(String remarksFP) {
            Remarks = remarksFP;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getRemarks2() { return Remarks2; }

        public void setRemarks2(String remarks2FP) {
            Remarks2 = remarks2FP;
            notifyPropertyChanged(BR.remarks2);
        }

        @Bindable
        public String getRemarks3() { return Remarks3; }

        public void setRemarks3(String remarks3FP) {
            Remarks3 = remarks3FP;
            notifyPropertyChanged(BR.remarks3);
        }

        @Bindable
        public String getRemarks4() { return Remarks4; }

        public void setRemarks4(String remarks4FP) {
            Remarks4 = remarks4FP;
            notifyPropertyChanged(BR.remarks4);
        }

        @Bindable
        public String getStatus() { return Status; }

        public void setStatus(String statusFP) {
            Status = statusFP;
            notifyPropertyChanged(BR.status);
        }
        @Bindable
        public String getWorkType() {
            return workType;
        }

        public void setWorkType(String workType) {
            this.workType = workType;
        }
        @Bindable
        public String getReplacementType() {
            return replacementType;
        }

        public void setReplacementType(String replacementType) {
            this.replacementType = replacementType;
        }
        @Bindable
        public String getTimeIn() {
            return timeIn;
        }

        public void setTimeIn(String timeIn) {
            this.timeIn = timeIn;
        }
        @Bindable
        public String getTimeOut() {
            return timeOut;
        }

        public void setTimeOut(String timeOut) {
            this.timeOut = timeOut;
        }
        @Bindable
        public String getProblem() {
            return problem;
        }

        public void setProblem(String problem) {
            this.problem = problem;
        }
        @Bindable
        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }
        @Bindable
        public String getJobSheetRemarks() {
            return jobSheetRemarks;
        }

        public void setJobSheetRemarks(String jobSheetRemarks) {
            this.jobSheetRemarks = jobSheetRemarks;
        }
        @Bindable
        public String getSalesNo() {
            return salesNo;
        }

        public void setSalesNo(String salesNo) {
            this.salesNo = salesNo;
        }
        @Bindable
        public Double getTotalEx() {
            return TotalEx;
        }

        public void setTotalEx(Double totalEx) {
            TotalEx = totalEx;
        }

        @Bindable
        public Double getDiscount() {
            return Discount;
        }

        public void setDiscount(Double discount) {
            Discount = discount;
        }

        @Bindable
        public Double getTotalTax() {
            return TotalTax;
        }

        public void setTotalTax(Double totalTax) {
            TotalTax = totalTax;
        }
        @Bindable
        public Double getTotalIn() {
            return TotalIn;
        }

        public void setTotalIn(Double totalIn) {
            TotalIn = totalIn;
        }
        @Bindable
        public String getCreatedTimeStamp() {
            return CreatedTimeStamp;
        }

        public void setCreatedTimeStamp(String createdTimeStamp) {
            CreatedTimeStamp = createdTimeStamp;
        }
        @Bindable
        public String getCreatedUser() {
            return CreatedUser;
        }

        public void setCreatedUser(String createdUser) {
            CreatedUser = createdUser;
        }
        @Bindable
        public String getLastModifiedDateTime() {
            return LastModifiedDateTime;
        }

        public void setLastModifiedDateTime(String lastModifiedDateTime) {
            LastModifiedDateTime = lastModifiedDateTime;
        }

        public String getLastModifiedUser() {
            return LastModifiedUser;
        }

        public void setLastModifiedUser(String lastModifiedUser) {
            LastModifiedUser = lastModifiedUser;
        }

        @Bindable
        public String getSignature() {
            return Signature;
        }

        public void setSignature(String signature) {
            Signature = signature;
        }

        @Bindable
        public String getImage() {
            return Image;
        }

        public void setImage(String image) {
            Image = image;
        }

        @Bindable
        public List<AC_Class.JobSheetDetails> getJobSheetDetailsList() { return jobSheetDetailsList; }

        public void setJobSheetDetailsList(List<JobSheetDetails> jobSheetDetailsList) {
            this.jobSheetDetailsList = jobSheetDetailsList;
//            notifyPropertyChanged(BR.);
        }

        public void addJobSheetDetail(JobSheetDetails jobSheetDetails) {
            jobSheetDetailsList.add(jobSheetDetails);
        }

        public void removeJobSheetDetail(int index) {
            jobSheetDetailsList.remove(index);
        }

        public AC_Class.JobSheetDetails getJobSheetDetail(int id) {
            int i;
            for (i=0; i<jobSheetDetailsList.size(); i++) {
                if (jobSheetDetailsList.get(i).getID()==id) {
                    return jobSheetDetailsList.get(i);
                }
            }
            return null;
        }
    }
    public static class JobSheetDetails extends BaseObservable implements Parcelable {
        public Integer ID;
        public String DocNo;
        public String DocDate;
        public String Location;
        public String ItemCode;
        public String ItemCodeInput;
        public String ItemDescription;
        public String UOM;
        public Double Quantity;
        public Double UPrice;
        public Double Discount;
        public Double SubTotal;
        public String TaxType;
        public Double TaxRate;
        public Double TaxValue;
        public Double Total_Ex;
        public Double Total_In;
        public String Line_No;
        public String Remarks;
        public String BatchNo;
        public String Remarks2;

        public JobSheetDetails() {
            Quantity = Double.valueOf(1);
            Discount = Double.valueOf(0);
            SubTotal = Double.valueOf(0);
            UPrice = Double.valueOf(0);
            TaxRate = Double.valueOf(0);
            TaxValue = Double.valueOf(0);
            Total_In = Double.valueOf(0);
            Total_Ex = Double.valueOf(0);
            setItemCode(null);
            setQuantity(Double.valueOf(1));
            setDiscount(Double.valueOf(0));
            setRemarks(null);
            setRemarks2(null);
        }

        public JobSheetDetails(Integer id, String docNo, String location, String itemCode, String itemDescription, String UOM, Double quantity, Double UPrice, Double discount, Double subTotal, String taxType, Double taxRate, Double taxValue, Double total_Ex, Double total_In, String line_no, String remarks, String batchno, String remarks2) {
            ID = id;
            DocNo = docNo;
            Location = location;
            ItemCode = itemCode;
            ItemDescription = itemDescription;
            this.UOM = UOM;
            Quantity = quantity;
            this.UPrice = UPrice;
            Discount = discount;
            SubTotal = subTotal;
            TaxType = taxType;
            TaxRate = taxRate;
            TaxValue = taxValue;
            Total_Ex = total_Ex;
            Total_In = total_In;
            Line_No = line_no;
            Remarks = remarks;
            BatchNo = batchno;
            Remarks2 = remarks2;
        }

        protected JobSheetDetails(Parcel in) {
            if (in.readByte() == 0) {
                ID = null;
            } else {
                ID = in.readInt();
            }
            DocNo = in.readString();
            Location = in.readString();
            ItemCode = in.readString();
            ItemDescription = in.readString();
            if (in.readByte() == 0) {
                Quantity = null;
            } else {
                Quantity = in.readDouble();
            }
            if (in.readByte() == 0) {
                UOM = null;
            } else {
                UOM = in.readString();
            }
            if (in.readByte() == 0) {
                UPrice = null;
            } else {
                UPrice = in.readDouble();
            }
            if (in.readByte() == 0) {
                Discount = null;
            } else {
                Discount = in.readDouble();
            }
            if (in.readByte() == 0) {
                SubTotal = null;
            } else {
                SubTotal = in.readDouble();
            }
            TaxType = in.readString();
            if (in.readByte() == 0) {
                TaxRate = null;
            } else {
                TaxRate = in.readDouble();
            }
            if (in.readByte() == 0) {
                TaxValue = null;
            } else {
                TaxValue = in.readDouble();
            }
            if (in.readByte() == 0) {
                Total_Ex = null;
            } else {
                Total_Ex = in.readDouble();
            }
            if (in.readByte() == 0) {
                Total_In = null;
            } else {
                Total_In = in.readDouble();
            }
            Line_No = in.readString();
            Remarks = in.readString();
            BatchNo = in.readString();
            Remarks2 = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (ID == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(ID);
            }
            dest.writeString(DocNo);
            dest.writeString(Location);
            dest.writeString(ItemCode);
            dest.writeString(ItemDescription);

            if (Quantity == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Quantity);
            }
            if (UOM == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeString(UOM);
            }
            if (UPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(UPrice);
            }
            if (Discount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Discount);
            }
            if (SubTotal == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(SubTotal);
            }
            dest.writeString(TaxType);
            if (TaxRate == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TaxRate);
            }
            if (TaxValue == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(TaxValue);
            }
            if (Total_Ex == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Total_Ex);
            }
            if (Total_In == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(Total_In);
            }
            dest.writeString(Line_No);
            dest.writeString(Remarks);
            dest.writeString(BatchNo);
            dest.writeString(Remarks2);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<JobSheetDetails> CREATOR = new Creator<JobSheetDetails>() {
            @Override
            public JobSheetDetails createFromParcel(Parcel in) {
                return new JobSheetDetails(in);
            }

            @Override
            public JobSheetDetails[] newArray(int size) {
                return new JobSheetDetails[size];
            }
        };

        @Bindable
        public Integer getID() {
            return ID;
        }

        public void setID(Integer ID) {
            this.ID = ID;
            notifyPropertyChanged(BR.iD);
        }

        @Bindable
        public String getDocNo() {
            return DocNo;
        }

        public void setDocNo(String docNo) {
            DocNo = docNo;
            notifyPropertyChanged(BR.docNo);
        }

        @Bindable
        public String getDocDate() {
            return DocDate;
        }

        public void setDocDate(String docDate) {

            DocDate = docDate;
            notifyPropertyChanged(BR.docDate);
        }

        @Bindable
        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
            notifyPropertyChanged(BR.location);
        }

        @Bindable
        public String getItemCode() {
            return ItemCode;
        }

        public void setItemCode(String itemCode) {
            ItemCode = itemCode;
            notifyPropertyChanged(BR.itemCode);
        }

        @Bindable
        public String getItemCodeInput() {
            return ItemCodeInput;
        }

        public void setItemCodeInput(String itemCodeInput) {
            ItemCodeInput = itemCodeInput;
            notifyPropertyChanged(BR.itemCodeInput);
        }


        @Bindable
        public String getItemDescription() {
            return ItemDescription;
        }

        public void setItemDescription(String itemDescription) {
            ItemDescription = itemDescription;
            notifyPropertyChanged(BR.itemDescription);
        }

        @Bindable
        public String getUOM() {
            return UOM;
        }

        public void setUOM(String uom) {
            UOM = uom;
            notifyPropertyChanged(BR.uOM);
        }

        @Bindable
        public Double getQuantity() {
            return Quantity;
        }

        public void setQuantity(Double quantity) {
            Quantity = quantity;
            notifyPropertyChanged(BR.quantity);
        }

        @Bindable
        public Double getUPrice() {
            return UPrice;
        }

        public void setUPrice(Double UPrice) {
            this.UPrice = UPrice;
            notifyPropertyChanged(BR.uPrice);
        }

        @Bindable
        public Double getDiscount() {
            return Discount;
        }

        public void setDiscount(Double discount) {
            Discount = discount;
            notifyPropertyChanged(BR.discount);
        }

        @Bindable
        public Double getSubTotal() {
            return SubTotal;
        }

        public void setSubTotal(Double subTotal) {
            SubTotal = subTotal;
            notifyPropertyChanged(BR.subTotal);
        }

        @Bindable
        public String getTaxType() {
            return TaxType;
        }

        public void setTaxType(String taxType) {
            TaxType = taxType;
            notifyPropertyChanged(BR.taxType);
        }

        @Bindable
        public Double getTaxRate() {
            return TaxRate;
        }

        public void setTaxRate(Double taxRate) {
            TaxRate = taxRate;
            notifyPropertyChanged(BR.taxRate);
        }

        @Bindable
        public Double getTaxValue() {
            return TaxValue;
        }

        public void setTaxValue(Double taxValue) {
            TaxValue = taxValue;
            notifyPropertyChanged(BR.taxValue);
        }

        @Bindable
        public Double getTotal_Ex() {
            return Total_Ex;
        }

        public void setTotal_Ex(Double total_Ex) {
            Total_Ex = total_Ex;
            notifyPropertyChanged(BR.total_Ex);
        }

        @Bindable
        public Double getTotal_In() {
            return Total_In;
        }

        public void setTotal_In(Double total_In) {
            Total_In = total_In;
            notifyPropertyChanged(BR.total_In);
        }

        @Bindable
        public String getLine_No() {
            return Line_No;
        }

        public void setLine_No(String line_No) {
            this.Line_No = line_No;
            notifyPropertyChanged(BR.line_No);
        }

        @Bindable
        public String getRemarks() { return Remarks;
        }

        public void setRemarks(String remarks) {
            this.Remarks = remarks;
            notifyPropertyChanged(BR.remarks);
        }

        @Bindable
        public String getRemarks2() { return Remarks2;
        }

        public void setRemarks2(String remarks2) {
            this.Remarks2 = remarks2;
            notifyPropertyChanged(BR.remarks2);
        }

        @Bindable
        public String getBatchNo() { return BatchNo;
        }

        public void setBatchNo(String batchNo) {
            this.BatchNo = batchNo;
            notifyPropertyChanged(BR.batch_No);
        }
    }


}