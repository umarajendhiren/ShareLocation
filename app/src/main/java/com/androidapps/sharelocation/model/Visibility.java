package com.androidapps.sharelocation.model;

public class Visibility {

    int NameGroup;
    int passwordGroup;

    public int getNameGroup() {
        return NameGroup;
    }

    public void setNameGroup(int nameGroup) {
        NameGroup = nameGroup;
    }

    public int getPasswordGroup() {
        return passwordGroup;
    }

    public void setPasswordGroup(int passwordGroup) {
        this.passwordGroup = passwordGroup;
    }

    public int getDeleteGroup() {
        return deleteGroup;
    }

    public void setDeleteGroup(int deleteGroup) {
        this.deleteGroup = deleteGroup;
    }

    public int getLeaveGroup() {
        return leaveGroup;
    }

    public void setLeaveGroup(int leaveGroup) {
        this.leaveGroup = leaveGroup;
    }

    public int getPhoneGroup() {
        return phoneGroup;
    }

    public void setPhoneGroup(int phoneGroup) {
        this.phoneGroup = phoneGroup;
    }

    int deleteGroup;
    int leaveGroup;
    int phoneGroup;

}
