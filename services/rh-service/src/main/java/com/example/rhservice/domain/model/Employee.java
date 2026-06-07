package com.example.rhservice.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"shift", "payrolls"})
@Entity
@Table(name = "employees")
/**
 * Employee domain entity.
 *
 * <p>Inherits common user properties from {@link User} (demonstrates inheritance). This class adds
 * employee-specific state such as working hours, salary and relations to {@link Shift} and {@link Payroll}.
 *
 * Design notes:
 * - Uses {@code @ManyToOne} for assigned {@link Shift} (an employee belongs to one shift).
 * - Uses {@code @OneToMany} for payroll history; cascade rules keep payroll rows in sync with the employee.
 * - Lombok generates equals/hashCode and accessor methods. Excluding heavy relations from {@code toString()} avoids recursion.
 */
public class Employee extends User {

    /** Total monthly working hours. Generated accessor: {@code getMonthlyHours()}. */
    private double monthlyHours;

    /** Base salary for the employee. */
    private double salary;

    /** Assigned shift for this employee. Use {@code getShift()} / {@code setShift(Shift)}. */
    @ManyToOne
    private Shift shift;

    /** Payroll history for the employee. Managed as part of the aggregate (one-to-many).
     *  The collection is initialized to avoid null checks.
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payroll> payrolls = new ArrayList<>();
}