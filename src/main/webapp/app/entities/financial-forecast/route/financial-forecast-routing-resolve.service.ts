import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFinancialForecast } from '../financial-forecast.model';
import { FinancialForecastService } from '../service/financial-forecast.service';

@Injectable({ providedIn: 'root' })
export class FinancialForecastRoutingResolveService implements Resolve<IFinancialForecast | null> {
  constructor(protected service: FinancialForecastService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFinancialForecast | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((financialForecast: HttpResponse<IFinancialForecast>) => {
          if (financialForecast.body) {
            return of(financialForecast.body);
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
