package com.food.ordering.system.order.service.domain.valueobject;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class StreetAddress  {
    private UUID id;
    private String street;
    private  String postalCode;
    private  String city;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreetAddress that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(street, that.street) && Objects.equals(postalCode, that.postalCode) && Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, postalCode, city);
    }
}
