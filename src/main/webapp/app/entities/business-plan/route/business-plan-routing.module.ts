import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BusinessPlanComponent } from '../list/business-plan.component';
import { BusinessPlanDetailComponent } from '../detail/business-plan-detail.component';
import { BusinessPlanUpdateComponent } from '../update/business-plan-update.component';
import { BusinessPlanRoutingResolveService } from './business-plan-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const businessPlanRoute: Routes = [
  {
    path: '',
    component: BusinessPlanComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BusinessPlanDetailComponent,
    resolve: {
      businessPlan: BusinessPlanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BusinessPlanUpdateComponent,
    resolve: {
      businessPlan: BusinessPlanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BusinessPlanUpdateComponent,
    resolve: {
      businessPlan: BusinessPlanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(businessPlanRoute)],
  exports: [RouterModule],
})
export class BusinessPlanRoutingModule {}
