import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse } from '../models/page.model';
import { Product, ProductDTO, StockUpdate } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly base = '/api/products';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, search?: string, category?: string, providerId?: number): Observable<PageResponse<Product>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    if (category) params = params.set('category', category);
    if (providerId != null) params = params.set('providerId', providerId);
    return this.http.get<PageResponse<Product>>(this.base, { params });
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.base}/${id}`);
  }

  getLowStock(page = 0, size = 10): Observable<PageResponse<Product>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Product>>(`${this.base}/low-stock`, { params });
  }

  create(dto: ProductDTO): Observable<Product> {
    return this.http.post<Product>(this.base, dto);
  }

  update(id: number, dto: ProductDTO): Observable<Product> {
    return this.http.put<Product>(`${this.base}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  updateStock(id: number, update: StockUpdate): Observable<any> {
    return this.http.post(`${this.base}/${id}/stock`, update);
  }

  uploadImage(id: number, file: File): Observable<Product> {
    const form = new FormData();
    form.append('file', file);
    return this.http.put<Product>(`${this.base}/${id}/image`, form);
  }

  deleteImage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}/image`);
  }

  getImageUrl(id: number): string {
    return `${this.base}/${id}/image`;
  }
}
