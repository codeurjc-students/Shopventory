import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProviderService } from '../../../core/services/provider.service';
import { AuthService } from '../../../core/services/auth.service';
import { Provider } from '../../../core/models/provider.model';

@Component({
  selector: 'app-provider-detail',
  templateUrl: './provider-detail.component.html'
})
export class ProviderDetailComponent implements OnInit {
  provider: Provider | null = null;
  loading = true;
  error = '';
  deleteError = '';

  constructor(
    public auth: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private providerService: ProviderService
  ) {}

  ngOnInit(): void {
    const id = +(this.route.snapshot.paramMap.get('id') ?? 0);
    this.providerService.getById(id).subscribe({
      next: p => { this.provider = p; this.loading = false; },
      error: () => { this.error = 'Provider not found.'; this.loading = false; }
    });
  }

  delete(): void {
    if (!this.provider || !confirm(`Delete provider "${this.provider.name}"?`)) return;
    this.providerService.delete(this.provider.id).subscribe({
      next: () => this.router.navigate(['/providers']),
      error: err => { this.deleteError = err.error?.error || 'Could not delete provider.'; }
    });
  }
}
