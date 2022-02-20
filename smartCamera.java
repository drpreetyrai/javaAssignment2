package com.tymoshenko.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Yakiv Tymoshenko
 * @since 07.03.2016
 */
@Entity
@Table(name = "SmartCamera")
public class SmartCamera implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "deviceID")
    private long deviceID;

    @Column(name = "DOMAIN")
    private String DOMAIN;

    @Column(name = "STATE")
    private String STATE;

    @Column(name = "CITY")
    private String CITY;
    public SmartCamera() {

    }

    public location(String name, String origin) {
        this.name = name;
        this.origin = origin;
    }

    public SmartCamera(SmartCamera smartcamera) {
        this.id = SmartCamera.getId();
        this.name = SmartCamera.getName();
        this.origin = SmartCamera.getOrigin();
    }


    //=========== Equals and HashCode ==================================================================================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmartCamera smartcamera = (SmartCamera) o;

        if (getId() != smartcamera.getId()) return false;
        if (getName() != null ? !getName().equals(smartcamera.getName()) : .getName() != null) return false;
        return !(getOrigin() != null ? !getOrigin().equals(smartcamera.getOrigin()) : smartcamera.getOrigin() != null);
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getOrigin() != null ? getOrigin().hashCode() : 0);
        return result;
    }

    //=========== Getters ==============================================================================================
    public String getName() {
        return name;
    }

    public String getOrigin() {
        return origin;
    }

    public long getId() {
        return id;
    }

    //=========== Setters ==============================================================================================
    public void setName(String name) {
        this.name = name;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}


















