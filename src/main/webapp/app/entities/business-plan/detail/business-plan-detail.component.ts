import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBusinessPlan } from '../business-plan.model';

@Component({
  selector: 'jhi-business-plan-detail',
  templateUrl: './business-plan-detail.component.html',
})
export class BusinessPlanDetailComponent implements OnInit {
  businessPlan: IBusinessPlan | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ businessPlan }) => {
      this.businessPlan = businessPlan;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
