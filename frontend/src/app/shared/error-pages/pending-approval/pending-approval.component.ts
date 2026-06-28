import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-pending-approval',
  template: `
    <div class="min-vh-100 d-flex align-items-center justify-content-center text-center">
      <div>
        <i class="bi bi-hourglass-split display-1 text-warning"></i>
        <h3 class="mt-3">Account Pending Approval</h3>
        <p class="text-muted">Your account is awaiting admin approval. Please check back soon.</p>
        <button class="btn btn-outline-secondary" (click)="auth.logout()">Sign Out</button>
      </div>
    </div>`
})
export class PendingApprovalComponent {
  constructor(public auth: AuthService) {}
}
