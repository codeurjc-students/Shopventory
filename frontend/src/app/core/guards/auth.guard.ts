import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.auth.isLoggedIn() && this.auth.isApproved()) {
      return true;
    }
    if (this.auth.isLoggedIn() && !this.auth.isApproved()) {
      this.router.navigate(['/pending-approval']);
      return false;
    }
    this.router.navigate(['/login']);
    return false;
  }
}
