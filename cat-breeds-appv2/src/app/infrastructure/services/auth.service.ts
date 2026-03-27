import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  User,
} from '../../domain/models/models';
import { environment } from '../../../environments/environment';

interface JwtPayload {
  sub: string;
  exp: number;
  iat: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly API = `${environment.apiUrl}/auth`;

  private currentUserSubject = new BehaviorSubject<User | null>(this.loadUserFromToken());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  // ── Public API ────────────────────────────────────────────────────────────

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API}/login`, payload).pipe(
      tap((res) => this.handleAuthResponse(res))
    );
  }

  register(payload: RegisterRequest): Observable<User> {
    return this.http.post<User>(`${this.API}/register`, payload);
  }

    logout(): void {
      if (typeof window !== 'undefined') {
        sessionStorage.removeItem(this.TOKEN_KEY);
      }
      this.currentUserSubject.next(null);
      this.router.navigate(['/login']);
    }

    getToken(): string | null {
      if (typeof window === 'undefined') return null;
      return sessionStorage.getItem(this.TOKEN_KEY);
    }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    try {
      const { exp } = jwtDecode<JwtPayload>(token);
      return Date.now() < exp * 1000;
    } catch {
      return false;
    }
  }

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // ── Private helpers ───────────────────────────────────────────────────────

  private handleAuthResponse(res: AuthResponse): void {
    // sessionStorage is cleared when tab closes — safer than localStorage for JWTs
    sessionStorage.setItem(this.TOKEN_KEY, res.token);
    const user: User = { id: '', username: res.username, email: '' };
    this.currentUserSubject.next(user);
  }

    private loadUserFromToken(): User | null {
      if (typeof window === 'undefined') return null;  // SSR guard
      const token = sessionStorage.getItem(this.TOKEN_KEY);
      if (!token) return null;
      try {
        const { sub, exp } = jwtDecode<JwtPayload>(token);
        if (Date.now() >= exp * 1000) {
          sessionStorage.removeItem(this.TOKEN_KEY);
          return null;
        }
        return { id: '', username: sub, email: '' };
      } catch {
        return null;
      }
    }
}
