package com.example.rhservice.domain.model;

import jakarta.persistence.Entity;
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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "admins")
/**
 * Admin domain entity.
 *
 * <p>Extends {@link User} to reuse shared user fields and adds admin-specific information.
 * This demonstrates simple inheritance and specialization: an Admin IS-A User with extra data.
 */
public class Admin extends User {

    /** A comma-separated or structured representation of permissions for this admin.
     *  In a richer model this might be a separate collection or enum.
     */
    private String permissions;
}