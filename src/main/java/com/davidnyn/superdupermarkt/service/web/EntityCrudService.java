package com.davidnyn.superdupermarkt.service.web;

import com.davidnyn.superdupermarkt.application.exceptions.EntityValidationException;
import com.davidnyn.superdupermarkt.data.model.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public abstract class EntityCrudService<DATATYPE extends BaseEntity, REPOSITORY extends JpaRepository<DATATYPE, Long>> {

    @Autowired
    protected REPOSITORY repo;

    public Optional<DATATYPE> findById(Long id)
    {
        return repo.findById(id);
    }

    public List<DATATYPE> findAllById(Iterable<Long> idList)
    {
        return repo.findAllById(idList);
    }

    public List<DATATYPE> findAll()
    {
        return repo.findAll();
    }

    public DATATYPE save(DATATYPE entity, boolean validate) throws EntityValidationException
    {
        if (validate)
            validateEntity(entity);

        repo.save(entity);
        return entity;
    }

    public DATATYPE save(DATATYPE entity) throws EntityValidationException
    {
        return save(entity, true);
    }

    public void validateEntity(DATATYPE entity) throws EntityValidationException
    {
    }

}
