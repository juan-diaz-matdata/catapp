import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BreedService } from '../../../infrastructure/services/breed.service';
import { ImageService } from '../../../infrastructure/services/image.service';
import { Breed, BreedImage } from '../../../domain/models/models';

@Component({
  selector: 'app-breeds',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="breeds-page">
      <header class="page-header">
        <h1>Razas de Gatos</h1>
        <p>Selecciona una raza para explorar sus características</p>
      </header>

      <div class="breed-selector card">
        <label>Selecciona una raza</label>
        <select [(ngModel)]="selectedBreedId" (ngModelChange)="onBreedChange()" [disabled]="loading">
          <option value="">-- Elige una raza --</option>
          <option *ngFor="let b of breeds" [value]="b.id">{{ b.name }}</option>
        </select>
      </div>

      <div *ngIf="loadingBreed" class="spinner"></div>

      <div *ngIf="selectedBreed && !loadingBreed" class="breed-detail">
        <div class="breed-info card">
          <div class="breed-name-row">
            <h2>{{ selectedBreed.name }}</h2>
            <span class="origin-badge">{{ selectedBreed.origin }}</span>
          </div>
          <p class="breed-description">{{ selectedBreed.description }}</p>
          <div class="breed-meta">
            <div class="meta-item">
              <span class="meta-label">🌡 Temperamento</span>
              <span class="meta-value">{{ selectedBreed.temperament }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">⏳ Vida</span>
              <span class="meta-value">{{ selectedBreed.lifeSpan }} años</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">⚖️ Peso</span>
              <span class="meta-value">{{ selectedBreed.weight }} kg</span>
            </div>
          </div>
          <div class="breed-stats">
            <div class="stat-bar-wrap" *ngFor="let stat of getStats()">
              <label>{{ stat.label }}</label>
              <div class="stat-bar">
                <div class="fill" [style.width.%]="stat.value * 20"></div>
              </div>
              <span class="stat-num">{{ stat.value }}/5</span>
            </div>
          </div>
        </div>

        <div class="carousel-panel card" *ngIf="images.length > 0">
          <div class="carousel-header">
            <h3>Galería de imágenes</h3>
            <div class="carousel-controls">
              <button (click)="prevImage()" [disabled]="currentIndex === 0">‹</button>
              <span>{{ currentIndex + 1 }} / {{ images.length }}</span>
              <button (click)="nextImage()" [disabled]="currentIndex === images.length - 1">›</button>
            </div>
          </div>
          <div class="carousel-track">
            <img [src]="images[currentIndex].url" [alt]="selectedBreed.name" class="carousel-img" />
          </div>
          <div class="carousel-dots">
            <span *ngFor="let img of images; let i = index"
              class="dot" [class.active]="i === currentIndex"
              (click)="currentIndex = i"></span>
          </div>
        </div>

        <div class="no-images card" *ngIf="images.length === 0 && !loadingBreed">
          <p>No hay imágenes disponibles para esta raza.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .breeds-page { max-width: 900px; margin: 0 auto; }
    .page-header { margin-bottom: 1.5rem; }
    .page-header h1 { font-size: 2rem; font-weight: 700; }
    .page-header p { color: var(--text-muted); }

    .breed-selector { margin-bottom: 1.5rem; }
    .breed-selector label { display: block; font-size: 0.8rem; font-weight: 600;
      text-transform: uppercase; color: var(--text-muted); margin-bottom: 0.5rem; }
    .breed-selector select { width: 100%; padding: 0.75rem 1rem;
      background: var(--surface-2); border: 1px solid var(--border);
      color: var(--text); border-radius: 8px; font-size: 0.95rem;
      outline: none; cursor: pointer; }
    .breed-selector select:focus { border-color: var(--accent); }
    .breed-selector select:disabled { opacity: 0.5; }

    .breed-detail { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; }
    @media (max-width: 700px) { .breed-detail { grid-template-columns: 1fr; } }

    .breed-name-row { display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem; }
    .breed-name-row h2 { font-size: 1.5rem; font-weight: 700; }
    .origin-badge { background: rgba(91,141,238,0.15); color: var(--accent-2);
      border: 1px solid rgba(91,141,238,0.3); padding: 0.25rem 0.75rem;
      border-radius: 100px; font-size: 0.8rem; font-weight: 600; }
    .breed-description { color: var(--text-muted); font-size: 0.9rem; line-height: 1.6; margin-bottom: 1.25rem; }

    .breed-meta { display: flex; flex-direction: column; gap: 0.5rem; margin-bottom: 1.25rem; }
    .meta-item { display: flex; justify-content: space-between; font-size: 0.88rem; }
    .meta-label { color: var(--text-muted); }
    .meta-value { font-weight: 500; }

    .breed-stats { display: flex; flex-direction: column; gap: 0.5rem; }
    .stat-num { font-size: 0.8rem; color: var(--text-muted); min-width: 28px; text-align: right; }

    .carousel-panel { display: flex; flex-direction: column; gap: 1rem; }
    .carousel-header { display: flex; justify-content: space-between; align-items: center; }
    .carousel-header h3 { font-size: 1rem; font-weight: 600; }
    .carousel-controls { display: flex; align-items: center; gap: 0.75rem; }
    .carousel-controls span { font-size: 0.85rem; color: var(--text-muted); }
    .carousel-controls button { background: var(--surface-2); border: 1px solid var(--border);
      color: var(--text); width: 32px; height: 32px; border-radius: 8px;
      cursor: pointer; font-size: 1.1rem; }
    .carousel-controls button:disabled { opacity: 0.4; cursor: not-allowed; }
    .carousel-track { border-radius: 8px; overflow: hidden; aspect-ratio: 4/3; }
    .carousel-img { width: 100%; height: 100%; object-fit: cover; }
    .carousel-dots { display: flex; justify-content: center; gap: 6px; flex-wrap: wrap; }
    .dot { width: 8px; height: 8px; border-radius: 50%; background: var(--border); cursor: pointer; }
    .dot.active { background: var(--accent); }
    .no-images { text-align: center; color: var(--text-muted); }
  `],
})
export class BreedsComponent implements OnInit {
  breeds: Breed[] = [];
  selectedBreed: Breed | null = null;
  images: BreedImage[] = [];
  selectedBreedId = '';
  loading = false;
  loadingBreed = false;
  currentIndex = 0;

  constructor(
    private breedService: BreedService,
    private imageService: ImageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.breedService.getAllBreeds().subscribe({
      next: (data) => { this.breeds = data; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }

  onBreedChange(): void {
    if (!this.selectedBreedId) {
      this.selectedBreed = null;
      this.images = [];
      return;
    }
    this.loadingBreed = true;
    this.currentIndex = 0;
    this.images = [];

    this.breedService.getBreedById(this.selectedBreedId).subscribe({
      next: (breed) => {
        this.selectedBreed = breed;
        this.cdr.detectChanges();
        this.imageService.getImagesByBreedId(this.selectedBreedId).subscribe({
          next: (imgs) => { this.images = imgs; this.loadingBreed = false; this.cdr.detectChanges(); },
          error: () => { this.loadingBreed = false; },
        });
      },
      error: () => { this.loadingBreed = false; },
    });
  }

  prevImage(): void { if (this.currentIndex > 0) this.currentIndex--; }
  nextImage(): void { if (this.currentIndex < this.images.length - 1) this.currentIndex++; }

  getStats() {
    if (!this.selectedBreed) return [];
    const b = this.selectedBreed;
    return [
      { label: 'Inteligencia', value: b.intelligence },
      { label: 'Afecto', value: b.affectionLevel },
      { label: 'Energía', value: b.energyLevel },
      { label: 'Adaptabilidad', value: b.adaptability },
    ].filter((s) => s.value != null);
  }
}