import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { AuthService } from '../../../core/services/auth.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html'
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  loading = true;
  error = '';
  actionError = '';

  constructor(
    public auth: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const id = +(this.route.snapshot.paramMap.get('id') ?? 0);
    this.orderService.getById(id).subscribe({
      next: o => { this.order = o; this.loading = false; },
      error: () => { this.error = 'Order not found.'; this.loading = false; }
    });
  }

  confirm(): void {
    if (!this.order) return;
    this.orderService.confirm(this.order.id).subscribe({
      next: o => { this.order = o; this.actionError = ''; },
      error: err => { this.actionError = err.error?.error || 'Could not confirm order.'; }
    });
  }

  deliver(): void {
    if (!this.order) return;
    this.orderService.deliver(this.order.id).subscribe({
      next: o => { this.order = o; this.actionError = ''; },
      error: err => { this.actionError = err.error?.error || 'Could not mark order as delivered.'; }
    });
  }

  cancel(): void {
    if (!this.order || !confirm('Cancel this order?')) return;
    this.orderService.cancel(this.order.id).subscribe({
      next: o => { this.order = o; this.actionError = ''; },
      error: err => { this.actionError = err.error?.error || 'Could not cancel order.'; }
    });
  }

  delete(): void {
    if (!this.order || !confirm('Delete this order? This action cannot be undone.')) return;
    this.orderService.delete(this.order.id).subscribe({
      next: () => this.router.navigate(['/orders']),
      error: err => { this.actionError = err.error?.error || 'Could not delete order.'; }
    });
  }
}
