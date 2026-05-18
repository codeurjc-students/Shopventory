import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse } from '../models/page.model';
import { Provider, ProviderDTO } from '../models/provider.model';

@Injectable({ providedIn: 'root' })
export class ProviderService {
  private readonly base = '/api/providers';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, search?: string): Observable<PageResponse<Provider>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<PageResponse<Provider>>(this.base, { params });
  }

  getById(id: number): Observable<Provider> {
    return this.http.get<Provider>(`${this.base}/${id}`);
  }

  create(dto: ProviderDTO): Observable<Provider> {
    return this.http.post<Provider>(this.base, dto);
  }

  update(id: number, dto: ProviderDTO): Observable<Provider> {
    return this.http.put<Provider>(`${this.base}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
