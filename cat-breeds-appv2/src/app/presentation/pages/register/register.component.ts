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
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-page">
      <div class="auth-card card">
        <div class="auth-header">
          <h1>Crear cuenta</h1>
          <p>Únete y explora todas las razas</p>
        </div>

        <div class="error-banner" *ngIf="errorMessage">{{ errorMessage }}</div>
        <div class="success-banner" *ngIf="successMessage">{{ successMessage }}</div>

        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div class="form-field">
            <label>Usuario</label>
            <input formControlName="username" type="text" placeholder="tu_usuario" />
            <span class="field-error"
              *ngIf="form.get('username')?.invalid && form.get('username')?.touched">
              El usuario es requerido (min. 3 caracteres)
            </span>
          </div>

          <div class="form-field">
            <label>Email</label>
            <input formControlName="email" type="email" placeholder="correo@ejemplo.com" />
            <span class="field-error"
              *ngIf="form.get('email')?.invalid && form.get('email')?.touched">
              Ingresa un email válido
            </span>
          </div>

          <div class="form-field">
            <label>Contraseña</label>
            <input formControlName="password" type="password" placeholder="••••••••" />
            <span class="field-error"
              *ngIf="form.get('password')?.invalid && form.get('password')?.touched">
              Mínimo 8 caracteres
            </span>
          </div>

          <div class="form-field">
            <label>Confirmar contraseña</label>
            <input formControlName="confirmPassword" type="password" placeholder="••••••••" />
            <span class="field-error"
              *ngIf="form.hasError('passwordMismatch') && form.get('confirmPassword')?.touched">
              Las contraseñas no coinciden
            </span>
          </div>

          <button class="btn-primary" type="submit" [disabled]="loading || form.invalid">
            <span *ngIf="!loading">Registrarse</span>
            <span *ngIf="loading">Creando cuenta...</span>
          </button>
        </form>

        <p class="auth-footer">
          ¿Ya tienes cuenta? <a routerLink="/login">Inicia sesión</a>
        </p>
      </div>
    </div>
  `,
  styles: [`
    .auth-page {
      display: flex; align-items: center; justify-content: center;
      min-height: calc(100vh - 64px); padding: 2rem;
    }
    .auth-card { width: 100%; max-width: 420px; }
    .auth-header {
      text-align: center; margin-bottom: 2rem;
      .logo { font-size: 2.5rem; }
      h1 { font-size: 1.8rem; font-weight: 700; margin: 0.5rem 0 0.25rem; letter-spacing: -0.03em; }
      p { color: var(--text-muted); font-size: 0.9rem; }
    }
    .auth-footer {
      text-align: center; margin-top: 1.25rem; font-size: 0.88rem; color: var(--text-muted);
      a { color: var(--accent); text-decoration: none; font-weight: 600;
        &:hover { text-decoration: underline; } }
    }
  `],
})
export class RegisterComponent {
  form: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group(
      {
        username: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  private passwordMatchValidator(group: FormGroup) {
    const pass = group.get('password')?.value;
    console.log("pass",pass);
    const confirm = group.get('confirmPassword')?.value;
    console.log("confirm",confirm);
    return pass === confirm ? null : { passwordMismatch: true };
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMessage = '';

    const { username, email, password } = this.form.value;
    this.authService.register({ username, email, password }).subscribe({
      next: () => {
        this.successMessage = '¡Cuenta creada! Redirigiendo...';
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err.status === 409 ? 'El usuario o email ya existe.' : 'Error al registrarse.';
      },
    });
  }
}
