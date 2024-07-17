package id.aegis.authentication.controller;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.aegis.authentication.model.CustomResponse;
import id.aegis.authentication.model.Organization;
import id.aegis.authentication.services.OrganizationService;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @PostMapping("/create")
    public CustomResponse<Organization> createOrganization(@RequestBody Organization organization, @AuthenticationPrincipal UserDetails userDetails) {
        Organization savedOrganization = organizationService.save(organization, userDetails);
        return new CustomResponse<>(savedOrganization, 201, "Organization created successfully");
    }

    @GetMapping("/get-all")
    public CustomResponse<List<Organization>> getAllOrganizations(@AuthenticationPrincipal UserDetails userDetails) {
        List<Organization> organizations = organizationService.findOrganizationsForUser(userDetails);
        return new CustomResponse<>(organizations, 200, "Organizations retrieved successfully");
    }

    @GetMapping("get-data-by-id/{id}")
    public CustomResponse<Organization> getOrganizationById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Organization organization = organizationService.findById(id);
            if (!organizationService.hasAccess(userDetails, organization)) {
                return new CustomResponse<>(null, 403, "Access denied");
            }
            return new CustomResponse<>(organization, 200, "Organization retrieved successfully");
        } catch (EntityNotFoundException e) {
            return new CustomResponse<>(null, 404, e.getMessage());
        }
    }

    @PutMapping("/edit")
    public CustomResponse<Organization> updateOrganization(@RequestBody Organization updatedOrganization, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Organization savedOrganization = organizationService.updateOrganization(updatedOrganization, userDetails);
            return new CustomResponse<>(savedOrganization, 200, "Organization updated successfully");
        } catch (EntityNotFoundException e) {
            return new CustomResponse<>(null, 404, e.getMessage());
        } catch (AccessDeniedException e) {
            return new CustomResponse<>(null, 403, e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    public CustomResponse<Void> deleteOrganization(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            organizationService.markAsDeleted(id, userDetails);
            return new CustomResponse<>(null, 200, "Organization deleted successfully");
        } catch (EntityNotFoundException e) {
            return new CustomResponse<>(null, 404, e.getMessage());
        } catch (AccessDeniedException e) {
            return new CustomResponse<>(null, 403, e.getMessage());
        }
    }
    
}
