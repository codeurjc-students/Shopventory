import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthResponse, LoginRequest, User, UserRegistration } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.loadCurrentUser();
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', credentials).pipe(
      tap(() => this.loadCurrentUser())
    );
  }

  register(data: UserRegistration): Observable<User> {
    return this.http.post<User>('/api/auth/register', data);
  }

  logout(): void {
    this.http.post('/api/auth/logout', {}).subscribe({
      complete: () => {
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
      }
    });
  }

  loadCurrentUser(): void {
    this.http.get<User>('/api/auth/me').subscribe({
      next: user => this.currentUserSubject.next(user),
      error: () => this.currentUserSubject.next(null)
    });
  }

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  isAdmin(): boolean {
    return this.currentUserSubject.value?.roles?.includes('ADMIN') ?? false;
  }

  isApproved(): boolean {
    return this.currentUserSubject.value?.approved ?? false;
  }
}
