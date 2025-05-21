import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { BusinessPlanFormService, BusinessPlanFormGroup } from './business-plan-form.service';
import { IBusinessPlan } from '../business-plan.model';
import { BusinessPlanService } from '../service/business-plan.service';

@Component({
  selector: 'jhi-business-plan-update',
  templateUrl: './business-plan-update.component.html',
})
export class BusinessPlanUpdateComponent implements OnInit {
  isSaving = false;
  businessPlan: IBusinessPlan | null = null;

  editForm: BusinessPlanFormGroup = this.businessPlanFormService.createBusinessPlanFormGroup();

  constructor(
    protected businessPlanService: BusinessPlanService,
    protected businessPlanFormService: BusinessPlanFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ businessPlan }) => {
      this.businessPlan = businessPlan;
      if (businessPlan) {
        this.updateForm(businessPlan);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const businessPlan = this.businessPlanFormService.getBusinessPlan(this.editForm);
    if (businessPlan.id !== null) {
      this.subscribeToSaveResponse(this.businessPlanService.update(businessPlan));
    } else {
      this.subscribeToSaveResponse(this.businessPlanService.create(businessPlan));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBusinessPlan>>): void {
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

  protected updateForm(businessPlan: IBusinessPlan): void {
    this.businessPlan = businessPlan;
    this.businessPlanFormService.resetForm(this.editForm, businessPlan);
  }
}
