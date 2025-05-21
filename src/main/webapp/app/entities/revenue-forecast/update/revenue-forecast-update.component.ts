import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RevenueForecastFormService, RevenueForecastFormGroup } from './revenue-forecast-form.service';
import { IRevenueForecast } from '../revenue-forecast.model';
import { RevenueForecastService } from '../service/revenue-forecast.service';
import { IProductOrService } from 'app/entities/product-or-service/product-or-service.model';
import { ProductOrServiceService } from 'app/entities/product-or-service/service/product-or-service.service';
import { IFinancialForecast } from 'app/entities/financial-forecast/financial-forecast.model';
import { FinancialForecastService } from 'app/entities/financial-forecast/service/financial-forecast.service';

@Component({
  selector: 'jhi-revenue-forecast-update',
  templateUrl: './revenue-forecast-update.component.html',
})
export class RevenueForecastUpdateComponent implements OnInit {
  isSaving = false;
  revenueForecast: IRevenueForecast | null = null;

  productOrServicesSharedCollection: IProductOrService[] = [];
  financialForecastsSharedCollection: IFinancialForecast[] = [];

  editForm: RevenueForecastFormGroup = this.revenueForecastFormService.createRevenueForecastFormGroup();

  constructor(
    protected revenueForecastService: RevenueForecastService,
    protected revenueForecastFormService: RevenueForecastFormService,
    protected productOrServiceService: ProductOrServiceService,
    protected financialForecastService: FinancialForecastService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProductOrService = (o1: IProductOrService | null, o2: IProductOrService | null): boolean =>
    this.productOrServiceService.compareProductOrService(o1, o2);

  compareFinancialForecast = (o1: IFinancialForecast | null, o2: IFinancialForecast | null): boolean =>
    this.financialForecastService.compareFinancialForecast(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ revenueForecast }) => {
      this.revenueForecast = revenueForecast;
      if (revenueForecast) {
        this.updateForm(revenueForecast);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const revenueForecast = this.revenueForecastFormService.getRevenueForecast(this.editForm);
    if (revenueForecast.id !== null) {
      this.subscribeToSaveResponse(this.revenueForecastService.update(revenueForecast));
    } else {
      this.subscribeToSaveResponse(this.revenueForecastService.create(revenueForecast));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRevenueForecast>>): void {
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

  protected updateForm(revenueForecast: IRevenueForecast): void {
    this.revenueForecast = revenueForecast;
    this.revenueForecastFormService.resetForm(this.editForm, revenueForecast);

    this.productOrServicesSharedCollection = this.productOrServiceService.addProductOrServiceToCollectionIfMissing<IProductOrService>(
      this.productOrServicesSharedCollection,
      revenueForecast.product
    );
    this.financialForecastsSharedCollection = this.financialForecastService.addFinancialForecastToCollectionIfMissing<IFinancialForecast>(
      this.financialForecastsSharedCollection,
      revenueForecast.forecast
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productOrServiceService
      .query()
      .pipe(map((res: HttpResponse<IProductOrService[]>) => res.body ?? []))
      .pipe(
        map((productOrServices: IProductOrService[]) =>
          this.productOrServiceService.addProductOrServiceToCollectionIfMissing<IProductOrService>(
            productOrServices,
            this.revenueForecast?.product
          )
        )
      )
      .subscribe((productOrServices: IProductOrService[]) => (this.productOrServicesSharedCollection = productOrServices));

    this.financialForecastService
      .query()
      .pipe(map((res: HttpResponse<IFinancialForecast[]>) => res.body ?? []))
      .pipe(
        map((financialForecasts: IFinancialForecast[]) =>
          this.financialForecastService.addFinancialForecastToCollectionIfMissing<IFinancialForecast>(
            financialForecasts,
            this.revenueForecast?.forecast
          )
        )
      )
      .subscribe((financialForecasts: IFinancialForecast[]) => (this.financialForecastsSharedCollection = financialForecasts));
  }
}
