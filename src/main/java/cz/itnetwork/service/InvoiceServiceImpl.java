package cz.itnetwork.service;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.dto.InvoiceStatisticDTO;
import cz.itnetwork.dto.mapper.InvoiceMapper;
import cz.itnetwork.entity.InvoiceEntity;
import cz.itnetwork.entity.PersonEntity;
import cz.itnetwork.entity.filter.InvoiceFilter;
import cz.itnetwork.entity.repository.InvoiceRepository;
import cz.itnetwork.entity.repository.PersonRepository;
import cz.itnetwork.entity.repository.specification.InvoiceSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Implementation of the {@link InvoiceService} interface providing business logic for managing invoices.
 *
 * <p>This class provides methods for creating, retrieving, updating, and deleting invoices, as well as obtaining invoice statistics.</p>
 *
 * @version 1.0
 * @since 2024
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public InvoiceDTO addInvoice(InvoiceDTO invoiceDTO) {
        InvoiceEntity invoiceEntity = invoiceMapper.toEntity(invoiceDTO);

        PersonEntity buyerEntity = personRepository.getReferenceById(invoiceEntity.getBuyer().getId());
        invoiceEntity.setBuyer(buyerEntity);

        PersonEntity sellerEntity = personRepository.getReferenceById(invoiceEntity.getSeller().getId());
        invoiceEntity.setSeller(sellerEntity);

        invoiceEntity = invoiceRepository.save(invoiceEntity);

        return invoiceMapper.toDTO(invoiceEntity);
    }

    @Override
    public List<InvoiceDTO> getInvoices(InvoiceFilter invoiceFilter) {
        InvoiceSpecification invoiceSpecification = new InvoiceSpecification(invoiceFilter);

        return invoiceRepository.findAll(invoiceSpecification, PageRequest.of(0, invoiceFilter.getLimit()))
                .stream()
                .map(invoiceEntity -> invoiceMapper.toDTO(invoiceEntity))
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDTO getInvoiceById(long id) {
        InvoiceEntity invoiceEntity = fetchInvoiceById(id);
        return invoiceMapper.toDTO(invoiceEntity);
    }

    @Override
    public ResponseEntity<Void> removeInvoiceById(long id) {
            InvoiceEntity fetchedInvoice = fetchInvoiceById(id);
            invoiceRepository.delete(fetchedInvoice);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       // Stav 204
    }

    /**
     * Fetches an invoice by its unique identifier.
     *
     * @param id The unique identifier of the invoice.
     * @return The invoice entity.
     * @throws NotFoundException if no invoice with the given ID is found.
     */
    private InvoiceEntity fetchInvoiceById(long id){
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice with id" + id + "wasn't found in the database."));
    }

    @Override
    public InvoiceDTO updateInvoice(long id, InvoiceDTO sourceInvoiceDTO) {

        InvoiceEntity targetInvoiceEntity = fetchInvoiceById(id);

        sourceInvoiceDTO.setId(id);

        invoiceMapper.updateInvoiceEntity(sourceInvoiceDTO, targetInvoiceEntity);

        targetInvoiceEntity.setBuyer(personRepository.getReferenceById(sourceInvoiceDTO.getBuyer().getId()));
        targetInvoiceEntity.setSeller(personRepository.getReferenceById(sourceInvoiceDTO.getSeller().getId()));



        InvoiceEntity savedInvoiceEntity = invoiceRepository.save(targetInvoiceEntity);
        return invoiceMapper.toDTO(savedInvoiceEntity);

    }

    @Override
    public InvoiceStatisticDTO getInvoiceStatistics() {
        return invoiceRepository.findInvoiceStatistic();
    }

}
