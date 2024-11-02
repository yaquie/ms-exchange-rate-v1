package pe.com.app.exchange.rate.service.mapper;

import pe.com.app.exchange.rate.model.TransaccionResponse;
import pe.com.app.exchange.rate.model.Transaccion;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {

    TransaccionResponse convert(Transaccion transaccion);
}
