import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../core/services/product.service';
import { ProviderService } from '../../../core/services/provider.service';
import { AuthService } from '../../../core/services/auth.service';
import { Product } from '../../../core/models/product.model';
import { Provider } from '../../../core/models/provider.model';
import { PageResponse } from '../../../core/models/page.model';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html'
})
export class ProductListComponent implements OnInit {
  page: PageResponse<Product> | null = null;
  currentPage = 0;
  searchTerm = '';
  categoryFilter = '';
  providerFilter: number | null = null;
  providers: Provider[] = [];
  loading = true;
  error = '';

  constructor(
    public auth: AuthService,
    private productService: ProductService,
    private providerService: ProviderService
  ) {}

  ngOnInit(): void {
    this.providerService.getAll(0, 100).subscribe(p => this.providers = p.content);
    this.load();
  }

  load(): void {
    this.loading = true;
    this.productService.getAll(
      this.currentPage, 10,
      this.searchTerm || undefined,
      this.categoryFilter || undefined,
      this.providerFilter ?? undefined
    ).subscribe({
      next: p => { this.page = p; this.loading = false; },
      error: () => { this.error = 'Failed to load products.'; this.loading = false; }
    });
  }

  search(): void { this.currentPage = 0; this.load(); }

  clearFilters(): void {
    this.searchTerm = '';
    this.categoryFilter = '';
    this.providerFilter = null;
    this.currentPage = 0;
    this.load();
  }

  nextPage(): void { if (this.page && !this.page.last) { this.currentPage++; this.load(); } }

  prevPage(): void { if (this.currentPage > 0) { this.currentPage--; this.load(); } }

  delete(id: number): void {
    if (!confirm('Delete this product?')) return;
    this.productService.delete(id).subscribe({ next: () => this.load() });
  }
}
