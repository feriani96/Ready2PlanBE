import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ExpenseForecastComponent } from '../list/expense-forecast.component';
import { ExpenseForecastDetailComponent } from '../detail/expense-forecast-detail.component';
import { ExpenseForecastUpdateComponent } from '../update/expense-forecast-update.component';
import { ExpenseForecastRoutingResolveService } from './expense-forecast-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const expenseForecastRoute: Routes = [
  {
    path: '',
    component: ExpenseForecastComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ExpenseForecastDetailComponent,
    resolve: {
      expenseForecast: ExpenseForecastRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ExpenseForecastUpdateComponent,
    resolve: {
      expenseForecast: ExpenseForecastRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ExpenseForecastUpdateComponent,
    resolve: {
      expenseForecast: ExpenseForecastRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(expenseForecastRoute)],
  exports: [RouterModule],
})
export class ExpenseForecastRoutingModule {}
