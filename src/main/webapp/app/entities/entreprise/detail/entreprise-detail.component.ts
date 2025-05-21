import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEntreprise } from '../entreprise.model';

@Component({
  selector: 'jhi-entreprise-detail',
  templateUrl: './entreprise-detail.component.html',
})
export class EntrepriseDetailComponent implements OnInit {
  entreprise: IEntreprise | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ entreprise }) => {
      this.entreprise = entreprise;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
