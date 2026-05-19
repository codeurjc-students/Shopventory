import { Component } from '@angular/core';

@Component({
  selector: 'app-server-error',
  template: `
    <div class="min-vh-100 d-flex align-items-center justify-content-center text-center">
      <div>
        <h1 class="display-1 fw-bold text-danger">500</h1>
        <h3>Server Error</h3>
        <p class="text-muted">An unexpected error occurred. Please try again later.</p>
        <a routerLink="/dashboard" class="btn btn-danger">Go to Dashboard</a>
      </div>
    </div>`
})
export class ServerErrorComponent {}
