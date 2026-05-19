import { Component } from '@angular/core';

@Component({
  selector: 'app-not-found',
  template: `
    <div class="min-vh-100 d-flex align-items-center justify-content-center text-center">
      <div>
        <h1 class="display-1 fw-bold text-muted">404</h1>
        <h3>Page Not Found</h3>
        <p class="text-muted">The page you are looking for does not exist.</p>
        <a routerLink="/dashboard" class="btn btn-primary">Go to Dashboard</a>
      </div>
    </div>`
})
export class NotFoundComponent {}
