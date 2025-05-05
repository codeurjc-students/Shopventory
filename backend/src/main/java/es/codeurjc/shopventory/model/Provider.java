package es.codeurjc.shopventory.model;

import java.util.Set;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "ProviderTable")
public class Provider {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    private String address;

    private String phoneNumber;

    private String webSite;

    private String contactPerson;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> type;

    public Provider() {
    }

    public Provider(String name, String address, String phoneNumber, String webSite, String contactPerson,
            Set<String> type) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.webSite = webSite;
        this.contactPerson = contactPerson;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Set<String> getType() {
        return type;
    }

    public void setType(Set<String> type) {
        this.type = type;
    }



}
