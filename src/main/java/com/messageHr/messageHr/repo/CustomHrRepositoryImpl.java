package com.messageHr.messageHr.repo;

import java.util.ArrayList;
import java.util.List;

import com.messageHr.messageHr.dto.Hr;

import jakarta.persistence.*;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

public class CustomHrRepositoryImpl implements CustomHrRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public int deleteByIdsOrNamesOrEmails(List<Integer> ids, List<String> names, List<String> emails) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Hr> delete = cb.createCriteriaDelete(Hr.class);
        Root<Hr> root = delete.from(Hr.class);

        List<Predicate> predicates = new ArrayList<>();

        if (!ids.isEmpty()) {
            predicates.add(root.get("id").in(ids));
        }

        if (!names.isEmpty()) {
            predicates.add(cb.lower(root.get("name")).in(names));
        }

        if (!emails.isEmpty()) {
            predicates.add(cb.lower(root.get("email")).in(emails));
        }

        delete.where(cb.or(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(delete).executeUpdate();
    }
}
