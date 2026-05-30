import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployeeService } from '../../../core/services/employee.service';
import { Employee } from '../../../core/models/employee.model';

@Component({
  selector: 'app-employee-detail',
  templateUrl: './employee-detail.component.html'
})
export class EmployeeDetailComponent implements OnInit {
  employee: Employee | null = null;
  loading = true;
  error = '';
  deleteError = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private employeeService: EmployeeService
  ) {}

  ngOnInit(): void {
    const id = +(this.route.snapshot.paramMap.get('id') ?? 0);
    this.employeeService.getById(id).subscribe({
      next: e => { this.employee = e; this.loading = false; },
      error: () => { this.error = 'Employee not found.'; this.loading = false; }
    });
  }

  delete(): void {
    if (!this.employee || !confirm(`Delete employee "${this.employee.name} ${this.employee.surname}"?`)) return;
    this.employeeService.delete(this.employee.id).subscribe({
      next: () => this.router.navigate(['/employees']),
      error: err => { this.deleteError = err.error?.error || 'Could not delete employee.'; }
    });
  }
}
