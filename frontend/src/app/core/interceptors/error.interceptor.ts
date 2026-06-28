import { Injectable } from '@angular/core';
import {
  HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router, private auth: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        const isAuthEndpoint = request.url.includes('/api/auth/login')
          || request.url.includes('/api/auth/register')
          || request.url.includes('/api/auth/me')
          || request.url.includes('/api/auth/logout');

        if (!isAuthEndpoint) {
          if (error.status === 401) {
            this.auth.loadCurrentUser();
            this.router.navigate(['/login']);
          } else if (error.status === 403) {
            this.router.navigate(['/403']);
          } else if (error.status === 404) {
            this.router.navigate(['/404']);
          } else if (error.status >= 500) {
            this.router.navigate(['/500']);
          }
        }
        return throwError(() => error);
      })
    );
  }
}
