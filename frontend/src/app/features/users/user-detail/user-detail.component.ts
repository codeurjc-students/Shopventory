import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html'
})
export class UserDetailComponent implements OnInit {
  user: User | null = null;
  loading = true;
  error = '';
  actionError = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    const id = +(this.route.snapshot.paramMap.get('id') ?? 0);
    this.userService.getById(id).subscribe({
      next: u => { this.user = u; this.loading = false; },
      error: () => { this.error = 'User not found.'; this.loading = false; }
    });
  }

  approve(): void {
    if (!this.user) return;
    this.userService.approve(this.user.id).subscribe({
      next: u => { this.user = u; this.actionError = ''; },
      error: err => { this.actionError = err.error?.error || 'Could not approve user.'; }
    });
  }

  toggleEnabled(): void {
    if (!this.user) return;
    this.userService.setEnabled(this.user.id, !this.user.enabled).subscribe({
      next: u => { this.user = u; this.actionError = ''; },
      error: err => { this.actionError = err.error?.error || 'Could not update account status.'; }
    });
  }

  delete(): void {
    if (!this.user || !confirm(`Delete user "${this.user.email}"?`)) return;
    this.userService.delete(this.user.id).subscribe({
      next: () => this.router.navigate(['/users']),
      error: err => { this.actionError = err.error?.error || 'Could not delete user.'; }
    });
  }
}
