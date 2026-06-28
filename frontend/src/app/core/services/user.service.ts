import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse } from '../models/page.model';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly base = '/api/users';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, search?: string): Observable<PageResponse<User>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<PageResponse<User>>(this.base, { params });
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.base}/${id}`);
  }

  getPending(): Observable<User[]> {
    return this.http.get<User[]>(`${this.base}/pending`);
  }

  approve(id: number): Observable<User> {
    return this.http.post<User>(`${this.base}/${id}/approve`, {});
  }

  setEnabled(id: number, value: boolean): Observable<User> {
    const params = new HttpParams().set('value', value);
    return this.http.post<User>(`${this.base}/${id}/enabled`, {}, { params });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
