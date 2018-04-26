
package com.flutterwave.raveandroid;

import android.os.Parcelable;
import android.os.Parcelable.Creator;
import org.parceler.Generated;
import org.parceler.IdentityCollection;
import org.parceler.ParcelWrapper;
import org.parceler.ParcelerRuntimeException;

@Generated("org.parceler.ParcelAnnotationProcessor")
@SuppressWarnings({
    "unchecked",
    "deprecation"
})
public class RavePayInitializer$$Parcelable
    implements Parcelable, ParcelWrapper<com.flutterwave.raveandroid.RavePayInitializer>
{

    private com.flutterwave.raveandroid.RavePayInitializer ravePayInitializer$$0;
    @SuppressWarnings("UnusedDeclaration")
    public final static Creator<RavePayInitializer$$Parcelable>CREATOR = new Creator<RavePayInitializer$$Parcelable>() {


        @Override
        public RavePayInitializer$$Parcelable createFromParcel(android.os.Parcel parcel$$2) {
            return new RavePayInitializer$$Parcelable(read(parcel$$2, new IdentityCollection()));
        }

        @Override
        public RavePayInitializer$$Parcelable[] newArray(int size) {
            return new RavePayInitializer$$Parcelable[size] ;
        }

    }
    ;

    public RavePayInitializer$$Parcelable(com.flutterwave.raveandroid.RavePayInitializer ravePayInitializer$$2) {
        ravePayInitializer$$0 = ravePayInitializer$$2;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$0, int flags) {
        write(ravePayInitializer$$0, parcel$$0, flags, new IdentityCollection());
    }

    public static void write(com.flutterwave.raveandroid.RavePayInitializer ravePayInitializer$$1, android.os.Parcel parcel$$1, int flags$$0, IdentityCollection identityMap$$0) {
        int identity$$0 = identityMap$$0 .getKey(ravePayInitializer$$1);
        if (identity$$0 != -1) {
            parcel$$1 .writeInt(identity$$0);
        } else {
            parcel$$1 .writeInt(identityMap$$0 .put(ravePayInitializer$$1));
            parcel$$1 .writeString(ravePayInitializer$$1 .country);
            parcel$$1 .writeString(ravePayInitializer$$1 .lName);
            parcel$$1 .writeDouble(ravePayInitializer$$1 .amount);
            parcel$$1 .writeString(ravePayInitializer$$1 .secretKey);
            parcel$$1 .writeString(ravePayInitializer$$1 .publicKey);
            parcel$$1 .writeInt((ravePayInitializer$$1 .withAccount? 1 : 0));
            parcel$$1 .writeString(ravePayInitializer$$1 .fName);
            parcel$$1 .writeInt((ravePayInitializer$$1 .withCard? 1 : 0));
            parcel$$1 .writeString(ravePayInitializer$$1 .txRef);
            parcel$$1 .writeInt((ravePayInitializer$$1 .allowSaveCard? 1 : 0));
            parcel$$1 .writeString(ravePayInitializer$$1 .meta);
            parcel$$1 .writeString(ravePayInitializer$$1 .narration);
            parcel$$1 .writeString(ravePayInitializer$$1 .currency);
            parcel$$1 .writeInt(ravePayInitializer$$1 .theme);
            parcel$$1 .writeInt((ravePayInitializer$$1 .staging? 1 : 0));
            parcel$$1 .writeString(ravePayInitializer$$1 .email);
        }
    }

    @Override
    public int describeContents() {
        return  0;
    }

    @Override
    public com.flutterwave.raveandroid.RavePayInitializer getParcel() {
        return ravePayInitializer$$0;
    }

    public static com.flutterwave.raveandroid.RavePayInitializer read(android.os.Parcel parcel$$3, IdentityCollection identityMap$$1) {
        int identity$$1 = parcel$$3 .readInt();
        if (identityMap$$1 .containsKey(identity$$1)) {
            if (identityMap$$1 .isReserved(identity$$1)) {
                throw new ParcelerRuntimeException("An instance loop was detected whild building Parcelable and deseralization cannot continue.  This error is most likely due to using @ParcelConstructor or @ParcelFactory.");
            }
            return identityMap$$1 .get(identity$$1);
        } else {
            com.flutterwave.raveandroid.RavePayInitializer ravePayInitializer$$4;
            int reservation$$0 = identityMap$$1 .reserve();
            ravePayInitializer$$4 = new com.flutterwave.raveandroid.RavePayInitializer();
            identityMap$$1 .put(reservation$$0, ravePayInitializer$$4);
            ravePayInitializer$$4 .country = parcel$$3 .readString();
            ravePayInitializer$$4 .lName = parcel$$3 .readString();
            ravePayInitializer$$4 .amount = parcel$$3 .readDouble();
            ravePayInitializer$$4 .secretKey = parcel$$3 .readString();
            ravePayInitializer$$4 .publicKey = parcel$$3 .readString();
            ravePayInitializer$$4 .withAccount = (parcel$$3 .readInt() == 1);
            ravePayInitializer$$4 .fName = parcel$$3 .readString();
            ravePayInitializer$$4 .withCard = (parcel$$3 .readInt() == 1);
            ravePayInitializer$$4 .txRef = parcel$$3 .readString();
            ravePayInitializer$$4 .allowSaveCard = (parcel$$3 .readInt() == 1);
            ravePayInitializer$$4 .meta = parcel$$3 .readString();
            ravePayInitializer$$4 .narration = parcel$$3 .readString();
            ravePayInitializer$$4 .currency = parcel$$3 .readString();
            ravePayInitializer$$4 .theme = parcel$$3 .readInt();
            ravePayInitializer$$4 .staging = (parcel$$3 .readInt() == 1);
            ravePayInitializer$$4 .email = parcel$$3 .readString();
            com.flutterwave.raveandroid.RavePayInitializer ravePayInitializer$$3 = ravePayInitializer$$4;
            identityMap$$1 .put(identity$$1, ravePayInitializer$$3);
            return ravePayInitializer$$3;
        }
    }

}
