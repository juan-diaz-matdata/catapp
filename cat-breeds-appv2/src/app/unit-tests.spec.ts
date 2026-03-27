
import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockToken =
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.' +
    btoa(JSON.stringify({ sub: 'testuser', exp: Math.floor(Date.now() / 1000) + 3600, iat: 0 })) +
    '.signature';

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [AuthService],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return null token when not authenticated', () => {
    expect(service.getToken()).toBeNull();
  });

  it('should return false for isAuthenticated when no token', () => {
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should store token and set user after login', () => {
    service.login({ username: 'testuser', password: 'pass123' }).subscribe((res) => {
      expect(res.token).toBe(mockToken);
      expect(service.getToken()).toBe(mockToken);
      expect(service.currentUser?.username).toBe('testuser');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({ token: mockToken, tokenType: 'Bearer', username: 'testuser' });
  });

  it('should clear token on logout', () => {
    sessionStorage.setItem('auth_token', mockToken);
    service.logout();
    expect(service.getToken()).toBeNull();
    expect(service.currentUser).toBeNull();
  });

  it('should call register endpoint with correct payload', () => {
    const payload = { username: 'newuser', email: 'new@test.com', password: 'pass1234' };
    service.register(payload).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ id: '1', username: 'newuser', email: 'new@test.com' });
  });

  it('should return false for expired token', () => {
    // expired token (exp in the past)
    const expired =
      'header.' +
      btoa(JSON.stringify({ sub: 'u', exp: 1000, iat: 0 })) +
      '.sig';
    sessionStorage.setItem('auth_token', expired);
    // Re-create the service to trigger loadUserFromToken
    const fresh = TestBed.inject(AuthService);
    expect(fresh.isAuthenticated()).toBeFalse();
  });
});


import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BreedService } from './breed.service';
import { environment } from '../../../environments/environment';
import { Breed } from '../../domain/models/models';

describe('BreedService', () => {
  let service: BreedService;
  let httpMock: HttpTestingController;

  const mockBreed: Breed = {
    id: 'abys', name: 'Abyssinian', description: 'Active cat', temperament: 'Active',
    origin: 'Ethiopia', lifeSpan: '14-15', weight: '4-5', intelligence: 5,
    affectionLevel: 5, energyLevel: 4, adaptability: 5, sheddingLevel: 2, healthIssues: 2,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule], providers: [BreedService] });
    service = TestBed.inject(BreedService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should get all breeds', () => {
    service.getAllBreeds().subscribe((breeds) => {
      expect(breeds.length).toBe(1);
      expect(breeds[0].name).toBe('Abyssinian');
    });
    const req = httpMock.expectOne(`${environment.apiUrl}/breeds`);
    expect(req.request.method).toBe('GET');
    req.flush([mockBreed]);
  });

  it('should get breed by id', () => {
    service.getBreedById('abys').subscribe((breed) => {
      expect(breed.id).toBe('abys');
    });
    const req = httpMock.expectOne(`${environment.apiUrl}/breeds/abys`);
    req.flush(mockBreed);
  });

  it('should search breeds with query param', () => {
    service.searchBreeds('aby').subscribe();
    const req = httpMock.expectOne((r) => r.url.includes('/breeds/search') && r.params.get('query') === 'aby');
    expect(req.request.method).toBe('GET');
    req.flush([mockBreed]);
  });
});


import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { authGuard } from './guards';
import { AuthService } from '../../infrastructure/services/auth.service';

describe('authGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(() => {
    authService = jasmine.createSpyObj('AuthService', ['isAuthenticated']);
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    });
    router = TestBed.inject(Router);
  });

  it('should allow navigation when authenticated', () => {
    authService.isAuthenticated.and.returnValue(true);
    TestBed.runInInjectionContext(() => {
      const result = authGuard({} as any, {} as any);
      expect(result).toBeTrue();
    });
  });

  it('should redirect to /login when not authenticated', () => {
    authService.isAuthenticated.and.returnValue(false);
    TestBed.runInInjectionContext(() => {
      const result = authGuard({} as any, {} as any);
      expect(result).not.toBeTrue();
    });
  });
});


import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../infrastructure/services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj('AuthService', ['login']);
    await TestBed.configureTestingModule({
      imports: [LoginComponent, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  it('form should be invalid when empty', () => {
    expect(component.form.invalid).toBeTrue();
  });

  it('form should be valid with correct values', () => {
    component.form.setValue({ username: 'user1', password: 'pass123' });
    expect(component.form.valid).toBeTrue();
  });

  it('should call authService.login on valid submit', () => {
    authService.login.and.returnValue(of({ token: 'tok', tokenType: 'Bearer', username: 'user1' }));
    component.form.setValue({ username: 'user1', password: 'pass123' });
    component.onSubmit();
    expect(authService.login).toHaveBeenCalled();
  });

  it('should show error message on 401', () => {
    authService.login.and.returnValue(throwError(() => ({ status: 401 })));
    component.form.setValue({ username: 'bad', password: 'badpass' });
    component.onSubmit();
    expect(component.errorMessage).toContain('incorrectas');
  });

  it('should not submit when form is invalid', () => {
    component.onSubmit();
    expect(authService.login).not.toHaveBeenCalled();
  });
});


import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../../infrastructure/services/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj('AuthService', ['register']);
    await TestBed.configureTestingModule({
      imports: [RegisterComponent, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  it('should fail validation when passwords do not match', () => {
    component.form.setValue({
      username: 'user', email: 'u@t.com', password: 'pass1234', confirmPassword: 'other',
    });
    expect(component.form.hasError('passwordMismatch')).toBeTrue();
  });

  it('should be valid when all fields match', () => {
    component.form.setValue({
      username: 'user1', email: 'u@t.com', password: 'pass1234', confirmPassword: 'pass1234',
    });
    expect(component.form.valid).toBeTrue();
  });

  it('should show conflict error on 409', () => {
    authService.register.and.returnValue(throwError(() => ({ status: 409 })));
    component.form.setValue({
      username: 'dup', email: 'dup@t.com', password: 'pass1234', confirmPassword: 'pass1234',
    });
    component.onSubmit();
    expect(component.errorMessage).toContain('ya existe');
  });
});
