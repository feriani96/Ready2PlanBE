import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EntrepriseComponent } from '../list/entreprise.component';
import { EntrepriseDetailComponent } from '../detail/entreprise-detail.component';
import { EntrepriseUpdateComponent } from '../update/entreprise-update.component';
import { EntrepriseRoutingResolveService } from './entreprise-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const entrepriseRoute: Routes = [
  {
    path: '',
    component: EntrepriseComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EntrepriseDetailComponent,
    resolve: {
      entreprise: EntrepriseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EntrepriseUpdateComponent,
    resolve: {
      entreprise: EntrepriseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EntrepriseUpdateComponent,
    resolve: {
      entreprise: EntrepriseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(entrepriseRoute)],
  exports: [RouterModule],
})
export class EntrepriseRoutingModule {}
