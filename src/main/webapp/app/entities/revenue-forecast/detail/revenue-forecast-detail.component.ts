import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRevenueForecast } from '../revenue-forecast.model';

@Component({
  selector: 'jhi-revenue-forecast-detail',
  templateUrl: './revenue-forecast-detail.component.html',
})
export class RevenueForecastDetailComponent implements OnInit {
  revenueForecast: IRevenueForecast | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ revenueForecast }) => {
      this.revenueForecast = revenueForecast;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
