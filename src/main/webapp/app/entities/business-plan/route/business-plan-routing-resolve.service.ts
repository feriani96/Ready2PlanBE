import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBusinessPlan } from '../business-plan.model';
import { BusinessPlanService } from '../service/business-plan.service';

@Injectable({ providedIn: 'root' })
export class BusinessPlanRoutingResolveService implements Resolve<IBusinessPlan | null> {
  constructor(protected service: BusinessPlanService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBusinessPlan | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((businessPlan: HttpResponse<IBusinessPlan>) => {
          if (businessPlan.body) {
            return of(businessPlan.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
