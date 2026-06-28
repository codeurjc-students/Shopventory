import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse } from '../models/page.model';
import { Order, OrderDTO, OrderType } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly base = '/api/orders';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, type?: OrderType): Observable<PageResponse<Order>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (type) params = params.set('type', type);
    return this.http.get<PageResponse<Order>>(this.base, { params });
  }

  getById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.base}/${id}`);
  }

  create(dto: OrderDTO): Observable<Order> {
    return this.http.post<Order>(this.base, dto);
  }

  confirm(id: number): Observable<Order> {
    return this.http.post<Order>(`${this.base}/${id}/confirm`, {});
  }

  deliver(id: number): Observable<Order> {
    return this.http.post<Order>(`${this.base}/${id}/deliver`, {});
  }

  cancel(id: number): Observable<Order> {
    return this.http.post<Order>(`${this.base}/${id}/cancel`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
