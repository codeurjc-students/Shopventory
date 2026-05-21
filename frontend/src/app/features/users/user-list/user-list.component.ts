import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../core/models/user.model';
import { PageResponse } from '../../../core/models/page.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html'
})
export class UserListComponent implements OnInit {
  page: PageResponse<User> | null = null;
  pending: User[] = [];
  currentPage = 0;
  loading = true;
  error = '';

  constructor(private userService: UserService) {}

  ngOnInit(): void { this.load(); this.loadPending(); }

  load(): void {
    this.loading = true;
    this.userService.getAll(this.currentPage, 10).subscribe({
      next: p => { this.page = p; this.loading = false; },
      error: () => { this.error = 'Failed to load users.'; this.loading = false; }
    });
  }

  loadPending(): void {
    this.userService.getPending().subscribe({ next: p => this.pending = p });
  }

  approve(id: number): void {
    this.userService.approve(id).subscribe({ next: () => { this.load(); this.loadPending(); } });
  }

  delete(id: number): void {
    if (!confirm('Delete this user?')) return;
    this.userService.delete(id).subscribe({ next: () => this.load() });
  }

  nextPage(): void { if (this.page && !this.page.last) { this.currentPage++; this.load(); } }
  prevPage(): void { if (this.currentPage > 0) { this.currentPage--; this.load(); } }
}
