import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, Subject, filter, take, takeUntil, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthResponse, LoginRequest, User, UserRegistration } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  private authReadySubject = new BehaviorSubject<boolean>(false);
  private cancelLoad$ = new Subject<void>();

  constructor(private http: HttpClient, private router: Router) {
    this.loadCurrentUser();
  }

  waitForAuth(): Observable<boolean> {
    return this.authReadySubject.pipe(filter(ready => ready), take(1));
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
    // Cancel any in-flight loadCurrentUser so it cannot restore auth after we clear it
    this.cancelLoad$.next();
    // Clear state and mark auth ready immediately — guards won't hang
    this.currentUserSubject.next(null);
    this.authReadySubject.next(true);

    this.http.post('/api/auth/logout', {}).subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login'])
    });
  }

  loadCurrentUser(): void {
    this.http.get<User>('/api/auth/me').pipe(
      takeUntil(this.cancelLoad$)
    ).subscribe({
      next: user => {
        this.currentUserSubject.next(user);
        this.authReadySubject.next(true);
      },
      error: () => {
        this.currentUserSubject.next(null);
        this.authReadySubject.next(true);
      }
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
