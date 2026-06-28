import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forbidden',
  template: `
    <div class="min-vh-100 d-flex align-items-center justify-content-center text-center">
      <div>
        <h1 class="display-1 fw-bold text-warning">403</h1>
        <h3>Access Denied</h3>
        <p class="text-muted">You do not have permission to access this page.</p>
        <a *ngIf="auth.isLoggedIn()" routerLink="/dashboard" class="btn btn-warning">Go to Dashboard</a>
        <a *ngIf="!auth.isLoggedIn()" routerLink="/login" class="btn btn-warning">Go to Login</a>
      </div>
    </div>`
})
export class ForbiddenComponent {
  constructor(public auth: AuthService) {}
}
