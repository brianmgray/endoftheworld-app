package com.endoftheworldimprov.service.impl;

import com.endoftheworldimprov.model.domain.Person;
import com.endoftheworldimprov.service.api.IPersonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Service("personService")
public class PersonServiceImpl extends AbstractServiceImpl implements IPersonService {
        
    @Transactional
    public void addPerson(Person person) {
        entityManager.persist(person);
    }

    @Transactional
    public List<Person> listPeople() {
        CriteriaQuery<Person> c = entityManager.getCriteriaBuilder().createQuery(Person.class);
        c.from(Person.class);
        return entityManager.createQuery(c).getResultList();
    }

    @Transactional
    public void removePerson(Integer id) {
        Person person = entityManager.find(Person.class, id);
        if (null != person) {
            entityManager.remove(person);
        }
    }
    
}
