package com.example.rhservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "employee")
@EqualsAndHashCode(exclude = "employee")
@Entity
@Table(name = "payrolls")
/**
 * Payroll record entity for an employee.
 *
 * <p>Contains month/year, paid status and an amount. Linked to {@link Employee} via {@code @ManyToOne}.
 * The class intentionally excludes {@code employee} from {@code toString()} and equals/hashCode to avoid cycles.
 */
public class Payroll {

    /** Database id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Payroll month label (e.g. "June"). */
    @Column(name = "payroll_month")
    private String month;

    /** Payroll year (e.g. "2026"). */
    @Column(name = "payroll_year")
    private String year;

    /** Whether the payroll was paid. */
    private boolean paid;

    /** Amount paid or due. */
    private double amount;

    /** Owning employee for this payroll entry. */
    @ManyToOne
    private Employee employee;
}