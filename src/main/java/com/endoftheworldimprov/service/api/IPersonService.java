package com.endoftheworldimprov.service.api;


import com.endoftheworldimprov.model.domain.Person;

import java.util.List;

public interface IPersonService {
    
    public void addPerson(Person person);
    public List<Person> listPeople();
    public void removePerson(Integer id);
}
