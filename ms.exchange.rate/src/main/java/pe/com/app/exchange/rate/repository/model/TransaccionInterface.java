package pe.com.app.exchange.rate.repository.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.com.app.exchange.rate.model.Transaccion;

@Repository
public interface TransaccionInterface  extends JpaRepository<Transaccion, Long> {
}
