import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { ProviderService } from '../../../core/services/provider.service';
import { Provider } from '../../../core/models/provider.model';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  productId?: number;
  providers: Provider[] = [];
  loading = false;
  error = '';
  categoriesInput = '';

  currentHasImage = false;
  removeImage = false;
  selectedFile: File | null = null;
  imagePreviewUrl: string | null = null;

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
        this.currentHasImage = p.hasImage ?? false;
      });
    }
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    this.selectedFile = file;
    this.removeImage = false;
    const reader = new FileReader();
    reader.onload = e => this.imagePreviewUrl = e.target?.result as string;
    reader.readAsDataURL(file);
  }

  clearFileSelection(): void {
    this.selectedFile = null;
    this.imagePreviewUrl = null;
  }

  markRemoveImage(): void {
    this.removeImage = true;
    this.selectedFile = null;
    this.imagePreviewUrl = null;
  }

  undoRemoveImage(): void {
    this.removeImage = false;
  }

  get existingImageUrl(): string {
    return this.productService.getImageUrl(this.productId!);
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
      next: product => this.handleImageAfterSave(product.id),
      error: err => { this.error = err.error?.error || 'Save failed'; this.loading = false; }
    });
  }

  private handleImageAfterSave(id: number): void {
    if (this.selectedFile) {
      this.productService.uploadImage(id, this.selectedFile).subscribe({
        next: () => this.router.navigate(['/products', id]),
        error: () => this.router.navigate(['/products', id])
      });
    } else if (this.removeImage) {
      this.productService.deleteImage(id).subscribe({
        next: () => this.router.navigate(['/products', id]),
        error: () => this.router.navigate(['/products', id])
      });
    } else {
      this.router.navigate(['/products', id]);
    }
  }
}
