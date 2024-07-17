package id.aegis.authentication.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import id.aegis.authentication.model.Organization;
import id.aegis.authentication.model.User;
import id.aegis.authentication.repo.OrganizationRepo;
import id.aegis.authentication.repo.UserRepo;

@Service
public class OrganizationService {
    
    @Autowired
    private OrganizationRepo organizationRepo;
    @Autowired
    private UserRepo userRepo;

    public Organization save(Organization organization, UserDetails userDetails) {
        Optional<User> userOpt = userRepo.findByEmail(userDetails.getUsername());
        User user = userOpt.get();
        organization.setCreatedBy(user.getId());
        organization.setCreatedAt(new Date());
        return organizationRepo.save(organization);
    }

    public List<Organization> findAll() {
        return organizationRepo.findAllByIsDeletedFalse();
    }

    public List<Organization> findByCreatedBy(User user) {
        return organizationRepo.findByCreatedByAndIsDeletedFalse(user);
    }

    public List<Organization> findOrganizationsForUser(UserDetails userDetails) {
        Optional<User> userOpt = userRepo.findByEmail(userDetails.getUsername());
        User user = userOpt.get();
        if (user.getRoleId() == 1) {
            return organizationRepo.findAllByIsDeletedFalse();
        } else {
            return organizationRepo.findByCreatedByAndIsDeletedFalse(user);
        }
    }

    public Organization findById(Long id) throws EntityNotFoundException {
        return organizationRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Organization not found"));
    }

    public Organization updateOrganization(Organization updatedOrganization, UserDetails userDetails) throws EntityNotFoundException, AccessDeniedException {
        Optional<User> userOpt = userRepo.findByEmail(userDetails.getUsername());
        User user = userOpt.get();
        
        Organization organization = findById(updatedOrganization.getId());
        if (!hasAccess(userDetails, organization)) {
            throw new AccessDeniedException("Access denied");
        }
        organization.setOrganizationName(updatedOrganization.getOrganizationName());
        organization.setAddress(updatedOrganization.getAddress());
        organization.setUpdatedBy(user.getId());
        organization.setUpdatedAt(new Date());
        return organizationRepo.save(organization);
    }

    public void delete(Organization organization) {
        organizationRepo.delete(organization);
    }

    public void markAsDeleted(Long id, UserDetails userDetails) throws EntityNotFoundException, AccessDeniedException {
        Optional<User> userOpt = userRepo.findByEmail(userDetails.getUsername());
        User user = userOpt.get(); 
        Organization organization = findById(id);
        if (!hasAccess(userDetails, organization)) {
            throw new AccessDeniedException("Access denied");
        }
        organization.setUpdatedBy(user.getId());
        organization.setIsDeleted(true);
        organization.setUpdatedAt(new Date());
        organizationRepo.save(organization);
    }

    public boolean hasAccess(UserDetails userDetails, Organization organization) {
        Optional<User> userOpt = userRepo.findByEmail(userDetails.getUsername());
        User user = userOpt.get();
        return user.getRoleId()==1 || organization.getCreatedBy().equals(user.getId());
    }
}
