package com.savvato.tribeapp.repositories;

import com.savvato.tribeapp.entities.Connection;
import com.savvato.tribeapp.entities.Noun;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionsRepository extends CrudRepository<Connection, Long> {
    List<Connection> findAllByToBeConnectedWithUserId(Long toBeConnectedWithUserId);
}
