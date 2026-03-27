import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject, switchMap, of } from 'rxjs';
import { BreedService } from '../../../infrastructure/services/breed.service';
import { Breed } from '../../../domain/models/models';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="search-page">
      <header class="page-header">
        <h1>Búsqueda de Razas</h1>
        <p>Filtra por nombre, origen o temperamento</p>
      </header>

      <div class="search-bar card">
        <div class="search-input-wrap">
          <span class="search-icon">Buscar</span>
          <input
            type="text"
            [(ngModel)]="query"
            (ngModelChange)="onQueryChange($event)"
            placeholder="Busca una raza..."
            class="search-input"
          />
          <button *ngIf="query" class="clear-btn" (click)="clearSearch()">✕</button>
        </div>
      </div>

      <div *ngIf="loading" class="spinner"></div>

      <div *ngIf="!loading" class="results-card card">
        <div class="table-header">
          <span class="results-count">{{ breeds.length }} resultado(s)</span>
        </div>

        <div class="table-wrap" *ngIf="breeds.length > 0">
          <table>
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Origen</th>
                <th>Temperamento</th>
                <th>Vida</th>
                <th>Inteligencia</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let b of breeds" class="breed-row">
                <td class="breed-name">{{ b.name }}</td>
                <td><span class="tag">{{ b.origin }}</span></td>
                <td class="temperament">{{ b.temperament }}</td>
                <td>{{ b.lifeSpan }}</td>
                <td>
                  <div class="mini-bar">
                    <div class="mini-fill" [style.width.%]="b.intelligence * 20"></div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="empty-state" *ngIf="breeds.length === 0 && query">

          <p>No se encontraron razas para <strong>"{{ query }}"</strong></p>
        </div>

        <div class="empty-state" *ngIf="breeds.length === 0 && !query">

          <p>Escribe algo para buscar razas</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .search-page { max-width: 960px; margin: 0 auto; }
    .page-header { margin-bottom: 1.5rem;
      h1 { font-size: 2rem; font-weight: 700; letter-spacing: -0.03em; }
      p { color: var(--text-muted); margin-top: 0.25rem; } }

    .search-bar { margin-bottom: 1.5rem; }
    .search-input-wrap { display: flex; align-items: center; gap: 0.75rem; position: relative; }
    .search-icon { font-size: 1.1rem; }
    .search-input {
      flex: 1; background: var(--surface-2); border: 1px solid var(--border);
      color: var(--text); padding: 0.75rem 1rem; border-radius: 8px;
      font-size: 0.95rem; outline: none;
      &:focus { border-color: var(--accent); }
    }
    .clear-btn { background: transparent; border: none; color: var(--text-muted);
      cursor: pointer; font-size: 0.9rem; padding: 0.25rem 0.5rem;
      &:hover { color: var(--text); } }

    .results-card {}
    .table-header { display: flex; justify-content: flex-end; margin-bottom: 1rem;
      .results-count { font-size: 0.85rem; color: var(--text-muted); } }

    .table-wrap { overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; font-size: 0.88rem; }
    th { text-align: left; padding: 0.6rem 1rem; color: var(--text-muted);
      font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em;
      border-bottom: 1px solid var(--border); font-weight: 600; }
    td { padding: 0.85rem 1rem; border-bottom: 1px solid var(--border); vertical-align: middle; }
    .breed-row:last-child td { border-bottom: none; }
    .breed-row:hover td { background: var(--surface-2); }
    .breed-name { font-weight: 600; color: var(--text); }
    .temperament { color: var(--text-muted); max-width: 200px;
      white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .tag { background: rgba(91, 141, 238, 0.12); color: var(--accent-2);
      padding: 0.2rem 0.6rem; border-radius: 100px; font-size: 0.78rem; font-weight: 500; }

    .mini-bar { width: 80px; height: 5px; background: var(--surface-2);
      border-radius: 3px; overflow: hidden;
      .mini-fill { height: 100%; background: var(--accent); border-radius: 3px; } }

    .empty-state { text-align: center; padding: 3rem 1rem; color: var(--text-muted);
      span { font-size: 2.5rem; display: block; margin-bottom: 0.75rem; }
      p { font-size: 0.95rem; } }
  `],
})
export class SearchComponent implements OnInit {
  breeds: Breed[] = [];
  query = '';
  loading = false;
  private search$ = new Subject<string>();

  constructor(private breedService: BreedService) {}

  ngOnInit(): void {
    this.search$
      .pipe(
        debounceTime(350),
        distinctUntilChanged(),
        switchMap((q) => {
          this.loading = true;
          return q.trim().length >= 2
            ? this.breedService.searchBreeds(q)
            : of([]);
        })
      )
      .subscribe({
        next: (data) => { this.breeds = data; this.loading = false; },
        error: () => { this.loading = false; },
      });
  }

  onQueryChange(q: string): void { this.search$.next(q); }

  clearSearch(): void {
    this.query = '';
    this.breeds = [];
    this.search$.next('');
  }
}
