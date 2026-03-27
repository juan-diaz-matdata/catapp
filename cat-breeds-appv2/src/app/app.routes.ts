
import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { noAuthGuard } from './core/guards/no-auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [noAuthGuard],
    loadComponent: () =>
      import('./presentation/pages/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    canActivate: [noAuthGuard],
    loadComponent: () =>
      import('./presentation/pages/register/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'breeds',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./presentation/pages/breeds/breeds.component').then((m) => m.BreedsComponent),
  },
  {
    path: 'search',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./presentation/pages/search/search.component').then((m) => m.SearchComponent),
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./presentation/pages/profile/profile.component').then((m) => m.ProfileComponent),
  },
  { path: '', redirectTo: 'breeds', pathMatch: 'full' },
  { path: '**', redirectTo: 'breeds' },
];