import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductOrServiceComponent } from '../list/product-or-service.component';
import { ProductOrServiceDetailComponent } from '../detail/product-or-service-detail.component';
import { ProductOrServiceUpdateComponent } from '../update/product-or-service-update.component';
import { ProductOrServiceRoutingResolveService } from './product-or-service-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const productOrServiceRoute: Routes = [
  {
    path: '',
    component: ProductOrServiceComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductOrServiceDetailComponent,
    resolve: {
      productOrService: ProductOrServiceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductOrServiceUpdateComponent,
    resolve: {
      productOrService: ProductOrServiceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductOrServiceUpdateComponent,
    resolve: {
      productOrService: ProductOrServiceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productOrServiceRoute)],
  exports: [RouterModule],
})
export class ProductOrServiceRoutingModule {}
