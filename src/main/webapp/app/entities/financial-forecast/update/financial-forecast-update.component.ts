import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { FinancialForecastFormService, FinancialForecastFormGroup } from './financial-forecast-form.service';
import { IFinancialForecast } from '../financial-forecast.model';
import { FinancialForecastService } from '../service/financial-forecast.service';
import { IBusinessPlan } from 'app/entities/business-plan/business-plan.model';
import { BusinessPlanService } from 'app/entities/business-plan/service/business-plan.service';

@Component({
  selector: 'jhi-financial-forecast-update',
  templateUrl: './financial-forecast-update.component.html',
})
export class FinancialForecastUpdateComponent implements OnInit {
  isSaving = false;
  financialForecast: IFinancialForecast | null = null;

  businessPlansCollection: IBusinessPlan[] = [];

  editForm: FinancialForecastFormGroup = this.financialForecastFormService.createFinancialForecastFormGroup();

  constructor(
    protected financialForecastService: FinancialForecastService,
    protected financialForecastFormService: FinancialForecastFormService,
    protected businessPlanService: BusinessPlanService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareBusinessPlan = (o1: IBusinessPlan | null, o2: IBusinessPlan | null): boolean =>
    this.businessPlanService.compareBusinessPlan(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ financialForecast }) => {
      this.financialForecast = financialForecast;
      if (financialForecast) {
        this.updateForm(financialForecast);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const financialForecast = this.financialForecastFormService.getFinancialForecast(this.editForm);
    if (financialForecast.id !== null) {
      this.subscribeToSaveResponse(this.financialForecastService.update(financialForecast));
    } else {
      this.subscribeToSaveResponse(this.financialForecastService.create(financialForecast));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFinancialForecast>>): void {
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

  protected updateForm(financialForecast: IFinancialForecast): void {
    this.financialForecast = financialForecast;
    this.financialForecastFormService.resetForm(this.editForm, financialForecast);

    this.businessPlansCollection = this.businessPlanService.addBusinessPlanToCollectionIfMissing<IBusinessPlan>(
      this.businessPlansCollection,
      financialForecast.businessPlan
    );
  }

  protected loadRelationshipsOptions(): void {
    this.businessPlanService
      .query({ filter: 'forecast-is-null' })
      .pipe(map((res: HttpResponse<IBusinessPlan[]>) => res.body ?? []))
      .pipe(
        map((businessPlans: IBusinessPlan[]) =>
          this.businessPlanService.addBusinessPlanToCollectionIfMissing<IBusinessPlan>(businessPlans, this.financialForecast?.businessPlan)
        )
      )
      .subscribe((businessPlans: IBusinessPlan[]) => (this.businessPlansCollection = businessPlans));
  }
}
