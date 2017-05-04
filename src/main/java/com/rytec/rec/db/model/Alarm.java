package com.rytec.rec.db.model;

import java.util.Date;

public class Alarm {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column alarm.id
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column alarm.device
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    private Integer device;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column alarm.sig
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    private Integer sig;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column alarm.value
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    private String value;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column alarm.time
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    private Date time;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column alarm.id
     *
     * @return the value of alarm.id
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column alarm.id
     *
     * @param id the value for alarm.id
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column alarm.device
     *
     * @return the value of alarm.device
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    public Integer getDevice() {
        return device;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column alarm.device
     *
     * @param device the value for alarm.device
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    public void setDevice(Integer device) {
        this.device = device;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column alarm.sig
     *
     * @return the value of alarm.sig
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    public Integer getSig() {
        return sig;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column alarm.sig
     *
     * @param sig the value for alarm.sig
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    public void setSig(Integer sig) {
        this.sig = sig;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column alarm.value
     *
     * @return the value of alarm.value
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    public String getValue() {
        return value;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column alarm.value
     *
     * @param value the value for alarm.value
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column alarm.time
     *
     * @return the value of alarm.time
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    public Date getTime() {
        return time;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column alarm.time
     *
     * @param time the value for alarm.time
     *
     * @mbggenerated Thu Apr 20 09:12:15 CST 2017
     */
    public void setTime(Date time) {
        this.time = time;
    }
}