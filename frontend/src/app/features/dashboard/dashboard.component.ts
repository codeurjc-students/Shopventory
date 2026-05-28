import { Component, OnInit } from '@angular/core';
import { DashboardService, DashboardStats } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  loading = true;
  error = '';

  constructor(private dashboardService: DashboardService, public auth: AuthService) {}

  ngOnInit(): void {
    this.dashboardService.getStats().subscribe({
      next: s => { this.stats = s; this.loading = false; },
      error: () => { this.error = 'Failed to load dashboard stats.'; this.loading = false; }
    });
  }
}
