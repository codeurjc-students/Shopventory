import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.auth.waitForAuth().pipe(
      map(() => {
        if (this.auth.isLoggedIn() && this.auth.isApproved()) return true;
        if (this.auth.isLoggedIn() && !this.auth.isApproved())
          return this.router.createUrlTree(['/pending-approval']);
        return this.router.createUrlTree(['/login']);
      })
    );
  }
}
