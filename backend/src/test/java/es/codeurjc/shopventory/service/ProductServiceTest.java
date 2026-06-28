package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.dto.ProductDTO;
import es.codeurjc.shopventory.exception.ConflictException;
import es.codeurjc.shopventory.exception.ResourceNotFoundException;
import es.codeurjc.shopventory.model.Product;
import es.codeurjc.shopventory.repository.ProductRepository;
import es.codeurjc.shopventory.repository.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProductService productService;

    private ProductDTO productDTO;
    private Product existingProduct;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setSku("TEST-001");
        productDTO.setPrice(new BigDecimal("29.99"));
        productDTO.setStock(50);
        productDTO.setMinStockThreshold(5);
        productDTO.setCategories(Set.of("Electronics"));

        existingProduct = new Product("Test Product", "TEST-001", "Desc", "Short",
                new BigDecimal("29.99"), 50, 5, Set.of("Electronics"));
    }

    @Test
    void create_newProduct_success() {
        when(productRepository.existsBySku("TEST-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        Product result = productService.create(productDTO);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_duplicateSku_throwsConflict() {
        when(productRepository.existsBySku("TEST-001")).thenReturn(true);

        assertThrows(ConflictException.class, () -> productService.create(productDTO));
        verify(productRepository, never()).save(any());
    }

    @Test
    void findAll_returnsPageResponse() {
        Page<Product> page = new PageImpl<>(List.of(existingProduct), PageRequest.of(0, 10), 1);
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);

        PageResponse<Product> result = productService.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void findById_notFound_throwsResourceNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findById(999L));
    }

    @Test
    void delete_existingProduct_success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.delete(1L));
        verify(productRepository).deleteById(1L);
    }

    @Test
    void isLowStock_whenStockBelowThreshold_returnsTrue() {
        Product lowStockProduct = new Product("Low", "LOW-001", null, null,
                BigDecimal.TEN, 2, 5, Set.of());
        assertTrue(lowStockProduct.isLowStock());
    }
}
