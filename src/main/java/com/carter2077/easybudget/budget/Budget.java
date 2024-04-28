package com.carter2077.easybudget.budget;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "budget")
@Getter @Setter @NoArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(generator="budget_gen")
    @SequenceGenerator(name="budget_gen", sequenceName="budget_seq", allocationSize=1)
    @Setter(AccessLevel.PROTECTED)
    private long id;

    @Column(name = "name")
    private String name;

    public Budget(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Budget [id=" + id + ", name=" + name + "]";
    }
}