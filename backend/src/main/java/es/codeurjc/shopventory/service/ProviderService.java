package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.dto.ProviderDTO;
import es.codeurjc.shopventory.exception.ConflictException;
import es.codeurjc.shopventory.exception.ResourceNotFoundException;
import es.codeurjc.shopventory.model.Provider;
import es.codeurjc.shopventory.repository.ProductRepository;
import es.codeurjc.shopventory.repository.ProviderRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final ProductRepository productRepository;

    public ProviderService(ProviderRepository providerRepository, ProductRepository productRepository) {
        this.providerRepository = providerRepository;
        this.productRepository = productRepository;
    }

    public Provider create(ProviderDTO dto) {
        if (providerRepository.existsByName(dto.getName())) {
            throw new ConflictException("Provider already exists: " + dto.getName());
        }
        return providerRepository.save(mapDtoToProvider(new Provider(), dto));
    }

    @Transactional(readOnly = true)
    public PageResponse<Provider> findAll(Pageable pageable) {
        return new PageResponse<>(providerRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Provider findById(Long id) {
        return getProviderOrThrow(id);
    }

    @Transactional(readOnly = true)
    public PageResponse<Provider> search(String query, Pageable pageable) {
        return new PageResponse<>(providerRepository.findByNameContainingIgnoreCase(query, pageable));
    }

    public Provider update(Long id, ProviderDTO dto) {
        Provider provider = getProviderOrThrow(id);
        if (!provider.getName().equals(dto.getName()) && providerRepository.existsByName(dto.getName())) {
            throw new ConflictException("Provider name already in use: " + dto.getName());
        }
        return providerRepository.save(mapDtoToProvider(provider, dto));
    }

    public void delete(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", id));
        long associated = productRepository.countByProvidersContaining(provider);
        if (associated > 0) {
            throw new ConflictException(
                "Cannot delete provider \"" + provider.getName() + "\": it has " + associated +
                " associated product(s). Remove this provider from all products first.");
        }
        providerRepository.deleteById(id);
    }

    private Provider mapDtoToProvider(Provider provider, ProviderDTO dto) {
        provider.setName(dto.getName());
        provider.setAddress(dto.getAddress());
        provider.setPhoneNumber(dto.getPhoneNumber());
        provider.setWebsite(dto.getWebsite());
        provider.setContactPerson(dto.getContactPerson());
        provider.setEmail(dto.getEmail());
        provider.setTypes(dto.getTypes());
        return provider;
    }

    private Provider getProviderOrThrow(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", id));
    }
}
