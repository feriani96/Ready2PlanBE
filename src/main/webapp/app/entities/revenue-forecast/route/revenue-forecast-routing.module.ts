import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RevenueForecastComponent } from '../list/revenue-forecast.component';
import { RevenueForecastDetailComponent } from '../detail/revenue-forecast-detail.component';
import { RevenueForecastUpdateComponent } from '../update/revenue-forecast-update.component';
import { RevenueForecastRoutingResolveService } from './revenue-forecast-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const revenueForecastRoute: Routes = [
  {
    path: '',
    component: RevenueForecastComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RevenueForecastDetailComponent,
    resolve: {
      revenueForecast: RevenueForecastRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RevenueForecastUpdateComponent,
    resolve: {
      revenueForecast: RevenueForecastRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RevenueForecastUpdateComponent,
    resolve: {
      revenueForecast: RevenueForecastRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(revenueForecastRoute)],
  exports: [RouterModule],
})
export class RevenueForecastRoutingModule {}
