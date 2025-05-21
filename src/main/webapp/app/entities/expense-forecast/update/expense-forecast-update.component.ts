import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ExpenseForecastFormService, ExpenseForecastFormGroup } from './expense-forecast-form.service';
import { IExpenseForecast } from '../expense-forecast.model';
import { ExpenseForecastService } from '../service/expense-forecast.service';
import { IFinancialForecast } from 'app/entities/financial-forecast/financial-forecast.model';
import { FinancialForecastService } from 'app/entities/financial-forecast/service/financial-forecast.service';

@Component({
  selector: 'jhi-expense-forecast-update',
  templateUrl: './expense-forecast-update.component.html',
})
export class ExpenseForecastUpdateComponent implements OnInit {
  isSaving = false;
  expenseForecast: IExpenseForecast | null = null;

  financialForecastsSharedCollection: IFinancialForecast[] = [];

  editForm: ExpenseForecastFormGroup = this.expenseForecastFormService.createExpenseForecastFormGroup();

  constructor(
    protected expenseForecastService: ExpenseForecastService,
    protected expenseForecastFormService: ExpenseForecastFormService,
    protected financialForecastService: FinancialForecastService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareFinancialForecast = (o1: IFinancialForecast | null, o2: IFinancialForecast | null): boolean =>
    this.financialForecastService.compareFinancialForecast(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ expenseForecast }) => {
      this.expenseForecast = expenseForecast;
      if (expenseForecast) {
        this.updateForm(expenseForecast);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const expenseForecast = this.expenseForecastFormService.getExpenseForecast(this.editForm);
    if (expenseForecast.id !== null) {
      this.subscribeToSaveResponse(this.expenseForecastService.update(expenseForecast));
    } else {
      this.subscribeToSaveResponse(this.expenseForecastService.create(expenseForecast));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExpenseForecast>>): void {
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

  protected updateForm(expenseForecast: IExpenseForecast): void {
    this.expenseForecast = expenseForecast;
    this.expenseForecastFormService.resetForm(this.editForm, expenseForecast);

    this.financialForecastsSharedCollection = this.financialForecastService.addFinancialForecastToCollectionIfMissing<IFinancialForecast>(
      this.financialForecastsSharedCollection,
      expenseForecast.forecast
    );
  }

  protected loadRelationshipsOptions(): void {
    this.financialForecastService
      .query()
      .pipe(map((res: HttpResponse<IFinancialForecast[]>) => res.body ?? []))
      .pipe(
        map((financialForecasts: IFinancialForecast[]) =>
          this.financialForecastService.addFinancialForecastToCollectionIfMissing<IFinancialForecast>(
            financialForecasts,
            this.expenseForecast?.forecast
          )
        )
      )
      .subscribe((financialForecasts: IFinancialForecast[]) => (this.financialForecastsSharedCollection = financialForecasts));
  }
}
