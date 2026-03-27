import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../infrastructure/services/auth.service';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-page">
      <div class="auth-card card">
        <div class="auth-header">
          <p>Inicia sesión para explorar las razas</p>
        </div>

        <div class="error-banner" *ngIf="errorMessage">{{ errorMessage }}</div>

        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div class="form-field">
            <label>Usuario</label>
            <input formControlName="username" type="text" placeholder="tu_usuario" />
            <span class="field-error"
              *ngIf="form.get('username')?.invalid && form.get('username')?.touched">
              El usuario es requerido
            </span>
          </div>

          <div class="form-field">
            <label>Contraseña</label>
            <input formControlName="password" type="password" placeholder="••••••••" />
            <span class="field-error"
              *ngIf="form.get('password')?.invalid && form.get('password')?.touched">
              La contraseña es requerida (min. 6 caracteres)
            </span>
          </div>

          <button class="btn-primary" type="submit" [disabled]="loading || form.invalid">
            <span *ngIf="!loading">Iniciar sesión</span>
            <span *ngIf="loading">Cargando...</span>
          </button>
        </form>

        <p class="auth-footer">
          ¿No tienes cuenta? <a routerLink="/register">Regístrate</a>
        </p>
      </div>
    </div>
  `,
  styles: [`
    .auth-page {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: calc(100vh - 64px);
      padding: 2rem;
    }
    .auth-card {
      width: 100%;
      max-width: 420px;
    }
    .auth-header {
      text-align: center;
      margin-bottom: 2rem;
      .logo { font-size: 2.5rem; }
      h1 { font-size: 1.8rem; font-weight: 700; margin: 0.5rem 0 0.25rem; letter-spacing: -0.03em; }
      p { color: var(--text-muted); font-size: 0.9rem; }
    }
    .auth-footer {
      text-align: center;
      margin-top: 1.25rem;
      font-size: 0.88rem;
      color: var(--text-muted);
      a { color: var(--accent); text-decoration: none; font-weight: 600;
        &:hover { text-decoration: underline; } }
    }
  `],
})
export class LoginComponent {
  form: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.form.value).subscribe({
      next: () => this.router.navigate(['/breeds']),
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err.status === 401
            ? 'Credenciales incorrectas'
            : 'Error al iniciar sesión. Intenta de nuevo.';
      },
    });
  }
}
