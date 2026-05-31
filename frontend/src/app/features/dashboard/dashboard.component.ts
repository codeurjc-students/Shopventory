import { Component, OnInit } from '@angular/core';
import { ChartData, ChartOptions } from 'chart.js';
import { DashboardService, DashboardStats } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  loading = true;
  error = '';

  pieChartData: ChartData<'pie', number[], string> = {
    labels: [],
    datasets: [{ data: [] }]
  };

  pieChartOptions: ChartOptions<'pie'> = {
    responsive: true,
    plugins: {
      legend: { position: 'right' },
      tooltip: {
        callbacks: {
          label: ctx => ` ${ctx.label}: ${ctx.parsed} products`
        }
      }
    }
  };

  constructor(private dashboardService: DashboardService, public auth: AuthService) {}

  ngOnInit(): void {
    this.dashboardService.getStats().subscribe({
      next: s => {
        this.stats = s;
        this.buildPieChart(s);
        this.loading = false;
      },
      error: () => { this.error = 'Failed to load dashboard stats.'; this.loading = false; }
    });
  }

  private buildPieChart(stats: DashboardStats): void {
    const colors = [
      '#0d6efd', '#198754', '#dc3545', '#ffc107',
      '#0dcaf0', '#6f42c1', '#fd7e14', '#20c997',
      '#d63384', '#6c757d'
    ];
    const dist = stats.categoryDistribution;
    this.pieChartData = {
      labels: dist.map((c: any) => c.category),
      datasets: [{
        data: dist.map((c: any) => c.count),
        backgroundColor: colors.slice(0, dist.length),
        hoverOffset: 8
      }]
    };
  }
}
