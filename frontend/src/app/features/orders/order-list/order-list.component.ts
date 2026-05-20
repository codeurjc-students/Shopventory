import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';
import { AuthService } from '../../../core/services/auth.service';
import { Order, OrderType } from '../../../core/models/order.model';
import { PageResponse } from '../../../core/models/page.model';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html'
})
export class OrderListComponent implements OnInit {
  page: PageResponse<Order> | null = null;
  currentPage = 0;
  typeFilter: OrderType | '' = '';
  loading = true;
  error = '';

  constructor(public auth: AuthService, private orderService: OrderService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.orderService.getAll(this.currentPage, 10, this.typeFilter || undefined).subscribe({
      next: p => { this.page = p; this.loading = false; },
      error: () => { this.error = 'Failed to load orders.'; this.loading = false; }
    });
  }

  confirm(id: number): void {
    this.orderService.confirm(id).subscribe({ next: () => this.load() });
  }

  cancel(id: number): void {
    if (!confirm('Cancel this order?')) return;
    this.orderService.cancel(id).subscribe({ next: () => this.load() });
  }

  delete(id: number): void {
    if (!confirm('Delete this order?')) return;
    this.orderService.delete(id).subscribe({ next: () => this.load() });
  }

  nextPage(): void { if (this.page && !this.page.last) { this.currentPage++; this.load(); } }
  prevPage(): void { if (this.currentPage > 0) { this.currentPage--; this.load(); } }
}
