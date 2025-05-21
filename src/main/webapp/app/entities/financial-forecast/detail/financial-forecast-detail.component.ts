import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFinancialForecast } from '../financial-forecast.model';

@Component({
  selector: 'jhi-financial-forecast-detail',
  templateUrl: './financial-forecast-detail.component.html',
})
export class FinancialForecastDetailComponent implements OnInit {
  financialForecast: IFinancialForecast | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ financialForecast }) => {
      this.financialForecast = financialForecast;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
