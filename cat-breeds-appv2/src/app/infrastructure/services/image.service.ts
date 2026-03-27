import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BreedImage } from '../../domain/models/models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ImageService {
  private readonly API = `${environment.apiUrl}/images`;

  constructor(private http: HttpClient) {}

  getImagesByBreedId(breedId: string): Observable<BreedImage[]> {
    const params = new HttpParams().set('breed_id', breedId);
    return this.http.get<BreedImage[]>(this.API, { params });
  }
}
