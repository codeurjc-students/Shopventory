import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { AuthService } from '../../../core/services/auth.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html'
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = true;
  error = '';
  stockQuantity = 0;
  stockReason = '';
  stockSuccess = '';
  stockError = '';

  constructor(
    public auth: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    const id = +(this.route.snapshot.paramMap.get('id') ?? 0);
    this.productService.getById(id).subscribe({
      next: p => { this.product = p; this.loading = false; },
      error: () => { this.error = 'Product not found.'; this.loading = false; }
    });
  }

  updateStock(): void {
    if (!this.product) return;
    this.productService.updateStock(this.product.id, { quantity: this.stockQuantity, reason: this.stockReason }).subscribe({
      next: () => {
        this.productService.getById(this.product!.id).subscribe(p => this.product = p);
        this.stockSuccess = 'Stock updated.';
        this.stockError = '';
        this.stockQuantity = 0;
        this.stockReason = '';
      },
      error: err => { this.stockError = err.error?.error || 'Update failed'; this.stockSuccess = ''; }
    });
  }
}
