import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  form: FormGroup;
  error = '';
  loading = false;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    this.auth.login(this.form.value).subscribe({
      next: (res) => {
        if (res.status === 'SUCCESS') {
          this.auth.loadCurrentUser();
          setTimeout(() => this.router.navigate(['/dashboard']), 300);
        } else {
          this.error = res.message || 'Login failed';
          this.loading = false;
        }
      },
      error: () => {
        this.error = 'Invalid credentials or account not approved.';
        this.loading = false;
      }
    });
  }
}
