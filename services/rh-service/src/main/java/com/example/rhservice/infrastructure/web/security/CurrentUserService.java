package com.example.rhservice.infrastructure.web.security;

import com.example.rhservice.application.port.out.UserLookupPort;
import com.example.rhservice.domain.model.Admin;
import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.domain.model.Payroll;
import com.example.rhservice.domain.model.Shift;
import com.example.rhservice.domain.model.User;
import com.example.rhservice.domain.model.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class CurrentUserService {

    private final UserLookupPort userLookupPort;

    public CurrentUserService(UserLookupPort userLookupPort) {
        this.userLookupPort = userLookupPort;
    }

    public User currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        return userLookupPort.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));
    }

    public boolean isAdmin(Authentication authentication) {
        return currentUser(authentication).getRole() == UserRole.ADMIN;
    }

    public void requireSelfOrAdmin(Authentication authentication, Long userId) {
        User user = currentUser(authentication);
        if (user.getRole() != UserRole.ADMIN && !user.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to access this profile");
        }
    }

    public void requirePayrollAccess(Authentication authentication, Payroll payroll) {
        User user = currentUser(authentication);
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        if (!(user instanceof Employee employee) || payroll.getEmployee() == null || !payroll.getEmployee().getId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to access this payroll");
        }
    }

    public void requireShiftAccess(Authentication authentication, Shift shift) {
        User user = currentUser(authentication);
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        if (!(user instanceof Employee employee) || employee.getShift() == null || !employee.getShift().getId().equals(shift.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to access this shift");
        }
    }

    public List<Payroll> filterPayrolls(Authentication authentication, List<Payroll> payrolls) {
        User user = currentUser(authentication);
        if (user.getRole() == UserRole.ADMIN) {
            return payrolls;
        }

        if (!(user instanceof Employee employee)) {
            return List.of();
        }

        return payrolls.stream()
                .filter(payroll -> payroll.getEmployee() != null && payroll.getEmployee().getId().equals(employee.getId()))
                .toList();
    }

    public List<Shift> filterShifts(Authentication authentication, List<Shift> shifts) {
        User user = currentUser(authentication);
        if (user.getRole() == UserRole.ADMIN) {
            return shifts;
        }

        if (!(user instanceof Employee employee) || employee.getShift() == null) {
            return List.of();
        }

        return shifts.stream()
                .filter(shift -> shift.getId().equals(employee.getShift().getId()))
                .toList();
    }

    public void requireAdmin(Authentication authentication) {
        if (!isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }
}