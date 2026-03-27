import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Breed, BreedImage } from '../../domain/models/models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BreedService {
  private readonly API = `${environment.apiUrl}/breeds`;

  constructor(private http: HttpClient) {}

  getAllBreeds(): Observable<Breed[]> {
    return this.http.get<Breed[]>(this.API);
  }

  getBreedById(breedId: string): Observable<Breed> {
    return this.http.get<Breed>(`${this.API}/${breedId}`);
  }

  searchBreeds(query: string): Observable<Breed[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<Breed[]>(`${this.API}/search`, { params });
  }
}