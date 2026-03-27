import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AsyncPipe, NgIf } from '@angular/common';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from './infrastructure/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, AsyncPipe, NgIf],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  isAuthenticated$: Observable<boolean>;

  constructor(private authService: AuthService) {
    this.isAuthenticated$ = this.authService.currentUser$.pipe(map((user) => !!user));
  }

  logout(): void {
    this.authService.logout();
  }
}