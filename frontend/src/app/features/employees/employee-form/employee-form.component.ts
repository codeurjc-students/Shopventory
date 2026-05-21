import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployeeService } from '../../../core/services/employee.service';

@Component({
  selector: 'app-employee-form',
  templateUrl: './employee-form.component.html'
})
export class EmployeeFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  employeeId?: number;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private employeeService: EmployeeService
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      position: ['', Validators.required],
      hireDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.employeeId = +id;
      this.employeeService.getById(this.employeeId).subscribe(emp => {
        this.form.patchValue({ ...emp, hireDate: emp.hireDate?.substring(0, 10) });
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    const dto = this.form.value;
    const obs = this.isEdit
      ? this.employeeService.update(this.employeeId!, dto)
      : this.employeeService.create(dto);
    obs.subscribe({
      next: () => this.router.navigate(['/employees']),
      error: err => { this.error = err.error?.error || 'Save failed'; this.loading = false; }
    });
  }
}
