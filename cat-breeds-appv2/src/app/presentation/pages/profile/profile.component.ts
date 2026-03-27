import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../infrastructure/services/user.service';
import { AuthService } from '../../../infrastructure/services/auth.service';
import { User } from '../../../domain/models/models';


@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="profile-page">
      <header class="page-header">
        <h1>Mi Perfil</h1>
        <p>Vista protegida — solo usuarios autenticados</p>
      </header>

      <div *ngIf="loading" class="spinner"></div>

      <div *ngIf="!loading && user" class="profile-grid">
        <!-- Avatar card -->
        <div class="avatar-card card">
          <div class="avatar">
            {{ getInitials(user.username) }}
          </div>
          <h2>{{ user.username }}</h2>
          <p class="user-email">{{ user.email }}</p>
          <span class="role-badge">🔒 Autenticado</span>

          <div class="token-info">
            <span class="token-label">Token JWT activo</span>
            <span class="token-status">✓ Válido</span>
          </div>

          <button class="btn-secondary logout-btn" (click)="logout()">
            Cerrar sesión
          </button>
        </div>

        <!-- Info card -->
        <div class="info-card card">
          <h3>Información de la cuenta</h3>

          <div class="info-row">
            <span class="info-label">👤 Usuario</span>
            <span class="info-value">{{ user.username }}</span>
          </div>
          <div class="info-row" *ngIf="user.email">
            <span class="info-label">📧 Email</span>
            <span class="info-value">{{ user.email }}</span>
          </div>
          <div class="info-row" *ngIf="user.id">
            <span class="info-label">🆔 ID</span>
            <span class="info-value mono">{{ user.id }}</span>
          </div>

          <div class="security-section">
            <h4>Seguridad</h4>
            <ul>
              <li>✓ Contraseña cifrada con BCrypt</li>
              <li>✓ Sesión gestionada por JWT</li>
              <li>✓ Token almacenado en sessionStorage</li>
              <li>✓ Rutas protegidas por AuthGuard</li>
            </ul>
          </div>
        </div>
      </div>

      <div *ngIf="!loading && !user && errorMessage" class="error-banner">
        {{ errorMessage }}
      </div>
    </div>
  `,
  styles: [`
    .profile-page { max-width: 800px; margin: 0 auto; }
    .page-header { margin-bottom: 1.5rem;
      h1 { font-size: 2rem; font-weight: 700; letter-spacing: -0.03em; }
      p { color: var(--text-muted); margin-top: 0.25rem; } }

    .profile-grid { display: grid; grid-template-columns: 280px 1fr; gap: 1.5rem;
      @media (max-width: 650px) { grid-template-columns: 1fr; } }

    .avatar-card { text-align: center; display: flex; flex-direction: column;
      align-items: center; gap: 0.75rem;
      h2 { font-size: 1.3rem; font-weight: 700; margin: 0; }
      .user-email { color: var(--text-muted); font-size: 0.88rem; } }

    .avatar {
      width: 80px; height: 80px; border-radius: 50%;
      background: linear-gradient(135deg, var(--accent), var(--accent-2));
      display: flex; align-items: center; justify-content: center;
      font-size: 1.8rem; font-weight: 700; color: #000;
    }

    .role-badge { background: rgba(82, 196, 122, 0.12); color: var(--success);
      border: 1px solid rgba(82, 196, 122, 0.3); padding: 0.3rem 0.9rem;
      border-radius: 100px; font-size: 0.8rem; font-weight: 600; }

    .token-info { width: 100%; background: var(--surface-2); border: 1px solid var(--border);
      border-radius: 8px; padding: 0.75rem 1rem; display: flex;
      justify-content: space-between; align-items: center; font-size: 0.82rem;
      .token-label { color: var(--text-muted); }
      .token-status { color: var(--success); font-weight: 600; } }

    .logout-btn { margin-top: 0.25rem; }

    .info-card {
      h3 { font-size: 1.1rem; font-weight: 700; margin-bottom: 1.25rem; }
      h4 { font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em;
        color: var(--text-muted); margin: 1.25rem 0 0.75rem; }
    }

    .info-row { display: flex; justify-content: space-between; align-items: center;
      padding: 0.75rem 0; border-bottom: 1px solid var(--border); font-size: 0.9rem;
      .info-label { color: var(--text-muted); }
      .info-value { color: var(--text); font-weight: 500; }
      .mono { font-family: 'Courier New', monospace; font-size: 0.8rem; color: var(--text-muted); } }

    .security-section ul { list-style: none; display: flex; flex-direction: column; gap: 0.5rem;
      li { font-size: 0.88rem; color: var(--text-muted); } }
  `],
})
export class ProfileComponent implements OnInit {
  user: User | null = null;
  loading = true;
  errorMessage = '';

  constructor(
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userService.getMe().subscribe({
      next: (u) => { this.user = u; this.loading = false; },
      error: () => {
        // fallback to local user from token
        this.user = this.authService.currentUser;
        this.loading = false;
        if (!this.user) this.errorMessage = 'No se pudo cargar el perfil.';
      },
    });
  }

  getInitials(name: string): string {
    return name ? name.substring(0, 2).toUpperCase() : '?';
  }

  logout(): void { this.authService.logout(); }
}
