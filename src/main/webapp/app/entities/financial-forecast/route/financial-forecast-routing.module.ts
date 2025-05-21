import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FinancialForecastComponent } from '../list/financial-forecast.component';
import { FinancialForecastDetailComponent } from '../detail/financial-forecast-detail.component';
import { FinancialForecastUpdateComponent } from '../update/financial-forecast-update.component';
import { FinancialForecastRoutingResolveService } from './financial-forecast-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const financialForecastRoute: Routes = [
  {
    path: '',
    component: FinancialForecastComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FinancialForecastDetailComponent,
    resolve: {
      financialForecast: FinancialForecastRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FinancialForecastUpdateComponent,
    resolve: {
      financialForecast: FinancialForecastRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FinancialForecastUpdateComponent,
    resolve: {
      financialForecast: FinancialForecastRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(financialForecastRoute)],
  exports: [RouterModule],
})
export class FinancialForecastRoutingModule {}
