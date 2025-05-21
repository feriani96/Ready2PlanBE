import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IExpenseForecast } from '../expense-forecast.model';

@Component({
  selector: 'jhi-expense-forecast-detail',
  templateUrl: './expense-forecast-detail.component.html',
})
export class ExpenseForecastDetailComponent implements OnInit {
  expenseForecast: IExpenseForecast | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ expenseForecast }) => {
      this.expenseForecast = expenseForecast;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
