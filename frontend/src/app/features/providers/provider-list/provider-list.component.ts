import { Component, OnInit } from '@angular/core';
import { ProviderService } from '../../../core/services/provider.service';
import { AuthService } from '../../../core/services/auth.service';
import { Provider } from '../../../core/models/provider.model';
import { PageResponse } from '../../../core/models/page.model';

@Component({
  selector: 'app-provider-list',
  templateUrl: './provider-list.component.html'
})
export class ProviderListComponent implements OnInit {
  page: PageResponse<Provider> | null = null;
  currentPage = 0;
  searchTerm = '';
  loading = true;
  error = '';

  constructor(public auth: AuthService, private providerService: ProviderService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.providerService.getAll(this.currentPage, 10, this.searchTerm || undefined).subscribe({
      next: p => { this.page = p; this.loading = false; },
      error: () => { this.error = 'Failed to load providers.'; this.loading = false; }
    });
  }

  delete(id: number): void {
    if (!confirm('Delete this provider?')) return;
    this.providerService.delete(id).subscribe({ next: () => this.load() });
  }

  nextPage(): void { if (this.page && !this.page.last) { this.currentPage++; this.load(); } }
  prevPage(): void { if (this.currentPage > 0) { this.currentPage--; this.load(); } }
}
