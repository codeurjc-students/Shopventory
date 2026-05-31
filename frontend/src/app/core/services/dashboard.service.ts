import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardStats {
  totalProducts: number;
  lowStockProducts: number;
  totalProviders: number;
  totalUsers: number;
  pendingApprovals: number;
  totalOrders: number;
  pendingSales: number;
  pendingPurchases: number;
  totalSalesAmount: number;
  topProducts: any[];
  lowestStockProducts: any[];
  categoryDistribution: any[];
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(private http: HttpClient) {}

  getStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>('/api/dashboard/stats');
  }
}
