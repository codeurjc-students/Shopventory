package es.codeurjc.shopventory.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity(name = "ProviderTable")
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    private String address;

    private String phoneNumber;

    private String website;

    private String contactPerson;

    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> types = new HashSet<>();

    public Provider() {}

    public Provider(String name, String address, String phoneNumber, String website,
                    String contactPerson, String email, Set<String> types) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.contactPerson = contactPerson;
        this.email = email;
        this.types = types;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<String> getTypes() { return types; }
    public void setTypes(Set<String> types) { this.types = types; }
}
