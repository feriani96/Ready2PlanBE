import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { EntrepriseFormService, EntrepriseFormGroup } from './entreprise-form.service';
import { IEntreprise } from '../entreprise.model';
import { EntrepriseService } from '../service/entreprise.service';

@Component({
  selector: 'jhi-entreprise-update',
  templateUrl: './entreprise-update.component.html',
})
export class EntrepriseUpdateComponent implements OnInit {
  isSaving = false;
  entreprise: IEntreprise | null = null;

  editForm: EntrepriseFormGroup = this.entrepriseFormService.createEntrepriseFormGroup();

  constructor(
    protected entrepriseService: EntrepriseService,
    protected entrepriseFormService: EntrepriseFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ entreprise }) => {
      this.entreprise = entreprise;
      if (entreprise) {
        this.updateForm(entreprise);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const entreprise = this.entrepriseFormService.getEntreprise(this.editForm);
    if (entreprise.id !== null) {
      this.subscribeToSaveResponse(this.entrepriseService.update(entreprise));
    } else {
      this.subscribeToSaveResponse(this.entrepriseService.create(entreprise));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEntreprise>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(entreprise: IEntreprise): void {
    this.entreprise = entreprise;
    this.entrepriseFormService.resetForm(this.editForm, entreprise);
  }
}
