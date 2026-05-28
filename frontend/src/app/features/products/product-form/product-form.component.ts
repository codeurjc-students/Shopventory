import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { ProviderService } from '../../../core/services/provider.service';
import { Provider } from '../../../core/models/provider.model';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html'
})
export class ProductFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  productId?: number;
  providers: Provider[] = [];
  loading = false;
  error = '';
  categoriesInput = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private providerService: ProviderService
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      sku: [''],
      description: [''],
      descriptionShort: [''],
      price: [0, [Validators.required, Validators.min(0)]],
      stock: [0, Validators.min(0)],
      minStockThreshold: [5, Validators.min(0)],
      providerIds: [[]]
    });
  }

  ngOnInit(): void {
    this.providerService.getAll(0, 100).subscribe(p => this.providers = p.content);
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.isEdit = true;
      this.productId = +id;
      this.productService.getById(this.productId).subscribe(p => {
        this.form.patchValue({
          name: p.name,
          sku: p.sku || '',
          description: p.description || '',
          descriptionShort: p.descriptionShort || '',
          price: p.price,
          stock: p.stock,
          minStockThreshold: p.minStockThreshold,
          providerIds: p.providers?.map(pr => pr.id) ?? []
        });
        this.categoriesInput = p.categories?.join(', ') || '';
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    const categories = this.categoriesInput
      .split(',').map(s => s.trim()).filter(s => s.length > 0);
    const dto = { ...this.form.value, categories };
    const obs = this.isEdit
      ? this.productService.update(this.productId!, dto)
      : this.productService.create(dto);
    obs.subscribe({
      next: () => this.router.navigate(['/products']),
      error: err => { this.error = err.error?.error || 'Save failed'; this.loading = false; }
    });
  }
}
