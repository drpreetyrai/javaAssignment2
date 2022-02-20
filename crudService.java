
package com.tymoshenko.controller.repository;

import java.util.List;

/**
 * Exposes CRUD API of wrapped Spring JpaRepository.
 *
 * @author Yakiv
 * @since 15.03.2016
 */
public interface CrudService<T> {

    /**
     * Saves an entity and flushes changes instantly.
     * Use for both create and update CRUD methods.
     *
     * @param entity the entity to insert/update into DB
     * @return the inserted/updated entity
     */
    T save(T entity);

    /**
     * Retrieves an entity by its id.
     *
     * @param id - must not be null.
     * @return the entity with the given id or null if none found
     */
    T readOne(Long id);

    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    List<T> readAll();

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     */
    void delete(Long id);
}
