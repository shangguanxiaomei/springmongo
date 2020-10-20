package p1.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Address {
    String houseNumber;
    String street;
    String city;
    String country;
}