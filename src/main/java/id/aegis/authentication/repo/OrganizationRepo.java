package id.aegis.authentication.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.aegis.authentication.model.Organization;
import id.aegis.authentication.model.User;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Long> {
    List<Organization> findByOrganizationName(String organizationName);

    List<Organization> findByCreatedBy(User user);

    List<Organization> findAllByIsDeletedFalse();
    List<Organization> findByCreatedByAndIsDeletedFalse(User user);
}
