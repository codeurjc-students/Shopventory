import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProviderService } from '../../../core/services/provider.service';

@Component({
  selector: 'app-provider-form',
  templateUrl: './provider-form.component.html'
})
export class ProviderFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  providerId?: number;
  loading = false;
  error = '';
  typesInput = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private providerService: ProviderService
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      address: [''],
      phoneNumber: [''],
      website: [''],
      contactPerson: [''],
      email: ['', Validators.email]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.providerId = +id;
      this.providerService.getById(this.providerId).subscribe(p => {
        this.form.patchValue(p);
        this.typesInput = p.types?.join(', ') || '';
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    const types = this.typesInput
      .split(',').map(s => s.trim()).filter(s => s.length > 0);
    const dto = { ...this.form.value, types };
    const obs = this.isEdit
      ? this.providerService.update(this.providerId!, dto)
      : this.providerService.create(dto);
    obs.subscribe({
      next: () => this.router.navigate(['/providers']),
      error: err => { this.error = err.error?.error || 'Save failed'; this.loading = false; }
    });
  }
}
