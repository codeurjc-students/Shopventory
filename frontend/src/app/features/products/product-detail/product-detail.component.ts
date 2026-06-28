import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { AuthService } from '../../../core/services/auth.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = true;
  error = '';
  stockQuantity: number | null = null;
  stockReason = '';
  stockSuccess = '';
  stockError = '';

  constructor(
    public auth: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    public productService: ProductService
  ) {}

  ngOnInit(): void {
    const id = +(this.route.snapshot.paramMap.get('id') ?? 0);
    this.productService.getById(id).subscribe({
      next: p => { this.product = p; this.loading = false; },
      error: () => { this.error = 'Product not found.'; this.loading = false; }
    });
  }

  updateStock(): void {
    if (!this.product || this.stockQuantity == null) return;
    this.productService.updateStock(this.product.id, { quantity: this.stockQuantity, reason: this.stockReason }).subscribe({
      next: () => {
        this.productService.getById(this.product!.id).subscribe(p => this.product = p);
        this.stockSuccess = 'Stock updated.';
        this.stockError = '';
        this.stockQuantity = null;
        this.stockReason = '';
      },
      error: err => { this.stockError = err.error?.error || 'Update failed'; this.stockSuccess = ''; }
    });
  }
}
