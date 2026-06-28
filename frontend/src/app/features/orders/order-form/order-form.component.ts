import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { ProductService } from '../../../core/services/product.service';
import { ProviderService } from '../../../core/services/provider.service';
import { Product } from '../../../core/models/product.model';
import { Provider } from '../../../core/models/provider.model';
import { OrderType } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html'
})
export class OrderFormComponent implements OnInit {
  form: FormGroup;
  products: Product[] = [];
  providers: Provider[] = [];
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private orderService: OrderService,
    private productService: ProductService,
    private providerService: ProviderService
  ) {
    this.form = this.fb.group({
      type: ['SALE', Validators.required],
      providerId: [null],
      customerName: [''],
      customerEmail: ['', Validators.email],
      deliveryDate: [''],
      discount: [0, [Validators.min(0), Validators.max(100)]],
      notes: [''],
      items: this.fb.array([this.newItem()])
    });
  }

  ngOnInit(): void {
    this.productService.getAll(0, 100).subscribe(p => this.products = p.content);
    this.providerService.getAll(0, 100).subscribe(p => this.providers = p.content);
  }

  get items(): FormArray {
    return this.form.get('items') as FormArray;
  }

  get orderType(): OrderType {
    return this.form.get('type')?.value;
  }

  newItem(): FormGroup {
    return this.fb.group({
      productId: [null, Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]]
    });
  }

  addItem(): void { this.items.push(this.newItem()); }

  removeItem(i: number): void {
    if (this.items.length > 1) this.items.removeAt(i);
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    const val = this.form.value;
    const dto = {
      type: val.type,
      providerId: val.providerId || undefined,
      customerName: val.customerName || undefined,
      customerEmail: val.customerEmail || undefined,
      deliveryDate: val.deliveryDate || undefined,
      discount: val.discount || 0,
      notes: val.notes || undefined,
      items: val.items
    };
    this.orderService.create(dto).subscribe({
      next: () => this.router.navigate(['/orders']),
      error: err => { this.error = err.error?.error || 'Order creation failed'; this.loading = false; }
    });
  }
}
