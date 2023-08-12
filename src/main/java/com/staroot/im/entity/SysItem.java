package com.staroot.im.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sysitem")
@Data // This annotation includes @Getter, @Setter, @EqualsAndHashCode, and @ToString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
public class SysItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String parent;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String datatype;

    @Column(nullable = false)
    private int seq;
}
