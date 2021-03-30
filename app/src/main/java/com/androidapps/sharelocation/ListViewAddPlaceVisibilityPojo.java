package com.androidapps.sharelocation;

public class ListViewAddPlaceVisibilityPojo {

    public boolean isHomeAvailable() {
        return isHomeAvailable;
    }

    public void setHomeAvailable(boolean homeAvailable) {
        isHomeAvailable = homeAvailable;
    }

    public boolean isWorkAvailable() {
        return isWorkAvailable;
    }

    public void setWorkAvailable(boolean workAvailable) {
        isWorkAvailable = workAvailable;
    }

    public boolean isGroceryAvailable() {
        return isGroceryAvailable;
    }

    public void setGroceryAvailable(boolean groceryAvailable) {
        isGroceryAvailable = groceryAvailable;
    }

    public boolean isSchoolAvailable() {
        return isSchoolAvailable;
    }

    public void setSchoolAvailable(boolean schoolAvailable) {
        isSchoolAvailable = schoolAvailable;
    }


    boolean isHomeAvailable = false;
    boolean isWorkAvailable = false;
    boolean isGroceryAvailable = false;
    boolean isSchoolAvailable = false;


}
