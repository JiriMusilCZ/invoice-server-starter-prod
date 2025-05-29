package cz.itnetwork.dto.mapper;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.entity.InvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    InvoiceDTO toDTO(InvoiceEntity source);

    InvoiceEntity toEntity(InvoiceDTO source);

    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "seller", ignore = true)
    void updateInvoiceEntity(InvoiceDTO source, @MappingTarget InvoiceEntity target);
}

