import { Component } from '@angular/core';

@Component({
  selector: 'app-forbidden',
  template: `
    <div class="min-vh-100 d-flex align-items-center justify-content-center text-center">
      <div>
        <h1 class="display-1 fw-bold text-warning">403</h1>
        <h3>Access Denied</h3>
        <p class="text-muted">You do not have permission to access this page.</p>
        <a routerLink="/dashboard" class="btn btn-warning">Go to Dashboard</a>
      </div>
    </div>`
})
export class ForbiddenComponent {}
