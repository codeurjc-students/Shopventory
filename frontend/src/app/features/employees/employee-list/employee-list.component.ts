import { Component, OnInit } from '@angular/core';
import { EmployeeService } from '../../../core/services/employee.service';
import { Employee } from '../../../core/models/employee.model';
import { PageResponse } from '../../../core/models/page.model';

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html'
})
export class EmployeeListComponent implements OnInit {
  page: PageResponse<Employee> | null = null;
  currentPage = 0;
  searchTerm = '';
  loading = true;
  error = '';

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.employeeService.getAll(this.currentPage, 10, this.searchTerm || undefined).subscribe({
      next: p => { this.page = p; this.loading = false; },
      error: () => { this.error = 'Failed to load employees.'; this.loading = false; }
    });
  }

  delete(id: number): void {
    if (!confirm('Delete this employee?')) return;
    this.employeeService.delete(id).subscribe({ next: () => this.load() });
  }

  nextPage(): void { if (this.page && !this.page.last) { this.currentPage++; this.load(); } }
  prevPage(): void { if (this.currentPage > 0) { this.currentPage--; this.load(); } }
}
