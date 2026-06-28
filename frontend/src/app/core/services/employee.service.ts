import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse } from '../models/page.model';
import { Employee, EmployeeDTO } from '../models/employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly base = '/api/employees';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, search?: string): Observable<PageResponse<Employee>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<PageResponse<Employee>>(this.base, { params });
  }

  getById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.base}/${id}`);
  }

  create(dto: EmployeeDTO): Observable<Employee> {
    return this.http.post<Employee>(this.base, dto);
  }

  update(id: number, dto: EmployeeDTO): Observable<Employee> {
    return this.http.put<Employee>(`${this.base}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
