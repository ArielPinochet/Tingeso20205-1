package com.Pep2.Tingeso.ReservaPago;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrosRepository extends JpaRepository<CarrosEntity, String> {

}
