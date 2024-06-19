package com.example.wolseytechhr;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class representing the "employee_table" in the Room Database.
 * It has fields for all variables that are stored for the profile section and setters as well.
 * This class is not currently used.
 *
 */
@Entity(tableName = "employee_table")
public class EmployeeEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String firstName = " ";
    public String middleName = " ";
    public String lastName = " ";
    public String address1 = " ";
    public String address2 = " ";
    public String city = " ";
    public String province = " ";
    public String postalCode = " ";
    public String phoneNumber = " ";
    public String cellNumber = " ";
    public String emailAddress = " ";

    /**
     * Default constructor for creating an EmployeeEntity object.
     */
    public EmployeeEntity() {

    }

    //======================= setters ==================================

    /**
     * Setter method for updating the first name.
     *
     * @param firstName The new first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Setter method for updating the middle name.
     *
     * @param middleName The new middle name.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Setter method for updating the last name.
     *
     * @param lastName The new last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Setter method for updating address line 1.
     *
     * @param address1 The new address line 1.
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * Setter method for updating address line 2.
     *
     * @param address2 The new address line 2.
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Setter method for updating the city.
     *
     * @param city The new city.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Setter method for updating the province.
     *
     * @param province The new province.
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Setter method for updating the postal code.
     *
     * @param postalCode The new postal code.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Setter method for updating the phone number.
     *
     * @param phoneNumber The new phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Setter method for updating the cell number.
     *
     * @param cellNumber The new cell number.
     */
    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    /**
     * Setter method for updating the email address.
     *
     * @param emailAddress The new email address.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


}
